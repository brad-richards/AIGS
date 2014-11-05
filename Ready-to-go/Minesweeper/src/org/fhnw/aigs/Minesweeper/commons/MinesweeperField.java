package org.fhnw.aigs.Minesweeper.commons;

/**
 * This class represents the logical unit of a field. It is not to be confused
 * with MinesweeperPane. It contains all information on a fields: Whether it
 * contains a mine, was flagged, how many surrounding mines there are, the
 * position and whether it has been uncovered yet.
 *
 * @author Matthias Stöckli
 * @version v1.0
 */
public class MinesweeperField {

    /**
     * Indicates whether or not the field contains a mine.
     */
    private boolean containsMine;
    
    /**
     * Indicates whether or not the field was flagged by the player.
     */
    private boolean hasFlag;
    
    /**
     * Indicates whether or not the field was uncovered.
     */
    private boolean isUncovered;
    
    /**
     * The number of mines around the field.
     */
    private int surroundingMinesCount;
    
    /**
     * The x-position of the field.
     */
    private int xPosition;
    
    /**
     * The y-position of the field.
     */
    private int yPosition;
    

    /** See {@link MinesweeperField#containsMine}. */
    public boolean getContainsMine() {
        return containsMine;
    }

    /** See {@link MinesweeperField#containsMine}. */
    public void setContainsMine(boolean containsMine) {
        this.containsMine = containsMine;
    }

    /** See {@link MinesweeperField#xPosition}. */
    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    /** See {@link MinesweeperField#yPosition}. */
    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    /** See {@link MinesweeperField#surroundingMinesCount}. */
    public int getSurroundingMinesCount() {
        return surroundingMinesCount;
    }

    /** See {@link MinesweeperField#hasFlag}. */
    public boolean getHasFlag() {
        return hasFlag;
    }

    /** See {@link MinesweeperField#isUncovered}. */
    public boolean getIsUncovered() {
        return isUncovered;
    }

    /** See {@link MinesweeperField#xPosition}. */
    public int getxPosition() {
        return xPosition;
    }

    /** See {@link MinesweeperField#yPosition}. */
    public int getyPosition() {
        return yPosition;
    }

    /** See {@link MinesweeperField#surroundingMinesCount}. */
    public void setSurroundingMinesCount(int surroundingMinesCount) {
        this.surroundingMinesCount = surroundingMinesCount;
    }

    /** See {@link MinesweeperField#hasFlag}. */
    public void setHasFlag(boolean hasFlag) {
        this.hasFlag = hasFlag;
    }

    /** See {@link MinesweeperField#isUncovered}. */
    public void setIsUncovered(boolean isUncovered) {
        this.isUncovered = isUncovered;
    }

    /**
     * Shows the field as a string (shows the position).
     * @return 
     */
    @Override
    public String toString() {
        return xPosition + " / " + yPosition;
    }

    /**
     * Adds 1 to the surroundingMinesCount.
     */
    public void addOneToMineCount() {
        surroundingMinesCount++;
    }

    /**
     * Checks whether the field has any surrounding mines.
     * @return True if there are no surrounding mines, false there are any.
     */
    public boolean hasNoSurroundingMines() {
        return surroundingMinesCount == 0;
    }
}
