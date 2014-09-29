package org.fhnw.aigs.commons;

/**
 * This enumeration helps controlling the game flow. The game logic must
 * implement the functionality of singleplayer and multiplayer games, the
 * GameMode itself has no functionality. <br>
 * There are three predefined GameModes:<br>
 * <ul>
 * <li>SinglePlayer, intended for singleplayer games against the AI</li>
 * <li>MultiPlayer, inteded for multiplayer parties with 2 or more
 * participants</li>
 * <li>Test, can be used for testing purposes</li>
 * </ul>
 *
 * @author Matthias Stöckli
 */
public enum GameMode {

    SinglePlayer,
    Multiplayer,
    Test
}
