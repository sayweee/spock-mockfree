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
    private final HashMap<String, Map<String, Class<?>>> staticMethodsSpecMap = new HashMap<>();

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
                Map<String, Class<?>> methodMap = staticMethodsSpecMap.get(specClassName);
                if (methodMap != null) {
                    Set<String> methodNameSet = methodMap.keySet();
                    for (String methodName : methodNameSet) {
                        mockfreeTransformer.mockStaticMethod(methodName, methodMap.get(methodName), Class.forName(specClassName));
                    }
                }
                invocation.proceed();
            }
        });
        spec.addCleanupSpecInterceptor(new AbstractMethodInterceptor() {
            @Override
            public void interceptCleanupSpecMethod(IMethodInvocation invocation) throws Throwable {
                Map<String, Class<?>> methodMap = staticMethodsSpecMap.get(specClassName);
                if (methodMap != null) {
                    mockfreeTransformer.recoveryClasses(Iterables.toArray(methodMap.values(), Class.class));
                }
                invocation.proceed();
            }
        });
    }

    private void buildStaticMethods(SpecInfo spec, String specClassName) {
        Method[] declaredMethods = spec.getReflection().getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (Modifier.isStatic(declaredMethod.getModifiers()) && declaredMethod.isAnnotationPresent(MockStatic.class)) {
                String methodName = declaredMethod.getName();
                MockStatic annotation = declaredMethod.getAnnotation(MockStatic.class);
                Class<?> targetClass = annotation.value();
                if (targetClass != void.class) {
                    String alias = annotation.alias();
                    Map<String, Class<?>> methodMap = new HashMap<>();
                    methodMap.put(alias.isEmpty() ? methodName : alias, targetClass);
                    staticMethodsSpecMap.put(specClassName, methodMap);
                } else {
                    log.info("specClassName:{} use @MockStatic but lost targetClass value", specClassName);
                }
            }
        }
    }

}
