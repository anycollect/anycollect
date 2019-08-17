package io.github.anycollect.metric;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static io.github.anycollect.metric.Utils.generate;
import static io.github.anycollect.metric.Utils.stringify;
import static org.openjdk.jmh.annotations.Mode.AverageTime;
import static org.openjdk.jmh.annotations.Mode.Throughput;

@Fork(1)
@Warmup(iterations = 5, time = 2000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 2000, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode({Throughput, AverageTime})
@State(Scope.Benchmark)
public class TagsEqualityBenchmark {
    private Tags persistentTags1;
    private Tags persistentTags2;
    private Tags immutableTags1;
    private Tags immutableTags2;
    private String stringTags1;
    private String stringTags2;

    @Setup
    public void init() {
        Tags first = generate(5);
        Tags second = generate(3);
        this.persistentTags1 = ConcatTags.of(first, second);
        this.persistentTags2 = ConcatTags.of(first, second);
        this.immutableTags1 = Tags.builder()
                .concat(first)
                .concat(second)
                .build();
        this.immutableTags2 = Tags.builder()
                .concat(first)
                .concat(second)
                .build();
        this.stringTags1 = stringify(persistentTags1);
        this.stringTags2 = stringify(persistentTags2);
    }

    @Benchmark
    public boolean referenceEquals() {
        return persistentTags1.equals(persistentTags1);
    }

    @Benchmark
    public boolean stringEquals() {
        return stringTags1.equals(stringTags2);
    }

    @Benchmark
    public boolean persistentEquals() {
        return persistentTags1.equals(persistentTags2);
    }

    @Benchmark
    public boolean immutableEquals() {
        return immutableTags1.equals(immutableTags2);
    }

    public static void main(final String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TagsEqualityBenchmark.class.getSimpleName())
                .include(Benchmarks.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
