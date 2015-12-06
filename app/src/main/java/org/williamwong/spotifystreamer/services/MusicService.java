package org.williamwong.spotifystreamer.services;

import android.app.Notification;
import android.app.NotificationManager;
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

    private static MusicService sMusicService;
    private final int NOTIFICATION_ID = 1;
    private MediaPlayer mMediaPlayer = null;
    private List<TrackModel> mTrackModels;
    private int mCurrentTrack;
    private long mDuration;
    private State mState = State.INITIALIZING;
    private NotificationManager mNotificationManager;
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
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        initMediaPlayer();
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
        setUpAsForeground(getCurrentlyPlayingTrackModel().getTrackName());
    }

    public void pauseSong() {
        if (mState == State.PLAYING) {
            mMediaPlayer.pause();
            mState = State.PAUSED;
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
    void setUpAsForeground(String text) {
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, new Intent(getApplicationContext(),
                        PlayerActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setTicker(text)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(text)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT < 16) {
            mNotification = notificationBuilder.getNotification();
        } else {
            mNotification = notificationBuilder.build();
        }
        startForeground(NOTIFICATION_ID, mNotification);
    }

    /**
     * Updates the notification.
     */
    void updateNotification(String text) {
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    enum State {
        INITIALIZING,
        PREPARING,
        PLAYING,
        PAUSED,
        STOPPED
    }
}
