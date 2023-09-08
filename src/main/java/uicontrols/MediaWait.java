package uicontrols;

import flashmonkey.FMTransition;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class MediaWait {

    public static FadeTransition play;
    public static TranslateTransition playLeft;

    private MediaWait() {
        /* empty constructor */
    }

    public static StackPane getPreBlur(final double wd) {

        final String imgStr = "image/Picture10.png";
        final Image img = new Image(imgStr);
        final ImageView imgView = new ImageView(img);
        imgView.setPreserveRatio(true);
        imgView.setFitWidth(wd);

        int ht = (int) imgView.fitHeightProperty().get();
        ht = ht == 0 ? 90 : ht;

        final GaussianBlur blur = new GaussianBlur();
        blur.setRadius(2);

        final Pane gradientPane = getGradientPane(ht);
        // animation
        transitionFmLeft(gradientPane);
        setPlayLeft(transitionFmLeft(gradientPane));

        FontIcon rIcon = new FontIcon(FontAwesomeSolid.CAMERA);
        rIcon.setIconSize(40);
        rIcon.setFill(Color.web(UIColors.FM_GREY));

        final Label lbl = new Label("v", rIcon);
        lbl.setPadding(new Insets(24, 0, 0, 24));
        final HBox lblPane = new HBox(lbl);
        lblPane.setViewOrder(-10);
        lblPane.setLayoutX(-90);
        lblPane.setMinHeight(ht);
        lblPane.setMaxHeight(ht);

        final HBox blurBox = new HBox();
        blurBox.setPrefSize(wd, ht);
        blurBox.getChildren().add(imgView);
        blurBox.setEffect(blur);

        play = transitionAppear(blurBox);

        final StackPane stack = new StackPane();
        stack.getChildren().addAll(blurBox, gradientPane, lblPane);
        stack.setMaxWidth(wd);
        stack.setMaxHeight(ht);
        stack.setAlignment(Pos.CENTER);
        stack.setClip(new Rectangle(wd, ht));

        play.play();
        playLeft.play();

        return stack;
    }

    public static void setPlayLeft(TranslateTransition trx) {
        playLeft = trx;
    }

    public static TranslateTransition transitionFmLeft(Node node) {
        // Transition the textframe
        final TranslateTransition trans = new TranslateTransition(Duration.millis(900), node);
        trans.setFromX(100f);
        trans.setToX(-300f);
        trans.setCycleCount(TranslateTransition.INDEFINITE);
        trans.setAutoReverse(false);

        return trans;
    }

    public static FadeTransition transitionAppear(Node node) {
        // Transition the textframe
        FadeTransition trax = new FadeTransition(Duration.millis(300));
        trax.setNode(node);
        trax.setFromValue(1.0);
        trax.setToValue(.4);
        trax.setCycleCount(Animation.INDEFINITE);
        trax.setAutoReverse(true);

        return trax;
    }

    private static HBox getGradientPane(int ht) {
        final HBox gradientPane = new HBox();
        gradientPane.setId("gradient-grey");
        gradientPane.setMinWidth(300);
        gradientPane.setMinHeight(ht);
        gradientPane.setMaxHeight(ht);
        return gradientPane;
    }


}
