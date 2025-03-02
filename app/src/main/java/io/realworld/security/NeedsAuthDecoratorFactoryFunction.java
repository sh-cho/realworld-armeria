package io.realworld.security;

import java.util.function.Function;

import com.linecorp.armeria.server.HttpService;
import com.linecorp.armeria.server.annotation.DecoratorFactoryFunction;

import jakarta.inject.Inject;

final class NeedsAuthDecoratorFactoryFunction implements DecoratorFactoryFunction<NeedsAuth> {

    @Inject
    JwtService jwtService;

    @Override
    public Function<? super HttpService, ? extends HttpService> newDecorator(NeedsAuth parameter) {
        return delegate -> {
            final PublicEndpointService maybePublic = delegate.as(PublicEndpointService.class);
            final NeedsAuthService maybeAuthenticated = delegate.as(NeedsAuthService.class);

            if (maybePublic != null || maybeAuthenticated != null) {
                return delegate;
            }

            return new NeedsAuthService(delegate, jwtService);
        };
    }
}
