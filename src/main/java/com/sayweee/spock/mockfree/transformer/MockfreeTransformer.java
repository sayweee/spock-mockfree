package com.sayweee.spock.mockfree.transformer;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.ModifierAdjustment;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.MethodManifestation;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;
import org.slf4j.Logger;
import org.spockframework.runtime.extension.ExtensionException;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Set;

import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;
import static net.bytebuddy.agent.builder.AgentBuilder.TypeStrategy.Default.REDEFINE;
import static net.bytebuddy.description.modifier.Visibility.PUBLIC;
import static net.bytebuddy.matcher.ElementMatchers.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author wangdengwu
 * Date 2022/8/27
 */
public class MockfreeTransformer {
    public static final String CLASS_NAME = "MockFreeTargetClasses";
    public static final String PACKAGE_PATH = "com.sayweee.spock.mockfree";
    public static final String TARGET_CLASSES = "targetClasses";
    private static final Logger log = getLogger(MockfreeTransformer.class);

    private static Instrumentation instrumentation;

    private static MockfreeTransformer instance;

    public static synchronized MockfreeTransformer getInstance() {
        if (instance == null) {
            instance = new MockfreeTransformer();
        }
        return instance;
    }

    private MockfreeTransformer() {
        log.info("Activating @MockFree transformation");
        instrumentation = ByteBuddyAgent.install();
        installTransformer();
    }

    private static void installTransformer() {
        try {
            Class<?> clazz = Class.forName(PACKAGE_PATH + "." + CLASS_NAME);
            Field declaredField = clazz.getDeclaredField(TARGET_CLASSES);
            buildAndInstallTransformer((String) declaredField.get(null));
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            log.warn("installTransformer error {}", e.getMessage());
        }
    }

    private static void buildAndInstallTransformer(final String classesString) {
        new AgentBuilder.Default(new ByteBuddy().with(TypeValidation.DISABLED))
                .ignore(none())
                .with(new InstallationListener())
                .with(new DiscoveryListener())
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(RETRANSFORMATION)
                .with(REDEFINE)
                .type(typeDescription -> isTransformable(classesString, typeDescription))
                .transform(MockfreeTransformer::transform)
                .installOn(instrumentation);
    }

    private static boolean isTransformable(final String classesString, final TypeDescription typeDescription) {
        return isInClasses(classesString, typeDescription);
    }

    private static boolean isInClasses(String classesString, TypeDescription typeDescription) {
        if (classesString.isEmpty()) {
            return false;
        }
        return Arrays.stream(classesString.split(",")).anyMatch(s -> typeDescription.getName().equals(s));
    }

    public void mockStaticMethod(Set<Method> methodsSet, Class<?> targetClass, Class<?> specClass) {
        DynamicType.Builder<?> builder = new ByteBuddy().redefine(targetClass);
        for (Method method : methodsSet) {
            builder = builder
                    .method(named(method.getName()).and(takesArguments(method.getParameterTypes())))
                    .intercept(MethodDelegation.to(specClass));
        }
        builder.make()
                .load(targetClass.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }

    public void recoveryClasses(Class<?>[] mockFreeClasses) throws IOException {
        for (Class<?> targetClass : mockFreeClasses) {
            ClassReloadingStrategy.of(instrumentation).reset(targetClass);
        }
    }

    private static DynamicType.Builder<?> transform(final DynamicType.Builder<?> builder, final TypeDescription typeDefinitions, final ClassLoader classLoader, final JavaModule javaModule, final ProtectionDomain protectionDomain) {
        return builder.visit(new ModifierAdjustment().withMethodModifiers(isPrivate(), PUBLIC))
                .visit(new ModifierAdjustment().withMethodModifiers(isFinal(), MethodManifestation.PLAIN))
                .visit(new ModifierAdjustment().withFieldModifiers(isFinal(), FieldManifestation.PLAIN))
                .visit(new ModifierAdjustment().withTypeModifiers(isFinal(), TypeManifestation.PLAIN));
    }

    private static class InstallationListener extends AgentBuilder.InstallationListener.Adapter {
        @Override
        public Throwable onError(final Instrumentation instrumentation, final ResettableClassFileTransformer classFileTransformer, final Throwable throwable) {
            throw new ExtensionException("Unable install mockfree transformation", throwable);
        }
    }

    private static class DiscoveryListener extends AgentBuilder.Listener.Adapter {
        @Override
        public void onError(final String typeName, final ClassLoader classLoader, final JavaModule module, final boolean loaded, final Throwable throwable) {
            log.warn("Could not transform class '{}', loaded: {}", typeName, loaded, throwable);
        }

        @Override
        public void onComplete(final String typeName, final ClassLoader classLoader, final JavaModule module, final boolean loaded) {
            log.trace("Processed class '{}', loaded: {}", typeName, loaded);
        }

        @Override
        public void onDiscovery(final String typeName, final ClassLoader classLoader, final JavaModule module, final boolean loaded) {
            log.trace("Processing class '{}', loaded: {}", typeName, loaded);
        }

        @Override
        public void onTransformation(final TypeDescription typeDescription, final ClassLoader classLoader, final JavaModule module, final boolean loaded, final DynamicType dynamicType) {
            log.debug("Transforming class '{}', loaded: {}", typeDescription, loaded);
        }

        @Override
        public void onIgnored(final TypeDescription typeDescription, final ClassLoader classLoader, final JavaModule module, final boolean loaded) {
            log.trace("Ignoring class '{}', loaded: {}", typeDescription, loaded);
        }
    }
}
