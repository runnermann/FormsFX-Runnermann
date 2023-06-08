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

import flashmonkey.ReadFlash;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import flashmonkey.FlashCardMM;
import type.cardtypes.CardLayout;
import type.cardtypes.GenericCard;
import type.celleditors.SectionEditor;
import uicontrols.ButtoniKon;

import java.util.ArrayList;


/**
 * AIMode selects one of 4 test types(As of the time of this writing). Multi-CHoice,
 * Multi-ANswer, True or False, and Write in answer. AI based on EncryptedUser.EncryptedUser history and
 * algorithms determines the users next test type of question. More details are in
 * notes.
 * <p>
 * Creation type is:
 * - double-horizontal cardlayout or double vertical
 * - Sections may be single or double cell
 *
 * @author Lowell Stadelman
 */
public class AIMode extends TestTypeBase implements GenericTestType<AIMode> {


      public AIMode() {
            super.setScore(2);
      }
      // @todo complete AIMode class


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
       * Sets bits 0 - 4 to Multi-Choice, Multi-Answer, True or False, Write it in, and AI to true
       * All other bits set to 0
       *
       * @return bitSet
       */
      @Override
      public int getTestType() {
            // could use 31 but this is more visual
            return 0b0000000000011111;
      }

      /**
       * Return the time until the answer
       * was clicked. If answer was not clicked
       * e.g. another button was clicked,
       * this time should not be stored.
       *
       * @return
       */
      @Override
      public int getSeconds() {
            return 0;
      }

      @Override
      public CardLayout getCardLayout() {
            return CardLayout.DOUBLE_HORIZ; // double horizontal
      }

      @Override
      public Pane getTReadPane(FlashCardMM cc, GenericCard genCard, Pane parentPane) {
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
            // STUB // selectAnsButton = ButtoniKon.getAnsSelect();
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
