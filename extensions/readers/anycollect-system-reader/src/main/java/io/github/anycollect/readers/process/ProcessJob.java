package io.github.anycollect.readers.process;

import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.metric.Sample;
import oshi.software.os.OSProcess;

import javax.annotation.Nonnull;
import java.util.List;

public final class ProcessJob implements Job {
    private final Process process;
    private final ProcessQuery stats;
    private OSProcess last = null;

    public ProcessJob(@Nonnull final Process process, @Nonnull final ProcessQuery stats) {
        this.process = process;
        this.stats = stats;
    }

    @Override
    public List<Sample> execute() throws ConnectionException {
        OSProcess current = copy(process.snapshot());
        List<Sample> samples = stats.execute(last, current);
        last = current;
        return samples;
    }

    // we need to make a copy of important fields because oshi can modify this object
    private OSProcess copy(final OSProcess source) {
        if (source == null) {
            return null;
        }
        OSProcess copy = new OSProcess();
        copy.setUserTime(source.getUserTime());
        copy.setKernelTime(source.getKernelTime());
        copy.setUpTime(source.getUpTime());
        copy.setResidentSetSize(source.getResidentSetSize());
        return copy;
    }
}
