package com.sayweee.spock.mockfree.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author wangdengwu
 * Date 2022/8/27
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface MockStatic {
    Class<?> value() default void.class;

    String alias() default "";
}
