package org.williamwong.spotifystreamer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.fragments.TrackFragment;
import org.williamwong.spotifystreamer.models.TrackModel;
import org.williamwong.spotifystreamer.services.MusicService;

import java.util.ArrayList;
import java.util.List;

public class TrackActivity extends AppCompatActivity implements TrackFragment.Callbacks {

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
    public void onTrackSelected(List<TrackModel> trackModels, int position) {
        // Only runs in single pane mode since TrackActivity only exists
        // on a phone.

        Intent musicIntent = new Intent(this, MusicService.class);
        musicIntent.putParcelableArrayListExtra(MusicService.EXTRA_TRACK_MODELS, (ArrayList<TrackModel>) trackModels);
        musicIntent.putExtra(MusicService.EXTRA_CURRENT_TRACK, position);
        startService(musicIntent);

//        TrackModel trackModel = trackModels.get(position);
//        Intent playerIntent = new Intent(this, PlayerActivity.class);
//        playerIntent.putExtra(MainActivity.TRACK_MODEL_KEY, trackModel);
//        startActivity(playerIntent);
    }
}
