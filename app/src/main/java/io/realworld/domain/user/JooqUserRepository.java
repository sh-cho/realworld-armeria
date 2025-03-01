package io.realworld.domain.user;

import static io.realworld.jooq.Tables.USERS;

import org.jooq.DSLContext;
import org.jspecify.annotations.Nullable;

import de.huxhorn.sulky.ulid.ULID;
import io.realworld.jooq.tables.records.UsersRecord;
import jakarta.inject.Inject;

public class JooqUserRepository implements UserRepository {

    private final DSLContext ctx;

    @Inject
    public JooqUserRepository(final DSLContext ctx) {
        this.ctx = ctx;
    }

    @Nullable
    @Override
    public UsersRecord findById(final ULID.Value ulid) {
        return ctx.fetchOne(USERS, USERS.ID.eq(ulid));
    }

    @Nullable
    @Override
    public UsersRecord findByEmail(final String email) {
        return ctx.fetchOne(USERS, USERS.EMAIL.eq(email));
    }

    @Override
    public UsersRecord save(UsersRecord user) {
        final UsersRecord usersRecord = ctx.newRecord(USERS);
        usersRecord.from(user);
        usersRecord.store();

        return usersRecord;
    }

    @Override
    public void update(final UsersRecord user) {
        user.update();
    }
}
