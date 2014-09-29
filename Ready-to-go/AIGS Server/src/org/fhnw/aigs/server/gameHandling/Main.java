package org.fhnw.aigs.server.gameHandling;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;
import org.fhnw.aigs.server.gui.ServerGUI;

/**
 * This is the Main class. It is responsible for the following tasks:
 * <ul>
 * <li>Loads the server configuration</li>
 * <li>Takes care of shutdown procedure</li>
 * <li>Starts logging</li>
 * <li>Starts GUI</li>
 * <li>Takes care of Keep Alive signals </li>
 * </ul>
 *
 * @author Matthias Stöckli
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
        setUpLogging();
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
     * This method is responsible for the logging by setting a FileHandler.
     * Usually the log files will be saved to the folder "logs", under the name
     * "aigs.log". Logs which exceed 10 MB in size will trigger the creation of
     * a new logfile.<br>
     * The property {@link ServerConfiguration#getIsXMLlogging() } indicates
     * whether the logging will be in the XML format or a more concise form is
     * being used.
     */
    private static void setUpLogging() {

        // Get the standard logger (root logger) from which all loggers inherit
        Logger rootLogger = Logger.getLogger("");

        try {
            // Creates "logs" folder, if it does not already exist
            String logDirectory = ServerConfiguration.getInstance().getLogDirectory();
            new File(logDirectory).mkdir();
            //new File("logs").mkdir();

            // Housekeeping: Get the old handlers and remove them.
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler h : handlers) {
                rootLogger.removeHandler(h);
            }

            // Add a filehandler to the root logger. All logging activity will
            // be saved to the file "logs/aigs.log". If the file exceeds 10 MB
            // a new file will be created (up to 10).
            FileHandler fileHandler = new FileHandler(logDirectory + "/aigs.log", 1073741824, 10);
            fileHandler.setEncoding("UTF-8");

            // Decides which logging format will be used.
            if (ServerConfiguration.getInstance().getIsXMLlogging()) {
                fileHandler.setFormatter(new XMLFormatter());
                fileHandler.setLevel(Level.ALL);
            } else {
                fileHandler.setFormatter(new SimpleFormatter());
            }
            rootLogger.addHandler(fileHandler);

            // Add a Console handler so all logs will also be shown on the console.
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(consoleHandler);

        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "File could not be opened or created. A new log file will be created.", ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (Exception ex) // All other exceptions
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "An unknown error occured.", ex);
        }
        Logger.getLogger(Main.class.getName()).log(Level.INFO, "Now logging...");
    }

    /**
     * Sets a shutdown hook, the ShutdownCleanUpThread will be started as soon
     * as the server closes (e.g. via {@link System#exit}).
     */
    private static void setUpShutdownHook() {
        Thread cleanUpThread = new Thread(new ServerShutdownCleanUp());
        cleanUpThread.setName("ShutdownCleanUpThread");
        Runtime.getRuntime().addShutdownHook(cleanUpThread);
        Logger.getLogger(Main.class.getName()).log(Level.INFO, "Shutdown hook has been registered.");
    }

    /**
     * Starts the GUI.
     */
    private static void setUpGUI() {
        ServerGUI gui = new ServerGUI();
        gui.setVisible(true);
        Logger.getLogger(Main.class.getName()).log(Level.INFO, "Set up GUI.\nNote: Please click on 'Start AIGS' to start the server (not started yet)");
    }

    /**
     * Load all users for the identification process.
     */
    private static void loadUsers() {
        User.readUsersFromXml();
    }
}
