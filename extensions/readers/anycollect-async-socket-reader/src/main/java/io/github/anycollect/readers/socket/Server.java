package io.github.anycollect.readers.socket;

import io.github.anycollect.core.api.Deserializer;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public final class Server extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);
    private final int port;
    private final Deserializer deserializer;
    private final Dispatcher dispatcher;

    public Server(final int port, @Nonnull final Deserializer deserializer, @Nonnull final Dispatcher dispatcher) {
        this.port = port;
        this.deserializer = deserializer;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new LineProtocolChannelInitializer(deserializer, dispatcher));
            server.bind(port).sync().channel().closeFuture().sync();
        } catch (Exception e) {
            LOG.debug("error", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
