package forms;

import campaign.Report;
import ch.qos.logback.classic.Level;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import flashmonkey.CreateFlash;
import flashmonkey.FlashCardOps;
import flashmonkey.FlashMonkeyMain;
import flashmonkey.ReadFlash;
import forms.utility.MetaDescriptor;
import javafx.geometry.Pos;
import metadata.DeckMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.FxNotify;

/**
 * This class is used for the deck meta data form and holds all the necessary data. This
 * class acts as a singleton where the current instance is available using
 * {@code getInstance}.
 *
 * @author Lowell Stadelman
 */
public class DeckMetaModel extends ModelParent {
	
	private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DeckMetaModel.class);
	//private static final Logger LOGGER = LoggerFactory.getLogger(DeckMetaModel.class);
	private MetaDescriptor descriptor = new MetaDescriptor();
	
	
	
	/**
	 * Provides the form fields with validation and messaging to the user.
	 */
	@Override
	public void createForm() {
		LOGGER.setLevel(Level.DEBUG);
		LOGGER.info("DeckMetaModel createForm called");
		//String[] pwCheck = new String[1];
		// For search by Class, Search by keyword, search by book, search by Prof, search by catagory subcatagory, university bldg classroom
		
		formInstance = Form.of(
			Group.of(
					Field.ofStringType(descriptor.deckDescriptProperty())
							//.id("form-field")
							.label("label_description")
							.placeholder("deckDescript_placeholder")
							.multiline(true)
							.required("required_error_message")
							.validate(StringLengthValidator.between(10, 255,"descript_error_message")),
					Field.ofStringType(descriptor.deckSchoolProperty())
							//.id("form-field")
							.label("label_school")
							//.required("required_error_message")
							.placeholder("school_placeholder")
							.validate(StringLengthValidator.upTo( 120,"upTo_length_error")),
					Field.ofStringType(descriptor.deckBookProperty())
							//.id("form-field")
							.label("label_book")
							//.required("required_error_message")
							.placeholder("book_placeholder")
							.validate( StringLengthValidator.upTo( 120, "upTo_length_error")),
					Field.ofStringType(descriptor.deckProfProperty())
							//.id("form-field")
							.label("label_prof")
							.placeholder("prof_placeholder")
							.required("required_error_message")
							.validate( StringLengthValidator.between(5, 120, "between_length_error")),
					Field.ofStringType(descriptor.courseCodeProperty())
							.label("label_course_code")
							.placeholder("course_code_placeholder")
							.validate( StringLengthValidator.upTo(120, "upTo_length_error")),
					Field.ofStringType(descriptor.deckClassProperty())
							//.id("form-field")
							.label("label_class")
							//.required("required_error_message")
							.placeholder("class_placeholder")
							.validate( StringLengthValidator.upTo(120, "upTo_length_error")),
					Field.ofStringType(descriptor.deckSubjProperty())
							//.id("form-field")
							.label("label_sub")
							.required("required_error_message")
							.placeholder("subj_placeholder")
							.validate( StringLengthValidator.between(5, 120, "between_length_error")),
					Field.ofStringType(descriptor.deckSubjSubCatProperty())
							//.id("form-field")
							.label("label_cat")
							//.required("required_error_message")
							.placeholder("cat_placeholder")
							.validate( StringLengthValidator.between(5, 120, "between_length_error")),
					Field.ofStringType(descriptor.deckLanguageProperty())
							//.id("form-field")
							.label("label_lang")
							.required("required_error_message")
							.placeholder("language_placeholder")
							.validate( StringLengthValidator.between(5, 120, "between_length_error"))
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
		DeckMetaData metaData = (DeckMetaData) data;
		getFormInstance().persist();
		// set user information for file system access
		LOGGER.info("formAction() values data.Descript(): {} " +
						" data.getSchool: {} " +
						" data.getBook: {} " +
						" data.getDeckProf: {} " +
						" data.getDeckLang: {}" +
						" data.getSubjCat: {}" +
						" data.getSubjSubCat: {}" +
						" data.getNumCard: {}" +
						" data.getCourseCode: {}",
		descriptor.getDeckDescript(), descriptor.getDeckSchool(), descriptor.getDeckBook(), descriptor.getDeckProf(), descriptor.getDeckLanguage(),
		descriptor.getSubj(), descriptor.getSubjSubCat(), descriptor.getNumCards(), descriptor.getCourseCode());
		//CreateFlash.getInstance().inventoryMeta()
		//DeckMetaData metaData = new DeckMetaData();//.setName(data.getName());
		
		metaData.setDescript(descriptor.getDeckDescript());
		metaData.setDeckSchool(descriptor.getDeckSchool());
		metaData.setDeckBook(descriptor.getDeckBook());
		metaData.setDeckProf(descriptor.getDeckProf());
		metaData.setDeckClass(descriptor.getDeckClass()); // classroom
		metaData.setSubj(descriptor.getSubj());
		metaData.setCat(descriptor.getSubjSubCat());
		metaData.setLang(descriptor.getDeckLanguage());
		metaData.setCourseCode(descriptor.getCourseCode());
		
		//System.out.println("\nPrinting meta: DeckMetaData[]:Print metadata in form action: ");
		//System.out.println(metaData.toString());
		//System.out.println();

		// attempt to save to existing deck
		// if successful
		if(doAction(metaData)) {
			
			LOGGER.info("formAction() User created, closing form");
			// close the metaDataForm window
			CreateFlash.getInstance().closeMetaWindow();

		} else {
			// failed, send user a message
			LOGGER.warn("formAction() set deckMetaData failed ???");
		}
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

		// Update File with this metadata
		// metaData.saveDeckMetaData();
		// Update database
		boolean bool;
		try {
			// update the metaDataAry
			FlashCardOps.getInstance().getFO().setMetaInFile(metaData, ReadFlash.getInstance().getDeckName());
	
			// send to database
			bool = Report.getInstance().reportDeckMetadata(metaData);
		} catch(Exception e) {
			LOGGER.warn("WARNING: {} StackTrace: {} ", e.getMessage(), e.getStackTrace());
			e.printStackTrace();
			bool = false;
		}
		
		if(bool) {
			String msg = "You're updates have been saved to the cloud and should be viewable soon.";
			FxNotify.notificationPurple("Awesomeness!",msg, Pos.CENTER, 20,
					"image/flashFaces_stars_75.png", FlashMonkeyMain.getWindow());
			return true;
		} else {
			// failed, send user a notification
			String msg = "Awe dang! That didn't get through. Try checking your internet connection.";
			FxNotify.notificationPurple("", " Ooops! " + msg, Pos.CENTER, 20,
					"image/flashFaces_sunglasses_60.png", FlashMonkeyMain.getWindow());
			return false;
		}
	}
	
	@Override
	public MetaDescriptor getDescriptor() {
		return descriptor;
	}
	
	
}
