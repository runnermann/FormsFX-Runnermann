package forms;

import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.dlsc.formsfx.view.util.ViewMixin;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import uicontrols.SceneCntl;

/**
 * The parent class for form panes
 */
public abstract class FormParentPane extends Pane implements ViewMixin {
	
	protected GridPane formPane;
	protected Pane spacer;
	protected Pane spacer1;
	private   HBox resetHBox;
	protected HBox buttonHBox;
	private static VBox msgVBox;
	protected GridPane innerGPane;
	protected FormModel model;
	protected FormData data;
	
	// Action related
	protected Button submitButton;
	// Button
	private Button resetButton;
	private Button clearButton;
	// FxForm related
	protected FormRenderer formRenderer;
	
	
	/**
	 * <pre>
	 *     Order of initialization for ViewMixin.init()
	 *         initializeSelf();
	 *         initializeParts();
	 *         layoutParts();
	 *         setupEventHandlers();
	 *         setupValueChangedListeners();
	 *         setupBindings();
	 * </pre>
	 * <p>the use of init() should be called from the child class.?.</p>
	 */
	protected FormParentPane() {
		init();
	}
	
	/**
	 * Returns the FormPane from the child class.
	 * @return
	 */
	public abstract GridPane getFormPane();
	
	//@Override
	public void initialize(FormModel model, FormData data) {
		
		spacer = new Pane();
		spacer1 = new Pane();
		buttonHBox = new HBox();
		resetHBox = new HBox(2);
		msgVBox = new VBox();
		// Action related
		submitButton = new Button("SUBMIT");
		resetButton = new Button("RESET");
		clearButton = new Button("CLEAR FORM");
		
		formPane = new GridPane();
		innerGPane = new GridPane();
		this.model = model;
		this.data = data;
	}
	
	/**
	 * This method sets up the necessary bindings for the logic of the
	 * application.
	 */
	@Override
	public void setupBindings() {
		submitButton.disableProperty().bind(model.getFormInstance().persistableProperty().not());
		resetButton.disableProperty().bind(model.getFormInstance().changedProperty().not());
		formRenderer.prefWidthProperty().bind(formPane.prefWidthProperty());
	}
	
	/**
	 * Button Actions
	 */
	@Override
	public void setupEventHandlers() {
		model.getFormInstance().persistableProperty().getValue();
		clearButton.setOnAction(event -> {
			model.getDescriptor().clear();
		});
		resetButton.setOnAction(event -> {
			model.getFormInstance().reset();
		});
		submitButton.setOnAction(e -> {
			//System.out.println("submitButton clicked");
			//model.getFormInstance().persist();
			model.formAction(data);
		});
	}
	
	@Override
	public void layoutParts() {
	
		
	//	LOGGER.info("*** create MetaData form called ***");
		formRenderer.setMaxWidth((SceneCntl.getWd() - 10));
		
		// Card info
		innerGPane = new GridPane();
		innerGPane.setHgap(6);
		innerGPane.setVgap(10);
		innerGPane.setAlignment(Pos.CENTER);
		innerGPane.setPadding(new Insets(4, 2 , 4, 2));
	//	innerGPane.addRow(0, cardNumLabel, imgNumLabel, vidNumLabel, audNumLabel);
	//	innerGPane.addRow(1, lastScoreLabel);
		
		formPane.setAlignment(Pos.CENTER);
		formPane.setHgap(10);
		formPane.setVgap(12);
		formPane.setPrefSize(400,600);
		//formPane.setMaxWidth(400);
		//formPane.setMaxHeight(600);
	/*	formPane.setOnKeyPressed(f -> {
			if(f.getCode() == KeyCode.ENTER) {
				// Save/submit action
				model.formAction(data);
			}
		});
	*/
		innerGPane.setId("infoPane");
		formRenderer.setId("formPane");
		formPane.setId("infoPane");
		
		submitButton.setMaxWidth(240);
		submitButton.setMinWidth(240);
		submitButton.setId("signInButton");
		
		resetButton.setMaxWidth(120);
		resetButton.setMinWidth(120);
		resetButton.setId("resetButton");
		
		clearButton.setMaxWidth(120);
		clearButton.setMinWidth(120);
		clearButton.setId("resetButton");
		
		resetHBox.setAlignment(Pos.CENTER_RIGHT);
		resetHBox.getChildren().addAll(clearButton, resetButton);
		
		buttonHBox.setAlignment(Pos.CENTER);
		buttonHBox.getChildren().add(submitButton);
		spacer.setMinHeight(20);
		spacer1.setMinHeight(20);
		
		formPane.addRow(0, spacer);
		formPane.addRow(1, innerGPane);
		formPane.addRow(2, resetHBox);
		formPane.addRow(3, formRenderer);
		formPane.addRow(4, buttonHBox);
		formPane.addRow(5, spacer1);
	}
	
	public void setFormPaneWidth(int w) {
		formPane.setMaxWidth(w);
		formPane.setMinWidth(w);
	}
	
	public void setFormDoublePane(Pane pane) {
		BorderPane bp = new BorderPane();
		bp.setLeft(formPane);
		bp.setRight(pane);

	}

	/**
	 * Sets the infoPane message above the form. Label
	 * is the primary label, Message is the message.
	 * img is inserted into the image on the right.
	 * @param lbl
	 * @param msg
	 * @param imageFullPath the relitive path to the image and name.
	 */
	protected void setInfoPane(String lbl, String msg, String imageFullPath) {
		System.out.println("FormParentPane.setInfoPane(..., imageFullPath: " + imageFullPath);

		ImageView img = new ImageView(imageFullPath);
		img.setPreserveRatio(true);
		img.setFitHeight(100);
		Label l = new Label( lbl);
		l.setId("infoPane-label");
		Label m = new Label(msg);
		m.setId("infoPane-message");
		// column, row, col-span, row-span
		innerGPane.add(l, 0,0, 1, 1);
		innerGPane.add(m,0, 1, 1, 1);
		innerGPane.add(img, 1, 0, 1, 2);
	}
}
