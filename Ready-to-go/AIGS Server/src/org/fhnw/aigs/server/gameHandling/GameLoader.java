package org.fhnw.aigs.server.gameHandling;

import org.fhnw.aigs.server.common.Main;
import org.fhnw.aigs.server.common.ServerConfiguration;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.tools.ant.*;
import org.fhnw.aigs.server.common.LogRouter;
import org.fhnw.aigs.server.common.LoggingLevel;

/**
 * This class is responsible is part of the plug-in mechanism. A game logic
 * module (game) consists of two parts: Game commons and game server logic. If a
 * player wants to initialize those games, they must be present in the gamelibs
 * folder in the form of jars. This class provides a mechanism to retrieve those
 * jars and the content inside the archives. It does so by providing an
 * URLClassLoader for every game. When a new game is started loaded, the server
 * will first check whether there is already a URLClassLoader pointing to the
 * jar containing all the game classes. If that is not the case, a new class
 * loader will be created and added to the {@link GameLoader#allClassLoaders}
 * collection.<br>
 * v1.0 Initial release<br>
 * v1.1 Functional changes<br>
 * v1.2 Added new methods<br>
 * v1.3 Changing of logging
 *
 * @author Matthias Stöckli (v1.0)
 * @version 1.3 (Raphael Stoeckli, 24.02.2015)
 */
public class GameLoader extends URLClassLoader {

    /**
     * This field contains a reference to all ClassLoaders, and thus all classes
     * being part of a game. The key is a string, the game's name. The key is
     * provided by the client who requests to start a game. If the key is not
     * identical to the name of the jar (or at least a part of the name), the
     * game cannot be loaded. This is one of the most common mistakes. Therefore
     * it is adviced to ensure that the game is named consistently throught all
     * classes.
     */
    private static HashMap<String, URLClassLoader> allClassLoaders = new HashMap<String, URLClassLoader>();

    /**
     * This method loads a ClassLoader which can then be used to load a game.
     *
     * @param name The name/key to load a game's classes.
     * @return The URLClassLoader containing all classes of the given game.
     */
    public static URLClassLoader getClassLoaderByName(String name) {
        // The mechanism is not case sensitive
        name = name.toLowerCase();

        // Get the class loader, if there is no ClassLoader yet, create a new one.
        if (allClassLoaders.containsKey(name)) {
            return allClassLoaders.get(name);
        } else {
            URLClassLoader newClassLoader = createClassLoaderByName(name);
            allClassLoaders.put(name, newClassLoader);
            return newClassLoader;

        }
    }

    /**
     * Returns a list of all Games in the GameLibs directory
     * @return Arraylist with game names
     * @since v1.1
     */
    public static ArrayList<String> getInstalledGames()
    {
        ArrayList<String> games = new ArrayList<>();
        String gamelibsDirectory = ServerConfiguration.getInstance().getGamelibsDirectory();
        if (GameLoader.checkFolderExists(gamelibsDirectory, true) == false)
        {
            //LOG//Logger.getLogger(GameLoader.class.getName()).log(Level.SEVERE, "The gamelibs folder could not be created. Check the server installation!");
            LogRouter.log(GameLoader.class.getName(), LoggingLevel.severe, "The gamelibs folder could not be created. Check the server installation!");
        }
        File dir = new File(gamelibsDirectory);
        File[] files = dir.listFiles();
        int pos;
        String name, ext;
        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isFile() == true)
            {
                name = files[i].getName();
                pos = name.lastIndexOf(".");
                if (pos > 0)
                {
                    ext = name.substring(pos);
                    name = name.substring(0,pos);
                    if (ext.toLowerCase().equals(".jar")) // Only Jars
                    {
                        if (name.toLowerCase().endsWith("server") || name.toLowerCase().endsWith("commons") || name.toLowerCase().endsWith("sources") || name.toLowerCase().endsWith("client"))
                        {
                            continue; // Skip other libraries (only __GAME__.jar will be listed)
                        }
                        games.add(name);
                    }
                }
            }

        }
        return games;
    }
    
    /**
     * Creastes a new ClassLoader using the game's name as a key.
     *
     * @param name The name/key to load a game's classes.
     * @return A new URLClassLoader containing a reference to all of the game's
     * classes.
     */
    private static URLClassLoader createClassLoaderByName(String name) {
        URLClassLoader newClassLoader;

        // Load all jars in the gamelibs folder.
        // File jarFolder = new File("gamelibs"); // DEPRECATED
        String gamelibsDirectory = ServerConfiguration.getInstance().getGamelibsDirectory();
        if (GameLoader.checkFolderExists(gamelibsDirectory, true) == false)
        {
            //LOG//Logger.getLogger(GameLoader.class.getName()).log(Level.SEVERE, "The gamelibs folder could not be created. Check the server installation!");
            LogRouter.log(GameLoader.class.getName(), LoggingLevel.severe, "The gamelibs folder could not be created. Check the server installation!");
        }
        
        File jarFolder = new File(gamelibsDirectory);
        File[] files = jarFolder.listFiles();

        URL gameJarURL = null;
        URL commonsJarURL = null;
        URL aigsCommonsURL = null;


        /*
         * Go through all files. Then grab those files that end on "Server"
         * or are identical with the provided game's name. This prevents the
         * common mistake that a game logic module's name does not end on Server,
         * e.g. instead of "TicTacToeServer" the name of the module is just 
         * "TicTacToe". Then add these files to a collection. Also load the 
         * AIGS Commons into every collection as the modules can make use of
         * the commons too.
         */
        try {
            for (File file : files) {
                String fileNameLC = file.getName().toLowerCase();
                if (fileNameLC.equals(name + "server.jar") || fileNameLC.equals(name + ".jar"))
                {
                    //gameJarURL = new URL("file:gamelibs/" + file.getName()); // DEPRECATED
                    gameJarURL = new URL("file:" + gamelibsDirectory + "/" + file.getName());
                } else if (fileNameLC.equals(name + "commons.jar"))
                {
                    //commonsJarURL = new URL("file:gamelibs/" + file.getName()); // DEPRECATED
                    commonsJarURL = new URL("file:" + gamelibsDirectory + "/" + file.getName());
                }
            }
            aigsCommonsURL = new URL("file:lib/AIGS_Commons.jar");
        } catch (MalformedURLException ex) {
            //LOG//Logger.getLogger(GameLoader.class.getName()).log(Level.SEVERE, "Could not load jar.", ex);
            LogRouter.log(GameLoader.class.getName(), LoggingLevel.severe, "Could not load jar.", ex);
        }
        catch (Exception ex) // All other exceptions
        {
            //LOG//Logger.getLogger(GameLoader.class.getName()).log(Level.SEVERE, "An unknown error occurred.", ex);
            LogRouter.log(GameLoader.class.getName(), LoggingLevel.severe, "An unknown error occurred.", ex);
        }

        // Create a new URLClassLoader using reflection (newInstance)
        URL[] urls = new URL[]{gameJarURL, commonsJarURL, aigsCommonsURL};
        newClassLoader = URLClassLoader.newInstance(urls, Main.class.getClassLoader());
        Class classLoaderClazz = URLClassLoader.class;
        Method addMethod = null;

        // Use reflection to get the hidden method "addURL" of the URLClassLoader
        // class. Then set the accessibility to true.
        try {
            addMethod = classLoaderClazz.getDeclaredMethod("addURL", new Class[]{URL.class});
            addMethod.setAccessible(true);
        } catch (NoSuchMethodException ex) {
            //LOG//Logger.getLogger(GameLoader.class.getName()).log(Level.SEVERE, "The method 'addURL' does not exist.", ex);
            LogRouter.log(GameLoader.class.getName(), LoggingLevel.severe, "The method 'addURL' does not exist.", ex);
        } catch (SecurityException ex) {
            //LOG//Logger.getLogger(GameLoader.class.getName()).log(Level.SEVERE, "Security was violated.", ex);
            LogRouter.log(GameLoader.class.getName(), LoggingLevel.severe, "Security was violated.", ex);
        }
        catch (Exception ex) // All other exceptions
        {
            //LOG//Logger.getLogger(GameLoader.class.getName()).log(Level.SEVERE, "An unknown error occurred.", ex);
            LogRouter.log(GameLoader.class.getName(), LoggingLevel.severe, "An unknown error occurred.", ex);
        }

        // Add the URLs to the jars to the newly creasted URLClassLoader.
        try {
            addMethod.invoke(newClassLoader, gameJarURL);
            addMethod.invoke(newClassLoader, commonsJarURL);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            //LOG//Logger.getLogger(GameLoader.class.getName()).log(Level.SEVERE, "URL could not be added.", ex);
            LogRouter.log(GameLoader.class.getName(), LoggingLevel.severe, "URL could not be added.", ex);
        }
        catch (Exception ex) // All other exceptions
        {
            //LOG//Logger.getLogger(GameLoader.class.getName()).log(Level.SEVERE, "An unknown error occurred.", ex);
            LogRouter.log(GameLoader.class.getName(), LoggingLevel.severe, "An unknown error occurred.", ex);
        }

        return newClassLoader;
    }

    /**
     * Rebuilds all game jars using the ant build.xml. Also see:
     * http://stackoverflow.com/questions/6733684/run-ant-from-java Basically
     * run the target "rebuildGames" from the build.xml-File.
     * @return Returns true if compilation was successful, otherwise false 
     */
    public static boolean rebuildClasses() {
        boolean state = true;
        File buildFile = new File("build.xml");
        Project p = new Project();

        p.setUserProperty("ant.file", buildFile.getAbsolutePath());
        p.init();
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        p.addReference("ant.projectHelper", helper);
        helper.parse(p, buildFile);
        try {
            p.executeTarget("rebuildGames");
        } catch (BuildException ex) {
            //LOG//Logger.getLogger(GameLoader.class.getName()).log(Level.SEVERE, "Could not load jar.", ex);
            LogRouter.log(GameLoader.class.getName(), LoggingLevel.severe, "Could not load jar.", ex);
            state = false;
        }
        allClassLoaders.clear();
        return state;
    }

    /**
     * Not sure about the need of this method (v1.1)
     * @deprecated 
     * @param urls
     * @param parent 
     */
    private GameLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
    
    /**
     * Checks the existence of a folder
     * @param path The path to be checked
     * @param createEmptyFolder If true, a empty directory will be created, if the folder does not exist
     * @since v1.2
     * @return True, if the folder exists, false if not, respectively if the folder could not be created
     */
    public static boolean checkFolderExists(String path, boolean createEmptyFolder)
    {
        try
        {
            File f = new File(path);
            if (f.exists())
            {
                if (f.isDirectory() == true)
                {
                    return true;
                }
                else
                {
                    //LOG//Logger.getLogger(GameLoader.class.getName()).log(Level.SEVERE, "The defined path '" + path + "' is not a folder");
                    LogRouter.log(GameLoader.class.getName(), LoggingLevel.severe, "The defined path '" + path + "' is not a folder");
                    return false; //
                }
            }
            else
            {
               if (createEmptyFolder == true)
               {
                   f.mkdir();
                   //LOG//Logger.getLogger(GameLoader.class.getName()).log(Level.INFO, "The folder '" + path + "' was created");
                   LogRouter.log(GameLoader.class.getName(), LoggingLevel.info, "The folder '" + path + "' was created");
                   return true;
               }
               else
               {
                   //LOG//Logger.getLogger(GameLoader.class.getName()).log(Level.WARNING, "The folder '" + path + "' does not exist");
                   LogRouter.log(GameLoader.class.getName(), LoggingLevel.waring, "The folder '" + path + "' does not exist");
                   return false;
               }
            }
       
        }
        catch(Exception e)
        {
            //LOG//Logger.getLogger(GameLoader.class.getName()).log(Level.SEVERE, "There is a problem with the path '" + path + "' (does not exist)", e);
            LogRouter.log(GameLoader.class.getName(), LoggingLevel.severe, "There is a problem with the path '" + path + "' (does not exist)", e);
            return false;
        }
    }
    
}
