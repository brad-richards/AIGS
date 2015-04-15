package org.fhnw.aigs.RockPaperScissors.commons;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GameState")
public enum GameState { None, Win, Lose, Draw }