/*
 * Copyright (c) 2019 - 2021. FlashMonkey Inc. (https://www.flashmonkey.xyz) All rights reserved.
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

import flashmonkey.FMTransition;
import flashmonkey.ReadFlash;
import fmtree.FMTWalker;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
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
 * @author Lowell Stadelman
 */
public class NoteTaker extends TestTypeBase implements GenericTestType<NoteTaker> {

      private static NoteTaker CLASS_INSTANCE;

      /**
       * Default constructor
       */
      private NoteTaker() {
            setScore(0);
      }

      /**
       * Returns a new instance of the class if the class hasn't been instantiated previously
       * else it returns the created instance.
       *
       * @return An instance of NoteTaker
       */
      public static NoteTaker getInstance() {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new NoteTaker();
            }
            return CLASS_INSTANCE;
      }


      @Override
      public boolean isDisabled() {
            return false;
      }


      @Override
      public GridPane getTReadPane(FlashCardMM cc, GenericCard genCard, Pane parentPane) {
            //LOGGER.info("\n\n *** called getTReadPane in NoteTaker *** \n");
            changed();

            // Local Variables
            GenericSection genSection = GenericSection.getInstance();
            GridPane gPane = new GridPane();
            gPane.setVgap(2);
            HBox upperHBox;
            upperHBox = genSection.sectionFactory(cc.getQText(), cc.getQType(), 1, true, 0, cc.getQFiles());

            final FlashCardMM currentCard = (FlashCardMM) FMTWalker.getInstance().getCurrentNode().getData();

            // There is not an answer button in NoteTaker so we provide the actions
            // for the gauges here.
            if (currentCard.getIsRightColor() == 0) {
                  final ReadFlash rf = ReadFlash.getInstance();
                  rf.new JustAns(currentCard);
                  rf.getProgGauge().moveNeedle(500, rf.incProg());
                  // When this is the last card, it will not be seen if
                  // we set the below.
//                  double progress = rf.getProgress();
//                  if (progress >= FMTWalker.getInstance().getCount()) {
//                        ReadFlash.getInstance().endGame();
//                  }
            }

            ReadFlash.getInstance().setShowAnsNavBtns(false);
            gPane.addRow(1, upperHBox);
            // Transition for Question, Right & end button click
            FMTransition.setQRight(FMTransition.transitionFmRight(upperHBox));
            // Transition for Question, left & start button click
            FMTransition.setQLeft(FMTransition.transitionFmLeft(upperHBox));

            return gPane;
      }


      public Pane getTEditorPane(ArrayList<FlashCardMM> flashList, SectionEditor q, SectionEditor a, Pane pane) {
            // Instantiate vBox
            VBox vBox = new VBox();
            vBox.getChildren().addAll(q.sectionHBox);
            return vBox;
      }

      /**
       * Sets 13th bit, or = 8192, all other bits are zero. A note taker card is displayed during tests and qAndA.
       * The NoteTaker is purely, for taking notes.
       * Not compatible ith Multi-Choice.
       *
       * @return bitSet
       */
      @Override
      public int getTestType() {
            // 8192
            // if this is reset, also set this in FlashCardOps setPriority.
            return 0b0010000000000000;
      }

      @Override
      public char getCardLayout() {
            // Single pane card
            return 'S';
      }

      @Override
      public GenericTestType getTest() {
            return new NoteTaker();
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
            return "Note Card";
      }

      @Override
      public void changed() {
            ReadFlash.getInstance().isChanged();
      }

      @Override
      public void ansButtonAction() {
            // stub
      }

      @Override
      public void nextAnsButtAction() {
            /* stub */
      }

      @Override
      public void prevAnsButtAction() {
            /* stub */
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
