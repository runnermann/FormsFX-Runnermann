package core;

import draw.shapes.FMCircle;
import draw.shapes.FMRectangle;
import flashmonkey.FlashCardMM;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import type.tools.imagery.Fit;

import java.util.ArrayList;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

public class ShapeUtility {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public static final int UPPER = 0;
    public static final int LOWER = 1;



    /**
     * Checks the deck for correct shapeFileNames. fileNames should be
     * unique and match it's section. IE U for upper and L for lower. The
     * cardNumber may match, but is independent of the deckNumber. If the
     * card changes locations, it should retain it's original FileName
     * number.
     * @param creatorListFC The CreatorList of FlashCardMM
     * @returns false if there is a match, true if all name are unique an in
     * the correct section.
     */
    private boolean checkFileNameStrings(final ArrayList<FlashCardMM> creatorListFC) {

        System.out.println("*** checkFileNameStrings called ***");
        ArrayList<FlashCardMM> flashListClone = (ArrayList<FlashCardMM>) creatorListFC.clone();


        if(flashListClone.size() < 1) {
            LOGGER.warning("There are no elements in the creatorList");
            return false;
        }


        int size = flashListClone.size();
        String qName;
        String ansName;

        for(int i = 0; i < size; i++) {

            qName   = flashListClone.get(i).getQFiles()[1];
            ansName = flashListClone.get(i).getAFiles()[1];

            System.out.println("\t" + i + ") qFileName: " + qName + "   |    aFileName: " + ansName);

            for(int j = i; j < size; j++) {

                String otherQ = flashListClone.get(j).getQFiles()[1];
                String otherAns = flashListClone.get(j).getAFiles()[1];

                System.out.println("\t" + i + ") otherQFile: " + otherQ + "   |    otherAFile: " + otherAns);

                if(qName.equals(otherQ) || ansName.equals((otherAns))) {
                    System.out.println("\tFailed");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Used with compareShapesInCreatorList. Compares two shapes of any Java shape type.
     *
     * @param original
     * @param otherShape
     * @return Returns true if shape "t" matches with "otherShape"
     */
    private boolean compareShapesHelper(Shape original, Shape otherShape, int idx) {

        CheckShapes check = new CheckShapes();

        if (original.getClass().equals(otherShape.getClass())) {
            // If the shape is not an instance of a rectangle or an ellipse, and if they are
            // if the shape in the foundNode does not match the original from the shapes array,
            // it will return false
            // Note: Oddness with comparison to rectangle and ellipse. Rectangle can be compared with t. However
            // ellipse does not equal the same values when comparing nodes.get(i) vs t. They are not the same

            if ((original.getClass().isInstance(new Rectangle()) ? check.equalRects((Rectangle) otherShape, (Rectangle) original) : check.equalEllipses((Ellipse) otherShape, (Ellipse) original))) {
                return true;
            }

        } else {

            System.out.println("classes did not match\n" +
                    "t: " + original.getClass().getName() + ",   nodes.get[" + idx + "]: " + otherShape.getClass().getName());
        }
        return false;
    }

    /**
     * Copies an array of shapes of unknown length of
     * either a Rectangle, or ellipse.
     *
     * @param shapes
     * @return
     */
    public Shape[] copyShapes(Shape... shapes) {

        Shape[] sAry = new Shape[shapes.length];
        // save array
        for (int idx = 0; idx < shapes.length; idx++) {
            sAry[idx] = (shapes[idx].getClass().getName().contains("Ellipse") ? copyEllipse((Ellipse) shapes[idx]) : copyRect((Rectangle) shapes[idx]));
        }

        return sAry;
    }

    /**
     * @param rect
     * @return Returns a deep copy of a Rectangle. Only copies height and width.
     */
    private Rectangle copyRect(Rectangle rect) {
        return new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    /**
     * @param c
     * @return Returns a deep copy of an ellipse. Only copies heigth and width.
     */
    private Ellipse copyEllipse(Ellipse c) {
        return new Ellipse(c.getCenterX(), c.getCenterY(), c.getRadiusX(), c.getRadiusY());
    }

    /**
     * Compares the shapes from all cards with the shapes
     * that were created or edited in this session. If
     * a card does not match it's original set provided in
     * shapesEditedTplArry, then it returns false and the cards
     * do not match the users input.
     *
     * @param shapesEditedTplArry
     * @param creatorList
     * @return true if any shapes are the same. False if no shapes are the same.
     */
    public boolean compareShapesInCreatorList(Shape[][][] shapesEditedTplArry, ArrayList<FlashCardMM> creatorList) {

        boolean bool = false;

        fileops.FileOpsShapes fo = new fileops.FileOpsShapes();
        for (int i = 0; i < 4; i++) {
            // get the shapes from the flashcard Shapefile in the creator list
            ArrayList<Shape> shapesU = fo.getListFromFile(creatorList.get(i).getQFiles()[1]);
            ArrayList<Shape> shapesL = fo.getListFromFile(creatorList.get(i).getAFiles()[1]);

            for (int j = 0; j < shapesU.size(); j++) {
                // compare shapes from creatorList cards with shapeTplArray elements in upper
                boolean booli = compareShapesHelper(shapesEditedTplArry[i][UPPER][j], shapesU.get(j), i);
                boolean boolii = compareShapesHelper(shapesEditedTplArry[i][LOWER][j], shapesL.get(j), i);
                bool = booli && boolii;
            }
        }
        return bool;
    }

    public class CheckShapes {


        /**
         * Compares the shapes provided in the parameters. The order of the
         * 2D array. The first column is the card index, the second column
         * are the shapes in that card. The expected order of the shapes is
         * rectangle, then ellipse.
         * <p>NOTE: Expects Java Shapes.  </p>
         *
         * @param shapesAry A double array of shapes
         * @param foundNode refereance to the shapes in the
         *                  popUp pane.
         * @return Returns true if both arrays are true. and logs an error message
         * providing details on the shape error.
         * <p>Exits if the pane containing shapes does not exist. </p>
         */
        public boolean checkShapes(Shape[] shapesAry, Node foundNode) {

            //CreateMediaTester.CheckShapes check = new CreateMediaTester.CheckShapes();

            System.out.println("\n *** in checkShapes() ***");
            System.out.println("foundNode: " + foundNode.getClass().getName().toString());
            // comment out from here for tester below

            int idx = 0;

            boolean bool = false;
            // First check that the node is a pane, as expected.
            // Otherwise return false
            if (foundNode.getClass().getName().endsWith("Pane")) {
                ObservableList nodes = ((Pane) foundNode).getChildren();
                // The first shape orignally inserted into the pane
                // during testing is exptected to be a rectangle. Otherwise
                // the node is probably another layer. Look for the first shape
                // and increment idx until we find it.
                while ( ! nodes.get(idx).getClass().isInstance(new Rectangle())) {
                    idx++;
                }
                // *** check the shapes ***
                // !!! Note: both "i" and "idx" are incremented
                // and checked. "idx" is from nodes, "i" is from
                // shapes array.
                for ( int i = 0; idx < nodes.size() ; idx++, i++) {
                    Shape t = (Shape) nodes.get(idx);
                    // If the two shapes are not the same class, it will
                    // return false.
                    if( ! compareShapesHelper(t, shapesAry[i], i)) {
                        return false;
                    }
                }
                bool = true;

            } else {
                LOGGER.warning("foundNode did not end with \"Pane\": EXITING....");
                assertTrue("foundNode could not find a Pane", false);
                bool = false;
                System.exit(1);
            }
            return bool;
        }


        /**
         * Checks if two rectangles are the same width and height
         *
         * @param rect1
         * @param rect2
         * @return True if both are the same, false otherwise.
         */
        boolean equalRects(Rectangle rect1, Rectangle rect2) {

            System.out.println("      |   x   |   y   |   wd  |   ht  |");
            System.out.printf ("rect1 | %.2f | %.2f | %.2f | %.2f |%n", rect1.getX(), rect1.getY(), rect1.getWidth(), rect1.getHeight() );
            System.out.printf ("rect2 | %.2f | %.2f | %.2f | %.2f |",   rect2.getX(), rect2.getY(), rect2.getWidth(), rect2.getHeight() );
            System.out.println(); // clear the buffer

            if (        ((int) rect1.getWidth() >= (int) rect2.getWidth()  - 2 && (int) rect1.getWidth()  <= (int) rect2.getWidth() + 2)
                    && ((int) rect1.getHeight() >= (int) rect2.getHeight() - 2 && (int) rect1.getHeight() <= (int) rect2.getHeight() + 2)
                    && ((int) rect1.getX() >= (int) rect2.getX() - 2  && (int) rect1.getX() <= (int) rect2.getX() + 2)
                    && ((int) rect1.getY() >= (int) rect2.getY() - 16 && (int) rect1.getY() <= (int) rect2.getY() + 16)
            ) {
                return true;
            }
            return false;
        }

        /**
         * Checks if the first rectangle matches the second rectangle after it has been
         * scaled.
         *
         * @param rect1
         * @param rect2
         * @return
         */
        boolean equalRectsScaled(Rectangle rect1, Rectangle rect2, Double scale) {

            System.out.println("\n *** in equalRectsScaled *** \n");

            FMRectangle fmRect = new FMRectangle(rect1);
            Rectangle scaledRect = fmRect.getScaledShape(scale);

            System.out.println("Scale: " + scale
                    + "\nOriginal Java Rectangle: " + rect1
                    + "\nscaled original: " + scaledRect
                    + "\nrect in rt pane: " + rect2
                    + "\n");

            return equalRects(scaledRect, rect2);
        }


        /**
         * An ellipse1 is drawn from top edge to bottom edge
         * and left edge to right edge. Thus radius is 1/2
         * the distance. and x & y are the original - radius.
         *
         * @param ellipse1
         * @param ellipse2
         * @return True if both are the same and false otherwise.
         */
        boolean equalEllipses(Ellipse ellipse1, Ellipse ellipse2) {

            double deltaCentX = ellipse1.getCenterX() + ellipse2.getRadiusX();
            double deltaCentY = ellipse1.getCenterY() + ellipse2.getRadiusY();
            double returnRadX = ellipse2.getRadiusX() * 2;
            double returnRadY = ellipse2.getRadiusY() * 2;

            System.out.println(); // clear the buffer
            System.out.println("    |  centX |  centY | rad_X | rad_Y |");
            System.out.printf ("el1 | %.2f | %.2f | %.2f | %.2f |%n", deltaCentX, deltaCentY, ellipse1.getRadiusX(), ellipse1.getRadiusY() );
            System.out.printf ("el2 | %.2f | %.2f | %.2f | %.2f |",   ellipse2.getCenterX(), ellipse2.getCenterY(), returnRadX, returnRadY );
            System.out.println(); // clear the buffer


            if (        ((int) deltaCentX >= (int) ellipse2.getCenterX() - 2 && (int) deltaCentX <= (int) ellipse2.getCenterX() + 2)
                    &&  ((int) deltaCentY >= (int) ellipse2.getCenterY() - 16 && (int) deltaCentY <= (int) ellipse2.getCenterY() + 16)
                    &&  ((int) ellipse1.getRadiusX() >= (int) returnRadX - 2 && (int) ellipse1.getRadiusX() <= (int) returnRadX + 2)
                    &&  ((int) ellipse1.getRadiusY() >= (int) returnRadY - 2 && (int) ellipse1.getRadiusY() <= (int) returnRadY + 2)
            ) {
                return true;
            }

            return false;
        }


        boolean equalEllipseScaled(Ellipse ellipse1, Ellipse ellipse2, Double scale) {

            FMCircle fmCircle = new FMCircle(ellipse1);
            Ellipse scaledEl = fmCircle.getScaledShape(scale);

            return equalEllipses(scaledEl, ellipse2);
        }

        /**
         * Compares shapes in the rightPane with the original shapes array. Uses the image
         * for scale.
         *
         * @param nodes
         * @param clsShapes
         * @param qoraImage
         * @return
         */
        public boolean compareShapesWRightPane(ArrayList<Node> nodes, Shape[] clsShapes, Image qoraImage, int idx) {

            boolean bool = (nodes.get(1).getClass().getName().contains("Rectangle") ? true : false);
            boolean returnBool = false;

            // i is moving backwards, there are 6 shape elements, subtract
            // two to get the right element.
            if (bool) {
                // shapes from the class
                System.out.println("Eval shapes and \"i\" = " + idx);
                //Shape[] clsShapes = shapesDblAry[i];
                Double scale = Fit.calcScale(qoraImage.getWidth(), qoraImage.getHeight(), 100, 100);

                // 1st element in nodes is the ImageView. Add 1
                boolean bool1 = equalRectsScaled((Rectangle) clsShapes[0], (Rectangle) nodes.get(1), scale);
                boolean bool2 = equalEllipseScaled((Ellipse) clsShapes[1], (Ellipse) nodes.get(2), scale);
                returnBool = bool1 && bool2;
                if (!bool) {
                    LOGGER.severe("\nShapes do not match iteration[" + idx + "]");
                }

            } else {
                assertTrue("\nfound element was not a shape", bool);
            }
            return returnBool;
        }
    }
}

