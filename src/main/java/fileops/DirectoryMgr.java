package fileops;

import authcrypt.AuthUtility;
import authcrypt.UserData;
import flashmonkey.ReadFlash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.testtypes.FMHashCode;

import java.awt.image.DirectColorModel;
import java.io.File;

/**
 * This class creates and manages media directories
 *
 * @author Lowell Stadelman
 */

public final class DirectoryMgr
{

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryMgr.class);

    //public static final String CANVAS = "/flashMonkeyFile/canvas/";
    //public static final String MEDIA = getWorkingDirectory() + "/flashMonkeyFile/media/";
   // private static String userHash = FileNaming.getCardHash(UserData.getUserName(), "");
    private static final String SYSTEM_DIR = getWorkingDirectory() + "/FlashMonkeyData/";
    public static final String EMOJI = "image/emojis/";


    public DirectoryMgr() { /* no args constructor */}

    private static int getUserNameHash() {
        //LOGGER.debug("DirectoryMgr getUserNameHash() userName:" + UserData.getUserName());
        return UserData.getUserName().hashCode();
    }

    public static boolean flashmonkeyExists() {
        File folder = new File(SYSTEM_DIR);
        return folder.exists();
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

    /**
     * Returns the media path depending on the type of media file
     * @param type Uses a char to set the type of file
     *             Note this differs from the QuestionMM
     *             class
     *             C = Canvas, for Image, Shapes or Both, M = Media (video or audio)
     */
    public static String getMediaPath(char type)
    {
        LOGGER.debug("DirectoryMgr getMediaPath(...) called type: " + type);
        String folderStr = ReadFlash.getInstance().getDeckName();
    
        System.out.println("folderStr: " + folderStr);
        
        if(folderStr.contains("default")) {
            folderStr = "";
        } else {
            folderStr.replace(" ", "");
            folderStr = folderStr.toLowerCase();
            LOGGER.debug("deckName is: {}", folderStr);
        }

        switch (type)
        {
            case 'C':
            case 'D':
            case 'c':
            case 'd':
            case 'M':
            case 'm':
            {
                return SYSTEM_DIR + getUserNameHash() + "/" + folderStr + "/media/";
            }
            case 'z':
            {
                return SYSTEM_DIR + getUserNameHash() + "/" + "userData/";
            }
            case 't':
            case 'T':
            default:
            {
                // Being inconsitent due to older file retrevial in agrFiles. Removed trailing "/"
                if(folderStr == null) {
                    return SYSTEM_DIR + getUserNameHash() + "/" + "decks";
                }
                return SYSTEM_DIR + getUserNameHash() + "/" + "decks/";
            }
        }
    }
}
