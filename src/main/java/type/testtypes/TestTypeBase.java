package type.testtypes;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class TestTypeBase {
      protected AtomicInteger atomicScore = new AtomicInteger(0);

      /**
       * Returns the possible score value for
       * this card. E.G. a note card as a multiple of 10.
       * does not have a score. It is just
       * viewed. Therefore it would be 0.
       * @return as stated
       */
      public int score() {
            return atomicScore.get();
      }

      /**
       * Sets the value of score for the implementing class.
       * Classes that implement this interface shall have a
       * score. If the class is a scoreless type, then its
       * score should be set to 0. Operationally set to 2. It is
       * intended that score is added or subtracted.
       *
       * @param num a double value at the desired score
       */
      public void setScore(double num) {
            this.atomicScore.set((int) num * 10);
      };

}
