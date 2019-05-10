package io.github.anycollect.extensions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Extension {
    String name();

    Class<?> point();

    AutoLoad autoload() default @Extension.AutoLoad(instanceName = "", enabled = false);

    @interface AutoLoad {
        String instanceName();

        InjectMode injectMode() default InjectMode.AUTO;

        boolean enabled() default true;
    }
}
