package io.github.anycollect.extensions;

import io.github.anycollect.extensions.definitions.ExtensionDefinition;
import io.github.anycollect.extensions.exceptions.ExtensionNotFoundException;
import io.github.anycollect.extensions.exceptions.MissingRequiredPropertyException;
import io.github.anycollect.extensions.exceptions.WrongExtensionClassException;

import java.io.Reader;
import java.util.List;

public interface ExtensionDefinitionLoader {
    /**
     * Loads all extensions definitions from reader.
     *
     * @param    reader                            the source that is used to load definitions
     * @throws   MissingRequiredPropertyException  if
     * @throws   ExtensionNotFoundException        if class of extension or extension point is not found in classpath
     * @throws   WrongExtensionClassException      if extension class doesn't implement or extend extension point class
     * @return                                     list of {@link ExtensionDefinition}
     */
    List<ExtensionDefinition> load(Reader reader);
}
