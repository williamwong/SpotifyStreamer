package org.williamwong.spotifystreamer.viewmodels;

import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.squareup.picasso.Picasso;

import org.williamwong.spotifystreamer.models.TrackModel;
import org.williamwong.spotifystreamer.services.MusicService;
import org.williamwong.spotifystreamer.utilities.TimerUtilities;

/**
 * View model for player interface
 * Created by williamwong on 11/29/15.
 */
public class PlayerViewModel {

    public ObservableField<String> trackName = new ObservableField<>();
    public ObservableField<String> artistName = new ObservableField<>();
    public ObservableField<String> albumName = new ObservableField<>();
    public ObservableField<String> albumImageUrl = new ObservableField<>();
    public ObservableField<String> timeCurrent = new ObservableField<>();
    public ObservableField<String> timeEnd = new ObservableField<>();
    public ObservableInt trackProgress = new ObservableInt();
    public ObservableBoolean isPlaying = new ObservableBoolean();

    private MusicService mMusicService;
    private Handler mHandler = new Handler();
    private Runnable mUpdateSeekBar;

    public PlayerViewModel() {
        mMusicService = MusicService.getMusicService();
        mUpdateSeekBar = new Runnable() {
            @Override
            public void run() {
                long currentDuration = mMusicService.getCurrentPosition();
                long totalDuration = mMusicService.getDuration();

                timeCurrent.set(TimerUtilities.milliSecondsToTimeString(currentDuration));
                timeEnd.set(TimerUtilities.milliSecondsToTimeString(totalDuration));

                int progress = TimerUtilities.millisecondsToPercentage(currentDuration, totalDuration);
                trackProgress.set(progress);

                mHandler.postDelayed(this, 100);
            }
        };

        setTrack(mMusicService.getCurrentlyPlayingTrackModel());
        isPlaying.set(true);
        updateSeekBar();
    }

    @BindingAdapter("android:src")
    public static void setImageUrl(ImageView view, String source) {
        Picasso.with(view.getContext()).load(source).into(view);
    }

    public void setTrack(TrackModel track) {
        trackName.set(track.getTrackName());
        artistName.set(track.getArtistName());
        albumName.set(track.getAlbumName());
        albumImageUrl.set(track.getImageUrl());
    }

    public void onPlayPause(View v) {
        if (isPlaying.get()) {
            mMusicService.pauseSong();
            isPlaying.set(false);
        } else {
            mMusicService.playSong();
            isPlaying.set(true);
        }
        setTrack(mMusicService.getCurrentlyPlayingTrackModel());
    }

    public void onPrevious(View v) {
        mMusicService.previousSong();
        setTrack(mMusicService.getCurrentlyPlayingTrackModel());
        isPlaying.set(true);
    }

    public void onNext(View v) {
        mMusicService.nextSong();
        setTrack(mMusicService.getCurrentlyPlayingTrackModel());
        isPlaying.set(true);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        // Remove message Handler from updating progress bar while touched
        mHandler.removeCallbacks(mUpdateSeekBar);
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateSeekBar);
        long totalDuration = mMusicService.getDuration();
        int currentPosition = TimerUtilities.percentageToMilliseconds(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mMusicService.seekTo(currentPosition);

        // update timer progress again
        updateSeekBar();
    }

    private void updateSeekBar() {
        mHandler.postDelayed(mUpdateSeekBar, 100);
    }

}
