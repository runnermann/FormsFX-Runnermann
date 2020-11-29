package flashmonkey;

import fmtree.FMTWalker;
import fmtree.FMTree;

import java.util.ArrayList;

/**
 * Testing if references are correct.
 */
public class RemoveThisTester {


    private static ArrayList flashListObject = new ArrayList();
    private static FMTWalker treeWalkerObj = FMTWalker.getInstance();



    /**
     * Tests if the ArrayList object in the parameter is the same as
     * the ArrayList set in the testers object.
     * @param otherFlashListObject
     */
    public static void testFlashListObject(ArrayList otherFlashListObject) throws Exception {

        // Check if referances are the same
        if(flashListObject != otherFlashListObject) {

     //       throw new Exception("ERROR: Error in testFlashListObject: flashlists are not the same");

        } else {
           // System.out.println("\n\n **** In FlashListTester *** \n\n\t... Printing flashList objects");
            //System.out.println(flashListObject);
            //System.out.println(otherFlashListObject);
            //System.out.println("\n\n");
        }
    }

    /**
     * Tests if the treeWalker object in the parameter is the same as the treeWalker set
     * in the testers treeWalkerObject.
     * @param otherWalkerObj
     * @throws Exception
     */
    public static void testTreeWalkerObj(FMTWalker otherWalkerObj) throws Exception {

        if(treeWalkerObj != otherWalkerObj) {

            throw new Exception("ERROR: TreewalkerObjects are not the same");

        } //else {
            //System.out.println("WalkerObjects are the same");
        //}

    }


    //public static void testIfCardsAreSame(FlashCardMM otherFlashCard, int index) {
    //    System.out.println();
    //}


    public static void setFlashListObject(ArrayList originalFlashList) {

        flashListObject = originalFlashList;
    }

    public static void setTreeWalkerObj(FMTWalker walkerObj) {
        treeWalkerObj = walkerObj;
    }




    // ************ checking on ShapeFile issue ************ //

    private static String fileString = "";


    public static void setFileString(String originalFileStr) {

        fileString = originalFileStr;
    }

    public static void testFileStrings(String otherFileStr) throws Exception{

        if(fileString == otherFileStr) {
            throw new Exception("Error: FileStrings are the same object. " + fileString + "; other fileStr: " + otherFileStr);
        }
        if(fileString.equals(otherFileStr)) {
            throw new Exception("ERROR: FileStrings are the same name: " + otherFileStr + "; other fileStr: " + otherFileStr);
        }
    }

}
