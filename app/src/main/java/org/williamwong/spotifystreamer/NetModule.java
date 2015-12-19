package org.williamwong.spotifystreamer;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * Net module for Dagger 2
 * Created by williamwong on 12/16/15.
 */
@Module
public class NetModule {

    public NetModule() {
    }

    @Provides
    @Singleton
        // Application reference must come from AppModule.class
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    SpotifyService providesSpotifyService() {
        return new SpotifyApi().getService();
    }
}
