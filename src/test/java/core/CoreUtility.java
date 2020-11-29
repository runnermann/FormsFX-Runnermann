package core;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreUtility {

    //private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreUtility.class);

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
    
}
