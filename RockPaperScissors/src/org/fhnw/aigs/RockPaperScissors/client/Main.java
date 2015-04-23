package org.fhnw.aigs.RockPaperScissors.client;

import org.fhnw.aigs.client.GUI.BaseGameWindow;
import org.fhnw.aigs.commons.GameMode;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    // Constants
    public static final String GAMENAME = "RockPaperScissors";
    public static final GameMode GAMEMODE = GameMode.Multiplayer;
    public static final String VERSION = "v1.0";

    /**
	 * The main() method is ignored in correctly deployed JavaFX application.
	 * main() serves only as fallback in case of problems.
	 */
   public static void main(String[] args) {
       launch(args);
   }
    
    /**
     * Start-Methode for JavaFX
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create an instance of a game
        RockPaperScissorsClientGame clientGame = new RockPaperScissorsClientGame(Main.GAMENAME, Main.VERSION, Main.GAMEMODE);

        // Create an instance of a game-window
        BaseGameWindow root = new BaseGameWindow(primaryStage, Main.GAMENAME);
        clientGame.setGameWindow(root);
        
        // Load a stylesheet (only if you have created one!)
        root.getStylesheets().add("/Assets/RockPaperScissors.css");
        
        // Create an instance of the game-board (currently just a placeholder
        RockPaperScissorsBoard board = new RockPaperScissorsBoard(clientGame);
        clientGame.setClientBoard(board);

        // Initialize the game
		root.InitGame(board, clientGame);
	}
}
