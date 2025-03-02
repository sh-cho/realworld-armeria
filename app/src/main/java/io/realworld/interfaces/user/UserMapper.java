package io.realworld.interfaces.user;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import de.huxhorn.sulky.ulid.ULID;
import io.realworld.jooq.tables.records.UsersRecord;

@Mapper
public abstract class UserMapper {
    public static final UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "token", source = "token")
    protected abstract Login.Response.UserInternal toUserInternal(UsersRecord user, String token);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "password", source = "user.password")
    @Mapping(target = "id", source = "id")
    public abstract UsersRecord toUsersRecord(Signup.Request.UserInternal user, ULID.Value id);

    public Login.Response toLoginResponse(UsersRecord user, String token) {
        return new Login.Response(toUserInternal(user, token));
    }
}
