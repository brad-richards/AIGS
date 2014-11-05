package org.fhnw.aigs.TicTacToe.server;

import org.fhnw.aigs.commons.Player;

/**
 * This class is the logical representation of a Field in TicTacToe.
 * @author Matthias Stöckli
 * @version v1.0
 */
public class TicTacToeField {
   
    /**
     * X-position of the field (0 based).
     */
    private int xPosition;
    
    /**
     * Y-position of the field (0 based).
     */
    private int yPosition;
    
    /**
     * the controlling player, null if not controlled by anyone
     */
    private Player controllingPlayer;

    /**
     * Creates a new instance of TicTacToeField.
     * @param xPosition The x-position of the field (0 based).
     * @param yPosition The y-position of the field (0 based).
     */
    public TicTacToeField(int xPosition, int yPosition){
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.controllingPlayer = null;
    }

     /**
     * See {@link TicTacToeField#xPosition}.
     */
    public int getxPosition() {
        return xPosition;
    }

     /**
     * See {@link TicTacToeField#xPosition}.
     */    
    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

     /**
     * See {@link TicTacToeField#yPosition}.
     */    
    public int getyPosition() {
        return yPosition;
    }

     /**
     * See {@link TicTacToeField#yPosition}.
     */
    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

     /**
     * See {@link TicTacToeField#controllingPlayer}.
     */    
    public Player getControllingPlayer() {
        return controllingPlayer;
    }
    
     /**
     * See {@link TicTacToeField#controllingPlayer}.
     */    
    public void setControllingPlayer(Player player){
        this.controllingPlayer = player;
    }

     /**
     * Returns true if {@link TicTacToeField#controllingPlayer} is defined, otherwise false
     */    
    public boolean hasPlayer() {
        return controllingPlayer != null;
    }
    
}
