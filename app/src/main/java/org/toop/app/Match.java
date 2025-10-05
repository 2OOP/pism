package org.toop.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.asset.ResourceLoader;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.NetworkingClientManager;
import org.toop.framework.networking.events.NetworkEvents;

import static java.lang.Thread.sleep;


public class Match {
    private String player1,player2;
    private boolean player1AI,player2AI;
    private String ip;
    private int port;
    private boolean isLocal;
    private GameType type;
    private long clientId;
    private static final Logger logger = LogManager.getLogger(Match.class);

    public Match(String player1, String player2, boolean player1AI, boolean player2AI, GameType type) {
        this.player1 = player1;
        this.player2 = player2;
        this.player1AI = player1AI;
        this.player2AI = player2AI;
        this.type = type;
        this.isLocal = true;

        startGameLoop();
    }
    public Match(String player1, boolean player1AI, String ip, int port, GameType type) {
        this.player1 = player1;
        this.player1AI = player1AI;
        this.ip = ip;
        this.port = port;
        this.type = type;
        this.isLocal = false;

        new EventFlow()
                .listen(this::handleStartClientResponse)
                .listen(this::handleYourTurn);

        startServerConnection();
        //startGameLoop();
    }
    private void handleYourTurn(NetworkEvents.YourTurnResponse response) {
        //System.out.println(response.toString());
        //new EventFlow().addPostEvent(NetworkEvents.SendMove.class, clientId,(short)1).asyncPostEvent();
        try{
            sleep(2000);
        }
        catch (InterruptedException e){}
        new EventFlow().addPostEvent(NetworkEvents.SendCommand.class, clientId,"MOVE 2").asyncPostEvent();
    }

    private void loginAndSubscribe(GameType type) {
        if(clientId > 0){
            new EventFlow().addPostEvent(NetworkEvents.SendLogin.class,clientId,player1).asyncPostEvent();
            new EventFlow().addPostEvent(NetworkEvents.SendSubscribe.class,clientId,GameType.toName(type)).asyncPostEvent();
            startGameLoop();
        }
        else {
            logger.warn("Internal client ID is invalid. Failed to log in.");
        }
    }

    void handleStartClientResponse(NetworkEvents.StartClientResponse response) {
        this.clientId = response.clientId();
        loginAndSubscribe(type);
    }

    private boolean startServerConnection() {
        if(!isLocal){
            if(ip == null || port <= 0){
                logger.warn("IP address or port is invalid");
                return false;
            }
            else{
                new EventFlow().addPostEvent(NetworkEvents.StartClient.class,ip,port).asyncPostEvent();
                return true;
            }
        }
        return false;
    }
    private void startGameLoop() {
        if(!isLocal){
            //new EventFlow().addPostEvent(NetworkEvents.SendMove.class,clientId,2).asyncPostEvent();
        }
    }
}
