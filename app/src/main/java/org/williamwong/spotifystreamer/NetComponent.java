package org.williamwong.spotifystreamer;

import org.williamwong.spotifystreamer.services.MusicService;
import org.williamwong.spotifystreamer.viewmodels.ArtistViewModel;
import org.williamwong.spotifystreamer.viewmodels.TrackViewModel;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Main application component
 * Created by williamwong on 12/16/15.
 */
@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(ArtistViewModel viewModel);

    void inject(TrackViewModel viewModel);

    void inject(MusicService musicService);
}
