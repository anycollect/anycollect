package io.github.anycollect.readers.socket;

import io.github.anycollect.core.api.Deserializer;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.metric.Metric;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import javax.annotation.Nonnull;

@ChannelHandler.Sharable
public final class DispatcherHandler extends SimpleChannelInboundHandler<String> {
    private final Deserializer deserializer;
    private final Dispatcher dispatcher;

    public DispatcherHandler(@Nonnull final Deserializer deserializer,
                             @Nonnull final Dispatcher dispatcher) {
        this.deserializer = deserializer;
        this.dispatcher = dispatcher;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final String msg) throws Exception {
        Metric family = deserializer.deserialize(msg);
        dispatcher.dispatch(family);
    }
}
