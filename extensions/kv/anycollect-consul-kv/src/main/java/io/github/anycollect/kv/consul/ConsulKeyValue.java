package io.github.anycollect.kv.consul;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;
import com.orbitz.consul.ConsulException;
import com.orbitz.consul.KeyValueClient;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.kv.KeyValue;
import io.github.anycollect.core.api.kv.KeyValueStorageException;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.jackson.AnyCollectModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

@Extension(name = ConsulKeyValue.NAME, contracts = KeyValue.class)
public final class ConsulKeyValue implements KeyValue, Lifecycle {
    public static final String NAME = "ConsulKv";
    private static final Logger LOG = LoggerFactory.getLogger(ConsulKeyValue.class);
    // TODO use abstraction, probably is should be extension
    private final ObjectMapper objectMapper;
    private final Consul consul;
    private final KeyValueClient keyValueClient;

    @ExtCreator
    public ConsulKeyValue(@ExtConfig final ConsulConfig config) {
        this.objectMapper = new ObjectMapper(new YAMLFactory());
        this.objectMapper.registerModule(new GuavaModule());
        this.objectMapper.registerModule(new AnyCollectModule());
        this.consul = Consul.builder()
                .withHostAndPort(HostAndPort.fromParts(config.host(), config.port()))
                .withPing(false)
                .build();
        this.keyValueClient = this.consul.keyValueClient();
    }

    @Override
    public <T> List<T> getValues(@Nonnull final String key, @Nonnull final Class<T> valueType)
            throws KeyValueStorageException {
        List<T> values = new ArrayList<>();
        List<String> valuesAsString;
        try {
            valuesAsString = keyValueClient.getValuesAsString(key);
        } catch (ConsulException e) {
            if (e.getCause() instanceof ConnectException) {
                LOG.warn("could not connect to consul");
                throw new KeyValueStorageException("could not connect to consul");
            }
            LOG.debug("could not get values by key \"{}\" from consul", key, e);
            throw new KeyValueStorageException("could not get values from consul", e);
        }
        for (String valueString : valuesAsString) {
            T value;
            try {
                value = objectMapper.readValue(valueString, valueType);
            } catch (IOException e) {
                LOG.debug("cannot parse value {}", valueString);
                continue;
            }
            values.add(value);
        }
        return values;
    }

    @Override
    public void init() {
        LOG.info("{} has been successfully initialised", NAME);
    }

    @Override
    public void destroy() {
        consul.destroy();
        LOG.info("{} has been successfully destroyed", NAME);
    }
}
