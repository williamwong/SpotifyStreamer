package org.williamwong.spotifystreamer.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.activities.PlayerActivity;
import org.williamwong.spotifystreamer.models.TrackModel;

import java.io.IOException;
import java.util.List;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {

    public static final String ACTION_PLAY = "org.williamwong.spotifystreamer.action.PLAY_SONG";
    public static final String ACTION_PAUSE = "org.williamwong.spotifystreamer.action.PAUSE_SONG";
    public static final String ACTION_NEXT = "org.williamwong.spotifystreamer.action.NEXT_SONG";
    public static final String ACTION_STOP = "org.williamwong.spotifystreamer.action.STOP_SONG";
    public static final String ACTION_PREVIOUS = "org.williamwong.spotifystreamer.action.PREVIOUS_SONG";

    private static MusicService sMusicService;
    private final int NOTIFICATION_ID = 1;
    private MediaPlayer mMediaPlayer = null;
    private List<TrackModel> mTrackModels;
    private int mCurrentTrack;
    private long mDuration;
    private State mState = State.INITIALIZING;
    private Notification mNotification;

    public static MusicService getMusicService() {
        if (sMusicService != null) {
            return sMusicService;
        } else {
            return null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sMusicService = this;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (action != null) {
            switch (action) {
                case ACTION_PLAY:
                    playSong();
                    break;
                case ACTION_PAUSE:
                    pauseSong();
                    break;
                case ACTION_NEXT:
                    nextSong();
                    break;
                case ACTION_STOP:
                    stopSong();
                    break;
                case ACTION_PREVIOUS:
                    previousSong();
                    break;
            }
        } else {
            initMediaPlayer();
        }

        return START_STICKY;
    }

    private void initMediaPlayer() {
        if (mMediaPlayer == null) mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void playSong() {
        if (mState == State.PAUSED) {
            mMediaPlayer.start();
            mState = State.PLAYING;
            setUpNotification();
            return;
        }

        if (mState == State.PLAYING) {
            stopSong();
        }

        try {
            mMediaPlayer.setDataSource(mTrackModels.get(mCurrentTrack).getPreviewUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
        mState = State.PREPARING;
        setUpNotification();
    }

    public void pauseSong() {
        if (mState == State.PLAYING) {
            mMediaPlayer.pause();
            mState = State.PAUSED;
            setUpNotification();
        }
    }

    private void stopSong() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mState = State.STOPPED;
    }

    public void nextSong() {
        if (mCurrentTrack < mTrackModels.size() - 1) {
            stopSong();
            mCurrentTrack++;
            playSong();
        }
    }

    public void previousSong() {
        if (mCurrentTrack > 0) {
            stopSong();
            mCurrentTrack--;
            playSong();
        }
    }

    public void seekTo(int currentPosition) {
        mMediaPlayer.seekTo(currentPosition);
    }

    public TrackModel getCurrentlyPlayingTrackModel() {
        if (mTrackModels != null) {
            return mTrackModels.get(mCurrentTrack);
        } else {
            return null;
        }
    }

    public void setTrackModels(List<TrackModel> trackModels) {
        mTrackModels = trackModels;
    }

    public void setCurrentTrack(int currentTrack) {
        mCurrentTrack = currentTrack;
    }

    public long getDuration() {
        return mDuration;
    }

    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) mMediaPlayer.release();
        mMediaPlayer = null;
        stopForeground(true);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mDuration = mediaPlayer.getDuration();
        mediaPlayer.start();
        mState = State.PLAYING;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        stopForeground(true);
        return false;
    }

    /**
     * Configures service as a foreground service. A foreground service is a service that's doing something the user is
     * actively aware of (such as playing music), and must appear to the user as a notification. That's why we create
     * the notification here.
     */
    void setUpNotification() {
        PendingIntent openPlayerIntent = PendingIntent.getActivity(getApplicationContext(),
                0, new Intent(getApplicationContext(), PlayerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent previousIntent = PendingIntent.getService(getApplicationContext(),
                0, new Intent(MusicService.ACTION_PREVIOUS), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pauseIntent = PendingIntent.getService(getApplicationContext(),
                0, new Intent(MusicService.ACTION_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent playIntent = PendingIntent.getService(getApplicationContext(),
                0, new Intent(MusicService.ACTION_PLAY), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent nextIntent = PendingIntent.getService(getApplicationContext(),
                0, new Intent(MusicService.ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);

        String message = getCurrentlyPlayingTrackModel().getTrackName() + " - " + getCurrentlyPlayingTrackModel().getArtistName();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setTicker(message)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(message)
                .setContentIntent(openPlayerIntent);

        notificationBuilder.addAction(android.R.drawable.ic_media_previous, "Previous", previousIntent);
        if (mState == State.PREPARING || mState == State.PLAYING) {
            notificationBuilder.addAction(android.R.drawable.ic_media_pause, "Pause", pauseIntent);
        } else {
            notificationBuilder.addAction(android.R.drawable.ic_media_play, "Play", playIntent);
        }
        notificationBuilder.addAction(android.R.drawable.ic_media_next, "Next", nextIntent);

        if (Build.VERSION.SDK_INT < 16) {
            mNotification = notificationBuilder.getNotification();
        } else {
            mNotification = notificationBuilder.build();
        }
        startForeground(NOTIFICATION_ID, mNotification);
    }

    enum State {
        INITIALIZING,
        PREPARING,
        PLAYING,
        PAUSED,
        STOPPED
    }
}
