package org.fhnw.aigs.TicTacToeSwing.server;

import java.util.Random;
import org.fhnw.aigs.commons.FieldStatus;
import org.fhnw.aigs.commons.Game;
import org.fhnw.aigs.commons.GameMode;
import org.fhnw.aigs.commons.Player;
import org.fhnw.aigs.commons.communication.FieldClickFeedbackMessage;
import org.fhnw.aigs.commons.communication.FieldClickMessage;
import org.fhnw.aigs.commons.communication.GameEndsMessage;
import org.fhnw.aigs.commons.communication.Message;
// -- References to internal packages (of this game)
import org.fhnw.aigs.TicTacToeSwing.commons.TicTacToeFieldChangedMessage;
import org.fhnw.aigs.TicTacToeSwing.commons.TicTacToeSymbol;

/**
 * This class represents the game logic part of TicTacToe. The size is defined
 * in the variables xFields and yFields which are set in the constructor. <br>
 * The game logic contains a multidimensional array of TicTacToeFields which
 * represent the fields, see {@link GameLogic#ticTacToeFields}.
 *
 * @author Matthias Stöckli
 * @version v1.0
 */
public class GameLogic extends Game {

    /**
     * The fields of the TicTacToe game. The first dimension represents the
     * x-axis, the second the y-axis. Therefore
     * <code>ticTacToeFields[1][1]</code> returns
     * the field in the middle of the board.
     */
    private TicTacToeField[][] ticTacToeFields;
    /**
     * The number if fields in the x-axis (3)
     */
    private final int xFields;
    /**
     * The number of fields in the y-axis (3)
     */
    private final int yFields;

    /**
     * Empty constructor which is necessary in order to load the game.
     */
    public GameLogic() {
        super("TicTacToeSwing", "v1.1", 2); // VERY IMPORTANT! The game name must be unique on the server (only onece 'TicTacToeSwing')
        xFields = 3;
        yFields = 3;
    }

    /**
     * See {@link Game#initialize} for more information about the way this
     * method works.<br>
     * This implementation checks for the GameMode. If SinglePlayer has been
     * chosen, a new player (the AI) will be generated and added to the list of
     * players. Afterwards, a random player will be picked, he or she will begin
     * the game. then, the game will be started.
     */
    @Override
    public void initialize() {
        setUpFields();

        if (gameMode == GameMode.SinglePlayer) {
            aiPlayer = new Player("AI", true);
            players.add(aiPlayer);
        }

        setCurrentPlayer(getRandomPlayer());
        if (getCurrentPlayer().equals(aiPlayer)) {
            aiAction();
        }
        startGame();

    }

    /**
     * Creates the (logical) fields of the game and fills them into
     * {@link GameLogic#ticTacToeFields}.
     */
    private void setUpFields() {
        ticTacToeFields = new TicTacToeField[xFields][yFields];
        for (int i = 0; i < xFields; i++) {
            for (int j = 0; j < yFields; j++) {
                ticTacToeFields[i][j] = new TicTacToeField(i, j);
            }
        }
    }

    /**
     * See {@link Game#processGameLogic} for more information about the way this
     * method works.<br>
     * ProcessGameLogic processes all the messages that control the flow of this
     * game.
     *
     * @param message The message passed from ServerMessageBroker.
     * @param sendingPlayer The player who sent the message.
     */
    @Override
    public void processGameLogic(Message message, Player sendingPlayer) {
        if (message instanceof FieldClickMessage) {


            int clickedX = ((FieldClickMessage) message).getXPosition();        // X-Coordinates on the game board
            int clickedY = ((FieldClickMessage) message).getYPosition();        // Y-Coordinates on the gameBoard

            TicTacToeField clickedField = ticTacToeFields[clickedX][clickedY];          // Get the TicTacToeField according to the coordinates

            // If the field's "controllingPlayer" value is null, the field is empty, otherwise it is controlled by a player
            boolean isClickedFieldEmpty = clickedField.getControllingPlayer() == null;

            // A FieldClickFeedBackMessage is prepared to inform the sender of the status of his or her turn
            FieldClickFeedbackMessage fieldClickFeedbackMessage;

            // If the field is empty...
            if (isClickedFieldEmpty == true && sendingPlayer.equals(getCurrentPlayer())) {
                clickedField.setControllingPlayer(sendingPlayer);                       // Set the current player as controlling player
                fieldClickFeedbackMessage = new FieldClickFeedbackMessage(clickedX, clickedY, FieldStatus.OK);      // Field is still unoccupied
                sendMessageToPlayer(fieldClickFeedbackMessage, sendingPlayer);          // Send a feedback about the turn to the sending player 

                // Determine the symbol (Cross or Nought): The first player (index == 0) is Cross, the second one (index != 0) is Nought;
                TicTacToeSymbol symbol = players.indexOf(getCurrentPlayer()) == 0 ? TicTacToeSymbol.Cross : TicTacToeSymbol.Nought;

                // Set up a FieldChangedMessage to inform all players about the new game situation
                TicTacToeFieldChangedMessage fieldChangedMessage = new TicTacToeFieldChangedMessage(clickedX, clickedY, sendingPlayer, symbol);
                sendMessageToAllPlayers(fieldChangedMessage);

                if (gameMode == GameMode.SinglePlayer) {
                    passTurnToNextPlayer();
                    aiAction();
                } else if (gameMode == GameMode.Multiplayer) {
                    passTurnToNextPlayer();
                }

            } else if (clickedField.hasPlayer() && clickedField.getControllingPlayer().equals(getCurrentPlayer()) == false) {
                // Field is blocked by opponent's field
                fieldClickFeedbackMessage = new FieldClickFeedbackMessage(clickedX, clickedY, FieldStatus.NoChange);
                sendMessageToPlayer(fieldClickFeedbackMessage, sendingPlayer);
            } else if (clickedField.hasPlayer()) {
// Field is occupied by the player himself
                fieldClickFeedbackMessage = new FieldClickFeedbackMessage(clickedX, clickedY, FieldStatus.Blocked);
                sendMessageToPlayer(fieldClickFeedbackMessage, sendingPlayer);
            }
        }

    }

    /**
     * Checks whether one of the player meets the winning conditions: A full
     * row, a full column or three diagonal fields.
     */
    @Override
    public void checkForWinningCondition() {
        for (Player p : players) {
            if ( // Check rows
                    (cfp(0, 0, p) && cfp(0, 1, p) && cfp(0, 2, p))
                    || (cfp(1, 0, p) && cfp(1, 1, p) && cfp(1, 2, p))
                    || (cfp(2, 0, p) && cfp(2, 1, p) && cfp(2, 2, p))
                    || // Check columns
                    (cfp(0, 0, p) && cfp(1, 0, p) && cfp(2, 0, p))
                    || (cfp(0, 1, p) && cfp(1, 1, p) && cfp(2, 1, p))
                    || (cfp(0, 2, p) && cfp(1, 2, p) && cfp(2, 2, p))
                    || // Check diagonals
                    (cfp(0, 0, p) && cfp(1, 1, p) && cfp(2, 2, p))
                    || (cfp(2, 0, p) && cfp(1, 1, p) && cfp(0, 2, p))) {
                GameEndsMessage gameEndsMessage = new GameEndsMessage(p.getName() + " won");
                sendMessageToAllPlayers(gameEndsMessage);
                return;
            }
        }
        boolean isDraw = calculateDraw();

        if (isDraw) {
            GameEndsMessage gameEndsMessage = new GameEndsMessage("Draw!");
            sendMessageToAllPlayers(gameEndsMessage);
        }
    }

    /**
     * Checks whether a player controls a field.
     *
     * @param x The x coordinate of the field.
     * @param y The y coordinate of the field.
     * @param p The player to be checked against.
     * @return True if the given player controls the field.
     */
    private boolean cfp(int x, int y, Player p) {
        // Prevent NullPointerException if there is no player
        if (ticTacToeFields[x][y].hasPlayer() == true) {
            return ticTacToeFields[x][y].getControllingPlayer().equals(p);
        } else {
            return false;
        }
    }

    /**
     * Method calculates draw condition in the game.<br>
     * If all the fields are occupied, then it is a draw.
     * @return True, if draw, otherwise false
     */
    private boolean calculateDraw() {
        // Calculate draw: If all the fields are occupied, then it is a draw.
        // Use binary boolean operators. As soon as a value is false the
        // "AND" operator (&) will be set to false thus indicating that
        // one field is not occupied.
        boolean isOccupied = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                isOccupied &= ticTacToeFields[i][j].hasPlayer();
            }
        }
        return isOccupied;
    }

    /**
     * The AI is controlled by this method. In this game the AI does not act
     * very intelligent. It simply takes a random field which is still not
     * occupied.
     */
    private void aiAction() {
        boolean turnIsPossible;
        // X and Y coordinates.
        int randomX;
        int randomY;
        Random r = new Random();
        do {
            // Get two random numbers.
            randomX = (int) r.nextInt(xFields);
            randomY = (int) r.nextInt(yFields);

            // Check whether the field is still unoccupied.
            turnIsPossible = ticTacToeFields[randomX][randomY].getControllingPlayer() == null;
            // Check for draw
            if (calculateDraw()) {
                return;
            }
        } while (turnIsPossible == false);

        // Determine which symbol is used (Cross or Nought)
        TicTacToeSymbol symbol = players.indexOf(aiPlayer) == 0 ? TicTacToeSymbol.Cross : TicTacToeSymbol.Nought;

        // Set the AI as the new controlling player.
        ticTacToeFields[randomX][randomY].setControllingPlayer(aiPlayer);

        // Send a "FieldChangedMessage" to the player.
        TicTacToeFieldChangedMessage fieldChangedMessage = new TicTacToeFieldChangedMessage(randomX, randomY, aiPlayer, symbol);
        sendMessageToAllPlayers(fieldChangedMessage);

        // Pass the turn to the next player.
        passTurnToNextPlayer();
    }
}
