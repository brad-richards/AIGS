package org.fhnw.aigs.Minesweeper.client;

import java.awt.HeadlessException;
import javax.swing.JOptionPane;
import org.fhnw.aigs.client.gameHandling.ClientGame;
import org.fhnw.aigs.commons.GameMode;
import org.fhnw.aigs.Minesweeper.commons.BoardChangeMessage;
import org.fhnw.aigs.Minesweeper.commons.MarkFieldStatusMesage;
import org.fhnw.aigs.Minesweeper.commons.RestartMessage;
import org.fhnw.aigs.Minesweeper.commons.SetUpBoardMessage;
import org.fhnw.aigs.commons.communication.GameEndsMessage;
import org.fhnw.aigs.commons.communication.GameStartMessage;
import org.fhnw.aigs.commons.communication.Message;

/**
 * This is an example game. 
 * The client can control how many mines are burried. This is somewhat different
 * to other approaches where the server is always in charge. The client controlled
 * approach allows the player to change the game's rules at runtime, e.g. via
 * prompt. In this case the values (e.g. total mines) are still hardcoded.
 * This could be changed easily.
 * @author Matthias Stöckli
 */
public class MinesweeperClientGame extends ClientGame{

    private MinesweeperBoard minesweeperBoard;
    
    private int xFields;
    private int yFields;
    private int totalMines;

    private int minesLeft = totalMines;
    
    public MinesweeperClientGame(String gameName, GameMode mode, int xFields, int yFields, int totalMines) {
        super(gameName, "v1.1", mode);
        this.xFields = xFields;
        this.yFields = yFields;
        this.totalMines = totalMines;
    }
    
    /**
     * This method is called when the connection has been established.
     * In this case no further action is required and the game is started 
     * immediately.
     */    
    @Override
    public void onGameReady() {
        startGame();
        SetUpBoardMessage setMineCountMessage = new SetUpBoardMessage(totalMines, xFields, yFields);
        sendMessageToServer(setMineCountMessage);
        gameWindow.getHeader().setStatusLabelText(minesLeft + " / " + totalMines);

    }

    /**
     * Processing of the MinesweeperClient logic. The following actions are defined:
     * <ul>
     * <li>Fade out loading screen.</li>
     * <li>Fields which are to be uncovered</li>
     * <li>Flag or undflag a field</li>
     * <li>Inform client about ending game</li>
     * </ul>
     * @param message The handled messges 
     */    
    @Override
    public void processGameLogic(Message message) {
        // GameStartMessage triggers the overlay to fade out.
        if(message instanceof GameStartMessage){
            gameWindow.fadeOutOverlay();
        }
        
        // A BoardChangeMessage shows which fields are to be uncovered.
        else if(message instanceof BoardChangeMessage){
            minesweeperBoard.manipulateGUI(message);
        
        }
        // Flags or unflags a field according to it's current state
        else if (message instanceof MarkFieldStatusMesage) {
            handleMarkFieldStatusMessage((MarkFieldStatusMesage) message);
        }
        // Ends the current game and asks the user whether he wants to restart the game.
        else if(message instanceof GameEndsMessage){
            handleGameEndsMessage((GameEndsMessage) message);
        }
        
    }


    /**
     * Sets the MinesweeperBoard.
     * @param minesweeperBoard The minesweeperBoard which represents the game's graphics.
     */
    public void setMinesweeperBoard(MinesweeperBoard minesweeperBoard){
        this.minesweeperBoard = minesweeperBoard;
    }
    
    /**
     * Changes the status label so that it reflects the number of flags/mines left.
     */
    public void increaseMinesLeft(){
        minesLeft++;
        gameWindow.getHeader().setStatusLabelText(minesLeft + " / " + totalMines);

    }
    
    /**
     * Restarts the game.
     */
    public void restart(){
        minesLeft = totalMines;
        minesweeperBoard.restartBoard();
    }

    /**
     * Flags or unflags a field.
     * @param markFieldStatusMessage 
     */
    private void handleMarkFieldStatusMessage(MarkFieldStatusMesage markFieldStatusMessage) {
        minesLeft = markFieldStatusMessage.getMinesLeft();
        gameWindow.getHeader().setStatusLabelText(minesLeft + " / " + totalMines);
        minesweeperBoard.manipulateGUI(markFieldStatusMessage);
    }
    
    /**
     * Ends the current game and asks the user whether he or she wants to restart
     * the game.
     * @param gameEndsMessage The GameEndMessage.
     * @throws HeadlessException 
     */
    private void handleGameEndsMessage(GameEndsMessage gameEndsMessage){
        String dialogueText = null;
        if(gameEndsMessage.getReason().equals("LOST")){
            noInteractionAllowed = true;
            minesweeperBoard.manipulateGUI(gameEndsMessage);
            dialogueText = "You lost. Do you want to restart the game?";
        }
        else{
            dialogueText = "You won! Do you want to restart the game?";
        }
        
        int result = JOptionPane.showConfirmDialog(null,
                dialogueText,
                "Restart?",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            restart();
            noInteractionAllowed = false;
            RestartMessage restartMessage = new RestartMessage();
            sendMessageToServer(restartMessage);
        } else {
            System.exit(0);
        }
    }
    
}
