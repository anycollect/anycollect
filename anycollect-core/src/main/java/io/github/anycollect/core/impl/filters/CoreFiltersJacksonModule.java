package io.github.anycollect.core.impl.filters;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import io.github.anycollect.core.impl.filters.tag.GenericTagFilter;
import io.github.anycollect.extensions.annotations.Extension;

@Extension(
        name = CoreFiltersJacksonModule.NAME,
        contracts = Module.class,
        autoload = @Extension.AutoLoad(instanceName = CoreFiltersJacksonModule.INSTANCE_NAME)
)
public final class CoreFiltersJacksonModule extends Module {
    public static final String NAME = "CoreFiltersJacksonModule";
    public static final String MODULE_NAME = "AnyCollectCoreFiltersModule";
    public static final String INSTANCE_NAME = "coreFiltersJacksonModule";

    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(final SetupContext context) {
        context.registerSubtypes(
                AcceptAllFilter.class,
                AcceptKeyFilter.class,
                DenyAllFilter.class,
                DenyKeyFilter.class,
                GenericTagFilter.class
        );
    }
}
