package org.fhnw.aigs.Minesweeper.client;

import javafx.application.Platform;
import org.fhnw.aigs.Minesweeper.commons.MinesweeperField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * The graphical representation of a Minesweeper field
 * @author Matthias Stöckli (v1.0)
 * @version 1.1 (Raphael Stoeckli)
 */
public class MinesweeperPane extends Pane {

    /**
     * Provides a graphic. It can be a mine count number, a mine or a flag.
     */
    private ImageView imageView;
    
    /**
     * The field height.
     */
    private double height;
 
    /**
     * The field width.
     */
    private double width;

    /**
     * Creates a new instance of MinesweeperPane and styles it.
     */
    public MinesweeperPane() {
        super();
        this.width = getWidth();
        this.height = getHeight();
        this.imageView = new ImageView();
        this.getStyleClass().add("minesweeperField");
        this.getChildren().add(imageView);
        
                

    }

    /**
     * Sets a flag (marks mine).
     */
    public void setFlag() {
        this.width = getWidth();
        this.height = getHeight();
        //imageView.setImage(new Image("/Assets/Images/flag.png", height, width, true, false));
        setImage(imageView, new Image("/Assets/Images/flag.png", height, width, true, false));
    }

    /**
     * Remove the image, e.g. the flag.
     */
    public void removeImage() {
        //imageView.setImage(null);
        setImage(imageView, null);
    }
    
    /**
     * Changes the style of the field so that it appears to be covered again.
     * This will be used if you restart the game.
     */
    void cover() {
        //this.getStyleClass().clear();
        //this.getStyleClass().add("minesweeperField");
        clearStyle(this); // Invoke
        addStyle(this, "minesweeperField"); // Invoke
    }
    
    /**
     * Uncover the field. This changes the style of the image and shows the number
     * of surrounding mines or the mine if there is one.
     * @param field The logical reprsentation of that field.
     */
    public void uncover(MinesweeperField field) {
        this.width = getWidth();
        this.height = getHeight();
        addStyle(this, "empty"); // Invoke
        if(field.getHasFlag()){
            removeImage();
        }
        if (field.getSurroundingMinesCount() > 0) {
            setImage(imageView, new Image("/Assets/Images/" + field.getSurroundingMinesCount() + ".png", height, width, true, false)); // Invoke
        }
        if (field.getContainsMine()){
            removeImage();
            addStyle(this, "mineRed"); // Invoke
        }
    }
    
    /**
     * Adds a new style in JavaFX by invoking.<br>This method is needed because the program will crash if an existing part of a window element is changed without invoking.
     * @param pane the reference to the pane. Most time 'this'
     * @param styleName The new style name
     * @since Version 1.1 (Raphael Stoeckli)
     */
    public void addStyle(MinesweeperPane pane, String styleName)
    {
        // IMPORTANT - Invoking!
        // This code is needed by JavaFX. If a style is changed outside of Platform.runLater, the programm will crash!
         Platform.runLater(new Runnable(){
            @Override
            public void run()
            {
                pane.getStyleClass().add(styleName);
            }
        });               
    }
    
    /**
     * Removes all styles in JavaFX by invoking.<br>This method is needed because the program will crash if an existing part of a window element is changed without invoking.
     * @param pane the reference to the pane. Most time 'this'
     * @since Version 1.1 (Raphael Stoeckli)
     */
    public void clearStyle(MinesweeperPane pane)
    {
        // IMPORTANT - Invoking!
        // This code is needed by JavaFX. If a style is changed outside of Platform.runLater, the programm will crash!
         Platform.runLater(new Runnable(){
            @Override
            public void run()
            {
                pane.getStyleClass().clear();
            }
        });               
    }    
    
    
    /**
     * Draws the new image in JavaFX by invoking.<br>This method is needed because the program will crash if an existing part of a window element is changed without invoking.
     * @param imageView The ImageView object (field on board) to change
     * @param image The new image object
     * @since Version 1.1 (Raphael Stoeckli)
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


}