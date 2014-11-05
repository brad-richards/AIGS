package org.fhnw.aigs.commons.communication;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a general purpose Message to show a text in a client (dialog box)
 * @author Raphael Stoeckli
 * @version 1.0 (21.10.2014)
 */
@XmlRootElement(name = "NotifyMessage")
public class NotifyMessage extends Message{
    
    /**
     * The message to show
     */
    private String message;

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public NotifyMessage() {
    }

    /**
     * Creates a new instance of NotifyMessage,
     *
     * @param message The message to show
     */
    public NotifyMessage(String message) {
        this.message = message;
    }

    /**
     * See {@link NotifyMessage#message}.
     */
    @XmlElement(name = "Message")
    public String getMessage() {
        return message;
    }

    /**
     * See {@link NotifyMessage#message}.
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    
}
