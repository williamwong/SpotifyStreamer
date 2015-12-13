package org.williamwong.spotifystreamer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.fragments.TrackFragment;
import org.williamwong.spotifystreamer.services.MusicService;

public class TrackActivity extends AppCompatActivity implements TrackFragment.Callbacks {

    private TrackFragment mTrackFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        Toolbar trackToolbar = (Toolbar) findViewById(R.id.track_toolbar);
        setSupportActionBar(trackToolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Retrieve arguments from intent
        String spotifyId = getIntent().getStringExtra(MainActivity.SPOTIFY_ID_KEY);
        String artistName = getIntent().getStringExtra(MainActivity.ARTIST_NAME_KEY);

        // Find fragment if exists. If not, create new instance of fragment
        if (savedInstanceState != null) {
            mTrackFragment = (TrackFragment) getSupportFragmentManager().findFragmentByTag(MainActivity.TRACK_FRAGMENT_TAG);
        } else if (mTrackFragment == null) {
            mTrackFragment = TrackFragment.newInstance(spotifyId);
        }

        // Set subtitle to be the artist name
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(artistName);
        }

        // Insert fragment into container
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.tracksContainer, mTrackFragment, MainActivity.TRACK_FRAGMENT_TAG);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI
                Intent preferencesIntent = new Intent(this, PreferencesActivity.class);
                startActivity(preferencesIntent);
                return true;

            case R.id.action_now_playing:
                if (MusicService.getMusicService().getCurrentlyPlayingTrackModel() != null) {
                    // User chose the "Now Playing" action, show the PlayerActivity
                    Intent playerIntent = new Intent(this, PlayerActivity.class);
                    startActivity(playerIntent);
                } else {
                    Toast.makeText(this, R.string.error_no_current_track, Toast.LENGTH_SHORT).show();
                }

                return true;

            default:
                // The user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTrackSelected() {
        // Only runs in single pane mode since TrackActivity only exists
        // on a phone.

        Intent playerIntent = new Intent(this, PlayerActivity.class);
        startActivity(playerIntent);
    }
}
