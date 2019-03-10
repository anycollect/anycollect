package io.github.anycollect.core.api.kv;

import javax.annotation.Nonnull;
import java.util.List;

public interface KeyValue {
    <T> List<T> getValues(@Nonnull String key,
                          @Nonnull Class<T> valueType);
}
