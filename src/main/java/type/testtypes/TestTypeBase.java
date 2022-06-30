package type.testtypes;

public abstract class TestTypeBase {
      private double score = 0.0;

      /**
       * Returns the possible score value for
       * this card. E.G. a note card
       * does not have a score. It is just
       * viewed. Therefore it would be 0.
       */
      public double score() {
            return score;
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
      public void setScore(double num) {
            score = num;
      }

}
