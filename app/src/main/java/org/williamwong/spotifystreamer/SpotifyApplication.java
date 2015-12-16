package org.williamwong.spotifystreamer;

import android.app.Application;
import android.content.Intent;

import org.williamwong.spotifystreamer.services.MusicService;

/**
 * Launcher class that overrides application to start MusicService
 * Created by williamwong on 11/27/15.
 */
public class SpotifyApplication extends Application {

    private static SpotifyApplication mContext;
    private NetComponent mNetComponent;

    public static SpotifyApplication getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        mNetComponent = org.williamwong.spotifystreamer.DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule())
                .build();

        Intent musicIntent = new Intent(this, MusicService.class);
        startService(musicIntent);
    }

    public NetComponent getNetComponent() {
        return mNetComponent;
    }
}
