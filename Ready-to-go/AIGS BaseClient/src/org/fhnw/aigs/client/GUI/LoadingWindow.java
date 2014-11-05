package org.fhnw.aigs.client.GUI;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

/**
 * This screen can be used to inform the user about the fact that there is
 * either no player to join the game with or that the connection has not been
 * established yet.
 * @version v1.0
 * @author Matthias Stöckli
 */
public class LoadingWindow extends BorderPane {

    ProgressBar progressBar;
    double progress = 0.0;

    /**
     * Create a new LoadingWindow.<br>
     * First a progress indicator is created and set up. The progress indicator
     * will turn indefinite. Then a label and a {@link ProgressBar} are shown.
     * In the end, the WaitThread will be started which shows that still no
     * connection has been established.
     */
    public LoadingWindow() {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(120, 120);
        progressIndicator.setMaxSize(120, 120);
        this.getStyleClass().add("loading");
        this.setCenter(progressIndicator);

        Label loadingLabel = new Label("Connecting...");
        loadingLabel.setId("loadingLabel");
        loadingLabel.setTextAlignment(TextAlignment.RIGHT);

        StackPane sp = new StackPane();
        sp.setAlignment(Pos.CENTER);
        sp.getChildren().add(loadingLabel);

        progressBar = new ProgressBar(0.0);
        progressBar.setTranslateY(50);
        sp.getChildren().add(progressBar);

        new LoadingWindow.WaitThread().start();
        setTop(sp);
    }

    /**
     * This thread simply waits and increases the progress of the progress bar.
     * If the bar is full it restarts.
     */
    private class WaitThread extends Thread {

        @Override
        public void run() {
            while (true) {
                if (progress <= 1) {
                    progress += 0.002;
                } else {
                    progress = 0;
                }
                progressBar.setProgress(progress);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(LoadingWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (Exception ex)
                {
                    Logger.getLogger(LoadingWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
