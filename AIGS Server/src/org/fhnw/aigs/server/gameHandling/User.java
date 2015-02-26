package org.fhnw.aigs.server.gameHandling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import org.fhnw.aigs.commons.communication.IdentificationResponseMessage;
import org.fhnw.aigs.server.common.LogRouter;
import org.fhnw.aigs.server.common.LoggingLevel;
import org.fhnw.aigs.server.gui.ServerGUI;
import org.fhnw.aigs.server.common.ServerConfiguration;

/**
 * This class is responsible for all User related tasks. It loads generates user
 * files, loads users, logs them on and off.<br>
 * v1.0 Initial release<br>
 * v1.1 Major changes in handling<br>
 * v1.1.1 Bugfixes<br>
 * v1.2 Changing of logging
 *
 * @author Matthias Stöckli
 * @version 1.2 (Raphael Stoeckli, 24.02.2015)
 */
@XmlRootElement(name = "User")
public class User {

    /**
     * A number used to generate unique user IDs. Will not be marshalled.
     */
    @XmlTransient
    public static long userCount;
    /**
     * A list of all current users. Will not be marshalled.<br>
     * Do not use add and remove methods directly on this list. <br>
     * Use instead {@link User#addUserToUserList(org.fhnw.aigs.server.gameHandling.User)} and 
     * {@link User#removeUserFromUserList(org.fhnw.aigs.server.gameHandling.User)}.
     */
    @XmlTransient
    public static UserList users;
    /**
     * The user's unique ID.
     */
    private long id;
    /**
     * The user's name.
     */
    private String userName;
    /**
     * The user's "password" or identification code.
     */
    private String identificationCode;
    /**
     * A flag that indicates whether or not a user is online.
     */
    private boolean loggedIn;
    
    /**
     * Indicates whether the user is a regular (known) user or an anonymos (adHoc created) user.<br>Will not be marshalled because adHoc users are not stored<br>
     * See also {@link User#writeUsersToXml()} about the handling of non-persistent users
     * @since v1.1
     */
    @XmlTransient
    private boolean anonymousUser;
    
   /**
     * Indicates whether the user is a copy of an already logged in user.<br>Will not be marshalled because doppelgangers are not stored<br>
     * See also {@link User#writeUsersToXml()} about the handling of non-persistent users
     * @since v1.1
     */
    @XmlTransient    
    private boolean doppelganger;
    
    /**
     * Indicates whether the user is a AI. This parameter is <b>optional</b>. Use it if you want to show an AI user in the server GUI.<br>Will not be marshalled because AIs are not stored<br>
     * See also {@link User#writeUsersToXml()} about the handling of non-persistent users
     * @since v1.1
     */
    @XmlTransient       
    private boolean aiUser;

    /**
     * See {@link User#id}.
     */
    @XmlElement(name = "Id")
    public long getId() {
        return id;
    }

    /**
     * See {@link User#userName}.
     */
    @XmlElement(name = "UserName")
    public String getUserName() {
        return userName;
    }

    /**
     * See {@link User#identificationCode}.
     */
    @XmlElement(name = "IdentificationCode")
    public String getIdentificationCode() {
        return identificationCode;
    }
    
    /**
     * See {@link User#loggedIn}.
     */
    @XmlTransient
    public boolean isLoggedIn() {
        return loggedIn;
    }
    
    /**
     * See {@link User#anonymousUser}.
     */
    @XmlTransient    
    public boolean isAnonymousUser(){
        return anonymousUser;
    }

     /**
     * See {@link User#doppelganger}.
     */
    @XmlTransient    
    public boolean isDoppelganger(){
        return doppelganger;
    }   
    
     /**
     * See {@link User#aiUser}.
     */
    @XmlTransient    
    public boolean isAI(){
        return aiUser;
    }    
        
    
    /**
     * See {@link User#id}.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * See {@link User#userName}.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * See {@link User#identificationCode}.
     */
    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    /**
     * See {@link User#loggedIn}.
     */
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
  
    /**
     * See {@link User#anonymousUser}.
     */
    public void setAnonymousUser(boolean isAnonymousUser) {
        this.anonymousUser = isAnonymousUser;
    }
    
     /**
     * See {@link User#doppelganger}.
     */
    public void setDoppelganger(boolean isDoppelganger) {
        this.doppelganger = isDoppelganger;
    }    
    
     /**
     * See {@link User#aiUser}.
     */
    public void setAI(boolean isAI) {
        this.aiUser = isAI;
    }      
    
    /**
     * Empty, zero-argument constructor.
     */
    public User() {
    }

    /**
     * Creates a new instance of User with a given user.
     *
     * @param userName The user's name.
     * @param password The user's password/identification code.
     */
    public User(String userName, String password) {
        this.id = getNextID();//userCount++;
        this.userName = userName;
        this.identificationCode = password;
    }  
    
    /**
     * Calculates the next free user ID from the static user list
     * @since v1.1
     * @return Next free user ID
     */
    private static long getNextID()
    {
        User.userCount = User.users.size();
        long id = -1;
        for (int i = 0; i < User.userCount; i++)
        {
            if (User.users.get(i).getId() > id)
            {
                id = User.users.get(i).getId();
            }
        }
        id++;
        return id;
    }    
    
    /**
     * Gets a user by his or her name.
     *
     * @param userName The user's name.
     * @return The user with the specified name or null if there is none.
     */
    public static User getUserByName(String userName) {
        for (User user : users) {
            if (user.userName.equals(userName)) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Adds a new user to the static user list
     * @param user User to add
     */
    public static void addUserToUserList(User user)
    {
        users.add(user);
        if (ServerConfiguration.getInstance().getIsAnonymousLoginAllowed() == true)
        {
            if (user.isNonPersistentUser() == true)
            {
                ServerGUI.getInstance().addUserToList(user); // Add only non-persistent users
            }
        }
        else
        {
            ServerGUI.getInstance().addUserToList(user); // Add all users
        }
        
        userCount = users.size();
    }
    
    /**
     * Removes a User from the static user list
     * @param user User to remove
     * @since v1.1
     */
    public static void removeUserFromUserList(User user)
    {
        try
        {
        ServerGUI.getInstance().removeUserFromList(user);
        users.remove(user);
        userCount = users.size();                                                          // Update user Count 
        }
        catch(Exception e)
        {
            // No action. User is somply not in list --> Go ahead
        }
    }
    
    /**
     * Removes all non persistent users (anonymous/AdHoc and doppelganger) from
     * the static user list
     * @since v1.1
     */
    public static void removeAllNonPersistentUsers()
    {
        for(int i = users.size() - 1; i >= 0; i--)                              // Inverse is better to delete the list from the end
        {
            if (users.get(i).isNonPersistentUser() == true)
            {
                removeUserFromUserList(users.get(i));
                users.remove(i);
            }
        }
        if (users.size() == 0)
        {
            User.userCount = 0;                                                 // Correction if <0
        }
    }
    
    /**
     * Method checks whether the user is non persistent:<br>
     * <ul>
     * <li>Anonymous user (AdHoc)</li>
     * <li>Doppelganger</li>
     * <li>AI User</li>
     * </ul>
     * @return True if the user is non-persistent, otherwise false 
     */
    public boolean isNonPersistentUser()
    {
        if (this.anonymousUser == true || this.doppelganger == true || this.isAI() == true)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    

    /**
     * Generates a password or identification code based on the specified
     * length. It consists of alphebetic values (a-Z).
     *
     * @param length The length of the password
     * @return A random password.
     */
    private static String generateRandomPassword(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            // Get random number boolean and random number from 0 to 25.
            // If the boolean is true, the letter will be a capital letter.
            SecureRandom random = new SecureRandom();
            boolean isCapital = random.nextBoolean();

            // Get a CharacterCode (char) based on the reandom number and
            // capitalize it, if the random boolean is true.
            // Then append the character to the password.
            int randomCharacterCode = random.nextInt(26);
            char character = (char) ('a' + (char) randomCharacterCode);
            if (isCapital) {
                character = Character.toUpperCase(character);
            }
            stringBuilder.append(character);
        }
        return stringBuilder.toString();
    }

    /**
     * Reads the file "conf/users.txt" and generates a new usersXml file. The
     * document should be plaintext. Every line is equals to one user.
     *
     * @throws IOException Thrown when the file could not be read.
     * @throws JAXBException Thrown when the users could not be serialized.
     * @deprecated Do not use thes method anymore. Use XML serialization ({@link User#readUsersFromXml()}) instead
     */
    private static void readUsersFromPlainText() throws IOException, JAXBException {
        File userFile = new File("conf/users.txt");
        //LOG//Logger.getLogger(User.class.getName()).log(Level.INFO, "Generating users from text file...");
        LogRouter.log(User.class.getName(), LoggingLevel.info, "Generating users from text file...");

        // Reads the file.
        InputStreamReader inputStreamReader = new FileReader(userFile);
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String input = "";
            users = new User.UserList();

            // Read file line by line
            while ((input = reader.readLine()) != null) {
                if (input.equals("")) {
                    continue;                          // Skip empty lines
                }

                // Use the current line as name and generate a new password
                String name = input;
                String identificationCode = generateRandomPassword(6);

                // Create a new user based on the name and password/identification code
                User user = new User(name, identificationCode);
                User.addUserToUserList(user);
                //LOG//Logger.getLogger(User.class.getName()).log(Level.INFO, "New user: {0}", name);
                LogRouter.log(User.class.getName(), LoggingLevel.info, "New user: {0}", name);
            }
        }
        // Save the file to "conf/usersXml.xml".
        File outputFile = new File("conf/usersXml.xml");
        JAXB.marshal(users, outputFile);
        //LOG//Logger.getLogger(User.class.getName()).log(Level.INFO, "Successfully created {0} users and saved to config file.", users.size());
        LogRouter.log(User.class.getName(), LoggingLevel.info, "Successfully created {0} users and saved to config file.", users.size());
    }
    
    /**
     * Handles the user login. If anonymous login is allowed, an Ad-hoc user will be created. If multiple logins are allowed, a doppelganger will be created.
     * @param userName The user (login) name to be checked.
     * @param playerName The users' displayed name
     * @param password The user's password/identification code.
     * @param isMultiLoginAllowed Flag that indicates whether the configuration
     * @return A IdentificationResponseMessage which is sent back to the client.
     * @since v1.1
     */
    public static IdentificationResponseMessage identify(String userName, String password, String playerName, boolean isMultiLoginAllowed)
    {
        IdentificationResponseMessage loginMessage;
       if (ServerConfiguration.getInstance().getIsAnonymousLoginAllowed() == true) // No password or Username required
       {
           User adHoc = null;
           String tempUsername = userName;
           if (tempUsername.length() == 0)
           {
               tempUsername = "Player";
           }
           String tempPassword = password;
           while (true) // Check until a valid user is determined
           {
                loginMessage = User.checkCredentials(tempUsername, tempPassword, isMultiLoginAllowed);
                if (loginMessage.getLoginSuccessful() == false) // A user with these credentials already exists -> choose other name and password
                {
                    tempUsername = getUnusedUsername(tempUsername);
                    tempPassword = User.generateRandomPassword(8);
                    adHoc = new User(tempUsername, tempPassword);
                    adHoc.setAnonymousUser(true);
                    //User.users.add(adHoc);
                    User.addUserToUserList(adHoc);
                    continue; 
                }
                else
                {
                  break;
                }
           }
           loginMessage.setPassword(tempPassword);
       }
       else
       {
           loginMessage = User.checkCredentials(userName, password, isMultiLoginAllowed);
           loginMessage.setPassword(password);
       }
       loginMessage.setPlayerName(playerName);
       return loginMessage;
    }
    
    /**
     * Returns an unused user name. This method is importatnt for anonymous login (Ad-hoc users)
     * @param template The initial username to check
     * @return A currently not used user name
     * @since v1.1
     */
    private static String getUnusedUsername(String template)
    {
        int counter = 1;
        boolean match = false;
        String userName = template;
        while(true)
        {
           match = false;
           for(User user : User.users)
            {
                if (user.getUserName().equals(userName) == true)
                {
                    match = true;
                    counter++;
                    userName = template + Integer.toString(counter);
                    break;
                }
            }
           if (match == false)
           {
               break;
           }
        }
        return userName;
    }

    /**
     * Checks whether a login attempt was successful of not and generates a
     * IdentificiationResponseMessage based on the result.
     *
     * @param userName The user name to be checked.
     * @param identificationCode The user's password/identification code.
     * @param isMultiLoginAllowed Flag that indicates whether the configuration
     * option {@link ServerConfiguration#isMultiLoginAllowed} is set to true or
     * the server is running on localhost.
     * @return A IdentificationResponseMessage which is sent back to the client.
     * @since v1.1 [was originally method identify(...)]
     */
    private static IdentificationResponseMessage checkCredentials(String userName, String identificationCode, boolean isMultiLoginAllowed) {
        IdentificationResponseMessage identificationResponseMessage = new IdentificationResponseMessage();

        // Iterate through all users
        for (User user : User.users) {
            // Check for correct user name and password. If the name and the
            // identification code do not match, set the respective flags 
            // in the IdentificationResponseMessage
            if (user.getUserName().equals(userName) == false || user.getIdentificationCode().equals(identificationCode) == false) {
                identificationResponseMessage.setLoginSuccessful(false);
                identificationResponseMessage.setReason("User name or password is incorrect.");
                // Check if the user is not logged in. If so, the login attempt
                // is successful.
            } else {
                if (user.isLoggedIn() == false) {
                    user.setLoggedIn(true);
                    identificationResponseMessage.setLoginName(user.getUserName());
                    identificationResponseMessage.setLoginSuccessful(true);
                    identificationResponseMessage.setReason("Login successful.");
                    break;

                    // If the user is already logged in, check whether multi login
                    // is allowed. If so, generate a "doppelganger", a copy of the
                    // current user with a number added to the name.
                } else {
                    if (isMultiLoginAllowed) {
                        User newUser = User.generateDoppelganger(user);
                        newUser.setDoppelganger(true);
                        identificationResponseMessage.setLoginName(newUser.getUserName());
                        identificationResponseMessage.setLoginSuccessful(true);
                        identificationResponseMessage.setReason("Created doppelganger. Login successful.");
                        User.addUserToUserList(newUser);
                        break;

                        // If the user is already logged in and multi login is not
                        // allowed, set the flags accordingly.
                    } else {
                        user.setLoggedIn(true);
                        identificationResponseMessage.setLoginName(user.getUserName());
                        identificationResponseMessage.setLoginSuccessful(false);
                        identificationResponseMessage.setReason("User is already logged in.");
                        return identificationResponseMessage;
                    }
                }
            }
        }

        return identificationResponseMessage;

    }

    /**
     * Remove a player from the list of active players/users. The user will not be removed 
     * from the static user list. Use method {@link User#removeUserFromUserList(org.fhnw.aigs.server.gameHandling.User)} 
     * if a user must be removed from the static user list.
     *
     * @param name The name of the player to be logged off.
     */
    public static void logOffUserByName(String name) {
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.getUserName().equals(name)) {
                users.get(i).loggedIn = false;
                //LOG//Logger.getLogger(User.class.getName()).log(Level.INFO, "Marked player " + user.getUserName() + " as being offline.", users.size());
                LogRouter.log(User.class.getName(), LoggingLevel.info, "Marked player " + user.getUserName() + " as being offline.", users.size());
            }
        }
    }

    /**
     * Returns a list of stored users from the file conf/usersXml.xml
     * @return List of registred users
     */
    public static UserList readUsersFromXml() {
        // Load the users config file and check for the existance
        File userConfigFile = new File("conf/usersXml.xml");
        if (userConfigFile.exists()) {
            // Parse the user configuration file's content directly using JAXB
            users = (User.UserList) JAXB.unmarshal(userConfigFile, User.UserList.class);
            //LOG//Logger.getLogger(User.class.getName()).log(Level.INFO, "Read user configuration.");
            LogRouter.log(User.class.getName(), LoggingLevel.system, "Read user configuration.");
        } else {
            //LOG//Logger.getLogger(User.class.getName()).log(Level.INFO, "User configuration file could not be read. Creating an empty user list.");
            LogRouter.log(User.class.getName(), LoggingLevel.system, "User configuration file could not be read. Creating an empty user list.");
            users = new UserList();
        }
        return users;
    }
    
    /**
     * Saves the static user list to the file conf/usersXml.xml
     * @since v1.1
     */
    public static void writeUsersToXml(){
        UserList tempList = new UserList();
        for(int i = 0; i < users.size(); i++)
        {
            if (users.get(i).isNonPersistentUser() == true)
            {
                continue; // Skip non-persistent users
            }
            tempList.add(users.get(i));
        }
        File userConfigFile = new File("conf/usersXml.xml");
        try
        {
            JAXB.marshal(tempList, userConfigFile);
            //LOG//Logger.getLogger(User.class.getName()).log(Level.INFO, "Write user configuration.");
            LogRouter.log(User.class.getName(), LoggingLevel.system, "Write user configuration.");
        }
        catch(Exception ex)
        {
            //LOG//Logger.getLogger(User.class.getName()).log(Level.INFO, "User configuration file could not be written.");
            LogRouter.log(User.class.getName(), LoggingLevel.system, "User configuration file could not be written.");
        }
    }
    

    /**
     * Reads users from a text file and writes it to an xml file.
     *
     * @param args No need for that.
     * @throws IOException
     * @throws JAXBException
     * @deprecated Do not use this method. Use the visual user management tool from she Server GUI instead
     */
    public static void main(String args[]) throws IOException, JAXBException {
        readUsersFromPlainText();
    }    
    
    /**
     * Generates a new user based on another user in the case multi login is
     * allowed.
     *
     * @param loggedInUser The "original" user.
     * @return The "doppelganger".
     */
    public static synchronized User generateDoppelganger(User loggedInUser) {
        // Create a new user with the same identification
        User doppelganger = new User();
        doppelganger.setId(userCount++);
        doppelganger.setIdentificationCode(loggedInUser.getIdentificationCode());

        // Get the old name
        String newName = "";
        String loggedInUserName = loggedInUser.getUserName();
        ArrayList<User> existingDoppelgangers = new ArrayList<>();

        for (User user : users) {
            if (user.getUserName().startsWith(loggedInUserName)) {
                existingDoppelgangers.add(user);
            }
        }
        if (existingDoppelgangers.size() > 0) {
            // The last doppelganger must be the newest one
            User newestDoppelganger = existingDoppelgangers.get(existingDoppelgangers.size() - 1);
            // Check if he or she has a number at the end of the user name
            Pattern p = Pattern.compile("(\\w*\\.\\w*)(\\d)");
            Matcher m = p.matcher(newestDoppelganger.getUserName());
            if (m.find()) {
                String lastNumber = m.group(2);

                // Try to add 1 to the user name.
                int parsedLastNumber = Integer.parseInt(lastNumber) + 1;
                newName = m.group(1) + parsedLastNumber++;
            } else {
                newName = loggedInUserName + "1";
            }
            doppelganger.setUserName(newName);
        }

        return doppelganger;
    }

    /**
     * This construct is storing users - it was created for XML-parsing purposes
     * and is just a wrapper for an ArrayList which is not natively supported by
     * JAXB without modifications.
     * @author Matthias Stöckli
     * @version v1.0
     */
    @XmlRootElement(name = "Users")
    @XmlSeeAlso({User.class})
    public static class UserList extends ArrayList<User> {
        
        /**
         * Standard constructor
         */
        public UserList()
        {
        }

        @XmlElement(name = "User")
        public List<User> getUsers()
        {
            return this;
        }
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getUserName());
        sb.append(" (ID: ");
        sb.append(this.getId());
        sb.append(")");
        if (this.isAnonymousUser() == true)
        {
            sb.append(" [AdHoc User]");
        }
        if (this.isDoppelganger() == true)
        {
            sb.append(" [Doppleganger]");
        }
        if (this.isAI() == true)
        {
            sb.append(" [AI]");
        }
       return sb.toString();
    }
}
