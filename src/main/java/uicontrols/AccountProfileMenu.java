package uicontrols;

import fileops.VertxLink;
import flashmonkey.FlashMonkeyMain;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class AccountProfileMenu {

    public AccountProfileMenu() {
        /* no args constructor */
    }

    public static GridPane profileMenu(Stage actionWindow) {
        VBox buttonBox = new VBox(2);
        buttonBox.setPadding(new Insets(20, 0, 6, 0));
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(20,0,0, 0));

        Label label = new Label("My profile menu");
        String text = "Save on purchases and become eligible to earn money by subscribing.";
        Hyperlink howLink = new Hyperlink("How I can earn money on FlashMonkey");
        //howLink.setId("hyperlink14white");
        howLink.setTextFill(Paint.valueOf("WHITE"));
        howLink.setOnAction(e -> FlashMonkeyMain.getWebView(VertxLink.CANCEL_POLICY.getLink()));

        Hyperlink cancelLink = new Hyperlink("Cancel my subscription");
        cancelLink.setOnAction(e -> FlashMonkeyMain.getFMSAccountPane());
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
        Button profileBtn = ButtoniKon.getProfileButton();
        profileBtn.setOnAction(e -> FlashMonkeyMain.getProfilePane());
        // pay system
        Button paySysBtn = ButtoniKon.getPaySysButton();
        paySysBtn.setOnAction(e -> FlashMonkeyMain.getFMSAccountPane());
        Button subsciptBtn = ButtoniKon.getSubscriptStatusButton();
        subsciptBtn.setOnAction(e -> FlashMonkeyMain.getSubscribePane());


        //buttonBox.getChildren().addAll(paySysBtn, subsciptBtn, profileBtn);
        buttonBox.getChildren().addAll(subsciptBtn, profileBtn);

        buttonBox.setAlignment(Pos.TOP_CENTER);
        gridPane.addRow(0, label);
        gridPane.addRow(1, explainer);
        gridPane.addRow(3, buttonBox);
        gridPane.addRow(5, howLink);
        gridPane.addRow(7, cancelLink);
        gridPane.setId("fileSelectPane");
        return gridPane;
    }







//    private static void showPopup() {
//        String emojiPath = "image/flashFaces_sunglasses_60.png";
//        String message   = "Oooph!" +
//                "\n I need to be connected to the cloud \n to create or change your account information.";
//        FxNotify.notificationBlue("Please Connect!", message, Pos.CENTER, 6,
//                emojiPath, FlashMonkeyMain.getWindow());
//    }
}
