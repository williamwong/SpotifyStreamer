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

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.activities.TrackActivity;

import java.io.IOException;

public class MusicServiceOld extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {

    private static final String ACTION_PLAY = "PLAY";
    private static String mUrl;
    private static MusicServiceOld mInstance = null;
    private static String mSongTitle;
    private static String mSongPicUrl;
    final int NOTIFICATION_ID = 1;
    NotificationManager mNotificationManager;
    Notification mNotification = null;
    State mState = State.INITIALIZING;
    private MediaPlayer mMediaPlayer = null;
    private int mBufferPosition;

    public static MusicServiceOld getInstance() {
        return mInstance;
    }

    public static void setSong(String url, String title, String songPicUrl) {
        mUrl = url;
        mSongTitle = title;
        mSongPicUrl = songPicUrl;
    }

    @Override
    public void onCreate() {
        mInstance = this;
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_PLAY)) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            initMediaPlayer();
        }
        return START_STICKY;
    }

    private void initMediaPlayer() {
        try {
            mMediaPlayer.setDataSource(mUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
        mState = State.PREPARING;
    }

    public void restartMusic() {
        mMediaPlayer.start();
    }

    protected void setBufferPosition(int progress) {
        mBufferPosition = progress;
    }

    /**
     * Called when MediaPlayer is ready
     */
    @Override
    public void onPrepared(MediaPlayer player) {
        // Begin playing music
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mState = State.INITIALIZING;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void pauseMusic() {
        if (mState.equals(State.PLAYING)) {
            mMediaPlayer.pause();
            mState = State.PAUSED;
            updateNotification(mSongTitle + "(paused)");
        }
    }

    public void startMusic() {
        if (!mState.equals(State.PREPARING) && !mState.equals(State.INITIALIZING)) {
            mMediaPlayer.start();
            mState = State.PLAYING;
            updateNotification(mSongTitle + "(playing)");
        }
    }

    public boolean isPlaying() {
        return (mState.equals(State.PLAYING));
    }

    public int getMusicDuration() {
        // Return current music duration
        return mMediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public int getBufferPercentage() {
        return mBufferPosition;
    }

    public void seekMusicTo(int pos) {
        // Seek music to pos
    }

    public String getSongTitle() {
        return mSongTitle;
    }

    public String getSongPicUrl() {
        return mSongPicUrl;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        setBufferPosition(percent * getMusicDuration() / 100);
    }

    /**
     * Updates the notification.
     */
    void updateNotification(String text) {
        // Notify NotificationManager of new intent
    }

    /**
     * Configures service as a foreground service. A foreground service is a service that's doing something the user is
     * actively aware of (such as playing music), and must appear to the user as a notification. That's why we create
     * the notification here.
     */
    void setUpAsForeground(String text) {
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), TrackActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext())
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

    enum State {
        INITIALIZING,
        PREPARING,
        PLAYING,
        PAUSED,
        STOPPED
    }
}
