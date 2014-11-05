package org.fhnw.aigs.TicTacToe.client;

import javafx.application.Application;
import javafx.stage.Stage;
import org.fhnw.aigs.client.GUI.BaseGameWindow;
import org.fhnw.aigs.commons.GameMode;

/**
 * main class of the client application.<br>
 * All major settings of the particular game, like the game name or the game mode are to be defined in this class.<br>
 * Don't forgett to set the same game name in the class {@link org.fhnw.aigs.TicTacToe.server.GameLogic} in the package 'server'.<br>
 * v1.0 Initial release<br>
 * v1.1 Functional changes<br>
 * v1.1.1 Minor changes due to Update of BaseClientand AIGSCommons
 * @author Matthias Stöckli (v1.0)
 * @version 1.1.1 (Raphael Stoeckli, 04.11.2014)
 */
public class TicTacToeMain extends Application {

    /**
     * Equivalent to the main method in JavaFX applications
     * @param primaryStage The primary stage - comparable to a main window
     */
    @Override
    public void start(Stage primaryStage) {
        
        // Create a root BaseGameWindow which shows a header, a footer and a content area.
        BaseGameWindow root = new BaseGameWindow(primaryStage, "/Assets/Stylesheets/tictactoe.css","TicTacToe");
        
        // Create a new game client
        TicTacToeClientGame ticTacToeClientGame = new TicTacToeClientGame("TicTacToe", "v1.1.1", GameMode.Multiplayer); // VERY IMPORTANT! The game name must be unique on the server (only onece 'TicTacToe')
       
        TicTacToeBoard ticTacToeBoard = new TicTacToeBoard(3, 3, ticTacToeClientGame);  // Create a new board instance
        root.setContent(ticTacToeBoard);                                        // Set the newly created board as the main content of the window
        // Provide the game with a reference to the root window. This will allow the game to change 
        ticTacToeClientGame.setGameWindow(root);                                // the header, footer etc. based on messages.
        ticTacToeClientGame.setTicTacToeBoard(ticTacToeBoard);                  // Provide the game with a direct reference to the game's graphical representation of the content.
        root.InitGame(ticTacToeBoard, ticTacToeClientGame);                     //Initializong Game
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
