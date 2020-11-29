package flashmonkey;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

//import org.junit.Test;
//import static flashmonkey.FlashCardOps.buildTree;
//import static flashmonkey.FlashCardOps.getFlashList;
//import static flashmonkey.FlashMonkeyMain.buildTreeWindow;
//import static org.junit.Assert.assertTrue;

public class AvlTreePaneTester {


    @Test
    public void studyDeckNameTenDat()
    {
        // Deck selected,
        // !! SCENE CHANGE !! to menu

        // click on study button
        //ReadFlash rf = new ReadFlash();
        ReadFlash.getInstance().setDeckName("ten.dat");

        assertTrue( ReadFlash.getInstance().getDeckName().equals("ten.dat"), "Deck name not ten.dat");
    }
    /*
    public static void main(String[] args) {

        ReadFlash.setDeckName("ten.dat");

        if(getFlashList() == null || getFlashList().isEmpty()) {
            FLASH_CARD_OPS.refreshFlashList();
        } else {
            FLASH_CARD_OPS.saveFlashList();
            FLASH_CARD_OPS.refreshFlashList();
        }

        buildTree();
        buildTreeWindow();


    }
    */
}
