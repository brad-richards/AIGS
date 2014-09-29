package org.fhnw.aigs.swingClient.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * This class provides represents the "frame" around a game. It is structured in
 * the following way:<br><ul>
 * <li>On the top, there is the {@link header}. It takes up 100% width and 10%
 * height.You can edit it's content via {@link BaseHeader#setGameNameText} and
 * {@link BaseHeader#setStatusLabelText}, see {@link BaseHeader}.</li>
 * <li>In the middle there is the actual content, the game itself. This will
 * take up 85 percent of the space. The content can be accessed via
 * {@link BaseGameWindow#getContent}.</li>
 * </ul>
 *
 * @author Matthias Stöckli
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
     * Creates a new BaseGameWindow.
     * @param title The title of the game which will be displayed as the
     * window's title.
     */
    public BaseGameWindow(String title) {
        super(title);

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

}
