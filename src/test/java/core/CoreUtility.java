package core;

import authcrypt.UserData;
import fileops.DirectoryMgr;
import fileops.utility.Utility;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CoreUtility {

    //private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreUtility.class);

    private static int delta_X = 0;
    private static int delta_Y = 10;

    /* *** GETTERS SETTERS *** */
    public static int getDeltaX() {
        return delta_X;
    }
    public static void setDeltaX(int x) {
        delta_X = x;
    }

    public static int getDeltaY() {
        return delta_Y;
    }
    public static void setDeltaY(int y) {
        delta_Y = y;
    }

    /**
     * The escapeKey while FxRobot is running
     * @param e
     */
    public void escapeKeyActions(KeyEvent e) {

        if(e.getCode() == KeyCode.ESCAPE) {
            Platform.exit();
            System.exit(0);
        }
    }
    
    public static String getWorkingDirectory()
    {
        String OS = (System.getProperty("os.name")).toUpperCase();
        if(OS.contains("WIN")) {
            
            LOGGER.debug("getting or creating file: " + System.getenv("AppData"));
            
            return System.getenv("AppData");
        } else {
            String home = System.getProperty("user.home");
            return home + "/Library/Application Support";
        }
    }

    public static boolean deleteTestFile(String fileName, String userName) {

        String SYSTEM_DIR = DirectoryMgr.getWorkingDirectory();
        String userNameHash = Utility.getMd5Hex(userName.toLowerCase());

        String dirStr = SYSTEM_DIR + "/FlashMonkeyData/" + userNameHash + "/decks/";
        String deckStr = dirStr + fileName;

        File dir = new File(dirStr);

        File file = new File(deckStr);
        LOGGER.debug("deleting directory: {}", dir);
        if(file.exists())  {
            LOGGER.debug("directory exists? {} ... deleting", true );
            file.delete();
            dir.delete();
            return true;
        }
        return false;
    }
    
}
