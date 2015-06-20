package org.williamwong.spotifystreamer.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

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

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.tracksContainer, mTrackFragment, TRACK_FRAGMENT_TAG);
    ft.commit();

  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_track, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
