package org.williamwong.spotifystreamer.viewmodels;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.models.ArtistModel;
import org.williamwong.spotifystreamer.utilities.BindableString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * View model for artist search interface
 * Created by williamwong on 12/13/15.
 */
public class ArtistViewModel {

    private static final int MIN_THUMBNAIL_WIDTH = 200;

    public ObservableBoolean isLoading = new ObservableBoolean(false);
    public BindableString searchArtistQuery = new BindableString();

    private List<ArtistModel> mArtistModels;
    private OnArtistsChangedListener mListener;

    public ArtistViewModel(List<ArtistModel> artistModels, OnArtistsChangedListener listener) {
        mArtistModels = artistModels;
        mListener = listener;
    }

    public boolean onSearchAction(TextView view, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            // Close keyboard
            ((InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            // Execute search
            if (searchArtistQuery.get().length() > 0) {
                searchArtist();
            } else {
                if (mListener != null) mListener.onErrorReceived(R.string.error_invalid_search);
            }

            return true;
        }
        return false;
    }

    /**
     * Searches Spotify for artist using Spotify API wrapper.
     */
    private void searchArtist() {
        isLoading.set(true);
        String artist = searchArtistQuery.get();

        Map<String, Object> options = new HashMap<>();
        options.put("limit", 10);
        SpotifyService spotify = new SpotifyApi().getService();
        spotify.searchArtists(artist, options, new Callback<ArtistsPager>() {
            Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void success(final ArtistsPager artistsPager, Response response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        isLoading.set(false);
                        updateArtistModels(artistsPager);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) mListener.onErrorReceived(R.string.error_network);
                    }
                });
            }
        });
    }

    /**
     * Updates mArtistModel list and notifies the listener about new data.
     *
     * @param artistsPager Results returned from Spotify API
     */
    private void updateArtistModels(ArtistsPager artistsPager) {
        mArtistModels.clear();
        if (artistsPager.artists.total > 0) {
            for (Artist artist : artistsPager.artists.items) {
                ArtistModel artistModel = new ArtistModel();
                artistModel.setName(artist.name);
                artistModel.setSpotifyId(artist.id);

                List<Image> images = artist.images;
                if (images != null && !images.isEmpty()) {
                    int imageIndex = 0;
                    for (int i = 0; i < images.size(); i++) {
                        boolean isLast = (i + 1 == images.size());
                        if (isLast) {
                            imageIndex = i;
                            break;
                        } else if (images.get(i + 1).width < MIN_THUMBNAIL_WIDTH) {
                            imageIndex = i;
                            break;
                        }
                    }
                    artistModel.setImageUrl(images.get(imageIndex).url);
                }

                // TODO add placeholder image

                mArtistModels.add(artistModel);
            }
        } else {
            if (mListener != null) mListener.onErrorReceived(R.string.error_no_artists_found);
        }

        if (mListener != null) mListener.onArtistsChanged(mArtistModels);
    }

    public void setOnArtistsChangedListener(OnArtistsChangedListener listener) {
        mListener = listener;
    }

    public void removeOnArtistsChangedListener() {
        mListener = null;
    }

    public interface OnArtistsChangedListener {
        void onArtistsChanged(List<ArtistModel> artists);

        void onErrorReceived(int resource);
    }
}
