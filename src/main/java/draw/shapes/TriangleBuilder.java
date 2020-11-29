package draw.shapes;

import uicontrols.UIColors;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.celleditors.DrawTools;
import type.celleditors.SectionEditor;

import java.util.ArrayList;
import java.lang.Math;

/**
 * This class creates a Triangle object.
 *
 * @author Benjamin Boyle
 */
public class TriangleBuilder extends GenericBuilder<FMTriangle, TriangleBuilder> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TriangleBuilder.class);
    
    // The mouse start point
    private double anchorX = 0.0;
    private double anchorY = 0.0;
    
    // Difference from mouse to a point
    private double deltaX = 0.0;
    private double deltaY = 0.0;
    
    private double prevMouseX = 0.0;
    private double prevMouseY = 0.0;
    
    private String fillColor;
    private String strokeColor;
    

    private double currentAngle = 0.0;

    public TriangleBuilder() {
        // no args constructor
    }
    
    /**
     * Full Constructor: Includes stroke color and fill color,
     * Sets this shape in the parent GenericBuilder
     * @param c
     * @param graphC
     * @param pane
     * @param editor
     */
    public TriangleBuilder(Canvas c, GraphicsContext graphC, Pane pane, SectionEditor editor, String strokeClr, String fillClr)
    {
        super(c, graphC, pane, editor);
        this.strokeColor = strokeClr;
        this.fillColor = fillClr;
        
        LOGGER.info("Full constructor called. ");
        
    }
    
    
    /**
     * Basic constructor: Sets this shape in the parent GenericBuilder,
     * creates a builder object using an already existing FMTriangle.
     * Called by getBuilder(...) in FMTriangle.
     * @param fmTri
     * @param c
     * @param graphC
     * @param pane
     * @param editor
     */
    public TriangleBuilder(FMTriangle fmTri, Canvas c, GraphicsContext graphC, Pane pane, SectionEditor editor)
    {
        super(c, graphC, pane, editor);
        this.anchorX = fmTri.getX();
        this.anchorY = fmTri.getY();
        
        Polygon fxPoly = fmTri.getShape();
    
        // add mouse actions to the shape when constuctor is called
        // to edit existing shapes
        fxPoly.setOnMousePressed( f -> shapePressed( f, fmTri, fxPoly));
        fxPoly.setOnMouseDragged( f -> shapeDragged( f, fmTri, fxPoly));
        fxPoly.setOnMouseReleased(f -> shapeReleased(   fmTri, fxPoly));
        
        getOverlayPane().getChildren().add( fxPoly);
    
        setNewShape(true); // Not sure if it should be true or false ...
        
        LOGGER.info("Basic constructor called");
    }

    @Override
    public void zeroDeltas() {
        deltaX = 0.0;
        deltaY = 0.0;
    }

    @Override
    public void setAnchors(MouseEvent mouse) {
        anchorX = mouse.getSceneX();
        anchorY = mouse.getSceneY();
    }





    /**
     * As the mouse is dragged, create the shape of the
     * Traingle.
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        Canvas can = getCanvas();
        GraphicsContext gC = getGc();

        gC.setStroke(Color.web(strokeColor));
        gC.setFill(Color.web(fillColor));
        
        DrawTools draw = DrawTools.getInstance();
        if (draw.getShapeNotSelected() && e.isAltDown()) {
            setNewShape(true);

            getGc().clearRect(can.getBoundsInLocal().getMinX(), can.getBoundsInLocal().getMinY(),
                    can.getBoundsInLocal().getWidth(), can.getBoundsInLocal().getHeight());

            deltaX = e.getSceneX() - anchorX;
            deltaY = e.getSceneY() - anchorY;

            double[] xList = new double[]{anchorX, anchorX + deltaX, anchorX + (deltaX / 2)};
            double[] yList = new double[]{anchorY + deltaY, anchorY + deltaY, anchorY};
            gC.strokePolygon(xList, yList, 3);
            e.consume();
        }
        else if (draw.getShapeNotSelected()) {
            setNewShape(true);

            getGc().clearRect(can.getBoundsInLocal().getMinX(), can.getBoundsInLocal().getMinY(),
                    can.getBoundsInLocal().getWidth(), can.getBoundsInLocal().getHeight());

            deltaX = e.getSceneX() - anchorX;
            deltaY = e.getSceneY() - anchorY;

            double diff = Math.sqrt(3);

            double[] xList = new double[]{anchorX + deltaX, anchorX  - (deltaY / diff), anchorX + (deltaY / diff)};
            double[] yList = new double[]{anchorY + deltaY, anchorY + (deltaX / diff), anchorY - (deltaX / diff)};
            gC.strokePolygon(xList, yList, 3);
            e.consume();

        }
    }

    @Override
    public void mouseReleased( MouseEvent mouse)
    {
        DrawTools draw = DrawTools.getInstance();
        if(draw.getShapeNotSelected())
        {

            Canvas can = getCanvas();
            GraphicsContext gC = getGc();

            gC.clearRect(can.getBoundsInLocal().getMinX(), can.getBoundsInLocal().getMinY(),
                    can.getBoundsInLocal().getWidth(), can.getBoundsInLocal().getHeight());

            System.out.println("\n *** mouseReleased in FMTriangle called *** ");

            if (isNewShape())
            {
                FMTriangle fmTri;

                if (mouse.isAltDown()) {
                    fmTri = new FMTriangle(
                            anchorX,
                            anchorY,
                            deltaX,
                            deltaY,
                            3,
                            this.strokeColor,
                            this.fillColor,
                            gbcopyArrayOfFMShapes.size() - 1
                    );
                } else {
                    double diff = Math.sqrt(3);
                    fmTri = new FMTriangle(
                            anchorX + deltaX,
                            anchorY + deltaY,
                            anchorX - (deltaY / diff),
                            anchorY + (deltaX / diff),
                            anchorX + (deltaY / diff),
                            anchorY - (deltaX / diff),
                            3,
                            this.strokeColor,
                            this.fillColor,
                            gbcopyArrayOfFMShapes.size() - 1
                    );
                }


                //LOGGER.debug("TriangleBuilder.mouseReleased x1, y1 (" +fmTri.x1 + ", " + fmTri.y1 + ") x2, y2 (" + fmTri.x2 + ") x3, y3 (" + fmTri.y2 + ", " + fmTri.x3 + " " + fmTri.y3 + ")");
                //LOGGER.debug("TriangleBuilder.mouseReleased anchorX, anchorY ( " + anchorX + ", " + anchorY + ")");
                // add shape to gbcopyArrayOfFMShapes
                gbcopyArrayOfFMShapes.add(fmTri);

                // get the shape from the saved shape
                Polygon fxTri = fmTri.getShape();

                // add mouse actions to the shape
                fxTri.setOnMousePressed( f -> shapePressed( f, fmTri, fxTri));
                fxTri.setOnMouseDragged( f -> shapeDragged( f, fmTri, fxTri));
                fxTri.setOnMouseReleased(f -> shapeReleased(   fmTri, fxTri));

                // show the shape in the overlayPane
                getOverlayPane().getChildren().add( fxTri);
                // Add the scaled version of the shape in the right pane
                int wd = (int) getOverlayPane().getWidth();
                int ht = (int) getOverlayPane().getHeight();

                //getRightPane().getChildren().add( fmCirc.getScaledShape());
                editorRef.setShapesInRtPane(gbcopyArrayOfFMShapes, wd, ht );

                setNewShape(false);
            }
        }
        DrawTools.setShapeNotSelected(true);
        mouse.consume();
    }





    /** ************************************************************************************************************ **
     *                                                                                                                 *
                                                CUT & PASTE ACTIONS
     *                                                                                                                 *
     ** ************************************************************************************************************ ***/





    /**
     * Overrides the GenericBuilder
     * Adds mouse actions to ellipses in the overlaypane
     * adds it to the overlayPane and adds it to the
     * right pane.
     * @param gs
     */
    @Override
    public Shape editableShapeAction(Shape fxTriangle, GenericShape gs) {
        //LOGGER.debug("\n*** editableShapeAction in RectBuilder ***" + "\t - THIS FMRectangle: " + ((FMRectangle) gs).toString());

        // add mouse actions to the shape
        fxTriangle.setOnMousePressed(f -> shapePressed( f, gs, fxTriangle));
        fxTriangle.setOnMouseDragged(f -> shapeDragged( f, gs, fxTriangle));
        fxTriangle.setOnMouseReleased(f -> shapeReleased(gs, fxTriangle));

        return fxTriangle;
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
    public void copyAction(MouseEvent mouse, GenericShape gs)
    {
        //LOGGER.debug("\t copyAction called in TriangleBuilder");

        FMTriangle fmTri;

        fmTri = new FMTriangle(mouse.getSceneX(), mouse.getSceneY(),
                ((FMTriangle) gs).getDeltaX(), ((FMTriangle) gs).getDeltaY(), gs.getStrokeWidth(),
                gs.getStrokeColor(), gs.getFillColor(), gbcopyArrayOfFMShapes.size() );

        gbcopyArrayOfFMShapes.add(fmTri);
        setPaste(true);
        DrawTools draw = DrawTools.getInstance();
        draw.clearNodes();

        mouse.consume();
    }


    /** ************************************************************************************************************ ***
     *                                                                                                                 *
     PANNING the shape
     and other mouse actions
     *                                                                                                                 *
     ** ************************************************************************************************************ ***/



    private ObservableList pts;
    /**
     * Gets the x and y locations of the mouse in the Shape and the pane.
     * or, if right mouse button is selected goes to shapeRightPress.
     * @param mouse The mouse MouseEvent
     */
    @Override
    public void shapePressed(MouseEvent mouse, GenericShape gs, Shape shape)
    {
        DrawTools.setShapeNotSelected(false);
        pts = ((Polygon) shape).getPoints();

        shape.setStrokeWidth(shape.getStrokeWidth() + 2);
        // Clear the resize nodes if they are present
        DrawTools draw = DrawTools.getInstance();
        draw.clearNodes();

        /** If shape right mouse click create resize nodes **/
        if(mouse.isSecondaryButtonDown()) {
            shapeRightPress(mouse, gs, shape);
        } else {
            LOGGER.info("\n\n *** mousePrimary button  in TriangleBuilder  ***");
    
            // Used for shape drag
            
            deltaX = gs.getX() - mouse.getSceneX();
            deltaY = gs.getY() - mouse.getSceneY();

            prevMouseX = mouse.getSceneX();
            prevMouseY = mouse.getSceneY();
            //LOGGER.debug("deltaX: " + deltaX + " shapeX: " + gs.getX());
            //LOGGER.debug("deltaY: " + deltaY + " shapeY: " + gs.getY());
        }
        mouse.consume();
    }
    
    
    @Override
    public void shapeDragged(MouseEvent mouse, GenericShape gs, Shape shape)
    {
        if(mouse.isPrimaryButtonDown());
        {
            // uses the observable list/reference
            // to move shape. :)
            for(int i = 0; i < pts.size()/2; i++) {
                double x = (double) pts.get(i*2);
                double y = (double) pts.get(i*2+1);
                pts.set(i*2, x + (mouse.getSceneX() - prevMouseX));
                pts.set(i*2+1, y + (mouse.getSceneY() - prevMouseY));
            }
            prevMouseX = mouse.getSceneX();
            prevMouseY = mouse.getSceneY();
        }
        mouse.consume();
    }

    /**
     * On mouse released, Updates the new location in
     * the gbcopyArrayOfFMShapes
     * @param gs
     * @param shape
     */
    @Override
    public void shapeReleased( GenericShape gs, Shape shape)
    {
        shape.setStrokeWidth(shape.getStrokeWidth() - 2);
        // Set the points in the traingle
        gs.setPoints(pts);

        // The first shape in the gbcopyArrayOfFMShapes is an FMRectangle
        // with the demensions of the original SnapShot rectangle.
        double origWd = ((FMRectangle) gbcopyArrayOfFMShapes.get(0)).getWd();
        double origHt = ((FMRectangle) gbcopyArrayOfFMShapes.get(0)).getHt();
        editorRef.setShapesInRtPane(gbcopyArrayOfFMShapes, origWd, origHt);
    }

    /** ************************************************************************************************************ ***
     *                                                                                                                 *
     *                                    RESIZING the shape ... Moving verticies                                     *
     *                                                                                                                 *
     ** ************************************************************************************************************ **/

    /**
     * Helper method, gives average of two values
     */
    private Point2D ptsAvg(Point2D... points) {
        double x = 0;
        double y = 0;
        int size = 0;
        for (Point2D point: points) {
            x += point.getX();
            y += point.getY();
            size++;
        }
        return new Point2D(Math.abs(x)/size, Math.abs(y)/size);
    }

    /**
     * Helper method.
     * Returns an gbcopyArrayOfFMShapes of Point2D x and y coordinates for the
     * center, and the outer tips of radiusX and radiusY.
     * Even numbers are horizontal verticies, odd are even.
     * 0 is an even number in this case
     * @return
     */
    //@Override
    private Point2D[] getPoints(Shape shape)
    {
        ObservableList<Double> rawPoints = ((Polygon) shape).getPoints();

        double[] points = new double[6];
        Point2D[] result = new Point2D[3];
        for(int i = 0; i < rawPoints.size(); i++) {
            points[i] = rawPoints.get(i);
        }
        for(int n = 0; n < points.length/2; n++) {
            result[n] = new Point2D(points[n * 2], points[n * 2 + 1]);
        }

        return result;
    }

    protected ArrayList<Circle> getHandles(GenericShape gs, Shape thisShape )
    {
        ArrayList<Circle> points = new ArrayList<>(3);
        Point2D[] pt = getPoints(thisShape);
        pt = getTranslatedPoints(thisShape, pt);
        Circle c;

        for(int i = 0; i < 3; i++ )
        {
            c = new Circle(pt[i].getX(), pt[i].getY(), 4, Color.web(UIColors.HIGHLIGHT_ORANGE));
            c.setOnMousePressed(e -> verticyPressed(e, thisShape));
            c.setOnMouseReleased(e -> verticyReleased(gs));
            points.add(c);
        }

        Circle unused = new Circle(0,0, 0);
        points.get(0).setOnMouseDragged(e -> verticyHDragged(e, gs, points.get(0), points.get(1), points.get(2), unused, thisShape)); // left node / H adjustment
        points.get(1).setOnMouseDragged(e -> verticyHDragged(e, gs, points.get(1), points.get(2), points.get(0), unused, thisShape)); // top node / W adjustment
        points.get(2).setOnMouseDragged(e -> verticyHDragged(e, gs, points.get(2), points.get(0), points.get(1), unused, thisShape)); // right node / H adjust

        //LOGGER.debug(" In FMRectangleBuilder() Checking point[1] " + points.get(1).toString());

        return points;
    }

    @Override
    public void verticyPressed(MouseEvent mouse, Shape s)
    {
        DrawTools.setShapeNotSelected(false);
        mouse.consume();
    }

    @Override
    public void verticyHDragged(MouseEvent mouse, GenericShape gs, Shape vertO, Shape vert2, Shape vert3, Shape unused, Shape shape)
    {
        if(! mouse.isSecondaryButtonDown() && ! mouse.isAltDown())
        {
            //System.out.println(" *** verticyH dragged *** ");

            double initialX = ((Circle) vertO).getCenterX();
            double initialY = ((Circle) vertO).getCenterY();

            ((Circle) vertO).setCenterX(mouse.getSceneX());
            ((Circle) vertO).setCenterY(mouse.getSceneY());

            Point2D[] points = getPoints(shape);
            points = getTranslatedPoints(shape, points);

            // Finds point in shape's points array and changes those positions to new vertO point values
            for (int i = 0; i < points.length; i++)
            {
                if (points[i].getX() == initialX && points[i].getY() == initialY) {
                    ((Polygon) shape).getPoints().set(i*2, mouse.getSceneX());
                    ((Polygon) shape).getPoints().set(i*2+1, mouse.getSceneY());
                }
            }
        }
        // TODO: Get rotation working properly

        else if (! mouse.isSecondaryButtonDown()) // Rotate around selected point
        {
            //LOGGER.debug(" *** Shape rotated *** ");

            Point2D[] points = getPoints(shape);
            points = getTranslatedPoints(shape, points);
            Point2D centerPoint = center(points);

            Point2D mousePosition = new Point2D(mouse.getSceneX(), mouse.getSceneY());

            double pivotX = ((Circle) vertO).getCenterX();
            double pivotY = ((Circle) vertO).getCenterY();

            double result = Math.atan2(mousePosition.getY() - pivotY, mousePosition.getX() - pivotX) -
                    Math.atan2(centerPoint.getY() - pivotY, centerPoint.getX() - pivotX);

            result = result * (180 / Math.PI);

            System.out.println(result);

            int anchorIndex = 0;
            for(int i = 0; i < points.length; i++) {
                if(points[i].getX() == pivotX && points[i].getY() == pivotY) {
                    anchorIndex = i;
                }
            }

            shape.getTransforms().add(new Rotate(result, pivotX, pivotY));
            double[] endPoints = pointToDouble(getPoints(shape));

            shape.getTransforms().removeAll();

            for(int i = 0; i < endPoints.length; i++) {
                ((Polygon) shape).getPoints().set(i, endPoints[i]);
            }

            ((Circle) vert2).setCenterX(points[(anchorIndex+1)%3].getX());
            ((Circle) vert2).setCenterY(points[(anchorIndex+1)%3].getY());
            ((Circle) vert3).setCenterX(points[(anchorIndex+2)%3].getX());
            ((Circle) vert3).setCenterY(points[(anchorIndex+2)%3].getY());


        }

        mouse.consume();
    }

    @Override
    // Unused
    public void verticyVDragged(MouseEvent mouse, GenericShape gs, Shape vertV, Shape vertVOther, Shape vertH1, Shape vertH2, Shape rect)
    {
        mouse.consume();
    }

    private Point2D center(Point2D... points) {
        double finalX = 0, finalY = 0, currX = 0, currY = 0;
        for (int i = 0; i < points.length; i++) {
            currX += points[i].getX();
            currY += points[i].getY();
        }
        finalX = currX / points.length;
        finalY = currY / points.length;

        return new Point2D(finalX, finalY);
    }

    private Point2D[] getTranslatedPoints(Shape s, Point2D[] points) {
        Point2D[] result = new Point2D[points.length];
        for(int i = 0; i < points.length; i++) {
            result[i] = s.localToParent(points[i]);
        }
        return result;
    }

    private double[] pointToDouble(Point2D... points) {
        double[] total = new double[points.length * 2];
        for(int i = 0; i < points.length; i++){
            total[i*2] = points[i].getX();
            total[i*2+1] = points[i].getY();
        }
        return total;
    }
}

