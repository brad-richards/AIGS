package org.fhnw.aigs.TicTacToeSwing.client;


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import org.fhnw.aigs.swingClient.GUI.BaseBoard;
import org.fhnw.aigs.swingClient.gameHandling.ClientGame;
import org.fhnw.aigs.TicTacToeSwing.commons.TicTacToeFieldChangedMessage;
import org.fhnw.aigs.TicTacToeSwing.commons.TicTacToeSymbol;
import org.fhnw.aigs.commons.communication.FieldClickFeedbackMessage;
import org.fhnw.aigs.commons.communication.FieldClickMessage;
import org.fhnw.aigs.commons.communication.Message;

/**
 * This is the class representing the TicTacToe board.
 * It contains 3 x 3 TicTacToePanes.
 * All event handlers and animations as well as the creation of the TicTacToePanes
 * occurs here.<br>
 * v1.0 Initial release<br>
 * v1.1 Changed Handling (integration of setFields() and SetEventHandler() into constructor)
 * @author Matthias Stöckli (v1.0)
 * @version v1.1
 */
public class TicTacToeBoard extends BaseBoard{
     
    public TicTacToeBoard(int x, int y, ClientGame clientGame){
        super(x, y, clientGame);
        this.setFields();
        this.setEventHandlers();
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
             TicTacToePane ticTacToePane = new TicTacToePane(i, j);
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
                
                // Event handler for the fields - reacts to the user's clicks.
                // Because we know that the children of all JPanels are JButtons
                // we can assume that 
                
                ((TicTacToePane)this.getField(i, j)).getButton().addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        TicTacToePane sourceField = (TicTacToePane)((JButton)event.getSource()).getParent(); // Get the source, i.e. which field was clicked
                        int xPosition = sourceField.getXPosition();                    // Get the column
                        int yPosition = sourceField.getYPosition();                       // Get the row

                        // Create a field click message based on the coordinates
                        FieldClickMessage fieldClickMessage = new FieldClickMessage(xPosition, yPosition);
                        clientGame.sendMessageToServer(fieldClickMessage);
                    }
                });
            }
        }
    }
    
    /**
     * This method is responsible for any UI changes.
     * @param message 
     */
    @Override
    public void manipulateGUI(Message message){
        // If one of the players changed the game situation, change the field
        if (message instanceof TicTacToeFieldChangedMessage) {
            changeField((TicTacToeFieldChangedMessage) message);
        } else
        // Provide the user with a feedback if the server sends a
        // FieldClickFeedbackMessage upon user interaction
        if(message instanceof FieldClickFeedbackMessage){
            provideFeedback();
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
     * Plays a "beep" sound when the user clicked on an invalid field.
     */
    private void provideFeedback() {
        Toolkit.getDefaultToolkit().beep(); 

    }
    
}
