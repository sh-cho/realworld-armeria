package io.realworld.di;

import com.linecorp.armeria.server.Server;

import dagger.Component;
import jakarta.inject.Singleton;

@Singleton
@Component(modules = {
        RealworldServerModule.class,
        RepositoryModule.class,
})
public interface RealworldServerFactory {

    Server server();

    static RealworldServerFactory create() {
        return DaggerRealworldServerFactory.create();
    }
}
