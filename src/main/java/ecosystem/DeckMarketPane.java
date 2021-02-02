package ecosystem;

import authcrypt.UserData;
import authcrypt.user.EncryptedAcct;
import ch.qos.logback.classic.Level;

import fileops.CloudOps;
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
import org.slf4j.LoggerFactory;
import uicontrols.SceneCntl;

import java.util.ArrayList;
import java.util.HashMap;

public class DeckMarketPane {

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DeckMarketPane.class);
    //private static final Logger LOGGER = LoggerFactory.getLogger(DeckSearchModel.class);

    /* VERSION */
    public static final long VERSION = FlashMonkeyMain.VERSION;
    /* SINGLETON */
    private static volatile DeckMarketPane CLASS_INSTANCE = null;

    private GridPane gp;
    // arraylist of DeckMetaData from query
    private static ArrayList<HashMap<String, String>> mapArray;
    // private String[] acctStatus; / in EncrypytedAcct
    // private boolean userExists = false;
    private EncryptedAcct acct;// = new EncryptedAcct();
    private ArrayList<HBox> teaserPaneList;
    private ScrollPane teaserScroll;
    private VBox teaserVBox;
    private CenterPane centerPane;
    private VBox topBar;
    private VBox lowerBar;
    private CloudOps clops = new CloudOps();
    private static ArrayList<Image> imageAry;
    // cartItems
    private static ArrayList<HashMap<String, String>> cartList;
    // buttons
    private Button purchaseButton;

    // Double-checked locking for singleton class. There are no guarantees
    // that only one singleton will exist. Always check.
    public static synchronized DeckMarketPane getInstance() {
        if(CLASS_INSTANCE == null) {
            System.out.println("DeckMarketPane CLASS_INSTANCE is null");
            synchronized (DeckMarketPane.class) {
                if (CLASS_INSTANCE == null) {
                    CLASS_INSTANCE = new DeckMarketPane();
                }
            }
        }
        return CLASS_INSTANCE;
    }


    // ******* Singleton private constructor ********* //

    private DeckMarketPane() {
        LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("DeckMarketPane constructor called");
        //System.out.println("Calling method. ???: " + Thread.currentThread().getStackTrace()[3].getMethodName());

        gp = new GridPane();
        // gp.setStyle("-fx-background-color: #32CD32");
    //    gp.setGridLinesVisible(true);

        // Data is set by formAction in DeckSearchModel
        mapArray = new ArrayList<>();
        teaserPaneList = new ArrayList<>();
        teaserScroll = new ScrollPane();
        cartList = new ArrayList<>();

        acct = new EncryptedAcct();

        //teaserScroll.setStyle("-fx-background-color: #FF5400");
        topBar = new VBox();
        lowerBar = new VBox();

        purchaseButton = new Button("purchase");
        purchaseButton.setId("purchButton");
        purchaseButton.setOnAction(e -> {
            ConsumerPane.EcoPurchase.purchaseAction(cartList);
        });
        setLowerBar();
        //lowerBar.getChildren().add(purchaseButton);
    }

    // ******* GETTERS *********

    ArrayList<HashMap<String, String>> getMapArray() {
        return this.mapArray;
    }

    // ******* SETTERS ********

    /**
     * builds the Pane. mapArray must have
     * data before being called.
     */
    public void build() {
        LOGGER.debug("build() called");
        // get media for decks.
        // should be limited to reduce
        // delay.
        ArrayList<String> strAry = setImgNames(mapArray);
        // @TODO get and put thumbs in public s3
    //    imageAry = clops.getMediaFmS3(strAry);
        imageAry = new ArrayList<>(1);
        for(int i = 0; i < 5; i++) {
            imageAry.add(new Image(getClass().getResourceAsStream("/image/professorLg.png")));
        }
        setTeaserPanes();
        int width = SceneCntl.getFileSelectPaneWd();
        teaserScroll.setContent(teaserVBox);
        teaserScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//        teaserScroll.setPrefWidth(width);
//        teaserScroll.setMaxHeight(446);
//        teaserScroll.setId("fileSelectOuter");
        // sets data in Encrypted account and
        // verifies that the user exists.
        //userExists = acct.exists();
        if( !acct.exists()) {
            LOGGER.warn("User does not exist in database when using DeckSearch userName: " + UserData.getUserName());
            // @TODO redirect user to create account on login page ?? or setup account in DB at create new user ???
        }
        centerPane = new CenterPane(0, this);
        gp = setColumns(gp);
        gp = setRowLayout(gp);
        layoutParts();
    }


    private void setTeaserPanes() {
        LOGGER.debug("setTeaserPanes() called");

        teaserVBox = new VBox(4);
        teaserVBox.setPadding(new Insets(10, 0, 10, 4));
        HashMap<String, String> map;// : mapArray
        TeaserPane tPane = new TeaserPane();

        for(int i = 0; i < mapArray.size(); i++) {
            int idx = i;
            map = mapArray.get(idx);
            // Set the map to the entries in the teaserPane

            HBox h = tPane.build(map, i);

            System.out.println("setTeaserPanes idx: " + idx);
            h.setOnMouseClicked(e -> {
                System.out.println("teaserPane clicked. idx: " + idx);
                centerPane.getPane().getChildren().clear();
                centerPane = new CenterPane(idx, this);
                gp.getChildren().remove(gp.getChildren().size() - 1);
                gp.add(centerPane.getPane(), 1, 1, 1, 1); // blue
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

    void setTopBar(String institute, String prof, String course) {
        topBar.setId("topBar");
        topBar.setPadding(new Insets(5,0,0, 480));
        Label university = new Label(institute);
        university.setId("white14");
        Label courseLabel = new Label("Professor: " + prof + ",  Course: " + course);
        courseLabel.setId("white14");
        // clear any previous nodes
        topBar.getChildren().clear();
        topBar.getChildren().addAll(university, courseLabel);
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
        gp.add(topBar, 0,0,3,1); // light purple
        gp.add(teaserScroll, 2, 1, 1, 1); // orange
        gp.add(lowerBar, 0, 2, 3, 1); // btn purple
        gp.add(centerPane.getPane(), 1, 1, 1, 1); // blue

        gp.setMinHeight(SceneCntl.getConsumerPaneHt());
        gp.setMinWidth(SceneCntl.getConsumerPaneWd());
        gp.minHeightProperty().bind(ConsumerPane.getInstance().heightProperty());
        gp.minWidthProperty().bind(ConsumerPane.getInstance().widthProperty());
    }


    protected ImageView getStars(String numStars, int width) {
        // @TODO finish getStars
        Image stars = new Image(getClass().getResourceAsStream("/icon/24/stars/stars" + numStars + ".png")); //new Image("File:/icon/24/stars/stars" + numStars + ".png");
        ImageView img = new ImageView(stars);
        img.setFitWidth(width);
        img.setPreserveRatio(true);
        img.setSmooth(true);

        return img;
    }


    protected GridPane getMarketPane() {
        return gp;
    }

    public void setMapArray(ArrayList<HashMap<String, String>> mapAry) {
        LOGGER.debug("setMapArray called");
        this.mapArray = mapAry;
    }

    // ***** Cart or Account related ***** //

    protected void addToCart(HashMap<String, String> deckMap) {
        this.cartList.add(deckMap);
    }

    public void setAcctData() {
        // @TODO validate this public method
        EncryptedAcct acct = new EncryptedAcct();
 //       acct.

    }

    EncryptedAcct getAcct() {
        return this.acct;
    }

    public void onClose() {
        LOGGER.debug("DeckMarketPane.onClose() called");
        //  this.CLASS_INSTANCE = null;
    }

}
