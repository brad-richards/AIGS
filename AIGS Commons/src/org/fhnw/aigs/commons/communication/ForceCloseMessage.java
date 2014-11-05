package org.fhnw.aigs.commons.communication;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This message is sent to clients when there is an error or another event that
 * causes the clients to close, e.g. the server shuts down, the connection was
 * aborted etc.
 *
 * @author Matthias Stöckli
 * @version v1.0
 */
@XmlRootElement(name = "ForceCloseMessage")
public class ForceCloseMessage extends Message {

    /**
     * The reason why the client has to be terminated.
     */
    private String reason;

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public ForceCloseMessage() {
    }

    /**
     * Creates a new instance of ForceCloseMessage
     *
     * @param reason The reason why the client has to be terminated.
     */
    public ForceCloseMessage(String reason) {
        this.reason = reason;
    }

    /**
     * See {@link ForceCloseMessage#reason}
     */
    @XmlElement(name = "Reason")
    public String getReason() {
        return reason;
    }

    /**
     * See {@link ForceCloseMessage#reason}
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
}
