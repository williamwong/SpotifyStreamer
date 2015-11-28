package org.williamwong.spotifystreamer;

import android.app.Application;
import android.content.Intent;

import org.williamwong.spotifystreamer.services.MusicService;

/**
 * This is a
 * Created by williamwong on 11/27/15.
 */
public class SpotifyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Intent musicIntent = new Intent(this, MusicService.class);
        startService(musicIntent);
    }
}
