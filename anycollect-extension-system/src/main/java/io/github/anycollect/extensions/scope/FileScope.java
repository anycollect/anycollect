package io.github.anycollect.extensions.scope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

public final class FileScope extends AbstractScope {
    private final File file;

    public static FileScope root(@Nonnull final File file) {
        return new FileScope(null, file);
    }

    public static FileScope child(@Nonnull final Scope parent, @Nonnull final File file) {
        return new FileScope(parent, file);
    }

    private FileScope(@Nullable final Scope parent,
                      @Nonnull final File file) {
        super(parent);
        this.file = file;
    }

    @Nonnull
    @Override
    public String getId() {
        return file.getName();
    }

    @Nonnull
    public File getFile() {
        return file;
    }
}
