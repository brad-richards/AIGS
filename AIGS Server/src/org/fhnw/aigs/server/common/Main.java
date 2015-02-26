package org.fhnw.aigs.server.common;

import org.fhnw.aigs.server.gui.StartServerAction;
import org.fhnw.aigs.server.gui.ServerGUI;
import org.fhnw.aigs.server.gameHandling.User;

/**
 * This is the Main class. It is responsible for the following tasks:
 * <ul>
 * <li>Loads the server configuration</li>
 * <li>Takes care of shutdown procedure</li>
 * <li>Starts logging</li>
 * <li>Starts GUI</li>
 * <li>Takes care of Keep Alive signals </li>
 * </ul><br>
 * v1.0 Initial release<br>
 * v1.1 Functional changes<br>
 * v1.2 Changing of logging
 *
 * @author Matthias Stöckli
 * @version v1.2 (Raphael Stoeckli, 24.02.2015)
 */
public class Main {
    
    /**
     * Starts the server and takes care of all the necessary steps to get a
     * working AIGS
     *
     * @param args Optional parameters
     */
    public static void main(String[] args) {
        setupServerConfiguration();        
        //setUpLogging();
        LogRouter.setUpLogging();
        loadUsers();
        setUpShutdownHook();
        
        // If the console mode is active, start the server immediately,
        // otherwise set up the GUI.
        if(ServerConfiguration.getInstance().getIsConsoleMode()){
            new StartServerAction().startServer();
            AIGSConsoleHandler aigsConsoleHandler = new AIGSConsoleHandler();
            aigsConsoleHandler.runInputLoop();
        }else{
            setUpGUI();
        }
    }

    /**
     * Initializes (reads) the ServerConfiguration and prints it out.
     */
    private static void setupServerConfiguration() {
        ServerConfiguration.initialize();
        ServerConfiguration.getInstance().printConfiguration();
    }

    /**
     * Sets a shutdown hook, the ShutdownCleanUpThread will be started as soon
     * as the server closes (e.g. via {@link System#exit}).
     */
    private static void setUpShutdownHook() {
        Thread cleanUpThread = new Thread(new ServerShutdownCleanUp());
        cleanUpThread.setName("ShutdownCleanUpThread");
        Runtime.getRuntime().addShutdownHook(cleanUpThread);
        //LOG//Logger.getLogger(Main.class.getName()).log(Level.INFO, "Shutdown hook has been registered.");
        LogRouter.log(Main.class.getName(), LoggingLevel.system, "Shutdown hook has been registered.");
    }

    /**
     * Starts the GUI.
     */
    private static void setUpGUI() {
        //ServerGUI gui = new ServerGUI();
        ServerGUI.getInstance().setVisible(true);
        //gui.setVisible(true);
        if (ServerConfiguration.getInstance().getIsAnonymousLoginAllowed() == true)
        {
           //LOG//Logger.getLogger(Main.class.getName()).log(Level.INFO, "Set up GUI.\nLogin system is disabled. Players can participate without checking the credentials.\nNote: Please click on 'Start AIGS' to start the server (not started yet)");
            LogRouter.log(Main.class.getName(), LoggingLevel.system, "Set up GUI.\n--> Login system is disabled. Players can participate without checking the credentials.\n--> Note: Please click on 'Start AIGS' to start the server (not started yet)");
        }
        else
        {
            //LOG//Logger.getLogger(Main.class.getName()).log(Level.INFO, "Set up GUI.\nLogin system is enabled. Playes can only participate after checking the credentials.\nNote: Please click on 'Start AIGS' to start the server (not started yet)");
            LogRouter.log(Main.class.getName(), LoggingLevel.system, "Set up GUI.\n--> Login system is enabled. Playes can only participate after checking the credentials.\n--> Note: Please click on 'Start AIGS' to start the server (not started yet)");
        }
    }

    /**
     * Load all users for the identification process.<br>
     * The visual presentation of the users is managed in {@link ServerGUI#loadUsers()}
     */
    public static void loadUsers() {
        User.readUsersFromXml();
    }
}
