/*
 * AUTHOR: Lowell Stadelman 
 */

package flashmonkey;

/*******************************************************************************
 * Version MultiMedia 2018-05-25
 * CLASS DESCRIPTION: An answer consists of a String "answer", and a string path. The
 * String path is a constant which is used later to identify the MultiMedia type.
 * 
 * To comply with the strategy that enables multiple answers to be correct
 * for this question. An ArrayList of other possible correct answers
 * is provided for comparison.
 * Sets added. 1/14/2016
 *
 * Version Stylable Text 2022-05-12
 * Adding the ability to style the text and paragraphs using RichText. Specifically
 * the miscFX.RichTextFX API downloaded and dynamically appended as an API library
 * from the Maven Repo.
 *
 * IMPLEMENTS Serializable, comparable
 *
 * @author Lowell Stadelman
 *
 ******************************************************************************/

import richtextfm.model.*;
import org.reactfx.util.Either;

import type.richtext.Hyperlink;
import type.richtext.HyperlinkOps;
import type.richtext.ParStyle;
import type.richtext.TextStyle;

import java.io.*;
import java.util.ArrayList;


@SuppressWarnings("unchecked")
public abstract class Answer implements Serializable, Comparable
{
   private static final long serialVersionUID = FlashMonkeyMain.VERSION;

    /*** CONSTANTS ***/
    public static final ArrayList<Integer> DEF_LIST = new ArrayList<>(1);
    private final static TextOps<String, TextStyle> styledTextOps = SegmentOps.styledTextOps();
    private final static HyperlinkOps<TextStyle> hyperlinkOps = new HyperlinkOps<>();
    
    /*** VARIABLES ***/
    private String aText;

    StyledDocument<ParStyle, Either<String, Hyperlink>, TextStyle> doc;
    // ANumber is the index number in flashList. It is used
    // as the anchor for a flashCard in flashList during a session.
    // It is set to it's index number during build tree.
    private int aNumber;
    
    /*** ARRAY ***/
    private ArrayList<Integer> answerSet; // a list of possible questions that 
                                          // this could be an answer for.
    /**
     * Default constructor
     */
    public Answer() {
        //GenericStyledArea line 687

   //     this.doc = new GenericEditableStyledDocument<ParStyle, TextStyle, SegmentOps>(ParStyle.EMPTY, TextStyle.EMPTY, SegmentOps.styledTextOps());
        // this.doc = new GenericEditableStyledDocument<>(initialParagraphStyle, initialTextStyle, segmentOps), segmentOps, preserveStyle, nodeFactory)
        //this.doc = styledTxtArea.getDocument();
        this.aText = "";
        this.aNumber = 0;
    }


    /**
     * Full constructor
     * @param txt The answer text
     * @param aNum The cards number in the stack, used as a referance
     * @param ansSet The set of questions this answer will also be correct for
     */
    public Answer(String txt, int aNum, ArrayList<Integer> ansSet)
    {
        this.aText = txt;
        this.aNumber = aNum;
        this.answerSet = ansSet;
    }

    /**
     * Creates a Answer Object with an empty ArrayList
     * @param txt
     * @param aNum
     */
    public Answer(String txt, int aNum) {
        if(txt == null) {
            aText = "";
        }
        else {
            this.aText = txt;
        }
        this.aNumber = aNum;
        this.answerSet = new ArrayList<>();
    }

    
    // DESCRIPTION: Shallow Copy Constructor
    public Answer(Answer original) {
        if(original == null) {
            System.err.println("ERROR: Answer class Copy Constructor given NULL"
                    + "Exiting...");
            System.exit(0);
        }
        else {
            this.aText = original.aText;
            this.aNumber = original.aNumber;
            this.answerSet = new ArrayList<>(original.answerSet);
        }
    }


    //*** SETTERS ***
    
    public void setAText(String txt)
    {
        this.aText = txt;
    }

    public void setANumber(int aNum)
    {
        this.aNumber = aNum;
    }

    /**
     * The set of index's of all correct possible answers.
     * May include this answer.
     * @param ansSet
     */
    public void setAnswerSet(ArrayList<Integer> ansSet)
    {
        this.answerSet = new ArrayList<>(ansSet);
    }

    // *** GETTERS ***

    public String getAText() {
        return this.aText;
    }

    protected int getANumber() {
        return this.aNumber;
    }
    
    protected ArrayList<Integer> getAnswerSet() {
        return new ArrayList<>(answerSet);
    }


    // *** OTHERS ***

    /**
     * toString method
     * @return this answer
     */
    public String toString() {
        return this.aText;
    }

    /**
     * equals method
     * @param other
     * @return
     */
    public boolean equals(Answer other) {
        return this.aText.equalsIgnoreCase(other.aText);
    }
    
    
    /**
	 * Default compareTo() that calls Strings compareTo method.
	 * All other number comparisons should be done with a comparator.
	 * @param otherAnswer, the objected being compared to.
	 * @return -1 if the other answer is larger.
	 * 			0 if the other answer is equal.
	 * 			1 if the other answer is smaller.
	 */
    @Override
    public int compareTo(Object otherAnswer) {
        return this.aText.compareTo(((Answer)otherAnswer).aText);
    }
}
