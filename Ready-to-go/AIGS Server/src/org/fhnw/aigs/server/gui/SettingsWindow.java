package org.fhnw.aigs.server.gui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerNumberModel;
import org.fhnw.aigs.server.common.LogRouter;
import org.fhnw.aigs.server.common.LoggingStyle;
import org.fhnw.aigs.server.common.LoggingThreshold;
import org.fhnw.aigs.server.common.ServerConfiguration;


/**
 * This class represents a window to manage the server settings. These settings 
 * will be stored in {@link ServerConfiguration#instance}<br>
 * v1.0 Initial release<br>
 * v1.1 Functional changes (added fields / removed fields)
 * @author Raphael Stoeckli (24.02.2015)
 * @version 1.1
 */
public class SettingsWindow extends JDialog{ 
        
    private JCheckBox anonmousLoginCheckbox;
    private JCheckBox consoleModeCheckbox;
    private JCheckBox multipleLoginCheckbox;
    private JCheckBox keepAliveManagerCheckbox;
    private JCheckBox hideOnCloseCheckbox;
    private JTextPane gamelibsDirectoryField;
    private JTextPane gamesDirectoryField;
    private JTextPane logsDirectoryField;
    private JTextPane whatIsMyIpField;
    private JSpinner keepAliveTimeoutSpinner;
    private JSpinner portNumberSpinner;
    private JSpinner linesToLogSpinner;
    private JComboBox loggingThresholdComboBox;
    private JComboBox loggingStyleComboBox;
    private JButton saveButton;
    private JButton cancelButton;
   
    
    /**
     * Standrd constructor without parameters
     */
    public SettingsWindow()
    {
        ImageIcon logoImage = new ImageIcon(getClass().getResource("/imgs/logo24px.png"));
        this.setIconImage(logoImage.getImage());
        this.setLocationByPlatform(true); // Better positioning of the window
        this.setResizable(false);  
        this.setAlwaysOnTop(true);
        this.setModal(true);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("AIGS Server - Settings");
        this.setPreferredSize(new java.awt.Dimension(720, 360));
        init();
        loadSettings();
    }
    
    /**
     * Initilizes the UI. The lower part of this method was genereated by the GUI builder
     * of NetBeans and cleaned up by hand
     */
    private void init()
    {
        anonmousLoginCheckbox = new JCheckBox("Disable login (anonymous login with AdHoc users instead)");
        anonmousLoginCheckbox.setToolTipText("<html>If checked, the user management is not used. In this case, login is possible with every user name and without an identification code.<br>AdHoc users will be created instead. These users will be automatically removed after disconnecting from the server.</html>");
        multipleLoginCheckbox = new JCheckBox("Allow multiple logins of one user");
        multipleLoginCheckbox.setToolTipText("If checked, a user can login multiple times, otherwise all further login attempts will be refused by the server");
        hideOnCloseCheckbox = new JCheckBox("Hide on window close (no termination)");
        hideOnCloseCheckbox.setToolTipText("If checked, the server programm will be minimized to the systray when clicking on the X-Button and not terminated");
        consoleModeCheckbox = new JCheckBox("Console-Modus (require program restart)");
        consoleModeCheckbox.setToolTipText("If checked, the server programm will run in a terminal window at the next startup");
        keepAliveManagerCheckbox = new JCheckBox("Use KeepAlive manager");
        keepAliveManagerCheckbox.setToolTipText("<html>If checked, the server will check whether clients are online within the keepAlive timeout by listening to a heartbeat signal.<br> If a client is not sending the signal within the timespan, the server will disconnect this client");
        logsDirectoryField = new JTextPane();
        logsDirectoryField.setToolTipText("Directory where log files are stored");
        gamesDirectoryField = new JTextPane();
        gamesDirectoryField.setToolTipText("Directory where game projects for recompiling are located");
        whatIsMyIpField = new JTextPane();
        whatIsMyIpField.setToolTipText("URL to a website which returns the referrer IP address of the server as plain text");
        gamelibsDirectoryField = new JTextPane();
        gamelibsDirectoryField.setToolTipText("Directory where game libraries are located");
        keepAliveTimeoutSpinner = new JSpinner();
        keepAliveTimeoutSpinner.setToolTipText("Milliseconds to wait until the server is disconnecting a not responding client. Applies to the KeepAlive option");
        keepAliveTimeoutSpinner.setModel(new SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        portNumberSpinner = new JSpinner();
        portNumberSpinner.setToolTipText("TCP Port to communicate with the AIGS server");
        portNumberSpinner.setModel(new SpinnerNumberModel(1, 1, 65535, 1));
        portNumberSpinner.setEditor(new JSpinner.NumberEditor(portNumberSpinner, "#"));
        linesToLogSpinner = new JSpinner();
        linesToLogSpinner.setToolTipText("Number of lines to show in the logging window");
        linesToLogSpinner.setModel(new SpinnerNumberModel(1, 1, 10000, 1));
        linesToLogSpinner.setEditor(new JSpinner.NumberEditor(linesToLogSpinner, "#"));
        loggingThresholdComboBox = new JComboBox(LoggingThreshold.values());
        loggingThresholdComboBox.setToolTipText("Level (threshold) of logging");
        loggingStyleComboBox = new JComboBox(LoggingStyle.values());
        loggingStyleComboBox.setToolTipText("Style (also level of detail) of logging");        
        
        cancelButton = new JButton("Cancel");
        cancelButton.setToolTipText("Discard all changes and close");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        }); 
        
        saveButton = new JButton("Save");
        saveButton.setToolTipText("Save the changes an close");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        JLabel linesToLogLabel = new JLabel("No. of lines in log window:");
        JLabel loggingThresholdLabel = new JLabel("Logging threshold:");
        JLabel loggingStyleLabel = new JLabel("Logging style:");
        JLabel logsDirectoryLabel = new JLabel("Logs directory:");
        JLabel gameDirLabel = new JLabel("Game directory (projects):");
        JLabel gameLibsLabel = new JLabel("Game library directory:");
        JLabel keepAliveLabel = new JLabel("KeepAlive timeout (ms):");
        JLabel portNumberLabel = new JLabel("Port number:");  
        JLabel whatIsMyIpLabel = new JLabel("\"What is my IP\" URL:");
        JLabel warningLabel = new JLabel("<html>Warning: After enabling this options, the graphical user <br>\ninterface (GUI) inclusive this dialog will not be visible anymore.<br>\nYou have to edit the ServerConfig.xml manually to turn on<br>\nthe GUI again.</html>");
        warningLabel.setForeground(new java.awt.Color(255, 0, 0));
        
        JScrollPane loggingScrollPane = new JScrollPane();
        loggingScrollPane.setViewportView(logsDirectoryField);
        JScrollPane gameScrollPane1 = new JScrollPane();
        gameScrollPane1.setViewportView(gamelibsDirectoryField);
        JScrollPane gameScrollPane2 = new JScrollPane();
        gameScrollPane2.setViewportView(gamesDirectoryField);
        JScrollPane connectivityScrollPane = new JScrollPane();
        connectivityScrollPane.setViewportView(whatIsMyIpField);       
        
        JPanel loginTitlePanel = new JPanel();
        GroupLayout loginTitlePanelLayout = new GroupLayout(loginTitlePanel);
        loginTitlePanel.setBorder(BorderFactory.createTitledBorder("Login / User"));
        loginTitlePanel.setLayout(loginTitlePanelLayout);
        loginTitlePanelLayout.setHorizontalGroup(
            loginTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(loginTitlePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(loginTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(anonmousLoginCheckbox)
                    .addComponent(multipleLoginCheckbox))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        loginTitlePanelLayout.setVerticalGroup(
            loginTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(loginTitlePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(anonmousLoginCheckbox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(multipleLoginCheckbox)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );        
        
        JPanel loggingTitlePanel = new JPanel();
        GroupLayout loggingTitlePanelLayout = new GroupLayout(loggingTitlePanel);
        loggingTitlePanel.setLayout(loggingTitlePanelLayout);        
        loggingTitlePanel.setBorder(BorderFactory.createTitledBorder("Logging"));
                loggingTitlePanelLayout.setHorizontalGroup(loggingTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(loggingTitlePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(loggingTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(logsDirectoryLabel)
                    .addComponent(linesToLogLabel)
                    .addComponent(loggingStyleLabel)
                    .addComponent(loggingThresholdLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(loggingTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(loggingThresholdComboBox, 0, 182, Short.MAX_VALUE)
                    .addGroup(loggingTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(loggingStyleComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(linesToLogSpinner, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                        .addComponent(loggingScrollPane, GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)))
                .addContainerGap())
        );
        loggingTitlePanelLayout.setVerticalGroup(loggingTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(loggingTitlePanelLayout.createSequentialGroup()
                .addGroup(loggingTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(linesToLogLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(linesToLogSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(loggingTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(loggingTitlePanelLayout.createSequentialGroup()
                        .addComponent(loggingStyleLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 6, GroupLayout.PREFERRED_SIZE))
                    .addComponent(loggingStyleComboBox, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(loggingTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(loggingThresholdLabel)
                    .addComponent(loggingThresholdComboBox, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(loggingTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(logsDirectoryLabel)
                    .addComponent(loggingScrollPane, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)))
        );       
        
        JPanel systemTitlePanel = new JPanel();
        GroupLayout systemTitleLayout = new GroupLayout(systemTitlePanel);
        systemTitlePanel.setLayout(systemTitleLayout);        
        systemTitlePanel.setBorder(BorderFactory.createTitledBorder("System"));
        systemTitleLayout.setHorizontalGroup(
            systemTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(systemTitleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(systemTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(warningLabel, GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                    .addGroup(systemTitleLayout.createSequentialGroup()
                        .addGroup(systemTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(hideOnCloseCheckbox)
                            .addComponent(consoleModeCheckbox))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        systemTitleLayout.setVerticalGroup(
            systemTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(systemTitleLayout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(hideOnCloseCheckbox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(consoleModeCheckbox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warningLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );    
        
        JPanel gameTitlePanel = new JPanel();
        GroupLayout gameTitleLayout = new GroupLayout(gameTitlePanel);
        gameTitlePanel.setLayout(gameTitleLayout);
        gameTitlePanel.setBorder(BorderFactory.createTitledBorder("Game"));
        gameTitleLayout.setHorizontalGroup(
            gameTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(gameTitleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(gameTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(gameDirLabel)
                    .addComponent(gameLibsLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(gameTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(gameScrollPane2)
                    .addComponent(gameScrollPane1))
                .addContainerGap())
        );
        gameTitleLayout.setVerticalGroup(
            gameTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(gameTitleLayout.createSequentialGroup()
                .addGroup(gameTitleLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(gameScrollPane1, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                    .addComponent(gameDirLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(gameTitleLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(gameScrollPane2, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                    .addComponent(gameLibsLabel)))
        );       
        
        JPanel connectivityTitlePanel = new JPanel();
        GroupLayout connectivityTitlePanelLayout = new GroupLayout(connectivityTitlePanel);
        connectivityTitlePanel.setLayout(connectivityTitlePanelLayout);        
        connectivityTitlePanel.setBorder(BorderFactory.createTitledBorder("Connectivity"));
        connectivityTitlePanelLayout.setHorizontalGroup(
            connectivityTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(connectivityTitlePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(connectivityTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(keepAliveManagerCheckbox)
                    .addGroup(connectivityTitlePanelLayout.createSequentialGroup()
                        .addGroup(connectivityTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(keepAliveLabel)
                            .addComponent(portNumberLabel)
                            .addComponent(whatIsMyIpLabel))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(connectivityTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(connectivityScrollPane)
                            .addGroup(connectivityTitlePanelLayout.createSequentialGroup()
                                .addGroup(connectivityTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(portNumberSpinner, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(keepAliveTimeoutSpinner, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        connectivityTitlePanelLayout.setVerticalGroup(
            connectivityTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(connectivityTitlePanelLayout.createSequentialGroup()
                .addComponent(keepAliveManagerCheckbox)
                .addGap(3, 3, 3)
                .addGroup(connectivityTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(keepAliveLabel)
                    .addComponent(keepAliveTimeoutSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(connectivityTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(portNumberLabel)
                    .addComponent(portNumberSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(connectivityTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(connectivityScrollPane, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                    .addComponent(whatIsMyIpLabel)))
        );        
        
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
               layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(loginTitlePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(loggingTitlePanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(gameTitlePanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(connectivityTitlePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(systemTitlePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(saveButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)
                        .addGap(31, 31, 31))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(loginTitlePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(loggingTitlePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gameTitlePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(systemTitlePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(connectivityTitlePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(saveButton)
                            .addComponent(cancelButton))))
                .addContainerGap())
        );
        
       this.addWindowListener(new CloseListener());
        pack();
    }
    
    /**
     * Method to trigger the save action ({@link SettingsWindow#saveSettings(boolean)})
     * @param evt Action event of the calling UI element
     */
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           
        saveSettings(true);
        CloseListener cl = new CloseListener();
        cl.closeWindow(this);        
    }                                          

    /**
     * Method to tigger the closing action of the window
     * @param evt Action event of the calling UI element
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {                                             
        CloseListener cl = new CloseListener();
        cl.closeWindow(this);
    }
    
    /**
     * Method loads the settings from {@link ServerConfiguration#instance}
     */
    private void loadSettings()
    {
      ServerConfiguration conf =  ServerConfiguration.getInstance(); 
      
      this.anonmousLoginCheckbox.setSelected(conf.getIsAnonymousLoginAllowed());
      this.multipleLoginCheckbox.setSelected(conf.getIsMultiLoginAllowed());
      this.hideOnCloseCheckbox.setSelected(conf.getHidesOnClose());
      this.consoleModeCheckbox.setSelected(conf.getIsConsoleMode());
      this.keepAliveManagerCheckbox.setSelected(conf.getUseKeepAliveManager());
      
      this.logsDirectoryField.setText(conf.getLogDirectory());
      this.gamesDirectoryField.setText(conf.getGameSourcesDirectory());
      this.gamelibsDirectoryField.setText(conf.getGamelibsDirectory());
      this.whatIsMyIpField.setText(conf.getWhatIsMyIpUrl());
      this.keepAliveTimeoutSpinner.setValue(conf.getKeepAliveTimeOut());
      this.portNumberSpinner.setValue(conf.getPortNumber());
    
      this.linesToLogSpinner.setValue(conf.getLinesToLog());
      this.loggingStyleComboBox.setSelectedItem(conf.getLoggerStyle());
      this.loggingThresholdComboBox.setSelectedItem(conf.getLoggerThreshold());
      
    }
    
    /**
     * Method stores the settings to {@link ServerConfiguration#instance}
     * @param validate In tru, the values will be validated firstly. If an illegal value ocurred, the storing process will be aborted
     */
    private void saveSettings(boolean validate)
    {
        if (validate == true)
        {
            if (this.logsDirectoryField.getText().length() == 0)
            {
                JOptionPane.showMessageDialog(this, "<html>No logs directory was specified.<br>Please insert a value</html>", "No logs directory", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            try
            {
                File f = new File(this.logsDirectoryField.getText());
                if (f.exists() == false)
                {
                    JOptionPane.showMessageDialog(this, "<html>The defined logs directory does not exist.<br>it will be created with the first log entry.</html>", "Folder does not exist", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(this, "<html>En error occurred when checking the logs directory.<br>Please check the inserted value</html>", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (this.gamesDirectoryField.getText().length() == 0)
            {
                JOptionPane.showMessageDialog(this, "<html>No game project directory was specified.<br>Please insert a value even if no projects are stored in this folder</html>", "No game project directory", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            try
            {
                File f = new File(this.gamesDirectoryField.getText());
                if (f.exists() == false)
                {
                    JOptionPane.showMessageDialog(this, "<html>The defined game project directory does not exist.<br>Please insert a valid path to an existing folder.</html>", "Folder does not exist", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(this, "<html>En error occurred when checking the game project directory.<br>Please check the inserted value</html>", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }            
            
            if (this.gamelibsDirectoryField.getText().length() == 0)
            {
                JOptionPane.showMessageDialog(this, "<html>No game library directory was specified.<br>Please insert a valid path to an existing folder</html>", "No game library directory", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            try
            {
                File f = new File(this.gamelibsDirectoryField.getText());
                if (f.exists() == false)
                {
                    JOptionPane.showMessageDialog(this, "<html>The defined game library directory does not exist.<br>Please insert a valid path to an existing folder.</html>", "Folder does not exist", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(this, "<html>En error occurred when checking the game library directory.<br>Please check the inserted value</html>", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            

            if (this.whatIsMyIpField.getText().length() == 0)
            {
                JOptionPane.showMessageDialog(this, "<html>No URL to check the own IP address was specified.<br>Please insert a valid URL</html>", "No URL", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            try
            {
                URL url = new URL(this.whatIsMyIpField.getText());
                url.toURI();
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(this, "<html>The insertet URL to check the own IP address seems to be not valid.<br>Please check the inserted value</html>", "Invalid URL", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // Validation end                 
        }
       
      String backupLoggerDirectory = ServerConfiguration.getInstance().getLogDirectory();

      ServerConfiguration.getInstance().setIsAnonymousLoginAllowed(this.anonmousLoginCheckbox.isSelected());
      ServerConfiguration.getInstance().setIsMultiLoginAllowed(this.multipleLoginCheckbox.isSelected());
      ServerConfiguration.getInstance().setHidesOnClose(this.hideOnCloseCheckbox.isSelected());
      ServerConfiguration.getInstance().setIsConsoleMode(this.consoleModeCheckbox.isSelected());
      ServerConfiguration.getInstance().setUseKeepAliveManager(this.keepAliveManagerCheckbox.isSelected());
      
      ServerConfiguration.getInstance().setLogDirectory(this.logsDirectoryField.getText());
      ServerConfiguration.getInstance().setGameSourcesDirectory(this.gamesDirectoryField.getText());
      ServerConfiguration.getInstance().setGamelibsDirectory(this.gamelibsDirectoryField.getText());
      ServerConfiguration.getInstance().setWhatIsMyIpUrl(this.whatIsMyIpField.getText());
      
      ServerConfiguration.getInstance().setKeepAliveTimeOut((int)this.keepAliveTimeoutSpinner.getValue());
      ServerConfiguration.getInstance().setPortNumber((int)this.portNumberSpinner.getValue());
      
      ServerConfiguration.getInstance().setLinesToLog((int)this.linesToLogSpinner.getValue());
      ServerConfiguration.getInstance().setLoggerStyle((LoggingStyle)this.loggingStyleComboBox.getSelectedItem());
      ServerConfiguration.getInstance().setLoggerThreshold((LoggingThreshold)this.loggingThresholdComboBox.getSelectedItem());
              
      ServerConfiguration.saveInstance();
      if (this.logsDirectoryField.getText().equals(backupLoggerDirectory) == false && ServerConfiguration.getInstance().getTempLogsDirectory().equals(""))
      {
          JOptionPane.showMessageDialog(this, "<html>The new logs directory will be used after a program restart:<br>" + this.logsDirectoryField.getText() + "</html>", "New logs directory", JOptionPane.INFORMATION_MESSAGE);
          ServerConfiguration.getInstance().setTempLogsDirectory(backupLoggerDirectory); // Write current location back for runtime purpose. Will be changed after restart 
      }
      
      LogRouter.updateRules(); // Update Logging-Rules
      if (ServerGUI.getInstance() != null) // GUI handling
      {
          ServerGUI.getInstance().getLogListener().setLimitLines(ServerConfiguration.getInstance().getLinesToLog()); // Update Lines to Log
      }
      
    }
    
    /**
     * Class to handle the close action of the window 
     */
    private class CloseListener implements WindowListener
    {
        /**
         * Standard constructor
         */
        public CloseListener()
        { }
        
        /**
         * Method to perform the actual closing action. This method can also called 
         * by creating an instance of this class manually
         * @param window 
         */
        public void closeWindow(Window window)
        {
            window.dispose();
        }
        
        /** This method is empty */
        @Override
        public void windowOpened(WindowEvent e) { /* Do nothing */ }
        /**
         * Called, when closing the window
         * @param e Action event of the calling UI element
         */
        @Override
        public void windowClosing(WindowEvent e) {
            this.closeWindow(e.getWindow());
        }
        /** This method is empty */
        @Override
        public void windowClosed(WindowEvent e) { /* Do nothing */ }
        /** This method is empty */
        @Override
        public void windowIconified(WindowEvent e) { /* Do nothing */ }
        /** This method is empty */
        @Override
        public void windowDeiconified(WindowEvent e) { /* Do nothing */ }
        /** This method is empty */
        @Override
        public void windowActivated(WindowEvent e) { /* Do nothing */ }
        /** This method is empty */
        @Override
        public void windowDeactivated(WindowEvent e) { /* Do nothing */ }
    }
    
    
}
