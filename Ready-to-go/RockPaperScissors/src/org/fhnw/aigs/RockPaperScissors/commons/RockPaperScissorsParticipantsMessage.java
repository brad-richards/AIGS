package org.fhnw.aigs.RockPaperScissors.commons;

import org.fhnw.aigs.commons.communication.Message;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.Player;

/**
 * Class used by the server to send the names of all players to all clients.
 */
@XmlRootElement(name = "RockPaperScissorsParticipantsMessage")
public class RockPaperScissorsParticipantsMessage extends Message
{
	// RockPaperScissors has only two players
    private Player playerOne;
    private Player playerTwo;

    /**
     * Return the first player
     * @return Player-Object
     */
    @XmlElement(name = "PlayerOne")		// Parameter name for XML must be specified for the getter
    public Player getPlayerOne() {
        return playerOne;
    }

    /**
     * Set the first player
     * @param playerOne Player-Object
     */
    public void setPlayerOne(Player playerOne) {
        this.playerOne = playerOne;
    }

    /**
     * Return the second player
     * @return Player-Object
     */
    @XmlElement(name = "PlayerTwo")		// Parameter name for XML must be specified for the getter
    public Player getPlayerTwo() {
        return playerTwo;
    }

    /**
     * Set the second player
     * @param playerTwo Player-Object
     */    
    public void setPlayerTwo(Player playerTwo) {
        this.playerTwo = playerTwo;
    }
    
    /**
     * Empty constructor (required)
     */    
    public RockPaperScissorsParticipantsMessage() {
    }
    
    /**
     * Constructor, including player info
     * @param one first player
     * @param two second player
     */
    public RockPaperScissorsParticipantsMessage(Player one, Player two)
    {
        super();
        this.setPlayerOne(one);
        this.setPlayerTwo(two);
    }    
}
