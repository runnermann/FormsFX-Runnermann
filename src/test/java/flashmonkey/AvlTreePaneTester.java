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
        FlashCardOps.getInstance().setDeckFileName("ten.dec");

        assertTrue( FlashCardOps.getInstance().getDeckFileName().equals("ten.dec"), "Deck name not ten.dec");

        FlashCardOps.getInstance().setDeckFileName("ten");
        assertTrue(FlashCardOps.getInstance().getDeckFileName().equals("ten.dec"), "Deck name not ten.dec when set to \"ten\"");
    }
    /*
    public static void main(String[] args) {

        ReadFlash.setDeckName("ten.dec");

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
