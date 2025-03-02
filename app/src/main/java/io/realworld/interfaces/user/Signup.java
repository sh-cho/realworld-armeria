package io.realworld.interfaces.user;

public interface Signup {
    record Request(UserInternal user) {
        public record UserInternal(String username, String email, String password) {}
    }
}
