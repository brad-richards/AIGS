package org.fhnw.aigs.server.common;
 
import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.fhnw.aigs.commons.XMLHelper;

/**
 * Class to manage the server-side logging. It supersede standard logging for better control of the formats, outputs and levels of the logs.<br>
 * The class uses the Java SE standard logger in the backend.<br>
 * Possible log levels are Severe, Warning, System (messages of the AIGS server), Game (messages of AIGS games on the server) and Info.
 * @version 1.0
 * @author Raphael Stoeckli (26.02.2015)
 */
public class LogRouter {
    
    /**
     * Singleton object of the logging threshold
     */
    private static LoggingThreshold threshold = null;
    
    /**
     * Singleton object of the logging style
     */
    private static LoggingStyle style = null;
    
    /**
     * Setter for the singleton instance of the logging threshold
     * @param threshold Logging threshold
     */
    public static void SetThreshold(LoggingThreshold threshold)
    {
        LogRouter.threshold = threshold;
    }
    
    /**
     * Setter for the singleton instance of the logging style
     * @param style Logging style
     */
    public static void SetLoggerStyle(LoggingStyle style)
    {
        LogRouter.style = style;
    }
    
    /**
     * Combined setter to update logging threshold and style
     * @param threshold Logging threshold
     * @param style Logging threshold
     */
    public static void updateRules(LoggingThreshold threshold, LoggingStyle style)
    {
        LogRouter.threshold = threshold;
        LogRouter.style = style;
    }
    
    /**
     * Method to update logging threshold and style from the server configuration (settings)
     */
    public static void updateRules()
    {
        LogRouter.updateRules(ServerConfiguration.getInstance().getLoggerThreshold(), ServerConfiguration.getInstance().getLoggerStyle());
    }    
    
    /**
     * This method is responsible for the logging by setting a FileHandler.
     * Usually the log files will be saved to the folder "logs", under the name
     * "aigs.log". Logs which exceed 10 MB in size will trigger the creation of
     * a new logfile.
     */
    public static void setUpLogging() {       
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
            /*
            if (ServerConfiguration.getInstance().getIsXMLlogging()) {
                fileHandler.setFormatter(new XMLFormatter());
                fileHandler.setLevel(Level.ALL);
            } else {
                fileHandler.setFormatter(new SimpleFormatter());
            }
            */
            fileHandler.setFormatter(new CustomFormatter(ServerConfiguration.getInstance().getLoggerStyle()));
            fileHandler.setLevel(Level.ALL); // Filtering will be done adHoc in the log() method
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
     * Method to process a log entry
     * @param className Name of the calling class
     * @param level Logging level of the entry
     * @param customMessage Message, can contain {0} which will be resolved with the content of the parameter (param)
     * @param param Parameter of the log entry
     */
    public static void log(String className, LoggingLevel level, String customMessage, Object param)
    {
        if (LogRouter.threshold == null) // Init (store threshold for better performance)
        {
            LogRouter.threshold = ServerConfiguration.getInstance().getLoggerThreshold();
        }
        if (LogRouter.style == null) // Init (store style for better performance)
        {
            LogRouter.style = ServerConfiguration.getInstance().getLoggerStyle();
        }
        customMessage = processMessage(customMessage);
        if (LogRouter.threshold == LoggingThreshold.off || level == LoggingLevel.none) { return; } // Discard
        else if (LogRouter.threshold == LoggingThreshold.all) // Sys, severe, warn, info
        {
            if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage, param);
            }
            else if (level == LoggingLevel.waring)
            {
                Logger.getLogger(className).log(Level.WARNING, customMessage, param);
            }
            else //if (level == LoggingLevel.info) // Info
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, param);
            }
        }
        else if (LogRouter.threshold == LoggingThreshold.warningSevereSystemGame) // Game, Sys, severe, warn
        {
             if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage, param);
            }
            else if (level == LoggingLevel.waring)
            {
                Logger.getLogger(className).log(Level.WARNING, customMessage, param);
            }
            else if (level == LoggingLevel.system)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, param);
            }
            else if (level == LoggingLevel.game)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, param);
            } 
        }        
        else if (LogRouter.threshold == LoggingThreshold.warningSevereSystem) // Sys, severe, warn
        {
             if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage, param);
            }
            else if (level == LoggingLevel.waring)
            {
                Logger.getLogger(className).log(Level.WARNING, customMessage, param);
            }
            else if (level == LoggingLevel.system)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, param);
            }
        }
        else if (LogRouter.threshold == LoggingThreshold.waringSevere) // Severe, warn
        {
             if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage, param);
            }
            else if (level == LoggingLevel.waring)
            {
                Logger.getLogger(className).log(Level.WARNING, customMessage, param);
            }           
        }
        else if (LogRouter.threshold == LoggingThreshold.severeSystem) // Sys, severe
        {
             if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage, param);
            }
            else if (level == LoggingLevel.system)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, param);
            }            
        }
        else if (LogRouter.threshold == LoggingThreshold.severeSystemGame) // Game, Sys, severe
        {
             if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage, param);
            }
            else if (level == LoggingLevel.system)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, param);
            }
            else if (level == LoggingLevel.game)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, param);
            }
        }        
        else if (LogRouter.threshold == LoggingThreshold.severe) // Severe
        {
             if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage, param);
            }
        }
        else if (LogRouter.threshold == LoggingThreshold.system) // Sys
        {
            if (level == LoggingLevel.system)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, param);
            }           
        }
        else if (LogRouter.threshold == LoggingThreshold.game) // Game
        {
            if (level == LoggingLevel.game)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, param);
            }           
        }
    }    
    
    /**
     * Method to process a log entry
     * @param className Name of the calling class
     * @param level Logging level of the entry
     * @param customMessage Message, can contain {NUMBERS} which will be resolved with the content of the parameters (params)
     * @param params Array of parameter of the log entry
     */
    public static void log(String className, LoggingLevel level, String customMessage, Object[] params)
    {
        if (LogRouter.threshold == null) // Init (store threshold for better performance)
        {
            LogRouter.threshold = ServerConfiguration.getInstance().getLoggerThreshold();
        }
        if (LogRouter.style == null) // Init (store style for better performance)
        {
            LogRouter.style = ServerConfiguration.getInstance().getLoggerStyle();
        }
        customMessage = processMessage(customMessage);
        if (LogRouter.threshold == LoggingThreshold.off || level == LoggingLevel.none) { return; } // Discard
        else if (LogRouter.threshold == LoggingThreshold.all) // Sys, severe, warn, info
        {
            if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage, params);
            }
            else if (level == LoggingLevel.waring)
            {
                Logger.getLogger(className).log(Level.WARNING, customMessage, params);
            }
            else //if (level == LoggingLevel.info) // Info
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, params);
            }
        }
        else if (LogRouter.threshold == LoggingThreshold.warningSevereSystemGame) // Game, Sys, severe, warn
        {
             if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage, params);
            }
            else if (level == LoggingLevel.waring)
            {
                Logger.getLogger(className).log(Level.WARNING, customMessage, params);
            }
            else if (level == LoggingLevel.system)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, params);
            }
            else if (level == LoggingLevel.game)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, params);
            } 
        }        
        else if (LogRouter.threshold == LoggingThreshold.warningSevereSystem) // Sys, severe, warn
        {
             if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage, params);
            }
            else if (level == LoggingLevel.waring)
            {
                Logger.getLogger(className).log(Level.WARNING, customMessage, params);
            }
            else if (level == LoggingLevel.system)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, params);
            }
        }
        else if (LogRouter.threshold == LoggingThreshold.waringSevere) // Severe, warn
        {
             if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage, params);
            }
            else if (level == LoggingLevel.waring)
            {
                Logger.getLogger(className).log(Level.WARNING, customMessage, params);
            }           
        }
        else if (LogRouter.threshold == LoggingThreshold.severeSystem) // Sys, severe
        {
             if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage, params);
            }
            else if (level == LoggingLevel.system)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, params);
            }            
        }
        else if (LogRouter.threshold == LoggingThreshold.severeSystemGame) // Game, Sys, severe
        {
             if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage, params);
            }
            else if (level == LoggingLevel.system)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, params);
            }
            else if (level == LoggingLevel.game)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, params);
            }
        }        
        else if (LogRouter.threshold == LoggingThreshold.severe) // Severe
        {
             if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage, params);
            }
        }
        else if (LogRouter.threshold == LoggingThreshold.system) // Sys
        {
            if (level == LoggingLevel.system)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, params);
            }           
        }
        else if (LogRouter.threshold == LoggingThreshold.game) // Game
        {
            if (level == LoggingLevel.game)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage, params);
            }           
        }

    }
    
    /**
     * Method to process a log entry
     * @param className Name of the calling class
     * @param level Logging level of the entry
     * @param customMessage Message to display (no further parameters)
     */
    public static void log(String className, LoggingLevel level, String customMessage)
    {
        if (LogRouter.threshold == null) // Init (store threshold for better performance)
        {
            LogRouter.threshold = ServerConfiguration.getInstance().getLoggerThreshold();
        }
        if (LogRouter.style == null) // Init (store style for better performance)
        {
            LogRouter.style = ServerConfiguration.getInstance().getLoggerStyle();
        }
        customMessage = processMessage(customMessage);
        if (LogRouter.threshold == LoggingThreshold.off || level == LoggingLevel.none) { return; } // Discard
        else if (LogRouter.threshold == LoggingThreshold.all) // Sys, severe, warn, info
        {
            if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage);
            }
            else if (level == LoggingLevel.waring)
            {
                Logger.getLogger(className).log(Level.WARNING, customMessage);
            }
            else //if (level == LoggingLevel.info) // Info
            {
                Logger.getLogger(className).log(Level.INFO, customMessage);
            }
        }
        else if (LogRouter.threshold == LoggingThreshold.warningSevereSystemGame) // Game, Sys, severe, warn
        {
            if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage);
            }
            else if (level == LoggingLevel.waring)
            {
                Logger.getLogger(className).log(Level.WARNING, customMessage);
            }
            else if (level == LoggingLevel.system)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage);
            }
            else if (level == LoggingLevel.game)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage);
            } 
        }        
        else if (LogRouter.threshold == LoggingThreshold.warningSevereSystem) // Sys, severe, warn
        {
            if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage);
            }
            else if (level == LoggingLevel.waring)
            {
                Logger.getLogger(className).log(Level.WARNING, customMessage);
            }
            else if (level == LoggingLevel.system)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage);
            }
        }
        else if (LogRouter.threshold == LoggingThreshold.waringSevere) // Severe, warn
        {
            if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage);
            }
            else if (level == LoggingLevel.waring)
            {
                Logger.getLogger(className).log(Level.WARNING, customMessage);
            }           
        }
        else if (LogRouter.threshold == LoggingThreshold.severeSystem) // Sys, severe
        {
            if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage);
            }
            else if (level == LoggingLevel.system)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage);
            }            
        }
        else if (LogRouter.threshold == LoggingThreshold.severeSystemGame) // Game, Sys, severe
        {
            if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage);
            }
            else if (level == LoggingLevel.system)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage);
            }
            else if (level == LoggingLevel.game)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage);
            }
        }        
        else if (LogRouter.threshold == LoggingThreshold.severe) // Severe
        {
            if (level == LoggingLevel.severe)
            {
                Logger.getLogger(className).log(Level.SEVERE, customMessage);
            }
        }
        else if (LogRouter.threshold == LoggingThreshold.system) // Sys
        {
            if (level == LoggingLevel.system)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage);
            }           
        }
        else if (LogRouter.threshold == LoggingThreshold.game) // Game
        {
            if (level == LoggingLevel.game)
            {
                Logger.getLogger(className).log(Level.INFO, customMessage);
            }           
        }

    }    
    
    /**
     * Method to route the custom message. In case of XML style, {@link XMLHelper#prettyPrintXml(java.lang.String)} will be used. 
     * Otherwise the input will not be changed. In case of the style "discard" an empty string will be returned.
     * @param input Message to process
     * @return Processed message
     */
    private static String processMessage(String input)
    {
        if (input == null) { return null;}
        if (LogRouter.style == LoggingStyle.discard){ return "";}
        else if ( LogRouter.style == LoggingStyle.compressed) { return input; }
        else if ( LogRouter.style == LoggingStyle.xmlFull || LogRouter.style == LoggingStyle.plainFull )
        {
            return input; 
        }
        else
        {
            return XMLHelper.prettyPrintXml(input);
        }     
    }
    
}
