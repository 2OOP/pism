package org.toop.framework.networking;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;

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

        String recSrvRemoved = rec.substring("SVR ".length());
        Pattern gamePattern = Pattern.compile("GAME (\\w+)", Pattern.CASE_INSENSITIVE);
        Matcher gameMatch = gamePattern.matcher(recSrvRemoved);

        if (gameMatch.find()) {
            switch(gameMatch.group(1)) {
                case "YOURTURN":   gameYourTurnHandler(recSrvRemoved);     return;
                case "MOVE":       gameMoveHandler(recSrvRemoved);         return;
                case "MATCH":      gameMatchHandler(recSrvRemoved);        return;
                case "CHALLENGE":  gameChallengeHandler(recSrvRemoved);    return;
                case "WIN",
                     "DRAW",
                     "LOSE":       gameWinConditionHandler(recSrvRemoved); return;
                default:                                                   return;
            }
        } else {

             Pattern getPattern = Pattern.compile("(\\w+)", Pattern.CASE_INSENSITIVE);
             Matcher getMatch = getPattern.matcher(recSrvRemoved);

            if (getMatch.find()) {
                switch(getMatch.group(1)) {
                    case "PLAYERLIST": playerlistHandler(recSrvRemoved); return;
                    case "GAMELIST":   gamelistHandler(recSrvRemoved);   return;
                    case "HELP":       helpHandler(recSrvRemoved);       return;
                    default:                                             return;
                }
            } else {
                return; // TODO: Should be an error.
            }
        }
    }

    private void gameMoveHandler(String rec) {
        String[] msg = Pattern
                .compile("(?:player|details|move):\\s*\"?([^\",}]+)\"?", Pattern.CASE_INSENSITIVE)
                .matcher(rec)
                .results()
                .map(m -> m.group(1).trim())
                .toArray(String[]::new);

        new EventFlow()
                .addPostEvent(new NetworkEvents.GameMoveResponse(this.connectionId, msg[0], msg[1], msg[2]))
                .asyncPostEvent();
    }

    private void gameWinConditionHandler(String rec) {
        String condition = Pattern
                .compile("\\b(win|draw|lose)\\b", Pattern.CASE_INSENSITIVE)
                .matcher(rec)
                .results()
                .toString()
                .trim();

        new EventFlow()
                .addPostEvent(new NetworkEvents.GameResultResponse(this.connectionId, condition))
                .asyncPostEvent();
    }

    private void gameChallengeHandler(String rec) {
        boolean isCancelled = rec.toLowerCase().startsWith("challenge accepted");
        try {
            String[] msg = Pattern
                    .compile("(?:CHALLENGER|GAMETYPE|CHALLENGENUMBER):\\s*\"?(.*?)\"?\\s*(?:,|})")
                    .matcher(rec)
                    .results()
                    .map(m -> m.group().trim())
                    .toArray(String[]::new);

            if (isCancelled) new EventFlow()
                    .addPostEvent(new NetworkEvents.ChallengeCancelledResponse(this.connectionId, msg[0]))
                    .asyncPostEvent();
            else             new EventFlow()
                    .addPostEvent(new NetworkEvents.ChallengeResponse(this.connectionId, msg[0], msg[1], msg[2]))
                    .asyncPostEvent();
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("Array out of bounds for: {}", rec, e);
        }
    }

    private void gameMatchHandler(String rec) {
        try {
            String[] msg = Pattern
                    .compile("\"([^\"]*)\"")
                    .matcher(rec)
                    .results()
                    .map(m -> m.group(1).trim())
                    .toArray(String[]::new);

            // [0] playerToMove, [1] gameType, [2] opponent
            new EventFlow()
                    .addPostEvent(new NetworkEvents.GameMatchResponse(this.connectionId, msg[0], msg[1], msg[2]))
                    .asyncPostEvent();
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("Array out of bounds for: {}", rec, e);
        }

    }

    private void gameYourTurnHandler(String rec) {
        String msg = Pattern
                .compile("TURNMESSAGE:\\s*\"([^\"]*)\"")
                .matcher(rec)
                .results()
                .toString()
                .trim();

        new EventFlow()
                .addPostEvent(new NetworkEvents.YourTurnResponse(this.connectionId, msg))
                .asyncPostEvent();
    }

    private void playerlistHandler(String rec) {
        String[] players = Pattern
                .compile("\"([^\"]+)\"")
                .matcher(rec)
                .results()
                .map(m -> m.group(1).trim())
                .toArray(String[]::new);

        new EventFlow()
                .addPostEvent(new NetworkEvents.PlayerlistResponse(this.connectionId, players))
                .asyncPostEvent();
    }

    private void gamelistHandler(String rec) {
        String[] gameTypes = Pattern
                .compile("\"([^\"]+)\"")
                .matcher(rec)
                .results()
                .map(m -> m.group(1).trim())
                .toArray(String[]::new);

        new EventFlow()
            .addPostEvent(new NetworkEvents.GamelistResponse(this.connectionId, gameTypes))
            .asyncPostEvent();
    }

    private void helpHandler(String rec) {
        logger.info(rec);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(cause.getMessage(), cause);
        ctx.close();
    }

}
