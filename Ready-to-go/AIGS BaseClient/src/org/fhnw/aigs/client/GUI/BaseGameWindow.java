package org.fhnw.aigs.client.GUI;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * This class provides represents the "frame" around a game. It is structured in
 * the following way:<br><ul>
 * <li>On the top, there is the {@link header}. It takes up 100% width and 10%
 * height.You can edit it's content via {@link BaseHeader#setGameNameText} and
 * {@link BaseHeader#setStatusLabelText}, see {@link BaseHeader}.</li>
 * <li>In the middle there is the actual content, the game itself. This will
 * take up 85 percent of the space. The content can be accessed via
 * {@link BaseGameWindow#getContent}.</li>
 * <li>The {@link BaseGameWindow#footer} is the last part which only takes up
 * 5%. It can be accessed via {@link BaseGameWindow#getFooter} but does not have
 * any functionality in the base client.</li>
 * </ul>
 *
 * @author Matthias Stöckli
 */
public class BaseGameWindow extends GridPane {

    /**
     * The JavaFX scene - in some way the application window itself
     */
    private Scene scene;
    /**
     * The stage represents the content of the scene
     */
    private Stage primaryStage;
    /**
     * Allows to show an overlay like a loading window. Use
     * {@link BaseGameWindow#setOverlay} and
     * {@link BaseGameWindow#fadeOutOverlay()} for that purpose.
     */
    private Node overlay;
    /**
     * The header, contains the game title and a status label which can be
     * modified
     */
    private BaseHeader header;
    /**
     * The actual game content, most of the times an intance of a class
     * inheriting of {@link BaseBoard}. Use the
     * {@link BaseGameWindow#setContent} to set the content.
     */
    private Node content;
    /**
     * The footer of the window. It can be changed with
     * {@link BaseGameWindow#setFooter} if needed.
     */
    private Pane footer;

    /**
     * This is the recommended constructor. This allows to use a stylesheet
     * which can then be used in other classes such as Fields or Boards.
     *
     * @param primaryStage The primary stage which is given by the main method.
     * @param gameStylesheetPath The (relative) path to a stylesheet (css).
     * Please note that the stylesheet must be stored in a folder which is used
     * as a source folder. Netbeans automatically adds folders to the source
     * folder, in other IDEs it may be necessary to add the folder to the class
     * path. If the stylesheet is store in the "src" folder, it should be
     * recognized by most IDEs immediately.
     * @param title The title of the game which will be displayed as the
     * window's title.
     */
    public BaseGameWindow(Stage primaryStage, String gameStylesheetPath, String title) {
        this(primaryStage, title);
        scene.getStylesheets().add(gameStylesheetPath);
    }

    /**
     * See
     * {@link BaseGameWindow#BaseGameWindow(javafx.stage.Stage, java.lang.String, java.lang.String)}.
     * This constructor does not specify a stylesheet.
     *
     * @param primaryStage The primary stage which is given by the main method.
     * @param title The title of the game which will be displayed as the
     * window's title.
     */
    public BaseGameWindow(Stage primaryStage, String title) {
        this.primaryStage = primaryStage;

        // Set the scree dimensions (4/3 of the screens width)
        // this makes sure that the Game Window always appears to be of the same
        // size.
        Screen screen = Screen.getPrimary();
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.setWidth(screen.getVisualBounds().getHeight() / 4 * 3);
        primaryStage.setHeight(screen.getVisualBounds().getHeight() / 4 * 3);
        primaryStage.setResizable(false);

        scene = new Scene(this);
        scene.getStylesheets().add("/Assets/Stylesheets/base.css");
        primaryStage.setScene(scene);

        // F11 performs Fullscreen toggle. This is more of a gimmick.
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if (t.getCode() == KeyCode.F11) {
                    toggleFullScreen();
                }
            }
        });

        // Constraints, the distribution on the horizontal level:
        RowConstraints headerConstraint = new RowConstraints();
        headerConstraint.setPercentHeight(10);
        RowConstraints contentRowConstraint = new RowConstraints();
        contentRowConstraint.setPercentHeight(85);
        RowConstraints footerConstraint = new RowConstraints();
        footerConstraint.setPercentHeight(5);
        this.getRowConstraints().addAll(headerConstraint, contentRowConstraint, footerConstraint);

        // Constraints, the distribution on the vertical level
        ColumnConstraints leftSpaceConstraint = new ColumnConstraints();
        leftSpaceConstraint.setPercentWidth(8);
        ColumnConstraints contentColumnConstraint = new ColumnConstraints();
        contentColumnConstraint.setPercentWidth(84);
        ColumnConstraints rightSpaceConstraint = new ColumnConstraints();
        rightSpaceConstraint.setPercentWidth(8);
        this.getColumnConstraints().addAll(leftSpaceConstraint, contentColumnConstraint, rightSpaceConstraint);

        this.getRowConstraints().add(new RowConstraints(RowConstraints.CONSTRAIN_TO_PREF));

        // Load the font used in the css
        Font.loadFont(getClass().getResource("/Assets/Fonts/AeroviasBrasilNF.ttf").toExternalForm(), 12);

        // Create header pane and style it
        Pane headerPane = new Pane();
        headerPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        headerPane.getStyleClass().add("darkBackground");

        // Create footer and style it
        footer = new Pane();
        footer.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        footer.getStyleClass().add("darkBackground");

        // Create left margin and style it
        Pane leftSpace = new Pane();
        leftSpace.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        leftSpace.getStyleClass().add("semiDarkBackground");

        // Create right margin and style it
        Pane rightSpace = new Pane();
        rightSpace.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        rightSpace.getStyleClass().add("semiDarkBackground");

        // Define header
        header = new BaseHeader(title);

        this.add(headerPane, 0, 0, 3, 1);
        this.add(header, 1, 0, 1, 1);

        // Add left and right space.
        this.add(leftSpace, 0, 1, 1, 1);
        this.add(rightSpace, 2, 1, 1, 1);
        this.add(footer, 0, 2, 3, 1);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });

    }

    /**
     * Activates full screen mode or deactivates it, if it already is in full
     * scren mode. The process will make use of the {@link javafx.application.Platform#runLater(java.lang.Runnable)}
     * method because this opperation must be performed on the JavaFX thread.
     * Please note: As the aspect ratio of normal games will be 1:1, the image
     * may be distortet.
     */
    public void toggleFullScreen() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                boolean isFullScreen = primaryStage.isFullScreen();
                if (isFullScreen) {
                    primaryStage.setFullScreen(false);
                } else {
                    primaryStage.setFullScreen(true);
                }
            }
        });

    }

    /**
     * See {@link BaseGameWindow#header}.
     */
    public BaseHeader getHeader() {
        return this.header;
    }

    /**
     * See {@link BaseGameWindow#footer}.
     */
    public Pane getFooter() {
        return this.footer;
    }

    /**
     * See {@link BaseGameWindow#content}.
     */
    public Node getContent() {
        return this.content;
    }

    /**
     * See {@link BaseGameWindow#content}.
     */
    public void setContent(Node newContent) {
        this.getChildren().remove(content);
        this.content = newContent;
        this.add(content, 1, 1);
        content.toFront();
    }

    /**
     * See {@link BaseGameWindow#header}.
     */
    public void setHeader(BaseHeader header) {
        this.header = header;
    }

    /**
     * See {@link BaseGameWindow#footer}.
     */
    public void setFooter(Pane footer) {
        this.footer = footer;
    }

    /**
     * See {@link BaseGameWindow#content}.
     */
    public void removeContent() {
        this.getChildren().remove(content);
        this.content = null;

    }

    /**
     * See {@link BaseGameWindow#overlay}.
     */
    public void setOverlay(Node overlay) {
        this.overlay = overlay;
        try {
            this.add(overlay, 1, 1);
        } catch (Exception e) {
        }
        overlay.toFront();
        content = null;
    }

    /**
     * This method allows to fade out an overlay set by
     * {@link BaseGameWindow#setOverlay}.
     */
    public void fadeOutOverlay() {
        FadeTransition ft = new FadeTransition(Duration.seconds(1), overlay);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.play();
        ft.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent args) {
                overlay.toBack();
                overlay = null;
            }
        });
    }
}
