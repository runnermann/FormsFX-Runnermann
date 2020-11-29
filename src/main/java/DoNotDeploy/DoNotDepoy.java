package DoNotDeploy;


//import com.sun.istack.internal.NotNull;
import fmannotations.FMAnnotations;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;


/**
 * This class is for testing purposes only. It holds referances to Text and Canvas cells
 * which are layered and set during read tests and are layered. The alternative to
 * this class is using computationally heavy recursion or graphs. More practical to
 * simply remove methods and classes which should not exist before deployment using
 * annotations.
 * @Author Lowell Stadelman
 */
@FMAnnotations.DoNotDeployType
public class DoNotDepoy {

    @FMAnnotations.DoNotDeployField
    private static Image canvasCellImageRef;

    @FMAnnotations.DoNotDeployField
    private static TextArea textCelltextAreaRef;


    /**
     * Returns the Canvas Cell image referance
     * <p>Assumes CanvasCell has set this field
     * prior to use.</p>
     * @return
     */
    @FMAnnotations.DoNotDeployMethod
    public static Image getCanvasCellImageRef() {
        return canvasCellImageRef;
    }

    public static void setCanvasCellImageRef(Image canvasImgRef) {
        canvasCellImageRef = canvasImgRef;
    }

    /**
     * Returns the TextCell textArea referance
     * <p>Assumes TextCell has set this field</p>
     * @return
     */
    @FMAnnotations.DoNotDeployMethod
    public static TextArea getTextCelltextAreaRef() {
        return textCelltextAreaRef;
    }

    @FMAnnotations.DoNotDeployMethod
    public static void setTextCelltextAreaRef(TextArea textCelltextAreaRef) {
        DoNotDepoy.textCelltextAreaRef = textCelltextAreaRef;
    }



}
