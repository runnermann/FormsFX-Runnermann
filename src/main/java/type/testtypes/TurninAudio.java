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

public class TurninAudio extends TestTypeBase implements GenericTestType<TurninAudio> {


      public TurninAudio() {
            setScore(2);
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
       * Sets bit 7 (= 128)from right, (Turn-in-Audio) to true
       * All other bits set to 0
       * Not compatible ith Multi-Choice
       *
       * @return bitSet
       */
      @Override
      public int getTestType() {
            // 128
            return 0b0000000010000000;
      }

      @Override
      public char getCardLayout() {
            return 'D'; // double horizontal
      }

      @Override
      public GenericTestType getTest() {

            return new TurninAudio();
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
            return "Turn in Audio";
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
            // STUB // selectAnsButton = ButtoniKon.getAnsSelect();
      }
}
