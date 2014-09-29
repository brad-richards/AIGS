package org.fhnw.aigs.BinaerOperatoren.client;

import org.fhnw.aigs.swingClient.GUI.BaseGameWindow;
import org.fhnw.aigs.swingClient.GUI.LoadingWindow;
import org.fhnw.aigs.swingClient.communication.ClientCommunication;
import org.fhnw.aigs.swingClient.communication.Settings;


/**
 * main class of the client application.<br>
 * All major settings of the particular game, like the game name or the game mode are to be defined in this class.<br>
 * Don't forgett to set the same game name in the class {@link org.fhnw.aigs.BinaerOperatoren.server.GameLogic} in the package 'server'.
 * @author Matthias Stöckli (v1.0)
 * @version 1.1 (Raphael Stoeckli, 13.08.2014)
 */
public class Main {
    public static void main(String[] args){
        BinaerOperatorenClientGame binaerOperatorenClientGame = new BinaerOperatorenClientGame("BinaerOperatoren"); // VERY IMPORTANT! The game name must be unique on the server (only onece 'BinaerOperatoren')
        BaseGameWindow baseGameWindow = new BaseGameWindow("Binäroperatoren");
        baseGameWindow.setContent(new LoadingWindow());
        binaerOperatorenClientGame.setGameWindow(baseGameWindow);

        
        BinaerOperatorenPanel binaerOperatorenPanel = new BinaerOperatorenPanel(binaerOperatorenClientGame);
        binaerOperatorenClientGame.addPanel(binaerOperatorenPanel);
        
        Settings.tryLoadSettings(true);
        
        // Start the communication
        ClientCommunication.setCredentials(binaerOperatorenClientGame, Settings.getInstance().getServerAddress(), Settings.getInstance().getServerPort());
        // ClientCommunication.setCredentialsUsingOnlineConfiguration(binaerOperatorenClientGame);  // DEPRECATED, respectively: to redefine
        
        Thread clientCommunicationThread = new Thread(ClientCommunication.getInstance());
        clientCommunicationThread.setName("ClientCommunicationThread");
        clientCommunicationThread.start();
        baseGameWindow.setVisible(true);
    }
}
