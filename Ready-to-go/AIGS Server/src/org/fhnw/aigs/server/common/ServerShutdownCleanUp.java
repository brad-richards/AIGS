package org.fhnw.aigs.server.common;

import org.fhnw.aigs.commons.communication.ForceCloseMessage;
import org.fhnw.aigs.server.gameHandling.GameManager;

/**
 * If the server shuts down, the {@link ServerShutdownCleanUp#run} method of
 * this class will send a {@link ForceCloseMessage} will be sent to all clients.<br>
 * v1.0 Initial release<br>
 * v1.1 Changing of logging
 *
 * @author Matthias Stöckli
 * @version 1.2 (Raphael Stoeckli, 24.02.2015)
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
        //LOG//java.util.logging.Logger.getLogger(ServerShutdownCleanUp.class.getName()).info("Server shuts down - informed all clients.");
        LogRouter.log(ServerShutdownCleanUp.class.getName(), LoggingLevel.system, "Server shuts down - informed all clients.");
    }
}