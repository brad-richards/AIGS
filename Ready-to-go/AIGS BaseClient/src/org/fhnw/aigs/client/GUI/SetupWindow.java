package org.fhnw.aigs.client.GUI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javax.swing.JOptionPane;
import org.fhnw.aigs.client.communication.ClientCommunication;
import org.fhnw.aigs.client.communication.Settings;
import org.fhnw.aigs.client.gameHandling.ClientGame;
import org.fhnw.aigs.commons.GameMode;
import org.fhnw.aigs.commons.JoinType;

/**
 * This class shows the overlay for the game setup.<br>
 * The user can specify a party name and may overwrite the player name.<br>
 * A new party can be started, an existing party can be joined or a 
 * random party can be joined. In case of random joining will a new party
 * be created, if no wating game is present on the server. If a party is startet,
 * it can be defined whether this party is publicly available or private.<br>
 * v1.0 Initial release<br>
 * v1.0.1 Typos 
 * @version v1.0.1 (Raphael Stoeckli, 13.04.2015)
 * @author Raphael Stoeckli
 */
public class SetupWindow extends BorderPane{
    
    private TextField partyNameField;
    private ReadOnlyTextField playerNameField;
    private CheckBox publicGameCheckBox;
    private ClientGame clientGame;
    
    /**
     * Creates a new SetupWindow. If automatic connection is defined in the settings, 
     * the window will disappear immediately and estabish a connection to the server.
     * @param clientGame The ClientGame object of the game
     */
    public SetupWindow(ClientGame clientGame) {
        this.clientGame = clientGame;
        
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
            clientGame.getGameWindow().setOverlay(new LoadingWindow());
        }
        else if (clientGame.getGameMode() == GameMode.SinglePlayer)             // Automatically connect to a singleplayer game
        {
            String player = Settings.getInstance().getDisplayname();
            if (player.length() < 1)
            {
                player = "Player";
            }
            connect(true, true, false, "Party", player);                        // Connect to a random game or create a public game
            clientGame.getGameWindow().setContent(new LoadingWindow());            
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
        this.getStyleClass().add("loading");
        // The settings are loaded previously (MUST BE!)
        ClientCommunication.setCredentials(this.clientGame, Settings.getInstance().getServerAddress(), Settings.getInstance().getServerPort());
 
            Label loadingLabel = new Label("Game Setup");
            loadingLabel.setId("loadingLabel");
            loadingLabel.setTextAlignment(TextAlignment.RIGHT);

            StackPane sp = new StackPane();
            sp.setAlignment(Pos.CENTER);
            sp.getChildren().add(loadingLabel);

            Label partyNamelabel = new Label("Party Name");
            partyNamelabel.setTranslateY(70);
            partyNamelabel.getStyleClass().add("loadingLabelSmall");
            StackPane.setAlignment(partyNamelabel, Pos.CENTER_LEFT);            // See 'comments' below
            StackPane.setMargin(partyNamelabel, new Insets(0, 20, 0, 20));      // wtf? Really JavaFX... Static method???
            sp.getChildren().add(partyNamelabel);


            partyNameField = new TextField();
            partyNameField.setTranslateY(120);
            partyNameField.getStyleClass().add("loadingTextField");
            partyNameField.setTooltip(new Tooltip("Will try to connect to a waiting game with this party name if clicking on 'Connect to Game'.\nOtherwise will create a new game with this party name if cklicking on 'Create new Game'"));
            partyNameField.getTooltip().getStyleClass().add("loadingTooltip");
            StackPane.setMargin(partyNameField, new Insets(0, 20, 0, 20));      // ...
            sp.getChildren().add(partyNameField);

            Label userNamelabel = new Label("Player Name");
            userNamelabel.setTranslateY(170);
            userNamelabel.getStyleClass().add("loadingLabelSmall");
            StackPane.setAlignment(userNamelabel, Pos.CENTER_LEFT);             // ...
            StackPane.setMargin(userNamelabel, new Insets(0, 20, 0, 20));       // ...        
            sp.getChildren().add(userNamelabel);


            playerNameField = new ReadOnlyTextField(true);
            playerNameField.setTranslateY(220);
            playerNameField.getStyleClass().add("loadingTextField");
            //userNameField.setDisable(true);
            playerNameField.setTooltip(new Tooltip("This will be your player name in the party"));
            playerNameField.getTooltip().getStyleClass().add("loadingTooltip");        
            playerNameField.setText(Settings.getInstance().getDisplayname());
            StackPane.setMargin(playerNameField, new Insets(0, 20, 0, 20));      // ... sigh
            sp.getChildren().add(playerNameField); 

            publicGameCheckBox = new CheckBox("Create publicly available party");
            publicGameCheckBox.getStyleClass().add("loadingCheckBox");
            publicGameCheckBox.setTooltip(new Tooltip("If checked, the created party will be publicly availabel. Random participants jan join in without knowing the party name.\nIf not checked, participants must explicitly state the party name to join in."));
            publicGameCheckBox.getTooltip().getStyleClass().add("loadingTooltip"); 
            publicGameCheckBox.setTranslateY(280);
           // publicGameCheckBox.setAlignment(Pos.CENTER_LEFT);                 // Not working because of bad design decision of JavaFX creators (?)
            StackPane.setAlignment(publicGameCheckBox, Pos.CENTER_LEFT);        // Don't question it
            StackPane.setMargin(publicGameCheckBox, new Insets(0, 20, 0, 20));  // ... -_-
            sp.getChildren().add(publicGameCheckBox);
            
            Button newButton = new Button("Create new Game");
            newButton.setTranslateY(350);
            newButton.getStyleClass().add("loadingButton");
            newButton.setTooltip(new Tooltip("Creates a new game with the inserted party name"));
            newButton.getTooltip().getStyleClass().add("loadingTooltip");
            newButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    boolean privateGame = !publicGameCheckBox.isSelected();     // Invert boolean value (public game in UI / private game in parameter)
                    connect(true, false, privateGame, partyNameField.getText(), playerNameField.getText());      // Create a new public or private game
                }
            });
            sp.getChildren().add(newButton);              
            
            Button connectButton = new Button("Connect to Game");
            connectButton.setTranslateY(440);
            connectButton.getStyleClass().add("loadingButton");
            connectButton.setTooltip(new Tooltip("Tries to connect to a waiting game with the inserted party name"));
            connectButton.getTooltip().getStyleClass().add("loadingTooltip");
            connectButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    connect(false, false, false, partyNameField.getText(), playerNameField.getText());     //connect to a specific game
                }
            });
            sp.getChildren().add(connectButton);

            Button autoconnectButton = new Button("Join random Game");
            autoconnectButton.setTranslateY(530);
            autoconnectButton.getStyleClass().add("loadingButton");
            autoconnectButton.setTooltip(new Tooltip("Tries to connect to a random waiting game"));
            autoconnectButton.getTooltip().getStyleClass().add("loadingTooltip");
            autoconnectButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    connect(false, true, false, "Party", playerNameField.getText());    // Connect to a random game
                }
            });
            sp.getChildren().add(autoconnectButton);            
            setTop(sp);      
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
        Settings.getInstance().setDisplayname(playerName);  // Temporal storage. Do not save this value to the settings
        this.clientGame.setPartyName(partyName);            // Set party name
        this.clientGame.getGameWindow().setOverlay(new LoadingWindow());    // Set waiting window
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
        this.setVisible(false);                                                 // Hidet the setup window
    }

    /**
     * Special derivation of TextField. It allows to show tool tips on a disabled 
     * TextField and also to select Text in the disabled TextField
     */
    private class ReadOnlyTextField extends TextField{

        /**
         * Standard constructor
         */
        public ReadOnlyTextField() {
            super();
            init(false);
        }

        /**
         * Constructor with state (enabled or disabled)
         * @param state If true, the control will be enabled, otherwise disabled
         */
        public ReadOnlyTextField(boolean state) {
            super();
            init(state);
        }
 
         /**
         * Constructor with state (enabled or disabled) and text
         * @param state If true, the control will be enabled, otherwise disabled
         * @param text The text to show
         */
        public ReadOnlyTextField(String text, boolean state) {
            super(text);
            init(state);
        }
        
        /**
         * Method to initialize the Control
         * @param state If true, the control will be enabled, otherwise disabled
         */
        private void init(boolean state)
        {
            this.setEditable(state);
            this.setPrefColumnCount(DEFAULT_PREF_COLUMN_COUNT);
            if (state == false)
            {
            this.setStyle("-fx-background-color: lightgray;");
            }
        }
                
    }
    
}
