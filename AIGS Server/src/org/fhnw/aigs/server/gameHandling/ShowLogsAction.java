package org.fhnw.aigs.server.gameHandling;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Opens the log file folder.<br>
 * v1.0 Initial release<br>
 * v1.1 Handling changed
 * @version v1.1 (Raphael Stoeckli, 28.10.2014)
 * @author Matthias Stöckli (v1.0)
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
            File logFolder = null;
            if(ServerConfiguration.getInstance().getTempLogsDirectory().equals(""))
            {
               logFolder = new File(ServerConfiguration.getInstance().getLogDirectory() + "/"); 
            }
            else
            {
              logFolder = new File(ServerConfiguration.getInstance().getTempLogsDirectory()+ "/"); // use old value until restart
            }
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
