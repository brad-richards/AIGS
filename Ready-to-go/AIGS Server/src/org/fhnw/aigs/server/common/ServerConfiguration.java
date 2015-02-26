package org.fhnw.aigs.server.common;

import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Due to the fact that the class uses the Singleton Pattern, it is not possible to instantiate
 * ClientCommunication directly. In order to get to an instance, use <b>getInstance()</b> instead.<br>
 * v1.0 Initial release<br>
 * v1.1 Functional changes<br>
 * v1.2 Features added and default values changed<br>
 * v1.2.1 Minor Changes in presets<br>
 * v1.3 Changes due to new log-handling
 * @author Matthias Stöckli (v1.0)
 * @version 1.3 (Raphael Stoeckli, 24.02.2015)
 */
@XmlRootElement(name="Configuration")
public class ServerConfiguration {

    /**
     * The temporary logs directory after changing the actual directory.<br>
     * The location can only be changed with a restart of the server.<br>
     * For handling purpose the old value has to be preserved in this 
     * parameter until restart. Will not be marshalled
     * @since v1.2
     */
    @XmlTransient
    private String tempLogsDirectory;
    
    /**
     * The port the server will connect to.<br>
     * Default: 25123
     */
    private int portNumber;
    
    /**
     * Indicates whether the ConsoleMode is activated. Please note: At the moment
     * the console mode is only functional.<br>
     * Default: false
     */
    private boolean isConsoleMode;
    
    /**
     * Indicates whether the console logging should be printed on one line.
     * Default: true<br>
     */
    //private boolean isCompactLoggingEnabled;
    
    /**
     * The interval between {@link org.fhnw.aigs.commons.communication.KeepAliveMessage}s.<br>
     * Default: 10000
     */
    private int keepAliveTimeOut;
    
    /**
     * Indicates whether the KeepAliveManager is active or not.<br>
     * Default: false
     */
    private boolean useKeepAliveManager;
    
    /**
     * Indicates whether the file logging should be in the verbose XML format.<br>
     * Default: false
     */
    //private boolean isXMLlogging;
    
    /**
     * Indicates whether the server will minimize to a tray icon if the close 
     * button is pressed.<br>
     * Default: false
     */
    private boolean hidesOnClose;
    
    /**
     * Indicates whether one user can log in several times. In production mode
     * it is not recommended to set this to true.<br>
     * Default: false
     */
    private boolean isMultiLoginAllowed;

    
    /**
     * URL to a service which returns the server's external IP address (referer) as plain text.<br>
     * If the service is implemented autonomically, the php script is as follows:
     * &lt;?php echo $_SERVER['REMOTE_ADDR']; ?&gt;
     * Default: 
     */
    private String whatIsMyIpUrl;
       
    /**
     * Location where logfiles are stored
     * Default: ./logs
     * @since v1.1
     */    
    private String logDirectory;
   
    /**
     * Location where the game libaries (JAR) are stored
     * Default: ./gamelibs
     * @since v1.1
     */        
    private String gamelibsDirectory;
    
    /**
     * Location where the game sources are stored. This location is only used if games are recompiled during runtime
     * Default: ./games
     * @since v1.1
     */        
    private String gameSourcesDirectory;    
    
    /**
     * Indicates whether the credentials of the clients will be checked from the server.<br>
     * If true, no username or identification code will be checked. An anonymous user will be created ad-hoc at runtime and discarded after the game termination or stop of the server
     * @since v1.2
     */
    private boolean isAnonymousLoginAllowed;
    
    /**
     * Level of logging
     * @since v1.3
     */
    private LoggingThreshold loggerThreshold;
    
    /**
     * Style of logging
     * @since v1.3
     */    
    private LoggingStyle loggerStyle;
    
    /**
     * Number of lines in the GUI to log
     * @since v1.3
     */    
    private int linesToLog;
    
    
    /** The sole instance of the ServerConfiguration */
    private static ServerConfiguration instance;

    /**
     * Gets the unique instance of ServerConfiguration and will create a new if
     * it has not been created yet.
     *
     * @return The instance of ServerConfiguration
     */
    public static ServerConfiguration getInstance() {
        if (instance == null) {
            return instance = new ServerConfiguration();
        } else {
            return instance;
        }
    }
    
    
    /** See {@link ServerConfiguration#portNumber}. */
    @XmlElement(name = "PortNumber")
    public int getPortNumber() {
        return instance.portNumber;
    }

    /** See {@link ServerConfiguration#isConsoleMode}. */
    @XmlElement(name = "IsConsoleMode")
    public boolean getIsConsoleMode() {
        return instance.isConsoleMode;
    }

    /** See {@link ServerConfiguration#loggerThreshold}. */
    @XmlElement(name = "LoggerThreshold")
    public LoggingThreshold getLoggerThreshold() {
        return loggerThreshold;
    }
    
    /** See {@link ServerConfiguration#loggerStyle}. */
    @XmlElement(name = "LoggerStyle")
    public LoggingStyle getLoggerStyle() {
        return loggerStyle;
    }    
    
    /** See {@link ServerConfiguration#linesToLog}. */
    @XmlElement(name = "LinesToLog")
    public int getLinesToLog() {
        return linesToLog;
    }    
    
    /** See {@link ServerConfiguration#keepAliveTimeOut}. */
    @XmlElement(name = "KeepAliveTimeOut")
    public int getKeepAliveTimeOut() {
        return instance.keepAliveTimeOut;    
    }    
    
    /** See {@link ServerConfiguration#useKeepAliveManager}. */
    @XmlElement(name = "UseKeepAliveManager")
    public boolean getUseKeepAliveManager() {
        return useKeepAliveManager;
    }    
            
    /** See {@link ServerConfiguration#isMultiLoginAllowed}. */
    @XmlElement(name = "IsMultiLoginAllowed")
    public boolean getIsMultiLoginAllowed() {
        return isMultiLoginAllowed;
    }
    
    /** See {@link ServerConfiguration#hidesOnClose}. */
    @XmlElement(name = "HidesOnClose")
    public boolean getHidesOnClose(){
        return hidesOnClose;
    }
    
    /** See {@link ServerConfiguration#whatIsMyIpUrl}.*/
    @XmlElement(name = "WhatIsMyIpUrl")
    public String getWhatIsMyIpUrl() {
        return whatIsMyIpUrl;
    }
    
     /** See {@link ServerConfiguration#logDirectory}.*/
    @XmlElement(name = "LogDirectory")   
    public String getLogDirectory() {
        return logDirectory;
    }

    /** See {@link ServerConfiguration#gamelibsDirectory}.*/
    @XmlElement(name = "GamelibsDirectory")   
    public String getGamelibsDirectory() {
        return gamelibsDirectory;
    }       
    
    /** See {@link ServerConfiguration#gameSourcesDirectory}.*/
    @XmlElement(name = "GameSourcesDirectory")   
    public String getGameSourcesDirectory() {
        return gameSourcesDirectory;
    }           
    
    /** See {@link ServerConfiguration#isAnonymousLoginAllowed}. */
    @XmlElement(name = "IsAnonymousLoginAllowed")
    public boolean getIsAnonymousLoginAllowed(){
        return isAnonymousLoginAllowed;
    }

    /** See {@link ServerConfiguration#portNumber}. */
    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    /** See {@link ServerConfiguration#isConsoleMode}. */
    public void setIsConsoleMode(boolean isConsoleMode) {
        this.isConsoleMode = isConsoleMode;
    }
    
    
    /** See {@link ServerConfiguration#keepAliveTimeOut}. */
    public void setKeepAliveTimeOut(int keepAliveTimeOut) {
        this.keepAliveTimeOut = keepAliveTimeOut;
    }

    /** See {@link ServerConfiguration#useKeepAliveManager}. */
    public void setUseKeepAliveManager(boolean useKeepAliveManager) {
        this.useKeepAliveManager = useKeepAliveManager;
    }
    
    /** See {@link ServerConfiguration#isMultiLoginAllowed}. */
    public void setIsMultiLoginAllowed(boolean isMultiLoginAllowed){
        this.isMultiLoginAllowed = isMultiLoginAllowed;
    }
    
    /** See {@link ServerConfiguration#loggerThreshold}. */
    public void setLoggerThreshold(LoggingThreshold loggerThreshold) {
        this.loggerThreshold = loggerThreshold;
    }

    /** See {@link ServerConfiguration#loggerStyle}. */
    public void setLoggerStyle(LoggingStyle loggerStyle) {
        this.loggerStyle = loggerStyle;
    }    
    
    /** See {@link ServerConfiguration#linesToLog}. */
    public void setLinesToLog(int linesToLog) {
        this.linesToLog = linesToLog;
    }    
    
    /** See {@link ServerConfiguration#hidesOnClose}. */
    public void setHidesOnClose(boolean hidesOnClose){
        this.hidesOnClose = hidesOnClose;
    }
    
    /** See {@link ServerConfiguration#whatIsMyIpUrl}. */
    public void setWhatIsMyIpUrl(String whatIsMyIpUrl){
        this.whatIsMyIpUrl = whatIsMyIpUrl;
    }     

    /** See {@link ServerConfiguration#logDirectory}. */
    public void setLogDirectory(String logDirectory) {
        this.logDirectory = logDirectory;
    }

    /** See {@link ServerConfiguration#gamelibsDirectory}. */    
    public void setGamelibsDirectory(String gamelibsDirectory) {
        this.gamelibsDirectory = gamelibsDirectory;
    }
    
    /** See {@link ServerConfiguration#gameSourcesDirectory}. */    
    public void setGameSourcesDirectory(String gameSourcesDirectory) {
        this.gameSourcesDirectory = gameSourcesDirectory;
    }    
    
    /** See {@link ServerConfiguration#isAnonymousLoginAllowed}. */
    public void setIsAnonymousLoginAllowed(boolean isAnonymousLoginAllowed)
    {
        this.isAnonymousLoginAllowed = isAnonymousLoginAllowed;
    }
   
    /** See {@link ServerConfiguration#tempLogsDirectory}. */
    @XmlTransient
    public String getTempLogsDirectory() {
        return tempLogsDirectory;
    }

    /** See {@link ServerConfiguration#tempLogsDirectory}. */
    public void setTempLogsDirectory(String tempLogsDirectory) {
        this.tempLogsDirectory = tempLogsDirectory;
    }     
    
    /** Creates the Singleton instance */
    static void initialize() {
        instance = new ServerConfiguration();
        instance.readConfiguration();
    }

    /** Private constructor to prevent direct instantiation */
    private ServerConfiguration() {
        this.tempLogsDirectory = "";    // Must be empty at starup. Only used if directory changed
    }

    /**
     * This method reads the server configuration. By default it will load the
     * file 'conf/ServerConfig.xml'. This file is directly dependent on the
     * "ServerConfiguration" class. If the file does not exist, a new file will
     * be created and hard coded default values will be used.
     */
    private void readConfiguration() {
        // Load the configuration file and check for the existance
        File configFile = new File("conf/ServerConfig.xml");
        if (configFile.exists()) {
            // Unmarshall/parse  the configuration file's content directly using JAXB
            instance = (ServerConfiguration) JAXB.unmarshal(configFile, ServerConfiguration.class);
            //LOG//Logger.getLogger(Main.class.getName()).log(Level.INFO, "Configuration file read.");
            LogRouter.log(ServerConfiguration.class.getName(), LoggingLevel.system, "Configuration file read.");
        } else {
            //If the configuration file does not exist,
            // standard, hard coded settings will be used and a new settings file will be created.
            ServerConfiguration.createNewConfiguration();
            //LOG//Logger.getLogger(Main.class.getName()).log(Level.INFO, "Configuration file could not be read, using default values instead.");
            LogRouter.log(ServerConfiguration.class.getName(), LoggingLevel.system, "Configuration file could not be read, using default values instead.");
        }
    }
    
    /** Creates a new ServerConfig.xml and writes it to the file system  */
    private static void createNewConfiguration() {
        // This preferences are hard coded values.
        // They will be written to the "ServerConfig.xml" after running this method.
        //\\***************************************//\\
        int newPortNumber = 25123;
        boolean newIsConsoleMode = false;
        int newKeepAliveTimeOut = 10000;
        boolean newUseKeepAliveManager = false;
        boolean newMultiLoginAllowed = false;
        boolean newHidesOnClose = false;
        boolean anonymousLoginAllowed = true;
        String newWhatIsMyIpUrl = "http://icanhazip.com/"; // Alter default value if php script migrated to fhnw doamin (or similar)
        String newLogDirectory = "./logs";
        String newGamelibsDirectory = "./gamelibs";
        String newGameSourcesDirectory = "./games";
        //\\***************************************//\\


        instance = getInstance();
        instance.portNumber = newPortNumber;
        instance.isConsoleMode = newIsConsoleMode;
        instance.keepAliveTimeOut = newKeepAliveTimeOut;
        instance.useKeepAliveManager = newUseKeepAliveManager;
        instance.isMultiLoginAllowed = newMultiLoginAllowed;
        instance.hidesOnClose = newHidesOnClose;
        instance.whatIsMyIpUrl = newWhatIsMyIpUrl;
        instance.logDirectory = newLogDirectory;
        instance.gamelibsDirectory = newGamelibsDirectory;
        instance.gameSourcesDirectory = newGameSourcesDirectory;
        instance.isAnonymousLoginAllowed = anonymousLoginAllowed;
        instance.loggerThreshold = LoggingThreshold.severeSystemGame;
        instance.loggerStyle = LoggingStyle.compressed;
        instance.linesToLog = 500;
        instance.tempLogsDirectory = ""; // Must be empty at starup. Only used if directory changed
        
        saveConfiguration(instance, "conf", "ServerConfig.xml");
    }

    /**
     * Saves the defined configuration
     * @param config Configuration to save
     * @param folder Folder where configuration is located. Do not add a trailing slash (/ or \)
     * @param file File to save the configuration in
     * @since v1.2
     */
    private static void saveConfiguration(ServerConfiguration config, String folder, String file)
    {
        File outputFile = new File(folder + "/" + file);
        if (outputFile.exists() == true) {
            outputFile.delete();
        }
        try {
            new File(folder).mkdir();
            outputFile.createNewFile();
            //LOG//Logger.getLogger(ServerConfiguration.class.getName()).log(Level.INFO, "Created a new configuration file.");
            LogRouter.log(ServerConfiguration.class.getName(), LoggingLevel.system, "Created a new configuration file.");
            JAXB.marshal(config, outputFile);
        } catch (IOException ex) {
            //LOG//Logger.getLogger(ServerConfiguration.class.getName()).log(Level.SEVERE, "Could not create new configuration file.", ex);
            LogRouter.log(ServerConfiguration.class.getName(), LoggingLevel.severe, "Could not create new configuration file.", ex);
        }
        config.printConfiguration();      
    }
    
    /**
     * Saves the current singleton instance of the server configuration
     * @since v1.2
     */
    public static void saveInstance()
    {
        saveConfiguration(ServerConfiguration.instance,"conf", "ServerConfig.xml");
    }

    /**
     * Logs the current ServerConfiguration.
     */
    public void printConfiguration() {
        Object[] configurationItems = new Object[]{
            portNumber,
            isConsoleMode,
            keepAliveTimeOut,
            useKeepAliveManager,
            isMultiLoginAllowed,
            isAnonymousLoginAllowed,
            hidesOnClose,
            whatIsMyIpUrl,
            logDirectory,
            gamelibsDirectory,
            gameSourcesDirectory,
            loggerThreshold,
            loggerStyle,
            linesToLog
       };

        //LOG//    
        String text = "The configuration is as follows:\n"
                    + "Port: {0} \n"
                    + "IsConsoleMode: {1}\n"
                    + "KeepAliveTimeOut: {2}\n"
                    + "UseKeepAliveManager: {3}\n"
                    + "IsMultiLoginAllowed: {4}\n"   
                    + "IsAnonymousLoginAllowed: {5}\n" 
                    + "HidesOnClose: {6}\n"
                    + "WhatIsMyIpUrl: {7}\n"
                    + "LogDirecory: {8}\n"
                    + "GamelibsDirecory: {9}\n"
                    + "GameSourcesDirecory: {10}\n"
                    + "loggerThreshold: {11}\n"
                    + "loggerStyle: {12}\n"
                    + "linesToLog: {13}\n";
        LogRouter.log(ServerConfiguration.class.getName(), LoggingLevel.system, text, configurationItems);
        
    }
    /**
     * This main method allows for the direct creation of a new configuration
     * file It will simply invoke "createNewConfiguration". parameters can be
     * changed
     */
    public static void main(String[] args) {
        createNewConfiguration();
    }

    
}
