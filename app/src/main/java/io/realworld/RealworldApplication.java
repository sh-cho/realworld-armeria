package io.realworld;

import static io.realworld.Tables.VINCE_TMP;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.docs.DocService;

import de.huxhorn.sulky.ulid.ULID;

public class RealworldApplication {

    private static final Logger logger = LoggerFactory.getLogger(RealworldApplication.class);

    public static void main(String[] args) {
        test();

//        final Server server = newServer(8080);
//        server.closeOnJvmShutdown();
//        server.start().join();
//
//        logger.info("Server has been started. Serving DocService at http://127.0.0.1:{}/docs",
//                    server.activeLocalPort());
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

    private static void test() {
        String userName = "root";
        String password = "root";
        String url = "jdbc:mysql://localhost:3306/realworld";

        // Connection is the only JDBC resource that we need
        // PreparedStatement and ResultSet are handled by jOOQ, internally
        try (Connection conn = DriverManager.getConnection(url, userName, password)) {
            // ...
            DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
            Result<Record> result = create.select().from(VINCE_TMP).fetch();

            for (Record r : result) {
                ULID.Value id = r.getValue(VINCE_TMP.ULID);
                Boolean isValid = r.getValue(VINCE_TMP.IS_VALID);
                LocalDateTime createdAt = r.getValue(VINCE_TMP.CREATED_AT);

                logger.info("ID: {}, isValid: {}, createdAt: {}", id, isValid, createdAt);
            }
        }

        // For the sake of this tutorial, let's keep exception handling simple
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
