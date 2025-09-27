package org.toop.framework.networking;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jdk.jfr.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkingGameClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LogManager.getLogger(NetworkingGameClientHandler.class);

    private final long connectionId;

    public NetworkingGameClientHandler(long connectionId) {
        this.connectionId = connectionId;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String rec = msg.toString().trim();

        if (rec.equalsIgnoreCase("err")) {
            logger.error("server-{} send back error, data: {}", ctx.channel().remoteAddress(), msg);
            return;
        }
        if (rec.equalsIgnoreCase("ok")) {
            logger.info("Received OK message from server-{}, data: {}", ctx.channel().remoteAddress(), msg);
            return;
        }
        if (rec.toLowerCase().startsWith("svr")) {
            logger.info("Received SVR message from server-{}, data: {}", ctx.channel().remoteAddress(), msg);
            new EventFlow().addPostEvent(new NetworkEvents.ServerResponse(this.connectionId)).asyncPostEvent();
            parseServerReturn(rec);
            return;
        }
        logger.info("Received unparsed message from server-{}, data: {}", ctx.channel().remoteAddress(), msg);

    }

    private void parseServerReturn(String rec) {
        if (rec.toLowerCase().contains("playerlist")) {
            playerListHandler(rec);
        } else if (rec.toLowerCase().contains("close")) {

        } else {}
    }

    private void playerListHandler(String rec) {
            Pattern pattern = Pattern.compile("\"([^\"]+)\"");
            String[] players = pattern.matcher(rec)
                    .results()
                    .map(m -> m.group(1))
                    .toArray(String[]::new);

            new EventFlow()
                    .addPostEvent(new NetworkEvents.PlayerListResponse(this.connectionId, players))
                    .asyncPostEvent();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(cause.getMessage(), cause);
        ctx.close();
    }

}
