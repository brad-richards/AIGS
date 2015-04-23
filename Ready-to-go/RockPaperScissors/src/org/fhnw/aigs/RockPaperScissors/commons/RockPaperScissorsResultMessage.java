package org.fhnw.aigs.RockPaperScissors.commons;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.communication.Message;

/**
 * Class used by the server to inform the client of the result of a move.
 * The result is from the point-of-view of the specific client; each client receives a different message.
 */
@XmlRootElement(name = "RockPaperScissorsResultMessage")
public class RockPaperScissorsResultMessage extends Message {
    private RockPaperScissorsSymbol mySymbol;		// Symbol for our moves
    private RockPaperScissorsSymbol opponentSymbol;	// Opponent's symbol
    private int turn;								// Turn number (1, 2, ...)
    private int myPoints;							// Our points
    private int opponentPoints;						// Opponent's points
    private String opponentName;					// Opponent's name
    private boolean isLastTurn;						// Last turn = game over?
    private String turnMessage;						// Result message
    private GameState myState;						// Our status
    
    /**
     * Returns our symbol
     * @return Symbol-Object
     */
    @XmlElement(name = "MySymbol")		// Parameter name for XML must be specified for the getter
    public RockPaperScissorsSymbol getMySymbol() {
        return mySymbol;
    }

    /**
     * Set our symbol
     * @param mySymbol Symbol-Object
     */
    public void setMySymbol(RockPaperScissorsSymbol mySymbol) {
        this.mySymbol = mySymbol;
    }

    /**
     * Get opponent's symbol
     * @return Symbol-Object
     */
    @XmlElement(name = "OpponentSymbol")		// Parameter name for XML must be specified for the getter    
    public RockPaperScissorsSymbol getOpponentSymbol() {
        return opponentSymbol;
    }

    /**
     * Set opponent's symbol
     * @param opponentSymbol Symbol-Object
     */
    public void setOpponentSymbol(RockPaperScissorsSymbol opponentSymbol) {
        this.opponentSymbol = opponentSymbol;
    }

    /**
     * Get our points
     * @return Punktzahl
     */
    @XmlElement(name = "MyPoints")		// Parameter name for XML must be specified for the getter
    public int getMyPoints() {
        return myPoints;
    }

    /**
     * Set our points
     * @param myPoints Punktzahl
     */
    public void setMyPoints(int myPoints) {
        this.myPoints = myPoints;
    }

    /**
     * Get opponent's points
     * @return Punktzahl
     */
    @XmlElement(name = "OpponentPoints")		// Parameter name for XML must be specified for the getter
    public int getOpponentPoints() {
        return opponentPoints;
    }

    /**
     * Set opponent's points
     * @param opponentPoints Punktzahl
     */
    public void setOpponentPoints(int opponentPoints) {
        this.opponentPoints = opponentPoints;
    }

    /**
     * Get opponent's name
     * @return Name of opponent
     */
    @XmlElement(name = "OpponentName")		// Parameter name for XML must be specified for the getter
    public String getOpponentName() {
        return opponentName;
    }

    /**
     * Set opponent's name
     * @param opponentName Name of opponent
     */
    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    /**
     * Get our status (won, lost, etc.)
     * @return GameState-Object
     */
    @XmlElement(name = "MyState")		// Parameter name for XML must be specified for the getter
    public GameState getMyState() {
        return myState;
    }

    /**
     * Set out status (won, lost, etc.)
     * @param state GameState-Object
     */
    public void setMyState (GameState state) {
        this.myState = state;
    }

    /**
     * Get result message
     * @return Message
     */
    @XmlElement(name = "TurnMessage")		// Parameter name for XML must be specified for the getter
    public String getTurnMessage() {
        return turnMessage;
    }

    /**
     * Set result message
     * @param turnMessage Message
     */
    public void setTurnMessage(String turnMessage) {
        this.turnMessage = turnMessage;
    }
    
    /**
     * Get boolean: was this the last move?
     * @return true, if this was the last move
     */
    @XmlElement(name = "IsLastTurn")		// Parameter name for XML must be specified for the getter
    public boolean isIsLastTurn() {
        return isLastTurn;
    }

    /**
     * Set boolean: was this the last move?
     * @param isLastTurn true, if this was the last move
     */
    public void setIsLastTurn(boolean isLastTurn) {
        this.isLastTurn = isLastTurn;
    }

    /**
     * Get turn number
     * @return Number
     */
    @XmlElement(name = "Turn")		// Parameter name for XML must be specified for the getter
    public int getTurn() {
        return turn;
    }

    /**
     * Set turn number
     * @param turn Number
     */
    public void setTurn(int turn) {
        this.turn = turn;
    }
    
    /**
     * Empty constructor (required)
     */
    RockPaperScissorsResultMessage() { }

    /**
     * Constructor with all parameters
     * @param myState  Our own status
     * @param lastTurn true, if last turn 
     * @param mySymbol Our symbol
     * @param opponentSymbol Opponent's symbol
     * @param opponentName Opponent's name
     * @param myPoints Our points
     * @param opponentPoints Opponent's points
     * @param turn Turn number
     * @param turnMessage Result message
     */
    public RockPaperScissorsResultMessage(GameState myState, boolean lastTurn, RockPaperScissorsSymbol mySymbol, RockPaperScissorsSymbol opponentSymbol, String opponentName, int myPoints, int opponentPoints, int turn, String turnMessage) {
      super();
      this.setMyState(myState);
      this.setIsLastTurn(lastTurn);
      this.setMyPoints(myPoints);
      this.setMySymbol(mySymbol);
      this.setOpponentName(opponentName);
      this.setOpponentSymbol(opponentSymbol);
      this.setOpponentPoints(opponentPoints);
      this.setTurn(turn);
      this.setTurnMessage(turnMessage);
    }
}
