/*
 *Controls the size of the scene in one class so that all of the scenes
 * are easily changed in one central class.
 */
package uicontrols;

import ch.qos.logback.classic.Level;
import com.sun.glass.ui.Screen;
import javafx.geometry.Point2D;
//import javafx.stage.Screen;

import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.prefs.BackingStoreException;

/**
 * Manages the height and width of windows
 *
 * @author Lowell Stadelman
 */
public abstract class SceneCntl {
      private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(SceneCntl.class);

      private static int deltaHt;
      private static int deltaWd;
      // Set by app
      // = d.width;
      private static int screenWd;
      private static int screenHt;
      private static int screenX;
      private static int screenY;
      private static final int defX = 0;
      private static final int defY = 0;
      private static final Point2D defaultXY = defaultStartXY();
      // Language
      private static ResourceBundle LANG_BUNDLE;
      // USER SETTINGS
      // default settings are the fields set in this class.
      private static final Properties userSettings = new Properties();
      private static final String propertiesFile = "/flashmonkey.properties";

      /**
       * Sets the app from the user's stored preferences.
       * @throws BackingStoreException
       */
      public static void setPref() throws BackingStoreException {
            // check that we are not larger than the screen.
            setScreenSize();
            //getStartXY();
            defaultAppSize();
            // set the user preferences from the file
            boolean load = loadUserSettings();
            setFromPreferences(load);
            setLangBundle();
      }


      /**
       * Sets the user settings from the
       * fields.
       * Called by this.onStop()
       */
      private static void buildUserSettings() {
            userSettings.setProperty("app-ht", Integer.toString(Box2D.APP_BOX.getHt()));
            userSettings.setProperty("app-wd", Integer.toString(Box2D.APP_BOX.getWd()));
            userSettings.setProperty("cell-ht", Integer.toString(Dim.CELL_HT.get()));
            userSettings.setProperty("right-cell-wd", Integer.toString(Dim.RIGHT_CELL_WD.get()));
            userSettings.setProperty("button-wd", Integer.toString(Dim.BUTTON_WD.get()));
            userSettings.setProperty("media-wd", Integer.toString(Dim.MEDIA_WD.get()));
            userSettings.setProperty("file-select-pane-wd", Integer.toString(Dim.FILE_SELECT_WD.get()));
            // LOWER SECTION HT exit buttons section
            userSettings.setProperty("south-bpane-ht", Integer.toString(Dim.SOUTH_BPANE_HT.get()));
            // LOWER SECTION HT gauges, l-r btn
            userSettings.setProperty("control-pane-ht", Integer.toString(Dim.CONTROL_PANE_HT.get()));
            userSettings.setProperty("consumer-pane-wd", Integer.toString(Dim.CONSUMER_PANE_WD.get()));
            userSettings.setProperty("consumer-pane-ht", Integer.toString(Dim.CONSUMER_PANE_HT.get()));
            userSettings.setProperty("right-cell-wd", Integer.toString(Dim.RIGHT_CELL_WD.get()));
            // sets if app read & edit stages are maximized 1 = true, 0 = false
            userSettings.setProperty("app-maximized", Dim.APP_MAXIMIZED.get() == 1 ? "1" : "0");
            Box2D[] bx = {Box2D.APP_BOX, Box2D.FORM_BOX};
            // AppBox is always left at default
            buildSceneProperties(bx);
      }

      /**
       * helper method to buildUserSettings
       * @param b
       */
      private static void buildSceneProperties(Box2D[] b) {
            for (int i = 0; i < b.length; i++) {
                  userSettings.setProperty(b[i].getHtName(), b[i].getHtStr());
                  userSettings.setProperty(b[i].getWdName(), b[i].getWdStr());
                  userSettings.setProperty(b[i].getXName(), b[i].getXStr());
                  userSettings.setProperty(b[i].getYName(), b[i].getYStr());
            }
      }

      /**
       * Sets preferences from the stored settings.
       * @param load True will load the boxes from settings, otherwise
       *          uses the default boxes.
       */
      private static void setFromPreferences(boolean load) {
            if (load) {
                  Dim.CELL_HT.set(Integer.parseInt(userSettings.getProperty("cell-ht")));
                  Dim.RIGHT_CELL_WD.set(Integer.parseInt(userSettings.getProperty("right-cell-wd")));
                  Dim.BUTTON_WD.set(Integer.parseInt(userSettings.getProperty("button-wd")));
                  Dim.MEDIA_WD.set(Integer.parseInt(userSettings.getProperty("media-wd")));
                  Dim.FILE_SELECT_WD.set(Integer.parseInt(userSettings.getProperty("file-select-pane-wd")));
                  Dim.SOUTH_BPANE_HT.set(Integer.parseInt(userSettings.getProperty("south-bpane-ht")));
                  Dim.CONTROL_PANE_HT.set(Integer.parseInt(userSettings.getProperty("control-pane-ht")));
                  Dim.CONSUMER_PANE_WD.set(Integer.parseInt(userSettings.getProperty("consumer-pane-wd")));
                  Dim.CONSUMER_PANE_HT.set(Integer.parseInt(userSettings.getProperty("consumer-pane-ht")));
                  // AppBox is always left at default
                  Box2D[] bx = {Box2D.APP_BOX, Box2D.FORM_BOX};
                  setFmScenePref(bx);
            }
      }

      /**
       * Helper method to setFromPreferences
       * @param b
       */
      private static void setFmScenePref(Box2D[] b) {
            for (int i = 0; i < b.length; i++) {
                  b[i].setWd(Integer.parseInt(userSettings.getProperty(b[i].getWdName())));
                  b[i].setHt(Integer.parseInt(userSettings.getProperty(b[i].getHtName())));
                  b[i].setX(Integer.parseInt(userSettings.getProperty(b[i].getXName())));
                  b[i].setY(Integer.parseInt(userSettings.getProperty(b[i].getYName())));
            }
      }

      /**
       * Saves user settings to file.
       * Called by onStop
       */
      private static void storeUserSettings() {
            String userDir = System.getProperty("user.home");
            String dirFile = userDir + propertiesFile;
            LOGGER.setLevel(Level.DEBUG);

            LOGGER.debug("Called storeUserSettings, dirFile: {}", dirFile);

            try (OutputStream out = new FileOutputStream(dirFile)) {
                  userSettings.store(out, "FlashMonkey Properties");
            } catch (FileNotFoundException e) {
                  e.printStackTrace();
            } catch (IOException e) {
                  e.printStackTrace();
            }
      }

      /**
       * Loads user settings from File.
       * Called by setPref()
       * @return return true if the file exists, If
       * false then do not load boxes in array.
       */
      private static boolean loadUserSettings() {
            String userDir = System.getProperty("user.home");
            File f = new File(userDir + propertiesFile);
            if (f.exists()) {
                  LOGGER.setLevel(Level.DEBUG);
                  LOGGER.debug("user settings file exists");
                  try (InputStream in = new FileInputStream(userDir + propertiesFile)) {
                        userSettings.load(in);
                        return true;
                  } catch (FileNotFoundException e) {
                        e.printStackTrace();
                  } catch (IOException e) {
                        e.printStackTrace();
                  }
            }
            return false;
      }

      private static void setLangBundle() {
            // When a new bundle is added. Add the country to the
            // array
            String[] c = {"uk", "us", "de"};
            int localIdx = 0;
            Locale def = new Locale("en", "us");
            Locale locale = Locale.getDefault();
            String country = locale.getCountry();
            ArrayList<String> countries = new ArrayList<>(c.length);
            countries.addAll(Arrays.stream(c).toList());
            // set the country or set as default US.
            locale = countries.contains(country) ? locale : def;
            LOGGER.debug("Printing system default counry: {}", country);

            LANG_BUNDLE = ResourceBundle.getBundle("demo-locale", locale);
      }

      private static void setScreenSize() {
            Screen s = Screen.getMainScreen();
      //      Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            screenWd = s.getWidth();  //d.width;
            screenHt = s.getHeight(); //d.height;
            screenX = s.getX();
            screenY = s.getY();
      }

      private static void defaultAppSize() {
            int ht = Box2D.APP_BOX.getHt();
            int wd = Box2D.APP_BOX.getWd();
            int h = ht + deltaHt > screenHt ? screenHt - deltaHt : ht;
            int w = wd + deltaWd > screenWd ? screenWd - deltaWd : wd;
            Box2D.APP_BOX.setHt(h);
            Box2D.APP_BOX.setWd(w);
      }


      /**
       * Calculates the center width based on the
       * App center.
       *
       * @return
       */
      public static int getCenterWd() {
            return Box2D.APP_BOX.getWd() - 8;
      }

      public static double calcCenterHt(double topHt, double btmHt, double currentHt) {
            return currentHt - (topHt + btmHt);
      }

      /**
       * Calculates cell hieght based on
       * the App centerPaneHeight
       *
       * @return
       */
      public static int calcCellHt() throws IllegalArgumentException {
            if (Dim.CFP_CENTER_HT.get() == 0) {
                  throw new IllegalArgumentException("CenterHeight cannot be 0. Be sure to set CenterHeight before" +
                      "usin this method.");
            }
            return Dim.CFP_CENTER_HT.get() / 2;
      }

      public static Point2D getDefaultXY() {
            return defaultXY;
      }

      public static int getBottomHt() {
            return Dim.CONTROL_PANE_HT.get() + Dim.SOUTH_BPANE_HT.get();
      }

      public static int getConsumerPaneWd() {
            return Dim.CONSUMER_PANE_WD.get();
      }

      public static int getConsumerPaneHt() {
            return Dim.CONSUMER_PANE_HT.get();
      }

      public static int getRightCellWd() {
            return Dim.RIGHT_CELL_WD.get();
      }

      public static int getButtonWidth() {
            return Dim.BUTTON_WD.get();
      }

      public static int getFileSelectPaneWd() {
            return Dim.FILE_SELECT_WD.get();
      }

      public static int getAppHt() {
            return Box2D.APP_BOX.getHt();
      }


      public static Box2D getAppBox() {
            return Box2D.APP_BOX;
      }

//      public static Box2D getReadFlashBox() {
//            return Box2D.READFLASH_BOX;
//      }

      public static Box2D getFormBox() {
            return Box2D.FORM_BOX;
      }


      /**
       * Ensure that setScreenSize() is called before using
       * this getter.
       *
       * @return the width of the screen.
       */
      public static int getScreenWd() {
            return screenWd;
      }

      /**
       * Ensure that setScreenSize() is called before using
       * this getter.
       *
       * @return
       */
      public static int getScreenHt() {
            return screenHt;
      }

      /**
       * Sets the default XY through detection of the screen.
       * <p><b>NOTE: </b> Makes use of sun Screen not javaFX which is
       * not accessible in a modular system. </p>
       * @return Sets the
       *        startXY in an atempt to set the app to the center of the
       *        screen. From centerX - 250, from centerY - 406
       */
      private static Point2D defaultStartXY() {
      //      Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            // The default minXY for the app.
            double screenX = Screen.getMainScreen().getPlatformWidth() / 2 -250;//(screenBounds.getWidth() / 2) - (250);
            double screenY = Screen.getMainScreen().getPlatformHeight() / 2 -406;//(screenBounds.getHeight() / 2) - (406);
            return new Point2D(screenX, screenY);
      }

      public static void onStop() {
            //LOGGER.debug("app ht: {}, appWd: {}", appHt, appWd);
            buildUserSettings();
            storeUserSettings();
      }

      /* **************
            ENUMS
      **************** */

      public enum Dim {
            CFP_CENTER_HT(600),
            CELL_HT(300),
            RIGHT_CELL_WD(100),
            BUTTON_WD(200),
            MEDIA_WD(128),
            FILE_SELECT_WD(400),
            SOUTH_BPANE_HT(128),
            CONTROL_PANE_HT(160),
            CONSUMER_PANE_WD(1264),
            CONSUMER_PANE_HT(754),
            APP_MAXIMIZED(0);

            private int value;
            Dim(int v) {
                  this.value = v;
            }

            /**
             * Delays setting the dimension by 300 milliseconds to avoid
             * conflicts created by macOS. E.G. JavaFX Mac resets the Stage to
             * non-maximized if the root scene is changed. The delay is needed
             * to prevent setting the new scene to the minimized xy and size.
             * @param v
             */
            public void set(int v) {
                        this.value = v;
            }

            public int get() {
                  return this.value;
            }
      }

      public enum Box2D {
//            READFLASH_BOX("readflash", 500, 800, 0, 0),
            APP_BOX("app", 500, 810, defX, defY),
            FORM_BOX("form", 500, 810, defX, defY);

            private int wd;
            private int ht;
            private int x;
            private int y;
            private final String name;// The read and edit stage

            Box2D(String name, int wd, int ht, int x, int y) {
                  boolean zero = (x + y) == 0;
                  this.name = name;
                  this.wd = wd;
                  this.ht = ht;
                  this.x = zero ? (int) defaultXY.getX() : x;
                  this.y = zero ? (int) defaultXY.getY() : y;
            }

            Box2D(String name, String wd, String ht, String x, String y) {
                  this.name = name;
                  this.wd = Integer.parseInt(wd);
                  this.ht = Integer.parseInt(ht);
                  this.x = Integer.parseInt(x);
                  this.y = Integer.parseInt(y);
            }

            public void setAll(int x, int y, int wd, int ht) {
                  boolean zero = x + y == 0;
                  this.x = zero ? (int) defaultXY.getX() : x;
                  this.y = zero ? (int) defaultXY.getY() : y;
                  this.wd = wd;
                  this.ht = ht;
            }

            // ** width ** //
            public int getWd() {
                  return wd;
            }

            public void setWd(int wd) {
                  this.wd = wd;
            }

            public String getWdName() {
                  return name + "-wd";
            }

            public String getWdStr() {
                  return Integer.toString(wd);
            }

            // ** height ** //
            public int getHt() {
                  return ht;
            }

            public void setHt(int ht) {
                  this.ht = ht;
            }

            public String getHtName() {
                  return name + "-ht";
            }

            public String getHtStr() {
                  return Integer.toString(ht);
            }

            // ** X ** //
            public int getX() {
                  return x;
            }

            public void setX(int x) {
                  this.x = x;
            }

            public String getXName() {
                  return name + "-x";
            }

            public String getXStr() {
                  return Integer.toString(x);
            }

            // ** Y ** //
            public int getY() {
                  return y;
            }

            public void setY(int y) {
                  this.y = y;
            }

            public String getYName() {
                  return name + "-y";
            }

            public String getYStr() {
                  return Integer.toString(y);
            }
      }
}
