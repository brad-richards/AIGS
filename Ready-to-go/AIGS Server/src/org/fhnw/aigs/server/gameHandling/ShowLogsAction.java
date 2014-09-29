/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhnw.aigs.server.gameHandling;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.fhnw.aigs.server.gui.ServerGUI;

/**
 * Opens the log file folder. 
 */
public class ShowLogsAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        showLogs();
    }
    
    /**
     * Opens the "logs" folder if possible.
     */
    public void showLogs(){
        try {
            File logFolder = new File(ServerConfiguration.getInstance().getLogDirectory() + "/");
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(logFolder);
            } else {
                JOptionPane.showMessageDialog(null, "Not supported on this OS.", "Not supported.", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Could not read logs path.", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(ShowLogsAction.class.getName()).log(Level.SEVERE, "Could not read logs path.", ex);
        }
        catch (Exception ex) // All other exceptions
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "An unknown error occured.", ex);
        }
        
    }
}
