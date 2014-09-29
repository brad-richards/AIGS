package org.fhnw.aigs.TicTacToeSwing.client;

import org.fhnw.aigs.swingClient.GUI.BaseGameWindow;
import org.fhnw.aigs.swingClient.GUI.LoadingWindow;
import org.fhnw.aigs.swingClient.communication.ClientCommunication;
import org.fhnw.aigs.swingClient.communication.Settings;
import org.fhnw.aigs.commons.GameMode;

/**
 * main class of the client application.<br>
 * All major settings of the particular game, like the game name or the game mode are to be defined in this class.<br>
 * Don't forgett to set the same game name in the class {@link org.fhnw.aigs.TicTacToeSwing.server.GameLogic} in the package 'server'.
 * @author Matthias Stöckli (v1.0)
 * @version 1.1 (Raphael Stoeckli, 12.08.2014)
 */
public class TicTacToeMain {

    /**
     * Starts the game.
     */
    public static void main(String[] args){

        // Create a new game client.
        TicTacToeClientGame ticTacToeClientGame = new TicTacToeClientGame("TicTacToeSwing", GameMode.SinglePlayer); // VERY IMPORTANT! The game name must be unique on the server (only onece 'TicTacToeSwing')        
        
        // Create a new BaseWindow.
        BaseGameWindow baseGameWindow = new BaseGameWindow("TicTacToeClient"); // This is only the title of the window
        baseGameWindow.setContent(new LoadingWindow());
        ticTacToeClientGame.setGameWindow(baseGameWindow);

        // Create the board and assign it to the game.
        TicTacToeBoard ticTacToeBoard = new TicTacToeBoard(3, 3, ticTacToeClientGame);
        ticTacToeBoard.setFields();
        ticTacToeBoard.setEventHandlers();
        ticTacToeClientGame.setTicTacToeBoard(ticTacToeBoard);
        
        Settings.tryLoadSettings(true);
        ClientCommunication.setCredentials(ticTacToeClientGame, Settings.getInstance().getServerAddress(), Settings.getInstance().getServerPort());
        // ClientCommunication.setCredentialsUsingOnlineConfiguration(ticTacToeClient);  // DEPRECATED, respectively: to redefine
        
        // Start the communication
        Thread clientCommunicationThread = new Thread(ClientCommunication.getInstance());
        clientCommunicationThread.setName("ClientCommunicationThread");
        clientCommunicationThread.start();
        baseGameWindow.setVisible(true);
    }

}
