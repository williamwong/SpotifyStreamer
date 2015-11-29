package org.williamwong.spotifystreamer.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.models.TrackModel;
import org.williamwong.spotifystreamer.services.MusicService;
import org.williamwong.spotifystreamer.utilities.TimerUtilities;

/**
 * Fragment for displaying track info and playing preview track
 * Created by w.wong on 6/21/2015.
 */
public class PlayerFragment extends DialogFragment {

    private static final String TRACK_MODEL_KEY = "trackModel";
    private TrackModel mTrackModel;
    private TextView mArtistNameTextView;
    private TextView mAlbumNameTextView;
    private TextView mTrackNameTextView;
    private TextView mTimeCurrentTextView;
    private TextView mTimeEndTextView;
    private ImageView mAlbumImageView;
    private ImageButton mPreviousButton;
    private ImageButton mPlayPauseButton;
    private ImageButton mNextButton;
    private MusicService mMusicService;

    private SeekBar mSeekBar;
    private Handler mHandler = new Handler();
    private Runnable mUpdateSeekBar;

    public PlayerFragment() {
    }

    public static PlayerFragment newInstance() {
        return new PlayerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        mArtistNameTextView = (TextView) view.findViewById(R.id.artistNameTextView);
        mAlbumNameTextView = (TextView) view.findViewById(R.id.albumNameTextView);
        mTrackNameTextView = (TextView) view.findViewById(R.id.trackNameTextView);
        mTimeCurrentTextView = (TextView) view.findViewById(R.id.timeCurrentTextView);
        mTimeEndTextView = (TextView) view.findViewById(R.id.timeEndTextView);
        mAlbumImageView = (ImageView) view.findViewById(R.id.albumImageView);
        mPreviousButton = (ImageButton) view.findViewById(R.id.previousButton);
        mPlayPauseButton = (ImageButton) view.findViewById(R.id.playPauseButton);
        mNextButton = (ImageButton) view.findViewById(R.id.nextButton);
        mSeekBar = (SeekBar) view.findViewById(R.id.previewSeekBar);

        mMusicService = MusicService.getMusicService();
        if (mMusicService != null) {
            mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMusicService.isPlayingOrPreparing()) {
                        mMusicService.pauseSong();
                    } else {
                        mMusicService.playSong();
                    }
                    updateView();
                }
            });
            mPreviousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMusicService.previousSong();
                    updateView();
                }
            });
            mNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMusicService.nextSong();
                    updateView();
                }
            });
            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // Remove message Handler from updating progress bar
                    mHandler.removeCallbacks(mUpdateSeekBar);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mHandler.removeCallbacks(mUpdateSeekBar);
                    int totalDuration = (int) mMusicService.getDuration();
                    int currentPosition = TimerUtilities.progressToTimer(seekBar.getProgress(), totalDuration);

                    // forward or backward to certain seconds
                    mMusicService.seekTo(currentPosition);

                    // update timer progress again
                    updateSeekBar();
                }
            });
            mUpdateSeekBar = new Runnable() {
                @Override
                public void run() {
                    if (mMusicService.isPlayingOrPreparing()) {
                        long currentDuration = mMusicService.getCurrentPosition();
                        long totalDuration = mMusicService.getDuration();

                        mTimeCurrentTextView.setText(TimerUtilities.milliSecondsToTimer(currentDuration));
                        mTimeEndTextView.setText(TimerUtilities.milliSecondsToTimer(totalDuration));

                        int progress = TimerUtilities.getProgressPercentage(currentDuration, totalDuration);
                        mSeekBar.setProgress(progress);
                    }

                    mHandler.postDelayed(this, 100);
                }
            };
        }

        updateSeekBar();
        updateView();

        return view;
    }

    private void updateSeekBar() {
        mHandler.postDelayed(mUpdateSeekBar, 100);
    }

    private void updateView() {
        if (mMusicService != null) {
            mTrackModel = mMusicService.getCurrentlyPlayingTrackModel();
            if (mMusicService.isPlayingOrPreparing()) {
                mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
            }
        }
        if (mTrackModel != null) {
            mArtistNameTextView.setText(mTrackModel.getArtistName());
            mAlbumNameTextView.setText(mTrackModel.getAlbumName());
            mTrackNameTextView.setText(mTrackModel.getTrackName());

            Picasso.with(getActivity())
                    .load(mTrackModel.getImageUrl())
                    .fit().centerCrop()
                    .into(mAlbumImageView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null) {
            int dialogWidth = getResources().getDimensionPixelSize(R.dimen.dialog_width);
            int dialogHeight = getResources().getDimensionPixelSize(R.dimen.dialog_height);
            getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TRACK_MODEL_KEY, mTrackModel);
    }
}
