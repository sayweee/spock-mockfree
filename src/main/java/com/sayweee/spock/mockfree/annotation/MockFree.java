package com.sayweee.spock.mockfree.annotation;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Author: wangdengwu
 * @Date: 2022/8/27
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
@GroovyASTTransformationClass("com.sayweee.spock.mockfree.transformation.MockFreeASTTransformation")
public @interface MockFree {
    Class<?>[] value() default {};
}
