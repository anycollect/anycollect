package io.github.anycollect.core.api.measurable;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.common.Plugin;
import io.github.anycollect.extensions.annotations.ExtPoint;

import javax.annotation.Nonnull;

@ExtPoint
public interface Measurers extends Plugin, Lifecycle {
    <T extends Measurable> Measurer<T> make(@Nonnull FamilyConfig config);
}
