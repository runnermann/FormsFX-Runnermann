package uicontrols.menus;

import fileops.VertxLink;
import flashmonkey.FlashMonkeyMain;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import uicontrols.ButtoniKon;

public class PayAcctMenu extends Menu {

    public PayAcctMenu() {
        super();
        init();
    }

    @Override
    public GridPane getGridPane() {
        setMenu();
        return gridPane;
    }

    private void setMenu() {
        label.setText("EARN");
        subLabel.setText("Save on purchases and become eligible to earn pay.");

        // Sign up for advanced and pay system
        Button subsciptBtn = ButtoniKon.getSubscriptStatusButton();
        subsciptBtn.setOnAction(e -> FlashMonkeyMain.getSubscribeWindow());

        Hyperlink howLink = new Hyperlink("How I can earn pay on FlashMonkey");
        howLink.setTextFill(Paint.valueOf("WHITE"));
        howLink.setOnAction(e -> FlashMonkeyMain.getWebView(VertxLink.CANCEL_POLICY.getLink()));

        Hyperlink cancelLink = new Hyperlink("Cancel my subscription");
        cancelLink.setOnAction(e -> FlashMonkeyMain.getSubscriptCancelWindow());
        cancelLink.setTextFill(Paint.valueOf("WHITE"));

        buttonBox.getChildren().addAll(subsciptBtn);

        GridPane.setHalignment(howLink, HPos.CENTER);
        GridPane.setHalignment(cancelLink, HPos.CENTER);

        gridPane.addRow(5, howLink);
        gridPane.addRow(7, cancelLink);
    }
}
