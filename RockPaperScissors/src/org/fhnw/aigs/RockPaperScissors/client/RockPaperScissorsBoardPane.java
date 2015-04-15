package org.fhnw.aigs.RockPaperScissors.client;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.fhnw.aigs.RockPaperScissors.commons.GameState;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsSymbol;

/**
 * Class for displaying a single field (rock, paper or scissors). Can be used
 * either for display (e.g., for the opponent), or for selectable fields (for us).
 * The display consists of a base symbol, and an overlay (indicating win or loss).
 * The overlay just use transparency, so that the underlying symbol remains visible.
 */
public class RockPaperScissorsBoardPane extends Pane {
	private final ImageView symbolImageView;	// Symbol
	private final ImageView overlayImageView;	// Overlay

	public RockPaperScissorsBoardPane() {
		super();
		this.symbolImageView = new ImageView();
		this.overlayImageView = new ImageView();
		this.getStyleClass().add("rockPaperScisorsField");
		this.getChildren().add(symbolImageView);
		this.getChildren().add(overlayImageView);
	}

	/**
	 * Set the symbol (rock, paper or scissors) and the overlay
	 * (none, X or check) based on the game status
	 * 
	 * @param symbol Symbol für underlying symbol
	 * @param state Status for overlay
	 */
	public void setSymbol(RockPaperScissorsSymbol symbol, GameState state) {
		Image symbolImage;
		Image overlayImage;
		double height = this.getWidth();
		double width = this.getHeight();

		if (symbol == RockPaperScissorsSymbol.Paper) {
			symbolImage = new Image("/Assets/paper.png", height, width, true, false);
		} else if (symbol == RockPaperScissorsSymbol.Rock) {
			symbolImage = new Image("/Assets/rock.png", height, width, true, false);
		} else if (symbol == RockPaperScissorsSymbol.Scissors) {
			symbolImage = new Image("/Assets/scissors.png", height, width, true, false);
		} else if (symbol == RockPaperScissorsSymbol.None) {
			symbolImage = new Image("/Assets/background.png", height, width, true, false);
		} else { // No symbol
			symbolImage = null;
		}

		if (state == GameState.Win) {
			overlayImage = new Image("/Assets/win.png", height, width, true, false);
		} else if (state == GameState.Lose) {
			overlayImage = new Image("/Assets/lose.png", height, width, true, false);
		} else { // no overlay
			overlayImage = null;
		}

		setImage(symbolImageView, overlayImageView, symbolImage, overlayImage);
	}

	/**
	 * Change the symbol and overlay
	 * 
	 * @param base ImageVie-Element des Panes für das Hintergrundbild
	 * @param overlay ImageVie-Element des Panes für das Overlay-Bild
	 * @param baseImage Overlay-Bild (Kreuz, Haken oder Nichts)
	 * @param overlayImage Hintergrundbild (Leer, Schere, Stein oder Papier)
	 */
	private void setImage(ImageView base, ImageView overlay, Image baseImage, Image overlayImage) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				base.setImage(baseImage);
				overlay.setImage(overlayImage);
			}
		});
	}
}