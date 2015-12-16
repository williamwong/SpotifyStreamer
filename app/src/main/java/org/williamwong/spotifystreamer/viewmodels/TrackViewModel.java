package org.williamwong.spotifystreamer.viewmodels;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.databinding.ObservableBoolean;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.models.TrackModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
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
public class TrackViewModel {

    public final ObservableBoolean isLoading = new ObservableBoolean(false);
    private final Resources mResources;
    private final SpotifyService mSpotify = new SpotifyApi().getService();
    private final SharedPreferences mPreferences;
    private OnTrackListChangedListener mListener;

    public TrackViewModel(SharedPreferences preferences, Resources resources) {
        mPreferences = preferences;
        mResources = resources;
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
            List<TrackModel> trackModels = new ArrayList<>();
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
                trackModels.add(trackModel);
            }
            if (mListener != null) mListener.onTrackListChanged(trackModels);
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

    public interface OnTrackListChangedListener {
        void onTrackListChanged(List<TrackModel> trackModels);

        void onErrorReceived(int resource);
    }
}
