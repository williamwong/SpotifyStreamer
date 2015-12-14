package org.williamwong.spotifystreamer.viewmodels;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.williamwong.spotifystreamer.R;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * View model for artist search interface
 * Created by williamwong on 12/13/15.
 */
public class ArtistViewModel {

    public ObservableBoolean isLoading = new ObservableBoolean(false);
    public ObservableField<String> searchArtistQuery = new ObservableField<>("");

//    public boolean onSearchAction(TextView view, int actionId, KeyEvent event) {
//        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//            // Close keyboard
//            ((InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
//                    .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//
//            // Execute search
//            if (searchArtistQuery.get().length() > 0) searchArtist(searchArtistQuery.get());
//
//            return true;
//        }
//        return false;
//    }
//
//    private void searchArtist(String artist) {
//        isLoading.set(true);
//
//        Map<String, Object> options = new HashMap<>();
//        options.put("limit", 10);
//        SpotifyService spotify = new SpotifyApi().getService();
//        spotify.searchArtists(artist, options, new Callback<ArtistsPager>() {
//
//            @Override
//            public void success(final ArtistsPager artistsPager, Response response) {
//                isLoading.set(false);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                Log.d("ArtistViewMode", "No artists found");
//            }
//        });
//    }

    public TextWatcher searchArtistQueryWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!searchArtistQuery.get().equals(s.toString())) {
                searchArtistQuery.set(s.toString());
            }
        }
    };

}
