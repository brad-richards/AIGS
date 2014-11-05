package org.fhnw.aigs.server.communication;

import org.fhnw.aigs.commons.communication.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import javax.xml.parsers.*;
import org.fhnw.aigs.commons.*;
import org.fhnw.aigs.server.gameHandling.GameManager;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.fhnw.aigs.server.gameHandling.*;

/**
 * This class is responsible for the message flow. One ServerMessageBroker is
 * assigned to every client connection. The incoming messages will be parsed and
 * analyzed. System messages such as {@link ClientClosedMessage}s will be
 * handled in this class. All other messages will be forwarded to the game
 * assigned to the game connected to the ServerMessageBroker.<br>
 * v1.0   Initial release<br>
 * v1.1   Functional changes<br>
 * v1.1.1 Minor changes (due to changes in other classes)
 *
 * @author Matthias Stöckli (v1.0)
 * @version 1.1.1 (Raphael Stoeckli, 27.10.2014)
 */
public class ServerMessageBroker implements Runnable {

    /**
     * The socket which connects the server to the client.
     */
    private Socket socket;
    /**
     * The BufferedReader used to read the incoming messages.
     */
    private BufferedReader in;
    /**
     * The game to which this connection is connected to.
     */
    private Game game;
    /**
     * The player using this connection.
     */
    private Player player;
    /**
     * A flag that shows whether this game has already been initialized or not.
     */
    private boolean isGameInitialized;
    /**
     * The currently received message in a human readable form.
     */
    private static String currentPrettyPrintedMessage = "";
    /**
     * Indicates whether the currently processed message is a message which is
     * not related to games, e.g. a JoinMessage etc.
     */
    private boolean nonGameMessageReceived = false;
    /**
     * A flag that indicates whether the current connection is still open, this
     * variable can help tracking exceptions.
     */
    private boolean connectionOpen = true;

    /**
     * Initializes the ServerMessageBroker and sets up a {@link Socket}.<br>
     * This class is responsible for parsing, interpreting and passing messages
     * to the game logic (client wise). This class is the counterpart of the
     * {@link org.fhnw.aigs.client.communication.ClientMessageBroker} on the server side.
     *
     * @param socket Socket which connects the client to the server.
     */
    public ServerMessageBroker(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
    }

    /**
     * Starts the message listening loop.
     */
    @Override
    public void run() {
        Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.INFO, "Server ready, waiting for incoming messages...");
        listenForMessages();
    }

    /**
     * This method constantly listens for new inputs coming from the client. The
     * messages are first parsed using {@link ServerMessageBroker#parseInput}
     * Then the parsed messages will be handled. System messages will be handled
     * by {@link ServerMessageBroker#checkForNonGameMessages}. All other
     * messages are then passed to the
     * {@link org.fhnw.aigs.commons.Game#processGameLogic} method.
     */
    private void listenForMessages() {
        String inputString = "";
        try {
            while (connectionOpen == true && (inputString = in.readLine()) != null) {
                Message parsedMessage = parseInput(inputString);

                if (parsedMessage instanceof KeepAliveMessage == false) {
                    printMessage(inputString);
                }
                checkForNonGameMessages(parsedMessage);
                
                // Stop the processing if a non game message was
                // handled or if there is no game or the message is not valid.
                if (nonGameMessageReceived || game == null) {
                    nonGameMessageReceived = false;
                    continue;
                }

                // Check for any kind of exception. In the case of an exception
                // the game will be terminated and the clients will be informed.
                try {
                    game.processGameLogic(parsedMessage, player);
                    game.checkForWinningCondition();
                }
                catch (StackOverflowError stackOverFlow) { // If the game throws an Exception, inform the clients.
                    // Handle StackOverFlowErrors
                    GameManager.terminateGame(game, player, "An error (something really bad) occured.");
                    ForceCloseMessage forceCloseMessage = new ForceCloseMessage("You caused a StackOverflowError (something really bad).");
                    forceCloseMessage.send(socket, player);
                    socket.close();
                    in.close();
                    connectionOpen = false;
                }                
                catch (Exception ex) {
                    ExceptionMessage exceptionMessage = new ExceptionMessage(ex);
                    exceptionMessage.send(socket, player);
                    GameManager.terminateGame(game, player, "An exception occured.");
                    socket.close();
                    in.close();
                    connectionOpen = false;
                    Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.SEVERE, "An exception in the game forced the server to close the following game: " + game.toString(), ex);
                } 
            }
        } catch (IOException ex) {
            GameManager.terminateGame(game, player, "An I/O exception occured.");
            try {
                socket.close();
                in.close();
                connectionOpen = false;
            } catch (IOException ex2) {
                Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.SEVERE, "The connection could not be closed", ex2);
            }
        }
        catch (Exception ex) // All other exceptions
        {
            GameManager.terminateGame(game, player, "An unknown exception occured.");
            try {
                socket.close();
                in.close();
                connectionOpen = false;
            } catch (IOException ex2) {
                Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.SEVERE, "The connection could not be closed", ex2);
            }            
        }
    }

    /**
     * This method parses the incoming messages.<br>
     * It first tries to create a DOMDocument in order to check whether the
     * string is valid xml and to determine the message type. If the message
     * cannot be parsed, the client will receive a {@link BadInputMessage}. If
     * the parsing is successful, the respective message class is loaded via the
     * game's ClassLoader, see {@link GameLoader}.<br>
     * In a last step, the {@link Message#parse} method will take care of the
     * parsing.
     *
     * @param inputString The message as received from the clients.
     * @return The parsed message.
     */
    private Message parseInput(String inputString) {
        // Used to create an input source and for parsing.
        StringReader reader = new StringReader(inputString);
        String messageClassPath = extractMessageClassPath(new InputSource(reader), inputString);

        // Recreate the reader because the reader was read with the last operation
        reader = new StringReader(inputString);

        // If the game has not yet been initialized, use the System class loader
        // to get to the messages in AIGS commons otherwise use the classloader
        // of the current game.
        ClassLoader loader = getClassLoader();

        // Try to load and parse the Message Class dynamically by the loginName provided
        // by the attribute "classPath" which is part of every message.
        try {
            Class messageClass = Class.forName(messageClassPath, true, loader);
            return Message.parse(reader, messageClass);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.SEVERE, "Could not find a matching class. Check the package name, @XmlElement annotations and the jars.", ex);
            return null;
        }
        catch (Exception ex) // All other exceptions
        {
            Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.SEVERE, "An unknown error occurred.", ex);
            return null;        
        }
    }

    /**
     * Uses a {@link org.w3c.dom.Document} to get the attribute "FullyQualifiedClassName"
     * of a message. The value of this attribute is inherent to every message.
     * By extracting it, messages can be instantiated by using reflection.
     *
     * @param inputSource The incoming message as an InputSource
     * @param inputString The incoming message as an InputString (for exception
     * handling purposes only)
     * @return The value of the attribute "FullyQualifiedClassName".
     */
    private String extractMessageClassPath(InputSource inputSource, String inputString) {
        //  Using a DOMDocument we can extract an attribute.
        Document DOMDocument;
        try {
            DOMDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource);
            // Read the attribute "classPath" of the sent XML. It represents the qualified loginName of the class
            // e.g. "org.fhnw.aigs.commons.TicTacToe.FieldClickMessage" which points to the class "FieldClickMessage".
            return DOMDocument.getDocumentElement().getAttribute("FullyQualifiedClassName");

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.SEVERE, "Could not parse input into xml format." + "Client sent the following string: \n{0}", inputString);
            BadInputMessage badInputMessage = new BadInputMessage(inputString);
            badInputMessage.send(socket, player);
            return null;
        }
        catch (Exception ex) // All other Exceptions
        {
            Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.SEVERE, "An unknonw error occurred.", ex);
            BadInputMessage badInputMessage = new BadInputMessage(inputString);
            badInputMessage.send(socket, player);
            return null;            
        }
    }

    /**
     * Gets the {@link ClassLoader} of a game.
     *
     * @return Returns the ClassLoader of a game if there is one, otherwise the
     * SystemClassLoader is returned.
     */
    private ClassLoader getClassLoader() {
        if (isGameInitialized) {
            return GameLoader.getClassLoaderByName(game.getGameName());
        } else {
            return ClassLoader.getSystemClassLoader();
        }
    }

    /**
     * Checks the incoming messages for the following system messages:<br><ul>
     * <li>{@link IdentificationMessage}</li>
     * <li>{@link ClientClosedMessage}</li>
     * <li>{@link JoinMessage}</li>
     * <li>{@link KeepAliveMessage}</li>
     * </ul>
     * The messages are then handled individually.
     *
     * @param parsedMessage The parsed message.
     */
    private void checkForNonGameMessages(Message parsedMessage) {
        Message m = parsedMessage;
        if (m instanceof IdentificationMessage) {
            handledentificationMessage(parsedMessage);
        } else if (m instanceof ClientClosedMessage) {
            handleClientClosedMessage(parsedMessage);
        } else if (parsedMessage instanceof JoinMessage) {
            handleJoinMessage(parsedMessage);
        } else if (parsedMessage instanceof KeepAliveMessage) {
            handleKeepAliveMessage(parsedMessage);
        }
    }

    /**
     * Checks whether the {@link IdentificationMessage} received from a client
     * is valid. Then do the necessary steps to authentificate the user.
     *
     * @param parsedMessage
     */
    private void handledentificationMessage(Message parsedMessage) {
        IdentificationMessage identificationMessage = (IdentificationMessage) parsedMessage;
        String loginName = identificationMessage.getLoginName();
        String password = identificationMessage.getPassword();
        String playerName = identificationMessage.getPlayerName();

        // Checks whether the server is running as local host or if the option
        // "IsMultiLoginAllowed" is set to true.
        boolean isMultiLoginAllowed;
        String ipAddress = socket.getRemoteSocketAddress().toString();
        if (ipAddress.startsWith("/127.0.0.1") || ServerConfiguration.getInstance().getIsMultiLoginAllowed()) {
            isMultiLoginAllowed = true;
        } else {
            isMultiLoginAllowed = false;
        }

        // Identify the user and create a player based on the message.
        IdentificationResponseMessage identificationResponseMessage = User.identify(loginName, password, playerName, isMultiLoginAllowed);
        this.player = new Player(identificationResponseMessage.getLoginName(), identificationResponseMessage.getPlayerName(), false);
        this.player.setSocket(socket);
        identificationResponseMessage.send(socket, player);
        nonGameMessageReceived = true;        // Was handled
    }

    /**
     * If the client closed the window (or the client was closed out of another
     * reason), this method closes the connection, logs off the user and
     * terminates the games he or she was in.
     *
     * @param parsedMessage The parsed message.
     */
    private void handleClientClosedMessage(Message parsedMessage) {
        ClientClosedMessage clientClosedMessage = (ClientClosedMessage) parsedMessage;
        try {
            socket.close();
            in.close();
            Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.INFO, "Connection successfully closed.");
        } catch (IOException ex) {
            Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.SEVERE, null, ex);
        }
        GameManager.terminateGame(game, player, clientClosedMessage.getReason());
        connectionOpen = true;
        nonGameMessageReceived = true;
    }

    /**
     * Handles a join request. When a user wants to start/initialize a game, he
     * or she will send a JoinMessage. The method will call
     * {@link GameManager#joinGame}.
     *
     * @param parsedMessage The {@link JoinMessage}.
     */
    private void handleJoinMessage(Message parsedMessage) {
        JoinMessage joinMessage = (JoinMessage) parsedMessage;
        // Ignore JoinMessages if there already is a game going on.
        if (isGameInitialized == false) {
            game = GameManager.joinGame(joinMessage, player, joinMessage.getPartyName());
            if (game == null) {
                connectionOpen = false;
            } else {
                isGameInitialized = true;
            }
        }
        nonGameMessageReceived = true;
    }

    /**
     * Handles the client's answers of {@link KeepAliveMessage}s. The response
     * is simply forwarded to the {@link KeepAliveManager}.
     *
     * @param parsedMessage The {@link KeepAliveMessage}.
     */
    private void handleKeepAliveMessage(Message parsedMessage) {
        KeepAliveMessage keepAliveMessage = (KeepAliveMessage) parsedMessage;
        KeepAliveManager.handleResponse(keepAliveMessage);
        nonGameMessageReceived = true;
    }

    /**
     * Shows the received message. If the option IsCompactLoggingEnabled" is set
     * to <b>True</b> in the {@link ServerConfiguration}, the output will be
     * made on one line, otherwise it will be formatted.
     *
     * @param inputString The string to be formatted.
     */
    private void printMessage(String inputString) {
        currentPrettyPrintedMessage = XMLHelper.prettyPrintXml(inputString);

        if (ServerConfiguration.getInstance().getIsCompactLoggingEnabled()) {
            Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.INFO, "<= {0}", inputString);
        } else {
            Logger.getLogger(ServerMessageBroker.class.getName()).log(Level.INFO, "<= \n{0}", currentPrettyPrintedMessage);
        }
    }
}