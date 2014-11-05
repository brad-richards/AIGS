package org.fhnw.aigs.swingClient.gameHandling;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.fhnw.aigs.swingClient.communication.ClientMessageBroker;
import org.fhnw.aigs.commons.communication.ClientClosedMessage;


/**
 * This class will run automatically as soon as the client closes.
 * This can either be via System.exit(), an exception or by simply closing
 * the application's window. It will send a ClientClosedMessage to the server
 * which will remove the game from the list of active games.
 * @author Matthias Stöckli
 * @version v1.0
 */
class ClientShutdownCleanUp implements Runnable {

    @Override
    public void run() {
        runCleanup();
    }
    
    /** Sends a ClientClosedMessage to the server upon termination. */
    private void runCleanup() {
        try{
            ClientClosedMessage clientClosedMessage = new ClientClosedMessage("One of the clients closed the game.");
            ClientMessageBroker.sendMessage(clientClosedMessage);            
        }catch(Exception ex){
            Logger.getLogger(ClientShutdownCleanUp.class.getName()).log(Level.SEVERE, "\"Could not send Shutdown Message. The connection may not have been established yet.", ex);
        }
    }
}
