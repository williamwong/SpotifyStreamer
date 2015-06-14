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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
public class SearchArtistFragment extends Fragment {

  public static final int MIN_THUMBNAIL_WIDTH = 200;

  private SpotifyService mSpotify = new SpotifyApi().getService();
  private EditText mSearchArtistEditText;
  private ListView mArtistsListView;

  public SearchArtistFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_main, container, false);

    mSearchArtistEditText = (EditText) view.findViewById(R.id.searchArtistEditText);
    mArtistsListView = (ListView) view.findViewById(R.id.artistsListView);

    mSearchArtistEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
          // Execute search
          searchArtist(v.getText().toString());

          // Close keyboard
          ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
              .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

          handled = true;
        }
        return handled;
      }
    });

    return view;
  }

  private void searchArtist(String artist) {
    Map<String, Object> options = new HashMap<>();
    options.put("limit", 10);
    mSpotify.searchArtists(artist, options, new Callback<ArtistsPager>() {
      Handler handler = new Handler(Looper.getMainLooper());
      @Override
      public void success(final ArtistsPager artistsPager, Response response) {
        handler.post(new Runnable() {
          @Override
          public void run() {
            updateArtists(artistsPager);
          }
        });
      }

      @Override
      public void failure(RetrofitError error) {

      }
    });
  }

  private void updateArtists(ArtistsPager artistsPager) {
    List<ArtistModel> artistModels = new ArrayList<>();
    for (Artist artist : artistsPager.artists.items) {

      ArtistModel artistModel = new ArtistModel();

      artistModel.setName(artist.name);

      List<Image> images = artist.images;
      if(images != null && !images.isEmpty()) {
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

      artistModels.add(artistModel);
    }

    ArtistAdapter artistAdapter = new ArtistAdapter(getActivity(), 0, artistModels);
    mArtistsListView.setAdapter(artistAdapter);
  }
}
