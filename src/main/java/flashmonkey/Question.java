/*
 * AUTHOR: Lowell Stadelman
 */

package flashmonkey;

import java.io.*;

/**
 * Base Question class. Contains only text. See multimedia/QuestionMM for
 * multi-media methods and fields.
 *
 * @author Lowell Stadelman
 */

@SuppressWarnings("unchecked")
public abstract class Question implements Serializable, Comparable
{
   private static final long serialVersionUID = FlashMonkeyMain.VERSION;

    //*** CONSTANTS ***
    public static final String DEFAULT_QUESTION = "";

    private String qText;

    // DESCRIPTION: DEFAULT CONSTRUCTOR
    public Question()
    {
        this.qText = DEFAULT_QUESTION;
    }

    // DESCRIPTION: FULL CONSTRUCTOR
    public Question(String q)
    {
        this.qText = q;
    }

    // DESCRIPTION: SHALLOW COPY CONSTRUCTOR
    public Question(Question original)
    {
        if(original != null)
        {
            this.qText = original.qText;
        }
        else
        {
           //System.out.println("Error: Cannot copy a null object");
            System.exit(0);                 
        }
    }

    /*** setters ***/
    public void setQText(String txt)
    {
        this.qText = txt;
    }

    /*** getters ***/
    public String getQText()
    {
        return this.qText;
    }

    public String toString()
    {
        return "Question: " + this.qText;
    }

    // DESCRIPTION: Equals method. Tests the text only.
    public boolean equals(Object other)
    {
        Question otherQuestion;
        
        if(other == null)
        {
            return false;
        }
        else if(this.getClass() != other.getClass())
        {
            return false;
        }
        else
        {
            otherQuestion = (Question) other;
            return this.qText.equalsIgnoreCase(otherQuestion.qText);
        }
    }
    
    /**
	 * Default compareTo() that calls Strings compareTo method.
	 * All other number comparisons should be done with a comparator.
	 * @param otherQuestion, the objected being compared to.
	 * @return -1 if the other string is larger.
	 * 			0 if the other string is equal.
	 * 			1 if the other string is smaller.
	 */
    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(Object otherQuestion) 
    {
        return this.qText.compareTo(((Question)otherQuestion).qText);
    }

}
