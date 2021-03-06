package io.github.anycollect.readers.jmx.query;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.query.AbstractQuery;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.jmx.server.JavaApp;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "name",
        defaultImpl = StdJmxQuery.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StdJmxQuery.class, name = "std"),
        @JsonSubTypes.Type(value = JvmGc.class, name = "gc"),
        @JsonSubTypes.Type(value = JvmMemory.class, name = "memory"),
        @JsonSubTypes.Type(value = JvmThreads.class, name = "threads"),
        @JsonSubTypes.Type(value = JvmRuntime.class, name = "runtime")
})
@ThreadSafe
@EqualsAndHashCode(callSuper = true)
public abstract class JmxQuery extends AbstractQuery<JavaApp> {
    public JmxQuery(@Nonnull final String id) {
        super(id);
    }

    public JmxQuery(@Nonnull final String id, @Nonnull final Tags tags, @Nonnull final Tags meta) {
        super(id, tags, meta);
    }

    @Nonnull
    public abstract Job bind(@Nonnull JavaApp app);
}
