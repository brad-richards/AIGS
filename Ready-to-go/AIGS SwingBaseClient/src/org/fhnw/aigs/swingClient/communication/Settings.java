package org.fhnw.aigs.swingClient.communication;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.fhnw.aigs.swingClient.GUI.SettingsWindow;

/**
 * Singleton Class for user settings<br>
 * v1.0 Initial release<br>
 * v1.1 Added some fileds
 * @author Raphael Stoeckli
 * @version v1.1 (14.10.2014)
 */
public class Settings {
    
    /**
     * The default settings file of the client (path)
     */
    private static final String SETTINGS_FILE = "./ClientConfig.xml";
    
    private static Settings settings;
    
    private String displayname;
    
    private String username;
    
    private String password;
    
    private String serverAddress;
    
    private int serverPort;
    
    private boolean useLogin;
    
    private boolean autoConnect;
    
    private boolean showSettingsAtStartup;

    
    private boolean initialized;
    
    private boolean gameIsRunning;
    
    /**
     * Gets the display name of the player
     * @return display name
     */
    public String getDisplayname() {
        return displayname;
    }    
    
    /**
     * Sets the display name of the player
     * @param displayname  display name
     */
    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }    
    
    /**
     * Gets the user name
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user name
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password (if using login)
     * @return Password
     */
    public String getPassword() {
        return password;
    }

        /**
     * Sets the password (if using login)
     * @param password Password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the server address or IP
     * @return server address or IP
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Sets the server address or IP
     * @param serverAddress server address or IP
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    /**
     * Gets the server port
     * @return server port
     */
    public int getServerPort() {
        return serverPort;
    }
    
    /**
     * Gets the string of the server port
     * @return server port
     */
    public String getServerPortString()
    {
        return Integer.toString(serverPort);
    }
    
    /**
     * Gets the state whether to use login or not
     * @return True, if no login is used (anonymous), otherwise false (login name and password required)
     */
      public boolean isUsingLogin() {
        return useLogin;
    }

      /**
       * Sets the state whether to use login or not
       * @param useLogin True, if no login is used (anonymous), otherwise false (login name and password required)
       */
    public void setUsingLogin(boolean useLogin) {
        this.useLogin = useLogin;
    }  
    
    /**
     * Gets the state whether to connect automatically to a waiting game at startup or to ask about connection
     * @return True, if the client is automatically joining a game or creating a new one, if none available
     */
    public boolean getAutoConnect() {
        return autoConnect;
    }

    /**
     * Sets the state whether to connect automatically to a waiting game at startup or to ask about connection
     * @param autoConnect True, if the client is automatically joining a game or creating a new one, if none available
     */
    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    } 
    
    /**
     * Sets the state whether to show the settings dialog on every startup or not
     * @return True if the dialog is showing up every startup
     */
    public boolean getShowSettingsAtStartup() {
        return showSettingsAtStartup;
    }

    /**
     * Sets the state whether to show the settings dialog on every startup or not
     * @param showSettingsAtStartup True if the dialog is showing up every startup
     */
    public void setShowSettingsAtStartup(boolean showSettingsAtStartup) {
        this.showSettingsAtStartup = showSettingsAtStartup;
    }
    
    /**
     * Gets the temporal running state of the game. 
     * This value is to globally determine the running state of a game without access to a clientGame object
     * @return True if the game is currently running, otherwise false
     */
    public boolean isGameRunning() {
        return gameIsRunning;
    }
 
    /**
     * Gets the state of the Settings object
     * @return True, if the the oject was filled with data, otherwise false
     */
    public boolean isInitialized()
    {
        return initialized;
    }

    /**
     * Sets the server port
     * @param serverPort server port
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
    
    /**
     * Returns the instance of the object. If not defined, a new instance will be created
     * @return singleton instance
     */
    public static Settings getInstance()
    {
        if (settings == null)
        {
            settings = new Settings();
            settings.setAutoConnect(false);
            settings.setShowSettingsAtStartup(false);
            settings.setUsingLogin(false);
            settings.setServerAddress("localhost");
            settings.setServerPort(25123);
            settings.setUsername("User");
            settings.setDisplayname("Player");
            settings.setPassword("---");
            settings.initialized = false;
            settings.gameIsRunning = false;
        }
        return settings;
    }
    
    /**
     * Sets the parameter {@link Settings#gameIsRunning} to true.
     * This not realized as regualar getter and setter to avoid serialization
     */
    public void SetGameRunning()
    {
        this.gameIsRunning = true;
    }
    
    /**
     * Sets the parameter {@link Settings#gameIsRunning} to false.
     * This not realized as regualar getter and setter to avoid serialization
     */    
    public void SetGameStop()
    {
        this.gameIsRunning = false;
    }
    
    /**
     * Fills all information of the instance. If instance is not defined yet, a new instance will be created
     * @param playername Display name of the player
     * @param username User name
     * @param identificationCode Password
     * @param address server address oder IP
     * @param port server port
     * @param useLogin state whether to use a login or not
     */
    public static void writeInstance(String playername, String username, String identificationCode, String address, int port, boolean useLogin, boolean autoConnect, boolean showSettings)
    {
          if (settings == null)
        {
            settings = new Settings();
        }
          settings.displayname = playername;
          settings.username = username;
          settings.password = identificationCode;
          settings.serverAddress = address;
          settings.serverPort = port;
          settings.useLogin = useLogin;
          settings.autoConnect = autoConnect;
          settings.showSettingsAtStartup = showSettings;
          settings.initialized = true;          
    }
    
    /**
     * Tries to load the settings file
     * @param showDialog If false, opeing of the settings window will be suppressed even if no settings file exists 
     * @return true if settings could be loaded, otherwise false
     */
    public static boolean tryLoadSettings(boolean showDialog)
    {
        Settings s = deserialize(SETTINGS_FILE);
        boolean state = false;
        if (s != null)
        {
            settings = s; // Overwrite config
            state = true;
        }
        else
        {
           s = getInstance(); // Force creation of instance before settings window
        }
        if (showDialog == true)
        {
            if (s.getShowSettingsAtStartup() == true || state == false)
            {
                    SettingsWindow settingsWindow = new SettingsWindow();
                    settingsWindow.setVisible(true);          
            }
        }
        return state;
    }
    
    /**
     * Saves the settings of the current instance
     * @return true if file could be created, otherwise false
     */
    public boolean saveSettings()
    {
        return serialize(SETTINGS_FILE);
    }
    
    /**
     * Serializes the current setting object
     * @param filename filename of the settings file
     * @return true if file could be created, otherwise false
     */
    private boolean serialize (String filename)
    {
            try {
                    XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new
                    FileOutputStream(filename)));
                    encoder.writeObject(this);
                    encoder.close();
                    return true;
                    }	    	
        catch (Exception e) { 
            return false; 
            }
    } 
    
    /**
     * Deserializes a settings file
     * @param filename filename of the settings file 
     * @return true if file could be loaded, otherwise false
     */
    private static Settings deserialize (String filename)
    {
            File file = new File (filename);

            if (file.exists() == false){
                    return null;
            }

            else {
                    try {
                            XMLDecoder encoder = new XMLDecoder(new BufferedInputStream(new
                            FileInputStream(filename)));
                            Settings object = (Settings) encoder.readObject();
                            encoder.close();
                            object.initialized = true;
                            return object;
                            }
                    catch (FileNotFoundException e) {
                            return null;				
                    }
            }
    }    
}

