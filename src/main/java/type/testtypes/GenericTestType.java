/*
 * Copyright (c) 2019 - 2021. FlashMonkey Inc. (https://www.flashmonkey.co) All rights reserved.
 *
 * License: This is for internal use only by those who are current employees of FlashMonkey Inc, or have an official
 *  authorized relationship with FlashMonkey Inc..
 *
 * DISCLAIMER OF WARRANTY.
 *
 * COVERED CODE IS PROVIDED UNDER THIS LICENSE ON AN "AS IS" BASIS, WITHOUT WARRANTY OF ANY
 *  KIND, EITHER EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION, WARRANTIES THAT THE COVERED
 *  CODE IS FREE OF DEFECTS, MERCHANTABLE, FIT FOR A PARTICULAR PURPOSE OR NON-INFRINGING. THE
 *  ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE COVERED CODE IS WITH YOU. SHOULD ANY
 *  COVERED CODE PROVE DEFECTIVE IN ANY RESPECT, YOU (NOT THE INITIAL DEVELOPER OR ANY OTHER
 *  CONTRIBUTOR) ASSUME THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 *  DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.  NO USE OF ANY COVERED
 *  CODE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 *
 */

package type.testtypes;

//import flashmonkey.ReadFlash;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import flashmonkey.FlashCardMM;
//import javafx.scene.layout.VBox;
import type.cardtypes.CardLayout;
import type.cardtypes.GenericCard;
import type.celleditors.SectionEditor;

import java.util.ArrayList;
//import java.util.LinkedList;

/**
 * GenericTestType provides the interface for All test types.
 * To create a new testType.
 * 1. Add the testType to the bottom of the GenericTestType array in
 * testtypes.TestList
 * 2. add it's bitSet number to its bitset. Use the bottom tests
 * index of the setBit. The next test will be the left bitSet from
 * that one.
 * 3. Implement all of the necessary methods from this interface.
 * You can use a previously finished TestType as a guide.
 *
 * @author Lowell Stadelman
 */
public interface GenericTestType<A extends GenericTestType> {

      /**
       * Call the isChanged() in ReadFlash if the answerButton
       * has been clicked. Or where appropriate.
       */
      void changed();

      /**
       * If this card/test type is disabled
       */
      boolean isDisabled();

      /**
       * Returns the Test creator Pane for each test type.
       *
       * @param flashList
       * @param q
       * @param a
       * @return
       */
      Pane getTEditorPane(ArrayList<FlashCardMM> flashList, SectionEditor q, SectionEditor a, Pane pane);

      /**
       * Returns the readPane that contains the testType.
       *
       * @param cc
       * @param genCard
       * @param pane    The parent pane
       * @return
       */
      Pane getTReadPane(FlashCardMM cc, GenericCard genCard, Pane pane);

      /**
       * Each testTypes save action
       * - Save BitSet testType array
       * - Save CardLayout
       * - Section Layouts
       */
      int getTestType();


      /**
       * Return the time until the answer
       * was clicked. If answer was not clicked
       * e.g. another button was clicked,
       * this time should not be stored.
       *
       * @return
       */
      int getSeconds();



      /**
       * Provides the card layout in a char.
       * layout: 'S' = single card layout,
       * 'D' = double horizontal card, and
       * 'd' = double vertical card
       *
       * @return
       */
      CardLayout getCardLayout();

      GenericTestType getTest();

      /**
       * Returns the array of ansButtons, An array
       * should include the prevAnsButton and nextAnsButton (if they are needed)
       * and always should include the answerButton. Used
       * in placement in the south/bottom pane
       *
       * @return
       */
      Button[] getAnsButtons();

      /**
       * Returns a score. E.G. a note card
       * does not have an ability to be answered. It is just
       * viewed. Therefore it wouldn't be scored.
       * @return an int value that is a multiple of 10. Thus for a
       * score of 1.5 set to 15
       */
      int score();

      /**
       * Sets the value of score for the implementing class.
       * Classes that implement this interface shall have a
       * score. If the class is a scoreless type, then its
       * score should be set to 0. Operationally set to 20. It is
       * intended that score is added or subtracted.
       *
       * @param num Set as an integer / 10. Example: For a score of 2 set to
       *        20. For a score of 1.5 set to 15.
       */
      void setScore(double num);

      /**
       * The ReadFlash class expects this method to return the implementation
       * from the TestType for its answerButton. An answerButton should provide
       * the testTypes expected behavior, format changes, when the EncryptedUser
       * clicks on the AnswerButton. Include correct and incorrect behavior as
       * a minimum.
       */
      Button getAnsButton();

      /**
       * Resets the selectAnswer button to "SELECT" and
       * the buttonIKon circle.
       * @return
       */
      void resetSelectAnsButton();

      String getName();

      void ansButtonAction();

      void prevAnsButtAction();

      void nextAnsButtAction();

      /**
       * Called when a new card is created. IE when
       * a flashCard is added to the flashList. Prepares
       * or clears the UI as well as any data structures
       * that should be cleared.
       */
      void reset();


      //public abstract void nextQButtonAction();  THESE ARE IN READFLASH not in tests

      //public abstract char getQLayout();

      //public abstract char getALayout();

}
