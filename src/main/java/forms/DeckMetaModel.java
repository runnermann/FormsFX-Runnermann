package forms;

import authcrypt.UserData;
import authcrypt.user.EncryptedAcct;
import campaign.Report;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.structure.Section;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.dlsc.formsfx.model.validators.StringNumRangeValidator;
import com.dlsc.formsfx.view.util.ColSpan;
import com.google.zxing.WriterException;
import ecosystem.QrCode;
import fileops.CloudOps;
import fileops.DirectoryMgr;
import fileops.FileNaming;
import fileops.utility.Utility;
import flashmonkey.CreateFlash;
import flashmonkey.FlashCardOps;
import flashmonkey.FlashMonkeyMain;
import forms.utility.MetaDescriptor;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import metadata.DeckMetaData;
import org.controlsfx.control.ToggleSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.FMAlerts;
import uicontrols.FxNotify;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is used for the deck meta data form and holds all the necessary data. This
 * class acts as a singleton where the current instance is available using
 * {@code getInstance}.
 *
 * @author Lowell Stadelman
 */
public class DeckMetaModel extends ModelParent {

      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DeckMetaModel.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(DeckMetaModel.class);
      private final MetaDescriptor descriptor = new MetaDescriptor();
      private long deck_id;
      private static String deckImgName;
      private String qrImagePathName;

      private StringProperty vertxQRLinkProperty = new SimpleStringProperty();

      public void setQRImageFileName(String qrImageFileName) {
            this.qrImagePathName = qrImageFileName;
      }


      /**
       * Provides the form fields with validation and messaging to the user.
       */
      @Override
      public void createForm() {
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.info("DeckMetaModel createForm called");
            // For search by Class, Search by keyword, search by book, search by Prof, search by catagory subcatagory, university bldg classroom
            formInstance = Form.of(
                    Group.of(
                        Field.ofStringType(descriptor.priceProperty())
                            .label("label_price")
                            .validate(StringNumRangeValidator.between(0, Integer.MAX_VALUE, "num_format_error"))
                            .required("required_error_message")
                            .span(ColSpan.HALF),
                        Field.ofStringType(descriptor.numStarsProperty())
                            .label("label_num_stars")
                            .validate(StringNumRangeValidator.between(0, 5, "num_star_error"))
                            .span(ColSpan.HALF),
                        Field.ofStringType(descriptor.deckDescriptProperty())
                            .label("label_description")
                            .placeholder("deckDescript_placeholder")
                            .multiline(true)
                            .required("required_error_message")
                            .validate(StringLengthValidator.between(10, 1000, "descript_error_message")),
                            Field.ofSingleSelectionType(descriptor.tutsProperty(), descriptor.selectedTutProperty())
                                .label("label_school")
                                .tooltip("tuts_tooltip")
                                .required("required_error_message"),
                            //.validate(StringLengthValidator.between(5, 120, "between_length_error")),
                            Field.ofStringType(descriptor.deckProfProperty())
                                .label("label_prof")
                                .placeholder("prof_placeholder")
                                .required("required_error_message")
                            .validate(StringLengthValidator.between(2, 120, "between_length_error"))
                        ),
                        Section.of(
                          Field.ofStringType(descriptor.deckBookProperty())
                              .label("label_book")
                              .placeholder("book_placeholder")
                              .validate(StringLengthValidator.upTo(120, "upTo_length_error")),
                          Field.ofStringType(descriptor.courseCodeProperty())
                              .label("label_course_code")
                              .placeholder("course_code_placeholder")
                              .validate(StringLengthValidator.upTo(120, "upTo_length_error"))
                        ).title("course_detail_label"),
                        Section.of(
                        Field.ofStringType(descriptor.deckClassProperty())
                            .label("label_class")
                            .placeholder("class_placeholder")
                            .validate(StringLengthValidator.upTo(120, "upTo_length_error")),
                        Field.ofStringType(descriptor.deckSubjProperty())
                            .label("label_sub")
                            .required("required_error_message")
                            .placeholder("subj_placeholder")
                            .validate(StringLengthValidator.between(4, 120, "between_length_error")),
                        Field.ofStringType(descriptor.deckSubjSubCatProperty())
                            .label("label_cat")
                            .placeholder("cat_placeholder")
                            .validate(StringLengthValidator.between(4, 120, "between_length_error")),
                        Field.ofStringType(descriptor.deckLanguageProperty())
                            .label("label_lang")
                            .required("required_error_message")
                            .placeholder("language_placeholder")
                            .validate(StringLengthValidator.between(4, 120, "between_length_error"))
                    ).title("course_detail_label")
                ).title("form_label")
                .i18n(rbs);
      }

      /**
       * Asynchronous.
       * call to the formActionAsyncHelper.
       * Called by the form button action. A form's Parent is the FormParentPane and the FormModel. Note: with exception to some forms.
       * */
      @Override
      public void formAction(FormData data) {
            DeckMetaData metaData = buildMetaData(data);
            ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
            Runnable task = () -> {
                  formActionAsyncHelper(metaData);
                  scheduledExecutor.shutdown();
            };
            scheduledExecutor.execute(task);
      }

      /**
       * If the deckMetaData is successfully submitted to the DB,
       * Gets the deckID from the insert or update. Builds the QR Code
       * and then saves the QRFile to disk.
       * @param metaData
       */
      private void formActionAsyncHelper(DeckMetaData metaData) {
            // doAction submits the metaData to the database.
            if (doAction(metaData)) {
                  LOGGER.info("formAction() User created, closing form");
                  // CreateFlash.getInstance().closeMetaWindow();
                  // create QR code
                  String deckQRname = FileNaming.getQRFileName(FlashCardOps.getInstance().getDeckFileName());
                  String qrDir = DirectoryMgr.getMediaPath('q');
                  //String qrFileName = deckQRname.substring(0, deckQRname.length() - 4) + ".png";

                  String pathName = qrDir + deckQRname;
                  try {
                        // deck_id is set by doAction.
                        // Predicessor Chain, get chain hash from db.
                        String qrlink = QrCode.buildSaveDeckQrCode(deck_id, pathName, UserData.getUserName());
                        vertxQRLinkProperty.set(qrlink);
                        send('q', deckQRname);

                        // set the form action completed listener to true;
                  } catch (WriterException e) {
                        LOGGER.warn("ERROR: WriterException caused by {} ", e.getMessage());
                        e.printStackTrace();
                        //System.exit(1);
                  } catch (IOException e) {
                        LOGGER.warn("ERROR: IOException message: {}", e.getMessage());
                        e.printStackTrace();
                        //System.exit(1);
                  }
            } else {
                  // failed, send user a message
                  LOGGER.warn("formAction() set deckMetaData failed ???");
                  String msg = "That didn't get updated in the ecosystem. " +
                      "\nTo update so others may access " +
                      "\nand purchase your deck, please check your"  +
                      "\nconnection and try again.";
                  FxNotify.notificationError("Ooops", msg, Pos.CENTER, 15,
                      "image/flashFaces_sunglasses_60.png", FlashMonkeyMain.getPrimaryWindow());
            }
      }

      /**
       * Asynchronously sends media to cloud. verifies if the item is there,
       * and re-attempts if it did not succeed.
       */
      private void send(char bucket, String qrPath) {
            AtomicInteger count = new AtomicInteger(); // increased to 5, for bad networks.
            if (fileops.utility.Utility.isConnected()) {
                  String[] uploads = new String[1];
                  uploads[0] = qrPath;
                  sendMedia(uploads, bucket);
            }
      }

      /**
       * Sends the media to s3, then checks to ensure
       * it exists. If not, returns false.
       * <p>Will overwrite the media if this is allowed by the bucket policies in the cloud.</p>
       * @param uploads
       * @param bucket either 'p' for public: the deck descript img, or 'a' for the avatar.
       * @return
       */
      private void sendMedia(String[] uploads, char bucket) {
            CloudOps.putMedia(uploads, bucket);
      }

      @Override
      public MetaDescriptor getDescriptor() {
            return descriptor;
      }

      public StringProperty getVertxQRLinkProperty() {
            return this.vertxQRLinkProperty;
      }

      /**
       *
       * @return Returns the URL to fetch the deck from VERTX server.
       */
 //     String getVertxGetDeckURL() {
 //           return vertxGetDeckURL;
 //     }

      long getDeckID() {
            if(deck_id == -99 || deck_id == 0) {
                  deck_id = Report.getInstance().queryGetDeckID();
            }
            return this.deck_id;
      }

      /**
       * Called by submit button
       * Saves metaData created by this form to file.
       * sends metaData to the cloud.
       * Prints a message if successful or not.
       * returns true if successful.
       */
      @Override
      public boolean doAction(final FormData data) {
            DeckMetaData metaData = (DeckMetaData) data;
     //       String path = DirectoryMgr.getMediaPath('t') + FlashCardOps.getInstance().getMetaFileName();
            // Save the metadata to file.
     //       FlashCardOps.getInstance().setMetaInFile(metaData, path);
            // Save it to the cloud.
            if( Utility.isConnected()) {
                  try {
                        // send to database
                        deck_id = Report.getInstance().formUpsertDeckMetadata(metaData);
                        if (deck_id > 0) {
                              Platform.runLater(() -> {
                                    String msg = "You're learning materials have been saved to the ecosystem. Be sure to select sell so other's can see and purchase it.";
                                    FxNotify.notificationItsCool("Oh Yeah", msg, Pos.CENTER, 15,
                                            "image/flashFaces_sunglasses_60.png", CreateFlash.getInstance().getMetaWindow());
                              });
                              return true;
                        }
                  } catch (Exception e) {
                        LOGGER.warn("WARNING: {} StackTrace: {} ", e.getMessage(), e.getStackTrace());
                        e.printStackTrace();
                  }
            }

            return false;
      }

      /**
       * Verify if user is a member and membership is current. Else will not allow to sell.
       * @param sellSwitch
       * @param shareSwitch
       */
      public void sellSwitchAction(ToggleSwitch sellSwitch, ToggleSwitch shareSwitch) {
            if (!current()) {
                  boolean b = getAlert();
                  if (b) {
                        // if true send to create subscription.
                        FlashMonkeyMain.getSubscribeWindow();
                  } else {
                        sellSwitch.setSelected(false);
                        descriptor.sellDeckProperty().set(false);
                        DeckMetaData.getInstance().setSellDeck(false);
                  }
//                  if (current()) {
//                        sellSwitch.selectedProperty().bindBidirectional(descriptor.sellDeckProperty());
//                        shareSwitch.selectedProperty().bindBidirectional(descriptor.shareDeckProperty());
//                  } else {
//                        sellSwitch.setSelected(false);
//                        descriptor.sellDeckProperty().set(false);
//                        CreateFlash.getInstance().metaAlertPopup("Ooph!", "Please update your membership to allow earning.");
//                  }
            } else {
                  descriptor.sellDeckProperty().bind(sellSwitch.selectedProperty());
                  descriptor.shareDeckProperty().bind(shareSwitch.selectedProperty());
            }
      }

      private boolean getAlert() {
            String str01 = "START EARNING\nFROM YOUR\nSTUDY MATERIALS";
            String str2 = " - Make cash" +
                "\n - Only minor credit card fees and taxes are subtracted";
            String str3 = "Click OK to Begin";
            FMAlerts alerts = new FMAlerts();
            VBox box = alerts.alertPane(str01, str2, str3);
            boolean b = alerts.choicePanePopup(" START EARNING ", " ", box, "image/qww_1.png",
                null);
            return b;
      }

      private boolean current() {
            EncryptedAcct acct = new EncryptedAcct();
            return acct.isCurrent();
      }


      /**
       * Called when the formAction Sets the data
       * to be sent to the DB.
       * @param data
       * @return
       */
      private DeckMetaData buildMetaData(FormData data) {
            DeckMetaData metaData = (DeckMetaData) data;
            getFormInstance().persist();
            // set user information for file system access
            metaData.setDeckImgName(descriptor.getDeckImgName());
            metaData.setDescript(descriptor.getDeckDescript());
            metaData.setDeckSchool(descriptor.getSelectedTut().getName());
            metaData.setDeckBook(descriptor.getDeckBook());
            metaData.setDeckProf(descriptor.getDeckProf());
            metaData.setDeckClass(descriptor.getDeckClass()); // classroom
            metaData.setSubj(descriptor.getSubj());
            metaData.setCat(descriptor.getSubjSubCat());
            metaData.setLang(descriptor.getDeckLanguage());
            metaData.setCourseCode(descriptor.getCourseCode());
            metaData.setPrice(Integer.parseInt(descriptor.getPrice()));
            metaData.setNumStars(Integer.parseInt(descriptor.getNumStars()));
            // NOTE: shareDeck and sellDeck are switches in DeckMetaPane
            metaData.setShareDistro(descriptor.getShareDeck());
            metaData.setSellDeck(descriptor.getSellDeck());

            return metaData;
      }



      @Override
      public void formAction() {
            /* stub */
      }
}
