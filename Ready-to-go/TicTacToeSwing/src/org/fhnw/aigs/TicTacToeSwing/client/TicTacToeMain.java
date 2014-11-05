package org.fhnw.aigs.TicTacToeSwing.client;

import org.fhnw.aigs.swingClient.GUI.BaseGameWindow;
import org.fhnw.aigs.commons.GameMode;

/**
 * main class of the client application.<br>
 * All major settings of the particular game, like the game name or the game mode are to be defined in this class.<br>
 * Don't forgett to set the same game name in the class {@link org.fhnw.aigs.TicTacToeSwing.server.GameLogic} in the package 'server'.<br>
 * v1.0 Initial release<br>
 * v1.1 Cahnges due to update of AIGSBaseClient and AIGSCommons
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
        BaseGameWindow baseGameWindow = new BaseGameWindow("TicTacToe");  // Create the BaseGameWindow with the title
        ticTacToeClientGame.setGameWindow(baseGameWindow);                      // Set reference to the BaseGameWindow in the ClientGame
        TicTacToeBoard ticTacToeBoard = new TicTacToeBoard(3, 3, ticTacToeClientGame); // Create new board
        ticTacToeClientGame.setTicTacToeBoard(ticTacToeBoard);                  // Set reference of the board in the ClientGame
        baseGameWindow.initGame(ticTacToeClientGame);           // Initialize the game
    }

}
