package org.fhnw.aigs.client.GUI;

import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import org.fhnw.aigs.client.gameHandling.ClientGame;
import org.fhnw.aigs.commons.communication.Message;

/**
 * The <b>abstract</b> BaseBoard class offers a convenient base class for
 * classic board games like chess. It is limited to two dimensions: X and Y.
 * <br>
 * The fields of the board are defined as <b>Panes</b> which act as a very
 * flexible container for all kinds of fields. Due to the nature of the parent
 * class, <b>GridPane</b> it is possible to add new content inside the grid by
 * using the "add" method in the following way: <br>
 * board.add(field, xPosition, yPosition).<br>
 * Please note: It is not possible to use BaseBoard directly as it is an
 * abstract class.<br>
 * Please note: The BaseBoard is designed with square-shaped boards in mind.
 * While it is possible to use other ratios, it will result in distortet
 * graphics.
 *
 * @author Matthias Stöckli
 * @version v1.0
 */
public abstract class BaseBoard extends GridPane {

    /**
     * The fields, represented as a two dimensional array
     */
    protected final Pane[][] fieldPanes;
    /**
     * The number of fields on the x-axis
     */
    protected final int fieldsX;
    /**
     * The number of fields on the y-axis
     */
    protected final int fieldsY;
    /**
     * The client game. The client game is used as "glue" between GUI and game.
     */
    protected ClientGame clientGame;

    /**
     * The constructor of the <b>BaseBoard</b>.
     *
     * @param fieldsX The number of fields on the x-axis
     * @param fieldsY The number of fields on the y-axis
     * @param clientGame The client game. The client game is used as "glue"
     * between GUI and game.
     */
    public BaseBoard(int fieldsX, int fieldsY, ClientGame clientGame) {
        this.fieldsX = fieldsX;
        this.fieldsY = fieldsY;
        this.fieldPanes = new Pane[fieldsX][fieldsY];
        this.clientGame = clientGame;
        this.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE); // For layout purposes
        setConstraints();                                       // Defines the grid structure of the BaseBoard
    }

    /**
     * {@link BaseBoard#fieldsX}
     */
    public int getFieldsX() {
        return fieldsX;
    }

    /**
     * {@link BaseBoard#fieldsY}
     */
    public int getFieldsY() {
        return fieldsY;
    }

    /**
     * {@link BaseBoard#fieldPanes}
     */
    public Pane[][] getFieldPanes() {
        return fieldPanes;
    }

    /**
     * Defines the grid structure of the BaseBoard. Basically, it calculates how
     * many percent of the available each field uses up. If the number of X and
     * Y fields is not equal, the bigger number will be the base of the
     * calculation. This prevents the fields from being distorted. Additionally
     * this method distributes space in the case of nonwhole numbers. E.g. if
     * the grid is 13 x 13 then 100/13 is not an integer. Therefore the the
     * percentage which every field uses must be distributed accross all fields.
     */
    private void setConstraints() {
        double percentagePerField = 0;
        int percentagePerFieldRounded = 0;
        boolean xIsBigger = (double) fieldsX > (double) fieldsY;
        if (xIsBigger) {
            percentagePerField = 100 / (double) fieldsY;
        } else {
            percentagePerField = 100 / (double) fieldsX;
        }

        // Check if the percentage is a whole number/integer
        // This must be distributed accross the fields         
        int distanceToInteger = 0;
        boolean isInteger = (percentagePerField == Math.floor(percentagePerField)) && !Double.isInfinite(percentagePerField);
        if (isInteger == false) {

            if (xIsBigger) {
                distanceToInteger = (int) (100 - Math.round(percentagePerField) * fieldsY);
            } else {
                distanceToInteger = (int) (100 - Math.round(percentagePerField) * fieldsX);
            }
        }
        percentagePerFieldRounded = (int) Math.round(percentagePerField);
        int distributionToRow = distanceToInteger;
        int distributionToColumn = distanceToInteger;

        // Distribute the rows.
        for (int i = 0; i < fieldsX; i++) {
            RowConstraints rc = new RowConstraints();
            if (distributionToRow > 0) {
                rc.setPercentHeight(percentagePerFieldRounded + 1);
            } else if (distributionToRow < 0) {
                rc.setPercentHeight(percentagePerFieldRounded - 1);
            } else {
                rc.setPercentHeight(percentagePerFieldRounded);
            }
            this.getRowConstraints().add(rc);

            if (distributionToRow < 0) {
                distributionToRow++;
            } else if (distributionToRow > 0) {
                distributionToRow--;
            }
        }

        // Distribute the collumns
        for (int i = 0; i < fieldsY; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            if (distributionToColumn > 0) {
                cc.setPercentWidth(percentagePerFieldRounded + 1);
            } else if (distributionToColumn < 0) {
                cc.setPercentWidth(percentagePerFieldRounded - 1);
            } else {
                cc.setPercentWidth(percentagePerFieldRounded);
            }
            this.getColumnConstraints().add(cc);

            if (distributionToColumn < 0) {
                distributionToColumn++;
            } else if (distributionToColumn > 0) {
                distributionToColumn--;
            }
        }
    }

    /**
     * Used to define the fields of the board. Usually the appearance of the
     * fields will also be defined by this method. A structure like this should
     * work well:<br>
     * <br><code>
     * // Iterate through all fields on the x and y axis, add the field to the
     * for(int i = 0; i &lt; fieldsX; i++){
     *     for(int j = 0; j &lt; fieldsY; j++){
     *        // Instantiate a new field, add it to the fieldPanes and add the field
     *        // as a graphical node.
     *        YourField yourField = new TicTacToeField();
     *        yourBoard[i][j] = yourBoard;
     *        this.add(yourField, i, j);
     *    }
     *}</code> Where YourField is a custom field class, usually a Pane or
     * similar and yourBoard is the board instance.
     */
    public abstract void setFields();

    /**
     * In this method the event handlers will be set, i.e. what each part of the
     * GUI actually does.
     */
    public abstract void setEventHandlers();

    /**
     * This message should contain all GUI related tasks based on the messages
     * received. Usually this method should be called by the
     * {@link org.fhnw.aigs.client.gameHandling.ClientGame#processGameLogic(org.fhnw.aigs.commons.communication.Message)} method.
     *
     * @param message The message to be processed. If there is no message, just
     * pass null.
     */
    public abstract void manipulateGUI(Message message);

    /**
     * Gets one of the board's fields.
     *
     * @param x The x position of the field.
     * @param y The y position of the field.
     * @return The board at the position x/y
     */
    public Pane getField(int x, int y) {
        return this.fieldPanes[x][y];
    }

    /**
     *
     * @param node The content which will replace the existing content of a
     * field.
     * @param x The x position of the field
     * @param y The y position of the field
     */
    public void replaceField(Node node, int x, int y) {
        this.fieldPanes[x][y].getChildren().clear();
        this.fieldPanes[x][y].getChildren().add(node);
    }
}
