/*
 * AUTHOR: Lowell Stadelman
 */

package flashmonkey;

// *** JAVAFX IMPORTS ***

import ch.qos.logback.classic.Level;
import fileops.BaseInterface;
import fileops.CloudOps;
import fileops.MediaSync;
import fileops.utility.Utility;
import fmannotations.FMAnnotations;
import fmtree.FMTWalker;
import forms.DeckMetaModel;
import forms.DeckMetaPane;
//import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import media.sound.SoundEffects;
import metadata.DeckMetaData;
import org.controlsfx.control.PrefixSelectionComboBox;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import type.cardtypes.GenericCard;
import type.celleditors.DrawTools;
import type.celleditors.SectionEditor;
import type.testtypes.GenericTestType;
import type.testtypes.MultiChoice;
import type.testtypes.TestList;
import uicontrols.*;
//import uicontrols.api.ComboBoxAutoComplete;
//import uicontrols.api.FMAutoCompleteBox;
//import uicontrols.api.SerialAutoCompleteBox;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/*******************************************************************************
 * Synchronized Singleton class provides the user interface to create new flash-cards.
 * <p> Creates a new FlashCardMM set to defaults in the createFlashScene method. FlashCardMM
 * constructor creates a new cID. The currentCard is updated in save methods.
 * New Card Action methods use the default FlashCardMM constructor</p>
 *<P>
 * To Add a new TestType. - Add the class name to the ObservableList TESTS,
 *  The left is the name that appears in the choicebox and the right is the class
 *  name. Add the class, include the unique bitSet. The index of TESTS is
 *  indicated by the bitset's bit that is set from the littleEndian. Only one
 *  bit should be set. </p>
 * <pre>
 * Algorithm
 *      BEGIN
 *          User selects test type or default AI is used.
 *              - if AI ???? @todo create AI
 *              - Test type selects Card type
 *                  - Default is Double Horizontal
 *               - Section type selected by
 *                  - Default is single with TextCell
 *                  - if user drags a file into textArea
 *                  - or if authcrypt.user clicks snapShot
 *                  - then section = double section with TextCell on left
 *                  and media cell on right
 *
 *                  - if user clicks 'X' button on Text area then section is
 *                  multi-media only. Multi-Media includes image and drawings
 *                  - if user clicks 'x' button on media area. Section becomes
 *                  single section with TextCell only.
 *
 *              User clicks next FlashCard
 *                  - flashCard is saved to creatorList
 *                  - TextArea is cleared
 *                  - multimedia areas are cleared
 *                  - Users previous test type is used
 *                  - Sections return to default.
 *
 *             User clicks save
 *                  - creatorList saved to file
 *                  - TextArea is cleared
 *                  - Multimedia is cleared.
 *                  - Test type is default.
 *                  - User is sent to menu.
 *
 *             Navigation through existing cards
 *                  User clicks previousQButton or nextQButton
 *                      flashListChanged = true
 *                      if(changed) saveChanges
 *                      clear editors
 *                      populate editors
 *                      setCenter.clickedOn( changed = true )
 *                      changed = true
 *
 *                  User clicks revert
 *                      ensure we have the right card
 *                      revert creatorList(idx) to flashList(idx)
 *                  User clicks on delete
 *                      ensure we have the right card
 *                      iterator.remove()
 *                      remove the card in creatorList
 * </pre>
 *
 * <pre>
 *  When a section first appears, it appears as a single text pane.
 *    - When the user clicks on snapshot:
 *    - The section becomes a double pane with text left and image right.
 *    - if the user drags a file into the section,
 *     it becomes a double pane with that type of media on the right.
 *
 *     Create
 *         - Text only 't'
 *         - Image or image and shapes only 'c'
 *         - Shapes only 'c'
 *         - Audio only 'm'
 *         - Video only 'm'
 *         - Combination text and media 'C' or 'M'
 * </pre>
 *
 * <pre>
 *  Test types in order. ComboBox selection types are order important. It is tightly bound.
 *  EncryptedUser must select a mode. If not we send the user an error message.
 *
 *    0 = Default = multi-choice, multi-answer, T or F, and Write in.
 *    1 = Multiple choice
 *    2 = Multiple answer
 *    3 = True or false
 *    4 = Write it in
 *    5 = FITB fill in the blank
 *    6 = Turn in audio
 *    7 = Turn in video
 *    8 = Turn in Drawing
 *    9 = Draw over image
 *    10 = MathCard
 *    11 = GraphCard
 *    12 = NoteTaker
 *
 * </pre>
 *
 * <pre>
 * <b>Changing to editing mode</b>
 *     On leftQButton or rightQButton click, mode changes from creating a new
 *     card, to editing an existing card.
 * </pre>
 *
 ******************************************************************************/


public final class CreateFlash<C extends GenericCard> implements BaseInterface {

      private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(CreateFlash.class);
      //private static final Logger LOGGER = LoggerFactory.getLogger(CreateFlash.class);
      // Only one instance of this class may exist within a JVM. Not
      // a 100% solution.
      private static CreateFlash CLASS_INSTANCE;

      // The message when the card is not enabled
      String cardNotEnabledMsg = null;
      // if cards in flashList have changed
      // ensures that if there is an exit, the users
      // modifications are saved.
      private boolean flashListChanged;

      // MarketPlace/sell button
      private static Button metaButton;
      // Buttons for deckActions and card
      private Button saveReturnButton, insertCardButton, newCardButton, abandButton;
      // prev and next buttons for editing existing cards
      private Button nextQButton, prevQButton, deleteQButton, undoQButton;
      // resets all q's performance metrics to zero
      private Button resetDeckButton;
      private VBox masterPane;
      private VBox cfpCenter;
      private Stage metaWindow;

      // flags
      private boolean isDisabled;


      /**
       * currentCard = is always a card stored in
       * the creatorList. The last card on the list
       * is always empty after start. The last card
       * is removed before being set in the list in
       * FlashCardOps.setListInFile(...)
       */
      private FlashCardMM currentCard;

      //*** VARIABLES ***
      private int startSize;


      private DeckMetaData meta;// = new DeckMetaData();

      //*** ARRAY List ***
      private static volatile ArrayList<FlashCardMM> creatorList;//  = new ArrayList<>(20);

      // The iterator index. Starts at the last existing
      // index prior to the new card added.
      private int listIdx;// = creatorList.size();

      /**
       * Used by the ComboBox Test Selector.
       * Note: Combobox contains placeholder classes that
       * currently do nothing.
       * To select the test types in the ComboBox.
       */
      private final ObservableList<TestMapper> TESTS = FXCollections.observableArrayList(TestMapper.getTestList(TestList.TEST_TYPES));
      private static PrefixSelectionComboBox<TestMapper> entryComboBox = new PrefixSelectionComboBox<TestMapper>();

      /* ***** FOR TESTING ONLY ********/
      DeckMetaPane metaPane;

      // Editors
      private static Editors editors;
      private static class Editors {
                  private final  SectionEditor EDITOR_U;
                  private final  SectionEditor EDITOR_L;

                  Editors(SectionEditor editorU, SectionEditor editorL) {
                        EDITOR_U = editorU;
                        EDITOR_L = editorL;
                  }
      }


      /*******  METHODS ********/

      /**
       * no args constructor
       */
      private CreateFlash() {
            LOGGER.setLevel(Level.ALL);
      }

      /* ------------------------------------------------------- **/

      /**
       * Returns an instance of the class. If getInstance hasn't been called before
       * a new instance of the class is created. otherwise it returns the existing
       * instance.
       * To intialize: ReadFlash readFlashObj = ReadFlash.getInstance();
       *
       * @return ReadFlash
       */
      public static synchronized CreateFlash getInstance() {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new CreateFlash();
            }
            FlashMonkeyMain.getPrimaryWindow().setResizable(true);
            if (null != metaButton && Utility.isConnected()) {
                  metaButton.setDisable(false);
                  metaButton.setTooltip(new Tooltip("Please check the internet connection."));
            }
            return CLASS_INSTANCE;
      }

      /* ------------------------------------------------------- **/

      /**
       * Scene CONSTRUCTOR
       */
      public Scene createFlashScene() {

            flashListChanged = false;
            LOGGER.setLevel(Level.ALL);

            LOGGER.debug("called createFlashScene()");
            //String name = FlashCardOps.getInstance().getDeckFileName();
            LOGGER.debug("deckName: {}", FlashCardOps.getInstance().getDeckLabelName());

            metaWindow = new Stage();
            meta = DeckMetaData.getInstance();
            // Label at top of window
            // Get deck name from readflash.
            FlashCardOps fcOps = FlashCardOps.getInstance();
            fcOps.resetDeckLabelName();
            Label mainLabel = new Label("Deck name:  " + fcOps.getDeckLabelName());
            // Set id for style in CSS
            mainLabel.setId("deckNameLabel");

            // If we are working on a deck that was just downloaded
            // Then update the FlashList.
            if (fcOps.getFlashList() == null || fcOps.getFlashList().isEmpty()) {
                  fcOps.refreshFlashList();
            } else {
                  // @todo remove always save to cloud in saveFlashList
                  //fcOps.unsafeSaveFlashList();
                  fcOps.safeSaveFlashList();
                  fcOps.refreshFlashList();
            }

            LOGGER.debug("flashList is empty: {}", fcOps.getFlashList().isEmpty());

            // If this is adding cards to an existing flashCard list, then
            // creates the list to be added to. Also sets the
            // startSize and the cardNum
            if (!fcOps.getFlashList().isEmpty()) {
                  setCreatorList();
            } else {
                  LOGGER.debug("flashList is empty.");
                  creatorList = new ArrayList<>(10);
            }

            // The index starts at the end
            // of the list.
            if (creatorList != null) {
                  listIdx = creatorList.size();
            }

            // NORTH & CENTER PANES entry fields
            HBox cfpNorth = new HBox(4);
            cfpNorth.setPadding(new Insets(2, 4, 2, 4));
            // Contains the CardType
            cfpCenter = new VBox(2);
            //cfpCenter.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            cfpCenter.setPadding(new Insets(0, 4, 0, 4));
            cfpCenter.setSpacing(2);

            // contains the buttons in a horizontal row
            VBox cfpSouth;
            cfpNorth.setId("cfpNorthPane");

            /*
             * ****  ComboBox selects the Test type. Default is no mode.  ****
             * This is OBE.
             * 0 = Default = multi-choice, multi-answer, T or F, and Write in.
             * 1 = Multiple choice
             * 2 = Multiple answer
             * 3 = True or false
             * 4 = Write it in
             * 5 = FITB fill in the blank
             * 6 = Turn in audio
             * 7 = Turn in video
             * 8 = Turn in Drawing
             * 9 = Draw over image
             * 10 = MathCard
             * 11 = GraphCard
             * 12 = NoteTaker
             */
            // Set the array of TESTS in comboList
            // entryComboBox.editableProperty().setValue(true);
            entryComboBox.getItems().addAll(TESTS);
            // Set tooltip
            entryComboBox.setTooltip(new Tooltip("Select the test mode for this card." +
                //"\nAI: (Default) Intelligent selection mode, Intelligently selects top 4 modes." +
                //"\nAll others are as stated. " +
                "\nEffects the card layout. " +
                "\n** Type a card name in for rapid search"));
            // Set prefered width and height of combo box
            entryComboBox.setMaxWidth(Double.MAX_VALUE);
            entryComboBox.setPrefHeight(15);
            entryComboBox.setPromptText("CARD TYPE");
            // set number of rows visible
            entryComboBox.setVisibleRowCount(5);


            // For quick search of combobox items
            // new SerialAutoCompleteBox(entryComboBox);

            // create blank card with new cID for the first card
            // and add it to creatorList. getCID from card in list.
            currentCard = new FlashCardMM();
            creatorList.add(currentCard);
            currentCard.setCNumber(999);

            LOGGER.info("CreatorList size: {}, listIdx: {}", creatorList.size(), listIdx);
            // section editors
            editors = new Editors(new SectionEditor( "", makeQPrompt(listIdx), 'U', this.currentCard.getCID() ),
                                          new SectionEditor("", "Enter the answer here", 'L', this.currentCard.getCID()) );

            // Disable buttons if entrycombobox is showing.
            if (entryComboBox.isVisible()) {
                  disableEditors();
            }

            // If this is the first session, set the default create to multichoice
            if (cfpCenter.getChildren().isEmpty()) {
                  MultiChoice mode = MultiChoice.getInstance();
                  cfpCenter.getChildren().addAll(mode.getTEditorPane(creatorList, editors.EDITOR_U, editors.EDITOR_L, cfpCenter));
            }

            // Action call
            entryComboBox.setOnAction(e -> {
                  SoundEffects.PRESS_BUTTON_COMMON.play();
                  cfpCenter.getChildren().clear();
                  editors.EDITOR_U.styleToPrompt();
                  editors.EDITOR_U.setPrompt(makeQPrompt(listIdx));
                  cfpCenter.getChildren().addAll(entryComboBox.getValue().getTestType().getTEditorPane(creatorList, editors.EDITOR_U, editors.EDITOR_L, cfpCenter));
                  enableEditors();
            });

            // Add comboBox to buttonBox
            cfpNorth.getChildren().addAll(entryComboBox, mainLabel);

            //****         CFP SOUTH          ***
            // CFP south contains buttons gauges and non content control/information
            masterPane = new VBox(2);
            getKeyboardShortcuts();
            cfpSouth = getCreatorButtons();
            navButtonDisplay(listIdx);
            cfpSouth.setPadding(new Insets(10, 4, 10, 4));
            // Create a master borderPane then add buttons to the scene
            masterPane.setAlignment(Pos.CENTER);
            masterPane.setId("cfMasterPane");
            masterPane.getStylesheets().addAll("css/buttons.css", "css/mainStyle.css");
            masterPane.getChildren().addAll(cfpNorth, cfpCenter, cfpSouth);
            // create the scene and set it's size and bind it so it is
            // responsive to the window size
            Scene masterScene = new Scene(masterPane, SceneCntl.getCreateFlashHt(), SceneCntl.getCreateFlashHt());
            changeListenerBinding(masterScene);
            // the initial displayed element "last in list" is empty. Other
            // cards are handled by nav buttons
            FMTWalker.getInstance().setCurrentNodeReferance(currentCard);
            fcOps.buildTree(creatorList);
            fcOps.refreshTreeWalker(creatorList);
            if ( ! FlashMonkeyMain.getPrimaryWindow().isFullScreen() && ! FlashMonkeyMain.treeWindow.isShowing()) {
                  FlashMonkeyMain.buildTreeWindow();
                  FlashMonkeyMain.AVLT_PANE.displayTree();
                  entryComboBox.show();
            }

            return masterScene;
      }   /* END createFlashScene() ***/

      /* ------------------------------------------------------- **/

      /**
       * Binds the masterBPane to SceneCntl
       */
      private void changeListenerBinding(Scene masterScene) {
            ChangeListener<Number> sceneBoxListener = (observable, oldVal, newVal) -> {
                  SceneCntl.getReadFlashBox().setHt((int) masterScene.getHeight());
                  SceneCntl.getReadFlashBox().setWd((int) masterScene.getWidth());
                  SceneCntl.getReadFlashBox().setX((int) masterScene.getX());
                  SceneCntl.getReadFlashBox().setY((int) masterScene.getY());
            };

            masterScene.widthProperty().addListener(sceneBoxListener);
            masterScene.heightProperty().addListener(sceneBoxListener);
            masterScene.xProperty().addListener(sceneBoxListener);
            masterScene.yProperty().addListener(sceneBoxListener);
      }

      /* ------------------------------------------------------- **/

      /**
       * Returns this cards cID
       *
       * @return
       */
      public String getCurrentCID() {
            return this.currentCard.getCID();
      }

      /* ------------------------------------------------------- **/


      /**
       * @return the selected testType in the comboBox.
       */
      public static GenericTestType getTestType() {
            if (null != entryComboBox && null != entryComboBox.getValue()) {
                  return entryComboBox.getValue().getTestType(); //getTestType();
            }
            return null;
      }


      /* ------------------------------------------------------- **/

      protected boolean getFlashListChanged() {
            return (this.flashListChanged);
      }

      /**
       * Checks if the currentCard is equal with it's index in the FLashCardOps.FlashList
       * Uses the underlying equals methods from FlashCardMM and sub-classes to
       * verify if the content: e.g. media, shapes, and text;  are the same.
       * <p><b>NOTE:</b> Does not check if this is the last card in the deck.</p>
       *
       * @param currentCard
       * @return 1 if true,  0 if it is false, and -1 if currentCard is out of range,
       */
      private int cardEqualsOriginal(FlashCardMM currentCard) {
            if (listIdx > FlashCardOps.getInstance().getFlashList().size() -1) {
                  return -1;
            }

            LOGGER.debug("printing current card:\n {}", currentCard );

            boolean isEqual = currentCard.equals(FlashCardOps.getInstance().getFlashList().get(currentCard.getANumber()));
            LOGGER.debug("currentCard.equals flashList: {}", isEqual);
            if ( ! isEqual ) {
                  this.flashListChanged = true;
                  return 0;
            }
            return 1;
      }

      /* ------------------------------------------------------- **/

      /**
       * DESCRIPTION: returns the length of the creatorList.
       *
       * @return returns the length of the creator list
       */
      protected int getLength() {
            return creatorList.size();
      }

      /* ------------------------------------------------------- **/

      /**
       * Returns the current card number for the next card with exception to 0.
       *
       * @return
       */
  /*  public static int getCardNum() {
        return cardNum;
    }
  */
      /* ------------------------------------------------------- **/
      public VBox getCFPCenter() {
            return this.cfpCenter;
      }

      /**
       * Returns the upper SectionEditor reference
       *
       * @return upper SectionEditor referenc
       */
//    public SectionEditor getEditorU() { return this.editorU; }

      /* ------------------------------------------------------- **/

      /**
       * Returns the lower SectionEditor reference
       *
       * @return lower SectionEditor reference
       */
//    public SectionEditor getEditorL() { return this.editorL; }

      /* ------------------------------------------------------- **/

      /**
       * Helper method for previous, next buttons, and  AVLTree
       * next prev card
       * Sets the CreateFlash UI
       * <pre>
       * - sets the upper and lower editors fields
       * - sets the ComboBox
       * </pre>
       * @param currentCard
       */
      void setSectionEditors(FlashCardMM currentCard) {
            this.currentCard = currentCard;
            // Get the test and set it in the ComboBox.
            // Uses the bitset from the TestType.
            GenericTestType ga = TestList.selectTest(currentCard.getTestType());
            entryComboBox.setValue(new TestMapper(ga.getName(), ga));

            // Set up for TestTypes that use FlashCardMM.
            // If it's a non-standard test, resetSection may cause errors.
            editors.EDITOR_U.resetSection();
            editors.EDITOR_L.resetSection();

            if ( currentCard.getQType() != 't' ) {
                  editors.EDITOR_U.setSectionMedia( currentCard.getQFiles(), currentCard.getQType(), 'U', currentCard.getCID() );
            }
            if ( currentCard.getAType() != 't' ) {
                  editors.EDITOR_L.setSectionMedia( currentCard.getAFiles(), currentCard.getAType(), 'L', currentCard.getCID() );
            }
            // remove the text areas and set up media
            if ( ! editors.EDITOR_U.hasTextCell(currentCard.getQType())) {
                  editors.EDITOR_U.deleteTCellAction();
            }
            if ( ! editors.EDITOR_L.hasTextCell(currentCard.getAType())) {
                  editors.EDITOR_L.deleteTCellAction();

            }
            // Set the text for key words
            editors.EDITOR_U.setText(currentCard.getQText());
            editors.EDITOR_L.setText(currentCard.getAText());

      }

      /* ------------------------------------------------------- **/

      /**
       * Used in SnapShot to set edit mode.
       * @param bool
       */
      public void setFlashListChanged(boolean bool) {
            this.flashListChanged = bool;
      }

      /* ------------------------------------------------------- **/

      public void setListIdx(int idx) {
            this.listIdx = idx;
      }


      /* ------------------------------------------------------- **/

      /**
       * Disables all buttons for CreateFlash and SectionEditors.
       */
      public void disableButtons() {
            editors.EDITOR_L.disableEditorBtns();
            editors.EDITOR_U.disableEditorBtns();
            this.insertCardButton.setDisable(true);
            this.newCardButton.setDisable(true);
            this.prevQButton.setDisable(true);
            this.nextQButton.setDisable(true);
            this.resetDeckButton.setDisable(true);
            this.saveReturnButton.setDisable(true);
            this.deleteQButton.setDisable(true);
      }

      /* ------------------------------------------------------- **/

      private void disableEditors() {
            this.isDisabled = true;
            editors.EDITOR_L.disableEditorBtns();
            editors.EDITOR_U.disableEditorBtns();
            editors.EDITOR_L.tCell.getTextArea().setEditable(false);
            editors.EDITOR_U.tCell.getTextArea().setEditable(false);
            editors.EDITOR_U.tCell.getTextArea().setOnMouseClicked(e -> {
                  if (isDisabled) {
                        notifyEditorsDiabled();
                        entryComboBox.show();
                  }
            });
            editors.EDITOR_L.tCell.getTextArea().setOnMouseClicked(e -> {
                  if (isDisabled) {
                        notifyEditorsDiabled();
                        entryComboBox.show();
                  }
            });
            cfpCenter.setOnMouseClicked(e -> {
                  if (isDisabled) {
                        notifyEditorsDiabled();
                        entryComboBox.show();
                  }
            });
      }

      private void enableEditors() {
            this.isDisabled = false;
            editors.EDITOR_L.enableEditorBtns();
            editors.EDITOR_U.enableEditorBtns();
            editors.EDITOR_L.tCell.getTextArea().setEditable(true);
            editors.EDITOR_U.tCell.getTextArea().setEditable(true);
      }

      private void notifyEditorsDiabled() {
            String msg = "    Please set the \"CARD TYPE\"";
            FxNotify.notification("Oooph!", msg, Pos.CENTER, 7,
                "emojis/Flash_headexplosion_60.png", FlashMonkeyMain.getPrimaryWindow());
      }

      /* ------------------------------------------------------- **/

      /**
       * Enables all buttons for createFlash and SectionEditors.
       */
      public void enableButtons() {
            editors.EDITOR_L.enableEditorBtns();
            editors.EDITOR_U.enableEditorBtns();
            this.insertCardButton.setDisable(false);
            if (this.listIdx > 0) {
                  this.prevQButton.setDisable(false);
                  //this.undoQButton.setDisable(false);
            } else {
                  this.prevQButton.setDisable(true);
            }
            if (this.listIdx < creatorList.size() - 1) {
                  this.nextQButton.setDisable(false);

            }
            if(this.listIdx < FlashCardOps.getInstance().getFlashList().size()) {
                  this.undoQButton.setDisable(false);
            }
            this.resetDeckButton.setDisable(false);
            this.newCardButton.setDisable(false);
            this.saveReturnButton.setDisable(false);
            this.deleteQButton.setDisable(false);
      }

      /* ------------------------------------------------------- **/

      /**
       * Sets the buttons for CreatorList and returns a vBox containing
       * the buttons
       *
       * @return VBox containing the buttons for this pane
       */
      private VBox getCreatorButtons() {
            newCardButton = ButtoniKon.getNewCardButton();
            newCardButton.setMaxWidth(Double.MAX_VALUE);
            newCardButton.setOnAction(e -> newCardButtonAction());

            //  "Add" & "Save" Buttons in the south pane
            insertCardButton = ButtoniKon.getInsertCardButton();

            // Actions and buttons
            insertCardButton.setOnAction(e -> {
                  insertCardAction(listIdx);
                  editors.EDITOR_U.tCell.getTextArea().requestFocus();
            });

            // Save button & Action.
            // Exits createFlash scene and saves the card
            // media, images, and or shapes.
            saveReturnButton = ButtoniKon.getSaveDeckButton();
            saveReturnButton.setMaxWidth(Double.MAX_VALUE);
            saveReturnButton.setOnAction(e -> {
                  saveReturnAction();
            });
            // Abandon button & Action
            // Exits createFlash scene
            abandButton = ButtoniKon.getQuitChangesButton();
            abandButton.setOnAction(e -> abandAction());

            nextQButton = ButtoniKon.getCreateQNextButton();
            nextQButton.setMaxWidth(Double.MAX_VALUE);
            nextQButton.setOnAction(e -> nextQButtonAction());

            prevQButton = ButtoniKon.getCreateQPrevButton();
            prevQButton.setMaxWidth(Double.MAX_VALUE);
            prevQButton.setOnAction(e -> prevQButtonAction());

            undoQButton = ButtoniKon.getUndoChangesButton();
            undoQButton.setMaxWidth(Double.MAX_VALUE);
            undoQButton.setOnAction(e -> undoQButtonAction(currentCard));

            deleteQButton = ButtoniKon.getDeleteCardButton();
            deleteQButton.setMaxWidth(Double.MAX_VALUE);
            deleteQButton.setOnAction(e -> {
                 boolean b =  deleteQButtonAction(currentCard);
                 if ( ! b ) {
                       return;
                 }
            });
            resetDeckButton = ButtoniKon.getResetOrderButton();
            resetDeckButton.setOnAction(e -> FlashCardOps.getInstance().resetPerformance());

            metaButton = ButtoniKon.getSellButton();
            metaButton.setMaxWidth(Double.MAX_VALUE);
            metaButton.setMaxHeight(Double.MAX_VALUE);
            metaButton.setOnAction(e -> metaButtonAction());

            // Organize the button boxes and sellButton into left and right
            GridPane navGrid = new GridPane();
            // Navigation
            HBox navH = new HBox(4);
            navH.getChildren().addAll(prevQButton, nextQButton);
            HBox.setHgrow(prevQButton, Priority.ALWAYS);
            HBox.setHgrow(nextQButton, Priority.ALWAYS);
            navGrid.add(navH, 0, 0, 5, 1);
            // Bind nav width to BorderPane width
            cfpCenter.widthProperty().addListener((obs, oldval, newVal) -> navH.setPrefWidth(newVal.doubleValue()));

            VBox southBox = new VBox(10);

            southBox.setAlignment(Pos.CENTER);
            VBox southBPane = getToolBoxContainer();
            southBPane.setAlignment(Pos.CENTER);
            southBox.getChildren().addAll(navGrid, southBPane);

            return southBox;
      }

      /* ------------------------------------------------------- **/

      public void saveReturnAction() {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            saveOnExit();
            onClose();
      }

      /**
       * Deck save action for this class that is called when FlashMonkey
       * is closed. May be used outside of the class. Single point call
       * that is always called when class is closed. Should be implemented
       * by any class that contains files to be saved to file or the cloud.
       * E.g. ReadFlash and CreateFlash.
       * Calls child action classes, e.g. drawtools "saveOnExit()" before saving
       * at this level.
       */
      @Override
      public void saveOnExit() {

            // SHAPES
            try {
                  if( DrawTools.instanceExists()) {
                        DrawTools.getInstance().saveOnExit();
                  }

                  Timer.getClassInstance().createTestTimeStop();
                  saveDeckAction();
                  creatorList.clear();
            } catch (Exception e) {
                  LOGGER.warn(e.getMessage());
                  e.printStackTrace();
            }
      }

      /* ------------------------------------------------------- **/

      /**
       * User is asked if they want to abandon all changes before exiting. If
       * false, the user is returned to the previous screen.
       */
      private void abandAction() {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            // If greater than the flashList size & deck hasChanged or the currentCard is not equal to the original
            if ( flashListChanged ||  0 == cardEqualsOriginal(currentCard)) {
                  FMAlerts alert = new FMAlerts();
                  String msg = "Delete ALL changes to this deck\n\nAre you Sure?";
                  int res = alert.choiceOptionActionPopup(
                      "Hmmmmm!", msg,
                      "emojis/flashFaces_sunglasses_60.png",
                      UIColors.FM_RED_WRONG_OPAQUE,
                      "DELETE CHANGES",
                      "SAVE CHANGES");
                  if (res == 1) {
                        FlashMonkeyMain.setWindowToMenu();
                        onClose();
                  } else {
                        // Send User back
                        return;
                  }
            }
            FlashMonkeyMain.setWindowToMenu();
            //return;
      }

      /* ------------------------------------------------------- **/

      /**
       * Closes Create Flash. Clears the tree. Closes the treeWindow. Does NOT save
       * the current work.
       */
      @Override
      public void onClose() {
//            if(HashTagEditorPopup.instanceExists()) {
//                  HashTagEditorPopup.getInstance().onClose();
//            }
            if( DrawTools.instanceExists() ) {
                  DrawTools.getInstance().onClose();
            }
            if (metaWindow.isShowing()) {
                  closeMetaWindow();
            }
            FlashMonkeyMain.treeWindow.close();

            Timer.getClassInstance().createTestTimeStop();
            LOGGER.info("createTestTime: {}", Timer.getClassInstance().getCreateTestsTime());
            creatorList.clear();
            //return;
      }

      /* ------------------------------------------------------- **/

      private VBox getToolBoxContainer() {
            Pane leftSpacer = new Pane();
            Pane rightSpacer = new Pane();
            HBox.setHgrow(leftSpacer, Priority.SOMETIMES);
            HBox.setHgrow(rightSpacer, Priority.SOMETIMES);
            ToolBar tBar = new ToolBar( leftSpacer, newCardButton, undoQButton, deleteQButton, new Separator( Orientation.VERTICAL ),
                resetDeckButton, saveReturnButton, abandButton, new Separator( Orientation.VERTICAL ),
                metaButton, rightSpacer );
            tBar.setStyle("-fx-background-color: TRANSPARENT");
            VBox vBox = new VBox();
            vBox.getChildren().add(tBar);
            vBox.setAlignment(Pos.CENTER);

            return vBox;
      }

      /* ------------------------------------------------------- **/

      /**
       * Deletes the card if called by a navigation button, or by the
       * choiceActionPopup for non-complete data
       * created by a navigation button non-complete data error.
       * Creates a popuup action pane and asks the user if they
       * indended to delete the card.
       * @param cc The current card.
       */
      private boolean deleteQButtonAction(FlashCardMM cc) {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            // send message to user and check that they
            //intend to delete the card
            final FlashCardMM ccRef = cc;
            boolean b = deletePopup();
            if (b) {
                  deleteCardAction(ccRef);
                  // buildTree and re-order deck
                  deleteClearHelper();
                  return true;
            } else {
                  // Send User back
                  return false;
            }
      }


      private boolean clearQButtonAction() {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            // send message to user and check that they
            // intend to delete the card
            boolean b = clearPopup();
            if(b) {
                  clearQOptionAction();
                  // buildTree and re-order deck
                  deleteClearHelper();
                  return true;
            } else {
                  // Send User back
                  return false;
            }
      }


      private void deleteClearHelper() {
            FlashCardOps fcOps = FlashCardOps.getInstance();
            fcOps.buildTree(creatorList);
            // set currentCard to the current listIdx, handle
            // last card problem.
            if(( creatorList.size() - 1 ) <= listIdx ) {
                  listIdx = creatorList.size()  - 1;
            }
            currentCard = creatorList.get(listIdx);
            fcOps.refreshTreeWalker(creatorList);
            if (!FlashMonkeyMain.treeWindow.isShowing()) {
                  FlashMonkeyMain.buildTreeWindow();
            }
            FlashMonkeyMain.AVLT_PANE.displayTree();
            // set sectionEditors to the currentCard
            setSectionEditors(currentCard);
      }


      /* ------------------------------------------------------- **/

      /**
       * Called when delete is from AVLTree in response to dialogue delete
       * action.Differntiates from deleteQButton in that the currentCard, and
       * listIdx and sectionEditors are set to the selected node in the tree rather
       * than the current listIdx.
       * @return
       */
      private boolean deleteQOptionAction() {
            // send message to user and check that they
            // intend to delete the card
            boolean b = deletePopup();
            if (b) {
                  // delete the card
                  deleteCardAction(currentCard);
                  // build the tree and re-order the deck
                  FlashCardOps fcOps = FlashCardOps.getInstance();
                  // after delete, rebuild the tree.
                  fcOps.refreshTreeWalker(creatorList);
                  fcOps.buildTree(creatorList);

            }
            return b;
      }

      private boolean clearQOptionAction() {
            // send message to user and check that they
            // intend to delete the card
            boolean b = clearPopup();
            if (b) {
                  // clear the editors by resetting to new.
                  editors = new Editors(new SectionEditor( "", makeQPrompt(listIdx), 'U', this.currentCard.getCID() ),
                      new SectionEditor("", "Enter the answer here", 'L', this.currentCard.getCID()) );
            }
            return b;
      }


      /* ------------------------------------------------------- **/


      private boolean deletePopup() {
            FMAlerts alert = new FMAlerts();
            String msg = "Delete this card\n\nAre you sure?";

           return alert.choiceOnlyActionPopup(
                "DELETE THIS CARD", msg,
                "emojis/flashFaces_sunglasses_60.png",
                UIColors.ICON_ELEC_BLUE,
                   null);
      }


      /* ------------------------------------------------------- **/

      private boolean clearPopup() {
            FMAlerts alert = new FMAlerts();
            String msg = "This clears the data in this card." +
                "\nIt will not be saved." +
                "\n\nAre you sure?";

            return alert.choiceOnlyActionPopup(
                "CLEAR THIS CARD", msg,
                "image/Flash_hmm_75.png",
                UIColors.ICON_ELEC_BLUE,
                    null);
      }

      /* ------------------------------------------------------- **/

      void newCardButtonAction() {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            // Check content and save, or give user
            // an error message. Respond based on user
            // choice.
            boolean ret = cardOnExitActions(false, true);
            if (ret) {
                  return;
            }
            editors.EDITOR_U.tCell.getTextArea().requestFocus();
      }

      /* ------------------------------------------------------- **/

      public void closeMetaWindow() {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            if (this.metaWindow != null && this.metaWindow.isShowing()) {
                  this.metaWindow.close();
                  DeckMetaData.getInstance().close();
            }
      }

      /* ------------------------------------------------------- **/

      // The "describe this deck" button
      private void metaButtonAction() {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            LOGGER.debug("sellButtonAction clicked");
            // @TODO make metaPane local variable
            if(Utility.isConnected()) {
                  if (metaWindow != null) {
                        metaPane = new DeckMetaPane();
                        metaWindow = new Stage();
                  }
                  if (!meta.getDataMap().isEmpty()) {
                        meta.getDataMap().clear();
                  }
                  if (metaPane != null) {
                        metaPane = new DeckMetaPane();
                  }
                  meta = DeckMetaData.getInstance();

                  Scene scene = new Scene(metaPane.getMainPane());
                  scene.getStylesheets().addAll("css/buttons.css", "css/fxformStyle.css");

                  metaWindow.setScene(scene);
                  metaWindow.setAlwaysOnTop(true);
                  metaWindow.show();
            } else {
                  String msg = "I didn't connect to the ecosystem. " +
                          "\nTo upload and make this deck available " +
                          "\nplease check the internet connection and" +
                          "\ntry again.";
                  FxNotify.notification("Ooops", msg, Pos.CENTER, 15,
                          "image/flashFaces_sunglasses_60.png", FlashMonkeyMain.getPrimaryWindow());
                  metaButton.setDisable(true);
            }

      }


      /* ------------------------------------------------------- **/

/*      public static final void resetSectionEditors() {
            editorU = new SectionEditor();
            editors.EDITOR_L = new SectionEditor();
      }*/


      /* ------------------------------------------------------- **/

      /**
       * Sets the UI to the next card for
       * editing
       */
      private void nextQButtonAction() {
            //SoundEffects.PRESS_BUTTON_COMMON.play();
            LOGGER.info("called nextQButtonAction");
            // Check content and save, or give user
            // an error message. Respond based on user
            // choice.
            boolean moveOK = cardOnExitActions(false, false);
            if ( ! moveOK) {
                  return;
            }

            // increment the cardIdx and for
            // setting fileNames to each card
            listIdx++;

            if (listIdx > -1 && listIdx < creatorList.size()) {
                  // set the current cards data into the fields
                  FlashCardMM currentCard = creatorList.get(listIdx);
                  // set section editors to current card
                  setSectionEditors(currentCard);
                  FMTWalker.getInstance().setCurrentNodeReferance(currentCard);
                  if ( ! FlashMonkeyMain.getPrimaryWindow().isFullScreen()) {
                        FlashMonkeyMain.AVLT_PANE.displayTree();
                  }
            }

            // Enable buttons according to idx
            navButtonDisplay(listIdx);

            String insertBtnMsg = "Insert a new card at " + (listIdx + 1);
            insertCardButton.setTooltip(new Tooltip(insertBtnMsg));
      }


      /* ------------------------------------------------------- **/


      /**
       * Sets the UI to the previous existing card for
       * editing
       */
      private void prevQButtonAction() {
            //SoundEffects.PRESS_BUTTON_COMMON.play();
            LOGGER.info("prevQButtonAction called");

            String insertBtnMsg = "Insert a new card at " + listIdx;
            insertCardButton.setTooltip(new Tooltip(insertBtnMsg));

            // Check content and save, or give user
            // an error message. Respond based on user
            // choice.
            boolean moveOK = cardOnExitActions(false, false);
            if ( ! moveOK) {
                  return;
            }
            // decrement the cardNumber for setting fileNames to each card
            int oldIdx = listIdx;
            listIdx--;

            // set the UI to the current card
            if (listIdx > -1) {
                  FlashCardMM currentCard = creatorList.get(listIdx);
                  // set section editors to current card.
                  setSectionEditors(currentCard);
                  // display the current node in the tree
                  FMTWalker.getInstance().setCurrentNodeReferance(currentCard);
                  if ( ! FlashMonkeyMain.getPrimaryWindow().isFullScreen()) {
                        FlashMonkeyMain.AVLT_PANE.displayTree();
                  }
            }
            // enable buttons according to idx
            LOGGER.info("listIdx: <{}>", listIdx);
            navButtonDisplay(listIdx);
      }


      /* ------------------------------------------------------- **/


      private void navButtonDisplay(final int idx) {
            LOGGER.debug("Idx: {}", idx);
            if (idx < 1) {
                  prevQButton.setDisable(true);
                  if( FlashCardOps.getInstance().getFlashList().size() == 0) {
                        this.undoQButton.setDisable(true);
                  } else {
                        this.undoQButton.setDisable(false);
                  }
                  //insertCardButton.setDisable(true);
            } else if (idx > (creatorList.size() - 2)) {
                  nextQButton.setDisable(true);
                  prevQButton.setDisable(false);
                  //insertCardButton.setDisable(false);
                 // if(flashListChanged) { undoQButton.setDisable(true); }
                  deleteQButton.setDisable(true);
                  if(this.listIdx > FlashCardOps.getInstance().getFlashList().size() - 1) {
                        this.undoQButton.setDisable(true);
                  } else {
                        this.undoQButton.setDisable(false);
                  }
            } else {
                  metaButton.setDisable(false);
                  prevQButton.setDisable(false);
                  nextQButton.setDisable(false);
                  deleteQButton.setDisable(false);
                  insertCardButton.setDisable(false);
                  if(this.listIdx > FlashCardOps.getInstance().getFlashList().size() - 1) {
                        this.undoQButton.setDisable(true);
                  } else {
                        this.undoQButton.setDisable(false);
                  }
            }
      }

      /**
       * Sets the next and last button visibility for the treeMap and readPane
       * helper method for setCard()
       * NOTE: Expects lowest and highest children are current.
       *
       * @param node Node that is refered to for buttons.
       */
      public  void buttonDisplay(FMTWalker.Node node) {
            LOGGER.debug("\n ***** in buttonTreeDisplay() ****");

            FlashCardMM fc = (FlashCardMM) node.getData();
            int idx = fc.getANumber();
            navButtonDisplay(idx);
      }


      /* ------------------------------------------------------- **/


      private void getKeyboardShortcuts() {
            // Keyboard shortcut, Moves between text fields
            editors.EDITOR_U.tCell.getTextArea().setOnKeyPressed((KeyEvent e) -> {
                  if (e.isAltDown() || e.isShiftDown()) {
                        if (e.getCode() == KeyCode.ENTER) {
                              // Check content and save, or give user
                              // an error message. Respond based on user
                              // choice.
                              boolean ret = cardOnExitActions(false, isMoveFromEndCard());
                              if (ret) {
                                    return;
                              }
                        }
                  }
            });
            // Keyboard shortcut, Moves between text fields
            editors.EDITOR_L.tCell.getTextArea().setOnKeyPressed((KeyEvent e) -> {
                  if (e.isAltDown() || e.isShiftDown()) {
                        if (e.getCode() == KeyCode.ENTER) {
                              // Check content and save, or give user
                              // an error message. Respond based on user
                              // choice.
                              boolean ret = cardOnExitActions(false, isMoveFromEndCard());
                              if (ret) {
                                    return;
                              }
                        }
                  }
            });
      }

      /* ------------------------------------------------------- **/

      /**
       * Returns this masterPane
       *
       * @return
       */
      public VBox getMasterPane() {
            return this.masterPane;
      }


      /* ------------------------------------------------------- **/


      /**
       * Checks if the last card question has either
       * text or files.length {@code >} 0;
       *
       * @return true if the last cards question has data.
       */
      private boolean lastCardQHasData() {
            FlashCardMM lastCard = creatorList.get(creatorList.size() - 1);
            return !lastCard.getQText().isEmpty() || lastCard.getQFiles().length > 0;
      }

      /* ------------------------------------------------------- **/

      /**
       * If the comboBox has a testType selected it returns true
       * otherwise it returns false and a pop-up error message
       * is displayed.
       *
       * @return
       */
      private boolean checkComboBox() {
            if (entryComboBox.getValue() == null) {
                  String emojiPath = "emojis/flashFaces_sunglasses_60.png";
                  String message = "  Ooops! \n  Please select a test type  ";
                  int w = 400; // (int) editors.EDITOR_U.textEditor.getTextCellVbox().getWidth();
                  double x = editors.EDITOR_U.sectionHBox.getScene().getWindow().getX() + (w / 8);
                  double y = editors.EDITOR_U.sectionHBox.getScene().getWindow().getY() + 50;
                  FMPopUpMessage.setPopup(
                      x,
                      y,
                      w,
                      message,
                      emojiPath,
                      FlashMonkeyMain.getPrimaryWindow(),
                      3000
                  );
                  return false;
            }
            return true;
      }

      /* ------------------------------------------------------- **/

      private void insertCardAction(int prevCNum) {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            LOGGER.info("\n *** insertCardAction() *** \n");
            LOGGER.debug("\t creatorList size: {}", creatorList.size() + 1);

            boolean ret = cardOnExitActions(false, false);
            if (ret) {
                  return;
            }

            // create new card for next round.
            currentCard = new FlashCardMM();
            // insert new card at the current location
            creatorList.add(listIdx, currentCard);
            currentCard.setCNumber(listIdx);

            FlashCardOps fcOps = FlashCardOps.getInstance();
            fcOps.buildTree(creatorList);
            fcOps.refreshTreeWalker(creatorList);
            if (!FlashMonkeyMain.treeWindow.isShowing()) {
                  FlashMonkeyMain.buildTreeWindow();
            }
            FlashMonkeyMain.AVLT_PANE.displayTree();
      }


      /* ------------------------------------------------------- **/

      /**
       * Deletes the card provided in the parameter.
       * Deleting and adding cards from the center of the list creates numbering issues.
       * To solve this issue, we begin with the listIdx which may not be accurate.
       * A search is conducted to find the cID that matches the delete card.
       * If it is not at the listIdx. Search uses
       * the binary search method to find the correct index. Once found it then checks
       * the found cards CID. Does not change the currentCard. Does not re-order the
       * deck. Just deletes the card from the creatorList.
       *
       * @param currentCard Card to be deleted.
       */
      private void deleteCardAction(FlashCardMM currentCard) {
            if(creatorList.size() < 1) {
                  return;
            }
            setFlashListChanged(true);

            // The closest reliable number to this cards
            // location in the current deck.
            String delCID = currentCard.getCID();
            int fwdIdx = listIdx;
            int revIdx = listIdx - 1;

            LOGGER.info("called");
            // niche case of delete first card
            if(fwdIdx == 0 && creatorList.get(fwdIdx).getCID().equals(delCID)) {
                  ListIterator<FlashCardMM> iterator = creatorList.listIterator(fwdIdx);
                  iterator.next();
                  iterator.remove();
                  //listIdx = fwdIdx - 1;
                  //currentCard = creatorList.get(moveToIdx);
                  //setSectionEditors(currentCard);
            }

            while (fwdIdx < creatorList.size() && revIdx > -1) {

                  // if the forwardIndex finds the delete cardID then :
                  if (fwdIdx < creatorList.size() && creatorList.get(fwdIdx).getCID().equals(delCID)) {
                        ListIterator<FlashCardMM> iterator = creatorList.listIterator(fwdIdx);
                        iterator.next();
                        iterator.remove();
                        //listIdx = fwdIdx - 1;
                        //currentCard = creatorList.get(listIdx);
                        //setSectionEditors(currentCard);

                        break;
                  }
                  if (revIdx > 1 && creatorList.get(revIdx).getCID().equals(delCID)) {
                        ListIterator iterator = creatorList.listIterator(revIdx);
                        iterator.next();
                        iterator.remove();
                        //listIdx = revIdx - 1;
                        //currentCard = creatorList.get(listIdx);
                        //setSectionEditors(currentCard);

                        break;
                  }
                  fwdIdx++;
                  revIdx--;
            }
      }

      /* ------------------------------------------------------- **/

      /**
       * Reverts the current card shown in the users display
       * to it's original version.
       * <p><b>NOTES:</b> Due to adding and deleting cards, the index
       * that this cards number originated from may no longer
       * be correct. Thus, this method conducts a search starting at
       * the original index and working from there in both directions.</p>
       * Algorithm
       * <pre>
       *      1) Get this card's aNumber
       *      2) find this card in creatorList starting at this aNumber
       *      3) change found card in creatorList to the flashlist(aNumber)
       *  </pre>
       *
       * @param currentCard
       */
      private void undoQButtonAction(FlashCardMM currentCard) {

            LOGGER.debug("undoQButtonAction called");

            //FlashCardOps fcOps = FlashCardOps.getInstance();
            // The closest reliable number to this cards
            // location in the current deck.
            final ArrayList<FlashCardMM> copyFlashList = FlashCardOps.getInstance().getFlashList();
            int aNum = currentCard.getANumber();
            int fwdIdx = aNum;
            int revIdx = aNum - 1;

            // finding the card
            while (fwdIdx < copyFlashList.size() && revIdx > -1) {
                  if (fwdIdx < copyFlashList.size() && copyFlashList.get(fwdIdx).getANumber() == aNum) {

                        creatorList.set(fwdIdx, copyFlashList.get(aNum));
                        fwdIdx++;
                        break;
                  } else if (revIdx > -1 && copyFlashList.get(revIdx).getANumber() == aNum) {

                        creatorList.set(revIdx, copyFlashList.get(aNum));
                        revIdx--;
                        break;
                  }
                  fwdIdx++;
                  revIdx--;
            }

            setSectionEditors(creatorList.get(listIdx));
      }

      /**
       * <p>This method helps to ensure conformance with data completion.</p>
       * <p> Provides a single point for the actions needed when a card is closed.
       * IE when a card is simply displayed vs edited.
       * The underlying process checks that the card is complete, provides
       * a choice message if iti s not. If the card is complete and it was edited
       * then it saves the card with no popup. If the card is new and complete, it saves the
       * card with no popup. If the card was not edited, it does not save the card
       * and there is no popup. Incomplete cards always create a popup.</p>
       * <p><b>NOTE:</b>Uses the underlying objects equal methods and
       * compares the currentCard from the CreatorList with the corresponding
       * card in the FlashCardOps.FlashList. </p>
       * <p><b>NOTE:</b> This is a heavy processing action due to the amount
       * of work being done.</p>
       * <p>Helps to ensure that decks are complete or reminds the creator
       * that there are incomplete cards.  Also important for the ecosystem.</p>
       *
       * @return Returns a true if it is safe to move to the next card, and a
       * false if the upstream navigation method should not move to the
       * next card.
       */
      public boolean cardOnExitActions(boolean fromTree, boolean moveForward) {
            // verify data completeness
            closeDrawToolsOnTreeClick( editors.EDITOR_U, editors.EDITOR_L);
            CardSaver cs = new CardSaver();
            int result = CardSaver.checkContent();
            // Check response. Popup error with buttons, return
            // an integer for next action
            int status = cs.statusResponse(result);
            boolean safeToMove;
            switch (status) {
                  // user requests delete
                  case -2: {
                        if(fromTree) {
                              // detect last card problem, we do not delete last card,
                              // just clear it.
                              //System.out.println("FlashList size: " + creatorList.size() );
                             if(listIdx != creatorList.size() - 1) {
                                    safeToMove = deleteQOptionAction();
                             } else {
                                    safeToMove = clearQOptionAction();
                             }
                        } else {
                              if(listIdx != creatorList.size() - 1) {
                                    safeToMove = deleteQButtonAction(currentCard);
                              } else {
                                    safeToMove = clearQButtonAction();
                              }
                        }

                        break;
                  }
                  // user requests to edit the card
                  // return false and send user back
                  case -1: {
                        safeToMove = false;
                        break;
                  }
                  case 1: {
                        // save actions:
                        // Check if the card has been changed by comparing with
                        // the corresponding card in the FlashCardOps.FlashList.
                        // If the card is not a new card, and it is equal, then just return
                        // true.
                        int opsFlashSize = FlashCardOps.getInstance().getFlashList().size();
                        //LOGGER.debug("\n\n**** printing Current Card **** \ncounter: {}\n{}", counter++,  currentCard);
                        LOGGER.debug("listIdx: {} is less than Ops FlashList.size: {} == {}",  listIdx, opsFlashSize, listIdx < opsFlashSize );
                        boolean inRange = listIdx < opsFlashSize && listIdx > -1;
                        if (inRange && 1 == cardEqualsOriginal(currentCard)) {
                              System.out.println("no change detected. not saving\n ////////////////////////\n");
                              // No changes, continue to next card
                              safeToMove = true;
                              break;
                        }
                        else if (listIdx < creatorList.size() - 1 && listIdx > -1) {
                              // save if in range
                              cs.saveEdtdCardInCreator(currentCard);
                        }
                        else {
                              if(moveForward) {
                                    cs.addNewCardForNewBtnAction();
                              } else {
                                    cs.addNewCardForPrevBtnAction();
                              }
                        }
                        safeToMove = true;
                        break;
                  }
                  default:
                  case 0: {
                        // There is no content. Do not save, just move on.
                        safeToMove = true;
                        break;
                  }
            }

             return safeToMove;
      }

      // *********************                   ************************* //

      /**
       * If the listIdx is equal to the last card of the creatorList.
       * @return True if the listIdx is set to the last card of the
       * creatorList.
       */
      public boolean isMoveFromEndCard() {
            return listIdx == creatorList.size() -1;
      }


      // ********************* INNER CLASS ************************* //

      private class CardSaver {
            CardSaver() { /* empty no args constructor */}


            /**
             * Adds flashCard
             * to the creatorList arrayList. Send media to the cloud. Increment
             * the listIdx, add blank card to end of creatorList.
             */
            private void addNewCardForNewBtnAction() {
                  setFlashListChanged(true);
                  LOGGER.info("\n *** addCardAction() *** \n");
                  LOGGER.debug("\n\t saved as qID, The qID: {}", currentCard.getCID());
                  LOGGER.debug("\t cNum {}", currentCard.getCNumber());
                  LOGGER.debug("\t creatorList size: {}", creatorList.size() + 1);
                  // Order is important for proper display of
                  // card number.
                  // update the listIdx
                  listIdx++;
                  // Save this card to the creator list
                  saveNewCardToCreator(listIdx);
                  addBlankFlashCardToCreator();
                  // Send this cards media.
                  // runs using executor service. On separate thread.
                  sendAndCheck();
                  // set the prompt
                  editors.EDITOR_U.styleToPrompt();
                  editors.EDITOR_U.setPrompt(makeQPrompt(listIdx));
                  editors.EDITOR_U.tCell.getTextArea().requestFocus();

                  insertCardButton.setText("New card");

                  navButtonDisplay(listIdx);
                  // display in the nav tree
                  if(creatorList.size() > 0) {
                        FlashCardOps.getInstance().buildTree(creatorList);
                        if (!FlashMonkeyMain.treeWindow.isShowing()) {
                              FlashMonkeyMain.buildTreeWindow();
                        }
                        FlashMonkeyMain.AVLT_PANE.displayTree();
                  }
            }

            /**
             * intended for saving a card when the previous button is pressed
             * by the user. This handles the niche problem of adding a card
             * at the end of the list when the previous button is clicked. Does not
             * increment the listIdx. Does not add a blank card to the end of
             * the creator list.
             */
            private void addNewCardForPrevBtnAction() {
                  setFlashListChanged(true);
                  saveNewCardToCreator(listIdx);
                  sendAndCheck();
                  // set the prompt
                  editors.EDITOR_U.styleToPrompt();
                  editors.EDITOR_U.setPrompt(makeQPrompt(listIdx));
                  editors.EDITOR_U.tCell.getTextArea().requestFocus();
                  insertCardButton.setText("New card");
                  navButtonDisplay(listIdx);
                  // display in the nav tree
                  if(creatorList.size() > 0) {
                        FlashCardOps.getInstance().buildTree(creatorList);
                        if (!FlashMonkeyMain.treeWindow.isShowing()) {
                              FlashMonkeyMain.buildTreeWindow();
                        }
                        FlashMonkeyMain.AVLT_PANE.displayTree();
                  }
            }

            /**
             * Helper method to addCardForNewAction and addCardForPrevAction.
             * Sends media and deck changes to cloud.
             */
            private void sendAndCheck() {
                  //Thread.dumpStack();
                  ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(3);
                  AtomicInteger count = new AtomicInteger(); // increased to 5, for bad networks.
                  AtomicBoolean bool = new AtomicBoolean(false);
                  if (Utility.isConnected()) {
                        String[] uploads = mediaMergeUnique(editors.EDITOR_U.getMediaNameArray(), editors.EDITOR_L.getMediaNameArray());
                        Runnable task = () -> {
                              count.getAndIncrement();
                              if (!sendMedia(uploads) || count.get() >= 5) {
                                    scheduledExecutor.shutdownNow();
                        /*if (count.get() >= 5) {
                            String errorMessage = " Not all media was uploaded. \nPlease check if your connected to the internet.";
                            FxNotify.notificationDark("Ooops!", errorMessage, Pos.CENTER, 6,
                                    "image/flashFaces_sunglasses_60.png", FlashMonkeyMain.getWindow());
                        }*/
                              }
                        };
                        scheduledExecutor.scheduleWithFixedDelay(task, 0, 3, TimeUnit.SECONDS);
                  }
            }

            private boolean sendMedia(String[] uploads) {
                  // upload
                  if (uploads.length == 0) {
                        return true;
                  }
                  CloudOps.putMedia(uploads);
                  // check
                  String[] tryAgain = CloudOps.checkExistsInS3(uploads, FlashCardOps.getInstance().getDeckFileName());
                  // if list is not 0 try to upload 5 more times,
                  // if fails, send message to user.
                  int tries = 0;
                return tryAgain == null || tryAgain[0] == null;
            }

            /**
             * adds the mediaFiles from upper and lower SectionEditors into a
             * single array. If The upper and lower media elements are the same
             * image, returns only one image.
             *
             * @param uAry
             * @param lAry
             * @return returns all of the mediafiles from upper and lower section
             * editors. If an array contains no elements, will return an array with the first element as null.
             * thus, this method may return null elements.
             */
            private String[] mediaMergeUnique(String[] uAry, String[] lAry) {
                  ArrayList<String> strAry = new ArrayList<>();
                  if (uAry[0] != null) strAry.add(uAry[0]);
                  if (uAry.length == 2 && uAry[1] != null) strAry.add(uAry[1]);
                  if (lAry[0] != null && !lAry[0].equals(uAry[0])) strAry.add(lAry[0]);
                  if (lAry.length == 2 && lAry[1] != null) strAry.add(lAry[1]);
                  return strAry.toArray(new String[strAry.size()]);
            }

            /**
             * Adds a new card to the end of the creator list. Numbering is
             * updated when a deck is uploaded from a file.
             * <p>
             * Assumes that the card is complete
             */
            private void saveNewCardToCreator(int paramIdx) {
                  LOGGER.info("\n *** IN SAVE NEW CARD TO CREATOR *** \n");
                  //if(checkContent(editors.EDITOR_U, editors.EDITOR_L) == 1) {
                  // Assign currentTest from combobox
                  GenericTestType gT = entryComboBox.getValue().getTestType();
                  // card at end of list, or ??
                  int cardNum = paramIdx;
                  // Create a new FlashCardMM
                  currentCard.setAll(
                      cardNum * 10,        // Not super important until later. cardNumbers end as a multiple of ten. Used to set a card in the Tree
                      gT.getTestType(),           // BitSet TESTS
                      gT.getCardLayout(),         // cardLayout
                      editors.EDITOR_U.getText(),          // questionText
                      editors.EDITOR_U.getMediaType(),     // question Section Layout 'M', Also the media type
                      editors.EDITOR_U.getMediaNameArray(),    // String[] of media files
                      editors.EDITOR_L.getText(),          // answerText
                      cardNum,                    // Not super important until later. answerNumber is a cards index in FlashList
                      editors.EDITOR_L.getMediaType(),     // answer section layout
                      editors.EDITOR_L.getMediaNameArray(),    // String[] of media files
                      new ArrayList<Integer>()    // Arraylist of other correct answers
                  );

                  LOGGER.info("added to creatorList, size is: " + creatorList.size());
                  LOGGER.debug("\n\n****currentCard ****\n{}\n\n ***************************", currentCard.toString());

                  gT.reset();
            }


            /* ------------------------------------------------------- **/

            private void addBlankFlashCardToCreator() {
                  currentCard = new FlashCardMM();
                  creatorList.add(currentCard);
                  setSectionEditors(currentCard);
            }

            /* ------------------------------------------------------- **/


            /**
             * Saves edits to the existing card in the creatorList and s3.
             *
             * @param currentCard
             */
            private void saveEdtdCardInCreator(FlashCardMM currentCard) {
                  setFlashListChanged(true);
                  LOGGER.debug(" *** CALLED SAVE EDITS IN CREATOR *** ");
                  // Assign currentTest from combobox
                  GenericTestType gA = entryComboBox.getValue().getTestType();
                  currentCard.setTestType( gA.getTestType() );
                  currentCard.setCardLayout( gA.getCardLayout() );
                  // do not change the card number here unless
                  // the user re-orders the deck.
                  // currentCard.setCNumber(idx);
                  currentCard.setQText(editors.EDITOR_U.getText());
                  currentCard.setAText(editors.EDITOR_L.getText());
                  currentCard.setQType(editors.EDITOR_U.getMediaType());
                  currentCard.setAType(editors.EDITOR_L.getMediaType());
                  // saves the double array as is
                  currentCard.setQFiles(editors.EDITOR_U.getMediaNameArray());
                  // saves the double array as is
                  currentCard.setAFiles(editors.EDITOR_L.getMediaNameArray());
                  // Save to s3
                  // and verify files were uploaded. Else
                  // display an error to the user.
                  // Runs on a seperate thread
                  sendAndCheck();
            }


            /**
             * <p>Provides an int and response based on the int returned from
             * checkContent().</p>
             * <pre>
             *     - A '0'  = Do nothing and continue.
             *     - A '-1' = Send user back 'return' to previous method.
             *     - A '1'  = Save and continue.
             *     - A '-2' = Delete this card and continue.
             *     - If the param is LESS than 2, it retuns a '0' and provides no error message;
             *     - If param is 3 gives user an error message for missing data.
             *     - Otherwise: If the param is even, it posts a 'MISSING_TESTTYPE' and returns a '-1'
             *     or '1' based on the users response.
             *     - If the testType is a singleSection card, and the param is equal to 3,
             *     it returns a '0'.
             *     - If the param is equal to 7, it is a complete double section card and it
             *     returns a '0'.
             *     - if the param is anything else it posts a MISSING_SAVE error message to
             *     the user and returns true;
             *     - if DrwwTools is open save the shapes.
             * </pre>
             *
             * @param result return Returns an int
             */
            private static int statusResponse(int result) {
                  LOGGER.debug("statusResponse(...) param result: <{}>", result);

                  // There is no content but TestType may be set.
                  if (result < 2) {
                        return 0;
                  }
                  if (result == 3) {
                        System.out.println("in CreateFlash.statusResponse and result == 3");
                        // data is not completed in answer. Create Popup choice
                        // and return the users response.
                        return notComplete('a');
                  }
                  // if testType is not set and
                  // there is data in the card.
                  if (result % 2 != 1) {
                        return noTestType();
                  }
                  // Is a single section card and card is complete.
                  if (getTestType().toString().contains("NoteTaker") && result == 3) {
                        return 1;
                  }
                  // Card is double section and complete
                  if (result == 7) {
                        return 1;
                  }

                  //Card is not-complete error. Notify user
                  return notComplete('q');
            }

            /**
             * Creates a popup with user response. Three options.
             * Close the popup, - Signifys go back and edit. Return 0;
             * Save button - return 1.
             * Delete button - return -2;
             *
             * @param qOrA
             * @return
             */
            private static  int notComplete(char qOrA) {
                  FMAlerts alerts = new FMAlerts();
                  return alerts.choiceOptionActionPopup("Delete or Save",
                      qOrA == 'a' ? FMAlerts.MISSING_ANSWER : FMAlerts.MISSING_QUESTION,
                      "emojis/flash_mustache_75.png",
                      "",
                      "SAVE",
                      "DELETE"
                  );
            }

            /**
             * Creates a popup with user response. Two options, delete or close
             * and edit.
             *
             * @return
             */
            private static int noTestType() {
                  FMAlerts alerts = new FMAlerts();
                  boolean stop = alerts.saveAlertAction(FMAlerts.MISSING_TESTTYPE, "DELETE", "emojis/flash_mustache_75.png");
                  if (stop) {
                        // user requested to delete the card
                        return -2;
                  }
                  // If user closes the box or chooses to make a change
                  // send back to edit the card
                  return -1;
            }

            /* ------------------------------------------------------- **/

            /**
             * Sets bits based on if content exists.
             * Checks if the upper editor, and the lower editor if exists,
             * either contains data, has no data, or the card is
             * incomplete. There are no error messages generated here.
             *
             * @return Returns 0 if the card contains no data. returns an even number
             * if the testType comboBox is not set, returns a 2 or 3 if editorU has
             * data and editorL does not, and 7 if everything has data and it is not a single card. and returns
             * 4 or 5 if editorL has data but editorU does not.
             */
            private static final int checkContent() {
                   //LOGGER.debug("checkContent: is text empty? editors.EDITOR_U: {}, editorL {} \nis there an image?: {} ", editors.EDITOR_U.getText().isEmpty(), editorL.getText().isEmpty(), (editorL.getMediaFileNames()[0] != null));

                  int stat = 0;
                  // check and save if there are shapes. Returns 2 if upper area has a drawing,
                  // 4 if lower area has a drawing, 6 for both and 0 for none;
                  // Is there a test type selected?
                  if (getTestType() != null) {
                        //stat set bit 1
                        stat |= (1); // = 1
                        LOGGER.debug("testType is set: stat bit 1 is set. stat: <{}>", stat);
                  }
                  // Upper section sets the 2nd bit "2".
                  // Is there text in the upper editor?
                  if (!editors.EDITOR_U.getText().isEmpty()) {
                        //stat set bit 2
                        stat |= (2);
                        LOGGER.debug("text in Question: <{}>, Setting bit 2. stat: <{}>", editors.EDITOR_U.getText(), stat);
                  } else if (editors.EDITOR_U.getMediaNameArray()[0] != null || editors.EDITOR_U.getArrayOfFMShapes().size() > 1) {
                        //stat set bit 2
                        stat |= (2);
                        LOGGER.debug("There is media in the QuestionMM mediaArray, Setting bit 2. stat: <{}>", stat);
                  }
                  //Lower section sets 3rd bit "4".
                  // Is there data in the lower editor? or if the CardLayout does not have a lower section
                  boolean isSingleCardLayout = null != getTestType() && getTestType().getCardLayout() == 'S';
                  if (!editors.EDITOR_L.getText().isEmpty() || isSingleCardLayout) {
                        //stat set bit 3
                        stat |= (4);
                        LOGGER.debug("text in Answer: <{}>, Setting bit 4. stat: <{}>", editors.EDITOR_U.getText(), stat);
                        } else if (editors.EDITOR_L.getMediaNameArray()[0] != null || editors.EDITOR_L.getArrayOfFMShapes().size() > 1) {
                        //stat set bit 3
                        stat |= (4);
                        LOGGER.debug("There is media in the AnswerMM mediaArray, Setting bit 4. stat: <{}>", stat);
                  }

                  //  LOGGER.info("If stat is odd, then the testType has been set. If it is even then TestType has not been set. User should be sent back to set it\n" +
                  //     "If the 2nd bit has been set, then there is data in the Question. This must always be true. and in the case of noteCard with only a question present is ok.\n" +
                  //     "If the 3rd bit 4 is set and the result is 7, then the card is allowed to be saved. ");
                  LOGGER.info("End checkContent. Returning stat: <{}>", stat);

                  return stat;
            }

      } // **** END INNER CLASS *****

      /**
       * Called by checkContent. Checks if an instance of DrawTools is open.
       * By default shapes are saved to file during close event. Adds 2,or 4 if
       * an instance is open. May not be needed as this is checked later by each
       * section editor's mediaFileNames.
       * <p> Used by circleOnClick in AvlTreePane </p>
       *
       * @param editorU
       * @param editorL
       */
      private static void closeDrawToolsOnTreeClick(SectionEditor editorU, SectionEditor editorL) {
            if(DrawTools.instanceExists()) {
                  DrawTools.getInstance().onClose();
            }
      }



      /* ------------------------------------------------------- **/

      /**
       * If this is the first session the currentCard
       * is null.
       */
      boolean isntStarted() {
            return currentCard == null;
      }



      /* ------------------------------------------------------- **/


      /**
       * DESCRIPTION: Sets the creatorList from file. Meaning that an ArrayList is
       * created from the binary file.
       */

      public void setCreatorList() {
            FlashCardOps fcOps = FlashCardOps.getInstance();

            creatorList = fcOps.cloneList(fcOps.getFlashList());
            startSize = creatorList.size();

            LOGGER.info("creatorList size: ", startSize);
      }

      /* ------------------------------------------------------- **/

      // lastSaveTime is used to prevent problems with multiple calls to this
      // method.
      private long lastSaveTime = 0l;
      /**
       * <p>Should not be called if the intent is to close the scene. Call saveOnExit()
       * instead.</p>
       * <p>Top level method using support methods provide the following:</p>
       * <p> 1. Saves the flashcard arraylist to the
       * binary file named/selected by the user at program start.</p>
       * <p> 2. Saves/synchronizes media and file to cloud. Saves users edits in the
       * shapesPane if open.</p>
       * <p> 3. Return to previous.</p>
       */
      private void saveDeckAction() {
            // Prevent double calls that may corrupt files. Some situations
            // will cause this to be called twice.
            // if this has been called in the last 2 seconds, just ignore
            // the call.
            LOGGER.debug("lastSaveTime: {}", lastSaveTime);
            if( ( lastSaveTime != 0 ) && System.currentTimeMillis() < lastSaveTime + 2000l ) {
                  LOGGER.debug("Interrupted file save. It was already saved in the past 2 seconds");
                  return;
            }
            if(creatorList.isEmpty()) {
                  LOGGER.warn("CreatorList is empty. Calling dumpstack");
                  Thread.dumpStack();
            }
            lastSaveTime = System.currentTimeMillis();
            LOGGER.info(" saveDeckAction ");
            Timer.getClassInstance().createTestTimeStop();
            // Check content and save, or give user
            // an error message. Respond based on user
            // choice.
            boolean safeToContinue = cardOnExitActions(false, false);
            // If requested, then
            // return user to screen.
            if ( ! safeToContinue ) {
                  return;
            }
            // If there are changes. Save them.
            if ( flashListChanged ) {
                  // update the deck info
                  this.updateDeckInfo(meta);
                  meta.updateDataMap();
                  // send metadata to database
                  // this is updated later.
     //             Report.getInstance().reportDeckMetadata(meta);

                  // Save the current card or not
                  FlashCardMM card = creatorList.get(creatorList.size() - 1);
                  if (card.getQText().isEmpty() && ( card.getQFiles() == null ||  card.getQFiles().length == 0) ) {
                        LOGGER.debug("calling saveButtonActionHelper. last cardQtext is empty. minus: -");
                        saveDeckActionHelper('-');
                  } else {
                        // Save deck metadata to DB.
                        LOGGER.debug("calling saveButtonActionHelper. minus: +");
                        saveDeckActionHelper('+');
                  }

                  FlashMonkeyMain.setWindowToMenu();
                  if (!FlashCardOps.getInstance().getMediaIsSynched()) {
                        LOGGER.debug("Calling MediaSync from CreateFlash.saveDeckAction()");
                        if (Utility.isConnected()) {
                              // token related
                              if (CloudOps.isInValid()) {
                                    LOGGER.debug("token is expired, not renewing");
                                    FMAlerts alerts = new FMAlerts();
                                    // method may not be on the same thread. Cannot create an
                                    // alert on another thread.
                                    alerts.sessionRestartPopup();
                              } else {
                                    //@TODO Add to a common thread pool
                                    new Thread(() -> {
                                          LOGGER.info("Calling syncMedia from CreateFlash");
                                          MediaSync.syncMedia(creatorList);
                                    }).start();
                              }
                        }
                  }
                  flashListChanged = false;
            }
      }

      /* ------------------------------------------------------- **/

      /**
       * Inventories the decks media and inserts it into the
       * DecksMetaData object.
       *
       * @param meta
       */
      public void updateDeckInfo(@NotNull DeckMetaData meta) {
            LOGGER.debug("updateDeckInfo called");

            // LOGGER.debug("before retrieveDEckMetaData(), meta array is: {}", meta.toString());
            // ensure object is updated from file
            //meta.setDataFmFile(); // may be causeing issues with deleting current data

            //LOGGER.debug("After retrieveDeckMetaData() from file, and before update, meta array is: {}", meta.toString());
            // reset deck meta to 0;
            //meta.reset();
            if (creatorList == null || creatorList.isEmpty()) {
                  ArrayList<FlashCardMM> fc = FlashCardOps.getInstance().getFlashList();
                  meta.setNumCard(fc.size());
                  inventoryMeta(fc, meta);
            } else {
                  meta.setNumCard(creatorList.size() - 1);
                  inventoryMeta(creatorList, meta);
            }

            // set metadata to array
            //    meta.updateDataAry();
            // save metadata to file
            FlashCardOps.getInstance().setMetaInFile(meta, FlashCardOps.getInstance().getDeckFileName());
            //LOGGER.debug("After meta.saveDeckMetaData() meta array is: {}", meta.toString());
      }

      /* ------------------------------------------------------- **/

      /**
       * Crawls and creates an inventory of the media
       * in a deck.
       * Helper method for updateDeckInfo
       *
       * @param creatorList
       * @param meta
       */
      public static DeckMetaData inventoryMeta(ArrayList<FlashCardMM> creatorList, DeckMetaData meta) {

            //LOGGER.info("creatorList size: ", (creatorList.size() - 1));
            //LOGGER.info("Deck: BEFORE {}", meta.toString());

            long time = System.currentTimeMillis();
            // reset the media count.

            meta.reset();
            for (int i = 0; i < creatorList.size() - 1; i++) {
                  meta.mediaCounter(creatorList.get(i).getAFiles()[0]);
                  meta.mediaCounter(creatorList.get(i).getQFiles()[0]);
                  meta.addTestType(creatorList.get(i).getTestType());
            }


            // TODO parallelize META_DATA sync performance!!! with forall
        /*
        Stream.of(creatorList)
                .peek(fc -> {
                    forall(0, creatorList.size() -1, (i) -> {
                        meta.mediaCounter(creatorList.get(i).getAFiles()[0]);
                        meta.mediaCounter(creatorList.get(i).getQFiles()[0]);
                        meta.addTestType(creatorList.get(i).getTestType());
                    });
                }).close();  */

            time = System.currentTimeMillis() - time;
            LOGGER.info("inventoryMeta serial processing time was: <{}> milliseconds", time);
            LOGGER.info("Deck: AFTER {}", meta.toString());

            return meta;
      }


      /* ------------------------------------------------------- **/


      /**
       * helper method to saveDeckAction
       * Saves the creator list to file.
       */
      private void saveDeckActionHelper(char minus) {

            LOGGER.info("saveButtonActionHelper char minus: <{}>, num cards <{}>", minus, creatorList.size());

            // clear the flashList. creatorList is a deep copy of flashList with
            // new cards added. !!! Important that the original file is maintained
            // until the user commits.
            try {
                  // creatorList minus 1. Last card is empty. '-' indicates this.
                  FlashCardOps.getInstance().saveFlashListToFile(creatorList, minus);

                  if (Utility.isConnected()) {
                        //CloudOps co = new CloudOps();
                        CloudOps.putDeck(FlashCardOps.getInstance().getDeckFileName());
                        //co.connectCloudOut('t', authcrypt.UserData.getUserName(), ReadFlash.getInstance().getDeckName());
                  }
                  creatorList.clear();
                  //   saveDeckButton.setDisable(true);
            } catch (Exception h) {
                  LOGGER.warn("ERROR: Exception thrown while saving"
                      + "flash card Deck in CreateFlashPane.saveButtonActionHelper() " +
                      "\n *** FILE NOT SAVED!!! *** \n line 258 CreateFlashPane \n Exiting ...");
                  h.printStackTrace();
                  System.exit(0);
            } finally {
                  //   this.creatorList.clear();
                  FlashCardOps.getInstance().refreshFlashList();
            }
      }


      /* ------------------------------------------------------- **/


      /**
       * Sets the prompt based upon the length of the creatorList
       *
       * @param cardNum
       */
      private String makeQPrompt(int cardNum) {

            int creatorListLength = creatorList.size();

            if (cardNotEnabledMsg != null) {
                  String msg = cardNotEnabledMsg;
                  cardNotEnabledMsg = null;
                  return msg;
            }

            if (creatorListLength < 5) {
                  return "Please create " + (-1 * (creatorListLength - 5)) + " more flash Cards ";
            }

            return "Flash Card " + (listIdx + 1);

      }


      /* ------------------------------------------------------- **/


      /*************************************************************
       INNER CLASS
       TestMapper for dropDown menu
       *************************************************************/

      /**
       * This class provides the object
       * with the Name of the test that
       * appears in the dropDown button and the related
       * TestType (aka AnswerType).
       */
      public static class TestMapper {
            private final String name;
            private final GenericTestType testType;

            TestMapper(String s, GenericTestType type) {
                  this.name = s;
                  this.testType = type;
            }

            // **** GETTERS *****

            public String getName() {
                  return this.name;
            }

            public GenericTestType getTestType() {
                  return this.testType;
            }

            @Override
            public String toString() {
                  return name;
            }

            static ArrayList<TestMapper> getTestList(GenericTestType[] tList) {
                  ArrayList<TestMapper> tm = new ArrayList<>(15);
                  for (int i = 0; i < tList.length; i++) {
                        tm.add(new TestMapper(tList[i].getName(), tList[i].getTest()));
                  }
                  return tm;
            }
      }

      // **************** METHODS USED FOR UI TESTING ******************

      @FMAnnotations.DoNotDeployMethod
      public Point2D getPrevBtnXY() {
            Bounds bounds = prevQButton.getLayoutBounds();
            return prevQButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getNextBtnXY() {
            Bounds bounds = nextQButton.getLayoutBounds();
            return nextQButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getNewCardBtnXY() {
            Bounds bounds = newCardButton.getLayoutBounds();
            return newCardButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getInsertCardBtnXY() {
            Bounds bounds = insertCardButton.getLayoutBounds();
            return insertCardButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getSaveDeckBtnXY() {
            Bounds bounds = saveReturnButton.getLayoutBounds();
            return saveReturnButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getLowerTextClearBtnXY() {
            return  editors.EDITOR_L.getClearTextBtnXY();
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getSellButtonXY() {
            Bounds bounds = metaButton.getLayoutBounds();
            return metaButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public ArrayList<FlashCardMM> getCreatorList() {
            return creatorList;
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getComboBoxXY() {
            Bounds bounds = entryComboBox.getLayoutBounds();
            return entryComboBox.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public DeckMetaModel getMetaModelObj() {
            return metaPane.getModel();
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getFormMinXY() {
            Double x = metaWindow.getX();
            Double y = metaWindow.getY();
            Point2D d2 = new Point2D(x, y);
            return d2;
      }

      @FMAnnotations.DoNotDeployMethod
      public boolean entryComboBoxIsShowing() {
            return this.entryComboBox.isShowing();
      }

      @FMAnnotations.DoNotDeployMethod
      public SectionEditor getEditor_U_ForTestingOnly() {
            return editors.EDITOR_U;
      }

      @FMAnnotations.DoNotDeployMethod
      public SectionEditor getEditor_L_ForTestingOnly() {
            return editors.EDITOR_L;
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getDeleteCardBtnXY() {
            Bounds bounds = deleteQButton.getLayoutBounds();
            return deleteQButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getUndoCardBtnXY() {
            Bounds bounds = undoQButton.getLayoutBounds();
            return undoQButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }
}
