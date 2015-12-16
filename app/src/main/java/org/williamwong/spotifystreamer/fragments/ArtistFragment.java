package org.williamwong.spotifystreamer.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.adapters.ArtistAdapter;
import org.williamwong.spotifystreamer.databinding.FragmentArtistBinding;
import org.williamwong.spotifystreamer.models.ArtistModel;
import org.williamwong.spotifystreamer.viewmodels.ArtistViewModel;

import java.util.ArrayList;
import java.util.List;

import icepick.State;

/**
 * A fragment containing a search bar and a list of results.
 */
public class ArtistFragment extends BaseFragment implements ArtistViewModel.OnArtistsChangedListener {

    @State
    ArrayList<ArtistModel> mArtistModels;
    private ArtistViewModel mArtistViewModel;
    private FragmentArtistBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mArtistModels == null) {
            mArtistModels = new ArrayList<>();
        }

        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        mBinding = FragmentArtistBinding.bind(view);
        Resources resources = getResources();
        mArtistViewModel = new ArtistViewModel(mArtistModels, resources);
        mArtistViewModel.setOnArtistsChangedListener(this);
        mBinding.setVm(mArtistViewModel);

        setupRecyclerView(mBinding.artistsRecyclerView);

        return view;
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

    private void hideSoftKeyboard() {
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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
    public void onArtistsChanged(List<ArtistModel> artists) {
        mBinding.artistsRecyclerView.getAdapter().notifyDataSetChanged();
        hideSoftKeyboard();
    }

    @Override
    public void onErrorReceived(int resource) {
        Toast.makeText(getActivity(), resource, Toast.LENGTH_SHORT).show();
    }
}
