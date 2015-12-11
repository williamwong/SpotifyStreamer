package org.williamwong.spotifystreamer.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.williamwong.spotifystreamer.R;

public class PreferencesFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}