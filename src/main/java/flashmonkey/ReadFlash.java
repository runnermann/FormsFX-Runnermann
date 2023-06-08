package flashmonkey;


import campaign.Report;
import fileops.BaseInterface;
import fileops.DirectoryMgr;
import fmannotations.FMAnnotations;
import fmtree.FMTWalker;
import javafx.animation.ScaleTransition;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import media.sound.SoundEffects;
import metadata.DeckMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.cardtypes.GenericCard;
import type.testtypes.*;
import type.tools.imagery.Fit;
import uicontrols.ButtoniKon;
import uicontrols.SceneCntl;
import uicontrols.UIColors;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.floor;

/*******************************************************************************
 * Provides the EncryptedUser.EncryptedUser interface to reading flash cards
 * <strong>Singleton Class. Synchronized.</strong>
 *
 * NOTES: This class starts off asking the EncryptedUser.EncryptedUser what study mode to use. The class
 * uses the int mode variable to determine which scene to represent.
 *
 * Modification to Multi-Media and Multi - Test Sept 28th 2018
 *
 * Previously buttonActions were lambda's within the readflash constructor
 *  - For the original test mode, muliple choice, much of the code existed
 *  within the lamda block. With multiple test and multiple media types and
 *  moving forward to interactive capabilities, Tests will become thier own
 *  classes, Multiple Choice did have a class but much of the code will
 *  exist in it's own class with a simple call from the constructor. Use of
 *  static variables and static objects is minimized appropriatly to reduce
 *  the amount of memory needed to run the program. In the MVC model, A test
 *  will be control, ie button actions. View, or appearance of buttons and
 *  thier actions according to settings are TBD
 *
 *  Creating individual classes for each test is theoretically more efficient,
 *  providing access to the type classes, or layers/levels of panes where they
 *  are needed, rather than simply through a single factory that parses a call
 *  into the desired format. The test will simply access the card, section, cell
 *  at the level that makes the best fit. In effect making the cardFactory
 *  unneeded with exception to a few classes. This means programming each test
 *  will take greater effort, however provides greater control of the desired
 *  actions in the compiler.
 *
 * @author Lowell Stadelman
 ******************************************************************************/


public final class ReadFlash implements BaseInterface {

      private volatile static ReadFlash CLASS_INSTANCE;
      //private static final ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ReadFlash.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(ReadFlash.class);

      // *** CONSTANTS ***
      protected static final GenericCard GEN_CARD = new GenericCard();

      // **** PANES **** //
      // The main scene object & pane
//      private Scene masterScene;
      private static BorderPane masterBPane;// = new BorderPane();
      // Titles and messages in rpNorth
      private static HBox rpNorth;
      // Q section and A section. May also contain the graph
      protected static VBox rpCenter;// = new VBox(2);  //
      // The study mode menu for Q&A, Test
      private static GridPane studyModeMenuPane = new GridPane();
      // Button box contained in southpane
      private GridPane exitBox;

      // **** VARIABLES **** //

      // **** INDEXES **** //
      /** the visible number displayed to the EncryptedUser.EncryptedUser for the card */
      //protected static int visIndex = 1;
      /**
       * The last score total
       **/
      private AtomicInteger score = new AtomicInteger(0);

      // *** BUTTONS *** //

      // The beginning of the tree
      protected static Button firstQButton;
      private static Button endQButton;
      private static Button nextQButton;
      private static Button prevQButton;
      protected static Button menuButton;
      // question and answer mode buttons
      private Button qaButton;
      private Button testButton;
      private Button deckSelectButton;

      // *** LABELS ***
      private static Label deckNameLabel;
      private static Label scoreLabel;
      protected static Label message;

      // *** TEXT FIELDS ***
      private static TextField scoreTextF; //= new TextField();

      // *** PROGRESS BARS & INDICATORS ***
      // Calculation for the progress displayed to the EncryptedUser
      // Only answer clicks are counted, not forward or backward buttons
      private static int progress = 0;
      private static MGauges progGauge;
      private static MGauges scoreGauge;
      private static MGauges timerGauge;
      private static MGauges durationGauge;
      private static GridPane pGaugePane;
      private static GridPane sGaugePane;
      private static GridPane timerPane;
      private static GridPane durationPane;
      private static int origNumCards;
      private static long startTime;

      // *** FLAGS ***
      private static boolean hasChanged;
      // Has the session been sent as an update to DB?
      private static boolean isReported;


      /**
       * Private Constructor
       */
      private ReadFlash() {
            /* no args constructor */
      }

      /**
       * Returns an instance of the class. If getInstance hasn't been called before
       * a new instance of the class is created. otherwise it returns the existing
       * instance.
       * To intialize: ReadFlash readFlashObj = ReadFlash.getInstance();
       *
       * @return ReadFlash
       */
      public static synchronized ReadFlash getInstance() {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new ReadFlash();
            }
            FlashMonkeyMain.getPrimaryWindow().setResizable(true);
            return CLASS_INSTANCE;
      }

      /**
       * The createRead Scene class creates the flashcard scene.
       * UI FLow: The user enters the scene from the MenuPane/modeSelectPane after
       * clicking on the study button.
       * 1) The first scene the EncryptedUser sees asks
       * them what study mode to use.
       * a) There is Q and A session which is similar to a
       * Flash card mode. The pane presents the 1st question and the
       * EncryptedUser clicks the answer button. The users can
       * traverse forward or backwards through the questions. There is no
       * scoring associated with QAndA mode.
       * b) The Test session is the test mode. In test mode the
       * EncryptedUser is presented a question and one of the test cards.
       */
      public BorderPane readScene() {
            isReported = false;
            LOGGER.info("\n\n **** createReadScene() called ****");
            //LOGGER.setLevel(Level.DEBUG);

            // NORTH & CENTER PANES read fields
            rpNorth = new HBox(2);
            // holds questionPane and answerPane
            rpCenter = new VBox(2);
            rpCenter.setId("rpCenter");
            rpCenter.setSpacing(2);
            masterBPane = new BorderPane();
            // nav buttons
            firstQButton = ButtoniKon.getQFirstButton();
            nextQButton = ButtoniKon.getQNextButton();
            prevQButton = ButtoniKon.getQPrevButton();
            endQButton = ButtoniKon.getQLastButton();
            // menu buttons shown in the menu pane
            deckSelectButton = ButtoniKon.getDeckSelectButton();

            preRead();

            menuButton = ButtoniKon.getMenuButton();
            exitBox = new GridPane();
            //exitBox.setHgap(2);
            // For the lower panel on modeSelectPane window */
            ColumnConstraints col0 = new ColumnConstraints();
            col0.setPercentWidth(50);
            exitBox.getColumnConstraints().add(col0);
            //exitBox.setVgap(2);
            exitBox.setPadding(new Insets(15, 15, 15, 15));
            exitBox.addColumn(1, menuButton);
            exitBox.setId("buttonBox");

            deckNameLabel = new Label(FlashCardOps.getInstance().getDeckLabelName());
            deckNameLabel.setId("label16blue");

            rpNorth.getChildren().add(deckNameLabel);

            // The study menu pane
            LOGGER.debug("setting window to modePane");
            masterBPane.setCenter(studyModeMenuPane);
            masterBPane.setBottom(exitBox);
            masterBPane.setId("bckgnd_image_study");

            LOGGER.debug("readflash masterBPane width: " + masterBPane.widthProperty());

            // *** BUTTON ACTIONS ***
            menuButton.setOnAction(e -> menuButtonAction());
            qaButton.setOnAction(e -> qaButtonAction());
            testButton.setOnAction(e -> testButtonAction());
            /*
             * Navigation buttons, handles actions for first, last, next, and previous
             * buttons. Sound effects in action methods.
             */
            nextQButton.setOnAction(e -> nextQButtonAction());
            prevQButton.setOnAction(e -> prevQButtonAction());
            endQButton.setOnAction(this::lastQButtonAction);
            firstQButton.setOnAction(this::firstQButtonAction);
            /*
             * shortCut Key actions
             */
            masterBPane.setOnKeyPressed(this::shortcutKeyActions);

            return masterBPane;
      }    // ******** END OF READ SCENE ********


      private void preRead() {
            testButton = ButtoniKon.getTestButton();
            qaButton = ButtoniKon.getQandAButton();

            FlashCardOps fcOps = FlashCardOps.getInstance();
            // If the flashList is not empty,save before
            // refreshing.
            if (fcOps.getFlashList() != null || ! fcOps.getFlashList().isEmpty()) {
                  fcOps.safeSaveFlashList();
            }
            fcOps.refreshFlashList();

            if (fcOps.getFlashList().size() < 4) {
                  emptyListAction();
            } else {
                  studyModeMenuPane = buildStudyModeMenuPane();
                  ScaleTransition animate = FlashMonkeyMain.animateScaleV(studyModeMenuPane);
                  animate.play();
                  origNumCards = fcOps.getFlashList().size();
                  setTree();
            }
      }


      /**
       * The shortCutKeys for ReadFlash actions.
       * Helper method to createReadScene()
       *
       * @param e
       */
      private void shortcutKeyActions(KeyEvent e) {
            FlashCardMM currentCard = (FlashCardMM) FMTWalker.getInstance().getCurrentNode().getData();
            GenericTestType test = TestList.selectTest(currentCard.getTestType());

            if (e.isAltDown() || e.isShiftDown()) {
                  if (e.getCode() == KeyCode.ENTER) {
                        LOGGER.debug("Enter pressed for testAnswer");
                        test.ansButtonAction();
                        masterBPane.requestFocus();
                  } else if (e.getCode() == KeyCode.LEFT) {
                        SoundEffects.SLIDE_LEFT.play();
                        LOGGER.debug("Left pressed for leftAnswer");

                        test.prevAnsButtAction();
                        masterBPane.requestFocus();

                  } else if (e.getCode() == KeyCode.RIGHT) {
                        SoundEffects.SLIDE_RIGHT.play();
                        LOGGER.debug("Right pressed for rightAnswer");
                        test.nextAnsButtAction();
                        masterBPane.requestFocus();
                  }
            } else if (e.getCode() == KeyCode.ENTER) {
                  LOGGER.debug("Enter pressed for testAnswer w/o alt or shift");
                  test.ansButtonAction();
                  masterBPane.requestFocus();
            } else if (e.getCode() == KeyCode.LEFT) {
                  prevQButtonAction();
                  masterBPane.requestFocus();
            } else if (e.getCode() == KeyCode.RIGHT) {
                  nextQButtonAction();
                  masterBPane.requestFocus();
            }
      }


      /**
       * Helper Method to ReadScene. Action when list has cards.
       * Builds tree, and sets values in appropriate panes.
       *
       * @return
       */
      private GridPane buildStudyModeMenuPane() {
            LOGGER.debug("listHasCardsAction() called");

            Label label = new Label("Study Mode");
            label.setId("label24White");
            GridPane gPane = new GridPane();
            VBox buttonBox = new VBox(2);
            VBox spacer = new VBox();
            spacer.setPrefHeight(300);
            buttonBox.setId("studyModePane");
            buttonBox.setAlignment(Pos.TOP_CENTER);
            buttonBox.getChildren().addAll(label, qaButton, testButton);

            gPane.setId("rightPaneTransp");
            gPane.setAlignment(Pos.CENTER);
            gPane.addRow(0, spacer);
            gPane.addRow(1, buttonBox);

            return gPane;
      }

      /**
       * Builds the tree and sets the first card to lowest card.
       */
      private void setTree() {
            FlashCardOps.getInstance().refreshTreeWalker();
            FMTWalker.getInstance().setHighLow();
            FMTWalker.getInstance().setToFirst();
      }

      /**
       * Sends the UI to the menu screen and displays the message
       * provided in the argument.
       * NOTE: Set the String errorMsg in FlashMonkeyMain. and
       * provide FlashMonkeyMain.errorMsg in the argument.
       */
      public void emptyListAction() {
            Button createButton = ButtoniKon.getCreateButton();

            studyModeMenuPane.setAlignment(Pos.CENTER);
            String imgPath = DirectoryMgr.EMOJI + "flashFaces_wrong_answer.png";

            LOGGER.debug("emoji file path: " + imgPath);

            ImageView iView;

            Image image = new Image(imgPath);
            // view of emoji photo
            iView = Fit.viewResize(image, 100, 100);
            int num = 4 - FlashCardOps.getInstance().getFlashList().size();

            String str = "Oooph! \nThere are not enough cards to work.\n\n Please create at least " + num + " more.\n\n";

            message = new Label(str);
            message.setId("message");
            message.setMaxWidth(SceneCntl.getButtonWidth());
            message.setTextAlignment(TextAlignment.CENTER);

            createButton.setOnAction(e -> FlashMonkeyMain.createButtonAction());
            createButton.setPrefWidth(SceneCntl.getButtonWidth());
            deckSelectButton.setOnAction(e -> deckSelectButtonAction());
            deckSelectButton.setPrefWidth(SceneCntl.getButtonWidth());

            studyModeMenuPane.getChildren().clear();
            studyModeMenuPane.addRow(0, iView);
            studyModeMenuPane.addRow(1, message);
            studyModeMenuPane.addRow(2, createButton);
            studyModeMenuPane.addRow(3, deckSelectButton);
      }

      /**
       * The previous question button action
       */
      private void prevQButtonAction() {
            FMTWalker.getInstance().getPrevious();
            navButtonActionCommon();
            FMTransition.getQLeft().play();
      }

      /**
       * The next question button action
       * - Get the next card from the tree,
       * - read the cards bitset and get the test
       * -
       */
      private void nextQButtonAction() {
            FMTWalker.getInstance().getNext();
            navButtonActionCommon();
            FMTransition.getQRight().play();
      }

      /**
       * Sends the treedisplay and the flashcard panes to the
       * first question in the tree.
       */
      private void firstQButtonAction(ActionEvent e) {
            FMTWalker.getInstance().setToFirst();
            navButtonActionCommon();
            FMTransition.getQLeft().play();
      }

      /**
       * sends the flashCard pane to the last flashCard that is displayed in the
       * tree. The firthest right card.
       */
      private void lastQButtonAction(ActionEvent e) {
            FMTWalker.getInstance().setToLast(); // O(log(n))
            navButtonActionCommon();
            FMTransition.getQRight().play();
      }

      /**
       * The common action for the nav buttons for ReadFlash
       */
      private void navButtonActionCommon() {
            SoundEffects.PRESS_BUTTON_COMMON.play();

            FlashCardMM currentCard = (FlashCardMM) FMTWalker.getInstance().getCurrentNode().getData();

            rpCenter.getChildren().clear();
            // All of the work is accessed from selectTest()
            GenericTestType test = TestList.selectTest(currentCard.getTestType());

            if (mode == 't') {
                  rpCenter.getChildren().add(test.getTReadPane(currentCard, GEN_CARD, rpCenter));
            } else {
                  rpCenter.getChildren().add(QandA.QandASession.getInstance().getTReadPane(currentCard, GEN_CARD, rpCenter));
            }
            masterBPane.setBottom(manageSouthPane(mode));
            // Set the timer gauge

            int seconds = getCardSeconds(currentCard);
            //durationGauge.setConsumedTime();
            timerGauge.setTimerTime(seconds);
            timerGauge.start();

            // Answer button fade in
            if (null != test.getAnsButton()) {
                  test.getAnsButton().setDisable(false);
                  FMTransition.nodeFadeIn = FMTransition.ansFadePlay(test.getAnsButton(), 1, 750, 500, false);
                  FMTransition.nodeFadeIn.play();
            }

            // Set the status of the answer button. A question should only
            // be answered once
            ansQButtonSet(currentCard.getIsRightColor(), test);
            buttonDisplay(FMTWalker.getInstance().getCurrentNode());
            FlashMonkeyMain.AVLT_PANE.displayTree();

            if (null != FMTransition.getAWaitTop()) {
                  try {
                        FMTransition.getAWaitTop().play();
                  } catch (NullPointerException e) {
                        // if there is an error, do nothing here.
                  }
            }
      }


      /**
       * Increment the progress count. Used by
       * progress gauge.
       *
       * @return
       */
      public double incProg() {
            LOGGER.debug("\n\n *** called incProg ***Progress: " + (progress + 1));
            return ++progress;
      }

      public MGauges getProgGauge() {
            return progGauge;
      }

      public double getProgress() {
            return progress;
      }

      private void initGauges() {
            int flSize = FlashCardOps.getInstance().getFlashList().size();
            int highestScore = FMTWalker.getInstance().highestPossibleScore() / 10;
            FlashCardMM currentCard = (FlashCardMM) FMTWalker.getInstance().getCurrentNode().getData();

            progGauge = new MGauges(120, 120);
            scoreGauge = new MGauges(120, 120);
            timerGauge = new MGauges(120, 120);
            durationGauge = new MGauges(366, 40 );
            pGaugePane = progGauge.makeGauge("COMPLETED", 0, flSize, 0);
            sGaugePane = scoreGauge.makeGauge("SCORE", 0, highestScore, -1 * highestScore);
            timerPane = timerGauge.makeTimer("TIMER", getCardSeconds(currentCard));
            durationGauge.setStartTime();
            durationPane = durationGauge.makeDurTimer(startTime);
      }

      private int getCardSeconds(FlashCardMM currentCard) {
            int seconds = currentCard.getSeconds();
            seconds = seconds > 0 ? seconds : 30;
            return seconds;
      }


      /**
       * <p><b>Returning to the main pane resets and saves data from the current
       * session.</b></p>
       * File Select Button Action: Sends the user back to the first scene where
       * they can select another flashDeck/file. The flashList is saved, then
       * reset to zero elements. MetaData is saved and sent to the DB. The AGR File list
       * is recreated from new showing any new files.
       */
      protected void deckSelectButtonAction() {
            timerGauge.stop();
            SoundEffects.GOTO_FILE_SELECT.play();
            // save metadata to local, and
            // send metadata to the db.
            // save flashlist changes.
            //leaveAction();
            LOGGER.debug("call to fileSelectButtonAction()");
            // clear the flashlist
            FlashCardOps.getInstance().clearFlashList();
            // get a new list of files
            FlashCardOps.getInstance().resetAgrList();
            // show fileSelectPane()
            FlashMonkeyMain.getFileSelectPane();
      }

      /**
       * TEST BUTTON ACTION:  provides the actions for the test button scene
       * Void method. testButton exists in the second menu after the study button
       * has been pressed by the EncryptedUser.
       *
       * <p><b>Note: </b> The expected Result... multi choice example:
       * the centerPane is set to two sections, the top section is for the
       * question, and the bottom section is for the answers. The first answer
       * drops down from the top and the successive answers come in from the right
       * or left depending on which answer button is clicked. The select button
       * selects the current answer that is in view and will compare it with the
       * correct answer for that question. It scores the answer and adds or
       * subtracts depending on if it is right or wrong.</p>
       *
       * <p>- Uses genericTest.getTReadpane to call the appropriate test and card.
       * After the first card is set, each test is responisible for displaying
       * the correct test card for "that card" being the "currentCard". The
       * qNavigation buttons are responsible for displaying the next card in the
       * treeWalker navigation tree. :)</p>
       */
      protected void testButtonAction() {
            startTime = System.currentTimeMillis();

            deckNameLabel.setId("label16blue");

            masterBPane.setId("readFlashPane");

            LOGGER.info(" testButtonAction called ");
            Timer.getClassInstance().startTestTime();

            if (!rpCenter.getChildren().isEmpty()) {
                  rpCenter.getChildren().clear();
            }

            initGauges();

            buttonDisplay(FMTWalker.getInstance().getCurrentNode());
            FlashCardMM currentCard = (FlashCardMM) FMTWalker.getInstance().getCurrentNode().getData();

            mode = 't';

            VBox topVBox = new VBox();

            FlashCardOps.getInstance().resetDeckLabelName();
            deckNameLabel = new Label(FlashCardOps.getInstance().getDeckLabelName());

            topVBox.getChildren().add(deckNameLabel);
            topVBox.setAlignment(Pos.CENTER);

            masterBPane.setTop(topVBox);
            VBox rpSouth = manageSouthPane('t');
            masterBPane.setBottom(rpSouth);

            // Set center last so top and bottom heights are
            // already calculated.
            masterBPane.setCenter(rpCenter);


            if (!FlashMonkeyMain.treeWindow.isShowing()) {
                  FlashMonkeyMain.buildTreeWindow();
            }

            navButtonActionCommon();
            FMTransition.getQRight().play();
      }

      /**
       * QAndA BUTTON ACTION
       * qaButton resides in the Test Mode Selection UI Menu.
       * qaButton Action method: Question and Answer button action
       * Void method
       * This action sets the scene to the simple question and answer mode
       * Uses treeWalker.
       */
      protected void qaButtonAction() {
            // zero timer and start to give credit
            // for studying this session.
            Timer.getClassInstance().startTestTime();
            startTime = System.currentTimeMillis();

            if (!rpCenter.getChildren().isEmpty()) {
                  rpCenter.getChildren().clear();
            }
            SoundEffects.PRESS_BUTTON_COMMON.play();
            initGauges();
            buttonDisplay(FMTWalker.getInstance().getCurrentNode());

            VBox topVBox = new VBox();
            FlashCardOps.getInstance().resetDeckLabelName();
            deckNameLabel = new Label(FlashCardOps.getInstance().getDeckLabelName());
            deckNameLabel.setId("label16blue");
            topVBox.getChildren().add(deckNameLabel);
            topVBox.setAlignment(Pos.CENTER);

            masterBPane.setTop(topVBox);

            masterBPane.setId("readFlashPane");

            mode = 'q';
            VBox rpSouth = manageSouthPane('q');
            masterBPane.setBottom(rpSouth);

            // Set center last so top and bottom heights are
            // already calculated.
            // rpCenter.getChildren().add(QandA.QandASession.getInstance().getTReadPane(currentCard, GEN_CARD, rpCenter));
            masterBPane.setCenter(rpCenter);
            if (!FlashMonkeyMain.treeWindow.isShowing()) {
                  FlashMonkeyMain.buildTreeWindow();
            }

            navButtonActionCommon();
            FMTransition.getQRight().play();

            FlashMonkeyMain.AVLT_PANE.displayTree();
      }

      private void menuButtonAction() {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            //leaveAction();
            // calls leaveAction
            saveOnExit();
            FlashMonkeyMain.setWindowToModeMenu();
      }

      /**
       * Actions taken when ReadFlash is closed.
       */
      public void leaveAction() {
            DeckMetaData meta = DeckMetaData.getInstance();
            try {
                  if( ! isReported) {
                        LOGGER.debug("trying to enter data to the DB for exit event");

                        Timer.getClassInstance().testTimeStop();
                        if(timerGauge != null)
                        {
                              timerGauge.stop();
                        }
                        // May be an uneccessary call
                        // since we only update test score.
                        meta.setDataFmFile();
                        meta.appendToScoresArry(progress, score.get() / 10);
                        // set metadata to
                        meta.updateDataMap();
                        FlashCardOps.getInstance().setMetaInFile(meta, FlashCardOps.getInstance().getDeckFileName());
                        // send test metadata to database
                        Report.getInstance().reportTestMetaData(meta.getDataMap());
                        Report.getInstance().sendSessionTime();
                  }

            } catch (Exception f) {
                  LOGGER.error("Error when exiting: ");
                  f.printStackTrace();
            } finally {
                  meta.close();
                  FlashMonkeyMain.treeWindow.close();
            }
      }

      /**
       * Deck save action for this class that is called when FlashMonkey
       * is closed. May be used outside of the class. Single point call
       * that is always called when class is closed. Should be implemented
       * by any class that contains files to be saved to file or the cloud.
       * E.g. ReadFlash and CreateFlash.
       * @return Always returns true;
       */
      @Override
      public boolean saveOnExit() {
            // Save the historical data in the original
            // FlashList not the cloned copy.
            FlashCardOps.getInstance().unsafeSaveFlashList();
            FlashCardOps.getInstance().clearFlashList();
            leaveAction();
            return true;
      }

      /**
       * Closes this class. Clears the tree. Closes the treeWindow. Does not save
       * the current work.
       */
      @Override
      public void onClose() {
            FlashMonkeyMain.treeWindow.close();
            timerGauge.stop();
            if(SceneCntl.Dim.APP_MAXIMIZED.get() == 0) {
                  SceneCntl.getAppBox().setX((int)FlashMonkeyMain.getPrimaryWindow().getX());
                  SceneCntl.getAppBox().setY((int)FlashMonkeyMain.getPrimaryWindow().getY());
            }
      }



      // *** SETTERS *** //


      /**
       * Sets the progress to the value in the argument
       *
       * @param num
       */
      public void setProgress(int num) {
            progress = num;
      }

      /**
       * Sets the display message to the String given in the
       * arguement.
       *
       * @param str
       */
      protected void setMessage(String str) {
            message.setText(str);
      }

      /**
       * Mode controls the bottom pane and what buttons,
       * and gauges are located there.
       *
       * @param m
       */
      public void setMode(char m) {
            mode = m;
      }


      // *** GETTERS *** //

      public void isChanged() {
            hasChanged = true;
      }

      public Pane getMasterBPane() {
            return masterBPane;
      }

      public ReadOnlyDoubleProperty getRPCenterWidthProperty() {
            return rpCenter.widthProperty();
      }

      /**
       * Returns the mode 't' or 'q'
       *
       * @return
       */
      public char getMode() {
            return mode;
      }

      /**
       * returns the value of score
       *
       * @return double score
       */
      public double getScore() {
            return score.doubleValue();
      }


      /**
       * Returns a string of the current score
       *
       * @return String score
       */
      public String printScore() {
            return Double.toString(score.get() / 10);
      }

      /**
       * <p>Usage: Used when a Question is answered
       * incorrectly and the question is re-inserted into
       * the tree. </p>
       * <p>Sets the next qNumber for inserted cards.</p>
       * Checks if a qNumber is occupied/used, If so, increments the qNumber until
       * there is an available index.
       *
       * @param fc
       * @param newNum
       */
      private static int nextQNumber(FlashCardMM fc, int newNum) {
            int num = newNum % 2 == 0 ? newNum : newNum + 1;
            // ensure always an odd integer to prevent n % 10 == 0
            for (int i = 1; i < 20; i += 2) {
                  newNum = num + i;
                  fc.setCNumber(newNum);
                  if (FMTWalker.getInstance().add(fc)) {
                        break;
                  }
            }

            return newNum;
      }


      private static boolean showAnsNavBtns = false;
      private static char mode = 't';
      /**
       * NOTE: Search is handled in 'a' since it is only available
       * in the Question and Answer charM. The search bar is added to the
       * rpSouth section. The center section is replaced by the search results
       * with a hyperlink from the SearchPane class. When the EncryptedUser.EncryptedUser clicks on
       * the hyperlink, the linked card replaces the list of search results.
       * The majority of these actions are handled in SearchPane as the endpoint.
       * <p>
       * - manageMode: Manages the rpSouth gridPane where most of the buttons reside
       * t is for Test pane. ais for and qAndA pane.
       * Buttons and search are available depending on the charM entered in
       * the parameter. If the answer nav buttons should be displayed,
       * set showAnsNavBtns to true. else false.
       *
       * @param charMode char
       * @return Returns a BorderPane
       */
      public VBox manageSouthPane(char charMode) {
            scoreTextF = new TextField();
            HBox navBtnHbox = new HBox();
            navBtnHbox.setSpacing(3);
            HBox buttonBox = new HBox();
            // padding top, right, bottom, left
            buttonBox.setPadding(new Insets(0, 0, 10, 0));
            buttonBox.setSpacing(15);
            buttonBox.setAlignment(Pos.CENTER);
            FMTWalker.getInstance().getCurrentNode().getData();
//            BorderPane contrlsBPane = new BorderPane();
//            contrlsBPane.setId("studyBtnPane");

            switch (charMode) {
                  case 't': {// Test charM
                        LOGGER.debug("\n ~^~^~^~ In ManageMode 't' for test ~^~^~^~");

                        navBtnHbox.getChildren().addAll(firstQButton, prevQButton, nextQButton, endQButton);

                        scoreLabel = new Label("Points");
                        scoreTextF.setMaxWidth(50);
                        scoreTextF.setEditable(false);
                        buttonBox.getChildren().clear();
                        buttonBox.getChildren().add(navBtnHbox);
                        buttonBox.setAlignment(Pos.CENTER);

                        // Three gauges
                        HBox gauge3Box = new HBox(3);
                        gauge3Box.setAlignment(Pos.CENTER);
                        gauge3Box.getChildren().addAll(sGaugePane, pGaugePane, timerPane);
                        gauge3Box.setStyle("-fx-background-color: TRANSPARENT");

                        VBox gaugeVBox = new VBox(3);
                        gaugeVBox.setAlignment(Pos.CENTER);
                        gaugeVBox.getChildren().addAll(durationPane, gauge3Box);
                        gaugeVBox.setStyle("-fx-background-color: TRANSPARENT");


                        // Contains gauges and buttons
                        BorderPane bottomBPane = new BorderPane();
                        bottomBPane.setId("gaugeBox");
                        bottomBPane.setMaxWidth(366);
                        bottomBPane.setMaxHeight(233);
                        bottomBPane.setPadding(new Insets(10, 10, 10, 10));
                        bottomBPane.setTop(buttonBox);
                        bottomBPane.setBottom(gaugeVBox);
                        bottomBPane.setMinHeight(SceneCntl.Dim.SOUTH_BPANE_HT.get());

                        VBox bottomVBox = new VBox(6);
                        bottomVBox.setAlignment(Pos.CENTER);
                        //vBox.setPadding(new Insets(10, 10, 10, 10));
                        bottomVBox.setId("studyBtnPane");
                        bottomVBox.getChildren().addAll(bottomBPane, exitBox);
                        return bottomVBox;
                  }
                  default:
                  case 'a': {// Q&A charM
                        VBox controlsVBox = new VBox(10);
                        controlsVBox.setPadding(new Insets(20, 10, 20, 10));
                        controlsVBox.setId("gaugeBox");
                        controlsVBox.setMaxWidth(240);
                        controlsVBox.setAlignment(Pos.CENTER);

                        SearchPane searchPane = SearchPane.getInstance(rpCenter.getHeight(), rpCenter.getWidth());
                        buttonBox.getChildren().clear();

                        // Sets the results from the search pane in the
                        // center section.
                        searchPane.getSearchField().setOnAction(e -> {
                              rpCenter.getChildren().clear();
                              rpCenter.getChildren().add(searchPane.getResultPane());
                        });

                        HBox gaugeBox = new HBox();
                        gaugeBox.setSpacing(3);
                        gaugeBox.setAlignment(Pos.CENTER);
                        scoreTextF.setEditable(false);
                        gaugeBox.getChildren().addAll(pGaugePane, timerPane);

                        // Navigation buttons
                        navBtnHbox.getChildren().clear();
                        navBtnHbox.getChildren().addAll(firstQButton, prevQButton, nextQButton, endQButton);
                        navBtnHbox.setAlignment(Pos.CENTER);
                        buttonBox.getChildren().add(navBtnHbox);

                        // The guage and button at the bottom of the screen
                        // BorderPane bottomBPane = new BorderPane();
                        HBox hBox = new HBox(searchPane.getSearchBox(FMTWalker.getInstance(), FlashCardOps.getInstance().getFlashList()));
                        hBox.setAlignment(Pos.CENTER);
                        controlsVBox.setAlignment(Pos.CENTER);
                        controlsVBox.getChildren().addAll(hBox, navBtnHbox, gaugeBox);

                        VBox vBox = new VBox();
                        vBox.setId("studyBtnPane");
                        vBox.setAlignment(Pos.CENTER);
                        // Returning the bottom section BorderPane
                        vBox.getChildren().addAll(controlsVBox, exitBox);
                        return vBox;
                  }
            }
      }

      // Other methods

      // *** BUTTON COLORS ***

      /**
       * Helper method for button.setOnActions()
       * Depending on the isRight setting, if question has been answered previously
       * or if its answer has been viewed previously, it cannot be answered in test
       * mode. This method disables the selectAnswerButton as well as sets its color
       * based on if the EncryptedUser answered the question correctly = green (isRight == 1),
       * incorrect = red {@code (isRight == -1); or simply viewed in q&a mode = grey
       * (isRight == 3).}
       *
       * @param isRight this cards isRight for this session
       * @param test    The testType
       */
      protected static void ansQButtonSet(int isRight, GenericTestType test) {
            try {
                  if (isRight != 0) {
                        if(mode == 't') {
                              test.getAnsButton().setDisable(true);
                        }

                        switch (isRight) {
                              case -1: // button set to red
                              {
                                    test.getAnsButton().setStyle("-fx-background-color: RGBA(255, 0, 0, .4); -fx-text-fill: RGBA(255, 255, 255, .7);");
                                    //LOGGER.debug(" set button color orange faded \n");
                                    break;
                              }
                              case 1: // button set to green
                              {
                                    test.getAnsButton().setStyle("-fx-background-color: RGBA(0, 255, 53, .4); -fx-text-fill: RGBA(255, 255, 255, .7);");
                                    break;
                              }
                              case 3: // button greyed out,
                              {
                                    test.getAnsButton().setStyle("-fx-background-color: RGBA(202, 202, 202, .7); -fx-text-fill: RGBA(255, 255, 255, .7);");
                                    break;
                              }
                              default: {
                                    // no changes
                                    break;
                              }
                        }
                  } else {
                        test.getAnsButton().setDisable(false);
                        test.getAnsButton().setVisible(true);
                        test.getAnsButton().setStyle("-fx-background-color: RGBA(255, 255, 255, .7); -fx-text-fill: " + UIColors.FOCUS_BLUE_OPAQUE + ";");
                  }
            } catch (NullPointerException f) {
                  // print an error message
                  LOGGER.debug("\n *** Null pointer in Test *** "
                      + "\n\tselectAnswerButton may not work as"
                      + "\n\tintended");
            }
      }


      public void setShowAnsNavBtns(Boolean bool) {
            showAnsNavBtns = bool;
      }

      /**
       * Sets the next and last button visibility for the treeMap and readPane
       * helper method for setCard()
       * NOTE: Expects lowest and highest children are current.
       *
       * @param node Node that is refered to for buttons.
       */
      protected void buttonDisplay(FMTWalker.Node node) {
            LOGGER.debug("\n ***** in buttonTreeDisplay() ****");

            Boolean low = (FMTWalker.getInstance().getLowestNode() == node);
            Boolean high = (FMTWalker.getInstance().getHighestNode() == node);

            // if not equal to lowest or highest node addresses
            if ( ! (low || high)) {
                  LOGGER.debug("not low nor high");
                  nextQButton.setDisable(false);
                  endQButton.setDisable(false);
                  prevQButton.setDisable(false);
                  firstQButton.setDisable(false);
            } else if (low) // if equal to the lowest child address
            {
                  LOGGER.debug("low is true");
                  nextQButton.setDisable(false);
                  endQButton.setDisable(false);
                  prevQButton.setDisable(true);
                  firstQButton.setDisable(true);
            } else // it is the highest child
            {
                  LOGGER.debug("high is true");
                  nextQButton.setDisable(true);
                  endQButton.setDisable(true);
                  prevQButton.setDisable(false);
                  firstQButton.setDisable(false);
            }
      }


      /**
       * Resets the visIndex, qIndex, and score that are not reset by
       * reset().
       * void method
       */
      protected void resetGaugeValues() {
            LOGGER.debug("\n\n *** called resetInd *** ");
            score.set(0);
            progress = 0;
      }

      /**
       * Provides the end of session statistics along with a media
       * overlay. Called by testTypes. Set the conditions and what to
       * display here.
       */
      public void endGame() {
            leaveAction();
            int size = FlashCardOps.getInstance().getFlashList().size();
            int numTestCards = DeckMetaData.getInstance().numTestCards();
            int threshold = size / 4;
            int scoreLimit = FMTWalker.getInstance().highestPossibleScore();
            EndGame endGame = new EndGame();
            if(numTestCards > 20
                    && numTestCards > threshold
                    && score.get() >= scoreLimit
                    && mode == 't') {

                  endGame.buildAndDisplay(
                          score.get() / 10,
                          scoreLimit / 10,
                          progress,
                          FMTWalker.getInstance().getCount(),
                          FlashCardOps.getInstance().getDeckLabelName(),
                          EndGame.HONORABLE
                  );
            } else {
                  endGame.buildAndDisplay(
                          score.get() / 10,
                          scoreLimit / 10,
                          progress,
                          FMTWalker.getInstance().getCount(),
                          FlashCardOps.getInstance().getDeckLabelName(),
                          EndGame.COMPLETE
                  );
            }
      }


      // *****************************************************************************

      //                      INNER CLASSES : ANSWER ACTIONS

      // *****************************************************************************

      /**
       * Used by TestTypes that do not require a prioritization, nor a right/wrong
       * action. EG NoteTaker.
       */
      public class JustAns {
            int flashListSize = FlashCardOps.getInstance().getFlashList().size();

            public JustAns(FlashCardMM currentCard) {
                  FlashCardMM listCard = FlashCardOps.getInstance().getFlashList().get(currentCard.getANumber());
                  // Set is right in the current session,
                  currentCard.setIsRightColor(1);
                  // Update in permenent version.
                  listCard.setNumRight(listCard.getNumRight() + 1);

                  if (listCard.getNumRight() > 2) {
                        // set remember to a higher num = lower priority.
                        remAction(currentCard, listCard);
                        currentCard.setIsRightColor(1);
                        listCard.setRtDate(Threshold.getRightDate());
                        listCard.setSessionSeen(listCard.getSessionSeen() + 1);
                        currentCard.setSessionSeen(currentCard.getSessionSeen() + 1);
                  }
            }

            /**
             * Helper method
             * Sets the remember setting in the flashList for this card.
             *
             * @param currentCard
             * @param listCard
             */
            private void remAction(FlashCardMM currentCard, FlashCardMM listCard) {
                  int rem = listCard.getRemember();
                  try {
                        rem += floor(flashListSize * .33);
                        listCard.setRemember(rem);
                  } catch (NullPointerException f) {
                        //LOGGER.debug("error: null pointer in setRemember");
                  }
            }
      }


      /**
       * Inner Class:
       * Contains right answer actions. List card will be used to store the new values when the
       * session is over.
       * CurrentCard is used for the current session.
       */
      public class RightAns {
            int flashListSize = FlashCardOps.getInstance().getFlashList().size();

            public RightAns(FlashCardMM currentCard, GenericTestType genTest) {

                  FlashCardMM listCard = FlashCardOps.getInstance().getFlashList().get(currentCard.getANumber());

                  SoundEffects.CORRECT_ANSWER.play();
                  timerGauge.stop();
                  durationGauge.setConsumedTime();
                  LOGGER.debug("/n*** rightAns called ***");
                  // add points if 1st visit or greater than
                  // 1st visit.
                  if(currentCard.getCNumber() %10 == 0) {
                        score.addAndGet(genTest.score());
                  } else {
                        score.addAndGet(10);
                  }
                  currentCard.setIsRightColor(1);
                  currentCard.setNumRight(currentCard.getNumRight() + 1);

                  if (currentCard.getNumRight() > 3) {
                        // set remember in the FlashList to a higher num = lower priority.
                        // Using listCard in the case this is an inserted card from a
                        // wrong answer.
                        remAction(listCard);
                  }

                  currentCard.setRtDate(Threshold.getRightDate());
                  currentCard.setSessionSeen(currentCard.getSessionSeen() + 1);
                  ButtoniKon.getRightAns(genTest.getAnsButton(), "GOOD!");
                  // good nod
                  correctNodActions(genTest.getAnsButton(), genTest);
                  genTest.resetSelectAnsButton();
                  // Score related
                  scoreTextF.setText(printScore());
                  scoreGauge.moveNeedle(500, score.get() / 10);

                  FlashMonkeyMain.AVLT_PANE.displayTree();
                  masterBPane.setBottom(manageSouthPane(mode));
                  masterBPane.requestFocus();
            }

            /**
             * Helper action method for the answerButton
             * Nods the answerButton up and down and sets its color.
             */
            private void correctNodActions(Button selectAnsBtn, GenericTestType test) {
                  FMTransition.goodNod = FMTransition.nodUpDownFade(selectAnsBtn, 250);
                  FMTransition.goodNod.play();
            }

            /**
             * Helper method
             * Sets the remember setting in the flashList for this card.
             *
             */
            private void remAction(FlashCardMM listCard) {
                  int rem = listCard.getRemember();
                  try {
                        rem += floor(flashListSize * .33);
                        listCard.setRemember(rem);
                  } catch (NullPointerException f) {
                        //LOGGER.debug("error: null pointer in setRemember");
                  }
            }
      }


      /**
       * INNER CLASS Wrong has the actions for TESTS that adds cards back into
       * the tree.
       */
      public class WrongAns {
            // initialization of newWrongNums
            int[] newWrongNums = {-256, -256, -256};
            int flashListSize = FlashCardOps.getInstance().getFlashList().size();

            /**
             * Adds two or three cards to the tree based on if the list is larger than 10 cards. Updates the cards data in
             * the list. The list is saved to file later . Tree cards are only for this session.
             *
             * @param currentCard
             * @param test
             */
            public WrongAns(FlashCardMM currentCard, GenericTestType test) {

                  timerGauge.stop();
                  durationGauge.setConsumedTime();
                  /**  Should animate the avlTreePane, the current circle should fade to red or green depending on
                   if the answer is wright or wrong.

                   Working on setting the wrong answer actions. The selectAnswerButton and actions should be in the
                   ReadFlash class since nearly all TESTS will have an answer button and the actions should be essentialy
                   the same as far as adding them back into the tree, and setting a cards score and remember. The current plan
                   is each test class contains a SelectAnswerButton as required by the generic abstract GenericTestType class. The
                   class should contain the buttons actionClass which will use the ReadFlash class right and wrong actions.
                   */

                  FlashCardMM listCard = FlashCardOps.getInstance().getFlashList().get(currentCard.getANumber());
                  /**
                   * The first time the EncryptedUser gets the card wrong,
                   * a) subtract 2 points,
                   * b) add the card back to the tree two or three more times,
                   * c) if the EncryptedUser gets it wrong the second or third time,
                   *    Set remember & make it a priority card.
                   */
                  if (currentCard.getCNumber() % 10 == 0 && currentCard.getSessionSeen() == 0) {
                        SoundEffects.WRONG_ANSWER_1.play();
                        // Score subtract card points
                        score.addAndGet(-test.score());

                        // Add 2 or 3 cards into the deck from tree
                        int newNum = (currentCard.getCNumber() + 1);
                        firstWrong(newNum, currentCard, listCard, test);

                        // If the list length is less than 10 do not add the second card.
                        if (flashListSize > 10) {
                              secondWrong(currentCard, listCard, test);
                        }
                        thirdWrong(currentCard, listCard, test);
                        FlashMonkeyMain.AVLT_PANE.displayTree(newWrongNums);
                        FMTWalker.getInstance().setHighLow();  // treeWalker.setHighLow();
                        progGauge.setMaxVal(500, FMTWalker.getInstance().getCount());

                  } else {
                        SoundEffects.WRONG_ANSWER_2.play();
                        score.addAndGet(-10);
                        // RemAction not used until after the card
                        // has been seen more than once for this session
                        remAction(currentCard, listCard);
                  }

                  wrongNodAction(test.getAnsButton());
                  // score related
                  scoreTextF.setText(printScore());
                  scoreGauge.moveNeedle(500, score.get() / 10);
                  ButtoniKon.getWrongAns(test.getAnsButton(), "Ooops!");

                  currentCard.setIsRightColor(-1);
                  listCard.setSessionSeen(listCard.getSessionSeen() + 1);
                  currentCard.setSessionSeen(currentCard.getSessionSeen() + 1);
            }

            /**
             * @param selectAnsBtn
             */
            public void wrongNodAction(Button selectAnsBtn) {
                  selectAnsBtn.setStyle("-fx-color: #FF0000;");
                  FMTransition.badNod = FMTransition.nodLRFade(selectAnsBtn);
                  FMTransition.badNod.play();
            }


            /**
             * Helper Method to selectAnswerButton action
             * Adds the first card to the stack when the EncryptedUser answers a question incorrectly.
             * The first card is added into the stack immediately after the card answered.
             *
             * @param newNum
             * @param currentCard
             */
            private void firstWrong(int newNum, FlashCardMM currentCard, FlashCardMM listCard, GenericTestType test) {
                  listCard.setNumRight(0);
                  FlashCardMM clone = currentCard.clone();
                  clone.setSessionSeen(1);
                  clone.setCNumber(newNum);
                  if (!FMTWalker.getInstance().add(clone)) {
                        newWrongNums[0] = nextQNumber(clone, newNum);
                  } else {
                        newWrongNums[0] = newNum;
                  }
                  clone.setIsRightColor(0);
            }

            /**
             * Helper Methods for select Ans Button set on action
             * We add 25% to the QNum of the card when it is re-inserted to
             * the stack.
             */
            private void secondWrong(FlashCardMM currentCard, FlashCardMM listCard, GenericTestType test) {
                  // 2nd time card is added in, its + 25%,
                  // create the new card to be inserted into the stack
                  listCard.setNumRight(0);
                  FlashCardMM clone = currentCard.clone();
                  clone.setSessionSeen(2);
                  int fcX10 = flashListSize * 10; // = 320
                  int newNum = (((fcX10 - currentCard.getCNumber()) / 3)
                      + currentCard.getCNumber());
                  clone.setCNumber(newNum);

                  if (!FMTWalker.getInstance().add(clone) || newNum % 2 == 0) {
                        newWrongNums[1] = nextQNumber(clone, newNum);
                  } else {
                        newWrongNums[1] = newNum;
                  }
                  clone.setIsRightColor(0);
            }

            /**
             * Helper method for select Ans Button Action
             * This method adds the card at the end of the deck when the EncryptedUser answers a question
             * incorrectly.
             *
             * @param currentCard
             */
            private void thirdWrong(FlashCardMM currentCard, FlashCardMM listCard, GenericTestType test) {
                  listCard.setNumRight(0);
                  // create the new card to be inserted into the stack
                  FlashCardMM clone = currentCard.clone();
                  int newNum = (FMTWalker.getInstance().getCount() * 10) + 1;
                  clone.setCNumber(newNum);
                  clone.setSessionSeen(3);
                  clone.setIsRightColor(0);
                  if (!FMTWalker.getInstance().getInstance().add(clone) || newNum % 2 == 0) {
                        newWrongNums[2] = nextQNumber(clone, newNum);
                  } else {
                        newWrongNums[2] = newNum;
                  }
                  clone.setIsRightColor(0);
            }

            /**
             * Helper method
             * Sets the remember setting in the flashList for this card.
             *
             * @param currentCard
             * @param listCard
             */
            private void remAction(FlashCardMM currentCard, final FlashCardMM listCard) {
        	/*
            If it is wrong the first time, it is added back
            into the stack three more times.

            Is it neccessary to increase its priority?
            - When the EncryptedUser continues to get it wrong during the
            same session.
            Solution: If this question continues to be a problem, it's remember/prioritization number should
            progressively decrease until the EncryptedUser answers it correctly. This can be used
            as an indicator during analysis of the types of questions the EncryptedUser is struggling
            with, if a classroom is having problems with this question... etc...
            Once the EncryptedUser begins to answer the question correctly, RightAns will progressively
            move the remember back to the right.
            */
                  int listCardRem = listCard.getRemember();

                  // If the current card ( this card in the deck and not this card in the list )
                  // has been seen before. Then set its priority.
                  if (currentCard.getSessionSeen() > 0) {
                        try {
                              listCardRem += floor(-1 * (flashListSize) * .33);
                              listCard.setRemember(listCardRem);
                        } catch (NullPointerException f) {
                              LOGGER.warn("error: null pointer in setRemember");
                        }
                  }
            }
            // DO SOMETHING IF ANSWER BEING ADDED IS IN ansSet

            //Helper method to do something??? if this question has more than
            // one correct answer. Currently always returns true.
            private boolean inAnsAry(FlashCardMM currentCard, int ansQID) {
                  for (int i = 0; i < 4; i++) {
                        //    if() //@todo finish in Answer Array Action line 1537 ReadFlash
                  }
                  return true;
            }
      } // ********* END Wrong Class **************


      // ***** FOR TESTING *****
      @FMAnnotations.DoNotDeployMethod
      public Point2D getStudyButtonXY() {
            Bounds bounds = masterBPane.getLayoutBounds();
            return masterBPane.localToScreen(bounds.getMinX() , bounds.getMinY() );
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getPrevQButtonXY() {
            Bounds bounds = prevQButton.getLayoutBounds();
            return prevQButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getNextQButtonXY() {
            Bounds bounds = nextQButton.getLayoutBounds();
            return nextQButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getEndQButtonXY() {
            Bounds bounds = endQButton.getLayoutBounds();
            return endQButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getFirstQButtonXY() {
            Bounds bounds = firstQButton.getLayoutBounds();
            return firstQButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getQnAButtonXY() {
            Bounds bounds = qaButton.getLayoutBounds();
            return qaButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getRPCenter2dPoint() {
            Bounds bounds = rpCenter.getLayoutBounds();
            return rpCenter.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getCreateButtonXY() {
            Bounds bounds = ButtoniKon.getCreateButton().getLayoutBounds();
            return ButtoniKon.getCreateButton().localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getTestButtonXY() {
            Bounds bounds = testButton.getLayoutBounds();
            return testButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getBackToMenuBtnXY() {
            Bounds bounds = menuButton.getLayoutBounds();
            return menuButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Button getNextQButton() {
            return nextQButton;
      }

      @FMAnnotations.DoNotDeployMethod
      public Button getPrevQButton() {
            return prevQButton;
      }

      @FMAnnotations.DoNotDeployMethod
      public Button getEndQButton() {
            return endQButton;
      }

      @FMAnnotations.DoNotDeployMethod
      public Button getFirstQButton() {
            return firstQButton;
      }

}
