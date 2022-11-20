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
import javafx.scene.transform.Rotate;
import media.sound.SoundEffects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.celleditors.DrawTools;
import type.celleditors.SectionEditor;
import uicontrols.UIColors;

import java.awt.geom.AffineTransform;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class ArrowBuilder extends GenericBuilder<FMPolyLine, ArrowBuilder> {

      private static final Logger LOGGER = LoggerFactory.getLogger(ArrowBuilder.class);

      // The mouse start point
      private double anchorX = 0.0;
      private double anchorY = 0.0;

      private double prevMouseX = 0.0;
      private double prevMouseY = 0.0;

      private double deltaX = 0.0;
      private double deltaY = 0.0;

      private String fillColor;
      private String strokeColor;

      //private StringProperty fillProperty = new SimpleStringProperty();
      //private StringProperty strokeProperty = new SimpleStringProperty();

      //private FMPolyLine fmLine;
      private Polyline fxLine;
      double[] arrowPts = {1.1, 1.1};

      private int verticyIdx;

      private final double[] arrowTop = new double[2];
      private final double[] tip = new double[2];
      private final double[] arrowBtm = new double[2];
      private final double[] lowerRectBase = new double[2];
      private final double[] upperRectBase = new double[2];
      private final double[] leftLowerRect = new double[2];
      private final double[] leftUpperRect = new double[2];


      public ArrowBuilder() {
            super();
      }

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
       * @param stroke
       * @param fill
       */
      public ArrowBuilder(Canvas c, GraphicsContext graphC, Pane overlayPane, SectionEditor editor, String stroke, String fill) {
            super(c, graphC, overlayPane, editor);
            fxLine = new Polyline();
            this.strokeColor = stroke;
            this.fillColor = fill;
      }

      /**
       * Basic constructor
       * Called by FMLine getBuilder
       *
       * @param fmPolyLine
       * @param c
       * @param graphC
       * @param pane
       * @param editor
       */
      public ArrowBuilder(FMPolyLine fmPolyLine, Canvas c, GraphicsContext graphC, Pane pane, SectionEditor editor) {
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
      }

      @Override
      public void zeroDeltas() {
            this.deltaX = 0.0;
            this.deltaY = 0.0;
      }

      @Override
      public void setAnchors(MouseEvent mouse) {
            anchorX = mouse.getSceneX();
            anchorY = mouse.getSceneY();
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
            GraphicsContext gC = getGc();

            gC.setStroke(Color.web(strokeColor));
            gC.setFill(Color.web(fillColor));

            if (paste) {
                  //LOGGER.debug("\t - paste action called ***");
                  // Paste the copy
                  pasteAction(mouse);
                  draw.getOverlayScene().setCursor(Cursor.DEFAULT);
                  paste = false;
            } else if (draw.getShapeNotSelected()) { // Avoids conflict between new shape, shape dragged, and shape resized
                  // Clear the resize nodes if they are present

                  draw.clearNodes();
                  setNewShape(true);

                  // Set the shape start point x and y
                  if (anchorX + anchorY == 0.0) {
                        setAnchors(mouse);
                        double[] pts = {anchorX, anchorY};
                        //fmLine = new FMPolyLine(pts,3, strokeColor, fillColor, gbcopyArrayOfFMShapes.size());
                        fxLine = new Polyline();
                  }

            }
            mouse.consume();
      }

      private double distance = 0;

      /**
       * As the mouse is dragged, create the shape of the
       * line.
       * ** DeltaX and DeltaY for a shape, are different than for a line or poly
       *
       * @param e
       */
      @Override
      public void mouseDragged(MouseEvent e) {
            Canvas can = getCanvas();
            GraphicsContext gC = getGc();
            Utility ut = new Utility();

            gC.setStroke(Color.web(strokeColor));
            gC.setFill(Color.web(fillColor));

            DrawTools draw = DrawTools.getInstance();
            if (draw.getShapeNotSelected()) {

                  setNewShape(true);
                  getGc().clearRect(can.getBoundsInLocal().getMinX(), can.getBoundsInLocal().getMinY(),
                      can.getBoundsInLocal().getWidth(), can.getBoundsInLocal().getHeight());

                  deltaX = e.getSceneX() - anchorX;
                  deltaY = e.getSceneY() - anchorY;
                  Point2D mousePt = new Point2D(e.getSceneX(), e.getSceneY());
                  Point2D anchorPt = new Point2D(anchorX, anchorY);
                  distance = anchorPt.distance(mousePt.getX(), mousePt.getY());


                  ArrayList<Point2D> rectBase = ut.calcRectBase(anchorPt, mousePt, distance);

                  tip[0] = anchorX;
                  tip[1] = anchorY;
                  // REF: When arrow points right
                  arrowTop[0] = rectBase.get(0).getX();
                  arrowTop[1] = rectBase.get(0).getY();
                  arrowBtm[0] = rectBase.get(1).getX();
                  arrowBtm[1] = rectBase.get(1).getY();
                  upperRectBase[0] = rectBase.get(2).getX();
                  upperRectBase[1] = rectBase.get(2).getY();
                  lowerRectBase[0] = rectBase.get(3).getX();
                  lowerRectBase[1] = rectBase.get(3).getY();

                  leftLowerRect[0] = rectBase.get(5).getX();
                  leftLowerRect[1] = rectBase.get(5).getY();
                  leftUpperRect[0] = rectBase.get(4).getX();
                  leftUpperRect[1] = rectBase.get(4).getY();

                  double[] arrowPts = {
                      arrowTop[0], arrowTop[1],
                      tip[0], tip[1],
                      arrowBtm[0], arrowBtm[1],
                      lowerRectBase[0], lowerRectBase[1],
                      leftLowerRect[0], leftLowerRect[1],
                      leftUpperRect[0], leftUpperRect[1],
                      upperRectBase[0], upperRectBase[1],
                      arrowTop[0], arrowTop[1]};

                  setXYarrs(arrowPts);
                  gC.strokePolyline(xAry, yAry, arrowPts.length / 2);

                  this.arrowPts = arrowPts;
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

            if (draw.getShapeNotSelected() && distance > 15) {
                  Canvas can = getCanvas();
                  GraphicsContext gC = getGc();
                  //for(double d : arrowPts) {
                  //    fmLine.getShape().getPoints().add(d);
                  //}

                  gC.clearRect(can.getBoundsInLocal().getMinX(), can.getBoundsInLocal().getMinY(),
                      can.getBoundsInLocal().getWidth(), can.getBoundsInLocal().getHeight());

                  if (isNewShape()) {
                        //double[] pts = Stream.of(fxLine.getPoints().toArray()).mapToDouble(o -> Double.parseDouble(o.toString())).toArray();

                        FMPolyLine fmLine = new FMPolyLine(
                            arrowPts,
                            3,
                            this.strokeColor,
                            this.fillColor,
                            gbcopyArrayOfFMShapes.size() - 1);

                        // add shape to gbcopyArrayOfFMShapes
                        gbcopyArrayOfFMShapes.add(fmLine);

                        // get the shape from the saved shape
                        Polyline fxxLine = fmLine.getShape();

                        // add mouse actions to the shape
                        fxxLine.setOnMousePressed(f -> shapePressed(f, fmLine, fxxLine));
                        fxxLine.setOnMouseDragged(f -> shapeDragged(f, fmLine, fxxLine));
                        fxxLine.setOnMouseReleased(f -> shapeReleased(f, fmLine, fxxLine));
                        // show the shape in the overlayPane
                        getOverlayPane().getChildren().add(fxxLine);
                        // Add the scaled version of the shape in the right pane
                        int wd = (int) getOverlayPane().getWidth();
                        int ht = (int) getOverlayPane().getHeight();

                        editorRef.setShapesInRtPane(gbcopyArrayOfFMShapes, wd, ht);

                        setNewShape(false);
                        anchorX = 0.0;
                        anchorY = 0.0;
                        zeroDeltas();
                        distance = 0.0;
                  }
            }

            LOGGER.debug("shapeIsNotSelected or distance is < 15");
            draw.setShapeNotSelected(true);
            mouse.consume();
      }

      private final double[] xAry = new double[8];
      private final double[] yAry = new double[8];

      private void setXYarrs(double[] ary) {
            for (int i = 0, j = 0; i < (ary.length / 2); i++) {
                  xAry[i] = ary[i * 2];
                  yAry[i] = ary[i * 2 + 1];
            }
      }

      /** ************************************************************************************************************ **
       *                                                                                                                 *
       *                                          CUT & PASTE ACTIONS
       *                                                                                                                 *
       ** ************************************************************************************************************ ***/

      /**
       * Overrides the GenericBuilder
       * Adds mouse actions to ellipses in the overlaypane
       * adds it to the overlayPane and adds it to the
       * right pane.
       * Called by genericBuilder
       *
       * @param fxLine The javaFX line object
       * @param gs     GenericShape object
       */
      @Override
      public Shape editableShapeAction(Shape fxLine, GenericShape gs) {
            // add mouse actions to the shape
            fxLine.setOnMousePressed(f -> shapePressed(f, gs, fxLine));
            fxLine.setOnMouseDragged(f -> shapeDragged(f, gs, fxLine));
            fxLine.setOnMouseReleased(f -> shapeReleased(f, gs, fxLine));

            return fxLine;
      }

      /**
       * <p>CopyPaste interface copyAction:</p>
       * <p>creates a new Arrow using FMLine using the mouse current X and Y, and the Shape's current
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
            System.err.println("shapePressed in arrowBuilder called");
            SoundEffects.ROBOT_SERVO_START.play();
            // if not adding to the existing line
            //if(gs.get) {
            fxShape.setStrokeWidth(fxShape.getStrokeWidth() + 2);
            Polyline fxLine1 = (Polyline) fxShape;
            fxL = fxLine1;
            fmL = (FMPolyLine) gs;
            obsvPts = fxLine1.getPoints();

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
            //}
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
            draw.getFillProperty().removeListener(this::strokeChanged);
            draw.getStrokeProperty().removeListener(this::fillChanged);
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
            if (!isNewShape()) {
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
                  editorRef.setShapesInRtPane(gbcopyArrayOfFMShapes, origWd, origHt);
                  //obsvPts.clear();
            }
      }


      /** ************************************************************************************************************ ***
       *                                                                                                                 *
       *                                    RESIZING the shape ... Moving verticies                                     *
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
