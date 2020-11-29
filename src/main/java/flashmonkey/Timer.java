
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


public class Timer 
{
    private static Timer CLASS_INSTANCE;
    //@todo Create a setter for the time zone. Currently set to default of Los Angeles in method.
    //private static final long serialVersionUID = FlashMonkeyMain.VERSION;
    private static long startTime;
    private long endTime = 0;
    // start times

    private long beginTime = 0;
    private long createTestsTime = 0;
    private long createNotesTime = 0;
    private long qnaTime = 0;
    private long takeTestTime = 0;
    // cloud analysis time
    private long cloudAnTime = 0;

    private Timer() {/* no args constructor */}

    public static synchronized Timer getClassInstance() {
            if(CLASS_INSTANCE == null) {
                CLASS_INSTANCE = new Timer();
            }
            return CLASS_INSTANCE;
    }

    // For testing cloud performance. Cloud Analysis
    public long getCloudAnTime() { return cloudAnTime; }
    
    // Use analytics and performance
    public long getCreateTestsTime() {
        return createTestsTime;
    }

    public long getQnATime() {
        return this.qnaTime;
    }

    public long getCreateNotesTime() {
        return this.createNotesTime;
    }

    public long getTakeTestTime() {
        return this.takeTestTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public long getBeginTime() {
        return this.beginTime;
    }

    public long getTotalTime() {
        return (this.endTime - this.beginTime);
    }



    /**
     * Start the timer, then stop and record with a stop... m
     * method.
     */
    public void startTime() {
        startTime = System.currentTimeMillis();
       //System.out.println("start time: " + startTime);
    }

    /**
     * Program start.
     */
    public void begin() {
        this.beginTime = System.currentTimeMillis();
    }

    /**
     * stops and captures creatTEstTime.
     */
    public void createTestTimeStop() {
        if(startTime != 0) {
            createTestsTime += System.currentTimeMillis() - startTime;
        }
        //startTime = 0;
    }

    /**
     * stops and captures testTime.
     */
    public void testTimeStop() {
        if(startTime != 0) {
            takeTestTime += System.currentTimeMillis() - startTime;
        }
       //System.out.println("in testTimeStop(), start: " + startTime + ", result testTime: " + takeTestTime);
        //start = 0;
    }

    /**
     * stops and captures createNOtesTime
     */
    public void createNotesTimeStop() {
        if(startTime != 0) {
            createNotesTime += System.currentTimeMillis() - startTime;
        }
        //startTime = 0;
    }

    /**
     * stops and captures qnATime
     */
    public void qnaTimeStop() {
        if(startTime != 0) {
            qnaTime += System.currentTimeMillis() - startTime;
        }
        //startTime = 0;
    }
     
     public void end() {
         this.endTime = System.currentTimeMillis();
     }



     public String printTimeNow() {
            LocalTime hour = ZonedDateTime.now().toLocalTime().truncatedTo(ChronoUnit.SECONDS);
            return hour.toString();
     }

     public String getTotalTimeString() {
         long millis = getTotalTime();
         long second = (millis / 1000) % 60;
         long minute = (millis / (1000 * 60)) % 60;
         long hour = (millis / (1000 * 60 * 60)) % 60;

         return String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);
     }

    /**
     * Returns the epoch date time in milliseconds using Jan 01, 1970 at 00:00:00
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
         double milliseconds = getTotalTime();
        //System.out.printf("Execution time %,4f milliseconds", milliseconds);
     }



     /** INNER CLASSES **/

     protected class LocalDateStringConverter extends StringConverter<LocalDate> {
         private String pattern = "MM/dd/yyyy";
         private DateTimeFormatter dtFormat;

         public LocalDateStringConverter() {
             dtFormat = DateTimeFormatter.ofPattern(pattern);
         }

         public LocalDateStringConverter(String pattern) {
             this.pattern = pattern;
             dtFormat = DateTimeFormatter.ofPattern(pattern);
         }

         /**
          * Returns a LocalDate
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
             if(date != null) {
                 text = dtFormat.format(date);
             }
             return text;
         }
     }

    protected class LocalTimeStringConverter extends StringConverter<LocalTime> {

         private String pattern = "HH:mm";
         private DateTimeFormatter dtFormat;

         public LocalTimeStringConverter() {
             dtFormat = DateTimeFormatter.ofPattern(pattern);
         }

         public LocalTimeStringConverter(String pattern) {
             this.pattern = pattern;
             dtFormat = DateTimeFormatter.ofPattern(pattern);
         }

        /**
         * Retuns a LocalTime object from the parameter
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
            if(time != null) {
                text = dtFormat.format(time);
            }
            return text;
        }
    }
}
