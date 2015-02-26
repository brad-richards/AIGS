package org.fhnw.aigs.server.communication;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JOptionPane;
import org.fhnw.aigs.server.common.LogRouter;
import org.fhnw.aigs.server.common.LoggingLevel;
import org.fhnw.aigs.server.common.ServerConfiguration;

/**
 * This class is responsible for the outgoing connections. It connects the
 * server to the clients. Due to the fact that the class uses the Singleton
 * Pattern, it is not possible to instantiate ClientCommunication directly. In
 * order to get to an instance, use {@link ServerCommunication#getInstance}
 * instead.<br>
 * v1.0 Initial release<br>
 * v1.1 Functional changes<br>
 * v1.2 Changing of logging
 * @version 1.2 (Raphael Stoeckli, 24.02.2015)
 * @author Matthias Stöckli (v1.0)
 */
public class ServerCommunication implements Runnable {
    
    
    /**
     * The running state of the current instance
     */
    private boolean runState;

    /**
     * Private constructor to prevent instantiation
     */
    private ServerCommunication() {
    }

    /**
     * Gets the only instance of ClientCommunication. Synchronized indicates
     * that this Singleton is thread-safe.
     *
     * @return The ServerCommunication instance
     */
    public static synchronized ServerCommunication getInstance() {
        return ServerCommunication.ServerCommunicationHolder.INSTANCE;
    }
    
    /**
     * Gets the running state of the current instance
     * @return runstate
     * @since v1.1
     */
    public boolean getRunState()
    {
        return runState;
    }

    /**
     * Provides a container for the ServerCommunication singleton
     */
    private static final class ServerCommunicationHolder {

        private static final ServerCommunication INSTANCE = new ServerCommunication();
    }
    /**
     * The server socket which allows the server to connect to clients
     */
    private static ServerSocket serverSocket = null;
    /**
     * The client (sockets) which are registered.
     */
    private static ArrayList<Socket> clientSockets = new ArrayList<Socket>();

    @Override
    public void run() {
        runState = true;
        establishConnection();
    }
    
    /**
     * Stops the server and closes all connections
     * @since v1.1
     */
    public void stop()
    {
        runState = false; // While-Loop will end
        cleanupThreads(true); // Stop all and clean up
        try
        {
        serverSocket.close();
        }
        catch (IOException ex)
        {
            //LOG//Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.INFO, "Could not close server socket", ex);
            LogRouter.log(ServerMessageBroker.class.getName(), LoggingLevel.waring, "Could not close server socket", ex);
        }
    }
    
    /**
     * Method to cleaning up the list of threads. Only closed clientSocket connections will be removed
     * @param stopAll Stopps all active clientSocket connections if true
     * @since v1.1
     */
    private static void cleanupThreads(boolean stopAll)
    {
        Iterator<Socket> i = clientSockets.iterator();
        Socket s;
        while(i.hasNext())
        {
            s = i.next();
            if (stopAll == true)
            {
                try
                {
                    s.close();
                }
                catch(IOException ex)
                {
                    //LOG//Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.INFO, "Could not close client socket", ex);
                    LogRouter.log(ServerMessageBroker.class.getName(), LoggingLevel.waring, "Could not close client socket", ex);
                }
            }
            if (s.isClosed() == true)
            {
                i.remove();
            }
        }
    }

   

    /**
     * This method will open new client sockets. It will start new
     * ServerMessageBroker Threads for every new connection that has
     * successfully been established. This method will run endlessly.
     */
    private void establishConnection() {
        cleanupThreads(true); // Stop all and clean up
        try {
            while (runState) {
                //Accept new client sockets
                Socket clientSocket = ServerCommunication.serverSocket.accept();
                clientSockets.add(clientSocket);
                cleanupThreads(false); // Only clean up closed connections
                //LOG//Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.INFO, "New connection established! Address: {0}", clientSocket.getInetAddress());
                LogRouter.log(ServerMessageBroker.class.getName(), LoggingLevel.info, "New connection established! Address: {0}", clientSocket.getInetAddress());

                // Create new Thread of ServerMessageBroker for new connection and start the thread
                Thread serverMessageBrokerThread = new Thread(new ServerMessageBroker(clientSocket));
                serverMessageBrokerThread.setName("ServerMessageBrokerThread" + clientSocket.getInetAddress().toString());
                serverMessageBrokerThread.start();
            }

        } catch (IOException ex) {
            if (runState) // Error while running
            {
            //LOG//Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.SEVERE, "Could not establish connection", ex);
            LogRouter.log(ServerMessageBroker.class.getName(), LoggingLevel.severe, "Could not establish connection", ex);
            }
            else // Connection closed
            {
             //LOG//Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.INFO, "Connection was interrupted due to shutdown", ex);   
             LogRouter.log(ServerMessageBroker.class.getName(), LoggingLevel.waring, "Connection was interrupted due to shutdown", ex);   
            }
        }
        catch (Exception ex) // All other Exceptions
        {
            //LOG//Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.SEVERE, "An unknown exception occurred while establishing connection", ex);
            LogRouter.log(ServerMessageBroker.class.getName(), LoggingLevel.severe, "An unknown exception occurred while establishing connection", ex);
        }
    }

    /**
     * Connects to the server using a {@link ServerSocket}.
     */
    public static void setUpServerSocket() {
        boolean serverSetupSuccessful = false;
        int port = ServerConfiguration.getInstance().getPortNumber();

        do {
            try {
                //LOG//Logger.getLogger(ServerCommunication.class.getName()).log(Level.INFO, "Try to connect to port {0}", port);
                LogRouter.log(ServerMessageBroker.class.getName(), LoggingLevel.info, "Try to connect to port {0}", port);
                serverSocket = new ServerSocket(port);                          // Tries to establish a connection on the standard port
                serverSetupSuccessful = true;
                continue;
            } catch (IOException ex) {
                //LOG//Logger.getLogger(ServerCommunication.class.getName()).log(Level.SEVERE, "Could not listen to port " + port + ", server will shut down.", ex);
                LogRouter.log(ServerMessageBroker.class.getName(), LoggingLevel.severe, "Could not listen to port " + port + ", server will shut down.", ex);
                JOptionPane.showMessageDialog(null, "Could not connect. Another instance of the AIGS seems to be running.", "There is a problem.", JOptionPane.ERROR_MESSAGE);

                System.exit(1);
            }
            catch (Exception ex) // All other exceptions
            {
                //LOG//Logger.getLogger(ServerCommunication.class.getName()).log(Level.SEVERE, "An unknown Error occurred. Server will shut down.", ex);
                LogRouter.log(ServerMessageBroker.class.getName(), LoggingLevel.severe, "An unknown Error occurred. Server will shut down.", ex);
                JOptionPane.showMessageDialog(null, "An unknown Error occurred", "There is a problem.", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            // Allows the user to add corrections via the command line
            Scanner scanner = new Scanner(System.in);                           // Object for obtaining a console input.
            String input = "";                                                  // Value of the console input

            // Regular expression to check for the correct format and port range (whole numbers from 0 to 65535)
            String portRegex = "^0*(?:6553[0-5]|655[0-2][0-9]|65[0-4][0-9]{2}|6[0-4][0-9]{3}|[1-5][0-9]{4}|[1-9][0-9]{1,3}|[0-9])$";

            // As long as the input does not match the port regex and the user did not type "exit", an input loop will run
            while (input.matches(portRegex) == false || input.equals("exit")) {
                input = scanner.next();                                         // Get the string input from console
                if (input.equals("exit")) {                                     // Check for the keyword "exit"
                    //LOG//Logger.getLogger(ServerCommunication.class.getName()).log(Level.INFO, "Shut down server...");
                    LogRouter.log(ServerMessageBroker.class.getName(), LoggingLevel.system, "Shut down server...");
                    System.exit(0);                                             // ShutdownCleanUp will take care of the rest

                    // If the input matches the port regex, the server tries to establish a connection under the new port number
                } else if (input.matches(portRegex)) {
                    port = Integer.parseInt(input);
                    break;
                } else {
                    //LOG//Logger.getLogger(ServerCommunication.class.getName()).log(Level.INFO, "Invalid port number '{0}'. Please enter a number between 0 and 65535", port);
                    LogRouter.log(ServerMessageBroker.class.getName(), LoggingLevel.waring, "Invalid port number '{0}'. Please enter a number between 0 and 65535", port);
                }
            }
        } while (serverSetupSuccessful == false);
        //LOG//Logger.getLogger(ServerCommunication.class.getName()).log(Level.INFO, "Connected successfully to port {0}, start listening...\nExternal IP: " + getExternalIp(), port);
        LogRouter.log(ServerMessageBroker.class.getName(), LoggingLevel.info, "Connected successfully to port {0}, start listening...\nExternal IP: " + getExternalIp(), port);
    }

    /**
     * A helper method to find out the external IP by using an external PHP
     * script. The script will reply the server's IP address in plain text.<br>
     * The script is as follows:<br>
     * <code>&lt;?php echo $_SERVER['REMOTE_ADDR']; ?&gt;</code>
     *
     * @return The server's external IP.
     */
    public static String getExternalIp() {
        try {
            URL whatIsMyIPURL = new URL(ServerConfiguration.getInstance().getWhatIsMyIpUrl());
            BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIPURL.openStream(), "UTF-8"));
            return in.readLine();
        } catch (Exception ex) {
            return "unknown";
        }
    }
}
