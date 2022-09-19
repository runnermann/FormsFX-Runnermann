/*
 * Copyright (c) 2019 - 2021. FlashMonkey Inc. (https://www.flashmonkey.xyz) All rights reserved.
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

import ch.qos.logback.classic.Level;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;

import media.sound.SoundEffects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.celleditors.DrawTools;
import type.celleditors.SectionEditor;
import uicontrols.UIColors;

import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * This class creates an FMPolyline object.
 * <p><b>PROBLEM:</b> In normal shapes, there is a click drag and release cycle
 * that creates the shape. With a polyline, each segment is created by a cycle.
 * A Polyline is a single line with multiple direction changes. A normal shape cycle
 * would create a new Polyline each cycle. </p>
 * <p><b>SOLUTION:</b> To create a polyline. We do not initialize the FMPolyline
 * until the first "mousePressed()" on the Canvas. A single segment is created without
 * the "alt" key. To create successive segments, the alt key must be used. IF the
 * "alt" key is down. The segments start position is at the ending of the previous
 * segment. If the "isAlt" is false, meaning that the "alt" key is not pressed,
 * we end the Polyline. </p>
 * <ol>
 *     <li>if anchorX {@literal &} anchorY == 0, initialize FMLine with properties</li>
 *     <li>The fxLine uses the FMPolyline object's Polyline with .getShape()</li>
 *     <li>color properties use fmLine's Polyline properties. </li>
 * </ol>
 *
 * @author Lowell Stadelman
 */
public class PolylineBuilder extends GenericBuilder<FMPolyLine, PolylineBuilder> {
      private static final Logger LOGGER = LoggerFactory.getLogger(PolylineBuilder.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(PolylineBuilder.class);


      // The mouse start point
      private double anchorX = 0.0;
      private double anchorY = 0.0;

      private double prevMouseX = 0.0;
      private double prevMouseY = 0.0;

      private String fillColor;
      private String strokeColor;

      //private StringProperty fillProperty = new SimpleStringProperty();
      //private StringProperty strokeProperty = new SimpleStringProperty();

      private Polyline fxLine;
      //private FMPolyLine fmLine;

      private int verticyIdx;

      public PolylineBuilder() {/*no args constructor*/}

      /**
       * Constructor used when new shapes are initially created, Rectangle is
       * used to define the
       * popUp panes size. Called first
       * to create the overlayPane over the area selected.
       *
       * @param c
       * @param graphC
       * @param overlayPane
       * @param editor
       * @param strokeClr
       * @param fillClr
       */
      public PolylineBuilder(Canvas c, GraphicsContext graphC, Pane overlayPane, SectionEditor editor, String strokeClr, String fillClr) {
            super(c, graphC, overlayPane, editor);
            fxLine = new Polyline();
            this.strokeColor = strokeClr;
            this.fillColor = fillClr;
            //strokeProperty.setValue(strokeClr);
            //fillProperty.setValue(fillClr);

            //LOGGER.setLevel(Level.DEBUG);
      }

      /**
       * Basic constructor without setting the stroke color or fill color
       * Called by FMLine getBuilder
       *
       * @param fmPolyLine
       * @param c
       * @param graphC
       * @param pane
       * @param editor
       */
      public PolylineBuilder(FMPolyLine fmPolyLine, Canvas c, GraphicsContext graphC, Pane pane, SectionEditor editor) {
            super(c, graphC, pane, editor);
            Polyline fxLine = fmPolyLine.getShape();

            // add mouse actions to the shape when constuctor is called
            // to edit existing shapes
            fxLine.setOnMousePressed(f -> shapePressed(f, fmPolyLine, fxLine));
            fxLine.setOnMouseDragged(f -> shapeDragged(f, fmPolyLine, fxLine));
            fxLine.setOnMouseReleased(f -> shapeReleased(f, fmPolyLine, fxLine));

            this.fillColor = fmPolyLine.getFillColor();
            this.strokeColor = fmPolyLine.getStrokeColor();

            getOverlayPane().getChildren().add(fxLine);

            setNewShape(true);

            //LOGGER.setLevel(Level.DEBUG);

      }


      @Override
      public void zeroDeltas() { /* empty */ }

      @Override
      public void setAnchors(MouseEvent mouse) {
            anchorX = mouse.getSceneX();
            anchorY = mouse.getSceneY();
            //System.out.println("anchorX: {} " + anchorX + ", anchorY: {}"  + anchorY);
      }

      /**
       * When the mouse is pressed in the overlayArea. If (copy = true), create
       * a shape.
       *
       * @param mouse
       */
      @Override
      public void mousePressed(MouseEvent mouse) {
            SoundEffects.ROBOT_SERVO_2.play();
            DrawTools draw = DrawTools.getInstance();
            // Clear the resize nodes if they are present
            draw.clearNodes();
            GraphicsContext gC = getGc();
            gC.setStroke(Color.web(strokeColor));
            gC.setFill(Color.web(fillColor));

            if (paste) {
                  //System.out.println("\t - paste action called ***");
                  // Paste the copy
                  pasteAction(mouse);
                  draw.getOverlayScene().setCursor(Cursor.DEFAULT);
                  paste = false;
            } else if (draw.getShapeNotSelected()) { // Avoids conflict between new shape, shape dragged, and shape resized
                  setNewShape(true);

                  // Set the shape start point x and y
                  if (anchorX + anchorY == 0.0) {
                        fxLine = new Polyline();
                        setAnchors(mouse);
                        double[] pts = {anchorX, anchorY};
                        //    fmLine = new FMPolyLine(pts,3, strokeColor, fillColor, gbcopyArrayOfFMShapes.size());
                        fxLine.getPoints().add(anchorX);
                        fxLine.getPoints().add(anchorY);

                        //    gbcopyArrayOfFMShapes.add(fmLine);
                  }
            }
            mouse.consume();
      }


      /**
       * As the mouse is dragged, create the shape of the
       * line.
       * ** DeltaX and DeltaY for a shape, are different than for a line or poly
       *
       * @param e
       */
      @Override
      public void mouseDragged(MouseEvent e) {
            //System.out.println("mouseDragged");
            Canvas can = getCanvas();
            GraphicsContext gC = getGc();

            gC.setStroke(Color.web(strokeColor));
            gC.setFill(Color.web(fillColor));

            DrawTools draw = DrawTools.getInstance();
            if (draw.getShapeNotSelected()) {

                  getGc().clearRect(can.getBoundsInLocal().getMinX(), can.getBoundsInLocal().getMinY(),
                      can.getBoundsInLocal().getWidth(), can.getBoundsInLocal().getHeight());

                  double[] xList = {anchorX, e.getSceneX()};
                  double[] yList = {anchorY, e.getSceneY()};
                  gC.strokePolyline(xList, yList, 2);

            }
            e.consume();
      }

      /**
       * When the mouse is released, set the data in FMShape, store the fmShape in the gbcopyArrayOfFMShapes, display
       * the shape in the right pane.
       *
       * @param mouse
       */
      @Override
      public void mouseReleased(MouseEvent mouse) {

            DrawTools draw = DrawTools.getInstance();
            if (draw.getShapeNotSelected()) {
                  Canvas can = getCanvas();
                  GraphicsContext gC = getGc();

                  gC.clearRect(can.getBoundsInLocal().getMinX(), can.getBoundsInLocal().getMinY(),
                      can.getBoundsInLocal().getWidth(), can.getBoundsInLocal().getHeight());

                  if (isNewShape()) {
                        setAnchors(mouse);

                        //Instatniate line in mousePressed. Use setters to update line.
                        if (!mouse.isAltDown()) {
                              LOGGER.debug("alt is not down");
                              fxLine.getPoints().add(mouse.getSceneX());
                              fxLine.getPoints().add(mouse.getSceneY());
                              double[] pts = Stream.of(fxLine.getPoints().toArray()).mapToDouble(o -> Double.parseDouble(o.toString())).toArray();

                              LOGGER.debug("line num points: {}", pts.length);

                              FMPolyLine fmLine = new FMPolyLine(pts, 3, strokeColor, fillColor, gbcopyArrayOfFMShapes.size());
                              // add shape to gbcopyArrayOfFMShapes
                              gbcopyArrayOfFMShapes.add(fmLine);
                              // get the shape from the saved shape
                              final Polyline fxxLine = fmLine.getShape();
                              // add mouse actions to the shape
                              fxxLine.setOnMousePressed(f -> shapePressed(f, fmLine, fxxLine));
                              fxxLine.setOnMouseDragged(f -> shapeDragged(f, fmLine, fxxLine));
                              fxxLine.setOnMouseReleased(f -> shapeReleased(f, fmLine, fxxLine));

                              // show the shape in the overlayPane
                              if (!getOverlayPane().getChildren().contains(fxxLine)) {
                                    getOverlayPane().getChildren().add(fxxLine);
                              }

                              int wd = (int) getOverlayPane().getWidth();
                              int ht = (int) getOverlayPane().getHeight();

                              editorRef.setShapesInRtPane(gbcopyArrayOfFMShapes, wd, ht);


                              //        this.fxLine = new Polyline();
                              anchorY = 0.0;
                              anchorX = 0.0;
                              setNewShape(false);
                              draw.setShapeNotSelected(true);
                        } else {
                              LOGGER.debug("alt is down in PolylineBuilder");
                              fxLine.getPoints().addAll(anchorX, anchorY);
                              setNewShape(true);
                              anchorX = mouse.getSceneX();
                              anchorY = mouse.getSceneY();
                        }
                  } else {
                        //System.out.println("not a new shape");
                  }
            } else {
                  //System.out.println("WARNING: shapeIsNotSelected... Skipped mouseReleased");
            }
            mouse.consume();
      }


      /** ************************************************************************************************************ **
       *                                                                                                                 *
       *                                          CUT & PASTE ACTIONS
       *                                                                                                                 *
       ** ************************************************************************************************************ ***/


      /**
       * Overrides the GenericBuilder
       * Adds mouse actions to fxLines in the overlaypane
       * adds it to the overlayPane and adds it to the
       * right pane.
       * Called by genericBuilder
       *
       * @param fxLine The javaFX line object
       * @param gs     GenericShape object
       */
      @Override
      public Shape editableShapeAction(Shape fxLine, GenericShape gs) {
            //System.out.println("editableShapeAction called");
            // add mouse actions to the shape
            fxLine.setOnMousePressed(f -> shapePressed(f, gs, fxLine));
            fxLine.setOnMouseDragged(f -> shapeDragged(f, gs, fxLine));
            fxLine.setOnMouseReleased(f -> shapeReleased(f, gs, fxLine));

            return fxLine;
      }

      /**
       * <p>CopyPaste interface copyAction:</p>
       * <p>creates a new FMLine using the mouse current X and Y, and the Shape's current
       * width and height, adds it to gbcopyArrayOfFMShapes, then clears
       * the nodes.</p>
       *
       * @param mouse
       * @param gs
       */
      @Override
      public void copyAction(MouseEvent mouse, GenericShape gs) {
            FMPolyLine fmPolyLine;

            fmPolyLine = new FMPolyLine(
                gs.getDblPts(), gs.getStrokeWidth(),
                gs.getStrokeColor(), gs.getFillColor(), gbcopyArrayOfFMShapes.size());

            gbcopyArrayOfFMShapes.add(fmPolyLine);
            setPaste(true);
            DrawTools draw = DrawTools.getInstance();
            draw.clearNodes();
            mouse.consume();
      }


      /**
       * *********************************************************************************************************** **
       * *
       * PANNING the shape
       * and other mouse actions
       * *
       * * ************************************************************************************************************
       ***/


      private ObservableList obsvPts;

      /**
       * Gets the x and y locations of the mouse in the Shape and the pane.
       * or, if right mouse button is selected goes to shapeRightPress.
       *
       * @param mouse The mouse MouseEvent
       */
      @Override
      public void shapePressed(MouseEvent mouse, GenericShape gs, Shape fxShape) {

            // if not adding to the existing line
            if (!mouse.isAltDown()) {
                  SoundEffects.ROBOT_SERVO_START.play();
                  fxShape.setStrokeWidth(fxShape.getStrokeWidth() + 2);
                  Polyline fxLine1 = (Polyline) fxShape;
                  fxL = fxLine1;
                  obsvPts = fxLine1.getPoints();
                  fmL = ((FMPolyLine) gs);

                  // Clear the resize nodes if they are present
                  DrawTools draw = DrawTools.getInstance();
                  draw.setShapeNotSelected(false);
                  draw.clearNodes();
                  // Clear all listeners unless
                  if (!mouse.isShiftDown()) {
                        draw.clearListeners();
                  }

                  draw.getFillProperty().addListener(this::fillChanged);
                  draw.getStrokeProperty().addListener(this::strokeChanged);

                  /* If fxShape right mouse click create resize nodes **/
                  if (mouse.isSecondaryButtonDown()) {
                        shapeRightPress(mouse, gs, fxShape);
                        //System.out.println("after shapeRightPress");
                  } else {
                        prevMouseX = mouse.getSceneX();
                        prevMouseY = mouse.getSceneY();
                  }
                  setNewShape(false);
                  mouse.consume();
            }
      }


      // ***************** LISTENERS ********************

      private Polyline fxL = new Polyline();
      private FMPolyLine fmL = new FMPolyLine();

      public void strokeChanged(ObservableValue<? extends String> prop, String oldVal, String newVal) {
            fxL.setStroke(Color.web(newVal));
            fmL.setStrokeColor(newVal);
            strokeColor = newVal;
            double origWd = ((FMRectangle) gbcopyArrayOfFMShapes.get(0)).getWd();
            double origHt = ((FMRectangle) gbcopyArrayOfFMShapes.get(0)).getHt();
            editorRef.setShapesInRtPane(gbcopyArrayOfFMShapes, origWd, origHt);
      }

      public void fillChanged(ObservableValue<? extends String> prop, String oldVal, String newVal) {
            fxL.setFill(Color.web(newVal));
            fmL.setFillColor(newVal);
            fillColor = newVal;
            double origWd = ((FMRectangle) gbcopyArrayOfFMShapes.get(0)).getWd();
            double origHt = ((FMRectangle) gbcopyArrayOfFMShapes.get(0)).getHt();
            editorRef.setShapesInRtPane(gbcopyArrayOfFMShapes, origWd, origHt);
      }

      @Override
      public void clearListeners() {
            DrawTools draw = DrawTools.getInstance();
            draw.getFillProperty().removeListener(this::fillChanged);
            draw.getStrokeProperty().removeListener(this::strokeChanged);
      }


      @Override
      public void shapeDragged(MouseEvent mouse, GenericShape gs, Shape fxShape) {
            newSound = startTime < System.currentTimeMillis() - 1500;
            if(newSound) {
                  startTime = System.currentTimeMillis();
                  SoundEffects.ROBOT_SERVO_3.play();
                  //isPlaying = false;
            }
            if (!mouse.isAltDown()) {
                  if (mouse.isPrimaryButtonDown()) {
                        // uses the observable list/reference
                        // to move shape. :)
                        Polyline fxLine = (Polyline) fxShape;
                        obsvPts = setObsvPts(fxLine, mouse);
                        prevMouseX = mouse.getSceneX();
                        prevMouseY = mouse.getSceneY();
                  }
                  mouse.consume();
            }
      }

      private ObservableList setObsvPts(Polyline fxLine, MouseEvent mouse) {
            ObservableList op = fxLine.getPoints();
            for (int i = 0; i < op.size() / 2; i++) {
                  double x = (double) op.get(i * 2);
                  double y = (double) op.get(i * 2 + 1);
                  op.set(i * 2, x + (mouse.getSceneX() - prevMouseX));
                  op.set(i * 2 + 1, y + (mouse.getSceneY() - prevMouseY));
            }
            return op;
      }


      /**
       * On mouse released, Sets the new location in
       * the gbcopyArrayOfFMShapes
       *
       * @param gs
       * @param shape
       */
      @Override
      public void shapeReleased(MouseEvent mouse, GenericShape gs, Shape shape) {
            // if not adding to the existing line
            if (!mouse.isAltDown() && !isNewShape()) {
                  SectionEditor editor = editorRef;
                  shape.setStrokeWidth(shape.getStrokeWidth() - 2);

                  editor.getRightPane().getChildren().clear();
                  if (editor.getImageView() != null) {
                        editor.getRightPane().getChildren().add(editor.getImageView());
                  }
                  // Update this fmShape/fmLine
                  obsvPts = setObsvPts(fxL, mouse);
                  gs.setPoints(obsvPts);
                  gs.setFillColor(fillColor);
                  gs.setStrokeColor(strokeColor);
                  // The first shape in the gbcopyArrayOfFMShapes is an FMRectangle
                  // with the demensions of the original SnapShot rectangle.
                  double origWd = ((FMRectangle) gbcopyArrayOfFMShapes.get(0)).getWd();
                  double origHt = ((FMRectangle) gbcopyArrayOfFMShapes.get(0)).getHt();
                  editorRef.setShapesInRtPane(   gbcopyArrayOfFMShapes, origWd, origHt);
                  //obsvPts.clear();
            } else {
                  //System.out.println("Called nich case in shapeReleased");
                  // Niche case: When user releases the "alt" key before releasing the
                  // mouse button
                  DrawTools draw = DrawTools.getInstance();
                  draw.setShapeNotSelected(true);
                  mouseReleased(mouse);
            }
      }


      /** ************************************************************************************************************ ***
       *                                                                                                                 *
       *                                    RESIZING the Line ... Moving verticies                                       *
       *                                                                                                                 *
       ** ************************************************************************************************************ **/

      /**
       * Helper method. Places the points at the lines end-points.
       * - Returns an array of Point2D x and y coordinates.
       *
       * @return
       */
      //@Override
      private Point2D[] get2DPoints(Shape shape) {
            ObservableList<Double> rawPoints = ((Polyline) shape).getPoints();
            int size = rawPoints.size();
            double[] points = new double[size];
            Point2D[] result = new Point2D[size / 2];

            for (int i = 0; i < size; i++) {
                  points[i] = rawPoints.get(i);
            }
            for (int n = 0; n < size / 2; n++) {
                  result[n] = new Point2D(points[n * 2], points[n * 2 + 1]);
            }

            return result;
      }

      private Point2D[] getTranslatedPoints(Shape s, Point2D[] points) {
            Point2D[] result = new Point2D[points.length];
            for (int i = 0; i < points.length; i++) {
                  result[i] = s.localToParent(points[i]);
            }
            return result;
      }


      /**
       * Gets the nodes/resize handles for a shape. Resize handles start point and end point
       *
       * @param thisShape This shape
       * @return Returns an arraylist of handles located left, right and top, bottom.
       */
      public ArrayList<Circle> getHandles(GenericShape gs, Shape thisShape) {
            //System.out.println("getHandles method");
            Point2D[] pts2D = get2DPoints(thisShape);
            ArrayList<Circle> verticies = new ArrayList<>(pts2D.length);
            Circle c;

            for (int i = 0; i < pts2D.length; i++) {
                  c = new Circle(pts2D[i].getX(), pts2D[i].getY(), 4, Color.web(UIColors.HIGHLIGHT_ORANGE));
                  int finalI = i;
                  c.setOnMousePressed(e -> verticyPressed(finalI, e, thisShape));
                  c.setOnMouseReleased(e -> verticyReleased(gs));
                  c.setOnMouseDragged(e -> verticyXYDragged(e, gs, verticies, thisShape));
                  verticies.add(c);
            }
            //System.out.println("after for loop");

            //System.out.println("return");
            return verticies;
      }

      @Override
      public void verticyPressed(int idx, MouseEvent mouse, Shape s) {
            this.verticyIdx = idx;
            DrawTools draw = DrawTools.getInstance();
            draw.setShapeNotSelected(false);
            mouse.consume();
      }

      /**
       * Used for the moving verticy. The verticy that is moving is index 0. The static verticy
       * is index 1.
       *
       * @param mouse
       * @param gs
       * @param vertArry An array containing verticies
       * @param shape    The Shape or Polygon being moved.
       */
      @Override
      public void verticyXYDragged(MouseEvent mouse, GenericShape gs, ArrayList<Circle> vertArry, Shape shape) {
            if (!mouse.isSecondaryButtonDown() && !mouse.isAltDown()) {
                  Circle c = vertArry.get(verticyIdx);
                  c.setCenterX(mouse.getSceneX());
                  c.setCenterY(mouse.getSceneY());
                  // set line endings to new position
                  ((Polyline) shape).getPoints().set(verticyIdx * 2, mouse.getSceneX());
                  ((Polyline) shape).getPoints().set(verticyIdx * 2 + 1, mouse.getSceneY());
            }
            mouse.consume();
      }

      /**
       * Not used
       */
      @Override
      public void verticyVDragged(MouseEvent mouse, GenericShape gs, Shape vertV, Shape vertVOther, Shape vertH1, Shape vertH2, Shape line) {
            // not used
      }

      /**
       * Not used
       */
      @Override
      public void verticyHDragged(MouseEvent mouse, GenericShape gs, Shape vertH, Shape vertHOther, Shape vertH1, Shape vertH2, Shape line) {
            // not used
      }
}
