package io.guthub.anycollect;

import io.github.anycollect.metric.ImmutableTags;
import io.github.anycollect.metric.Tag;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.metric.ConcatTags;
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

    private Tags generate(int numberOfTags) {
        ImmutableTags.Builder builder = Tags.builder();
        for (int i = 0; i < numberOfTags; i++) {
            String key = RandomStringUtils.random(5);
            String value = RandomStringUtils.random(10);
            builder.tag(key, value);
        }
        return builder.build();
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

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Benchmarks.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
