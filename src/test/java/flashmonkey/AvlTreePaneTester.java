package flashmonkey;

import authcrypt.user.EncryptedAcct;
import core.ImageUtility;
import core.RobotUtility;
import core.ShapeUtility;
import fileops.utility.Utility;
import type.draw.shapes.FMRectangle;
import type.draw.shapes.GenericShape;
import fileops.DirectoryMgr;
import fileops.FileOpsUtil;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.Ignore;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import type.celleditors.DrawTools;
import type.celleditors.SnapShot;
import type.celltypes.MediaPopUp;
import type.testtypes.QandA;
import type.tools.imagery.Fit;
import uicontrols.FxNotify;
import uicontrols.UIColors;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//import org.junit.Test;
//import static flashmonkey.FlashCardOps.buildTree;
//import static flashmonkey.FlashCardOps.getFlashList;
//import static flashmonkey.FlashMonkeyMain.buildTreeWindow;
//import static org.junit.Assert.assertTrue;

public class AvlTreePaneTester {


    @Test
    public void studyDeckNameTenDat()
    {
        // Deck selected,
        // !! SCENE CHANGE !! to menu

        // click on study button
        //ReadFlash rf = new ReadFlash();
        FlashCardOps.getInstance().setDeckFileName("ten.dec");

 //       assertTrue( FlashCardOps.getInstance().getDeckFileName().equals("ten.dec"), "Deck name not ten.dec");

        FlashCardOps.getInstance().setDeckFileName("ten");
 //       assertTrue(FlashCardOps.getInstance().getDeckFileName().equals("ten.dec"), "Deck name not ten.dec when set to \"ten\"");
    }
    /*
    public static void main(String[] args) {

        ReadFlash.setDeckName("ten.dec");

        if(getFlashList() == null || getFlashList().isEmpty()) {
            FLASH_CARD_OPS.refreshFlashList();
        } else {
            FLASH_CARD_OPS.saveFlashList();
            FLASH_CARD_OPS.refreshFlashList();
        }

        buildTree();
        buildTreeWindow();


    }
    */
}
