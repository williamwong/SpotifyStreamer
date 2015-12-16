package org.williamwong.spotifystreamer.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.databinding.ListItemArtistBinding;
import org.williamwong.spotifystreamer.models.ArtistModel;
import org.williamwong.spotifystreamer.viewmodels.ItemArtistViewModel;

import java.util.List;

/**
 * List adapter for artist search results
 * Created by w.wong on 6/14/2015.
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private final List<ArtistModel> mArtistModels;

    public ArtistAdapter(List<ArtistModel> artistModels) {
        mArtistModels = artistModels;
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemArtistBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_item_artist,
                parent,
                false);
        return new ArtistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        holder.bindArtist(mArtistModels.get(position));
    }

    @Override
    public int getItemCount() {
        return mArtistModels.size();
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {
        final ListItemArtistBinding mBinding;

        public ArtistViewHolder(ListItemArtistBinding binding) {
            super(binding.artistItemView);
            mBinding = binding;
        }

        public void bindArtist(ArtistModel artist) {
            if (mBinding.getVm() == null) {
                mBinding.setVm(new ItemArtistViewModel(artist));
            } else {
                mBinding.getVm().setArtist(artist);
            }
        }
    }
}
