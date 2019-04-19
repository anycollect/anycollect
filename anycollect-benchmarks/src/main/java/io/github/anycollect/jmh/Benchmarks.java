package io.github.anycollect.jmh;

import io.github.anycollect.metric.Tag;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.tags.ConcatTags;
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
public class Benchmarks {
    private Tags first;
    private Tags second;
    private Tags persistentTags;
    private Tags immutableTags;

    @Setup
    public void init() {
        this.first = generate(5);
        this.second = generate(3);
        this.persistentTags = ConcatTags.of(first, second);
        this.immutableTags = Tags.builder()
                .concat(first)
                .concat(second)
                .build();
    }

    @Benchmark
    public Tags iteratePersistent() {
        for (Tag tag : persistentTags) ;
        return persistentTags;
    }

    @Benchmark
    public Tags createPersistent() {
        return ConcatTags.of(first, second);
    }

    @Benchmark
    public Tags iterateImmutableTags() {
        for (Tag tag : immutableTags) ;
        return immutableTags;
    }

    @Benchmark
    public Tags createImmutableTags() {
        return Tags.builder()
                .concat(first)
                .concat(second)
                .build();
    }

    public static void main(final String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Benchmarks.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
