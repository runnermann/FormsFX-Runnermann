package type.testtypes;

//import flashmonkey.ReadFlash;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import flashmonkey.FlashCardMM;
//import javafx.scene.layout.VBox;
import type.cardtypes.GenericCard;
import type.celleditors.SectionEditor;

import java.util.ArrayList;
import java.util.BitSet;
//import java.util.LinkedList;

/**
 * GenericTestType provides the interface for All test types.
 * To create a new testType.
 * 1. Add the testType to the bottom of the GenericTestType array in
 * testtypes.TestList
 * 2. add it's bitSet number to its bitset. Use the bottom tests
 * index of the setBit. The next test will be the left bitSet from
 * that one.
 * 3. Implement all of the neccessary methods from this interface.
 * You can use a previously finished TestType as a guide.
 *
 *
 *@author Lowell Stadelman
 */
public interface GenericTestType<A extends GenericTestType>
{
    // @todo finish GenericTestType
    
    
    /**
     * If this card/test type is disabled
     */
    public abstract boolean isDisabled();
    
    /**
     * Returns the Test creator Pane for each test type.
     * @param flashList
     * @param q
     * @param a
     * @return
     */
    public abstract Pane getTEditorPane(ArrayList<FlashCardMM> flashList, SectionEditor q, SectionEditor a);

    /**
     * Returns the readPane that contains the testType.
     * @param cc
     * @param genCard
     * @param pane The parent pane
     * @return
     */
    public abstract Pane getTReadPane(FlashCardMM cc, GenericCard genCard, Pane pane);

    /**
     * Each testTypes save action
     * - Save BitSet testType array
     * - Save CardLayout
     * - Section Layouts
     */
    public abstract int getTestType();


    /**
     * Provides the card layout in a char.
     * layout: 'S' = single card layout,
     * 'D' = double horizontal card, and
     * 'd' = double vertical card
     * @return
     */
    public abstract char getCardLayout();

    public abstract GenericTestType getTest();

    /**
     * Returns the array of ansButtons, An array
     * should include the prevAnsButton and nextAnsButton (if they are needed)
     * and always should include the answerButton. Used
     * in placement in the south/bottom pane
     * @return
     */
    public abstract Button[] getAnsButtons();

    /**
     * returns each tests answerButton as opposed
     * to the array of AnswerButtons.
     * @return
     */
    public abstract Button getAnsButton();

    public abstract String getName();

    public abstract void ansButtonAction();

    public abstract void prevAnsButtAction();

    public abstract void nextAnsButtAction();

    /**
     * Called when a new card is created. IE when
     * a flashCard is added to the flashList. Prepares
     * or clears the UI as well as any data structures
     * that should be cleared.
     */
    public abstract void reset();



    //public abstract void nextQButtonAction();  THESE ARE IN READFLASH not in tests

    //public abstract char getQLayout();

    //public abstract char getALayout();

}
