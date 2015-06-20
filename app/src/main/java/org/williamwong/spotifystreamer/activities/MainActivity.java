package org.williamwong.spotifystreamer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.fragments.ArtistFragment;
import org.williamwong.spotifystreamer.fragments.TrackFragment;

public class MainActivity extends AppCompatActivity implements ArtistFragment.Callbacks{

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override
  public void onArtistSelected(String spotifyId, String artistName) {
    Intent trackIntent = new Intent(this, TrackActivity.class);
    trackIntent.putExtra(TrackFragment.SPOTIFY_ID, spotifyId);
    trackIntent.putExtra(TrackFragment.ARTIST_NAME, artistName);
    startActivity(trackIntent);
  }
}
