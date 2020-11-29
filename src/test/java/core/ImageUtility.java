package core;

import javafx.scene.image.Image;

import java.awt.*;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

public class ImageUtility {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Compares two images by scoring a pixel either above or below a threshold.
     * If the scores match, then it is the same image. Will not work if both images
     * are a single color.
     * @param image1
     * @param image2
     * @return
     */
    public static boolean imagesLookTheSame(String msg, Image image1, Image image2) {

        double scoreImg1 = 0;
        double scoreImg2 = 0;
        int thresh = -3751494;

        if(image1 != null && image2 != null) {

            if(image1.getHeight() != image2.getHeight()
                    || image1.getWidth() != image2.getWidth()) {
                LOGGER.warning("Error in imagesLookTheSame()... Images are not the same height and/or width");
                System.out.println("image1 ht: " + image1.getWidth() + ", wd: " + image1.getHeight());
                System.out.println("image2 ht: " + image2.getWidth() + ", wd: " + image2.getHeight());
                return false;
            }

            for(int x = 0; x < image1.getWidth(); x++) {
                for (int y = 0; y < image1.getHeight(); y++) {
                    //System.out.println("x: " + x + ", y: " + y);

                    if (image1.getPixelReader().getArgb(x, y) < thresh) {

                        scoreImg1++;
                    }

                    if ((image2.getPixelReader().getArgb(x, y)) < thresh) {
                        scoreImg2++;
                    }
                }
            }

            System.out.println("Img Color score image1: " + scoreImg1 + ", Img Color score image2: " + scoreImg2);

            if(scoreImg1 < ( scoreImg2 + 5) && (scoreImg1 > scoreImg2 - 5)) {
                return true;
            }
        } else {
            LOGGER.warning("In " + msg + ": Null image when comparing. image1 == null: " + (image1 == null) + ", image2 == null: " + (image2 == null));
        }
        return false;
    }


    /**
     * Tests the images of text if they look the same. Does not use OCR. Checks pixels. Is not a perfect solution
     * and may error. Expects a white background and dark-grey to black text.
     * @param image1 text from the expected image
     * @param image2 text from the image to be tested
     * @return
     */
    public static boolean txtImgsLookTheSame(Image image1, Image image2) {

        double scoreImg1 = 0;
        double scoreImg2 = 0;
        int thresh = -1000;

        if(image1 != null && image2 != null) {

            if(image1.getWidth() != image2.getWidth() && image1.getHeight() != image2.getHeight()) {
                System.out.println("Image1 is not the same size as Image 2\n" +
                        "image1: wd x ht " + image1.getWidth() + " x " + image1.getWidth() +
                        "\nimage2: wd x ht " + image2.getWidth() + " x " + image2.getHeight());
                assertTrue("Shapes are not the same size", false);
                System.exit(1);
            }
            for (int r = 0; r < image1.getWidth(); r++) {
                for (int c = 0; c < image1.getHeight(); c++) {
                    //System.out.println(image1.getPixelReader().getArgb(r,c));

                    // if the pixel color is ... just using "-1000"
                    if( image1.getPixelReader().getArgb(r, c) < thresh ) {

                        scoreImg1++;
                    }
                    if( ( image2.getPixelReader().getArgb(r, c)) < thresh ) {
                        scoreImg2++;
                    }
                }
            }

            LOGGER.info("Txt Color score image1: " + scoreImg1 + ", Txt Color score image2: " + scoreImg2);

            if(scoreImg1 < ( scoreImg2 + 31) && (scoreImg1 > scoreImg2 - 31)) {
                return true;
            }


        } else {
            LOGGER.warning("Null image when comparing. image1 == null: " + (image1 == null) + ", image2 == null: " + (image2 == null));
        }
        return false;
    }

    /**
     * Due to gamma, and how some OS's/machines deal with screen color, the ARGB values may change
     * from test to test. This is a difference due to the background color
     * and Alpha. Alpha being transparency. Some OS's change the screen color due to the
     * ambient lighting available, or time of day. If there is an error and it is not due
     * to a missing shape, then check that the rCheck, gCheck, bCheck, values are correct in the calling
     * method above.
     * @param x pixel X location
     * @param y pixel Y location
     * @param msg Error message indicating which test or image the test is on
     * @param rCheck The expected red value of the pixel
     * @param gCheck The expected green value of the pixel
     * @param bCheck The expected blue value of the pixel
     * @throws Exception
     */
    public static void checkPixColor(int x, int y, String msg, int rCheck, int gCheck, int bCheck) throws Exception {
        Robot awtRobot = new Robot();
        java.awt.Color awtPixColor = awtRobot.getPixelColor( x,  y);


        int rPix = awtPixColor.getRed();
        int bPix = awtPixColor.getBlue();
        int gPix = awtPixColor.getGreen();

        boolean rBool = (rCheck > rPix - 10 && rCheck < rPix + 10 );
        boolean bBool = (bCheck > bPix - 10 && bCheck < bPix + 10 );
        boolean gBool = (gCheck > gPix - 10 && gCheck < gPix + 10 );
        System.out.println("\npix colors < r: " + rPix + "  g: " + gPix + "  b: " + bPix + " >");
        System.out.println("expecting   < r: " + rCheck + "  g: " + gCheck + "  b: " + bCheck + " >");

        assertTrue(msg, rBool && bBool && gBool);
    }


    /**
     * Returns the awt.Color from the pixel at x, y on
     * the screen
     * @param x
     * @param y
     * @return Returns an awt.Color object of the pixel
     * provided at the location provided in the arguments
     * @throws Exception
     */
    java.awt.Color getAWTPixColorFmScreen(double x, double y) throws Exception {

        Robot awtBot = new Robot();
        return awtBot.getPixelColor(20, 20);
    }
}
