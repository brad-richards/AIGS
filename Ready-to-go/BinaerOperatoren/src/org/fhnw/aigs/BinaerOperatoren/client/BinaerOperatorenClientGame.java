package org.fhnw.aigs.BinaerOperatoren.client;

import javax.swing.JOptionPane;
import org.fhnw.aigs.BinaerOperatoren.commons.MaxScoreMessage;
import org.fhnw.aigs.BinaerOperatoren.commons.QuestionMessage;
import org.fhnw.aigs.BinaerOperatoren.commons.StatusMessage;
import org.fhnw.aigs.commons.communication.GameEndsMessage;
import org.fhnw.aigs.commons.communication.GameStartMessage;
import org.fhnw.aigs.commons.communication.Message;
import org.fhnw.aigs.swingClient.gameHandling.ClientGame;
    
/**
 * This class represents the client side of the BinaerOperatorenSpiel.<br>
 * v1.0 Initial release<br>
 * v1.0.1 Minor changes due to versioning of the program (Program version is v1.1)
 * @author Matthias Stöckli
 * @version v1.0.1
 */
public class BinaerOperatorenClientGame extends ClientGame{

    /**
     * A reference to the game's graphical representation
     */
    private BinaerOperatorenPanel binaerOperatorenPanel;
    
    /**
     * The current score of the game. 
     */
    private int currentScore = 0;
    
    /**
     * The score the player must achieve to win the game.
     * It is defined by the GameLogic class on the server side.
     */
    private int maxScore = 0;
    
    /**
     * Creates a new instance of BinaerOperatorenClientGame.
     * @param gameName The name of the game ("BinaerOperatoren")
     */
    public BinaerOperatorenClientGame(String gameName){
        super(gameName);
        this.setVersionString("v1.1"); // Version of the game
    }
    
    /**
     * The incoming messages will be processed in this method.
     * @param message 
     */
    @Override
    public void processGameLogic(Message message) {

        // Start the game, remove the loading screen
        if(message instanceof GameStartMessage){
            getGameWindow().setContent(binaerOperatorenPanel);
        }
        // Set the max score
        else if(message instanceof MaxScoreMessage){
            MaxScoreMessage maxScoreMessage = (MaxScoreMessage)message;
            this.maxScore = maxScoreMessage.getMaxScore();
            binaerOperatorenPanel.manipulateGUI(message);
        }

        // Show a new question
        else if(message instanceof QuestionMessage){
            binaerOperatorenPanel.manipulateGUI(message);
        }
        // Change the status label
        else if(message instanceof StatusMessage){
            binaerOperatorenPanel.manipulateGUI(message);
        }
        // Show a game end message
        else if (message instanceof GameEndsMessage) {
            setNoInteractionAllowed(true);
            GameEndsMessage gameEndMessage = (GameEndsMessage) message;
            JOptionPane.showMessageDialog(null, gameEndMessage.getReason(), "Game ends", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);

        }
    }

    /**
     * Just send a JoinMessage as soon as the succesful identification has been
     * confirmed.
     */
    @Override
    public void onGameReady() {
        startGame();
    }

    /**
     * Adds the BinaerOperatorenPanel to the game.
     * @param binaerOperatorenPanel 
     */
    void addPanel(BinaerOperatorenPanel binaerOperatorenPanel) {
        this.binaerOperatorenPanel = binaerOperatorenPanel;
    }

    /**
     * Adds or substracts the value to the score.
     * @param amount The amount.
     */
    public void changeScore(int amount) {
        currentScore += amount;
    }
    
    /**
     * Gets the current score of the game.
     * @return The current score.
     */
    public int getScore(){
        return currentScore;
    }

    /**
     * Gets the maximum score that is needed to win the game.
     * @return The maximum score.
     */
    public int getMaxScore() {
        return maxScore;
    }

    
}
