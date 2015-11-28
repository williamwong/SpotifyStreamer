package org.williamwong.spotifystreamer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.fragments.ArtistFragment;
import org.williamwong.spotifystreamer.fragments.PlayerFragment;
import org.williamwong.spotifystreamer.fragments.TrackFragment;

public class MainActivity extends AppCompatActivity implements ArtistFragment.Callbacks, TrackFragment.Callbacks {

    public static final String SPOTIFY_ID_KEY = "spotifyId";
    public static final String ARTIST_NAME_KEY = "artistName";
    public static final String TRACK_MODEL_KEY = "trackModel";
    public static final String TRACK_FRAGMENT_TAG = "trackFragment";
    public static final String PLAYER_FRAGMENT_DIALOG_TAG = "playerFragment";

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

    @Override
    public void onTrackSelected() {
        // Only runs in two pane mode, since MainActivity will only host the
        // TrackFragment if on a tablet.

        PlayerFragment playerFragment = PlayerFragment.newInstance();
        playerFragment.show(getSupportFragmentManager(), PLAYER_FRAGMENT_DIALOG_TAG);
        Log.d("MainActivity", "Open PlayerFragment");
    }
}
