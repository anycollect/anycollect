package io.github.anycollect;

import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.Route;
import io.github.anycollect.core.api.SyncReader;
import io.github.anycollect.core.api.Writer;
import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.core.impl.router.StdRouter;
import io.github.anycollect.core.impl.router.config.ImmutableRouterConfig;
import io.github.anycollect.core.impl.router.config.RouterConfig;
import io.github.anycollect.core.impl.router.config.TopologyItem;
import io.github.anycollect.extensions.Instance;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.annotations.InjectMode;
import io.github.anycollect.extensions.context.ExtendableContext;
import io.github.anycollect.extensions.loaders.InstanceLoader;
import io.github.anycollect.extensions.scope.Scope;
import io.github.anycollect.extensions.scope.SimpleScope;
import io.github.anycollect.extensions.substitution.VarSubstitutor;
import io.github.anycollect.metric.MeterRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Extension(name = AutoRouterLoader.NAME, point = InstanceLoader.class)
public final class AutoRouterLoader implements InstanceLoader {
    public static final String NAME = "AutoRouterLoader";
    private final InstanceLoader parent;
    private final Scope scope;
    private final MeterRegistry registry;
    private final PullManager pullManager;

    @ExtCreator
    public AutoRouterLoader(@ExtDependency(qualifier = "parent") @Nonnull final InstanceLoader parent,
                            @ExtDependency(qualifier = "meterRegistry") @Nonnull final MeterRegistry registry,
                            @ExtDependency(qualifier = "pullManager") @Nonnull final PullManager pullManager) {
        this.parent = parent;
        this.scope = new SimpleScope(parent.getScope(), "auto.router");
        this.registry = registry;
        this.pullManager = pullManager;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public VarSubstitutor getVarSubstitutor() {
        return parent.getVarSubstitutor();
    }

    @Override
    public void load(@Nonnull final ExtendableContext context) {
        List<Route> routes = new ArrayList<>();
        List<String> readers = new ArrayList<>();
        List<String> writers = new ArrayList<>();
        for (Instance instance : context.getInstances()) {
            Object resolved = instance.resolve();
            if (resolved instanceof Route) {
                routes.add((Route) resolved);
            }
            if (resolved instanceof Reader || resolved instanceof SyncReader) {
                readers.add(((Route) resolved).getId());
            }
            if (resolved instanceof Writer) {
                writers.add(((Route) resolved).getId());
            }
        }
        ImmutableRouterConfig.Builder builder = RouterConfig.builder();
        for (String reader : readers) {
            for (String writer : writers) {
                builder.addTopology(TopologyItem.of(reader, writer));
            }
        }
        StdRouter router = StdRouter.of(routes, registry, pullManager, builder.build());
        context.addInstance(new Instance(context.getDefinition(StdRouter.NAME),
                "router", router, InjectMode.MANUAL, scope));
    }
}
