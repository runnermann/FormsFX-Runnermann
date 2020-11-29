package ecosystem;

import campaign.db.DBFetchMulti;
import ch.qos.logback.classic.Level;
import flashmonkey.FlashMonkeyMain;
import forms.DeckSearchPane;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.slf4j.LoggerFactory;
import uicontrols.FxNotify;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * Contains the elements providing more details
 * on the deck that is on display;
 */
public class CenterPane {

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(CenterPane.class);
    //private static final Logger LOGGER = LoggerFactory.getLogger(DeckSearchModel.class);

    private GridPane gp;
    private VBox mediaPane;
    private GridPane titlePane;
    private VBox pricePane;
    private VBox descriptVBox;
    private ScrollPane descriptScroll;
    private static int mapIdx;



    CenterPane(int index, DeckMarketPane dmp) {
        LOGGER.setLevel(Level.DEBUG);
        mapIdx = index;
        gp = new GridPane();
        gp.setStyle("-fx-background-color: BLUE");
        gp.setGridLinesVisible(true);
        mediaPane = new VBox();
        titlePane = new GridPane();
        pricePane = new VBox(4);
        descriptVBox = new VBox(4);
        descriptScroll = new ScrollPane();
        layoutParts();
        setData(index, dmp);
    }

    private GridPane setColumns(GridPane gp) {
        LOGGER.debug("setColLayout called");

        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        //col1.setPrefWidth(316);
        col1.setPercentWidth(68);
        //col2.setPrefWidth(156);
        col2.setPercentWidth(32);
        //col1.setPercentWidth(70);
        //col2.setPercentWidth(30);
        gp.getColumnConstraints().addAll(col1, col2);
        //gp.setMaxWidth(472);
        //gp.setPrefWidth(400);
        //gp.setMinWidth(300);
        return gp;
    }

    private GridPane setRowLayout(GridPane gp) {
        LOGGER.debug("setRowLayout called");

        RowConstraints mediaRow = new RowConstraints(266);
        RowConstraints titleRow = new RowConstraints(24);
        RowConstraints lowerRow = new RowConstraints( );
        lowerRow.setVgrow(Priority.ALWAYS);
        gp.getRowConstraints().addAll(mediaRow, titleRow, lowerRow);
        gp.setMinHeight(300);
        return gp;
    }

    private void layoutParts() {
        LOGGER.debug("layoutParts called");

        gp = setColumns(gp);
        gp = setRowLayout(gp);
        gp.add(mediaPane, 0, 0, 2, 1);
        gp.add(titlePane, 0, 1, 2, 1);
        gp.add(descriptScroll, 0, 2, 1, 1);
        gp.add(pricePane, 1, 2, 1, 1);

        descriptVBox.setMaxWidth(Double.MAX_VALUE);
        descriptScroll.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(descriptVBox, Priority.ALWAYS);
        GridPane.setHgrow(pricePane, Priority.ALWAYS);

        mediaPane.setMinWidth(300);
    }


    // ************** SETTERS **************

    /**
     * A deck should know it's index to be added to the
     * cart.
     * @param idx
     */
    public void setMapIdx(int idx) {
        this.mapIdx = idx;
    }

    /**
     * Sets the data in the panes.
     * @param idx
     */
    private void setData(int idx, DeckMarketPane dmp) {
        LOGGER.debug("setData called");

        // hashmap is set in DBFetchToMapAry
        if(dmp.getMapArray().isEmpty()) {
            String message = " I was unable to find anything for that search." +
                    "\nTry a different spelling or a broader search.";
            FxNotify.notificationPurple("Oooph!", message, Pos.CENTER, 10,
                    "image/flashFaces_sunglasses_60.png", FlashMonkeyMain.getWindow());
        } else {
            HashMap<String, String> map = dmp.getMapArray().get(idx);
            setTitlePane(dmp, map.get("deck_name"), map.get("deck_numstars"), map.get("num_users"), getFriends(map.get("deck_id")),
                    getClassMates(map.get("deck_id")));
            setDescriptPane(map.get("deck_descript"), map.get("num_users"), map.get("creator_email"), map.get("create_date"),
                    map.get("last_date"));

            double fee = dmp.getAcct().getFee();
            double price = Double.parseDouble(map.get("price"));
            setPricePane(dmp, map.get("num_cards"), map.get("num_video"), map.get("num_imgs"), map.get("num_audio"),
                    price, fee, calcTotal(fee, price), idx);
            dmp.setTopBar(map.get("deck_school"), map.get("deck_prof"), map.get("section"));
            ImageView img = new ImageView(dmp.getDeckImg(idx));
            img.fitWidthProperty().bind(mediaPane.widthProperty());
            img.maxWidth(Double.MAX_VALUE);
            img.setPreserveRatio(true);
            img.setSmooth(true);
            mediaPane.getChildren().add(img);
        }
    }



    /* called  by setData() */
    private void setDescriptPane(String descript, String numUsers, String authors, String createDate, String lastUsed) {

        LOGGER.debug("setDescriptPane called");

        TextArea descriptArea = new TextArea(descript);
        descriptArea.setWrapText(true);
        Label users = new Label("Num. Users" + numUsers);
        Label auth = new Label("Authors: " + authors);
        // date related

        LOGGER.debug("createDate {}", createDate );

        LocalDate date = LocalDate.parse(createDate);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd MMM yyyy");
        Label created = new Label("Since: " + format.format(date));
        date = Instant.ofEpochMilli(Long.valueOf(lastUsed)).atZone(ZoneId.systemDefault()).toLocalDate();
        Label last = new Label("Last used: " + format.format(date));
        VBox vBox  = new VBox(2);
        //vBox.setPrefWidth(264);

        vBox.getChildren().addAll(descriptArea, users, auth, created, last);
        descriptScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        descriptScroll.setContent(vBox);
    }

    /* called  by setData() */
    private void setTitlePane(DeckMarketPane dmp, String deckName, String numStars, String numUsers, String friends, String classMates) {
        LOGGER.debug("setTitlePane called");

        titlePane.add(new Label(deckName), 0, 0, 1, 1);
        titlePane.add(dmp.getStars(numStars, 64), 1, 0, 1, 1);
        titlePane.add(new Label(numUsers), 2, 0, 1, 1);
        // 2nd row
        titlePane.add(new Label(friends ), 1, 1, 1, 1);
        titlePane.add(new Label(classMates ), 2, 1, 1, 1);
    }

    /* called by setData() */
    private void setPricePane(DeckMarketPane dmp, String numCard, String numVid, String numImg, String numAud, double price, double nonPreemFee, double total, int idx) {
        LOGGER.debug("setPricePane() called");

        Hyperlink preemLink = new Hyperlink("Get premium");
        preemLink.setId("link");
        preemLink.setOnAction(e -> {
            // @TODO go to getPreemium stripe page
        });
        Hyperlink cartLink = new Hyperlink("Add to cart");
        cartLink.setId("link");
        cartLink.setOnAction(e -> {
            // add to cart
            dmp.addToCart(dmp.getMapArray().get(idx));
            // show in cart box
            // TODO update/show items in cart box
        });

        Region spacer1 = new Region();
        Region spacer2 = new Region();
        spacer1.setPrefHeight(40);
        spacer2.setPrefHeight(60);

        Label card = new Label("DECK SIZE: " + numCard + " cards");
        Label vid = new Label("video: " + numVid);
        Label aud = new Label("audio: " + numAud);
        Label img = new Label("image: " + numImg);

        VBox totalsBox = new VBox(2);
        totalsBox.setId("totalsBox");
        Label p = new Label("Price: $" + price);
        Label f = new Label("Non-premium Fee: $" + nonPreemFee);
        Label t = new Label("Total: $" + total);
        totalsBox.getChildren().addAll(p, f, t, spacer2, preemLink, cartLink);

        pricePane.getChildren().addAll(card, vid, img, aud, spacer1, totalsBox);
    }




    // ********* GETTERS **********

    public int getMapIdx() {
        return mapIdx;
    }

    public GridPane getPane() {
        return this.gp;
    }


    private String getFriends(String deck_id) {
        // @TODO finish getFriends

        DBFetchMulti.DECK_FRIENDS.query(deck_id);
        LOGGER.warn("Called DECK_FREINDS that has not been implemented . Returning empty");

        String empty = "";
        return empty;
    }

    private String getClassMates(String deck_id) {
        // @TODO finish getClassMates

        DBFetchMulti.DECK_CLASSMATES.query(deck_id);
        LOGGER.warn("Called DECK_CLASSMATES that has not been implemented . returning empty");

        String empty = "";
        return empty;
    }

    private double calcTotal(double price, double fee) {
        return price + fee;
    }

}
