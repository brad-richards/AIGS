package org.fhnw.aigs.server.gameHandling;

import org.fhnw.aigs.server.common.ServerConfiguration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.fhnw.aigs.commons.Game;
import org.fhnw.aigs.commons.Player;
import org.fhnw.aigs.commons.communication.KeepAliveMessage;
import org.fhnw.aigs.server.common.LogRouter;
import org.fhnw.aigs.server.common.LoggingLevel;

/**
 * This class helps detecting idle clients.<br>
 * It runs a <b>KeepAliveMessage</b> in a predefined interval.<br>
 * This interval is stored in the attribute <b>keepAliveTimeOut</b> and is
 * defined in the ServerConfiguration. It can be changed, when needed. The
 * server must be restarted to use the new interval. Every client is obliged to
 * send a response to the KeepAliveMessage within that time frame. If it does
 * not, the clients will be disconnected and therefore the game will end.
 * <b>Please note:</b> Do not use a low keepAliveTimeOut. If the user debugs the
 * project, the break point may block the communication between Client and
 * Server, therefore the signals cannot reach the KeepAliveManager. The server
 * will then interpret this as a time out. An interval of one or two minutes
 * (60000 to 120000 ms) is sufficient to detect blocked clients. Is is possible
 * to turn of the KeepAliveManager by setting "UseKeepAliveManager" in the
 * Server Configuration to "false".<br>
 * v1.0 Initial release<br>
 * v1.1 Changing of logging
 *
 * @author Matthias Stöckli (v1.0)
 * @version v1.1 (Raphael Stoeckli 24.02.2015)
 */
public class KeepAliveManager implements Runnable {

    /**
     * A list with all players (identified by their name) that still need to
     * send a response to the KeepAliveMessage
     */
    private static HashMap<String, Player> duePlayers = new HashMap<String, Player>();
    /**
     * A list with games that needs to be closed due to a time out
     */
    private static ArrayList<Game> gamesToBeClosed = new ArrayList<Game>();
    /**
     * The interval between a KeepAliveMessage and the expected answer
     */
    private int keepAliveTimeOut;

    /**
     * This method will be called from the ServerMessageBroker in order to
     * inform the KeepAliveManager of an incoming response.
     *
     * @param keepAliveResponse The response of a client in the form of a
     * KeepAliveMessage
     */
    public static synchronized void handleResponse(KeepAliveMessage keepAliveResponse) {
        if (keepAliveResponse.getPlayer() != null) {
            String name = keepAliveResponse.getPlayer().getName();
            if (duePlayers.containsKey(name)) {
                duePlayers.remove(name);
            }
        }
    }

    /**
     * Starts {@link KeepAliveManager#startKeepAliveLoop}.
     */
    @Override
    public void run() {
        startKeepAliveLoop();
    }

    /**
     * This method starts the KeepAlive loop. It iterates through every active
     * game and sends a {@link KeepAliveMessage} to all players (except AI).
     * Then the players are put on a list. Now all client's will receive a
     * message to which they must provide an answer (a message of the same
     * type). It they do so, the players will be removed from the list. After a
     * predefined interval, the list will be checked again. Those players who
     * did not send an answer will be regarded as inactive. The games they are
     * in will then be closed. Then the list is cleared, and so the process
     * begins anew.
     */
    private synchronized void startKeepAliveLoop() {
        keepAliveTimeOut = ServerConfiguration.getInstance().getKeepAliveTimeOut();
        while (true) {
            // Go through all the running games
            for (Game game : GameManager.runningGames) {
                for (Player player : game.getPlayers()) {
                    if (player.isAi()) {
                        continue;
                    }
                    KeepAliveMessage keepAliveMessage = new KeepAliveMessage();
                    keepAliveMessage.setSentTime(new Date());
                    game.sendMessageToPlayer(keepAliveMessage, player);
                    duePlayers.put(player.getName(), player);
                    //LOG//Logger.getLogger(KeepAliveManager.class.getName()).log(Level.FINE, "Sent KeepAlive to {0}", player.getName());
                    LogRouter.log(KeepAliveManager.class.getName(), LoggingLevel.info, "Sent KeepAlive to {0}", player.getName());
                }
            }

            // Wait for the amount of time defined in the keepAliveTimeOut
            // See ServerConfiguration.xml for that.
            try {
                Thread.sleep(keepAliveTimeOut);
            } catch (InterruptedException ex) {
                //LOG//Logger.getLogger(KeepAliveManager.class.getName()).log(Level.SEVERE, null, ex);
                LogRouter.log(KeepAliveManager.class.getName(), LoggingLevel.severe, null, ex);
            }

            // Iterate through all games again and check whether an inactive player
            // is in. If this is the case, close the game.
            for (Game game : GameManager.runningGames) {
                for (Player player : game.getPlayers()) {
                    if (duePlayers.containsKey(player.getName())) {
                        gamesToBeClosed.add(game);
                    }
                }
            }

            // Close the games.
            for (Game game : gamesToBeClosed) {
                GameManager.terminateGame(game, "One of the players did not answer.");
            }
            // Clear the list.
            gamesToBeClosed.clear();
        }
    }
}
