/*
 * AUTHOR: Lowell Stadelman
 */
package flashmonkey;


import campaign.Report;
import ecosystem.ConsumerPane;
import fileops.Utility;
import flashmonkey.utility.Sleep;
import fmannotations.FMAnnotations;
import forms.*;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.ButtoniKon;
import uicontrols.FxNotify;
import uicontrols.SceneCntl;

//import java.awt.event.MouseEvent;

import static fileops.DirectoryMgr.flashmonkeyExists;
import static flashmonkey.FlashCardOps.*;


/**
 * <p>Adding Multi-Media, at last! as well as a Test type. This version has a flexable
 * layout that is planned to provide Multiple Card layout types, section types, and TestTypes. The
 * previous version, did not fully implement sections but fully implemented the
 * tree as a navigator/ progress indicator to the EncryptedUser.EncryptedUser. It provided flexibility
 * in allowing the EncryptedUser.EncryptedUser to delete cards and hide them. It prioritized cards
 * for the EncryptedUser.EncryptedUser based on thier performance on tests. It provided only two layouts.
 * A layout for the standard question and answer FlashCard, and a multiple-choice
 * test.</p>
 *
 * <p>This version, allows multiple testing types, and multiple section types.
 * Section types are key to implementing multi-media. Card types allow for multiple
 * test types that require different layouts form the Top and Bottom only
 * sections. This version also begins to look at more interactive activities
 * for learning.</p>
 *
 * @version iOOily: FlashMonkey Multi-Media and Multi-Test
 * @author Lowell Stadelman
 */
public class FlashMonkeyMain extends Application {
    
    @SuppressWarnings("rawtypes")
    // for serializing objects to file. The serial version.
    // Used with "serialVersionUID"
    public static final long VERSION = 20200612;
    public static final AVLTreePane AVLT_PANE = new AVLTreePane();
    
    private static Stage window;
    private static Stage actionWindow;
    protected static Stage treeWindow;
    
    private static Image flash;
    private static Image bckgndImg;
    
    private static CreateFlash createFlash;
    private static ReadFlash readFlash;
    private static Button sceneOneButton;
    //protected static AVLTreePane avltPane = new AVLTreePane();
    protected static Scene sceneTree;
    private static BorderPane treePane;
    private static BorderPane firstPane;
//    private static Button exitButton;
    private static Button searchRscButton;
    private static Button menuButton;
    private static Button createButton;
    // campaign
    // @TODO Add report back in
    //private static Report report;

    // The error message to be displayed in the UI
    private static String errorMsg = "";
    // FLAGS
    private static boolean isInEditMode;

    // THE LOGGER
    private static final Logger LOGGER = LoggerFactory.getLogger(FlashMonkeyMain.class);

    
    /** Testing MemoryChartPane **/
    //private static Stage chartWindow;
    //private static Scene chartScene;
    //private static BorderPane chartPane;


    // *** Java FX UI *** STAGE *** ***
    @Override
    public void start(Stage primaryStage) {
        // set to call stop() when stage is closed
        primaryStage.setOnHidden( e -> stop());
        // reporting app performace to DB
        Report.getInstance().sessionStart();
        // failure flag if authcrypt.user is
        // in edit mode. If true, save work
        // before closing.
        isInEditMode = false;

        window = primaryStage;
        treeWindow = new Stage();

        // Preserve deck-data if something happens
        // and do not over-write decks with nothing.
        // If the computer goes to sleep, or focus changes from Flashmonkey,
        // save the users data
  /*
        primaryStage.focusedProperty().addListener((observable, oldValue, newValue ) ->
        {
    
            System.err.println("\n *** Start() primaryStage.focusedProperty() Focus has changed *** \n");
            
            if(oldValue) {
                try
                {   // If the user is creating/editing cards
                    if (createFlash.getFlashListChanged()) {
                        Timer time = Timer.getClassInstance();
                        createFlash.eventSave();
                        LOGGER.info("Changed focus: Window closed at " + time.printTimeNow());

                    // Or the user is testing / studying
                   } else if (FlashCardOps.getInstance().getFlashList().size() > 4) {

                        //@todo replace refreshFlashList in FlashMonkeyMain stop() method with another save method.
                        FlashCardOps.getInstance().saveFListToFile();
                        LOGGER.info("\n %^& Changed focus Saving flashList %^& ");
                    }
                } catch (Exception e) {

                    //@todo create message method and variable, and output messages to the user
                    // SomeClass.message = "Current deck failed to save on exit"
                    LOGGER.warn("ERROR: Focus Changed caused an Exception saving file in start() {}", this.getClass().getName());

                }
            }
        });
        
   */
        
        // Uncomment to create the memory monitor
        //chartWindow = new Stage();
        //buildChartWindow();
        // Getting image resources as a stream is finicky. No more than two dashes per name,
        // and cannot be under a subdirectory. Although convieniently finds the resource directory
        // on it's own.
 //       flash = new Image(getClass().getResourceAsStream("/image/flash_astronaut_150.png"));
	    
        flash = new Image(getClass().getResourceAsStream("/image/fm_multiLogo_5.png"));
        bckgndImg = new Image(getClass().getResourceAsStream("/image/fm_bckgrnd.png"));
        Image icon = new Image(getClass().getResourceAsStream("/icon/flashMonkey_Icon.png"));
        window.getIcons().add(icon);

        // Determine if FlashMonkey Data Directory
        // exists and show signUp or signIn
        if(flashmonkeyExists()) {
            getSignInPane();
        } else {
            getSignUpPane();
        }

        window.setTitle("FlashMonkey");
        window.show();

        LOGGER.debug("in FlashMonkeyMain and window x = " + window.getX());
    } // ******** END START ******* //
    
    /**
     *   The first scene is the sign-in page. Then either
     *   fileSelectPane pane (study deck) or create authcrypt.user.
     *   IF fileSelectPane, also provides the ability to create a new deck.
     * @return Returns the first scene or landing scene999
     */
    public static Scene getFirstScene(GridPane focusPane) {
        LOGGER.debug("*** getFirstScene called ***");

        Scene firstScene;
        firstPane = new BorderPane();
        GridPane btnBox = new GridPane();
        //exitButton = ButtoniKon.getExitButton();

        // For the lower panel on initial window
        ColumnConstraints col0 = new ColumnConstraints();
        col0.setPercentWidth(50);
        btnBox.getColumnConstraints().add(col0);
        btnBox.setId("buttonBox");
        
        // buttons for intial window
 //       exitButton.setOnAction(e -> {
//            Report.getInstance().endSessionTime();
 //           System.exit(0);
//        });
    
        GridPane gridPane1 = new GridPane();

        // Calculate logo fit height
        int fitHeight = calcFitHeight((int) focusPane.getPrefHeight());
        int gapHeight = (int) calcGapHeight(fitHeight);
    
        ImageView flashAstronaut = new ImageView(flash);
        flashAstronaut.setFitHeight(fitHeight);
        flashAstronaut.setPreserveRatio(true);
        flashAstronaut.setSmooth(true);
        HBox imageBox = new HBox();
        imageBox.getChildren().add(flashAstronaut);
        imageBox.setAlignment(Pos.CENTER);
        //martian.setSmooth(true);
        //Label label = new Label("", flashAstronaut);
        //label.setId("fmLabel");
    
        
        gridPane1.setAlignment(Pos.CENTER);
        gridPane1.setVgap(gapHeight);
        //gridPane1.setId("mainGridPane");
        //gridPane1.maxWidthProperty().bind(mainPane.widthProperty());
        //gridPane1.setPrefWidth(350);
    
        // *** PLACE NODES IN PANE ***
        gridPane1.getChildren().clear();
        gridPane1.addRow(0, imageBox); // column 0, row 0
        //gridPane1.addRow(1, spacer);
        gridPane1.addRow(1, focusPane);
        // Here, we set the image directly, in the pane!?!
        BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true);
        firstPane.setBackground(new Background(new BackgroundImage(bckgndImg,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER,
                bSize)));
        
        // *************************************
        // The users account set in top
        //firstPane.setTop( getAccountBox() );
        firstPane.setCenter(gridPane1);
        firstPane.setBottom(getExitBox());
        firstScene = new Scene(firstPane, SceneCntl.getWd(), SceneCntl.getHt());
        firstScene.getStylesheets().addAll("css/buttons.css", "css/mainStyle.css");
        Sleep.storeOnDetected(firstScene, window);
        return firstScene;
    }
    
    /**
     * Calculates the fitHeight of the
     * logo based on the height of the
     * gPane that is displayed in the
     * scene.
     * @param gPaneHt
     * @return
     */
    private static int calcFitHeight(int gPaneHt) {
        System.err.println("gpane ht: " + gPaneHt);
        return 600 - gPaneHt;
    }
    
    /**
     * Calculate the gapHeight for the logo
     * between logo and pane below.
     * @param fitHeight
     * @return
     */
    private static double calcGapHeight(int fitHeight) {
        return fitHeight / 4.5;
    }

    /**
     * Creates the Navigation/Menu stage. The Navigation/MenuStage provides the buttons
     *   used to navigate to studying, back to the firstScene, create/edit scene. It is second
     *   scene unless the EncryptedUser is creating a new deck.
     * @return Returns the Navigation Scene
     */
    public static Scene getNavigationScene() {
        Scene mainScene;
        BorderPane mainPane = new BorderPane();
        GridPane tempPane = new GridPane();
        tempPane.setAlignment(Pos.CENTER);
        
        LOGGER.debug("\n *** getNavigationScene() ***");
    	//FlashCard flash = new FlashCard(); // haha!!

        // *** CENTER MENU BUTTONS ***
        // go back to file select pane
        Button deckSelectButton = ButtoniKon.getDeckSelectButton();
        deckSelectButton.setOnAction(e -> ReadFlash.getInstance().deckSelectButtonAction());
        // go to study menu
        Button studyButton = ButtoniKon.getStudyButton();
        studyButton.setOnAction(e -> studyButtonAction() );

        
        // go to create pane
        createButton = ButtoniKon.getCreateButton();
        createButton.setOnAction(e -> {
            LOGGER.debug("in createButton.setOnAction()");
            createButtonAction();
        });
        
        ReadFlash.getInstance().resetScoreNProg();

        tempPane.addRow(0, deckSelectButton);
        tempPane.addRow(2, studyButton);
        tempPane.addRow(3, createButton);
        
        mainPane.setId("bckgnd_image");
        mainPane.setTop(getAccountBox());
        mainPane.setCenter(tempPane);
        mainPane.setBottom(getExitBox());        
        mainScene = new Scene(mainPane, SceneCntl.getWd(), SceneCntl.getHt());
        mainScene.getStylesheets().addAll("css/mainStyle.css", "css/buttons.css");
	    menuButton.setDisable(true);
        
        return mainScene;
    } // end Scene getNavigationScene

    protected static void createButtonAction() {
        isInEditMode = true;
        Timer.getClassInstance().startTime();
        createFlash = CreateFlash.getInstance();
        // getInstance().refreshFlashList();
        menuButton.setDisable(false);
        window.setScene(createFlash.createFlashScene());   // called once
    }


    /**
     * Sends EncryptedUser.EncryptedUser to a new screen and calls the
     * treePane
     */
    private static void studyButtonAction() {
        LOGGER.debug("\n*** studyButtonAction() FlashMonkeyMain ***");
        Timer.getClassInstance().testTimeStop();
        LOGGER.debug("testTime: " + Timer.getClassInstance().getTakeTestTime());
        readFlash = ReadFlash.getInstance();
	    menuButton.setDisable(false);
        window.setScene(readFlash.readScene());
    }


    /**
     * Assist with StudyButtonAction used in navigation scene when creating
     * a new study session. Actions are not needed when moving from either test
     * or from QAndA sessions when the UI should be consistant and the tree structure
     * stable from changes except for modifications due to an incorrect answer.
     */
    protected static void buildTreeWindow() {
        LOGGER.debug("called");

        treePane = new BorderPane();
        AVLT_PANE.setStyle("-fx-background-color: #393E46" );
        treePane.setCenter(AVLT_PANE);
        sceneTree = new Scene(treePane);

        treeWindow.setTitle("Navigation and Status tree");
        treeWindow.setScene(sceneTree);
        treeWindow.show();
    }




    /**
     * Creates an HBox with the menu and exit buttons.
     * @return HBox
     */
    private static GridPane getExitBox() {
        GridPane buttonBox = new GridPane(); // HBox with spacing provided
        buttonBox.setHgap(2);
        Button exitButton = ButtoniKon.getExitButton();
        exitButton.setOnAction(e -> {
            try {
                Report.getInstance().endSessionTime();
            } catch (NullPointerException f){
                LOGGER.warn(f.getMessage(), f.getStackTrace());
                f.printStackTrace();
            } finally {
                System.exit(0);
            }
            
        });
        menuButton = ButtoniKon.getMenuButton();
        ColumnConstraints col0 = new ColumnConstraints();
        col0.setPercentWidth(50);
        buttonBox.setId("buttonBox");
        buttonBox.getColumnConstraints().add(col0);
        buttonBox.setPadding(new Insets(15, 15, 15, 15));
        buttonBox.addColumn(1, menuButton);
        buttonBox.addColumn(2, exitButton);
        
        return buttonBox;
    }
    
    
    
    private static HBox getAccountBox() {
        HBox hBox = new HBox(2);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(2, 2, 2, 2));
        

        authcrypt.UserData user = new authcrypt.UserData();
        String name = user.getFirstName();
        ImageView userImg = new ImageView(user.getUserImage());
        userImg.setFitHeight(60);
        userImg.setSmooth(true);
        userImg.setPreserveRatio(true);
        // Verify the user is logged in
        userImg.setOnMouseClicked(e -> {
            getAccountPane();
            //accountClickAction(e);
        });

        userImg.setOnMouseEntered(e -> {
            FlashMonkeyMain.getWindow().getScene().setCursor(Cursor.HAND);
        });

        userImg.setOnMouseExited(e -> {
            FlashMonkeyMain.getWindow().getScene().setCursor(Cursor.DEFAULT);
        });

        // If not send to log in pane

        // If true show user a drop down menu
        // for now show the user the StudentInfoForm
        
        hBox.getChildren().addAll(new Label(name), userImg);

        return hBox;
    }
    
    /*private static void accountClickAction(MouseEvent e) {
        getAccountPane();
    }*/

    /** Getter Setter for error message **/

    /**
     * Sets the error message to be displayed
     * in the UI.
     * @return
     */
    public static String getErrorMsg()
    {
        return errorMsg;
    }

    /**
     * Gets the error message to be displayed
     * in the UI
     * @param str
     */
    public static void setErrorMsg(String str)
    {
        errorMsg = str;
    }
    
    /**
     * Used with getFileScene, the initial screen that is presented to the EncryptedUser.EncryptedUser.
     * It provides the label along with graphics and reads in
     * the File[] listOfFiles from the users previous card decks, if any exist. If
     * not, then will only display the create button.
     * File methods that this method relies on are in the FlashCard() class.
     * @return Returns the BorderPane containing the files
     */
    //
    private static GridPane getFilePane() {



        Button newDeckButton;
        int width = SceneCntl.getFileSelectPaneWd();
        GridPane gridPane1 = new GridPane();
        gridPane1.setPadding(new Insets(20,0,0, 0));
        ScrollPane scrollP = new ScrollPane();
        scrollP.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollP.setPrefWidth(width);
        scrollP.setMaxHeight(446);
        scrollP.setId("fileSelectOuter");
        
        //label.setFont(Font.loadFont("File:font/Raleway-Medium.ttf", 25));
        Label selectLabel = new Label("Select a deck to study");
        selectLabel.setPadding(new Insets(0,0,20,0));
        selectLabel.setId("h2Label");

        gridPane1.setAlignment(Pos.TOP_CENTER);
        //gridPane1.setVgap(20);
        gridPane1.setPrefHeight(width);
        
        // *** PLACE NODES IN PANE ***
        GridPane.setHalignment(selectLabel, HPos.CENTER);
        FlashCardOps.FileSelectPane fsp = getInstance().new FileSelectPane();
        fsp.createPane();
        fsp.paneForFiles.setId("fileSelectPane");
        fsp.paneForFiles.setPrefWidth(width);

        // The search for resources button
        searchRscButton = ButtoniKon.getSearchRsc();
        
        // If there are files, display them in a ScrollPane  */
        // from import static FlashCardOps
        if(getInstance().getAgrListSize() > 0) {
            scrollP.setContent(fsp.paneForFiles);

            gridPane1.addRow(1, scrollP); // column 0, row 1
            newDeckButton = ButtoniKon.getNewDeck();
            
            HBox buttonBox = new HBox(25);
            buttonBox.setPadding(new Insets(20, 0, 6, 0));
            //buttonBox.setPrefWidth(350);
            buttonBox.getChildren().addAll(newDeckButton, searchRscButton);

            //@todo file select method. Look at this for efficiency if needed!
            // new file button action
            newDeckButton.setOnAction(e -> {
                gridPane1.getChildren().clear();
                // from flashmonkey.FlashCardOps.fileSelectPane()
                fsp.paneForFiles.getChildren().clear();
                fsp.paneForFiles.getChildren().add(fsp.newFile());
                gridPane1.addRow(0, fsp.paneForFiles);
                gridPane1.addRow(2, sceneOneButton);
            });
            gridPane1.addRow(0, selectLabel);
            gridPane1.addRow(3, buttonBox);
        }
        else // else display a new file pane
        {
            searchRscButton.setPadding(new Insets(20,0,0,0));
            gridPane1.addRow(2, fsp.paneForFiles);
            gridPane1.addRow(4, searchRscButton);
        }
        
        searchRscButton.setOnAction(e -> {
            getSearchPane();
        });
        
        // Takes the authcrypt.user back to
        // the select file pane when the createNewDeck
        // scene is displayed.
        sceneOneButton = new Button("Back");
        //sceneOneButton.setFont(Font.loadFont("File:font/Raleway-Medium.ttf", 25));
        /**
         * Button action: Create a new file/deck
         * Shows the EncryptedUser.EncryptedUser the new file textArea where
         * the EncryptedUser.EncryptedUser may enter the FlashCard deck name
         */
        sceneOneButton.setOnAction(e -> {
            fsp.paneForFiles.getChildren().clear();
            gridPane1.getChildren().clear();
            window.setScene(getFirstScene(getFilePane()));
        });
    
        //firstPane.setTop( getAccountBox() );
        menuButton.setDisable(true);
        return gridPane1;
    }
    
    
    public static void getSignInPane() {
        LOGGER.debug("getSignInPane() called");
        SignInModel model = new SignInModel();
        SignInPane signInPane = new SignInPane(model);
        model.getFormInstance();

        window.setScene(getFirstScene(signInPane.getSignInPane()));
        signInPane.getSignInPane().requestFocus();
    }
    
    public static void getSignUpPane() {
        SignUpModel model = new SignUpModel();
        SignUpPane signUpPane = new SignUpPane(model);
        model.getFormInstance();
        window.setScene(getFirstScene(signUpPane.getSignUpPane()));
        signUpPane.getSignUpPane().requestFocus();
    }
    
    public static void getAccountPane() {
        if(Utility.isConnected()) {
            StudentModel model = new StudentModel();
            StudentPane stuPane = new StudentPane();
            model.getFormInstance();
            Scene scene = new Scene(stuPane.getFormPane());
            scene.getStylesheets().addAll("css/buttons.css", "css/fxformStyle.css");
            actionWindow = new Stage();
            actionWindow.setScene(scene);
            actionWindow.show();
        } else {
            // show error message
            String emojiPath = "image/flashFaces_sunglasses_60.png";
            String message   = "Oooph!" +
                    "\n I need to be connected to the cloud \n to create or change your account information.";
            FxNotify.notificationPurple("Please Connect!", message, Pos.CENTER, 9,
                    emojiPath, FlashMonkeyMain.getWindow());
        }
    }

    public static void resetActionWindow(Pane pane) {
        actionWindow.setScene(new Scene(pane));
        actionWindow.show();
    }

    public static void closeActionWindow() {
        actionWindow.close();
    }
    
    public static void getSearchPane() {
        LOGGER.debug("getSearchWindow() called");
        if(actionWindow == null) {
            ConsumerPane cPane = ConsumerPane.getInstance();
            Scene scene = new Scene(cPane.getConsumerPane());
            scene.getStylesheets().addAll("css/consumerButtons.css", "css/consumerPane.css");
            actionWindow = new Stage();
            actionWindow.setScene(scene);
        }
        actionWindow.show();
    }

    public static Stage getActionWindow() {
        return actionWindow;
    }
    
    public static void getFileSelectPane() {
        window.setScene(getFirstScene(getFilePane()));
    }
    public static Stage getWindow() {
        return window;
    }
    // public static Stage getActionWindow() {return actionWindow; }

    /**
     * Returns the Main Window's current XY location
     * @return Point2D location of the current XY position
     */
    public static Point2D getWindowXY () {
        double x = window.getX();
        double y = window.getY();
        return new Point2D(x, y);
    }

    public static void setTopPane() {
        firstPane.setTop(getAccountBox());
    }

    /**
     * Sets the window to nav
     */
    public static void setWindowToNav() {

        LOGGER.debug("setWindowToNav called");

        isInEditMode = false;
        window.setScene(getNavigationScene());
    }

    /**
     * Returns if editor/creator mode is active.
     * Side effects chose AVLTreePane and TextCell.buildCell
     * @return true if in editor/creator mode
     */
    public static final boolean isInEditorMode() {
        return isInEditMode;
    }

    /**
     * Used to set if the edit/create class is in use and
     * visible. Used by AVLTreePane: Checks the visibility
     * for navigation onClick
     * @param bool
     */
    public static void setIsInEditMode(boolean bool) {
        isInEditMode = bool;
    }

    /**
     * Stop... When FlashMonkey is stopped by the OS or
     * by other actions with exception to the exit button.
     * Saves the current flashDeck to file
     */
    // @TODO add stop() back in
    @Override
    public void stop()
    {
        LOGGER.info("stop() called");

        // close out deck session then send
        // deck_metadata to DB.
        //report.endSessionTime();

        /*
        if(treeWindow.isShowing()) {
            treeWindow.close();
        }

        LOGGER.info("\n*** stop() called ***");
        try {
            if(createFlash.getLength() != 0)
            {
                createFlash.saveOnExit();
            }
            else
            {
                //@todo replace refreshFlashList in FlashMonkeyMain stop() method with another save method.
                FlashCardOps.getInstance().saveFListToFile();
                //flashOps.refreshFlashList();
            }

        } catch(Exception e) {
            //@todo create message method and variable, and output messages to user
            // SomeClass.message = "Current deck failed to save on exit"
            System.exit(0);
        }
        LOGGER.info("\n\n** Saved current flashList to local file  **"
                        +"\n************** End of program **************");

         */
        System.exit(0);
    }

    // memory used chart follows
    /*
    protected void buildChartWindow()
    {

        System.out.println("*^*^* BuidTreeWindow called *^*^*");

        chartPane = createMemoryMonitor();
        //avltPane.setStyle("-fx-background-color: #393E46" );
        //chartPane.setCenter(avltPane);

        //   flashOps.refreshAll(); // commented out. Duplicates treebuild call on its own when needed.
        chartScene = new Scene(chartPane);
        chartWindow.setTitle("MemoryMonitor");
        chartWindow.setScene(chartScene);
        chartWindow.show();
    }



    int i = 0;
    Long[] changeAry = new Long[360];
    int size = 0;
    int count = 1;
    Long oldUsedMemory = 0l;
    long calculatedAvgGrowth = 8661; // bias

    private Long avgMemoryConsumed() {

        Long sum = 0l;
        Long change = 0l;

        // removing the first and last elements
        for(int i = 1; i < size; i++) {



            if (changeAry[i] > 10)
            {
                sum += changeAry[i];
                count++;
                //System.out.println("What is null? i = " + i + " value " + changeAry[i]);
            } else {
                //System.out.println("Mem used is < 10");
                //System.out.println(changeAry[i]);
            }
        }

        sum /= count;
        change = sum - calculatedAvgGrowth;

        //System.out.println("Sum = " + sum + "\n\n");
        return change;
    }

    private BorderPane createMemoryMonitor() {

        LongProperty totalMemory = new SimpleLongProperty(Runtime.getRuntime().totalMemory());
        LongProperty freeMemory  = new SimpleLongProperty(Runtime.getRuntime().freeMemory());
        LongProperty maxMemory   = new SimpleLongProperty(Runtime.getRuntime().maxMemory());
        NumberBinding usedMemory = totalMemory.subtract(freeMemory);
        // for estimating the amount of memory consumed by this application.
        LongProperty memoryUse   = new SimpleLongProperty();

        Label usedMemoryLabel = new Label();
        usedMemoryLabel.textProperty().bind(usedMemory.asString("Used memory: %,d "));
        Label freeMemoryLabel = new Label();
        freeMemoryLabel.textProperty().bind(freeMemory.asString("Free memory: %,d"));
        Label totalMemoryLabel = new Label();
        totalMemoryLabel.textProperty().bind(totalMemory.asString("Total memory: %,d"));
        Label maxMemoryLabel = new Label();
        maxMemoryLabel.textProperty().bind(maxMemory.asString("Max memory: %,d"));

        Label memoryUseLabel = new Label();
        memoryUseLabel.textProperty().bind(memoryUse.asString("Avg Memory Consumed: %,d"));

        Series<Number, Number> series = new Series<>();
        series.setName("Used memory");

        AtomicInteger time = new AtomicInteger();

        Timeline updateMemory = new Timeline(new KeyFrame(Duration.seconds(1), event -> {

            totalMemory.set(Runtime.getRuntime().totalMemory());
            freeMemory.set(Runtime.getRuntime().freeMemory());
            maxMemory.set(Runtime.getRuntime().maxMemory());
            series.getData().add(new Data<>(time.incrementAndGet(), usedMemory.getValue()));
            if (series.getData().size() > 100) {
                series.getData().subList(0, series.getData().size() - 100).clear();
            }

            //System.out.println(" usedMemory " + usedMemory.longValue());
            //System.out.println(" oldUsedMemory " + oldUsedMemory);
            //System.out.println(usedMemory.longValue() - oldUsedMemory);

            changeAry[i] = usedMemory.longValue() - oldUsedMemory;
            //System.out.println("memoryUsed[i] = " + changeAry[i]);

            // When the GC collects memory used is less than 0
            // Remove cycles that are 0 or less for a more accurate
            // understanding of this monitors average use.
            if(i > 0) {
                memoryUse.set(avgMemoryConsumed());
            }
            i++;
            size++;
            oldUsedMemory = usedMemory.longValue();
        }));

        // Seconds ... or number of cycles to test
        updateMemory.setCycleCount(359);
        updateMemory.play();

        // Display the labels above the chart
        VBox labels = new VBox(usedMemoryLabel, freeMemoryLabel, totalMemoryLabel, maxMemoryLabel, memoryUseLabel);
        labels.setAlignment(Pos.CENTER);

        // The chart
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time");
        xAxis.setForceZeroInRange(false);
        NumberAxis yAxsis = new NumberAxis();
        yAxsis.setLabel("Memory");
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxsis);
        chart.setAnimated(false);
        chart.getData().add(series);

        BorderPane chartPane = new BorderPane(chart, labels, null, null, null);

        return chartPane;
    }
    */
    public static void main(String[] args)
    {
        launch(args);
    }


    /* *** FOR TESTING *** */
    @FMAnnotations.DoNotDeployMethod
    public static Point2D getCreateButtonXY() {
        Bounds bounds = createButton.getLayoutBounds();
        return createButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 20);
    }


}


