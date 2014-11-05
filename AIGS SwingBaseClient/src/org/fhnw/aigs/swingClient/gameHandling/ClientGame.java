package org.fhnw.aigs.swingClient.gameHandling;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fhnw.aigs.swingClient.GUI.BaseGameWindow;
import org.fhnw.aigs.swingClient.communication.ClientMessageBroker;
import org.fhnw.aigs.commons.GameMode;
import org.fhnw.aigs.commons.JoinType;
import org.fhnw.aigs.commons.Player;
import org.fhnw.aigs.commons.communication.JoinMessage;
import org.fhnw.aigs.commons.communication.Message;

/**
 * This class is the equivalent of "Game" on the client side. However it is
 * merely a container class that contains all important facts of a game:<br>
 * The game name, a reference to the player, the gameMode and a reference to the
 * current "BaseGameWindow". The latter allows the game to manipulate the GUI
 * directly when using the standard AIGS BaseClient.<br>
 * v1.0 Initial release<br>
 * v1.1 Functional changes, additional properties
 *
 * @author Matthias Stöckli (v1.0)
 * @version v1.1 (Raphael Stoeckli, 22.10.2014)
 */
public abstract class ClientGame {

    /**
     * The name of the game, e.g. "TicTacToe". This must be the same name as the
     * package name!
     */
    protected String gameName;
    
    /**
     * The name of the Party
     */
    protected String partyName;
    
    /**
     * The type of starting the game respectively to join in a existing game
     */
    protected JoinType joinType;
    
    /**
     * A reference to the player, this will usually be set by the
     * ClientMessageBroker in the course of the identification process. Once the
     * client was identified, the server sends back the definite player name
     * which will be used to identify the player.
     */
    protected Player player;
    
    /**
     * The game mode helps controlling the game flow. if the game mode
     * "SinglePlayer" is selected, it is not possible to use the method
     * "startGameWith" on the client side.
     */
    protected GameMode gameMode;
    
    /**
     * The BaseGameWindow holds a reference to the gameWindow. This class is
     * used in the standard JavaFX based AIGS BaseClient for showing the GUI. It
     * can be accessed whenever the GUI changes on a large scale, for example
     * when an overlay (loading screen, end game screen etc.) should be faded in
     * or out.
     *
     */
    protected BaseGameWindow gameWindow;

    /**
     * Indicates whether the game is still running or not
     */
    protected boolean noInteractionAllowed;
    
    /**
     * Indicates a (optional) version number of the game. Use this for better version control of games on the server
     */
    protected String versionString;    
    
    /**
     * See {@link ClientGame#gameWindow}
     */
    public BaseGameWindow getGameWindow() {
        return gameWindow;
    }
    
    /**
     * See {@link ClientGame#gameName}
     */
    public String getGameName() {
        return gameName;
    }
    
     /**
     * See {@link ClientGame#joinType}
     */      
    public JoinType getJoinType() {
        return joinType;
    }
    
     /**
     * See {@link ClientGame#partyName}
     */  
    public String getPartyName()
    {
        return partyName;
    }    

    /**
     * See {@link ClientGame#player}
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * See {@link ClientGame#gameMode}
     */
    public GameMode getGameMode() {
        return gameMode;
    }
    
    /**
     * * See {@link ClientGame#noInteractionAllowed}
     */
    public boolean isNoInteractionAllowed() {
        return noInteractionAllowed;
    }
    
    /**
     * See {@link ClientGame#versionString}.
     */        
    public String getVersionString() {
        return versionString;
    }        
    
    /**
     * See {@link ClientGame#player}
     */
    public void setPlayer(Player player) {
        this.player = player;
        
    }

    /**
     * See {@link ClientGame#gameName}
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
    
    /**
     * See {@link ClientGame#partyName}
     */    
    public void setPartyName(String partyName){
        this.partyName = partyName;
    }
    
    /**
     * See {@link ClientGame#joinType}
     */     
    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }        
    
    /**
     * See {@link ClientGame#gameWindow}
     */
    public void setGameWindow(BaseGameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    /**
     * See {@link ClientGame#gameMode}
     */
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }    
    
    /**
     * * See {@link ClientGame#noInteractionAllowed}
     */
    public void setNoInteractionAllowed(boolean noInteractionAllowed) {
        this.noInteractionAllowed = noInteractionAllowed;
    }
    
    /**
    * See {@link ClientGame#versionString}.
    */ 
    public void setVersionString(String versionString) {
        this.versionString = versionString;
    }     
    
    /**
     * Creates a new instance of a ClientGame with the specified name and a
     * gameMode. This constructor will set up the Shutdown hook which takes care
     * of the house keeping like sending RoceCloseMessages upon termination.
     *
     * @param gameName The game's name, e.g. TicTacToe
     * @param mode The game mode, e.g. SinglePlayer
     */
    public ClientGame(String gameName, GameMode mode) {
        setUpLogging();                                     // Start Logging
        this.gameName = gameName;
        this.gameMode = mode;
        this.joinType = JoinType.Auto;                      // Default

        Thread shutdownCleanUpThread = new Thread(new ClientShutdownCleanUp());
        shutdownCleanUpThread.setName("ShutdownCleanUpThread");
        Runtime.getRuntime().addShutdownHook(shutdownCleanUpThread);
    }
    

    /**
     * Creates a new instance of a ClientGame with the specified name, version and
     * gameMode. This constructor will set up the Shutdown hook which takes care
     * of the house keeping like sending RoceCloseMessages upon termination.
     *
     * @param gameName The game's name, e.g. TicTacToe
     * @param mode The game mode, e.g. SinglePlayer
     * @param version The version of the game
     */
    public ClientGame(String gameName, String version, GameMode mode) {
        setUpLogging();                                     // Start Logging
        this.gameName = gameName;
        this.gameMode = mode;
        this.versionString = version;
        this.joinType = JoinType.Auto;                      // Default

        Thread shutdownCleanUpThread = new Thread(new ClientShutdownCleanUp());
        shutdownCleanUpThread.setName("ShutdownCleanUpThread");
        Runtime.getRuntime().addShutdownHook(shutdownCleanUpThread);
    }
    

    /**
     * Creates a new instance of a ClientGame with the specified name. This
     * constructor does not specify a game mode. It can be used when there is no
     * need for a game mode. The GameMode will then be set to SinglePlayer which
     * will have no effect on the game as long as not specifically implemente in
     * the game logic.
     *
     * @param gameName The game's name, e.g. "TicTacToe"
     */
    public ClientGame(String gameName) {
        this(gameName, GameMode.SinglePlayer);
    }    
    
    /**
     * This method is the core of every game on the client side. Due to the
     * abstract nature, it must be implemented by every ClientGame. The method
     * is responsible for the the processing of incoming messages. It is
     * intended to work in a very similar way to the <b>processGameLogic</b>
     * method of the server side.<br>
     * ProcessGameLogic() will be called as soon as a non system message (e.g.
     * ClientShutdownMessage or similar) arrives. The
     * {@link ClientMessageBroker} will forward the class to this method. In
     * this method it will then be interpreted and the results will change
     * whatever is needed, e.g. the GUI.
     *
     * @param message The message to be processed.
     */
    public abstract void processGameLogic(Message message);
    
    /**
     * This <b>abstract</b> method will be called once a connection to the server$
     * has successfully been established. This method is the last possibility for
     * the player to ask for any special input or similar, before a game starts.
     * Usually, the onGameReady() method ends with a call of
     * {@link ClientGame#startGame} which will trigger a JoinMessage.
     * The server will then start a new game or let the player join an existing game. 
     */
    public abstract void onGameReady();

    
    /**
     * Sends a message to the server.
     * <i>Please note: This method is just a shorthand. It just calls the 
     * ClientMessageBroker's sendMessage method which will call the method's
     * sendMessage method.
     * The same effect could be achieved
     * by statically loading ClientMessageBroker.</i>
     * @param message The message to be sent.
     */
    public void sendMessageToServer(Message message){
        if(noInteractionAllowed){
            Logger.getLogger(ClientGame.class.getName()).log(Level.INFO,
                    "It is not possible to send messages if the flag \"noInteractionAllowed\" is set to true");            
        }else{
            ClientMessageBroker.sendMessage(message);
        }
    }
    
    /**
     * Initializes the game on the server side by sending a JoinMessage. 
     * The Server will decide whether to create or join a game depending on 
     * the defined join type. The JoinMessage will contain the name of the game,
     * the join type and the game mode.<br>
     * This method will be called in {@link ClientGame#onGameReady()}
     */
    public void startGame() {
        JoinMessage joinMessage = null;
        if (this.joinType == JoinType.Auto)
        {
            joinMessage = new JoinMessage(gameName, gameMode, JoinType.Auto);
        }
        else
        {
            joinMessage = new  JoinMessage(gameName, gameMode, partyName, this.joinType);
        }
        sendMessageToServer(joinMessage);     
    }
    
    /**
     * This method is similar to the "startGame()" method.
     * Initializes the game on the server side by sending a JoinMessage.
     * The JoinMessage will contain the name of the game and the game mode.
     * Additionally a player can indicate a {@link org.fhnw.aigs.commons.Game#partyName}. In this case
     * the server will match all people who indicated the same partyName
     * @param partyName The name of the party, e.g. "TicTacPascal"
     */
    public void startNamedParty(String partyName) {
        if (partyName.isEmpty()) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, "Please name the party.");
            System.exit(0);        
        }
        if (gameMode == GameMode.SinglePlayer) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, "You cannot use 'startGameAsNamedPArty' if you want to play "
                        + "in single player mode.");            
            System.exit(0);
        }
        JoinMessage joinMessage = new JoinMessage(gameName, gameMode, partyName, JoinType.JoinParticularGame);
        sendMessageToServer(joinMessage);
    }
    
    /**
     * This method is responsible for the logging by setting a FileHandler.
     * Usually the log files will be saved to the folder "logs", under the name
     * "aigs.log" If this is not possible, a new log file will be created.
     */
    private static void setUpLogging() {

        // Get the standard logger (root logger) from which all loggers inherit
        Logger rootLogger = Logger.getLogger("");
        try {
            new File("logs").mkdir();           // Creates "logs" folder, if it does not already exist

            // Housekeeping: Get the old handlers and remove them.
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler h : handlers) {
                rootLogger.removeHandler(h);
            }

            // Add a filehandler to the root logger. All logging activity will be saved in the file "logs/aigs.log"
            // Additionally all logs shall also be shown on the console
            rootLogger.addHandler(new FileHandler("logs/aigs.log"));
            ConsoleHandler h = new ConsoleHandler();
            h.setLevel(Level.ALL);

            rootLogger.addHandler(h);
        } catch (IOException ex) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, "File could not be opened or created. A new log file will be created.", ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, "An unknown error occurred.", ex); 
        }
        Logger.getLogger(ClientGame.class.getName()).log(Level.INFO, "Logging was set up.");
    }    
}
    
    

