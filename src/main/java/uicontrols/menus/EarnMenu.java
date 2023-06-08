package uicontrols.menus;

import fileops.VertxLink;
import flashmonkey.FlashMonkeyMain;
import flashmonkey.StatsPane;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import media.sound.SoundEffects;
import uicontrols.ButtoniKon;

public class EarnMenu extends Menu {

    public EarnMenu() {
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
        subLabel.setText("Earn pay and gain credibility.");

        // Sign up for advanced and pay system
        Button subsciptBtn = ButtoniKon.getSubscriptStatusButton();
        subsciptBtn.setOnAction(e -> {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            FlashMonkeyMain.getSubscribeWindow();
        });
        // Stats Pane
        Button statsBtn = ButtoniKon.getStatsPaneButton();
        statsBtn.setOnAction(e -> {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            earnButtonAction();
        });

        Hyperlink howLink = new Hyperlink("How I can earn pay on FlashMonkey");
        howLink.setTextFill(Paint.valueOf("WHITE"));
        howLink.setOnAction(e -> {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            FlashMonkeyMain.getWebView(VertxLink.CANCEL_POLICY.getLink());
        });

        Hyperlink cancelLink = new Hyperlink("Cancel my subscription");
        cancelLink.setOnAction(e -> {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            FlashMonkeyMain.getSubscriptCancelWindow();
        });
        cancelLink.setTextFill(Paint.valueOf("WHITE"));

        buttonBox.getChildren().addAll(subsciptBtn, statsBtn);

        GridPane.setHalignment(howLink, HPos.CENTER);
        GridPane.setHalignment(cancelLink, HPos.CENTER);

        gridPane.addRow(5, howLink);
        gridPane.addRow(7, cancelLink);
    }

    private void earnButtonAction() {
        FlashMonkeyMain.getStatsWindow();
    }

}
