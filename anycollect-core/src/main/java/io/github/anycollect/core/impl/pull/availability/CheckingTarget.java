package io.github.anycollect.core.impl.pull.availability;

import io.github.anycollect.core.api.target.Target;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@ToString
@ThreadSafe
public final class CheckingTarget<T extends Target<?>> {
    private final T target;
    private final AtomicReference<Check> check;

    public static <T extends Target<?>> CheckingTarget<T> of(@Nonnull final T target) {
        return new CheckingTarget<>(target);
    }

    public CheckingTarget(@Nonnull final T target) {
        this.target = target;
        this.check = new AtomicReference<>(Check.unknown(System.currentTimeMillis()));
    }

    public CheckingTarget(@Nonnull final T target, final long timestamp) {
        this.target = target;
        this.check = new AtomicReference<>(Check.unknown(timestamp));
    }

    public T get() {
        return target;
    }

    public void update(final Check check) {
        this.check.set(check);
    }

    public Check check() {
        return check.get();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CheckingTarget<?> that = (CheckingTarget<?>) o;
        return Objects.equals(target, that.target)
                && Objects.equals(check.get(), that.check.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, check.get());
    }
}
