package flashmonkey;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import type.cardtypes.DoubleHorzCard;


public class EndGame {
    // The Pane that contains EndGame
    private StackPane endGamePane = new StackPane();
    private static double percentScore;

    private EndGame() {
        /* private no args constructor */
    }

    /**
     * Full constructor
     * @param score The total score
     * @param treeWalkerCount
     * @param progress
     */
    public EndGame(double score, int treeWalkerCount, int progress, String deckName) {
        GridPane statsPane = getStatsPane(score, treeWalkerCount, progress,  deckName);
        this.endGamePane.getChildren().add(statsPane);
    }


    /**
     * Returns the StackPane containing
     * the endGame.
     * @return
     */
    public StackPane getPane() {
        return endGamePane;
    }


    /**
     * Actions for end of game behavior
     * @param score
     * @param treeWalkerCount
     * @return
     */
    private GridPane getStatsPane(double score, int treeWalkerCount, int progress, String deckName) {

        String endGameMessage = "\n\n Stack: " + deckName + "\n\n  Score: " + score + " out of " + (treeWalkerCount * 2) + " possible points"
                +  "\n  Completed: " + progress + " out of: " + treeWalkerCount + " questions";
        DoubleHorzCard dblCard = new DoubleHorzCard();
        
        // set local percent score
        this.percentScore = score / treeWalkerCount;

        GridPane gPane = dblCard.retrieveCard(endGameMessage, 't', " ", 't', null, null );

        // @todo finish endGame method
        return gPane;
    }
    
    public static double getPercentScore() {
        return percentScore;
    }
}
