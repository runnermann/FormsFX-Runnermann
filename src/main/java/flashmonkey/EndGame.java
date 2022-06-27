package flashmonkey;

import forms.DeckMetaPane;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import uicontrols.FMAlerts;
import uicontrols.UIColors;


public class EndGame {
      // The Pane that contains EndGame
      private final StackPane endGamePane = new StackPane();
      private static double percentScore;

      private EndGame() {
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
      public EndGame(double score, double possibleScore, int progress, int treeLength, String deckName) {
            String str = getStatsPane(score, possibleScore, progress, treeLength, deckName);
            StringBuilder sb = new StringBuilder(str);
            String choice = "\n\n\n Validate your expertise to your peers and future employers." +
                "       \n\n Click \"ok\" to start earning for your hard work";
            sb.append(choice);
            FMAlerts alert = new FMAlerts();
            boolean b = alert.choiceOnlyActionPopup("MISSION COMPLETED", sb.toString(),
                "image/i_got_paid.png", UIColors.ICON_ELEC_BLUE);
            if (b) {
                  // Sell this deck action
                  describeAndSellAction();
            } else {
                  // Send User back to main menu
                  FlashMonkeyMain.getWindow().setScene(FlashMonkeyMain.getMenuScene());
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

            String endGameMessage = "\n\n" + deckName + "\n\n  Score: " + score + " out of " + possibleScore + " possible points"
                + "\n  Completed: " + progress + " out of: " + treeLength + " questions";


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

            Scene scene = new Scene(metaPane.getMainGridPain());
            scene.getStylesheets().addAll("css/buttons.css", "css/fxformStyle.css");

            metaWindow.setScene(scene);
            metaWindow.show();
      }
}
