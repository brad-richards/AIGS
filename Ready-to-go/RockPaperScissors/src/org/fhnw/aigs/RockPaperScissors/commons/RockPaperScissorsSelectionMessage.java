package org.fhnw.aigs.RockPaperScissors.commons;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.communication.Message;

/**
 * Class use by the client, to inform the server of the player's
 * selection (paper, scissors, roch) for this move
 */
@XmlRootElement(name = "RockPaperScissorsSelectionMessage")
public class RockPaperScissorsSelectionMessage extends Message {
    private RockPaperScissorsSymbol symbol;  // The selected move
    
    /**
     * Returns the symbol
     * @return RockPaperScissorsSymbol-Object
     */
    @XmlElement(name = "Symbol")		// Parameter name for XML must be specified for the getter
    public RockPaperScissorsSymbol getSymbol() {
        return symbol;
    }
 
    /**
     * Set symbol
     * @param symbol RockPaperScissorsSymbol-Object
     */
    public void setSymbol(RockPaperScissorsSymbol symbol) {
        this.symbol = symbol;
    }
    
    /**
     * Empty constructor (required)
     */
    public RockPaperScissorsSelectionMessage() { }
    
    /**
     * Constructor with symbol info
     * @param symbol Selected symbol
     */
    public RockPaperScissorsSelectionMessage(RockPaperScissorsSymbol symbol)
    {
      super();
      this.setSymbol(symbol);
    }    
}
