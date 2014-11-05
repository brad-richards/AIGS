package org.fhnw.aigs.swingClient.GUI;

import java.awt.Font;
import java.awt.Rectangle;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A simple screen which can be shown, when the winning conditions have been
 * met. The easiest way to achieve this would be to use the
 * {@link BaseGameWindow#setContent(javax.swing.JPanel)} method.
 *
 * @author Matthias Stöckli
 * @version v1.0
 */
public class GameEndWindow extends JPanel{

    /**
     * Create a new instance of the GameEndWindow and create a label based on
     * the winner's name.
     *
     * @param winner The winner of the game.
     */
    public GameEndWindow(String winner) {        
        this.setLayout(null);
        
        JLabel loadingLabel = new JLabel(winner + " won!");
        Font scoreFont = new Font("Arial", Font.BOLD, 32);
        loadingLabel.setBounds(new Rectangle(20, 160, 400, 50));
        loadingLabel.setFont(scoreFont);
        
        this.add(loadingLabel);
    }

}
