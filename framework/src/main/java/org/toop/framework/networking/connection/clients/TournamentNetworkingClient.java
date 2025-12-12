package org.toop.framework.networking.connection.clients;

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
import org.toop.framework.eventbus.bus.EventBus;
import org.toop.framework.networking.connection.exceptions.CouldNotConnectException;
import org.toop.framework.networking.connection.handlers.NetworkingGameClientHandler;
import org.toop.framework.networking.connection.interfaces.NetworkingClient;

import java.net.InetSocketAddress;

public class TournamentNetworkingClient implements NetworkingClient {
    private static final Logger logger = LogManager.getLogger(TournamentNetworkingClient.class);

    private final EventBus eventBus;
    private Channel channel;

    public TournamentNetworkingClient(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public InetSocketAddress getAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    @Override
    public void connect(long clientId, String host, int port) throws CouldNotConnectException {
        try {
            Bootstrap bootstrap = new Bootstrap();
            EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(
                    new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            NetworkingGameClientHandler handler = new NetworkingGameClientHandler(eventBus, clientId);

                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LineBasedFrameDecoder(1024)); // split at \n
                            pipeline.addLast(
                                    new StringDecoder(CharsetUtil.UTF_8)); // bytes -> String
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            pipeline.addLast(handler);
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            this.channel = channelFuture.channel();
        } catch (Exception _) {
            throw new CouldNotConnectException(clientId);
        }
    }

    @Override
    public boolean isActive() {
        return this.channel != null && this.channel.isActive();
    }

    @Override
    public void writeAndFlush(String msg) {
        String literalMsg = msg.replace("\n", "\\n").replace("\r", "\\r");
        if (isActive()) {
            this.channel.writeAndFlush(msg);
            logger.info("Connection {} sent message: '{}' ", this.channel.remoteAddress(), literalMsg);
        } else {
            logger.warn("Cannot send message: '{}', connection inactive. ", literalMsg);
        }
    }

    @Override
    public void closeConnection() {
        if (this.channel != null && this.channel.isActive()) {
            this.channel
                    .close()
                    .addListener(
                            future -> {
                                if (future.isSuccess()) {
                                    logger.info(
                                            "Connection {} closed successfully",
                                            this.channel.remoteAddress());
                                } else {
                                    logger.error(
                                            "Error closing connection {}. Error: {}",
                                            this.channel.remoteAddress(),
                                            future.cause().getMessage());
                                }
                            });
        }
    }
}
