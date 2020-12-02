/*
 * AUTHOR: Lowell Stadelman 
 */

package flashmonkey;

import ch.qos.logback.classic.Level;
import fileops.*;
import forms.searchables.CSVUtil;
import flashmonkey.utility.SelectableRdoField;
import fmannotations.FMAnnotations;
import fmtree.FMTWalker;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.io.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
//import java.util.logging.Logger;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import metadata.DeckMetaData;
import org.slf4j.LoggerFactory;
import uicontrols.UIColors;

import javax.imageio.ImageIO;


/*******************************************************************************
 *
 * <p>Explanation: This class was formerly the original FlashCard
 * class that contained all varaibles and methods related
 * to FlashCards and FlashCard operations. To minimize the
 * number of variables that are instantiated and saved
 * FlashCardOps or FlashCard operations remained in this class. It was
 * renamed to FlashCardOps, and FlashCard specific varaibles
 * were moved to a new flashcard class.</p>
 *
 *
 * <p>Not compatible with Version 20170512 and earlier do
 * to flashCard class restructuring. </p>
 *
 * <p>CLASS DESCRIPTION: This is the FlashCard class and is a child to the AnswerMM
 * class. It contains variables and methods for the the FlashCard class.</p>
 * <ul>
 * <li> This class contains methods and variables specific to create a FlashCard. This
 * class may at a future date, be seperated into seperate elements to prevent
 * unneccessary variables from being saved to the flashList and simplify
 * modifications and management. </li>
 *
 * <li>This class outputs flashcards to a binary file, it contains an array that
 * is used to read the flashcards. This is done so that this class may
 * remain server side. The EncryptedUser.EncryptedUser will not have access to the correct answer
 * unless they are using it for study sessions. </li>
 * </ul>
 *
 * @version iOOily FlashMonkeyMM Date: 2018/08/04
 * @author Lowell Stadelman
 * @TODO deserialize files with restrictions. ValidatingObjectInputStream in = new ValidatingObjectInputStream(fileInput); in.accept(Foo.class);
 ******************************************************************************/

public class FlashCardOps//< T extends Comparable<T>> extends FlashCardMM<T> implements Comparable<T>
{
    /* SINGLETON */
    //private volatile static FlashCardOps CLASS_INSTANCE;
    private static FlashCardOps CLASS_INSTANCE;
    
    // Logging reporting level is set in src/main/resources/logback.xml
    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FlashCardOps.class);


    private static final long serialVersionUID = FlashMonkeyMain.VERSION;
    // count, To keep track of when flashCards are added to an array.
    // Prevents unneccessary communications with
    // cloud servers.
    private int count;
    private static File DECK_FOLDER = new File(DirectoryMgr.getMediaPath('t'));
    private static String fileName = "default";
    private static final FMTWalker TREE_WALKER = FMTWalker.getInstance();


    //*** OBJECTS ***
    protected final FileOperations FO = new FileOperations();
    public final CloudOps CO = new CloudOps();

    //** The current threshold from the last time the tree was built.
    private static Threshold thresh;
    // if null call resetAgrList
    private static AgrFileList agrList;// = new AgrFileList(DECK_FOLDER);

    // --- FLAGS ---
    // File synchronization flag. If media files have been synchronized, don't do it again unless
    // there are changes in CreateFlash.
    private static boolean mediaIsSynced;

            

    /**
     * Stores the flashcards that are created from the binary file,
     * that are added at the head of the list. And that are
     * stored back to the file.
     */
    private static ArrayList<FlashCardMM> flashListMM = new ArrayList<>();


    private FlashCardOps() {
        //LOGGER.setLevel(Level.DEBUG);
    }
    
    
    public static synchronized FlashCardOps getInstance() {
        if(CLASS_INSTANCE == null) {
            CLASS_INSTANCE = new FlashCardOps();
        }
        return CLASS_INSTANCE;
    }


    // ************************************** ******* **************************************
    // ************************************** SETTERS **************************************
    // ************************************** ******* **************************************
    
    
    /**
     * Sets the flashList to the parameter
     * @param flashList
     */
    public void setFlashList(ArrayList<FlashCardMM> flashList) {
        this.flashListMM = flashList;
    }

    /**
     * Passes the password and name through to CloudOps to be consumed
     * during the getDeckList request. Called by SignInModel at user
     * initial signIn.
     * @param name
     * @param pw
     */
    public void setObjsFmS3(String name, String pw) { CO.setS3DeckList(name, pw); }

    public void setMediaIsSynced(boolean bool) {
        mediaIsSynced = bool;
    }



    // ************************************** ******* **************************************
    // ************************************** GETTERS **************************************
    // ************************************** ******* **************************************

    public static boolean getMediaIsSynched() {
        return mediaIsSynced;
    }
    
    /**
     * Returns a list of the media contained in the current flashList;
     * Used by MediaSync.mediaSync().
     * @return
     */
    @SuppressWarnings("unchecked")
    public ArrayList<MediaSyncObj> getFlashListMedia() {
        ArrayList<MediaSyncObj> mediaSyncObjs = new ArrayList<>(flashListMM.size());
        String name;
    
        LOGGER.info("in getFlashMedia. flashListMM length: {}",  flashListMM.size());

        
        for(FlashCardMM fc : flashListMM) {
            // Question
            for(int i = 0; i < fc.getQFiles().length; i++) {
                if(fc.getQFiles()[i] != null) {
                    name = fc.getQFiles()[i];
                    if (name != null || !name.equals("")) {
                        mediaSyncObjs.add(new MediaSyncObj(name, fc.getQType(), 1));
                    }
                }
            }
            // Answer
            for(int i = 0; i < fc.getAFiles().length; i++) {
                if(fc.getAFiles()[i] != null) {
                    name = fc.getAFiles()[i];
                    if (name != null || !name.equals("")) {
                        mediaSyncObjs.add(new MediaSyncObj(name, fc.getAType(), 1));
                    }
                }
            }
        }
        
        return mediaSyncObjs;
    }
    
    public FileOperations getFO() {
        return this.FO;
    }


    /**
     * Returns the current threshold determined when the tree was last built.
     */
    protected Threshold getThresh()
    {
        return this.thresh;
    }
    

    /**
     * Returns the size of the AgrList, for use in main
     * @return 
     */
    public int getAgrListSize()
    {
        return agrList.getSize();
    }
    
    public void resetAgrList() {
        //if(cloudLinks == null || cloudLinks.isEmpty()) { LOGGER.warn("cloudLinks is empty or null "); }
            agrList = new AgrFileList(DECK_FOLDER);
    }
    
    public File getDeckFolder() {
        return DECK_FOLDER;
    }

    
    /**
     * getFlashList
     * @return ArrayList of FlashCards
     */
    public final ArrayList<FlashCardMM> getFlashList() {
        try {
            RemoveThisTester.testFlashListObject(flashListMM);
        } catch (Exception e) {
            LOGGER.warn("ERROR: line 211: flashlists are not the same\n{}", (Object) e.getStackTrace());
            System.exit(0);
        }
        
        return flashListMM;
    }
    

    /**
     * getElement() 
     * @param i
     * @return returns a new FlashCardMM object cloned from the ArrayList
     */
    public FlashCardMM getElement(int i) {
        try {
            return new FlashCardMM(flashListMM.get(i));
        }
        catch(IndexOutOfBoundsException e) {
            return null;
        }                
    }
    
    /**
     * Returns the tree_walker
     * @return
     */
    public FMTWalker getTreeWalker() {
        return TREE_WALKER;
    }
    
    
    
    
    // ************************************** ******* **************************************
    // ************************************** OTHERS  **************************************
    // ************************************** ******* **************************************
    
    

    
    /**
     * copyList() Creates a deep copy of the ArrayList.
     * @param original
     * @return a copy of the FlashList of type {@link FlashCardMM} in the
     * argument
     */
    public ArrayList cloneList(ArrayList<FlashCardMM> original) {
        LOGGER.info("---&&&--- cloneList called ---&&&---");
        ArrayList<FlashCardMM> cloneList = new ArrayList<>(original.size());
        original.forEach((element) -> {
            FlashCardMM fcClone = element.clone();
            cloneList.add(fcClone);
        });
        
        return cloneList;
    }
    
    
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
            LOGGER.warn("\tError no files in directory ");
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
     * Resets the flashList to 0 elements then
     * gets the flashList from the local file.
     */
    public void refreshFlashList()
    {
        LOGGER.info("\n\n\t---^^^--- REFRESHING FLASHLIST  ---^^^---\n" +
        " pulling it from saved file\n\n");
        //LOGGER.debug("Calling method. ???: " + Thread.currentThread().getStackTrace()[3].getMethodName());
        LOGGER.debug("DeckName: {}", ReadFlash.getInstance().getDeckName());
        clearFlashList();
        flashListMM = FO.getListFromFile();

        // Testing flashList objects
        //RemoveThisTester.setFlashListObject(flashListMM);

    }

    /**
     * This method clears and rebuilds the tree. It does not
     * reset highest and lowest child referances.
     */
    public void refreshTreeWalker()
    {
        LOGGER.debug("*** FlashCardOps.refreshTreeWalker() ***");

        TREE_WALKER.clear();
        RemoveThisTester.setTreeWalkerObj(TREE_WALKER);

        buildTree();
    }

    /**
     * Clears andrebuilds the tree to the list provided in the paramter.
     * Does not reset the highest and lowest child referances.
     * @param cardList
     */
    public void refreshTreeWalker(ArrayList<FlashCardMM> cardList) {
        TREE_WALKER.clear();
        RemoveThisTester.setTreeWalkerObj(TREE_WALKER);

        buildTree(cardList);
    }
    
    /**
     * Clears the current card deck. Used when a new card deck should be created
     * from list of flashCards existing in the selected file. Does not recreate
     * the flashList from the file.
     */
    public void clearFlashList() {
    	flashListMM.clear();
    }

    /**
     * Saves the current flashList to file and the cloud (if connected). Used by
     * ReadFlash.
     */
    public void saveFlashList() {
        LOGGER.debug("*!*!* FlashOps saveFlashlist *!*!*");

        // keep the last element
        FO.setListinFile(flashListMM, '+');
        // @TODO change method name, saveFListToFile, to reflect saving to cloud as well
        if(Utility.isConnected()) {
           // CloudOps co = new CloudOps();
            CO.putDeck(ReadFlash.getInstance().getDeckName() + ".dat");
            //CO.connectCloudOut('t', authcrypt.UserData.getUserName(), ReadFlash.getInstance().getDeckName() + ".dat");
        }
        try {
            RemoveThisTester.testFlashListObject(flashListMM);
        } catch (Exception e) {
            LOGGER.warn("ERROR: line 291: flashlists are not the same\n{}", (Object) e.getStackTrace());
            System.exit(0);
        }
    }

    /**
     * <p>Adds a flashCard to the tree according to the cards qNumber.</p>
     *
     * <p>Note!!! The saved file numSeen is different than the treeCard's numSeen.
     * Both are incremented, however the treeCard's numSeen is not saved and is
     * zeroized for each session so that it's priority can be adjusted according
     * to number of times they have been answered this session.</p>
     *
     * <p>Used during test sessions for prioritized cards</p>
    */
    private void buildTree()
    {   
        LOGGER.info(" ---- START BUILDTREE ---- ");

        LOGGER.debug("\t number elements in flashList: {}", flashListMM.size());
        LOGGER.debug("\t is tree empty? should be: {}", TREE_WALKER.isEmpty());

        // set the threshold, and set the time last seen to before or after the first card.
        thresh = new Threshold(flashListMM);
        ZonedDateTime lastDate;
        if(flashListMM.size() > 0) {
            lastDate = flashListMM.get(0).getRtDate();
            lastDate = lastDate.minusSeconds(20);
        } else {
            ZoneId zID = ZoneId.of("America/Los_Angeles");
            lastDate = ZonedDateTime.now(zID);
        }

        // Build the tree.
        for(int i = 0; i < flashListMM.size(); i++) {
            FlashCardMM localCard = flashListMM.get(i);

            // Dont show cards that have a high enough correct rate.
            // If the flashcard is below the threshold, add the card to the tree
            if(! thresh.boolHide(localCard))
            {
                // ANumber is the index number in flashList. It is used
                // to modify a flashCard in flashList.
                localCard.setANumber(i);

                // A flashCards priority is based on several factors that modify
                // it's placing in the tree. A flashCard's QNumber is modified
                // based off of it's priority.
                localCard = setPriority(localCard, i, lastDate);

                // set isRight to 0/unanswered for this session
                localCard.setIsRight(0);
                // set localCard numSeen to 0
                localCard.setNumSeen(0);

                // Add the card to the tree. The tree will use the comparitor
                // to set the cards location in the tree based on it's QNumber.
                TREE_WALKER.add(localCard);
            }
        }
    }
    
    /**
     * <p>Builds the AVLTree Navigation data Structure that orders the cards in the deck.
     * A Cards aNum or answer number is set to that cards index in the FlashList.
     * Values for isRight and numSeen are reset to "0". Also set it the cards priority
     * which provides it's rank/order within the tree.
     * Used in CreateFlash.</p>
     * <p>Used by creator</p>
     * @param cardList
     * @TODO check with older version, order is not correct!
     */
    protected void buildTree(ArrayList<FlashCardMM> cardList) {

        LOGGER.debug("in FlashCardOps.buildTree(ArrayList... list.size:{}", cardList.size());

        ZonedDateTime lastDate;
        lastDate = cardList.get(0).getRtDate();
        lastDate = lastDate.minusSeconds(20);
        
        int count = 0;
        for(FlashCardMM cd : cardList) {
            cd.setANumber(count++);
            cd = setPriority(cd, count, lastDate);
            cd.setIsRight(0);
            cd.setNumSeen(0);
            TREE_WALKER.add(cd);
        }
    }

    /**
     * Prioritizes a card by setting it's qNumber. A card is prioritized according to it's last seen date.
     * Was it seen before the 1st card or after it. -The remember value is also a bias to assist
     * with setting the priority. Remember is set during the EncryptedUser.EncryptedUser session or manually set by the EncryptedUser.EncryptedUser in
     * the EditFlashCard UI. Important notes during build tree:
     *     * - Priority(remember) if it's greater than the threshold it is automatically hidden.
     *     * - When added to the tree, a card number is multiplied by 10 leaving space for
     *     *    cards to be inserted for other non-permanent reasons.
     *     *    ie If the card was answered incorrectly during this session, the card is given a number that is
     *     *    not a multiple of 10.
     *              - Handled by a seperate method.
     *
     *     Cards are ordered
     *     Worst performance
     *     unseen
     *     correctly answered
     *
     *     Cards that have been correctly answered more than meets the threshold are hidden.
     */
    private final static FlashCardMM setPriority(FlashCardMM fc, int i, ZonedDateTime lastDate) {
        //LOGGER.debug("\n***in FlashCardOps.setPriority()***");
        int size = flashListMM.size();
        // If the flashcard is below the threshold, add the card to the tree
        // Set the QNumber for the flash card according to it's index
        // remember/priority, & date last right.
        try {
            //If the card has not been seen before,
            if(fc.getNumSeen() == 0)
            {
                fc.setCNumber( i * 10 ); // =  i * 10;
                return fc;
            }
            // if getRemember is less than 0 see it at the start
            else if(fc.getRemember() < 0) {
                //fc.setCNumber( i * -10 + (size * -1) );
                fc.setCNumber(10 * (i + fc.getRemember() + size) * -1);
                return fc;
            }
            // If card has been seen after card #1, qNumber = 10 * list.size()
            else if(lastDate.compareTo(fc.getRtDate()) <= 0) {
                fc.setCNumber(10 * (i + fc.getRemember() + size));// = ( 10 * (i + fc.getRemember() + flashListMM.size()));
                return fc;
            }
            // For the case where the card is stale, or has a last correct date
            // earlier than the first cards last correct date,
            // This assists to re-org the card tree so that cards that haven't
            // been seen recently, are moved to the front of the tree. Unless
            // set remember is set to move it to the back of the tree.
            // if card getRight date is less than card 1's date
            // set the qNum to the index number * 10 + flashList size.
            else {
                //fc.setQNumber( flashList.size() + (i + fc.getRemember()) * 10);
                fc.setCNumber((i + fc.getRemember()) * 10);
                return fc;
            }
        }
        catch (NullPointerException e)
        {
            fc.setCNumber(10 * (fc.getRemember() + i));// = 10 * (fc.getRemember() + i);
            return fc;
        }
    }

    /**
     * Resets the deck so that all performance variables are set to zero
     */
    public void resetPerformance() {

        FlashCardMM resetCard;

        for(int i = 0; i < flashListMM.size(); i++) {

            resetCard = flashListMM.get(i);
            resetCard.setNumSeen(0);
            resetCard.setNumRight(0);
            resetCard.setIsRight(0);
            resetCard.setRemember(0);
            resetCard.setSeconds(0);
            //resetCard.setRtDate(new ZonedDateTime().);
        }
    }

    /**
     * Checks if flashList is null or length is greater than 3
     * @return returns false if flashList is less than 3 or is null, else it returns true.
     */
    private static boolean checkFList()
    {
        if(flashListMM == null || flashListMM.size() < 3) {
            //ReadFlash.tPaneObj.answerText.setText(ReadFlash.test.getTheseAns()[ReadFlash.taIndex].toString());
            return false;
        }
        return true;
    }
    
    
    
    
    
    // ******************************* ************* ******************************* //
    //                                  INNER CLASS
    //                                FileOperations
    // ******************************* ************* ******************************* //
    
 
    /***************************************************************************
    * *** INNER CLASS FileOperations ***
    * DESCRIPTION: Contains the variables for fileName, and FOLDER; Contains
    * ArrayList listOfFiles. Contians methods to Output to a serilized file
    * from an ArrayList and input to an ArrayList from a serilized file.
    **************************************************************************/
    public class FileOperations {
        private ObjectInputStream input;
        private ObjectOutputStream output;
        // *** GETTERS ***

        /**
         * @return the current FileName
         */
        public String getFileName()
        {
            return fileName;
        }

        /**
         * @return Returns the flashCard list from the currently selected file
         */
        @SuppressWarnings("unchecked")
        public ArrayList<FlashCardMM> getListFromFile() {
            final File check = new File(DECK_FOLDER + "/" + fileName);
            LOGGER.info("calling getListFromFile, filePath: " + check.getPath() );
            if( agrList.getLinkObj() != null) {
                if(check.exists()) {
                    LOGGER.debug("fileName exists {}", fileName);
                    return connectFileIn(fileName);
                }     
                else {
                    LOGGER.debug("fileName does not exist, creating new arrayList");
                    return new ArrayList();
                }
            }
            else {
                /** the file is remote and needs to be created
                 * @todo create folder and file on remote system
                 * according to name ... 
                 */
                LOGGER.warn("agrList is empty");
                return null;
            }
        }

        // *** SETTERS ***
        
        public void setFileName(String fName) {
            LOGGER.debug("setFileName fileName: {}", fileName);
            fileName = fName;
        }

        /**
         * Creates a deep copy of the flashCard ArrayList from the previous version.
         * It only copies the question, answer, and answer set.file The answer
         * number and question number are set to the index of each flashCard.
         * @param oFC The original flashCard list
         * @return a copy of the arrayList from the previous version.
         */
        public ArrayList copyFmLastVersion(ArrayList<FlashCardMM> oFC) {
            LOGGER.warn("*** copyFmLastVersion() called Deck may not "
                    + "be compatible with this version ***");

            ArrayList<FlashCardMM> copy = new ArrayList<>(oFC.size());
            if(oFC.isEmpty()) {
                LOGGER.warn("in FlashCard.copyFmLastVersion() list is empty ");
            }
            else {
                int i = 0;
                //for(int i = 0; i < oFC.size(); i++)
                for(FlashCardMM o : oFC)
                {
                   //   copy.add(new FlashCardMM(i, o.getQuestionMM().getQText(), o.getAText(), o.getAnswerSet()));
                    LOGGER.debug("copy no {}", i);
                    i++;
                }
            }
            return copy;
        }
        
        public boolean buildFileName(String bName)
        {
            int num;

            num = bName.lastIndexOf('.');
            if(num > 0)
            {
                bName = bName.substring(0, num);
            }
                
            bName = bName.replaceAll("[-/,.:;?!@$%#^&@+'{}()\"]", "");
            
            if(bName.length() > 2) {
                ReadFlash.getInstance().setDeckName(bName);
                fileName = bName + ".dat";
                fileExists(fileName, DECK_FOLDER);
                return true;
            }
            else
            {
                return false;
            }
        }


        // OTHER METHODS
    
    
        /**
         * Inputs from a local binary/serialized file to an ArrayList.
         * @param fileName
         * @return
         */
        protected ArrayList<FlashCardMM> connectFileIn(String fileName) {
            LOGGER.info("\n\n~~~~  In connectBinaryIn  ~~~~");
            LOGGER.debug("Calling method. ???: " + Thread.currentThread().getStackTrace()[3].getMethodName());
            try {
                //new Thread(
                LOGGER.info("trying filePath: {}/{}", DECK_FOLDER, fileName);
                
                FileInputStream fileIn = new FileInputStream(DECK_FOLDER + "/" + fileName);

                input = new ObjectInputStream(new BufferedInputStream(fileIn));
                flashListMM.clear();

                while(flashListMM.add((FlashCardMM)input.readObject()));

                LOGGER.info("flashlist size: {}", flashListMM.size());

            } catch(ClassNotFoundException e) {
                LOGGER.warn("\tClassNotFound Exception: connectBinaryIn() Line 1134 FlashCard");
                
            } catch(EOFException e) {
                //LOGGER.warn("\tEnd of file Exception: connectBinaryIn() Line 1134 FlashCard");

            } catch(FileNotFoundException e) {
                LOGGER.warn("\tFile Not Found exception: connectBinaryIn() Line 1134 FlashCard");
                // System.out.println(e.getMessage());
                //e.printStackTrace();

            } catch(IOException e) {
                LOGGER.warn("\t****** IO Exception in FlashCard.connectBinaryIn() *******");
                //e.printStackTrace();

                try {
                    LOGGER.warn("\t *** Trying to copy fromFmLastVersion *** ");
                    flashListMM = copyFmLastVersion((ArrayList<FlashCardMM>) input.readObject());

                } catch(IOException f) {
                    //System.err.println("\tFM IOException, copyFmLastVersion failed!");
                    f.getStackTrace();
                    f.getCause();

                } catch(ClassNotFoundException f) {
                    LOGGER.warn("\tClassNotFound Exception line 323 FlashCard");
                    //System.out.println(f.getMessage());
                    //System.out.println(Arrays.toString(f.getStackTrace()));
                }
            } catch(Exception e) {
                // Set errorMsg in FlashMonkeyMain and let the ReadFlash class
                // handle the error in createReadScene()
        //        FlashMonkeyMain.setErrorMsg(" --   Oooph my bad!!!   --\n" +
        //                "The flash card deck you chose \n\"" + ReadFlash.getDeckName()
        //                + "\"\n will not work with this version of FlashMonkey" );
                
                LOGGER.warn("\tUnknown Exception in connectBinaryIn: ");
                e.printStackTrace();
            } finally {
                closeInStream(input, null);
            }
            
            return flashListMM;
        }


        /**
         * <p>Outputs an array of Strings to file</p>
         * @param m DeckMetaData containing a decks
         *                metaData. THe file stored is named
         *                from the DeckName and ends with .met
         */
        public void setMetaInFile(DeckMetaData m, String deckName) {
         //   LOGGER.debug("setMetaInFile called");
            File folder = new File(DirectoryMgr.getMediaPath('t'));
            String metaFileName = deckName + ".met";

            try (DataOutputStream dataOut =
                         new DataOutputStream(new FileOutputStream(folder + "/" + metaFileName))){
                
                LOGGER.debug("set MetaDataObj in file");
                LOGGER.debug("scoresToArray looks like: {}", scoresToArray(m.getScores()));
                
                dataOut.writeUTF(Long.toString(m.getLastDate()));
                dataOut.writeUTF(m.getCreateDate());
                dataOut.writeUTF(m.getCreatorEmail());
                dataOut.writeUTF(m.getDeckFileName());
                dataOut.writeUTF(m.getDescript());
                dataOut.writeUTF(m.getNumImg());
                dataOut.writeUTF(m.getNumVideo());
                dataOut.writeUTF(m.getNumAudio());
                dataOut.writeUTF(m.getNumCard());
                dataOut.writeInt(m.getNumSessions());
                dataOut.writeUTF(m.getDeckBook());
                dataOut.writeUTF(m.getDeckClass());
                dataOut.writeUTF(m.getDeckProf());
                dataOut.writeUTF(m.getDeckSchool());
                dataOut.writeUTF(m.getSubj());
                dataOut.writeUTF(m.getCat());
                dataOut.writeUTF(m.getLang());
                dataOut.writeUTF(m.getLastScore());
                dataOut.writeUTF(scoresToArray(m.getScores()));
            }
            catch(EOFException e) {
                //System.out.println(e.getMessage() + "\n" + e.getStackTrace());
                //e.printStackTrace();
            }
            catch(FileNotFoundException e) {
            //    LOGGER.warn(e.getMessage() + "\n" + e.getStackTrace());
                e.printStackTrace();
            }
            catch(IOException e) {
             //   LOGGER.warn(e.getMessage() + "\n" + e.getStackTrace());
                e.printStackTrace();
            }
            catch(Exception e ) {
            //    LOGGER.warn("Unknown Exception: " + "\n" + e.getStackTrace());
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
         * @param filePath the full path name of the file to be
         *                 retrieved.
         * @return A DeckMetaData obj from the file in the Param.
         */
        public void setMetaObjFromFile(String filePath) {

            LOGGER.debug("getMetaFromFile called");
            try (DataInputStream dataIn = new DataInputStream(new FileInputStream(filePath))){
                LOGGER.debug("getMetaFromFile Trying");
                DeckMetaData obj = DeckMetaData.getInstance();
    
                System.out.println("Printout from file: \n" +
                                "1. setLastDate: " + dataIn.readLong() + "\n" +
                                "2. setCreateDate " + dataIn.readUTF() + "\n" +
                                "3. setOrigAuthor " + dataIn.readUTF() + "\n" +
                                "4. setDeckFileName " + dataIn.readUTF() + "\n" +
                                "5. setDescript" + dataIn.readUTF() + "\n" +
                                "6. setNumImg" + dataIn.readInt() + "\n" +
                                "7. setNumVideo" + dataIn.readInt() + "\n" +
                                "8. setNumAudio" + dataIn.readInt() + "\n" +
                                "9. setNumCard" + dataIn.readInt() + "\n" +
                                "10. setNumSessions" + dataIn.readInt() + "\n" +
                                "11. setDeckBook" + dataIn.readUTF() + "\n" +
                                "12. setDeckClass" + dataIn.readUTF() + "\n" +
                                "13. setDeckProf" + dataIn.readUTF() + "\n" +
                                "14. setDeckSchool" + dataIn.readUTF() + "\n" +
                                "15. setSubj" + dataIn.readUTF() + "\n" +
                                "16. setCat" + dataIn.readUTF() + "\n" +
                                "17. setLang" + dataIn.readUTF() + "\n" +
                                "18. setLastScore" + dataIn.readUTF() + "\n" +
                                "19. setScores" +dataIn.readUTF()  + "\n");

                
                obj.setLastDate(dataIn.readLong());
                obj.setCreateDate(dataIn.readUTF());
                obj.setCreatorEmail(dataIn.readUTF());
                obj.setDeckFileName(dataIn.readUTF());
                obj.setDescript(dataIn.readUTF());
                obj.setNumImg(dataIn.readInt());
                obj.setNumVideo(dataIn.readInt());
                obj.setNumAudio(dataIn.readInt());
                obj.setNumCard(dataIn.readInt());
                obj.setNumSessions(dataIn.readInt());
                obj.setDeckBook(dataIn.readUTF());
                obj.setDeckClass(dataIn.readUTF());
                obj.setDeckProf(dataIn.readUTF());
                obj.setDeckSchool(dataIn.readUTF());
                obj.setSubj(dataIn.readUTF());
                obj.setCat(dataIn.readUTF());
                obj.setLang(dataIn.readUTF());
                obj.setLastScore(dataIn.readUTF());
                //obj.setTestTypes
                obj.setScores(scoresReadBack(dataIn.readUTF()));

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
                // Set errorMsg in FlashMonkeyMain and let the ReadFlash class
                // handle the error in createReadScene()
                //        FlashMonkeyMain.setErrorMsg(" --   Oooph my bad!!!   --\n" +
                //                "The flash card deck you chose \n\"" + ReadFlash.getDeckName()
                //                + "\"\n will not work with this version of FlashMonkey" );

                LOGGER.warn("\tUnknown Exception in getMetaFromFile: ");
                e.printStackTrace();
            }
            
            //return null;
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
         * <P><b>USE</b> when an empty element exists at the end of the list. </P>
         * <p>Outputs any arraylist including FlashList (given in the parameter) to a serialized
         *             file from the arrayList in the parameter.</p>
         * @param arrayList FlashList or other ArrayList
         * @param minus The minus that defines the folder where this
         *             file will be created. If char = '-' then
         *             the last element is subtracted from the list. This should be used
         *             when there is an empty element at the end of the list.
         *
         */
        public void setListinFile(ArrayList arrayList, char minus) {
    
            LOGGER.info("\nsetListInFile with TYPE\n");
            
            File folder = new File(DirectoryMgr.getMediaPath(minus));

    //       UserData userData = new UserData();
    //        CloudOps co = new CloudOps();
            //fileName = ReadFlash.getInstance().getDeckName() + ".dat";
            
            LOGGER.info(" in setListInFile(ArrayList). path is: " + folder + "/" + fileName +
                    "\n and numCards: " + arrayList.size());

            LOGGER.info("setListInFile with minus: {}", minus);

            // action
            try
            {
                output = new ObjectOutputStream(new BufferedOutputStream
                            (new FileOutputStream(folder + "/" + fileName), 512));
                if(minus == '-') {
                    for (int i = 0; i < arrayList.size() - 1; i++) {
                        output.writeObject(arrayList.get(i));
                    }
                } else {

                    LOGGER.info("setListInFile(w/ minus: {}) building list to full size", minus);

                    for (int i = 0; i < arrayList.size(); i++) {
                        output.writeObject(arrayList.get(i));
                    }
                }
                // Send the String file
                //co.connectCloudOut('t', userData.getUserName(), fileName);
            }
            catch(EOFException e) {
                //System.out.println(e.getMessage() + "\n" + e.getStackTrace());
                //e.printStackTrace();
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
            finally{
                closeOutStream(output, null);
            }
        }

        /**
         * Outputs the verify object in the parameter to file
         * @return returns the message if successful or not
         */
        public String setVerifyInFile(Object obj) {

            if(obj.getClass().getName().contains("UserAuthInfo")) {
                boolean bool = setObjInFile(obj, "verifyData.met");
                if (bool) {
                    return "Success: New user created";
                }
            }
            return "Failed: User was not created.";
        }

        /**
         * Outputs the object in the Param to a file in the filePathName
         * @param obj
         * @param fileName
         * @return true if successful
         */
        public boolean setObjInFile(Object obj, String fileName) {

            File folder = new File(DirectoryMgr.getMediaPath('z'));
            if(folder.exists()) {
                return false;
            }
            else {
                folder.mkdirs();
            }

            LOGGER.info("n called setObjInFile(Obj)  and path is: " + folder + "/" + fileName);
            boolean bool = false;
            String fullPathName = folder + "/" + fileName;
            try {
                output = new ObjectOutputStream(new BufferedOutputStream
                        (new FileOutputStream(fullPathName), 512));
                output.writeObject(obj);
                bool = true;
            }
            catch(EOFException e) {
                LOGGER.warn(e.getMessage());
            }
            catch(FileNotFoundException e) {
                LOGGER.warn(e.getMessage());
            }
            catch(IOException e) {
                LOGGER.warn("IOException: in connectBinaryOut Saving to file\n{}", e.getMessage());
                //e.printStackTrace();
            }
            catch(Exception e) {
                LOGGER.warn("Unknown Exception: \nFileName: {} \nCause: {} \nStackTrace: {}", fileName, e.getCause().toString(), Arrays.toString(e.getStackTrace()));
            }
            finally {
                closeOutStream(output, null);
                return bool;
            }
        }


        public Object getVerifyFmFile() {
            return getObjectFmFile("verifyData.met");
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

            try {
                //new Thread(
                FileInputStream fileIn = new FileInputStream(pathName);

                input = new ObjectInputStream(new BufferedInputStream(fileIn));
                return input.readObject();

            } catch(FileNotFoundException e) {
                LOGGER.warn("\tFile Not Found exception:  getObjFmFile() Line 986 FlashCard");
                LOGGER.warn(e.getMessage());
                e.printStackTrace();

            } catch(IOException e) {
                LOGGER.warn("\t****** IO Exception in FlashCard. getObjFmFile() *******");
                e.printStackTrace();

            } catch(Exception e) {
                // Set errorMsg in FlashMonkeyMain and let the ReadFlash class
                // handle the error in createReadScene()
                //        FlashMonkeyMain.setErrorMsg(" --   Oooph my bad!!!   --\n" +
                //                "The flash card deck you chose \n\"" + ReadFlash.getDeckName()
                //                + "\"\n will not work with this version of FlashMonkey" );

                LOGGER.warn("\tUnknown Exception in connectBinaryIn: ");
                e.printStackTrace();

            } finally {
                closeInStream(input, null);
            //    return null;
            }
            return null;
        }
    
        /**
         * Saves an image to the desired directory
         * @param imgFileName The fileName
         * @param image the image to save
         * @param type The type of media to save the image. Should be a 'c' 'C' for images in decks
         *             or 't' for the usersImg.
         * @return Returns the fileName
         */
        public String saveImage(String imgFileName, Image image, char type) {
            LOGGER.info("saving media to file");

            DirectoryMgr dirMgr = new DirectoryMgr();
            String mediaPath = dirMgr.getMediaPath(type);
            File imageFile = new File(mediaPath + imgFileName);
            FileOpsUtil.folderExists(imageFile);
            String mediaFileName = imgFileName;
    
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", imageFile);
            } catch(IOException e) {
                LOGGER.warn("ERROR: image was not saved. FileName: {}", mediaFileName);
            }
            
            return mediaFileName;
        }

        /**
         * Used for deckSearch and other areas to reduce errors created by user
         * input for US Schools. Used to keep naming uniform. IE Univeristy of Texas
         * at Austin. Assists with search. This method creates the school objects used
         * in drop down boxes.
         * @return
         */
        public ObservableList<CSVUtil.SchoolObj> getSchoolObjs() {
            String res = "processedCampus.dat";
            ArrayList<CSVUtil.SchoolObj> sclObjects;
            ObjectInputStream inputs;
            
            try {
                //inputs = new ObjectInputStream(new BufferedInputStream(new getResourceAsStream(res)));
                inputs = new ObjectInputStream(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream(res)));
                //xxxxx Problem here --> Usually caused by the need to recompile the Schools File created by forms.searchables.CSVUtil
                sclObjects = (ArrayList<CSVUtil.SchoolObj>) inputs.readObject();
                //ArrayList<String> instList = new ArrayList<>();
                //for(CSVUtil.SchoolObj o : sclObjects) {
                //    instList.add(o.getName());
                //}
                ObservableList<CSVUtil.SchoolObj> ol = new SimpleListProperty<>(FXCollections.observableArrayList(sclObjects));
                return ol;
                
            } catch (FileNotFoundException e) {
                LOGGER.warn("WARNING could not find file: {}\n{}", e.getMessage(), e.getStackTrace());
            } catch(IOException e) {
                LOGGER.warn("\t****** IO Exception in FlashCard. getObjFmFile() *******");
                e.printStackTrace();
            } catch (Exception e) {
                LOGGER.warn("WARNING Exception get schoolObj: {}\n {}", e.getMessage(), e.getStackTrace());
                e.printStackTrace();
            }
            return null;
        }
        
        
    
        /**
         * Primarily used for testing
         * Prints the qNum and the aNum for the list of cards
         * //@param fList Expects an ArrayList<FlashCardMM>
         */
        /*   public void printList(ArrayList<FlashCardMM> fList)
        {
            fList.forEach((fc) -> {
               //System.out.println("\tqNum = "+ fc.getCNumber() + ", aNum: " + fc.getANumber() + " numSeen: " + fc.getNumSeen()
                 + " remember: " + fc.getRemember());
            });
        }
        */
        
        
    }
    
    // ****** END OF INNERCLASS FileOperations ************
    
    
    /**
     * Closes the input stream
     * @param closeMe
     * @param message
     */
	private void closeInStream(ObjectInputStream closeMe, String message) {
		try {
			//System.out.println("closeInStream() method in FlashCard");
			closeMe.close();  // To prevent the compiler from
			// complaining and runtime errors
		}
		catch(NullPointerException e) {
			if (message == null) {
				// do nothing
			}
			else {
				//System.out.println(message);
			}
		}
		catch(EOFException e) {
			LOGGER.warn("End Of File Exception while closing");
			//System.out.println(e.getMessage());
			//System.out.println("Exiting ...");
			System.exit(0);
		}
		catch(IOException e) {
			//System.out.println("IOFile Exception: while closing" + e.getMessage());
			//System.out.println("Exiting ...");
			System.exit(0);
		}
	}
	
	private void closeOutStream(ObjectOutputStream closeMe, String message) {
		try {
			closeMe.close();  // To prevent the compiler from
			// complaining and runtime errors
		}
		catch(NullPointerException e) {
			if (message == null) {
				// do nothing
			}
			else {
				//System.out.println(message);
			}
		}
		catch(EOFException e) {
			System.exit(0);
		}
		catch(IOException e) {
			System.exit(0);
		}
	}
	
	
	// ******************************* ************* ******************************* //
    //                                  INNER CLASSES
    // ******************************* ************* ******************************* //
    
    
    /**
     * DESCRIPTION: THis class creates the fileSelectPane that is output to the 
     * users screen. The EncryptedUser may select the file they desire. That file will
     * be used until the EncryptedUser selects another file, or exits. If no file exists
     * or if the EncryptedUser chooses, a new file can be created.
     */
    protected class FileSelectPane {
        private boolean fileSelected = false;
        protected ToggleGroup rdoGp = new ToggleGroup(); // radio button group for listOfFiles
        protected VBox paneForFiles = new VBox(4);
        private FileOperations fo = new FileOperations();
        
        // For the file does not exist window        
        private TextField newNameField;   // new name field
        private Label labelNnf;
        private Label plainTxt;

        //*** VIEW PANE CONSTRUCTOR ***

        /**
         * File select pane
         */
        protected FileSelectPane() {
            agrList = new AgrFileList(DECK_FOLDER);
        }
        
        protected void createPane() {
            LOGGER.setLevel(Level.DEBUG);
            LOGGER.info("FileSelectPane called");
            Label recLabel = new Label("Recent decks");
            recLabel.setTextFill(Color.WHITE);
            recLabel.setId("deckNameLabel");
            //Label oldLabel = new Label("Older than 3 months");
    
            ArrayList<LinkObj> currentList = agrList.getRecentFiles();
            ArrayList<LinkObj> oldList = agrList.getOlderFiles();
    
            LOGGER.info("CurrentFiles.size() {}, olderFiles.size() {}", currentList.size(), oldList.size());
            LOGGER.info("AGRList.size() {}", agrList.getSize());
    
            // Check if folder exists and has more than one file .
            // first file is default file.
            if((DECK_FOLDER.exists()) && (agrList.getSize() > 0)) {
                // output list of files{
                //If there is a folder that exists and the agregated list of files is greater
                // than 0, filter out copies -"copy" 2) output the filenames, eliminate the
                // ".dat" 3) add a radio button 4) add the rdo/actionListener
                if(currentList.size() > 0) {
                    SelectableRdoField rdoField = new SelectableRdoField();
                    paneForFiles.getChildren().add(recLabel);
                    for (LinkObj lObject : currentList) {
                        // The deck element
                        HBox fieldBox = rdoField.buildField(lObject);
                        // Deck element action
                        fieldBox.setOnMouseClicked( e -> {
                            if(e.isSecondaryButtonDown()) {
                                fieldSecondaryAction();
                            } else {
                                String name = lObject.getDescrpt();
                                name = name.substring(0, name.length() -4);
                                fieldPrimaryAction(lObject, name);
                            }
                        });
                        paneForFiles.getChildren().add(fieldBox);
                    }
                }
            }
            else  // create a new file since one does not exist
            {
                paneForFiles = newFile();
            }
        } // END PANE FOR FILES
    
        /**
         * Helper method to FileSelectPane. Provides
         * the primary key action for field mouse click.
         * @param lObject
         */
        private void fieldPrimaryAction(LinkObj lObject, String deckName) {
            LOGGER.debug("rdoButton clicked");
            ReadFlash.getInstance().resetInd();
            String lObjName = lObject.getDescrpt();
            // filename without the .dat ending
            // used by ReadFlash and other classes.
            ReadFlash.getInstance().setDeckName(deckName);
            agrList.setLinkObj(lObject);
            if(lObject.getCloudLink() != null) {
                System.out.println("CLOUD linkObject clicked, fileName: {}" + lObject.getDescrpt());
            //@TODO get and use SignedURL :) 11-11-2020
                fo.setFileName(lObjName);
                lObject.getCloudLink().retrieveDeckFmCloud();
            } else {
                System.out.println("LOCAL linkObject clicked, fileName: {}" + lObjName);
        
                fo.setFileName(lObjName);
                refreshFlashList();
            }
            fileSelected = true;
            FlashMonkeyMain.getWindow().getScene().setCursor(Cursor.DEFAULT);
            // takes User back to main menu
            FlashMonkeyMain.getWindow().setScene(FlashMonkeyMain.getNavigationScene());
        }
        
        private void fieldSecondaryAction() {
            // @todo fieldSecondaryAction in fileSelectPane, rightClick on deckLinks
        }
        

        /**
         * creates a new fileName in the
         * directory. If the name is successfully created it sends the user
         * to the CreateFlashPane.createFlashScene() so that the user may
         * immediatly begin creating new flashCards.
         * 
         * @return Returns a VBox
         */
        protected VBox newFile() {
            count = 0;
            FlashMonkeyMain.setIsInEditMode(true);
            // Text field for new file names
            newNameField = new TextField();
            newNameField.setStyle("-fx-text-fill: rgba(0,0,0,.6); ");
            
            Button nnfButton = new Button("Next");
            VBox box = new VBox();

            box.setSpacing(10);

            // Label and prompt displayed if there are no files
            if(agrList.getSize() == 0)
            {
                labelNnf = new Label("Ok,  Let's get you an A\n\n");
                labelNnf.setTextFill(Paint.valueOf(UIColors.FM_WHITE));
                plainTxt = new Label("Name your first study deck.");
                newNameField.setText("psst! clue.. \"History 105 Ch1\"");
                labelNnf.setId("h2Label");
                box.getChildren().addAll(labelNnf, plainTxt, newNameField);
            }
            else {
                labelNnf = new Label("Ok... \n"
                        + "Name your study deck. ");
                newNameField.setText("clue.. \"CS 220 Ch6\"");
                box.getChildren().addAll(labelNnf, newNameField);
            }

            newNameField.setOnKeyPressed(e -> {
                if(count <= 0) {
                    newNameField.setStyle("-fx-text-fill: rgba(0,0,0,1); ");
                    this.newNameField.setText("");
                    count++;
                }
                if(e.getCode() == KeyCode.ENTER) {
                    nnfButtonAction();
                }
        	});

            nnfButton.setOnAction( (ActionEvent e) -> nnfButtonAction() );

            box.getChildren().add(nnfButton);
            return box;
        }  
        
        private void nnfButtonAction() {
            if(count > 0)
            {
                if(fo.buildFileName(newNameField.getText()))// gets new file name fm field
                {
                    //flashList.clear();
                    LinkObj lo = new LinkObj(newNameField.getText());
                    agrList.setLinkObj(lo);

                    fileSelected = true;
                    CreateFlash createFlash = CreateFlash.getInstance();
                    FlashMonkeyMain.getWindow().setScene(createFlash.createFlashScene());
                }
            }
            else {
                newNameField.setText("");
                newNameField.setPromptText("Aww... enter what you're studying");
                newNameField.setStyle("-fx-prompt-text-fill: #0093EF;");
            }
        }
    } /*  *** *** *** END OF FileSelectPane PRIVATE CLASS *** *** ***/


    /****** TESTING METHODS *****/

    @FMAnnotations.DoNotDeployMethod
    public Point2D getNewNameFieldXYforTest() {
        FileSelectPane fsp = new FileSelectPane();
    return new Point2D(fsp.newNameField.getBoundsInLocal().getMinX() + 10, fsp.newNameField.getBoundsInLocal().getMinY() + 10);
    }
}



