package org.toop.frontend.networking;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkingGameClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LogManager.getLogger(NetworkingGameClientHandler.class);

    public NetworkingGameClientHandler() {}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        logger.debug("Received message from server-{}, data: {}", ctx.channel().remoteAddress(), msg);

        // TODO: Handle server messages
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(cause.getMessage(), cause);
        ctx.close();
    }

}
