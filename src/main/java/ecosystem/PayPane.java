package ecosystem;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.transform.Rotate;
import uicontrols.ButtoniKon;
import uicontrols.SceneCntl;


public class PayPane {

      public PayPane() {
            /* no args contstructor */
      }

      public static StackPane getPayPane() {
            Pane lowerPane = new Pane();
            BorderPane bp = new BorderPane();
            VBox v = comingSoonPane();
            v.setTranslateX(120);
            v.setTranslateY(200);
            lowerPane.getChildren().add(getMainPane());
            Pane anchor = new Pane();
            bp.setCenter(v);
            anchor.getChildren().add(bp);
            AnchorPane.setLeftAnchor(bp, 10.0);
            AnchorPane.setBottomAnchor(bp, 100.0);
            StackPane s = new StackPane(lowerPane, anchor);

            return s;
      }

      private static GridPane getMainPane() {
            GridPane gPane = new GridPane();
            gPane.setPadding(new Insets(0, 0, 16, 0));
            gPane.setPrefHeight(SceneCntl.getFormBox().getHt());
            gPane.setPrefWidth(SceneCntl.getFormBox().getWd());

            Image iGotPaid = new Image("image/i_got_paid.png");
            ImageView iView = new ImageView(iGotPaid);
            String str = "You just earned $42.89 from \"Test Card Deck 123\". " +
                "The amount has been added to your FlashMonkey wallet." +
                "Thank you for your contributions to your peers.";
            String payDateStr = "Your next pay date is on Friday May 7, 2021";
            Hyperlink seePayLink = new Hyperlink("How does pay work?");
            seePayLink.setId("signInHyp");
            Label label = new Label(str);
            Label payDateLabel = new Label(payDateStr);
            label.setId("label16Blue");
            payDateLabel.setId("label16Blue");

            Button shareBtn = ButtoniKon.getShareButton();
            Button walletBtn = ButtoniKon.getWalletButton();

            HBox btnBox = new HBox(4);
            btnBox.setPadding(new Insets(16, 0, 0, 0));
            btnBox.getChildren().addAll(shareBtn, walletBtn);
            btnBox.setAlignment(Pos.CENTER);

            gPane.setAlignment(Pos.CENTER);
            gPane.addRow(0, iView);
            GridPane.setHalignment(iView, HPos.CENTER);
            gPane.addRow(1, label);
            GridPane.setHalignment(label, HPos.CENTER);
            gPane.addRow(2, payDateLabel);
            GridPane.setHalignment(payDateLabel, HPos.CENTER);
            gPane.addRow(3, seePayLink);
            GridPane.setHalignment(seePayLink, HPos.CENTER);
            gPane.addRow(4, btnBox);
            GridPane.setHalignment(btnBox, HPos.CENTER);
            return gPane;
      }

	private static VBox comingSoonPane() {
		// Light source
		Light.Distant light = new Light.Distant(45.0, 80.0, Color.ORANGERED);
		// Light effect
		Lighting effect = new Lighting();
		effect.setLight(light);
		effect.setSurfaceScale(2.0);

		Text t = new Text("Coming Soon\n");
		t.setFill(Color.ORANGE);
		t.setFont(Font.font("null", FontWeight.BOLD, 36));
		t.setBoundsType(TextBoundsType.VISUAL);
		t.setEffect(effect);
		t.setTextAlignment(TextAlignment.CENTER);

		Text t1 = new Text("This page will inform you \nwhen you've been paid");
		t1.setFill(Color.ORANGE);
		t1.setFont(Font.font("null", FontWeight.BOLD, 24));
		t1.setBoundsType(TextBoundsType.VISUAL);
		t1.setTextAlignment(TextAlignment.CENTER);
		t1.setEffect(effect);

		VBox b = new VBox(10);
		b.getChildren().addAll(t, t1);
		b.setStyle("-fx-background-color: #ffffff95");
		b.setOpacity(60);
		b.setPadding(new Insets(20,20,20,20));
		b.setAlignment(Pos.CENTER);

		//Creating the rotation transformation
		Rotate rotate = new Rotate();
		//Setting pivot points for the rotation
		rotate.setPivotX(120);
		rotate.setPivotY(100);
		rotate.setAngle(30);
		//Adding the transformation
		b.getTransforms().addAll(rotate);
		return b;
	}


}
