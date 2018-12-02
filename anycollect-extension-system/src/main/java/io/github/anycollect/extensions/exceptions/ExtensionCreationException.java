package io.github.anycollect.extensions.exceptions;

import io.github.anycollect.extensions.definitions.Definition;
import io.github.anycollect.extensions.definitions.Dependency;

import java.util.List;

public class ExtensionCreationException extends ExtensionException {
    public ExtensionCreationException(final Definition definition,
                                      final List<Dependency> dependencies,
                                      final Throwable cause) {
        super(String.format("cannot create extension %s with %s ", definition, dependencies), cause);
    }
}
