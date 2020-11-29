
package fmtree;

/******************************************************************************
* COURSE:	CS112                   DAY:		TUE THU 1:30 - 3:30
* PROGRAMMERS:	Lowell Stadelman
* CHAPTER:	999			PROJECT: 	************ 
* DATE CREATED 	*** *** 2016
*******************************************************************************
*PROGRAM TITLE: calculator
*DESCRTIPTION:	This program serves as a simple calculator. The EncryptedUser.EncryptedUser can input
* any number at the start and use "+, -, *, /, and =. "R" or "r" will return
* the result. To EncryptedUser.EncryptedUser can use the program as many times as they desire. To con-
* tinue simply enter y or yes when asked and an n or no to exit.
* 
*PURPOSE:  The EncryptedUser.EncryptedUser tests the calculator class for operation.
*
*ALGORITHM:	
*
*       BEGIN
*           
                 
*	END
******************************************************************************/

// *** IMPORTS ***
import java.util.Random;
import java.util.function.BiConsumer;

public class AVLTreeTester//<E> 
{
     

    // CONSTANTS
    public static final int NUMBER_NODES = 10; // number of nodes
    public static final int NUMBER = 50; // cieling of possible numbers in node
    
    public static int[] numbers = {20, 30, 10, 15, 5, 0, 50, 25, 35, 40, 51};
    
    // Commented out for the use of the AVLTreeGUI
    // Uncomment to test Binary Tree and AVL Tree
    
    /*
    public static void main(String[] args) 
    {
        // *** OBJECTS ***
        BinarySearchTree bST = new BinarySearchTree();
        AVLTree aVL = new AVLTree();
        Random rand = new Random();
        
        //create a binary search tree
        for(int i = 0; i < numbers.length; i++) {
                        
            bST.add(numbers[i]);  
        }
        
        // print the binary search tree
        System.out.println("The Binary Search Tree \n" + bST.toString());
        bST.inOrderTraverse();
        
        
        // test the AVL tree
        System.out.println("\n\n\n AVLTree");
        
        for(int i = 0; i < numbers.length; i++) {
            aVL.add(numbers[i]);
        }
        
        //print the AVL tree
        System.out.println("The AVL tree \n " + aVL.toString());
        System.out.println("");
        aVL.inOrderTraverse();
    }
       
   */ 
    

}
