package org.toop.framework.networking;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class NetworkingClient {
    private static final Logger logger = LogManager.getLogger(NetworkingClient.class);

    private long connectionId;
    private Channel channel;
    private NetworkingGameClientHandler handler;

    public NetworkingClient(
            Supplier<NetworkingGameClientHandler> handlerFactory,
            String host,
            int port,
            long connectionId) {
        this.connectionId = connectionId;
        try {
            Bootstrap bootstrap = new Bootstrap();
            EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    handler = handlerFactory.get();

                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new LineBasedFrameDecoder(1024)); // split at \n
                    pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));     // bytes -> String
                    pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                    pipeline.addLast(handler);
                }
            });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            this.channel = channelFuture.channel();
        } catch (Exception e) {
            logger.error("Failed to create networking client instance", e);
        }
    }

    public NetworkingGameClientHandler getHandler() {
        return handler;
    }

    public void setConnectionId(long connectionId) {
        this.connectionId = connectionId;
    }

    public boolean isChannelActive() {
        return this.channel != null && this.channel.isActive();
    }

    public void writeAndFlush(String msg) {
        String literalMsg = msg.replace("\n", "\\n").replace("\r", "\\r");
        if (isChannelActive()) {
            this.channel.writeAndFlush(msg);
            logger.info("Connection {} sent message: '{}'", this.channel.remoteAddress(), literalMsg);
        } else {
            logger.warn("Cannot send message: '{}', connection inactive.", literalMsg);
        }
    }

    public void writeAndFlushnl(String msg) {
        if (isChannelActive()) {
            this.channel.writeAndFlush(msg + "\r\n");
            logger.info("Connection {} sent message: '{}'", this.channel.remoteAddress(), msg);
        } else {
            logger.warn("Cannot send message: '{}', connection inactive.", msg);
        }
    }

    public void login(String username) {
        this.writeAndFlush("login " + username + "\n");
    }

    public void logout() {
        this.writeAndFlush("logout\n");
    }

    public void sendMove(int move) {
        this.writeAndFlush("move " + move + "\n"); // append \n so server receives a full line
    }

    public void getGamelist() {
        this.writeAndFlush("get gamelist\n");
    }

    public void getPlayerlist() {
        this.writeAndFlush("get playerlist\n");
    }

    public void subscribe(String gameType) {
        this.writeAndFlush("subscribe " + gameType + "\n");
    }

    public void forfeit() {
        this.writeAndFlush("forfeit\n");
    }

    public void challenge(String playerName, String gameType) {
        this.writeAndFlush("challenge " + playerName + " " + gameType + "\n");
    }

    public void acceptChallenge(String challengeNumber) {
        this.writeAndFlush("challenge accept " + challengeNumber + "\n");
    }

    public void sendChatMessage(String message) {
        this.writeAndFlush("message " + "\"" + message + "\"" + "\n");
    }

    public void help(String command) {
        this.writeAndFlush("help " + command + "\n");
    }

    public void closeConnection() {
        if (this.channel != null && this.channel.isActive()) {
            this.channel.close().addListener(future -> {
                if (future.isSuccess()) {
                    logger.info("Connection {} closed successfully",  this.channel.remoteAddress());
                } else {
                    logger.error("Error closing connection {}. Error: {}",
                            this.channel.remoteAddress(),
                            future.cause().getMessage());
                }
            });
        }
    }

    public long getId() {
        return this.connectionId;
    }

}
