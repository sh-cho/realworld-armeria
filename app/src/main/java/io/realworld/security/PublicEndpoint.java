package io.realworld.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.linecorp.armeria.server.annotation.DecoratorFactory;

/**
 * Just marker annotation for public endpoints
 *
 * @see NeedsAuth
 */
@DecoratorFactory(PublicEndpointDecoratorFactoryFunction.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD})
public @interface PublicEndpoint {
}
