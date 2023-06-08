package type.tools.video;

import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.info.VideoSize;

import java.io.File;

public class Fit {

      private static final int MAX_HT = 1024;
      private static final int MAX_WD = 1024;

      public Fit() { /* no args */}

      /**
       * Checks the
       *
       * @param sourceInfo
       * @return A video size scaled to less than
       * the max wd and max ht
       */
      public static VideoSize size(MultimediaInfo sourceInfo) throws EncoderException {

            VideoSize size = sourceInfo.getVideo().getSize();
            int ht = size.getHeight();
            int wd = size.getWidth();

            if (MAX_HT < ht || MAX_WD < wd) {
                  double scale = type.tools.imagery.Fit.calcScale(wd, ht, MAX_WD, MAX_HT);
                  wd = (int) (1 * Math.ceil(wd * scale));
                  ht = (int) (1 * Math.ceil(ht * scale));
            }
            return new VideoSize(wd, ht);
      }

      /**
       * Checks if the size is less than the max ht and max wd
       *
       * @param sourceInfo
       * @return True if the ht and wd are less than max ht and max wd
       */
      public static boolean checkSize(MultimediaInfo sourceInfo) {
            VideoSize size = sourceInfo.getVideo().getSize();
            int ht = size.getHeight();
            int wd = size.getWidth();
            return MAX_HT >= ht && MAX_WD >= wd;
      }

      /**
       * Verifies that the max duration of the video
       * is less than 7 minutes
       *
       * @param sourceInfo the sourceInfo object
       * @param minutes
       * @return True if the video is <code> <= </code> the minutes provided in the param
       */
      public static boolean checkDuration(MultimediaInfo sourceInfo, int minutes) {
            long time = sourceInfo.getDuration();
            long max = (minutes * 60 * 1000);
            return max <= time;
      }
}
