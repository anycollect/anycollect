package io.github.anycollect.core.api.query;

import javax.annotation.Nonnull;
import java.util.Set;

public interface Queries<Q extends Query> {
    void update(@Nonnull Set<Q> queries);
}
