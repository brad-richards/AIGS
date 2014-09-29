/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhnw.aigs.server.gameHandling;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;
import org.fhnw.aigs.server.gui.ServerGUI;

/**
 * Recompile and reloads all the game classes. Reloading means it recompiles and distributes
 * them using several custom ant targets. Use this function whenever you changed
 * the game logic of your games. This method sends a ForceCloseMessage to all
 * clients and terminates all games.
 * v1.0 Origin Name 'ReloadClassesAction'
 * v1.1 Refactored to 'RecompileClassesAction'
 * @version 1.2 (Raphael Stoeckli, 11.09.2014)
 */
public class RecompileClassesAction implements ActionListener {

    private Vector<String> content;
    private JList list;
    private boolean GUImode = false;
    
    public RecompileClassesAction(Vector<String> content, JList list)
    {
        this.content = content;
        this.list = list;
        this.GUImode = true;
        actionPerformed(null);
    }
    
    public RecompileClassesAction()
    {
        this.GUImode = false;
        actionPerformed(null);
    }    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        recompileClasses();
        if (this.GUImode == true)
        {
            reloadClasses();
        }
    }
    
    /**
     * Recomplie and Reloads all games/classes. This means that all .jar files in the 
     * folder "games" will be unloaded and reloaded.
     * This causes the termination of all running and waiting games.
     */
    public void recompileClasses(){
        
        String gamesDirectory = ServerConfiguration.getInstance().getGameSourcesDirectory();
        File dir = new File(gamesDirectory);
        File[] files = dir.listFiles();
        if (files.length == 0)
        {
            Logger.getLogger(RecompileClassesAction.class.getName()).log(Level.INFO, "No games to recompile found...\nContinue without action");
            return;
        }
        
        boolean state = GameLoader.rebuildClasses();

        if (state == true)
        {
            Logger.getLogger(RecompileClassesAction.class.getName()).log(Level.INFO, "-------------------\nRECOMPILED AND RELOADED ALL GAMES\n-------------------");
        }
        else
        {
            Logger.getLogger(RecompileClassesAction.class.getName()).log(Level.SEVERE, "An error occurred while compiling. Check appearance of the AIGS Commons project, appearance of the bin folder in the AIGS Commons project and the validiy of AIGS Common or the projects in the games folder.");
        }
        for (int i = 0; i < GameManager.waitingGames.size(); i++) {
            if(ServerConfiguration.getInstance().getIsConsoleMode() == false){
                ServerGUI.removeGameFromList(GameManager.waitingGames.get(i), true);                
            }
            GameManager.terminateGame(GameManager.waitingGames.get(i), "Server restarts.");

        }
        for (int i = 0; i < GameManager.runningGames.size(); i++) {
            if(ServerConfiguration.getInstance().getIsConsoleMode() == false){
                ServerGUI.removeGameFromList(GameManager.runningGames.get(i), false);
            }
            GameManager.terminateGame(GameManager.runningGames.get(i), "Server restarts.");
        }

    }
  
    /**
     * Reloads all games/classes. This is only a visual feedback
     */    
    public void reloadClasses()
    {
        ArrayList<String> games = GameLoader.getInstalledGames();
        this.content.clear();
        for(int i = 0; i < games.size(); i++)
        {
            this.content.add(games.get(i));
        }
        this.list.setListData(this.content);        
    }

}
