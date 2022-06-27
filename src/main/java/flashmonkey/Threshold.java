/**
 * This class tracks the threashold of a flashCard deck. The EncryptedUser.EncryptedUser may have
 * successfully reviewed a card enough times to throw it into a hidden state.
 * However, the EncryptedUser.EncryptedUser may need to add cards to the deck changing the threashold value.
 * This class ensures that a card remains hidden despite the threashold value
 * increase. It also ensures that occasionaly, if a card has not been
 * reviewed for some period of time, it will become unhidden.
 */

package flashmonkey;

/*** imports **/

import static java.lang.Math.ceil;

import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
//import java.time.LocalTime;
import java.time.Duration;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

/**
 * The threshold that determines if a card is hidden.
 * @author Lowell Stadelman
 */
public class Threshold {
      public static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.of(2018, 1,
          1, 00, 00, 00, 01, ZoneId.of("America/Los_Angeles"));
      private static int threshold;
      public static ZoneId zID;// = ZoneId.of("America/Los_Angeles");
      double factor;
      private int days;


      public Threshold() {
            threshold = 100;
            factor = 3;
            ZoneId zID = ZoneId.of("America/Los_Angeles");
      }

      /**
       * Sets a flashCards remember value to forget to hide the question if the
       * question has been answered correctly more than 3 times, if the question
       * was answered within 2 minutes, and has been viewed previously within the
       * past 5 days.
       * @param fList
       */
      public Threshold(ArrayList<FlashCardMM> fList) {
            factor = 3;
            calcThreshold(fList);
            zID = ZoneId.of("America/Los_Angeles");
      }

      /**
       * Sets the date the card was answered correctly.
       * @return
       */
      protected static ZonedDateTime getRightDate() {
            return ZonedDateTime.now(zID);
      }
    
    
    /*protected ZonedDateTime defaultDate()
    {
    	return ZonedDateTime.of(2018, 1, 1, 00, 00, 00, 01, zID);
    }
    */

      /**
       * returns the threshold
       * @return the threshold
       */
      protected static int getThreshold() {
            return threshold;
      }

      /**
       * Calculates the threshold
       * @param fList The flashlist
       */
      protected void calcThreshold(ArrayList<FlashCardMM> fList) {
            if (fList.size() > 8) {
                  threshold = (int) ceil((double) fList.size() * factor);
                  //System.out.println("threshold is " + threshold);
            } else {
                  threshold = 25;
            }
      }

      /**
       * Determines if a flashcard is a candidate to be hidden until the number of
       * days has been met.
       * @param fc The flashcard
       * @return true if the time period is under the number of days that days
       * is set too and if it's eligable to be hidden.
       */
      protected boolean boolHide(FlashCardMM fc) {
            if (fc != null && fc.getRtDate() != null) {
                  Period period = Period.between(fc.getRtDate().toLocalDate(), ZonedDateTime.now(zID).toLocalDate());

                  // TESTING

                  if (fc.getRemember() >= threshold) {
                        // testing
                        //if(fc.getNumRight() >= 3 && period.getDays() <= days && fc.getSeconds() <= 120)
                        //{
                        //System.out.println("\tFlashCard: " + fc.getQuestionMM() + " is hidden");
                        //}

                        // hide if, the card was answered correclty 3 time or more times, if it's under the period of days to be hidden,
                        // and it was answered within 120 seconds or two minutes.
                        return fc.getNumRight() >= 3 && period.getDays() <= days && fc.getSeconds() <= 120;
                  }
            }
            return false;
      }

      public static void hideThis(FlashCardMM fc) {
            if (fc != null && fc.getRtDate() != null) {
                  fc.setNumRight(4);
                  fc.setRemember(threshold + (fc.getANumber() * 10));
                  fc.setRtDate(ZonedDateTime.now(zID));
                  fc.setSeconds(17);
            }
            //return fc;
      }

      /**
       * sets the limit in the number of days that a card should be hidden after
       * meeting the threshold.
       * @param d the number of days a card should be hidden after meeting the
       * threshold.
       */
      public void setDays(int d) {
            days = d;
      }

      /**
       * returns the limit/number of days that a card should be hidden before
       * needing to be reviewed again.
       * @return int the number of days that the limit sis set to.
       */
      public int getDays() {
            return days;
      }

}
