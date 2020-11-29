package type.sectiontype;

import flashmonkey.FlashMonkeyMain;
import flashmonkey.ReadFlash;
import uicontrols.SceneCntl;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import type.celltypes.GenericCell;

/**
 * @author Lowell Stadelman
 */
public class DoubleCellSection //extends GenericSection
{
    private Pane leftCell = new Pane();
    private Pane rightCell = new Pane();
    private HBox sectionHBox = new HBox();


    //@Override
    public HBox sectionView(String txt, char type, int numHSections, boolean isEqual, double otherHt, String ... fileName)
    {
        GenericCell gc = new GenericCell();

       //System.out.println("DoubleCellSection.sectionView() ");
        int wd = SceneCntl.getCenterWd();
        int ht = (int) ReadFlash.getInstance().getMasterBPane().getHeight();
        // text cell
        leftCell = gc.cellFactory(txt, wd, ht, numHSections, isEqual, otherHt);
        rightCell.getChildren().clear();
        // Set the rightCell initial width and height from SceneCntl
        rightCell = gc.cellFactory(type, rightCell, SceneCntl.getRightCellWd(), SceneCntl.getCellHt(), false, fileName);
        rightCell.setMinWidth(SceneCntl.getRightCellWd());

//        HBox.setHgrow(leftCell, Priority.ALWAYS);

       //System.out.println("DoubleCell.sectionView filename: image = " + fileName[0] + "; shapes = " +fileName[1]);
       //System.out.println("DoubleCell.sectionView type: " + type);
        sectionHBox.getChildren().addAll(leftCell, rightCell);
        //sectionHBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Set the initial section height
        double no = SceneCntl.calcCenterHt(30, 214, FlashMonkeyMain.getWindow().getHeight());
        sectionHBox.setPrefHeight(no / numHSections);
        leftCell.setPrefHeight(no / numHSections);

        leftCell.setPrefWidth(FlashMonkeyMain.getWindow().getWidth() - 124);

        // RESPONSIVE SIZING for width and height
        ReadFlash.getInstance().getMasterBPane().widthProperty().addListener((obs, oldval, newVal) -> {
            leftCell.setMinWidth(newVal.doubleValue() - 108);
            leftCell.setMaxWidth(newVal.doubleValue() - 108);
        });

        ReadFlash.getInstance().getMasterBPane().heightProperty().addListener((obs, oldval, newval) -> {
            double val = newval.doubleValue() / numHSections;
            val -= 214 / numHSections; // grrrrrr!
            sectionHBox.setPrefHeight(val);
            sectionHBox.setMaxHeight(val);
            leftCell.setPrefHeight(val);
            leftCell.setMaxHeight(val);
        });

        return sectionHBox;
    }


    /**
     * Returns the HBox containing this section.
     * @return Returns the HBox containing this section
     */
    public HBox getSectionHBox()
    {
        return sectionHBox;
    }

}
