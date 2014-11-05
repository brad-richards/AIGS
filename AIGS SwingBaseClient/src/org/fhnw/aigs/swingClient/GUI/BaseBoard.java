package org.fhnw.aigs.swingClient.GUI;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;
import org.fhnw.aigs.swingClient.gameHandling.ClientGame;
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
 * abstract class.
 *
 * @author Matthias Stöckli
 * @version v1.0
 */
public abstract class BaseBoard extends JPanel {

    /**
     * The fields, represented as a two dimensional array
     */
    protected final JPanel[][] fieldPanes;
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
        // 10% for header
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = new Dimension(screenSize.width/4*3, screenSize.height/4*3);
        Dimension headerSize = new Dimension(windowSize.width, windowSize.height/10);
        
        this.setMinimumSize(headerSize);
        this.setMaximumSize(headerSize);
        this.setPreferredSize(headerSize);

        this.fieldsX = fieldsX;
        this.fieldsY = fieldsY;
        this.setLayout(new GridLayout(fieldsX, fieldsY));
        this.fieldPanes = new JPanel[fieldsX][fieldsY];
        this.clientGame = clientGame;
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
    public JPanel[][] getFieldPanes() {
        return fieldPanes;
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
     * In this method the event handlers will be set, i.e. what every part of
     * the GUI actually does.
     */
    public abstract void setEventHandlers();

    /**
     * This message should contain all GUI related tasks based on the messages
     * received. Usually this method should be called by the
     * {@link org.fhnw.aigs.commons.Game#processGameLogic(org.fhnw.aigs.commons.communication.Message, org.fhnw.aigs.commons.Player)} method.
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
    public JPanel getField(int x, int y) {
        return this.fieldPanes[x][y];
    }

    /**
     * Method replaces a field on the specified position
     * @param newPanel The content which will replace the existing content of a
     * field.
     * @param x The x position of the field
     * @param y The y position of the field
     */
    public void replaceField(JPanel newPanel, int x, int y) {
        this.fieldPanes[x][y] = null;
        this.fieldPanes[x][y] = newPanel;
    }
}
