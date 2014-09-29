package org.fhnw.aigs.TicTacToeSwing.client;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.fhnw.aigs.TicTacToeSwing.commons.TicTacToeSymbol;

/**
 * A simple {@link javax.swing.JPanel } which can contain a cross or a nought symbol.
 * @author Matthias Stöckli 
 */
public class TicTacToePane extends JPanel{
    
    /**
     * Represents the field as a button.
     */
    private JButton panelButton;
    private int xPosition;
    private int yPosition;
    
    
    /**
     * Creates a new instance of TicTacToePane.
     * @param xPosition The x-position of the field.
     * @param yPosition The y-Position of the feld.
     */
    public TicTacToePane(int xPosition, int yPosition){
        this.setLayout(new BorderLayout());
        panelButton = new JButton();
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.add(panelButton);
    }
    
    /**
     * The image (cross or nought)
     */
    private final Icon symbolIcon = null;
    
    
    /**
     * Sets the image view to the respective symbol (cross or nought)
     * @param playerSymbol The symbol to be set
     */
    public void setPlayerSymbol(TicTacToeSymbol playerSymbol){
        Image img = null;
        
        // Load the image depending on the symbol
        if(playerSymbol == TicTacToeSymbol.Cross){
            img = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/Assets/Images/cross.png"));
        }else if(playerSymbol == TicTacToeSymbol.Nought){
            img = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/Assets/Images/nought.png"));
        }
        
        if(img != null)
            panelButton.setIcon(new ImageIcon(img));
        
    }

    /**
     * Gets the button which represents the GUI element.
     * @return The button.
     */
    public JButton getButton() {
        return panelButton;
    }

    /**
     * Get the x-position of this element.
     * @return The x-position.
     */
    public int getXPosition() {
        return xPosition;
    }

    /**
     * Get the y-position of this element.
     * @return The y-position.
     */
    public int getYPosition() {
        return yPosition;
    }
  
    
}
