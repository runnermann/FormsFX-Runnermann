/*
 * AUTHOR: Lowell Stadelman
 */
package flashmonkey;


import authcrypt.UserData;
import campaign.Report;
import ch.qos.logback.classic.Level;
import ecosystem.ConsumerPane;

import ecosystem.EcoPane;
import ecosystem.PayPane;
import fileops.BaseInterface;
import fileops.utility.Utility;
import flashmonkey.utility.VersionTimeStamp;
import fmannotations.FMAnnotations;
import forms.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
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

//import javafx.stage.StageStyle;
import javafx.stage.StageStyle;
import org.slf4j.LoggerFactory;
import type.celltypes.VideoPlayerPopUp;
import uicontrols.*;

import java.util.prefs.BackingStoreException;

import static flashmonkey.FlashCardOps.*;
import static fileops.DirectoryMgr.flashmonkeyExists;


/**
 * <p>Adding Multi-Media, at last! as well as a Test type. This version has a flexable
 * layout that is planned to provide Multiple Card layout types, section types, and TestTypes. The
 * previous version, did not fully implement sections but fully implemented the
 * tree as a navigator/ progress indicator to the EncryptedUser. It provided flexibility
 * in allowing the EncryptedUser to delete cards and hide them. It prioritized cards
 * for based on thier performance on tests. </p>
 *
 * <p>This version, allows multiple testing types, and multiple section types.
 * Section types are key to implementing multi-media. Card types allow for multiple
 * test types that require different layouts form the Top and Bottom only
 * sections. This version also begins to look at more interactive activities
 * for learning.</p>
 *
 * @author Lowell Stadelman
 * @version This version adds Stylable Formatted Text to the text area's
 */
public class FlashMonkeyMain extends Application implements BaseInterface {

      @SuppressWarnings("rawtypes")

      // The publicly known version
      public static final String VERSION_STR = "1.2.1";
      // for serializing objects to file. The serial version.
      // Used with "serialVersionUID"
      public static final long VERSION = 20200612;
      public static final AVLTreePane AVLT_PANE = new AVLTreePane();

      private static Stage window;
      private static Stage actionWindow;
      protected static Stage treeWindow;

      private static Image flash;
      //private static Image bckgndImg;

      private static CreateFlash createFlash;
      private static ReadFlash readFlash;

      //protected static AVLTreePane avltPane = new AVLTreePane();
      protected static Scene sceneTree;
      private static Scene searchScene;
      private static BorderPane treePane;
      private static BorderPane firstPane;
      //    private static Button exitButton;
      private static Button searchRscButton;
      private static Button menuButton;
      private static Button createButton;
      private static Button backButton;
      // campaign
      // @TODO Add report back in
      //private static Report report;
      // The error message to be displayed in the UI
      private static String errorMsg = "";
      // FLAGS
      private static boolean isInEditMode;
      private static boolean returnToFullScreen;
      private static final BooleanProperty isLoggedinProperty = new SimpleBooleanProperty(false);


      // THE LOGGER
      //private static final Logger LOGGER = LoggerFactory.getLogger(FlashMonkeyMain.class);
      private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FlashMonkeyMain.class);


      // *** Java FX UI *** STAGE *** ***
      @Override
      public void start(Stage primaryStage) {
            // We control when the platform closes to ensure
            // any open stage/window saves its work before
            // the application is closed. We must call Platform.exit()
            // to close the JVM.
            //Platform.setImplicitExit(false);
            try {
                  // set the app to the users preferences
                  SceneCntl.setPref();
                  LOGGER.setLevel(Level.DEBUG);
                  // reporting app performace to DB
                  //LOGGER.debug("start called");
                  Report.getInstance().sessionStart();
                  //LOGGER.debug("timeCheck line 111 fmMain. Completed Report.getInstance90.sessionStart()");
                  // failure flag if authcrypt.user is
                  // in edit mode. If true, save work
                  // before closing.
                  isInEditMode = false;
                  window = primaryStage;
//            window.setAlwaysOnTop(true);
                  // set to call stop() when stage is closed
                  window.setOnCloseRequest(e -> {
                        //saveOnExit();
                        onClose();
                  });
                  treeWindow = new Stage();
                  // Getting image resources as a stream is finicky. No more than two dashes per name,
                  // and cannot be under a subdirectory. Although conveniently finds the resource directory
                  // on it's own.
                  flash = new Image(getClass().getResourceAsStream("/image/logo/vertical_logo_white_8per_120x325.png"));
                  Image icon = new Image(getClass().getResourceAsStream("/image/logo/blue_flash_128.png"));
                  window.getIcons().add(icon);
                  // Determine if FlashMonkey Data Directory
                  // exists and show signUp or signIn
                  if (flashmonkeyExists()) {
                        showSignInPane();
                  } else {
                        showIntroPane();
                  }

                  window.setTitle("FlashMonkey");
                  window.show();
            } catch (BackingStoreException e) {
                  LOGGER.warn(e.getMessage());
                  e.printStackTrace();
            } catch (Exception e) {
                  e.printStackTrace();
            }

            //LOGGER.debug("in FlashMonkeyMain and window x = " + window.getX());
      } // ******** END START ******* //

      public static String getVersionStr() {
            return VERSION_STR;
      }

      /**
       * The first scene is the sign-in or signUp page. Then either
       * fileSelectPane pane (study deck) or create authcrypt.user.
       * IF fileSelectPane, also provides the ability to create a new deck.
       *
       * @return Returns the first scene or landing scene
       */
      public static Scene getFirstScene(GridPane focusPane) {
            Scene firstScene;
            firstPane = new BorderPane();
            VBox spacer = new VBox();
            spacer.setPrefHeight(40);
            VBox spacerL = new VBox();
            spacerL.setPrefHeight(40);

            GridPane gridPane1 = new GridPane();

            // Calculate logo fit height
            int fitHeight = 120; //calcFitHeight((int) focusPane.getPrefHeight());
            int gapHeight = (int) calcGapHeight(fitHeight);

            ImageView iconView = new ImageView(flash);
            //iconView.setFitHeight(fitHeight);
            iconView.setFitHeight(fitHeight);
            iconView.setPreserveRatio(true);
            iconView.setSmooth(true);
            VBox imageBox = new VBox();
            imageBox.getChildren().add(iconView);
            imageBox.setAlignment(Pos.CENTER);

            gridPane1.setAlignment(Pos.TOP_CENTER);
            gridPane1.setVgap(gapHeight);

            // *** PLACE NODES IN PANE ***
            gridPane1.getChildren().clear();
            gridPane1.addRow(0, spacer);
            gridPane1.addRow(1, imageBox); // column 0, row 1
            gridPane1.addRow(2, spacerL);
            gridPane1.addRow(3, focusPane);

            firstPane.setCenter(gridPane1);
            // grey #393E46  // #29AbE2
            firstPane.setStyle("-fx-background-color:" + UIColors.FM_GREY); //#393E46"); //086ABF
            // firstPane.setBottom(getExitBox());
            menuButton = ButtoniKon.getMenuButton();
            firstPane.setId("bckgnd_image"); // the background image
            firstScene = new Scene(firstPane, SceneCntl.getAppBox().getWd(), SceneCntl.getAppBox().getHt());
            firstScene.getStylesheets().addAll("css/buttons.css", "css/mainStyle.css");
            //Sleep.storeOnDetected(firstScene, window);

            ChangeListener<Number> stagePropsListener = (observable, oldVal, newVal) -> {
                  SceneCntl.getAppBox().setHt((int) firstScene.getHeight());
                  SceneCntl.getAppBox().setWd((int) firstScene.getWidth());
                  SceneCntl.getAppBox().setX((int) firstScene.getX());
                  SceneCntl.getAppBox().setY((int) firstScene.getY());
            };

            window.widthProperty().addListener(stagePropsListener);
            window.heightProperty().addListener(stagePropsListener);
            window.xProperty().addListener(stagePropsListener);
            window.yProperty().addListener(stagePropsListener);

            return firstScene;
      }

      /**
       * Calculates the fitHeight of the
       * logo based on the height of the
       * gPane that is displayed in the
       * scene.
       *
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
       *
       * @param fitHeight
       * @return
       */
      private static double calcGapHeight(int fitHeight) {
            return fitHeight / 6;
      }

      /**
       * Creates the Navigation/Menu stage. The Navigation/MenuStage provides the buttons
       * used to navigate to studying, back to the firstScene, create/edit scene. It is second
       * scene unless the EncryptedUser is creating a new deck.
       *
       * @return Returns the Navigation Scene
       */
      public static Scene getMenuScene() {
            Scene mainScene;
            BorderPane mainPane = new BorderPane();
            GridPane gridPane = new GridPane();
            gridPane.setAlignment(Pos.CENTER);
            VBox buttonBox = new VBox(2);

            LOGGER.debug("\n *** getNavigationScene() ***");
            Label label = new Label("Action Menu");
            label.setId("label24White");

            // *** CENTER MENU BUTTONS ***
            // go back to file select pane
            Button deckSelectButton = ButtoniKon.getDeckSelectButton();
            deckSelectButton.setOnAction(e -> ReadFlash.getInstance().deckSelectButtonAction());
            // go to study menu
            Button studyButton = ButtoniKon.getStudyButton();
            studyButton.setOnAction(e -> studyButtonAction());

            // go to create pane
            createButton = ButtoniKon.getCreateButton();
            createButton.setOnAction(e -> {
                  LOGGER.debug("in createButton.setOnAction()");
                  createButtonAction();
            });

            ReadFlash.getInstance().resetScoreNProg();
            buttonBox.getChildren().addAll(label, deckSelectButton, studyButton, createButton);
            gridPane.addRow(0, buttonBox);

            buttonBox.setId("fileSelectPane");
            buttonBox.setAlignment(Pos.TOP_CENTER);
            gridPane.setId("rightPaneTransp");
            mainPane.setId("bckgnd_image"); // the background image
            mainPane.setTop(getAccountBox());
            mainPane.setCenter(gridPane);
            mainPane.setBottom(getExitBox());
            mainScene = new Scene(mainPane, SceneCntl.getAppBox().getWd(), SceneCntl.getAppBox().getHt());
            mainScene.getStylesheets().addAll("css/mainStyle.css", "css/buttons.css");
            menuButton.setDisable(true);

            return mainScene;
      } // end Scene getNavigationScene

      protected static void createButtonAction() {
            isInEditMode = true;
            Timer.getClassInstance().startTime();
            createFlash = CreateFlash.getInstance();
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
            AVLT_PANE.setStyle("-fx-background-color:" + UIColors.FM_GREY);
            treePane.setCenter(AVLT_PANE);
            sceneTree = new Scene(treePane);

            treeWindow.setTitle("Navigation and Status tree");
            treeWindow.setScene(sceneTree);
            if (!window.isFullScreen()) {
                  treeWindow.show();
            }
      }

      /**
       * Creates an HBox with the menu and exit buttons.
       *
       * @return HBox
       */
      private static GridPane getExitBox() {
            GridPane buttonBox = new GridPane(); // HBox with spacing provided
            buttonBox.setHgap(2);
            menuButton = ButtoniKon.getMenuButton();
            ColumnConstraints col0 = new ColumnConstraints();
            col0.setPercentWidth(50);
            buttonBox.setId("buttonBox");
            buttonBox.getColumnConstraints().add(col0);
            buttonBox.setPadding(new Insets(15, 15, 15, 15));
            buttonBox.addColumn(2, menuButton);

            return buttonBox;
      }


      private static HBox getAccountBox() {
            HBox hBox = new HBox(2);
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(2, 2, 2, 2));

            String name = UserData.getFirstName();
            Button acctBtn = ButtoniKon.getAccountButton();
            Button iGotPdBtn = ButtoniKon.getIgotPdButton();

            acctBtn.setOnMousePressed(e -> {
                  if (e.isSecondaryButtonDown()) {
                        acctSecondaryAction();
                  } else {
                        window.setScene(getFirstScene(AccountProfileMenu.profileMenu(actionWindow)));
                        firstPane.setOnMouseReleased(f -> window.setScene(getFirstScene(getFilePane())));
                        firstPane.setTop(getAccountBox());
                  }
            });
            // @TODO implement the getPaidPane with real data
            // and uncomment this page.
            // iGotPdBtn.setOnMouseClicked(e -> getPaidPane());
            // cursors
            acctBtn.setOnMouseEntered(e -> FlashMonkeyMain.getWindow().getScene().setCursor(Cursor.HAND));
            acctBtn.setOnMouseExited(e -> FlashMonkeyMain.getWindow().getScene().setCursor(Cursor.DEFAULT));
            iGotPdBtn.setOnMouseEntered(e -> FlashMonkeyMain.getWindow().getScene().setCursor(Cursor.HAND));
            iGotPdBtn.setOnMouseExited(e -> FlashMonkeyMain.getWindow().getScene().setCursor(Cursor.DEFAULT));

            hBox.getChildren().addAll(new Label(name), acctBtn, iGotPdBtn);

            return hBox;
      }

      private static void acctSecondaryAction() {
            String version = VersionTimeStamp.getVersionBuildStamp();
            FxNotify.notificationDark("Version", version, Pos.CENTER, 10, "image/blue_flash_128.png", FlashMonkeyMain.getWindow());
      }

      public static void setReturnToFullScreen(boolean bool) {
            returnToFullScreen = bool;
      }

      public static boolean isSetToFullScreen() {
            return returnToFullScreen;
      }

      public static void wasMaximizedReset() {
            if (isSetToFullScreen()) {
                  setReturnToFullScreen(false);
                  getWindow().setFullScreenExitHint("");
                  getWindow().setIconified(false);
                  getWindow().setFullScreen(true);
            }
      }

      public static void minimizeFullScreen() {
            if (FlashMonkeyMain.getWindow().isFullScreen()) {
                  FlashMonkeyMain.getWindow().setFullScreen(false);
                  FlashMonkeyMain.getWindow().setIconified(true);
                  FlashMonkeyMain.setReturnToFullScreen(true);
            }
      }

      /**
       * Sets the error message to be displayed
       * in the UI.
       *
       * @return
       */
      public static String getErrorMsg() {
            return errorMsg;
      }

      /**
       * Gets the error message to be displayed
       * in the UI
       *
       * @param str
       */
      public static void setErrorMsg(String str) {
            errorMsg = str;
      }

      /**
       * Used with getFileScene, the initial screen that is presented to the EncryptedUser.EncryptedUser.
       * It provides the label along with graphics and reads in
       * the File[] listOfFiles from the users previous card decks, if any exist. If
       * not, then will only display the create button.
       * File methods that this method relies on are in the FlashCard() class.
       *
       * @return Returns the BorderPane containing the files
       */
      private static GridPane getFilePane() {
            Button newDeckButton;
            int width = SceneCntl.getFileSelectPaneWd();
            GridPane gridPane1 = new GridPane();
            gridPane1.setPadding(new Insets(20, 0, 0, 0));
            ScrollPane scrollP = new ScrollPane();
            scrollP.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollP.setPrefWidth(width);
            scrollP.setMaxHeight(446);
            scrollP.setStyle("-fx-background-color:transparent");

            Label selectLabel = new Label("Select a deck to study");
            selectLabel.setId("label24White");
            gridPane1.setId("fileSelectPane");
            gridPane1.setAlignment(Pos.TOP_CENTER);
            gridPane1.setPrefHeight(width);

            // *** PLACE NODES IN PANE ***
            GridPane.setHalignment(selectLabel, HPos.CENTER);

            DeckSelectorPane deckSelector = new DeckSelectorPane();
            deckSelector.selectFilePane();
            deckSelector.paneForFiles.setPrefWidth(width);
            deckSelector.paneForFiles.setId("fileSelectPane");

            // The search for resources button
            searchRscButton = ButtoniKon.getSearchRsc();

            // If there are files, display them in a ScrollPane  */
            // from import static FlashCardOps
            if (getInstance().getAgrListSize() > 0) {
                  scrollP.setContent(deckSelector.paneForFiles);

                  gridPane1.addRow(1, scrollP); // column 0, row 1
                  newDeckButton = ButtoniKon.getNewDeck();

                  HBox buttonBox = new HBox(25);
                  buttonBox.setPadding(new Insets(20, 0, 6, 0));
                  buttonBox.getChildren().addAll(newDeckButton, searchRscButton);
                  gridPane1.addRow(0, selectLabel);
                  gridPane1.addRow(3, buttonBox);

                  backButton = ButtoniKon.getBackButton();
                  backButton.setAlignment(Pos.CENTER);
                  backButton.setOnAction(e -> {
                        deckSelector.paneForFiles.getChildren().clear();
                        gridPane1.getChildren().clear();
                        window.setScene(getFirstScene(getFilePane()));
                  });

                  // new file button action
                  newDeckButton.setOnAction(e -> {
                        gridPane1.getChildren().clear();
                        HBox hb = new HBox(25);
                        hb.setPadding(new Insets(20, 0, 6, 0));
                        hb.getChildren().addAll(backButton, searchRscButton);
                        // from flashmonkey.FlashCardOps.fileSelectPane()
                        deckSelector.paneForFiles.getChildren().clear();
                        gridPane1.addRow(0, deckSelector.newFile());
                        gridPane1.addRow(2, hb);
                  });
            } else // else display a new file pane
            {
                  searchRscButton.setPadding(new Insets(20, 0, 0, 0));
                  gridPane1.addRow(2, deckSelector.paneForFiles);
                  gridPane1.addRow(4, searchRscButton);
            }

            searchRscButton.setOnAction(e -> {
                  getSearchWindow();
            });

            menuButton.setDisable(true);
            return gridPane1;
      }


      public static void showSignInPane() {
            //LOGGER.debug("showSignInPane() called");
            SignInModel model = new SignInModel();
            SignInPane signInPane = new SignInPane(model);
            model.getFormInstance();
            window.setScene(getFirstScene(signInPane.getMainGridPane()));
            signInPane.getMainGridPane().requestFocus();
      }

      public static void showSignUpPane() {
            SignUpModel model = new SignUpModel();
            SignUpPane signUpPane = new SignUpPane(model);
            model.getFormInstance();
            window.setScene(getFirstScene(signUpPane.getSignUpPane()));
            signUpPane.getSignUpPane().requestFocus();
      }

      public static void showConfirmPane() {
            ConfirmationModel model = new ConfirmationModel();
            ConfirmationPane confirmPane = new ConfirmationPane(model);
            model.getFormInstance();
            window.setScene(getFirstScene(confirmPane.getConfirmPane()));
            confirmPane.requestFocus();
      }

      private static void showIntroPane() {
            IntroPane introPane = new IntroPane();
            introPane.getSignInBtn().setOnAction(e -> showSignInPane());
            introPane.getSignUpBtn().setOnAction(e -> showSignUpPane());
            window.setScene(getFirstScene(introPane.getIntroPane()));
            introPane.getIntroPane().requestFocus();
      }

      public static void showResetOnePane() {
            // @TODO REmove model if not needed
            // Do we need the model here?
            //ResetOneModel model = new ResetOneModel();
            ResetOnePane reset = new ResetOnePane();
            //model.getFormInstance();
            window.setScene(getFirstScene(reset.getMainGridPane()));
            reset.getMainGridPain().requestFocus();
      }

      public static void showResetTwoPane() {
            // @TODO Remove model if not needed
            // Do we need the model here?
            //ResetTwoModel model = new ResetTwoModel();
            ResetTwoPane reset = new ResetTwoPane();
            //model.getFormInstance();
            window.setScene(getFirstScene(reset.getMainGridPane()));
            reset.getMainGridPain().requestFocus();
      }


      /**
       * Displays the users pay.
       */
      private static void getPaidPane() {
            if (Utility.isConnected()) {
                  Scene scene = new Scene(PayPane.getPayPane());
                  scene.getStylesheets().addAll("css/buttons.css", "css/mainStyle.css");
                  if (actionWindow != null && actionWindow.isShowing()) {
                        actionWindow.close();
                  }
                  actionWindow = new Stage();
                  actionWindow.setScene(scene);
                  actionWindow.show();
            } else {
                  // show error message
                  String emojiPath = "image/flashFaces_sunglasses_60.png";
                  String message = "Oooph!" +
                      "\n I need to be connected to the cloud \n to create or change your account information.";
                  FxNotify.notificationWarning("Please Connect!", message, Pos.CENTER, 9,
                      emojiPath, FlashMonkeyMain.getWindow(), 15);
            }
      }

      public static void setActionWindow(Scene scene) {
            if (actionWindow != null && actionWindow.isShowing()) {
                  actionWindow.close();
            }
            minimizeFullScreen();
            actionWindow = new Stage(StageStyle.UTILITY);
            actionWindow.setScene(scene);
            actionWindow.setOnHidden(e -> wasMaximizedReset());
            actionWindow.show();
      }

      public static void resetActionWindow(Pane pane) {
            if (actionWindow != null && actionWindow.isShowing()) {
                  actionWindow.setScene(new Scene(pane));
                  actionWindow.show();
            }
      }

      public static void closeActionWindow() {
            if (actionWindow != null && actionWindow.isShowing()) {
                  actionWindow.close();
                  actionWindow = null;
            }
      }

      public static void getProfilePane() {
            if (Utility.isConnected()) {
                  StudentModel model = new StudentModel();
                  StudentPane stuPane = new StudentPane();
                  model.getFormInstance();
                  Scene scene = new Scene(stuPane.getMainGridPain());
                  scene.getStylesheets().addAll("css/buttons.css", "css/fxformStyle.css");
                  if (actionWindow != null && actionWindow.isShowing()) {
                        actionWindow.close();
                  }
                  minimizeFullScreen();
                  actionWindow = new Stage();
                  actionWindow.setScene(scene);
                  actionWindow.setOnHidden(e -> wasMaximizedReset());
                  actionWindow.show();
            } else {
                  // show error message
                  //showPopup();
            }
      }

      public static void getSubscribeWindow() {
            if (Utility.isConnected()) {
                  EcoPane ePane = new EcoPane();
                  Scene scene = new Scene(ePane.getReqSubscribePane());
                  if (actionWindow != null && actionWindow.isShowing()) {
                        actionWindow.close();
                  }
                  minimizeFullScreen();
                  actionWindow = new Stage();
                  actionWindow.setScene(scene);
                  actionWindow.setOnHidden(e -> wasMaximizedReset());
                  actionWindow.show();
            } else {
                  //showPopup();
            }
      }

      /**
       * The users account form
       */
      public static void getFMSAccountWindow() {
            AccountPane actPane = new AccountPane();
            Scene scene = new Scene(actPane.getMainGridPain());
            if (actionWindow != null && actionWindow.isShowing()) {
                  actionWindow.close();
            }
            minimizeFullScreen();
            actionWindow = new Stage();
            actionWindow.setScene(scene);
            actionWindow.setOnHidden(e -> wasMaximizedReset());
            actionWindow.show();
      }

      /**
       * General Purpose webview window
       */
      public static void getWebView(String page) {
            EcoPane eco = new EcoPane();
            Scene scene = new Scene(eco.getWebViewPane(page, 400, 650));
            if (actionWindow != null && actionWindow.isShowing()) {
                  actionWindow.close();
            }
            minimizeFullScreen();
            actionWindow = new Stage();
            actionWindow.setScene(scene);
            actionWindow.setOnHidden(e -> wasMaximizedReset());
            actionWindow.show();
      }

      /**
       * The deck search and econ panes
       */
      public static void getSearchWindow() {
            LOGGER.debug("getSearchWindow() called");
            if (actionWindow != null && actionWindow.isShowing()) {
                  actionWindow.close();
            }

            minimizeFullScreen();

            Scene scene = new Scene(ConsumerPane.getInstance().getConsumerPane());
            scene.getStylesheets().addAll("css/consumerButtons.css", "css/consumerPane.css");
            actionWindow = new Stage();
            actionWindow.setScene(scene);
            actionWindow.setResizable(false);
            actionWindow.setOnHidden(e -> {
                  ConsumerPane.getInstance().onClose();
                  FlashMonkeyMain.closeActionWindow();
                  wasMaximizedReset();
            });
            actionWindow.show();
      }

      /**
       * The video player popup
       *
       * @return
       */
      public static void getVideoWindow(String mediaPathString) {
            LOGGER.debug("getVideoWindow called");
            if (actionWindow != null && actionWindow.isShowing()) {
                  actionWindow.close();
            }
            minimizeFullScreen();
            VideoPlayerPopUp vidPlayPopup = VideoPlayerPopUp.getInstance(mediaPathString);
            StackPane playerStackPane = vidPlayPopup.getVideoPlayerPane();
            playerStackPane.setPrefSize(1054, 644);
            playerStackPane.setId("vidPlayerVBox");
            Scene scene = new Scene(playerStackPane);
            scene.getStylesheets().addAll("css/mainStyle.css", "css/buttons.css");

            actionWindow = new Stage();
            actionWindow.setScene(scene);
            actionWindow.setOnHidden(e -> {
                  vidPlayPopup.onClose();
                  FlashMonkeyMain.closeActionWindow();
                  wasMaximizedReset();
            });
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

      /**
       * Returns the Main Window's current XY location
       *
       * @return Point2D location of the current XY position
       */
      public static Point2D getWindowXY() {
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
      public static void setWindowToMenu() {
            LOGGER.debug("setWindowToNav called");
            isInEditMode = false;
            window.setScene(getMenuScene());
      }

      /**
       * Returns if editor/creator mode is active.
       * Side effects chose AVLTreePane and TextCell.buildCell
       *
       * @return true if in editor/creator mode
       */
      public static final boolean isInEditorMode() {
            return isInEditMode;
      }

      /**
       * Used to set if the edit/create class is in use and
       * visible. Used by AVLTreePane: Checks the visibility
       * for navigation onClick
       *
       * @param bool
       */
      public static void setIsInEditMode(boolean bool) {
            isInEditMode = bool;
      }

      public static void deckNotCompatiblePopup() {
            //dontSaveDeck.add(deckFileName);
            String msg = "   SOMETHING IS WRONG! \n\n" +
                "   The deck is not compatible with this \n" +
                "   version of FlashMonkey.\n " +
                "   We're working on this, but for now we \n" +
                "   are not able to process the file. \n\n";
            FxNotify.notificationDark("OUCH!", msg, Pos.CENTER, 30,
                "emojis/Flash_headexplosion_60.png", FlashMonkeyMain.getWindow());
      }

      /**
       * Only used for verification to activate stop().
       * Flag avoids unneccessary actions when user quits
       * before logging in.
       */
      public static void setLoggedinToTrue() {
            isLoggedinProperty.setValue(true);
      }

      /**
       * Only used for verification to activate stop()
       * and action on sleep.
       * Flag avoids unneccessary actions when user quits
       * before logging in.
       */
      public static boolean getImmutableIsLoggedin() {
            return isLoggedinProperty.get();
      }

      private void windowCloseListener() {

      }

      @Override
      public void saveOnExit() {
            throw new UnsupportedOperationException("Called saveOnExit and saveOnExit in FlashMonkeyMain is not supported.");
      }

      @Override
      public void onClose() {
            if (isLoggedinProperty.get()) {
                  if (readFlash != null) {
                        readFlash.leaveAction();
                  } else if (createFlash != null) {
                        createFlash.saveReturnAction();
                  }
                  SceneCntl.onStop();
                  Report.getInstance().endSessionTime();
                  if (createFlash != null) {
                        createFlash.onClose();
                  }
                  // checks if deck is compatible before saving
                  //FlashCardOps.getInstance().safeSaveFlashList();
            }
      }

      /**
       * Called by Application. When FlashMonkey is stopped by the OS or
       * by closing the main window.
       * Saves the current flashDeck to file.
       */
      private boolean notStopped = true;
      @Override
      public void stop() {
            if(notStopped ) {
                  notStopped = false;
                  LOGGER.debug("Called STOP in FlashMonkeyMain");

                  if (isLoggedinProperty.get()) {
                        if (readFlash != null) {
                              readFlash.leaveAction();
                        } else if (createFlash != null) {
                              createFlash.saveReturnAction();
                        }
                        SceneCntl.onStop();
                        Report.getInstance().endSessionTime();
                        if (createFlash != null) {
                              createFlash.onClose();
                        }
                        if (actionWindow != null && actionWindow.isShowing()) {
                              actionWindow.close();
                        }
                        // checks if deck is compatible before saving
                        FlashCardOps.getInstance().safeSaveFlashList();
                  }
            }

            //onClose();
            System.exit(0);
      }



      public static void main(String[] args) {
            launch(args);
      }


      /* *** FOR TESTING *** */
      @FMAnnotations.DoNotDeployMethod
      public static Point2D getCreateButtonXY() {
            Bounds bounds = createButton.getLayoutBounds();
            return createButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 20);
      }


}


