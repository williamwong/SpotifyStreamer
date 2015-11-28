package org.williamwong.spotifystreamer.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.fragments.PlayerFragment;
import org.williamwong.spotifystreamer.models.TrackModel;

public class PlayerActivity extends AppCompatActivity {

    private PlayerFragment mPlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Find fragment if exists. If not, create new instance of fragment
        if (savedInstanceState != null) {
            mPlayerFragment = (PlayerFragment) getSupportFragmentManager()
                    .findFragmentByTag(MainActivity.PLAYER_FRAGMENT_DIALOG_TAG);
        } else if (mPlayerFragment == null) {
            mPlayerFragment = PlayerFragment.newInstance();
        }

        // Insert fragment into container
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.playerContainer, mPlayerFragment, MainActivity.PLAYER_FRAGMENT_DIALOG_TAG);
        ft.commit();
    }

}
