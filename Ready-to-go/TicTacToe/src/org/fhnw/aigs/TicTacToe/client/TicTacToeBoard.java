package org.fhnw.aigs.TicTacToe.client;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.fhnw.aigs.client.GUI.BaseBoard;
import org.fhnw.aigs.client.gameHandling.ClientGame;
import org.fhnw.aigs.commons.FieldStatus;
import org.fhnw.aigs.commons.communication.FieldClickFeedbackMessage;
import org.fhnw.aigs.commons.communication.FieldClickMessage;
import org.fhnw.aigs.commons.communication.Message;
// -- References to internal packages (of this game)
import org.fhnw.aigs.TicTacToe.commons.TicTacToeFieldChangedMessage;
import org.fhnw.aigs.TicTacToe.commons.TicTacToeSymbol;

/**
 * This is the class representing the TicTacToe board.
 * It contains 3 x 3 TicTacToePanes.
 * All event handlers and animations as well as the creation of the TicTacToePanes
 * occurs here.<br>
 * v1.0 Initial release<br>
 * v1.0.1 Minor changes
 * @author Matthias Stöckli (v1.0)
 * @version v1.0.1
 */
public class TicTacToeBoard extends BaseBoard{
     
    public TicTacToeBoard(int x, int y, ClientGame clientGame){
        super(x, y, clientGame);
        this.setFields();                        // Define the appearance of the board and the fields
        this.setEventHandlers();                 // Set the event handlers, i.e. how the GUI will react
    }
    
    /**
     * Sets the field appearance of the TicTacToeFields.
     * In this case it is sufficient to iterate through the x and y axis
     * and create a new TicTacToeField for every field. Then this new field
     * is added to the TicTacToeBoard. <br>
     */
    @Override
    public void setFields(){
     for(int i = 0; i < fieldsX; i++){
         for(int j = 0; j < fieldsY; j++){
             TicTacToePane ticTacToePane = new TicTacToePane();
             fieldPanes[i][j] = ticTacToePane;             
             this.add(ticTacToePane, i, j);
         }
     }
    }
    
    /**
     * This method wires the TicTacToeFields to their respective actions.
     * Whenever one of the fields is clicked, a FieldClickedMessage will be sent
     * which is based upon the clicked field. The server will then react.
     */
    @Override
    public void setEventHandlers(){
        // Go through all fields
        for(int i = 0; i < this.getFieldsX(); i++){
            for(int j = 0; j < this.getFieldsY(); j++){
                
                // Set an anonymous event handler which reacts on clicks
                this.getField(i, j).setOnMouseClicked(
                        new EventHandler<MouseEvent>(){
                            @Override
                            public void handle(MouseEvent event) {
                                TicTacToePane sourceField = (TicTacToePane)event.getSource(); // Get the source, i.e. which field was clicked
                                int xPosition = getColumnIndex(sourceField);                    // Get the column
                                int yPosition = getRowIndex(sourceField);                       // Get the row
                                
                                // Create a field click message based on the coordinates
                                FieldClickMessage fieldClickMessage = new FieldClickMessage(xPosition, yPosition);
                                    clientGame.sendMessageToServer(fieldClickMessage);                                
                            }});
            }
        }
    }
    
    /**
     * This method is responsible for any UI changes.
     * @param message 
     */
    @Override
    public void manipulateGUI(Message message){
        if(message instanceof TicTacToeFieldChangedMessage){
            changeField((TicTacToeFieldChangedMessage)message);          
        }
        else if(message instanceof FieldClickFeedbackMessage){
            provideFeedback((FieldClickFeedbackMessage)message);
        }
    }
    /**
     * Changes the field according to the TicTacToeFieldChangedMessage sent by the
     * server.
     * @param fieldChangedMessage The message sent by the server.
     */
    private void changeField(TicTacToeFieldChangedMessage fieldChangedMessage) {
        int x = fieldChangedMessage.getXPosition();
        int y = fieldChangedMessage.getYPosition();
        TicTacToePane changedField = (TicTacToePane)this.getField(x, y);
        
        TicTacToeSymbol playerSymbol = fieldChangedMessage.getPlayerSymbol();
        changedField.setPlayerSymbol(playerSymbol);
    }

    /**
     * Provides a feedback based on the feedback message sent by the server.
     * @param fieldClickFeedbackMessage The message which indicates which field was clicked
     */
    private void provideFeedback(FieldClickFeedbackMessage fieldClickFeedbackMessage) {
        int x = fieldClickFeedbackMessage.getxPosition();
        int y = fieldClickFeedbackMessage.getyPosition();
        TicTacToePane changedField = (TicTacToePane)this.getField(x, y);

        if(fieldClickFeedbackMessage.getFieldStatus() == FieldStatus.Blocked || fieldClickFeedbackMessage.getFieldStatus() == FieldStatus.NoChange ){
                        changedField.playBusyAnimation();
        }
    }
    
}
