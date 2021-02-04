package ecosystem;

import campaign.db.DBFetchMulti;
import ch.qos.logback.classic.Level;
import flashmonkey.FlashMonkeyMain;
import forms.utility.Alphabet;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.slf4j.LoggerFactory;

import uicontrols.FxNotify;
import uicontrols.SceneCntl;

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
    private GridPane pricePane;

    private ScrollPane descriptScroll;
    private static int mapIdx;

    public void onClose() {
        gp = null;
        mediaPane = null;
        titlePane = null;
        pricePane = null;
        descriptScroll = null;
        mapIdx = 0;
    }


    /**
     * Constructor, Sets the center pane to the card at the index
     * @param index Th eindex of the card to be displayed in the center pane
     * @param dmp THe deck market pane instance
     */
    CenterPane(int index, DeckMarketPane dmp) {
        LOGGER.setLevel(Level.DEBUG);
        mapIdx = index;
        gp = new GridPane();
    //    gp.setStyle("-fx-background-color: BLUE");
    //    gp.setGridLinesVisible(true);
        mediaPane = new VBox();
        titlePane = new GridPane();
        pricePane = new GridPane();
        descriptScroll = new ScrollPane();
        layoutParts();
        setData(index, dmp);
    }

    private GridPane setGPColumns(GridPane gp) {
        LOGGER.debug("setColLayout called");
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        col1.setPercentWidth(58);
        col2.setPercentWidth(40);
        col1.setHgrow(Priority.NEVER);
        col2.setHgrow(Priority.NEVER);
        gp.getColumnConstraints().addAll(col1, col2);

        return gp;
    }

    private GridPane setTitleColumns(GridPane gp) {
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        ColumnConstraints col3 = new ColumnConstraints();
        col1.setPercentWidth(60);
        col2.setPercentWidth(30);
        col3.setPercentWidth(10);
        gp.getColumnConstraints().addAll(col1, col2, col3);

        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        row1.setPrefHeight(30);
        row2.setPrefHeight(15);
        gp.getRowConstraints().addAll(row1, row2);
        return gp;
    }

    private GridPane setDescriptColumns(GridPane gp) {
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        //col2.setHgrow(Priority.ALWAYS);
        col1.setPercentWidth(40);
        col2.setPercentWidth(60);
        gp.getColumnConstraints().addAll(col1, col2);
        return gp;
    }

    private GridPane setRowLayout(GridPane gp) {
        LOGGER.debug("setRowLayout called");
        RowConstraints spacer = new RowConstraints(10);
        RowConstraints mediaRow = new RowConstraints(266);
        // Hard coded to match row1 & row2 in setTitleColumns
        RowConstraints titleRow = new RowConstraints(45);
        RowConstraints lowerRow = new RowConstraints( );
        lowerRow.setVgrow(Priority.ALWAYS);
        gp.getRowConstraints().addAll(spacer, mediaRow, spacer, titleRow, lowerRow);
        gp.setMinHeight(300);
        return gp;
    }

    private void layoutParts() {
        LOGGER.debug("layoutParts called");

        gp = setGPColumns(gp);
        gp = setRowLayout(gp);
        gp.add(mediaPane, 0, 1, 2, 1);
        gp.add(titlePane, 0, 3, 2, 1);
        gp.add(descriptScroll, 0, 4, 1, 1);
        gp.add(pricePane, 1, 4, 1, 1);

        mediaPane.setId("mediaPane");
        titlePane.setId("titlePane");
        descriptScroll.setId("scrollPane");
        pricePane.setId("pricePane");

        //descriptVBox.setMaxWidth(Double.MAX_VALUE);
        descriptScroll.setMaxHeight(Double.MAX_VALUE);
        // GridPane.setHgrow(descriptVBox, Priority.ALWAYS);
        GridPane.setHgrow(pricePane, Priority.ALWAYS);

        mediaPane.setMinWidth(300);
    }


    // ************** SETTERS **************



    /*
    The set of panes underneath the title pane and to the left of the price panes. Contains the TextArea with
    the long description of the deck. ANd underneath many of the other metrics such as number of users cards, media
    etc...
    called  by setData() */
    GridPane grid;
    private void setDescriptPane(String descript, String numUsers, String authors, String createDate, String lastUsed) {

        LOGGER.debug("setDescriptPane called");
        grid = new GridPane();
        grid = setDescriptColumns(grid);

        Label textArea = new Label(descript);
        textArea.setWrapText(true);
        textArea.setId("description");
        textArea.setPadding(new Insets(10,0,40,0));

        //textArea.prefWidthProperty().bindBidirectional(descriptScroll.prefWidthProperty());
        Label ul = new Label("Users: ");
        Label un = new Label(numUsers);
        Label al = new Label("Authors: " );
        Label an = new Label(Alphabet.decrypt(authors));

        // date related

        LOGGER.debug("createDate {}", createDate );

        LocalDate date = LocalDate.parse(createDate);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd MMM yyyy");
        Label created = new Label("Since: " + format.format(date));
        date = Instant.ofEpochMilli(Long.valueOf(lastUsed)).atZone(ZoneId.systemDefault()).toLocalDate();
        Label last = new Label("Last used: " + format.format(date));

        //vBox.setPrefWidth(264);
        grid.add(textArea, 0, 0, 1, 2);
        grid.add(ul, 0, 2, 1, 1);
        grid.add(un, 1, 2, 1, 1);
        grid.add(al, 0, 3, 1, 1);
        grid.add(an, 1, 3, 1, 1);
        grid.add(created, 0, 4, 1, 1);
        grid.add(last, 0, 5, 1, 1);

    //    grid.setGridLinesVisible(true);

        ul.setId("purple14");
        al.setId("purple14");
        un.setId("purpleRight");
        an.setId("purpleRight");

        descriptScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        descriptScroll.setContent(grid);

        // Set textArea min and max width... to be adjustable ... PITA!!!
        descriptScroll.widthProperty().addListener((obs, oldval, newVal) -> {
            System.out.println("descriptScroll new width: " + newVal.doubleValue() );
            textArea.setMinWidth(newVal.doubleValue() - 8);
            textArea.setMaxWidth(newVal.doubleValue() - 8);

            grid.setMinWidth(newVal.doubleValue() - 8);
            grid.setMaxWidth(newVal.doubleValue() - 8);
        });
    }

    /* The title pane underneath the image in the CenterPane called  by setData() */
    private void setTitlePane(DeckMarketPane dmp, String deckName, String numStars, String numUsers, String friends, String classMates) {
        LOGGER.debug("setTitlePane called");
         Label deckLabel = new Label(deckName);
         deckLabel.setId("bold16");
         Label numLabel = new Label(numUsers);
         numLabel.setId("norm16");
         Label classLabel = new Label(classMates);
         classLabel.setId("purple14");
         Label friendsLabel = new Label(friends);
         friendsLabel.setId("purple14");

        titlePane = setTitleColumns(titlePane);
        titlePane.add(deckLabel, 0, 0, 1, 1);
        titlePane.add(dmp.getStars(numStars, 100), 1, 0, 1, 1);
        titlePane.add(numLabel, 2, 0, 1, 1);
        // 2nd row
        titlePane.add(classLabel, 1, 1, 1, 2);
        titlePane.add(friendsLabel, 2, 1, 1, 2);

    }

    /* called by setData() */
    private void setPricePane(DeckMarketPane dmp, String numCard, String numVid, String numImg, String numAud, double price, double nonPreemFee, double total, int idx) {
        LOGGER.debug("setPricePane() called");
        pricePane = setPriceColumns(pricePane);
        //pricePane.setGridLinesVisible(true);
        pricePane.setPadding(new Insets(10, 0, 0,0));

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

        Text card = new Text("DECK SIZE: ");
        Text cardNum = new Text(numCard + " cards");
        pricePane.add(card, 0,0);
        pricePane.add(cardNum, 1,0);

        Text vidLbl = new Text("video:" );
        Text vidNum = new Text(numVid);
        pricePane.add(vidLbl, 0, 1);
        pricePane.add(vidNum, 1, 1);

        Text audLbl = new Text("audio:");
        Text audNum = new Text(numVid);
        pricePane.add(audLbl, 0,2);
        pricePane.add(audNum, 1, 2);

        Text imgLbl = new Text("image:");
        Text imgNum = new Text(numVid);
        pricePane.add(imgLbl, 0,3);
        pricePane.add(imgNum, 1, 3);

        Region space = new Region();
        Region space1 = new Region();
        space.setPrefHeight(20);
        space1.setPrefHeight(20);
        pricePane.add(space, 0, 4, 2,2);

        Text p = new Text("Price:" );
        Text pp = new Text("$" + price);
        Text f = new Text("Non-premium Fee:");
        Text ff = new Text("$" + nonPreemFee);
        Label t = new Label("Total:");
        Text tt = new Text("$" + total);
        pricePane.add(p, 0, 6);
        pricePane.add(pp, 1, 6);
        pricePane.add(f, 0, 7);
        pricePane.add(ff, 1, 7);
        pricePane.add(t, 0, 8);
        pricePane.add(tt, 1, 8);
        pricePane.add(space1, 0,9,2,1);
        pricePane.add(preemLink, 0, 10);
        pricePane.add(cartLink, 0, 11);
    }

    private GridPane setPriceColumns(GridPane gp) {
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col1.setPercentWidth(60);
        col2.setPercentWidth(40);
        col2.setHalignment(HPos.RIGHT);
        gp.getColumnConstraints().addAll(col1, col2);
        return gp;
    }


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
            if(img == null) {
                System.out.println("DeckMarketPane.setData() cloud not find image");
                System.exit(1);
            }
            double wd = SceneCntl.getConsumerPaneWd() * .38;

            img.fitWidthProperty().bind(mediaPane.widthProperty());
            //img.maxWidth(676 - 8);
            //img.minWidth(676 - 8);
            img.setPreserveRatio(true);
            img.setSmooth(true);
            mediaPane.getChildren().add(img);
            mediaPane.setMaxWidth(wd);
            mediaPane.setAlignment(Pos.CENTER);

        }
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

        String empty = "Friends: ";
        return empty;
    }

    private String getClassMates(String deck_id) {
        // @TODO finish getClassMates

        DBFetchMulti.DECK_CLASSMATES.query(deck_id);
        LOGGER.warn("Called DECK_CLASSMATES that has not been implemented . returning empty");

        String empty = "Class Mates: ";
        return empty;
    }

    private double calcTotal(double price, double fee) {
        return price + fee;
    }



}
