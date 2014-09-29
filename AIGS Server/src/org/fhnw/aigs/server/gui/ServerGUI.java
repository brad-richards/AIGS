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
import java.util.logging.Level;
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
import org.fhnw.aigs.server.communication.ServerCommunication;
import org.fhnw.aigs.server.gameHandling.GameManager;
import org.fhnw.aigs.server.gameHandling.LoggerTextAreaHandler;
import org.fhnw.aigs.server.gameHandling.RecompileClassesAction;
import org.fhnw.aigs.server.gameHandling.ReloadClassesAction;
import org.fhnw.aigs.server.gameHandling.ServerConfiguration;
import org.fhnw.aigs.server.gameHandling.ShowLogsAction;
import org.fhnw.aigs.server.gameHandling.StartServerAction;
import org.fhnw.aigs.server.gameHandling.StopServerAction;

/**
 * This is the server's GUI. There are total of three information panels:
 * <ul>
 * <li>A text field showing all logs.</li>
 * <li>A list of all running games.</li>
 * <li>An information panel about the running games.</li>
 * </ul>
 * <br>
 * Additionally there are four buttons:
 * <ul>
 * <li>A button to start the server. </li>
 * <li>A button to close/terminate the server. </li>
 * <li>A button to show the folder where the logs are locatet</li>
 * <li>A button to reload and recompile all games classes</li>
 * </ul>
 * <br>
 * Please not that some functionality is implemented in subclasses, e.g.
 * the server start.
 * @author Matthias Stöckli
 */
public class ServerGUI extends JFrame {

    public static Game selectedGame;
    private static JList gameList;
    private static JScrollPane gameListScrollPane;
    private static JList waitingGameList;
    private static JScrollPane waitingGameListScrollPane;    
    private static JList availableGamesList;
    private static JScrollPane availableGamesListScrollPane;    
    private static JTextArea logTextArea;
    private static JScrollPane logTextAreaScrollPane;
    private static JPanel gameInformationPanel;  
    private static JPanel topPanel;
    private static JPanel statusPanel;
    private static JPanel buttonPanel;        
    private static Vector<Game> listContent;
    private static Vector<Game> waitingListContent;
    private static Vector<String> availableGamesContent;
    private JButton startButton;
    private JButton stopButton;
    private JButton reloadClassesButton;
    private JButton recompileClassesButton;
    private JLabel statusLabel;
    private JLabel gameNameLabel;
    private JLabel participantsLabel;
    
    private static JList participantsList;
    private static JScrollPane particiantsScrollPane;      
    private static ListModel participantsModel;
    
    private JLabel partyLabel;
    private JLabel idLabel;
    private JLabel ipLabel;
    private JButton endGameButton;
    
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
       
        setupActionListeners();
        
        logTextAreaScrollPane.setBounds(10,0,650,615);
        gameInformationPanel.setBounds(1020,200,220,415);
        allGamesPanel.setBounds(670,200,340, 415);
        availableGamesListScrollPane.setBounds(670, 0, 570, 190);
        
        allGamesPanel.add(gameListScrollPane);
        allGamesPanel.add(waitingGameListScrollPane);
        
        middlePanel.add(logTextAreaScrollPane);
        middlePanel.add(allGamesPanel);
        middlePanel.add(gameInformationPanel);
        middlePanel.add(availableGamesListScrollPane);
        
        
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
    public static void addGameToList(Game game, boolean waiting) {
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
    public static void removeGameFromList(Game game, boolean waiting) {
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
     * Returns the game having a specified index in the game list.
     *
     * @param listIndex The position/index in the list.
     * @param waiting If true, the source ist the list of waiting games, otherwise the list of active games
     * @return Game at the specified position/index.
     */
    public static Game getGameFromListByListIndex(int listIndex, boolean waiting) {
        if (waiting == true)
        {
            if(listIndex >= 0){
                return waitingListContent.elementAt(listIndex);
            }else{
                return null;
            }
        }
        else
        {
            if(listIndex >= 0){
                return listContent.elementAt(listIndex);
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
            Logger.getLogger(ServerGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * In this method all necessary steps are taken to set up the upper part of
     * the GUI: Butttons and labels are added.
     */
    private void setUpTopPanel() {
        topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
        statusPanel = new JPanel(new GridLayout(1,3));
        buttonPanel = new JPanel(new GridLayout(1,5));
        
        
        this.add(topPanel, BorderLayout.NORTH);
        ImageIcon logoImage = new ImageIcon(getClass().getResource("/imgs/logo.png"));
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

        JButton closeButton = new JButton("Close AIGS");
        closeButton.addActionListener(new ServerGUI.CloseServerAction());

        JButton showLogsButton = new JButton("Show Logs");
        showLogsButton.addActionListener(new ShowLogsAction());
        
        reloadClassesButton = new JButton("Reload classes");     

        recompileClassesButton = new JButton("Recompile and load classes");
        
        statusPanel.add(logoLabel);
        statusPanel.add(statusLabel);
        statusPanel.add(ipLabel);
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
        participantsLabel = new JLabel("Participants:");
        participantsLabel.setBounds(new Rectangle(10, 75, 400, 20));
        
        participantsList = new JList();
        participantsModel = new DefaultListModel();
        participantsList.setModel(participantsModel);
        particiantsScrollPane = new JScrollPane(participantsList);
        particiantsScrollPane.setBounds(10, 95, 190, 165);
        
        partyLabel = new JLabel("Party name:  ");
        partyLabel.setBounds(new Rectangle(10, 260, 400, 40));

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
        rightPanelContentPanel.add(participantsLabel);
        rightPanelContentPanel.add(particiantsScrollPane);
        rightPanelContentPanel.add(partyLabel);
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
        logTextAreaScrollPane = new JScrollPane(logTextArea);
        logTextAreaScrollPane.setBorder(BorderFactory.createTitledBorder(new MetalBorders.TextFieldBorder(), "Server Log"));
        Logger rootLogger = Logger.getLogger("");
        rootLogger.addHandler(new LoggerTextAreaHandler(logTextArea));        
    }

    /**
     * Sets up the game list in the middle.
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
        
        gameList.addListSelectionListener(new ServerGUI.GameListSelectionListener(gameNameLabel, participantsList, idLabel, partyLabel, statusLabel, endGameButton, false));
        waitingGameList.addListSelectionListener(new ServerGUI.GameListSelectionListener(gameNameLabel, participantsList, idLabel, partyLabel, statusLabel, endGameButton, true));
    }
 
    /**
     * Sets up the list with available (not running) games on top.
     */    
    private void setUpAvailableGames()
    {
        availableGamesContent = new Vector<>();
        availableGamesList = new JList(availableGamesContent);
        availableGamesListScrollPane = new JScrollPane(availableGamesList);
        availableGamesListScrollPane.setBorder(BorderFactory.createTitledBorder(new MetalBorders.TextFieldBorder(), "Available Games"));
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
     * Handles the click event on the close button
     */
    private static class CloseServerAction implements ActionListener {

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
    private static class GameListSelectionListener implements ListSelectionListener {

        private final JLabel gameNameLabel;
        private final JList participantsList;
        private final JLabel idLabel;
        private final JLabel partyLabel;
        private final JLabel statusLabel;
        private final JButton endGameButton;
        private boolean waiting;

        private GameListSelectionListener(JLabel gameNameLabel, JList participantsList, JLabel idLabel, JLabel partyLabel, JLabel statusLabel, JButton endGameButton, boolean waiting) {
            this.idLabel = idLabel;
            this.gameNameLabel = gameNameLabel;
            this.participantsList = participantsList;
            this.partyLabel = partyLabel;
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
                statusLabel.setText("Status:      Waiting") ;
            }
            else
            {
                index = gameList.getSelectedIndex();
                statusLabel.setText("Status:      Active") ;
            }
            ServerGUI.selectedGame = ServerGUI.getGameFromListByListIndex(index, waiting);  // waiting indicates from which list the value is
            DefaultListModel model = (DefaultListModel)participantsList.getModel();
            model.removeAllElements();            
            if(index != -1){
                endGameButton.setEnabled(true);
            idLabel.setText("ID:              " + Long.toString(selectedGame.getId()));
            gameNameLabel.setText("Game:        " + selectedGame.getGameName());
            partyLabel.setText("Party name:  " + selectedGame.getPartyName());

            for (Player p : selectedGame.getPlayers()) {
                model.addElement(p.getName() + " (ID:" + p.getId() +")");
            }
            //participantsLabel.setText(participants);
            }else{
                endGameButton.setEnabled(false);
                idLabel.setText("ID:              " );
                gameNameLabel.setText("Game:        ");
                partyLabel.setText("Party name:  ");
            }
        }
    }
}
    
