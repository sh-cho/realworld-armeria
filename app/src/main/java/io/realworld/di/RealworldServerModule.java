package io.realworld.di;

import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.logging.AccessLogWriter;

import dagger.Module;
import dagger.Provides;
import de.huxhorn.sulky.ulid.ULID;
import io.realworld.interfaces.user.UserController;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Module
interface RealworldServerModule {

    @Singleton
    @Provides
    static Server server(@Named("port") int port, UserController userController) {
        final ServerBuilder sb = Server.builder();

        sb.http(port)
          .accessLogWriter(AccessLogWriter.common(), true)
          .annotatedService("/api", userController)
          .serviceUnder("/docs", new DocService());

        return sb.build();
    }

    @Named("port")
    @Provides
    static int port() {
        return 8080;
    }

    @Provides
    static ULID ulid() {
        return new ULID();
    }
}
