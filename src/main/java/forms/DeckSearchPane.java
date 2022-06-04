package forms;


import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import metadata.DeckSearchData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.ButtoniKon;


public class DeckSearchPane extends FormParentPane {
	
	//private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DeckSearchPane.class);
	private static final Logger LOGGER = LoggerFactory.getLogger(DeckSearchPane.class);
	
	private DeckSearchModel model;
	private DeckSearchData searchData;

	// Button box contained in southpane
	GridPane exitBox;
	private static Button exitButton;
	protected static Button menuButton;// = MENU.get();
	
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
		model = new DeckSearchModel();
		searchData = new DeckSearchData();
		this.initialize(model, searchData);
	}
	
	@Override
	public void initializeParts() {
		super.formRenderer = new FormRenderer(model.getFormInstance());
		super.setSubmitButtonTitle("find");
		super.setFormPainHeight(674);
		super.addExitBox(getExitBox());
	}
	
	@Override
	public void setupValueChangedListeners() {
		
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
	}
	
	
	@Override
	public GridPane getMainGridPain() {
		this.mainGridPain.setMaxHeight(600);
		this.mainGridPain.setMaxWidth(400);
		this.mainGridPain.setStyle("-fx-background-radius: 15 15 15 15; -fx-border-radius: 15 15 15 15; ");
		return this.mainGridPain;
	}

	private GridPane getExitBox() {
		exitButton = ButtoniKon.getExitButton();
		menuButton = ButtoniKon.getMenuButton();
		exitBox = new GridPane(); // HBox with spacing provided
		exitBox.setHgap(2);
		/* For the lower panel on modeSelectPane window */
		ColumnConstraints col0 = new ColumnConstraints();
		col0.setPercentWidth(50);
		exitBox.getColumnConstraints().add(col0);
		exitBox.setVgap(2);
		exitBox.setPadding(new Insets(15, 15, 15, 15));
		exitBox.addColumn(1, menuButton);
		exitBox.addColumn(2, exitButton);
		//exitBox.setId("buttonBox");
		return exitBox;
	}

	@Override
	public void paneAction() { /* do nothing */}

}
