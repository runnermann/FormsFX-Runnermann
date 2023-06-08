package ecosystem;

import authcrypt.UserData;
import authcrypt.user.EncryptedAcct;

import fileops.VertxLink;
import flashmonkey.FlashMonkeyMain;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import media.sound.SoundEffects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.ButtoniKon;
import uicontrols.SceneCntl;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * ContainerPane for all of the ecosystem.
 */
public class DeckMarketPane {

    //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DeckMarketPane.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(DeckMarketPane.class);

    /* VERSION */
    public static final long VERSION = FlashMonkeyMain.VERSION;
    /* SINGLETON */
    private static DeckMarketPane CLASS_INSTANCE = null;

    private GridPane mainGP;
    private boolean isBuilt = false;
    // arraylist of DeckMetaData from query
    private static ArrayList<HashMap<String, String>> metaDataMapArray;
    // private String[] acctStatus; / in EncrypytedAcct
    // private boolean userExists = false;
    private EncryptedAcct acct;// = new EncryptedAcct();
    private ArrayList<HBox> teaserPaneList;
    private final ScrollPane teaserScroll;
    private VBox teaserVBox;
    private CenterPane centerPane;
    private GridPane topBar;
    private final VBox lowerBar;
    //private CloudOps clops = new CloudOps();
    private static ArrayList<Image> imageAry;

    // cartItems decks
    private static ArrayList<HashMap<String, String>> cartList;
//    private static Text cartText;
    private static VBox cartPane;
    // buttons
    private final Button purchaseButton;
    private static long total;

    public void onClose() {
        LOGGER.debug("DeckMarketPane called onClose");
        centerPane.onClose();// = null;
        teaserPaneList = null;
        acct = null;
        metaDataMapArray = null;
        teaserVBox = null;
        imageAry = null;
        cartList = null;
        CLASS_INSTANCE = null;
    }

    // Double-checked locking for singleton class. There are no guarantees
    // that only one singleton will exist. Always check.
    public static synchronized DeckMarketPane getInstance() {
        if(CLASS_INSTANCE == null) {
            synchronized (DeckMarketPane.class) {
                if (CLASS_INSTANCE == null) {
                    CLASS_INSTANCE = new DeckMarketPane();
                }
                //this.reset();
            }
        }
        return CLASS_INSTANCE;
    }


    // ******* Singleton private constructor ********* //

    private DeckMarketPane() {
        //LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("DeckMarketPane constructor called");
        //System.out.println("Calling method. ???: " + Thread.currentThread().getStackTrace()[3].getMethodName());

        mainGP = new GridPane();
        cartPane = new VBox();
        cartPane.setId("cartPane");
        // gp.setStyle("-fx-background-color: #32CD32");
        // gp.setGridLinesVisible(true);

        // Data is set by formAction in DeckSearchModel
        metaDataMapArray = new ArrayList<>();
        teaserPaneList = new ArrayList<>();
        teaserScroll = new ScrollPane();
        cartList = new ArrayList<>();
        // the users account information
        acct = new EncryptedAcct();
        lowerBar = new VBox();

        purchaseButton = ButtoniKon.getPurchasButton();
        purchaseButton.setOnAction(e -> {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            ConsumerPane.EcoPurchase.purchaseAction(cartList);
        });
        setLowerBar();
        //lowerBar.getChildren().add(purchaseButton);
    }


    private void reset() {

    }



    // ******* GETTERS *********

    ArrayList<HashMap<String, String>> getMapArray() {
        return metaDataMapArray;
    }

    // ******* SETTERS ********

    /**
     * builds the Pane. mapArray must have
     * data before being called.
     */
    public void build() {
        if( !isBuilt) {
            isBuilt = true;
            LOGGER.debug("build() called");
            // get media for decks.
            // should be limited to reduce
            // delay.
            ArrayList<String> strAry = setImgNames(metaDataMapArray);
            imageAry = new ArrayList<>(1);
            String s3link;
            for(int i = 0; i < metaDataMapArray.size(); i++) {
                s3link = VertxLink.DECK_DESCRIPT_PHOTO.getEndPoint() + metaDataMapArray.get(i).get("deck_photo");
                imageAry.add(new Image(s3link, true));
            }
            setTeaserPanes();

            teaserScroll.setId("teaser");

            teaserScroll.setStyle("-fx-background-color: TRANSPARENT");
            teaserScroll.setContent(teaserVBox);
            teaserScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            // sets data in Encrypted account and
            // verifies that the user exists.
            //userExists = acct.exists();
            if( !acct.exists()) {
                LOGGER.warn("User does not exist in database when using DeckSearch userName: " + UserData.getUserName());
                // @TODO redirect user to create account on login page ?? or setup account in DB at create new user ???
            }

            centerPane = new CenterPane(0, this);
            mainGP = setColumns(mainGP);
            mainGP = setRowLayout(mainGP);
            layoutParts();
        } else {
            setTeaserPanes();
            teaserScroll.setContent(teaserVBox);
        }
    }


    private void setTeaserPanes() {
        LOGGER.debug("setTeaserPanes() called");

        teaserVBox = new VBox(4);
        teaserVBox.setStyle("-fx-background-color: TRANSPARENT");
        teaserVBox.setPadding(new Insets(10, 4, 10, 4));
        HashMap<String, String> map;// : mapArray
        TeaserPane tPane = new TeaserPane();

        for(int i = 0; i < metaDataMapArray.size(); i++) {
            int idx = i;
            map = metaDataMapArray.get(idx);
            // Set the map to the entries in the teaserPane

            HBox h = tPane.build(map, i);

            h.setOnMouseClicked(e -> {
                centerPane.getPane().getChildren().clear();
                centerPane = new CenterPane(idx, this);
                mainGP.getChildren().remove(mainGP.getChildren().size() - 1);
                mainGP.add(centerPane.getPane(), 1, 1, 1, 1); // blue
            });

            teaserPaneList.add(h);
            teaserVBox.getChildren().add(h);
        }
    }

    private ArrayList<String> setImgNames(ArrayList<HashMap<String, String>> mapAry) {
        ArrayList<String> imgNames = new ArrayList<>(mapAry.size());
        for(int i = 0; i < mapAry.size(); i++) {
            imgNames.add(mapAry.get(i).get("deck_photo"));
        }
        return imgNames;
    }

    void setTopBar(String institute, String prof, String course, int cartsize, long total) {
        if(topBar == null) {
            topBar = new GridPane();
            topBar.setId("topBar");
            topBar = setTopColumns(topBar);
        } else {
            topBar.getChildren().clear();
        }

        //topBar.setPadding(new Insets(5,40,0, 480));

        Label university = new Label(institute);
        university.setId("white14");
        university.setStyle("-fx-text-fill: white");
        Label courseLabel = new Label("Professor: " + prof + ",  Course: " + course);
        courseLabel.setStyle("-fx-text-fill: white");
        courseLabel.setId("white14");
        VBox profBox = new VBox();
        profBox.getChildren().addAll(university, courseLabel);
        profBox.setAlignment(Pos.CENTER_LEFT);
        topBar.add(profBox, 1,0);
        Label cartLbl = new Label("Cart: " + cartsize + " decks $" + total);

        VBox cartBox = new VBox();
        cartBox.setId("cartBox");
        cartBox.setMaxWidth(120);
        cartBox.getChildren().addAll(cartLbl);
        cartBox.setAlignment(Pos.CENTER_RIGHT);

        topBar.add(cartBox, 2, 0);
    }

    private GridPane setTopColumns(GridPane gp) {
        ColumnConstraints col1 = new ColumnConstraints(); // column 0 = spacer
        ColumnConstraints col2 = new ColumnConstraints(); // column 1 = main
        ColumnConstraints col3 = new ColumnConstraints(); // teaser
        col1.setPercentWidth(38);
        col2.setPercentWidth(38);
        col2.setHalignment(HPos.RIGHT);
        col3.setPercentWidth(23);
        col3.setHalignment(HPos.RIGHT);
        gp.getColumnConstraints().addAll(col1, col2, col3);
        return gp;
    }

    void setLowerBar() {
        lowerBar.setId("lowerBar");
        lowerBar.setAlignment(Pos.CENTER_RIGHT);
        lowerBar.setPadding(new Insets(0,20,0,0));
        lowerBar.getChildren().add(purchaseButton);
    }


    public GridPane setColumns(GridPane gp) {
        LOGGER.debug("setColLayout() called");

        ColumnConstraints col1 = new ColumnConstraints(); // column 0 = spacer
        ColumnConstraints col2 = new ColumnConstraints(); // column 1 = main
        ColumnConstraints col3 = new ColumnConstraints(); // teaser
        //col1.setPrefWidth(500);
        col1.setHalignment(HPos.CENTER);
        col1.setPercentWidth(38);
        //col2.setPrefWidth(476);
        col2.setPercentWidth(38);
        col2.setHgrow(Priority.NEVER);
        //col3.setPrefWidth(400);
        col3.setPercentWidth(24);
        gp.getColumnConstraints().addAll(col1, col2, col3);
        return gp;
    }

    public GridPane setRowLayout(GridPane gp) {
        LOGGER.debug("setRowLayout called");

        RowConstraints top = new RowConstraints(48);
        RowConstraints center = new RowConstraints();
        center.setVgrow(Priority.ALWAYS);
        center.setValignment(VPos.CENTER);
        RowConstraints bottom = new RowConstraints(48);
        gp.getRowConstraints().addAll(top, center, bottom);
        return gp;
    }


    /* called  by setData() */
    Image getDeckImg(int index) {
        // todo get image from cloud
        // todo ensure connected and message if not.
        //imageAry = new ArrayList<>(1);
        //imageAry.add(new Image(getClass().getResourceAsStream(mapArray.get(index)));
        return imageAry.get(index);
    }

    public void layoutParts() {
        LOGGER.debug("layoutParts() called");
        // column, row, col-span, row-span
        if( ! mainGP.getChildren().contains(teaserScroll)) {
            mainGP.add(teaserScroll, 2, 1, 1, 1); // orange
            mainGP.add(topBar, 0, 0, 3, 1); // transparent
            mainGP.add(lowerBar, 0, 2, 3, 1); // btn purple
            mainGP.add(centerPane.getPane(), 1, 1, 1, 1); // blue

            mainGP.setMinHeight(SceneCntl.getConsumerPaneHt());
            mainGP.setMinWidth(SceneCntl.getConsumerPaneWd());
            mainGP.setMaxWidth(SceneCntl.getConsumerPaneWd());
            //mainGP.setMaxHeight(SceneCntl.getConsumerPaneHt());
            mainGP.minHeightProperty().bind(ConsumerPane.getInstance().heightProperty());
            mainGP.minWidthProperty().bind(ConsumerPane.getInstance().widthProperty());
        }
    }


    protected ImageView getStars(String numStars, int width) {
        Image stars = new Image(getClass().getResourceAsStream("/icon/24/stars/stars" + numStars + ".png")); //new Image("File:/icon/24/stars/stars" + numStars + ".png");
        ImageView img = new ImageView(stars);
        img.setFitWidth(width);
        img.setPreserveRatio(true);
        img.setSmooth(true);
        img.setId("pad6bottom");

        return img;
    }


    protected GridPane getMarketPane() {
        return mainGP;
    }

    public double getHeight() {
        return this.mainGP.getBoundsInLocal().getHeight();
    }

    public double getX() {
        return this.mainGP.getLayoutX();
    }
    public double getY() {
        return this.mainGP.getLayoutY();
    }

    public void setMapArray(ArrayList<HashMap<String, String>> mapAry) {
        LOGGER.debug("setMapArray called");
        metaDataMapArray = mapAry;
    }

    /**
     * If the returned data from the query is empty.
     * @return true if there is not any data stored in the map.
     */
    public boolean isEmpty() {
        return (metaDataMapArray.size() == 0 || ! metaDataMapArray.get(0).get("empty").equals("false"));
    }

    // ***** Cart or Account related ***** //

    protected int getCartSize() {
        return cartList.size();
    }

    protected long getTotal() {
        return total;
    }

    protected void addToCart(HashMap<String, String> deckMap, int idx) {
        cartList.add(deckMap);
        HashMap<String, String> map = metaDataMapArray.get(idx);
        calcTotal();
        setTopBar(map.get("deck_school"), map.get("deck_prof"), map.get("section"), cartList.size(), total);
    }


    /**
     * Calculates the total cost including deck price that were added to the list
     * as well as the non-preem fee's per deck.
     */
    public void calcTotal() {
        total = 0;
        for(HashMap<String, String> m : cartList) {
            total += Long.parseLong(m.get("price"));
            total += getAcct().getFee();
        }
    }

    public void setAcctData() {
        // @TODO validatorActionSwitch this public method
        EncryptedAcct acct = new EncryptedAcct();
 //       acct.

    }

    EncryptedAcct getAcct() {
        return this.acct;
    }

}
