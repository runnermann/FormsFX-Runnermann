package flashmonkey;


import fileops.*;
import forms.DeckNameModel;
import forms.DeckNamePane;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.util.*;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.FxNotify;
import uicontrols.UIColors;
import flashmonkey.utility.SelectableRdoField;

/**
 * DESCRIPTION: THis class creates the fileSelectPane that is output to the
 * users screen. The EncryptedUser may select the file they desire. That file will
 * be used until the EncryptedUser selects another file, or exits. If no file exists
 * or if the EncryptedUser chooses, a new file can be created.
 */
public class DeckSelectorPane {

      // Logging reporting level is set in src/main/resources/logback.xml
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(DeckSelectorPane.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(DeckSelectorPane.class);
      protected VBox paneForFiles = new VBox(4);
      // For the file does not exist window
      private TextField newNameField;   // new name field

      //*** VIEW PANE CONSTRUCTOR ***
      //* File select pane
      protected DeckSelectorPane() {
            //agrList = new AgrFileList(DECK_FOLDER);
      }

      /**
       * Creates the pane containing the decks available for the user's
       * selection. Builds the file select pane and inserts the available
       * decks based on the most recent deck.
       */
      protected void selectFilePane() {
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.info("FileSelectPane called");
            Label recLabel = new Label("RECENT DECKS");
            recLabel.setTextFill(Color.WHITE);
            recLabel.setId("deckNameLabel");
            Label oldLabel = new Label("\nPREVIOUS DECKS");
            oldLabel.setTextFill(Color.WHITE);
            oldLabel.setId("deckNameLabel");
            final AgrFileList agrList = FlashCardOps.getInstance().getAgrList();

            ArrayList<LinkObj> currentList = agrList.getRecentFiles();
            ArrayList<LinkObj> oldList = agrList.getOlderFiles();


            // Check if folder exists and has more than one file .
            // first file is default file.
            if (agrList.getSize() > 0) {
                  LOGGER.debug("currentList.size(): <{}>", currentList.size());
                  if (currentList.size() > 0) {
                        addChildren(currentList, recLabel);
                        addChildren(oldList, oldLabel);
                  }
            } else {
                  // create a new file since one does not exist
                  paneForFiles.getChildren().clear();
                  paneForFiles.getChildren().add(newFile());
            }
      } // END PANE FOR FILES

      protected void selectPrePane() {
            ArrayList<LinkObj> list = new ArrayList<>(3);
            LinkObj l1 = new LinkObj("$Downloading deck1.", new CloudLink("$Downloading Deck1.", System.currentTimeMillis(), 10), 10);
            LinkObj l2 = new LinkObj("$Downloading deck2.", new CloudLink("$Downloading Deck2.", System.currentTimeMillis(), 10), 10);
            LinkObj l3 = new LinkObj("$Downloading deck3.", new CloudLink("$Downloading Deck3.", System.currentTimeMillis(), 10), 10);
            list.add(l1);
            list.add(l2);
            list.add(l3);

            addPreChildren(list);
      }

      private void addPreChildren(ArrayList<LinkObj> list) {
            SelectableRdoField rdoField = new SelectableRdoField();
            Label loadLbl = new Label("LOADING");
            loadLbl.setTextFill(Color.WHITE);
            loadLbl.setId("deckNameLabel");
            paneForFiles.getChildren().add(loadLbl);

            for (LinkObj lObject : list) {
                  Pane gradientPane = new Pane();
                  gradientPane.setId("gradient");
                  gradientPane.setMinWidth(1500);
                  gradientPane.setMinHeight(20);

                  // animation
                  transitionFmLeft(gradientPane);
                  setTextLeft(transitionFmLeft(gradientPane));
                  // The deck element
                  HBox fieldBox = rdoField.buildField(lObject);
                  // Deck element action

                  GaussianBlur blur = new GaussianBlur();
                  blur.setRadius(5);
                  fieldBox.setEffect(blur);
                  //fieldBox.setPadding(new Insets(12));

                  StackPane stack = new StackPane();
                  stack.getChildren().addAll(fieldBox, gradientPane);

                  //paneForFiles.setPadding(new Insets(0, 10, 0, 10));
                  paneForFiles.getChildren().add(stack);
                  textLeft.play();
            }
      }

      protected TranslateTransition textLeft;

      public void setTextLeft(TranslateTransition trx) {
            textLeft = trx;
      }

      public static TranslateTransition transitionFmLeft(Node node) {
            // Transition the textframe
            TranslateTransition trans = new TranslateTransition(Duration.millis(1400), node);
            trans.setFromX(-500f);
            trans.setToX(500);
            trans.setCycleCount(TranslateTransition.INDEFINITE);
            trans.setAutoReverse(false);

            return trans;
      }

      private void addChildren(ArrayList<LinkObj> list, Label label) {

            SelectableRdoField rdoField = new SelectableRdoField();
            paneForFiles.getChildren().add(label);
            for (LinkObj lObject : list) {
                  // The deck element
                  HBox fieldBox = rdoField.buildField(lObject);
                  // Deck element action
                  fieldBox.setOnMouseClicked(e -> {
                        if (e.isSecondaryButtonDown()) {
                              fieldSecondaryAction();
                        } else {
                              FlashCardOps.getInstance().fieldPrimaryAction(lObject, lObject.getDescrpt());
                        }
                  });
                  paneForFiles.getChildren().add(fieldBox);
            }
      }


      private void fieldSecondaryAction() {
            // @todo fieldSecondaryAction in fileSelectPane, rightClick on deckLinks
      }

      //* creates a new fileName in the
      //* directory. If the name is successfully created it sends the user
      //* to the CreateFlashPane.createFlashScene() so that the user may
      //* immediatly begin creating new flashCards.
      //*
      //* @return Returns a VBox
      protected GridPane newFile() {
            FlashMonkeyMain.setIsInEditMode(true);
            DeckNamePane dmPane = new DeckNamePane();
            return dmPane.getMainGridPain();
      }

}
