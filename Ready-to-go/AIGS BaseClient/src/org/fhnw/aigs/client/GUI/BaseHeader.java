package org.fhnw.aigs.client.GUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

/**
 * Provides a simple header for the GameWindow. It shows the title of the game
 * and a status which can be defined with the method
 * {@link BaseHeader#setStatusLabelText(java.lang.String)}.<br>
 * v1.0 Initial Release<br>
 * v1.1 Changes of teh UI<br>
 * v1.1.1 Minor changes of the UI (tool tips)<br>
 * v1.2 Changes of layer handling
 * @author Matthias Stöckli (v1.0)
 * @version v1.2 (Raphael Stoeckli, 23.04.2015)
 */
public class BaseHeader extends GridPane {

    private final Label gameNameLabel;
    private final Label statusLabel;
    private final Button settingsButton; 

    /**
     * Create a new instance of BaseHeader.
     *
     * @param gameName The name of the game which will be showed in a label.
     */
    public BaseHeader(String gameName) {
        this.setId(LayerType.header.toString());                                // Sets the ID as "HEADER"
        gameNameLabel = new Label(gameName);
        statusLabel = new Label();
        settingsButton = new Button();
        settingsButton.getStyleClass().add("settings"); // Refernce to CSS (Icon also in CSS)
        settingsButton.setTooltip(new Tooltip("Opens the settings window"));
        settingsButton.getTooltip().getStyleClass().add("loadingTooltip");            
        settingsButton.setPrefSize(28, 28);
        settingsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
              SettingsWindow settingsWindow = new SettingsWindow();
              settingsWindow.setVisible(true);
            }
        });

        // Create constraints - the left part of the screen is reserved for the game name
        // The right part for the statusLabel
        ColumnConstraints gameNameConstraint = new ColumnConstraints();
        gameNameConstraint.setPercentWidth(50);
        ColumnConstraints statusConstraint = new ColumnConstraints();
        statusConstraint.setPercentWidth(50);
        this.getColumnConstraints().addAll(gameNameConstraint, statusConstraint);

        // Set the alignment to center, load the fonts and apply css ids to the
        // labels. Finally, add them to the header.
        this.setAlignment(Pos.CENTER);
        Font.loadFont(("/Assets/Fonts/AeroviasBrasilNF.ttf"), 12);
        
        
        if (gameName.length() > 12) // Workaround: String measuring would be a better method
        {
            gameNameLabel.setId("gameNameSmall");
            statusLabel.setId("statusSmall");
        }
        else
        {
             gameNameLabel.setId("gameName");
             statusLabel.setId("status");
        }
        
        //this.gridLinesVisibleProperty().set(true); // For debugging the Layout
     
        this.add(gameNameLabel, 0, 0);
        this.add(statusLabel, 1, 0);
        this.add(settingsButton, 2, 0);
        
        
        
    }
    /**
     * Allows to set the text of the title label.
     *
     * @param text The text to be display
     */
    public void setGameNameText(final String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameNameLabel.setText(text);
            }
        });
    }

    /**
     * Allows to set the text of the status label. Most commonly something like
     * "Your turn" will be displayed here.
     *
     * @param text The text to be display
     */
    public void setStatusLabelText(final String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                statusLabel.setText(text);
            }
        });
    }
}
