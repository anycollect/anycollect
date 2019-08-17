package io.github.anycollect.meter.api;

import io.github.anycollect.metric.Key;
import io.github.anycollect.metric.Tags;
import org.apiguardian.api.API;

import javax.annotation.Nonnull;

@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
public interface MeterId {
    @Nonnull
    Key getKey();

    @Nonnull
    Tags getTags();

    @Nonnull
    Tags getMeta();

    @Nonnull
    String getUnit();
}
