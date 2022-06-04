package fileops;

import authcrypt.UserData;
import flashmonkey.FlashCardMM;
import flashmonkey.FlashCardOps;
import flashmonkey.FlashMonkeyMain;
import flashmonkey.ReadFlash;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import metadata.DeckMetaData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.FxNotify;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/***************************************************************************
 * <P><B>Do not use this class. Use FlashCard Ops to interact with
 * the methods of this class. This class is isolated. </B></P>
 *
 * DESCRIPTION: Contains the variables for fileName, and FOLDER; Contains
 * ArrayList listOfFiles. Contians methods to Output to a serilized file
 * from an ArrayList and input to an ArrayList from a serilized file.
 **************************************************************************/
public abstract class FileOperations implements Serializable {

    private static final long serialVersionUID = FlashMonkeyMain.VERSION;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileOperations.class);
    //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(FileOperations.class);
    private static String fileName = "default.dec";
    private static File deckFolder;

    protected FileOperations() { /* no args constructor */ }


    // *** GETTERS ***

    protected final String getFileName() throws IllegalStateException {
        return fileName;
    }

    /**
     * Returns the path to the folder where
     * decks are stored.
     * NOTE: use a forward slash at the beginning of the file name.
     * The JVM does not allow a trailing forward slash from this string.
     * To correct for this BUG, add the slash after this method. ie
     * {@code getDeckFolder() + "/" + getFileName() }
     * @return Returns the folder on the system that contains decks. Does
     * not include the trailing forward-slash. See above.
     */
    protected File getDeckFolder() {
        if(deckFolder == null) {
            deckFolder = new File(DirectoryMgr.SYSTEM_DIR
            + DirectoryMgr.getUserNameHash() + "/decks");
        }
        return deckFolder;
    }

    protected File getUserFolder() {
        return new File(DirectoryMgr.SYSTEM_DIR
        + DirectoryMgr.getUserNameHash());
    }

    protected String getDeckTitle() throws IllegalStateException {
        if(fileName == null) {
            throw new IllegalStateException("The deckTitle cannot be used while the fileName is null."
                    + "\nEnsure the fileName is set before using this method");
        }
        // remove ending
        int start = fileName.indexOf('$') + 1;
        return fileName.substring(start, fileName.length() -4);
    }



    // *** SETTERS ***

    /**
     * Creates a deep copy of the flashCard ArrayList from the previous version.
     * It only copies the question, answer, and answer set.file The answer
     * number and question number are set to the index of each flashCard.
     *
     * @param oFC The original flashCard list
     * @return a copy of the arrayList from the previous version.
     */
    private ArrayList copyFmLastVersion(ArrayList<FlashCardMM> oFC) {
        LOGGER.warn("*** copyFmLastVersion() called Deck may not "
                + "be compatible with this version ***");

        ArrayList<FlashCardMM> copy = new ArrayList<>(oFC.size());
        if (oFC.isEmpty()) {
            LOGGER.warn("in FlashCard.copyFmLastVersion() list is empty ");
        } else {
            int i = 0;
            //for(int i = 0; i < oFC.size(); i++)
            for (FlashCardMM o : oFC) {
                //copy.add(new FlashCardMM(i, o.getQuestionMM().getQText(), o.getAText(), o.getAnswerSet()));
                LOGGER.debug("copy no {}", i);
                i++;
            }
        }
        return copy;
    }

    protected void setFileName(String name) throws IllegalArgumentException {
        System.out.println("Name: " + name);
        if(name.equals("default")) {
            throw new IllegalArgumentException("WARNING: fileName cannot be default");
        } else {
            fileName = name;
        }
    }

    /**
     * Checks the FileName and if the FileName is valid, returns a valid
     * fileName with illegal characters removed.;
     * @param bName
     * @return
     * @throws IllegalStateException
     */
    protected String parseFileName(String bName) throws IllegalArgumentException {
        if(bName.isBlank() || bName == null) {
            throw new IllegalArgumentException("FileName contains only whiteSpace or is null");
        }
        if (bName.length() > 3) {
            String name = FileNaming.buildDeckFileName(bName);
            fileExists(name, getDeckFolder());
            return name;
        }
        return null;
    }


    // OTHER METHODS

    /**
     * Checks if file exists. If not then create it.
     * @param fileName
     * @param folder
     * @return
     */
    public boolean fileExists(String fileName, File folder ) {
        boolean bool = false;
        File fullPathFileName = new File(folder + "/" + fileName);
        LOGGER.info("fileExists() fullPathFileName: {}", fullPathFileName.getPath());
        try {
            if(!folder.isDirectory()) {
                folder.mkdirs();
                fullPathFileName.createNewFile();
                bool = true;
            }
            else if(fullPathFileName.exists()) {
                bool = true;
            }
            else {
                fullPathFileName.createNewFile();
                bool = true;
            }
        }
        catch(NullPointerException e) {
            //System.out.println(e.getMessage());
            LOGGER.warn("\tError no files in directory {}" );
            e.printStackTrace();
            System.exit(0);
        }
        catch(IOException e) {
            //System.out.println("\tProblems creating " + fullPathFileName);
            LOGGER.warn("ERROR {}\n{}",e.getMessage());
            e.printStackTrace();
        }
        return bool;
    }


    /**
     * Inputs from a local binary/serialized file to an ArrayList.
     *
     * @param pathName
     * @return Returns an arraylist of flashcardMM
     */
    //protected ArrayList<FlashCardMM> connectFileIn(String fileName) {
     protected ArrayList<FlashCardMM> getListFmFile(String pathName) {
        // String path = DirectoryMgr.getMediaPath('t') + fileName;
        LOGGER.info("\n\n~~~~  In connectBinaryIn  ~~~~");
        LOGGER.debug("Calling method. ???: " + Thread.currentThread().getStackTrace()[3].getMethodName());

        ArrayList<FlashCardMM> flashListMM = new ArrayList<>(4);

        try (ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(pathName)))) {

            while (flashListMM.add((FlashCardMM) input.readObject())) ;

            LOGGER.info("flashlist size: {}", flashListMM.size());

        } catch (EOFException e) {
            // do nothing, this is expected
        } catch (ClassNotFoundException e) {
            LOGGER.warn("\tClassNotFound Exception: connectBinaryIn() Line 1134 FlashCard");
        } catch (FileNotFoundException e) {
            LOGGER.warn("\tFile Not Found exception: connectBinaryIn() Line 1134 FlashCard");
        } catch (IOException e) {
            LOGGER.warn("\t****** IO Exception in FlashCard.connectBinaryIn() *******");
            e.printStackTrace();

            try (ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(deckFolder + "/" + fileName)))) {
                LOGGER.warn("\t *** Trying to copy fromFmLastVersion *** ");
                flashListMM = copyFmLastVersion((ArrayList<FlashCardMM>) input.readObject());

            } catch (IOException f) {
                System.err.println("\tFM IOException, copyFmLastVersion failed!");
                f.printStackTrace();

            } catch (ClassNotFoundException f) {
                LOGGER.warn("\tClassNotFound Exception line 323 FlashCard");
                //System.out.println(f.getMessage());
                //System.out.println(Arrays.toString(f.getStackTrace()));
            }
        } catch (Exception e) {
            // Set errorMsg in FlashMonkeyMain and let the ReadFlash class
            // handle the error in createReadScene()
            FlashMonkeyMain.setErrorMsg(" --   Oooph my bad!!!   --\n" +
                    "The flash card deck you chose \n\"" + getDeckTitle()
                    + "\"\n will not work with this version of FlashMonkey");

            LOGGER.warn("\tUnknown Exception in connectBinaryIn: ");
            e.printStackTrace();
        }

        return flashListMM;
    }

    /**
     * <P><b>USE</b> when an empty element exists at the end of the list. </P>
     * <p>Outputs any arraylist including FlashList (given in the parameter) to a serialized
     *             file from the arrayList in the parameter.</p>
     * @param arrayList FlashList or other ArrayList
     * @param minus The minus that defines the folder where this
     *             file will be created. If char = '-' then
     *             the last element is subtracted from the list. This should be used
     *             when there is an empty element at the end of the list.
     */
    public void setListinFile(ArrayList arrayList, char minus) {

        File folder = new File(DirectoryMgr.getMediaPath(minus));
        folder.mkdirs();
        //Thread.dumpStack();

        // action
        try (ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(folder + "/" + fileName), 512))) {
            if(minus == '-') {
                for (int i = 0; i < arrayList.size() - 1; i++) {
                    output.writeObject(arrayList.get(i));
                }
            } else {
                for (int i = 0; i < arrayList.size(); i++) {
                    output.writeObject(arrayList.get(i));
                }
            }
        }
        catch(EOFException e) {
            /* do nothing */
        }
        catch(FileNotFoundException e) {
            LOGGER.warn(e.getMessage() + "\n" + e.getStackTrace());
            e.printStackTrace();
        }
        catch(IOException e) {
            LOGGER.warn(e.getMessage() + "\n" + e.getStackTrace());
            e.printStackTrace();
        }
        catch(Exception e ) {
            LOGGER.warn("Unknown Exception: " + "\n" + e.getStackTrace());
            e.printStackTrace();
        }
    }

    /**
     * <p>Outputs an array of Strings to file</p>
     * <p>NOTE: this is saved as a text file. Not as a serializable java object. </p>
     * @param m DeckMetaData containing a decks
     *                metaData. THe file stored is named
     *                from the DeckName and ends with .met
     * @param pathName The path and fileName for this file.
     */
    public void setMetaInFile(DeckMetaData m, String pathName) {
        //   LOGGER.debug("setMetaInFile called");
        //Thread.dumpStack();

        try (DataOutputStream dataOut = new DataOutputStream(new FileOutputStream(pathName))){

            LOGGER.debug("set MetaDataObj in file");
            LOGGER.debug("scoresToArray looks like: {}", scoresToArray(m.getScores()));

            dataOut.writeUTF(Long.toString(m.getLastDate())); //1
            dataOut.writeUTF(m.getCreateDate());    // 2
            dataOut.writeUTF(m.getCreatorEmail());  // 3
            dataOut.writeUTF(m.getDeckFileName());  // 4
            dataOut.writeUTF(m.getDescript());      // 5
            dataOut.writeUTF(m.getNumImg());        // 6
            dataOut.writeUTF(m.getNumVideo());      // 7
            dataOut.writeUTF(m.getNumAudio());      // 8
            dataOut.writeUTF(m.getNumCard());       // 9
            dataOut.writeInt(m.getNumSessions());   // 10
            dataOut.writeUTF(m.getDeckBook());      // 11
            dataOut.writeUTF(m.getDeckClass());     // 12
            dataOut.writeUTF(m.getDeckProf());      // 13
            dataOut.writeUTF(m.getDeckSchool());    // 14
            dataOut.writeUTF(m.getSubj());          // 15
            dataOut.writeUTF(m.getCat());           // 16
            dataOut.writeUTF(m.getLang());          // 17
            dataOut.writeUTF(m.getLastScore() == null ? "0" : m.getLastScore());    // 18
            dataOut.writeUTF(scoresToArray(m.getScores()) == null ? "0/" : scoresToArray(m.getScores()));   // 19
            dataOut.writeUTF(m.getPrice());                         // 20
            dataOut.writeUTF(Boolean.toString(m.isSellDeck()));     // 21
            dataOut.writeUTF(Boolean.toString(m.isShareDistro()));  // 22
        }
        catch(EOFException e) {
            /* do nothing */
        }
        catch(FileNotFoundException e) {
            LOGGER.warn(e.getMessage() + "\n" + e.getStackTrace());
            e.printStackTrace();
        }
        catch(IOException e) {
            LOGGER.warn(e.getMessage() + "\n" + e.getStackTrace());
            e.printStackTrace();
        }
        catch(Exception e ) {
            LOGGER.warn("Unknown Exception: " + "\n" + e.getStackTrace());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to setMetaInFile
     * @param scores
     * @return
     */
    private String scoresToArray(ArrayList<String> scores) {
        StringBuilder scoreStr = new StringBuilder("0/0/0");
        if(scores.size() > 0) {

            for (int i = 0; i < scores.size(); i++) {
                scoreStr.append(scores.get(i) + ",");
            }
        }
        return scoreStr.toString();
    }


    /**
     * Returns a DeckMetaData obj from the file in the Param
     * @param path the full path name of the file to be
     *      *                 retrieved.
     * @throws IllegalStateException if the deckFileName has not been set
     * prior to this method being used.
     */
    public void setMetaObjFromFile(String path) {

        LOGGER.debug("getMetaFromFile called");
        try (DataInputStream dataIn = new DataInputStream(new FileInputStream(path))) {
            DeckMetaData data = DeckMetaData.getInstance();

//            System.out.println("Printout from file: \n" +
//                    "1. setLastDate: " + dataIn.readLong() + "\n" +
//                    "2. setCreateDate " + dataIn.readUTF() + "\n" +
//                    "3. setOrigAuthor " + dataIn.readUTF() + "\n" +
//                    "4. setDeckFileName " + dataIn.readUTF() + "\n" +
//                    "5. setDescript " + dataIn.readUTF() + "\n" +
//                    "6. setNumImg " + dataIn.readInt() + "\n" +
//                    "7. setNumVideo " + dataIn.readInt() + "\n" +
//                    "8. setNumAudio " + dataIn.readInt() + "\n" +
//                    "9. setNumCard " + dataIn.readInt() + "\n" +
//                    "10. setNumSessions " + dataIn.readInt() + "\n" +
//                    "11. setDeckBook " + dataIn.readUTF() + "\n" +
//                    "12. setDeckClass " + dataIn.readUTF() + "\n" +
//                    "13. setDeckProf " + dataIn.readUTF() + "\n" +
//                    "14. setDeckSchool " + dataIn.readUTF() + "\n" +
//                    "15. setSubj " + dataIn.readUTF() + "\n" +
//                    "16. setCat " + dataIn.readUTF() + "\n" +
//                    "17. setLang " + dataIn.readUTF() + "\n" +
//                    "18. setLastScore " + dataIn.readUTF() + "\n" +
//                    "19. setScores " +dataIn.readUTF()  + "\n" +
//                    "20. setPrice " +dataIn.readUTF() +"\n" +
//                    "21. setSellDeck " +dataIn.readUTF() +"\n" +
//                    "22. setShareDist " +dataIn.readUTF());


            data.setLastDate(dataIn.readLong());                            // 1
            data.setCreateDate(dataIn.readUTF());                           // 2
            data.setCreatorEmail(dataIn.readUTF());                         // 3
            data.setDeckFileName(dataIn.readUTF());                         // 4
            data.setDescript(dataIn.readUTF());                             // 5
            data.setNumImg(dataIn.readInt());                               // 6
            data.setNumVideo(dataIn.readInt());                             // 7
            data.setNumAudio(dataIn.readInt());                             // 8
            data.setNumCard(dataIn.readInt());                              // 9
            data.setNumSessions(dataIn.readInt());                          // 10
            data.setDeckBook(dataIn.readUTF());                             // 11
            data.setDeckClass(dataIn.readUTF());                            // 12
            data.setDeckProf(dataIn.readUTF());                             // 13
            data.setDeckSchool(dataIn.readUTF());                           // 14
            data.setSubj(dataIn.readUTF());                                 // 15
            data.setCat(dataIn.readUTF());                                  // 16
            data.setLang(dataIn.readUTF());                                 // 17
            data.setLastScore(dataIn.readUTF());                            // 18
            data.setScores(scoresReadBack(dataIn.readUTF()));               // 19
            data.setPrice(Integer.parseInt(dataIn.readUTF()));              // 20
            data.setSellDeck(Boolean.parseBoolean(dataIn.readUTF()));       // 21
            data.setShareDistro(Boolean.parseBoolean(dataIn.readUTF()));    // 22

        } catch (EOFException e) {
            // end of file exception. Do nothing. this is expected.
        } catch(FileNotFoundException e) {
            LOGGER.warn("\tFile Not Found exception:  getObjFmFile() Line 986 FlashCard");
            LOGGER.warn(e.getMessage());
            e.printStackTrace();
        } catch(IOException e) {
            LOGGER.warn("\t****** IO Exception in FlashCard. getObjFmFile() *******");
            e.printStackTrace();
        } catch(Exception e) {
            LOGGER.warn("\tUnknown Exception in getMetaFromFile: ");
            e.printStackTrace();
        }
    }

    /**
     * Helper method to getMetaFromFile
     * @param str
     * @return an arraylist string containing scores
     */
    private ArrayList<String> scoresReadBack(String str) {
        String[] parts = str.split(",");
        ArrayList<String> scores = new ArrayList<>();
        for(String s : parts) {
            scores.add(s);
        }
        return scores;
    }

    /**
     * Outputs the verify object in the parameter to file
     * @param str reset file
     * @return true if user exists, else false
     */
    public String resetVerifyInFile(String[] str) {
        StringBuilder sb = new StringBuilder(str[0]);
        sb.append("," + str[1]);
        sb.append("," + str[2]);

        boolean bool = verifyHelper(sb.toString(), "verifyData.met", true);
        if (bool) {
            return "Success: New user created";
        }
        return "Failed: User was not created.";
    }

    /**
     * Outputs the verify object in the parameter to file
     * @return If the user does not exist, returns successful. Else
     * it fails.
     */
    public String setVerifyInFile(String[] str) {
        StringBuilder sb = new StringBuilder(str[0]);
        for(int i = 1; i < str.length; i++) {
            sb.append("," + str[i]);
        }

        boolean bool = verifyHelper(sb.toString(), "verifyData.met", false);
        if (bool) {
            return "Success: New user created";
        }
        return "Failed: User was not created.";
    }

    /**
     * Outputs the object in the Param to a file in the filePathName if it does not exist and
     * isReset is false. if isReset is true, the userFile must exist.
     * @param data
     * @param fileName
     * @return true if successful
     */
    private boolean verifyHelper(String data, String fileName, boolean isReset) {

        LOGGER.debug("\nCalled verifyHelper() with outputStream");
        //Thread.dumpStack();

        File folder = new File(DirectoryMgr.getMediaPath('z'));
        createZFolder(folder, isReset);
        boolean bool = false;
//        if(!bool) {
//            return false;
//        }

        //boolean bool = false;
        String fullPathName = folder + "/" + fileName;
        try (DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fullPathName), 512))) {
            LOGGER.debug("\tAttempting to set the encrypted file to disk.");
            bool = true;
            dataOut.writeUTF(data);
        }
        catch(EOFException e) {
            //Expected end of while statement LOGGER.warn(e.getMessage());
            return true;
        }
        catch(FileNotFoundException e) {
            LOGGER.warn(e.getMessage());
            bool = false;
        }
        catch(IOException e) {
            LOGGER.warn("IOException: in connectBinaryOut Saving to file\n{}", e.getMessage());
            bool = false;
            //e.printStackTrace();
        }
        catch(Exception e) {
            LOGGER.warn("Unknown Exception: \nFileName: {} \nCause: {} \nStackTrace: {}", fileName, e.getCause().toString(), Arrays.toString(e.getStackTrace()));
            bool = false;
        }
        finally {
            LOGGER.debug("\tCompleted\n");
            return bool;
        }
    }

    /**
     * used by setDataInFile
     * @param folder
     * @param isReset
     */
    private void createZFolder(File folder, boolean isReset) {
//        if(isReset) {
//            LOGGER.debug("\tdatInFile is a reset");
//            if ( ! folder.exists()) {
//                return false;
//            }
//        }
//        else {
            LOGGER.debug("\tchecking if folder exists, if not I will create it.");
            File userFile = new File(folder.getPath() + "/verifyData.met");
            if (! userFile.exists()) {
                    folder.mkdirs();
            }
//        }
        //LOGGER.debug("\t called setDataInFile(Obj)  and path is: {}/{}", folder,  fileName);
        //return false;
    }

    public String[] getVerifyFmFile() {
        String str = getVerifyHelper("verifyData.met");
        // break into an array
        String[] ary = str.split(",");
        if(ary.length == 3) {
            return ary;
        } else {
            LOGGER.warn("GetVerify failed !!!");
            return new String[0];
        }
    }

    /**
     * Expects all data to be of String. Using writeUTF under the hood. If
     * Non UTF-8 characters are expected, modify this method and use the more
     * inclusive writeChars.
     * @return If successful, returns Data using a DataInputStream from the file, else returns null.
     */
    public String getVerifyHelper(String fileName) {
        File folder = new File(DirectoryMgr.getMediaPath('z'));
        String pathName = folder + "/" + fileName;
        File metFile = new File(pathName);

        if(!metFile.exists() ) {
            LOGGER.warn("folder {} or file {} does not exist", folder.getName(), fileName );
            return null;
        }

        LOGGER.info("n called getObjFmFile() and path is: {}", pathName);

        try (DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(pathName)))) {
            //while (true) {
            return dataIn.readUTF();
            // }
        }catch (EOFException e) {
            // expected, ending file
        } catch(FileNotFoundException e) {
            LOGGER.warn("\tFile Not Found exception");
            LOGGER.warn(e.getMessage());
            e.printStackTrace();

        } catch(IOException e) {
            LOGGER.warn("\t****** IO Exception in FlashCard. getObjFmFile() *******");
            //e.printStackTrace();

        } catch(Exception e) {
            // Set errorMsg in FlashMonkeyMain and let the ReadFlash class
            // handle the error in createReadScene()
            //        FlashMonkeyMain.setErrorMsg(" --   Oooph my bad!!!   --\n" +
            //                "The flash card deck you chose \n\"" + ReadFlash.getDeckName()
            //                + "\"\n will not work with this version of FlashMonkey" );

            LOGGER.warn("\tUnknown Exception in connectBinaryIn: ");
            //e.printStackTrace();

        }
        return null;
    }

    /**
     * @return If successful, returns an object from the file, else returns null.
     */
    public Object getObjectFmFile(String fileName) {
        File folder = new File(DirectoryMgr.getMediaPath('z'));
        String pathName = folder + "/" + fileName;
        File metFile = new File(pathName);

        if(!metFile.exists() ) {
            LOGGER.warn("folder {} or file {} does not exist", folder.getName(), fileName );
            return null;
        }

        LOGGER.info("n called getObjFmFile() and path is: {}", pathName);

        try (ObjectInputStream input1 = new ObjectInputStream(new BufferedInputStream(new FileInputStream(pathName)))) {
            return input1.readObject();

        } catch(FileNotFoundException e) {
            LOGGER.warn("\tFile Not Found exception");
            LOGGER.warn(e.getMessage());
            e.printStackTrace();

        } catch(IOException e) {
            LOGGER.warn("\t****** IO Exception in FlashCard. getObjFmFile() *******");
            //e.printStackTrace();

        } catch(Exception e) {
            // Set errorMsg in FlashMonkeyMain and let the ReadFlash class
            // handle the error in createReadScene()
            //        FlashMonkeyMain.setErrorMsg(" --   Oooph my bad!!!   --\n" +
            //                "The flash card deck you chose \n\"" + ReadFlash.getDeckName()
            //                + "\"\n will not work with this version of FlashMonkey" );

            LOGGER.warn("\tUnknown Exception in connectBinaryIn: ");
            //e.printStackTrace();

        }
        return null;
    }

    /**
     * Saves an image to the desired directory
     * @param imgFileName The fileName
     * @param image the image to save
     * @param mime the file ending for the image. .png or .gif
     * @param type The type of media to save the image. Should be a 'c' 'C' for images in decks
     *             or 't' for the usersImg.
     * @return Returns the fileName
     */
    public boolean saveImage(String imgFileName, Image image, String mime, char type) {
        LOGGER.info("saving image to file, fileName: {}", imgFileName);
        DirectoryMgr dirMgr = new DirectoryMgr();
        final String path = dirMgr.getMediaPath(type);
        final String fullPath = path + imgFileName;
        File mediaFile = new File(path);
        FileOpsUtil.folderExists(mediaFile);

        try {
            return ImageIO.write(SwingFXUtils.fromFXImage(image, null), mime, new File(fullPath));
        } catch(IOException e) {
            LOGGER.warn("ERROR: image was not saved. FileName: {}", fullPath);
        }
        return false;
    }

    private void writeImageHelper(Image image, File outputFile, String mime) throws IOException {
        //File outputFile = new File("C:/JavaFX/");
        LOGGER.debug("writeImageHelper called");
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        ImageIO.write(bImage, mime, outputFile);
    }
}

