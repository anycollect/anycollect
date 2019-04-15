package io.github.anycollect.jmh;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.core.impl.serializers.graphite.GraphiteSerializer;
import io.github.anycollect.core.impl.serializers.graphite.GraphiteSerializerConfig;
import io.github.anycollect.metric.ImmutableTags;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.tags.ConcatTags;
import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import static org.openjdk.jmh.annotations.Mode.AverageTime;
import static org.openjdk.jmh.annotations.Mode.Throughput;

@Fork(1)
@BenchmarkMode({Throughput, AverageTime})
@State(Scope.Benchmark)
public class Serialization {
    private Metric immutableMetric;
    private Metric persistentMetric;
    private Serializer serializer;

    @Setup
    public void init() {
        Tags first = generate(5);
        Tags second = generate(3);
        Tags persistentTags = ConcatTags.of(first, second);
        Tags immutableTags = Tags.builder()
                .concat(first)
                .concat(second)
                .build();
        Metric base = Metric.builder()
                .key("jvm.memory.used")
                .concatTags(generate(3))
                .at(System.currentTimeMillis())
                .gauge("test", 1)
                .build();
        persistentMetric = base.frontTags(first).frontTags(second);
        immutableMetric = Metric.builder()
                .key("jvm.memory.used")
                .concatTags(first)
                .concatTags(second)
                .concatTags(generate(3))
                .at(System.currentTimeMillis())
                .gauge("test", 1)
                .build();
        this.serializer = new GraphiteSerializer(GraphiteSerializerConfig.DEFAULT);
    }

    @Benchmark
    public String serializeImmutable() throws SerialisationException {
        return serializer.serialize(immutableMetric);
    }

    @Benchmark
    public String serializePersistent() throws SerialisationException {
        return serializer.serialize(persistentMetric);
    }

    public static void main(final String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Benchmarks.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

    private Tags generate(final int numberOfTags) {
        ImmutableTags.Builder builder = Tags.builder();
        for (int i = 0; i < numberOfTags; i++) {
            String key = RandomStringUtils.random(5);
            String value = RandomStringUtils.random(10);
            builder.tag(key, value);
        }
        return builder.build();
    }
}
