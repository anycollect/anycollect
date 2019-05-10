package io.github.anycollect.test;

import io.github.anycollect.extensions.Instance;
import io.github.anycollect.extensions.context.ContextImpl;
import io.github.anycollect.extensions.context.DelegatingContext;
import io.github.anycollect.extensions.context.ExtendableContext;
import io.github.anycollect.extensions.loaders.ClassPathManifestScanDefinitionLoader;
import io.github.anycollect.extensions.loaders.InstanceLoader;
import io.github.anycollect.extensions.loaders.snakeyaml.YamlInstanceLoader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

public final class TestContext extends DelegatingContext {
    private final ContextImpl context = new ContextImpl();

    public TestContext(final String manifest) throws FileNotFoundException {
        ClassPathManifestScanDefinitionLoader loader = new ClassPathManifestScanDefinitionLoader();
        loader.load(context);
        File config = FileUtils.getFile("src", "test", "resources", manifest);
        InstanceLoader instanceLoader = new YamlInstanceLoader(new FileReader(config));
        instanceLoader.load(context);
    }

    public Instance getInstance(final String name) {
        return getInstances().stream()
                .filter(instance -> instance.getInstanceName().equals(name))
                .findFirst().get();
    }

    public Instance getInstance(final Class<?> type) {
        List<Instance> instances = getInstances(type);
        if (instances.size() != 1) {
            throw new IllegalArgumentException("there are " + instances.size() + " instances for " + type);
        }
        return instances.get(0);
    }

    public List<Instance> getInstances(final Class<?> type) {
        return getInstances().stream()
                .filter(instance -> instance.getDefinition().getExtensionPointClass().isAssignableFrom(type))
                .collect(Collectors.toList());
    }

    @Override
    protected ExtendableContext getContext() {
        return context;
    }
}
