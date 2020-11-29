package type;

import draw.shapes.*;
import javafx.scene.input.MouseEvent;

/**
 *
 *
 *     @author Lowell Stadelman
 */
public interface CopyPasteInterface<T extends GenericShape>
{

    void copyAction(MouseEvent mouse, GenericShape gs);

    void pasteAction(MouseEvent mouse);

    //Shape editableShapeAction(Shape shape, GenericShape gs);

}
