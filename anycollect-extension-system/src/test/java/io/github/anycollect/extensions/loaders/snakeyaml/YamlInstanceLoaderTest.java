package io.github.anycollect.extensions.loaders.snakeyaml;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.github.anycollect.extensions.Definition;
import io.github.anycollect.extensions.Instance;
import io.github.anycollect.extensions.context.ContextImpl;
import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.extensions.dependencies.ConfigDefinition;
import io.github.anycollect.extensions.dependencies.MultiDependencyDefinition;
import io.github.anycollect.extensions.dependencies.SingleDependencyDefinition;
import io.github.anycollect.extensions.exceptions.MissingRequiredPropertyException;
import io.github.anycollect.extensions.utils.ConstrictorUtils;
import io.github.anycollect.extensions.utils.TestConfigUtils;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class YamlInstanceLoaderTest {
    private static String selfRefYaml;
    private static String selfRefsYaml;
    private static String refToUnknownDependencyYaml;
    private static String refsToUnknownDependencyYaml;

    private static String loadScalarYaml;
    private static String loadSequenceYaml;

    private static String unsupportedConfigYaml;
    private static String wrongConfigYaml;


    @BeforeAll
    static void setUpFiles() throws IOException {
        List<String> wrongRefConfigs = TestConfigUtils.splitFileBySeparator(
                "/config/anycollect-wrong-ref.yaml", "###");
        selfRefYaml = wrongRefConfigs.get(0);
        selfRefsYaml = wrongRefConfigs.get(1);
        refToUnknownDependencyYaml = wrongRefConfigs.get(2);
        refsToUnknownDependencyYaml = wrongRefConfigs.get(3);

        List<String> wrongLoadConfigs = TestConfigUtils.splitFileBySeparator(
                "/config/anycollect-wrong-load.yaml", "###");
        loadScalarYaml = wrongLoadConfigs.get(0);
        loadSequenceYaml = wrongLoadConfigs.get(1);

        List<String> illegalConfigs = TestConfigUtils.splitFileBySeparator(
                "/config/anycollect-illegal-configure.yaml", "###");
        unsupportedConfigYaml = illegalConfigs.get(0);
        wrongConfigYaml = illegalConfigs.get(1);
    }

    private List<Definition> definitions;

    @BeforeEach
    void setUp() {
        definitions = new ArrayList<>();
        Definition ext1 = Definition.builder()
                .withName("Ext1")
                .withExtension(ExtPoint1.class, ConstrictorUtils.createFor(Ext1.class))
                .build();
        Definition ext2 = Definition.builder()
                .withName("Ext2")
                .withExtension(ExtPoint2.class, ConstrictorUtils.createFor(Ext2.class))
                .build();
        Definition ext3 = Definition.builder()
                .withName("Ext3")
                .withExtension(ExtPoint3.class, ConstrictorUtils.createFor(Ext3.class, ExtPoint1.class, List.class, Ext3Config.class))
                .withSingleDependency(new SingleDependencyDefinition("ext1", ExtPoint1.class, false, 0))
                .withMultiDependency(new MultiDependencyDefinition("ext2", ExtPoint2.class, false, 1))
                .withConfig(new ConfigDefinition(Ext3Config.class, true, 2))
                .build();
        definitions.add(ext1);
        definitions.add(ext2);
        definitions.add(ext3);
    }

    @Test
    @DisplayName("complex configuration test")
    void complexTest() throws IOException {
        List<Instance> instances = loadFile("anycollect.yaml");

        Definition ext1def = definitions.get(0);
        Instance ext1 = instances.get(0);
        assertThat(ext1.getInstanceName()).isEqualTo("ext1");
        assertThat(ext1.getDefinition()).isSameAs(ext1def);

        Definition ext2def = definitions.get(1);
        Instance ext2_1 = instances.get(1);
        assertThat(ext2_1.getInstanceName()).isEqualTo("ext2_1");
        assertThat(ext2_1.getDefinition()).isSameAs(ext2def);

        Instance ext2_2 = instances.get(2);
        assertThat(ext2_2.getInstanceName()).isEqualTo("ext2_2");
        assertThat(ext2_2.getDefinition()).isSameAs(ext2def);

        Definition ext3def = definitions.get(2);
        Instance ext3 = instances.get(3);
        assertThat(ext3.getInstanceName()).isEqualTo("Ext3");
        Ext3 ext3Resolved = (Ext3) ext3.resolve();
        assertThat(ext3Resolved.getExt1()).isSameAs(ext1.resolve());
        assertThat(ext3Resolved.getExt2s()).hasSameElementsAs(Arrays.asList((Ext2) ext2_1.resolve(), (Ext2) ext2_2.resolve()));
        assertThat(ext3.getDefinition()).isSameAs(ext3def);
    }

    @Test
    @DisplayName("must fail if no extension definition not found")
    void mustFailIfDefinitionNotFound() {
        definitions.remove(0);
        Assertions.assertThrows(ConfigurationException.class, () -> loadFile("anycollect.yaml"));
    }

    @Test
    @DisplayName("!ref tag is designed for single dependency")
    void refTagIsDesignedForSingleDependency() {
        Assertions.assertThrows(ConfigurationException.class, () -> loadFile("anycollect-illegal-ref-tag.yaml"));
    }

    @Test
    @DisplayName("!refs tag is designed for multi dependencies")
    void refsTagIsDesignedForMultiDependencies() {
        Assertions.assertThrows(ConfigurationException.class, () -> loadFile("anycollect-illegal-refs-tag.yaml"));
    }

    @Test
    @DisplayName("self reference in !ref or !refs is forbidden")
    void selfReferenceIsForbidden() {
        ConfigurationException exRef = Assertions.assertThrows(ConfigurationException.class,
                () -> loadString(selfRefYaml));
        assertThat(exRef).hasMessageContaining("could not find definition for ext1");
        ConfigurationException exRefs = Assertions.assertThrows(ConfigurationException.class,
                () -> loadString(selfRefsYaml));
        assertThat(exRefs).hasMessageContaining("could not find definition for ext1");
    }

    @Test
    @DisplayName("Reference in !ref or !refs to unknown instance is forbidden")
    void unknownReferenceIsForbidden() {
        ConfigurationException exRef = Assertions.assertThrows(ConfigurationException.class,
                () -> loadString(refToUnknownDependencyYaml));
        assertThat(exRef).hasMessageContaining("could not find definition for ext1");
        ConfigurationException exRefs = Assertions.assertThrows(ConfigurationException.class,
                () -> loadString(refsToUnknownDependencyYaml));
        assertThat(exRefs).hasMessageContaining("could not find definition for ext2");
    }

    @Test
    @DisplayName("!load tag must be used only for mapping")
    void loadTagMustBeUsedOnlyForMapping() {
        ConfigurationException exScalar = Assertions.assertThrows(ConfigurationException.class,
                () -> loadString(loadScalarYaml));
        assertThat(exScalar).hasMessageContaining("!load").hasMessageContaining("illegal");
        ConfigurationException exSequence = Assertions.assertThrows(ConfigurationException.class,
                () -> loadString(loadSequenceYaml));
        assertThat(exSequence).hasMessageContaining("!load").hasMessageContaining("illegal");
    }

    @Test
    @DisplayName("try to configure not configurable extension must fail")
    void tryToConfigureNotConfigurableExtensionMustFail() {
        ConfigurationException ex = Assertions.assertThrows(ConfigurationException.class,
                () -> loadString(unsupportedConfigYaml));
        assertThat(ex).hasStackTraceContaining("is not supported").hasStackTraceContaining("Ext1");
    }

    @Test
    @DisplayName("must throw high level exception if jackson fail to parse config")
    void mustThrowHighLevelExceptionIfExtensionConfigIsNotValid() {
        ConfigurationException ex = Assertions.assertThrows(ConfigurationException.class,
                () -> loadString(wrongConfigYaml));
        assertThat(ex).hasCauseInstanceOf(UnrecognizedPropertyException.class);
    }

    @Test
    @DisplayName("extension is required property")
    void extensionIsRequiredProperty() {
        MissingRequiredPropertyException ex = Assertions.assertThrows(MissingRequiredPropertyException.class,
                () -> loadFile("anycollect-extension-required.yaml"));
        assertThat(ex.getProperty()).isEqualTo("extension");
    }

    private List<Instance> loadString(String content) {
        return loadReader(new StringReader(content));
    }

    private List<Instance> loadFile(String name) throws IOException {
        try (FileReader reader = new FileReader(new File("src/test/resources/config/" + name))) {
            return loadReader(reader);
        }
    }

    private List<Instance> loadReader(Reader reader) {
        ContextImpl context = new ContextImpl(this.definitions);
        new YamlInstanceLoader(reader).load(context);
        Collection<Instance> instances = context.getInstances();
        return new ArrayList<>(instances);
    }

    interface ExtPoint1 {

    }

    interface ExtPoint2 {

    }

    interface ExtPoint3 {

    }

    static public class Ext1 implements ExtPoint1 {

    }

    static public class Ext2 implements ExtPoint2 {

    }

    static public class Ext3 implements ExtPoint3 {
        private final ExtPoint1 ext1;
        private final List<ExtPoint2> ext2s;

        public Ext3(ExtPoint1 ext1, List<ExtPoint2> ext2s, Ext3Config config) {
            this.ext1 = ext1;
            this.ext2s = ext2s;
        }

        public ExtPoint1 getExt1() {
            return ext1;
        }

        public List<ExtPoint2> getExt2s() {
            return ext2s;
        }
    }

    @EqualsAndHashCode
    static class Ext3Config {
        private final String key;

        @JsonCreator
        Ext3Config(@JsonProperty("key") String key) {
            this.key = key;
        }
    }
}