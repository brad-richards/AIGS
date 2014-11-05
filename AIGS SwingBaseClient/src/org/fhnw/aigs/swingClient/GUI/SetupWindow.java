package org.fhnw.aigs.swingClient.GUI;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.fhnw.aigs.commons.GameMode;
import org.fhnw.aigs.commons.JoinType;
import org.fhnw.aigs.swingClient.communication.ClientCommunication;
import org.fhnw.aigs.swingClient.communication.Settings;
import org.fhnw.aigs.swingClient.gameHandling.ClientGame;


/**
 * This class shows the JPanel for the game setup.<br>
 * The user can specify a party name and may overwrite the player name.<br>
 * A new party can be started, an existing party can be joined or a 
 * random party can be joined. In case of random joining will a new party
 * be created, if no wating game is present on the server. If a party is startet,
 * it can be defined whether this party is public available or private.
 * @version v1.0
 * @author Raphael Stoeckli (23.10.2014)
 */
public class SetupWindow extends BackgroundPanel{
    
    private JButton joinGameButton;
    private JButton newGameButton;
    private JTextField partyNameField;
    private JTextField playerNameField;
    private JCheckBox publicGameCheckBox;
    private JButton randomGameButton;
    private ClientGame clientGame;
    
    /**
     * Creates a new SetupWindow. If automatic connection is defined in the settings, 
     * the window will disappear immediately and estabish a connection to the server.
     * @param clientGame The ClientGame object of the game
     */
    public SetupWindow(ClientGame clientGame)
    {
        this.clientGame = clientGame;
        // The settings are loaded previously (MUST BE!)
        ClientCommunication.setCredentials(this.clientGame, Settings.getInstance().getServerAddress(), Settings.getInstance().getServerPort());
        this.setLayout(null);

        if (Settings.getInstance().getAutoConnect() == true)                    // Automatically connect to a random game
        {
            init();
            String player = Settings.getInstance().getDisplayname();
            if (player.length() < 1)
            {
                player = "Player";
            }
            playerNameField.setText(player);
            connect(true, true, false, "Party", player);                        // Connect to a random game or create a public game
        }
        else if (clientGame.getGameMode() == GameMode.SinglePlayer)             // Automatically connect to a singleplayer game
        {
            String player = Settings.getInstance().getDisplayname();
            if (player.length() < 1)
            {
                player = "Player";
            }
            connect(true, true, false, "Party", player);                        // Connect to a random game or create a public game            
        }
        else                                                                    // Only show UI
        {
           init();         
        }
        
    }
    
    /**
     * Initializes the window elements
     */
    private void init()
    {
        Font titleFont = new Font("Aerovias Brasil NF", Font.BOLD, 65);
        Font subTitleFont = new Font("Aerovias Brasil NF", Font.PLAIN, 42);
        Font inputFont = new Font("Aerovias Brasil NF", Font.PLAIN, 38);
        Font checkboxFont = new Font("Aerovias Brasil NF", Font.PLAIN, 32);
        Font buttonFont = new Font("Aerovias Brasil NF", Font.BOLD, 32);
        
        JLabel titleLabel = new JLabel("Game Setup");
        titleLabel.setFont(titleFont);
        titleLabel.setBounds(200, 24, 300, 65);
        JLabel partyNamelabel = new JLabel("Party Name");
        partyNamelabel.setFont(subTitleFont);
        partyNamelabel.setBounds(40, 100, 640, 46);
        partyNameField = new JTextField();
        partyNameField.setFont(inputFont);
        partyNameField.setBounds(40, 150, 640, 46); 
        partyNameField.setMargin(new Insets(2,10,2,10));
        JLabel playerNameLabel = new JLabel("Player Name");
        playerNameLabel.setFont(subTitleFont);
        playerNameLabel.setBounds(40, 200, 640, 46);  
        playerNameField = new JTextField();
        playerNameField.setFont(inputFont);
        playerNameField.setBounds(40, 250, 640, 46); 
        playerNameField.setMargin(new Insets(2,10,2,10));
        playerNameField.setText(Settings.getInstance().getDisplayname());
        publicGameCheckBox = new JCheckBox("Create public available party");
        publicGameCheckBox.setFont(checkboxFont);
        publicGameCheckBox.setOpaque(false);
        publicGameCheckBox.setBounds(40, 300, 640, 46); 
           
        newGameButton = new JButton("Create new Game");
        newGameButton.setFont(buttonFont);
        newGameButton.setBounds(200, 375, 300, 60); 
        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                newGameButtonActionPerformed(evt);
            }
        });
        joinGameButton = new JButton("Connect to Game");
        joinGameButton.setFont(buttonFont);
        joinGameButton.setBounds(200, 450, 300, 60); 
        joinGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                joinGameButtonActionPerformed(evt);
            }
        });
        randomGameButton = new JButton("Join random Game");
        randomGameButton.setFont(buttonFont);
        randomGameButton.setBounds(200, 525, 300, 60);
        randomGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                randomGameButtonActionPerformed(evt);
            }
        });
        
        this.add(titleLabel);
        this.add(partyNamelabel);
        this.add(partyNameField);
        this.add(playerNameLabel);
        this.add(playerNameField);
        this.add(publicGameCheckBox);
        this.add(newGameButton);
        this.add(joinGameButton);
        this.add(randomGameButton);
               
        this.setPreferredSize(new Dimension(600, 800));
        this.setSize(new Dimension(600, 800));
    }
    
    /**
     * Handles the event when cklicking on the button to create a new game
     * @param evt ActionEvent data
     */
    private void newGameButtonActionPerformed(ActionEvent evt) {                                              
        boolean privateGame = !publicGameCheckBox.isSelected();                 // Invert boolean value (public game in UI / private game in parameter)
        connect(true, false, privateGame, this.partyNameField.getText(), this.playerNameField.getText()); // Create a new public or private game
    }                                             

    /**
     * Handles the Event when cklicking on the button to join an existing game
     * @param evt ActionEvent data
     */    
    private void joinGameButtonActionPerformed(ActionEvent evt) {                                               
        connect(false, false, false, this.partyNameField.getText(), this.playerNameField.getText());     //connect to a specific game
    }                                              

    /**
     * Handles the Event when cklicking on the button to join a random game or create a new one
     * @param evt ActionEvent data
     */        
    private void randomGameButtonActionPerformed(ActionEvent evt) {                                                 
        connect(false, true, false, "Party", this.playerNameField.getText());   // Connect to a random game
    }     
    
    /**
     * Method establishes a connection to the server
     * @param createGame If true, a new game will be created, otherwiese the client tries to connect to a waiting game
     * @param randomConnect If true, the client will try to connect to a random game (or create a new one, if no available).<br> Otherwise it will create a game with the specified party name or will try to connect to a game with the specified party name
     * @param privateGame If true, a private game will be created in case of creating. This parameter is not applying if createGame is false.
     * @param partyName The name of the party
     * @param playerName The name of the Player
     */
    private void connect(boolean createGame, boolean randomConnect, boolean privateGame, String partyName, String playerName)
    {
        if (partyName.length() < 1)
        {
            JOptionPane.showMessageDialog(null, "<html>Please define a party name.<br>For example: 'Party' or 'New game'</html>", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (playerName.length() < 1)
        {
            JOptionPane.showMessageDialog(null, "<html>Please define a player name.<br>For example: 'Carl' or 'Player one'</html>", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Settings.getInstance().setDisplayname(playerName);                      // Temporary storage. Do not save this value to the settings
        if (Settings.getInstance().getAutoConnect() == true || randomConnect == true) // Connect automatically or create a new game
        {
            this.clientGame.setJoinType(JoinType.Auto);
        }
        else
        {
            if (createGame == true)
            {
                if (privateGame == true)
                {
                    this.clientGame.setJoinType(JoinType.CreateNewPrivateGame); 
                }
                else
                {
                    this.clientGame.setJoinType(JoinType.CreateNewGame);                    
                }
            }
            else
            {
                this.clientGame.setJoinType(JoinType.JoinParticularGame);
            }
        }
        Settings.getInstance().SetGameRunning();                                // Set to running state unless no other information from the server
        Thread communicationThread = new Thread(                                // Create a new thread to avoid freezing the window
        ClientCommunication.getInstance());                                     // Create instance of the communication
        communicationThread.start();
        this.clientGame.setPartyName(partyName);                                // Set party name
        this.clientGame.getGameWindow().setContent(new LoadingWindow());        // Set waiting window
    }
    
}
