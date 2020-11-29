package type;

import draw.shapes.FMCircle;
import draw.shapes.FMRectangle;
import draw.shapes.GenericShape;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;
import type.celleditors.SectionEditor;

/**
 * @author Lowell Stadelman
 */
public interface FMMouseInterface//<T extends GenericShape<T>>
{
    void deleteButtonAction(Shape s, GenericShape gs);

    void mousePressed(MouseEvent mouse);

    void mouseDragged(MouseEvent mouse);

    void mouseReleased(MouseEvent mouse);

    void shapePressed(MouseEvent mouse, GenericShape gs, Shape s);

    void shapeDragged(MouseEvent mouse, GenericShape gs, Shape s);

    void shapeReleased(GenericShape gs, Shape shape);

    void verticyPressed(MouseEvent mouse, Shape s);

    void verticyHDragged(MouseEvent mouse, GenericShape gs, Shape vertV, Shape vertVOther, Shape vertH1, Shape vertH2, Shape shape);

    void verticyVDragged(MouseEvent mouse, GenericShape gs, Shape vertV, Shape vertVOther, Shape vertH1, Shape vertH2, Shape shape);

    void verticyReleased(GenericShape gs);

}
