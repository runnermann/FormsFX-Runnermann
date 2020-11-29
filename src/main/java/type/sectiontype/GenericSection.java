package type.sectiontype;

/**
 * The abstract class to for section views.
 *
 * @author Lowell Stadelman
 */

import javafx.scene.layout.HBox;

public class GenericSection
{

    private static GenericSection CLASS_INSTANCE;

    private GenericSection() { /* default constructor */}

    public static GenericSection getInstance() {
        if(CLASS_INSTANCE == null) {
            CLASS_INSTANCE = new GenericSection();
        }
        return CLASS_INSTANCE;
    }
    /**
     * Abstract method, Must provide a left cell type, the string that is either text for a text area,
     *  or filePath/fileName for a multi-media file. Will return a singleCell or section view if the
     *  rightType is set to 'N' and data in rtTxt is ignored.
     * @param txt       Text in the TextArea
     * @param mmType    The multi-media.
     * @param path      The filePath/fileName if multi-media.
     * @return Return a node
     */
    //protected abstract Node sectionView(String txt, char mmType, String path);

    /**
     * Creates a section, either single or double, with cells
     * populated with data. If rtType == 'N' then it is a single
     * cell section.

     * @param txt      The text in a textArea
     * @param cType    The type of cell. if mmType == 't' then the single section is text only,
     *                  any other type that is in a single section is represented by the lower case
     *                  equivelant of what they are.
     * @param path      The filePath w/ fileName for a file
     *
     * @return  Returns E as a VBox.
     */

    public HBox sectionFactory(String txt, char cType, int numHSections, boolean isEqual, double otherHt, String ... path)
    {
        switch(cType)
        {
            case 'C':   // image
            case 'D':   // drawing
            case 'M':   // media audio or video
            {
                DoubleCellSection s = new DoubleCellSection();
                return s.sectionView( txt, cType, numHSections, isEqual, otherHt, path);
            }
            // Single section
            case 'c': // image
            case 'd': // drawing
            case 'm': // media
            case 't': // text
            default:
            {
                SingleCellSection s = new SingleCellSection();
                return s.sectionView( txt, cType, numHSections, isEqual, otherHt, path);
            }
        }
    }
}
