package org.williamwong.spotifystreamer.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.fragments.TrackFragment;

public class TrackActivity extends AppCompatActivity {

    private static final String TRACK_FRAGMENT_TAG = "trackFragment";
    private TrackFragment mTrackFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        // Retrieve arguments from intent
        String spotifyId = getIntent().getStringExtra(MainActivity.SPOTIFY_ID_KEY);
        String artistName = getIntent().getStringExtra(MainActivity.ARTIST_NAME_KEY);

        // Find fragment if exists. If not, create new instance of fragment
        if (savedInstanceState != null) {
            mTrackFragment = (TrackFragment) getSupportFragmentManager().findFragmentByTag(TRACK_FRAGMENT_TAG);
        } else if (mTrackFragment == null) {
            mTrackFragment = TrackFragment.newInstance(spotifyId);
        }

        // Set subtitle to be the artist name
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(artistName);
        }

        // Insert fragment into container
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.tracksContainer, mTrackFragment, TRACK_FRAGMENT_TAG);
        ft.commit();
    }

}
