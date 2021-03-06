package org.fhnw.aigs.client.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerNumberModel;
import org.fhnw.aigs.commons.communication.IdentificationMessage;
import org.fhnw.aigs.client.communication.Settings;

/**
 * This class is responsible for the client settings.<br>
 * This window is to manage the client settings. A user name, the identification, the 
 * server address and the server port can be defined. After a validation, the settings are stored
 * throug the Setting class. The window can be called to any time when the program is running.
 * Changes of the settings will be available after a restart or at the very first run of the program.<br>
 * This class was originally named 'IdentificationGUI' and refactored to 'SettingsWindow' in v1.1<br>
 * v1.0 Initial release<br>
 * v1.1 Added funcionality and some fields<br>
 * v1.2 Complete rewrite
 * @author Matthias Stöckli (v1.0)
 * @version 1.2 (Raphael Stoeckli, 14.10.2014)
 */
public class SettingsWindow extends JDialog {

    /**
     * Reference to the window
     */
    private static SettingsWindow frame;
    
    /**
     * A checkbox to enable or diable loading this dialog at startup
     */
    private JCheckBox startupCheckbox;

    /**
     * A checkbox to enable or diable the login
     */    
    private JCheckBox useLoginCheckbox;
    
    /**
     * A checkbox to enable or disable auto-connection to waiting games respectively auto-cration if no game is available
     */
    private JCheckBox autoConnectCheckbox;

    /**
     * The Server Port
     */
    private JSpinner portSpinner;
    
    /**
     * The Server URL or IP
     */
    private JTextField serverAddressField;
    
     /**
     * The user name field.
     */
    private JTextField usernameField;
    
     /**
     * The displayed name field of the user.
     */
    private JTextField displaynameField;
    
    /**
     * The password field.
     */    
    private JTextField passwordField; 
    
    /**
     * The user name or player field
     */
   // private JLabel usernameLabel;
    
    /**
     * The cancel button
     */
    private JButton cancelButton;
    
    /**
     * The save button
     */
    private JButton saveButton; 

    /**
     * Creates a new Setting window
     */
      public SettingsWindow() {
        this.setTitle("Settings");
        this.setSize(466, 232);
        this.setLocationByPlatform(true); // Better positioning of the window
        this.setResizable(false);
        init(); // Building the GUI
        fillData(); // Fill presets
        SettingsWindow.frame = this;
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // validation will decide when to close
        this.setAlwaysOnTop(true);
        this.setModal(true);
        this.addWindowListener(new SettingsWindow.CloseWindowAdapter()); 
    }
     
   /**
    * Initializing the GUI. The lower part of this method was generated by NetBeans and cleaned up by hand
    */   
    private void init()
    {
        startupCheckbox = new JCheckBox("Open this dialog at client startup");
        startupCheckbox.setToolTipText("If checked, this settings dialog will appear on every startup of the game");
        autoConnectCheckbox = new JCheckBox("Automatically connect to a waiting game / Create new game automatically if none available");
        autoConnectCheckbox.setToolTipText("<html>If checked, the client connects automatically to a waiting game if available on the server.<br>If no game is waiting, the client creates automatically a new waiting game</html>");
        useLoginCheckbox = new JCheckBox("Use login on server");
        useLoginCheckbox.setToolTipText("<html>If checked, the client sends username and password as identification to the server.<br>If not checked, the option of anonymous logon has to be enabled on the server. In this case, no password is required and the user name is only the display name</html> ");
        useLoginCheckbox.addActionListener(new SettingsWindow.ToggleAnonymousLoginAction());
        displaynameField = new JTextField();
        displaynameField.setToolTipText("Name of the player (will be displayed in the game)");         
        usernameField = new JTextField();
        usernameField.setToolTipText("Name of the player or user name (if login is enabled)"); 
        passwordField = new JTextField();
        passwordField.setToolTipText("Password of the user (if login is enabled)");
        serverAddressField = new JTextField();
        serverAddressField.setToolTipText("<html>A valid server address or IP like '127.0.0.1' or 'http://aigs.ch'.<br>Use 'localhost' when running an AIGS server on the same computer like the client</html>");
        portSpinner = new JSpinner();
        portSpinner.setToolTipText("<html>A valid port number from 1 to 65535.<br>The default value for AIGS is 25123</html>");
        portSpinner.setModel(new SpinnerNumberModel(1, 1, 65535, 1));
        portSpinner.setEditor(new JSpinner.NumberEditor(portSpinner, "#"));
        saveButton = new JButton("Save and close");
        saveButton.setToolTipText("Saves the settings and closes the dialog");
        saveButton.addActionListener(new SettingsWindow.CheckAndCreateAction());
        cancelButton = new JButton("Cancel");
        cancelButton.setToolTipText("Discards the changes and closes the dialog");
        cancelButton.addActionListener(new SettingsWindow.DiscardAction());

        JLabel playerNameLabel = new JLabel("Player Name:");
        JLabel usernameLabel = new JLabel("Login Name:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel serverAddressLabel = new JLabel("Server address:");
        JLabel serverPortLabel = new JLabel("Server Port:");

        GroupLayout layout = new GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(autoConnectCheckbox)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(startupCheckbox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(245, 245, 245))
                            .addComponent(useLoginCheckbox)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                            .addComponent(serverAddressLabel, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(passwordLabel, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(serverPortLabel, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE))
                                    .addComponent(usernameLabel, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(usernameField, GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                                    .addComponent(passwordField)
                                    .addComponent(serverAddressField)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(portSpinner, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(saveButton)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cancelButton))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(playerNameLabel, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(displaynameField, GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)))
                        .addContainerGap())
                );
                layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(startupCheckbox)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(autoConnectCheckbox)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(useLoginCheckbox)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(displaynameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(playerNameLabel))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(usernameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(usernameLabel))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(passwordLabel))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(serverAddressField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(serverAddressLabel))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(serverPortLabel)
                                    .addComponent(portSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 29, Short.MAX_VALUE))
                            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(saveButton)
                                    .addComponent(cancelButton))
                                .addContainerGap())))
                );        
        pack();      
    }      

    /**
     * Handles close action. Shows a prompt which asks for confirmation.
     */
    private static class CloseWindowAdapter extends WindowAdapter {

        @Override
        public void windowClosing(final WindowEvent e) {
            HandleClosingAction(e.getComponent());
        }
        
        public static void HandleClosingAction(Component c)
        {
               if (Settings.getInstance().isInitialized() == false) // Only ask, if this is the very first start of the program, respectively if no settings file exists
            { 
                int result = JOptionPane.showConfirmDialog(c,
                        "Are you sure you want to close the application?",
                        "Close the application?",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
            else
            {
                SettingsWindow.frame.dispose(); // Close settings window
            }         
        }
    }
    
    /**
     * Closes the window without saving the settings
     */
    private class DiscardAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            CloseWindowAdapter.HandleClosingAction(SettingsWindow.frame);
        }
    }    

    /**
     * Enables or disables the identification code field depending on the 
     * check state of the anoymous login checkbox
     */
    private class ToggleAnonymousLoginAction implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) {
            boolean checkState = useLoginCheckbox.isSelected();
            if (checkState == false)
            {
                passwordField.setText("");
                passwordField.setBackground(Color.LIGHT_GRAY);
                passwordField.setEnabled(false);
                usernameField.setText("");
                usernameField.setBackground(Color.LIGHT_GRAY);
                usernameField.setEnabled(false);
            }
            else
            {
                passwordField.setEnabled(true);
                passwordField.setBackground(Color.WHITE);
                usernameField.setEnabled(true);
                usernameField.setBackground(Color.WHITE);
             //   usernameLabel.setText("Login name:");
            }
        }
        
    }
    
    /**
     * Validates the content and saves through Setting class
     * {@link IdentificationMessage}.
     */
    private class CheckAndCreateAction implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent e) {
            String playerName = displaynameField.getText().trim();
            String userName = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String address = serverAddressField.getText().trim();
            boolean useLogin = useLoginCheckbox.isSelected();
            boolean autoConnect = autoConnectCheckbox.isSelected();
            boolean showSettings = startupCheckbox.isSelected();
            int port;
            try
            {
                port = (int)portSpinner.getValue();
            }
            catch(Exception ex)
            {
                showMessage("Server port must be a integer between 1 and 65535","Invalid input");
                return;
            }
            if (playerName.equals("")) {
                showMessage("Player name not defined.","Invalid input");
                return;
            }
            if (userName.equals("") && useLogin == true) {
                showMessage("Login name not defined.","Invalid input");
                return;
            } 
            if (password.equals("") && useLogin == true) {
                showMessage("Password not defined.","Invalid input");
                return;
            }            
            if (address.equals(""))
            {
                showMessage("Server address or IP not defined","Invalid input");
                return;                
            }
            if (port < 1 || port > 65535)
            {
                showMessage("Server port must be between 1 and 65535","Invalid input");
                return;                  
            }
            // Creating the (not from server validated) user settings
            
            if (Settings.getInstance().isInitialized() == true) // Existing Settings will be overwritten --> Message to user if game is running
            {
                if (Settings.getInstance().isGameRunning() == true)
                {
                    showMessage("Changes of the user settings will only be applied after a restart of the client", "Settings changed");
                }
            }
            
            if (useLogin == false)
            {
                password = "---"; // Default string (only for transmission, will not be checked, if anonymous login is enabled on the server)
                userName = "User";
            }
            

            Settings.writeInstance(playerName, userName, password, address, port, useLogin, autoConnect, showSettings);
            Settings.getInstance().saveSettings();
            SettingsWindow.frame.dispose(); // Close window
        }
    }
    
    /**
     * Method to show a message box with an OK button
     * @param message Message to show
     * @param title Title of the message box
     */
    private void showMessage(String message, String title)
    {
        JOptionPane.showMessageDialog(SettingsWindow.frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Method fills existing settings information in the fields of the settings window, if existing
     */
    private void fillData()
    {
            if (Settings.getInstance().isUsingLogin() == false)
            {
                useLoginCheckbox.setSelected(false);
                passwordField.setText("");
                passwordField.setBackground(Color.LIGHT_GRAY);
                passwordField.setEnabled(false);
                usernameField.setText("");
                usernameField.setBackground(Color.LIGHT_GRAY);
                usernameField.setEnabled(false);
            }
            else
            {
                useLoginCheckbox.setSelected(true);
                passwordField.setText(Settings.getInstance().getPassword());
                passwordField.setBackground(Color.WHITE);
                passwordField.setEnabled(true); 
                usernameField.setText(Settings.getInstance().getUsername());
                usernameField.setBackground(Color.WHITE);
                usernameField.setEnabled(true); 
            }
            startupCheckbox.setSelected(Settings.getInstance().getShowSettingsAtStartup());
            autoConnectCheckbox.setSelected(Settings.getInstance().getAutoConnect());
            displaynameField.setText(Settings.getInstance().getDisplayname());
            serverAddressField.setText(Settings.getInstance().getServerAddress());
            portSpinner.setValue(Settings.getInstance().getServerPort());
    }

    /**
     * Shows the user that the identification was not successful.
     *
     * @param reason Text with the reason
     */
    public static void notifyOfFailure(String reason) {
       SettingsWindow.frame.showMessage(reason, "Error");
    }
}