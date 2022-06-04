/*
 * AUTHOR: Lowell Stadelman 
 */

package flashmonkey;

import fileops.*;
import fileops.utility.Utility;
import forms.searchables.CSVUtil;
import fmtree.FMTWalker;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import java.io.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.FMAlerts;


/*******************************************************************************
 * <p>Singleton Class, The class is expected to be used by the main thread. Although this classes methods
 * may be used in separate threads. Not thread safe.</p>
 * <p>Not compatible with Version 20170512 and earlier do
 * to flashCard class restructuring. </p>
 *
 * <p>CLASS DESCRIPTION: FlashCardOps contains variables and methods for the operation of the FlashCard class.</p>
 * <ul>
 * <li> This class contains methods and variables for input and output to file. For Output to cloud see CloudOps and
 * S3 related operating classes. </li>
 *
 * <li>Outputs flashcards to a binary file, it contains an array that
 * is used to read the flashcards. This is done so that this class may
 * remain server side. The EncryptedUser.EncryptedUser will not have access to the correct answer
 * unless they are using it for study sessions. </li>
 * </ul>
 *
 * @version iOOily FlashMonkeyMM Date: 2018/08/04
 * @author Lowell Stadelman
 * // @TODO deserialize files with restrictions. ValidatingObjectInputStream in = new ValidatingObjectInputStream(fileInput); in.accept(Foo.class);
 ******************************************************************************/

public class FlashCardOps extends FileOperations implements Serializable {//< T extends Comparable<T>> extends FlashCardMM<T> implements Comparable<T>

    // Logging reporting level is set in src/main/resources/logback.xml
    //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(FlashCardOps.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(FileOperations.class);

    private static final long serialVersionUID = FlashMonkeyMain.VERSION;
    private static final FMTWalker TREE_WALKER = FMTWalker.getInstance();
    private static String deckTitle;

    //** The current threshold from the last time the tree was built.
    private static Threshold thresh;

    // --- FLAGS ---
    // File synchronization flag. If media files have been synchronized with the cloud, don't do it again unless
    // there are changes in CreateFlash.
    private static volatile boolean mediaIsSynced;

    /**
     * Stores the flashcards that are created from the binary file,
     * that are added at the head of the list. And that are
     * stored back to the file.
     */
    private static volatile ArrayList<FlashCardMM> flashListMM = new ArrayList<>();
    // if null call resetAgrList
    private static AgrFileList agrList;


    /* SINGLETON */
    private static FlashCardOps CLASS_INSTANCE;
    private FlashCardOps() {
        super();
        fileExists("default.dec", getUserFolder());
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
     * Use when a fileName is not trusted or is input from a user. However, if the
     * file is already in existance, this method modifies the name if it is illegal.
     * @param name
     * @throws IllegalStateException
     * @return Returns the legal fileName
     */
    public String setDeckFileName(@NotNull String name) throws IllegalStateException {
        String n = parseFileName(name);
        System.out.println("FlashCardOps line 102 name: " + n);
        setFileName(n);
        return n;
    }
    
    /**
     * Sets the flashList to the parameter
     * @param flashList
     */
    public void setFlashList(ArrayList<FlashCardMM> flashList) {
        flashListMM = flashList;
    }

    /**
     * Passes the password and name through to CloudOps to be consumed
     * during the getDeckList request. Called by SignInModel at user
     * initial signIn.
     * @param name
     * @param pw
     * @return returns 1
     */
    public int setObjsFmS3(String name, String pw) {
        return CloudOps.setS3DeckList(name, pw);
    }

    public void setMediaIsSynced(boolean bool) {
        mediaIsSynced = bool;
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
    public void saveFlashListToFile(ArrayList arrayList, char minus) {
        setListinFile(arrayList, minus);
    }



    // ************************************** ******* **************************************
    // ************************************** GETTERS **************************************
    // ************************************** ******* **************************************

    public void resetDeckLabelName() {
        deckTitle = getDeckTitle();
    }

    /**
     * The deckLabelName is the deckFileName without the
     * mime ending.
     * @throws IllegalStateException if the fileName is null
     * @return
     */
    public final String getDeckLabelName() throws IllegalStateException {
        if(deckTitle == null || deckTitle.contains("default")) {
            deckTitle = getDeckTitle();
        }
        return deckTitle;
    }

    public final String getMetaFileName() throws IllegalStateException {
        String name = getDeckFileName();
        return name.substring(0, name.length() - 4) + ".met";
    }


    public final boolean getMediaIsSynched() {
        return mediaIsSynced;
    }
    
    /**
     * Returns a list of the media contained in the current flashList;
     * Used by MediaSync.mediaSync().
     * @return
     */
    @SuppressWarnings("unchecked")
    public ArrayList<MediaSyncObj> getFlashListMedia() {
        return getMediaHelper(flashListMM);
    }

    /**
     * Returns a list of the media contained in the current flashList;
     * Used by MediaSync.mediaSync().
     * @return
     */
    public ArrayList<MediaSyncObj> getFlashListMedia(ArrayList<FlashCardMM> flashList) {
        return getMediaHelper(flashList);
    }

    /**
     * Returns a list of unique media contained in the current flashList. Each media file string
     * will only exist once. IE if an image occurs more than once in a deck, it will only exist
     * once in the returned ArrayList.
     * @param flashList
     * @return Returns a list of the unique mediaFileStrings contained in the current flashList;
     */
    private ArrayList<MediaSyncObj> getMediaHelper(ArrayList<FlashCardMM> flashList) {
        LOGGER.info("in getFlashMedia. flashListMM length: {}", flashList.size());
        Set<MediaSyncObj> mediaSyncObjs = new HashSet<>(flashList.size());
        String name;
        for(FlashCardMM fc : flashList) {
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
        
        return new ArrayList<>(mediaSyncObjs);
    }



    /**
     * Returns the current threshold determined when the tree was last built.
     */
    protected Threshold getThresh()
    {
        return this.thresh;
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

    public final AgrFileList getAgrList() {
        if(agrList == null) {
            resetAgrList();
        }
        return agrList;
    }

    int getAgrListSize() {
        return agrList.getSize();
    }

    void resetAgrList() {
        //if(cloudLinks == null || cloudLinks.isEmpty()) { LOGGER.warn("cloudLinks is empty or null "); }
        agrList = new AgrFileList(getDeckFolder());
    }

    /**
     * @return Returns the flashCard list from the currently selected file
     */
    @SuppressWarnings("unchecked")
    protected ArrayList<FlashCardMM> getListFromFile() {
        String fileName = getFileName();
        final File check = new File(getDeckFolder() + "/" + fileName);
        LOGGER.info("calling getListFromFile, filePath: " + check.getPath());
        if (agrList.getLinkObj() != null) {
            if (check.exists()) {
                LOGGER.debug("fileName exists {}", fileName);
                return super.getListFmFile(check.getPath());
            } else {
                LOGGER.debug("fileName does not exist, creating new arrayList");
                return new ArrayList();
            }
        } else {
            /** the file is remote and needs to be created
             * @todo create folder and file on local system???
             * according to name ...
             */
            LOGGER.warn("agrList is empty");
            return null;
        }
    }

    public final String getDeckFileName() {
        return getFileName();
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
     * Resets the flashList to 0 elements then
     * gets the flashList from the local file.
     */
    public void refreshFlashList()
    {
        LOGGER.info("\n\n\t---^^^--- REFRESHING FLASHLIST  ---^^^---\n" +
        " pulling it from saved file\n\n");
        //LOGGER.debug("Calling method. ???: " + Thread.currentThread().getStackTrace()[3].getMethodName());
        LOGGER.debug("DeckName: {}", getFileName());
        clearFlashList();
        flashListMM = getListFromFile();

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
     * Saves the current flashList to file and the cloud(if connected),
     * if the file is {@code >} 0 cards. Used by
     * ReadFlash.
     */
    public void saveFlashList() {
        LOGGER.debug("*!*!* FlashOps saveFlashlist *!*!*");

        if(flashListMM.size() > 0) {
            // keep the last element
            setListinFile(flashListMM, '+');
            // @TODO change method name, saveFListToFile, to reflect saving to cloud as well
            if (Utility.isConnected() ) {
                // CloudOps co = new CloudOps();
                CloudOps.putDeck(getFileName());
                //CO.connectCloudOut('t', authcrypt.UserData.getUserName(), ReadFlash.getInstance().getDeckName());
            }
        }
        LOGGER.debug("in saveFlashList and flashListMM is size 0");
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
     * // @TODO check with older version, order is not correct!
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
     * Resets the deck so that all user performance variables are set to zero
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
    

    /**
     * Used for deckSearch and other areas to reduce errors created by user
     * input for US Schools. Used to keep naming uniform. IE University of Texas
     * at Austin. Assists with search. This method creates the school objects used
     * in drop down boxes.
     * @return
     */
    public ObservableList<CSVUtil.SchoolObj> getSchoolObjs() {
        String res = "processedCampus.dat";
        ArrayList<CSVUtil.SchoolObj> sclObjects;

        try (ObjectInputStream inputs = new ObjectInputStream(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream(res)))) {
            //xxxxx Problem here --> Usually caused by the need to recompile the Schools File created by forms.searchables.CSVUtil
            sclObjects = (ArrayList<CSVUtil.SchoolObj>) inputs.readObject();
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


    /****** TESTING METHODS *****/

    /*@FMAnnotations.DoNotDeployMethod
    public Point2D getNewNameFieldXYforTest() {
        FileSelectPane fsp = new FileSelectPane();
    return new Point2D(fsp.newNameField.getBoundsInLocal().getMinX() + 10, fsp.newNameField.getBoundsInLocal().getMinY() + 10);
    }*/

    //* Helper method to FileSelectPane. Provides
    //* the primary key action for field mouse click.
    //* @param lObject
    void fieldPrimaryAction(LinkObj lObject, String deckName) {
        LOGGER.debug("rdoButton clicked");

        // ensure token is still valid
        if(Utility.isConnected() && CloudOps.isInValid()) {
            LOGGER.warn("Token is expired, login again. ");
            FMAlerts alerts = new FMAlerts();
            alerts.sessionRestartPopup();
        }
        else {
            LOGGER.debug("token is good or renewed");
            ReadFlash.getInstance().resetInd();
            String lObjName = lObject.getDescrpt();
            agrList.setLinkObj(lObject);
            // if the deck is from the cloud
            if (lObject.getCloudLink() != null) {
                LOGGER.debug("CLOUD linkObject clicked, fileName: {}" + lObject.getDescrpt());
                //lObjName = parseFileName(lObjName);
                setFileName(lObjName);
                lObject.getCloudLink().retrieveDeckFmCloud();
                refreshFlashList();
            } else {
                LOGGER.debug("LOCAL linkObject clicked, fileName: {}" + lObjName);
                //lObjName = parseFileName(lObjName);
                setFileName(lObjName);
                refreshFlashList();
            }

            // media sync
            LOGGER.debug("has flashlist been downloaded yet? flashlist size: {}", flashListMM.size() );
            if(flashListMM.size() > 0 && Utility.isConnected()) {
                // @TODO move thread to MediaSync for async download. Not here
                new Thread(() -> {
                    LOGGER.info("Calling syncMedia from FlashCardOps primaryAction");
                    MediaSync.syncMedia(flashListMM);
                }).start();
            }

            FlashMonkeyMain.getWindow().getScene().setCursor(Cursor.DEFAULT);
            // takes User back to main menu
            FlashMonkeyMain.getWindow().setScene(FlashMonkeyMain.getNavigationScene());
        }
    }
}



