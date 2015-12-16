package org.williamwong.spotifystreamer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import icepick.Icepick;

/**
 * Base fragment that saves and restores instance state
 * Created by williamwong on 12/15/15.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
}
