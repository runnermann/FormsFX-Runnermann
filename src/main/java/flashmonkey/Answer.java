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
 * IMPLEMENTS Serializable, comparable
 *
 * @author Lowell Stadelman
 *
 ******************************************************************************/

import java.io.*;
import java.util.ArrayList;

@SuppressWarnings("unchecked")
public abstract class Answer implements Serializable, Comparable {
      private static final long serialVersionUID = FlashMonkeyMain.VERSION;

      /*** CONSTANTS ***/
      public static final ArrayList<Integer> DEF_LIST = new ArrayList<>(1);

      /*** VARIABLES ***/
      private String aText;
      // ANumber is the index number in flashList. It is used
      // as the anchor for a flashCard in flashList during a session.
      // It is set to its index number during build tree.
      private int aNumber;
      // A list of possible questions that this answer could be an
      // answer for.
      private ArrayList<Integer> answerSet;


      /**
       * Default constructor
       */
      public Answer() {
            this.aText = "";
            this.aNumber = 0;
      }


      /**
       * Full constructor
       *
       * @param txt    The answer text
       * @param aNum   The cards number in the stack, used as a referance
       * @param ansSet The set of questions this answer will also be correct for
       */
      public Answer(String txt, int aNum, ArrayList<Integer> ansSet) {
            this.aText = txt;
            this.aNumber = aNum;
            this.answerSet = ansSet;
      }

      /**
       * Creates a Answer Object with an empty ArrayList
       *
       * @param txt
       * @param aNum
       */
      public Answer(String txt, int aNum) {
            if (txt == null) {
                  aText = "";
            } else {
                  this.aText = txt;
            }
            this.aNumber = aNum;
            this.answerSet = new ArrayList<>();
      }


      /**
       * Shallow Copy Constructor
       *
       * @param original
       */
      public Answer(Answer original) {
            if (original == null) {
                  System.err.println("ERROR: Answer class Copy Constructor given NULL"
                      + "Exiting...");
                  System.exit(0);
            } else {
                  this.aText = original.aText;
                  this.aNumber = original.aNumber;
                  this.answerSet = new ArrayList<>(original.answerSet);
            }
      }


      //*** SETTERS ***

      public void setAText(String txt) {
            this.aText = txt;
      }

      public void setANumber(int aNum) {
            this.aNumber = aNum;
      }

      /**
       * The set of index's of all correct possible answers.
       * May include this answer.
       *
       * @param ansSet
       */
      public void setAnswerSet(ArrayList<Integer> ansSet) {
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


      public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Integer i : answerSet) {
                  sb.append(i + ", ");
            }
            return "Answer text: " + this.aText
                + "\n aNumber: " + aNumber
                + "\n answerSet: " + sb;
      }

      /**
       * equals method
       *
       * @param other
       * @return
       */
      public boolean equals(Answer other) {
            return this.aText.equalsIgnoreCase(other.aText);
      }


      /**
       * Default compareTo() that calls Strings compareTo method.
       * All other number comparisons should be done with a comparator.
       *
       * @param otherAnswer, the objected being compared to.
       * @return -1 if the other answer is larger.
       * 0 if the other answer is equal.
       * 1 if the other answer is smaller.
       */
      @Override
      public int compareTo(Object otherAnswer) {
            return this.aText.compareTo(((Answer) otherAnswer).aText);
      }
}
