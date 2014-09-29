package org.fhnw.aigs.Minesweeper.commons;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fhnw.aigs.commons.communication.Message;

/**
 * This message is used by the Minesweeper clients to initialize a game. The
 * player can easily control the setup of the game like this, e.g. via prompt.
 * This message will then be interpreted by the server and will be used in the
 * process of the "setUpFields" method.
 *
 * @author Matthias Stöckli
 */
@XmlRootElement(name = "SetMineCountMessage")
public class SetUpBoardMessage extends Message {

    /**
     * Empty zero-argument constructor.
     */
    public SetUpBoardMessage() {
    }

    /**
     * Creates a new instance of SetUpBoardMessage.
     *
     * @param totalMines The number of mines.
     * @param xFields The number of fields on the x-axis
     * @param yFields The number of fields on the y-axis
     */
    public SetUpBoardMessage(int totalMines, int xFields, int yFields) {
        this.totalMines = totalMines;
        this.xFields = xFields;
        this.yFields = yFields;
    }
    /**
     * The number of mines.
     */
    private int totalMines;
    /**
     * the number of fields on the x-axis
     */
    private int xFields;
    /**
     * the number of fields on the y-axis
     */
    private int yFields;

    /** See {@link SetUpBoardMessage#totalMines}. */
    @XmlElement(name = "TotalMines")
    public int getTotalMines() {
        return totalMines;
    }

    /** See {@link SetUpBoardMessage#xFields}. */
    @XmlElement(name = "X-Fields")
    public int getxFields() {
        return xFields;
    }

    /** See {@link SetUpBoardMessage#yFields}. */
    @XmlElement(name = "Y-Fields")
    public int getyFields() {
        return yFields;
    }

    /** See {@link SetUpBoardMessage#totalMines}. */
    public void setTotalMines(int totalMines) {
        this.totalMines = totalMines;
    }

    /** See {@link SetUpBoardMessage#xFields}. */
    public void setxFields(int xFields) {
        this.xFields = xFields;
    }

    /** See {@link SetUpBoardMessage#yFields}. */
    public void setyFields(int yFields) {
        this.yFields = yFields;
    }
}
