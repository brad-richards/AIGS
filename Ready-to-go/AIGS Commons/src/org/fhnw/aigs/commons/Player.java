package org.fhnw.aigs.commons;

import java.net.Socket;
import java.util.Objects;
import javax.xml.bind.annotation.XmlTransient;

/**
 * This class represents the Player. A player has a {@link name} and a unique
 * {@link id} which is not to be confused with the ID the player can have inside
 * a game. For every game, there must be a new player. This means that one
 * player cannot be in two games. Therefore a new connection must be established
 * for every new game. This is why it is possible to identify every player by
 * his or her {@link socket}. This socket can be used to send messages.
 *
 * @author Matthias Stöckli
 */
public class Player {

    /**
     * Indicates how many players there are already. This is used to calculate
     * new player ids
     */
    private static int playerCount;
    /**
     * The unique id of the player
     */
    private int id;
    /**
     * The player's name
     */
    private String name;
    /**
     * A flag that indicates whether a player is a human being or a AI. This can
     * be used to control the game flow.
     */
    private boolean isAi;
    /**
     * Represents the socket of the player. The socket is used to transfer
     * messages to players. It is set as soon as a player wants to join a game.
     * It will not be sent in the XML messages.
     */
    @XmlTransient
    private Socket socket;

    // Default constructor for serializing purposes
    public Player() {
    }

    public Player(String name, boolean isAi) {
        this.id = playerCount;
        this.name = name;
        this.isAi = isAi;
        playerCount++;
    }

    /**
     * See {@link Player#id}.
     */
    public int getId() {
        return id;
    }

    /**
     * See {@link Player#id}.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * See {@link Player#name}.
     */
    public String getName() {
        return name;
    }

    /**
     * See {@link Player#name}.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * See {@link Player#socket}.
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * See {@link Player#isAi}.
     */
    public boolean isAi() {
        return this.isAi;
    }

    /**
     * See {@link Player#socket}.
     */
    @XmlTransient
    public Socket getSocket() {
        return socket;
    }

    /**
     * The equals method has been overriden in order to allow for the comparison
     * between to players which share the same name. This is useful when
     * receiving a message from the server and the id from the same player is
     * different even though it is the same player.
     *
     * @param otherPlayer The player to be checked
     * @return Whether the two players share the same name / are equals
     */
    @Override
    public boolean equals(Object otherPlayer) {
        if (otherPlayer == null) {
            return false;
        }
        if (getClass() != otherPlayer.getClass()) {
            return false;
        }

        final Player other = (Player) otherPlayer;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
    
    /**
     * Returns a string representation of a player consisting of the id and the name.
     * @return A string representing the player.
     */
    @Override
    public String toString(){
        return this.id + ": " + this.name;
    }
}
