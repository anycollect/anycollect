package io.github.anycollect.readers.jmx.query;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.anycollect.core.api.query.AbstractQuery;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.MetricFamily;
import io.github.anycollect.metric.Tags;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.management.MBeanServerConnection;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = StdJmxQuery.class)
@ThreadSafe
@EqualsAndHashCode(callSuper = true)
public abstract class JmxQuery extends AbstractQuery {
    public JmxQuery(@Nonnull final String id) {
        super(id);
    }

    @Nonnull
    public abstract List<MetricFamily> executeOn(@Nonnull MBeanServerConnection connection,
                                                 @Nonnull Tags targetTags)
            throws QueryException, ConnectionException;
}
