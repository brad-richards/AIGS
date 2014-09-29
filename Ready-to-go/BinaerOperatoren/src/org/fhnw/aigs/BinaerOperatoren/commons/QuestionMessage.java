package org.fhnw.aigs.BinaerOperatoren.commons;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.communication.Message;

/**
 * This message is used to send a question to the client.
 * @author Matthias Stöckli
 */
@XmlRootElement(name = "QuestionMessage")
public class QuestionMessage extends Message{
    
    /** The question to be sent to the client. */
    private String question;
    
    /** Default zero-argument constructor */
    public QuestionMessage(){ }
    
    /**
     * Creates a new instance of QuestionMessage
     * @param question The question to be sent to the client.
     */
    public QuestionMessage(String question){
        this.question = question;
    }
    
    /** See {@link QuestionMessage#question}*/
    @XmlElement(name = "Question")
    public String getQuestion() {
        return question;
    }

    /** See {@link QuestionMessage#question}*/
    public void setQuestion(String question) {
        this.question = question;
    }
}
