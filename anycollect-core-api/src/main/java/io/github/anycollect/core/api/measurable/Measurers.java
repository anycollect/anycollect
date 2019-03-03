package io.github.anycollect.core.api.measurable;

import javax.annotation.Nonnull;

public interface Measurers {
    boolean hasDefinition(@Nonnull String familyName);

    <T extends Measurable> Measurer<T> make(@Nonnull FamilyConfig config);
}
