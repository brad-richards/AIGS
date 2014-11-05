package org.fhnw.aigs.Minesweeper.commons;

import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.communication.Message;

/**
 * This message triggers a game restart. This is an example of a completely
 * empty message. in Minesweeper there is no need for any attributes.
 *
 * @author Matthias Stöckli
 * @version v1.0
 */
@XmlRootElement(name = "RestartMessage")
public class RestartMessage extends Message {

    /**
     * Empty zero-argument constructor.
     */
    public RestartMessage() {
    }
}
