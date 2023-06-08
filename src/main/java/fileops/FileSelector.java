package fileops;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileSelector {

    /**
     * Popup for the file chooser.
     * @param imgFile The actual file name
     * @param suggestName The File chooser suggested name.
     */
    public static void saveQRImagePopup(File imgFile, String suggestName) {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save QR-Code");
        fileChooser.getExtensionFilters().addAll(new javafx.stage.FileChooser.ExtensionFilter("png", "*.png"));
        fileChooser.setInitialFileName(suggestName);
        File dest = fileChooser.showSaveDialog(stage);
        if(dest != null) {
            try {
                Files.copy(imgFile.toPath(), dest.toPath(), REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
