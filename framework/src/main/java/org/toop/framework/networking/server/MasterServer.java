package org.toop.framework.networking.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.toop.framework.SnowflakeGenerator;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;

import java.time.Duration;
import java.util.Map;

public class MasterServer {
    private final int port;
    private final Server gs;

    ChannelFuture future;
    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;

    public MasterServer(int port, Map<String, Class<? extends TurnBasedGame>> gameTypes, Duration challengeDuration) {
        this.port = port;
        this.gs = new Server(gameTypes, challengeDuration);
    }

    public void start() throws InterruptedException {

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.SO_BACKLOG, 128);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new LoggingHandler(LogLevel.INFO));
            bootstrap.childHandler(
                    new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {

                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new LineBasedFrameDecoder(8192));
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());

                            long userid = SnowflakeGenerator.nextId();
                            User user = new User(userid, ""+userid);
                            pipeline.addLast(new ConnectionHandler(user, gs));
                        }
                    }
            );

            future = bootstrap.bind(port).sync();

            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void stop() {
        if (future == null) {
            return;
        }

        future.channel().close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        future = null;
        bossGroup = null;
        workerGroup = null;
    }
}
