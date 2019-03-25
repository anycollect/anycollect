package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.measurable.MeasurementPath;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.metric.Type;
import io.github.anycollect.readers.jmx.server.JavaApp;

import javax.annotation.Nonnull;
import java.util.Collections;

public final class JvmRuntime extends JmxQuery {
    private final StdJmxQuery uptime;

    public JvmRuntime() {
        this("", Tags.empty(), Tags.empty());
    }

    public JvmRuntime(@Nonnull final String prefix,
                      @Nonnull final Tags tags,
                      @Nonnull final Tags meta) {
        super("jvm.runtime", tags, meta);
        this.uptime = new StdJmxQuery(prefix.isEmpty() ? "jvm.runtime.uptime" : prefix + "." + "jvm.runtime.uptime",
                tags,
                meta,
                "ms",
                Collections.emptyList(),
                "java.lang:type=Runtime",
                null,
                Collections.singletonList(new MeasurementPath("Uptime", Stat.value(), Type.TIMESTAMP)));
    }


    @Nonnull
    @Override
    public Job bind(@Nonnull final JavaApp app) {
        return uptime.bind(app);
    }
}