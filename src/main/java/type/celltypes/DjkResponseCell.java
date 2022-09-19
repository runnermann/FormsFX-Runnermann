package type.celltypes;

import flashmonkey.FlashMonkeyMain;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.tools.calculator.ExpNode;
import type.tools.calculator.Operator;
import uicontrols.UIColors;

import java.io.Serializable;

/**
 * <p>Note: This class is serializable as aare all cells</p>
 * <p>DjkResponseCell relates to math. It contains the UI response from
 * the Dijkstra's shunting algorithm. It is independent from operations
 * of the class. This class makes use of Operator, an Enum.</p>
 */
public class DjkResponseCell extends GenericCell implements Serializable {
      private static final long serialVersionUID = FlashMonkeyMain.VERSION;
      private static final Logger LOGGER = LoggerFactory.getLogger(CanvasCell.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(CanvasCell.class);

      public DjkResponseCell() {
            /* no args constructor */
      }


      public final ScrollPane buildCell(String response, double paneWd, double paneHt, int numSections) {
            VBox vBox = new VBox(2);
            ScrollPane scrollPane = new ScrollPane();

            scrollPane.setContent(vBox);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setFitToWidth(true);
            return scrollPane;
      }


}
