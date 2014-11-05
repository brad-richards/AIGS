package org.fhnw.aigs.Minesweeper.commons;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.fhnw.aigs.commons.communication.*;

/**
 * This message is used to handle Board change messages
 * @author Matthias Stöckli
 * @version v1.0
 */
@XmlRootElement(name="BoardChangeMessage")
public class BoardChangeMessage extends Message {
	
        /** All uncovered fields. */
	private MinesweeperField[] uncoveredMinesweeperFields;
        
        /**
         * Empty, zero-argument constructor
         */
	public BoardChangeMessage(){}
	
        /**
         * Creates a new instance of BoardchangeMessage with an array of 
         * MinesweeperFields which were uncovered.
         * @param uncoveredMinesweeperFields 
         */
	public BoardChangeMessage(MinesweeperField[] uncoveredMinesweeperFields){
		this.setUncoveredMinesweeperFields(uncoveredMinesweeperFields);
	}	
	
	@XmlElementWrapper(name = "UncoveredMinesweeperFields")
	@XmlElement(name="Field")
	public MinesweeperField[] getUncoveredMinesweeperFields() {
		return uncoveredMinesweeperFields;
	}

	private void setUncoveredMinesweeperFields(MinesweeperField[] uncoveredMinesweeperFields) {
		this.uncoveredMinesweeperFields = uncoveredMinesweeperFields;
	}
}
