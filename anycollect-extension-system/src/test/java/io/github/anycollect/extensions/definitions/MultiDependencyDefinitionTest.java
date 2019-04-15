package io.github.anycollect.extensions.definitions;

import io.github.anycollect.extensions.Definition;
import io.github.anycollect.extensions.Instance;
import io.github.anycollect.extensions.dependencies.MultiDependencyDefinition;
import io.github.anycollect.extensions.utils.ConstrictorUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class MultiDependencyDefinitionTest {
    @Test
    @DisplayName("instances of multiple dependency can have different definitions")
    void instancesCanHaveDifferentDefinitions() {
        Definition dependency1Def = Definition.builder()
                .withName("One")
                .withExtension(Object.class, ConstrictorUtils.createFor(Object.class))
                .build();
        Definition dependency2Def = Definition.builder()
                .withName("Two")
                .withExtension(Object.class, ConstrictorUtils.createFor(Object.class))
                .build();

        Instance dependency1 = dependency1Def.createInstance("one");
        Instance dependency2 = dependency2Def.createInstance("two");

        MultiDependencyDefinition definition = new MultiDependencyDefinition("test", Object.class, false, 0);

        Assertions.assertDoesNotThrow(() -> definition.create(Arrays.asList(dependency1, dependency2)));
    }
}