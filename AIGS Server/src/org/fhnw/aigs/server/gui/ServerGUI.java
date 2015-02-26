package org.fhnw.aigs.server.gui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.metal.MetalBorders;
import org.fhnw.aigs.commons.Game;
import org.fhnw.aigs.commons.Player;
import org.fhnw.aigs.server.common.LogRouter;
import org.fhnw.aigs.server.common.LoggingLevel;
import org.fhnw.aigs.server.communication.ServerCommunication;
import org.fhnw.aigs.server.gameHandling.GameManager;
import org.fhnw.aigs.server.gameHandling.RecompileClassesAction;
import org.fhnw.aigs.server.gameHandling.ReloadClassesAction;
import org.fhnw.aigs.server.common.ServerConfiguration;
import org.fhnw.aigs.server.common.ShowLogsAction;
import org.fhnw.aigs.server.gameHandling.User;

/**
 * This is the server's GUI. Several controls and information panels are provided.<br>
 * Controls:<br>
 * <ul>
 * <li>Start server (service)</li>
 * <li>Stop server (service)</li>
 * <li>Close the program</li>
 * <li>Show the logs</li>
 * <li>Reload the avilable games</li>
 * <li>Recompile and reload the avalibale games. Thos only applies if game projects are stored on the AIGS server</li>
 * <li>Call the server settings dialog (configuration)</li>
 * <li>Call the user management dialog</li>
 * <li>End a game (forced) if selected in the list of waiting or running games</li>
 * </ul>
 *<br>
 * Information panels:<br>
 * <ul>
 * <li>The server log</li>
 * <li>A list of available / installed games</li>
 * <li>A list of active users. This list will show the persistent users if anonymous login is disabled, otherwise adHoc created users</li>
 * <li>A list of active (running) games</li>
 * <li>A list of waiting (not started yet) games</li>
 * <li>Detail information panel with a list of participants of a selected game</li>
 * </ul>
 * <br>
 * Please not that some functionality is implemented in subclasses, e.g.
 * the server start.<br>
 * <br>v1.1 New functions and fixes
 * <br>v1.2 New functions (settings and user management) and change from static to singleton
 * <br>v1.3 Changing of logging
 * @author Matthias Stöckli
 * @version v1.3 (Raphael Stoeckli, 24.02.2015)
 */
public class ServerGUI extends JFrame {
    
    /**
     * The singleton instance of the GUI
     */
    private static ServerGUI instance;

    /**
     * Gets the singleton instance or initializes it if not defined
     * @return instance of the GUI
     */
    public static ServerGUI getInstance() {
        if (instance == null)
        {
            instance = new ServerGUI();
        }
        return instance;
    }
    
    public  Game selectedGame;
    private JList gameList;
    private JScrollPane gameListScrollPane;
    private JList waitingGameList;
    private JScrollPane waitingGameListScrollPane;    
    private JList availableGamesList;
    private JScrollPane availableGamesListScrollPane;    
    private JTextArea logTextArea;
    private JScrollPane logTextAreaScrollPane;
    private JPanel gameInformationPanel;  
    private JPanel topPanel;
    private JPanel statusPanel;
    private JPanel settingsPanel;
    private JPanel buttonPanel;        
    private Vector<Game> listContent;
    private Vector<Game> waitingListContent;
    private Vector<String> availableGamesContent;
    private Vector<User> activeUsersContent;
    private JButton startButton;
    private JButton stopButton;
    private JButton closeButton;
    private JButton showLogsButton;
    private JButton reloadClassesButton;
    private JButton recompileClassesButton;
    private JButton settingsButton;
    private JButton userManagementButton;
    private JLabel statusLabel;
    private JLabel gameNameLabel;
    private JLabel participantsLabel;
    private JLabel versionLabel;
    
    private JList activeUsersList;
    private JScrollPane activeUsersListScrollPane;    
    
    private JList participantsList;
    private JScrollPane particiantsScrollPane;      
    private ListModel participantsModel;
    
    private JLabel partyLabel;
    private JLabel publicGameLabel;
    private JLabel idLabel;
    private JLabel ipLabel;
    private JButton endGameButton;
    
    private LimitLinesDocumentListener  logListener;

    public LimitLinesDocumentListener getLogListener() {
        return logListener;
    }

    public void setLogListener(LimitLinesDocumentListener logListener) {
        this.logListener = logListener;
    }
    
    /**
     * Constructor of GUI class
     */
    public ServerGUI() {
        super("AIGS - AI Game Server ");
        setLookAndFeel();
        setUpWindow();
        setAIGSTrayIcon();
        setUpTopPanel(); 
 
        JPanel middlePanel = new JPanel(null);
        JPanel allGamesPanel = new JPanel(new GridLayout(2, 1));
        setUpLoggingPanel();
        setUpGameInformationPanel();
        setUpGameList();
        setUpAvailableGames();
        setUpActiveUsers();
        loadUsers();
       
        setupActionListeners();
        
        logTextAreaScrollPane.setBounds(10,0,650,615);
        gameInformationPanel.setBounds(1020,200,220,415);
        allGamesPanel.setBounds(670,200,340, 415);
        availableGamesListScrollPane.setBounds(670, 0, 280, 190); //570>280
        activeUsersListScrollPane.setBounds(960, 0, 280, 190); //570>285
        
        allGamesPanel.add(gameListScrollPane);
        allGamesPanel.add(waitingGameListScrollPane);
        
        middlePanel.add(logTextAreaScrollPane);
        middlePanel.add(allGamesPanel);
        middlePanel.add(gameInformationPanel);
        middlePanel.add(availableGamesListScrollPane);
        middlePanel.add(activeUsersListScrollPane);
        
        
        this.add(middlePanel, BorderLayout.CENTER);

        pack();
    }

    /**
     * Shows the GUI when hidden.
     */
    public void showGUI() {
        this.setVisible(true);
    }

    /**
     * Sets a Sys-Tray icon for the AIGS. This method is based on the Javadoc
     * entry on "SystemTray".
     * See: http://docs.oracle.com/javase/7/docs/api/java/awt/SystemTray.html
     */
    public void setAIGSTrayIcon() {
            // Add an application icon
        ImageIcon logoImage = new ImageIcon(getClass().getResource("/imgs/logo24px.png"));
        this.setIconImage(logoImage.getImage());

        
        // Set tray icon
        TrayIcon trayIcon = null;
        if (SystemTray.isSupported()) {
            
            if(ServerConfiguration.getInstance().getHidesOnClose() == true){
                this.setDefaultCloseOperation(HIDE_ON_CLOSE);            
            }else{
                this.setDefaultCloseOperation(EXIT_ON_CLOSE);
                
                final JFrame thisFrame = this;
                this.addWindowStateListener(new WindowAdapter() {
                     @Override
                     public void windowIconified(WindowEvent e) {
                       thisFrame.setVisible(false);
                    }
                });
            }

            
            // Get the SystemTray instance and load an image
            SystemTray tray = SystemTray.getSystemTray();
            
            // Listener to open the application.
            ActionListener openListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showGUI();
                }
            };
            
            // Listener to close the application.
            ActionListener closeListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            };

            // Create a popup menu
            PopupMenu popup = new PopupMenu();

            // Creation of the "Öffnen" menu entry 
            MenuItem defaultItem = new MenuItem("Open AIGS");
            defaultItem.addActionListener(openListener);

            // Creation of the "Beenden" menu entry 
            MenuItem closeItem = new MenuItem("Close AIGS");
            closeItem.addActionListener(closeListener);

            popup.add(defaultItem);
            popup.add(closeItem);

            trayIcon = new TrayIcon(logoImage.getImage(), "AIGS", popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(openListener);

            // add the tray image
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {

            }
        }else{
            // If tray is not supported close the application.
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        }
    }

    /**
     * Adds the specified game to the game list.
     *
     * @param game The game to be added
     * @param waiting If true, the game will be added to the waiting games list, otherwiese to the active games list
     */
    public void addGameToList(Game game, boolean waiting) {
        if (waiting == true)
        {
         waitingListContent.add(game);
         waitingGameList.setListData(waitingListContent);
        }
        else
        {
        listContent.add(game);
        gameList.setListData(listContent);
        }
    }

    /**
     * Removes the specified game from the game list.
     *
     * @param game The game to be removed
     * @param waiting If true, the game will be removed from the waiting games list, otherwiese from the active games list
     */
    public void removeGameFromList(Game game, boolean waiting) {
        if (waiting == true)
        {
        waitingListContent.remove(game);        
        waitingGameList.setListData(waitingListContent);
        waitingGameList.setSelectedIndex(-1);           
        }
        else
        {
        listContent.remove(game);        
        gameList.setListData(listContent);
        gameList.setSelectedIndex(-1);
        }
    }
    
    /**
     * Adds the specified user to the list
     * @param user User to add
     */
    public void addUserToList(User user)
    {
        activeUsersContent.add(user);
        activeUsersList.setListData(activeUsersContent);
    }
    
    /**
     * Removes all usrest from the list
     */
    public void removeAllUsersFromList()
    {
        activeUsersContent.clear();
        activeUsersList.setListData(activeUsersContent);
    }
    
    /**
     * Removes the specified user from the list
     * @param user User to remove
     */
    public void removeUserFromList(User user)
    {
        activeUsersContent.remove(user);
        activeUsersList.setListData(activeUsersContent);
        activeUsersList.setSelectedIndex(-1);
    }
    

    /**
     * Returns the game having a specified index in the game list.
     *
     * @param listIndex The position/index in the list.
     * @param waiting If true, the source ist the list of waiting games, otherwise the list of active games
     * @return Game at the specified position/index.
     */
    public Game getGameFromListByListIndex(int listIndex, boolean waiting) {
        if (waiting == true)
        {
            if(listIndex >= 0){
                return ServerGUI.getInstance().waitingListContent.elementAt(listIndex);
            }else{
                return null;
            }
        }
        else
        {
            if(listIndex >= 0){
                return ServerGUI.getInstance().listContent.elementAt(listIndex);
            }else{
                return null;
            }            
        }
    }

    /**
     * Sets the Look and Feel of the application. The standard LAF for this
     * application is <b>Nimbus</b>.
     */
    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            //LOG//Logger.getLogger(ServerGUI.class.getName()).log(Level.SEVERE, null, ex);
            LogRouter.log(ServerGUI.class.getName(), LoggingLevel.severe, null, ex);
        }
    }
    
    /**
     * In this method all necessary steps are taken to set up the upper part of
     * the GUI: Butttons and labels are added.
     */
    private void setUpTopPanel() {
        topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
        statusPanel = new JPanel(new GridLayout(1,4));
        buttonPanel = new JPanel(new GridLayout(1,5));
        settingsPanel = new JPanel(new GridLayout(1,2));
        settingsPanel.setBorder( new EmptyBorder( 10, 200, 10, 0 ));
        
        
        this.add(topPanel, BorderLayout.NORTH);
        ImageIcon logoImage = new ImageIcon(getClass().getResource("/imgs/logo.png"));
        ImageIcon settingsLogo = new ImageIcon(getClass().getResource("/imgs/settings.png"));
        ImageIcon usersLogo = new ImageIcon(getClass().getResource("/imgs/users.png"));
        JLabel logoLabel = new JLabel(logoImage);
        
        statusLabel = new JLabel("offline");
        statusLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        statusLabel.setForeground(new Color(156, 25, 25));
        
        ipLabel = new JLabel(ServerCommunication.getExternalIp());
        ipLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        ipLabel.setForeground(Color.GRAY);  
        
        startButton = new JButton("Start AIGS");
        stopButton = new JButton("Stop AIGS");
        stopButton.setEnabled(false);
        stopButton.addActionListener(new StopServerAction(statusLabel, startButton, stopButton));
        startButton.addActionListener(new StartServerAction(statusLabel, startButton, stopButton));
        
        statusLabel.setForeground(new Color(24, 105, 36));        
        
        settingsButton = new JButton(settingsLogo);
        settingsButton.addActionListener(new SettingsAction());
        settingsButton.setToolTipText("Opens the system settings window");
        userManagementButton = new JButton(usersLogo);
        userManagementButton.addActionListener(new UserSettingsAction());
        userManagementButton.setToolTipText("Opens the user management window");
        settingsPanel.add(settingsButton);
        settingsPanel.add(userManagementButton);
        

        closeButton = new JButton("Close AIGS");
        closeButton.addActionListener(new ServerGUI.CloseServerAction());

        showLogsButton = new JButton("Show Logs");
        showLogsButton.addActionListener(new ShowLogsAction());
        
        reloadClassesButton = new JButton("Reload classes");     

        recompileClassesButton = new JButton("Recompile and load classes");
        
        statusPanel.add(logoLabel);
        statusPanel.add(statusLabel);
        statusPanel.add(ipLabel);
        statusPanel.add(settingsPanel);
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(closeButton);
        buttonPanel.add(showLogsButton);
        buttonPanel.add(reloadClassesButton);
        buttonPanel.add(recompileClassesButton);
        
        topPanel.add(statusPanel);
        topPanel.add(buttonPanel);
    }

    /**
     * Sets up the window (size, minimum size, layout, 'resizability')
     */
    private void setUpWindow() {
        this.setSize(1280, 768);
        this.setMinimumSize(new Dimension(1280, 768));
        this.setLayout(new BorderLayout());
        this.setResizable(false);
    }

    /**
     * Sets up the game information panel on the right.
     */
    private void setUpGameInformationPanel() {
        gameInformationPanel = new JPanel(new BorderLayout());
        gameInformationPanel.setBorder(BorderFactory.createTitledBorder(new MetalBorders.TextFieldBorder(), "Selected Game"));
        JPanel gameInformationButtonPanel = new JPanel(new FlowLayout());
        gameInformationPanel.add(gameInformationButtonPanel, BorderLayout.NORTH);

        JPanel rightPanelContentPanel = new JPanel();
        rightPanelContentPanel.setLayout(null);
        idLabel = new JLabel("ID:              ");
        idLabel.setBounds(new Rectangle(10, 0, 400, 20));
        statusLabel = new JLabel("Status:      ");
        statusLabel.setBounds(10,25,400,20);
        gameNameLabel = new JLabel("Game:        ");
        gameNameLabel.setBounds(new Rectangle(10, 50, 400, 20));
        versionLabel = new JLabel("Version:        ");
        versionLabel.setBounds(new Rectangle(10, 75, 400, 20));        
        partyLabel = new JLabel("Party name:  ");
        partyLabel.setBounds(new Rectangle(10, 100, 400, 20));
        publicGameLabel = new JLabel("Public party:  ");
        publicGameLabel.setBounds(new Rectangle(10, 125, 400, 20));
        participantsLabel = new JLabel("Participants:");
        participantsLabel.setBounds(new Rectangle(10, 150, 400, 20));
        
        participantsList = new JList();
        participantsModel = new DefaultListModel();
        participantsList.setModel(participantsModel);
        particiantsScrollPane = new JScrollPane(participantsList);
        particiantsScrollPane.setBounds(10, 170, 190, 165);        
        
        

        endGameButton = new JButton("End game");
        endGameButton.setEnabled(false);
        endGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameManager.terminateGame(selectedGame, "Termination requested by administrator.");
            }
        });

        gameInformationButtonPanel.add(endGameButton);
        rightPanelContentPanel.add(idLabel);
        rightPanelContentPanel.add(statusLabel);
        rightPanelContentPanel.add(gameNameLabel);
        rightPanelContentPanel.add(versionLabel);
        rightPanelContentPanel.add(participantsLabel);
        rightPanelContentPanel.add(partyLabel);
        rightPanelContentPanel.add(publicGameLabel);
        rightPanelContentPanel.add(particiantsScrollPane);
        
        gameInformationPanel.add(rightPanelContentPanel, BorderLayout.CENTER);
    } 

    /**
     * Sets up the logging panel on the left.
     */
    private void setUpLoggingPanel() {
        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font("Dialog", Font.PLAIN, 12));
        logTextArea.setWrapStyleWord(true);
        this.logListener = new LimitLinesDocumentListener(ServerConfiguration.getInstance().getLinesToLog());
        logTextArea.getDocument().addDocumentListener(this.logListener); // Limits the number of lines
        logTextAreaScrollPane = new JScrollPane(logTextArea);
        logTextAreaScrollPane.setBorder(BorderFactory.createTitledBorder(new MetalBorders.TextFieldBorder(), "Server Log"));
        Logger rootLogger = Logger.getLogger("");
        rootLogger.addHandler(new LoggerTextAreaHandler(logTextArea));        
    }

    /**
     * Sets up the game lists (waiting and running) in the lower middle part.
     */
    private void setUpGameList() {
        listContent = new Vector<Game>();
        waitingListContent = new Vector<Game>();
        gameList = new JList(listContent);
        waitingGameList = new JList(waitingListContent);
        gameListScrollPane = new JScrollPane(gameList);
        waitingGameListScrollPane = new JScrollPane(waitingGameList);
        gameListScrollPane.setBorder(BorderFactory.createTitledBorder(new MetalBorders.TextFieldBorder(), "Active Games"));
        waitingGameListScrollPane.setBorder(BorderFactory.createTitledBorder(new MetalBorders.TextFieldBorder(), "Waiting Games"));
        
        gameList.addListSelectionListener(new ServerGUI.GameListSelectionListener(gameNameLabel, versionLabel, participantsList, idLabel, partyLabel, publicGameLabel,statusLabel, endGameButton, false));
        waitingGameList.addListSelectionListener(new ServerGUI.GameListSelectionListener(gameNameLabel, versionLabel, participantsList, idLabel, partyLabel, publicGameLabel, statusLabel, endGameButton, true));
    }
 
    /**
     * Sets up the list with available (not running) games on top left.
     */    
    private void setUpAvailableGames()
    {
        availableGamesContent = new Vector<>();
        availableGamesList = new JList(availableGamesContent);
        availableGamesListScrollPane = new JScrollPane(availableGamesList);
        availableGamesListScrollPane.setBorder(BorderFactory.createTitledBorder(new MetalBorders.TextFieldBorder(), "Available Games"));
    }
    
    /**
     * Sets up the list of users (n top right.<br>
     * If anonymous login is enbaled, only adHoc created users will be shown in this list.<br>
     * Otherwise, all persistent users (logged in or not) and doppelgangers (logged in) will be shown.
     */        
    private void setUpActiveUsers()
    {
        activeUsersContent = new Vector<>();
        activeUsersList = new JList(activeUsersContent);
        activeUsersListScrollPane = new JScrollPane(activeUsersList);
        activeUsersListScrollPane.setBorder(BorderFactory.createTitledBorder(new MetalBorders.TextFieldBorder(), "Active Users"));      
    }
   
    /**
     * Sets up the ActionListerners for reload and recompile buttons
     */
    private void setupActionListeners()
    {
        reloadClassesButton.addActionListener(new ReloadClassesAction(availableGamesContent, availableGamesList));  // No listener. Only list will be updated
        recompileClassesButton.addActionListener(new RecompileClassesAction(availableGamesContent, availableGamesList));
    }
    
    /**
     * Loads all users to the user list (top right)
     */
    private void loadUsers()
    {
            this.removeAllUsersFromList();
            for(int i = 0; i < User.users.size(); i++)
            {
                if (ServerConfiguration.getInstance().getIsAnonymousLoginAllowed() == true)
                {
                    if (User.users.get(i).isNonPersistentUser() == true)
                    {
                        this.addUserToList(User.users.get(i)); // Add only non-persistent users
                    }
                }
                else
                {
                    this.addUserToList(User.users.get(i)); // Add all users
                }
            }
    }
      
    /**
     * Handles the click on the settings button
     */
    private class SettingsAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            SettingsWindow sw = new SettingsWindow();
            sw.setVisible(true);
            loadUsers();                                                        // reload all users after the settings
        }
    }
    
    /**
     * Handles the click on the user management button
     */
    private class UserSettingsAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            UserSettingsWindow sw = new UserSettingsWindow();
            sw.setVisible(true);                                                         
        }
    
}
    
    /**
     * Handles the click event on the close button
     */
    private class CloseServerAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {      
                int result = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to close the application?",
                        "Close the application?",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }        
    }

    /**
     * Handles user interaction in the game list. Shows current information
     * about the selected game and allows to use certain actions
     */
    private class GameListSelectionListener implements ListSelectionListener {

        private final JLabel gameNameLabel;
        private final JList participantsList;
        private final JLabel idLabel;
        private final JLabel partyLabel;
        private final JLabel publicGamelabel;
        private final JLabel statusLabel;
        private final JLabel versionLabel;
        private final JButton endGameButton;
        private boolean waiting;

        private GameListSelectionListener(JLabel gameNameLabel, JLabel versionLabel, JList participantsList, JLabel idLabel, JLabel partyLabel, JLabel publicGameLabel, JLabel statusLabel, JButton endGameButton, boolean waiting) {
            this.idLabel = idLabel;
            this.versionLabel = versionLabel;
            this.gameNameLabel = gameNameLabel;
            this.participantsList = participantsList;
            this.partyLabel = partyLabel;
            this.publicGamelabel = publicGameLabel;
            this.statusLabel = statusLabel;
            this.endGameButton = endGameButton;
            this.waiting = waiting;
        }
        
        @Override
        public void valueChanged(ListSelectionEvent e) {
            int index;
            if (waiting == true)
            {
                index = waitingGameList.getSelectedIndex();
                gameList.clearSelection();
                waitingGameList.setSelectedIndex(index);
                statusLabel.setText("Status:           Waiting") ;
                
            }
            else
            {
                index = gameList.getSelectedIndex();
                waitingGameList.clearSelection();
                gameList.setSelectedIndex(index);
                statusLabel.setText("Status:           Active") ;
            }
            ServerGUI.getInstance().selectedGame = ServerGUI.getInstance().getGameFromListByListIndex(index, waiting);  // waiting indicates from which list the value is
            DefaultListModel model = (DefaultListModel)participantsList.getModel();
            model.removeAllElements();            
            if(index != -1)
            {
                if (selectedGame.getVersionString() == null)
                {
                    versionLabel.setText("Version:         Unversioned");
                }
                else
                {
                    versionLabel.setText("Version:         " + selectedGame.getVersionString());
                }
                endGameButton.setEnabled(true);
                idLabel.setText("ID:                  " + Long.toString(selectedGame.getId()));
                gameNameLabel.setText("Game:            " + selectedGame.getGameName());
                partyLabel.setText("Party name:  " + selectedGame.getPartyName());
                publicGameLabel.setText("Public party:  " + Boolean.toString(!selectedGame.isPrivateGame()));

                for (Player p : selectedGame.getPlayers()) {
                    model.addElement(p.getName() + " (ID:" + p.getId() +")");
                }
                //participantsLabel.setText(participants);
            }else
            {
                endGameButton.setEnabled(false);
                statusLabel.setText("Status:           ") ;
                idLabel.setText("ID:              " );
                gameNameLabel.setText("Game:        ");
                versionLabel.setText("Version:        ");
                partyLabel.setText("Party name:  ");
                publicGameLabel.setText("Public party:  ");
            }
        }
    }
    }
    
