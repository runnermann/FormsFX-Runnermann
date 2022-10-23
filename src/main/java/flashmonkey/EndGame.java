package flashmonkey;

import forms.DeckMetaPane;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import media.sound.SoundEffects;
import uicontrols.FMAlerts;
import uicontrols.UIColors;


public class EndGame {
      // The Pane that contains EndGame
      private final StackPane endGamePane = new StackPane();
      private static double percentScore;
      public static final char HONORABLE = 'A';
      public static final char HIGH = 'B';
      public static final char AVG = 'C';
      public static final char COMPLETE = 'Z';

      public EndGame() {
            /* private no args constructor */
      }

      /**
       * Full constructor
       *
       * @param score
       * @param possibleScore
       * @param progress
       * @param treeLength
       * @param deckName
       */
      public void buildAndDisplay(double score, double possibleScore, int progress, int treeLength, String deckName, char level) {
            String str = getStatsPane(score, possibleScore, progress, treeLength, deckName);
            StringBuilder sb = new StringBuilder(str);
            String choice = "\n\n\nValidate your expertise to your peers and future employers." +
                "       \n\nClick \"ok\" to start earning for your hard work";
            sb.append(choice);
            FMAlerts alert = new FMAlerts();

            boolean b;

            switch(level) {
                  case 'A': {
                        b = alert.choiceOnlyActionPopup("CONGRATULATIONS", null, sb.toString(),
                                "image/i_got_paid.png", UIColors.ICON_ELEC_BLUE, SoundEffects.DECK_END_HIGHSCORE);
                        break;
                  }
                  case 'B': {
                        b = alert.choiceOnlyActionPopup("GOOD", null, sb.toString(),
                                "image/i_got_paid.png", UIColors.ICON_ELEC_BLUE, SoundEffects.GAME_OVER);
                        break;
                  }
                  case 'C': {
                        b = alert.choiceOnlyActionPopup("COMPLETE", null, sb.toString(),
                                "image/i_got_paid.png", UIColors.ICON_ELEC_BLUE, SoundEffects.GAME_OVER);
                        break;
                  }
                  default:
                  case 'Z': {
                        b = alert.choiceOnlyActionPopup("GAME OVER", null, sb.toString(),
                                "image/i_got_paid.png", UIColors.ICON_ELEC_BLUE, SoundEffects.GAME_OVER);

                  }
            }

            if (b) {
                  // Sell this deck action
                  describeAndSellAction();
            } else {
                  // Send User back to main menu
                  FlashMonkeyMain.setWindowToModeMenu();
            }
            return;
      }


      /**
       * Returns the StackPane containing
       * the endGame.
       *
       * @return
       */
      public StackPane getPane() {
            return endGamePane;
      }


      /**
       * Actions for end of game behavior
       *
       * @param score
       * @return
       */
      private String getStatsPane(double score, double possibleScore, int progress, int treeLength, String deckName) {

            String endGameMessage = "\n\n" + deckName + "\n\nScore: " + score + " out of " + possibleScore + " possible points"
                + "\nCompleted: " + progress + " out of: " + treeLength + " questions";


            // set local percent score
            percentScore = score / possibleScore;
            return endGameMessage;
      }

      public static double getPercentScore() {
            return percentScore;
      }

      private void describeAndSellAction() {
            Stage metaWindow = new Stage();
            DeckMetaPane metaPane = new DeckMetaPane();
            if (metaWindow != null) {
                  metaPane = new DeckMetaPane();
                  metaWindow = new Stage();
            }
            //DeckMetaData meta = DeckMetaData.getInstance();

            Scene scene = new Scene(metaPane.getMainPane());
            scene.getStylesheets().addAll("css/buttons.css", "css/fxformStyle.css");

            metaWindow.setScene(scene);
            metaWindow.show();
      }
}
