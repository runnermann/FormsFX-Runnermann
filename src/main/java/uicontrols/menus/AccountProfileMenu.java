package uicontrols.menus;

import fileops.VertxLink;
import flashmonkey.FlashMonkeyMain;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import media.sound.SoundEffects;
import uicontrols.ButtoniKon;
import uicontrols.SceneCntl;

public class AccountProfileMenu extends Menu {

      public AccountProfileMenu() {
            super();
            init();
      }

      @Override
      public GridPane getGridPane() {
            setMenu();
            return gridPane;
      }

      private void setMenu() {
            label.setText("SETTINGS");
            //subLabel.setText("Profile related operations.");

            Hyperlink howLink = new Hyperlink("How I can earn pay on FlashMonkey");
            howLink.setTextFill(Paint.valueOf("WHITE"));
            howLink.setOnAction(e -> {
                  SoundEffects.PRESS_BUTTON_COMMON.play();
                  FlashMonkeyMain.getWebView(VertxLink.CANCEL_POLICY.getLink());
            });

            Hyperlink logoutLink = new Hyperlink("LOG OUT");
            logoutLink.setOnAction(e -> {
                  SoundEffects.PRESS_BUTTON_COMMON.play();
                  FlashMonkeyMain.logOutAction();
            });
            logoutLink.setTextFill(Paint.valueOf("WHITE"));

            // The users profile form
            Button profileBtn = ButtoniKon.getProfileButton();
            profileBtn.setOnAction(e -> {
                  SoundEffects.PRESS_BUTTON_COMMON.play();
                  FlashMonkeyMain.getProfilePane();
            });

            buttonBox.getChildren().addAll(profileBtn);

            GridPane.setHalignment(howLink, HPos.CENTER);
            GridPane.setHalignment(logoutLink, HPos.CENTER);

            gridPane.addRow(5, howLink);
            gridPane.addRow(7, logoutLink);
      }

}
