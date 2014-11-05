package org.fhnw.aigs.commons.communication;

import javax.xml.bind.annotation.*;
import org.fhnw.aigs.commons.FieldStatus;

/**
 * This general purpose message can be used as a status message in games. It
 * contains a turn statatus, e.g. "OK", "BLOCKED" etc.
 *
 * @author Matthias Stöckli
 * @version v1.0
 */
@XmlRootElement(name = "ResultMessage")
public class ResultMessage extends Message {

    /**
     * The status of the field.
     */
    private FieldStatus turnStatus;

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public ResultMessage() {
    }

    /**
     * Creates a new instance of ResultMessage,
     *
     * @param turnStatus The turn status.
     */
    public ResultMessage(FieldStatus turnStatus) {
        this.turnStatus = turnStatus;
    }

    /**
     * See {@link ResultMessage#turnStatus}.
     */
    @XmlElement(name = "TurnStatus")
    public FieldStatus getTurnStatus() {
        return turnStatus;
    }

    /**
     * See {@link ResultMessage#turnStatus}.
     */
    public void setTurnStatus(FieldStatus turnStatus) {
        this.turnStatus = turnStatus;
    }
}
