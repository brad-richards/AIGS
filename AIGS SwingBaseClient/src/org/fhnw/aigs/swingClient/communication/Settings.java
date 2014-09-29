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
 * Singleton Class for user settings
 * @author Raphael Stoeckli
 */
public class Settings {
    
    private static final String SETTINGS_FILE = "./ClientConfig.xml";
    
    private static Settings settings;
    
    private String username;
    
    private String identificationCode;
    
    private String serverAddress;
    
    private int serverPort;
    
    private boolean initialized;

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
     * Gets the identification code
     * @return identification code
     */
    public String getIdentificationCode() {
        return identificationCode;
    }

        /**
     * Sets the identification code
     * @param identificationCode identification code
     */
    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
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
            settings.initialized = false;
        }
        return settings;
    }
    
    /**
     * Fills all information of the instance. If instance is not defined yet, a new instance will be created
     * @param username user name
     * @param identificationCode Identification code
     * @param address server address oder IP
     * @param port server port
     */
    public static void writeInstance(String username, String identificationCode, String address, int port)
    {
          if (settings == null)
        {
            settings = new Settings();
        }
          settings.username = username;
          settings.identificationCode = identificationCode;
          settings.serverAddress = address;
          settings.serverPort = port;
          settings.initialized = true;
    }
    
    
    /**
     * Tries to load the settings file
     * @param showDialog if true, a new the settings window will be showed if no settings found
     * @return true if settings could be loaded, otherwise false
     */
    public static boolean tryLoadSettings(boolean showDialog)
    {
        Settings s = deserialize(SETTINGS_FILE);
        if (s != null)
        {
            settings = s; // Overwrite config
            return true;
        }
        else
        {
            if (showDialog == true)
            {
                getInstance(); // Force creation of instance before settings window
                SettingsWindow settingsWindow = new SettingsWindow();
                settingsWindow.setVisible(true);
                        
               // getInstance().serialize(SETTINGS_FILE);
            }
            return false;
        }
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
