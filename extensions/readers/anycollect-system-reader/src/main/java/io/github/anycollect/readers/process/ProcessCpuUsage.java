package io.github.anycollect.readers.process;

public final class ProcessCpuUsage {
    private final long userTicks;
    private final long kernelTicks;

    public ProcessCpuUsage(final long userTicks, final long kernelTicks) {
        this.userTicks = userTicks;
        this.kernelTicks = kernelTicks;
    }
}
