package io.realworld.domain.user;

import org.jspecify.annotations.Nullable;

import de.huxhorn.sulky.ulid.ULID;
import io.realworld.jooq.tables.records.UsersRecord;

public interface UserRepository {

    @Nullable
    UsersRecord findById(ULID.Value ulid);

    @Nullable
    UsersRecord findByEmail(String email);

    UsersRecord save(UsersRecord user);

    void update(UsersRecord user);
}
