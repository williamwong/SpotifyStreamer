package org.williamwong.spotifystreamer.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.fragments.PlayerFragment;

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

}
