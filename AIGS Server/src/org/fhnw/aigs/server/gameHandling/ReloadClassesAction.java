package org.fhnw.aigs.server.gameHandling;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JList;

/**
 * Class manages reloading of dynamically loaded classes in the server.<br>
 * The reloade function has only informative purpose. The actual reloading
 * of classes happens at runtime when connection to games.
 * @author Raphael Stoeckli
 * @version v1.0
 */
public class ReloadClassesAction implements ActionListener{
    
    private Vector<String> content;
    private JList list;

    /**
     * Action to reload classes (only informative)
     * @param content Vector with content
     * @param list the JList to update
     */
    public ReloadClassesAction(Vector<String> content, JList list)
    {
        this.content = content;
        this.list = list;
        actionPerformed(null); // Initialize
    } 
    
    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayList<String> games = GameLoader.getInstalledGames();
        this.content.clear();
        for(int i = 0; i < games.size(); i++)
        {
            this.content.add(games.get(i));
        }
        this.list.setListData(this.content);

    }
    
}
