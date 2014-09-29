package org.fhnw.aigs.Minesweeper.client;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.fhnw.aigs.client.GUI.BaseBoard;
import org.fhnw.aigs.client.gameHandling.ClientGame;
import org.fhnw.aigs.Minesweeper.commons.BoardChangeMessage;
import org.fhnw.aigs.Minesweeper.commons.MarkFieldMessage;
import org.fhnw.aigs.Minesweeper.commons.MarkFieldStatusMesage;
import org.fhnw.aigs.Minesweeper.commons.MinesweeperField;
import org.fhnw.aigs.commons.communication.FieldClickMessage;
import org.fhnw.aigs.commons.communication.Message;

/**
 * This is the class representing the Minesweeper board.
 * It contains x x y MinesweeperPanes.
 * All event handlers and animations as well as the creation of the MinesweeperPanes
 * occurs here.
 * @author Matthias Stöckli
 */
public class MinesweeperBoard extends BaseBoard {

    /**
     * Create a new instance of MinesweeperBoard.
     * @param fieldsX Number of fields in the x-axis.
     * @param fieldsY Number of fields in the y-axis.
     * @param clientGame Reference to the ClientGame.
     */
    public MinesweeperBoard(int fieldsX, int fieldsY, ClientGame clientGame) {
        super(fieldsX, fieldsY, clientGame);    
    }

    /**
     * Sets up the Minesweeper fields (graphical representations).
     */
    @Override
    public void setFields() {
        for (int i = 0; i < fieldsX; i++) {
            for (int j = 0; j < fieldsY; j++) {
                MinesweeperPane pane = new MinesweeperPane();
                fieldPanes[i][j] = pane;
                this.add(pane, i, j);
            }
        }
    }
    
    /**
     * Changes the GUI according to the incoming messages.
     * @param message The incoming message.
     */
    @Override
    public void manipulateGUI(Message message) {
        
        // Flags or unflags a field.
        if (message instanceof MarkFieldStatusMesage) {
            handleMarkFieldStatusMessage((MarkFieldStatusMesage)message);
        }
        // Covers or uncovers a field.
        else if (message instanceof BoardChangeMessage) {
            handleBoardChangeMessage((BoardChangeMessage) message);
        }
    }

    /**
     * Wires the following actions to the fields: When a player clicks the
     * primary button (left mouse button), the field will be uncovered. If he or
     * she clicks the secondary button (right mouse button), then the field will
     * be flagged.
     */
    @Override
    public void setEventHandlers() {
        // Go through all fields
        for (int i = 0; i < this.getFieldsX(); i++) {
            for (int j = 0; j < this.getFieldsY(); j++) {

                // Set an anonymous event handler which reacts on clicks
                this.getField(i, j).setOnMouseClicked(
                        new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {

                        MinesweeperPane sourceField = (MinesweeperPane) event.getSource(); // Get the source, i.e. which field was clicked
                        int xPosition = getColumnIndex(sourceField);                    // Get the column
                        int yPosition = getRowIndex(sourceField);                       // Get the row

                        if (event.getButton().equals(MouseButton.PRIMARY)) {
                            // Handle left click                            	
                            // Create a field click message based on the coordinates
                            FieldClickMessage fieldClickMessage = new FieldClickMessage(xPosition, yPosition);
                            clientGame.sendMessageToServer(fieldClickMessage);

                        } else if (event.getButton().equals(MouseButton.SECONDARY)) {
                            // Handle right click (mark mine)
                            MarkFieldMessage markFieldMessage = new MarkFieldMessage(xPosition, yPosition);
                            clientGame.sendMessageToServer(markFieldMessage);
                        }

                    }
                });

            }
        }
    }

    /**
     * Covers all fields and removes all images.
     */
    public void restartBoard() {
        for (int i = 0; i < fieldsX; i++) {
            for (int j = 0; j < fieldsY; j++) {
                ((MinesweeperPane) fieldPanes[i][j]).removeImage();
                ((MinesweeperPane) fieldPanes[i][j]).cover();
            }
        }
    }

    
    /**
     * Unflags or flags the field.
     * @param markFieldStatusMesage The MarkFieldStatusMessage 
     */
    private void handleMarkFieldStatusMessage(MarkFieldStatusMesage markFieldStatusMesage) {
        int x = markFieldStatusMesage.getPositionX();
        int y = markFieldStatusMesage.getPositionY();
        if (markFieldStatusMesage.getHasFlag()) {
            ((MinesweeperPane) fieldPanes[x][y]).setFlag();
        } else {
            ((MinesweeperPane) fieldPanes[x][y]).removeImage();
        }
    }
    
    /**
     * Covers or uncovers an array of fields.
     * @param boardChangeMessage The BoardChangeMessage.
     */
    private void handleBoardChangeMessage(BoardChangeMessage boardChangeMessage) {
        for (MinesweeperField field : boardChangeMessage.getUncoveredMinesweeperFields()) {
            int x = field.getxPosition();
            int y = field.getyPosition();
            if (field.getHasFlag()) {
                ((MinesweeperClientGame) clientGame).increaseMinesLeft();
            }
            ((MinesweeperPane) fieldPanes[x][y]).uncover(field);
        }
    }
}
