package org.williamwong.spotifystreamer.fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.activities.MainActivity;
import org.williamwong.spotifystreamer.adapters.TrackAdapter;
import org.williamwong.spotifystreamer.databinding.FragmentTrackBinding;
import org.williamwong.spotifystreamer.models.TrackModel;
import org.williamwong.spotifystreamer.viewmodels.TrackViewModel;

import java.util.ArrayList;
import java.util.List;

import icepick.State;

/**
 * A fragment containing a list of top ten tracks from an artist.
 */
public class TrackFragment extends BaseFragment implements TrackViewModel.OnTrackListChangedListener {

    @State
    ArrayList<TrackModel> mTrackModels;
    private String mSpotifyId;
    private FragmentTrackBinding mBinding;
    private TrackViewModel mTrackViewModel;

    public static TrackFragment newInstance(String spotifyId) {
        TrackFragment trackFragment = new TrackFragment();
        Bundle args = new Bundle();
        args.putSerializable(MainActivity.SPOTIFY_ID_KEY, spotifyId);
        trackFragment.setArguments(args);
        return trackFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(MainActivity.SPOTIFY_ID_KEY)) {
            mSpotifyId = getArguments().getString(MainActivity.SPOTIFY_ID_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mTrackModels == null) {
            mTrackModels = new ArrayList<>();
        }

        View view = inflater.inflate(R.layout.fragment_track, container, false);
        mBinding = FragmentTrackBinding.bind(view);
        mTrackViewModel = new TrackViewModel();
        mTrackViewModel.setOnTrackListChangedListener(this);
        mBinding.setVm(mTrackViewModel);

        if (mTrackModels.isEmpty()) mTrackViewModel.searchTracks(mSpotifyId);

        setupRecyclerView(mBinding.tracksRecyclerView);

        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        TrackAdapter trackAdapter = new TrackAdapter(mTrackModels);
        recyclerView.setAdapter(trackAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTrackViewModel.removeOnTrackListChangedListener(this);
    }

    @Override
    public void onTrackListChanged(List<TrackModel> trackModels) {
        mTrackModels.clear();
        for (TrackModel track : trackModels) mTrackModels.add(track);
        mBinding.tracksRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onErrorReceived(int resource) {
        Toast.makeText(getActivity(), getString(resource), Toast.LENGTH_SHORT).show();
    }
}
