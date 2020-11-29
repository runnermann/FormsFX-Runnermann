package ecosystem;

import ch.qos.logback.classic.Level;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class TeaserPane {

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(TeaserPane.class);
    //private static final Logger LOGGER = LoggerFactory.getLogger(DeckSearchModel.class);


    public TeaserPane() {
        LOGGER.setLevel(Level.DEBUG);
        System.out.println("TeaserPane no-arg constructor called");
    }



    public GridPane getPane(HashMap<String, String> map, int idx) {

        LOGGER.debug("getPane() called");

        GridPane gp = new GridPane();
        gp.setPrefWidth(292);
        gp.setPrefHeight(164);

        gp.setStyle("-fx-background-color: #393E46");

        gp = setColLayout(gp);
        gp.setHgap(4);
        gp.setVgap(4);

        // column one
            // deck_name
        Label titlelbl = new Label(map.get("deck_name"));
        titlelbl.setWrapText(true);
        gp.add(titlelbl,0,0 );
            // section descript
        gp.add(new Label(map.get("section")), 1, 0);
            // media
        if(hasMedia(map.get("num_imgs"), map.get("num_video"), map.get("num_audio"))) {
            gp.add(new Label("Media"), 2, 0);
        }
            // stars
        gp.add(DeckMarketPane.getInstance().getStars(map.get("deck_numstars"), 48), 4, 0);
        // column two
        ImageView img = new ImageView(DeckMarketPane.getInstance().getDeckImg(idx));
        img.setFitWidth(126);
        img.setPreserveRatio(true);
        img.setSmooth(true);
        gp.add(img, 0, 2, 3, 1);
        gp.add(getAuthors(map.get("creator_email")), 4, 2);
        return gp;
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
        for(int i = 0; i < authors.length - 1; i++) {
            sb.append(authors[i] + ", ");
        }
        sb.append(authors[authors.length - 1]);
        return new Label(sb.toString());
    }



    // **** SETTERS ****




    public boolean hasMedia(String imgs, String video, String audio) {
        return (video != "" | imgs != "" | audio != "");
    }


}
