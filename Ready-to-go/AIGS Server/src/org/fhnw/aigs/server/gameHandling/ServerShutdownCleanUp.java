package org.fhnw.aigs.server.gameHandling;

import org.fhnw.aigs.commons.communication.ForceCloseMessage;

/**
 * If the server shuts down, the {@link ServerShutdownCleanUp#run} method of
 * this class will send a {@link ForceCloseMessage} will be sent to all clients.
 *
 * @author Matthias Stöckli
 * @version v1.0
 */
public class ServerShutdownCleanUp implements Runnable {

    @Override
    public void run() {
        runCleanup();
    }

    /**
     * Send a {@link ForceCloseMessage} to all players.
     */
    private void runCleanup() {
        ForceCloseMessage forceCloseMessage = new ForceCloseMessage("Server was shut down.");
        GameManager.sendMessageToAllPlayersOnServer(forceCloseMessage);
        java.util.logging.Logger.getLogger(ServerShutdownCleanUp.class.getName()).info("Server shuts down - informed all clients.");
    }
}