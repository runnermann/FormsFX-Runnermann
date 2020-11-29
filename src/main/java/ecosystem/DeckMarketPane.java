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
    private boolean userExists = false;
    private EncryptedAcct acct = new EncryptedAcct();
    private ArrayList<GridPane> teaserPanes;
    private ScrollPane teaserScroll;
    private VBox teaserContainer;
    private CenterPane centerPane;
    private VBox topBar;
    private VBox lowerBar;
    private CloudOps clops = new CloudOps();
    private static ArrayList<Image> imageAry;
    // cartItems
    private static ArrayList<HashMap<String, String>> cartList;


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

    private DeckMarketPane() {
        LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("DeckMarketPane constructor called");
        //System.out.println("Calling method. ???: " + Thread.currentThread().getStackTrace()[3].getMethodName());

        gp = new GridPane();
        // gp.setStyle("-fx-background-color: #32CD32");
        gp.setGridLinesVisible(true);

        // Data is set by formAction in DeckSearchModel
        mapArray = new ArrayList<>();
        teaserPanes = new ArrayList<>();
        teaserScroll = new ScrollPane();
        cartList = new ArrayList<>();

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
//        imageAry = clops.getMediaFmS3(strAry);
        setTeaserPanes();
        teaserScroll.setContent(teaserContainer);
        teaserScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        // sets data in Encrypted account and
        // verifies that the user exists.
        userExists = acct.exists();
        if(! userExists) {
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


        teaserContainer = new VBox(4);
        HashMap<String, String> map;// : mapArray

        for(int i = 0; i < mapArray.size(); i++) {
            map = mapArray.get(i);
            // Set the map to the entries in the teaserPane
            TeaserPane tPane = new TeaserPane();
            int finalI = i;
            tPane.getPane(map, i).setOnMouseClicked(e -> {
                centerPane = new CenterPane(finalI, this);
            });

            teaserPanes.add(tPane.getPane(map, i));
            teaserContainer.getChildren().add(teaserPanes.get(i));
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
        topBar.setPadding(new Insets(0,0,0, 510));
        Label university = new Label(institute);
        Label courseLabel = new Label(prof + " " + course);
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

    protected void addToCart(HashMap<String, String> deckMap) {
        this.cartList.add(deckMap);
    }

    /* called  by setData() */
    Image getDeckImg(int index) {
        // todo get image from cloud
        // todo ensure connected and message if not.
        return imageAry.get(index);
    }

    public void layoutParts() {

        LOGGER.debug("layoutParts() called");
        // column, row, col-span, row-span
        gp.add(topBar, 0,0,3,1); // light purple
        gp.add(centerPane.getPane(), 1, 1, 1, 1); // blue
        gp.add(teaserScroll, 2, 1, 1, 1); // orange
        gp.add(lowerBar, 0, 2, 3, 1); // btn purple

        gp.setMinHeight(SceneCntl.getConsumerPaneHt());
        gp.setMinWidth(SceneCntl.getConsumerPaneWd());
        gp.minHeightProperty().bind(ConsumerPane.getInstance().heightProperty());
        gp.minWidthProperty().bind(ConsumerPane.getInstance().widthProperty());
    }


    protected ImageView getStars(String numStars, int width) {
        // @TODO finish getStars
        ImageView img = new ImageView("/icon/24/stars/stars" + numStars + ".png");
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

    public void setAcctData() {
        // @TODO validate this public method
        EncryptedAcct acct = new EncryptedAcct();
 //       acct.

    }

    public void onClose() {
        LOGGER.debug("DeckMarketPane.onClose() called");
      //  this.CLASS_INSTANCE = null;
    }

    EncryptedAcct getAcct() {
        return this.acct;
    }
}
