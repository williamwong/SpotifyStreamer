package org.williamwong.spotifystreamer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.fragments.ArtistFragment;

public class MainActivity extends AppCompatActivity implements ArtistFragment.Callbacks {

    public static final String SPOTIFY_ID_KEY = "spotifyId";
    public static final String ARTIST_NAME_KEY = "artistName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        Intent trackIntent = new Intent(this, TrackActivity.class);
        trackIntent.putExtra(SPOTIFY_ID_KEY, spotifyId);
        trackIntent.putExtra(ARTIST_NAME_KEY, artistName);
        startActivity(trackIntent);
    }
}
