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
import android.widget.SeekBar;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.databinding.FragmentPlayerBinding;
import org.williamwong.spotifystreamer.services.MusicService;
import org.williamwong.spotifystreamer.utilities.TimerUtilities;
import org.williamwong.spotifystreamer.viewModels.PlayerViewModel;

/**
 * Fragment for displaying track info and playing preview track
 * Created by w.wong on 6/21/2015.
 */
public class PlayerFragment extends DialogFragment {

    private MusicService mMusicService;

    private SeekBar mSeekBar;
    private Handler mHandler = new Handler();
    private Runnable mUpdateSeekBar;

    private FragmentPlayerBinding mBinding;
    private PlayerViewModel mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        mBinding = FragmentPlayerBinding.bind(view);
        mViewModel = new PlayerViewModel();
        mBinding.setVm(mViewModel);

        mSeekBar = (SeekBar) view.findViewById(R.id.previewSeekBar);

        mMusicService = MusicService.getMusicService();
        if (mMusicService != null) {
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

                        mViewModel.timeCurrent.set(TimerUtilities.milliSecondsToTimer(currentDuration));
                        mViewModel.timeEnd.set(TimerUtilities.milliSecondsToTimer(totalDuration));

                        int progress = TimerUtilities.getProgressPercentage(currentDuration, totalDuration);
                        mSeekBar.setProgress(progress);
                    }

                    mHandler.postDelayed(this, 100);
                }
            };
        }

        updateSeekBar();
        mViewModel.setTrack(mMusicService.getCurrentlyPlayingTrackModel());

        return view;
    }

    private void updateSeekBar() {
        mHandler.postDelayed(mUpdateSeekBar, 100);
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
}
