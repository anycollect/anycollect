package io.github.anycollect.extensions.definitions;

import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.extensions.Definition;
import io.github.anycollect.extensions.Instance;
import io.github.anycollect.extensions.dependencies.ConfigDefinition;
import io.github.anycollect.extensions.dependencies.MultiDependencyDefinition;
import io.github.anycollect.extensions.dependencies.SingleDependencyDefinition;
import io.github.anycollect.extensions.exceptions.ExtensionCreationException;
import io.github.anycollect.extensions.utils.ConstrictorUtils;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Definition tests")
class DefinitionTest {
    @Test
    @DisplayName("fail fast if null was passed to builder")
    void nullsIsForbidden() {
        Assertions.assertThrows(NullPointerException.class, () -> Definition.builder().withName(null));
        Assertions.assertThrows(NullPointerException.class,
                () -> Definition.builder().withExtension((Class) null, ConstrictorUtils.createFor(Extension.class)));
        Assertions.assertThrows(NullPointerException.class,
                () -> Definition.builder().withExtension(ExtensionPoint.class, null));
    }

    @Test
    @DisplayName("extension class must implement extension point class")
    @SuppressWarnings("unchecked")
    void extensionClassMustImplementExtensionPointClass() {
        Assertions.assertDoesNotThrow(() -> Definition.builder().withExtension(ExtensionPoint.class, ConstrictorUtils.createFor(Extension.class)));
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    Class spec = ExtensionPoint.class;
                    Class impl = FakeExtension.class;
                    Definition.builder().withExtension(spec, ConstrictorUtils.createFor(impl));
                });
        assertThat(ex).hasMessageContaining(FakeExtension.class.getName())
                .hasMessageContaining(FakeExtension.class.getName())
                .hasMessageContaining("implement");
    }

    @Test
    @DisplayName("config class is optional property")
    void configClassIsOptional() {
        Assertions.assertDoesNotThrow(
                () -> {
                    Definition definition = Definition.builder()
                            .withName("name")
                            .withExtension(ExtensionPoint.class, ConstrictorUtils.createFor(Extension.class))
                            .build();
                    assertThat(definition.getConfigDefinition()).isEmpty();
                });
    }

    @Test
    @DisplayName("name, extension point class, extension class is required")
    void allRequiredPropertiesMustBeSpecified() {
        IllegalStateException ex1 = Assertions.assertThrows(IllegalStateException.class,
                () -> Definition.builder().withName("nonnull").build());
        assertThat(ex1).hasMessageContaining("extension classes must be specified");
        IllegalStateException ex2 = Assertions.assertThrows(IllegalStateException.class,
                () -> Definition.builder().withExtension(ExtensionPoint.class, ConstrictorUtils.createFor(Extension.class)).build());
        assertThat(ex2).hasMessageContaining("name must be specified");
    }

    @Test
    @DisplayName("not given required dependency when creating new instance definition then fail")
    void allDependenciesMustBePassedForCreation() {
        Definition definition = Definition.builder()
                .withName("Test")
                .withExtension(ExtensionPoint.class, ConstrictorUtils.createFor(Extension.class, ExtensionPoint.class))
                .withSingleDependency(new SingleDependencyDefinition("delegate", ExtensionPoint.class, false, 0))
                .build();
        ConfigurationException ex = Assertions.assertThrows(ConfigurationException.class,
                () -> definition.createInstance("test"));

        assertThat(ex).hasMessageContaining("delegate");
    }

    @Test
    @DisplayName("optional dependency can be not passed to create new instance definition")
    void optionalDependenciesIsNotRequiredForCreation() {
        Definition definition = Definition.builder()
                .withName("Test")
                .withExtension(ExtensionPoint.class, ConstrictorUtils.createFor(Extension.class, ExtensionPoint.class))
                .withSingleDependency(new SingleDependencyDefinition("delegate", ExtensionPoint.class, true, 0))
                .build();
        Assertions.assertDoesNotThrow(() -> definition.createInstance("test"));
    }

    @Test
    @DisplayName("extension point class and extension class must be equal")
    void ExtensionPointClassAndExtensionClassMustBeEqual() {
        Definition dependencyDef = Definition.builder()
                .withName("Dependency")
                .withExtension(Object.class, ConstrictorUtils.createFor(Object.class))
                .build();
        Instance wrongDelegate = dependencyDef.createInstance("dep");
        Definition definition = Definition.builder()
                .withName("Test")
                .withExtension(ExtensionPoint.class, ConstrictorUtils.createFor(Extension.class, ExtensionPoint.class))
                .withSingleDependency(new SingleDependencyDefinition("delegate", ExtensionPoint.class, false, 0))
                .build();
        ConfigurationException ex = Assertions.assertThrows(ConfigurationException.class,
                () -> definition.createInstance("test", null, Collections.singletonMap("delegate", wrongDelegate), Collections.emptyMap()));
        assertThat(ex)
                .hasMessageContaining(Object.class.getName())
                .hasMessageContaining(ExtensionPoint.class.getName())
                .hasMessageContaining("delegate");
    }

    @Test
    @DisplayName("extension point class and each extension class must be equal")
    void ExtensionPointClassAndEachExtensionClassMustBeEqual() {
        Definition dependencyDef = Definition.builder()
                .withName("Dependency")
                .withExtension(Object.class, ConstrictorUtils.createFor(Object.class))
                .build();
        Instance wrongDelegate = dependencyDef.createInstance("dep");
        Definition definition = Definition.builder()
                .withName("Test")
                .withExtension(ExtensionPoint.class, ConstrictorUtils.createFor(Extension.class, List.class))
                .withMultiDependency(new MultiDependencyDefinition("delegate", ExtensionPoint.class, false, 0))
                .build();
        ConfigurationException ex = Assertions.assertThrows(ConfigurationException.class,
                () -> definition.createInstance("test",
                        null,
                        Collections.emptyMap(),
                        Collections.singletonMap("delegate", Collections.singletonList(wrongDelegate))));
        assertThat(ex)
                .hasMessageContaining(Object.class.getName())
                .hasMessageContaining(ExtensionPoint.class.getName())
                .hasMessageContaining("delegate");
    }

    @Test
    @DisplayName("required config must be passed")
    void requiredConfigMustBePassed() {
        Definition definition = create(
                new ConfigDefinition(Config.class, false, 1));
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> definition.createInstance("test", null, Collections.emptyMap(), Collections.emptyMap()));
        assertThat(ex).hasMessageContaining("config");
    }

    @Test
    @DisplayName("config must be an instance of correct class")
    void configClassMustBeCorrect() {
        Definition definition = create(new ConfigDefinition(Config.class, false, 1));
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> definition.createInstance("test", new Object(), Collections.emptyMap(), Collections.emptyMap()));
        assertThat(ex).hasMessageContaining("config").hasMessageContaining(Config.class.getName());
    }

    @Test
    @DisplayName("element config must be an instance of correct class")
    void elementConfigClassMustBeCorrect() {
        Definition definition = create(new ConfigDefinition("aliases", String.class, false, 1, false));
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> definition.createInstance("test", Collections.singletonList(1), Collections.emptyMap(), Collections.emptyMap()));
        assertThat(ex).hasMessageContaining("config")
                .hasMessageContaining(Integer.class.getName())
                .hasMessageContaining(String.class.getName());
    }

    @Test
    @DisplayName("try to pass config, when it is not needed, is illegal")
    void doNotPassConfigWhenItIsNotAppropriate() {
        Definition definition = create();
        ConfigurationException ex = Assertions.assertThrows(ConfigurationException.class,
                () -> definition.createInstance("test", new Object(), Collections.emptyMap(), Collections.emptyMap()));
        assertThat(ex).hasMessageContaining("configuration is not supported").hasMessageContaining("Test");
    }

    @Test
    @DisplayName("extension must not be abstract")
    void extensionMustNotBeAbstract() {
        ConfigurationException ex = Assertions.assertThrows(ConfigurationException.class, () -> Definition.builder()
                .withName("abstract")
                .withExtension(ExtensionPoint.class, ConstrictorUtils.createFor(AbstractExtension.class))
                .build());
        assertThat(ex).hasMessageContaining("abstract").hasMessageContaining(AbstractExtension.class.getName());
    }

    @Test
    @DisplayName("extension must not be private")
    void extensionMustNotBePrivate() {
        ConfigurationException ex = Assertions.assertThrows(ConfigurationException.class, () -> Definition.builder()
                .withName("private")
                .withExtension(ExtensionPoint.class, ConstrictorUtils.createFor(PrivateExtension.class))
                .build());
        assertThat(ex).hasMessageContaining("public").hasMessageContaining(PrivateExtension.class.getName());
    }

    @Test
    @DisplayName("high level exceptions if extension could not be instantiated")
    void highLevelExceptionIfExtensionCouldNotBeInstantiated() {
        Definition definition = Definition.builder()
                .withName("Test")
                .withExtension(ExtensionPoint.class, ConstrictorUtils.createFor(Extension.class, ExtensionPoint.class, ExtensionPoint.class))
                .build();
        ExtensionCreationException ex = Assertions.assertThrows(ExtensionCreationException.class,
                () -> definition.createInstance("test"));

        assertThat(ex.getCause()).hasMessage("instantiation failed");
    }

    @Test
    @DisplayName("It is appropriate to not specify any of multiple dependencies")
    void itIsAppropriateToNotSpecifyAnyOfMultiDependency() {
        Definition definition = Definition.builder()
                .withName("Test")
                .withExtension(ExtensionPoint.class, ConstrictorUtils.createFor(Extension.class, List.class))
                .withMultiDependency(new MultiDependencyDefinition("delegates", ExtensionPoint.class, false, 0))
                .build();
        Extension resolved = (Extension) definition.createInstance("test").resolve();
        assertThat(resolved.getDelegates()).isEmpty();
    }

    @Test
    @DisplayName("constructor must be public")
    void constructorMustBePublic() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> Definition.builder()
                .withName("Private")
                .withExtension(ExtensionPoint.class, ConstrictorUtils.createFor(Extension.class, ExtensionPoint.class, ExtensionPoint.class, ExtensionPoint.class))
        );
        assertThat(ex).hasMessageContaining("public");
    }

    private static Definition create() {
        return Definition.builder()
                .withName("Test")
                .withExtension(ExtensionPoint.class, ConstrictorUtils.createFor(Extension.class))
                .build();
    }

    private static Definition create(ConfigDefinition configDefinition) {
        return Definition.builder()
                .withName("Test")
                .withExtension(ExtensionPoint.class, ConstrictorUtils.createFor(Extension.class))
                .withConfig(configDefinition)
                .build();
    }

    interface ExtensionPoint {

    }

    @EqualsAndHashCode
    public static class Extension implements ExtensionPoint {
        private final List<ExtensionPoint> delegates = new ArrayList<>();

        public Extension() {

        }

        public Extension(ExtensionPoint delegate1, ExtensionPoint delegate2) throws Exception {
            throw new Exception("instantiation failed");
        }

        private Extension(ExtensionPoint delegate1, ExtensionPoint delegate2, ExtensionPoint delegate3) {

        }

        public Extension(ExtensionPoint delegate) {
            delegates.add(delegate);
        }

        public Extension(List<ExtensionPoint> delegates) {
            this.delegates.addAll(delegates);
        }

        public List<ExtensionPoint> getDelegates() {
            return delegates;
        }
    }

    private static class PrivateExtension implements ExtensionPoint {
        public PrivateExtension() {

        }
    }

    public static abstract class AbstractExtension implements ExtensionPoint {

    }

    private static class Config {

    }

    public static class FakeExtension {

    }
}