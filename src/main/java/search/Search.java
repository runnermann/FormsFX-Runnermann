/*
 * AUTHOR: Lowell Stadelman
 */

package search;

/*******************************************************************************
 * CLASS DESCRIPTION: This is the search class.
 * Uses a HashTableChain with a word/term used in the flash deck question or
 * answer objects of FlashCard. The index of where the term is used is provided
 * in an array/set. As the EncryptedUser.EncryptedUser types in a stream of terms, the arrays are
 * compared and indices that are not in all of the sets are eliminated.
 * UPDATE: Now uses a HashSet....
 * (Future) Term descrimination. Terms that are commonly used words are eliminated. IE
 * "the" "are" "is" thus, in theory, removing some wasted memory on terms that 
 * do not help with a search. If a term such as "the" is in every index, then 
 * it does not provide assistance with singling out the correct index. As the 
 * EncryptedUser.EncryptedUser is typing into the field, the commonly used words are simply ignored.
 * T(n) is also improved a little during searches. 
 * 
 * All GUI components are provided outside of this class
 * The constructor expects a String of terms, and getFound() returns the 
 * ArrayList of indexes available after descrimination. 
 * 
 * Considerations (-Old for the tree):
 *  BUILD the tree: straight forward build the tree as time permits without 
 *      slowing down the EncryptedUser.EncryptedUser or bogging down the server. i.e. as a seperate
 *      thread
 *  SEARCH the tree: find it in the tree
 *  DELETE from the tree: How to handle an index that has been edited so the 
 *      term is no longer in that index. Or the index has been removed.
 *  
 *
 *  BEGIN:
 *      BUILD HASHTABLE
 *          IF the term is in the table, add it's index, add it to the chain. 
 *          ELSE add the term to the tree with index
 *
 *      -Old Forrest search... Consider for letter by letter, but build is 10,000 x slower
 *      FIND
 *          LOOK for the term in the tree
 *              IF it's not in the tree, return the previous set
 *              ELSE 
 *                  - convert indices to a SET // removes duplicates
 *                  - setTerm1.returnAll(setTerm2) // returns the unions of the 
 *                  two sets
 *          DISPLAY results with an actionable/clickable link
 *      DELETE FROM TREE
 *          LOOK for the term in the tree
 *              IF the term is not in the tree, SOUT term + "not in tree"
 *              ELSE IF term exists in tree and has > 1 indices
 *                  delete the index
 *              ELSE delete the term from the tree       
 *  END
 * 
 *      
 *          
 * @author Lowell Stadelman
 ******************************************************************************/

/*** IMPORTS ***/
//import avltree.*;
//import avltree.*;
import flashmonkey.FlashCardMM;
import flashmonkey.FlashCardOps;
import flashmonkey.Timer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Comparator;
import java.util.Iterator;

public class Search //extends FlashCardOps
{

    //private static final long serialVersionUID = FlashMonkeyMain.VERSION;
    // *** CONSTANTS ***
    private static final int CALC_INDEX = 'a';

    // ** The Hash Table Chain data structure
    protected FMHashTableChain fmHashTableChain = new FMHashTableChain();

    // Boolean Set to true if a value is found. Else false.
    protected Boolean bool;
    
    // ** the matching index with the word **
    private Term term;
    //private Comparator comparator = null;
    
    int numWords = 0;

    /**
     * Default constructor
     */
    public Search()
    {
        bool = true;
    }


    public Boolean getBool()
    {
        return this.bool;
    }

    /**
     * BuildMap: Builds a searchable hash tablechain structure from an array.
     * pre-condition Expects that flashList array is greater than 1.
     * @param flashList
     * 
     * //@todo check buildMap() method testing string split with new lines 2017/6/12
     * //@todo check buildMap() method testing removal of patterns, ie "***" and "..." for replaceAll
     */
    @SuppressWarnings("unchecked")
    public <E extends Comparable<E>> void buildMap(ArrayList<FlashCardMM> flashList)
    {
        
        //FlashCardMM fc;
        int fcSize = flashList.size();
        //System.out.println("flashList size: " + fcSize);
        
        for(int indexi = 0; indexi < fcSize; indexi++)
        {
            FlashCardMM cardMM = flashList.get(indexi);
            //fc = flashList.get(indexi);
            String[] qWords = cardMM.getQText().split("\\s"); // ?? Problem with new lines ??

            String[] aWords = cardMM.getAText().split("\\s");


            /*
            System.out.println("words are split");
            System.out.println("QUESTION LENGTH " + qWords.length + 1);

            System.out.println("/n*********************************************************************");
            System.out.println("****** " + indexi + " question " + cardMM.getQText() + " ******");
            System.out.println("/n*********************************************************************");
            */
            int qLength = qWords.length;
            int aLength = aWords.length;
            int length = aLength;
            
            /**
             * If the answer is smaller than the question, make answer the same 
             * length as the question. Otherwise answer is longer and both run
             * according to the length of answer. 
             */
            if (length < qLength)
            {
                length = qLength;
            }
            /**
             * clean and insert into the hash table chain
             * key is the clean term, and value is the index in
             * the flashList array.
             */
            for(int j = 0; j < length; j++)
            {
                if(j < qLength)
                { 
                    String qClean = clean(qWords[j]);
                    if(qClean.length() > 2)
                    {
                        term = new Term(qClean, indexi);
                        //System.out.println(qClean);
                        fmHashTableChain.put(qClean, term);                        
                        this.numWords++;
                    }                  
                }
                if(j < aLength)  
                {
                    String aClean = clean(aWords[j]);
                    if(aClean.length() > 2)
                    {
                        term = new Term(aClean, indexi);
                        //System.out.println(aClean);
                        fmHashTableChain.put(aClean, term);                        
                        this.numWords++;
                    }
                } 
            }
        }
    }



    /**
     * Searches for a set of terms and returns the union of flashcards 
     * associated with the terms in the argument array.
     * @param strFind String of words to search for
     * @return Returns a FlashCard Array from the Union of the terms provided
     * in the argument. 
     */
    public Set<FlashCardMM> find(String strFind, ArrayList<FlashCardMM> flashList)
    {
        System.out.println("\n\n^*^*^ SEARCH FIND method called ^*^*^\n\n");

        
        Set unionSet = new HashSet();
        Set otherSet = new HashSet();
        
        ArrayList<FlashCardMM> searchResult = new ArrayList<>();
        // Split String strFind into seperate words
        Object value;
        String[] w = strFind.trim().split("\\s");

        System.out.println(); // clearing
        
        // Loop through the terms & get their indices from the hash table
        for(int i = 0; i < w.length; i++) 
        {
            System.out.println("in loop");
            // Get the first term and add its indices to the union set
            value = fmHashTableChain.get(clean(w[i].toLowerCase()));
            Term termi = (Term) value;

            if(value == null && w[i].length() > 2)
            {
                // set bool to false to indicate to the using method
                // that this String is not a valid hyperLink.
                System.out.println("value is null or less than 3");
                bool = false;
            }
            else if(value != null)
            {
                // set bool to true to indicate to the using method that String values are valid.
                bool = true;
                if(unionSet.isEmpty())
                {
                    unionSet.addAll(termi.indices);
                    System.out.println("UnionSet is empty \n"); // for " + termi.word + " : " + unionSet.toString());
                }
                else
                {
                    System.out.println("Unionset is not empty\n");
                    otherSet.addAll(termi.indices);
                    unionSet.retainAll(otherSet);
                }
            }
        }


        Set searchResultSet = new HashSet();
        // GET THE INTERSECTION OF INDEXES from union set
        Iterator<Integer> setIterator = unionSet.iterator();

            while(setIterator.hasNext()) 
            {
                searchResultSet.add(flashList.get(setIterator.next()));
                //searchResult.add(flashList.get(setIterator.next()));
            }
            System.out.println("searchResult indicies: ");
            for(FlashCardMM cardMM : searchResult) {
                System.out.println(cardMM.getQText());
            }

        unionSet.clear();
        otherSet.clear();

        return searchResultSet;
    }
    

    /**
     * For testing purposes. Prints the number of words.
     */
    protected void printNumWords()
    {
        System.out.println("Number of words: " + this.numWords);
    }

    /**
     * Removes all non word characters
     * pre-condition intended to be used with a single word.
     * @param str
     * @return A string with all non word characters removed. ie *"9six to five
     * would be returned as iesixtofive
     */
    protected static String clean(String str)
    { 
        str = str.toLowerCase().replaceAll("\\W+", "");
        str = str.replaceAll("\\s", "");
        return str.trim();
    }

    /** 
     * Inner Class containing the term and indices array
     */
    @SuppressWarnings("rawtypes")
    public static final class Term implements Comparable
    {
        // The term contained in the Node
        String word;
        // Holds the indices that contain the term. Indices are the indexes to 
        // or array element that contains the address to the flashCard that
        // contains that word or term. 
        ArrayList<Integer> indices = new ArrayList<>();
        
        public Term()
        {
            // default constructor
        }
        
        /**
         * Constructor
         * @param wd
         */
        public Term(String wd, int index)
        {
            setWord(wd);
            this.indices.add(index);
        }
        
        /**
         * Setter
         * @param wd String, The word stored in the search Term
         */
        protected void setWord(String wd)
        {
            this.word = wd;
        }
        
        
        /**
         * getWord
         * @return Returns the word from the Term
         */
        protected String getWord()
        {
            return this.word;
        }
        
        
        /**
         * toString method: 
         * @return Returns the term and the indices contained in the array
         */
        @Override
        public String toString()
        {
            return this.word + printIndices();
        }
        
        private String printIndices()
        {
            String str = "";
            for(int i = 0; i < this.indices.size(); i++) 
            {
                str += this.indices.get(i).toString() + ",";
            }
            return str;
        }
        
        /**
         * DESCRIPTION: Equals method.
         * @param other
         */
        @Override
        public boolean equals(Object other)
        {
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
                Term otherTerm = (Term) other;
                return this.word.equalsIgnoreCase(otherTerm.word);
            }
        }

        /**
         * Default compareTo() that calls Strings compareTo method.
         * All other number comparisons should be done with a comparator.
         * @param otherTerm, the objected being compared to.
         * @return -1 if this term is smaller than other.
         *          0 if this term is equal to the other.
         *          1 if this term is larger than other.
         */
        @Override
        public int compareTo(Object otherTerm)
        {
                System.out.println(" compare = " + this.getWord().compareToIgnoreCase(((Term)otherTerm).getWord()));

                return this.getWord().compareToIgnoreCase(((Term)otherTerm).getWord());
        }
    }
}
