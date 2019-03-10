package io.github.anycollect.readers.socket.netty;

import io.github.anycollect.core.api.Deserializer;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import javax.annotation.Nonnull;

public final class LineProtocolChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final DispatcherHandler handler;

    public LineProtocolChannelInitializer(@Nonnull final Deserializer deserializer,
                                          @Nonnull final Dispatcher dispatcher) {
        this.handler = new DispatcherHandler(deserializer, dispatcher);
    }

    @Override
    protected void initChannel(final SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encode", new StringEncoder());
        pipeline.addLast("handler", handler);
    }
}
