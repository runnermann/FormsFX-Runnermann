/*
 * Copyright (c) 2019 - 2021. FlashMonkey Inc. (https://www.flashmonkey.co) All rights reserved.
 *
 * License: This is for internal use only by those who are current employees of FlashMonkey Inc, or have an official
 *  authorized relationship with FlashMonkey Inc..
 *
 * DISCLAIMER OF WARRANTY.
 *
 * COVERED CODE IS PROVIDED UNDER THIS LICENSE ON AN "AS IS" BASIS, WITHOUT WARRANTY OF ANY
 *  KIND, EITHER EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION, WARRANTIES THAT THE COVERED
 *  CODE IS FREE OF DEFECTS, MERCHANTABLE, FIT FOR A PARTICULAR PURPOSE OR NON-INFRINGING. THE
 *  ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE COVERED CODE IS WITH YOU. SHOULD ANY
 *  COVERED CODE PROVE DEFECTIVE IN ANY RESPECT, YOU (NOT THE INITIAL DEVELOPER OR ANY OTHER
 *  CONTRIBUTOR) ASSUME THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 *  DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.  NO USE OF ANY COVERED
 *  CODE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 *
 */

package type.draw.shapes;

import flashmonkey.FlashMonkeyMain;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import type.celleditors.DrawTools;
import type.celleditors.SectionEditor;

import java.io.Serializable;
import java.util.stream.Stream;

import static uicontrols.UIColors.*;

/**
 * A serializable Polyline. Holds the values pts, Note that javaFX shapes
 * are not serializable.
 * In addition to the Java API Line, also holds the index in the shapeArray
 * Constructor builds a Line.
 */
public class FMPolyLine extends GenericShape<FMPolyLine> implements Serializable {
      private static final long serialVersionUID = FlashMonkeyMain.VERSION;

      // This shapes x and y positions
      //private ObservableList<Double> pts;
      private double[] pts;

      // Stroke and fill
      private double strokeWidth;
      private String fillColor = "00000000";
      private String strokeColor = "#2980b9";

      // A shape knows its index in the
      // shape array
      private int shapeAryIdx;


      /**
       * No args constructor
       */
      public FMPolyLine() { /* empty */ }

      /**
       * Called by shape builders that extend this class.
       *
       * @param pts1
       * @param strokeW
       * @param stroke
       * @param fill
       */
      public FMPolyLine(double[] pts1, double strokeW, String stroke, String fill) {
            //pts1 = pts.length != 0 ? pts1 : new double[0];
            this.pts = Stream.of(pts1).mapToDouble(o -> Double.parseDouble(o.toString())).toArray();
            this.fillColor = fill;
            this.strokeColor = stroke;
            this.strokeWidth = strokeW;
      }


      /**
       * Full constructor
       *
       * @param pts1   the ObservableList of points
       * @param stroke stroke color
       * @param fill   fill color
       * @param index  this objects index in the arrayOfShapes/arrayOfFMShapes
       */
      public FMPolyLine(double[] pts1, double strokeW, String stroke, String fill, int index) {
            double[] pt = new double[pts1.length];
            for (int i = 0; i < pts1.length; i++) {
                  pt[i] = pts1[i];
            }
            this.pts = pt;
            this.fillColor = fill;
            this.strokeColor = stroke;
            this.strokeWidth = strokeW;
            this.shapeAryIdx = index;
      }

      /**
       * <p> Creates an FMLine using a shallow copy of the parameters values.
       * if the parameter does not have a stroke or fill color, sets stroke to {@code UIColors.
       * HIGHLIGHT_PINK and fill to UIColors.TRANSPARENT}</p>
       *
       * @param fxLine ..
       */
      public FMPolyLine(Polyline fxLine) {
            this.pts = Stream.of(fxLine.getPoints().toArray()).mapToDouble(o -> Double.parseDouble(o.toString())).toArray();
            this.strokeWidth = fxLine.getStrokeWidth();
            this.strokeColor = fxLine.getStroke().toString();
            this.fillColor = fxLine.getFill().toString();
      }

      /**
       * FMLine Copy constructor
       *
       * @param other ..
       */
      public FMPolyLine(FMPolyLine other) {
            this.pts = other.getDblPts();
            this.strokeWidth = other.getStrokeWidth();
            this.strokeColor = other.getStrokeColor();
            this.fillColor = other.getFillColor();
            this.shapeAryIdx = other.shapeAryIdx;
      }


      /**
       * @return Returns a deep copy of this FMLine
       */
      @Override
      public FMPolyLine clone() {
            return new FMPolyLine(
                this.pts,
                this.getStrokeWidth(),
                this.getStrokeColor(),
                this.getFillColor(),
                this.shapeAryIdx
            );
      }


      /**
       * *********************************************************************************************************** ***
       * *
       * GETTERS
       * *
       * * ************************************************************************************************************
       **/

      public ObservableList<Double> getPts() {
            return this.getPts();
      }

      public double[] getDblPts() {
            return this.pts;
      }

      public int getShapeAryIdx() {
            return this.shapeAryIdx;
      }

      public double getStrokeWidth() {
            return strokeWidth;
      }

      public String getStrokeColor() {
            return strokeColor;
      }

      public String getFillColor() {
            return fillColor;
      }

      /**
       * @return Returns a JavaFX PolyLine from this object
       */
      @Override
      public Polyline getShape() {
            Polyline p = new Polyline(pts);
            p.setFill(Color.web(fillColor));
            p.setStroke(Color.web(strokeColor));
            p.setStrokeWidth(strokeWidth);
            return p;
      }

      /**
       * Returns a scaled Line using the scale
       * provided in the parameter.
       *
       * @param scaleY ..
       * @return ..
       */
      @Override
      public Polyline getScaledShape(double scaleY) {
            //setPoints(this.line.getPoints()); // points already set

            int size = this.pts.length;
            double[] scaledPts = new double[size];
            // Using JavaFX Line
            for (int i = 0; i < size; i++) {
                  scaledPts[i] = pts[i] * scaleY;
            }
            Polyline l = new Polyline(scaledPts);
            l.setStrokeWidth(this.getStrokeWidth() * scaleY);
            l.setStroke(Color.web(this.getStrokeColor()));
            l.setFill(Color.web(this.getFillColor()));

            return l;
      }


      @Override
      public GenericBuilder<FMPolyLine, PolylineBuilder> getBuilder(SectionEditor editor, boolean scaled) {

            DrawTools dt = DrawTools.getInstance();
            //scaled to be used later = scaled
            return new PolylineBuilder(
                this,
                dt.getCanvas(),
                dt.getGrapContext(),
                dt.getOverlayPane(),
                editor
            );
      }

      @Override
      public GenericBuilder<FMPolyLine, PolylineBuilder> getBuilder(SectionEditor editor) {

            DrawTools draw = DrawTools.getInstance();
            return new PolylineBuilder(
                draw.getCanvas(),
                draw.getGrapContext(),
                draw.getOverlayPane(),
                editor,
                this.getStrokeColor(),
                this.getFillColor()
            );
      }


      /**
       * *********************************************************************************************************** ***
       * *
       * SETTERS
       * *
       * * ************************************************************************************************************
       **/


      public void setShapeAryIdx(int i) {
            this.shapeAryIdx = i;
      }

      public void setStrokeWidth(double strokeWd) {
            this.strokeWidth = strokeWd;
      }

      public void setStrokeColor(String strokeClr) {
            this.strokeColor = strokeClr;
      }

      public void setFillColor(String fillClr) {
            this.fillColor = fillClr;
      }


      /**
       * not used
       *
       * @param notUsed ..
       **/
      @Override
      public void setX(double notUsed) {
            // not used
      }

      /**
       * not used
       *
       * @param notUsed ..
       **/
      @Override
      public void setY(double notUsed) {
            // not used
      }

      /**
       * Sets the points for this PolyLine
       *
       * @param points ..
       */
      public void setPoints(ObservableList points) {
            double[] p = new double[points.size()];
            for (int i = 0; i < points.size(); i++) {
                  p[i] = (double) points.get(i);
            }
            this.pts = p;
      }


      /**
       * Returs the X position of this shape. Generally the top left
       * (aka minimum)  or the X position of the center of the
       * shape if it si an ellipse.
       *
       * @return ..
       */
      @Override
      public double getX() {
            return pts[0];
      }

      /**
       * Returns the Y position of this shape Generally the top left
       * ( aka minimum ) or the Y position of the center of the
       * shape if it is an ellipse.
       *
       * @return ..
       */
      @Override
      public double getY() {
            return pts[1];
      }


      /** ************************************************************************************************************ ***
       *                                                                                                                 *
       OTHER METHODS
       *                                                                                                                 *
       ** ************************************************************************************************************ **/


      /**
       * We are only concerned with xy location, width, and height
       *
       * @param other FMLine
       * @return boolean if the FMLine in the parameter matches
       * this FMLine.
       */
      @Override
      public boolean equals(Object other) {

            if (other.getClass() != FMPolyLine.class) {
                  return false;
            }

            FMPolyLine otherLine = (FMPolyLine) other;
            return this.pts.equals(otherLine.pts);
      }


      /**
       * Returns a hashcode for this FMLine based on width, height, x, y
       * and the hashcode from the classname.
       *
       * @return A hashcode for this FMPolyline
       */
      @Override
      public int hashCode() {
            return this.getClass().getName().hashCode()
                + this.pts.hashCode();
      }


      private double[] getXPts() {
            double[] p = new double[pts.length];
            for (int i = 0; i < pts.length / 2; i++) {
                  p[i] = pts[i];
            }
            return p;
      }


      private double[] getYPts() {
            double[] p = new double[pts.length];
            for (int i = pts.length / 2 + 1; i < pts.length / 2; i++) {
                  p[i] = pts[i];
            }
            return p;
      }


      /**
       * Converts this object to a Grahics Context for use with Canvas
       *
       * @param canvas The canvas that this object will be drawn.
       * @return Returns this object as GraphicsContext
       * strokePolyline of the canvas given in the parameter.
       */
      @Override
      public Canvas convertToCanvas(Canvas canvas) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.strokePolyline(getXPts(), getYPts(), (pts.length / 2));
            gc.setStroke(convertColor(getStrokeColor()));
            gc.setFill(convertColor(getFillColor()));
            gc.setLineWidth(this.getStrokeWidth());

            return canvas;
      }

      /**
       * not used
       */
      @Override
      public FMPolyLine convertFmCanvas(Canvas l) {
            return new FMPolyLine();
      }


      @Override
      public String toString() {
            StringBuilder sb = new StringBuilder("FMPolyline toString()");
            sb.append(" rightPaneIndex = " + getShapeAryIdx() + "\n points: ");
            for (Double o : pts) {
                  sb.append(o + ",");
            }
            sb.append("\n lineWd = " + getStrokeWidth()
                + "\n lineColor = " + getStrokeColor()
                + "\n fillColor = " + getFillColor());

            return sb.toString();
      }

}
