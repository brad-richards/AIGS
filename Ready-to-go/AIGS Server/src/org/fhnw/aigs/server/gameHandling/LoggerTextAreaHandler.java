package org.fhnw.aigs.server.gameHandling;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Custom logger for JTextArea. Slightly modified. See
 * http://stackoverflow.com/questions/10785560/write-logger-message-to-file-and-textarea-while-maintaining-default-behaviour-in
 *
 * @author Matthias Stöckli, Edwin Dalorzo
 * @version v1.0
 */
public class LoggerTextAreaHandler extends java.util.logging.Handler {

    public LoggerTextAreaHandler(final JTextArea textArea) {
        setLevel(Level.ALL);
        this.textArea = textArea;
    }
    private JTextArea textArea;

    @Override
    public void publish(final LogRecord record) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String text = textArea.getText() + "\n";
                text = (textArea.getText());
                String output = record.getMessage();

                // Delete the following parts:
                // <?xml version="1.0" encoding="UTF-8"?>
                // xmlns:aigs="https://ol19ns11008.fhnw.ch/
                // This information is not really needed.

                if (record.getParameters() != null) {
                    for (int i = 0; i < record.getParameters().length; i++) {
                        output = output.replace("{" + i + "}", record.getParameters()[i].toString());
                    }
                }
                // Remove other bloaty xml elements.
                output = output.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
                output = output.replace("xmlns:aigs=\"https://ol19ns11008.fhnw.ch/", "");

                text = text + output + "\n";

                textArea.setText(text);
            }
        });
    }

    public JTextArea getTextArea() {
        return this.textArea;
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}