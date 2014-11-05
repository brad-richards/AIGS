/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhnw.aigs.commons;

/**
 * This enumerator is for definition of the type of joining a game on the AIGS server.<br>
 * <ul>
 * <li>CreateNewGame: A new game with a specific party name will be created. The game will be public</li>
 * <li>CreateNewPrivateGame: A new game with a specific party name will be created. The game will be private</li>
 * <li>JoinParticularGame: A game with a specific nam will be joined</li>
 * <li>Auto: Will join to a random waiting (public) game or create a new (public) one, if no game available</li>
 * </ul>
 * @author Raphael Stoeckli (23.10.2014)
 * @version v1.0
 */
public enum JoinType {
    CreateNewGame,
    CreateNewPrivateGame,
    JoinParticularGame,
    Auto
}
