package io.realworld.interfaces.user;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.annotation.Attribute;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.server.annotation.Param;
import com.linecorp.armeria.server.annotation.Post;
import com.linecorp.armeria.server.annotation.ProducesJson;

import de.huxhorn.sulky.ulid.ULID;
import io.realworld.security.JwtService;
import io.realworld.security.NeedsAuth;
import io.realworld.domain.user.UserRepository;
import io.realworld.jooq.tables.records.UsersRecord;
import io.realworld.security.PublicEndpoint;
import jakarta.inject.Inject;

@NeedsAuth
public final class UserController {

    private final UserRepository userRepository;
    private final ULID ulid;
    private final JwtService jwtService;

    private static final UserMapper userMapper = UserMapper.INSTANCE;

    @Inject
    public UserController(final UserRepository userRepository, final ULID ulid, final JwtService jwtService) {
        this.userRepository = userRepository;
        this.ulid = ulid;
        this.jwtService = jwtService;
    }

    // temp
    @Get("/users/find")
    @PublicEndpoint
    public HttpResponse getUserByEmail(@Param String email) {
        final UsersRecord user = userRepository.findByEmail(email);
        if (user == null) {
            return HttpResponse.of(HttpStatus.NOT_FOUND);
        }

        return HttpResponse.ofJson(userMapper.toLoginResponse(user, "temp"));
    }

    @Post("/users")
    @ProducesJson
    public Login.Response signUp(Signup.Request request) {
        final UsersRecord user = userMapper.toUsersRecord(request.user(), ulid.nextValue());
        userRepository.save(user);

        return userMapper.toLoginResponse(user, "temp");
    }

    @Post("/users/login")
    @PublicEndpoint
    @ProducesJson
    public Login.Response login(Login.Request request) {
        final UsersRecord user = userRepository.findByEmail(request.user().email());
        if (user == null || !user.getPassword().equals(request.user().password())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return userMapper.toLoginResponse(user, jwtService.createToken(user.getId().toString()));
    }

    @Get("/user")
    @ProducesJson
    public Login.Response getCurrentUser(@Attribute("userId") String userId) {
        final UsersRecord user = userRepository.findById(ULID.parseULID(userId));
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        return userMapper.toLoginResponse(user, "temp1");
    }
}
