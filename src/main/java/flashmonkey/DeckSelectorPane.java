package flashmonkey;


import fileops.*;
import forms.DeckNameModel;
import forms.DeckNamePane;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.util.*;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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
            Label recLabel = new Label("Recent decks");
            recLabel.setTextFill(Color.WHITE);
            recLabel.setId("deckNameLabel");
            Label oldLabel = new Label("Previous decks");
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
