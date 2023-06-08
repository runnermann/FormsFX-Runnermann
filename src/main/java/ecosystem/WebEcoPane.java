package ecosystem;

import authcrypt.UserData;
import authcrypt.user.EncryptedAcct;
import ch.qos.logback.classic.Level;
import fileops.FileNaming;
import fileops.VertxLink;
import flashmonkey.FlashMonkeyMain;
import forms.utility.Alphabet;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.FxNotify;

import java.util.ArrayList;
import java.util.HashMap;


public class WebEcoPane extends BorderPane {

    //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(WebEcoPane.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(WebEcoPane.class);

    WebEngine engine = new WebEngine();

    private String[] deckIds = null;
    private ArrayList<HashMap<String, String>> cartList;

    public void setDeckIds(String[] idAry) {
        this.deckIds = idAry;
    }
    public void setCartList(ArrayList<HashMap<String, String>> cartList) {
        this.cartList = cartList;
    }

    /**
     * <p>Returns a BorderPane containing a JavaFX Browser.</p>
     * <p>If the browser successfully connects, the user may purchase
     * the deck which was selected in the ConsumerPane/DeckMarketPane.</p>
     * @return Returns a BorderPane containing a JavaFX Browser
     */
    protected BorderPane getPurchasePane() {

        // -----------------     start webview    --------------------- //
        WebView webView = new WebView();
        engine = webView.getEngine();
        //@todo Create the method to purchase multiple decks at a time.
        if(deckIds != null) {
            engine.setUserAgent(getPurchJson(cartList));
            LOGGER.debug(getPurchJson(cartList));

        } else {
            // throw error
            LOGGER.warn("No deckIds were given in if statement to send to WebEngine.UserAgent");
        }

        // Load from local file.
        // Works to load first page, but following pages
        // need to come from the server.
        //	File f = new File(getClass().getClassLoader().getResource("java/main/index.html").getFile());
        //	engine.load(f.toURI().toString());
        // Load web page from remote
        engine.load(VertxLink.REQ_PURCHASE.getLink());
        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (Worker.State.SUCCEEDED.equals(newValue)) {
                    // verify address is the correct address and there has not been a redirect.
                //@todo finish prevent redirects from potential malicious actors
                    //System.out.println("domain: " + engine.getLocation());
                    String domain = engine.locationProperty().getValue();
                    if (!domain.startsWith("https://")) {
                        LOGGER.warn("WARNING: CRITICAL!!! Ecosystem purchase attempting a non secure connection at: {}", engine.getLocation());
                        boolean bool = engine.getLocation().equals(VertxLink.REQ_PURCHASE.getLink());
                        if (bool) {
                            String msg = "NOTICE! There was an attempt to reach a non-secure link. Please reset the connection to the internet.";
                            FxNotify.notificationRedAlert("", " Oooph! " + msg, Pos.CENTER, 8,
                                    "image/flashFaces_smirking_75.png", FlashMonkeyMain.getPrimaryWindow());
                            //System.exit(1);
                        }
                    }
                    if (!engine.getLocation().equals(VertxLink.REQ_PURCHASE.getLink())) {
                        LOGGER.warn("WARNING: CRITICAL!!! Ecosystem purchase attempting domain: {}", engine.getLocation());
                        //System.out.println("Not a jackrabbit");
                        //System.exit(1);
                    }
                } else {
                    LOGGER.warn("failed, location after load: {}, EcoPane. line 108 failed to contact server.", engine.getLocation());
                }
            } catch (Exception e) {
                LOGGER.warn(e.getMessage());
                e.printStackTrace();
            }
        });

        VBox rBox = new VBox(webView);
        BorderPane bPane = new BorderPane();

    //    VBox lBox = new VBox();
    //    lBox.setMinSize(100, 560);
    //    lBox.setAlignment(Pos.CENTER);
        bPane.setCenter(rBox);
        bPane.setStyle("-fx-background-color: TRANSPARENT");
    //    bPane.setLeft(lBox);

        rBox.maxHeightProperty().bind(DeckMarketPane.getInstance().getMarketPane().heightProperty().subtract(70));
        rBox.minHeightProperty().bind(DeckMarketPane.getInstance().getMarketPane().heightProperty().subtract(70));
    //    rBox.setMaxHeight(320);
        rBox.setMaxWidth(400);
        rBox.setPadding(new Insets(12, 12, 0, 12));
        rBox.setId("payPane");

        return bPane;
    }

    /**
     * General Purpose Webview pane.
     * @param page Use a defined enum getLink from VertxLink
     *             for the page. If the page hasn't been defined
     *             then create it.
     * @param wd min width
     * @param ht min height
     * @return The pane containing the webview of the page provided
     * in the param.
     */
    public Pane getWebViewPane(String page, int wd, int ht) {
        WebView webView = new WebView();
        webView.setMinSize(wd, ht);
        engine = webView.getEngine();
        engine.load(page);

        Pane pane = new Pane();
        pane.getChildren().add(webView);

        return pane;
    }


    /**
     * <p>Returns a BorderPane containing a JavaFX Browser.</p>
     * <p>Makes a get request for a Membership Request. If the broswer
     * successfully connects, the user may enter their details
     * to create a membership subscription.</p>
     * <p>Note that Java 17 and JavaFX 17 have broken WebEngine's UserAgent.
     * For Java17 and JavaFX 17,  seeking an alternative. </p>
     * @return Returns a BorderPane containing a JavaFX Browser.
     */
    public BorderPane getReqSubscribePane() {
        // we use the encrypted users name. This is as good as it needs to be.
        String json = "{\"x1\":\"" + Alphabet.encrypt(UserData.getUserName()) + "\"}";

        //System.out.println("json UserAgent string: " + json);

        // -----------------     start webview    --------------------- //
        WebView webView = new WebView();
        engine = webView.getEngine();
        engine.userAgentProperty().setValue(json);
        engine.load(VertxLink.REQ_MEMBER.getLink());
        engine.setJavaScriptEnabled(true);

        engine.setOnAlert(webEvent -> {
            LOGGER.error("WebKit Alert: " + webEvent.getData());
        });

        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                    // if user has previously subscribed (they are a "preem_distro"), the server returns
                    // the onBoarding page, else (they are "free") it returns the subscription
                    // page.
                        String domain = engine.locationProperty().getValue();
                        if (domain.endsWith("HF25XZ")) {
                            if (newValue == Worker.State.SUCCEEDED) {
                                //System.out.println("getReqSubscriptPane worker.state succeeded!");
                                engine.executeScript("callMe(\"" + Alphabet.encrypt(UserData.getUserName()) + "\")");
                            }
                        }
            });

            // RESPONSIVE SIZING for width and height by using stackPane
            StackPane sPane = new StackPane();
            sPane.getChildren().add(webView);
            BorderPane bp = new BorderPane();
            bp.setCenter(sPane);

            return bp;
    }


    /**
     * <p>Returns a BorderPane containing a JavaFX Browser</p>
     * <p>If the browser succesfully connects, makes a request
     * for the users pay-platform account information. IE
     * shows the user their account balance and next
     * deposit date to their account.</p>
     * @return
     */
    protected BorderPane getPayAccount() {

        // -----------------     start webview    --------------------- //
        WebView webView = new WebView();

        engine = webView.getEngine();
        engine.load(VertxLink.REQ_ACCT.getLink());

        VBox rBox = new VBox(webView);
        BorderPane bPane = new BorderPane();

        VBox lBox = new VBox();
        lBox.setMinSize(100, 600);
        lBox.setAlignment(Pos.CENTER);
        bPane.setRight(rBox);
        bPane.setLeft(lBox);

        return bPane;

    }


    // ***** OTHER ***** //


    /**
     * Prepares JSON data to send to Vertx
     * Check that there is data before
     * @param cartList
     * @return
     */
    private String getPurchJson(@NotNull ArrayList<HashMap<String, String>> cartList) {
        //@TODO set getPurchJson for an array of items
        //@TODO in getPurchJson get deck price from Vertx
        HashMap<String, String> map = cartList.get(0);
        EncryptedAcct acct  = DeckMarketPane.getInstance().getAcct();
        String buyer    = UserData.getFirstName();  // let the user input this information in stripe.
        String userName     = Alphabet.encrypt(UserData.getUserName());   // orig_email of the buyer
        String buyerHash    = FileNaming.hashToHex(UserData.getUserName()); // The s3 subdirectory the deck is sent to.
        String deckId       = map.get("deck_id");       // id of deck to purchase
        String price        = map.get("price");         // Does not go forward
        String deckName     = map.get("deck_name");     // Name to be displayed
        String fullDeckName = map.get("full_name");     // Deck full name that is used in S3
        String fee          = map.get("fee");           // non-preem fee
        String currency = acct.getCurrency();

        String json = "{" +
                "\"real_name\":\"" + buyer + "\"" +
                ",\"byr_email\":\"" + userName + "\"" +
                ",\"deck_id\":\"" + deckId + "\"" +
                ",\"deck_price\":\"" + price + "\"" +
                ",\"deck_name\":\"" + deckName + "\"" +
                ",\"full_name\":\"" + fullDeckName + "\"" +
                ",\"currency\":\"" + currency + "\"" +
                ",\"fee\":\"" + fee + "\"" +
                ",\"total\":\"" + (fee + price) + "\"" +
                ",\"hash\":\"" + buyerHash + "\"" +
                "}";

        return json;
    }
}