package org.williamwong.spotifystreamer.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import org.williamwong.spotifystreamer.models.TrackModel;

import java.io.IOException;
import java.util.List;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {

    public static final String EXTRA_TRACK_MODELS = "org.williamwong.spotifystreamer.TRACK_MODELS";
    public static final String EXTRA_CURRENT_TRACK = "org.williamwong.spotifystreamer.CURRENT_TRACK";

    private static MusicService sMusicService;

    private MediaPlayer mMediaPlayer = null;
    private List<TrackModel> mTrackModels;
    private int mCurrentTrack;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sMusicService = this;
    }

    public static MusicService getMusicService() {
        if (sMusicService != null) {
            return sMusicService;
        } else {
            return null;
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MusicService", "MusicService has started");
        if (intent.getExtras() != null) {
            mTrackModels = intent.getParcelableArrayListExtra(EXTRA_TRACK_MODELS);
            mCurrentTrack = intent.getIntExtra(EXTRA_CURRENT_TRACK, 0);
            initMediaPlayer();

            if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
            playSong();
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

    private void playSong() {
        try {
            mMediaPlayer.setDataSource(mTrackModels.get(mCurrentTrack).getPreviewUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
    }

    public void pauseSong() {
        mMediaPlayer.pause();
    }

    public void nextSong() {
        if (mCurrentTrack < mTrackModels.size()) {
            mCurrentTrack++;
            playSong();
        }
    }

    public void previousSong() {
        if (mCurrentTrack > 0) {
            mCurrentTrack--;
            playSong();
        }
    }

    public TrackModel getCurrentlyPlayingTrackModel() {
        if (mTrackModels != null) {
            return mTrackModels.get(mCurrentTrack);
        } else {
            return null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        return false;
    }
}
