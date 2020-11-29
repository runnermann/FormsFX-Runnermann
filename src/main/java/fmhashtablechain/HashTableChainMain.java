
package fmhashtablechain;

import flashmonkey.*;
import search.Search;

import java.util.ArrayList;

/**
 * 
 * Tester: Tests the HashTableChain structure
 * Explanation: Uses a simple array and checks to see if the data entered is 
 * stored in a Hash Table of chained ArrayLists. Uses The key value pair is
 * an array of integers. A single integer per pair is stored in both the key
 * and the value. This ensures that when a value is changed for a key, it is
 * easier to track. Particularly when the structure grows to over 2000 items.
 * 
 * The tester checks if collisions are resolved correctly and if all keys are
 * original. No duplicates. This is observable as the array is twice the size
 * of the available numbers in the Random number generator. It also checks to
 * ensure that when a value is changed for a key. The program will return 
 * the new value instead of the old one. 
 * 
 * The tester checks if a value is removed when a key is deleted. indicating t
 * hat the key has been deleted. WHen the program rehashes the data structure
 * it checks that the deleted values are not re created. This is observable. 
 * 
 * The data structure uses the iterator. The program will not work if the 
 * iterator is not functional. 
 * 
 * Algorithm
 * 
 *  BEGIN
 *      Variables and Objects.
 *          
 *      set length of Array to be used to import ints 
 *      Create array of randomly generated numbers. 
 *          convert ints to Strings and insert into array
 *          Number is 1/4 the size of the array. 
 *      set int num to an inputArray element. num is used throughout the tester
 *
 *      
 * @author Lowell Stadelman
 */

// *** IMPORTS ***



public class HashTableChainMain {

    
    public static final int LENGTH = 2150;
    public static final String DOTS = "***************************************";
    public static final String SPACE = "\n \n \n \n";
    
    /**
     * @param args the command line arguments
     */
 /*
    public static void main(String[] args) {
      
        FlashCardMM flash = new FlashCardMM();
        FlashCardMM.FileOperations fo = flash.new FileOperations();
        
        fo.setFileName("NewFM_Eng201");  
        fo.buildFileName(fo.getFileName());
        
        
        try
        {
            FlashCard.flashList.clear();
            //Thread.sleep(300);
            FlashCard.flashList = fo.getListFromFile();
        //}
        //catch(InterruptedException e)
        //{
            // left blank
        }
        catch(Exception e) 
        {
            // left blank
        }
        
        // ** The Hash Table Chain data structure to be tested
        FMHashTableChain fmHTChain = new FMHashTableChain();
        
        // ** The search object **
        search.Search search = new Search();

        // HEADER Message
        System.out.println(SPACE);
        System.out.println("\n" + DOTS);
        System.out.println("Testing FMHashTableChain data structure");
        System.out.println(DOTS + "\n");
        
    
        
        // Create the data structure from the array of randome String numerals
        System.out.println("\n2. Attempting to create the HashTable Chain : \n");
        search.buildMap();
        
        if( search.fmHashTableChain.size() > 1) {
            System.out.println("  Success");
        } else {
            System.out.println("\nbuild failed\n");
        }

        // *** Check if it will return an item ***
        System.out.println("\n3. Test if it will return an item in the "
                + "structure");
        System.out.println("    should read define : " + search.fmHashTableChain.get("define"));
        
        //*** Check data structure integrity to see if it will report true in
        // false cases. 
        System.out.println("\n4. Look for something not in the table ");
        System.out.println("    Trying to return a key of zulu and I "
                + "should return null : " + search.fmHashTableChain.get("zulu"));
        
        
        // *** Check to see if the Remove Method is functioning
        //System.out.println("\n5. Now Testing REMOVE method");
        //System.out.println("    Attempting to remove define");
        //System.out.println("    If removed it should return define : " 
        //        + search.fmHashTableChain.remove("define"));
        //System.out.println("   Check if the number was removed. Should return null");
        //System.out.println(" define : " + search.fmHashTableChain.get("define"));
        
        
        
        // Message
        System.out.println(SPACE);
        System.out.println("\n" + DOTS);
        System.out.println("searching for some words");
        System.out.println("looking for the, and, defined, president");
        System.out.println(DOTS + "\n"); 
        
        search.find(" argument which");
        
        //System.out.println("\n7. Now printing the table. Following the table is the"
        //        + " Array of integers. \nThe data stucture above should be "
        //        + "1/4 the size of the array below it");
        //System.out.println("\n NOTE: All integers are both key and value ");
        
        // Message
        //System.out.println(SPACE);
        //System.out.println("\n" + DOTS);
        //System.out.println("The Hash Table Chain Structure");
        //System.out.println(DOTS + "\n"); 

        
        //System.out.println("\n"+ search.fmHashTableChain.toString());
        
        System.out.println(SPACE);
        System.out.println("\n" + DOTS);
        System.out.println("Number of entries: " + search.fmHashTableChain.size());
        System.out.println(DOTS + "\n"); 

        
    }
*/  
}
