package forms;

import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.sun.glass.ui.Screen;
import fileops.DirectoryMgr;
import fileops.FileNaming;
import fileops.VertxLink;
import fileops.utility.Utility;
import flashmonkey.CreateFlash;
import flashmonkey.FlashCardOps;
import fmannotations.FMAnnotations;
//import javafx.geometry.Point2D;
import metadata.DeckMetaData;
import type.tools.imagery.ImageUploader;
import uicontrols.FMHyperlink;
import uicontrols.SceneCntl;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.controlsfx.control.ToggleSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class DeckMetaPane extends FormParentPane {

      private static final Logger LOGGER = LoggerFactory.getLogger(DeckMetaPane.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DeckMetaPane.class);

      private HBox containerHBox;
      private GridPane leftGPane;
      private GridPane rightGPane;
      private Label cLbl;
      private Label cNum;
      private Label iLbl;
      private Label iNum;
      private Label vLbl;
      private Label vNum;
      private Label aLbl;
      private Label aNum;
      private Label lastScoreLabel;
      private ToggleSwitch sellSwitch;
      private ToggleSwitch shareDistSwitch;
      private Button qrButton;
      private static String vertxGet;// = VertxLink.QRCODE_DECK.getLink() + model.getDeckID();
      private static FMHyperlink deckLink;

      private Label sellLabel;
      private Label shareLabel;
      private Label creatorLabel;
      private ImageView qrView;
      //private ImageView deckView;
      private ImageUploader imgUp;


      private static DeckMetaModel model;
      private DeckMetaData meta;


      public DeckMetaPane() {
            super(); // FormParent
            LOGGER.info("DeckMetaModel called.");
      }

      @Override
      public ScrollPane getMainPane() {
            return this.scrollPane;
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
            CreateFlash.getInstance().updateDeckInfo(meta);

            leftGPane = getGrid();
            rightGPane = getGrid();
            imgUp = new ImageUploader();
            containerHBox = new HBox(4);
            deckLink = new FMHyperlink("Not yet available.", "");
            deckLink.setDisable(true);
            // Get the deck ID
            fetchIDAsync();

            creatorLabel = new Label("CREATOR:  " + meta.getCreatorAvatarName());
            lastScoreLabel = new Label("LAST SCORE: " + meta.calcLastScore());//model.getDataModel().getLastScore());
            sellSwitch = new ToggleSwitch();
            shareDistSwitch = new ToggleSwitch();
            creatorLabel.setId("label-bold-grey-emph");
            sellLabel = new Label("Sell this deck");
            sellLabel.setId("label-bold-grey-emph");
            shareLabel = new Label("Allow others to earn and share");
            shareLabel.setId("label-bold-grey-emph");
            leftGPane.setId("formPane");
            rightGPane.setId("formPane");

            mainVBox.setId("infoPane");

            cLbl = new Label("CARDS:");
            cNum = new Label(meta.getNumCard());
            iLbl = new Label("IMAGES:");
            iNum = new Label(meta.getNumImg());
            vLbl = new Label("VIDEOS:");
            vNum = new Label(meta.getNumVideo());
            aLbl = new Label("AUDIO:");
            aNum = new Label(meta.getNumAudio());

            cLbl.setId("label-blue-small");
            iLbl.setId("label-blue-small");
            vLbl.setId("label-blue-small");
            aLbl.setId("label-blue-small");
            lastScoreLabel.setId("label-blue-small");

            //super.setSubmitButtonTitle("SAVE");
            qrButton = new Button("SAVE TO DESKTOP");
            qrButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: #ffffff; -fx-font-size: 14;");
            qrButton.setMaxWidth(200);

            File qrImgFile = showQrCode();
            // User save location selector
            qrButton.setOnAction(e -> {
                  saveQRImageStage(qrImgFile, "QR-Code-" + FlashCardOps.getInstance().getDeckLabelName() + ".png");
            });

            // is necessary to offset the control to the left, we don't use the provided label
            sellSwitch.setId("sellSwitch");
            sellSwitch.getStyleClass().add("sellSwitch");
            shareDistSwitch.setId("sellSwitch");
            shareDistSwitch.getStyleClass().add("sellSwitch");
            // Builds the pane containing the form fields.
            super.formRenderer = new FormRenderer(model.getFormInstance());
      }

      /**
       * Makes an async call to the DB to fetch the deck id.
       */
      public static void fetchIDAsync() {
            ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
            Runnable task = () -> {
                  long deckID = model.getDeckID();
                  if(deckID != -99) {
                        vertxGet = VertxLink.QRCODE_DECK.getLink() + deckID;
                        deckLink = new FMHyperlink("Link to your deck. Right click to copy", vertxGet);
                        deckLink.setDisable(false);
                  }
                  scheduledExecutor.shutdown();
            };
            scheduledExecutor.execute(task);
      }


      @Override
      public void setupBindings() {
            // SellSwitch and ShareSwitch should not be able to turn on unless the user is a subscriber and are current.
            // The descriptor should not change/nor listen, unless the sellswitch is on. This is controlled in the model
            // in the sellSwitchAction(). The submit button should be off unless the sellSwitch is on. If the user is not
            // a subscriber and is not current, they should be directed to subscribe.
            sellSwitch.textProperty().bind(Bindings.when(sellSwitch.selectedProperty()).then("ON  ").otherwise("OFF "));
            shareDistSwitch.textProperty().bind(Bindings.when(shareDistSwitch.selectedProperty()).then("ON  ").otherwise("OFF "));
            sellSwitch.disableProperty().bind(model.getFormInstance().validProperty().not());
            // Share switch should not be selectable if the sell switch is disabled
            shareDistSwitch.disableProperty().bind(sellSwitch.selectedProperty().not());
            submitButton.disableProperty().bind(model.getFormInstance().persistableProperty().not());
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

      /**
       * Replaces ParentForm to allow us to set up a
       * a two column form.
       */
      private void setParentParts() {
            double scale = Screen.getMainScreen().getPlatformScaleY();
            if( scale != 1.0) {
                  double ht = Screen.getMainScreen().getVisibleHeight() * (int) (scale / 2);
                  // leave some room
                  ht -= 40;
                  if(ht > 800) {
                        ht = 800;
                  }
                  mainVBox.setPrefHeight(ht);
            } else {
                  mainVBox.setPrefHeight(800);
            }

            formRenderer.setId("formPane");

            submitButton.setMaxWidth(300);
            submitButton.setMinWidth(300);
            submitButton.setId("signInButton");

            buttonHBox.setAlignment(Pos.CENTER);
            buttonHBox.getChildren().add(submitButton);
            spacer.setMinHeight(20);
            spacer1.setMinHeight(20);

      }

      private GridPane getGrid() {
            GridPane gPane = new GridPane();
            gPane.setHgap(6);
            gPane.setVgap(10);
            gPane.setAlignment(Pos.CENTER);
            gPane.setPadding(new Insets(4, 2, 4, 2));
            return gPane;
      }


      /**
       * Creates the top section of the metadata form.
       */
      @Override
      public void layoutParts() {
            setParentParts();
//            super.layoutParts();
            LOGGER.info("*** create MetaData form called ***");
            super.submitButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: #ffffff; -fx-font-size: 14; -fx-font-weight: BOLD");
            formRenderer.setMaxWidth(SceneCntl.getFormBox().getWd() - 10);
            formRenderer.setPrefSize(SceneCntl.getFormBox().getWd(), SceneCntl.getFormBox().getHt());

            GridPane detailsGrid = new GridPane();
            detailsGrid = setDescriptColumns(detailsGrid);
            // cards
            detailsGrid.add(cLbl, 0, 0, 1, 1);
            detailsGrid.add(cNum, 1, 0, 1, 1);
            // image
            detailsGrid.add(iLbl, 0, 1, 1, 1);
            detailsGrid.add(iNum, 1, 1, 1, 1);
            // video
            detailsGrid.add(vLbl, 0, 2, 1, 1);
            detailsGrid.add(vNum, 1, 2, 1, 1);
            // audio number
            detailsGrid.add(aLbl, 0, 3, 1, 1);
            detailsGrid.add(aNum, 1, 3, 1, 1);
            // score
            detailsGrid.add(lastScoreLabel,0, 4, 2, 1);

            VBox switchBox2 = new VBox(5);
            switchBox2.getChildren().addAll(shareLabel, shareDistSwitch);
            // private VBox statsBox;
            VBox switchBox1 = new VBox(5);
            switchBox1.getChildren().addAll(sellLabel, sellSwitch);
            VBox switchBox = new VBox(20);
            switchBox.getChildren().addAll(switchBox1, switchBox2);
            VBox linkBox = new VBox(10);
            linkBox.getChildren().addAll(deckLink, qrButton);

            // column 0, row 0 , column span 1, row span 1
      //      leftGPane.setGridLinesVisible(true);
            leftGPane.add(creatorLabel, 0, 0, 1, 1);
            // add the deck details grid to the left grid
            leftGPane.add(detailsGrid, 0, 1, 1, 1);
      //      leftGPane.add(lastScoreLabel, 0, 2, 1, 1);
            leftGPane.add(switchBox, 0, 4, 1, 1);

            // Temp QR code column 1, set in method.
            leftGPane.add(qrView, 1, 0, 1, 3);

            leftGPane.add(linkBox, 1, 4, 1, 1);
            // Row 6 is empty

            VBox deckImgUploaderBox = imgUp.getVBox();
            deckImgUploaderBox.setAlignment(Pos.CENTER);

            ImageView instructionView = new ImageView("image/upload_img_2.png");
            StackPane deckImgStack = new StackPane(instructionView);
            deckImgStack.getChildren().addAll(deckImgUploaderBox);
            deckImgStack.setPrefSize(480, 270);

            leftGPane.add(deckImgStack, 0, 7, 2,1);
            // Add the form to the right pane
            rightGPane.addRow(0, formRenderer);

            leftGPane.setPrefWidth(SceneCntl.getFormBox().getWd());
            rightGPane.setPrefWidth(SceneCntl.getFormBox().getWd());

            containerHBox.getChildren().addAll(leftGPane, rightGPane);
            mainVBox.getChildren().addAll(spacer, containerHBox, buttonHBox, spacer1);
            scrollPane.setContent(mainVBox);

            int wd = SceneCntl.getFormBox().getWd() * 2 + 12;
            scrollPane.setPrefWidth(wd);
            super.setFormPaneWidth(wd);
      }


      @Override
      public void paneAction() {
            model.getDescriptor().setDeckImgNameProperty(imgUp.getImgName());
            imgUp.snapShot('p');
            if(Utility.isConnected()) {
                  showQrCode();
            }
      }

      /**
       * Call when the QR code has changed or to display in the pane.
       */
      private File showQrCode() {
            // qr code related
            String deckQRfile = FileNaming.getQRFileName(FlashCardOps.getInstance().getDeckFileName());
            File file = new File(DirectoryMgr.getMediaPath('q') + "/" + deckQRfile);
            if (file.exists()) {
                  Image img = new Image("file:" + file.getPath());
                  if(qrView == null) {
                        qrView = new ImageView();
                  }
                  qrView.setImage(img);
                  String vertxGet = VertxLink.QRCODE_DECK.getLink() + model.getDeckID();
                  LOGGER.debug("vertxGet: " + vertxGet);
                  deckLink = new FMHyperlink("Link to your deck. Right click to copy", vertxGet);
                  deckLink.setOnMouseClicked(this::linkAction);
            //      leftGPane.add(deckLink, 1, 4, 1, 1);
            } else {
                  // set to default
                  qrView = new ImageView(new Image(getClass().getResourceAsStream("/image/QR_Code_IndexPg.png")));
                  //leftGPane.add(qrView, 1, 0, 1, 3);
            }
            return file;
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

      /**
       * If there has been an image uploaded to the ImageUploader,
       * takes a SnapShot of the visible image, saves it to the local
       * file, and sends it too the cloud.
       */
      private void saveDeckImage() {

      }

      private void linkAction(MouseEvent e) {
            if (e.getButton().equals(MouseButton.SECONDARY)) {
                  final Clipboard cb = Clipboard.getSystemClipboard();
                  final ClipboardContent content = new ClipboardContent();
                  content.putString(deckLink.getLink());
                  cb.setContent(content);
            } else {
                  Desktop desktop = Desktop.getDesktop();
                  if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        try {
                              desktop.browse(new URI(deckLink.getLink()));
                        } catch (IOException exc) {
                              exc.printStackTrace();
                        } catch (URISyntaxException ex) {
                              ex.printStackTrace();
                        }
                  }
            }
      }

      private GridPane setDescriptColumns(GridPane gp) {
            ColumnConstraints col1 = new ColumnConstraints();
            ColumnConstraints col2 = new ColumnConstraints();
            col1.setPercentWidth(40);
            col2.setPercentWidth(60);
            gp.getColumnConstraints().addAll(col1, col2);
            return gp;
      }

      /* *** FOR TESTING *** */
      @FMAnnotations.DoNotDeployMethod
      public DeckMetaModel getModel() {
            return this.model;
      }

}
