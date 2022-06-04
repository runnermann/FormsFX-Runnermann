package type.testtypes;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import flashmonkey.FlashCardMM;
import type.cardtypes.GenericCard;
import type.celleditors.SectionEditor;

import java.util.ArrayList;
import java.util.BitSet;

public class TurninVideo implements GenericTestType<TurninVideo>
{


    public TurninVideo()
    {
        // no args constructor
    }
    
    
    @Override
    public boolean isDisabled() {
        return true;
    }

    @Override
    public Pane getTEditorPane(ArrayList<FlashCardMM> flashList, SectionEditor q, SectionEditor a, Pane pane)
    {
        // Instantiate vBox and "set spacing" !important!!!
        VBox vBox = new VBox(2);

        vBox.getChildren().addAll(q.sectionHBox, a.sectionHBox);

        return vBox;
    }

    /**
     * Sets bit 6 ( = 64)(Turn-in-Video) to true
     * All other bits set to 0
     * Not compatible ith Multi-Choice
     * @return bitSet
     */
    @Override
    public int getTestType() {
        // 64
        return 0b0000000001000000;
    }


    @Override
    public char getCardLayout()
    {
        return 'D'; // double horizontal
    }

    @Override
    public GenericTestType getTest() {

        return new TurninVideo();
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
        return "Turn in video";
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
    public Pane getTReadPane(FlashCardMM cc, GenericCard genCard, Pane parentPane)
    {
        return new Pane();
    }

    @Override
    public void reset() {
        // stub
    }
}
