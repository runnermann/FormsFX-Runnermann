package type.testtypes;

import flashmonkey.ReadFlash;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import flashmonkey.FlashCardMM;
import type.cardtypes.GenericCard;
import type.celleditors.SectionEditor;
import uicontrols.ButtoniKon;

import java.util.ArrayList;

public class DrawOnImage extends TestTypeBase implements GenericTestType<DrawOnImage> {

      //SectionEditor editorU, editorL;
      //GenericCard gCard;

      public DrawOnImage() {
            super.setScore(2);
      }


      @Override
      public boolean isDisabled() {
            return true;
      }

      @Override
      public VBox getTEditorPane(ArrayList<FlashCardMM> flashList, SectionEditor q, SectionEditor a, Pane pane) {
            // Instantiate vBox and "set spacing" !important!!!
            VBox vBox = new VBox(2);
            vBox.getChildren().addAll(q.sectionHBox, a.sectionHBox);

            return vBox;
      }

      /**
       * Sets Bit 10 or 1024 (Turn-in-Video) to true
       * All other bits set to 0
       *
       * @return bitSet
       */
      public int getTestType() {
            // 1024
            return 0b0000010000000000;
      }

      @Override
      public char getCardLayout() {
            return 'D'; // double horizontal
      }

      @Override
      public GenericTestType getTest() {

            return new DrawOnImage();
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
            return "Draw On Image";
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
      public Pane getTReadPane(FlashCardMM cc, GenericCard genCard, Pane parentPane) {
            return new Pane();
      }

      @Override
      public void reset() {
            // stub
      }

      @Override
      public void resetSelectAnsButton() {
            // stub // selectAnsButton = ButtoniKon.getAnsSelect();
      }
}
