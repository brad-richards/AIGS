package org.fhnw.aigs.RockPaperScissors.server;

import java.util.ArrayList;
import org.fhnw.aigs.RockPaperScissors.commons.GameState;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsSelectionMessage;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsParticipantsMessage;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsResultMessage;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsSymbol;
import org.fhnw.aigs.commons.Game;
import org.fhnw.aigs.commons.Player;
import org.fhnw.aigs.commons.communication.GameEndsMessage;
import org.fhnw.aigs.commons.communication.Message;

/**
 * Server-side game logic: turn results are computed and sent to the clients
 */
public class GameLogic extends Game {
    public static final String GAMENAME = "RockPaperScissors"; // Must match the value in client.Main
    public static final String VERSION = "v1.1"; // Server-logic version
    public static final int MINNUMBEROFPLAYERS = 2; // Number of players required for a game
    public static final int NUMBEROFTURNS = 3; // Game is over after this many turns

    private ArrayList<RockPaperScissorsTurn> turnPlayers; // List of all player-turns
    private int turnNumber; // Current turn number
    private boolean lastTurn; // Whether this is the last turn

    /**
     * Empty constructor (required); work handled by the superclass
     */
    public GameLogic() {
        super(GameLogic.GAMENAME, GameLogic.VERSION, GameLogic.MINNUMBEROFPLAYERS);
    }

    /**
     * Initialize the game
     */
    @Override
    public void initialize() {
        turnPlayers = new ArrayList<>();
        RockPaperScissorsTurn turn = null;
        // Create turn-object for each player
        for (int i = 0; i < players.size(); i++) {
            turn = new RockPaperScissorsTurn(players.get(i));
            turnPlayers.add(turn);
        }
        turnNumber = 1;
        lastTurn = false;

        // For this particular game, the order of players is irrelevant; all players must play before the results are reveals.
        // However, the AIGS-Framework requires that we specify a player to start.
        setCurrentPlayer(getRandomPlayer());

        // Start the game
        startGame();

        // Inform all players that the game has started
        RockPaperScissorsParticipantsMessage identification = new RockPaperScissorsParticipantsMessage(turnPlayers.get(0).getPlayer(), turnPlayers.get(1).getPlayer());
        sendMessageToAllPlayers(identification);
    }

    /**
     * Process messages from the clients.
     * @param msg Message from client; messages types are defined in the commons package
     * @param player Player who sent the message
     */
    @Override
    public void processGameLogic(Message msg, Player player) {
        if (msg instanceof RockPaperScissorsSelectionMessage) {
            if (turnNumber == NUMBEROFTURNS)
                lastTurn = true;

            RockPaperScissorsSelectionMessage castMsg = (RockPaperScissorsSelectionMessage) msg;
            // Locate the turn-object for this player, and set the symbol from the message
            for (int i = 0; i < turnPlayers.size(); i++) {
                if (turnPlayers.get(i).getPlayerID() == player.getId()) {
                    turnPlayers.get(i).setTurnSymbol(castMsg.getSymbol());
                    turnPlayers.get(i).setTurnFinished(true);
                    break;
                }
            }
            // Checking for a winner is handled in checkForWinningCondition
        }
    }

    /**
     * This method is called after every player move, to see if the round is over
     */
    @Override
    public void checkForWinningCondition() {
        if (allPlayersFinished() == true) {
            // If all players have made their moves, determine the round winner (-1 = draw)
            int winnerIndex = calculateTurnWinner();

            // Send player-specific message to all players
            RockPaperScissorsResultMessage result = null;
            String messageText = "";
            RockPaperScissorsTurn me = null;
            RockPaperScissorsTurn opponent = null;
            for (int i = 0; i < turnPlayers.size(); i++) {
                me = turnPlayers.get(i);
                opponent = turnPlayers.get(me.getOpponentIndex());
                if (winnerIndex == -1) { // if a draw
                    messageText = "Two times " + GameLogic.printSymbol(me.getTurnSymbol()) + ". Draw!";
                } else if (i == winnerIndex) { // this player won
                    messageText = GameLogic.printSymbol(me.getTurnSymbol()) + " beats " + GameLogic.printSymbol(opponent.getTurnSymbol()) + ". " + me.getPlayerName() + " wins!";
                } else { // opponent won
                    messageText = GameLogic.printSymbol(opponent.getTurnSymbol()) + " beats " + GameLogic.printSymbol(me.getTurnSymbol()) + ". " + opponent.getPlayerName() + " wins!";
                }
                result = new RockPaperScissorsResultMessage(me.getTurnState(), lastTurn, me.getTurnSymbol(), opponent.getTurnSymbol(), opponent.getPlayerName(), me.getPoints(), opponent.getPoints(),
                        turnNumber, messageText);
                sendMessageToPlayer(result, me.getPlayer()); // Send message to player
            }

            // Reset the turn objects for the next round
            for (int i = 0; i < turnPlayers.size(); i++) {
                turnPlayers.get(i).nextTurn();
            }
            turnNumber++;

            if (lastTurn) {
                RockPaperScissorsTurn winner = calculateGameWinner();
                GameEndsMessage endMessage = null;
                if (winner == null) { // If a draw over all rounds
                    endMessage = new GameEndsMessage("Draw!");
                } else {
                    endMessage = new GameEndsMessage(winner.getPlayerName() + " won with " + Integer.toString(winner.getPoints()) + " rounds.");
                }
                sendMessageToAllPlayers(endMessage);
            }
        }
    }

    /**
     * Have all players sent their moves?
     * @return true if all players have submitted their moves, else false
     */
    private boolean allPlayersFinished() {
        boolean finished = true;
        for (int i = 0; i < turnPlayers.size(); i++) {
            if (!turnPlayers.get(i).hasTurnFinished()) {
                finished = false; // Player found, who has not moved
                break;
            }
        }
        return finished;
    }

    /**
     * Determine who has won the round, and return the corresponding index from the ArrayList. Currently supports only two players
     * @return Index of the player who has won
     */
    private int calculateTurnWinner() {
        int winnerIndex = -1; // -1 means a drawn game
        for (int i = 0; i < turnPlayers.size(); i++) {
            for (int j = i + 1; j < turnPlayers.size(); j++) {
                if (turnPlayers.get(i).getTurnSymbol() == turnPlayers.get(j).getTurnSymbol()) { // drawn round
                    turnPlayers.get(i).setTurnState(GameState.Draw);
                    turnPlayers.get(j).setTurnState(GameState.Draw);
                } else if (turnPlayers.get(i).getTurnSymbol() == RockPaperScissorsSymbol.Paper && turnPlayers.get(j).getTurnSymbol() == RockPaperScissorsSymbol.Rock) {
                    turnPlayers.get(i).setTurnState(GameState.Win, 1);
                    turnPlayers.get(j).setTurnState(GameState.Lose);
                } else if (turnPlayers.get(i).getTurnSymbol() == RockPaperScissorsSymbol.Paper && turnPlayers.get(j).getTurnSymbol() == RockPaperScissorsSymbol.Scissors) {
                    turnPlayers.get(i).setTurnState(GameState.Lose);
                    turnPlayers.get(j).setTurnState(GameState.Win, 1);
                } else if (turnPlayers.get(i).getTurnSymbol() == RockPaperScissorsSymbol.Rock && turnPlayers.get(j).getTurnSymbol() == RockPaperScissorsSymbol.Paper) {
                    turnPlayers.get(i).setTurnState(GameState.Lose);
                    turnPlayers.get(j).setTurnState(GameState.Win, 1);
                } else if (turnPlayers.get(i).getTurnSymbol() == RockPaperScissorsSymbol.Rock && turnPlayers.get(j).getTurnSymbol() == RockPaperScissorsSymbol.Scissors) {
                    turnPlayers.get(i).setTurnState(GameState.Win, 1);
                    turnPlayers.get(j).setTurnState(GameState.Lose);
                } else if (turnPlayers.get(i).getTurnSymbol() == RockPaperScissorsSymbol.Scissors && turnPlayers.get(j).getTurnSymbol() == RockPaperScissorsSymbol.Rock) {
                    turnPlayers.get(i).setTurnState(GameState.Lose);
                    turnPlayers.get(j).setTurnState(GameState.Win, 1);
                } else if (turnPlayers.get(i).getTurnSymbol() == RockPaperScissorsSymbol.Scissors && turnPlayers.get(j).getTurnSymbol() == RockPaperScissorsSymbol.Paper) {
                    turnPlayers.get(i).setTurnState(GameState.Win, 1);
                    turnPlayers.get(j).setTurnState(GameState.Lose);
                }
                turnPlayers.get(i).setOpponentIndex(j);
                turnPlayers.get(j).setOpponentIndex(i);
            }
        }
        // Look for winner. If there is no winner, then the index will remain -1
        for (int i = 0; i < turnPlayers.size(); i++) {
            if (turnPlayers.get(i).getTurnState() == GameState.Win) {
                winnerIndex = i;
                break;
            }
        }
        return winnerIndex;
    }

    /**
     * Return the turn-object of the overall game winner, or null if the game is drawn.
     * @return RockPaperScissorsTurn-Objekt of the winner, or null if game is drawn
     */
    private RockPaperScissorsTurn calculateGameWinner() {
        int winnerIndex = 0; // Assume first player has won
        int maxPoints = turnPlayers.get(0).getPoints(); // points of the first player
        boolean draw = true; // Assume drawn will be true
        // Search through players, beginning with second player, looking for higher points
        for (int i = 1; i < turnPlayers.size(); i++) {
            if (turnPlayers.get(i).getPoints() > maxPoints) {
                // found higher point total: new winner
                winnerIndex = i;
                maxPoints = turnPlayers.get(i).getPoints();
                draw = false;
            } else if (turnPlayers.get(i).getPoints() < maxPoints) {
                // lower points, so game is not drawn
                draw = false;
            }
        }
        if (draw) {
            return null;
        } else {
            return turnPlayers.get(winnerIndex);
        }
    }

    /**
     * Return a string for the given symbol
     * @param symbol Symbol (rock, paper, scissors)
     * @return String value of the enumerated value
     */
    public static String printSymbol(RockPaperScissorsSymbol symbol) {
        if (symbol == RockPaperScissorsSymbol.Paper)
            return "Paper";
        else if (symbol == RockPaperScissorsSymbol.Rock)
            return "Rock";
        else if (symbol == RockPaperScissorsSymbol.Scissors)
            return "Scissors";
        else
            return "None";
    }
}
