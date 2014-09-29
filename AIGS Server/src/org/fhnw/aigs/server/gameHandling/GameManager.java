package org.fhnw.aigs.server.gameHandling;

import org.fhnw.aigs.commons.GameMode;
import java.lang.reflect.*;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fhnw.aigs.commons.*;
import org.fhnw.aigs.commons.communication.*;
import org.fhnw.aigs.server.communication.ServerMessageBroker;
import org.fhnw.aigs.server.gui.ServerGUI;

/**
 * This class manages all games. It creastes new games, lets players join games,
 * loads Game classes and terminates games. Once a game has been initialized,
 * the GameManager does not do much for the game, expect terminating it.
 *
 * @author Matthias Stöckli
 */
public class GameManager {

    /**
     * This list contains all running games.
     */
    public static volatile ArrayList<Game> runningGames = new ArrayList<>();
    /**
     * This list contains all games in which a player still waits for other
     * players.
     */
    public static volatile ArrayList<Game> waitingGames = new ArrayList<>();
    /**
     * This list contains a reference to all players on the server.
     */
    public static volatile ArrayList<Player> allPlayers = new ArrayList<>();

    /**
     * This method initializes a game. As soon as a client sends a JoinMessage,
     * it is passed to the game manager. The manager will check whether there
     * are already games of the same type which could be joined. If that is not
     * the case, it starts a new party. If the game is a single player game, the
     * game will also be started immediately.
     *
     * @param joinMessage The JoinMessage sent by the player.
     * @param player A reference to the player who sent the JoinMessage.
     * @param partyName If desired, the player can join a named party.
     * @return The newly created game.
     */
    public static synchronized Game joinGame(JoinMessage joinMessage, Player player, String partyName) {
        Game joinedGame;
        String gameName = joinMessage.getGameName();
        GameMode gameMode = joinMessage.getGameMode();

        // If the player wants to play a single player party or if there are no
        // games available, create a new game/party.
        if (gameMode == GameMode.SinglePlayer || waitingGames.isEmpty()) {
            joinedGame = createNewGame(gameName, gameMode, partyName, player);
            if (joinedGame == null) {
                return null;
            }
            joinedGame.addPlayer(player);
            Logger.getLogger(GameManager.class.getName()).log(Level.INFO, "{0} created and joined a new {1} party!", new Object[]{player.getName(), gameName});
        } // If the player specified a party's name, join a party with that name.
        else if (joinMessage.getPartyName() != null) {
            joinedGame = joinParty(gameName, gameMode, player, partyName);
            if (joinedGame == null) {
                return null;     // <-- For exception handling purposes.
            }
            Logger.getLogger(GameManager.class.getName()).log(Level.INFO, "{0} joined the party {1} in multiplayer {2}", new Object[]{player.getName(), partyName, gameName});
        } else {
            // This is the most common use case - if the player did not specify
            // a party name, join a random game of the same type.            
            joinedGame = joinRandomGame(gameName, gameMode, player);
            if (joinedGame == null) {
                return null;     // <-- For exception handling purposes.
            }
            Logger.getLogger(GameManager.class.getName()).log(Level.INFO, "{0} joined a random party in multiplayer {1}", new Object[]{player.getName(), gameName});
        }

        // Check if the game has enough participants, if so, remove it from the
        // waiting games list and add it to the running games list.
        if (joinedGame.hasEnoughParticipants()) {
            waitingGames.remove(joinedGame);
            runningGames.add(joinedGame);

            if(ServerConfiguration.getInstance().getIsConsoleMode() == false){
                // Add the game to the GUI
                ServerGUI.addGameToList(joinedGame,false);
                ServerGUI.removeGameFromList(joinedGame, true);
            }

            // If there is an exception in the initialization process, report it
            try {
                joinedGame.initialize();
            } catch (Exception ex) {
                Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, "Could not initialize game.", ex);
                ExceptionMessage exceptionMessage = new ExceptionMessage(ex);
                exceptionMessage.send(player.getSocket(), player);
            }
            Logger.getLogger(GameManager.class.getName()).log(Level.INFO, "Initialized {0} (ID {1})", new Object[]{gameName, joinedGame.getId()});
        }

        return joinedGame;
    }

    /**
     * Joins the first game in the list of waiting games with the same gameName.
     *
     * @param gameName The game's name.
     * @param gameMode The game mode.
     * @param player The player who wants to join the game.
     * @return The game to be joined.
     */
    private static Game joinRandomGame(String gameName, GameMode gameMode, Player player) {
        for (int i = 0; i < waitingGames.size(); i++) {
            Game waitingGame = waitingGames.get(i);

            // Check for running games of specified type and join it, if it matches
            if (waitingGame.getGameName().equals(gameName)) {
                waitingGame.addPlayer(player);
                return waitingGame;
            }
        }
        return null;                    // This should never happen.
    }

    /**
     * Join a named game, called "party". It is possible to add a party name to
     * a {@link JoinMessage}. The name will then be used to join other players
     * who chose the same party name. The purpose of this method is to allow
     * players to play the game with an exactly defined set of people. This
     * could easily be used to build a lobby-like environment.
     *
     * @param gameName The game's name.
     * @param gameMode The game mode.
     * @param player The player who wants to join the party/game.
     * @param partyName The name of the party.
     * @return The game to be joined.
     */
    private static Game joinParty(String gameName, GameMode gameMode, Player player, String partyName) {
        for (int i = 0; i < waitingGames.size(); i++) {
            Game waitingGame = waitingGames.get(i);

            // Check for running games of specified type and party name and
            // join it, if it matches.
            if (waitingGame.getGameName().equals(gameName)
                    && waitingGame.getPartyName().equals(partyName)) {
                waitingGame.addPlayer(player);
                return waitingGame;
            }
        }
        return createNewGame(gameName, gameMode, partyName, player);
    }

    /**
     * Creates a new game based on the game's name, game mode and a party name
     * and adds it to the waiting games list.
     *
     * @param gameName The game's name.
     * @param gameMode The game mode.
     * @param partyname The name of a party (can be null).
     * @param player The player who creates the game.
     * @return The newly creasted game.
     */
    private static Game createNewGame(String gameName, GameMode gameMode, String partyname, Player player) {
        Game newGame = loadGameFromJar(gameName, player);
        if (newGame == null) {
            return null;
        }          // <-- For exception handling purposes.
        newGame.setGameMode(gameMode);
        newGame.setPartyName(partyname);
        waitingGames.add(newGame);
        
        if(ServerConfiguration.getInstance().getIsConsoleMode() == false){
            // Add the game to the GUI
            ServerGUI.addGameToList(newGame,true);
        }        
        return newGame;
    }

    /**
     * Loads a game from a jar file. This method is being called if a new game
     * is created. The method will use an URLClassLoader to load the class.
     *
     * @param gameName The game's name.
     * @param player The player who creates the game.
     * @return The newly created game.
     */
    @SuppressWarnings("unchecked")
    private static Game loadGameFromJar(String gameName, Player player) {
        Game loadedGame = null;

        // Get the class loader of the game (if there is any) and load the class
        // "GameLogic". If there is no classloader yet, the GameLoader will create
        // a new one.
        try {
            // Load the GameLogic class.
            URLClassLoader loader = GameLoader.getClassLoaderByName(gameName);
            Class<Game> gameClazz;
            gameClazz = (Class<Game>) Class.forName("org.fhnw.aigs." + gameName + ".server.GameLogic", true, loader); // Changed package order v1.1

            // Create a new game instance using reflection. 
            // Use an empty, zero-argument constructor for that purpose.
            Constructor constructor = gameClazz.getConstructor(new Class[]{});
            loadedGame = (Game) constructor.newInstance(new Object[]{});
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, "There is no game class with this name.");
            ForceCloseMessage forceCloseMessage = new ForceCloseMessage("There is no GameLogic class for this game.");
            forceCloseMessage.send(player.getSocket(), player);
        } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException | InstantiationException | SecurityException | NoSuchMethodException ex) {
            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, "Game could not be instantiated. Check the constructor of your GameLogic class."
                    + "Most probably the constructor of the GameLogic class is not parameterless.", ex);
            ExceptionMessage exceptionMessage = new ExceptionMessage((Exception) ex.getCause());
            exceptionMessage.send(player.getSocket(), player);
        } catch (NullPointerException ex) {
            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, "There is no GameLogic class for this game:" + gameName);
            ForceCloseMessage forceCloseMessage = new ForceCloseMessage("There is no GameLogic class for this game: " + gameName);
            forceCloseMessage.send(player.getSocket(), player);
        }
        catch (Exception ex) // All other exceptions
        {
            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, "An unknown error occured: " + ex.getMessage());
            ForceCloseMessage forceCloseMessage = new ForceCloseMessage("An unknown error occured whle loading the game. Please look at the logs");
            forceCloseMessage.send(player.getSocket(), player);
        }

        return loadedGame;
    }

    /**
     * Terminates a game and removes the terminating player before a message is
     * sent to all players. This method is used when a single player can be
     * identified as source. This method sends a {@link ForceCloseMessage} to
     * all players.
     *
     * @param game The game to be terminated.
     * @param terminatingPlayer The player who closed the game or is otherwise
     * responsible for the end of a game.
     * @param reason The reason why the game has to be terminated.
     */
    public static void terminateGame(Game game, Player terminatingPlayer, String reason) {
        if (game != null) {
            if (runningGames.contains(game)) {
                runningGames.remove(game);

                // Log off all users.
                for (int i = 0; i < game.getPlayers().size(); i++) {
                    Player player = game.getPlayers().get(i);
                    User.logOffUserByName(player.getName());
                }

                if(ServerConfiguration.getInstance().getIsConsoleMode() == false){
                    // Refresh GUI
                    ServerGUI.removeGameFromList(game, false);
                    Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.INFO, "Removed the game {0} from the running games list.", game.toString());                    
                }

                // Remove the player who is the reason on why the game has to be
                // closed from the game. He or she will then not receive a
                // ForceCloseMessage. This done because the player could not receive
                // it anyway and this would just cause other exceptions.
                game.removePlayer(terminatingPlayer);
                ForceCloseMessage forceCloseMessage = new ForceCloseMessage(reason);
                game.sendMessageToAllPlayers(forceCloseMessage);
            } //Do the same steps if the game is in the list of the waiting games.
            else if (waitingGames.contains(game)) {
                waitingGames.remove(game);
                // Refresh GUI
                ServerGUI.removeGameFromList(game, true);
                Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.INFO, "Removed the game {0} from the waiting games list.", game.toString());
                game.removePlayer(terminatingPlayer);
                ForceCloseMessage forceCloseMessage = new ForceCloseMessage(reason);
                game.sendMessageToAllPlayers(forceCloseMessage);
            }
        }
    }

    /**
     * Terminates a game and removes the terminating player before a message is
     * sent to all players. This method sends a {@link ForceCloseMessage} to all
     * players.
     *
     * @param game The game to be terminated.
     * @param reason The reason why the game has to be terminated.
     */
    public static void terminateGame(Game game, String reason) {

        // If the game is null, don't terminate the game.
        if (game == null) {
            return;
        }

        // Create a ForceCloseMessage and send it to all players.
        ForceCloseMessage forceCloseMessage = new ForceCloseMessage(reason);
        game.sendMessageToAllPlayers(forceCloseMessage);

        // Log off all users.
        for (int i = 0; i < game.getPlayers().size(); i++) {
            Player player = game.getPlayers().get(i);
            User.logOffUserByName(player.getName());
        }

        // End game if it is a running game.
        if (runningGames.contains(game)) {
            runningGames.remove(game);
            if(ServerConfiguration.getInstance().getIsConsoleMode() == false){
                ServerGUI.removeGameFromList(game, false);
            }
            Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.INFO, "Removed the game {0} from the running games list.", game.toString());
        } // End game if it is a waiting game.
        else if (waitingGames.contains(game)) {
            waitingGames.remove(game);
            if(ServerConfiguration.getInstance().getIsConsoleMode() == false){
                ServerGUI.removeGameFromList(game, true);
            }
            Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.INFO, "Removed the game {0} from the waiting games list.", game.toString());
        }
    }

    /**
     * Get a running game by it's ID.
     *
     * @param id The game's ID.
     * @return The game with the specified ID.
     */
    public static Game getRunningGameById(long id) {
        for (Game game : runningGames) {
            if (game.getId() == id) {
                return game;
            }
        }
        return null;
    }

    /**
     * Checks whether a certain user name is already in use. This function is
     * currently not used.
     *
     * @param name The name to be checked.
     * @return True if the name is already in use, false if not.
     */
    public static boolean checkIfNameAlreadyExists(String name) {
        // Go through all the running games' players
        for (Game game : runningGames) {
            for (Player player : game.getPlayers()) {
                if (player.getName().equals(name)) {
                    return true;
                }
            }
        }

        // Go through all the waiting games
        for (Game game : waitingGames) {
            for (Player player : game.getPlayers()) {
                if (player.getName().equals(name)) {
                    return true;
                }
            }
        }

        // If there is no player with that name, return false
        return false;
    }

    /**
     * Sends a message to all players on the server. It is not adviced to use
     * this method.
     *
     * @param message The message to be sent.
     */
    public static void sendMessageToAllPlayersOnServer(Message message) {
        ArrayList<Game> allGames = new ArrayList<>();
        allGames.addAll(GameManager.runningGames);
        allGames.addAll(GameManager.waitingGames);

        for (Game game : allGames) {
            game.sendMessageToAllPlayers(message);
        }
    }
}
