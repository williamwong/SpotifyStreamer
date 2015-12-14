package org.williamwong.spotifystreamer.viewmodels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.view.View;

import org.williamwong.spotifystreamer.models.ArtistModel;

/**
 * View model for individual artist views in RecyclerViews
 * Created by williamwong on 12/13/15.
 */
public class ItemArtistViewModel extends BaseObservable{
    private ArtistModel mArtist;

    public ItemArtistViewModel(ArtistModel artist) {
        mArtist = artist;
    }

    public String getName() {
        return mArtist.getName();
    }

    public String getImageUrl() {
        return mArtist.getImageUrl();
    }

    public void onItemClick(View v) {
        Context context = v.getContext();
        if (context instanceof OnArtistClickListener) {
            ((OnArtistClickListener) context).onArtistSelected(mArtist.getSpotifyId(), mArtist.getName());
        }
    }

    public void setArtist(ArtistModel artist) {
        mArtist = artist;
        notifyChange();
    }

    public interface OnArtistClickListener {
        void onArtistSelected(String spotifyId, String artistName);
    }
}
