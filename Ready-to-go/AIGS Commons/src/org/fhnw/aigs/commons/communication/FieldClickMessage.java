package org.fhnw.aigs.commons.communication;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This message is sent by clients to indicate that the user clicked on a
 * certain field. The server's game logic then evaluates the effect of that
 * action. Usually a {@link FieldClickFeedbackMessage} follows as an answer,
 * accompanied by a {@link FieldChangedMessage} if the FieldClickMessage had any
 * effect on the game.
 *
 * @author Matthias Stöckli
 * @version v1.0
 */
@XmlRootElement(name = "FieldClickMessage")
public class FieldClickMessage extends Message {

    /**
     * The x-Position of the field.
     */
    private int xPosition;
    /**
     * The y-Position of the field.
     */
    private int yPosition;

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public FieldClickMessage() {
    }

    /**
     * Creates a new instance of FieldClickMessage.
     *
     * @param xPosition The x-Position of the field that has been clicked.
     * @param yPosition The y-Position of the field that has been clicked.
     */
    public FieldClickMessage(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    /**
     * See {@link FieldClickMessage#xPosition}.
     */
    @XmlElement(name = "X")
    public int getXPosition() {
        return this.xPosition;
    }

    /**
     * See {@link FieldClickMessage#yPosition}.
     */
    @XmlElement(name = "Y")
    public int getYPosition() {
        return this.yPosition;
    }

    /**
     * See {@link FieldClickMessage#xPosition}.
     */
    public void setXPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    /**
     * See {@link FieldClickMessage#yPosition}.
     */
    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }
}
