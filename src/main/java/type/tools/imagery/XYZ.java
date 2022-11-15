package type.tools.imagery;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Class for image zoom and pan within a pane.
 */
public class XYZ {

      private AnchorPane ap;
//      private static ImageView imgView;

      private char markTxt = 'A';
      private static boolean isSet = false;


//      public void start(Stage primaryStage) throws Exception {
//            ap = new AnchorPane();
//            Image img = new Image("https://upload.wikimedia.org/wikipedia/commons/d/dc/Medical_X-Ray_imaging_SEQ07_nevit.jpg");
//            imgView = new ImageView(img);
//
//            ScrollPane sp = new ScrollPane(imgView);
//            AnchorPane.setTopAnchor(sp, 0.0);
//            AnchorPane.setLeftAnchor(sp, 0.0);
//            AnchorPane.setBottomAnchor(sp, 0.0);
//            AnchorPane.setRightAnchor(sp, 0.0);
//
//            ap.getChildren().add(sp);
//
//            imgView.setOnMouseClicked(this::onClick);
//            imgView.setOnScroll(this::imageScrolled);
//            Scene scene = new Scene(ap);
//            primaryStage.setTitle("Zoom Image");
//            primaryStage.setScene(scene);
//            primaryStage.show();
//      }

      public void onClick(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY) {
                  placeMarker(event.getSceneX(), event.getSceneY());
            }
      }

//      private void imageScrolled(ScrollEvent event) {
//            // When holding CTRL mouse wheel will be used for zooming
//            if (event.isControlDown()) {
//                  double delta = event.getDeltaY();
//                  double adjust = delta / 1000.0;
//                  double zoom = Math.min(10, Math.max(0.1, imgView.getScaleX() + adjust));
//                  setImageZoom(zoom);
//                  event.consume();
//            }
//      }

      private void placeMarker(double sceneX, double sceneY) {
            Circle circle = new Circle(2);
            circle.setStroke(Color.RED);
            circle.setTranslateY(-12);
            Label marker = new Label(String.valueOf(markTxt), circle);
            marker.setTextFill(Color.RED);
            markTxt++;
            Point2D p = ap.sceneToLocal(sceneX, sceneY);
            AnchorPane.setTopAnchor(marker, p.getY());
            AnchorPane.setLeftAnchor(marker, p.getX());
            ap.getChildren().add(marker);
      }


      private static double anchorX;
      private static double anchorY;
      private static double zeroX;
      private static double zeroY;
      private static double deltaX;
      private static double deltaY;
      private static double w;
      private static double h;
      private static double viewWd;
      private static double viewHt;
      private static double deltaScroll;
      private static Image image2;

      public static ImageView zoomImage(ScrollEvent e, ImageView v, Image i) {
            double scale = getMouseScrollDelta(e, v);
            w = i.getWidth() * scale;
            h = i.getHeight() * scale;
            image2 = new Image(i.getUrl(), w, h, true, true);
            v.setImage(image2);
            return v;
      }

      public static double getMouseScrollDelta(ScrollEvent e, ImageView imgV) {
            deltaScroll += e.getDeltaY();
            double adjust = deltaScroll / 1000.0;
            double zoom = Math.min(10, Math.max(0.1, imgV.getScaleX() + adjust));
            e.consume();
            return zoom;
      }

      public static void set(MouseEvent mouse, Image i,
                             double viewWidth, double viewHeigth) {
                  anchorX = mouse.getSceneX();
                  anchorY = mouse.getSceneY();
                  viewWd = viewWidth;
                  viewHt = viewHeigth;
                  mouse.consume();
      }

      public static Rectangle2D drag(MouseEvent e) {
            double x = e.getSceneX();
            double y = e.getSceneY();
            deltaX = anchorX - x;
            deltaY = anchorY - y;
            e.consume();
            return new Rectangle2D( deltaX + zeroX, deltaY + zeroY, viewWd, viewHt);
      }

      public static void release() {
            zeroX += deltaX;
            zeroY += deltaY;
      }
}
