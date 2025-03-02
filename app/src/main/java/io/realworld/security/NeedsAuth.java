package io.realworld.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.linecorp.armeria.server.annotation.DecoratorFactory;

/**
 * @see <a href="https://blog.dogac.dev/building-an-authorization-framework-with-armeria/">Building an
 * Authorization Framework with Armeria</a>
 */
@DecoratorFactory(NeedsAuthDecoratorFactoryFunction.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface NeedsAuth {
}
