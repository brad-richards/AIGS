package org.fhnw.aigs.client.GUI;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.fhnw.aigs.client.communication.Settings;
import org.fhnw.aigs.commons.communication.IdentificationMessage;

/**
 * This class is responsible for the client settings.<br>
 * This window is to manage the client settings. A user name, the identification, the 
 * server address and the server port can be defined. After a validation, the settings are stored
 * throug the Setting class. The window can be called to any time when the program is running.
 * Changes of the settings will be available after a restart or at the very first run of the program.<br>
 * This class was originally named 'IdentificationGUI' and refactored to 'SettingsWindow' in v1.1
 * @author Matthias Stöckli (v1.0)
 * @version 1.1 (Raphael Stoeckli, 12.08.2014)
 */
public class SettingsWindow extends JDialog {

    /**
     * The user name field.
     */
    private static JTextField userNameTextField;
    /**
     * The identification code field.
     */
    private static JTextField identificationCodeField;
    
    /**
     * The Server URL or IP
     */
    private static JTextField serverAddressField;
    
    /**
     * The Server Port
     */
    private static JTextField serverPortField;    
    
    /**
     * A label which is used to show an error message.
     */  
    private static JLabel errorMessageLabel;
    /**
     * The window itself. This reference is used to close the window from
     * another class.
     */
    private static JDialog frame;

    /**
     * Create a new Setting window
     */
      public SettingsWindow() {
        this.setTitle("Settings");
        this.setSize(400, 300);
        this.setLocationByPlatform(true); // Better positioning of the window
        this.setResizable(false);
        frame = this;

        // Create GUI Elements.
        JPanel basePanel = new JPanel(new GridLayout(10, 0));

        errorMessageLabel = new JLabel();
        errorMessageLabel.setForeground(Color.RED);

        JLabel userNameLabel = new JLabel("User name (e.g. marco.fischer)");
        userNameTextField = new JTextField();

        JLabel identificationCodeLabel = new JLabel("Identification code (e.g. aWcKyY)");
        identificationCodeField = new JTextField();

        JLabel serverLabel = new JLabel("Server address or URL (e.g. 'localhost' or '127.0.0.1')");
        serverAddressField = new JTextField();        
        
        JLabel portLabel = new JLabel("Server port (default: 25123)");
        serverPortField = new JTextField();         
        
        JButton sendButton = new JButton("Check and create configuration");
        sendButton.addActionListener(new SettingsWindow.CheckAndCreateAction());

        // Add them to the JPanel.
        basePanel.add(errorMessageLabel);
        basePanel.add(userNameLabel);
        basePanel.add(userNameTextField);
        basePanel.add(identificationCodeLabel);
        basePanel.add(identificationCodeField);
        basePanel.add(serverLabel);
        basePanel.add(serverAddressField);
        basePanel.add(portLabel);
        basePanel.add(serverPortField);
        basePanel.add(sendButton);
        this.setContentPane(basePanel);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // validation will decide when to close
        this.setAlwaysOnTop(true);
        this.setModal(true);
        this.addWindowListener(new SettingsWindow.CloseWindowAdapter());
        
        fillData();
    }

    /**
     * Handles close action. Shows a prompt which asks for confirmation.
     */
    private static class CloseWindowAdapter extends WindowAdapter {

        @Override
        public void windowClosing(final WindowEvent e) {
            if (Settings.getInstance().isInitialized() == false) // Only ask, if this is the very first start of the program, respectively if no settings file exists
            { 
                int result = JOptionPane.showConfirmDialog(e.getComponent(),
                        "Are you sure you want to close the application?",
                        "Close the application?",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
            else
            {
                frame.dispose(); // Close settings window
            }
        }
    }

    /**
     * Validates the content and saves through Setting class
     * {@link IdentificationMessage}.
     */
    private static class CheckAndCreateAction implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent e) {
            String userName = userNameTextField.getText().trim();
            String identificationCode = identificationCodeField.getText().trim();
            String address = serverAddressField.getText().trim();
            String portString = serverPortField.getText().trim();

            if (userName.equals("")) {
                errorMessageLabel.setText("Username not defined.");
                return;
            }
            
            if (identificationCode.equals("")) {
                errorMessageLabel.setText("Identification Code not defined.");
                return;
            }            
            
            if (address.equals(""))
            {
                errorMessageLabel.setText("Server address or IP not defined");
                return;                
            }
            
            if (portString.equals(""))
            {
                errorMessageLabel.setText("Server port not defined");
                return;                
            }
            int port = -1;
            try
            {
                port = Integer.parseInt(portString);
            }
            catch(NumberFormatException ex)
            {
                errorMessageLabel.setText("Server port must be a integer between 0 and 65535");
                return;                
            }
            if (port < 0 || port > 65535)
            {
                errorMessageLabel.setText("Server port must be between 0 and 65535");
                return;                  
            }
            // Creating the (not from server validated) user settings
            
            if (Settings.getInstance().isInitialized() == true) // Existing Settings will be overwritten --> Message to user
            {
                JOptionPane.showMessageDialog(frame, "Changes of the user settings will only be applied after a restart of the client", "Settings changed", JOptionPane.INFORMATION_MESSAGE);
            }
            
            Settings.writeInstance(userName, identificationCode, address, port);
            Settings.getInstance().saveSettings();
            frame.dispose(); // Close window
        }
    }
    
    /**
     * Method fills existing settings information in the fields of the settings window, if existing
     */
    private void fillData()
    {
        if (Settings.tryLoadSettings(false) == true)
        {
            userNameTextField.setText(Settings.getInstance().getUsername());
            identificationCodeField.setText(Settings.getInstance().getIdentificationCode());
            serverAddressField.setText(Settings.getInstance().getServerAddress());
            serverPortField.setText(Settings.getInstance().getServerPortString());
        }
    }

    /**
     * Shows the user that the identification was not successful.
     *
     * @param reason Text with the reason
     */
    public static void notifyOfFailure(String reason) {
        errorMessageLabel.setText(reason);
    }
}
