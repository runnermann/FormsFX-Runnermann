package ecosystem;

import ch.qos.logback.classic.Level;
import flashmonkey.FlashMonkeyMain;
import forms.DeckSearchPane;
import forms.FormParentPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import metadata.DeckMetaData;
import org.slf4j.LoggerFactory;
import uicontrols.SceneCntl;

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
    private static volatile ConsumerPane CLASS_INSTANCE = null;

    static StackPane mainPane;
    private AnchorPane layer1; // Search
    private DeckSearchPane searchPane;
    private DeckMarketPane layer0; // everything else


    // Double-checked locking for singleton class
    public static synchronized ConsumerPane getInstance() {

        if(CLASS_INSTANCE == null) {
            synchronized (ConsumerPane.class) {
                if (CLASS_INSTANCE == null) {
                    CLASS_INSTANCE = new ConsumerPane();
                    //System.out.println("DeckMetaData called new instance");
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
        mainPane = new StackPane();

        //mainPane.setStyle("-fx-background-color: #D20035");
        searchPane = new DeckSearchPane();
        layer1 = new AnchorPane(searchPane.getFormPane());


        layoutSearch();
        //layer0 = new DeckMarketPane(); // everything else
    }


    private void layoutSearch() {
        LOGGER.debug("layoutSearch called");
        mainPane.getChildren().clear();
        layer1.setTopAnchor(searchPane.getFormPane(), 0.0);
        layer1.setLeftAnchor(searchPane.getFormPane(), 0.0);
        // set 20 from left and 50 from top
        //layer1.getFormPane().setPadding(new Insets(20,0,0,50));
        mainPane.getChildren().add(layer1);
    }


    public void layoutConsumer() {
        LOGGER.debug("layoutConsumer called");
        mainPane.setAlignment(Pos.TOP_LEFT);
        layer1.setTopAnchor(searchPane.getFormPane(), 20.0);
        layer1.setLeftAnchor(searchPane.getFormPane(), 50.0);
        layer1.setMaxWidth(600);
        // EcoPurchase ep = new EcoPurchase();
        layer0 = DeckMarketPane.getInstance(); // everything else
        //layer0.getMarketPane().setAlignment((Pos.TOP_LEFT));
        //layer1.getFormPane().setAlignment(Pos.CENTER_LEFT);
        //layer1.getFormPane().setPadding(new Insets(0,0,0,50));
        mainPane.getChildren().clear();
        //
        // mainPane.setAlignment(Pos.TOP_LEFT);
        // StackPane contains layer0 & layer1 that are Objects, need to use getPane().
        mainPane.getChildren().addAll( layer0.getMarketPane(), layer1);
    }

    public static class EcoPurchase {

        private static void layoutWebView() {
            LOGGER.debug("layoutWebView called");
            EcoPane ePane = new EcoPane();
            mainPane.getChildren().add(ePane.getEcoPane());
        }

        public static void purchaseAction(ArrayList<HashMap<String, String>> cartList) {
            layoutWebView();
        }

    }


    public StackPane getConsumerPane() {
        return this.mainPane;
    }

    public void onClose() {
        LOGGER.debug("ConsumerPane.onClose() called");
    //    this.CLASS_INSTANCE = null;
    //    DeckMarketPane.getInstance().onClose();
    }

}
