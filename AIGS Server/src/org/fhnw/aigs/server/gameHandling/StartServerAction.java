package org.fhnw.aigs.server.gameHandling;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import org.fhnw.aigs.server.communication.ServerCommunication;
import org.fhnw.aigs.server.gui.ServerGUI;


    /**
     * Responsible for starting the server.
     * This class will be used by the server GUI if one clicks on the button
     * "start server", if the Console Mode is active, this action will be called
     * automatically.
     * 
    * @author Matthias Stöckli
     */
    public class StartServerAction implements ActionListener{
    private JLabel statusLabel;
    private JButton startButton;
    private JButton stopButton;
        
        /**
         * Empty constructor, used when in console mode
         */
        public StartServerAction(){
            
        }
        
        /**
         * Creates an instance of StartServerAction and let the status label
         * and the start/stop buttons reflect that the server started.
         * @param statusLabel The server status label ("online", "offline").
         * @param startButton The button which is used to start the server.
         * @param stopButton The button which is used to stop the server.
         */
        public StartServerAction(JLabel statusLabel, JButton startButton, JButton stopButton){
            this.statusLabel = statusLabel;
            this.startButton = startButton;
            this.stopButton = stopButton;
        }
        
        /**
         * Routes the press of the "Start Server" button to the
         * {@link StartServerAction#startServer} method.
         * @param e The action event.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            statusLabel.setText("online ");
            statusLabel.setForeground(Color.GREEN);
            startButton.setEnabled(false); 
            stopButton.setEnabled(true);    
            startServer();
            
        }
        
        /**
         * Starts the server.
         * Starts the communication and the KeepAliveManager.
         */
        public void startServer(){
            
            if (ServerCommunication.getInstance().getRunState() == true)
            {
                Logger.getLogger(ServerGUI.class.getName()).log(Level.INFO, "Server is allready running (no action needed). If you want to restart, use the 'stop' command first");
                return;
            }
            
            // Sets the server side communication and starts listening for clients by starting a new thread
            ServerCommunication.setUpServerSocket();
            Thread ServerCommunicationThread = new Thread(ServerCommunication.getInstance());
            ServerCommunicationThread.start();

            // Start sending KeepAliveManager signals if the configuration does not
            // tell otherwise
            if (ServerConfiguration.getInstance().getUseKeepAliveManager()) {
                KeepAliveManager keepAlive = new KeepAliveManager();
                Thread keepAliveThread = new Thread(keepAlive);
                keepAliveThread.setName("KeepAliveThread");
                keepAliveThread.start();
                Logger.getLogger(ServerGUI.class.getName()).log(Level.INFO, "Start sending KeepAliveMessages...");
            }

        }
    }
