package ecosystem;

import ch.qos.logback.classic.Level;
import flashmonkey.FlashMonkeyMain;
import forms.utility.Alphabet;
import javafx.beans.value.WritableValue;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.slf4j.LoggerFactory;
import uicontrols.UIColors;

import java.util.HashMap;

public class TeaserPane extends ToggleButton {

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(TeaserPane.class);
    //private static final Logger LOGGER = LoggerFactory.getLogger(DeckSearchModel.class);


    public TeaserPane() {
        LOGGER.setLevel(Level.DEBUG);
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


    private GridPane gridP;
    public HBox build(HashMap<String, String> map, int idx) {
        LOGGER.debug("getPane() called");

        HBox rdoBox = new HBox(6);
        gridP = new GridPane();
   //     gridP.setId("teaserPane");

        gridP = setColLayout(gridP);
        gridP.setHgap(3);
        gridP.setVgap(3);

        // column one
        // deck_name
        Label deckLbl = new Label(map.get("deck_name"));
        deckLbl.setWrapText(true);
        gridP.add(deckLbl,0,0 );
        // section descript
        gridP.add(new Label("section3"), 0, 1);
        // media
  //      if(hasMedia(map.get("num_imgs"), map.get("num_video"), map.get("num_audio"))) {
            LOGGER.debug("teaserPane has media");
            gridP.add(new Label("Media"), 0, 2);
  //      }

        // column two
        Image image = new Image(getClass().getResourceAsStream("/image/profDemoMktImg.png"));
        ImageView img = new ImageView(image); //new ImageView(DeckMarketPane.getInstance().getDeckImg(idx));

        img.setFitWidth(128);
        img.setPreserveRatio(true);
        img.setSmooth(true);
        // columnNum, rowNum, num cols, num rows
        gridP.add(img, 1, 0, 1, 3);

        // stars
        // columnNum, rowNum, num cols, num rows
        //gridP.add(DeckMarketPane.getInstance().getStars(map.get("deck_numstars"), 80), 0, 4, 2, 1);
        gridP.add(getStarPane(map.get("deck_numstars"), map.get("num_users"), map.get("creator_email")), 0, 4, 2, 1);
        //rdoBox.setId("#teaser" + idx);
        rdoBox.getChildren().add(gridP);

        gridP.setOnMouseEntered(e -> {
            gridP.setBackground(new Background(new BackgroundFill(UIColors.convertColor(UIColors.GRID_GREY), CornerRadii.EMPTY, Insets.EMPTY)));
            FlashMonkeyMain.getWindow().getScene().setCursor(Cursor.HAND);
        });

        gridP.setOnMouseExited(e -> {
            gridP.setBackground(Background.EMPTY);
            FlashMonkeyMain.getWindow().getScene().setCursor(Cursor.DEFAULT);
        });

        return rdoBox;
    }

    public GridPane setColLayout(GridPane gp) {
        LOGGER.debug("setColLayout() called");

        ColumnConstraints col1 = new ColumnConstraints(160); // column 0 = 160 wide
        ColumnConstraints col2 = new ColumnConstraints(140); // column 1 = 140 wide
        gp.getColumnConstraints().addAll(col1, col2);
        return gp;
    }

    private Label getAuthors(String ... authors) {
        StringBuilder sb = new StringBuilder();
        sb.append("Created by: ");
        for(int i = 0; i < authors.length - 1; i++) {
            sb.append(Alphabet.decrypt(authors[i]) + ", ");
        }
        sb.append(Alphabet.decrypt(authors[authors.length - 1]));
        return new Label(sb.toString());
    }

    private HBox getStarPane(String numStars, String numUsers, String creator) {
        HBox box = new HBox(4);
        box.getChildren().addAll(DeckMarketPane.getInstance().getStars(numStars, 64), new Label("  " + numUsers + "  "),  getAuthors(creator));
        return box;
    }



    // **** SETTERS ****



    public boolean hasMedia(String imgs, String video, String audio) {
        return true;// (video != "" | imgs != "" | audio != "");
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
