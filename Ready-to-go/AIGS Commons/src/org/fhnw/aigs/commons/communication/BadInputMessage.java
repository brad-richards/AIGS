package org.fhnw.aigs.commons.communication;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This message is sent to a client when the server receives an invalid input.
 *
 * @author Matthias Stöckli
 * @version v1.0
 */
@XmlRootElement(name = "BadInputMessage")
public class BadInputMessage extends Message {

    /**
     * The invalid input that was sent to the server.
     */
    private String input;

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public BadInputMessage() {
    }

    /**
     * Creates a new instance of BadInputMessage.
     *
     * @param input
     */
    public BadInputMessage(String input) {
        this.input = input;
    }

    /**
     * See {@link input}.
     */
    @XmlElement(name = "Input")
    public String getInput() {
        return input;
    }

    /**
     * See {@link input}.
     */
    public void setInput(String input) {
        this.input = input;
    }
}
