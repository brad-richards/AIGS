package org.fhnw.aigs.swingClient.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Provides a simple header for the GameWindow.
 * It shows the title of the game and a status which can be defined with
 * the method {@link BaseHeader#setStatusLabelText(java.lang.String)}.
 * @author Matthias Stöckli
 */
public class BaseHeader extends BackgroundPanel{
    private final JLabel gameNameLabel;
    private final JLabel statusLabel;
    private final JButton settingsButton;
 //   private final Image headerImage;
    
    /**
     * Create a new instance of BaseHeader.
     * @param gameName The name of the game which will be showed in a label.
     */
    public BaseHeader(String gameName, Dimension headerDimension){
        setBackgroundImage("/Assets/BasePatterns/grey_wash_wall.png");
        this.setBounds(0, 0, headerDimension.width, headerDimension.height);
        this.setLayout(null);


        Font font = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
                //InputStream input = this.getClass().getResourceAsStream(gameName)
                InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("Assets/Fonts/AeroviasBrasilNF.ttf");
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, input));
                font = new Font("Aerovias Brasil NF", Font.PLAIN, 42);
       } catch (FontFormatException | IOException ex) {
            font = new Font("ARIAL", Font.PLAIN, 42);
            Logger.getLogger(BaseHeader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        gameNameLabel = new JLabel(gameName);
        gameNameLabel.setFont(font);
        gameNameLabel.setForeground(Color.WHITE);
        gameNameLabel.setBounds(40,10,240, 50);
        
        statusLabel = new JLabel();     
        statusLabel.setFont(font);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBounds(400,10,500, 50);
        
        settingsButton = new JButton();
        settingsButton.setBounds(755, 10, 28, 28);
        java.net.URL imageUrl = this.getClass().getResource("/Assets/BasePatterns/settings.png");
        Icon ico = new ImageIcon(imageUrl);
        settingsButton.setIcon(ico);
        
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsWindow settingsWindow = new SettingsWindow();
                settingsWindow.setVisible(true);
            }
        });
        
        this.add(gameNameLabel);
        this.add(statusLabel);
        this.add(settingsButton);
    }
    
    /**
     * Allows to set the text of the title label.
     * @param text The text to be display
     */
    public void setGameNameText(final String text) {
        this.gameNameLabel.setText(text);
    }    
    
    /**
     * Allows to set the text of the status label.
     * Most commonly something like "Your turn" will be displayed here.
     * @param text The text to be display
     */
    public void setStatusLabelText(final String text){
        this.statusLabel.setText(text);
    }
  
}
