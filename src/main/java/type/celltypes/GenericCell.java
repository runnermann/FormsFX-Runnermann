package type.celltypes;

import fileops.DirectoryMgr;
import flashmonkey.ReadFlash;
import javafx.scene.layout.Pane;

import javax.print.DocFlavor;

/**
 * Assists with building and selecting different cell types in
 * the UI.
 * @author Lowell Stadelman
 */
public class GenericCell//  extends GenericSection
{
    AVCell mCell;
    public CanvasCell cCell;
    public TextCell tCell;


    /**
     * Returns the cell containing a media that is sized according to the scale givein in the
     * parameters.
     * @param cellType Media (video or audio) = 'M'/'m' or Canvas / Image Drawing = 'C' 'c' 'D' 'd'
     * @param pane
     * @param paneWd
     * @param paneHt
     * @param scaleable If the image or media should rescale if the container it is in
     *                  is resized after it's initial size
     * @param fileNames Files as an array. Expects the image or media to be at idx [0]
     * @return Returns the cell containing a media
     */
    public Pane cellFactory(char cellType, Pane pane, int paneWd, int paneHt, boolean scaleable, String ... fileNames)
    {
        final String CANVAS = DirectoryMgr.getMediaPath('C');//"../flashMonkeyFile/image/";
        final String MEDIA = DirectoryMgr.getMediaPath('M');//

        switch (cellType)
        {
            case 'M': // Audio or Video doubleCell section
            case 'm': // Audio or video for singleCell section
            {
                mCell = new AVCell();
                return mCell.buildCell( paneWd, paneHt, MEDIA + fileNames[0]);
            }
            case 'c': // image or both. for singleCell section
            case 'd': // drawing only. singleCell
            case 'C': // image, or both. for doubleCell section
            case 'D': // drawing only. doubleCell
            default:
            {
                String canvasPaths[] = new String[2];
                int i = 0;
                cCell = new CanvasCell();

                for(String s : fileNames)
                {
                    canvasPaths[i++] = CANVAS + s;
                }
                // the image cell on the right.
                return cCell.buildCell(pane, paneWd, paneHt, scaleable, canvasPaths);
            }
        }
    }

    /**
     * Builds the left TextCell
     * @param text
     * @return
     */
    public Pane cellFactory(String text, int paneWd, int paneHt, int numSections, boolean isEqual, double otherHt) {
        tCell = new TextCell();
        return tCell.buildCell(text, "", paneWd, paneHt, numSections, isEqual, otherHt);
    }

}
