package type.cardtypes;


import flashmonkey.FMTransition;
import fmannotations.FMAnnotations;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import type.celltypes.CellLayout;
import type.celltypes.GenericCell;
import type.sectiontype.GenericSection;



/**
 * @author Lowell Stadelman
 */
public class DoubleHorzCard extends GenericCard
{
    private HBox qBox = new HBox();
    private HBox ansBox = new HBox();

    //private VBox vBox = new VBox();
    private GridPane gPane;

    //@Override
    public GridPane retrieveCard(String qTxt, CellLayout upperType, String aTxt, CellLayout lowerType, String[] qFiles, String[] aFiles)
    {
        gPane = new GridPane();
        gPane.setVgap(2);

        // IS THIS SHOWING IN THE COMPARISION?
        GenericSection gs = GenericSection.getInstance();
        qBox = gs.sectionFactory(qTxt, upperType, 2, true, 0, qFiles);
        ansBox = gs.sectionFactory(aTxt, lowerType, 2, true, 0, aFiles);
        ansBox.setAlignment(Pos.CENTER);

        gPane.addRow(2, ansBox);
        gPane.addRow(1, qBox);

        FMTransition.setQRight( FMTransition.transitionFmRight(qBox));
        FMTransition.setQLeft( FMTransition.transitionFmLeft(qBox));
        FMTransition.setAWaitTop( FMTransition.waitTransFmTop(ansBox, 0, 300, 350));

        return gPane;
    }
}
