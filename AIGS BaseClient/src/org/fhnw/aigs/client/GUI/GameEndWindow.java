package org.fhnw.aigs.client.GUI;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

/**
 * A simple screen which can be shown, when the winning conditions have been
 * met. The easiest way to achieve this would be to use the
 * {@link BaseGameWindow#setOverlay(javafx.scene.Node)} method.<br>
 * v1.0 Initial release<br>
 * v1.1 Changes of layer handling
 * @version v1.1 (Raphael Stoeckli, 23.04.2015) 
 * @author Matthias Stöckli (v1.0)
 */
public class GameEndWindow extends BorderPane {

    /**
     * Create a new instance of the GameEndWindow and create a label based on
     * the winner's name.
     *
     * @param winner The winner of the game.
     */
    public GameEndWindow(final String winner) {
        this.setId(LayerType.end.toString());                                   // Sets the ID as "END"
        this.getStyleClass().add("loading");
        Label winningLabel = new Label("Game ends! " + winner + "wins!");
        winningLabel.setId("winningLabel");
        winningLabel.setTextAlignment(TextAlignment.RIGHT);

        StackPane sp = new StackPane();
        sp.setAlignment(Pos.CENTER);
        sp.getChildren().add(winningLabel);

        setTop(sp);
    }
}
