package org.fhnw.aigs.RockPaperScissors.server;

import org.fhnw.aigs.RockPaperScissors.commons.GameState;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsSymbol;
import org.fhnw.aigs.commons.Player;

/**
 * Represents a move (turn) by a player. Objects of this class are managed by the server and used to determine who wins each round.
 */
public class RockPaperScissorsTurn {
    private Player player;                       // Player for this turn 
    private boolean hasTurnFinished;             // Whether the turn is finished
    private RockPaperScissorsSymbol turnSymbol;  // Symbol played (rock, paper, scissors)
    private int points;                          // Points earned
    private GameState turnState;                 // Status for this round (won, lost, etc.)
    private int opponentIndex;                   // Index of the opponent's turn object

    /**
     * Getter for the opponent's turn index
     * @return Index of opponent's turn
     */
    public int getOpponentIndex() {
        return opponentIndex;
    }

    /**
     * Setter for the opponent's turn index
     * @param opponentIndex Index of opponent's turn
     */
    public void setOpponentIndex(int opponentIndex) {
        this.opponentIndex = opponentIndex;
    }

    /**
     * Getter for the status
     * @return Status of the move
     */
    public GameState getTurnState() {
        return turnState;
    }

    /**
     * Setter for the status
     * @param turnState Status of the move
     */
    public void setTurnState(GameState turnState) {
        this.turnState = turnState;
    }
    
    /**
     * Setter for the status and adds points to current point total
     * @param turnState Status of the move
     * @param points Number of points to add to the total (usually 0 or 1)
     */
    public void setTurnState(GameState turnState, int points) {
        this.turnState = turnState;
        this.points += points;
    }

    /**
     * Getter for player
     * @return Player-Object
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Getter for turn-finished
     * @return true if the turn is finished
     */
    public boolean hasTurnFinished() {
        return hasTurnFinished;
    }

    /**
     * Setter for turn-finished
     * @param hasTurnFinished true if the turn is finished
     */
    public void setTurnFinished(boolean hasTurnFinished) {
        this.hasTurnFinished = hasTurnFinished;
    }

    /** Getter for selected symbol
     * @return Selected symbol
     */
    public RockPaperScissorsSymbol getTurnSymbol() {
        return turnSymbol;
    }

    /**
     * Setter for selected symbol
     * @param turnSymbol Selected symbol
     */
    public void setTurnSymbol(RockPaperScissorsSymbol turnSymbol) {
        this.turnSymbol = turnSymbol;
    }

    /**
     * Getter for points
     * @return Points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Setter for points
     * @param points Points
     */
    public void setPoints(int points) {
        this.points = points;
    }
    
    /**
     * Constructor, including player info
     * @param player Player for this turn-object 
     */
    public RockPaperScissorsTurn(Player player) {
        this.player = player;
        this.hasTurnFinished = false;
        this.points = 0;
        this.turnSymbol = RockPaperScissorsSymbol.None;
        this.turnState = GameState.None;
    }
    
    /**
     * Prepare for the next turn
     */
    public void nextTurn() {
        this.hasTurnFinished = false;
        this.turnSymbol = RockPaperScissorsSymbol.None;
        this.turnState = GameState.None;
    }
    
    /**
     * Getter for player name (shortcut)
     * @return Player name from player-object
     */
    public String getPlayerName() {
        return this.player.getName();
    }
    
    /**
     * Getter for player ID (shortcut)
     * @return ID from player-object
     */
    public int getPlayerID() {
        return this.player.getId();
    }
}
