package org.fhnw.aigs.TicTacToe.client;

import javafx.application.Application;
import javafx.stage.Stage;
import org.fhnw.aigs.TicTacToe.server.GameLogic;
import org.fhnw.aigs.client.GUI.BaseGameWindow;
import org.fhnw.aigs.client.GUI.LoadingWindow;
import org.fhnw.aigs.client.communication.ClientCommunication;
import org.fhnw.aigs.client.communication.Settings;
import org.fhnw.aigs.commons.GameMode;

/**
 * main class of the client application.<br>
 * All major settings of the particular game, like the game name or the game mode are to be defined in this class.<br>
 * Don't forgett to set the same game name in the class {@link org.fhnw.aigs.TicTacToe.server.GameLogic} in the package 'server'.
 * @author Matthias Stöckli (v1.0)
 * @version 1.1 (Raphael Stoeckli, 12.08.2014)
 */
public class TicTacToeMain extends Application {

    /**
     * Equivalent to the main method in JavaFX applications
     * @param primaryStage The primary stage - comparable to a main window
     */
    @Override
    public void start(Stage primaryStage) {
        // Create a new game client
        TicTacToeClientGame ticTacToeClient = new TicTacToeClientGame("TicTacToe", GameMode.Multiplayer); // VERY IMPORTANT! The game name must be unique on the server (only onece 'TicTacToe')
        
        // Create a root BaseGameWindow which shows a header, a footer and a content area.
        BaseGameWindow root = new BaseGameWindow(primaryStage, "/Assets/Stylesheets/tictactoe.css","TicTacToe");
        primaryStage.setTitle("TicTacToe");                         // Set the title of the window
        primaryStage.show();                                        // Show the window
        
        TicTacToeBoard ticTacToeBoard = new TicTacToeBoard(3, 3, ticTacToeClient);  // Create a new board instance
        ticTacToeBoard.setFields();                        // Define the appearance of the board and the fields
        ticTacToeBoard.setEventHandlers();                 // Set the event handlers, i.e. how the GUI will react

        // Set the newly created board as the main content of the window
        root.setContent(ticTacToeBoard);
        
        // Show a loading screen which will disappear as soon as a connection has been established
        root.setOverlay(new LoadingWindow());

        // Provide the game with a reference to the root window. This will allow the game to change 
        // the header, footer etc. based on messages.
        ticTacToeClient.setGameWindow(root);                        
        
        // Provide the game with a direct reference to the game's graphical representation of the content.
        ticTacToeClient.setTicTacToeBoard(ticTacToeBoard);
                      
        Settings.tryLoadSettings(true);
        
        // AWS: 54.213.87.246
        // Set the credentials (host and port)

        ClientCommunication.setCredentials(ticTacToeClient, Settings.getInstance().getServerAddress(), Settings.getInstance().getServerPort());
       // ClientCommunication.setCredentialsUsingOnlineConfiguration(ticTacToeClient);  // DEPRECATED, respectively: to redefine
        // Start the communication
        Thread clientCommunicationThread = new Thread(ClientCommunication.getInstance());
        clientCommunicationThread.setName("ClientCommunicationThread");
        clientCommunicationThread.start();
    }


    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
