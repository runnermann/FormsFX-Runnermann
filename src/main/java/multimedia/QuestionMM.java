/*
 * AUTHOR: Lowell Stadelman
 */

package multimedia;

import java.io.Serializable;


/*** IMPORTS ***/
import flashmonkey.FlashMonkeyMain;
import flashmonkey.Question;

/**
 * @author Lowell Stadelman
 */
public class QuestionMM extends Question implements Serializable, Comparable
{
    private static final long serialVersionUID = FlashMonkeyMain.VERSION;

    /** VARIABLES **/
    private char qType;
    private String[] qFiles;

    /** DEFAULT CONSTRUCTOR **/
    public QuestionMM()
    {
        super();
        this.qType = 't';
    }


    /** FULL CONSTRUCTOR **/
    //public QuestionMM(String q, int type, String fileName)
    public QuestionMM(String q, char type, String[] fileAry)
    {
        super(q);
        this.qType = type;
        this.qFiles = fileAry;
    }

    // DESCRIPTION: SHALLOW COPY CONSTRUCTOR
    public QuestionMM(QuestionMM original)
    {
        if(original != null)
        {
            this.setQText(original.getQText());
            this.qType = original.qType;
            this.qFiles = new String[] {original.qFiles[0], original.qFiles[1]};
        }
        else
        {
           //System.out.println("Error: Cannot copy a null object");
            System.exit(0);
        }
    }

    /** SETTERS **/

    public void setQType(char type) {
        this.qType = type;
    }

    public void setQFiles(String[] files) {
        this.qFiles = files;
    }


    /*** GETTERS ***/

    /**
     * Returns the files associated with this
     * question.
     * @return
     */
    public String[] getQFiles()
    {
        return this.qFiles;
    }

    /**
     * Returns the media Type. The media type chars
     * I = images, D = drawings, A = audio, or V = video
     * @return returns the media type
     */
    public char getQType()
    {
        return this.qType;
    }

    /**
     * toStsring method
     * @return Returns the question
     */
    public String toString()
    {
        return "Question: " + this.getQText();
    }


}