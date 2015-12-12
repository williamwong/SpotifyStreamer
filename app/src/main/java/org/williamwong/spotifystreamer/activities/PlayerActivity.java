package org.williamwong.spotifystreamer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.fragments.PlayerFragment;
import org.williamwong.spotifystreamer.models.TrackModel;
import org.williamwong.spotifystreamer.services.MusicService;

public class PlayerActivity extends AppCompatActivity {

    private PlayerFragment mPlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Toolbar playerToolbar = (Toolbar) findViewById(R.id.player_toolbar);
        setSupportActionBar(playerToolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find fragment if exists. If not, create new instance of fragment
        if (savedInstanceState != null) {
            mPlayerFragment = (PlayerFragment) getSupportFragmentManager()
                    .findFragmentByTag(MainActivity.PLAYER_FRAGMENT_DIALOG_TAG);
        } else if (mPlayerFragment == null) {
            mPlayerFragment = new PlayerFragment();
        }

        // Insert fragment into container
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.playerContainer, mPlayerFragment, MainActivity.PLAYER_FRAGMENT_DIALOG_TAG);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                TrackModel track = MusicService.getMusicService().getCurrentlyPlayingTrackModel();
                if (track != null) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TITLE, track.getTrackName());
                    shareIntent.putExtra(Intent.EXTRA_TEXT, track.getExternalUrl());
                    startActivity(shareIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
