/*
 *Controls the size of the scene in one class so that all of the scenes
 * are easily changed in one central class. 
 */
package uicontrols;

import flashmonkey.CreateFlash;
import flashmonkey.ReadFlash;
import javafx.geometry.Point2D;
import javafx.stage.Screen;

import javafx.geometry.Rectangle2D;

import java.awt.*;
import java.io.File;

/**
 * Manages the height and width of windows
 *
 * @author Lowell Stadelman
 */
public abstract class SceneCntl
{
    // iPhone 6? screen dimensions
    //private static int ht = 440;
    // Larger screen for mathCard
    private static int ht = 810;
    private static int wd = 500;
    private static int cellHt = 300;
    private static int rightCellWd = 100;
    private static int buttonWidth = 200;
    private static int editorWd = wd;
    private static int editorHt = ht;
    private static int editorSectionHt = 300;
    private static int mediaWidth = 128;
    private static int fileSelectPaneWd = 400;
    // LOWER SECTION HT buttons gauges section
    private static int southBPaneHt = 128;
    //private static Rectangle2D screenBounds;
    //private static Point2D startXY;
    //private Toolkit tk = Toolkit.getDefaultToolkit();
    //private Dimension d = tk.getScreenSize();
    private static int screenWt;// = d.width;
    private static int screenHt; //= d.height;
    private static int consumerPaneWd = 1264;
    private static int consumerPaneHt = 700;


    public SceneCntl() {

    }

    /**
     * Constructor
     * @param width width of the scene
     * @param height height of the scene
     */
    public SceneCntl(int width, int height)
    {
        wd = width;
        ht = height;
        //screenBounds = Screen.getPrimary().getVisualBounds();
    }
    /**
     * sets the height of the scene
     * @param ht 
     */
    public static void setHt(int ht)
    {
        ht = ht;
    }

    /**
     * sets the height of the cells/sections
     * @param cHt
     */
    public static void setCellHt(int cHt) {
        cellHt = cHt;
    }
    /**
     * sets the width of the scene
     * @param wd 
     */
    public static void setWd(int wd)
    {
        wd = wd;
    }

    public static void setButtonWidth( int wd ) { buttonWidth = wd; }
    
    /**
     * The controls pane ht / lower section
     * @return Ht for southBPane
     */
    public static int getSouthBPaneHt() {
        return southBPaneHt;
    }

    /**
     * The controls pane ht / lower section
     * @param southBPaneHt
     */
    public static void setSouthBPaneHt(int southBPaneHt) {
        SceneCntl.southBPaneHt = southBPaneHt;
    }
    
    /**
     * returns the height of the scend
     * @return ht
     */
    public static int getHt()
    {
        return ht;
    }

    /**
     * returns width
     * @return width of the scene
     */
    public static int getWd()
    {
        return wd;
    }

    public static int getCenterWd() {
        int width;
        if(ReadFlash.getInstance().getRPCenter() != null) {
            width = (int) ReadFlash.getInstance().getRPCenter().getBoundsInLocal().getWidth();
        } else {
            width = (int) CreateFlash.getInstance().getCFPCenter().getBoundsInLocal().getWidth();
        }
        if(width == 0) {
            width = wd;
        }
        return width - 8;
    }

    public static double calcCenterHt(double topHt, double btmHt, double currentHt) {
        return currentHt - (topHt + btmHt);
    }

    public static int getCellHt() {
        int height;
        if(ReadFlash.getInstance().getRPCenter() != null) {
            height =(int) ReadFlash.getInstance().getRPCenter().getBoundsInLocal().getHeight() / 2;
        } else {
            height =(int)CreateFlash.getInstance().getCFPCenter().getBoundsInLocal().getHeight() / 2;
        }
        if(height == 0) {
            height = cellHt;
        }
        return height;
    }

    public static int getConsumerPaneWd() { return consumerPaneWd; }

    public static int getConsumerPaneHt() { return consumerPaneHt; }

    public static int getRightCellWd() { return rightCellWd; }

    public static int getButtonWidth() { return buttonWidth; }

    public static int getEditorSectionHt() { return editorSectionHt; }

    public static int getEditorHt() { return editorHt; }

    public static int getEditorWd() { return editorWd; }

    public static int getMediaWidth() { return mediaWidth; }
    
    public static int getFileSelectPaneWd() {return fileSelectPaneWd; }

    public static int getScreenWd() {
        if( screenWt == 0 ) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension d = tk.getScreenSize();
            screenHt = d.height;
           return screenWt = d.width;
        }
        return screenWt;
    }

    /**
     * If the screenHt is not set by another EncryptedUser.EncryptedUser, you must ensure
     * that this field is set by your EncryptedUser.EncryptedUser.
     * @return
     */
    public static int getScreenHt() {
        if(screenHt == 0) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension d = tk.getScreenSize();
            screenWt = d.width;
            return screenHt = d.height;
        }
        return screenHt;
    }




    //public static Rectangle2D getBounds() { return screenBounds; }

    /**
     * Positions the start XY location so that the
     * start location is either the default, or
     * the users last position.
     * @return
     */
    public static Point2D getStartXY() {

        Point2D startXY = defaultStartXY();

        File check = new File("/src/resources/usersettings");
        if(check.exists()) {
            // @TODO complete app startXY
            // startXY = ....
        }
        return startXY;
    }

    private static Point2D defaultStartXY() {

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        // set the offset from the right edge of the screen
        double xOffSet = 20;
        // set the offset from the top of the screen
        double yOffSet = 30;
        // The default minXY for the app.
        double screenX = (screenBounds.getWidth() - wd) / 4 * 3 ;
        double screenY = ( screenBounds.getHeight() / 2 ) - ( ht / 2 );
        return new Point2D(screenX, screenY);
    }
}
