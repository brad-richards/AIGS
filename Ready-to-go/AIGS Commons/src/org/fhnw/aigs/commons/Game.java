package org.fhnw.aigs.commons;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.fhnw.aigs.commons.communication.FieldClickMessage;
import org.fhnw.aigs.commons.communication.GameEndsMessage;
import org.fhnw.aigs.commons.communication.GameStartMessage;
import org.fhnw.aigs.commons.communication.Message;
import org.fhnw.aigs.commons.communication.PlayerChangedMessage;

/**
 * This <b>abstract</b> is the base class of all games on the <b>server</b>
 * side.<br>
 * Do not use this class on the clients. Instead use <b>ClientGam</b>. This
 * class cannot be instantiated directly but must extend another class.<br>
 * The class supports the player with a lot of helper methods like
 * "pickRandomPlayer".<br>
 * If the predefined behaviour does not match your game, override the methods
 * involved. The method "hasEnoughParticipants" for example can differ among
 * games, even though in most cases the basic implementation given in this class
 * will suffice.<br>
 *
 * <strong>VERY Important: Only use an empty constructor!</strong>
 * The class will be automatically loaded. The empty constructor of the
 * inherited class must call Game's constructor. Example:<br>
 * <code>super("TicTacToe", 2, 2);</code>
 *
 * @author Matthias Stöckli
 */
public abstract class Game {

    //<editor-fold desc="Attributes area">
    /**
     * The currently highest game id. This variable is used to generate new game
     * IDs.
     */
    public static volatile long currentHighestId;
    /**
     * Minimum number of players.
     */
    protected final int minNumberOfPlayers;
    /**
     * Name of the game. The name <b>must</b> match the name of the
     * project/folder etc. The game name must not be changed!
     */
    protected final String gameName;
    /**
     * The game mode helps controlling the game flow. if the game mode
     * "SinglePlayer" is selected, it is not possible to use the method
     * "startGameWith" on the client side.
     */
    protected GameMode gameMode;
    /**
     * The game's unique id. It is based on "idCounter".
     */
    protected long id;
    /**
     * List of all players in the current game.
     */
    protected ArrayList<Player> players = new ArrayList<Player>();
    /**
     * The player whose turn it is, i.e. the active player who is allowed to
     * move his or her token etc.
     */
    private Player currentPlayer;
    /**
     * The AI is represented as an ordinary player.
     */
    protected Player aiPlayer;
    /**
     * Name of the party, e.g. "pascal04". This can be used to make sure that
     * one can join a game with a specific group of people.
     */
    protected String partyName;
    /**
     * Indicates whether the game is still running or not
     */
    protected boolean gameEnded;

    /**
     * Game is <b>abstract</b>, therefore it is not possible to create instances
     * without inheriting from the class. Use this and only this constructor
     * when defining a new class inheriting from Game.<br>
     *
     * @param gameName The name of the game.
     * @param minNumberOfPlayers The minimum required number of players.
     */
    public Game(String gameName, int minNumberOfPlayers) {
        this.minNumberOfPlayers = minNumberOfPlayers;
        this.gameName = gameName;
        this.id = currentHighestId;
        currentHighestId++;
    }

    /**
     * This is the first method that will be called after the game has been set
     * up. It can serve as some sort of substitute for a constructor. Here all
     * preparations can be made, e.g. setting up the board, distributing tokens
     * etc.
     */
    public abstract void initialize();

    /**
     * This method starts the game. It generates GameStartMessage based on the
     * current game. This message will be sent to all players. They will be
     * informed about the game's start and the starting player. Usually this
     * method is called at the end of the <b>initialize()</b> method after
     * everything has been set up.<br>
     * Please note: If your minNumberOfPlayers is 1 or 0 (which is actually
     * impossible) then the currentPlayer will automatically be set to the only
     * player in game.
     */
    public void startGame() {
        if (minNumberOfPlayers == 0 || minNumberOfPlayers == 1) {
            currentPlayer = players.get(0);
        }
        GameStartMessage gameStartMessage = new GameStartMessage(currentPlayer);
        sendMessageToAllPlayers(gameStartMessage);
        Logger.getLogger("Start the game with current player set to " + currentPlayer.getName());
    }

    /**
     * This method is the core of every game on the server side. Due to the
     * abstract nature, it must be implemented by every Game. The method is
     * responsible for the the processing of incoming messages. It is intended
     * to work in a very similar way to the <b>processGameLogic</b>
     * method of the client side.<br>
     * ProcessGameLogic() will be called as soon as a non system message (e.g.
     * JoinMessage or similar) arrives. The <b>ServerMessageBroker</b> on the server side will
     * forward the class to this method. In this method it will then be
     * interpreted and the results will change the game accordingly, e.g. move
     * player's tokens etc. The message must first be checked for the type. This
     * can be done in the following way:<br>
     * <code>if(message instanceof [[YourMessageClass]])</code><br>
     * Where [[YourMessageClass]] is the name of the message class, e.g.
     * {@link FieldClickMessage}. Then the message can be explicitly cast to the
     * desired type:<br>
     * <code>FieldClickMessage fieldClickMessage = (FieldClickMessage)message;</code><br>
     * In the end of every processGameLogic method a call to
     * {@link Game#passTurnToNextPlayer()} or similar should be made in order to
     * pass the turn to the next player.
     *
     * @param message The message passed from ServerMessageBroker.
     * @param sendingPlayer The player who sent the message.
     */
    public abstract void processGameLogic(Message message, Player sendingPlayer);

    /**
     * This method will be called automatically after the
     * {@link Game#processGameLogic} method finished. It is used to determine
     * whether a game ends or not. It is up to the user to define what will
     * happen. In most cases a {@link GameEndsMessage} should be sent to all
     * clients in order to inform them that the game is over. The clients'
     * reactions should reflect this fact and they should shut down.
     */
    public abstract void checkForWinningCondition();

    /**
     * Checks whether the game already has enough participants to start the
     * game. It will start as soon as the minimum amount of players is reached.
     * In some cases, this method will not reflect the needs of the players,
     * e.g. when there are certain constraints. In this case, it is best to
     * override the method. It will be automatically called after a new player
     * joins the game in the <b>GameManager.joinGame}</b> on the server side.
     *
     * @return True if the game has enough participants, false if not.
     */
    public boolean hasEnoughParticipants() {
        if (this.gameMode == GameMode.SinglePlayer) {
            return true;
        } else {
            return this.gameMode != GameMode.SinglePlayer && this.players.size() == minNumberOfPlayers;
        }
    }

    /**
     * Passes the turn to the next player in the queue based on the id. The id
     * is based on the index of the player inside the {@link Game#players}
     * collection. If it is player 1's turn, the player 2 will be next, if the
     * player 2 is the last player, the next player will be player 1 etc. After
     * the current player was set, a "PlayerChangedMessage" is sent to all
     * clients. It is up to them to react accordingly.
     */
    public void passTurnToNextPlayer() {
        Player oldPlayer = currentPlayer;
        Player newPlayer = null;

        int currentPlayerIndex = players.indexOf(currentPlayer);
        if (currentPlayerIndex < players.size() - 1) {
            currentPlayer = players.get(currentPlayerIndex + 1);
        } else {
            currentPlayer = players.get(0);
        }

        newPlayer = currentPlayer;
        PlayerChangedMessage endTurnMessage = new PlayerChangedMessage(oldPlayer, newPlayer);
        sendMessageToAllPlayers(endTurnMessage);
    }

    /**
     * Sends a message to all players in the current game.<br>
     * The message will not be sent to players with the {@link Player#isAi}
     * flag.
     *
     * @param message The message to be sent.
     */
    public void sendMessageToAllPlayers(Message message) {
        for (Player p : players) {
            if (p.isAi() == false || p.getSocket() != null) {
                message.send(p.getSocket(), p);
            }
        }
    }

    /**
     * Sends a message to a single player. The message will not be sent to
     * players with the {@link Player#isAi} flag.
     *
     * @param message The message to be sent.
     * @param player The player who will receive the message.
     */
    public void sendMessageToPlayer(Message message, Player player) {
        if (player.isAi() == false) {
            message.send(player.getSocket(), player);
        }
    }

    /**
     * Sends a message to the current player. The message will not be sent to
     * players with the {@link Player#isAi} flag.
     *
     * @param message The message to be sent.
     */
    public void sendMessageToCurrentPlayer(Message message) {
        if (currentPlayer.isAi() == false) {
            message.send(currentPlayer.getSocket(), currentPlayer);
        }
    }

    /**
     * See {@link Game#currentPlayer}.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * See {@link Game#gameMode}.
     */
    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * See {@link Game#gameName}.
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * See {@link Game#id}.
     */
    public long getId() {
        return id;
    }

    /**
     * See {@link Game#players}.
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * See {@link Game#partyName}.
     */
    public String getPartyName() {
        return this.partyName;
    }

    /**
     * See {@link Game#gameEnded}.
     */
    public boolean isGameEnded() {
        return gameEnded;
    }

    /**
     * This method sets the current player manually. Additionally a
     * {@link PlayerChangedMessage} will be sent.
     *
     * @param newPlayer The (new) current player
     */
    public void setCurrentPlayer(Player newPlayer) {
        Player oldPlayer = currentPlayer;
        this.currentPlayer = newPlayer;
        PlayerChangedMessage playerChangedMessage = new PlayerChangedMessage(oldPlayer, newPlayer);
        sendMessageToAllPlayers(playerChangedMessage);
    }

    /**
     * See {@link Game#gameMode}.
     */
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * See {@link Game#partyName}.
     */
    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    /**
     * See {@link Game#gameEnded}.
     */
    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    /**
     * Gets a player by his or her ID.
     *
     * @param id The player's ID.
     * @return The player with the specified id.
     */
    public Player getPlayerById(int id) {
        for (Player player : players) {
            if (player.getId() == id) {
                return player;
            }
        }
        return null;
    }

    /**
     * Adds a new player to the list of players.
     *
     * @param player The player to be added.
     */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * Removes a player from the game.
     *
     * @param player The player to be removed.
     */
    public void removePlayer(Player player) {
        players.remove(player);
    }

    /**
     * A convenient way to remove a player by his or her user name.
     *
     * @param name The player's name.
     */
    public void removePlayerByName(String name) {
        Player player;
        for (int i = 0; i < players.size(); i++) {
            player = players.get(i);
            if (player.getName().equals(name)) {
                players.remove(player);
            }
        }
    }

    /**
     * Gets a random player.
     *
     * @return Random player.
     */
    public Player getRandomPlayer() {
        int randomPlayer = (int) Math.floor(Math.random() * minNumberOfPlayers);      // <-- nicht gut...
        return players.get(randomPlayer);
    }

    /**
     * This method tests whether a list of players are playing in this game.
     *
     * @param playerNames The names of the players.
     * @return Returns whether the specified players take part in this game.
     */
    public boolean containsPlayers(String[] playerNames) {
        boolean[] matches = new boolean[playerNames.length];

        for (int i = 0; i < playerNames.length; i++) {
            for (int j = 0; j < players.size(); j++) {
                if (playerNames[i].equals(players.get(j).getName())) {
                    matches[i] = true;
                }
            }
        }
        boolean allMatch = true;
        for (boolean b : matches) {
            allMatch &= b;
        }
        return allMatch;
    }

    /**
     * Shows the game's name and the ID.
     *
     * @return The game's name and it's ID.
     */
    @Override
    public String toString() {
        return gameName + ", ID = " + id;
    }
}
