package org.fhnw.aigs.server.gui;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.fhnw.aigs.server.gameHandling.User;

/**
 * This class represents a window to manage the user settings. These settings 
 * will be stored in {@link User#users}. The user management is only applying 
 * if anonymous login is disable (users have to log in).
 * @author Raphael Stoeckli (29.10.2014)
 * @version 1.0
 */
public class UserSettingsWindow extends JDialog {
    
    private JButton addButton;
    private JButton alterButton;
    private JButton cancelButton;
    private JButton newButton;
    private JButton removeButton;
    private JButton saveButton;
    private JLabel userIDLabel;
    private JList userList;
    private JTextField usernameField;
    private JTextField passwordField;
    private Vector<User> userContent;
    private User currentUser;
    private boolean changesApplied;
    
    /**
     * Standard constructor without parameters
     */
    public UserSettingsWindow()
    {
        ImageIcon logoImage = new ImageIcon(getClass().getResource("/imgs/logo24px.png"));
        this.setIconImage(logoImage.getImage());
        this.setLocationByPlatform(true); // Better positioning of the window
        this.setResizable(false);  
        this.setAlwaysOnTop(true);
        this.setModal(true);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("AIGS Server - User Management");
        this.setPreferredSize(new Dimension(515, 255));
        init();
        loadData();
        setNewUser();
    }
    
    /**
     * Initilizes the UI. The lower part of this method was genereated by the GUI builder
     * of NetBeans and cleaned up by hand
     */
    private void init()
    {
        changesApplied = false;
        newButton = new JButton("New");
        newButton.setToolTipText("Clears all fields and prepares the form to add a new user");
        addButton = new JButton("Create");
        addButton.setToolTipText("Adds a new user to te user list");
        alterButton = new JButton("Alter");
        alterButton.setToolTipText("Alters the current selected uset");
        removeButton = new JButton("Remove");
        removeButton.setToolTipText("Removes the current user from the user list");
        saveButton = new JButton("Save");
        saveButton.setToolTipText("Saves the user settings and closes this window");
        cancelButton = new JButton("Cancel");
        cancelButton.setToolTipText("Discards all changes and closes this window");
        userIDLabel = new JLabel();
        userIDLabel.setText("---");
        usernameField = new JTextField();
        passwordField = new JTextField();
        userContent = new Vector<User>();
        userList = new JList(userContent);
        
        JLabel usernameLabel = new JLabel("User name:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel userIDCaptionLabel = new JLabel("User ID:");
        
        JScrollPane usernameScrollPane = new JScrollPane();
        JScrollPane passwordScrollPane = new JScrollPane();
        JScrollPane userlistScrollPane = new JScrollPane();
        
        userlistScrollPane.setViewportView(userList);
        usernameScrollPane.setViewportView(usernameField);
        passwordScrollPane.setViewportView(passwordField);
        
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        alterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                alterButtonActionPerformed(evt);
            }
        });
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        
        usernameField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                usernameFieldKeyReleased(evt);
            }
        });
        
        userList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                userListValueChanged(evt);
            }
        }); 

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(passwordLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(passwordScrollPane))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(usernameLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(usernameScrollPane))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(saveButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(newButton)
                                .addGap(6, 6, 6)
                                .addComponent(addButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(alterButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(removeButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(userIDCaptionLabel)
                                .addGap(18, 18, 18)
                                .addComponent(userIDLabel)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(userlistScrollPane, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(newButton)
                            .addComponent(addButton)
                            .addComponent(alterButton)
                            .addComponent(removeButton))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(userIDCaptionLabel)
                            .addComponent(userIDLabel))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(usernameLabel)
                            .addComponent(usernameScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(passwordLabel)
                            .addComponent(passwordScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 92, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(saveButton)
                            .addComponent(cancelButton)))
                    .addComponent(userlistScrollPane))
                .addContainerGap())
        );
        this.addWindowListener(new UserSettingsWindow.CloseListener());
        pack();
    }
    
    /**
     * Method to load the data from {@link User#users}.
     */
    private void loadData()
    {
        this.userContent.clear();
        for(int i = 0; i < User.users.size(); i++)
        {
            if (User.users.get(i).isNonPersistentUser() == true) // No non-persistent users
            {
                continue;
            }
            this.userContent.add(User.users.get(i));
        }
        userList.setListData(this.userContent); 
   }
    
    /**
     * Metod to prepare the form for inserting a new user. This method adds no 
     * new user to the list. Use {@link UserSettingsWindow#createNewUser()} for this task
     */
    private void setNewUser()
    {
        userList.setSelectedIndex(-1);
        usernameField.setText("");
        passwordField.setText("");
        userIDLabel.setText("---");
        removeButton.setEnabled(false);
        alterButton.setEnabled(false);
        addButton.setEnabled(false);
        this.currentUser = null;
    }
    
    /**
     * Method fills the data from the defined user into the form. If no user (null)
     * was specified {@link UserSettingsWindow#setNewUser()} will be called
     * @param user User to process
     */
    private void setCurrentUser(User user)
    {
        if (user == null )
        {
            setNewUser();
            return;
        }
        usernameField.setText(user.getUserName());
        passwordField.setText(user.getIdentificationCode());
        userIDLabel.setText(Long.toString(user.getId()));
        removeButton.setEnabled(true);
        alterButton.setEnabled(true);
        addButton.setEnabled(false);
        this.currentUser = user;    
    }
    
    /**
     * Method creates a new user into the list
     */
    private void createNewUser()
    {
        String username = usernameField.getText();
        if (username.length() == 0)
        {
            JOptionPane.showMessageDialog(this, "Please insert a valid user name", "Invalid user name", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int len = this.userContent.size();
        for(int i = 0; i < len; i++)
        {
            if (this.userContent.get(i).getUserName().equalsIgnoreCase(username))
            {
            JOptionPane.showMessageDialog(this, "The user name already exists. Please select another one.", "Invalid user name", JOptionPane.INFORMATION_MESSAGE);
            return;                
            }
        }
        this.currentUser = new User(username, passwordField.getText());
        //setCurrentUser(this.currentUser);
        this.userContent.add(this.currentUser);
        userList.setListData(this.userContent);
        int size = userList.getModel().getSize(); 
        userList.setSelectedIndex(size - 1);
        userList.ensureIndexIsVisible( size - 1 );  
    }
    
    /**
     * Method alters the data of the currently selected user
     */
    private void alterUser()
    {
        if (currentUser == null)
        {
            return;
        }
        String username = usernameField.getText();
        if (username.length() == 0)
        {
            JOptionPane.showMessageDialog(this, "Please insert a valid user name", "Invalid user name", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int len = this.userContent.size();
        for(int i = 0; i < len; i++)
        {
            if (this.userContent.get(i).getUserName().equalsIgnoreCase(username) && currentUser.getUserName().equalsIgnoreCase(username) == false)
            {
            JOptionPane.showMessageDialog(this, "The user name already exists. Please select another one.", "Invalid user name", JOptionPane.INFORMATION_MESSAGE);
            return;                
            }
        }
        this.currentUser.setUserName(username);
        this.currentUser.setIdentificationCode(passwordField.getText());
        for(int i = 0; i < this.userContent.size(); i++)
        {
          if(this.userContent.get(i).getId() == this.currentUser.getId())
          {
              this.userContent.set(i, this.currentUser);
              break;
          }
        }
        userList.setListData(this.userContent);
          
    }
    
    /**
     * Method removes the currently selected user from the list
     */
    private void deleteUser()
    {
        if (currentUser == null)
        {
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, "Do you want do delete the user '" + this.currentUser.getUserName() + "'?", "Delete user", JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.YES_OPTION) { return; }
        for(int i = 0; i < this.userContent.size(); i++)
        {
          if(this.userContent.get(i).getId() == this.currentUser.getId())
          {
              this.userContent.remove(i);
              break;
          }
        }
        this.currentUser = null;
        userList.setListData(this.userContent);
    }
    
    /**
     * Method stores the changes to the static user list in {@link User#users}
     */
    private void save()
    {
        for (int i =  User.users.size() -1; i >= 0; i--)
        {
            if (User.users.get(i).isNonPersistentUser() == true)
            {
                continue;
            }
            else
            {
                User.removeUserFromUserList(User.users.get(i)); // remove persistent user
            }
        }
        for(int i = 0; i < this.userContent.size(); i++) 
        {
            User.addUserToUserList(this.userContent.get(i)); // Add persistent user
        }
        User.writeUsersToXml(); // Save persistent users
    }    
    
    /**
     * Method to tigger the action of preparing a new user.<br>
     * see {@link UserSettingsWindow#setNewUser()}
     * @param evt Action event of the calling UI element
     */
    private void newButtonActionPerformed(ActionEvent evt) {                                          
        setNewUser();
    }    
    
    /**
     * Method to tigger the action of creating a new user.<br>
     * see {@link UserSettingsWindow#createNewUser()}
     * @param evt Action event of the calling UI element
     */    
    private void addButtonActionPerformed(ActionEvent evt) {                                          
        createNewUser();
        changesApplied = true;
    }                                         

    /**
     * Method to tigger the action of altering a user.<br>
     * see {@link UserSettingsWindow#alterUser()}
     * @param evt Action event of the calling UI element
     */    
    private void alterButtonActionPerformed(ActionEvent evt) {                                            
        alterUser();
        changesApplied = true;
    }  
    
    /**
     * Method to tigger the action of removing a user.<br>
     * see {@link UserSettingsWindow#deleteUser()}
     * @param evt Action event of the calling UI element
     */    
    private void removeButtonActionPerformed(ActionEvent evt) {                                             
        deleteUser();
        changesApplied = true;
    }       

    /**
     * Method to tigger the action of saving changes.<br>
     * see {@link UserSettingsWindow#save()}
     * @param evt Action event of the calling UI element
     */    
    private void saveButtonActionPerformed(ActionEvent evt) {                                           
            if (changesApplied == false)
            {
                JOptionPane.showMessageDialog(this, "No changes have been made", "No changes", JOptionPane.INFORMATION_MESSAGE);                
            }
            else
            {
                save();
            }
        CloseListener cl = new CloseListener();
        cl.closeWindow(this);
    }                                          

    /**
     * Method to tigger the closing action (discard changes) of the window
     * @param evt Action event of the calling UI element
     */    
    private void cancelButtonActionPerformed(ActionEvent evt) {                                             
        CloseListener cl = new CloseListener();
        cl.closeWindow(this);
    }                                            

    /**
     * Method to tigger the action of changing the user selection in the list. 
     * This is used to set the {@link UserSettingsWindow#currentUser}
     * @param evt Action event of the calling UI element
     */    
    private void userListValueChanged(ListSelectionEvent evt) {                                      
        try
        {
            setCurrentUser((User)userList.getSelectedValue());
        }
        catch(Exception ex)
        {
            setNewUser();
        }
                
    }                                     

    /**
     * Method to tigger the action of changing the user name. 
     * This method is to decide whether the buttons to add, alter or remove a 
     * user are enabled or disabled
     * @param evt Action event of the calling UI element
     */    
    private void usernameFieldKeyReleased(KeyEvent evt) {                                          
        if (usernameField.getText().length() == 0)
        {
            alterButton.setEnabled(false);
            addButton.setEnabled(false);
        }
        else
        {
            if (this.currentUser == null)
            {
                alterButton.setEnabled(false);
                addButton.setEnabled(true);                
            }
            else
            {
                alterButton.setEnabled(true);
                addButton.setEnabled(false);
            }
        }
    }                                                   
    
    /**
     * Class to handle the close action of the window 
     */    
    private class CloseListener implements WindowListener
    {
        
        /**
         * Standard constructor
         */        
        public CloseListener()
        { }
        
        /**
         * Method to perform the actual closing action. This method can also called 
         * by creating an instance of this class manually
         * @param window 
         */        
        public void closeWindow(Window window)
        {
            window.dispose();
        }
        
        /** This method is empty */
        @Override
        public void windowOpened(WindowEvent e) { /* Do nothing */ }
        /**
         * Called, when closing the window
         * @param e Action event of the calling UI element
         */
        @Override
        public void windowClosing(WindowEvent e) {
            this.closeWindow(e.getWindow());
        }
        /** This method is empty */
        @Override
        public void windowClosed(WindowEvent e) { /* Do nothing */ }
        /** This method is empty */
        @Override
        public void windowIconified(WindowEvent e) { /* Do nothing */ }
        /** This method is empty */
        @Override
        public void windowDeiconified(WindowEvent e) { /* Do nothing */ }
        /** This method is empty */
        @Override
        public void windowActivated(WindowEvent e) { /* Do nothing */ }
        /** This method is empty */
        @Override
        public void windowDeactivated(WindowEvent e) { /* Do nothing */ }
    }    
    
}
