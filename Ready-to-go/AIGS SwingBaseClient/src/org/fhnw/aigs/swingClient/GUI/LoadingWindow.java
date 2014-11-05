package org.fhnw.aigs.swingClient.GUI;

import java.awt.Font;
import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * This screen can be used to inform the user about the fact that no game 
 * has yet been established.
 * @version v1.0
 * @author Matthias Stöckli
 */
public class LoadingWindow extends BackgroundPanel{
    JProgressBar progressBar;
    int progress = 0;

    /**
     * Create a new LoadingWindow.<br>
     * First a progress indicator is created and set up. The progress indicator
     * will turn indefinite. Then a label and a {@link javax.swing.JProgressBar} are shown.
     * In the end, the WaitThread will be started which shows that still no
     * connection has been established.
     */
    public LoadingWindow() {        
        this.setLayout(null);
        JLabel loadingLabel = new JLabel("Connecting...");
        loadingLabel.setBounds(new Rectangle(20, 160, 400, 50));
        Font font = new Font("Aerovias Brasil NF", Font.PLAIN, 42);
        loadingLabel.setFont(font);
        
        progressBar = new JProgressBar();
        progressBar.setBounds(new Rectangle(20, 100, 400, 50));
        this.add(loadingLabel);
        this.add(progressBar);

        new LoadingWindow.WaitThread().start();
    }

    /**
     * This thread simply waits and increases the progress of the progress bar.
     * If the bar is full it restarts.
     */
    private class WaitThread extends Thread {

        public void run() {
            while (true) {
                if (progress <= 100) {
                    progress ++;
                } else {
                    progress = 0;
                }
                progressBar.setValue(progress);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(LoadingWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (Exception ex) // All other exceptions
                {
                    Logger.getLogger(LoadingWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
