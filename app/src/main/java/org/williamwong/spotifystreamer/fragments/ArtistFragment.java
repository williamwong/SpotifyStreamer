package org.williamwong.spotifystreamer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.adapters.ArtistAdapter;
import org.williamwong.spotifystreamer.databinding.FragmentArtistBinding;
import org.williamwong.spotifystreamer.models.ArtistModel;
import org.williamwong.spotifystreamer.viewmodels.ArtistViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment containing a search bar and a list of results.
 */
public class ArtistFragment extends Fragment implements ArtistViewModel.OnArtistsChangedListener {

    private static final String ARTIST_MODELS_KEY = "artistsModels";

    private ArrayList<ArtistModel> mArtistModels;
    private ArtistViewModel mArtistViewModel;
    private FragmentArtistBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setupArtistModels(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        mBinding = FragmentArtistBinding.bind(view);
        mArtistViewModel = new ArtistViewModel(mArtistModels, this);
        mBinding.setVm(mArtistViewModel);

        setupRecyclerView(mBinding.artistsRecyclerView);

        return view;
    }

    /**
     * Retrieve list of artists if fragment is not new. Otherwise, initialize list of artists.
     * @param savedInstanceState Bundle that may contain saved list of artists
     */
    private void setupArtistModels(Bundle savedInstanceState) {
        if (savedInstanceState != null &&
                savedInstanceState.getParcelableArrayList(ARTIST_MODELS_KEY) != null) {
            mArtistModels = savedInstanceState.getParcelableArrayList(ARTIST_MODELS_KEY);
        } else {
            mArtistModels = new ArrayList<>();
        }
    }

    /**
     * Initialize Recycler View and attach the list of artists using an adapter
     *
     * @param recyclerView RecyclerView to initialize
     */
    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ArtistAdapter artistAdapter = new ArtistAdapter(mArtistModels);
        recyclerView.setAdapter(artistAdapter);
    }

    /**
     * Remove reference to fragment from the view model when the view is destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mArtistViewModel.removeOnArtistsChangedListener();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ARTIST_MODELS_KEY, mArtistModels);
    }

    @Override
    public void onArtistsChanged(List<ArtistModel> artists) {
        mBinding.artistsRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onErrorReceived(int resource) {
        Toast.makeText(getActivity(), resource, Toast.LENGTH_SHORT).show();
    }
}
