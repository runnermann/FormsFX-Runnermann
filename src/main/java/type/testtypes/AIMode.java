package type.testtypes;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import flashmonkey.FlashCardMM;
import type.cardtypes.GenericCard;
import type.celleditors.SectionEditor;

import java.util.ArrayList;
import java.util.BitSet;


/**
 * AIMode selects one of 4 test types(As of the time of this writing). Multi-CHoice,
 * Multi-ANswer, True or False, and Write in answer. AI based on EncryptedUser.EncryptedUser history and
 * algorithms determines the users next test type of question. More details are in
 * notes.
 *
 * Creation type is:
 *  - double-horizontal cardlayout or double vertical
 *  - Sections may be single or double cell
 *
 *  @author Lowell Stadelman
 */
public class AIMode implements GenericTestType<AIMode>
{


    public AIMode()
    {
        // no args constructor
    }
    // @todo complete AIMode class
    
    
    @Override
    public boolean isDisabled() {
        return true;
    }
    
    @Override
    public VBox getTEditorPane(ArrayList<FlashCardMM> flashList, SectionEditor q, SectionEditor a)
    {
        System.out.println("\ncalled getTEditorPane in AIMode");
        // Instantiate vBox and "set spacing" !important!!!
        VBox vBox = new VBox(2);
        vBox.getChildren().addAll(q.sectionHBox, a.sectionHBox);

        return vBox;
    }

    /**
     * Sets bits 0 - 4 to Multi-Choice, Multi-Answer, True or False, Write it in, and AI to true
     * All other bits set to 0
     * @return bitSet
     */
    @Override
    public int getTestType()
    {
        System.out.println("\ncalled getTestType in AIMode");
        // could use 31 but this is more visual
        return 0b0000000000011111;
    }

    @Override
    public char getCardLayout()
    {
        System.out.println("\n called getCardLayout in AIMode");
        return 'D'; // double horizontal
    }

    @Override
    public Pane getTReadPane(FlashCardMM cc, GenericCard genCard, Pane parentPane)
    {
        System.out.println("\ncalled getTReadPane in AIMode :) ");
        return new Pane();
    }

    @Override
    public GenericTestType getTest() {

        return new AIMode();
    }

    @Override
    public Button[] getAnsButtons() {
        return null;
    }

    @Override
    public Button getAnsButton() {
        return null;
    }

    @Override
    public String getName() {
        return "AI Mode";
    }

    @Override
    public void ansButtonAction() {
        // stub
    }

    @Override
    public void nextAnsButtAction()
    {
        // stub
    }

    @Override
    public void prevAnsButtAction()
    {
        // stub
    }

    @Override
    public void reset() {
        // stub
    }


    /*
    @Override
    public GenericBuilder<FMRectangle, RectangleBuilder> getBuilder(SectionEditor editor) {

        return new RectangleBuilder(
                DrawTools.getCanvas(),
                DrawTools.getGrapContext(),
                //editor.arrayOfFMShapes,
                DrawTools.getOverlayPane(),
                editor
        );
    }
    */

}
