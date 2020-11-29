package draw.shapes;

import uicontrols.UIColors;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import type.celleditors.DrawTools;
import type.celleditors.SectionEditor;

import java.util.ArrayList;

/**
 * For predefined polygons
 */
public class PolygonBuilder extends GenericBuilder<FMPolygon, PolygonBuilder> {
	
	// The mouse start point
	private double anchorX = 0.0;
	private double anchorY = 0.0;
	
	// Difference from a mouse to a point
	private double deltaX = 0.0;
	private double deltaY = 0.0;
	
	private double initMouseX = 0.0;
	private double initMouseY = 0.0;
	
	/**
	 * No args constructor
	 */
	public PolygonBuilder() { /* no args constructor */}
	
	
	/**
	 * Basic constructor: Sets this shape in the parent GenericBuilder.
	 * @param c
	 * @param graphC
	 * @param pane
	 * @param editor
	 */
	public PolygonBuilder(Canvas c, GraphicsContext graphC, Pane pane, SectionEditor editor) {
		super(c, graphC, pane, editor);
	}
	
	/**
	 * /**
	 *      * Basic constructor: Sets this shape in the parent GenericBuilder,
	 *      * creates a builder object using an already existing FMPolygon.
	 *      * Called by getBuilder(...) in FMTriangle.
	 * @param fmPoly
	 * @param c
	 * @param graphC
	 * @param pane
	 * @param editor
	 */
	public PolygonBuilder(FMPolygon fmPoly, Canvas c, GraphicsContext graphC, Pane pane, SectionEditor editor) {
		super(c, graphC, pane, editor);
		
		this.anchorX = fmPoly.getX();
		this.anchorY = fmPoly.getY();
		
		Polygon fxPoly = fmPoly.getShape();
		// add mouse actions to the shape when constuctor is called
		// to edit existing shapes
		fxPoly.setOnMousePressed( f -> shapePressed(f, fmPoly, fxPoly));
		fxPoly.setOnMouseDragged( f -> shapeDragged(f, fmPoly, fxPoly));
		fxPoly.setOnMouseReleased( f -> shapeReleased( fmPoly, fxPoly));
		
		getOverlayPane().getChildren().add( fxPoly);
		
		setNewShape(true); // Not sure if it should be true or false ...
		
	}
	
	@Override
	void setAnchors(MouseEvent mouse) {
		anchorX = mouse.getSceneX();
		anchorY = mouse.getSceneY();
	}
	
	@Override
	protected ArrayList<Circle> getHandles(GenericShape gs, Shape thisShape) {
		return null;
	}
	
	@Override
	protected Shape editableShapeAction(Shape shape, GenericShape gs) {
		return null;
	}
	
	@Override
	public void zeroDeltas() {
		deltaX = 0.0;
		deltaY = 0.0;
	}
	
	/**
	 * CopyPaste interface copyAction
	 * creates a new FMCircle using the mouse current X and Y, and the Shape's current
	 * radiusX and radiusY, adds it to gbcopyArrayOfFMShapes, then clears
	 * the nodes.
	 * @param mouse
	 * @param gs
	 */
	@Override
	public void copyAction(MouseEvent mouse, GenericShape gs) {
	
	}
	
	/**
	 * For pre-defined polygons
	 * @param e
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		
		Canvas can = getCanvas();
		GraphicsContext gC = getGc();
		
		gC.setStroke(Color.web(UIColors.HIGHLIGHT_PINK));
		DrawTools draw = DrawTools.getInstance();
		
		deltaX = e.getSceneX() - anchorX;
		deltaY = e.getSceneY() - anchorY;
		
		if (draw.getShapeNotSelected() && e.isAltDown()) {
			setNewShape(true);
			
			getGc().clearRect(can.getBoundsInLocal().getMinX(), can.getBoundsInLocal().getMinY(),
					can.getBoundsInLocal().getWidth(), can.getBoundsInLocal().getHeight());
		}
		
		
		
		
	}
	
	@Override
	public void mouseReleased(MouseEvent mouse) {
	
	}
	
	@Override
	public void shapePressed(MouseEvent mouse, GenericShape gs, Shape s) {
	
	}
	
	@Override
	public void shapeDragged(MouseEvent mouse, GenericShape gs, Shape s) {
	
	}
	
	@Override
	public void shapeReleased(GenericShape gs, Shape shape) {
	
	}
	
	@Override
	public void verticyPressed(MouseEvent mouse, Shape s) {
	
	}
	
	@Override
	public void verticyHDragged(MouseEvent mouse, GenericShape gs, Shape vertV, Shape vertVOther, Shape vertH1, Shape vertH2, Shape shape) {
	
	}
	
	@Override
	public void verticyVDragged(MouseEvent mouse, GenericShape gs, Shape vertV, Shape vertVOther, Shape vertH1, Shape vertH2, Shape shape) {
	
	}
}
