package org.fhnw.aigs.BinaerOperatoren.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.fhnw.aigs.BinaerOperatoren.commons.AnswerMessage;
import org.fhnw.aigs.BinaerOperatoren.commons.MaxScoreMessage;
import org.fhnw.aigs.BinaerOperatoren.commons.QuestionMessage;
import org.fhnw.aigs.BinaerOperatoren.commons.StatusMessage;
import org.fhnw.aigs.commons.communication.Message;
import org.fhnw.aigs.swingClient.GUI.BackgroundPanel;

/**
 * This is the graphical representation of the BinaerOperatorenSpiel.
 * It contains labels for the score the status and the current question,
 * an answer field and a send button.
 * @author Matthias Stöckli
 */
public class BinaerOperatorenPanel extends BackgroundPanel {
    /**
     * Shows the current score.
     */
    private JLabel scoreLabel;
    
    /**
     * Shows the current status (e.g. was the answer correct or not?).
     */
    private JLabel statusLabel;
    
    /**
     * Shows the current question.
     */
    private JLabel questionLabel;
    
    /**
     * Input field for the answer.
     */
    private JTextField answerField;
    
    /**
     * Button which allows to send the answer.
     */
    private JButton sendButton;
    
    /**
     * A reference to the game.
     */
    private BinaerOperatorenClientGame clientGame;
    
    
    /**
     * Constructor with clientGame reference
     * @param clientGame clientGame reference
     */
    public BinaerOperatorenPanel(final BinaerOperatorenClientGame clientGame){
        this.clientGame = clientGame;

        //Sets a null layout
        this.setLayout(null);
        
        createScoreLabel(clientGame);
        createStatusLabel();
        createQuestionLabel();
        
        // Creates the answer field.
        answerField = new JTextField();
        answerField.setBounds(new Rectangle(20,170,400, 40));

        createSendButton(clientGame);
        
        // Add all the labels
        this.add(scoreLabel);
        this.add(statusLabel);
        this.add(questionLabel);
        this.add(answerField);
        this.add(sendButton);
    }
    
    /**
     * Changes the GUI according to the incoming messages.
     * @param message The received message
     */
    public void manipulateGUI(Message message){
        // Set the score that must be reached to win the game.
        if(message instanceof MaxScoreMessage){
            scoreLabel.setText("0 / " + clientGame.getMaxScore());
        }
        // Show new question
        else if(message instanceof QuestionMessage){
            handleQuestionMessage((QuestionMessage)message);
        }
        // Show whether the answer was correct or wrong.
        else if(message instanceof StatusMessage){
            handleStatusMessage((StatusMessage)message);
        }
    }

    /**
     * Creates the score label and styles it.
     * @param clientGame A reference to the client game.
     */
    private void createScoreLabel(final BinaerOperatorenClientGame clientGame) {
        Font scoreFont = new Font("Aerovias Brasil NF", Font.PLAIN, 42);
        scoreLabel = new JLabel("0 / " + clientGame.getMaxScore());
        scoreLabel.setFont(scoreFont);
        scoreLabel.setForeground(Color.green);
        scoreLabel.setBounds(new Rectangle(20,20,400, 40));
        
        
    }

    /**
     * Creates the status label and styles it.
     */
    private void createStatusLabel() {
        Font statusFont = new Font("Arial", Font.BOLD, 20);
         statusLabel = new JLabel();
        statusLabel.setFont(statusFont);
        statusLabel.setBounds(new Rectangle(20,50,400, 40));
    }

    /**
     * Creates the question label and styles it.
     */
    private void createQuestionLabel() {
        Font questionFont = new Font("Arial", Font.BOLD, 40);
         questionLabel = new JLabel("Frage: ");
        questionLabel.setFont(questionFont);
        questionLabel.setBounds(new Rectangle(20,110,400, 60));
    }

    /**
     * Creates the send button, styles it and adds an action listener.
     * @param clientGame A reference to the client game.
     */
    private void createSendButton(final BinaerOperatorenClientGame clientGame) {
        sendButton = new JButton("Senden");
        sendButton.setBounds(new Rectangle(20,240,100, 40));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = answerField.getText().trim();
                AnswerMessage answerMessage = new AnswerMessage(text);
                clientGame.sendMessageToServer(answerMessage);
                sendButton.setEnabled(false);
            }
        });
    }

    /**
     * Handles an incoming status message.
     * @param statusMessage The StatusMessage
     */
    private void handleStatusMessage(StatusMessage statusMessage) {
        // Enable the send button again.
        sendButton.setEnabled(true);
        if(statusMessage.getIsWrongInput() == true){
            statusLabel.setText("Fehlerhafte Eingabe.");
            statusLabel.setForeground(Color.red);
        }else if(statusMessage.getIsCorrectAnswer() == false){
            statusLabel.setText("Falsche Antwort :(... Richtig ist: " + statusMessage.getCorrectAnswer());
            statusLabel.setForeground(Color.red);
            clientGame.changeScore(-1);
        }else{
            statusLabel.setText("Korrekt!");
            statusLabel.setForeground(Color.green);
            clientGame.changeScore(1);
        }
        
        scoreLabel.setText(clientGame.getScore() + " / " + clientGame.getMaxScore());
        if(clientGame.getScore() < 0){
            scoreLabel.setForeground(Color.red);
        }else{
            scoreLabel.setForeground(Color.green);
        }
    }

    /**
     * Shows a new question
     * @param questionMessage The QuestionMessage.
     */
    private void handleQuestionMessage(QuestionMessage questionMessage) {
        questionLabel.setText(questionMessage.getQuestion());
    }
}

