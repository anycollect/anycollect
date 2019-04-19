package io.github.anycollect.jmh;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.core.impl.serializers.graphite.GraphiteSerializer;
import io.github.anycollect.core.impl.serializers.graphite.GraphiteSerializerConfig;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static io.github.anycollect.jmh.Utils.generate;
import static org.openjdk.jmh.annotations.Mode.AverageTime;
import static org.openjdk.jmh.annotations.Mode.Throughput;

@Fork(1)
@BenchmarkMode({Throughput, AverageTime})
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.NANOSECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class Serialization {
    private Metric immutableMetric;
    private Metric persistentMetric;
    private Serializer serializer;

    @Setup
    public void init() {
        Tags first = generate(5);
        Tags second = generate(3);
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
}
