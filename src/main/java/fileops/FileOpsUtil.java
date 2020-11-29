package fileops;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileOpsUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileOpsUtil.class);
    
    public FileOpsUtil() {
        /* empty constructor */
    }

    /**
     * Checks if the file in the parameter exists, if not it creates it.
     *
     * @param path
     * @return
     */

    public static boolean folderExists(File path) {
        boolean bool = false;

        //File fullPathFileName = new File( path + "/" + mediaName);

        try {
            if (!path.isDirectory()) {
                path.mkdirs();
                //fullPathFileName.createNewFile();
                bool = true;
            } else {
                //fullPathFileName.createNewFile();
                bool = true;
            }
        } catch (NullPointerException e) {

            LOGGER.warn("NullPointerError in folderExists");
            //System.out.println(e.getMessage());
            //e.printStackTrace();
            System.exit(0);
        }

        return bool;
    }
}
