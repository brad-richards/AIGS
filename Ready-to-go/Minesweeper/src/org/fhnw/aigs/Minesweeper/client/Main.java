package org.fhnw.aigs.Minesweeper.client;

import javafx.application.Application;
import javafx.stage.Stage;
import org.fhnw.aigs.client.GUI.BaseGameWindow;
import org.fhnw.aigs.client.GUI.LoadingWindow;
import org.fhnw.aigs.client.communication.ClientCommunication;
import org.fhnw.aigs.client.communication.Settings;
import org.fhnw.aigs.commons.GameMode;

/**
 * main class of the client application.<br>
 * All major settings of the particular game, like the game name or the game mode are to be defined in this class.<br>
 * Don't forgett to set the same game name in the class {@link org.fhnw.aigs.Minesweeper.server.GameLogic} in the package 'server'.<br>
 * v1.0 Initial release<br>
 * v1.1 Functional changes<br>
 * v1.2 Changes due to update on AIGSCommons and AIGSBaseClient
 * @author Matthias Stöckli (v1.0)
 * @version 1.2 (Raphael Stoeckli, 04.11.2014)
 */
public class Main extends Application {
    
    /** Number of fields on the x-axis. */
    int xFields = 9;
    /** Number of fields on the y-axis. */
    int yFields = 9;
    /** Number of totalMines in the game. */
    int totalMines = 10;
    
    /**
     * This is the starting point of the application 
     * @param primaryStage The primary stage - the "window".
     */
    @Override
    public void start(Stage primaryStage) {
        
        // Create a root BaseGameWindow which shows a header, a footer and a
        // content area. Reference a css layout file  and set the
        // title.
        BaseGameWindow root = new BaseGameWindow(primaryStage, "Minesweeper");
        root.getStylesheets().add("/Assets/Stylesheets/minesweeper.css");
        
        // Create a new ClientGame, set a title (will be used to identify the game)
        // and set the GameMode
        MinesweeperClientGame minesweeperClientGame = new MinesweeperClientGame(
                "Minesweeper", GameMode.SinglePlayer, xFields, yFields, totalMines); // VERY IMPORTANT! The game name must be unique on the server (only onece 'Minesweeper')

//      primaryStage.setTitle("Minesweeper");
//      primaryStage.show(); // Show the window

        // Create a new board instance
        MinesweeperBoard minesweeperBoard = new MinesweeperBoard(xFields, yFields, minesweeperClientGame);

        // Define the appearance of the board and set the event handler
//        minesweeperBoard.setFields();                                         //-->
//        minesweeperBoard.setEventHandlers();                                  //-->

        // Set the newly created board as the main content of the window
//        root.setContent(minesweeperBoard);                                    //-->

        // Show a loading screen which will disappear as soon as a connection
        // has been established
//        root.setOverlay(new LoadingWindow());                                 //-->

        // Provide the game with a reference to the root window. This will allow
        // the game to change the header, footer etc. based on messages.
        minesweeperClientGame.setGameWindow(root);

        // Provide the game with a direct reference to the game's graphical
        // representation of the content.
        minesweeperClientGame.setMinesweeperBoard(minesweeperBoard);

        root.InitGame(minesweeperBoard, minesweeperClientGame);                             // Initialized game
        
//        Settings.tryLoadSettings(true);

        // AWS: 54.213.87.246
//        ClientCommunication.setCredentials(minesweeperClientGame, Settings.getInstance().getServerAddress(), Settings.getInstance().getServerPort());
        // ClientCommunication.setCredentialsUsingOnlineConfiguration(minesweeperClientGame);  // DEPRECATED, respectively: to redefine

//        Thread communicationThread = new Thread(
//                ClientCommunication.getInstance());
//        communicationThread.start();
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
