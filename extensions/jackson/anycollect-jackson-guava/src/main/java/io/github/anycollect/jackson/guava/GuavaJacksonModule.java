package io.github.anycollect.jackson.guava;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.api.JacksonModule;

@Extension(
        name = GuavaJacksonModule.NAME,
        point = JacksonModule.class,
        autoload = @Extension.AutoLoad(instanceName = GuavaJacksonModule.INSTANCE_NAME)
)
public final class GuavaJacksonModule implements JacksonModule {
    public static final String NAME = "GuavaJacksonModule";
    public static final String INSTANCE_NAME = "guavaJacksonModule";

    @Override
    public Module module() {
        return new GuavaModule();
    }
}
