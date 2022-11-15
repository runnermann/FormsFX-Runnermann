/*
 * AUTHOR: Lowell Stadelman
 */

package flashmonkey;

import ch.qos.logback.classic.Level;
import fileops.*;
import fileops.utility.Utility;
import forms.searchables.CSVUtil;
import fmtree.FMTWalker;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import media.sound.SoundEffects;
import org.jetbrains.annotations.NotNull;
import uicontrols.FMAlerts;


/*******************************************************************************
 * <p>Singleton Class, The class is expected to be used by the main thread. Although this classes methods
 * may be used in separate threads. Not thread safe.</p>
 * <p>Not compatible with Version 20170512 and earlier do
 * to flashCard class restructuring. </p>
 *
 * <p>CLASS DESCRIPTION: FlashCardOps contains variables and methods for the operation of the FlashCard class.</p>
 * <ul>
 * <li> This class handles input and output to file. For Output to cloud see CloudOps and
 * S3 related operating classes. </li>
 *
 * <li>Outputs flashcards to a binary file, it contains the array of
 * FLashCardMM's. </li>
 * </ul>
 *
 * @version Date: 2018/08/04
 * @author Lowell Stadelman
 * // @TODO deserialize files with restrictions. ValidatingObjectInputStream in = new ValidatingObjectInputStream(fileInput); in.accept(Foo.class);
 ******************************************************************************/

public class FlashCardOps extends FileOperations implements Serializable {//< T extends Comparable<T>> extends FlashCardMM<T> implements Comparable<T>

      // Logging reporting level is set in src/main/resources/logback.xml
      private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(FlashCardOps.class);
      //private static final Logger LOGGER = LoggerFactory.getLogger(FileOperations.class);

      private static final ArrayList<String> dontSaveDeck = new ArrayList<>();
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
            LOGGER.setLevel(Level.DEBUG);
      }

      public static synchronized FlashCardOps getInstance() {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new FlashCardOps();
            }
            return CLASS_INSTANCE;
      }

      public static boolean instanceExists() {
            return CLASS_INSTANCE != null;
      }


      // ************************************** ******* **************************************
      // ************************************** SETTERS **************************************
      // ************************************** ******* **************************************

      /**
       * Use when a fileName is not trusted or is input from a user. However, if the
       * file is already in existance, this method modifies the name if it is illegal.
       *
       * @param name
       * @return Returns the legal fileName
       * @throws IllegalStateException
       */
      public String setDeckFileName(@NotNull String name) throws IllegalStateException {
            String n = parseFileName(name);
            setFileName(n);
            return n;
      }

      /**
       * Sets the flashList to the parameter
       *
       * @param flashList
       */
      public void setFlashList(ArrayList<FlashCardMM> flashList) {
            flashListMM = flashList;
      }

      /**
       * Passes the password and name through to CloudOps to be consumed
       * during the getDeckList request. Called by SignInModel at user
       * initial signIn.
       *
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
       * file from the arrayList in the parameter.</p>
       *
       * @param arrayList FlashList or other ArrayList
       * @param minus     The minus that defines the folder where this
       *                  file will be created. If char = '-' then
       *                  the last element is subtracted from the list. This should be used
       *                  when there is an empty element at the end of the list.
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
       *
       * @return
       * @throws IllegalStateException if the fileName is null
       */
      public final String getDeckLabelName() throws IllegalStateException {
            if (deckTitle == null || deckTitle.contains("default")) {
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
       *
       * @return
       */
      @SuppressWarnings("unchecked")
//      public ArrayList<MediaSyncObj> getFlashListMedia() {
//            return getMediaHelper(flashListMM);
//      }

      /**
       * Returns a list of the media contained in the current flashList;
       * Used by MediaSync.mediaSync().
       *
       * @return
       */
      public ArrayList<MediaSyncObj> getFlashListMedia(ArrayList<FlashCardMM> flashList) {
            return getMediaHelper(flashList);
      }

      /**
       * Returns a list of unique media contained in the current flashList. Each media file string
       * will only exist once. IE if an image occurs more than once in a deck, it will only exist
       * once in the returned ArrayList.
       *
       * @param flashList
       * @return Returns a list of the unique mediaFileStrings contained in the current flashList;
       */
      private ArrayList<MediaSyncObj> getMediaHelper(ArrayList<FlashCardMM> flashList) {
            LOGGER.info("in getFlashMedia. flashListMM length: {}", flashList.size());
            Set<MediaSyncObj> mediaSyncObjs = new HashSet<>(flashList.size());
            String name;
            for (FlashCardMM fc : flashList) {
                  // Question
                  for (int i = 0; i < fc.getQFiles().length; i++) {
                        if (fc.getQFiles()[i] != null) {
                              name = fc.getQFiles()[i];
                              if (name != null || !name.equals("")) {
                                    mediaSyncObjs.add(new MediaSyncObj(name, fc.getQType(), 1));
                              }
                        }
                  }
                  // Answer
                  for (int i = 0; i < fc.getAFiles().length; i++) {
                        if (fc.getAFiles()[i] != null) {
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
       * getFlashList
       *
       * @return ArrayList of FlashCards
       */
      public final ArrayList<FlashCardMM> getFlashList() {
            return flashListMM;
      }


      /**
       * getElement()
       *
       * @param i
       * @return returns a new FlashCardMM object cloned from the ArrayList
       */
//      public FlashCardMM getElement(int i) {
//            try {
//                  return new FlashCardMM(flashListMM.get(i));
//            } catch (IndexOutOfBoundsException e) {
//                  return null;
//            }
//      }

      /**
       * Returns the tree_walker
       *
       * @return
       */
      public FMTWalker getTreeWalker() {
            return TREE_WALKER;
      }

      public final AgrFileList getAgrList() {
            if (agrList == null) {
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
                  //the file is remote and needs to be created.
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
       *
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
      public void refreshFlashList() {
            LOGGER.info("\n\n\t---^^^--- REFRESHING FLASHLIST  ---^^^---\n" +
                " pulling it from saved file\n\n");
            //LOGGER.debug("Calling method. ???: " + Thread.currentThread().getStackTrace()[3].getMethodName());
            LOGGER.debug("DeckName: {}", getFileName());
            flashListMM = getListFromFile();
      }

      /**
       * This method clears and rebuilds the tree. It does not
       * reset highest and lowest child referances.
       */
      public void refreshTreeWalker() {
            LOGGER.debug("*** FlashCardOps.refreshTreeWalker() ***");

            TREE_WALKER.clear();
            buildTree();
      }

      /**
       * Clears and rebuilds the tree to the list provided in the paramter.
       * Does not reset the highest and lowest child referances.
       *
       * @param cardList
       */
      public void refreshTreeWalker(ArrayList<FlashCardMM> cardList) {
            TREE_WALKER.clear();
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

      public void addToNotCompatibleList(String name) {
            dontSaveDeck.add(name);
      }

      /**
       * Check if the flashList is compatible and save it to file and cloud.
       * The sub-level ensures the application is connected before saving
       * to cloud. If the user has not selected a deck before exiting,
       * no action is taken.
       */
      public void safeSaveFlashList() {
            String name = FlashCardOps.getInstance().getDeckFileName();
            if (!dontSaveDeck.contains(name) && !name.equals("default.dec")) {
                  try {
                        if (CreateFlash.getInstance() != null
                            && CreateFlash.getInstance().getFlashListChangedUnmodifiable()
                            && (CreateFlash.getInstance().getCreatorList().size() != 0 )) {

                              CreateFlash.getInstance().saveOnExit();
                        } else {
                              LOGGER.error("ERROR SAVING FLASHLIST: FlashList is empy. Did not save list in safeSaveFlashList!");
                        }

                        /* else if (ReadFlash.getInstance() != null
                            && ReadFlash.getInstance().hasChanged()) {
                              // Ensure that user cannot get here
                              // if there are deck compatibility issues.
                              unsafeSaveFlashList();
                        }*/
                  } catch (Exception e) {
                        LOGGER.warn(e.getMessage());
                        e.printStackTrace();
                  }
            }
      }

      /**
       * Unsafe Save. Saves the flashList and does not check if
       * it was compatible. Ensure that it is not saving from a new file.
       * This method will rewrite an empty deck if the deck is not compatible.
       * Saves the current flashList to file and the cloud(if connected),
       * if the file is {@code >} 0 cards. Used by
       * ReadFlash.
       */
      public void unsafeSaveFlashList() {
            LOGGER.debug("*!*!* FlashOps saveFlashlist *!*!*");

            if (flashListMM.size() > 0) {
                  // keep the last element
                  setListinFile(flashListMM, '+');

                  if (Utility.isConnected()) {
                        CloudOps.putDeck(getFileName());
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
      private void buildTree() {
            LOGGER.info(" ---- START BUILDTREE() ---- ");
            LOGGER.debug("\t number elements in flashList: {}", flashListMM.size());
            LOGGER.debug("\t is tree empty? should be: {}", TREE_WALKER.isEmpty());

            // set the threshold, and set the time last seen to before or after the first card.
            thresh = new Threshold(flashListMM);
            ZonedDateTime lastDate;
            if (flashListMM.size() > 0) {
                  lastDate = flashListMM.get(0).getRtDate();
                  lastDate = lastDate.minusSeconds(20);
            } else {
                  ZoneId zID = ZoneId.of("America/Los_Angeles");
                  lastDate = ZonedDateTime.now(zID);
            }

            // Build the tree.
            for (int i = 0; i < flashListMM.size(); i++) {
                  FlashCardMM localCard = flashListMM.get(i);

                  // Don't show cards that have a high enough correct rate.
                  // If the flashcard is below the threshold, add the card to the tree
                  if (!thresh.boolHide(localCard)) {
                        // ANumber is the index number in flashList. It is used
                        // to modify a flashCard in flashList.
                        localCard.setANumber(i);

                        // A flashCards priority is based on several factors that modify
                        // it's placing in the tree. A flashCard's QNumber is modified
                        // based off of it's priority.
                        localCard = setPriority(localCard, i, lastDate);

                        // set isRight to 0/unanswered for this session
                        localCard.setIsRightColor(0);
                        // set localCard unseen for this session
                        localCard.setSessionSeen(0);

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
       *
       * @param cardList // @TODO check with older version, order is not correct!
       */
      protected void buildTree(ArrayList<FlashCardMM> cardList) {
            LOGGER.debug("in FlashCardOps.buildTree(ArrayList... list.size:{}", cardList.size());
            int count = 0;
            for (FlashCardMM cd : cardList) {
                  cd.setCNumber(count++);
                  TREE_WALKER.add(cd);
            }
      }

      /**
       * Prioritizes a card by setting it's qNumber. A card is prioritized according to it's last seen date.
       * Was it seen before the 1st card or after it. -The remember value is also a bias to assist
       * with setting the priority. Remember is set during the EncryptedUser session or manually set by the EncryptedUser in
       * the EditFlashCard UI. Important notes during build tree:
       * * - Priority(remember) if it's greater than the threshold it is automatically hidden.
       * * - When added to the tree, a card number is multiplied by 10 leaving space for
       * *    cards to be inserted for non-permanent reasons.
       * *    ie If the card was answered incorrectly during this session, the card is given a number that is
       * *    not a multiple of 10.
       * - Handled by a seperate method.
       * Cards that have been correctly answered more than meets the threshold are hidden.
       */
      private final static FlashCardMM setPriority(FlashCardMM fc, int idx, ZonedDateTime lastDate) {
            //LOGGER.debug("\n***in FlashCardOps.setPriority()***");
            int size = flashListMM.size();
            // If the flashcard is below the threshold, add the card to the tree
            // Set the QNumber for the flash card according to it's index
            // remember/priority, & date last right.
            try {
//                  NoteCards
//                  if(fc.getTestType() == 0b0010000000000000) {
//                        fc.setCNumber(i * 10);
//                        return fc;
//                  }
                  //If the card has not been seen before,
//                  if (fc.getNumSeen() == 0) {
//                        fc.setCNumber(i * 10 -100); // =  i * 10;
//                        return fc;
//                  }
                  // if card has been answered wrong,
                  // getRemember is less than 0.
                  // Set it at the start
                  if (fc.getRemember() < 0) {
                        //fc.setCNumber( i * -10 + (size * -1) );
                        fc.setCNumber(10 * (fc.getRemember()));
                        return fc;
                  }
                  // If card has a seen date after card #1, qNumber = 10 * list.size()
                  else if (lastDate.compareTo(fc.getRtDate()) <= 0) {
                        fc.setCNumber(20 * (idx + fc.getRemember() + size));// = ( 10 * (i + fc.getRemember() + flashListMM.size()));
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
                        fc.setCNumber(idx * 10);
                        return fc;
                  }
            } catch (NullPointerException e) {
                  fc.setCNumber(10 * (fc.getRemember() + idx));// = 10 * (fc.getRemember() + i);
                  return fc;
            }
      }



      /**
       * Checks if flashList is null or length is greater than 3
       *
       * @return returns false if flashList is less than 3 or is null, else it returns true.
       */
      private static boolean checkFList() {
          //ReadFlash.tPaneObj.answerText.setText(ReadFlash.test.getTheseAns()[ReadFlash.taIndex].toString());
          return flashListMM != null && flashListMM.size() >= 3;
      }


      /**
       * Used for deckSearch and other areas to reduce errors created by user
       * input for US Schools. Used to keep naming uniform. IE University of Texas
       * at Austin. Assists with search. This method creates the school objects used
       * in drop down boxes.
       *
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
            } catch (IOException e) {
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
     */

    /**
      * Helper method to FileSelectPane. Provides
      * the primary key action for mouse click.
      * @param lObject
      */
      void fieldPrimaryAction(LinkObj lObject, String deckName) {
            LOGGER.debug("rdoButton clicked");

            // ensure token is still valid
            if (Utility.isConnected() && CloudOps.isInValid()) {
                  LOGGER.warn("Token is expired, login again? ");
                  FMAlerts alerts = new FMAlerts();
                  alerts.sessionRestartPopup();
            } else {
                  LOGGER.debug("token is good or renewed");
                  ReadFlash.getInstance().resetGaugeValues();
                  String lObjName = lObject.getDescrpt();
                  agrList.setLinkObj(lObject);
                  // if the deck is from the cloud
                  if (lObject.getCloudLink() != null) {
                        LOGGER.debug("CLOUD linkObject clicked, fileName: {}" + lObject.getDescrpt());
                        setFileName(lObjName);
                        lObject.getCloudLink().retrieveDeckFmCloud();
                        refreshFlashList();
                  } else {
                        LOGGER.debug("LOCAL linkObject clicked, fileName: {}" + lObjName);
                        setFileName(lObjName);
                        // resets the width of the  text pane
                        refreshFlashList();
                  }

                  // media sync
                  LOGGER.debug("has flashlist been downloaded yet? flashlist size: {}", flashListMM.size());
                  if (flashListMM.size() > 0 && Utility.isConnected()) {
                        // @TODO move thread to MediaSync for async download. Not here
                        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
                        Runnable task = () -> {
                              LOGGER.info("Calling syncMedia from FlashCardOps primaryAction");
                              MediaSync.syncMedia(flashListMM);
                              scheduledExecutor.shutdown();
                        };
                        scheduledExecutor.execute(task);
                  }
                  if (flashListMM.size() > 0) {
                        SoundEffects.PRESS_BUTTON_COMMON.play();
                        // Send user to main menu
                        FlashMonkeyMain.setPaneToModeMenu();
                  }
            }
      }
}



