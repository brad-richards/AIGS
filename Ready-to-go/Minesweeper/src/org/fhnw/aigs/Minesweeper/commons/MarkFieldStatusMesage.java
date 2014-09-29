package org.fhnw.aigs.Minesweeper.commons;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.communication.Message;

/**
 * This message is used to inform the client about the status of a flag.
 *
 * @author Matthias Stöckli
 */
@XmlRootElement(name = "MarkFieldStatusMesage")
public class MarkFieldStatusMesage extends Message {

    /**
     * The x-position of the field that was flagged or unflagged.
     */
    private int positionX;
    /**
     * The y-position of the field that was flagged or unflagged.
     */
    private int positionY;
    
    /**
     * Indicates whether or not the field has a flag or not.
     */
    private boolean hasFlag;
    
    /**
     * Mines/flags left on the board
     */
    private int minesLeft;

    /**
     * Empty zero-argument constructor.
     */
    public MarkFieldStatusMesage() {
    }

    /**
     * Constructor with arguments
     * @param x Horizontal coordianate on the board
     * @param y Vertical coordinate on the board
     * @param hasFlag True if the field has a flag, otherwise false 
     * @param minesLeft Number of remaining flags or mines on the board
     */
    public MarkFieldStatusMesage(int x, int y, boolean hasFlag, int minesLeft) {
        this.positionX = x;
        this.positionY = y;
        this.hasFlag = hasFlag;
        this.minesLeft = minesLeft;
    }

    @XmlElement(name = "X")
    public int getPositionX() {
        return positionX;
    }

    @XmlElement(name = "Y")
    public int getPositionY() {
        return positionY;
    }

    @XmlElement(name = "HasFlag")
    public boolean getHasFlag() {
        return hasFlag;
    }

    @XmlElement(name = "MinesLeft")
    public int getMinesLeft() {
        return minesLeft;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public void setHasFlag(boolean hasFlag) {
        this.hasFlag = hasFlag;
    }

    public void setMinesLeft(int minesLeft) {
        this.minesLeft = minesLeft;
    }
}
