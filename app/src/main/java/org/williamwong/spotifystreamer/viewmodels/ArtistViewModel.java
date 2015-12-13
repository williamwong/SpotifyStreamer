package org.williamwong.spotifystreamer.viewmodels;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * View model for artist search interface
 * Created by williamwong on 12/13/15.
 */
public class ArtistViewModel {

    public ObservableBoolean isLoading = new ObservableBoolean(false);
    public ObservableField<String> searchArtistQuery = new ObservableField<>("");

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
