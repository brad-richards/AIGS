package org.fhnw.aigs.BinaerOperatoren.server;

import java.security.SecureRandom;
import org.fhnw.aigs.BinaerOperatoren.commons.AnswerMessage;
import org.fhnw.aigs.BinaerOperatoren.commons.MaxScoreMessage;
import org.fhnw.aigs.BinaerOperatoren.commons.QuestionMessage;
import org.fhnw.aigs.BinaerOperatoren.commons.StatusMessage;
import org.fhnw.aigs.commons.Game;
import org.fhnw.aigs.commons.Player;
import org.fhnw.aigs.commons.communication.GameEndsMessage;
import org.fhnw.aigs.commons.communication.Message;

public class GameLogic extends Game {
    
    /** A random number generator */
    private SecureRandom secureRandom = new SecureRandom();
    /** the result of the current question */
    private int resultForCurrentQuestion = 0; 
    /** The score to win the game. */
    private int currentScore = 0;
    /** The score to win the game */
    private int maxScore =  5;
    /** The highest number that will appear in questions, e.g. 255 (0 included) */
    private int highestNumber = 15;
    
    /**
     * Empty constructor which is necessary in order to load the game.<br>
     * Don't forgett to set the same game name in the class {@link org.fhnw.aigs.BinaerOperatoren.client.Main} in the package 'client'.
     */     
    public GameLogic(){
        super("BinaerOperatoren", 1);
    }
    
    @Override
    public void initialize() {
        startGame();
        MaxScoreMessage maxScoreMessage = new MaxScoreMessage(maxScore);
        sendMessageToPlayer(maxScoreMessage, getCurrentPlayer());
        askQuestion();
    }

    @Override
    public void processGameLogic(Message message, Player sendingPlayer) {
        if(message instanceof AnswerMessage){
            checkAnswer(message, sendingPlayer);
        }
    }

    /**
     * Checks the answer the client gave.
     * @param message The answer message.
     */
    public void checkAnswer(Message message, Player sendingPlayer){
        AnswerMessage answerMessage = (AnswerMessage) message;
        StatusMessage statusMessage = new StatusMessage();
        int value = 0;
        
        // Try to parse the answer.
        try {
            value = Integer.parseInt(answerMessage.getAnswer());
        } catch (NumberFormatException ex) {
            // If the parsing failed, inform the client
            statusMessage.setIsWrongInput(true);
            sendMessageToPlayer(statusMessage, sendingPlayer);
            return;
        }

        // Check whether the answer was correct or not. Then send a StatusMessage
        // to the player.
        if (value != resultForCurrentQuestion) {
            statusMessage.setIsWrongInput(false);
            statusMessage.setIsCorrectAnswer(false);
            statusMessage.setCorrectAnswer(resultForCurrentQuestion);
            currentScore -= 1;
            sendMessageToPlayer(statusMessage, sendingPlayer);
        } else {
            statusMessage.setIsWrongInput(false);
            statusMessage.setIsCorrectAnswer(true);
            currentScore += 1;
            sendMessageToAllPlayers(statusMessage);
        }
        // Ask a new question.
        askQuestion();        
    }
    
    /**
     * Asks a new questions and sends a {@link QuestionMessage}.
     */
    public void askQuestion(){
        // Calculates a random number between 0 and 2 which represents the 
        // question type.
        int questionType = secureRandom.nextInt(3);
        
        // Calculates two random numbers.
        int firstNumber = secureRandom.nextInt(highestNumber);
        int secondNumber = secureRandom.nextInt(highestNumber);
        QuestionMessage questionMessage = null;

        // Gets a new QuestionMessage based on the question type and the random
        // numbers. 0 = OR, 1 = AND, 2 = XOR
        switch(questionType){
            case 0: questionMessage = askOrQuestion(firstNumber, secondNumber); break;
            case 1: questionMessage = askAndQuestion(firstNumber, secondNumber); break;
            case 2: questionMessage = askXorQuestion(firstNumber, secondNumber); break;
        }
        sendMessageToPlayer(questionMessage, getCurrentPlayer());
    }

    /**
     * Checks whether the {@link GameLogic#currentScore} reached the {@link GameLogic#maxScore}.
     */
    @Override
    public void checkForWinningCondition() {
        if(currentScore == maxScore){
            GameEndsMessage gameEndsMessage = new GameEndsMessage("You won!");
            sendMessageToPlayer(gameEndsMessage, getCurrentPlayer());
        }
    }

    /**
     * Calculates the result using the OR ( | ) operator.
     * @param firstNumber The first number.
     * @param secondNumber The second number.
     * @return The result.
     */
    private QuestionMessage askOrQuestion(int firstNumber, int secondNumber) {
        resultForCurrentQuestion = firstNumber | secondNumber;
        return new QuestionMessage(firstNumber + " OR " + secondNumber);
                
    }

    /**
     * Calculates the result using the AND ( &amp; ) operator.
     * @param firstNumber The first number.
     * @param secondNumber The second number.
     * @return The result.
     */
    private QuestionMessage askAndQuestion(int firstNumber, int secondNumber) {
        resultForCurrentQuestion = firstNumber & secondNumber;
        return new QuestionMessage(firstNumber + " AND " + secondNumber);
    }

    /**
     * Calculates the result using the XOR ( ^ ) operator.
     * @param firstNumber The first number.
     * @param secondNumber The second number.
     * @return The result.
     */
    private QuestionMessage askXorQuestion(int firstNumber, int secondNumber) {
        resultForCurrentQuestion = firstNumber ^ secondNumber;
        return new QuestionMessage(firstNumber + " XOR " + secondNumber);
    }
    
}
