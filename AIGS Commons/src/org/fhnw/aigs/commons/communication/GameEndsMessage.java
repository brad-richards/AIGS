package org.fhnw.aigs.commons.communication;

import javax.xml.bind.annotation.*;

/**
 * This message can be sent by the server when the game ends. Usually it will be
 * sent at the end of a {@link org.fhnw.aigs.commons.Game#checkForWinningCondition()}" call. It will inform
 * the clients about the end of the game. There is no built-in logic handling
 * this message - it must be implemented in all clients and games.
 *
 * @author Matthias Stöckli
 */
@XmlRootElement(name = "GameEndsMessage")
public class GameEndsMessage extends Message {

    /**
     * The "reason" or the answer on the question "who won and why?" You can
     * also put in a string like "WON" which can then be processed by the client
     * to react accordingly.
     */
    private String reason;

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public GameEndsMessage() {
    }

    /**
     * Creates a new instance of GameEndsMessage.
     *
     * @param reason
     */
    public GameEndsMessage(String reason) {
        this.reason = reason;
    }

    /**
     * See {@link reason}.
     */
    @XmlElement(name = "Reason")
    public String getReason() {
        return reason;
    }

    /**
     * See {@link reason}.
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
}
