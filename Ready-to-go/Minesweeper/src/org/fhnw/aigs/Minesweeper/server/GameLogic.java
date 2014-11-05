package org.fhnw.aigs.Minesweeper.server;

import java.security.SecureRandom;
import java.util.HashSet;
import org.fhnw.aigs.commons.Game;
import org.fhnw.aigs.commons.Player;
import org.fhnw.aigs.Minesweeper.commons.BoardChangeMessage;
import org.fhnw.aigs.Minesweeper.commons.MarkFieldMessage;
import org.fhnw.aigs.Minesweeper.commons.MarkFieldStatusMesage;
import org.fhnw.aigs.Minesweeper.commons.MinesweeperField;
import org.fhnw.aigs.Minesweeper.commons.RestartMessage;
import org.fhnw.aigs.Minesweeper.commons.SetUpBoardMessage;
import org.fhnw.aigs.commons.communication.FieldClickMessage;
import org.fhnw.aigs.commons.communication.GameEndsMessage;
import org.fhnw.aigs.commons.communication.Message;

/**
 * This class represents the game logic part of Minesweeper.<br>
 * v1.0 Initial release<br>
 * v1.1 Minor changes in Handling
 * @author Matthias Stöckli (v1.1)
 * @version v1.1
 */
public class GameLogic extends Game {

    MinesweeperField[] fields;
    /**
     * Number of fields in the y-axis.
     */
    int fieldsX;
    /**
     * Number of fields in the y-axis.
     */
    int fieldsY;
    /**
     * Number of hidden mines. Controlled by the client.
     */
    int totalMines;
    /**
     * number of mines minus number of flags
     */
    int minesLeft;
    boolean gameEnded = false;

    /**
     * Empty constructor which is necessary in order to load the game.<br>
     * Don't forgett to set the same game name in the class {@link org.fhnw.aigs.Minesweeper.client.Main} in the package 'client'.
     */    
    public GameLogic() throws Exception {
        super("Minesweeper", "v1.1", 1);
        // setUpFields will be called after "SetmineCountMessage" arrived.
        // See processGameLogic.
    }

    @Override
    public void initialize() {
        startGame(); // Starts the game
    }

    /**
     * When all fields which do not contain a mine are uncovered, the game ends.
     */
    @Override
    public void checkForWinningCondition() {
        // If one of the uncovered fields still doesn't contain a mine,
        // set this boolean to false.
        boolean hasWon = true;

        for (MinesweeperField f : fields) {
            // As soon as there is a field which is uncovered and is NOT a mine
            // the game goes on
            if (f.getIsUncovered() == false && f.getContainsMine() == false) {
                hasWon = false;
            }
        }

        if (hasWon) {
            // If the game has already ended (player won or lost) the winning conditions
            // are irrelevant.
            if (gameEnded == false) {
                gameEnded = true;
                GameEndsMessage gameEndsMessage = new GameEndsMessage("WON");
                sendMessageToPlayer(gameEndsMessage, getCurrentPlayer());
            }
        }
    }

    /**
     * The game logic of Minesweeper takes place in this method..
     *
     * @param message
     * @param player
     */
    @Override
    public void processGameLogic(Message message, Player player) {


        if (message instanceof SetUpBoardMessage) {
            setUpBoard(message);
        } else if (message instanceof RestartMessage) {
            restart();
        } else if (message instanceof MarkFieldMessage) {
            markField(message);
        } else if (message instanceof FieldClickMessage) {
            handleFieldClick(message, player);

        }
    }

    /**
     * Creates the logical entities of minesweeper fields
     */
    private void setUpFields() {
        for (int i = 0; i < fieldsX; i++) {
            for (int j = 0; j < fieldsY; j++) {
                fields[(i * fieldsX) + j] = new MinesweeperField();
                fields[(i * fieldsX) + j].setxPosition(i);
                fields[(i * fieldsX) + j].setyPosition(j);
            }
        }

        // Create a random number generator
        SecureRandom random = new SecureRandom();
        MinesweeperField field = null;

        /**
         * Create a new mine on a random position. If a position has already
         * been occupied by another mine, try again.
         */
        for (int i = 0; i < totalMines; i++) {
            boolean setField = false;
            while (setField == false) {
                int randomIndex = random.nextInt((fieldsX * fieldsY) - 1);
                field = fields[randomIndex];
                if (field.getContainsMine()) {
                    continue;
                } else {
                    field.setContainsMine(true);
                    setField = true;
                }
            }
        }
        calculateSurroundingMines();
    }

    /**
     * Gets all fields without mines around the specified field. The borders are marked by fields with mines in proximity 
     * @param field Field to be checked (center)
     * @return HashSet of all fields without mines around the specified field
     */
    private HashSet<MinesweeperField> coverUpConnectedFields(MinesweeperField field) {
        HashSet<MinesweeperField> nonMineFields = new HashSet<MinesweeperField>();
        MinesweeperField[] checkFields = new MinesweeperField[4];
        checkFields[0] = getFieldTranslatedByDirection(field, Direction.UPPER);
        checkFields[1] = getFieldTranslatedByDirection(field, Direction.LEFT);
        checkFields[2] = getFieldTranslatedByDirection(field, Direction.RIGHT);
        checkFields[3] = getFieldTranslatedByDirection(field, Direction.LOWER);

        for (MinesweeperField f : checkFields) {
            if (f != null && f.getContainsMine() == false && f.hasNoSurroundingMines() && f.getIsUncovered() == false) {
                // Check all blank fields again
                f.setIsUncovered(true);
                nonMineFields.add(f);
                nonMineFields.addAll(coverUpConnectedFields(f));
            } else if (f != null && f.getContainsMine() == false && f.hasNoSurroundingMines() == false) {
                // Don't check non-blank fields.
                nonMineFields.add(f);
                f.setIsUncovered(true);
            }
        }

        return nonMineFields;
    }

    /**
     * Method to set up the minesweeper board
     * @param message message to process
     */
    private void setUpBoard(Message message) {
        SetUpBoardMessage setUpBoardMessage = (SetUpBoardMessage) message; // Cast
        this.totalMines = setUpBoardMessage.getTotalMines();
        this.minesLeft = totalMines;
        fieldsX = setUpBoardMessage.getxFields();
        fieldsY = setUpBoardMessage.getyFields();
        fields = new MinesweeperField[fieldsX * fieldsY];
        setUpFields();
    }

    /**
     * Restarts the game
     */
    private void restart() {
        gameEnded = false;
        this.minesLeft = totalMines;
        setUpFields();
    }

    /**
     * Marks a filed as uncovered (nothing to do), as flagged or as unflagged
     * @param message Message to process
     */
    private void markField(Message message) {
        MarkFieldMessage markFieldMessage = (MarkFieldMessage) message; // Cast
        boolean isFlagged;

        int x = markFieldMessage.getPositionX();
        int y = markFieldMessage.getPositionY();
        MinesweeperField field = fields[x * fieldsX + y];

        if (field.getIsUncovered()) {
            return;
        }
        if (field.getHasFlag()) {
            isFlagged = false;
            field.setHasFlag(false);
            minesLeft++;
        } else {
            isFlagged = true;
            field.setHasFlag(true);
            minesLeft--;
        }
        MarkFieldStatusMesage markFieldStatusMesage = new MarkFieldStatusMesage(x, y, isFlagged, minesLeft);
        sendMessageToPlayer(markFieldStatusMesage, getCurrentPlayer());
    }

    /**
     * Handles the user action when clicking on a flied
     * @param message Message to process
     * @param player Player who clicked
     */
    private void handleFieldClick(Message message, Player player) {
        FieldClickMessage fieldClickMessage = (FieldClickMessage) message; // Cast

        int x = fieldClickMessage.getXPosition();
        int y = fieldClickMessage.getYPosition();
        MinesweeperField field = fields[x * fieldsX + y];

        // Ignore the move if the field was already uncovered or flagged
        if (field.getIsUncovered() == true || field.getHasFlag()) {
            return;
        }
        if (field.getContainsMine()) {
            handleGameEnd(player);
        } else if (field.getSurroundingMinesCount() > 0) {
            uncoverDangerousField(field, player);
        } else {
            uncoverHarmlessField(field, player);
        }
    }

    /**
     * Iterates through every field, then check the surrounding fields for mines
     * and add one to the minecount of the field for every surrounding mine.
     */
    private void calculateSurroundingMines() {
        MinesweeperField field;

        // Iteration
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            MinesweeperField[] checkFields = new MinesweeperField[8];

            // Look up fields
            checkFields[0] = getFieldTranslatedByDirection(field, Direction.UPPER_LEFT);
            checkFields[1] = getFieldTranslatedByDirection(field, Direction.UPPER);
            checkFields[2] = getFieldTranslatedByDirection(field, Direction.UPPER_RIGHT);
            checkFields[3] = getFieldTranslatedByDirection(field, Direction.LEFT);
            checkFields[4] = getFieldTranslatedByDirection(field, Direction.RIGHT);
            checkFields[5] = getFieldTranslatedByDirection(field, Direction.LOWER_LEFT);
            checkFields[6] = getFieldTranslatedByDirection(field, Direction.LOWER);
            checkFields[7] = getFieldTranslatedByDirection(field, Direction.LOWER_RIGHT);

            // If there IS a field (not null) and there is a mine, add one to count.
            for (MinesweeperField f : checkFields) {
                if (f != null && f.getContainsMine()) {
                    field.addOneToMineCount();
                }
            }
        }
    }

    /**
     * Finds one of the surrounding fields of another field.
     *
     * @param field The field to start.
     * @param direction The direction in which direction the field to be found
     * lies.
     * @return
     */
    private MinesweeperField getFieldTranslatedByDirection(MinesweeperField field, Direction direction) {
        int x = field.getxPosition();
        int y = field.getyPosition();

        /**
         * Every {@link Direction} translates the fields. Basis of the
         * translation is the one dimensional array in which the fields are
         * stored.
         */
        switch (direction) {
            case UPPER_LEFT:
                x--;
                y--;
                break;
            case UPPER:
                y--;
                break;
            case UPPER_RIGHT:
                x++;
                y--;
                break;
            case LEFT:
                x--;
                break;
            case RIGHT:
                x++;
                break;
            case LOWER_LEFT:
                x--;
                y++;
                break;
            case LOWER:
                y++;
                break;
            case LOWER_RIGHT:
                x++;
                y++;
                break;
        }

        // Is out of bounds, don't try
        if (x < 0 || x > fieldsX || y < 0 || y > fieldsY) {
            return null;
        }

        // Go through all fields and return the one with the calculated coordinates
        for (MinesweeperField f : fields) {
            if (f.getxPosition() == x && f.getyPosition() == y) {
                return f;
            }
        }
        return null;
    }

    /**
     * If the player clicked on a mine, the game will end. All fields will be
     * uncovered. The user will be asked whether he wants to restart the game.
     *
     * @param player A reference to the player.
     */
    private void handleGameEnd(Player player) {
        gameEnded = true;
        // first send a BoardChangedMessage (client will then see all fields 
        for (int i = 0; i < fields.length; i++) {
            fields[i].setIsUncovered(true);
        }
        BoardChangeMessage boardChangeMessage = new BoardChangeMessage(fields);
        sendMessageToPlayer(boardChangeMessage, player);

        GameEndsMessage gameEndMessage = new GameEndsMessage("LOST");
        sendMessageToPlayer(gameEndMessage, player);
    }

    /**
     * Uncovers a field which is surrounded by one or more mines.
     *
     * @param field The clicked field.
     * @param player A reference to the player.
     */
    private void uncoverDangerousField(MinesweeperField field, Player player) {
        HashSet<MinesweeperField> uncoveredMinesweeperFields = new HashSet<MinesweeperField>();
        field.setIsUncovered(true);
        uncoveredMinesweeperFields.add(field);

        // Set all uncovered fields as unflagged
        for (MinesweeperField f : uncoveredMinesweeperFields) {
            if (field.getHasFlag()) {
                field.setHasFlag(false);
                minesLeft++;
            }

        };

        // Send a message with all the uncovered fields.
        BoardChangeMessage boardChangeMessage = new BoardChangeMessage(uncoveredMinesweeperFields.toArray(new MinesweeperField[uncoveredMinesweeperFields.size()]));
        sendMessageToPlayer(boardChangeMessage, player);
    }

    /**
     * Uncover all fileds without a mine
     * @param field Field where player clicked
     * @param player Player who clicked
     */
    private void uncoverHarmlessField(MinesweeperField field, Player player) {
        HashSet<MinesweeperField> uncoveredMinesweeperFields = coverUpConnectedFields(field);

        // Set all uncovered fields as unflagged
        if (field.getHasFlag()) {
            field.setHasFlag(false);
            minesLeft++;
        }

        // Additionally add the clicked field to the list of all uncovered fields.
        uncoveredMinesweeperFields.add(field);

        // Send a message with all the uncovered fields.
        BoardChangeMessage boardChangeMessage = new BoardChangeMessage(uncoveredMinesweeperFields.toArray(new MinesweeperField[uncoveredMinesweeperFields.size()]));
        sendMessageToPlayer(boardChangeMessage, player);
    }

    /**
     * This enumeration is just a shorthand for the surrounding mines algorithm.
     * See {@link GameLogic#getFieldTranslatedByDirection}.
     */
    public enum Direction {

        UPPER_LEFT,
        UPPER,
        UPPER_RIGHT,
        LEFT,
        RIGHT,
        LOWER_LEFT,
        LOWER,
        LOWER_RIGHT
    }
}
