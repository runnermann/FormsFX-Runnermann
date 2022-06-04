package type.testtypes;

import flashmonkey.*;
import uicontrols.ButtoniKon;
import uicontrols.SceneCntl;
import fmtree.FMTWalker;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import type.cardtypes.GenericCard;
import type.celleditors.SectionEditor;
import type.sectiontype.GenericSection;
import type.sectiontype.SingleCellSection;
//import uicontrols.SceneCntl;

import java.util.ArrayList;

//import static flashmonkey.ReadFlash.FLASH_CARD_OPS;

/**
 * Provides a True or False test type
 * This class uses the singleton design pattern. It is not a thread safe Singleton to avoid the overhead of thread-
 * synchronization. The intent is one JVM per EncryptedUser.EncryptedUser which will reside on the users machine. Only one class
 * is needed per JVM.
 * @author Lowell Stadelman
 */
public class TrueOrFalse implements GenericTestType<TrueOrFalse> {
    // The class instance for a singleton class
    private static TrueOrFalse CLASS_INSTANCE;
    private GenericSection genSection;
    private Button selectAnsButton;
    private RadioButton trueRdoBtn;
    private RadioButton falseRdoBtn;

    /**
     * Singleton private constructor
     */
    private TrueOrFalse() { /*no args constructor*/ }

    public static synchronized TrueOrFalse getInstance() {
        if(CLASS_INSTANCE == null) {
            CLASS_INSTANCE = new TrueOrFalse();
        }
        return CLASS_INSTANCE;
    }
    
    @Override
    public boolean isDisabled() {
        return false;
    }

    /**
     * Does not use the 2nd section editor.
     * @param flashList
     * @param q
     * @param a
     * @return HBox
     */
    @Override
    public Pane getTEditorPane(ArrayList<FlashCardMM> flashList, SectionEditor q, SectionEditor a, Pane pane)
    {
        // Instantiate HBox and "set spacing" !important!!!
        VBox vBox = new VBox(2);
        vBox.getChildren().addAll(q.sectionHBox, trueOrFalsePane(a));
        if(a.getPlainText() != null && a.getPlainText().equals("T")) {
            this.trueRdoBtn.setSelected(true);
        } else {
            this.falseRdoBtn.setSelected(true);
        }

        return vBox;
    }

    @Override
    public GridPane getTReadPane(FlashCardMM currentCard, GenericCard genCard, Pane parentPane) {
        // Each section is its' own object. Not using GenericCard
        genSection = GenericSection.getInstance();
        GridPane gPane = new GridPane();
        gPane.setVgap(2);

        HBox upperHBox;
        HBox lowerHBox;
        double lowerHBoxHt = 64;

        // set the lower section
        SingleCellSection l = new SingleCellSection();
        lowerHBox = l.sectionView(trueOrFalsePane(lowerHBoxHt));
        // set the upper section
        upperHBox = genSection.sectionFactory(currentCard.getQText(), currentCard.getQType(), 1, false, lowerHBoxHt, currentCard.getQFiles());

        StackPane stackP = new StackPane();
        ReadFlash.getInstance().setShowAnsNavBtns(false);

        // T or F buttons do not have any visible actions. Select ansBtn
        // will provide feed back and save the answer, like MultiChoice actions.
        if(selectAnsButton == null) {
            selectAnsButton = ButtoniKon.getAnsSelect();
        } else {
            selectAnsButton = ButtoniKon.getJustAns(selectAnsButton, "SELECT");
        }
        selectAnsButton.setOnAction(e -> ansButtonAction());

        // add the question to vBox
        // upperHBox = genSection.sectionFactory(currentCard.getQText(), currentCard.getQType(), 1, currentCard.getQFiles());

        stackP.getChildren().add(lowerHBox);
        stackP.getChildren().add(selectAnsButton);

        gPane.addRow(2, stackP);
        gPane.addRow(1, upperHBox);

        // Transition for Question, Right & end button click
        FMTransition.setQRight(FMTransition.transitionFmRight(upperHBox));
        // Transition for Question, left & start button click
        FMTransition.setQLeft(FMTransition.transitionFmLeft(upperHBox));
        // Transition for Answer for all navigation
        FMTransition.setAWaitTop(FMTransition.waitTransFmTop(lowerHBox, 0, 300, 350));

        return gPane;
    }

    /**
     * Sets BitSet 4 (= 16) (TrueOrFalse) to true
     * All other bits set to 0
     * Not compatible with Multi-Choice
     * @return bitSet
     */
    @Override
    public int getTestType() {
        // 16
        return 0b0000000000010000;
    }

    /**
     * Sets the card to a double horizontal. Method used during saves. Saves the
     * card type
     *
     * @return Returns the char 'd'
     */
    @Override
    public char getCardLayout() {
        return 'D'; // double vertically stacked
    }

    @Override
    public GenericTestType getTest() {

        return new TrueOrFalse();
    }

    @Override
    public Button[] getAnsButtons()
    {
        return null;
    }

    @Override
    public Button getAnsButton() {
        return this.selectAnsButton;
        //return null;
    }

    @Override
    public String getName() {
        return "True or False";
    }

    @Override
    public void ansButtonAction() {
        FlashCardOps fcOps = FlashCardOps.getInstance();
        ReadFlash rf = ReadFlash.getInstance();
        FlashCardMM currentCard = (FlashCardMM) FMTWalker.getCurrentNode().getData();
        FlashCardMM listCard = fcOps.getFlashList().get(currentCard.getANumber());
        rf.getProgGauge().moveNeedle(500, rf.incProg());
        double progress = rf.getProgress();
        if ((trueRdoBtn.isSelected() && currentCard.getAText().equals("T"))
                || (falseRdoBtn.isSelected() && currentCard.getAText().equals("F"))) {
            rf.new RightAns(currentCard, listCard, this);
        } else {
            rf.new WrongAns(currentCard, listCard, this);
            selectAnsButton.setDisable(true);
        }

        if(progress >= FMTWalker.getCount()) {
            ReadFlash.getInstance().endGame();
        }
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


    /**
     * The True or False pane for the Read Pane
     *
     * @return VBox containing the TrueOrFalsePane
     */
    private VBox trueOrFalsePane(double sectionHt)
    {
        // Set spacing between RdoButtons
        VBox tfBox = new VBox(10);
        tfBox.setMinWidth(SceneCntl.getCenterWd());

        trueRdoBtn = new RadioButton("True");
        falseRdoBtn = new RadioButton("False");

        ToggleGroup grp = new ToggleGroup();
        grp.getToggles().addAll(trueRdoBtn, falseRdoBtn);

        tfBox.getChildren().addAll(trueRdoBtn, falseRdoBtn);
        tfBox.setId("tfBox");
        tfBox.setMaxHeight(sectionHt);
        tfBox.setMinHeight(sectionHt);

        // Set the style of the pane to appear the same as a TextArea
        //tfBox.setStyle("-fx-background-color: #FFFFFFFF;"
        //             + "-fx-padding: 20;");
        return tfBox;
    }

    /**
     * EDITOR PANE True or False radio button pane.
     *
     * @return VBox containing the TrueOrFalsePane
     */
    private VBox trueOrFalsePane(SectionEditor editor)
    {
        // Set spacing between RdoButtons
        VBox tfBox = new VBox(10);
       // tfBox.setMaxHeight(150);
        tfBox.setMinWidth(SceneCntl.getCenterWd());

        trueRdoBtn = new RadioButton("True");
        falseRdoBtn = new RadioButton("False");

        ToggleGroup grp = new ToggleGroup();
        grp.getToggles().addAll(trueRdoBtn, falseRdoBtn);

        tfBox.getChildren().addAll(trueRdoBtn, falseRdoBtn);
        tfBox.setSpacing(10);
        tfBox.setMaxWidth(Double.MAX_VALUE);

        // Set the default value.
//        editor.tCell.getTextArea().setText("F");
        trueRdoBtn.setOnAction(e -> {
//            editor.tCell.getTextArea().setText("T");
        });

        falseRdoBtn.setOnAction(e -> {
 //           editor.tCell.getTextArea().setText("F");
        });

        // Set the style of the pane to appear the same as a TextArea
        //tfBox.setStyle("-fx-background-color: #FFFFFFFF;"
        //        + "-fx-padding: 20;");
        tfBox.setId("tfBox");
        return tfBox;
    }

    public void onClose()
    {
        //task = null;
    }

    @Override
    public void reset() {
        trueRdoBtn.setSelected(false);
        falseRdoBtn.setSelected(false);
    }
}
