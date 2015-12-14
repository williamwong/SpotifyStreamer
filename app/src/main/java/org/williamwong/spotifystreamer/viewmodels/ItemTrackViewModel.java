package org.williamwong.spotifystreamer.viewmodels;

import android.content.Context;
import android.view.View;

import org.williamwong.spotifystreamer.models.TrackModel;
import org.williamwong.spotifystreamer.services.MusicService;

import java.util.List;

/**
 * View model for individual track views in RecyclerViews
 * Created by williamwong on 12/13/15.
 */
public class ItemTrackViewModel {
    private List<TrackModel> mTrackList;
    private int mPosition;

    public ItemTrackViewModel(List<TrackModel> trackList, int position) {
        mTrackList = trackList;
        mPosition = position;
    }

    public String getTrackName() {
        return mTrackList.get(mPosition).getTrackName();
    }

    public String getAlbumName() {
        return mTrackList.get(mPosition).getAlbumName();
    }

    public String getImageUrl() {
        return mTrackList.get(mPosition).getImageUrl();
    }

    public void setTrack(List<TrackModel> trackList, int position) {
        mTrackList = trackList;
        mPosition = position;
    }

    public void onTrackClick(View v) {
        MusicService service = MusicService.getMusicService();
        service.setTrackModels(mTrackList);
        service.setCurrentTrack(mPosition);
        service.playSong();

        // Launch PlayerFragment and/or Activity
        Context context = v.getContext();
        if (context instanceof OnTrackClickListener) {
            ((OnTrackClickListener) context).onTrackSelected();
        }
    }

    public interface OnTrackClickListener {
        void onTrackSelected();
    }
}
