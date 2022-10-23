package forms;

import com.dlsc.formsfx.view.renderer.FormRenderer;
import ecosystem.QrCode;
import fileops.DirectoryMgr;
import fileops.FileNaming;
import fileops.VertxLink;
import fileops.utility.Utility;
import flashmonkey.CreateFlash;
import flashmonkey.FlashCardOps;
import fmannotations.FMAnnotations;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import metadata.DeckMetaData;
import org.controlsfx.control.ToggleSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.SceneCntl;

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
      private static Hyperlink link;

      private Label sellLabel;
      private Label shareLabel;
      private Label creatorLabel;
      private ImageView qrView;

      protected static DeckMetaModel model;
      protected DeckMetaData meta;



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
            CreateFlash.getInstance().updateDeckInfo(meta); // seems to be clearing out the form after downloading from file or from DB???
            link = new Hyperlink("not yet available.");
            link.setDisable(true);

            fetchIDAsync();

            creatorLabel = new Label("CREATOR: " + meta.getCreatorEmail());
            lastScoreLabel = new Label("LAST SCORE: " + meta.calcLastScore());//model.getDataModel().getLastScore());
            sellSwitch = new ToggleSwitch();
            shareDistSwitch = new ToggleSwitch();
            creatorLabel.setId("label-bold-grey-emph");
            sellLabel = new Label("Sell this deck");
            sellLabel.setId("label-bold-grey-emph");
            shareLabel = new Label("Allow others to earn and share");
            shareLabel.setId("label-bold-grey-emph");

            cLbl = new Label("CARDS:");// + meta.getNumCard());//model.getDataModel().getNumCards());
            cNum = new Label(meta.getNumCard());
            iLbl = new Label("IMAGES:");// + meta.getNumImg());//model.getDataModel().getNumImgs());
            iNum = new Label(meta.getNumImg());
            vLbl = new Label("VIDEOS:");// + meta.getNumVideo());//model.getDataModel().getNumVideo());
            vNum = new Label(meta.getNumVideo());
            aLbl = new Label("AUDIO:");// + meta.getNumAudio());//model.getDataModel().getNumAudio());
            aNum = new Label(meta.getNumAudio());

            cLbl.setId("label-blue-small");
            iLbl.setId("label-blue-small");
            vLbl.setId("label-blue-small");
            aLbl.setId("label-blue-small");
            lastScoreLabel.setId("label-blue-small");

            super.setSubmitButtonTitle("SAVE");
            qrButton = new Button("SAVE TO DESKTOP");
            qrButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: #ffffff; -fx-font-size: 14;");
            qrButton.setMaxWidth(200);

            File qrImgFile = showQrCode();
            // User save location selector
            qrButton.setOnAction(e -> {
                  saveQRImageStage(qrImgFile, "QR-Code-" + FlashCardOps.getInstance().getDeckLabelName() + ".png");
            });

            // is necessary to offset the control to the left, because we don't use the provided label
            sellSwitch.setId("sellSwitch");
            sellSwitch.getStyleClass().add("sellSwitch");
            shareDistSwitch.setId("sellSwitch");
            shareDistSwitch.getStyleClass().add("sellSwitch");
            // Builds the pane containing the form fields.
            super.formRenderer = new FormRenderer(model.getFormInstance());
      }

      public static void fetchIDAsync() {
            ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
            Runnable task = () -> {
                  long deckID = model.getDeckID();
                  if(deckID != -99) {
                        vertxGet = VertxLink.QRCODE_DECK.getLink() + deckID;
                        link = new Hyperlink(vertxGet);
                        link.setDisable(false);
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
       * Creates the top section of the metadata form.
       */
      @Override
      public void layoutParts() {
            super.layoutParts();
            LOGGER.info("*** create MetaData form called ***");
            super.submitButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: #ffffff; -fx-font-size: 14; -fx-font-weight: BOLD");
            formRenderer.setMaxWidth(SceneCntl.getFormBox().getWd() - 10);
            formRenderer.setPrefSize(SceneCntl.getFormBox().getWd(), SceneCntl.getFormBox().getHt());

            GridPane grid = new GridPane();
            grid = setDescriptColumns(grid);

            grid.add(cLbl, 0, 0, 1, 1);
            grid.add(cNum, 1, 0, 1, 1);
            grid.add(iLbl, 0, 1, 1, 1);
            grid.add(iNum, 1, 1, 1, 1);
            grid.add(vLbl, 0, 2, 1, 1);
            grid.add(vNum, 1, 2, 1, 1);
            grid.add(aLbl, 0, 3, 1, 1);
            grid.add(aNum, 1, 3, 1, 1);

            VBox switchBox2 = new VBox(5);
            switchBox2.getChildren().addAll(shareLabel, shareDistSwitch);
            // private VBox statsBox;
            VBox switchBox1 = new VBox(5);
            switchBox1.getChildren().addAll(sellLabel, sellSwitch);
            VBox switchBox = new VBox(20);
            switchBox.getChildren().addAll(switchBox1, switchBox2);

            // column 0, row 0 , column span 1, row span 1
            innerGPane.add(creatorLabel, 0, 0, 1, 1);
            innerGPane.add(grid, 0, 1, 1, 1);
            innerGPane.add(lastScoreLabel, 0, 2, 1, 1);
            innerGPane.add(switchBox, 0, 4, 1, 1);

            // Temp QR code column 1
            innerGPane.add(qrView, 1, 1, 1, 4);
            innerGPane.add(qrButton, 1, 5, 1, 1);
            // Link is in row 6

      }

      @Override
      public void paneAction() {
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
                        innerGPane.add(qrView, 1, 0, 1, 3);
                  }
                  qrView.setImage(img);
                  // String fmVertx = "https://www.flashmonkey.xyz/Q52/FFG415/:" ;
                  //QrCode qrcode = new QrCode();
                  String vertxGet = VertxLink.QRCODE_DECK.getLink() + model.getDeckID();
                  LOGGER.debug("vertxGet: " + vertxGet);
                  link = new Hyperlink(vertxGet);
                  link.setOnMouseClicked(this::linkAction);
                  innerGPane.add(link, 0, 6, 2, 1);
                  //qrButton.setDisable(false);
            } else {
                  // set to default
                  qrView = new ImageView(new Image(getClass().getResourceAsStream("/image/QR_Code_IndexPg.png")));
                  innerGPane.add(qrView, 1, 0, 1, 3);
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

      private void linkAction(MouseEvent e) {
            if (e.getButton().equals(MouseButton.SECONDARY)) {
                  final Clipboard cb = Clipboard.getSystemClipboard();
                  final ClipboardContent content = new ClipboardContent();
                  content.putString(link.getText());
                  cb.setContent(content);
            } else {
                  Desktop desktop = Desktop.getDesktop();
                  if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        try {
                              desktop.browse(new URI(link.getText()));
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
