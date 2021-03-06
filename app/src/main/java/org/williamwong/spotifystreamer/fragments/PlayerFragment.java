package org.williamwong.spotifystreamer.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.databinding.FragmentPlayerBinding;
import org.williamwong.spotifystreamer.models.TrackModel;
import org.williamwong.spotifystreamer.services.MusicService;
import org.williamwong.spotifystreamer.viewmodels.PlayerViewModel;

/**
 * Fragment for displaying track info and playing preview track
 * Created by w.wong on 6/21/2015.
 */
public class PlayerFragment extends DialogFragment implements MusicService.OnTrackChangedListener {

    private PlayerViewModel mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        setHasOptionsMenu(true);

        FragmentPlayerBinding binding = FragmentPlayerBinding.bind(view);
        mViewModel = new PlayerViewModel();
        binding.setVm(mViewModel);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null) {
            int dialogWidth = getResources().getDimensionPixelSize(R.dimen.dialog_width);
            int dialogHeight = getResources().getDimensionPixelSize(R.dimen.dialog_height);
            getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        }

        MusicService service = MusicService.getMusicService();
        if (service != null) {
            service.registerListener(this);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                MusicService service = MusicService.getMusicService();
                if (service == null) return true;

                TrackModel track = service.getCurrentlyPlayingTrackModel();
                if (track != null) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TITLE, track.getTrackName());
                    shareIntent.putExtra(Intent.EXTRA_TEXT, track.getExternalUrl());
                    startActivity(shareIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MusicService service = MusicService.getMusicService();
        if (service != null) {
            service.unregisterListener(this);
        }
    }

    @Override
    public void onTrackChanged(TrackModel track, int position, boolean isComplete) {
        mViewModel.setTrack(track);
        if (isComplete) {
            mViewModel.isPlaying.set(false);
        }
    }
}
