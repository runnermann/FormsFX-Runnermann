package type.cardtypes;

//@todo implement the and answer actions

import flashmonkey.FMTransition;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import type.sectiontype.GenericSection;

/**
 * @author Lowell Stadelman
 */

public class DoubleVertCard extends GenericCard
{
    public HBox qBox = new HBox();
    public HBox ansBox = new HBox();
    HBox hBox = new HBox();

    //@Override
    public HBox retrieveCard(String qTxt, char upperType, String aTxt, char lowerType, String[] qFiles, String[] aFiles)
    {
        GenericSection gs = GenericSection.getInstance();

        qBox = gs.sectionFactory(qTxt, upperType, 1, true, 0, qFiles);
        ansBox = gs.sectionFactory(aTxt, lowerType, 1, true, 0, aFiles);
        hBox.getChildren().addAll(qBox, ansBox);

        FMTransition.setQRight( FMTransition.transitionFmRight(qBox));
        FMTransition.setQLeft( FMTransition.transitionFmLeft(qBox));
        FMTransition.setAWaitTop( FMTransition.waitTransFmTop(ansBox, 0, 300, 350));

        return hBox;
    }
}
