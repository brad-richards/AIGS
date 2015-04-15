package org.fhnw.aigs.RockPaperScissors.client;

import javax.swing.JOptionPane;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsParticipantsMessage;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsResultMessage;
import org.fhnw.aigs.client.gameHandling.ClientGame;
import org.fhnw.aigs.commons.GameMode;
import org.fhnw.aigs.commons.communication.GameEndsMessage;
import org.fhnw.aigs.commons.communication.GameStartMessage;
import org.fhnw.aigs.commons.communication.Message;

/**
 * Class to handle client-side game processing, and process messages from the server
 */
public class RockPaperScissorsClientGame extends ClientGame {
    private RockPaperScissorsBoard clientBoard;  // Reference to the GUI

    /**
     * Getter for clientBoard
     * @return RockPaperScissorsBoard-Objekt
     */
    public RockPaperScissorsBoard getClientBoard() {
        return clientBoard;
    }

    /**
     * Setter for clientBoard
     * @param clientBoard RockPaperScissorsBoard-Objekt
     */
    public void setClientBoard(RockPaperScissorsBoard clientBoard) {
        this.clientBoard = clientBoard;
    }
    
    /**
     * Constructor called to create a new game instance; everything is
     * handled by the constructor in the superclass
     * @param gameName Name of the game
     * @param mode GameMode (SinglePlayer, MultiPlayer)
     * @param version Program version
     */
    public RockPaperScissorsClientGame(String gameName, String version, GameMode mode)
    {
        super(gameName, version, mode);
    }

    /**
     * Process server messages
     * @param message Message from the server; this must be one of
     * the messages defined in the commons pachage
     */
    @Override
    public void processGameLogic(Message message) {
        if (message instanceof GameStartMessage) {
            gameWindow.fadeOutOverlay();  // Fade in the game board
        } else if (message instanceof RockPaperScissorsParticipantsMessage) {
        	// Set opponent's name, and the header
            RockPaperScissorsParticipantsMessage msg = (RockPaperScissorsParticipantsMessage)message;
            if (msg.getPlayerOne().getName().equals(player.getName())) {
                clientBoard.setNamesAndPoints(msg.getPlayerTwo().getName(), 0, 0); 
            } else {
               clientBoard.setNamesAndPoints(msg.getPlayerOne().getName(), 0, 0);
            }
            clientBoard.setHeader("Waiting for opponent's move...");
        } else if (message instanceof RockPaperScissorsResultMessage) {
        	// Update the GUI to reflect move result; prepare for next round
            clientBoard.setHeader("Move is finished");
            RockPaperScissorsResultMessage result = (RockPaperScissorsResultMessage) message;
            clientBoard.updateGUI(result.getOpponentSymbol(), result.getMyState(), result.getMySymbol(), result.getOpponentName(), result.getOpponentPoints(), result.getMyPoints());
            clientBoard.nextTurn(result.getTurnMessage(), result.isIsLastTurn());
        } else if (message instanceof GameEndsMessage) {  // Standard message from AIGS commons
        	// Disable GUI, display end-of-game dialog, end program
            setNoInteractionAllowed(true);
            GameEndsMessage gameEndMessage = (GameEndsMessage) message;
            JOptionPane.showMessageDialog(null, gameEndMessage.getReason(), "Game ends", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    /**
     * Called when the game is ready to start
     */
    @Override
    public void onGameReady() {
        startGame();
        clientBoard.setHeader("Waiting for opponent's move...");
    }    
}
