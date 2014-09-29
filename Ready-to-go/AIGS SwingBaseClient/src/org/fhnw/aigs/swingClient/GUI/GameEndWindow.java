package org.fhnw.aigs.swingClient.GUI;

import java.awt.Font;
import java.awt.Rectangle;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameEndWindow extends JPanel{


    public GameEndWindow(String winner) {        
        this.setLayout(null);
        
        JLabel loadingLabel = new JLabel(winner + " won!");
        Font scoreFont = new Font("Arial", Font.BOLD, 32);
        loadingLabel.setBounds(new Rectangle(20, 160, 400, 50));
        loadingLabel.setFont(scoreFont);
        
        this.add(loadingLabel);
    }

}
