package io.github.anycollect.core.api.target;

import javax.annotation.Nonnull;
import java.util.Set;

public interface Targets<T extends Target> {
    void update(@Nonnull Set<T> targets);
}
