package fileops;

import flashmonkey.FlashMonkeyMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.draw.shapes.GenericShape;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

//import sun.net.www.content.text.Generic;

/**
 * This class outputs files used for FMShapes into the program, and
 * inputs draw.shapes into files.
 *
 *     @author Lowell Stadelman
 */
public class FileOpsShapes //<T extends GenericShape>
{

    //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FileOpsShapes.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(FileOpsShapes.class);


    /**
     * Saves the draw.shapes array to the file in the parameter
     * @param shapes The array of draw.shapes
     * @param shapeFileName The relitive path to the file.
     */
      public void setShapesInFile(ArrayList<GenericShape> shapes, String shapeFileName) {

        LOGGER.debug("\n\n~^~^~^~^~^~^~^~^~ in shapes setListInFile() ~^~^~^~^~^~^~^~^\n\n");

            String shapePath = DirectoryMgr.getMediaPath('c') + shapeFileName;
        LOGGER.debug("shapePath + fileName: {}", shapePath);
        LOGGER.debug("Shapes size: " + shapes.size());
        File file = new File(shapePath);
        System.out.println("Directory exists: " + DirectoryMgr.getMediaPath('c'));
        //Thread.dumpStack();

        try (ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(shapePath), 512))) {
            LOGGER.debug("Shapes size: " + shapes.size());
        
            for (int i = 0; i < shapes.size(); i++) {
                output.writeObject(shapes.get(i));
            }
            output.flush();
        } catch (EOFException e) {
            LOGGER.warn(e.getMessage());
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            LOGGER.warn(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.warn("Unknown Exception: Trying to save shape file, shapePath: {}", shapePath);
            e.printStackTrace();
        }
    }
    


    /**
     * Returns the shapes list from the currently selected file
     * @return Returns the flashCard list from the currently selected file
     */
    public ArrayList getListFromFile(String shapeFileName) {
        if(shapeFileName == null || shapeFileName.endsWith("null")) {
            return new ArrayList(0);
        }
        String fullPath = DirectoryMgr.getMediaPath('c') + shapeFileName;
        File file = new File(fullPath);
        if(file.exists()) {
            return connectBinaryIn(fullPath);
        }
        return new ArrayList(0);
    }

    /**
     * Provide the fullPathName, returns the ShapesFileArray.
     * @param shapePathName
     * @return
     */
    public ArrayList getListFromPath(String shapePathName) {
        return connectBinaryIn(shapePathName);
    }

    /**
     * Gets binary data from a file.
     * @param shapePathStr
     * @return
     */
    private ArrayList<GenericShape> connectBinaryIn(String shapePathStr) {
        LOGGER.debug("\n\n ~^~^~^~^~^~^~^~ in FileOpsShapes.connectBinaryIn, fileName: " + shapePathStr + " ~^~^~^~^~^~^~^~\n");

        ArrayList<GenericShape> shapes = new ArrayList<>(3);

        try(ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(shapePathStr)))) {

            while(true)
            {
                shapes.add( (GenericShape) input.readObject());
            }
        }
        catch(EOFException e) {
            // do nothing, expected
        }
        catch(FileNotFoundException e) {
            LOGGER.warn("\tFileOpsShapes File Not Found exception: connectBinaryIn() Line 106 ");
            e.printStackTrace();
        }
        catch(IOException e) {
            LOGGER.warn("\t****** IO Exception in FlashCard.connectBinaryIn() *******");
            e.printStackTrace();
        }
        catch(Exception e) {
            // Set errorMsg in FlashMonkeyMain and let the ReadFlash class
            // handle the error in createReadScene()

            LOGGER.warn("\tUnknown Exception in connectBinaryIn: ");
            LOGGER.warn("\tFileName: " + shapePathStr);
            e.printStackTrace();

        }

        return shapes;
    }
}
