package io.github.anycollect.readers.jmx.query;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.anycollect.core.api.query.AbstractQuery;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.management.*;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class",
        defaultImpl = StdJmxQuery.class)
@ThreadSafe
@EqualsAndHashCode(callSuper = true)
public abstract class JmxQuery extends AbstractQuery {
    public JmxQuery(@Nonnull final String id) {
        super(id);
    }

    @Nonnull
    public abstract List<Metric> executeOn(@Nonnull MBeanServerConnection connection,
                                           @Nonnull Tags targetTags)
            throws QueryException, ConnectionException;

    protected Set<ObjectName> queryNames(@Nonnull final MBeanServerConnection connection,
                                         @Nonnull final ObjectName objectPattern) throws ConnectionException {
        try {
            return connection.queryNames(objectPattern, null);
        } catch (IOException e) {
            throw new ConnectionException("could not query names", e);
        }
    }
}
