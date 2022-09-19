package type.celleditors;

import flashmonkey.CreateFlash;
import flashmonkey.FlashMonkeyMain;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.celltypes.TextCell;
import uicontrols.UIColors;


public class HashTagEditorPopup {

    // THE LOGGER
    //private static final Logger LOGGER = LoggerFactory.getLogger(HashTagEditorPopup.class);
    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(HashTagEditorPopup.class);
    private int w;
    private int h;


    private static HashTagEditorPopup CLASS_INSTANCE;

    private HashTagEditorPopup() {
        h = 300;
        w = 400;
    }

    /**
     * Singleton class instantiation. There
     * should only be one instance of HashTagEditorPopup
     * Synchronized
     *
     * @return The class instance
     */
    public static synchronized HashTagEditorPopup getInstance() {
        if(CLASS_INSTANCE == null) {
            CLASS_INSTANCE = new HashTagEditorPopup();
        }

        return CLASS_INSTANCE;
    }

    public static boolean instanceExists() {
        return CLASS_INSTANCE != null;
    }

    public void popup(SectionEditor editor) {
        handler(editor);
    }

    private void handler(SectionEditor editor) {
        CreateFlash.getInstance().setFlashListChanged(true);
        CreateFlash.getInstance().disableButtons();

        String text = editor.tCell.getTextArea().getText();
        TextCell textCell = new TextCell();

        VBox vBox = new VBox(textCell.buildCell(text, "Make this image searchable. Please enter key words for this image or media", false, 0));
        //tCell.getTextArea().setFocusTraversable(false);
        textCell.getTextArea().requestFocus();
        textCell.getTextArea().setEditable(true);

        Scene scene = new Scene(vBox);
        Stage window = new Stage();
        window.setTitle("Add keywords about this image.");
        window.setOnCloseRequest(e -> {
            String str = textCell.getTextArea().getText();
            editor.tCell.getTextArea().setText(str);

            CreateFlash.getInstance().enableButtons();
 //           CLASS_INSTANCE = null;
        });
        window.setScene(scene);
        window.setWidth(w);
        window.setHeight(h);
        window.show();
    }

//    public static void onClose() {
//        if(null != CLASS_INSTANCE) {
//            //editor.setText(tCell.getTextArea().getText());
//            FlashMonkeyMain.getPrimaryWindow().hide();
//            CreateFlash.getInstance().enableButtons();
//            CLASS_INSTANCE = null;
//        }
//    }


}
