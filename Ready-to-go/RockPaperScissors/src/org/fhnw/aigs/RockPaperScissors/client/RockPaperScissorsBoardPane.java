package org.fhnw.aigs.RockPaperScissors.client;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.fhnw.aigs.RockPaperScissors.commons.GameState;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsSymbol;

/**
 * Klasse zur Darstellung eines Feldes (Schere, Stein, Papier).<br>
 * Entweder nur als Anzeige (des Gegners) oder zum anklicken (eigene Felder).<br>
 * Abgeleitet von Pane, kann in GridPane eingebunden werden.
 * @author Raphael Stoeckli
 * @version v1.0
 */
public class RockPaperScissorsBoardPane extends Pane{
    
    // VARIBELDEFINITIONEN
    private final ImageView symbolImageView;                                    // ImageView des Hintergrunds
    private final ImageView overlayImageView;                                   // ImageView des Overlays
    
    /**
     * Konstruktor ohne Parameter
     */
    public RockPaperScissorsBoardPane()
    {
        super();                                                                // Initialisieren der Super-Klasse
        this.symbolImageView = new ImageView();                                 // Initialisieren des ImageViews für Hintergrundbild
        this.overlayImageView = new ImageView();                                // Initialisieren des ImageViews für Overlay-Bild (Kreuz oder Haken)
        this.getStyleClass().add("rockPaperScisorsField");                      // Registrieren der Style-Klasse aus CSS (wurde bereits in Main-Klasse eingebunden)
        this.getChildren().add(symbolImageView);                                // Füge Hintergrundbild dem Pane hinzu
        this.getChildren().add(overlayImageView);                               // Lege Overlay-Bild darüber (Bild muss Alpha-Kanal enthalten, da ansonsten Hintergrund komplett verdeckt wird)
    }
    
    /**
     * Methode zum Setzen des Symbols (Schere, Stein, Papier) und des Status (Nichts, Kreuz, Haken)
     * @param symbol Symbol für Hintergrundbild
     * @param state Status, bestimmt ob nichts, ein Kreuz oder ein Haken als Overlay angezeigt wird
     */
    public void setSymbol(RockPaperScissorsSymbol symbol, GameState state)
    {
        Image symbolImage;                                                      // Definition Bild
        Image overlayImage;                                                     // "
        double height = this.getWidth();                                        // Ermittle Breite für Bild aus Breite von Pane (this)
        double width = this.getHeight();                                        // Ermittle Höhe für Bild aus Höhe von Pane (this)
        
        if (symbol == RockPaperScissorsSymbol.Paper)                            // Wenn Symbol Papier ist...
        {
            symbolImage = new Image("/Assets/Images/paper.png", height, width, true, false); // Erstelle Bild aus Assets-Package
        }
        else if (symbol == RockPaperScissorsSymbol.Rock)                        // Wenn Symbol Stein ist...
        {
            symbolImage = new Image("/Assets/Images/rock.png", height, width, true, false); // Erstelle Bild aus Assets-Package
        }
        else if (symbol == RockPaperScissorsSymbol.Scissors)                    // Wenn Symbol Schere ist...
        {
            symbolImage = new Image("/Assets/Images/scissors.png", height, width, true, false); // Erstelle Bild aus Assets-Package
        }
        else if (symbol == RockPaperScissorsSymbol.None)                        // Wenn kein Symbol (nur leeres Hintergrundbild) dargestellt werden soll...
        {
            symbolImage = new Image("/Assets/Images/background.png", height, width, true, false); // Erstelle Bild aus Assets-Package
        }
        else                                                                    // Wenn Bild komplett gelöscht werden soll...
        {
            symbolImage = null;                                                 // Bild löschen (null stellt nichts dar)
        }
        
        if (state == GameState.Win)                                             // Wenn ein Haken gesetzt werde soll (Gewonnen)...
        {
            overlayImage = new Image("/Assets/Images/win.png", height, width, true, false); // Erstelle Bild für Overlay aus Assets-Package
        }
        else if (state == GameState.Lose)                                       // Wenn ein Kreuz gesetzt werde soll (Verloren)...
        {
            overlayImage = new Image("/Assets/Images/lose.png", height, width, true, false); // Erstelle Bild für Overlay aus Assets-Package
        }
        else                                                                    // Wenn nichts gesetzt werde soll (Gewonnen)...
        {
            overlayImage = null;                                                // Overlay-Bild löschen (null stellt nichts dar)
        }
        
        setImage(symbolImageView, overlayImageView, symbolImage, overlayImage); // Aufruf der eigentlichen Methode zum Ändern der Bilder
    }
    
    /**
     * Methode zum Ändern der Bilder (Hintergrund und Overlay)<br>
     * Die Manipulation muss in Platform.runLater erfolgen um einen Absturz zur Laufzeit zu verhindern
     * @param base ImageVie-Element des Panes für das Hintergrundbild
     * @param overlay ImageVie-Element des Panes für das Overlay-Bild
     * @param baseImage Overlay-Bild (Kreuz, Haken oder Nichts)
     * @param overlayImage Hintergrundbild (Leer, Schere, Stein oder Papier)
     */
    private void setImage(ImageView base, ImageView overlay, Image baseImage, Image overlayImage)
    {
         Platform.runLater(new Runnable(){                                      // WICHTIG! Bilder dürfen nicht direkt auf den ImageViews geändert werden, sondern müssen in Platform.runLater abgearbeitet werden
            @Override                                                           // Impementierte Methode
            public void run()
            {
                base.setImage(baseImage);                                       // Setze Hintergrundbild
                overlay.setImage(overlayImage);                                 // Setze Overlay-Bild
            }
        });       
    }
    
    
}
