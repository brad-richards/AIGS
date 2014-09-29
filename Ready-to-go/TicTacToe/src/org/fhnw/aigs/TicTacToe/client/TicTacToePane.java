package org.fhnw.aigs.TicTacToe.client;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
// -- References to internal packages (of this game)
import org.fhnw.aigs.TicTacToe.commons.TicTacToeSymbol;

/**
 * A simple {@link Pane } which can contain a cross or a nought symbol.
 * @author Matthias Stöckli (v1.0)
 * @version 1.1 (Raphael Stoeckli, 31.07.2014)
 */
public class TicTacToePane extends Pane{
    
    /**
     * The image (cross or nought)
     */
    private final ImageView symbolImageView;
    
    /**
     * Creates a new, empty instance of TicTacToePane.
     */
    public TicTacToePane(){
        super();
        this.symbolImageView = new ImageView();
        // Use the class ticTacToeField declared in the css file.
        this.getStyleClass().add("ticTacToeField");
        this.getChildren().add(symbolImageView);
    }
    
    /**
     * Sets the image view to the respective symbol (cross or nought)
     * @param playerSymbol The symbol to be set
     */
    public void setPlayerSymbol(TicTacToeSymbol playerSymbol){
        Image symbolImage;

        double height = this.getWidth();
        double width = this.getHeight();
        if(playerSymbol == TicTacToeSymbol.Cross){
            symbolImage = new Image("/Assets/Images/cross.png", height, width, true, false);
        }else if(playerSymbol == TicTacToeSymbol.Nought){
            symbolImage = new Image("/Assets/Images/nought.png", height, width, true, false);
        }else{
            symbolImage = null;
        }
    
        setImage(symbolImageView, symbolImage); // Set the new Symbol
        
    }
    
    /**
     * Draws the new image in JavaFX by invoking.<br>This method is needed because the program will crash if a part of a window element is changed without invoking.
     * @param imageView The ImageView object (field on board) to change
     * @param image The new image object
     * @since Version 1.1 (by Raphael Stoeckli)
     */
    public void setImage(ImageView imageView, Image image)
    {
        // IMPORTANT - Invoking!
        // This code is needed by JavaFX. If an image is changed outside of Platform.runLater, the programm will crash!
         Platform.runLater(new Runnable(){
            @Override
            public void run()
            {
                imageView.setImage(image);
            }
        });       
    }
    
    
    /**
     * Creates an animation that indicates that the selected symbol has already
     * been placed there.
     */
    public void playBusyAnimation(){
        ImageView symbol = (ImageView) this.getChildren().get(0);
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), symbol);
        scaleTransition.setToX(1.10);
        scaleTransition.setToY(1.10);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);
        scaleTransition.play();
    }
  
}
