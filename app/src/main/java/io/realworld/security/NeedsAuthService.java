package io.realworld.security;

import org.apache.commons.lang3.StringUtils;

import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.HttpService;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.SimpleDecoratingHttpService;

import io.netty.util.AttributeKey;
import jakarta.inject.Inject;

final class NeedsAuthService extends SimpleDecoratingHttpService {

    public static final AttributeKey<String> USER_ID_KEY = AttributeKey.valueOf("userId");

    private final JwtService jwtService;

    @Inject
    NeedsAuthService(HttpService delegate, JwtService jwtService) {
        super(delegate);

        this.jwtService = jwtService;
    }

    @Override
    public HttpResponse serve(final ServiceRequestContext ctx, final HttpRequest req) throws Exception {
        final String token = StringUtils.removeStart(req.headers().get("Authorization"), "Token ");
        if (StringUtils.isEmpty(token)
            || !jwtService.verifyToken(token)) {
            return HttpResponse.of(HttpStatus.UNAUTHORIZED);
        }

        final String userId = jwtService.getUserId(token);
        if (userId == null) {
            // invalid claims
            return HttpResponse.of(HttpStatus.UNAUTHORIZED);
        }

        ctx.setAttr(USER_ID_KEY, userId);

        final HttpService delegate = (HttpService) unwrap();
        return delegate.serve(ctx, req);
    }
}
