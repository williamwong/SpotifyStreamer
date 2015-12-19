package org.williamwong.spotifystreamer.viewmodels;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.databinding.ObservableBoolean;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.SpotifyApplication;
import org.williamwong.spotifystreamer.models.TrackModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * View model for track list
 * Created by williamwong on 12/15/15.
 */
public class TrackViewModel implements Parcelable {

    public static final Parcelable.Creator<TrackViewModel> CREATOR = new Parcelable.Creator<TrackViewModel>() {
        public TrackViewModel createFromParcel(Parcel source) {
            return new TrackViewModel(source);
        }

        public TrackViewModel[] newArray(int size) {
            return new TrackViewModel[size];
        }
    };
    public ObservableBoolean isLoading = new ObservableBoolean(false);
    public List<TrackModel> mTrackModels = new ArrayList<>();
    @Inject
    Resources mResources;
    @Inject
    SpotifyService mSpotify;
    @Inject
    SharedPreferences mPreferences;
    private OnTrackListChangedListener mListener;

    public TrackViewModel() {
        SpotifyApplication.getContext().getNetComponent().inject(this);
    }

    protected TrackViewModel(Parcel in) {
        this.isLoading = in.readParcelable(ObservableBoolean.class.getClassLoader());
        this.mTrackModels = in.createTypedArrayList(TrackModel.CREATOR);
    }

    /**
     * Searches Spotify for top 10 artist tracks using Spotify API wrapper.
     *
     * @param mSpotifyId Spotify ID representing an artist
     */
    public void searchTracks(String mSpotifyId) {
        isLoading.set(true);

        String country = mPreferences.getString("country", "US");

        Map<String, Object> options = new HashMap<>();
        options.put("country", country);
        mSpotify.getArtistTopTrack(mSpotifyId, options, new Callback<Tracks>() {
            Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void success(final Tracks tracks, Response response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        isLoading.set(false);
                        updateTracks(tracks.tracks);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        isLoading.set(false);
                        if (mListener != null) mListener.onErrorReceived(R.string.error_network);
                    }
                });
            }
        });
    }

    /**
     * Updates mTrackModels list and refreshes the list adapter to display new data.
     *
     * @param tracks List of tracks returned from Spotify API
     */
    private void updateTracks(List<Track> tracks) {
        if (!tracks.isEmpty()) {
            for (Track track : tracks) {
                TrackModel trackModel = new TrackModel();
                trackModel.setTrackName(track.name);
                trackModel.setAlbumName(track.album.name);
                trackModel.setPreviewUrl(track.preview_url);
                trackModel.setExternalUrl(track.external_urls.get("spotify"));

                List<Image> images = track.album.images;
                if (images != null && !images.isEmpty()) {
                    int imageIndex = 0;
                    for (int i = 0; i < images.size(); i++) {
                        boolean isLast = (i + 1 == images.size());
                        if (isLast) {
                            imageIndex = i;
                            break;
                        } else if (images.get(i + 1).width < mResources.getInteger(R.integer.min_thumbnail_pixel_width)) {
                            imageIndex = i;
                            break;
                        }
                    }
                    trackModel.setImageUrl(images.get(imageIndex).url);
                }

                List<String> artistNames = new ArrayList<>();
                for (ArtistSimple artist : track.artists) {
                    artistNames.add(artist.name);
                }
                trackModel.setArtistName(TextUtils.join(", ", artistNames));

                // TODO set placeholder image
                mTrackModels.add(trackModel);
            }
            if (mListener != null) mListener.onTrackListChanged();
        } else {
            if (mListener != null) mListener.onErrorReceived(R.string.error_no_tracks_found);
        }
    }

    public void setOnTrackListChangedListener(OnTrackListChangedListener listener) {
        mListener = listener;
    }

    public void removeOnTrackListChangedListener(OnTrackListChangedListener listener) {
        if (mListener == listener) mListener = null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.isLoading, 0);
        dest.writeTypedList(mTrackModels);
    }

    public interface OnTrackListChangedListener {
        void onTrackListChanged();

        void onErrorReceived(int resource);
    }
}
