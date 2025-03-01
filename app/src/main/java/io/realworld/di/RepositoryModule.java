package io.realworld.di;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.realworld.domain.user.JooqUserRepository;
import io.realworld.domain.user.UserRepository;

@Module
interface RepositoryModule {

    @Provides
    static DataSource hikariDataSource() {
        // TODO: make it configurable (AssistedInject?)
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3307/realworld");
        config.setUsername("root");
        config.setPassword("root");
        config.setMaximumPoolSize(10);

        return new HikariDataSource(config);
    }

    @Provides
    static DSLContext dslContext(DataSource dataSource) {
        return DSL.using(dataSource, SQLDialect.MYSQL);
    }

    @Binds
    UserRepository userRepository(JooqUserRepository userRepository);
}
