package org.fhnw.aigs.commons.communication;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This message is sent by every client that uses the BaseClient. It informs the
 * server about the fact that a client closed the game. Therefore the server
 * will terminate the game.
 *
 * @author Matthias Stöckli
 * @version v1.0
 */
@XmlRootElement(name = "ClientClosedMessage")
public class ClientClosedMessage extends Message {

    /**
     * The reason why the client closed
     */
    private String reason;

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public ClientClosedMessage() {
    }

    /**
     * Creates a new instance of ClientClosedMessage.
     *
     * @param reason
     */
    public ClientClosedMessage(String reason) {
        this.reason = reason;
    }

    /**
     * See {@link ClientClosedMessage#reason}
     */
    @XmlElement(name = "Reason")
    public String getReason() {
        return reason;
    }

    /**
     * See {@link ClientClosedMessage#reason}
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
}