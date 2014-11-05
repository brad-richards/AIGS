package org.fhnw.aigs.Minesweeper.commons;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fhnw.aigs.commons.communication.Message;

/**
 * This message is used to inform the server about the flagging or unflagging
 * of a field.
 * @author Matthias Stöckli
 * @version v1.0
 */
@XmlRootElement(name = "MarkFieldMessage")
public class MarkFieldMessage extends Message {
    
    /**
     * The x-position of the field.
     */
    private int positionX;
    
    /**
     * The y-position of the field.
     */
    private int positionY;

    /**
     * Empty zero-argument constructor.
     */
    public MarkFieldMessage() {
    }

    /**
     * Creates a new instance of MarkFieldMessage.
     * @param x The x-position of the field.
     * @param y The y-position of the field.
     */
    public MarkFieldMessage(int x, int y) {
        this.positionX = x;
        this.positionY = y;
    }
    
    /**
     * see {@link MarkFieldMessage#positionX}
     */
    @XmlElement(name = "X")
    public int getPositionX() {
        return positionX;
    }

    /**
     * see {@link MarkFieldMessage#positionY}
     */    
    @XmlElement(name = "Y")
    public int getPositionY() {
        return positionY;
    }

    /**
     * see {@link MarkFieldMessage#positionX}
     */    
    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    /**
     * see {@link MarkFieldMessage#positionY}
     */    
    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }
}
