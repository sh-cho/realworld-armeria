package io.realworld.security;

import java.util.function.Function;

import com.linecorp.armeria.server.HttpService;
import com.linecorp.armeria.server.annotation.DecoratorFactoryFunction;

final class PublicEndpointDecoratorFactoryFunction implements DecoratorFactoryFunction<PublicEndpoint> {

    @Override
    public Function<? super HttpService, ? extends HttpService> newDecorator(PublicEndpoint parameter) {
        return PublicEndpointService::new;
    }
}
