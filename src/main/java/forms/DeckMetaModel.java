package forms;

import authcrypt.UserData;
import authcrypt.user.EncryptedAcct;
import campaign.Report;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.dlsc.formsfx.model.validators.StringNumRangeValidator;
import com.dlsc.formsfx.view.util.ColSpan;
import com.google.zxing.WriterException;
import ecosystem.QrCode;
import fileops.DirectoryMgr;
import fileops.FileNaming;
import flashmonkey.FlashCardOps;
import flashmonkey.FlashMonkeyMain;
import forms.utility.MetaDescriptor;
import javafx.geometry.Pos;
import metadata.DeckMetaData;
import org.controlsfx.control.ToggleSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.FMAlerts;
import uicontrols.FxNotify;
import uicontrols.UIColors;

import java.io.IOException;

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
                        Field.ofStringType(descriptor.deckBookProperty())
                            .label("label_book")
                            .placeholder("book_placeholder")
                            .validate(StringLengthValidator.upTo(120, "upTo_length_error")),
                        Field.ofStringType(descriptor.deckProfProperty())
                            .label("label_prof")
                            .placeholder("prof_placeholder")
                            .required("required_error_message")
                            .validate(StringLengthValidator.between(2, 120, "between_length_error")),
                        Field.ofStringType(descriptor.courseCodeProperty())
                            .label("label_course_code")
                            .placeholder("course_code_placeholder")
                            .validate(StringLengthValidator.upTo(120, "upTo_length_error")),
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
                    )
                ).title("form_label")
                .i18n(rbs);
      }

      /**
       * Save user information or fail . If the user has not been created before,
       * after success sends the user to fileSelectPane, otherwise fail.
       */
      @Override
      public void formAction(FormData data) {
            DeckMetaData metaData = buildMetaData(data);
// ***** String institute = descriptor.getSelectedTut().getText();
            // attempt to save to existing deck
            // if successful

            if (doAction(metaData)) {
                  LOGGER.info("formAction() User created, closing form");
                  // CreateFlash.getInstance().closeMetaWindow();
                  // create QR code
                  String deckQRname = FileNaming.getQRFileName(FlashCardOps.getInstance().getDeckFileName());
                  String dir = DirectoryMgr.getMediaPath('q');
                  try {
                        // deck_id is set by doAction.
                        // Predicessor Chain, get chain hash from db.
                        QrCode.buildDeckQrCode(deck_id, dir, deckQRname, UserData.getUserName());
                  } catch (WriterException e) {
                        LOGGER.warn("ERROR: WriterException caused by {} ", e.getMessage());
                        e.printStackTrace();
                        System.exit(1);
                  } catch (IOException e) {
                        LOGGER.warn("ERROR: IOException message: {}", e.getMessage());
                        e.printStackTrace();
                        System.exit(1);
                  }
            } else {
                  // failed, send user a message
                  LOGGER.warn("formAction() set deckMetaData failed ???");
            }
      }

      @Override
      public MetaDescriptor getDescriptor() {
            return descriptor;
      }

      /**
       * Saves metaData created by this form to file.
       * sends metaData to the cloud.
       * Prints a message if successful or not.
       * returns true if successful.
       */
      @Override
      public boolean doAction(final FormData data) {
            DeckMetaData metaData = (DeckMetaData) data;
            String path = DirectoryMgr.getMediaPath('z') + FlashCardOps.getInstance().getMetaFileName();

            // Update File with this metadata
            // metaData.saveDeckMetaData();
            // Update database
            boolean bool = false;
            try {
                  // update the metaDataAry
                  FlashCardOps.getInstance().setMetaInFile(metaData, path);

                  // send to database
                  deck_id = Report.getInstance().reportDeckMetadata(metaData);
                  if (deck_id > 0) {
                        bool = true;
                  }
            } catch (Exception e) {
                  LOGGER.warn("WARNING: {} StackTrace: {} ", e.getMessage(), e.getStackTrace());
                  e.printStackTrace();
                  bool = false;
            }

            if (bool) {
                  String msg = "You're updates have been saved to the cloud and should be viewable.";
                  FxNotify.notificationDark("Awesomeness!", msg, Pos.CENTER, 12,
                      "image/flashFaces_stars_75.png", FlashMonkeyMain.getWindow());
                  return true;
            } else {
                  // failed, send user a notification
                  String msg = "Awe dang! That didn't get through. Try checking your internet connection.";
                  FxNotify.notificationDark("Ooops", msg, Pos.CENTER, 12,
                      "image/flashFaces_sunglasses_60.png", FlashMonkeyMain.getWindow());
                  return false;
            }
      }

//	public void sellListener(ToggleSwitch sellSwitch) {
//		sellSwitch.selectedProperty().bindBidirectional(descriptor.sellDeckProperty());
//	}
//
//	public void shareListener(ToggleSwitch shareSwitch) {
//		shareSwitch.selectedProperty().bindBidirectional(descriptor.shareDeckProperty());
//	}

      public void sellSwitchAction(ToggleSwitch sellSwitch, ToggleSwitch shareSwitch) {
            if (!current()) {
                  FMAlerts alerts = new FMAlerts();
                  boolean b = alerts.choiceOnlyActionPopup(" Advanced FlashMonkey Alert ", FMAlerts.JOIN_OR_FAIL, "image/logo/vertical_logo_blue_480.png",
                      UIColors.ICON_ELEC_BLUE);
                  if (b) {
                        // if true send to create subscription.
                        FlashMonkeyMain.getSubscribeWindow();
                  } else {
                        sellSwitch.setSelected(false);
                  }
                  if (current()) {
                        sellSwitch.selectedProperty().bindBidirectional(descriptor.sellDeckProperty());
                        shareSwitch.selectedProperty().bindBidirectional(descriptor.shareDeckProperty());
                  } else {
                        sellSwitch.setSelected(false);
                  }
            }
      }

      private boolean current() {
            EncryptedAcct acct = new EncryptedAcct();
            return acct.isCurrent();
      }


      private DeckMetaData buildMetaData(FormData data) {
            DeckMetaData metaData = (DeckMetaData) data;
            getFormInstance().persist();
            // set user information for file system access
            LOGGER.info("formAction() values " +
                    "\ndata.Descript(): {} " +
                    "\ndata.getSchool: {} " +
                    "\ndata.getBook: {} " +
                    "\ndata.getDeckProf: {} " +
                    "\ndata.getDeckLang: {}" +
                    "\ndata.getSubjCat: {}" +
                    "\ndata.getSubjSubCat: {}" +
                    "\ndata.getNumCard: {}" +
                    "\ndata.getCourseCode: {}" +
                    "\ndata.isSellDeck: {}" +
                    "\ndata.isShareDeck: {}",
                descriptor.getDeckDescript(), descriptor.getSelectedTut(), descriptor.getDeckBook(), descriptor.getDeckProf(), descriptor.getDeckLanguage(),
                descriptor.getSubj(), descriptor.getSubjSubCat(), descriptor.getNumCards(), descriptor.getCourseCode(), descriptor.getSellDeck(),
                descriptor.getShareDeck());

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
