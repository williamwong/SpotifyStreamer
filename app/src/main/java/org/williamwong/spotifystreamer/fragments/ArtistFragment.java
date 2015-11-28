package org.williamwong.spotifystreamer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.adapters.ArtistAdapter;
import org.williamwong.spotifystreamer.models.ArtistModel;

import java.util.ArrayList;
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
 * A fragment containing a search bar and a list of results.
 */
public class ArtistFragment extends Fragment {

    public static final int MIN_THUMBNAIL_WIDTH = 200;
    public static final String ARTIST_MODELS_KEY = "artistsModels";

    private SpotifyService mSpotify = new SpotifyApi().getService();
    private ArrayList<ArtistModel> mArtistModels;
    private ArtistAdapter mArtistAdapter;
    private Callbacks mCallbacks;
    private ProgressBar mArtistProgressBar;
    private ListView mArtistsListView;

    public ArtistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);

        // Retrieve mArtistModels if fragment is not new. Otherwise, initialize mArtistModels
        if (savedInstanceState != null &&
                savedInstanceState.getParcelableArrayList(ARTIST_MODELS_KEY) != null) {
            mArtistModels = savedInstanceState.getParcelableArrayList(ARTIST_MODELS_KEY);
        } else {
            mArtistModels = new ArrayList<>();
        }

        mArtistsListView = (ListView) view.findViewById(R.id.artistsListView);
        mArtistAdapter = new ArtistAdapter(getActivity(), mArtistModels);
        mArtistsListView.setAdapter(mArtistAdapter);
        mArtistsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCallbacks != null) {
                    ArtistModel artist = mArtistModels.get(position);
                    mCallbacks.onArtistSelected(artist.getSpotifyId(), artist.getName());
                }
            }
        });

        // TODO Add clear button
        EditText searchArtistEditText = (EditText) view.findViewById(R.id.searchArtistEditText);
        searchArtistEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Close keyboard
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    // Execute search
                    searchArtist(v.getText().toString());

                    handled = true;
                }
                return handled;
            }
        });

        mArtistProgressBar = (ProgressBar) view.findViewById(R.id.artistProgressBar);

        return view;
    }

    /**
     * Searches Spotify for artist using Spotify API wrapper.
     *
     * @param artist Name of artist for query
     */
    private void searchArtist(String artist) {
        mArtistProgressBar.setVisibility(View.VISIBLE);

        Map<String, Object> options = new HashMap<>();
        options.put("limit", 10);
        mSpotify.searchArtists(artist, options, new Callback<ArtistsPager>() {
            Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void success(final ArtistsPager artistsPager, Response response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mArtistProgressBar.setVisibility(View.GONE);
                        updateArtists(artistsPager);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mArtistProgressBar.setVisibility(View.GONE);
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
     * Updates mArtistModel list and refreshes the list adapter to display new data.
     *
     * @param artistsPager Results returned from Spotify API
     */
    private void updateArtists(ArtistsPager artistsPager) {
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
            Toast.makeText(getActivity(),
                    getString(R.string.error_no_artists_found),
                    Toast.LENGTH_SHORT)
                    .show();
        }

        mArtistAdapter.notifyDataSetChanged();
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        mArtistsListView.setChoiceMode(activateOnItemClick ?
                ListView.CHOICE_MODE_SINGLE :
                ListView.CHOICE_MODE_NONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ARTIST_MODELS_KEY, mArtistModels);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallbacks = null;
    }

    public interface Callbacks {
        void onArtistSelected(String spotifyId, String artistName);
    }
}
