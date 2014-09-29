package org.fhnw.aigs.commons.communication;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.FieldStatus;

/**
 * This message can be used to indicate a feedback for players. When a player
 * clicks a field, usually a {@link FieldClickMessage} is being sent to the
 * server. Then the server evaluates whether that turn is valid or not. E.g.
 * when the player wants to move despite not being the currentPlayer he or she
 * will be informed that the action was not possible. By setting the fieldStatus
 * the client can react accordingly. A FieldClickFeedbackMessage is typically
 * only used to trigger graphical reactions on the client side, not for game
 * logic purposes.
 *
 * @author Matthias Stöckli
 */
@XmlRootElement(name = "FieldClickFeedbackMessage")
public class FieldClickFeedbackMessage extends Message {

    /**
     * The x-Position of the field.
     */
    private int xPosition;
    /**
     * The y-Position of the field.
     */
    private int yPosition;
    /**
     * The FeildStatus, e.g. NoChange, Error etc., see {@link FieldStatus}.
     */
    private FieldStatus fieldStatus;

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public FieldClickFeedbackMessage() {
    }

    /**
     * Creates a new instance of FieldClickFeedbackMessage
     *
     * @param xPosition The x-Position of the field.
     * @param yPosition The y-Position of the field.
     * @param turnStatus The Turn status.
     */
    public FieldClickFeedbackMessage(int xPosition, int yPosition, FieldStatus turnStatus) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.fieldStatus = turnStatus;
    }

    /**
     * See {@link FieldClickFeedbackMessage#xPosition}.
     */
    @XmlElement(name = "X")
    public int getxPosition() {
        return xPosition;
    }

    /**
     * See {@link FieldClickFeedbackMessage#yPosition}.
     */
    @XmlElement(name = "Y")
    public int getyPosition() {
        return yPosition;
    }

    /**
     * See {@link FieldClickFeedbackMessage#fieldStatus}.
     */
    @XmlElement(name = "FieldStatus")
    public FieldStatus getFieldStatus() {
        return fieldStatus;
    }

    /**
     * See {@link FieldClickFeedbackMessage#xPosition}.
     */
    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    /**
     * See {@link FieldClickFeedbackMessage#yPosition}.
     */
    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    /**
     * See {@link FieldClickFeedbackMessage#fieldStatus}.
     */
    public void setFieldStatus(FieldStatus fieldStatus) {
        this.fieldStatus = fieldStatus;
    }
}
