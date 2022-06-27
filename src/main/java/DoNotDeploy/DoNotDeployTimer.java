package DoNotDeploy;

import javafx.util.StringConverter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.lang.*;


/**
 * This is a repeat class of Timer. This is the non singleton class
 * used during development for performance testing. It should not be
 * deployed.
 */
public class DoNotDeployTimer {


      // @ TODO delete THis Class before deploying.
      //private static final long serialVersionUID = FlashMonkeyMain.VERSION;
      private long startTime;
      private long endTime = 0;
      // start times

      // cloud analysis time
      private final long cloudAnTime = 0;

      public DoNotDeployTimer() {/* no args constructor */}


      // For testing cloud performance. Cloud Analysis
      public long getCloudAnTime() {
            return cloudAnTime;
      }

      // Use analytics and performance

      public long getEndTime() {
            return this.endTime;
      }

      public long getStartTime() {
            return this.startTime;
      }

      public long getTotalTime() {
            return (this.endTime - this.startTime);
      }


      /**
       * Start the timer, then stop and record with a stop... m
       * method.
       */
      public void start() {
            startTime = System.currentTimeMillis();
            //System.out.println("start time: " + startTime);
      }

      /**
       * Program start.
       */
      public void begin() {
            this.startTime = System.currentTimeMillis();
      }

      public void end() {
            this.endTime = System.currentTimeMillis();
      }


      public String printTimeNow() {
            LocalTime hour = ZonedDateTime.now().toLocalTime().truncatedTo(ChronoUnit.MILLIS);
            return hour.toString();
      }

      public String getTotalTimeString() {
            long now = this.endTime - this.startTime;
            long millis = now % 1000;
            long second = (now / 1000) % 60;
            long minute = (now / (1000 * 60) % 60);
            long hour = (now / (1000 * 60 * 60) % 60);

            return String.format("%02d:%02d:%02d:%d", hour, minute, second, millis);
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
