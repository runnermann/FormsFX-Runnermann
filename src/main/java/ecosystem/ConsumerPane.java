package ecosystem;

import flashmonkey.CreateFlash;
import flashmonkey.FlashMonkeyMain;
import forms.DeckSearchPane;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Contains the layers for the search on layer 1,
 * and beneath are the market and teaser panes.
 */
public class ConsumerPane extends StackPane {

      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ConsumerPane.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerPane.class);
      /* VERSION */
      public static final long VERSION = FlashMonkeyMain.VERSION;
      /* SINGLETON */
      private static ConsumerPane CLASS_INSTANCE = null;

      static StackPane mainStackPane;
      private AnchorPane layer2Anchor; // Search
      private DeckSearchPane searchPane;
      private DeckMarketPane layer1; // everything else


      /**
       * Closes this pane and calls searchPane.onClose,
       * and sets the class_instance to null.
       */
      public void onClose() {
            searchPane.onClose();
            LOGGER.debug("ConsumerPane.onClose() called");
            if (layer1 != null) {
                  layer1.onClose();
                  layer1 = null;
                  layer2Anchor = null;
            }
            mainStackPane.getChildren().clear();
            mainStackPane = null;
            CLASS_INSTANCE = null;
      }


      // Double-checked locking for singleton class
      public static synchronized ConsumerPane getInstance() {
            if (CLASS_INSTANCE == null) {
                  synchronized (ConsumerPane.class) {
                        if (CLASS_INSTANCE == null) {
                              CLASS_INSTANCE = new ConsumerPane();
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
            //LOGGER.setLevel(Level.DEBUG);
            mainStackPane = new StackPane();
            searchPane = new DeckSearchPane();
            layoutSearch();
      }


      private void layoutSearch() {
            LOGGER.debug("layoutSearch called");
            mainStackPane.getChildren().clear();
            //AnchorPane.setTopAnchor(searchPane.getMainPane(), 0.0);
            //AnchorPane.setLeftAnchor(searchPane.getMainPane(), 0.0);
            // set 20 from left and 50 from top
            //layer1.getFormPane().setPadding(new Insets(20,0,0,50));
            //mainStackPane.getChildren().add(layer2Anchor);
            mainStackPane.getChildren().add(searchPane.getMainGridPane());
      }


      public void layoutConsumer() {
            LOGGER.debug("layoutConsumer called");

            mainStackPane.setAlignment(Pos.TOP_LEFT);
            BorderPane bp = new BorderPane(searchPane.getMainGridPane());
            layer2Anchor = new AnchorPane(bp);
            AnchorPane.setTopAnchor(bp, 28.0);
            AnchorPane.setLeftAnchor(bp, 42.0);
            layer2Anchor.setMaxWidth(600);
            // EcoPurchase ep = new EcoPurchase();
            layer1 = DeckMarketPane.getInstance(); // everything else
            //layer0.getMarketPane().setAlignment((Pos.TOP_LEFT));
            //layer1.getFormPane().setAlignment(Pos.CENTER_LEFT);
            //layer1.getFormPane().setPadding(new Insets(0,0,0,50));
            mainStackPane.getChildren().clear();
            //
            // mainPane.setAlignment(Pos.TOP_LEFT);
            // StackPane contains layer0 & layer1 that are Objects, need to use getPane().
            mainStackPane.getChildren().addAll(layer1.getMarketPane(), layer2Anchor);
      }

      public static class EcoPurchase {
            // Set on layer above visible layers.
            private static void layoutWebView(WebEcoPane eco) {
                  LOGGER.debug("layoutWebView called");
                  WebEcoPane ePane = eco;

                  BorderPane bp = ePane.getPurchasePane();
                  AnchorPane layer3 = new AnchorPane(bp);
                  //DeckMarketPane dmpInstance = DeckMarketPane.getInstance();
                  AnchorPane.setTopAnchor(bp, 30.0);
                  AnchorPane.setLeftAnchor(bp, 370.0);
                  // @TODO setmaxheigth to be responsive
                  //bp.setMaxHeight(370);
                  //layer3.setMaxHeight(370);
                  mainStackPane.getChildren().add(layer3);
            }

            public static void purchaseAction(ArrayList<HashMap<String, String>> cartList) {
                  if (cartList.size() > 0) {
                        WebEcoPane ePane = new WebEcoPane();
                        ePane.setDeckIds(getDeckIds(cartList));
                        ePane.setCartList(cartList);
                        layoutWebView(ePane);
                  } else {
                        String errorMessage = " Please select a deck for your purchase";
                        CreateFlash.getInstance().metaAlertPopup(errorMessage);
                  }
            }

            private static String[] getDeckIds(ArrayList<HashMap<String, String>> cartList) {
                  String[] idAry = new String[cartList.size()];
                  for (int i = 0; i < cartList.size(); i++) {
                        idAry[i] = cartList.get(i).get("deck_id");
                  }
                  return idAry;
            }
      }

      public static class EcoReqSubscription {
            private static void layoutWebView() {
                  LOGGER.debug("layoutWebView called");
                  WebEcoPane ePane = new WebEcoPane();
                  mainStackPane.getChildren().add(ePane.getReqSubscribePane());
            }

            public static void reqSubscription() {
                  layoutWebView();
            }
      }


      public StackPane getConsumerPane() {
            return mainStackPane;
      }
}
