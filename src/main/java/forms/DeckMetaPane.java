package forms;

import com.dlsc.formsfx.view.renderer.FormRenderer;
import fileops.DirectoryMgr;
import fileops.FileNaming;
import flashmonkey.CreateFlash;
import flashmonkey.FlashCardOps;
import fmannotations.FMAnnotations;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import metadata.DeckMetaData;
import org.controlsfx.control.ToggleSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.SceneCntl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class DeckMetaPane extends FormParentPane {

      private static final Logger LOGGER = LoggerFactory.getLogger(DeckMetaPane.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DeckMetaPane.class);

      private Label cardNumLabel;
      private Label imgNumLabel;
      private Label vidNumLabel;
      private Label audNumLabel;
      private Label lastScoreLabel;
      private ToggleSwitch sellSwitch;
      private ToggleSwitch shareDistSwitch;
      private Button qrButton;

      private Label sellLabel;
      private Label shareLabel;
      private HBox statsBox;
      private VBox switchBox1;
      private VBox switchBox2;
      private ImageView qrView;

      protected DeckMetaModel model;
      protected DeckMetaData meta;

      public DeckMetaPane() {
            super(); // FormParent
            LOGGER.info("DeckMetaModel called.");
      }

      @Override
      public GridPane getMainGridPain() {
            return this.mainGridPain;
      }

      /**
       * This method is called by Forms api ViewMixin
       */
      @Override
      public void initializeSelf() {
            model = new DeckMetaModel();
            meta = DeckMetaData.getInstance();
            this.initialize(model, meta);
      }

      /**
       * This method initializes all child class nodes and regions.
       */
      @Override
      public void initializeParts() {
            CreateFlash.getInstance().updateDeckInfo(meta); // seems to be clearing out the form after downloading from file or from DB???
            cardNumLabel = new Label("Cards: " + meta.getNumCard());//model.getDataModel().getNumCards());
            imgNumLabel = new Label("Images: " + meta.getNumImg());//model.getDataModel().getNumImgs());
            vidNumLabel = new Label("Videos: " + meta.getNumVideo());//model.getDataModel().getNumVideo());
            audNumLabel = new Label("Audio: " + meta.getNumAudio());//model.getDataModel().getNumAudio());
            lastScoreLabel = new Label("Last Score: " + meta.calcLastScore());//model.getDataModel().getLastScore());
            sellSwitch = new ToggleSwitch();
            shareDistSwitch = new ToggleSwitch();
            sellLabel = new Label("Sell this deck");
            sellLabel.setId("switch-label");
            shareLabel = new Label("Allow others to distribute in their social media");
            shareLabel.setId("switch-label");

            cardNumLabel.setId("label-bold-white-emph");
            imgNumLabel.setId("label-bold-white-emph");
            vidNumLabel.setId("label-bold-white-emph");
            audNumLabel.setId("label-bold-white-emph");
            lastScoreLabel.setId("label-bold-white-emph");

            qrButton = new Button("SAVE TO DESKTOP");
            qrButton.setStyle("-fx-background-color: #F2522E; -fx-text-fill: #ffffff; -fx-font-size: 14;");
            qrButton.setMaxWidth(Double.MAX_VALUE);

            File qrImgFile = showQrCode();

            qrButton.setOnAction(e -> {
                  saveQRImageStage(qrImgFile, "QR-Code-" + FlashCardOps.getInstance().getDeckLabelName() + ".png");
            });


            // is necessary to offset the control to the left, because we don't use the provided label
            //sellSwitch.setTranslateX(-20);
            sellSwitch.setId("sellSwitch");
            sellSwitch.getStyleClass().add("sellSwitch");

            //shareDistSwitch.setTranslateX(-20);
            shareDistSwitch.setId("sellSwitch");
            shareDistSwitch.getStyleClass().add("sellSwitch");

            // Builds the pane containing the form fields.
            super.formRenderer = new FormRenderer(model.getFormInstance());
      }


      @Override
      public void setupBindings() {
            // SellSwitch and ShareSwitch should not be able to turn on unless the user is a subscriber and are current.
            // The descriptor should not change/nor listen, unless the sellswitch is on. This is controlled in the model
            // in the sellSwitchAction(). The submit button should be off unless the sellSwitch is on. If the user is not
            // a subscriber and is not current, they should be directed to subscribe.
            sellSwitch.textProperty().bind(Bindings.when(sellSwitch.selectedProperty()).then("ON  ").otherwise("OFF "));
            shareDistSwitch.textProperty().bind(Bindings.when(shareDistSwitch.selectedProperty()).then("ON  ").otherwise("OFF "));
            sellSwitch.disableProperty().bind(model.getFormInstance().persistableProperty().not());
            // Share switch should not be selectable if the sell switch is disabled
            shareDistSwitch.disableProperty().bind(sellSwitch.selectedProperty().not());
            submitButton.disableProperty().bind(model.getFormInstance().validProperty().not());
            // bind qrButton to sellSwitch
            qrButton.disableProperty().bind(sellSwitch.selectedProperty().not());
      }


      @Override
      public void setupValueChangedListeners() {
            sellSwitch.setOnMouseClicked(event -> {
                  model.getFormInstance().changedProperty().setValue(true);
                  model.sellSwitchAction(sellSwitch, shareDistSwitch);
                  //model.getFormInstance().persistableProperty().setValue(true);
            });
            shareDistSwitch.setOnMouseClicked(event -> {
                  model.getFormInstance().changedProperty().setValue(true);
                  //model.getFormInstance().persistableProperty().setValue(true);
            });
      }


      @Override
      public void layoutParts() {
            super.layoutParts();
            LOGGER.info("*** create MetaData form called ***");
            super.submitButton.setStyle("-fx-background-color: #F2522E; -fx-text-fill: #ffffff; -fx-font-size: 14; -fx-font-weight: BOLD");

            formRenderer.setMaxWidth(SceneCntl.getFormBox().getWd() - 10);
            formRenderer.setPrefSize(SceneCntl.getFormBox().getWd(), SceneCntl.getFormBox().getHt());

            statsBox = new HBox(5);
            statsBox.getChildren().addAll(cardNumLabel, imgNumLabel, vidNumLabel, audNumLabel);
            switchBox2 = new VBox(5);
            switchBox2.getChildren().addAll(shareLabel, shareDistSwitch);
            switchBox1 = new VBox(5);
            switchBox1.getChildren().addAll(sellLabel, sellSwitch);
            VBox box = new VBox(20);
            box.getChildren().addAll(switchBox1, switchBox2);

            // column 0, row 0 , column span 1, row span 1
            innerGPane.add(statsBox, 0, 0, 1, 1);
            innerGPane.add(lastScoreLabel, 0, 1, 1, 1);
            innerGPane.add(box, 0, 2, 1, 1);

            // Temp QR code column 1
            innerGPane.add(qrView, 1, 0, 1, 3);
            innerGPane.add(qrButton, 1, 3, 1, 1);

      }

      @Override
      public void paneAction() {
            showQrCode();
      }

      /**
       * Call when the QR code has changed or to display in the pane.
       */
      private File showQrCode() {
            // qr code related
            String deckQRfile = FileNaming.getQRFileName(FlashCardOps.getInstance().getDeckFileName());
            File file = new File(DirectoryMgr.getMediaPath('q') + "/" + deckQRfile);
            if (file.exists()) {
                  qrView = new ImageView(new Image("file:" + file.getPath()));
                  innerGPane.add(qrView, 1, 0, 1, 3);
                  //qrButton.setDisable(false);
            } else {
                  // set to default
                  qrView = new ImageView(new Image(getClass().getResourceAsStream("/image/QR_Code_IndexPg.png")));
                  //qrButton.setDisable(true);
            }
            return file;
      }

      private void setQRPane(String qrFileString) {
            qrView = new ImageView(new Image(qrFileString));
      }

      private ImageView getQRview() {
            return qrView;
      }

      private void saveQRImageStage(File imgFile, String suggestName) {
            Stage stage = new Stage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save QR-Code");
            fileChooser.getExtensionFilters().addAll(new javafx.stage.FileChooser.ExtensionFilter("png", "*.png"));
            fileChooser.setInitialFileName(suggestName);
            File dest = fileChooser.showSaveDialog(stage);
            try {
                  Files.copy(imgFile.toPath(), dest.toPath());
            } catch (IOException e) {
                  e.printStackTrace();
            }
      }

      /* *** FOR TESTING *** */
      @FMAnnotations.DoNotDeployMethod
      public DeckMetaModel getModel() {
            return this.model;
      }

}
