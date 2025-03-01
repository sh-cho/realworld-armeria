package io.realworld.interfaces.user;

import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import io.realworld.jooq.tables.records.UsersRecord;

public interface Login {
    record Request(UserInternal user) {
        public record UserInternal(String email, String password) {}
    }

    record Response(UserInternal user) {
        public record UserInternal(String email, String token, String username, String bio,
                                   @Nullable String image) {}
    }

    @org.mapstruct.Mapper
    abstract class Mapper {
        public static final Mapper INSTANCE = Mappers.getMapper(Mapper.class);

        @Mapping(target = "token", source = "token")
        protected abstract Response.UserInternal toUserInternal(UsersRecord user, String token);

        public Response toLoginResponse(UsersRecord user, String token) {
            return new Response(toUserInternal(user, token));
        }
    }
}
