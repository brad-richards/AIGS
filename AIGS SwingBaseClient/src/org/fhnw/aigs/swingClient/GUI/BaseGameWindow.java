package org.fhnw.aigs.swingClient.GUI;

import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.fhnw.aigs.commons.GameMode;
import org.fhnw.aigs.swingClient.communication.Settings;
import org.fhnw.aigs.swingClient.gameHandling.ClientGame;


/**
 * This class provides represents the "frame" around a game. It is structured in
 * the following way:<br><ul>
 * <li>On the top, there is the {@link header}. It takes up 100% width and 10%
 * height.You can edit it's content via {@link BaseHeader#setGameNameText} and
 * {@link BaseHeader#setStatusLabelText}, see {@link BaseHeader}.</li>
 * <li>In the middle there is the actual content, the game itself. This will
 * take up 85 percent of the space. The content can be accessed via
 * {@link BaseGameWindow#getContent}.</li>
 * </ul><br>
 * v1.0 Initial release<br>
 * v1.1 Change of handling and UI improvements
 *
 * @author Matthias Stöckli (v1.0)
 * @version v1.1 (Raphael Stoeckli, 22.10.2014)
 */
public class BaseGameWindow extends JFrame {

    /**
     * The header, contains the game title and a status label which can be
     * modified
     */
    private BaseHeader header;
    /**
     * The actual game content, most of the times an intance of a class
     * inheriting of {@link BaseBoard}. Use the
     * {@link BaseGameWindow#setContent} to set the content.
     */
    private JPanel content;
    /**
     * The content of the window. It can be changed with
     * {@link BaseGameWindow#setContent(javax.swing.JPanel)} if needed.
     */

    private final BackgroundPanel contentPanel;
        
    /**
     * The title of the Window
     */
    private String title;    

    /**
     * Creates a new BaseGameWindow.
     * @param title The title of the game which will be displayed as the
     * window's title.
     */
    public BaseGameWindow(String title) {
        super(title);
        this.title = title;

        // A small hack to get a frame with an actual content size of 800, see
        // http://stackoverflow.com/questions/2451252/swing-set-jframe-content-area-size
        JFrame temp = new JFrame();
        temp.pack();
        Insets insets = temp.getInsets();
        temp = null;
        this.setPreferredSize(new Dimension(insets.left + insets.right + 790,
                    insets.top + insets.bottom + 790));
        
        this.setResizable(false);
        this.getContentPane().setLayout(null);
        

        // Create and place header (10%)
        header = new BaseHeader(title, new Dimension(800, 60));
        

        
        // Left border
        BackgroundPanel leftSpace = new BackgroundPanel();
        leftSpace.setBackgroundImage("/Assets/BasePatterns/gray_sand.png");
        leftSpace.setBounds(0, 40, 40, 740);

        
        // Right border
        BackgroundPanel rightSpace = new BackgroundPanel();
        rightSpace.setBounds(760, 40, 40, 740);
        rightSpace.setBackgroundImage("/Assets/BasePatterns/gray_sand.png");

        // Bottom border
        BackgroundPanel bottomSpace = new BackgroundPanel();
        bottomSpace.setBounds(0, 780, 800, 20);
        bottomSpace.setBackgroundImage("/Assets/BasePatterns/grey_wash_wall.png");

        // Content
        contentPanel = new BackgroundPanel();
        contentPanel.setBounds(40,60,720,720);
        contentPanel.setBackgroundImage("/Assets/BasePatterns/light_honeycomb.png");
        contentPanel.setLayout(null);
        
        this.add(header);
        this.add(leftSpace);
        this.add(rightSpace);
        this.add(bottomSpace);
        this.add(contentPanel);
        this.setVisible(true);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    /** See {@link BaseGameWindow#header}.*/
    public BaseHeader getHeader() {
        return this.header;
    }

    /** See {@link BaseGameWindow#content}.*/
    public JPanel getContent() {
        return this.content;
    }

    /** See {@link BaseGameWindow#content}.*/
    public void setContent(JPanel newContent) {
        if(this.content != null){
            removeContent();
        }

        this.content = newContent;
        this.content.setBounds(0, 0, 720, 720);
        contentPanel.add(content);
        repaint();
        pack();
    }

    /** See {@link BaseGameWindow#header}.*/
    public void setHeader(BaseHeader header) {
        this.header = header;
    }

    /** See {@link BaseGameWindow#content}.*/
    public void removeContent() {
        if(content != null){
            this.contentPanel.remove(content);
            this.content = null;
        }

    }
    
    /**
     * Initializes the game.<br>
     * This method will call the settings window if no settings are defined or
     * if the window is configured to be visible at every startup.<br>
     * Depending on the settings, the setup window or the loading window 
     * will be enabled. An auomatic connection will be established in case
     * of the loading window. Call this method in the <b>main()</b> method of your game 
     * (in Main.java or a similar calss containing the main method)
     * @param content The main pane of the game. This parameter is unused (only for compatibility reason)
     * @param clientGame The client game object of the game
     * @since v1.1
     * @deprecated You can use directly {@link BaseGameWindow#initGame(org.fhnw.aigs.swingClient.gameHandling.ClientGame)}
     */
    public void initGame(JPanel content, ClientGame clientGame)
    {
        initGame(clientGame);                                                   // Call the actual method
    }
    
    /**
     * Initializes the game.<br>
     * This method will call the settings window if no settings are defined or
     * if the window is configured to be visible at every startup.<br>
     * Depending on the settings, the setup window or the loading window 
     * will be enabled. An auomatic connection will be established in case
     * of the loading window. Call this method in the <b>main()</b> method of your game 
     * (in Main.java or a similar calss containing the main method)
     * @param clientGame The client game object of the game
     * @since v1.1
     */
    public void initGame(ClientGame clientGame)
    {
        Settings.tryLoadSettings(true);                                         // Open Settings window, if defined
        if (clientGame.getGameMode() == GameMode.SinglePlayer || Settings.getInstance().getAutoConnect() == true)
        {
            SetupWindow dummy = new SetupWindow(clientGame);                    // Will only create a waiting screen, because no setup is needed
                                                                                // The dummy instance will automatically load the waiting screen (no need to use setContent)
        }
        else
        {
            this.setContent(new SetupWindow(clientGame));                       // Will create a setup screen
        }
        if (clientGame.getVersionString() != null)
        {
            this.setTitle(this.title + " " + clientGame.getVersionString());
        }
    }    

}
