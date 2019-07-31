package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import java.util.function.ToDoubleFunction;

public interface Gauge {
    Gauge NOOP = new Gauge() { };

    static <T> GaugeBuilder<T> make(@Nonnull final String key,
                                    @Nonnull final T obj,
                                    @Nonnull final ToDoubleFunction<T> value) {
        return make(Key.of(key), obj, value);
    }


    static <T> GaugeBuilder<T> make(@Nonnull final Key key,
                                    @Nonnull final T obj,
                                    @Nonnull final ToDoubleFunction<T> value) {
        return new GaugeBuilder<>(key, obj, value);
    }

    final class GaugeBuilder<T> extends BaseMeterBuilder<GaugeBuilder<T>> {
        private final T obj;
        private final ToDoubleFunction<T> value;

        GaugeBuilder(@Nonnull final Key key,
                     @Nonnull final T obj,
                     @Nonnull final ToDoubleFunction<T> value) {
            super(key);
            this.obj = obj;
            this.value = value;
        }

        @Override
        protected GaugeBuilder<T> self() {
            return this;
        }

        public GaugeBuilder<T> unit(@Nonnull final String unit) {
            return super.unit(unit);
        }

        public Gauge register(@Nonnull final MeterRegistry registry) {
            ImmutableMeterId id = new ImmutableMeterId(getKey(), getUnit(),
                    getTagsBuilder().build(), getMetaBuilder().build());
            return registry.gauge(id, obj, value);
        }
    }
}
