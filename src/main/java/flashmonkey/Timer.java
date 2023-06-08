package flashmonkey;

/*******************************************************************************
 * Provides the time it takes to operate. Call start method at the beginning
 * of the operation you wish to test and end() at the end of the methods or
 * operation you wish to test. Call printTime() to print the time. :)
 ******************************************************************************/

import javafx.util.StringConverter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.lang.*;

/*** IMPORTS ***/


public class Timer {
      private static Timer CLASS_INSTANCE;
      private static long fmStartTime;
      private static long testStartTime;
      private static long createStartTime;
      // start times

      private static final long fmBeginTime = System.currentTimeMillis();
      private long createTime = 0;
      private long createNotesTime = 0;
      private long qnaTime = 0;
      private long takeTestTime = 0;
      // cloud analysis time
      private final long cloudAnTime = 0;
      private String note;

      private Timer() {/* no args constructor */}

      public static synchronized Timer getClassInstance() {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new Timer();
            }
            return CLASS_INSTANCE;
      }

      // For testing cloud performance. Cloud Analysis
      public long getCloudAnTime() {
            return cloudAnTime;
      }

      // Use analytics and performance

      /**
       * Zeros the create time and returns the quantity
       * of the create time.
       * @return the createTime
       */
      public long getCreateTimeNonModifiable() {
            long num = createTime;
            createTime = 0;
            return num;
      }

      public void startQnATime() {
            qnaTime = System.currentTimeMillis();
      }

      /**
       * For use by FM use time, not user metrics
       * @return system time - testStartTime.
       */
      public long getQnATime() {
            long num = qnaTime;
            qnaTime = 0;
            return System.currentTimeMillis() - num;
      }

      /**
       * Zeros the createNotesTime and returns the quantity
       * of createNotesTime;
       * @return the quantity of create notes time.
       */
      public long getCreateNotesTimeNonModifiable() {
            long num = this.createNotesTime;
            this.createNotesTime = 0;
            return num;
      }

      public long getTakeTestTime() {
            long num = this.takeTestTime;
            this.takeTestTime = 0;
            return num;
      }

      public long getBeginTime() {
            return this.fmBeginTime;
      }

      public long getFMTotalTime() {
            return (System.currentTimeMillis() - this.fmBeginTime);
      }

      public String getNote() {
            return this.note;
      }

      // ******* SETTER ********
      public void setNote(String note) {
            this.note = note;
      }


      /**
       * Start the timer, then stop and record with a stop... m
       * method.
       */
      public void startFlashMonkeyUseTime() {
            fmStartTime = System.currentTimeMillis();
      }

      public void startCreateTime() {
            createStartTime = System.currentTimeMillis();
            createTime = 0;
      }

      public void startTestTime() {
            testStartTime = System.currentTimeMillis();
      }

      /**
       * Stops and captures creatTime. Adds the
       * quantity of time to the existing time if
       * any exists previously.
       */
      public void createTimeStop() {
            if (createStartTime != 0) {
                  long useTime = System.currentTimeMillis() - createStartTime;
                  createTime += useTime;
            }
      }

      /**
       * stops and captures testTime.
       */
      public void testTimeStop() {
            if (testStartTime != 0) {
                  takeTestTime += System.currentTimeMillis() - testStartTime;
            }
      }

//      /**
//       * stops and captures qnATime
//       */
//      public void qnaTimeStop() {
//            if (readOrCreateStartTime != 0) {
//                  qnaTime += System.currentTimeMillis() - readOrCreateStartTime;
//            }
//            readOrCreateStartTime = 0;
//      }

      public String printTimeNow() {
            LocalTime hour = ZonedDateTime.now().toLocalTime().truncatedTo(ChronoUnit.SECONDS);
            return hour.toString();
      }

      public String getTotalTimeString() {
            long now = getFMTotalTime();
            long millis = now % 1000;
            long second = (now / 1000) % 60;
            long minute = (now / (1000 * 60)) % 60;
            long hour = (now / (1000 * 60 * 60)) % 60;

            return String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);
      }

      /**
       * Returns the epoch date time in milliseconds using Jan 01, 1970 at 00:00:00
       *
       * @param strDate Expects "MM/dd/yyyy"
       * @param strTime Expects "hh:mm:ss"
       * @return the epoch date time in milliseconds using Jan 01, 1970 at 00:00:00
       */
      public Long getMills(String strDate, String strTime) {
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate date = new LocalDateStringConverter().fromString(strDate);
            LocalTime time = new LocalTimeStringConverter().fromString(strTime);
            LocalDateTime ldt = LocalDateTime.of(date, time);

            ZonedDateTime zdt = ldt.atZone(ZoneId.of("America/Los_Angeles"));
            return zdt.toInstant().toEpochMilli();
      }

      public Long getMillis(LocalDate date, LocalTime time) {

            LocalDateTime ldt = LocalDateTime.of(date, time);
            ZonedDateTime zdt = ldt.atZone(ZoneId.of("America/Los_Angeles"));
            return zdt.toInstant().toEpochMilli();
      }


      public void printTime() {
            double milliseconds = getFMTotalTime();
      }


      /**
       * INNER CLASSES
       **/

      protected class LocalDateStringConverter extends StringConverter<LocalDate> {
            private String pattern = "MM/dd/yyyy";
            private final DateTimeFormatter dtFormat;

            public LocalDateStringConverter() {
                  dtFormat = DateTimeFormatter.ofPattern(pattern);
            }

            public LocalDateStringConverter(String pattern) {
                  this.pattern = pattern;
                  dtFormat = DateTimeFormatter.ofPattern(pattern);
            }

            /**
             * Returns a LocalDate
             *
             * @param strDate The String date yyyy/mm/dd
             * @return a LocalDate
             */
            @Override
            public LocalDate fromString(String strDate) {
                  if (strDate != null && !strDate.trim().isEmpty()) {
                        return LocalDate.parse(strDate, dtFormat);
                  }
                  return null;
            }

            @Override
            public String toString(LocalDate date) {
                  String text = null;
                  if (date != null) {
                        text = dtFormat.format(date);
                  }
                  return text;
            }
      }

      protected class LocalTimeStringConverter extends StringConverter<LocalTime> {
            private String pattern = "HH:mm";
            private final DateTimeFormatter dtFormat;

            public LocalTimeStringConverter() {
                  dtFormat = DateTimeFormatter.ofPattern(pattern);
            }

            public LocalTimeStringConverter(String pattern) {
                  this.pattern = pattern;
                  dtFormat = DateTimeFormatter.ofPattern(pattern);
            }

            /**
             * Retuns a LocalTime object from the parameter
             *
             * @param strTime Expected in hh:mm:ss
             * @return
             */
            @Override
            public LocalTime fromString(String strTime) {
                  if (strTime != null) {
                        return LocalTime.parse(strTime, dtFormat);
                  }
                  return null;
            }

            @Override
            public String toString(LocalTime time) {
                  String text = null;
                  if (time != null) {
                        text = dtFormat.format(time);
                  }
                  return text;
            }
      }
}
