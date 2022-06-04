package forms;

import authcrypt.user.EncryptedStud;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import fmannotations.FMAnnotations;
import javafx.scene.layout.GridPane;
import org.slf4j.LoggerFactory;

/**
 * Displays users data. 1) Ensure this form is not displayed unless the user
 * has passed login. 2) Ensure users data is not over-writen unless the user
 * has passed login. 3) Ensure users data is not available unless the user
 * has passed login.
 */
public class StudentPane extends FormParentPane {
	
	private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(StudentPane.class);
	
	StudentModel model;
	EncryptedStud student;
	
	public StudentPane() {
		super();
		LOGGER.info("StudentPane called");
		//init();
	}
	
	/**
	 * This method is used to set up the stylesheets.
	 */
	@Override
	public void initializeSelf() {
		
		model = new StudentModel();
		student = new EncryptedStud();
		this.initialize(model, student);
	}
	
	/**
	 * This method initializes all nodes and regions.
	 */
	@Override
	public void initializeParts() {
		
		super.formRenderer = new FormRenderer(model.getFormInstance());
	}
	
	@Override
	public void setupValueChangedListeners() {
		/* empty */
	}
	
	@Override
	public void layoutParts() {
		super.layoutParts();
		LOGGER.info(" layoutParts called");
	}
	
	@Override
	public GridPane getMainGridPain() {
		return this.mainGridPain;
	}

	@Override
	public void paneAction() { /* do nothing */}
	
	/* *** FOR TESTING *** */
	@FMAnnotations.DoNotDeployMethod
	public StudentModel getModel() {
		return this.model;
	}
}
