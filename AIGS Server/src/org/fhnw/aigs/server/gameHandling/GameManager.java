package org.fhnw.aigs.server.gameHandling;

import org.fhnw.aigs.server.common.ServerConfiguration;
import org.fhnw.aigs.commons.GameMode;
import java.lang.reflect.*;
import java.net.URLClassLoader;
import java.util.ArrayList;
import org.fhnw.aigs.commons.*;
import org.fhnw.aigs.commons.communication.*;
import org.fhnw.aigs.server.common.LogRouter;
import org.fhnw.aigs.server.common.LoggingLevel;
import org.fhnw.aigs.server.gui.ServerGUI;

/**
 * This class manages all games. It creastes new games, lets players join games,
 * loads Game classes and terminates games. Once a game has been initialized,
 * the GameManager does not do much for the game, expect terminating it.<br>
 * v1.0 Initial release<br>
 * v1.1 Features added<br>
 * v1.2 Major changes in handlung and additional features<br>
 * v1.3 Changing of logging
 *
 * @author Matthias Stöckli
 * @version v1.3 (Raphael Stoeckli, 24.02.2015)
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
        JoinType joinType = joinMessage.getJoinType();
        String message = "";
        boolean gameCreated = false;
        
        if ((joinType == JoinType.CreateNewGame || joinType == JoinType.CreateNewPrivateGame) && gameMode != GameMode.SinglePlayer) // New (Multiplayer)
        {
            Game existing = checkIfPartyAlreadyExists(gameName, partyName);
            if (existing != null) // Game already exists
            {
                //LOG//Logger.getLogger(GameManager.class.getName()).log(Level.WARNING, "Can not create the party '{0}' of the type {1}, because this party already exists.", new Object[]{partyName, gameName});
                LogRouter.log(GameManager.class.getName(), LoggingLevel.waring, "Can not create the party '{0}' of the type {1}, because this party already exists.", new Object[]{partyName, gameName});
                JoinResponseMessage response = new JoinResponseMessage(joinType, gameMode, false, false, "The party '" + partyName + "' could not be created, because this party name already exists");
                response.send(player.getSocket(), player);    
               
                return null; // Send Message to user: Game with this name can not be creted because it already exists
            }
            else
            {
                    joinedGame = createNewGame(gameName, gameMode, partyName, player);
                    if (joinedGame != null)
                    {
                        if (joinType == JoinType.CreateNewPrivateGame)
                        {
                            joinedGame.setPrivateGame(true);
                        }
                        else
                        {
                            joinedGame.setPrivateGame(false);
                        }
                        joinedGame.addPlayer(player);
                        //LOG//Logger.getLogger(GameManager.class.getName()).log(Level.INFO, "{0} created and joined a new {1} party with the name '{2}'!", new Object[]{player.toString(), gameName, partyName});
                        LogRouter.log(GameManager.class.getName(), LoggingLevel.game, "{0} created and joined a new {1} party with the name '{2}'!", new Object[]{player.toString(), gameName, partyName});
                        gameCreated = true;
                    }
            }
        }
        else if (joinType == JoinType.JoinParticularGame && gameMode != GameMode.SinglePlayer) // Existing (Multiplayer) --> Private game
        {
            Game existing = checkIfPartyAlreadyExists(gameName, partyName);
            if (existing == null) // Game does not exist
            {
                boolean isRunning = checkIfPartyIsRunning(gameName, partyName);
                JoinResponseMessage response = null;
                if (isRunning == true)
                {
                    //LOG//Logger.getLogger(GameManager.class.getName()).log(Level.WARNING, "Can not join the party '{0}' of the type {1}, because this party has already started.", new Object[]{partyName, gameName});
                    LogRouter.log(GameManager.class.getName(), LoggingLevel.waring, "Can not join the party '{0}' of the type {1}, because this party has already started.", new Object[]{partyName, gameName});
                    response = new JoinResponseMessage(joinType, gameMode,false, false, "The party '" + partyName + "' could not be joined, because it has already started");     
                }
                else
                {
                    //LOG//Logger.getLogger(GameManager.class.getName()).log(Level.WARNING, "Can not join the party '{0}' of the type {1}, because this party does not exist.", new Object[]{partyName, gameName});
                    LogRouter.log(GameManager.class.getName(), LoggingLevel.waring, "Can not join the party '{0}' of the type {1}, because this party does not exist.", new Object[]{partyName, gameName});
                    response = new JoinResponseMessage(joinType, gameMode,false, false, "The party '" + partyName + "' could not be joined, because this party name does not exist");                
                }
                response.send(player.getSocket(), player);             
                return null; // Send Message to user: No game with this name exists
            }
            else
            {
                joinedGame = joinParty(gameName, gameMode, player, partyName, false);
                if (joinedGame != null)
                {
                    //LOG//Logger.getLogger(GameManager.class.getName()).log(Level.INFO, "{0} joined the existing {1} party with the name '{2}'!", new Object[]{player.toString(), gameName, partyName});
                    LogRouter.log(GameManager.class.getName(), LoggingLevel.game, "{0} joined the existing {1} party with the name '{2}'!", new Object[]{player.toString(), gameName, partyName});
                }
                else
                {
                     message = "Could not join the party";
                }
            }            
        }
        else // Join or create (public game) + Singleplayer
        {
            if (gameMode == GameMode.SinglePlayer || checkIfPartyIsWaiting(gameName, true) == false) 
            {
                partyName = getRandomPartyName(gameName, partyName);            // Update party name
                joinedGame = createNewGame(gameName, gameMode, partyName, player);
                if (joinedGame != null)
                {
                    joinedGame.addPlayer(player);
                    if (gameMode == GameMode.SinglePlayer)
                    {
                        User aiDummy = new User(gameName + "-AI", "");          // Create a AI dummy for visual purpose
                        aiDummy.setAI(true);
                        User.addUserToUserList(aiDummy);                        // Add dummy AI user to User list
                        joinedGame.setPrivateGame(true);                        // Single player is always private
                    }
                    else
                    {
                        joinedGame.setPrivateGame(false);                       // Create a public game
                    }
                    //LOG//Logger.getLogger(GameManager.class.getName()).log(Level.INFO, "{0} created and joined a new {1} party with the name '{2}'!", new Object[]{player.toString(), gameName, partyName});
                    LogRouter.log(GameManager.class.getName(), LoggingLevel.game, "{0} created and joined a new {1} party with the name '{2}'!", new Object[]{player.toString(), gameName, partyName});
                    gameCreated = true;
                }
                else
                {
                     message = "Could not create the party";
                     gameCreated = false;
                }
            }
            else // Multiplayer or waiting games (public)
            {              
                joinedGame = joinRandomGame(gameName, gameMode, player);
                if (joinedGame != null)
                {
                    //LOG//Logger.getLogger(GameManager.class.getName()).log(Level.INFO, "{0} joined a random party in multiplayer {1}", new Object[]{player.getName(), gameName});
                    LogRouter.log(GameManager.class.getName(), LoggingLevel.game, "{0} joined a random party in multiplayer {1}", new Object[]{player.getName(), gameName});
                }
                else
                {
                     message = "Could not join a random party";
                }
                gameCreated = false;
            }
        }
        
        if (joinedGame == null)
        {
            JoinResponseMessage response = new JoinResponseMessage(joinType, gameMode,false, false, message);                
            response.send(player.getSocket(), player);             
            return null;
        }
        // Check if the game has enough participants, if so, remove it from the
        // waiting games list and add it to the running games list.
        if (joinedGame.hasEnoughParticipants()) {
            waitingGames.remove(joinedGame);
            runningGames.add(joinedGame);

            if(ServerConfiguration.getInstance().getIsConsoleMode() == false){
                // Add the game to the GUI
                ServerGUI.getInstance().addGameToList(joinedGame,false);
                ServerGUI.getInstance().removeGameFromList(joinedGame, true);
            }
            // If there is an exception in the initialization process, report it
            try {
                joinedGame.initialize();
            } catch (Exception ex) {
                //LOG//Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, "Could not initialize game.", ex);
                LogRouter.log(GameManager.class.getName(), LoggingLevel.severe, "Could not initialize game.", ex);
                ExceptionMessage exceptionMessage = new ExceptionMessage(ex);
                exceptionMessage.send(player.getSocket(), player);
            }
            //LOG//Logger.getLogger(GameManager.class.getName()).log(Level.INFO, "Initialized {0} (ID {1})", new Object[]{gameName, joinedGame.getId()});
            LogRouter.log(GameManager.class.getName(), LoggingLevel.info, "Initialized {0} (ID {1})", new Object[]{gameName, joinedGame.getId()});
        }
        JoinResponseMessage response = new JoinResponseMessage(joinType, true, gameCreated);                
        response.send(player.getSocket(), player); 
        return joinedGame;
    }

    /**
     * Joins the first public game in the list of waiting games with the same gameName.
     *
     * @param gameName The game's name.
     * @param gameMode The game mode.
     * @param player The player who wants to join the game.
     * @return The game to be joined.
     */
    private static Game joinRandomGame(String gameName, GameMode gameMode, Player player)
    {
        for (int i = 0; i < waitingGames.size(); i++)
        {
            Game waitingGame = waitingGames.get(i);
            if (waitingGame.isPrivateGame() == true) // Skip all private games
            {
                continue;
            }
            // Check for running games of specified type and join it, if it matches
            if (waitingGame.getGameName().equals(gameName)) {
                waitingGame.addPlayer(player);
                return waitingGame;
            }
        }
        return null;                    // This should never happen.
    }

    /**
     * Joins a named game, with the defined party name.
     * The purpose of this method is to allow players to play the game with an
     * exactly defined set of people. This could easily be used to build a
     * lobby-like environment.
     *
     * @param gameName The game's name.
     * @param gameMode The game mode.
     * @param player The player who wants to join the party/game.
     * @param partyName The name of the party.
     * @param createNew If true, a new game will be crated if the party with the defined party name was not found. Otherwise null will be returned
     * @return The game to be joined or null if no game to join (see createNew) was found.
     */
    private static Game joinParty(String gameName, GameMode gameMode, Player player, String partyName, boolean createNew) {
        Game waitingGame = checkIfPartyAlreadyExists(gameName, partyName);       
        if (waitingGame == null)
        {
            if (createNew == true)
            {
                return createNewGame(gameName, gameMode, partyName, player);
            }
            else
            {
                return null;
            }
        }
        else
        {
            waitingGame.addPlayer(player);
            return waitingGame;
        }
    }
    
    
    /**
     * Gets a free random game name
     * @param gameName Name of the game (type)
     * @param template Template to build the party name. A number will appended to the template
     * @return A unique party name
     */
    private static String getRandomPartyName(String gameName, String template)
    {
        int counter = 1;
        String name = template;
        while(true)
        {
            if (checkIfPartyAlreadyExists(gameName, name)!= null) // waiting games
            {
                counter++;
                name = template + "(" + Integer.toString(counter) + ")";
                continue;
            }
            if (checkIfPartyIsRunning(gameName, name) == true) // running games
            {
                counter++;
                name = template + "(" + Integer.toString(counter) + ")";
                continue;
            }
            break;
        }
        return name;
    }
    
    /**
     * Method to check whether a party of the defined game type currently is waiting. This method is used to find random waiting games.
     * @param gameName Name of the game (type)
     * @param onlyPublicGames If true, only public parties will be considered, otherwise all games will be considered
     * @return True, if at least one party is waiting, otherwise false
     */
    private static boolean checkIfPartyIsWaiting(String gameName, boolean onlyPublicGames)
    {
        if (waitingGames.isEmpty() == true) { return false; }
        for (int i = 0; i < waitingGames.size(); i++) 
        {
            if (onlyPublicGames == true && waitingGames.get(i).isPrivateGame() == true)
            {
                continue;
            }
          if (waitingGames.get(i).getGameName().equals(gameName))
          {
              return true;
          }
        }
        return false;
    }    
    
    /**
     * Method to check whether a party of the defined game type and party name currently is running
     * @param gameName Name of the game (type)
     * @param partyName Name of the party to check
     * @return True, if a party with the defined name and type is running, otherwise false
     */
    private static boolean checkIfPartyIsRunning(String gameName, String partyName)
    {
        for (int i = 0; i < runningGames.size(); i++) 
        {
          if (runningGames.get(i).getGameName().equals(gameName) && runningGames.get(i).getPartyName().equals(partyName) )
          {
              return true;
          }
        }
        return false;
    }
    
    /**
     * Method to check whether a party of the defined game type and name is waiting
     * @param gameName Name of the game (type)
     * @param partyName Name of the party to check
     * @return The game object of the defined type and party name if existing (waiting), otherwise null
     */
    private static Game checkIfPartyAlreadyExists(String gameName, String partyName)
    {
        Game waitingGame;
          for (int i = 0; i < waitingGames.size(); i++) 
          {
            waitingGame = waitingGames.get(i);

            // Check for running games of specified type and party name and
            // join it, if it matches.
            if (waitingGame.getGameName().equals(gameName) && waitingGame.getPartyName().equals(partyName)) 
            {
                return waitingGame;
            }
           } 
          return null;
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
            ServerGUI.getInstance().addGameToList(newGame,true);
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
            //LOG//Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, "There is no game class with this name.");
            LogRouter.log(GameManager.class.getName(), LoggingLevel.severe, "There is no game class with this name.");
            ForceCloseMessage forceCloseMessage = new ForceCloseMessage("There is no GameLogic class for this game.");
            forceCloseMessage.send(player.getSocket(), player);
        } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException | InstantiationException | SecurityException | NoSuchMethodException ex) {
            //LOG//Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, "Game could not be instantiated. Check the constructor of your GameLogic class."
            //LOG//        + "Most probably the constructor of the GameLogic class is not parameterless.", ex);
            LogRouter.log(GameManager.class.getName(), LoggingLevel.severe, "Game could not be instantiated. Check the constructor of your GameLogic class. Most probably the constructor of the GameLogic class is not parameterless.", ex);
            ExceptionMessage exceptionMessage = new ExceptionMessage((Exception) ex.getCause());
            exceptionMessage.send(player.getSocket(), player);
        } catch (NullPointerException ex) {
            //LOG//Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, "There is no GameLogic class for this game:" + gameName);
            LogRouter.log(GameManager.class.getName(), LoggingLevel.severe, "There is no GameLogic class for this game:" + gameName);
            ForceCloseMessage forceCloseMessage = new ForceCloseMessage("There is no GameLogic class for this game: " + gameName);
            forceCloseMessage.send(player.getSocket(), player);
        }
        catch (Exception ex) // All other exceptions
        {
            //LOG//Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, "An unknown error occured: " + ex.getMessage());
            LogRouter.log(GameManager.class.getName(), LoggingLevel.severe, "An unknown error occured: " + ex.getMessage());
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
                    ServerGUI.getInstance().removeGameFromList(game, false);
                    //LOG//Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.INFO, "Removed the game {0} from the running games list.", game.toString());
                    LogRouter.log(GameManager.class.getName(), LoggingLevel.game, "Removed the game {0} from the running games list.", game.toString());
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
                ServerGUI.getInstance().removeGameFromList(game, true);
                //LOG//Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.INFO, "Removed the game {0} from the waiting games list.", game.toString());
                LogRouter.log(GameManager.class.getName(), LoggingLevel.game, "Removed the game {0} from the waiting games list.", game.toString());
                game.removePlayer(terminatingPlayer);
                ForceCloseMessage forceCloseMessage = new ForceCloseMessage(reason);
                game.sendMessageToAllPlayers(forceCloseMessage);
            }
            GameManager.cleanUpUsers();                                         // Clean up user list
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
                ServerGUI.getInstance().removeGameFromList(game, false);
            }
            //LOG//Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.INFO, "Removed the game {0} from the running games list.", game.toString());
            LogRouter.log(GameManager.class.getName(), LoggingLevel.game, "Removed the game {0} from the running games list.", game.toString());
        } // End game if it is a waiting game.
        else if (waitingGames.contains(game)) {
            waitingGames.remove(game);
            if(ServerConfiguration.getInstance().getIsConsoleMode() == false){
                ServerGUI.getInstance().removeGameFromList(game, true);
            }
            //LOG//Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.INFO, "Removed the game {0} from the waiting games list.", game.toString());
            LogRouter.log(GameManager.class.getName(), LoggingLevel.game, "Removed the game {0} from the waiting games list.", game.toString());
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
     * Method to clean up users. All users which are not in a waiting or running 
     * game will be removed from the static user list
     */
    public static void cleanUpUsers()
    {
        ArrayList<User> tempUsers = new ArrayList<User>();
        boolean found;
        int len = User.users.size(); // This is more efficient than dynamic peeked in for
        int lenW = waitingGames.size(); // "
        int lenR = runningGames.size(); // "
        Game g;
        User u;
        String loginName;
        int j, k, lenP;
        for(int i = 0; i < len; i++)
        {
            u = User.users.get(i);
            if (u.isNonPersistentUser() == false) { continue; }     // Only remove non persistent users
            loginName = u.getUserName();
            found = false;
            for(j = 0; j < lenW; j++) // Wating games
            {
                g = waitingGames.get(j);
                lenP = g.getPlayers().size();
                for(k = 0; k < lenP; k++)
                {
                    if (g.getPlayers().get(k).getLoginName().equals(loginName))
                    {
                       found = true;
                       break;
                    }
                }
                if (found == true) // No further check needed -> User still active
                {
                    break;
                }
            }
            if (found == true) // No further check needed -> User still active
            {
                continue;
            }
            for(j = 0; j < lenR; j++) // Running games
            {
                g = runningGames.get(j);
                lenP = g.getPlayers().size();
                for(k = 0; k < lenP; k++)
                {
                    if (g.getPlayers().get(k).getLoginName().equals(loginName))
                    {
                       found = true;
                       break;
                    }
                }
                if (found == true) // No further check needed -> User still active
                {
                    break;
                }
            }
            if (found == false)
            {
                tempUsers.add(u); // Mark the user with this index to remove
            }
        }
        len = tempUsers.size();
        for(int i = 0; i < len; i++)
        {
            u = tempUsers.get(i);
            User.removeUserFromUserList(u);
        }
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
