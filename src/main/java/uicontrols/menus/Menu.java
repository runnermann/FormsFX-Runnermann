package uicontrols.menus;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import uicontrols.SceneCntl;

public abstract class Menu {

    VBox buttonBox;
    GridPane gridPane;// = new GridPane();
    Label label;// = new Label("MY PROFILE MENU");
    Label subLabel;

    Menu() {
        buttonBox = new VBox(2);
        gridPane = new GridPane();
        label = new Label("MENU");
        subLabel = new Label();
    }

    void init() {
        initializeParts();
    }


    // ***** ABSTRACT METHODS *****
    public abstract GridPane getGridPane();



    public void initializeParts() {
        buttonBox.setPadding(new Insets(20, 0, 6, 0));

        subLabel.setMaxWidth(SceneCntl.getFileSelectPaneWd() - 50);
        GridPane.setHalignment(label, HPos.CENTER);
        GridPane.setHalignment(subLabel, HPos.CENTER);


        label.setId("label24White");
        subLabel.setId("label16White");
        subLabel.setWrapText(true);

        buttonBox.setAlignment(Pos.TOP_CENTER);

        gridPane.setId("opaqueMenuPaneDark");
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(20, 0, 0, 0));
        gridPane.setOnKeyPressed(k -> newt(k));
        gridPane.addRow(0, label);
        gridPane.addRow(1, subLabel);
        gridPane.addRow(3, buttonBox);
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
}
