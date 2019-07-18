package io.github.anycollect.test;

import io.github.anycollect.assertj.MetricAssert;
import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.Router;
import io.github.anycollect.extensions.Instance;
import io.github.anycollect.extensions.annotations.InjectMode;
import io.github.anycollect.extensions.context.ContextImpl;
import io.github.anycollect.extensions.context.DelegatingContext;
import io.github.anycollect.extensions.context.ExtendableContext;
import io.github.anycollect.extensions.loaders.ClassPathManifestScanDefinitionLoader;
import io.github.anycollect.extensions.loaders.InstanceLoader;
import io.github.anycollect.extensions.loaders.snakeyaml.YamlInstanceLoader;
import io.github.anycollect.extensions.scope.Scope;
import io.github.anycollect.extensions.scope.SimpleScope;
import io.github.anycollect.extensions.substitution.VarSubstitutor;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

public final class TestContext extends DelegatingContext {
    private final Scope rootScope = new SimpleScope(null, "root");
    private final ContextImpl context = new ContextImpl();

    public TestContext(final String manifest) throws FileNotFoundException {
        ClassPathManifestScanDefinitionLoader loader = new ClassPathManifestScanDefinitionLoader();
        loader.load(context);
        File config = FileUtils.getFile("src", "test", "resources", manifest);
        InstanceLoader instanceLoader = new YamlInstanceLoader(rootScope, new FileReader(config), VarSubstitutor.EMPTY);
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

    public Interceptor intercept(@Nonnull final String readerId) {
        Reader reader = (Reader) getInstance(readerId).resolve();
        String interceptorId = "interceptor";
        Interceptor interceptor = new InterceptorImpl(interceptorId);
        addInstance(new Instance(getDefinition(InterceptorImpl.NAME),
                interceptorId,
                interceptor,
                InjectMode.MANUAL,
                rootScope,
                false
        ));
        String routerId = "router";
        Router router = new TestRouter(reader, interceptor);
        addInstance(new Instance(getDefinition(TestRouter.NAME),
                routerId,
                router,
                InjectMode.MANUAL,
                rootScope,
                false
        ));
        return new InterceptorWrapper(interceptor, router);
    }

    private static class InterceptorWrapper implements Interceptor {
        private final Interceptor interceptor;
        private final Router router;

        InterceptorWrapper(final Interceptor interceptor, final Router router) {
            this.interceptor = interceptor;
            this.router = router;
        }

        @Override
        public void start() {
            router.start();
        }

        @Override
        public void stop() {
            router.stop();
        }

        @Override
        public MetricAssert intercepted(final String key) {
            return interceptor.intercepted(key);
        }

        @Override
        public MetricAssert intercepted(final String key, final Tags tags) {
            return interceptor.intercepted(key, tags);
        }

        @Override
        public MetricAssert intercepted(final String key, final Tags tags, final Tags meta) {
            return interceptor.intercepted(key, tags, meta);
        }

        @Override
        public void write(@Nonnull final List<? extends Metric> metrics) {
            interceptor.write(metrics);
        }

        @Override
        public String getId() {
            return interceptor.getId();
        }
    }
}
