package org.fhnw.aigs.commons.communication;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.Player;

/**
 * This message can be used in games where the fields are under the control of a
 * certain player. If the controller of a field changes, this message is then
 * sent to all clients.
 *
 * @author Matthias Stöckli
 * @version v1.0
 */
@XmlRootElement(name = "FieldChangedMessage")
public class FieldChangedMessage extends Message {

    /**
     * The x-Position of the field. *
     */
    private int xPosition;
    /**
     * The y-Position of the field. *
     */
    private int yPosition;
    /**
     * The player that now controls the field. *
     */
    private Player newControllingPlayer;

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public FieldChangedMessage() {
    }

    /**
     * Creates a new instance of FieldChangedMessage.
     *
     * @param xPosition The x-position of the field.
     * @param yPosition The y-position of the field.
     * @param newControllingPlayer The player that now controls the field.
     */
    public FieldChangedMessage(int xPosition, int yPosition, Player newControllingPlayer) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.newControllingPlayer = newControllingPlayer;
    }

    /**
     * See {@link FieldChangedMessage#xPosition}
     */
    @XmlElement(name = "X")
    public int getXPosition() {
        return xPosition;
    }

    /**
     * See {@link FieldChangedMessage#yPosition}
     */
    @XmlElement(name = "Y")
    public int getYPosition() {
        return yPosition;
    }

    /**
     * See {@link FieldChangedMessage#newControllingPlayer}
     */
    @XmlElement(name = "NewControllingPlayer")
    public Player getNewControllingPlayer() {
        return newControllingPlayer;
    }

    /**
     * See {@link FieldChangedMessage#xPosition}
     */
    public void setXPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    /**
     * See {@link FieldChangedMessage#yPosition}
     */
    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    /**
     * See {@link FieldChangedMessage#newControllingPlayer}
     */
    public void setNewControllingPlayer(Player newControllingPlayer) {
        this.newControllingPlayer = newControllingPlayer;
    }
}