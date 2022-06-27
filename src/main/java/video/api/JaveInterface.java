package video.api;

import ch.qos.logback.classic.Level;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import org.slf4j.LoggerFactory;
import type.celleditors.SectionEditor;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.InputFormatException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.info.VideoSize;
import ws.schild.jave.progress.EncoderProgressListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class JaveInterface implements EncoderProgressListener {

      private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(SectionEditor.class);
      private double progress;
      private final Encoder encoder;
      private long fileLength;
      private final IntegerProperty simpleInt = new SimpleIntegerProperty(0);
      private boolean isComplete;

      public JaveInterface() {
            LOGGER.setLevel(Level.DEBUG);
            encoder = new Encoder();
      }


      public void transfer(File sourceFile, File outputFile, MultimediaInfo sourceInfo, MultimediaObject mmObject) throws EncoderException {

            LOGGER.debug("starting to transfer a video file");

            //this.fileLength = mmObject.getFile().length();

            System.out.println(mmObject.getInfo().getMetadata());
            System.out.println("Source info size: " + sourceInfo.getVideo().getSize().getWidth());

            // get the current video's attributes
            VideoSize size = type.tools.video.Fit.size(sourceInfo);
            //Audio Attributes
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec(AudioAttributes.DIRECT_STREAM_COPY);
            audio.setBitRate(128000);
            audio.setChannels(2);

            VideoAttributes video = new VideoAttributes();
            if (sourceInfo.getVideo().getBitRate() >= 640000) {
                  video.setBitRate(sourceInfo.getVideo().getBitRate() / 2);
            } else {
                  video.setBitRate(sourceInfo.getVideo().getBitRate());
            }
            if (sourceInfo.getVideo().getFrameRate() >= 30) {
                  video.setFrameRate((int) sourceInfo.getVideo().getFrameRate() / 2);
            } else {
                  video.setFrameRate((int) sourceInfo.getVideo().getFrameRate());
            }

            // set the size of the video in the metadata
            Map<String, String> map = new HashMap<>(6);
            map.put("Metadata", size.getWidth().toString() + "x" + size.getHeight().toString());
            mmObject.getInfo().setMetadata(map);

            video.setSize(size);
            video.setCodec("h264");
            //Encoding attributes
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setOutputFormat("mp4");
            attrs.setAudioAttributes(audio);
            attrs.setVideoAttributes(video);

            encode(mmObject, outputFile, attrs);
      }

      public void encode(MultimediaObject mmObject, File outputFile, EncodingAttributes attrs)
          throws java.lang.IllegalArgumentException, EncoderException {
            encoder.encode(mmObject, outputFile, attrs, this);
      }

      @Override
      public void sourceInfo(MultimediaInfo multimediaInfo) {
            System.out.println("\n\t *** Jave called JaveInterface.sourceInfo(): " + multimediaInfo.toString() + " ***\n");
      }


      @Override
      public void progress(int permil) {
            progress += permil;
            System.out.println("Progress: " + progress);
      }

      @Override
      public void message(String s) {
            System.out.println("\n\t *** Jave message: " + s + "***\n");
      }

      public double getProgress() {

            return this.progress;
      }

      public void stop() {
            encoder.abortEncoding();
      }

      public String buildMetaDataString(int wd, int ht) {
            return "Video: h264 (High), yuv420p, " + wd + "x" + ht + ", ";
      }


}
