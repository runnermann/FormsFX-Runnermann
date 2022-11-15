package type.testtypes;

import flashmonkey.FMTransition;
import flashmonkey.ReadFlash;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import type.cardtypes.GenericCard;
import type.celleditors.SectionEditor;
import flashmonkey.FlashCardMM;
import type.sectiontype.GenericSection;
import uicontrols.ButtoniKon;

import java.util.ArrayList;


/**
 * Note on this class
 * 1. How to indicate to the EncryptedUser.EncryptedUser, and parser, that "this" is a blank without making it
 * difficult for the EncryptedUser.EncryptedUser, and the parser. AND without restricting the EncryptedUser.EncryptedUser's
 * format. IE for math or computer science, where the "clue" or symbol to the
 * parser, is what the EncryptedUser.EncryptedUser needs to use for their question. IE the EncryptedUser.EncryptedUser
 * enters a [____] as part of a format in computer science, then
 * it would cause an error if the parser looked for it as a symbol to indicate
 * that this is a question for the fill in the blank answer.
 */
public class FillnTheBlank extends TestTypeBase implements GenericTestType<FillnTheBlank> {
      // The singleton instance of this class
      private static FillnTheBlank CLASS_INSTANCE;

      Button answerButton;

      /**
       * Default No-Args Constructor
       */
      public FillnTheBlank() { super.setScore(2);}


      @Override
      public boolean isDisabled() {
            return true;
      }

      /**
       * Returns an instance of the class. Use this method to
       * call the class. IE
       * MultiChoice multiChoiceObje = MultiChoice.getInstance();
       *
       * @return
       */
      public static synchronized FillnTheBlank getInstance() {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new FillnTheBlank();
            }
            return CLASS_INSTANCE;
      }

      /**
       * Returns the editor panes
       *
       * @param flashList
       * @param q
       * @param a
       * @return Returns a VBox containing the upper and lower editor panes
       */
      @Override
      public VBox getTEditorPane(ArrayList<FlashCardMM> flashList, SectionEditor q, SectionEditor a, Pane pane) {
            // Instantiate vBox and "set spacing" !important!!!
            VBox vBox = new VBox(2);

            //gCard = new GenericCard();
            //Pane pane = gCard.cardFactory("", 'T', "", 'T', 'D',files);

            /**    TEST CARD CREATION, SHAPES, IMAGES, VIDEO, AUDIO   Uncomment cfpCenter.* below to test and comment out above (2x) cfpCenter.*  **/

            //editorU = new SectionEditor("Enter a question here", flashList.size(), 'U', gCard.getTextCell().getTextArea());
            //editorL = new SectionEditor("Enter the answer here", flashList.size(), 'L', gCard.getTextCell().getTextArea());
            vBox.getChildren().addAll(q.sectionHBox);

            return vBox;
      }

      /**
       * Returns the read pane for the FillInTheBlank class.
       *
       * @param cc
       * @param genCard
       * @return
       */
      @Override
      public Pane getTReadPane(FlashCardMM cc, GenericCard genCard, Pane parentPane) {
            // Local Variables
            GenericSection genSection = GenericSection.getInstance();
            HBox upperHBox;

            answerButton = new Button("Answer");


            upperHBox = genSection.sectionFactory(cc.getQText(), cc.getQType(), 1, true, 0, cc.getQFiles());
            ReadFlash.getInstance().setShowAnsNavBtns(false);

            // The answer btn action
            // Keeping action local. Makes using lamda easier.
        /*
        answerButton.setOnAction((ActionEvent e) ->
        {

        }
        */

            // Transition for Question, Right & end button click
            FMTransition.setQRight(FMTransition.transitionFmRight(upperHBox));

            return new Pane(upperHBox);

      }

      /**
       * Sets Bit 5 (=32) for (FITB) to true
       * All other bits set to 0
       * Not compatible with Multi-Choice
       *
       * @return bitSet
       */
      @Override
      public int getTestType() {
            // 32
            return 0b0000000000100000;
      }

      @Override
      public char getCardLayout() {
            return 'S'; // Single pane card
      }

      @Override
      public GenericTestType getTest() {

            return new FillnTheBlank();
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
            return "FITB";
      }

      @Override
      public void changed() {
            ReadFlash.getInstance().isChanged();
      }

      @Override
      public void ansButtonAction() {
            changed();
            // stub
      }

      @Override
      public void nextAnsButtAction() {
            // stub
      }

      @Override
      public void prevAnsButtAction() {
            // stub
      }

      @Override
      public void reset() {
            // stub
      }

      @Override
      public void resetSelectAnsButton() {
            answerButton = ButtoniKon.getAnsSelect();
      }
}
