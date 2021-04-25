package flashmonkey;


import campaign.Report;
import ch.qos.logback.classic.Level;
import fileops.DirectoryMgr;
import fmannotations.FMAnnotations;
import fmtree.FMTWalker;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import metadata.DeckMetaData;
import org.slf4j.LoggerFactory;
import type.cardtypes.GenericCard;
import type.testtypes.*;
import type.tools.imagery.Fit;
import uicontrols.ButtoniKon;
import uicontrols.SceneCntl;

import static java.lang.Math.floor;

/*******************************************************************************
 * Provides the EncryptedUser.EncryptedUser interface to reading flash cards
 * <strong>Singleton Class. Synchronized.</strong>
 * 
 * NOTES: This class starts off asking the EncryptedUser.EncryptedUser what study mode to use. The class
 * uses the int mode variable to determine which scene to represent.
 *
 * Modification to Multi-Media & Multi - Test Sept 28th 2018
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


public final class ReadFlash {

    private volatile static ReadFlash CLASS_INSTANCE;
    private static final ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ReadFlash.class);
    

    // *** CONSTANTS ***
    protected static final GenericCard GEN_CARD = new GenericCard();


    // **** PANES **** //
    // The main scene object & pane
    private Scene masterScene;
    private static BorderPane masterBPane;// = new BorderPane();
    // Titles and messages in rpNorth
    private static HBox rpNorth;
    // Q section and A section. May also contain the graph
    protected static VBox rpCenter;// = new VBox(2);  //
    // Contains gauges, exit button, Navigation buttons etc...
    //public static BorderPane rpSouth;
    // The menu for Q&A or Test
    private static GridPane modeSelectPane = new GridPane();
    // Button box contained in southpane
    GridPane exitBox;

    // **** VARIABLES **** //
    /** The name of the active deck **/
    private static String deckName;

    // **** INDEXES **** //
    /** the visible number displayed to the EncryptedUser.EncryptedUser for the card */
    //protected static int visIndex = 1;
    /** The last score total **/
    private static double score = 0.0;

    // *** BUTTONS *** //
    private static Button exitButton;
    /** The beginning of the tree **/
    protected static Button firstQButton;
    private static Button endQButton;
    private static Button nextQButton;
    private static Button prevQButton;
    //private static Button createButton;

    /** Back button .. returns to the QorA/Test menu **/
    protected static Button menuButton;// = MENU.get();
    

    /** question and answer mode buttons*/
    private Button qaButton;
    private Button testButton;
    private Button deckSelectButton;
    // private static boolean hint = false;
    /** MENU BUTTONS **/
    /** resets flashList and sends EncryptedUser.EncryptedUser to the firstScene **/
    // protected static Button fileSelectButton = new Button(" < | Study deck selection");

    // *** LABELS ***
    private static Label deckNameLabel;// = new Label(FC_OBJ.FO.getFileName());
    protected static Label message; // = new Label();
    private static Label scoreLabel; //?????

    // *** TEXT FIELDS ***
    private static TextField scoreTextF = new TextField();

    // *** PROGRESS BARS & INDICATORS ***    
    /** Calculation for the progress displayed to the EncryptedUser.EncryptedUser
     Only answer clicks are counted, not forward or backward buttons */
    private static int   progress = 0;
    private static MGauges  progGauge;// = new MGauges(100, 66.3);
    private static MGauges  scoreGauge;
    private static GridPane pGaugePane;
    private static GridPane sGaugePane;


    /**
     * Private Constructor
     */
    private ReadFlash() {
        deckName = "default";
    }

    /**
     * Returns an instance of the class. If getInstance hasn't been called before
     * a new instance of the class is created. otherwise it returns the existing
     * instance.
     * To intialize: ReadFlash readFlashObj = ReadFlash.getInstance();
     * @return ReadFlash
     */
    public static synchronized ReadFlash getInstance() {
        if(CLASS_INSTANCE == null) {
            CLASS_INSTANCE = new ReadFlash();
        }
        return CLASS_INSTANCE;
    }
     
    /**
     * The createRead Scene class creates the flashcard scene.
     *  UI FLow: The user enters the scene from the MenuPane/modeSelectPane after
     * clicking on the study button.
     * 1) The first scene the EncryptedUser.EncryptedUser sees asks
     * them what study mode to use.
     *  a) There is Q and A session which is a
     * normal Flash card mode. The pane presents the 1st question and the
     * EncryptedUser.EncryptedUser clicks for the answer or the next question. The users can
     * traverse forward or backwards through the questions. There is no
     * scoring associated with QAndA mode.
     *  b) The Test session is the test mode. In test mode the
     * EncryptedUser.EncryptedUser is presented a question and one of the test cards.
     */
    public Scene readScene() {
        LOGGER.info("\n\n **** createReadScene() called ****");
        LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("Set ReadFlash LOGGER to debug");



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
    
        exitButton = ButtoniKon.getExitButton();
        menuButton = ButtoniKon.getMenuButton();
        exitBox = new GridPane(); // HBox with spacing provided
        exitBox.setHgap(2);

        /* For the lower panel on modeSelectPane window */
        ColumnConstraints col0 = new ColumnConstraints();
        col0.setPercentWidth(50);
        exitBox.getColumnConstraints().add(col0);
        exitBox.setVgap(2);

        exitBox.setPadding(new Insets(15, 15, 15, 15));
        exitBox.addColumn(1, menuButton);
        exitBox.addColumn(2, exitButton);
        exitBox.setId("buttonBox");
        
        deckNameLabel = new Label(getDeckName());
        deckNameLabel.setId("label16");

        rpNorth.getChildren().add(deckNameLabel);
        

        // The study menu pane
        LOGGER.debug("setting window to modePane");
        masterBPane.setCenter(modeSelectPane);
        masterBPane.setBottom(exitBox);
        masterBPane.setId("readFlashPane");

        masterScene = new Scene(masterBPane, SceneCntl.getWd(), SceneCntl.getHt());

        LOGGER.debug("readflash masterBPane width: " + masterBPane.widthProperty());
        
        // *** BUTTON ACTIONS ***
        menuButton.setOnAction(e -> FlashMonkeyMain.setWindowToNav());
        exitButton.setOnAction(e -> exitAction() );
        qaButton.setOnAction((ActionEvent e) -> qaButtonAction());
        testButton.setOnAction((ActionEvent e) -> testButtonAction());
        /*
        * Navigation buttons, handles actions for first, last, next, and previous
        * buttons.
         */
        nextQButton.setOnAction((ActionEvent e) -> nextQButtonAction());
        prevQButton.setOnAction((ActionEvent e) -> prevQButtonAction());
        endQButton.setOnAction(this::endQButtonAction);
        firstQButton.setOnAction(this::firstQButtonAction);
        /*
         * shortCut Key actions
         */
        masterBPane.setOnKeyPressed(this::shortcutKeyActions);
        masterScene.getStylesheets().addAll("css/mainStyle.css", "css/buttons.css");

        return masterScene;

    }    // ******** END OF READ SCENE ********

    private void preRead() {
        testButton = ButtoniKon.getTestButton();
        qaButton = ButtoniKon.getQandAButton();

        FlashCardOps fcOps = FlashCardOps.getInstance();
        // If the flashList is empty/ or not,
        // reset the flashList, save first
        // if not empty.
        if(fcOps.getFlashList() == null || fcOps.getFlashList().isEmpty()) {
            fcOps.refreshFlashList();
        } else {
            fcOps.saveFlashList();
            fcOps.refreshFlashList();
        }

        if(fcOps.getFlashList().size() < 4) {
            emptyListAction();
        } else {
            modeSelectPane = listHasCardsAction();
            setTree();
        }
    }



    /**
     * The shortCutKeys for ReadFlash actions.
     * Helper method to createReadScene()
     * @param e
     */
    private void shortcutKeyActions(KeyEvent e) {
        FlashCardMM currentCard = (FlashCardMM) FMTWalker.getCurrentNode().getData();
        GenericTestType test = TestList.selectTest( currentCard.getTestType() );

        if (e.isAltDown() || e.isShiftDown()) {

            if(e.getCode() == KeyCode.ENTER)
            {
                LOGGER.debug("Enter pressed for testAnswer");
                test.ansButtonAction();
                masterBPane.requestFocus();
            }
            else if (e.getCode() == KeyCode.LEFT)
            {
                LOGGER.debug("Left pressed for leftAnswer");

                test.prevAnsButtAction();
                masterBPane.requestFocus();

            }
            else if (e.getCode() == KeyCode.RIGHT)
            {
                LOGGER.debug("Right pressed for rightAnswer");
                test.nextAnsButtAction();
                masterBPane.requestFocus();
            }
        }
        else if(e.getCode() == KeyCode.ENTER)
        {
            LOGGER.debug("Enter pressed for testAnswer w/o alt or shift");
            test.ansButtonAction();
            masterBPane.requestFocus();
        }
        else if (e.getCode() == KeyCode.LEFT)
        {
            prevQButtonAction();
            masterBPane.requestFocus();
        }
        else if (e.getCode() == KeyCode.RIGHT)
        {
            nextQButtonAction();
            masterBPane.requestFocus();
        }
    }


    /**
     * Helper Method to ReadScene. Action when list has cards.
     * Builds tree, and sets values in appropriate panes.
     * @return
     */
    private GridPane listHasCardsAction()
    {
        LOGGER.debug("listHasCardsAction() called");

        int flSize;
        flSize = FlashCardOps.getInstance().getFlashList().size();

        progGauge = new MGauges(100, 100);
        scoreGauge = new MGauges(100, 100);
        pGaugePane = progGauge.makeGauge("COMPLETED", 0, (flSize - 1), 0);
        sGaugePane = scoreGauge.makeGauge("SCORE", 0, flSize * 2, -1 * (flSize * 2));
        progGauge.moveNeedle(500, flSize - 1);
        scoreGauge.moveNeedle(500, flSize * 2);
        progGauge.moveNeedle(500, 0);
        scoreGauge.moveNeedle(500, 0);

        Label label = new Label("Study Mode");
        label.setId("label24White");
        GridPane gPane = new GridPane();
        VBox buttonBox = new VBox(2);
        buttonBox.setId("studyModePane");
        buttonBox.setAlignment(Pos.TOP_CENTER);
        buttonBox.getChildren().addAll(label, qaButton, testButton);

        gPane.setId("rightPaneTransp");
        gPane.setAlignment(Pos.CENTER);
        gPane.addRow(0, buttonBox);

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
     * NOTE: Set the the String errorMsg in FlashMonkeyMain. and
     * provide FlashMonkeyMain.errorMsg in the argument.
     */
    public void emptyListAction() {
        Button createButton = ButtoniKon.getCreateButton();
        
        modeSelectPane.setAlignment(Pos.CENTER);
        String imgPath = DirectoryMgr.EMOJI + "flashFaces_wrong_answer.png";

        LOGGER.debug("emoji file path: " + imgPath);

        ImageView iView;

        Image image = new Image( imgPath );
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
        
        modeSelectPane.getChildren().clear();
        modeSelectPane.addRow(0, iView);
        modeSelectPane.addRow(1, message);
        modeSelectPane.addRow(2, createButton);
        modeSelectPane.addRow(3, deckSelectButton);
    }

    /**
     * The previous question button action
     */
    @SuppressWarnings("rawTypes")
    private void prevQButtonAction()
    {
        // Get the previous node in treeWalker
        FMTWalker.getInstance().getPrevious();

        //Node currentNode = FMTWalker.getCurrentNode();
        FlashCardMM currentCard = (FlashCardMM) FMTWalker.getCurrentNode().getData();

        rpCenter.getChildren().clear();
        // All of the work is accessed from selectTest()
        GenericTestType test = TestList.selectTest( currentCard.getTestType() );

        if(mode == 't') {
            rpCenter.getChildren().add(test.getTReadPane(currentCard, GEN_CARD, rpCenter));
        } else {
            rpCenter.getChildren().add(QandA.QandASession.getInstance().getTReadPane(currentCard, GEN_CARD, rpCenter));
        }
        masterBPane.setBottom(manageSouthPane(mode));

        if(test.getAnsButton() != null) {
            test.getAnsButton().setDisable(false);
            FMTransition.nodeFadeIn = FMTransition.ansFadePlay(test.getAnsButton(), 1, 750, false);
            FMTransition.nodeFadeIn.play();
            //test.getAnsButton().setText("");
        }

        // Set the status of the answer button. A question should only
        // be answered once
        ansQButtonSet(currentCard.getIsRight(), test);
        //setAnsButtonStatus(currentCard.getIsRight(), test);

        buttonTreeDisplay(FMTWalker.getCurrentNode());
        FlashMonkeyMain.AVLT_PANE.displayTree();
        // The animation for this button
        FMTransition.getQLeft().play();
        if(FMTransition.getAWaitTop() != null) {
            FMTransition.getAWaitTop().play();
        }
    }


    /**
     * The next question button action
     * - Get the next card from the tree,
     *  - read the cards bitset and get the test
     *  -
     */
    private void nextQButtonAction() {
        // Get the next node in the treeWalker
        FMTWalker.getInstance().getNext();

        // Node currentNode = FMTWalker.getCurrentNode();
        FlashCardMM currentCard = (FlashCardMM) FMTWalker.getCurrentNode().getData();

        rpCenter.getChildren().clear();
        // All of the work is accessed from selectTest()
        GenericTestType test = TestList.selectTest( currentCard.getTestType() );
        
        if(mode == 't') {
            rpCenter.getChildren().add(test.getTReadPane(currentCard, GEN_CARD, rpCenter));
        } else {
            rpCenter.getChildren().add(QandA.QandASession.getInstance().getTReadPane(currentCard, GEN_CARD, rpCenter));
        }
        masterBPane.setBottom(manageSouthPane(mode));
    
        if(test.getAnsButton() != null) {
            test.getAnsButton().setDisable(false);
            FMTransition.nodeFadeIn = FMTransition.ansFadePlay(test.getAnsButton(), 1, 750, false);
            FMTransition.nodeFadeIn.play();
            //test.getAnsButton().setText("");
        }

        // Set the status of the answer button. A question should only
        // be answered once
        ansQButtonSet(currentCard.getIsRight(), test);

        buttonTreeDisplay( FMTWalker.getCurrentNode());
        // The animation for this button
        FMTransition.getQRight().play();
        if(FMTransition.getAWaitTop() != null) {
            FMTransition.getAWaitTop().play();
        }
        FlashMonkeyMain.AVLT_PANE.displayTree();
        buttonTreeDisplay(FMTWalker.getCurrentNode());
    }


    //@todo change qNavButtonAction() to private in readflash.
    public void qNavButtonAction() {
        LOGGER.debug("\n *** qNavButtonAction ***");
        buttonTreeDisplay(FMTWalker.getCurrentNode());
    }

    /**
     * Sends the treedisplay and the flashcard panes to the
     * first question in the tree.
     */
    private void firstQButtonAction(ActionEvent e)
    {
        //visIndex = 1;
        FMTWalker.getInstance().setToFirst();

        FlashCardMM currentCard = (FlashCardMM) FMTWalker.getCurrentNode().getData();

        rpCenter.getChildren().clear();
        GenericTestType test = TestList.selectTest( currentCard.getTestType() );
        if(mode == 't') {
            rpCenter.getChildren().add(test.getTReadPane(currentCard, GEN_CARD, rpCenter));
        } else {
            rpCenter.getChildren().add(QandA.QandASession.getInstance().getTReadPane(currentCard, GEN_CARD, rpCenter));
        }
        masterBPane.setBottom(manageSouthPane(mode));

        // answer button transitions
        if(test.getAnsButton() != null) {
            test.getAnsButton().setDisable(false);
            FMTransition.nodeFadeIn = FMTransition.ansFadePlay(test.getAnsButton(), 1, 750, false);
            FMTransition.nodeFadeIn.play();
            //test.getAnsButton().setText("");
        }

        // Set the status of the answer button. A question should only
        // be answered once
        // setAnsButtonStatus(currentCard.getIsRight(), test);
        ansQButtonSet(currentCard.getIsRight(), test);

        FlashMonkeyMain.AVLT_PANE.displayTree();
        // Animations
        FMTransition.getQLeft().play();
        if(FMTransition.getAWaitTop() != null) {
            FMTransition.getAWaitTop().play();
        }
        buttonTreeDisplay(FMTWalker.getCurrentNode());
    }

    /**
     * sends the flashCard pane to the last flashCard that is displayed in the
     * tree. The firthest right card.
     */
    private void endQButtonAction(ActionEvent e) {
        //visIndex = FLASH_CARD_OPS.getFlashList().size();
        FMTWalker.getInstance().setToLast(); // O(log(n))

        //Node currentNode = TR.getCurrentNode();
        FlashCardMM currentCard = (FlashCardMM) FMTWalker.getCurrentNode().getData();

        rpCenter.getChildren().clear();
        GenericTestType test = TestList.selectTest( currentCard.getTestType() );
        if(mode == 't') {
            rpCenter.getChildren().add(test.getTReadPane(currentCard, GEN_CARD, rpCenter));
        } else {
            rpCenter.getChildren().add(QandA.QandASession.getInstance().getTReadPane(currentCard, GEN_CARD, rpCenter));
        }
        masterBPane.setBottom(manageSouthPane(mode));

        if(test.getAnsButton() != null) {
            test.getAnsButton().setDisable(false);
            FMTransition.nodeFadeIn = FMTransition.ansFadePlay(test.getAnsButton(), 1, 750, false);
            FMTransition.nodeFadeIn.play();
        }

        // Set the status of the answer button. A question should only
        // be answered once
        //setAnsButtonStatus(currentCard.getIsRight(), test);
        ansQButtonSet(currentCard.getIsRight(), test);

        FlashMonkeyMain.AVLT_PANE.displayTree();
        // Animations
        FMTransition.getQRight().play();
        if(FMTransition.getAWaitTop() != null) {
            FMTransition.getAWaitTop().play();
        }
        buttonTreeDisplay(FMTWalker.getCurrentNode());
    }

    /**
     * Sets the answerButton status to either enabled,
     * or disabled based on a cards isRight status. 0
     * is unanswered, anything else it has been answered.
     * @param isRight The status of this question in this session
     * @param test The TestType for this question.
     */
    private void setAnsButtonStatus(int isRight, GenericTestType test) {
        if(isRight == 0) {
            if (test.getAnsButton() != null) {
                test.getAnsButton().setDisable(false);
                FMTransition.nodeFadeIn = FMTransition.ansFadePlay(test.getAnsButton(), 1, 750, false);
                FMTransition.nodeFadeIn.play();
            }
        } else {
            test.getAnsButton().setDisable(true);
        }
    }

    /**
     * Increment the progress count. Used by
     * progress gauge.
     * @return
     */
    public double incProg()
    {
        LOGGER.debug("\n\n *** called incProg ***Progress: " + (progress + 1));

        return ++progress;
    }

    /**
     * Reset the score and progress counts
     */
    public void resetScoreNProg() {

        LOGGER.debug("\n\n *** called resetScoreNProg() ***");

        score = 0;
        progress = 0;
    }

    public MGauges getProgGauge() {
        return progGauge;
    }

    public double getProgress() {
        return progress;
    }


    /**
     * <p><b>Returning to the main pane resets and saves data from the current
     * session.</b></p>
     * File Select Button Action: Sends the user back to the first scene where
     * they can select another flashDeck/file. The flashList is saved, then
     * reset to zero elements. MetaData is saved and sent to the DB. The AGR File list
     * is recreated from new showing any new files.
     */
    protected void deckSelectButtonAction()
    {
        // save metadata and
        // send metadata to the db.
        // save flashlist changes.
	    leaveAction();
        LOGGER.debug("call to fileSelectButtonAction()");
        FlashCardOps fcOps = FlashCardOps.getInstance();
        // clear the flashlist
        fcOps.clearFlashList();
        // get a new list of files
        fcOps.resetAgrList();
        // show fileSelectPane()
        FlashMonkeyMain.getFileSelectPane();
    }

    /**
     * TEST BUTTON ACTION:  provides the actions for the test button scene
     * Void method. testButton exists in the second menu after the study button
     * has been pressed by the EncryptedUser.EncryptedUser.
     *
     * <p><b>Note: </b> The xpected Result... multi choice example:
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
    protected void testButtonAction()
    {

        LOGGER.info(" testButtonAction called ");
        Timer.getClassInstance().startTime();

        if( !rpCenter.getChildren().isEmpty()) {
            rpCenter.getChildren().clear();
        }

        buttonTreeDisplay(FMTWalker.getCurrentNode());
        FlashCardMM currentCard = (FlashCardMM) FMTWalker.getCurrentNode().getData();
        
        System.err.println("\tCurrentCard data is null? " + (currentCard.getQText() == null));
        mode = 't';

        // All of the work is accessed from selectTest()
        GenericTestType test = TestList.selectTest( currentCard.getTestType() );
        rpCenter.getChildren().add(test.getTReadPane(currentCard, GEN_CARD, rpCenter));

        VBox topVBox = new VBox();
        Label deckNameLabel = new Label("Flash Deck:  " + deckName);
        deckNameLabel.setId("label16");
        topVBox.getChildren().add(deckNameLabel);
 //       topVBox.setStyle("-fx-background-color: " + UIColors.GRAPH_BGND);
        topVBox.setAlignment(Pos.CENTER);

        masterBPane.setTop(topVBox);
        masterBPane.setCenter(rpCenter);
        masterBPane.setBottom(manageSouthPane('t'));

        if( ! FlashMonkeyMain.treeWindow.isShowing()) {
            FlashMonkeyMain.buildTreeWindow();
        }
        FlashMonkeyMain.AVLT_PANE.displayTree();
    }


    /**
     * QAndA BUTTON ACTION
     * qaButton Action method: Question and Answer button action
     * Void method
     * This action sets the scene to the simple question and answer mode
     * Uses treeWalker
     */

    protected void qaButtonAction()
    {
        if(! rpCenter.getChildren().isEmpty()) {
            rpCenter.getChildren().clear();
        }
    
        buttonTreeDisplay(FMTWalker.getCurrentNode());
        FlashCardMM currentCard = (FlashCardMM) FMTWalker.getCurrentNode().getData();
        rpCenter.getChildren().add( QandA.QandASession.getInstance().getTReadPane(currentCard, GEN_CARD, rpCenter));
        //studyButton.setText("Back");
        masterBPane.setCenter(rpCenter);
        mode = 'q';
        masterBPane.setBottom(manageSouthPane('q'));

        if( ! FlashMonkeyMain.treeWindow.isShowing()) {
            FlashMonkeyMain.buildTreeWindow();
        }

        FlashMonkeyMain.AVLT_PANE.displayTree();
    }

    public void leaveAction() {
        DeckMetaData meta = DeckMetaData.getInstance();
        
        try {
            LOGGER.debug("trying to enter data to the DB for exit event");

            Timer.getClassInstance().testTimeStop();
            
            // May be an uneccessry call
            // since we only update test score.
            meta.setDataFmFile();
            meta.addTestData(progress, (int) score);
            // set metadata to array
            meta.updateDataMap();
            // save metadata to file
            //meta.saveDeckMetaData(deckName);
            FlashCardOps.getInstance().getFO().setMetaInFile(meta, deckName);
            // send test metadata to database
            Report.getInstance().reportTestMetaData(meta.getDataMap());
            Report.getInstance().endSessionTime();

        } catch(Exception f) {
            LOGGER.error("Error when exiting: ");
            f.printStackTrace();
        } finally {
            
            // MAY CAUSE ISSUES WITH SAVING ON LEAVING
            
            meta.close();
            FlashCardOps.getInstance().saveFlashList();
	        meta = DeckMetaData.getInstance();
        }
    }

    public void exitAction() {
        leaveAction();
        System.exit(0);
    }

    // *** SETTERS *** //
       
    /**
     * Adds the value in the score to the value in the argument
     * @param num
     */
    public void addScore(double num)
    {
    	score += num;
    }

    /**
     * Subtracts the value in the arugement from the
     * score.
     * @param num
     */
    public void subtractScore(double num)
    {
        score -= num;
    }

    /**
     * Sets the progress to the value in the argument
     * @param num
     */
    public void setProgress(int num)
    {
        progress = num;
    }

    /**
     *
     */
    public void setDeckName(String name) {
        deckName = name;

     //   LOGGER.info("\n *^*^* in ReadFlash.setDeckname() *^*^*\n\tdeckName: " + deckName + "\n");
     //   if(!name.contains("A new deck on s3")) {
     //       System.out.println("Calling method. ???: ");
     //       Thread.currentThread().dumpStack();
     //       System.exit(0);
     //   }
    }

    /**
     * Sets the display message to the String given in the
     * arguement.
     * @param str
     */
    protected void setMessage(String str)
    {
        message.setText(str);
    }

    /**
     * Mode controls the bottom pane and what buttons,
     * and gauges are located there.
     * @param m
     */
    public void setMode(char m) { mode = m; }
    
    
    // *** GETTERS *** //

    public Pane getMasterBPane() {
        return this.masterBPane;
    }
    public Pane getRPCenter() {
        return rpCenter;
    }

    /**
     * Returns the mode 't' or 'q'
     * @return
     */
    public char getMode() { return mode; }
    
    /**
     * returns the value of score
     * static method
     * @return double score
     */
    public double getScore()
    {
    	return score;
    }

    /**
     * Returns the current deckName
     * @return
     */
    public String getDeckName() {
        LOGGER.debug("\n *^*^* in ReadFlash.getDeckname() *^*^*\n\tdeckName: " + deckName + "\n");
        return deckName;
    }
    
    
    /**
     * Returns a string of the current score
     * @return String score
     */
    public String printScore()
    {
    	return Double.toString(score);
    }
 
    /**
     * Checks if an qNumber is occupied, If so, increments the qNumber until 
     * there is an available index. 
     * @param fc
     * @param newNum 
     */
    private static int addPlus(FlashCardMM fc, int newNum)
    {
        int num = newNum % 2 == 0 ? newNum : newNum + 1;
        
        for(int i = 1; i < 20; i += 2 ) {
            newNum = num + i;
            //newNum = newNum % 2 == 0 ? newNum++ : newNum;
            System.out.println("newNum: " + newNum);
            fc.setCNumber(newNum);
            if(FMTWalker.getInstance().add(fc)) {
                break;
            }
        }

        return newNum;
    }


    private static boolean showAnsNavBtns = false;
    private static char mode = 'q';
    /**
     * NOTE: Search is handled in 'Q' since it is only available
     * in the Question and Answer mode. The search bar is added to the
     * rpSouth section. The center section is replaced by the search results
     * with a hyperlink from the SearchPane class. When the EncryptedUser.EncryptedUser clicks on
     * the hyperlink, the linked card replaces the list of search results.
     * The majority of these actions are handled in SearchPane as the endpoint.
     *
     * - manageMode: Manages the rpSouth gridPane where most of the buttons reside
     * T is for Test pane. Q is for and qAndA pane, and E is for edit pane.
     * Buttons and search are available depending on the mode entered in
     * the parameter. If the answer nav buttons should be displayed,
     * set showAnsNavBtns to true. else false.
     * @param mode char
     * @return Returns a BorderPane
     */
    public VBox manageSouthPane(char mode)
    {
        HBox navBtnHbox = new HBox();
        navBtnHbox.setSpacing(3);
        HBox buttonBox = new HBox();
        // padding top, right, bottom, left
        buttonBox.setPadding(new Insets(0, 0, 10, 0));
        buttonBox.setSpacing(15);

        FMTWalker.getCurrentNode().getData();
        BorderPane contrlsBPane = new BorderPane();
        contrlsBPane.setStyle("-fx-background-color: #29abe2");

        //contrlsBPane.setMaxHeight(SceneCntl.getContrlPaneHt());

        switch(mode)
        {
            case 't': // Test mode
            {
                LOGGER.debug("\n ~^~^~^~ In ManageMode set mode to 'T' ~^~^~^~");

                scoreLabel = new Label("Points");
                scoreTextF.setMaxWidth(50);
                
                
                HBox boxR = new HBox();
                buttonBox.getChildren().clear();
                navBtnHbox.getChildren().addAll(firstQButton, prevQButton, nextQButton, endQButton);

                // If this is a multichoice test, set showAnsNavBtns to true
     //           if(showAnsNavBtns) {
     //               boxR.getChildren().addAll(TESTS[testsIdx].getAnsButtons());
     //           }

                buttonBox.getChildren().addAll(navBtnHbox, boxR);
                HBox scoreBox = new HBox();
                scoreBox.setPadding(new Insets(0, 10, 10, 10));
                scoreBox.setSpacing(10);
                scoreTextF.setEditable(false);

                scoreBox.getChildren().addAll(sGaugePane, pGaugePane);
    
                BorderPane bottomBPane = new BorderPane();
                bottomBPane.setPadding(new Insets(20, 10, 10, 10));
                bottomBPane.setLeft(buttonBox);
                bottomBPane.setRight(scoreBox);
                //bottomBPane.setLeft(studyButton);
                bottomBPane.setMaxHeight(SceneCntl.getSouthBPaneHt());
                bottomBPane.setMinHeight(SceneCntl.getSouthBPaneHt());
                //bottomBPane.setBottom(exitBox);
                //contrlsBPane.setPadding(new Insets(20, 0, 0, 0));
                //contrlsBPane.setTop(buttonBox);
                //contrlsBPane.setBottom(bottomBPane);
                VBox vBox = new VBox();
                vBox.setId("studyBtnPane");
                vBox.getChildren().addAll(bottomBPane, exitBox);
                return vBox;
            }
            default:
            case 'a': // Q&A mode
            {
                SearchPane searchPane = SearchPane.getInstance(rpCenter.getHeight(), rpCenter.getWidth());
                
                // contrlsBPane.setPadding(new Insets(10, 0, 0, 0));
                // Set search field in top section of bottom area
                // contrlsBPane.setTop(searchPane.getSearchBox(FMTWalker.getInstance(), FlashCardOps.getInstance().getFlashList() ));
                buttonBox.getChildren().clear();

                // Sets the results from the search pane in the
                // center section.
                searchPane.getSearchField().setOnAction(e ->
                {
                    rpCenter.getChildren().clear();
                    rpCenter.getChildren().add(searchPane.getResultPane());
                });

                // Navigation buttons
                navBtnHbox.getChildren().clear();
                navBtnHbox.getChildren().addAll(firstQButton, prevQButton, nextQButton, endQButton);
                buttonBox.getChildren().add(navBtnHbox);

                contrlsBPane.setCenter(buttonBox);
                
                // The guage and button at the bottom of the screen
         //       HBox bottom = new HBox();
                BorderPane bottomBPane = new BorderPane();
                bottomBPane.setPadding(new Insets(20, 10, 10, 10));
                //bottom.setSpacing(10);
                bottomBPane.setTop(searchPane.getSearchBox(FMTWalker.getInstance(), FlashCardOps.getInstance().getFlashList() ));
                bottomBPane.setCenter(navBtnHbox);
                bottomBPane.setRight(pGaugePane);
                //bottomBPane.setBottom(exitBox);
                VBox vBox = new VBox();
                vBox.setId("studyBtnPane");
                contrlsBPane.setBottom(bottomBPane);

                // Returning the bottom section BorderPane
                vBox.getChildren().addAll(bottomBPane, exitBox);
                return vBox;
            }
        }
        /** Set the selected mode to master bottom pane **/
        //masterBPane.setBottom(contrlsBPane);
    }
    
    // Other methods

    // *** BUTTON COLORS ***
    /**
     * Helper method for button.setOnActions()
     * Depending on the isRight setting, if question has been answered previously
     * or if its answer has been viewed previously, it cannot be answered in test
     * mode. This method disables the selectAnswerButton as well as sets its color
     * based on if the EncryptedUser.EncryptedUser answered the question correctly = green (isRight == 1),
     * incorrect = red (isRight == -1); or simply viewed in q&a mode = grey
     * (isRight == 3).
     * @param isRight this cards isRight for this session
     * @param test The testType
     */
    protected static void ansQButtonSet(int isRight, GenericTestType test)
    {
        try
        {
            // Not all test types have an answer button.
            //test.getAnsButton().setText("");
        //    ButtoniKon.ANS_SELECT.getJustAns(test.getAnsButton(), "Select");
            
            if(isRight != 0) {
                test.getAnsButton().setDisable(true);

                switch(isRight)
                {
                    case -1: // button set to red
                    {
                        test.getAnsButton().setStyle("-fx-background-color: RGBA(255, 0, 0, .4); -fx-text-fill: RGBA(255, 255, 255, .7);");
                        //LOGGER.debug(" set button color orange faded \n");
                        break;
                    }
                    case 1: // button set to green
                    {
                        test.getAnsButton().setStyle("-fx-background-color: RGBA(0, 255, 53, .4); -fx-text-fill: RGBA(255, 255, 255, .7);");
                        //FMTransition.goodNod = FMTransition.nodUpDownFade(selectAnswerButton);
                        //FMTransition.goodNod.play();
                        //LOGGER.debug(" set button color green faded \n");
                        break;
                    }
                    case 3: // button greyed out,
                    {
                        test.getAnsButton().setStyle("-fx-background-color: RGBA(202, 202, 202, .7); -fx-text-fill: RGBA(255, 255, 255, .7);");
                        //LOGGER.debug(" set button color grey faded \n");
                        break;
                    }
                    default:
                    {
                        // no changes
                        break;
                    }
                }
            }
            else {
                test.getAnsButton().setDisable(false);
                test.getAnsButton().setVisible(true);
                test.getAnsButton().setStyle("-fx-color: #FFFFFF;");
            }
        }
        catch (NullPointerException f) {
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
     * @param node Node that is refered to for buttons. 
     */
    protected void buttonTreeDisplay(FMTWalker.Node node) {
        LOGGER.debug("\n ***** in buttonTreeDisplay() ****");

            Boolean low  = (FMTWalker.getLowestNode()  == node);
            Boolean high = (FMTWalker.getHighestNode() == node);

            // if not equal to lowest or highest node addresses
            if( !(low || high))
            {
                LOGGER.debug("not low nor high");
                nextQButton.setDisable(false);
                endQButton.setDisable(false);
                prevQButton.setDisable(false);
                firstQButton.setDisable(false);
            }
            else if(low) // if equal to the lowest child address
            {
                LOGGER.debug("low is true");
                nextQButton.setDisable(false);
                endQButton.setDisable(false);
                prevQButton.setDisable(true);
                firstQButton.setDisable(true);
            }
            else // it is the highest child
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
    protected void resetInd() {
        LOGGER.debug("\n\n *** called resetInd *** ");
        score = 0.0;
        progress = 0;
    }

    public void endGameStats() {

    }

    /**
     * Provides the end of session statistics along with a media
     * overlay.
     */
    public void endGame() {

        // @todo finisher video for endGame call
        String mediaFullPath = null;
        EndGame endGame = new EndGame( getScore(), FMTWalker.getCount(), progress, deckName);
        rpCenter.getChildren().clear();
        rpCenter.getChildren().add(endGame.getPane());
    }


    /**
     * Inner Class:
     * Contains right answer actions. List card will be used to store the new values when the
     * session is over.
     * CurrentCard is used for the current session.
     */
    public class RightAns
    {
        int flashListSize = FlashCardOps.getInstance().getFlashList().size();

         public RightAns(FlashCardMM currentCard, FlashCardMM listCard, GenericTestType genTest) {
             LOGGER.debug("/n*** rightAns called ***");


             if(currentCard.getNumSeen() == 0 ) {// this is the first time this session
                 score += 2;
                 currentCard.setIsRight(1);
                 genTest.getAnsButton().setText(" Sweet ");
             // change it's priority to lower priority.
             } else {

                 // Add 1 point to the score. Makes it possible to get a higher score
                 // if the EncryptedUser.EncryptedUser answers the question the 1st time incorrectly. Then gets
                 // the next 3 questions correct.
                 score += 1;
                 genTest.getAnsButton().setText(" Yeah ");
             }

             listCard.setNumRight(listCard.getNumRight() + 1);

             if( listCard.getNumRight() > 3 ) {
                 remAction(currentCard, listCard);
             }

             currentCard.setIsRight(1);

             listCard.setRtDate(Threshold.getRightDate());
             listCard.setNumSeen(listCard.getNumSeen() + 1);
             currentCard.setNumSeen(currentCard.getNumSeen() + 1);
             ButtoniKon.getRightAns(genTest.getAnsButton(), "Sweet!");
             // good nod
             correctNodActions(genTest.getAnsButton());
             // Score related
             scoreTextF.setText(printScore());
             scoreGauge.moveNeedle(500, getScore());

             FlashMonkeyMain.AVLT_PANE.displayTree();
             masterBPane.setBottom(manageSouthPane(mode));
             masterBPane.requestFocus();
         }

         /**
          * Helper action method for the answerButton
          * Nods the answerButton up and down and sets its color.
          */
        private void correctNodActions(Button selectAnsBtn) {
            FMTransition.goodNod = FMTransition.nodUpDownFade(selectAnsBtn, 250);
            FMTransition.goodNod.play();
        }

        /**
         * Helper method
         * Sets the remember setting in the flashList for this card.
         * @param currentCard
         * @param listCard
         */
        private void remAction(FlashCardMM currentCard, FlashCardMM listCard) {
            int rem = listCard.getRemember();
            try {
                rem += floor(flashListSize * .33);
                listCard.setRemember(rem);
            }
            catch(NullPointerException f) {
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
         * @param currentCard
         * @param listCard
         * @param test
         */
        public WrongAns(FlashCardMM currentCard, FlashCardMM listCard, GenericTestType test)
        {

            /**    Thinking should animate the avlTreePane, the curent circle should fade to red or green depending on
                if the answer is wright or wrong.

            Working on setting the wrong answer actions. The selectAnswerButton and actions should be in the
            ReadFlash class since nearly all TESTS will have an answer button and the actions should be essentialy
            the same as far as adding them back into the tree, and setting a cards score and remember. The current plan
            is each test class contains a SelectAnswerButton as required by the generic abstract GenericTestType class. The
            class should contain the buttons actionClass which will use the ReadFlash class right and wrong actions.
            */

            /**
             * The first time the EncryptedUser.EncryptedUser gets the card wrong,
             * a) subtract 2 points,
             * b) add the card back to the tree two or three more times,
             * c) if the EncryptedUser.EncryptedUser gets it wrong the second or third time,
             *    Set remember & make it a priority card.
             */
            if(currentCard.getCNumber() % 10 == 0 && currentCard.getNumSeen() == 0) {
                // Score subtract 2 points
                score -= 2;

                // Add 2 or 3 cards into the deck from tree
                int newNum = (currentCard.getCNumber() +1);
                firstWrong(newNum, currentCard, listCard);

                // If the list length is less than 10 do not add the second card.
                if(flashListSize > 10) {
                    secondWrong(currentCard, listCard);
                }
                thirdWrong(currentCard, listCard);
                FlashMonkeyMain.AVLT_PANE.displayTree(newWrongNums);
                FMTWalker.getInstance().setHighLow();  // treeWalker.setHighLow();
                progGauge.setBase(FMTWalker.getCount());
            }
            else {
                // Score subtract 1 point
                score -= 1;

                // RemAction not used until after the card has been seen more than once for this session
                remAction(currentCard, listCard);
            }

            // score related
            scoreTextF.setText(printScore());
            scoreGauge.moveNeedle(500, getScore());
            ButtoniKon.getWrongAns(test.getAnsButton(), "Ooops!");
            wrongNodAction(test.getAnsButton());

            currentCard.setIsRight(-1);
            listCard.setNumSeen(listCard.getNumSeen() + 1);
            currentCard.setNumSeen(currentCard.getNumSeen() + 1);
        }

        /**
         *
         * @param selectAnsBtn
         */
        public void wrongNodAction(Button selectAnsBtn) {
            selectAnsBtn.setStyle("-fx-color: #FF0000;");
            FMTransition.badNod = FMTransition.nodLRFade(selectAnsBtn);
            FMTransition.badNod.play();
        }


        /**
         * Helper Method to selectAnswerButton action
         * Adds the first card to the stack when the EncryptedUser.EncryptedUser answers a question incorrectly.
         * The first card is added into the stack immediately after the card answered. 
         * @param newNum
         * @param currentCard
         */
        private void firstWrong(int newNum, FlashCardMM currentCard, FlashCardMM listCard) {
            listCard.setNumRight(0);
            FlashCardMM iAdd = new FlashCardMM(currentCard);
            iAdd.setNumSeen(1);
            //iAdd.setIsRight(0);
            iAdd.setCNumber(newNum);
            if(!FMTWalker.getInstance().add(iAdd)) {
                newWrongNums[0] = addPlus(iAdd, newNum);
            } else {
                newWrongNums[0] = newNum;
            }
            iAdd.setIsRight(0);
        }

        /**
         * Helper Methods for select Ans Button set on action
         * This method adds 25% to the QNum of the card as it is added into
         * the stack
         */
        private void secondWrong(FlashCardMM currentCard, FlashCardMM listCard) {
            // 2nd time card is added in, its + 25%, 
            // create the new card to be inserted into the stack
            listCard.setNumRight(0);
            FlashCardMM iAdd = new FlashCardMM(currentCard);
            //iAdd.setIsRight(0);
            iAdd.setNumSeen(2);
            int fcX10 = flashListSize * 10; // = 320
            int newNum = (((fcX10 - currentCard.getCNumber()) / 3)     // ((320 - 0) / 4) + 1 = 81
                        + currentCard.getCNumber());
            iAdd.setCNumber(newNum);
    
            if(!FMTWalker.getInstance().add(iAdd) || newNum % 2 == 0) {
                newWrongNums[1] = addPlus(iAdd, newNum);
            } else {
                newWrongNums[1] = newNum;
            }
            iAdd.setIsRight(0);
        }

        /** 
         * Helper method for select Ans Button Action
         * This method adds the card at the end of the deck when the EncryptedUser.EncryptedUser answers a question
         * incorrectly.
         * @param currentCard
         */
        private void thirdWrong(FlashCardMM currentCard, FlashCardMM listCard) {
            listCard.setNumRight(0);
            // create the new card to be inserted into the stack
            FlashCardMM iAdd = new FlashCardMM(currentCard);
            int newNum = (FMTWalker.getCount() * 10) + 1;
            iAdd.setCNumber(newNum);
            iAdd.setNumSeen(3);
            iAdd.setIsRight(0);
            if(!FMTWalker.getInstance().getInstance().add(iAdd) || newNum % 2 == 0) {
                newWrongNums[2] = addPlus(iAdd, newNum);
            } else {
                newWrongNums[2] = newNum;
            }
            iAdd.setIsRight(0);
        }

        /**
         * Helper method
         * Sets the remember setting in the flashList for this card. 
         * @param currentCard
         * @param listCard
         */
        private void remAction(FlashCardMM currentCard, FlashCardMM listCard) {
            // @todo change remAction() to private
        	/*
            Debate over should increase priority if gets the question wrong and
            it has been seen in a previous session but this is the first time this
            session. Response is if it is wrong the first time, it is added back
            into the stack three more times.

            Is it neccessary to increase its priority?
            - Only if the EncryptedUser.EncryptedUser continues to get it wrong during the
            same session.
            Solution: If this question continues to be a problem, it's remember should
            progressively decrease until the EncryptedUser.EncryptedUser answers it correctly. This can be used
            as an indicator during analysis of the types of questions the EncryptedUser.EncryptedUser is struggling
            with, if a classroom is having problems with this question... etc...
            Once the EncryptedUser.EncryptedUser begins to answer the question correctly, RightAns will progressively
            move the remember back to the right.
            */
        	int rem = listCard.getRemember();

            // If the current card ( this card in the deck and not this card in the list )
            // has been seen twice > 1. Then set its priority.
            if(currentCard.getNumSeen() > 1) {
                try {
                    rem += floor(-1 * (flashListSize)  * .33);
                    listCard.setRemember(rem);
                }
                catch(NullPointerException f) {
                    //LOGGER.debug("error: null pointer in setRemember");
                }  
            }
        }
        // DO SOMETHING IF ANSWER BEING ADDED IS IN ansSet

        //Helper method to do something??? if this question has more than
        // one correct answer. Currently always returns true.
        private boolean inAnsAry(FlashCardMM currentCard, int ansQID) {
            for(int i = 0; i < 4; i++)
            {
            //    if() //@todo finish in Answer Array Action line 1537 ReadFlash
            }
            return true;
        }
    } // ********* END Wrong Class **************



    // ***** FOR TESTING *****
    @FMAnnotations.DoNotDeployMethod
    public Point2D getStudyButtonXY() {
        Bounds bounds = ButtoniKon.getStudyButton().getLayoutBounds();
        return ButtoniKon.getStudyButton().localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
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
