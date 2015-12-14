package org.williamwong.spotifystreamer.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.databinding.ListItemTrackBinding;
import org.williamwong.spotifystreamer.models.TrackModel;
import org.williamwong.spotifystreamer.viewmodels.ItemTrackViewModel;

import java.util.List;

/**
 * List adapter for track list.
 * Created by w.wong on 6/19/2015.
 */
public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    private final List<TrackModel> mTrackModels;

    public TrackAdapter(List<TrackModel> trackModels) {
        mTrackModels = trackModels;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemTrackBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_item_track,
                parent,
                false);
        return new TrackViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        holder.bindTrack(mTrackModels, position);
    }

    @Override
    public int getItemCount() {
        return mTrackModels.size();
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder{
        final ListItemTrackBinding mBinding;

        public TrackViewHolder(ListItemTrackBinding binding) {
            super(binding.trackItemView);
            mBinding = binding;
        }

        public void bindTrack(List<TrackModel> trackModels, int position) {
            if (mBinding.getVm() == null) {
                mBinding.setVm(new ItemTrackViewModel(trackModels, position));
            } else {
                mBinding.getVm().setTrack(trackModels, position);
            }
        }

    }
}
