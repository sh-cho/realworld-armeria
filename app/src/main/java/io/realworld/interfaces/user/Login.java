package io.realworld.interfaces.user;

import org.jspecify.annotations.Nullable;

interface Login {
    record Request(UserInternal user) {
        record UserInternal(String email, String password) {}
    }

    record Response(UserInternal user) {
        record UserInternal(String email, String token, String username, String bio,
                                   @Nullable String image) {}
    }
}
