package org.fhnw.aigs.commons.communication;

import java.util.Date;
import javax.xml.bind.annotation.*;

/**
 * This message is sent to all players in a certain interval if the
 * KeepAliveManager is activated via the settings. This message serves as a
 * ping. It has one big caveat however: If the developers uses breakpoints the
 * communication can be blocked and therefore the messages cannot be parsed.
 * This and the fact that this process produces a lot of overhead is why it is
 * not recommended to use these messages, unless really needed.
 *
 * @author Matthias Stöckli
 */
@XmlRootElement(name = "KeepAliveMessage")
public class KeepAliveMessage extends Message {

    /**
     * The Date and Time when the message was sent
     */
    private Date sentTime;
    /**
     * The Date and Time when the message was answered
     */
    private Date answerTime;

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public KeepAliveMessage() {
    }

    /**
     * See {@link sentTime}.
     */
    @XmlElement(name = "SentTime")
    public Date getSentTime() {
        return sentTime;
    }

    /**
     * See {@link answerTime}.
     */
    @XmlElement(name = "AnswerTime")
    public Date getAnswerTime() {
        return answerTime;
    }

    /**
     * See {@link sentTime}.
     */
    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    /**
     * See {@link answerTime}.
     */
    public void setAnswerTime(Date answerTime) {
        this.answerTime = answerTime;
    }
}
