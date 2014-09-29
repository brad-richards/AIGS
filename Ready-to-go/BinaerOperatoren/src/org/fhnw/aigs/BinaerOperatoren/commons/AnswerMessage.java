package org.fhnw.aigs.BinaerOperatoren.commons;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.communication.Message;

/**
 * Sends the answer typed into the text box to the server.
 * @author Matthias Stöckli
 */
@XmlRootElement(name = "AnswerMessage")
public class AnswerMessage extends Message {
    
    /** The answer to the question. */
    private String answer;
    
    /** Default zero-argument constructor. */
    public AnswerMessage(){ }
    
    /**
     * Creates a new instance of AnswerMessage
     * @param answer The answer to the question.
     */
    public AnswerMessage(String answer){
        this.answer = answer;
    }
    
    /** See {@link AnswerMessage#answer}*/
    @XmlElement(name = "AnswerMessage")
    public String getAnswer() {
        return answer;
    }

    /** See {@link AnswerMessage#answer}*/
    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
