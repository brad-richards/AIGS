package org.fhnw.aigs.BinaerOperatoren.commons;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.communication.Message;

/**
 * Informs the client about the score needed to win the game.
 * @author Matthias Stöckli
 * @version v1.0
 */
@XmlRootElement(name ="MaxScoreMessage")
public class MaxScoreMessage extends Message{
    
    /** The score needed to win the game */
    private int maxScore;
    
    /** Empty zero-argument constructor */
    public MaxScoreMessage(){}
    
    public MaxScoreMessage(int maxScore){
        this.maxScore = maxScore;
    }
    
    /** See {@link MaxScoreMessage#maxScore}*/
    @XmlElement(name ="MaxScore")
    public int getMaxScore() {
        return maxScore;
    }

    /** See {@link MaxScoreMessage#maxScore}*/
    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }
}
