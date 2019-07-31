package io.github.anycollect.readers.socket.netty;

import io.github.anycollect.core.api.Deserializer;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.metric.Sample;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

@ChannelHandler.Sharable
public final class DispatcherHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger LOG = LoggerFactory.getLogger(DispatcherHandler.class);
    private final Deserializer deserializer;
    private final Dispatcher dispatcher;

    public DispatcherHandler(@Nonnull final Deserializer deserializer,
                             @Nonnull final Dispatcher dispatcher) {
        this.deserializer = deserializer;
        this.dispatcher = dispatcher;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final String msg) throws Exception {
        Sample sample;
        try {
            sample = deserializer.deserialize(msg);
        } catch (SerialisationException e) {
            LOG.debug("could not deserialize string {}", msg, e);
            return;
        }
        dispatcher.dispatch(sample);
    }
}
