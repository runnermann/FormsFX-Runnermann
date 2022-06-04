package multimedia;

import java.io.Serializable;
import java.util.ArrayList;

import flashmonkey.Answer;
import flashmonkey.FlashMonkeyMain;

/**
 * @author Lowell Stadelman
 */
public class AnswerMM extends Answer implements Serializable, Comparable
{
    private static final long serialVersionUID = FlashMonkeyMain.VERSION;

    /** VARIABLES **/
    private char aType;
    private String[] aFiles;

    /**
     * Default constructor
     */
    public AnswerMM()
    {
        super("", 0, DEF_LIST);
        this.aType = 't';
    }

    /**
     * Constructor Version MultiMedia 2018-05-25
     * Does not store multi media in the object. Stores the
     * path to the MM file, Constants provide the multi media type
     * to be created later in the view pane
     * @param txt
     * @param type  M = media (video and audio) , D = drawing, S = shape, defualt = null
     * @param ansSet
     */
    public AnswerMM(String txt, int aNum, char type, ArrayList<Integer> ansSet, String[] fileAry)
    {
        super(txt, aNum, ansSet);
        this.aType = type;
        this.aFiles = fileAry;
    }

    // CopyConstructor
    public AnswerMM(AnswerMM original)
    {
        super();
        this.setAText(original.getAText());
        this.setANumber(original.getANumber());
        this.setAnswerSet( new ArrayList<>(original.getAnswerSet())); // is this wrong?
        this.aFiles = new String[] {original.aFiles[0], original.aFiles[1]};
        this.aType = original.aType;
    }


    /*** SETTERS ***/

    public void setAType(char type) {
        this.aType = type;
    }

    public void setAFiles(String[] files) {
        this.aFiles = files;
    }

    /**
     * Sets the media Type. Use the media type chars
     * I = images, D = drawings, A = audio, or V = video
     * @param type The char representing the type of media
     */
    protected void setMediaType(char type)
    {
        this.aType = type;
    }


    /*** GETTERS ***/

    /**
     * Returns the media Type. The media type chars
     * I = images, D = drawings, A = audio, or V = video
     * @return returns the media type
     */
    public char getAType()
    {
        return this.aType;
    }

    /**
     * Returns the files associated with this answer.
     * @return
     */
    public String[] getAFiles() { return this.aFiles; }

    @Override
    public boolean equals(Object obj) {
        if(obj != null) {
            AnswerMM mm = (AnswerMM) obj;
            return ( this.getAText().equals(mm.getAText()) );
        } else {
            //System.out.println("Recieved null input in AnswerMM equals :(");
            return false;
        }
    }
}
