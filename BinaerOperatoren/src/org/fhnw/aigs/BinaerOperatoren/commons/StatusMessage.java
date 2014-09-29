package org.fhnw.aigs.BinaerOperatoren.commons;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.communication.Message;

/**
 * This message informs the client about the status of an answer, i.e.
 * whether the answer was correct or wrong. 
 * @author Matthias Stöckli
 */
@XmlRootElement(name = "StatusMessage")
public class StatusMessage extends Message {
    
    /**
     * Indicates whether the input was wrong (non numeric).
     */
    private boolean isWrongInput;
    
    /**
     * Indicates whether the answer was right or wrong.
     */
    private boolean isCorrectAnswer;
    
    /*
     * The correct answer.
     */
    private int correctAnswer;
    
    /**
     * Empty, zero-argument constructor.
     */
    public StatusMessage(){ }    

    @XmlElement(name ="IsWrongInput")
    public boolean getIsWrongInput() {
        return isWrongInput;
    }

    public void setIsWrongInput(boolean isWrongInput) {
        this.isWrongInput = isWrongInput;
    }

    @XmlElement(name ="IsCorrectAnswer")
    public boolean getIsCorrectAnswer() {
        return isCorrectAnswer;
    }

    public void setIsCorrectAnswer(boolean isCorrectAnswer) {
        this.isCorrectAnswer = isCorrectAnswer;
    }

    @XmlElement(name ="CorrectAnswer")
    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    
    
}
