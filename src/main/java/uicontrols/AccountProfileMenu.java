package uicontrols;

import fileops.VertxLink;
import flashmonkey.FlashMonkeyMain;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AccountProfileMenu {

      public AccountProfileMenu() {
            /* no args constructor */
      }

      public static GridPane profileMenu() {
            VBox buttonBox = new VBox(2);
            buttonBox.setPadding(new Insets(20, 0, 6, 0));
            GridPane gridPane = new GridPane();
            gridPane.setAlignment(Pos.CENTER);
            gridPane.setPadding(new Insets(20, 0, 0, 0));

            Label label = new Label("MY PROFILE MENU");
            String text = "Save on purchases and become eligible to earn money by subscribing.";
            Hyperlink howLink = new Hyperlink("How I can earn money on FlashMonkey");
            //howLink.setId("hyperlink14white");
            howLink.setTextFill(Paint.valueOf("WHITE"));
            howLink.setOnAction(e -> FlashMonkeyMain.getWebView(VertxLink.CANCEL_POLICY.getLink()));

            Hyperlink cancelLink = new Hyperlink("Cancel my subscription");
            cancelLink.setOnAction(e -> FlashMonkeyMain.getSubscriptCancelWindow());
            //cancelLink.setId("hyperlink14white");
            cancelLink.setTextFill(Paint.valueOf("WHITE"));

            Label explainer = new Label(text);
            explainer.setMaxWidth(SceneCntl.getFileSelectPaneWd() - 50);
            GridPane.setHalignment(label, HPos.CENTER);
            GridPane.setHalignment(explainer, HPos.CENTER);
            GridPane.setHalignment(howLink, HPos.CENTER);
            GridPane.setHalignment(cancelLink, HPos.CENTER);

            label.setId("label24White");
            explainer.setId("label16White");
            explainer.setWrapText(true);
            // The users profile form
            Button profileBtn = ButtoniKon.getProfileButton();
            profileBtn.setOnAction(e -> FlashMonkeyMain.getProfilePane());
            // Sign up for advanced and pay system
            Button subsciptBtn = ButtoniKon.getSubscriptStatusButton();
            subsciptBtn.setOnAction(e -> FlashMonkeyMain.getSubscribeWindow());

            gridPane.setOnKeyPressed(k -> newt(k));

            //buttonBox.getChildren().addAll(paySysBtn, subsciptBtn, profileBtn);
            buttonBox.getChildren().addAll(subsciptBtn, profileBtn);

            buttonBox.setAlignment(Pos.TOP_CENTER);
            gridPane.addRow(0, label);
            gridPane.addRow(1, explainer);
            gridPane.addRow(3, buttonBox);
            gridPane.addRow(5, howLink);
            gridPane.addRow(7, cancelLink);
            gridPane.setId("opaqueMenuPaneDark");
            return gridPane;
      }

      private static void newt(KeyEvent k) {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.F,
                    KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN);
            //public void handle(KeyEvent ke) {
            if (keyComb.match(k)) {
                  adminSecondaryAction();
                  k.consume(); // <-- stops passing the event to next node
            }
            //}
      }

      private static void adminSecondaryAction() {
//            Pane pane = new Pane();
//            pane.setPrefSize(100, 100);
//            FlashMonkeyMain.setActionWindow(new Scene(originCreatorAction()));

      }

      /**
       * Give origins power to create Campaigns, and ability to
       * see insights. :)
       * 1) Create growth QR-Code and Link
       * 2) create campaigns
       * 3) See insights
       * @return
       */
//      private static GridPane originCreatorAction() {
//            // Show existing qr codes:
//            GridPane grid = new GridPane();
//            Button existingAllBtn = new Button("all existing campaigns");
//            Button createNew = new Button("create new campaign");
//            existingAllBtn.setOnAction(e -> showExisting());
//            createNew.setOnAction(e -> create());
//
//            grid.addRow(0, existingAllBtn);
//            grid.addRow(0, createNew);
//
//            return grid;
//      }

//      private static StackPane showExisting() {
//            // show owner email
//            // show note
//            // show link
//            // show qr code
//            // Show hits
//      }
//
//      private static StackPane create() {
//
//      }

}
