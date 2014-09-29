package org.fhnw.aigs.swingClient.GUI;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.fhnw.aigs.swingClient.communication.ClientCommunication;
import org.fhnw.aigs.swingClient.communication.Settings;
import org.fhnw.aigs.swingClient.gameHandling.ClientGame;
import org.fhnw.aigs.commons.communication.IdentificationMessage;

/**
 * This class is responsible for checking the identity of a player and the client settings.<br>
 * <span style="color:red"><b>The following text is deprecated (from v1.0)</b><br>
 * In order to start a game, a client must first be identified. This happens
 * with the help of the {@link IdentificationMessage}. This message needs a user
 * name and an identification code. In a first attempt, the BaseClients
 * mechanisms will try to use a file called <b>aigs.user</b> located in the base
 * folder of the game.<br>
 * If there is no such file, this dialogue will be shown, asking the user to
 * enter the name and the identification code. If the identification process is
 * successful, the credentials will be saved in abovementioned file.<br>
 * Later on, this dialogue should not be displayed anymore.
 *</span>
 * @author Matthias Stöckli
 * @version 1.1 (Raphael Stoeckli)
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
     * The current clientGame. It will be used to send the identification
     * message.
     */
    private static ClientGame clientGame;

    /**
     * Create a new SettingsGUI.
     */
//public SettingsWindow(ClientGame clientGame) {
      public SettingsWindow() {
        this.setTitle("Settings");
        //super("Settings");
//        SettingsWindow.clientGame = clientGame;
        //this.frame = this;
        this.setSize(400, 300);
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
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
            if (Settings.getInstance().isInitialized() == false)
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
                frame.dispose();
            }
        }
    }

    /**
     * Sends the strings typed in to the server by sending them in a
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
            frame.dispose();
            
            
            // Sends an identification to the Server over the new connection
//            IdentificationMessage identificationMessage = new IdentificationMessage(userName, identificationCode);
//            clientGame.sendMessageToServer(identificationMessage);
//            Logger.getLogger(ClientCommunication.class.getName()).log(Level.INFO, "Sent identification!");
        }
    }
    
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
     * Closes the window.
     */
    /*
    public static void notifyOfSuccess() {
        if (ClientCommunication.hasReadFromFile == false) {
            frame.dispose();
        }
    }
    */
    
    /*
     public static void notifyOfSuccess() {
        if (Settings.getInstance().isInitialized()) {
            frame.dispose();
        }
    }   
    */

    /**
     * Shows the user that the identification was not successful.
     *
     * @param reason
     */
    public static void notifyOfFailure(String reason) {
        errorMessageLabel.setText(reason);
    }
}
