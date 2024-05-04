package io.realworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.docs.DocService;

public class RealworldApplication {

    private static final Logger logger = LoggerFactory.getLogger(RealworldApplication.class);

    public static void main(String[] args) {
        final Server server = newServer(8080);

        server.closeOnJvmShutdown();

        server.start().join();

        logger.info("Server has been started. Serving DocService at http://127.0.0.1:{}/docs",
                    server.activeLocalPort());
    }

    private static Server newServer(int port) {
        final ServerBuilder sb = Server.builder();
        sb.http(8080);
        configureServices(sb);
        return sb.build();
    }

    private static void configureServices(final ServerBuilder sb) {
        sb.serviceUnder("/docs", new DocService());
    }
}
