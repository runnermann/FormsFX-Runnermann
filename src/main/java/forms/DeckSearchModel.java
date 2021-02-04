package forms;

import campaign.db.DBFetchToMapAry;
import ch.qos.logback.classic.Level;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Section;
import com.dlsc.formsfx.view.util.ColSpan;
import ecosystem.ConsumerPane;
import ecosystem.DeckMarketPane;
import flashmonkey.FlashMonkeyMain;
import forms.utility.SearchRemoteDescriptor;
import javafx.geometry.Pos;
import org.slf4j.LoggerFactory;
import uicontrols.FxNotify;
import uicontrols.SceneCntl;

import java.util.ArrayList;
import java.util.HashMap;

public class DeckSearchModel extends ModelParent {
	
	private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DeckSearchModel.class);
	//private static final Logger LOGGER = LoggerFactory.getLogger(DeckSearchModel.class);
	private SearchRemoteDescriptor descriptor = new SearchRemoteDescriptor();
	
	/**
	 * Provides the form fields with validation and messaging to the user.
	 */
	@Override
	public void createForm() {
		LOGGER.setLevel(Level.DEBUG);
		LOGGER.info("DeckSearchModel createForm called");
		//String[] pwCheck = new String[1];
		// For search by Class, Search by keyword, search by book, search by Prof, search by catagory subcatagory, university bldg classroom
		Section s = Section.of(
				Field.ofSingleSelectionType(descriptor.tutsProperty(), descriptor.selectedTutProperty())
						.label("institute_search_label"),
				Field.ofSingleSelectionType(descriptor.professorsProperty(), descriptor.selectedProfProperty())
						.label("professor_search_label"),
				Field.ofSingleSelectionType(descriptor.courseCodesProperty(), descriptor.SelectedCourseCodeProperty())
						.label("coursecode_search_label"));
		s.collapse(true);
		s.collapsedProperty().addListener((observable, oldValue, newValue) -> descriptor.setProfBarProperties(observable, s));
		s.title("search_prof_bar");
		
		Section y = Section.of(
				Field.ofSingleSelectionType(descriptor.subjectsProperty(), descriptor.selectedSubjectProperty())
						.label("subject_search_label"),
				Field.ofSingleSelectionType(descriptor.sectionsProperty(), descriptor.selectedSectionProperty())
						.label("section_search_label"));
		s.collapse(false);
		s.collapsedProperty().addListener((observable, oldValue, newValue) -> descriptor.setProfBarProperties(observable, s));
		y.title("search_subject_bar");
		
		formInstance = Form.of( s, y )
				.title("form_label")
				.i18n(rbs);

				/*Section.of(
				
				).title("search_email_bar"),
				Section.of(
				
				).title("search_deckkey_bar")*/
	}
	
	
	/**
	 * Save user information or fail . If the user has not been created before,
	 * after success sends the user to fileSelectPane, otherwise fail.
	 */
	@Override
	public void formAction(FormData data) {
		LOGGER.debug("formAction called");
		getFormInstance().persist();
		//if(formInstance.getGroups().get(0).hasChanged()) {


            DeckMarketPane dm = DeckMarketPane.getInstance();
			String institute = descriptor.getSelectedTut().getText();
			String prof = descriptor.getSelectedProf();
			String courseCode = descriptor.getSelectedCourseCode();
			// an arrayList of hashMaps. Contains a hashMap of columnNames for
			// each row from the DB resonse.
			ArrayList<HashMap<String, String>> response;
			
				System.out.println(" data selected by form is: institute<" + institute ); //+


			if (prof != null & courseCode == null) {
				String[] strAry = {institute.strip(), prof.strip()};
				System.out.println("DeckSearchModel query for PROF only: ");
				response = this.query(0, strAry);
				dm.setMapArray(response);

			} else if (prof == null & courseCode != null) {
				String[] strAry = {institute.strip(), courseCode.strip()};
				System.out.println("DeckSearchModel query for PROF and COURSECODE");
				response = this.query(1, strAry);
                dm.setMapArray(response);
			} else if (prof != null & courseCode != null) {
				String[] strAry = {institute.strip(), prof.strip(), courseCode.strip()};
				System.out.println("DeckSearchModel query for CourseCode only");
				response = this.query(2, strAry);
                dm.setMapArray(response);
			} else {
				System.out.println("DeckSearchModel query using neither PROF NOR COURSECODE");
				//System.out.println("DeckSearchModel query returning: " + query(3, institute));
				response = this.query(3, institute);
                dm.setMapArray(response);
			}

//			dm.setAcctStat(acctStatQuery());
		if(dm.isEmpty()) {
			String message = " I was unable to find anything for that search." +
					"\nTry a broader search or use different information.";
			FxNotify.notificationPurple("Oooph!", message, Pos.CENTER, 10,
					"image/flashFaces_sunglasses_60.png", FlashMonkeyMain.getWindow());
		} else {
			dm.build();
			ConsumerPane.getInstance().layoutConsumer();
			FlashMonkeyMain.getActionWindow().setMinWidth(SceneCntl.getConsumerPaneWd());
			FlashMonkeyMain.getActionWindow().setMinHeight(SceneCntl.getConsumerPaneHt());
			FlashMonkeyMain.getActionWindow().minWidthProperty().bind(ConsumerPane.getInstance().widthProperty());
			FlashMonkeyMain.getActionWindow().minHeightProperty().bind(ConsumerPane.getInstance().heightProperty());
			FlashMonkeyMain.getActionWindow().setOnHidden( e -> {
				ConsumerPane.getInstance().onClose();
				FlashMonkeyMain.closeActionWindow();
			});
		}
	}
	
	private String[] clean(String[] ary) {
		String[] out = new String[ary.length];
		for(int i = 0; i < ary.length; i++) {
			ary[i] = ary[i].replace("\\W", " ");
			out[i] = ary[i].strip();
		}
		return out;
	}
	
	@Override
	public boolean doAction(FormData data) {
		return false;
	}
	
	@Override
	public SearchRemoteDescriptor getDescriptor() {
		return descriptor;
	}
	

	private ArrayList<HashMap<String, String>> query(int queryType, String ... searchBy) {

		LOGGER.debug("DeckSearchModel.query() called");

		StringBuilder sb = new StringBuilder(" WHERE deck_school = '" + searchBy[0] + "'");
		switch(queryType) {
			// include professor only
			case 0: {
				sb.append(" AND deck_prof = '" + searchBy[1].strip() + "';");
				
				System.out.println("sending query: " + sb.toString());
				
				return DBFetchToMapAry.DECK_METADATA_MULTIPLE.query(sb.toString());
			}
			// include class only
			case 1: {
				sb.append(" AND deck_class = '" + searchBy[1].strip() + "';");
				
				System.out.println("sending query: " + sb.toString());

				return DBFetchToMapAry.DECK_METADATA_MULTIPLE.query(sb.toString());
			}
			// include prof and class
			case 2: {
				sb.append(" AND deck_prof = '" + searchBy[1].strip() + "' AND deck_class = '" + searchBy[2].strip() + "';");
				
				System.out.println("sending query: " + sb.toString());

				return DBFetchToMapAry.DECK_METADATA_MULTIPLE.query(sb.toString());
			}
			// by school/institute only
			case 3: {
				sb.append(";");
				
				System.out.println("sending query: " + sb.toString());
				System.out.println("query returning: ");
				return DBFetchToMapAry.DECK_METADATA_MULTIPLE.query(sb.toString());
			}
		}
		// This should not be reached, but is the default.
		return new ArrayList<HashMap<String, String>>();
	}
}
