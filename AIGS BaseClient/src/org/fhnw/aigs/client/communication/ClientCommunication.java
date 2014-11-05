package org.fhnw.aigs.client.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fhnw.aigs.client.GUI.SettingsWindow;
import org.fhnw.aigs.client.gameHandling.ClientGame;
import org.fhnw.aigs.commons.communication.IdentificationMessage;
import org.fhnw.aigs.commons.communication.IdentificationResponseMessage;

/**
 * This class is responsible for everything related to the network communication
 * to the server. Due to the fact that the class uses the Singleton Pattern, it
 * is not possible to instantiate ClientCommunication directly. In order to get
 * to an instance, use <b>getInstance()</b> instead.<br>
 * v1.0 Initial release<br>
 * v1.1 Functional changes<br>
 * v1.1.1 Minor changes due to changes in other clssses (dependencies)
 * 
 * @author Matthias Stöckli (v1.0)
 * @version v1.1.1 (Raphael Stoeckli, 22.10.2014)
 */
public class ClientCommunication implements Runnable {

    /**
     * The port number which is used for communication with the server. It must
     * be an integer between 0 and 65535.
     */
    private int port;
    /**
     * The host address. This is usually the external IP address of the server
     * or, if developping locally, "localhost".
     */
    private String host;
    /**
     * The Socket, i.e. the connection to the server.
     */
    private Socket socket;
    /**
     * A flag which indicates whether a connection has been established.
     */
    public static boolean isConnected;
    
    /**
     * A flag which indicates whether the client communication has been set
     * using the online configuration file. See
     * {@link ClientCommunication#setCredentialsUsingOnlineConfiguration}.
     */
    //public static boolean hasReadFromFile;
    
    /**
     * The client game which will be used to initialize the
     * {@link ClientMessageBroker}.
     */
    private ClientGame clientGame;

    /**
     * Private constructor to prevent instantiation
     */
    private ClientCommunication() {
    }

    /**
     * Gets the only instance of ClientCommunication. Synchronized indicates
     * that this Singleton is thread-safe.
     *
     * @return The ServerCommunication instance
     */
    public static synchronized ClientCommunication getInstance() {
        return ClientCommunication.ClientCommunicationHolder.INSTANCE;
    }

    /**
     * Provides a container for the ServerCommunication singleton
     */
    private static final class ClientCommunicationHolder {

        private static final ClientCommunication INSTANCE = new ClientCommunication();
    }

    /**
     * See {@link ClientCommunication#port}.
     */
    public int getPort() {
        return port;
    }

    /**
     * See {@link ClientCommunication#host}.
     */
    public String getHost() {
        return host;
    }

    /**
     * See {@link ClientCommunication#clientGame}.
     */
    public ClientGame getClientGame() {
        return clientGame;
    }

    /**
     * See {@link ClientCommunication#socket}
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * See {@link ClientCommunication#port}.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * See {@link ClientCommunication#host}.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * See {@link ClientCommunication#clientGame}.
     */
    public void setClientGame(ClientGame clientGame) {
        this.clientGame = clientGame;
    }

    /**
     * Usually there is no need to use this method as the connection process
     * takes place automatically. See {@link ClientCommunication#socket}.
     */
    public void setClientGame(Socket socket) {
        this.socket = socket;
    }

    /**
     * This method must be called prior to connection to the server.<br>
     * It sets the clientGame, the host (IP address or localhost) and the port
     * (e.g. 25123).
     *
     * @param clientGame The clientGame.
     * @param host The host to which the client will establish a connection. It
     * can either be a valid IP addreess or localhost
     * @param port A valid port number between
     */
    public static void setCredentials(ClientGame clientGame, String host, int port) {
        ClientCommunication instance = getInstance();
        instance.setPort(port);
        instance.setHost(host);
        instance.setClientGame(clientGame);
    }

    /**
     * This is an alternative for the {@link ClientCommunication#setCredentials(org.fhnw.aigs.client.gameHandling.ClientGame, java.lang.String, int) }
     * This method downloads the credentials (port and address) from a
     * predefined, permanent url. This should make sure that the port and host
     * are always up to date
     * @deprecated Don't use this method in the future, respectively make it configurable
     * @param clientGame The ClientGame instance
     */
    public static void setCredentialsUsingOnlineConfiguration(ClientGame clientGame) {
        // CHANGE TO THE ACTUAL LOCATION OF THE FILE CONTAINING THE STANDARD CREDENTIALS.
        String urlString = "http://www.poebel.ch/aigs/aigs.txt";
        ClientCommunication instance = getInstance();
        instance.setClientGame(clientGame);

        try {
            URL url = new URL(urlString);
            Logger
                    .getLogger(ClientCommunication.class
                    .getName()).log(Level.INFO, "Downloading settings from URL:{0}", urlString);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

            // Check for correct connection: In some cases only limited connection is available
            // e.g. when not properly logged in into the FHNW network
            instance.setHost(reader.readLine());
            instance.setPort(Integer.parseInt(reader.readLine()));
            Logger.getLogger(ClientCommunication.class.getName()).log(Level.INFO, "Loaded the following settings: {0}:{1}", new Object[]{instance.getHost(), instance.getPort()});

        } catch (MalformedURLException ex) {
            Logger.getLogger(ClientCommunication.class.getName()).log(Level.SEVERE, "URL is malformed.", ex);
        } catch (IOException ex) {
            // If the settings could not be read, use localhost and a standard port instead.
            Logger.getLogger(ClientCommunication.class.getName()).log(Level.SEVERE, "Could not load settings from " + urlString + ""
                    + "Please check your internet connection. Meanwhile localhost and the port 25123 will be used.", ex);
            instance.setPort(25123);
            instance.setHost("localhost");
        }
        catch (Exception ex) // All orher exceptions
        {
            Logger.getLogger(ClientCommunication.class.getName()).log(Level.SEVERE, "An unknown Error occurred.", ex);
        }

    }

    /**
     * Starts the method {@link ClientCommunication#establishConnection}.
     */
    @Override
    public void run() {
        establishConnection();
    }

    /**
     * Establishes a connection to the server. A {@link Socket} is created in
     * order to connect to the server. If this is not possible, the client waits
     * for 5 seconds and retries again. As soon as the connection has been
     * successfully established, a new message loop will be started.
     *
     */
    private void establishConnection() {       
        do {
            try {
                // Open a new socket to the specified host/port.
                socket = new Socket(host, port);
                isConnected = true;
                Logger.getLogger(ClientCommunication.class.getName()).log(Level.INFO, "Connection to server established!");

                // Create a new ClientMessageBroker which is responsible for
                // all messaging-related routig and parsing. Create a thread and
                // start it. 
                ClientMessageBroker clientMessageBroker = new ClientMessageBroker(socket, clientGame);
                Thread clientMessageBrokerThread = new Thread(clientMessageBroker);
                clientMessageBrokerThread.setName("ClientMessageBrokerThread");
                clientMessageBrokerThread.start();

                // Check the user's identity.
                checkIdentity();

            } catch (UnknownHostException ex) {
                Logger.getLogger(ClientCommunication.class
                        .getName()).log(Level.SEVERE, "Do not know about host: {0}", host);
                System.err.println(
                        "Retrying in 5 Seconds...");
            } // This exception is very common. It means that no connection could
            // be established. In most cases this simply means that the server
            // is not running.
            catch (IOException ex) {
                Logger.getLogger(ClientCommunication.class
                        .getName()).log(Level.SEVERE, "Could not get I/O: {0}: {1}", new Object[]{host, port});
                System.err.println(
                        "Retrying in 5 Seconds...");
            }

            // Wait 5 seconds (5000 ms) and then try to reconnect.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ClientCommunication.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } while (socket == null && isConnected == false);

    }

    /**
     * This method looks whether an instance of the Setting class existing.
     * If not available, the settings window will be showed.<br>
     * The user name and identifivcation stored in the settings will be
     * sent as an identification to the server
     * via a {@link IdentificationMessage}.<br>
     * If the name and the identification code match and the user is currently
     * not logged in the system, the identification is successful. The server
     * will send an {@link IdentificationResponseMessage} either way, informing
     * the client about success of failure of the log-in attempt. <br>
     * If the log in failed, the {@link SettingsWindow} will be shown to the
     * user.
     */  
    private void checkIdentity() 
    {
        if (Settings.getInstance().isInitialized() == false)
        {
            Settings.tryLoadSettings(true); // Opens the settings window if no settings available
        }
            // Sends an identification to the Server over the new connection
            IdentificationMessage identificationMessage = new IdentificationMessage(Settings.getInstance().getUsername(), Settings.getInstance().getPassword(),Settings.getInstance().getDisplayname());
            
            clientGame.sendMessageToServer(identificationMessage);
            Logger.getLogger(ClientCommunication.class.getName()).log(Level.INFO, "Sent identification!");        

    }
}
