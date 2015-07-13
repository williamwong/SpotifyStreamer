package org.williamwong.spotifystreamer.fragments;

import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.activities.MainActivity;
import org.williamwong.spotifystreamer.models.TrackModel;

import java.io.IOException;

/**
 * Fragment for displaying track info and playing preview track
 * Created by w.wong on 6/21/2015.
 */
public class PlayerFragment extends DialogFragment {

    private static final String TRACK_MODEL_KEY = "trackModel";
    private TrackModel mTrackModel;

    public PlayerFragment() {
    }

    public static PlayerFragment newInstance(TrackModel trackModel) {
        PlayerFragment playerFragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putParcelable(MainActivity.TRACK_MODEL_KEY, trackModel);
        playerFragment.setArguments(args);
        return playerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(MainActivity.TRACK_MODEL_KEY)) {
            mTrackModel = getArguments().getParcelable(MainActivity.TRACK_MODEL_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        if (savedInstanceState != null) {
            mTrackModel = savedInstanceState.getParcelable(TRACK_MODEL_KEY);
        }

        TextView artistNameTextView = (TextView) view.findViewById(R.id.artistNameTextView);
        TextView albumNameTextView = (TextView) view.findViewById(R.id.albumNameTextView);
        TextView trackNameTextView = (TextView) view.findViewById(R.id.trackNameTextView);
        ImageView albumImageView = (ImageView) view.findViewById(R.id.albumImageView);
        ImageButton previousButton = (ImageButton) view.findViewById(R.id.previousButton);
        ImageButton playPauseButton = (ImageButton) view.findViewById(R.id.playPauseButton);
        ImageButton nextButton = (ImageButton) view.findViewById(R.id.nextButton);

        if (mTrackModel != null) {
            artistNameTextView.setText(mTrackModel.getArtistName());
            albumNameTextView.setText(mTrackModel.getAlbumName());
            trackNameTextView.setText(mTrackModel.getTrackName());

            Picasso.with(getActivity())
                    .load(mTrackModel.getImageUrl())
                    .fit().centerCrop()
                    .into(albumImageView);

            playPauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playMusic(mTrackModel.getPreviewUrl());
                }
            });
        }

        return view;
    }

    private void playMusic(String previewUrl) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(previewUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return false;
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        mediaPlayer.prepareAsync();
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
