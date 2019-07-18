package io.github.anycollect.test;

import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.Router;
import io.github.anycollect.core.api.Writer;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;

import javax.annotation.Nonnull;

@Extension(name = TestRouter.NAME, point = Router.class)
public final class TestRouter implements Router {
    public static final String NAME = "TestRouter";
    private final Reader reader;
    private final Writer writer;

    @ExtCreator
    public TestRouter(@ExtDependency(qualifier = "reader") @Nonnull final Reader reader,
                      @ExtDependency(qualifier = "writer") @Nonnull final Writer writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public void start() {
        reader.start(writer::write);
    }

    @Override
    public void stop() {
    }
}
