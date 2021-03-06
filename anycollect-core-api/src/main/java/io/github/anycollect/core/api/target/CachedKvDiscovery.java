package io.github.anycollect.core.api.target;

import io.github.anycollect.core.api.kv.KeyValue;
import io.github.anycollect.core.api.kv.KeyValueStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.*;

/**
 * Helper implementation that can be used in the following cases:
 * target creation is expensive and separation of definition and target is required;
 * target needs some additional objects (such as connection pool, executor service, etc)
 * that cannot be passed to plain-text based definition;
 * target needs some shared services (that should be in {@link TargetFactory}.
 *
 * @param <D> - type of target definition, value object
 * @param <T> - type of {@link Target}
 */
@ThreadSafe
public final class CachedKvDiscovery<D, T extends Target> implements ServiceDiscovery<T> {
    private static final Logger LOG = LoggerFactory.getLogger(CachedKvDiscovery.class);
    private final KeyValue kv;
    private final Class<D> definitionClass;
    private final TargetFactory<D, T> targetFactory;
    private final String key;
    private Map<D, T> previous = new HashMap<>();

    public static <T extends Target> CachedKvDiscovery<T, T> create(@Nonnull final KeyValue kv,
                                                                    @Nonnull final Class<T> targetClass,
                                                                    @Nonnull final String key) {
        return new CachedKvDiscovery<>(kv, targetClass, TargetFactory.identity(), key);
    }

    public CachedKvDiscovery(@Nonnull final KeyValue kv,
                             @Nonnull final Class<D> definitionClass,
                             @Nonnull final TargetFactory<D, T> targetFactory,
                             @Nonnull final String key) {
        this.kv = kv;
        this.definitionClass = definitionClass;
        this.targetFactory = targetFactory;
        this.key = key;
    }

    @Override
    public synchronized Set<T> discover() {
        List<D> definitions;
        try {
            definitions = kv.getValues(key, definitionClass);
        } catch (KeyValueStorageException e) {
            LOG.warn("could not get target definitions from key-value storage, "
                    + "previous target definitions will be used", e);
            return new HashSet<>(previous.values());
        }
        Map<D, T> apps = new HashMap<>();
        for (D definition : definitions) {
            if (previous.containsKey(definition)) {
                apps.put(definition, previous.get(definition));
                continue;
            }
            T target = null;
            try {
                target = targetFactory.create(definition);
            } catch (TargetCreationException e) {
                LOG.error("could not create target from definition {}", definition, e);
            }
            apps.put(definition, target);
        }
        this.previous = apps;
        return new HashSet<>(apps.values());
    }
}
