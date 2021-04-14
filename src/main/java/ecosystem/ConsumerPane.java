package ecosystem;

import ch.qos.logback.classic.Level;
import flashmonkey.FlashMonkeyMain;
import forms.DeckSearchPane;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Contains the layers for the search on layer 1,
 * and beneath are the market and teaser panes.
 */
public class ConsumerPane extends StackPane {

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ConsumerPane.class);
    //private static final Logger LOGGER = LoggerFactory.getLogger(DeckSearchModel.class);
    /* VERSION */
    public static final long VERSION = FlashMonkeyMain.VERSION;
    /* SINGLETON */
    private static ConsumerPane CLASS_INSTANCE = null;

    static StackPane mainStackPane;
    private AnchorPane layer1; // Search
    private DeckSearchPane searchPane;
    private DeckMarketPane layer0; // everything else

    /**
     * Closes this pane and calls searchPane.onClose,
     * and sets the class_instance to null.
     */
    public void onClose() {
        searchPane.onClose();
        LOGGER.debug("ConsumerPane.onClose() called");
        layer0.onClose();
        layer1 = null;
        mainStackPane.getChildren().clear();
        mainStackPane = null;
        CLASS_INSTANCE = null;
    }


    // Double-checked locking for singleton class
    public static synchronized ConsumerPane getInstance() {
        System.out.println("ConsumerPane getInstance called");
        if(CLASS_INSTANCE == null) {
            System.out.println("ConsumerPane outer called new instance");
            synchronized (ConsumerPane.class) {
                if (CLASS_INSTANCE == null) {
                    CLASS_INSTANCE = new ConsumerPane();
                    System.out.println("ConsumerPane inner called new instance");
                }
            }
        }
        return CLASS_INSTANCE;
    }

    private ConsumerPane() {
        init();
    }

    //xxxxx maparray build by search results and used by CenterPane and TeaserPane
    public void init() {
        LOGGER.setLevel(Level.DEBUG);
        mainStackPane = new StackPane();

        //mainPane.setStyle("-fx-background-color: #D20035");
        searchPane = new DeckSearchPane();
        layer1 = new AnchorPane(searchPane.getFormPane());


        layoutSearch();
        //layer0 = new DeckMarketPane(); // everything else
    }


    private void layoutSearch() {
        LOGGER.debug("layoutSearch called");
        mainStackPane.getChildren().clear();
        layer1.setTopAnchor(searchPane.getFormPane(), 0.0);
        layer1.setLeftAnchor(searchPane.getFormPane(), 0.0);
        // set 20 from left and 50 from top
        //layer1.getFormPane().setPadding(new Insets(20,0,0,50));
        mainStackPane.getChildren().add(layer1);
    }


    public void layoutConsumer() {
        LOGGER.debug("layoutConsumer called");
        mainStackPane.setAlignment(Pos.TOP_LEFT);
        layer1.setTopAnchor(searchPane.getFormPane(), 20.0);
        layer1.setLeftAnchor(searchPane.getFormPane(), 50.0);
        layer1.setMaxWidth(600);
        // EcoPurchase ep = new EcoPurchase();
        layer0 = DeckMarketPane.getInstance(); // everything else
        //layer0.getMarketPane().setAlignment((Pos.TOP_LEFT));
        //layer1.getFormPane().setAlignment(Pos.CENTER_LEFT);
        //layer1.getFormPane().setPadding(new Insets(0,0,0,50));
        mainStackPane.getChildren().clear();
        //
        // mainPane.setAlignment(Pos.TOP_LEFT);
        // StackPane contains layer0 & layer1 that are Objects, need to use getPane().
        mainStackPane.getChildren().addAll( layer0.getMarketPane(), layer1);
    }

    public static class EcoPurchase {
        // Set on layer above visible layers.
        private static void layoutWebView(EcoPane eco) {
            LOGGER.debug("layoutWebView called");
            EcoPane ePane = eco;

            BorderPane bp = ePane.getPurchasePane();
            AnchorPane layer3 = new AnchorPane(bp);
            //DeckMarketPane dmpInstance = DeckMarketPane.getInstance();
            layer3.setTopAnchor(bp, 30.0);
            layer3.setLeftAnchor(bp, 370.0);
            // @TODO setmaxheigth to be responsive
            //bp.setMaxHeight(370);
            //layer3.setMaxHeight(370);
            mainStackPane.getChildren().add(layer3);
        }

        public static void purchaseAction(ArrayList<HashMap<String, String>> cartList) {
            EcoPane ePane = new EcoPane();
            ePane.setDeckIds(getDeckIds(cartList));
            ePane.setCartList(cartList);
            layoutWebView(ePane);
        }

        private static String[] getDeckIds(ArrayList<HashMap<String, String>> cartList) {
            String[] idAry = new String[cartList.size()];
            for(int i = 0; i < cartList.size(); i++) {
                idAry[i] = cartList.get(i).get("deck_id");
            }
            return idAry;
        }
    }

    public static class EcoOnboard {
        private static void layoutWebView() {
            LOGGER.debug("layoutWebView called");
            EcoPane ePane = new EcoPane();
            mainStackPane.getChildren().add(ePane.getReqMembershipPane());
        }

        public static void reqSubscription() {
            layoutWebView();
        }
    }


    public StackPane getConsumerPane() {
        return this.mainStackPane;
    }

}
