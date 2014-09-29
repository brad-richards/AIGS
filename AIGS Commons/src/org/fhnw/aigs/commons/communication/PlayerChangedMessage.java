package org.fhnw.aigs.commons.communication;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.Player;

/**
 * This message can be sent whenever the {@link org.fhnw.aigs.commons.Game#currentPlayer} was manually
 * changed. This message will automatically be sent to all players when the
 * method {@link org.fhnw.aigs.commons.Game#passTurnToNextPlayer} is being invoked.
 *
 * @author Matthias Stöckli
 */
@XmlRootElement(name = "PlayerChangedMessage")
public class PlayerChangedMessage extends Message {

    /**
     * The player that used to be the current player
     */
    private Player oldPlayer;
    /**
     * The new current player
     */
    private Player newPlayer;

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public PlayerChangedMessage() {
    }

    /**
     * Creates a new instance of PlayerChangedMessage.
     *
     * @param oldPlayer The player that used to be the current player.
     * @param newPlayer The new current player.
     */
    public PlayerChangedMessage(Player oldPlayer, Player newPlayer) {
        this.oldPlayer = oldPlayer;
        this.newPlayer = newPlayer;
    }

    /**
     * See {@link PlayerChangedMessage#oldPlayer}
     */
    @XmlElement(name = "OldPlayer")
    public Player getOldPlayer() {
        return oldPlayer;
    }

    /**
     * See {@link PlayerChangedMessage#newPlayer}
     */
    @XmlElement(name = "NewPlayer")
    public Player getNewPlayer() {
        return newPlayer;
    }

    /**
     * See {@link PlayerChangedMessage#oldPlayer}
     */
    public void setOldPlayer(Player oldPlayer) {
        this.oldPlayer = oldPlayer;
    }

    /**
     * See {@link PlayerChangedMessage#newPlayer}
     */
    public void setNewPlayer(Player newPlayer) {
        this.newPlayer = newPlayer;
    }
}
