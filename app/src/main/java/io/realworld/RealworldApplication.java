package io.realworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linecorp.armeria.server.Server;

import io.realworld.di.RealworldServerFactory;

final class RealworldApplication {

    private static final Logger logger = LoggerFactory.getLogger(RealworldApplication.class);

    public static void main(String[] args) {
        final RealworldServerFactory factory = RealworldServerFactory.create();
        final Server server = factory.server();

        server.closeOnJvmShutdown();
        server.start().join();

        logger.info("Server has been started. Serving DocService at http://127.0.0.1:{}/docs",
                    server.activeLocalPort());
    }

//    private static void test() {
//        String userName = "root";
//        String password = "root";
//        String url = "jdbc:mysql://localhost:3306/realworld";
//
//        // Connection is the only JDBC resource that we need
//        // PreparedStatement and ResultSet are handled by jOOQ, internally
//        try (Connection conn = DriverManager.getConnection(url, userName, password)) {
//            // ...
//            DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
//            Result<Record> result = create.select().from(TMP).fetch();
//
//            for (Record r : result) {
//                ULID.Value id = r.getValue(TMP.ULID);
//                Boolean isValid = r.getValue(TMP.IS_VALID);
//                LocalDateTime createdAt = r.getValue(TMP.CREATED_AT);
//
//                logger.info("ID: {}, isValid: {}, createdAt: {}", id, isValid, createdAt);
//            }
//        }
//
//        // For the sake of this tutorial, let's keep exception handling simple
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
