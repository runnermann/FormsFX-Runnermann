package fileops;

import authcrypt.UserData;
import fileops.utility.Utility;
import flashmonkey.FlashCardOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * This class creates and manages media directories.
 *
 * It privides the naming for the directories and files used
 * by flashmonkey.
 *
 * @author Lowell Stadelman
 */

public final class DirectoryMgr {

      private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryMgr.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DirectoryMgr.class);

      public static final String SYSTEM_DIR = getWorkingDirectory() + "/FlashMonkeyData/";
      // co.flashmonkey directory
      public static final String CPR_DIR = getWorkingDirectory() + "/.cpr/co/FlashMonkey/";
      // app.topscholar directory
      // public static final String CPR_2_DIR = getWorkingDirectory() + "/.cpr/app/TopScholar/";
      public static final String EMOJI = "image/emojis/";
      public static final String USER_ONE = "/resuone.enc";
      public static final String USER_ZERO = "/resuzero.cpr";
      public static final String LOGOUT = "/.cpr";


      public DirectoryMgr() {
            /* no args constructor */
      }

      /**
       * @return Returns a hex to a safe name from an MD5 hash of the UserName or the users original_email
       */
      public static String getUserNameHash() {
            LOGGER.debug("DirectoryMgr getUserNameHash() userName:" + UserData.getUserName());
            return Utility.getMd5Hex(UserData.getUserName().toLowerCase());
      }

      public static boolean flashmonkeyExists() {
            File folder = new File(SYSTEM_DIR);
            return folder.exists();
      }

      public static boolean resuExists() {
            File file = new File(CPR_DIR + USER_ONE);
            return file.exists();
      }


      public static String getWorkingDirectory() {
            String OS = (System.getProperty("os.name")).toUpperCase();
            if (OS.contains("WIN")) {
                  return System.getenv("AppData");
            } else {
                  //System.out.println("\n\n **** NOT A WINDOWS SYSTEM? ***\n\n");
                  String home = System.getProperty("user.home");
                  return home + "/Library/Application Support";
            }
      }


      private String getPersistentHash(String str) {
            return Utility.getMd5Hex(str);
      }


      /**
       * Returns the media path depending on the type of media file. Does not
       * name the file.
       *
       * @param type <pre>
       *                         Uses a char to set the type of file
       *                         Note this differs from the QuestionMM
       *                         class
       *                         t = text, C = Canvas, for Image, Shapes or Both,
       *                         M = Media (video or audio), q = qr code, d = ????
       *                         z = user and verification data,
       *                         q = qr image
       *                         a = user avatar
       *                         p = public used for a decks marketplace image.
       *                         </pre>
       * @return Returns the path to the directory. Note that there is not a trailing
       * forward slash "/". It would be removed by {@code File()}
       */
      public static String getMediaPath(char type) {
            LOGGER.debug("DirectoryMgr getMediaPath(...) called type: " + type);

            switch (type) {
                  case 'C':
                  case 'D':
                  case 'M':
                  case 'V':
                  case 'c':
                  case 'd':
                  case 'i':
                  case 'm':
                  case 'v': {
                        String folderStr = FlashCardOps.getInstance().getDeckLabelName();
                        if (folderStr.contains("default")) {
                              //folderStr = "";
                              LOGGER.warn("folderString contains default");
                              System.exit(1);
                        } else {
                              //folderStr = folderStr.substring(0, folderStr.length() -4);
                              folderStr = folderStr.strip().toLowerCase();
                              LOGGER.debug("deckName is: {}", folderStr);
                        }
                        return SYSTEM_DIR + getUserNameHash() + "/" + folderStr + "/media/";
                  }
                  case 'a': // user avatar image
                  case 'z': {
                        return SYSTEM_DIR + getUserNameHash() + "/userData/";
                  }
                  case 'q': {
                        return SYSTEM_DIR + getUserNameHash() + "/decks/qr/";
                  }
                  case 'p': {
                        return SYSTEM_DIR + getUserNameHash() + "/decks/image/";
                  }
                  case 't':
                  case 'T':
                  default: {
                        return SYSTEM_DIR + getUserNameHash() + "/decks/";
                  }
            }
      }
}
