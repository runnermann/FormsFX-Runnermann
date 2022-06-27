package type.testtypes;


import flashmonkey.FMTransition;
import flashmonkey.FlashCardMM;
import flashmonkey.ReadFlash;

import fmannotations.FMAnnotations;
import fmtree.FMTWalker;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import type.cardtypes.GenericCard;
import type.celleditors.SectionEditor;
import type.sectiontype.GenericSection;
import uicontrols.ButtoniKon;

import java.util.ArrayList;


/**
 * This is the general or default, non-scoring/non-test layout. It is a generic FlashCard with a question and answer.
 * <p>
 * Might be more appropriate to be independent of tests. However, gives flexibility
 * as a method to study by leaving it in as a test. This class does not include search. The child class
 * QandAMain includes search and is selected in Menu.
 *
 * @author Lowell Stadelman
 */
public abstract class QandA implements GenericTestType<QandA> {

      // The lower HBox used in TReadPane lambda
      private HBox lowerHBox;
      private HBox upperHBox;

      /**
       * Answer Button
       **/
      private static Button answerButton;
      //private static char mode = 't';

      private QandA() {/* default constructor */}

      @Override
      public boolean isDisabled() {
            return false;
      }


      /**
       * Returns the testpane
       *
       * @param flashList the flashlist
       * @param q         The questinos secion editor
       * @param a         the answers section editor
       * @return Returns the testPane
       */
      @Override
      public Pane getTEditorPane(ArrayList<FlashCardMM> flashList, SectionEditor q, SectionEditor a, Pane pane) {
            // Instantiate vBox and "set spacing" !important!!!
            VBox vBox = new VBox(2);
            vBox.getChildren().addAll(q.sectionHBox, a.sectionHBox);
            return vBox;
      }

      @Override
      public GridPane getTReadPane(FlashCardMM cc, GenericCard genCard, Pane parentPane) {

            ReadFlash rf = ReadFlash.getInstance();

            // Local variables
            GenericSection genSection = GenericSection.getInstance();
            GridPane gPane = new GridPane();
            gPane.setVgap(2);


            StackPane stackP = new StackPane();
            if (answerButton == null) {
                  answerButton = ButtoniKon.getAnsSelect();
            } else {
                  answerButton = ButtoniKon.getJustAns(answerButton, "Show Answer");
            }


            upperHBox = genSection.sectionFactory(cc.getQText(), cc.getQType(), 2, true, 0, cc.getQFiles());
            // Using 'T' in the cType(Cell type) to start off with a defualt empty answer string
            // in the answer section.
            lowerHBox = genSection.sectionFactory("", 't', 2, true, 0, cc.getAFiles());

            rf.setShowAnsNavBtns(false);
            // The answer btn action
            // Keeping action local. Makes using lamda easier.
            answerButton.setOnAction(e -> ansButtonAction());
//        answerButton.setOnAction((ActionEvent e) ->
//        {
//            nextAnsButtAction();
//            lowerHBox.getChildren().clear();
//            FMTransition.nodeFadeIn = FMTransition.ansFadePlay(lowerHBox, 1, 750, false);
//
//            lowerHBox.getChildren().add(genSection.sectionFactory(cc.getAText(), cc.getAType(), 2, true, 0, cc.getAFiles()));
//            if (cc.getIsRight() == 0) {
//                rf.getProgGauge().moveNeedle(500, rf.incProg());
//            }
//            //  answerButton.setVisible(false);
//            cc.setIsRight(1);
//            FMTransition.nodeFadeIn.play();
//        });

            VBox btnVBox = new VBox();
            btnVBox.setPadding(new Insets(10));
            btnVBox.getChildren().add(answerButton);
            btnVBox.setAlignment(Pos.BOTTOM_CENTER);

            stackP.setAlignment(Pos.BOTTOM_CENTER);

            stackP.getChildren().add(lowerHBox);
            stackP.getChildren().add(answerButton);

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
       * Sets 4th (= 8) bit, all others set to 0
       * Compatible with Multi-Choice
       *
       * @return bitSet
       */
      @Override
      public int getTestType() {
            // lowest bit = 8, actual = 32776
            // high bit indicates this card
            // works with multi-choice / AI
            return 0b1000000000000100;
      }

      /**
       * This card is a double horizontal layout
       *
       * @return returns 'D' for a double horizontal card
       */
      @Override
      public char getCardLayout() {
            return 'D'; // double horizontal
      }

      @Override
      public Button[] getAnsButtons() {
            return null;
      }

      /**
       * The ReadFlash class expects this method to return the implementation
       * from the TestType for its answerButton. An answerButton should provide
       * the testTypes expected behavior, format changes, when the EncryptedUser
       * clicks on the AnswerButton. Include correct and incorrect behavior as
       * a minimum.
       *
       * @return returns the tests answerButton as opposed to the array of AnswerButtons.
       */
      @Override
      public Button getAnsButton() {
            return answerButton;
      }

      @Override
      public void ansButtonAction() {
            lowerHBox.getChildren().clear();
            GenericSection genSection = GenericSection.getInstance();
            final FlashCardMM cc = (FlashCardMM) FMTWalker.getCurrentNode().getData();
            final ReadFlash rf = ReadFlash.getInstance();
            FMTransition.nodeFadeIn = FMTransition.ansFadePlay(lowerHBox, 1, 750, false);

            lowerHBox.getChildren().add(genSection.sectionFactory(cc.getAText(), cc.getAType(), 2, true, 0, cc.getAFiles()));
            if (cc.getIsRight() == 0) {
                  rf.getProgGauge().moveNeedle(500, rf.incProg());
            }
            //  answerButton.setVisible(false);
            cc.setIsRight(1);
            FMTransition.nodeFadeIn.play();
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
      public String getName() {
            return "Q and A";
      }


      @Override
      public void reset() {
            lowerHBox = null;
            answerButton = null;
      }


      /**
       * Used in test session. Does not display a search pane and uses
       * 'T' for mode.
       */
      public static class QandATest extends QandA {

            private static QandATest CLASS_INSTANCE;

            private QandATest() {
                  changed();
            }

            public static synchronized QandATest getInstance() {
                  if (CLASS_INSTANCE == null) {
                        CLASS_INSTANCE = new QandATest();
                  }
                  return CLASS_INSTANCE;
            }

            @Override
            public GenericTestType<QandA> getTest() {
                  return QandATest.getInstance();
            }

            @Override
            public void changed() {
                  ReadFlash.getInstance().isChanged();
            }

            /**
             * Returns a score. E.G. a note card
             * does not have an ability to be answered. It is just
             * viewed. Therefore it wouldn't be scored.
             */
            @Override
            public double score() {
                  return 0;
            }

            /**
             * Sets the value of score for the implementing class.
             * Classes that implement this interface shall have a
             * score. If the class is a scoreless type, then its
             * score should be set to 0. Operationally set to 2. It is
             * intended that score is added or subtracted. IE in the
             * case of a wrong answer, the card is inserted back
             * into the deck with a different score.
             *
             * @param num
             */
            @Override
            public void setScore(double num) {
                  /* not used */
            }
      }

      /**
       * Used in the Question and Answer "mode" in MenuPane of ReadFlash. Displays
       * a search box and uses 'A' for mode
       */
      public static class QandASession extends QandA {
            /**
             * The class instance for the singleton class
             */
            private static QandASession CLASS_INSTANCE;

            /**
             * Default constructor. Creates super/parent class
             */
            private QandASession() {
                  ReadFlash.getInstance().setMode('a');
                  changed();
            }

            /**
             * Provides only one instance of this class.
             *
             * @return Returns an instance of the class
             */
            public static synchronized QandASession getInstance() {
                  if (CLASS_INSTANCE == null) {
                        CLASS_INSTANCE = new QandASession();
                  }
                  return CLASS_INSTANCE;
            }

            @Override
            public void changed() {
                  ReadFlash.getInstance().isChanged();
            }


            /**
             * ------ GETTERS -----
             **/

            @Override
            public GenericTestType<QandA> getTest() {
                  return new QandASession();
            }

            /**
             * Returns a score. E.G. a note card
             * does not have an ability to be answered. It is just
             * viewed. Therefore it wouldn't be scored.
             */
            @Override
            public double score() {
                  return 0;
            }

            /**
             * Sets the value of score for the implementing class.
             * Classes that implement this interface shall have a
             * score. If the class is a scoreless type, then its
             * score should be set to 0. Operationally set to 2. It is
             * intended that score is added or subtracted. IE in the
             * case of a wrong answer, the card is inserted back
             * into the deck with a different score.
             *
             * @param num
             */
            @Override
            public void setScore(double num) {
                  /* not used */
            }


            /* *** FOR TESTING *** */
            @FMAnnotations.DoNotDeployMethod
            public Point2D getAnswerButtonXY() {
                  Bounds bounds = answerButton.getLayoutBounds();
                  return answerButton.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
            }

      }

      /* *** FOR TESTING *** */
      @FMAnnotations.DoNotDeployMethod
      public Node getUpperHBox() {
            return upperHBox;
      }

      @FMAnnotations.DoNotDeployMethod
      public Node getLowerHBox() {
            return lowerHBox;
      }


}
