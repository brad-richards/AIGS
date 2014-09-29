package org.fhnw.aigs.server.gameHandling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import org.fhnw.aigs.commons.communication.IdentificationResponseMessage;

/**
 * This class is responsible for all User related tasks. It loads generates user
 * files, loads users, logs them on and off.
 *
 * @author Matthias Stöckli
 */
@XmlRootElement(name = "User")
public class User {

    /**
     * A number used to generate unique user IDs. Will not be marshalled.
     */
    @XmlTransient
    public static long userCount;
    /**
     * A list of all current users. Will not be marshalled.
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
        this.id = userCount++;
        this.userName = userName;
        this.identificationCode = password;
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
     */
    private static void readUsersFromPlainText() throws IOException, JAXBException {
        File userFile = new File("conf/users.txt");
        Logger.getLogger(User.class.getName()).log(Level.INFO, "Generating users from text file...");

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
                users.add(user);
                Logger.getLogger(User.class.getName()).log(Level.INFO, "New user: {0}", name);
            }
        }
        // Save the file to "conf/usersXml.xml".
        File outputFile = new File("conf/usersXml.xml");
        JAXB.marshal(users, outputFile);
        Logger.getLogger(User.class.getName()).log(Level.INFO, "Successfully created {0} users and saved to config file.", users.size());
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
     */
    public static IdentificationResponseMessage identify(String userName, String identificationCode, boolean isMultiLoginAllowed) {
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
                    identificationResponseMessage.setUserName(user.getUserName());
                    identificationResponseMessage.setLoginSuccessful(true);
                    break;

                    // If the user is already logged in, check whether multi login
                    // is allowed. If so, generate a "doppelganger", a copy of the
                    // current user with a number added to the name.
                } else {
                    if (isMultiLoginAllowed) {
                        User newUser = User.generateDoppelganger(user);
                        identificationResponseMessage.setUserName(newUser.getUserName());
                        identificationResponseMessage.setLoginSuccessful(true);
                        User.users.add(newUser);
                        break;

                        // If the user is already logged in and multi login is not
                        // allowed, set the flags accordingly.
                    } else {
                        user.setLoggedIn(true);
                        identificationResponseMessage.setUserName(user.getUserName());
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
     * Remove a player from the list of active players/users.
     *
     * @param name The name of the player to be logged off.
     */
    public static void logOffUserByName(String name) {
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.getUserName().equals(name)) {
                users.get(i).loggedIn = false;
                Logger.getLogger(User.class.getName()).log(Level.INFO, "Marked player " + user.getUserName() + " as being offline.", users.size());
            }
        }
    }

    public static UserList readUsersFromXml() {
        // Load the users config file and check for the existance
        File userConfigFile = new File("conf/usersXml.xml");
        if (userConfigFile.exists()) {
            // Parse the user configuration file's content directly using JAXB
            users = (User.UserList) JAXB.unmarshal(userConfigFile, User.UserList.class);
            Logger.getLogger(User.class.getName()).log(Level.INFO, "User configuration file read.");
            return users;
        } else {
            Logger.getLogger(User.class.getName()).log(Level.INFO, "User configuration file could not be read.");
        }

        return null;
    }

    /**
     * Reads users from a text file and writes it to an xml file.
     *
     * @param args No need for that.
     * @throws IOException
     * @throws JAXBException
     */
    public static void main(String args[]) throws IOException, JAXBException {
        readUsersFromPlainText();
    }

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
     */
    @XmlRootElement(name = "Users")
    @XmlSeeAlso({User.class})
    public static class UserList extends ArrayList<User> {

        public UserList() {
        }

        @XmlElement(name = "User")
        public List<User> getUsers() {
            return this;
        }
    }
}
