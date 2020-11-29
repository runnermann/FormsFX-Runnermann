package draw.shapes;

import flashmonkey.FlashMonkeyMain;
import uicontrols.UIColors;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import type.celleditors.DrawTools;
import type.celleditors.SectionEditor;

import java.io.Serializable;

import static uicontrols.UIColors.convertColor;


/**
 * A serializable circle. Holds the values centerX, centerY, and radiusX and radiusY. Fill color, stroke color, and stroke width.
 * Constructor builds a circle. To get a circle use the full constructor, the default constructor,
 * or build one from a circle.
 *
 * @author Lowell Stadelman
 */
public class FMCircle extends GenericShape<FMCircle> implements Serializable
{
    private static final long serialVersionUID = FlashMonkeyMain.VERSION;

    private double radiusX;
    private double radiusY;
    private int shapeAryIdx;
    private double strokeWidth;
    private String strokeColor;
    private String fillColor;

    // This shapes x and y positions
    private double x;  // centerX
    private double y;  // centerY


    /**
     * No Args constructor. Sets this circle to default of centerX, centerY, r to 10.
     * Strokewidth, color, and fill color are set. Fill color is transparent
     */
    public FMCircle()
    {
        System.out.println("Called no args constructor in FM_CIRCLE");
    }

    /**
     * Full constructor FMCircle
     * @param x1
     * @param y1
     * @param radX
     * @param radY
     * @param strokeW
     * @param stroke
     * @param fill
     * @param shapeAryIdx
     */
    public FMCircle (double x1, double y1, double radX, double radY ,
                     double strokeW, String stroke, String fill, int shapeAryIdx)
    {
        this.x = x1;
        this.y = y1;
        this.radiusX = radX;
        this.radiusY = radY;
        this.strokeWidth = strokeW;
        this.strokeColor = stroke;
        this.fillColor = fill;
        this.shapeAryIdx = shapeAryIdx;
    }


    /**
     * <p> Creates an FMCircle using a shallow copy of the parameters values.
     * if the parameter does not have a stroke or fill color, sets stroke to <@code>UIColors.
     * HIGHLIGHT_PINK</@code> and fill to <@code>UIColors.TRANSPARENT</@code></p>
     * @param e A Java ellipse
     */
    public FMCircle(Ellipse e)
    {
        this.x = e.getCenterX();
        this.y = e.getCenterY();
        this.strokeWidth = e.getStrokeWidth();
        this.strokeColor = (e.getStroke() == null ?  UIColors.HIGHLIGHT_PINK : e.getStroke().toString());
        this.fillColor   = (e.getFill() == null ? UIColors.TRANSPARENT : e.getFill().toString());
        this.radiusX = e.getRadiusX();
        this.radiusY = e.getRadiusY();
    }


    /**
     * From a circle
     * @param c
     */
    public FMCircle(Circle c)
    {
        this.x = c.getCenterX();
        this.y = c.getCenterY();
        this.strokeWidth = c.getStrokeWidth();
        this.strokeColor = c.getStroke().toString();
        this.fillColor = c.getFill().toString();
        this.shapeAryIdx = shapeAryIdx;
        this.radiusY = c.getRadius();
        this.radiusX = radiusY;
    }


    /**
     * Copy constructor
     * @param other
     */
    public FMCircle(FMCircle other)
    {
        this.x = other.x;
        this.y = other.y;
        this.strokeWidth = other.strokeWidth;
        this.strokeColor = other.strokeColor;
        this.fillColor = other.fillColor;
        this.shapeAryIdx = other.shapeAryIdx;
        this.radiusX = other.radiusX;
        this.radiusY = other.radiusY;

        System.out.println("FMCircle copy: " + this);
    }
    
    /**
     * @return Returns a deep copy of this object
     */
    @Override
    public FMCircle clone() {
        return new FMCircle(
            this.x,
            this.y,
            this.radiusX,
            this.radiusY,
            this.strokeWidth,
            this.strokeColor,
            this.fillColor,
            this.shapeAryIdx
        );
    }



    /** ************************************************************************************************************ ***
     *                                                                                                                 *
                                                        GETTERS
     *                                                                                                                 *
     ** ************************************************************************************************************ **/
    /**
     * Returns a Circle from this object
     * @return Retruns a circle from this object
     */
    @Override
    public Ellipse getShape()
    {
        Ellipse e = new Ellipse(this.getX(), this.getY(), this.radiusX, this.radiusY);
        
        e.setStrokeWidth(this.getStrokeWidth());
        e.setStroke(convertColor(this.getStrokeColor()));
        e.setFill(convertColor(this.getFillColor()));
        
        System.out.println(toString());
        
        return e;
    }
    
    /**
     * Returns a scaled circle using the scale
     * provided in the parameter.
     * @param scaleY
     * @return
     */
    @Override
    public Ellipse getScaledShape(double scaleY)
    
    //       imageHt is correct when set in the right pane. Need to set the scale for the shapes... :P
    {
        System.out.println("\n *** FMCIRCLE getScaledShape() ***"
                + "\t Ellipse scaleY: " + scaleY);
        
        // USing a JavaFX Ellipse
        Ellipse e = new Ellipse(
                this.getX() * scaleY - 1,
                this.getY() * scaleY - 1,
                this.radiusX * scaleY,
                this.radiusY * scaleY);
        
        e.setStrokeWidth(this.getStrokeWidth() * scaleY);
        e.setStroke(convertColor(this.getStrokeColor()));
        e.setFill(convertColor(this.getFillColor()));
        
        return e;
    }

    public double getX() { return this.x; }

    public double getY() { return this.y; };

    public int getShapeAryIdx() { return this.shapeAryIdx; }

    public double getStrokeWidth() { return this.strokeWidth; }

    public String getStrokeColor() { return this.strokeColor; }

    public String getFillColor() { return this.fillColor; }


    @Override
    public GenericBuilder<FMCircle, CircleBuilder> getBuilder(SectionEditor editor, boolean scaled) {

        DrawTools dt = DrawTools.getInstance();
        //scaled to be used later = scaled
        return new CircleBuilder(
                this,
                dt.getCanvas(),
                dt.getGrapContext(),
                dt.getOverlayPane(),
                editor
        );
    }

    @Override
    public GenericBuilder<FMCircle, CircleBuilder> getBuilder(SectionEditor editor)
    {
        DrawTools draw = DrawTools.getInstance();
        return new CircleBuilder(
                draw.getCanvas(),
                draw.getGrapContext(),
                draw.getOverlayPane(),
                editor,
                this.strokeColor,
                this.fillColor
        );
    }


    


    public double getRadiusX()
    {
        return this.radiusX;
    }

    public double getRadiusY()
    {
        return this.radiusY;
    }





    /** ************************************************************************************************************ ***
     *                                                                                                                 *
                                                        SETTERS
     *                                                                                                                 *
     ** ************************************************************************************************************ **/



    public void setX(double x1) { this.x = x1; }

    public void setY(double y1) { this.y = y1; }

    public void setShapeAryIdx(int i) { this.shapeAryIdx = i; }

    public void setStrokeWidth(double strokeWd) { this.strokeWidth = strokeWd; }

    public void setStrokeColor(String strokeClr) { this.strokeColor = strokeClr; }

    public void setFillColor(String fillClr) { this.fillColor = fillClr; }

    public void setRadiusX(double radX)
    {
        this.radiusX = radX;
    }

    public void setRadiusY(double radY)
    {
        this.radiusY = radY;
    }
    
    /**
     * Stub
     * @param pts
     */
    public void setPoints(ObservableList<Double> pts) { /* stub */}



    /** ************************************************************************************************************ ***
     *                                                                                                                 *
                                                    OTHER METHODS
     *                                                                                                                 *
     ** ************************************************************************************************************ **/


    /**
     * We are only concerned with xy location, width, and height
     * @param other FMCircle
     * @return boolean if the FMCircle in the parameter matches
     * this FMCircle.
     */
    @Override
    public boolean equals(Object other ) {

        if(other.getClass() != FMCircle.class) {
            return false;
        }
        FMCircle otherFMCirc = (FMCircle) other;
        if(this.radiusX == otherFMCirc.getRadiusX() &&
            this.radiusY == otherFMCirc.getRadiusY() &&
            this.x == otherFMCirc.getX()
            && this.y == otherFMCirc.getY()) {
            return true;
        }
        return false;
    }


    /**
     * Returns a hashcode for this FMCircle based on radX, radY, x, y
     * and the hashcode from the classname.
     * @return A hashcode for this FMCircle
     */
    @Override
    public int hashCode() {
        return  this.getClass().getName().hashCode()
            + (int) this.radiusX
            + (int) this.radiusY
            + (int) this.x
            + (int) this.y;
    }


    /**
     * Converts this object to a Grahics Context for use with Canvas
     * @param canvas The canvas that this object will draw on.
     * @return Returns this object as GraphicsContext
     * strokecircle of the canvas given in the parameter.
     */
    @Override
    public Canvas convertToCanvas(Canvas canvas)
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.strokeOval(this.getX(), this.getY(), radiusX * 2, radiusY * 2);
        gc.setStroke(convertColor(this.getStrokeColor()));
        gc.setFill(convertColor(this.getFillColor()));
        gc.setLineWidth(this.getStrokeWidth());

        return canvas;
    }

    /**
     * Converts a Canvas Circle to an FMCircle
     * @param circle The Canvas Circle
     * @return Returns a Serializable FMCircle
     */
    @Override
    public FMCircle convertFmCanvas(Canvas circle)
    {
        return new FMCircle(
                circle.getLayoutX() - 3,
                circle.getLayoutY() - 3,
                circle.getHeight() / 2,
                circle.getWidth(),
                circle.getGraphicsContext2D().getLineWidth(),
                circle.getGraphicsContext2D().getStroke().toString(),
                circle.getGraphicsContext2D().getFill().toString(),
                0);
    }

    @Override
    public String toString()
    {
        return  " rightPaneIndex = " + getShapeAryIdx()
                + "\n centerX = " + this.getX()
                + "\n centerY = " + this.getY()
                + "\n radiusX: " + radiusX
                + "\n radiusY: " + radiusY
                + "\n lineWd = " + getStrokeWidth()
                + "\n lineColor = " + getStrokeColor()
                + "\n fillColor = " + getFillColor();
    }

}
