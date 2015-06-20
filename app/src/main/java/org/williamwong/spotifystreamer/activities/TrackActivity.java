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

    String spotifyId = getIntent().getStringExtra(TrackFragment.SPOTIFY_ID);
    String artistName = getIntent().getStringExtra(TrackFragment.ARTIST_NAME);

    if (savedInstanceState != null) {
      mTrackFragment = (TrackFragment) getSupportFragmentManager().findFragmentByTag(TRACK_FRAGMENT_TAG);
    } else if (mTrackFragment == null) {
      mTrackFragment = TrackFragment.newInstance(spotifyId, artistName);
    }

    getSupportActionBar().setSubtitle(artistName);

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.tracksContainer, mTrackFragment, TRACK_FRAGMENT_TAG);
    ft.commit();

  }

}
