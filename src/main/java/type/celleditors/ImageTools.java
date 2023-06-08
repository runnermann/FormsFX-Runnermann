package type.celleditors;

import fileops.BaseInterface;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import uicontrols.ButtoniKon;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * Class provides the popup to modify an image. Uses the SectionEditor Reference
 * in order to show the modifications in the RightPane.
 */
public class ImageTools implements BaseInterface {
    private final Button cropButton, rotate_L_Button, rotate_R_Button, abandButton, saveButton;
    private static ImageView iViewFmSectionEditor;
    private boolean changed;
    private static Image imageFmSectionEditor;

    private static ImageTools CLASS_INSTANCE;


    /**
     * private no args constuctor for Singleton Class
     */
    private ImageTools() {
        iViewFmSectionEditor = new ImageView();
        cropButton = ButtoniKon.getImgCropButton();
        saveButton = ButtoniKon.getImgSaveButton();
        abandButton = ButtoniKon.getImgAbandButton();
        rotate_L_Button = ButtoniKon.getRotate_L_Button();
        rotate_R_Button = ButtoniKon.getRotate_R_Button();
    }

    /**
     * Singleton Class instantiation
     * We will import files. Important to not recreate this class.
     *
     * returns the class instance. If one already exists, returns the existing
     * isntance. If not, creates a new instance.
     */
    public static synchronized ImageTools getInstance() {
        if (CLASS_INSTANCE == null) {
            CLASS_INSTANCE = new ImageTools();
        }
        return CLASS_INSTANCE;
    }

    /**
     * Check if DrawTools has been instantiated before calling
     * a method for verifying state. No need to instantiate DrawTools
     * to verify if it exists.
     * Only public static method in this Singleton Class.
     * @return true if the CLASS_INSTANCE exists.
     */
    public static boolean instanceExists() {
        return CLASS_INSTANCE != null;
    }


    /**
     * NOT USED!
     * Saved from action that calls this
     * class. Saves from Stage.onHidden()
     */
    @Override
    public boolean saveOnExit() {
        /* STUB */
        throw new UnsupportedOperationException("Called saveOnExit and saveOnExit in ImageTools is not supported.");
    }

    public void saveOnExit(SectionEditor editorRef) {
        if(changed) {
            String mime = ".png";
            editorRef.saveImageFromImageTool(imageFmSectionEditor, mime);
        }
    }

    /**
     * To be called by the class on stage.onHidden()
     * Closes this class. Clears the tree. Closes the treeWindow. Does not save
     * the current work.
     */
    @Override
    public void onClose() {
        iViewFmSectionEditor = null;
        imageFmSectionEditor = null;
        CLASS_INSTANCE = null;
    }

    public ImageView handler(ImageView iView) {
        this.iViewFmSectionEditor = iView;
        return this.iViewFmSectionEditor;
    }


    private BufferedImage crop(BufferedImage src, int x1, int y1, int x2, int y2) {
        int x = x1 > x2 ? x2 : x1;
        int y = y1 > y2 ? y2 : y1;
        return src.getSubimage(x, y, Math.abs(x2 - x1), Math.abs(y2 - y1));
    }



    // ******************* PRIVATE METHODS ****************************

    public VBox getToolBoxContainer(SectionEditor editor) {
        Pane leftSpacer = new Pane();
        Pane rightSpacer = new Pane();
        HBox.setHgrow(leftSpacer, Priority.SOMETIMES);
        HBox.setHgrow(rightSpacer, Priority.SOMETIMES);
        setButtons(editor);
//        ToolBar tBar = new ToolBar( leftSpacer, cropButton, rotate_L_Button, rotate_R_Button, new Separator( Orientation.VERTICAL ),
//                saveButton, abandButton, rightSpacer );
        ToolBar tBar = new ToolBar( leftSpacer, rotate_L_Button, rotate_R_Button, new Separator( Orientation.VERTICAL ),
                abandButton, rightSpacer );
        tBar.setStyle("-fx-background-color: TRANSPARENT");
        VBox vBox = new VBox();
        vBox.getChildren().add(tBar);
        vBox.setAlignment(Pos.CENTER);
        vBox.setId("toolBox");
        return vBox;
    }

    private void setButtons(SectionEditor editor) {
        //@TODO REMOVE ASAP
        imageFmSectionEditor = iViewFmSectionEditor.getImage();

        cropButton.setMaxWidth(Double.MAX_VALUE);
        cropButton.setOnAction(e -> cropAction());

        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setOnAction(e -> saveAction());

        abandButton.setMaxWidth(Double.MAX_VALUE);
        abandButton.setOnAction(e -> abandAction());

        rotate_L_Button.setMaxWidth(Double.MAX_VALUE);
        rotate_L_Button.setOnAction(e -> rotate_L_Action(editor, iViewFmSectionEditor.getImage()));

        rotate_R_Button.setMaxWidth(Double.MAX_VALUE);
        rotate_R_Button.setOnAction(e -> rotate_R_Action(editor, iViewFmSectionEditor.getImage()));
    }

    private void cropAction() {
        changed = true;
    }

    private void saveAction() {
        changed = true;
    }

    private void abandAction() {
        changed = true;
    }

    /**
     * Rotates image container 90 degrees clockwise
     * <p>NOTE: that the image and ImageView are reoriented
     * and their ht - wd do not change in ImageView's
     * implementation.</p>
     */
    private void rotate_R_Action(SectionEditor editor, Image image) {
        rotate(editor, image, 90);
    }

    /**
     * Rotates image container 90 degrees counter-clockwise
     * <p>NOTE: that the image and ImageView are reoriented
     *    and their ht - wd do not change in ImageView's
     *   implementation.</p>
     */
    private void rotate_L_Action(SectionEditor editor, Image image) {
        rotate(editor, image, -90);
    }

    private void rotate(SectionEditor sectionEditor, Image image, int degrees) {
        changed = true;
        final double rads = Math.toRadians(degrees);
        final double sin = Math.abs(Math.sin(rads));
        final double cos = Math.abs(Math.cos(rads));
        final int w = (int) Math.floor(image.getWidth() * cos + image.getHeight() * sin);
        final int h = (int) Math.floor(image.getHeight() * cos + image.getWidth() * sin);
        final BufferedImage rotatedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        final AffineTransform at = new AffineTransform();
        at.translate(w / 2, h / 2);
        at.rotate(rads,0, 0);
        at.translate(-image.getWidth() / 2, -image.getHeight() / 2);
        final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage source = SwingFXUtils.fromFXImage(image, null);
        rotateOp.filter(source, rotatedImage);
        Image rotated = SwingFXUtils.toFXImage(rotatedImage, null);
        this.imageFmSectionEditor = rotated;
        iViewFmSectionEditor.setImage(rotated);
        iViewFmSectionEditor.setFitHeight(rotated.getHeight());
        iViewFmSectionEditor.setFitWidth(rotated.getWidth());

        sectionEditor.setImageHelperForRPane(rotated);
    }

}
