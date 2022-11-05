/*
 * AUTHOR: Lowell Stadelman
 */
package flashmonkey;


import authcrypt.Auto;
import authcrypt.user.EncryptedStud;
import campaign.Report;
import campaign.db.DBInsert;
import ch.qos.logback.classic.Level;
import ecosystem.ConsumerPane;

import ecosystem.WebEcoPane;
import ecosystem.PayPane;
import fileops.BaseInterface;
import fileops.DirectoryMgr;
import fileops.utility.Utility;
import flashmonkey.utility.VersionTimeStamp;
import fmannotations.FMAnnotations;
import forms.*;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

//import javafx.stage.StageStyle;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import media.sound.SoundEffects;
import org.slf4j.LoggerFactory;
import type.celltypes.VideoPlayerPopUp;
import uicontrols.*;

import java.awt.Toolkit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.prefs.BackingStoreException;

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
      public static final String VERSION_STR = "1.4.1";
      // for serializing objects to file. The serial version.
      // Used with "serialVersionUID"
      public static final long VERSION = 20200612;
      public static final AVLTreePane AVLT_PANE = new AVLTreePane();

      private static Stage primaryWindow;
      private static Stage actionWindow;
      protected static Stage treeWindow;

      private static Image flash;
      //private static Image bckgndImg;

      private static CreateFlash createFlash;
      private static ReadFlash readFlash;

      //protected static AVLTreePane avltPane = new AVLTreePane();
      protected static Scene sceneTree;
      private static Scene rootScene;
      private static BorderPane treePane;
      private static BorderPane firstPane;
      //    private static Button exitButton;
      private static Button searchButton;
      private static Button menuButton;
      private static Button createButton;
      private static Button backButton;
      // campaign
      // @TODO Add report back in
      //private static Report report;
      // The error message to be displayed in the UI
      private static String errorMsg = "";
      // FLAGS
      // if the account menu is showing. Used by
      // acct button.
      private static boolean acctButtonRowShowing = false;
      private static boolean acctShowing = false;
      private static boolean isInEditMode;
      private static BooleanProperty returnToFullScreenProperty = new SimpleBooleanProperty(false);
      private static final BooleanProperty isLoggedinProperty = new SimpleBooleanProperty(false);


      // THE LOGGER
      //private static final Logger LOGGER = LoggerFactory.getLogger(FlashMonkeyMain.class);
      private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FlashMonkeyMain.class);


      // *** Java FX UI *** STAGE *** ***
      @Override
      public void start(Stage primaryStage) {

            SoundEffects.APP_START.play();

            // We control when the platform closes to ensure
            // any open stage/window saves its work before
            // the application is closed. We must call Platform.exit()
            // to close the JVM.
            //Platform.setImplicitExit(false);
            try {
                  // set the app to the users stored preferences
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
                  primaryWindow = primaryStage;

                  // set to call onClose() before stage is closed.
                  primaryWindow.setOnCloseRequest(e -> {
                        //saveOnExit();
                        onClose();
                  });
                  treeWindow = new Stage();
                  // Getting image resources as a stream is finicky. No more than two dashes per name,
                  // and cannot be under a subdirectory. Although conveniently finds the resource directory
                  // on it's own.
      //            flash = new Image(getClass().getResourceAsStream("/image/logo/vertical_logo_white_8per_120x325.png"));
                  Image icon = new Image(getClass().getResourceAsStream("/image/logo/blue_flash_128.png"));
                  primaryWindow.getIcons().add(icon);
                  // Determine if FlashMonkey Data Directory
                  // exists and show signUp or signIn
                  if (flashmonkeyExists()) {
                        // Attempt to login from FMKeyChain
                        // 1. Get the directory
                        // 2. Check if resu.enc key exists
                        if(DirectoryMgr.resuExists()) {
                              Auto auto = new Auto();
                              // 3. ifExists == true && notConnected
                              if( ! Utility.isConnected() ) {
                                    // log-in locally from file.
                                    if(auto.login()) {
                                          //SoundEffects.ACCESS_GRANTED.play();
                                          // Does not affect log-in, only for actions.
                                          setLoggedinToTrue();
                                          InnerScene.setFirstScene(getFilePane());
                                          setTopPane();
                                    }
                                    else {
                                          // show login over pre
                                          showSignInPane();
                                    }
                              } // else is connected
                              else {
                                    // 3a. Ifexists == true && isConnected():
                                    // 3a1. Show security prePane over fileSelectPre
                                    //    - SecurityPre is circle-dot spinner
                                    //InnerScene.setFirstScene(getFilePane(true));
                                    InnerScene.setFirstScene(getPreFilePane());
                                    // allow graphics to work while waiting for
                                    // access from VertX and deckList from S3.
                                    ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
                                    Runnable task = () -> {
                                          auto.login();
                                          scheduledExecutor.shutdown();
                                    };
                                    scheduledExecutor.execute(task);
                              }
                        }
                        else {
                              // 3b. ifExists == false: show createAcct over fileSelectPre
                              showSignUpPane();
                        }
                  }
                  else {
                        // This is the first time FlashMonkey has
                        // been set up. FlashMonkeyData dir does not
                        // exist.
                        showIntroPane();
                  }

                  rootScene.getStylesheets().addAll("css/buttons.css", "css/mainStyle.css");
                  primaryWindow.setTitle("FlashMonkey");
                  primaryWindow.show();

            } catch (BackingStoreException e) {
                  LOGGER.warn(e.getMessage());
                  e.printStackTrace();
            } catch (Exception e) {
                  e.printStackTrace();
            }
      } // ******** END START ******* //

      public static String getVersionStr() {
            return VERSION_STR;
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
       * Sets the window to the ModeMenu. Use
       * when app is in another section. E.G.
       * ReadFlash or CreateFlash.
       */
      public static void setWindowToModeMenu() {
            LOGGER.debug("setWindowToNav called");
            isInEditMode = false;
            acctButtonRowShowing = true;
            InnerScene.setFirstScene(getModeMenuPane());
      }

      /**
       * Sets the CenterPane to the ModeMenu. Use
       * when the app is already in the first section.
       * E.G. from FileSelectPane when a new file is
       * selected.
       */
      public static void setPaneToModeMenu() {
            isInEditMode = false;
            InnerScene.setToModeMenuPane();
      }

      /**
       * Creates the Navigation/Menu stage. The Navigation/MenuStage provides the buttons
       * used to navigate to studying, back to the firstScene or deck selection, or to create/edit.
       *
       * @return Returns the Navigation Scene
       */
      private static GridPane getModeMenuPane() {
            GridPane gridPane = new GridPane();
            gridPane.setAlignment(Pos.CENTER);
            VBox buttonBox = new VBox(2);

            LOGGER.debug("\n *** getNavigationScene() ***");
            Label label = new Label("Action Menu");
            label.setId("label24White");

            // *** CENTER MENU BUTTONS ***
            // go back to file select pane
            Button deckSelectButton = ButtoniKon.getDeckSelectButton();
            deckSelectButton.setOnAction(e -> InnerScene.setTofilePane());
            // go to study menu
            Button studyButton = ButtoniKon.getStudyButton();
            studyButton.setOnAction(e -> studyButtonAction());

            // go to create pane
            createButton = ButtoniKon.getCreateButton();
            createButton.setOnAction(e -> {
                  LOGGER.debug("in createButton.setOnAction()");
                  createButtonAction();
            });

            //ReadFlash.getInstance().resetGaugeValues();
            buttonBox.getChildren().addAll(label, deckSelectButton, studyButton, createButton);
            gridPane.addRow(0, buttonBox);

            buttonBox.setId("opaqueMenuPaneDark");
            buttonBox.setAlignment(Pos.TOP_CENTER);
            gridPane.setId("rightPaneTransp");

            menuButton.setDisable(true);

            ScaleTransition animate = animateScaleV(gridPane);
            animate.play();

            return gridPane;
      } // end Scene getNavigationScene

      protected static void createButtonAction() {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            isInEditMode = true;
            Timer.getClassInstance().startTime();
            createFlash = CreateFlash.getInstance();
            menuButton.setDisable(false);
            showCreatePane();
      }

      public static void showCreatePane() {
//            setPrimaryWindowDims(createFlash.createFlashScene());
            //primaryWindow.setScene(createFlash.createFlashScene());   // called once
            createFlash = CreateFlash.getInstance();
            createFlash = CreateFlash.getInstance();
            rootScene.setRoot(createFlash.createFlashScene());
            createFlash.showComboBox();
      }


      /**
       * Sends EncryptedUser to a new screen and calls the
       * treePane
       */
      private static void studyButtonAction() {
            SoundEffects.PRESS_STUDY.play();
            LOGGER.debug("\n*** studyButtonAction() FlashMonkeyMain ***");
            Timer.getClassInstance().testTimeStop();
            LOGGER.debug("testTime: " + Timer.getClassInstance().getTakeTestTime());
            readFlash = ReadFlash.getInstance();
            menuButton.setDisable(false);
            ReadFlash.getInstance().resetGaugeValues();
            rootScene.setRoot(readFlash.readScene());
            //createFlash = CreateFlash.getInstance();
//            setPrimaryWindowDims(readFlash.readScene());
            //primaryWindow.setScene(readFlash.readScene());
      }

      /**
       * Assist with StudyButtonAction used in navigation scene when creating
       * a new study session. Actions are not needed when moving from either test
       * or from QAndA sessions when the UI should be consistent and the tree structure
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
            if (!primaryWindow.isFullScreen()) {
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

      /**
       * Provides the account button and actions
       * @return
       */
      private static HBox getAccountBox() {
            HBox hBox = new HBox(2);
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(2, 2, 2, 2));
            Button acctBtn = ButtoniKon.getAccountButton();
            Button iGotPdBtn = ButtoniKon.getIgotPdButton();

            acctBtn.setOnMousePressed(m -> {
                  if (m.isSecondaryButtonDown()) {
                        acctSecondaryAction();
                  } else if( !acctShowing) {
                        acctShowing = true;
                        InnerScene.setToAccountProfileMenu();
                      firstPane.setOnMouseClicked(f -> {
                            acctShowing = false;
                            InnerScene.setTofilePane();
                      });
                  } else {
                        acctShowing = false;
                        InnerScene.setTofilePane();
                  }
            });
            // @TODO implement the getPaidPane with real data
            // and uncomment this page.
            // iGotPdBtn.setOnMouseClicked(e -> getPaidPane());
            // hBox.getChildren().addAll(emailLabel, acctBtn, iGotPdBtn);
            hBox.getChildren().addAll( acctBtn, iGotPdBtn);

            return hBox;
      }

      private static void acctSecondaryAction() {
            String version = VersionTimeStamp.getVersionBuildStamp();
            FxNotify.notification("Version", version, Pos.CENTER, 10, "image/blue_flash_128.png", primaryWindow);
      }

      /**
       * For coordination on apple when user is using the app
       * in maximized size.
       */
      public static void wasMaximizedReset() {
            LOGGER.debug("called");
            if (returnToFullScreenProperty.get()) {
                  LOGGER.debug("isSetToFullScreen was set to TRUE. Now setting it to full screen");
                  returnToFullScreenProperty.setValue(false);
                  primaryWindow.setFullScreenExitHint("");
                  primaryWindow.setIconified(false);
                  primaryWindow.setFullScreen(true);
            }
      }

      /**
       * For coordination on apple when user is using the app
       * in maximized size.
       */
      public static void minimizeFullScreen() {
            LOGGER.debug("Called");
            if (primaryWindow.isFullScreen()) {
                  LOGGER.debug(" primaryWindow is set to Full screen. Now setting it to minimize");
                  primaryWindow.setFullScreen(false);
                  primaryWindow.setIconified(true);
                  returnToFullScreenProperty.setValue(true);
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
            int width = SceneCntl.getFileSelectPaneWd();
            DeckSelectorPane deckSelector = new DeckSelectorPane();
            deckSelector.selectFilePane();
            deckSelector.paneForFiles.setPrefWidth(width);
            deckSelector.paneForFiles.setId("fileSelectPane");

            ScrollPane scrollP = getFileScroll(width);
            GridPane fileGridPane;
            Button newDeckButton = ButtoniKon.getNewDeck();
            newDeckButton.setDisable(false);

            // If there are files, display them in a ScrollPane  */
            // from import static FlashCardOps
            if (FlashCardOps.getInstance().getAgrListSize() > 0) {
                  fileGridPane = getGridPane(newDeckButton);
                  disableButtons(false);
                  scrollP.setContent(deckSelector.paneForFiles);
                  fileGridPane.addRow(1, scrollP); // column 0, row 1

                  // new file button action
                  newDeckButton.setOnAction(e -> {
                        SoundEffects.PRESS_BUTTON_COMMON.play();

                        //backButton.setAlignment(Pos.CENTER);
                        backButton.setOnAction(f -> {
                              SoundEffects.PRESS_BUTTON_COMMON.play();
                              deckSelector.paneForFiles.getChildren().clear();
                              fileGridPane.getChildren().clear();
                              InnerScene.setTofilePane();
                        });
                        fileGridPane.getChildren().clear();
                        HBox hbox = new HBox(25);
                        hbox.setPadding(new Insets(20, 0, 6, 0));
                        //hbox.setAlignment(Pos.CENTER);
                        hbox.getChildren().addAll(backButton, searchButton);
                        // from flashmonkey.FlashCardOps.fileSelectPane()
                        deckSelector.paneForFiles.getChildren().clear();
                        // Animate
                        //GridPane gp = deckSelector.newFile();
                        ScaleTransition animate = new ScaleTransition(Duration.seconds(0.3), fileGridPane);
                        animate.setFromY(0.3);
                        animate.setToY(1.0);
                        fileGridPane.addRow(0, deckSelector.newFile());
                        fileGridPane.addRow(2, hbox);
                        animate.play();
                  });
            } else {// else display a new file pane
                  fileGridPane = new GridPane();
                  searchButton.setPadding(new Insets(20, 0, 0, 0));
                  searchButton.setDisable(false);
                  fileGridPane.addRow(2, deckSelector.paneForFiles);
                  fileGridPane.addRow(4, searchButton);
            }

            menuButton.setDisable(true);
            return fileGridPane;
      }

      private static GridPane getPreFilePane() {
            GridPane gridPane1 = new GridPane();
            gridPane1.setPadding(new Insets(20, 0, 0, 0));
            int width = SceneCntl.getFileSelectPaneWd();

            DeckSelectorPane deckSelector = new DeckSelectorPane();
            deckSelector.selectPrePane();
            deckSelector.paneForFiles.setPrefWidth(width);
            deckSelector.paneForFiles.setId("fileSelectPane");
            ScrollPane scrollP = getFileScroll(width);

            Button newDeckButton = ButtoniKon.getNewDeck();
            newDeckButton.setDisable(true);
            gridPane1 = getGridPane(newDeckButton);
            disableButtons(true);
            scrollP.setContent(deckSelector.paneForFiles);
            gridPane1.addRow(1, scrollP); // column 0, row 1

            return gridPane1;
      }

      private static void disableButtons(boolean bool) {
            backButton.setDisable(bool);
            searchButton.setDisable(bool);
            menuButton.setDisable(bool);
      }

      private static GridPane getGridPane(Button newDeckButton) {
            HBox buttonBox = getButtonBox(newDeckButton);
            GridPane gridPane1 = new GridPane();
            gridPane1.setPadding(new Insets(20, 0, 0, 0));
            Label selectLabel = new Label("SELECT A STUDY DECK");
            selectLabel.setId("label24White");
            gridPane1.setId("opaqueMenuPaneDark");
            gridPane1.setAlignment(Pos.TOP_CENTER);
            gridPane1.addRow(0, selectLabel);
            gridPane1.addRow(3, buttonBox);

            // *** PLACE NODES IN PANE ***
            GridPane.setHalignment(selectLabel, HPos.CENTER);
            return gridPane1;
      }

      private static HBox getButtonBox(Button newDeckButton) {
            // The search for resources button
            backButton = ButtoniKon.getBackButton();
            searchButton = ButtoniKon.getSearchRsc();
            searchButton.setOnAction(e -> {
                  SoundEffects.PRESS_BUTTON_COMMON.play();
                  getSearchWindow();
            });
            menuButton = ButtoniKon.getMenuButton();
            HBox buttonBox = new HBox(25);
            buttonBox.setPadding(new Insets(20, 0, 6, 0));
            buttonBox.getChildren().addAll(newDeckButton, searchButton);
            return buttonBox;
      }


      private static ScrollPane getFileScroll(int width) {
            ScrollPane scrollP = new ScrollPane();
            scrollP.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollP.setPrefWidth(width);
            scrollP.setFitToHeight(true);
            scrollP.setStyle("-fx-background-color:transparent");
            return scrollP;
      }

      /**
       * Call when this is shown in the first scene.
       */
      public static void showSignInPane() {
            SignInModel model = new SignInModel();
            SignInPane signInPane = new SignInPane(model);
            model.getFormInstance();
            InnerScene.setFirstScene(signInPane.getMainGridPane());
            //primaryWindow.setScene(InnerScene.getFirstScene(signInPane.getMainGridPane()));
            signInPane.getMainGridPane().requestFocus();
      }

      /**
       * Call when this is shown in the first scene.
       */
      public static void showSignUpPane() {
            SignUpModel model = new SignUpModel();
            SignUpPane signUpPane = new SignUpPane(model);
            model.getFormInstance();
            InnerScene.setFirstScene(signUpPane.getSignUpPane());
            signUpPane.getSignUpPane().requestFocus();
      }

      /**
       * Call when in intro section. IE from a hyperlink.
       */
      public static void showSignUpInnerPane() {
            InnerScene.setToSignUpPane();
      }

      public static void showSignInInnerPane() {
            InnerScene.setToSignInPane();
      }

      public static void showConfirmPane() {
            InnerScene.setToConfirmPane();
      }

      private static void showIntroPane() {
            // Log user has opened the application for the first time.
            ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
            Runnable task = () -> {
                  DBInsert.SESSION_NOTE.doInsert(new EncryptedStud());
                  scheduledExecutor.shutdown();
            };
            scheduledExecutor.execute(task);

            IntroPane introPane = new IntroPane();
            introPane.getSignInBtn().setOnAction( e ->{
                  SoundEffects.PRESS_BUTTON_COMMON.play();
                  InnerScene.setToSignInPane();
            });
            introPane.getSignUpBtn().setOnAction(e -> {
                  SoundEffects.PRESS_BUTTON_COMMON.play();
                  InnerScene.setToSignUpPane();
            });
            InnerScene.setFirstScene(introPane.getIntroPane());
            introPane.getIntroPane().requestFocus();
      }

      public static void showResetOnePane() {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            InnerScene.setToResetOnePane();
      }

      public static void showResetTwoPane() {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            InnerScene.setToResetTwoPane();
      }

      /**
       * Animates a scale growth from 30% of its original height to
       * 100% in 300 milliseconds. Grows from center.
       * Synchronized to prevent multiple simultaneous users.
       * @param node
       * @return
       */
      public synchronized static ScaleTransition animateScaleV(Node node) {
            ScaleTransition scaler = new ScaleTransition(Duration.seconds(0.3), node);
            scaler.setFromY(0.3);
            scaler.setToY(1.0);
            return scaler;
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
                  FxNotify.notification("Please Connect!", message, Pos.CENTER, 9,
                      emojiPath, primaryWindow);
            }
      }

      public static void setTreeWindow(Scene scene) {
            if(treeWindow != null) {
                  treeWindow.close();
            }
            minimizeFullScreen();
            treeWindow = new Stage(StageStyle.UTILITY);
            treeWindow.setScene(scene);
            treeWindow.setOnHidden(e -> wasMaximizedReset());
            treeWindow.show();
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
                  Scene scene = new Scene(stuPane.getMainPane());
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
                  String emojiPath = "image/flashFaces_sunglasses_60.png";
                  String message = "I need to be connected to the cloud.";
                  FxNotify.notification("Please Connect!", message, Pos.CENTER, 9,
                          emojiPath, primaryWindow);
            }
      }

      public static void getSubscribeWindow() {
            if (Utility.isConnected()) {
                  WebEcoPane ePane = new WebEcoPane();
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
                  String emojiPath = "image/flashFaces_sunglasses_60.png";
                  String message = "I need to be connected to the cloud.";
                  FxNotify.notification("Please Connect!", message, Pos.CENTER, 9,
                          emojiPath, primaryWindow);
            }
      }

      /**
       * The users account form
       */
      public static void getSubscriptCancelWindow() {
            if(Utility.isConnected()) {
                  SubscriptCancelPane cancelPane = new SubscriptCancelPane();
                  Scene scene = new Scene(cancelPane.getMainGridPain());
                  scene.getStylesheets().addAll("css/buttons.css", "css/mainStyle.css");
                  if (actionWindow != null && actionWindow.isShowing()) {
                        actionWindow.close();
                  }
                  minimizeFullScreen();
                  actionWindow = new Stage();
                  actionWindow.setScene(scene);
                  actionWindow.setOnHidden(e -> wasMaximizedReset());
                  actionWindow.show();
            } else {
                  String emojiPath = "image/flashFaces_sunglasses_60.png";
                  String message = "I need to be connected to the cloud to cancel your account.";
                  FxNotify.notification("Please Connect!", message, Pos.CENTER, 9,
                          emojiPath, primaryWindow);
            }
      }

      /**
       * General Purpose webview window
       */
      public static void getWebView(String page) {
            if(Utility.isConnected()) {
                  WebEcoPane eco = new WebEcoPane();
                  Scene scene = new Scene(eco.getWebViewPane(page, 400, 650));
                  if (actionWindow != null && actionWindow.isShowing()) {
                        actionWindow.close();
                  }
                  minimizeFullScreen();
                  actionWindow = new Stage();
                  actionWindow.setScene(scene);
                  actionWindow.setOnHidden(e -> wasMaximizedReset());
                  actionWindow.show();
            } else {
                  String emojiPath = "image/flashFaces_sunglasses_60.png";
                  String message = "I need to be connected to the cloud to cancel your account.";
                  FxNotify.notification("Please Connect!", message, Pos.CENTER, 9,
                          emojiPath, primaryWindow);
            }
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
            actionWindow.setMaxHeight(Toolkit.getDefaultToolkit().getScreenSize().getHeight());
   //         actionWindow.setResizable(false);
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
            InnerScene.setTofilePane();
      }

      public static Stage getPrimaryWindow() {
            return primaryWindow;
      }

      /**
       * Returns the Main Window's current XY location
       *
       * @return Point2D location of the current XY position
       */
      public static Point2D getWindowXY() {
            double x = primaryWindow.getX();
            double y = primaryWindow.getY();
            return new Point2D(x, y);
      }

      public static void setTopPane() {
            firstPane.setTop(getAccountBox());
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
            FxNotify.notification("OUCH!", msg, Pos.CENTER, 30,
                "emojis/Flash_headexplosion_60.png", primaryWindow);
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


      @Override
      public boolean saveOnExit() {
            throw new UnsupportedOperationException("Called saveOnExit and saveOnExit in FlashMonkeyMain is not supported.");
      }

      @Override
      public void onClose() {
            if (isLoggedinProperty.get()) {
                  if (null != readFlash) {
                        readFlash.leaveAction();
                  } else if (createFlash != null) {
                        createFlash.saveReturnAction();
                        // called in stop()
                        createFlash.saveOnExit();
                        createFlash.onClose();
                  }
                  SceneCntl.onStop();
                  Report.getInstance().endSessionTime();
                  if (null != actionWindow && actionWindow.isShowing()) {
                        actionWindow.close();
                  }
            }
      }

      /**
       * Called by Application. When FlashMonkey is stopped by the OS or
       * by closing the main window.
       * Saves the current flashDeck to file.
       * This stop action may be interupted if the user requests to stop
       * in CreateFlash onSave();
       */
      private boolean notStopped = true;
      @Override
      public void stop() {
            if(notStopped ) {
                  notStopped = false;
                  LOGGER.debug("Called STOP in FlashMonkeyMain");
                  //SoundEffects.APP_END.play();

                  if (isLoggedinProperty.get()) {
                        // checks if deck is compatible before saving
                        FlashCardOps.getInstance().safeSaveFlashList();
                  }
            }
            //onClose();
            Platform.exit();
            System.exit(0);
      }

      /* ***************************************** */

      /////////         MAIN METHOD          ////////

      /* ***************************************** */

      public static void main(String[] args) {
            launch(args);
      }


      /* ***************************************** */

      /////////         INNER CLASS          ////////

      /* ***************************************** */

      private class InnerScene {

            private static GridPane gridPaneFirstScene = new GridPane();
            private static VBox spacerL = new VBox();
            /**
             * The first scene is the sign-in or signUp page. Then either
             * fileSelectPane pane (study deck) or create authcrypt.user.
             * IF fileSelectPane, also provides the ability to create a new deck.
             *
             * @return Returns the first scene or landing scene
             */
            public static void setFirstScene(GridPane focusPane) {
                  //Scene firstScene;
                  firstPane = new BorderPane();
                  setupFirstGrid();
                  menuButton = ButtoniKon.getMenuButton();
                  gridPaneFirstScene.addRow(3, focusPane);

                  // Set the account access button to the top
                  // always. If not the log in scene. HAHA
                  if(acctButtonRowShowing) {
                        firstPane.setTop(getAccountBox());
                        firstPane.setPadding(new Insets(0, 0, 100, 0));
                        spacerL.setMinHeight(280);
                  }

                  firstPane.setCenter(gridPaneFirstScene);
                  // grey #393E46  // #29AbE2
                  firstPane.setStyle("-fx-background-color:" + UIColors.FM_GREY); //#393E46"); //086ABF
                  firstPane.setId("bckgnd_image"); // the background image

                  //setPrimaryWindowDims(firstPane);
                  if(rootScene == null) {
                        rootScene = new Scene(firstPane, SceneCntl.getAppBox().getWd(), SceneCntl.getAppBox().getHt());
                        //rootScene = new Scene(firstPane, SceneCntl.getScreenWd(), SceneCntl.getScreenHt());
                        primaryWindow.setScene(rootScene);
                  } else {
                        rootScene.setRoot(firstPane);
                        primaryWindow.setScene(rootScene);
                  }
            }

            private void setOverPre(GridPane focusPane) {
                  firstPane = new BorderPane();
                  setupFirstGrid();
                  gridPaneFirstScene.addRow(3, focusPane);




            }

            private static void setupFirstGrid() {
                  VBox spacer = new VBox();
                  spacer.setPrefHeight(116);
                  spacerL.setPrefHeight(40);
                  // Calculate logo fit height
                  int fitHeight = 128; //calcFitHeight((int) focusPane.getPrefHeight());
                  int gapHeight = (int) calcGapHeight(fitHeight);
                  VBox imageBox = new VBox();
                  imageBox.setPrefHeight(fitHeight);
                  imageBox.setAlignment(Pos.CENTER);
                  gridPaneFirstScene.setAlignment(Pos.TOP_CENTER);
                  gridPaneFirstScene.setVgap(gapHeight);
                  // *** PLACE NODES IN PANE ***
                  gridPaneFirstScene.getChildren().clear();
                  gridPaneFirstScene.addRow(0, spacer);
                  gridPaneFirstScene.addRow(1, imageBox); // column 0, row 1
                  gridPaneFirstScene.addRow(2, spacerL);
            }

            // The action menu
            private static void setToModeMenuPane() {
                  acctButtonRowShowing = true;
                  gridPaneFirstScene.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 3);
                  gridPaneFirstScene.addRow(3, getModeMenuPane());
            }

            private static void setTofilePane() {
                  acctButtonRowShowing = true;
                  gridPaneFirstScene.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 3);
                  GridPane filePane = getFilePane();
                  ScaleTransition animate = animateScaleV(filePane);
                  gridPaneFirstScene.addRow(3, filePane);
                  // prevent unwanted actions on pane click
                  // bc the pane has an action.
                  firstPane.setOnMouseClicked(null);
                  animate.play();
            }



            private static void setToConfirmPane() {
                  ConfirmationModel model = new ConfirmationModel();
                  ConfirmationPane confirmPane = new ConfirmationPane(model);
                  model.getFormInstance();
                  gridPaneFirstScene.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 3);
                  gridPaneFirstScene.addRow(3, confirmPane.getConfirmPane());
                  confirmPane.requestFocus();
            }

            private static void setToAccountProfileMenu() {
                  gridPaneFirstScene.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 3);
                  GridPane gp = AccountProfileMenu.profileMenu();
                  ScaleTransition animate = animateScaleV(gp);
                  gridPaneFirstScene.addRow(3, gp);
                  animate.play();
            }

            private static void setToSignInPane() {
                  SignInModel model = new SignInModel();
                  SignInPane signInPane = new SignInPane(model);
                  model.getFormInstance();
                  gridPaneFirstScene.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 3);
                  gridPaneFirstScene.addRow(3, signInPane.getMainGridPane());
                  signInPane.getMainGridPane().requestFocus();
            }

            private static void setToSignUpPane() {
                  SignUpModel model = new SignUpModel();
                  SignUpPane signUpPane = new SignUpPane(model);
                  model.getFormInstance();
                  gridPaneFirstScene.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 3);
                  gridPaneFirstScene.addRow(3, signUpPane.getSignUpPane());
                  signUpPane.getSignUpPane().requestFocus();
            }

            private static void setToResetOnePane() {
                  ResetOnePane reset = new ResetOnePane();
                  gridPaneFirstScene.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 3);
                  gridPaneFirstScene.addRow(3, reset.getMainGridPain());
                  reset.getMainGridPain().requestFocus();
            }

            private static void setToResetTwoPane() {
                  ResetTwoPane reset = new ResetTwoPane();
                  gridPaneFirstScene.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 3);
                  gridPaneFirstScene.addRow(3, reset.getMainGridPain());
                  reset.getMainGridPain().requestFocus();
            }
      }


      /* *** FOR TESTING *** */
      @FMAnnotations.DoNotDeployMethod
      public static Point2D getCreateButtonXY() {
            Bounds bounds = createButton.getLayoutBounds();
            return createButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 20);
      }


}


