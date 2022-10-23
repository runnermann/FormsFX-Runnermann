package type.testtypes;

import flashmonkey.FMTransition;
import flashmonkey.ReadFlash;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import flashmonkey.FlashCardMM;
import type.cardtypes.GenericCard;
import type.celleditors.SectionEditor;

import java.util.ArrayList;

/**
 * This class is is similar to the multi-choice class. It has several possible correct answers.
 * The UI UX: The EncryptedUser.EncryptedUser ma select an answer box indicating that answer is selected. WHen the EncryptedUser.EncryptedUser
 * presses the answer button, the answers that are selected are compared with the correct ans array.
 */
public class MultiAnswer extends TestTypeBase implements GenericTestType<MultiAnswer> {
      private GenericCard gCard;
      private HBox lowerHBox;

      private Button selectAnsButton, nextAnsButton, prevAnsButton;

      public MultiAnswer() {
            super.setScore(2);
      }


      @Override
      public boolean isDisabled() {
            return true;
      }

      @Override
      public Pane getTEditorPane(ArrayList<FlashCardMM> flashList, SectionEditor q, SectionEditor a, Pane pane) {

            //@todo finish getTEditorPane in MultiAnswer
            // Instantiate vBox and "set spacing" !important!!!
            HBox hBox = new HBox(2);
            hBox.getChildren().addAll(q.sectionHBox, a.sectionHBox);

            return hBox;
      }

      /**
       * @param cc
       * @param genCard
       * @return
       */
      @Override
      public Pane getTReadPane(FlashCardMM cc, GenericCard genCard, Pane parentPane) {
            GridPane gPane = new GridPane();
            HBox upperHBox = new HBox();
            gPane.setVgap(2);

            gPane.setPrefSize(parentPane.getWidth(), parentPane.getHeight());

            lowerHBox = new HBox();
            StackPane stackP = new StackPane();
            VBox btnVBox = new VBox();
            btnVBox.setPadding(new Insets(10));
            btnVBox.getChildren().add(selectAnsButton);
            btnVBox.setAlignment(Pos.BOTTOM_CENTER);
            stackP.getChildren().add(lowerHBox);

            stackP.getChildren().add(btnVBox);

            gPane.addRow(2, stackP);
            gPane.addRow(1, upperHBox);

            // Transition for Question, Right & end button click
            FMTransition.setQRight(FMTransition.transitionFmRight(upperHBox));
            // Transition for Question, left & start button click
            FMTransition.setQLeft(FMTransition.transitionFmLeft(upperHBox));
            // Transition for Answer for all navigation
            FMTransition.setAWaitTop(FMTransition.waitTransFmTop(lowerHBox, 0, 300, 350));

            ReadFlash.getInstance().setShowAnsNavBtns(false);
            return new Pane();
      }

      /**
       * Sets bit 2 (= 4) (Turn-in-Video) to true
       * All other bits set to 0
       * Not compatible ith Multi-Choice
       *
       * @return bitSet
       */
      @Override
      public int getTestType() {
            // 4
            return 0b0000000000000100;
      }

      @Override
      public char getCardLayout() {
            return 'D'; // double horizontal
      }

      @Override
      public GenericTestType getTest() {
            return new MultiAnswer();
      }

      @Override
      public Button[] getAnsButtons() {
            return null;
      }

      @Override
      public void changed() {
            ReadFlash.getInstance().isChanged();
      }

      @Override
      public Button getAnsButton() {
            return null;
      }

      @Override
      public String getName() {
            return "Multi-Answer";
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
}
