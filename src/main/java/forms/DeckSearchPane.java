package forms;


import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import metadata.DeckSearchData;
import org.slf4j.LoggerFactory;


public class DeckSearchPane extends FormParentPane {
	
	private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DeckSearchPane.class);
	
	private DeckSearchModel model;
	private DeckSearchData searchData;
	
	public DeckSearchPane() {
		super(); // FormParent
	    LOGGER.info("DeckMetaModel called.");
	}

	public void onClose() {
		model = null;
	}
	
	/**
	 * This method is the first call from API FormsFX.ViewMixin
	 */
	@Override
	public void initializeSelf() {
		// getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		// LOGGER.warn("called");
		model = new DeckSearchModel();
		searchData = new DeckSearchData();
		this.initialize(model, searchData);
		//model.getFormInstance().persistableProperty().getValue();
	}
	
	@Override
	public void initializeParts() {
		// LOGGER.warn("called");
		super.formRenderer = new FormRenderer(model.getFormInstance());
	}
	
	@Override
	public void setupValueChangedListeners() {
		
		model.getFormInstance().changedProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("The form has " + (newValue ? "" : "not ") + "changed.");
		/*	String methodname = Thread.currentThread().getStackTrace()[2].getMethodName();
			String classname = Thread.currentThread().getStackTrace()[2].getClassName();
			int line = Thread.currentThread().getStackTrace()[2].getLineNumber();
			System.out.println("Caller is " + classname + "." + methodname + "(...)." +  " at line: " + line);
			Thread.dumpStack();
		 */
			System.out.println(); // clear buffer
		});
		model.getFormInstance().validProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("The form is " + (newValue ? "" : "not ") + "valid.");
		/*	String methodname = Thread.currentThread().getStackTrace()[2].getMethodName();
			String classname = Thread.currentThread().getStackTrace()[2].getClassName();
			int line = Thread.currentThread().getStackTrace()[2].getLineNumber();
			System.out.println("Caller is " + classname + "." + methodname + "(...)." +  " at line: " + line);
			Thread.dumpStack();
		*/
			System.out.println(); // clear buffer
		});
		model.getFormInstance().persistableProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("The form is " + (newValue ? "" : "not ") + "persistable.");
		/*	String methodname = Thread.currentThread().getStackTrace()[2].getMethodName();
			String classname = Thread.currentThread().getStackTrace()[2].getClassName();
			int line = Thread.currentThread().getStackTrace()[2].getLineNumber();
			System.out.println("Caller is " + classname + "." + methodname + "(...)." +  " at line: " + line);
			Thread.dumpStack();
		*/
			System.out.println(); // clear buffer
		});
		
	}
	
	@Override
	public void layoutParts() {
		String msg = "Get smart faster. Find the study material that a " +
				"classmate or previous student created for the courses " +
				"you are taking.";
		String lbl = "Find a study deck";
		String imgPath = "/emojis/blue_tulip.png";
		super.layoutParts();
		super.setInfoPane(lbl, msg, imgPath);
		// LOGGER.warn("called");
		// LOGGER.info("*** create SearchData form called ***");
		//displayForm.setPrefSize(800, 600);
	}
	
	
	@Override
	public GridPane getFormPane() {
		// LOGGER.warn("called");
		this.formPane.setMaxHeight(600);
		this.formPane.setMaxWidth(400);
		this.formPane.setStyle("-fx-background-radius: 15 15 15 15; -fx-border-radius: 15 15 15 15; ");
		return this.formPane;
	}

}
