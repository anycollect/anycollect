package io.github.anycollect.readers.process;

import io.github.anycollect.core.api.target.AbstractTarget;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.metric.Tags;
import oshi.software.os.OSProcess;

import javax.annotation.Nonnull;

/**
 * The system process that have to be live but it is not.
 */
public final class EphemeralProcess extends AbstractTarget implements Process {
    public EphemeralProcess(@Nonnull final String id, @Nonnull final Tags tags, @Nonnull final Tags meta) {
        super(id, tags, meta);
    }

    @Override
    public OSProcess snapshot() throws ConnectionException {
        throw new ConnectionException("process is dead");
    }
}
