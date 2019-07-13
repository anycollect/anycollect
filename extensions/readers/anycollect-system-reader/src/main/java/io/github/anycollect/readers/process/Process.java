package io.github.anycollect.readers.process;

import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.exceptions.ConnectionException;
import oshi.software.os.OSProcess;

public interface Process extends Target {
    OSProcess snapshot() throws ConnectionException;
}
