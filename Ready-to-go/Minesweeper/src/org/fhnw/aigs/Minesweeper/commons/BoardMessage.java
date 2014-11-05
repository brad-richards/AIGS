package org.fhnw.aigs.Minesweeper.commons;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.communication.Message;

/**
 * This message is used to inform the client about fields that changed their status.
 *
 * @author Matthias Stöckli
 * @version v1.0
 */
@XmlRootElement(name = "BoardMessage")
public class BoardMessage extends Message {

    /**
     *  An array of all fields that changed (e.g. because they were uncovered)
     */
    private MinesweeperField[] fields;

    /**
     * Empty zero-argument constructor.
     */
    public BoardMessage() {
    }

    /**
     * Constructor of the class
     * @param fields Array with all MinesweeperField objects
     */
    public BoardMessage(MinesweeperField[] fields) {
        this.setFields(fields);
    }

    @XmlElementWrapper(name = "Fields")
    @XmlElement(name = "Field")
    public MinesweeperField[] getFields() {
        return fields;
    }

    public void setFields(MinesweeperField[] fields) {
        this.fields = fields;
    }
}
