package org.williamwong.spotifystreamer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.fragments.ArtistFragment;
import org.williamwong.spotifystreamer.fragments.TrackFragment;

public class MainActivity extends AppCompatActivity implements ArtistFragment.Callbacks {

    public static final String SPOTIFY_ID_KEY = "spotifyId";
    public static final String ARTIST_NAME_KEY = "artistName";
    public static final String TRACK_FRAGMENT_TAG = "trackFragment";

    private boolean mIsTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout tracksContainer = (FrameLayout) findViewById(R.id.tracksContainer);
        if (tracksContainer != null) {
            mIsTwoPane = true;
            ArtistFragment artistFragment = (ArtistFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.artist_fragment);
            if (artistFragment != null) {
                artistFragment.setActivateOnItemClick(true);
            }
        }
    }

    /**
     * Handles artist selected and start new activity with track results.
     * This is a callback from the {@link org.williamwong.spotifystreamer.fragments.ArtistFragment.Callbacks}
     * class.
     *
     * @param spotifyId  a Spotify id representing the artist
     * @param artistName the name of the artist
     */
    @Override
    public void onArtistSelected(String spotifyId, String artistName) {
        if (mIsTwoPane) {
            TrackFragment trackFragment = TrackFragment.newInstance(spotifyId);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.tracksContainer, trackFragment, TRACK_FRAGMENT_TAG);
            ft.commit();
        } else {
            Intent trackIntent = new Intent(this, TrackActivity.class);
            trackIntent.putExtra(SPOTIFY_ID_KEY, spotifyId);
            trackIntent.putExtra(ARTIST_NAME_KEY, artistName);
            startActivity(trackIntent);
        }
    }
}
