package type.cardtypes;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import type.celltypes.AVCell;
import type.celltypes.CanvasCell;
import type.celltypes.TextCell;

/**
 *
 *
 *     @author Lowell Stadelman
 */
public class GenericCard<T extends GenericCard>
{

    public DoubleHorzCard dhc;// = new DoubleHorzCard();
    public DoubleVertCard dvc;


    /**
     * Creates a        flashCard in the layout specified.
     *  layout =        'S' = single card layout, 'D' = double horizontal card, and d = double vertical card
     * @param qTxt      The question or upper text
     * @param upType    The upper type 't' = text, ...
     * @param aTxt      The answer text or upper text
     * @param lowerType The lower type 't' = text, ...
     * @param layout    The card layout type.
     * @param qFiles    The file paths for multi-media.
     * @param aFiles    The file paths for multi-media.
     * @return Returns a Pane.
     */
    public Pane cardFactory(String qTxt, char upType, String aTxt, char lowerType, char layout, String[] qFiles, String[] aFiles)
    {
        switch (layout)
        {
            case 'S': // SingleCellCard
            {
                SingleCard s = new SingleCard();
                return s.retrieveCard(qTxt, upType, qFiles);
            }
            case 'D': // two sections stacked vertically
            default:
            {
                dhc = new DoubleHorzCard();
                return dhc.retrieveCard(qTxt, upType, aTxt, lowerType, qFiles, aFiles);
            }
            case 'd': // two sections side by side
            {
                dvc = new DoubleVertCard(); // up = left & lower = right
                return dvc.retrieveCard(qTxt, upType, aTxt, lowerType, qFiles, aFiles);
            }
        }
    }
}
