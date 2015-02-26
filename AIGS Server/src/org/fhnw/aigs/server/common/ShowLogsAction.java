package org.fhnw.aigs.server.common;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 * Opens the log file folder.<br>
 * v1.0 Initial release<br>
 * v1.1 Handling changed<br>
 * v1.2 Changing of logging
 * 
 * @version v1.2 (Raphael Stoeckli, 24.02.2015)
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
            //LOG//Logger.getLogger(ShowLogsAction.class.getName()).log(Level.SEVERE, "Could not read logs path.", ex);
            LogRouter.log(ShowLogsAction.class.getName(), LoggingLevel.severe, "Could not read logs path.", ex);
        }
        catch (Exception ex) // All other exceptions
        {
            //LOG//Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "An unknown error occured.", ex);
            LogRouter.log(ShowLogsAction.class.getName(), LoggingLevel.severe, "An unknown error occured.", ex);
        }
        
    }
}
