package io.github.anycollect.readers.socket.netty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.Deserializer;
import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.extensions.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

@Extension(name = NettyAsyncSocketReader.NAME, point = Reader.class)
public final class NettyAsyncSocketReader implements Reader, Lifecycle {
    public static final String NAME = "NettyAsyncSocketReader";
    private static final Logger LOG = LoggerFactory.getLogger(NettyAsyncSocketReader.class);
    private final Deserializer deserializer;
    private final int port;
    private volatile Server server;
    private final String id;

    @ExtCreator
    public NettyAsyncSocketReader(@ExtDependency(qualifier = "deserializer") @Nonnull final Deserializer deserializer,
                                  @ExtConfig @Nonnull final Config config,
                                  @InstanceId @Nonnull final String id) {
        this.deserializer = deserializer;
        this.id = id;
        this.port = config.port;
    }

    @Override
    public void start(@Nonnull final Dispatcher dispatcher) {
        server = new Server(port, deserializer, dispatcher);
        server.start();
    }

    @Override
    public void init() {
        LOG.info("{}({}) has been successfully initialized", id, NAME);
    }

    @Override
    public void destroy() {
        LOG.info("{}({}) has been successfully destroyed", id, NAME);
    }

    @Override
    public String getId() {
        return id;
    }

    public static final class Config {
        private final int port;

        @JsonCreator
        public Config(@JsonProperty("port") final int port) {
            this.port = port;
        }
    }
}
