package org.fhnw.aigs.RockPaperScissors.client;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.fhnw.aigs.RockPaperScissors.commons.GameState;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsSelectionMessage;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsSymbol;

/**
 * Class defining the game board
 */
public class RockPaperScissorsBoard extends GridPane {
    private RockPaperScissorsClientGame clientGame;		// ClientGame object
    private GridPane fieldsPane;						// GridPane for selection fields
    private GridPane opponentPane;						// GridPane to display opponent's move
    private Label opponentLabel;						// Label for opponent name and points
    private Label myLabel;								// Label for our name and points
    private Separator separator;						// Horizontal line as separator
    private RockPaperScissorsBoardPane opponentField;	// Pane to display opponent's move
    private RockPaperScissorsBoardPane rockField;		// Pane to select rock
    private RockPaperScissorsBoardPane paperField;		// Pane to select paper
    private RockPaperScissorsBoardPane scissorsField;	// Pane to select scissors
    
    /**
     * Constructor to build main GUI
     * @param clientGame The ClientGame object for this game
     */
    public RockPaperScissorsBoard(RockPaperScissorsClientGame clientGame)
    {
        this.clientGame = clientGame;
        
        opponentField = new RockPaperScissorsBoardPane();
        rockField = new RockPaperScissorsBoardPane();
        paperField = new RockPaperScissorsBoardPane();
        scissorsField = new RockPaperScissorsBoardPane();
        opponentLabel = new Label("Gegner: 0");
        myLabel = new Label("Ich: 0");
        separator = new Separator(Orientation.HORIZONTAL);
        fieldsPane = new GridPane();
        opponentPane = new GridPane();
        
        opponentField.setSymbol(RockPaperScissorsSymbol.None, GameState.None);
        rockField.setSymbol(RockPaperScissorsSymbol.Rock, GameState.None);
        paperField.setSymbol(RockPaperScissorsSymbol.Paper, GameState.None);
        scissorsField.setSymbol(RockPaperScissorsSymbol.Scissors, GameState.None);
        opponentLabel.getStyleClass().add("playerLabel");
        myLabel.getStyleClass().add("playerLabel");
        separator.getStyleClass().add("separator");
        
        opponentPane.add(opponentField, 0, 0);
        opponentPane.setAlignment(Pos.CENTER);        
        fieldsPane.add(rockField, 0, 0);
        fieldsPane.add(paperField, 1, 0);
        fieldsPane.add(scissorsField, 2, 0);
        fieldsPane.vgapProperty().set(10);
        fieldsPane.hgapProperty().set(10);
        fieldsPane.setPadding(new Insets(10,10,10,10));
         
        this.add(opponentLabel, 0, 0);
        this.add(opponentPane, 0, 1);
        this.add(separator, 0, 2);
        this.add(fieldsPane, 0, 3);
        this.add(myLabel, 0, 4);
        this.vgapProperty().set(20);
        this.setAlignment(Pos.CENTER);
                
        // Event handlers for the three selection fields
        
        rockField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setSelectedPane(RockPaperScissorsSymbol.Rock);
                RockPaperScissorsSelectionMessage msg = new RockPaperScissorsSelectionMessage(RockPaperScissorsSymbol.Rock);
                clientGame.sendMessageToServer(msg);
            }
        });                
        
        paperField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setSelectedPane(RockPaperScissorsSymbol.Paper);
                RockPaperScissorsSelectionMessage msg = new RockPaperScissorsSelectionMessage(RockPaperScissorsSymbol.Paper);
                clientGame.sendMessageToServer(msg);
            }
        });
        
        scissorsField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setSelectedPane(RockPaperScissorsSymbol.Scissors);
                RockPaperScissorsSelectionMessage msg = new RockPaperScissorsSelectionMessage(RockPaperScissorsSymbol.Scissors);
                clientGame.sendMessageToServer(msg);
            }
        });
  
    }
    
    /**
     * Reset the formatting for all selection fields, then mark the selected one.
     * If nothing is selected, then this just resets the styles; for example, at
     * the beginning of a new move.
     * This makes use of classes defined in the CSS
     * @param symbol Selected symbol
     */
    private void setSelectedPane(RockPaperScissorsSymbol symbol)
    {
        Platform.runLater(new Runnable() {
        @Override
        public void run() {
        	// remove any applied styles
            rockField.getStyleClass().remove("rockPaperScissorsField");
            rockField.getStyleClass().remove("rockPaperScissorsFieldSelected");
            paperField.getStyleClass().remove("rockPaperScissorsField");
            paperField.getStyleClass().remove("rockPaperScissorsFieldSelected");
            scissorsField.getStyleClass().remove("rockPaperScissorsField");
            scissorsField.getStyleClass().remove("rockPaperScissorsFieldSelected");
            
            // set style as required
            if (symbol == RockPaperScissorsSymbol.Paper) {
                paperField.getStyleClass().add("rockPaperScissorsFieldSelected");
            } else if (symbol == RockPaperScissorsSymbol.Rock) {
                rockField.getStyleClass().add("rockPaperScissorsFieldSelected");
            } else if (symbol == RockPaperScissorsSymbol.Scissors) {
                scissorsField.getStyleClass().add("rockPaperScissorsFieldSelected");
            }
        }});
    }
    
    /**
     * Update the GUI: symbols and descriptive texts
     * @param opponentSymbol Opponents symbol
     * @param myState Mein Status (won, lost, etc.)
     * @param mySymbol Our symbol
     * @param opponentName Opponent's name
     * @param opponentPoints Opponent's points
     * @param myPoints Our points
     */
    public void updateGUI(RockPaperScissorsSymbol opponentSymbol, GameState myState, RockPaperScissorsSymbol mySymbol, String opponentName, int opponentPoints, int myPoints){ 
        setSymbols(opponentSymbol, myState, mySymbol);
        setNamesAndPoints(opponentName, opponentPoints, myPoints);
    }
    
    /**
     * set the symbol images (ours and opponent's), including overlays to show results
     * @param opponentSymbol Opponent's symbol
     * @param myState Our status (won, lost, etc.)
     * @param mySymbol Our symbol
     */
    public void setSymbols(RockPaperScissorsSymbol opponentSymbol, GameState myState, RockPaperScissorsSymbol mySymbol) { 
        if (myState == GameState.Win) {
            opponentField.setSymbol(opponentSymbol, GameState.Lose);
            if (mySymbol == RockPaperScissorsSymbol.Rock) {
            	rockField.setSymbol(RockPaperScissorsSymbol.Rock, GameState.Win);
            } else if (mySymbol == RockPaperScissorsSymbol.Paper) {
            	paperField.setSymbol(RockPaperScissorsSymbol.Paper, GameState.Win);
            } else {
            	scissorsField.setSymbol(RockPaperScissorsSymbol.Scissors, GameState.Win);
        	}
        } else if (myState == GameState.Lose) {
            opponentField.setSymbol(opponentSymbol, GameState.Win);
            if (mySymbol == RockPaperScissorsSymbol.Rock) {
            	rockField.setSymbol(RockPaperScissorsSymbol.Rock, GameState.Lose);
            } else if (mySymbol == RockPaperScissorsSymbol.Paper) {
            	paperField.setSymbol(RockPaperScissorsSymbol.Paper, GameState.Lose);
            } else {
            	scissorsField.setSymbol(RockPaperScissorsSymbol.Scissors, GameState.Lose);
            }       
        } else { // The move is a draw, no one wins
            opponentField.setSymbol(opponentSymbol, GameState.Draw);
            rockField.setSymbol(RockPaperScissorsSymbol.Rock, GameState.Draw);
            paperField.setSymbol(RockPaperScissorsSymbol.Paper, GameState.Draw);
            scissorsField.setSymbol(RockPaperScissorsSymbol.Scissors, GameState.Draw);
        }    
    }
    
    /**
     * Set the labels containing names and points.
     * @param opponentName Opponent's name
     * @param opponentPoints Opponent's points
     * @param myPoints Our points
     */
    public void setNamesAndPoints(String opponentName, int opponentPoints, int myPoints) {
            Platform.runLater(new Runnable() {
            @Override
            public void run() {
                opponentLabel.setText(opponentName + ": " + Integer.toString(opponentPoints) + " Points");
                myLabel.setText("Ich: " + Integer.toString(myPoints) + " Points");
            }
        });
    }
    
    /**
     * Change text in the window header, to the right of the game name.
     * We access the main window via the ClientGame object
     * @param text Text to display
     */
    public void setHeader(String text) {
        clientGame.getGameWindow().getHeader().setStatusLabelText(text);
    }
    
    /**
     * Prepare the client for the next move
     * @param message Message to display, from previous move
     * @param lastTurn True if this was the last move of the game
     */
   public void nextTurn(String message, boolean lastTurn) {
       if (!lastTurn) {
    	   JOptionPane.showMessageDialog(null, message + "\nClick OK for the next move.", "Move finished", JOptionPane.INFORMATION_MESSAGE);
       } else { // last move
    	   JOptionPane.showMessageDialog(null, message + "\nClick OK to see the winner.", "Move finished", JOptionPane.INFORMATION_MESSAGE);
       }
       // Reset the displayed selections, to prepare for the next move
       setSymbols(RockPaperScissorsSymbol.None, GameState.None, RockPaperScissorsSymbol.None);
       setSelectedPane(RockPaperScissorsSymbol.None);
       setHeader("Waiting for opponent's move...");
   } 
}
