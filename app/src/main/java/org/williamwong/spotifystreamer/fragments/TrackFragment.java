package org.williamwong.spotifystreamer.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.activities.MainActivity;
import org.williamwong.spotifystreamer.adapters.TrackAdapter;
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
 * A fragment containing a list of top ten tracks from an artist.
 */
public class TrackFragment extends Fragment {

    private static final String TRACK_MODELS_KEY = "trackModels";

    private SpotifyService mSpotify = new SpotifyApi().getService();
    private ArrayList<TrackModel> mTrackModels;
    private TrackAdapter mTrackAdapter;
    private String mSpotifyId;
    private ProgressBar mTrackProgressBar;

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
        View view = inflater.inflate(R.layout.fragment_track, container, false);

        mTrackProgressBar = (ProgressBar) view.findViewById(R.id.trackProgressBar);

        if (savedInstanceState != null &&
                savedInstanceState.getParcelableArrayList(TRACK_MODELS_KEY) != null) {
            mTrackModels = savedInstanceState.getParcelableArrayList(TRACK_MODELS_KEY);
        } else {
            mTrackModels = new ArrayList<>();
            searchTracks(mSpotifyId);
        }

        RecyclerView trackRecyclerView = (RecyclerView) view.findViewById(R.id.tracksRecyclerView);
        trackRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        trackRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mTrackAdapter = new TrackAdapter(getActivity(), mTrackModels);
        trackRecyclerView.setAdapter(mTrackAdapter);

        return view;
    }

    /**
     * Searches Spotify for top 10 artist tracks using Spotify API wrapper.
     *
     * @param mSpotifyId Spotify ID representing an artist
     */
    private void searchTracks(String mSpotifyId) {
        mTrackProgressBar.setVisibility(View.VISIBLE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String country = preferences.getString("country", "US");

        Map<String, Object> options = new HashMap<>();
        options.put("country", country);
        mSpotify.getArtistTopTrack(mSpotifyId, options, new Callback<Tracks>() {
            Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void success(final Tracks tracks, Response response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTrackProgressBar.setVisibility(View.GONE);
                        updateTracks(tracks.tracks);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTrackProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),
                                getString(R.string.error_network),
                                Toast.LENGTH_SHORT)
                                .show();
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
        mTrackModels.clear();
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
                        } else if (images.get(i + 1).width < getResources()
                                .getInteger(R.integer.min_thumbnail_pixel_width)) {
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
        } else {
            Toast.makeText(getActivity(),
                    getString(R.string.error_no_tracks_found),
                    Toast.LENGTH_SHORT)
                    .show();
        }

        mTrackAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TRACK_MODELS_KEY, mTrackModels);
    }
}
