/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhnw.aigs.server.gameHandling;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.fhnw.aigs.server.communication.ServerCommunication;
import org.fhnw.aigs.server.gui.ServerGUI;

    /**
     * Responsible for stopping the server without closing the server GUI.
     * This class will be used by the server GUI if one clicks on the button
     * "stop server", if the Console Mode is active<br>
     * v1.0   Initial release<br>
     * v1.0.1 Minor changes
    * @author Raphael Stoeckli
    * @version 1.0.1 (27.10.2014)
     */
public class StopServerAction implements ActionListener{
    
    private JLabel statusLabel;
    private JButton startButton;
    private JButton stopButton;
        
    /**
     * Empty constructor, used when in console mode
     */
    public StopServerAction(){

    }
    
    /**
     * Creates an instance of StopServerAction and let the status label
     * and the start/stop buttons reflect that the server stopped.
     * @param statusLabel The server status label ("online", "offline").
     * @param startButton The button which is used to start the server.
     * @param stopButton The button which is used to stop the server.
     */
    public StopServerAction(JLabel statusLabel, JButton startButton, JButton stopButton){
        this.statusLabel = statusLabel;
        this.startButton = startButton;
        this.stopButton = stopButton;
    }    
    

    @Override
    public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null,
                        "Are you sure to stop the server? This will disconnect all clients.",
                        "Stop Server?",
                        JOptionPane.YES_NO_OPTION);
                if (result != JOptionPane.YES_OPTION) {
                    return; // Do nothing
                }        
        
            statusLabel.setText("offline ");
            statusLabel.setForeground(new Color(156, 25, 25));
            startButton.setEnabled(true); 
            stopButton.setEnabled(false);
            
            stopServer();
    }
    
    /**
     * Stops the Server
     */
        public void stopServer(){
            Logger.getLogger(ServerGUI.class.getName()).log(Level.INFO, "Begin shutdown process...");
            Thread cleanUpThread = new Thread(new ServerShutdownCleanUp());
            cleanUpThread.setName("ShutdownCleanUpThread");
            cleanUpThread.start();
            try
            {
                cleanUpThread.join();
            }
            catch(InterruptedException ex){}
            User.removeAllNonPersistentUsers();                                 // Removes all previously created users
            ServerCommunication.getInstance().stop(); // Run this at the end of cleanUpThread
            Logger.getLogger(ServerGUI.class.getName()).log(Level.INFO, "Server shut down finished. No further messages will be sent to clients. ");                
        }    
    
    
}
