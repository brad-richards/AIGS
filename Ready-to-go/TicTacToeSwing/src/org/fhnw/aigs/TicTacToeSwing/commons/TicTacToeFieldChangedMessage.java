package org.fhnw.aigs.TicTacToeSwing.commons;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.Player;
import org.fhnw.aigs.commons.communication.FieldChangedMessage;

/**
 * This message is used to inform clients about changes on the board.
 * @author Matthias Stöckli
 */
@XmlRootElement(name = "TicTacToeFieldChangedMessage")
public class TicTacToeFieldChangedMessage extends FieldChangedMessage {
    
    /**
     * The (new) symbol of a field.
     */
    private TicTacToeSymbol playerSymbol;

    public TicTacToeFieldChangedMessage() {
    }

    /**
     * Creates a new instance of TicTacToeFieldChangedMessage
     * @param xPosition The x-position of the field
     * @param yPosition The y-position of the field
     * @param newControllingPlayer The player that now controls the field
     * @param playerSymbol The symbol of the field
     */
    public TicTacToeFieldChangedMessage(int xPosition, int yPosition, Player newControllingPlayer, TicTacToeSymbol playerSymbol) {
        super(xPosition, yPosition, newControllingPlayer);
        this.playerSymbol = playerSymbol;
    }

    @XmlElement(name = "Symbol")
    public TicTacToeSymbol getPlayerSymbol() {
        return playerSymbol;
    }

    public void setPlayerSymbol(TicTacToeSymbol playerSymbol) {
        this.playerSymbol = playerSymbol;
    }
}
