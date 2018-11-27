package io.github.anycollect.extensions.snakeyaml;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.github.anycollect.extensions.definitions.ConfigParameterDefinition;
import io.github.anycollect.extensions.definitions.ExtensionDefinition;
import io.github.anycollect.extensions.definitions.ExtensionInstanceDefinition;
import io.github.anycollect.extensions.exceptions.ConfigurationException;
import io.github.anycollect.extensions.exceptions.MissingRequiredPropertyException;
import io.github.anycollect.extensions.utils.TestConfigUtils;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class YamlExtensionInstanceDefinitionLoaderTest {
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

    private List<ExtensionDefinition> definitions;

    @BeforeEach
    void setUp() {
        definitions = new ArrayList<>();
        ExtensionDefinition ext1 = ExtensionDefinition.builder()
                .withName("Ext1")
                .withExtension(ExtPoint1.class, Ext1.class)
                .build();
        ExtensionDefinition ext2 = ExtensionDefinition.builder()
                .withName("Ext2")
                .withExtension(ExtPoint2.class, Ext2.class)
                .build();
        ExtensionDefinition ext3 = ExtensionDefinition.builder()
                .withName("Ext3")
                .withExtension(ExtPoint3.class, Ext3.class)
                .withConfig(new ConfigParameterDefinition(Ext3Config.class, true, 0))
                .build();
        definitions.add(ext1);
        definitions.add(ext2);
        definitions.add(ext3);
    }

    @Test
    @DisplayName("complex configuration test")
    void complexTest() throws IOException {
        List<ExtensionInstanceDefinition> instances = loadFile("anycollect.yaml");

        ExtensionDefinition ext1def = definitions.get(0);
        ExtensionInstanceDefinition ext1 = instances.get(0);
        assertThat(ext1.getInstanceName()).isEqualTo("ext1");
        assertThat(ext1.getDependencies()).isEmpty();
        assertThat(ext1.getExtensionDefinition()).isSameAs(ext1def);
        assertThat(ext1.getConfig()).isEmpty();

        ExtensionDefinition ext2def = definitions.get(1);
        ExtensionInstanceDefinition ext2_1 = instances.get(1);
        assertThat(ext2_1.getInstanceName()).isEqualTo("ext2_1");
        assertThat(ext2_1.getDependencies()).isEmpty();
        assertThat(ext2_1.getExtensionDefinition()).isSameAs(ext2def);
        assertThat(ext2_1.getConfig()).isEmpty();

        ExtensionInstanceDefinition ext2_2 = instances.get(2);
        assertThat(ext2_2.getInstanceName()).isEqualTo("ext2_2");
        assertThat(ext2_2.getDependencies()).isEmpty();
        assertThat(ext2_2.getExtensionDefinition()).isSameAs(ext2def);
        assertThat(ext2_2.getConfig()).isEmpty();

        ExtensionDefinition ext3def = definitions.get(2);
        ExtensionInstanceDefinition ext3 = instances.get(3);
        assertThat(ext3.getInstanceName()).isEqualTo("Ext3");
        assertThat(ext3.getDependencies()).hasSize(2);
        assertThat(ext3.getDependencies().get("ext1").getInstance()).isEqualTo(ext1);
        assertThat(ext3.getDependencies().get("ext2").getInstances()).containsExactly(ext2_1, ext2_2);
        assertThat(ext3.getExtensionDefinition()).isSameAs(ext3def);
        assertThat(ext3.getConfig()).contains(new Ext3Config("value"));
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

    @Test()
    @DisplayName("extension is required property")
    void extensionIsRequiredProperty() {
        MissingRequiredPropertyException ex = Assertions.assertThrows(MissingRequiredPropertyException.class,
                () -> loadFile("anycollect-extension-required.yaml"));
        assertThat(ex.getProperty()).isEqualTo("extension");
    }

    private List<ExtensionInstanceDefinition> loadString(String content) {
        return loadReader(new StringReader(content));
    }

    private List<ExtensionInstanceDefinition> loadFile(String name) throws IOException {
        try (FileReader reader = new FileReader(new File("src/test/resources/config/" + name))) {
            return loadReader(reader);
        }
    }

    private List<ExtensionInstanceDefinition> loadReader(Reader reader) {
        Collection<ExtensionInstanceDefinition> definitions =
                new YamlExtensionInstanceDefinitionLoader(reader, this.definitions).load();
        return new ArrayList<>(definitions);
    }

    interface ExtPoint1 {

    }

    interface ExtPoint2 {

    }

    interface ExtPoint3 {

    }

    static class Ext1 implements ExtPoint1 {

    }

    static class Ext2 implements ExtPoint2 {

    }

    static class Ext3 implements ExtPoint3 {
        private final Ext1 ext1;
        private final List<Ext2> ext2s;

        public Ext3(Ext1 ext1, List<Ext2> ext2s, Ext3Config config) {
            this.ext1 = ext1;
            this.ext2s = ext2s;
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