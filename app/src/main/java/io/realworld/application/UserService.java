package io.realworld.application;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.server.annotation.Param;

import io.realworld.domain.user.UserRepository;
import io.realworld.interfaces.user.Login;
import io.realworld.jooq.tables.records.UsersRecord;
import jakarta.inject.Inject;

public final class UserService {

    private final UserRepository userRepository;

    @Inject
    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // temp
    @Get("/find")
    public HttpResponse getUserByEmail(@Param("email") String email) {
        final UsersRecord user = userRepository.findByEmail(email);
        if (user == null) {
            return HttpResponse.of(HttpStatus.NOT_FOUND);
        }

        return HttpResponse.ofJson(Login.Mapper.INSTANCE.toLoginResponse(user, "temp"));
    }

    @Get("/test1")
    public HttpResponse test1() {
        return HttpResponse.of("test1");
    }
}
