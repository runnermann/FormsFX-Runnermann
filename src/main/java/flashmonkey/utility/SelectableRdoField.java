package flashmonkey.utility;

//import flashmonkey.FlashMonkeyMain;
//import flashmonkey.ReadFlash;
import flashmonkey.FlashMonkeyMain;
import flashmonkey.LinkObj;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import uicontrols.UIColors;

import javafx.geometry.Pos;

import javafx.scene.control.skin.RadioButtonSkin;

import javafx.beans.value.WritableValue;
import javafx.css.StyleableProperty;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;

import javax.xml.bind.annotation.XmlType;
import java.awt.event.ActionEvent;

public class SelectableRdoField extends ToggleButton {
	
	public SelectableRdoField() {
		initialize();
	}
	
	public void initialize() {
		//getStyleClass().setAll(DEFAULT_STYLE_CLASS);
		setAccessibleRole(AccessibleRole.LIST_ITEM);
		// alignment is styleable through css. Calling setAlignment
		// makes it look to css like the user set the value and css will not
		// override. Initializing alignment by calling set on the
		// CssMetaData ensures that css will be able to override the value.
		((StyleableProperty<Pos>)(WritableValue<Pos>)alignmentProperty()).applyStyle(null, Pos.CENTER_LEFT);
	}
	
	
	
	public HBox buildField(LinkObj lObject) {
		
		Image syncImg;
		Text testDate;// = new Text("16/8/24  2020-7-16 ");
		HBox deckRdoBox = new HBox(6);
		
		if(lObject.getCloudLink() != null) {
			testDate = new Text("Score not visible");
			syncImg = new Image(getClass().getResourceAsStream("/icon/24/cloud_white.png"));
		} else {
			//@TODO set user last test data to proper data in paneForFiles
			testDate = new Text("16/8/24  2020-7-16 ");
			syncImg = new Image(getClass().getResourceAsStream("/icon/24/home_white.png"));
		}
		// Last test date and score
		ImageView syncView = new ImageView(syncImg);
		testDate.setFill(Color.WHITE);
		HBox dateBox = new HBox(testDate);
		dateBox.setMaxWidth(150);
		dateBox.setMinWidth(150);
		dateBox.setMaxHeight(24);
		dateBox.setAlignment(Pos.CENTER_RIGHT);
		
		// The arrow image
		Image arrowImg = new Image(getClass().getResourceAsStream("/icon/24/arrow_ltpurple.png"));
		ImageView arrowView = new ImageView(arrowImg);
		// The deck name
		String lObjName = lObject.getDescrpt();
		
		if (!lObjName.contains("copy") && !lObjName.contains("default") ) {
			// remove file ending
			int num = lObjName.indexOf(".");
			String tempName = lObjName.substring(0, num);
			// if length is > 25, truncate and add ellipsis
			String deckStr = tempName.length() > 25 ? tempName.substring(0, 22) + "..." : tempName;
			
			Text text = new Text(deckStr);
			text.setFill(Color.WHITE);
			HBox textBox = new HBox(text);
			textBox.setMaxWidth(150);
			textBox.setMinWidth(150);
			textBox.setMaxHeight(24);
			textBox.setAlignment(Pos.CENTER_LEFT);
			
			// symbol icon
			
			deckRdoBox.getChildren().addAll(arrowView, textBox, syncView, dateBox);
			deckRdoBox.setId("#" + tempName);
			
			deckRdoBox.setOnMouseEntered(e -> {
				deckRdoBox.setBackground(new Background(new BackgroundFill(UIColors.convertColor(UIColors.BUTTON_PURPLE_50), CornerRadii.EMPTY, Insets.EMPTY)));
				FlashMonkeyMain.getWindow().getScene().setCursor(Cursor.HAND);
			});
			
			deckRdoBox.setOnMouseExited(e -> {
				deckRdoBox.setBackground(Background.EMPTY);
				FlashMonkeyMain.getWindow().getScene().setCursor(Cursor.DEFAULT);
			});
		}
		return deckRdoBox;
	}
	
	/**
	 * Toggles the state of the radio button if and only if the RadioButton
	 * has not already selected or is not part of a {@link ToggleGroup}.
	 */
	@Override public void fire() {
		// we don't toggle from selected to not selected if part of a group
		if (getToggleGroup() == null || !isSelected()) {
			super.fire();
		}
	}
	
	/***************************************************************************
	 *                                                                         *
	 * Accessibility handling                                                  *
	 *                                                                         *
	 **************************************************************************/
	
	/** {@inheritDoc} */
	@Override
	public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
		switch (attribute) {
			case SELECTED: return isSelected();
			default: return super.queryAccessibleAttribute(attribute, parameters);
		}
	}
}
