package com.sayweee.spock.mockfree.extension;

import com.google.auto.service.AutoService;
import com.google.common.collect.Iterables;
import com.sayweee.spock.mockfree.annotation.MockStatic;
import com.sayweee.spock.mockfree.transformer.MockfreeTransformer;
import org.slf4j.Logger;
import org.spockframework.runtime.extension.AbstractMethodInterceptor;
import org.spockframework.runtime.extension.IGlobalExtension;
import org.spockframework.runtime.extension.IMethodInvocation;
import org.spockframework.runtime.model.SpecInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author wangdengwu
 * Date 2022/8/27
 */
@AutoService(IGlobalExtension.class)
public class GlobalExtension implements IGlobalExtension {
    private static final Logger log = getLogger(GlobalExtension.class);

    private static final MockfreeTransformer mockfreeTransformer = MockfreeTransformer.getInstance();
    private final HashMap<String, Map<Class<?>, Set<Method>>> staticMethodsSpecMap = new HashMap<>();

    @Override
    public void visitSpec(SpecInfo spec) {
        String specClassName = spec.getPackage() + "." + spec.getName();
        buildStaticMethods(spec, specClassName);
        if (staticMethodsSpecMap.get(specClassName) == null) {
            return;
        }
        spec.addSetupSpecInterceptor(new AbstractMethodInterceptor() {
            @Override
            public void interceptSetupSpecMethod(IMethodInvocation invocation) throws Throwable {
                Map<Class<?>, Set<Method>> methodsMap = staticMethodsSpecMap.get(specClassName);
                if (methodsMap != null) {
                    Set<Class<?>> targetClasses = methodsMap.keySet();
                    for (Class<?> targetClass : targetClasses) {
                        mockfreeTransformer.mockStaticMethod(methodsMap.get(targetClass), targetClass, Class.forName(specClassName));
                    }
                }
                invocation.proceed();
            }
        });
        spec.addCleanupSpecInterceptor(new AbstractMethodInterceptor() {
            @Override
            public void interceptCleanupSpecMethod(IMethodInvocation invocation) throws Throwable {
                Map<Class<?>, Set<Method>> methodsMap = staticMethodsSpecMap.get(specClassName);
                if (methodsMap != null) {
                    mockfreeTransformer.recoveryClasses(Iterables.toArray(methodsMap.keySet(), Class.class));
                }
                invocation.proceed();
            }
        });
    }

    private void buildStaticMethods(SpecInfo spec, String specClassName) {
        Method[] declaredMethods = spec.getReflection().getDeclaredMethods();
        Map<Class<?>, Set<Method>> methodsMap = new HashMap<>();
        for (Method declaredMethod : declaredMethods) {
            if (Modifier.isStatic(declaredMethod.getModifiers()) && declaredMethod.isAnnotationPresent(MockStatic.class)) {
                MockStatic annotation = declaredMethod.getAnnotation(MockStatic.class);
                Class<?> targetClass = annotation.value();
                if (targetClass != void.class) {
                    Set<Method> methodSet = methodsMap.get(targetClass);
                    if (methodSet == null) {
                        methodSet = new HashSet<>();
                    }
                    methodSet.add(declaredMethod);
                    methodsMap.put(targetClass, methodSet);
                } else {
                    log.info("specClassName:{} use @MockStatic but lost targetClass value", specClassName);
                }
            }
        }
        staticMethodsSpecMap.put(specClassName, methodsMap);
    }

}
