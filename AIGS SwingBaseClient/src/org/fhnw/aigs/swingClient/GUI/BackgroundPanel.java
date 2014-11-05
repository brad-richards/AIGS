package org.fhnw.aigs.swingClient.GUI;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * A simple JPanel which shows a background pattern.
 * 
 * @author Matthias Stöckli
 * @version v1.0
 */
public class BackgroundPanel extends JPanel{
   
  /**
   * The background pattern
   */
  private Image backgroundImage;
 
  /**
   * Create a new instance of BackgroundPanel and set a light honeycomb
   * background as standard tile.
   */
  public BackgroundPanel(){
      setBackgroundImage("/Assets/BasePatterns/light_honeycomb.png");
  }
  
     /**
     * Create a new instance of BackgroundPanel and uses a relative path to get
     * load an image which will be used as a background pattern of this panel.s
     */
    public BackgroundPanel(String imagePath) {
        setBackgroundImage(imagePath);
    }
  
  /**
   * Loop the background image as many times as necessary and draw the images.
   * @param g The graphics object of the instance
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    	int widthTile = backgroundImage.getWidth(null);	
    	int heightTile = backgroundImage.getHeight(null);	
    	int numberOfXTiles = this.getWidth() / widthTile;	
    	int numberOfYTiles = this.getHeight() / heightTile;
    	
    	// go through numberOfXTiles in X and Y + 1 (if the width of the container is not divisible by the width of a tile)
    	for(int i = 0; i < numberOfXTiles + 1; i++){					
    		for(int j = 0; j < numberOfYTiles + 1; j++)
    		{
        	    	g.drawImage(backgroundImage, i * widthTile,j * heightTile, this);       // Draw the picture at the particular positions
                }
    	}
  }
  
  /**
   * Set a new image by using the relative path to an image.
   * @param imagePath The path of the image, e.g. "/Assets/BasePatterns/grey_wash_wall.png".
   */
  public void setBackgroundImage(String imagePath){
          this.backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
      }
  }
    