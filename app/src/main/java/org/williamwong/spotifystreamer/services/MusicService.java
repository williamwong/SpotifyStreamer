package org.williamwong.spotifystreamer.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.activities.PlayerActivity;
import org.williamwong.spotifystreamer.models.TrackModel;

import java.io.IOException;
import java.util.List;

// TODO Add callbacks to notify view models about song changes

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

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
    private boolean mShowNotification;
    private State mState = State.INITIALIZING;
    private Notification mNotification;
    private NotificationCompat.Builder mNotificationBuilder;
    private PendingIntent mOpenPlayerIntent;
    private PendingIntent mPreviousIntent;
    private PendingIntent mPauseIntent;
    private PendingIntent mPlayIntent;
    private PendingIntent mNextIntent;
    private SharedPreferences mPreferences;

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

        mOpenPlayerIntent = PendingIntent.getActivity(getApplicationContext(),
                0, new Intent(getApplicationContext(), PlayerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        mPreviousIntent = PendingIntent.getService(getApplicationContext(),
                0, new Intent(MusicService.ACTION_PREVIOUS), PendingIntent.FLAG_UPDATE_CURRENT);
        mPauseIntent = PendingIntent.getService(getApplicationContext(),
                0, new Intent(MusicService.ACTION_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT);
        mPlayIntent = PendingIntent.getService(getApplicationContext(),
                0, new Intent(MusicService.ACTION_PLAY), PendingIntent.FLAG_UPDATE_CURRENT);
        mNextIntent = PendingIntent.getService(getApplicationContext(),
                0, new Intent(MusicService.ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPreferences.registerOnSharedPreferenceChangeListener(this);
        setShowNotification(mPreferences.getBoolean("show_notification", true));

        mNotificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentIntent(mOpenPlayerIntent)
                .addAction(android.R.drawable.ic_media_previous, "Previous", mPreviousIntent)
                .addAction(android.R.drawable.ic_media_pause, "Pause", mPauseIntent)
                .addAction(android.R.drawable.ic_media_next, "Next", mNextIntent);
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
            if (mShowNotification) {
                setUpNotification();
            }
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
        if (mShowNotification) {
            setUpNotification();
        }
    }

    public void pauseSong() {
        if (mState == State.PLAYING) {
            mMediaPlayer.pause();
            mState = State.PAUSED;
            if (mShowNotification) {
                setUpNotification();
            }
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

    public void setShowNotification(boolean showNotification) {
        mShowNotification = showNotification;
        if (mShowNotification) {
            if (mState != State.INITIALIZING)
                setUpNotification();
        } else {
            stopForeground(true);
        }
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
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);
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

        String message = getCurrentlyPlayingTrackModel().getTrackName() + " - " + getCurrentlyPlayingTrackModel().getArtistName();

        mNotificationBuilder
                .setTicker(message)
                .setContentText(message);

        if (mState == State.PREPARING || mState == State.PLAYING) {
            mNotificationBuilder.mActions.set(1, new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", mPauseIntent));
        } else {
            mNotificationBuilder.mActions.set(1, new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", mPlayIntent));
        }

        if (Build.VERSION.SDK_INT < 16) {
            mNotification = mNotificationBuilder.getNotification();
        } else {
            mNotification = mNotificationBuilder.build();
        }
        startForeground(NOTIFICATION_ID, mNotification);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("show_notification")) {
            setShowNotification(sharedPreferences.getBoolean(key, true));
        }
    }

    enum State {
        INITIALIZING,
        PREPARING,
        PLAYING,
        PAUSED,
        STOPPED
    }
}
