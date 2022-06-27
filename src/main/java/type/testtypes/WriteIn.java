package type.testtypes;

import flashmonkey.ReadFlash;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import flashmonkey.FlashCardMM;
import type.cardtypes.GenericCard;
import type.celleditors.SectionEditor;

import java.util.ArrayList;
import java.util.BitSet;

public class WriteIn extends TestTypeBase implements GenericTestType<WriteIn> {


      public WriteIn() {
            // no args constructor
      }

      @Override
      public boolean isDisabled() {
            return true;
      }


      @Override
      public Pane getTEditorPane(ArrayList<FlashCardMM> flashList, SectionEditor q, SectionEditor a, Pane pane) {
            // Instantiate vBox and "set spacing" !important!!!
            VBox vBox = new VBox(2);
            vBox.getChildren().addAll(q.sectionHBox, a.sectionHBox);

            return vBox;
      }

      /**
       * Sets BitSet 8 (= 256) (Write in) to true
       * All other bits set to 0
       * Not compatible ith Multi-Choice
       *
       * @return bitSet
       */
      @Override
      public int getTestType() {
            // 256
            return 0b0000000100000000;
      }

      @Override
      public char getCardLayout() {
            return 'D'; // double horizontal
      }

      @Override
      public Pane getTReadPane(FlashCardMM cc, GenericCard genCard, Pane parentPane) {


        /*

        //@todo complete writeButtonAction and implement the write testing mode
        System.out.println("\n*** writeButtonAction ***");
        mode = 'a';

        BorderPane rpSouth;

        // clear the center pane and refresh flashList
        reset();
        rpCenter.getChildren().clear();
        rpCenter.setVgap(2);

        if(FLASH_CARD_OPS.treeWalker.getRoot() == null)
        {
            //System.out.println("Tree is null, building the tree");
            //FlashCard.buildTree();
        }

        FLASH_CARD_OPS.treeWalker.setToFirst();
        FlashCardMM currentCard = (FlashCardMM) FMTWalker.getCurrentNode().getData();
        buttonTreeDisplay(FMTWalker.getCurrentNode());

        qPaneObj.setQText(visIndex + ") " + currentCard.getQuestionMM());

        ansPaneObj.answerPane.setVisible(true);
        // adding the answer pane to the center pane of borderpane
        rpCenter.addRow(2, ansPaneObj.answerPane);
        rpCenter.addRow(1, qPaneObj.questionPane);

        // The buttons in the bottom
        rpSouth = manageMode('Q');
        masterBPane.setCenter(rpCenter);
        masterBPane.setTop(rpNorth);
        masterBPane.setBottom(rpSouth);
        FlashMonkeyMain.avltPane.displayTree();

        */


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
            return "Writen response";
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

      //public abstract char getQLayout();

      //public abstract char getALayout();
}
