package multimedia;

import java.io.Serializable;
import java.util.ArrayList;

import flashmonkey.Answer;
import flashmonkey.FlashMonkeyMain;

/**
 * @author Lowell Stadelman
 */
public class AnswerMM extends Answer implements Serializable, Comparable {
      private static final long serialVersionUID = FlashMonkeyMain.VERSION;

      /**
       * VARIABLES
       **/
      private char aType;
      private String[] aFiles;

      /**
       * Default constructor
       */
      public AnswerMM() {
            super("", 0, DEF_LIST);
            this.aType = 't';
      }

      /**
       * Constructor Version MultiMedia 2018-05-25
       * Does not store multi media in the object. Stores the
       * path to the MM file, Constants provide the multi media type
       * to be created later in the view pane
       *
       * @param txt
       * @param type   M = media (video and audio) , D = drawing, S = shape, defualt = null
       * @param ansSet
       */
      public AnswerMM(String txt, int aNum, char type, ArrayList<Integer> ansSet, String[] fileAry) {
            super(txt, aNum, ansSet);
            this.aType = type;
            //this.aFiles = fileAry;
            setAFiles(fileAry);
      }

      // CopyConstructor
      public AnswerMM(AnswerMM original) {
            super();
            this.setAText(original.getAText());
            this.setANumber(original.getANumber());
            this.setAnswerSet(new ArrayList<>(original.getAnswerSet())); // is this wrong?
            if(original.aFiles != null) {
                  if(original.aFiles.length > 1) {
                        this.aFiles = new String[]{original.aFiles[0], original.aFiles[1]};
                  } else {
                        this.aFiles = new String[]{original.aFiles[0]};
                  }
            }
            this.aType = original.aType;
      }


      /*** SETTERS ***/

      public void setAType(char type) {
            this.aType = type;
      }

      public void setAFiles(String[] files) {
            if (files != null) {
                  this.aFiles = new String[files.length];
                  for (int i = 0; i < files.length; i++) {
                        this.aFiles[i] = files[i];
                  }
            }
      }

      /**
       * Sets the media Type. Use the media type chars
       * I = images, D = drawings, A = audio, or V = video
       *
       * @param type The char representing the type of media
       */
      protected void setMediaType(char type) {
            this.aType = type;
      }


      /*** GETTERS ***/

      /**
       * Returns the media Type. The media type chars
       * I = images, D = drawings, A = audio, or V = video
       *
       * @return returns the media type
       */
      public char getAType() {
            return this.aType;
      }

      /**
       * This Answer media files
       *
       * @return Returns a deep copy of the immutable media files associated with this
       * answer.
       */
      public String[] getAFiles() {
            if (this.aFiles != null) {
                  String[] s = new String[this.aFiles.length];
                  for (int i = 0; i < this.aFiles.length; i++) {
                        s[i] = this.aFiles[i];
                  }
                  return s;
            }
            return null;
      }

      /**
       * Not a complete hash.
       * Returns a hashcode with the expected use of comparison
       * of multi-media answers where to prevent the same answers
       * from being inserted more than once.
       *
       * @return A hashcode of the text and the media files,
       */
      @Override
      public int hashCode() {
            int hash1 = this.getAText().hashCode();
            int hash2 = 0;
            int hash3 = 0;
            if(null != this.getAFiles() && null != this.getAFiles()[0]) {
                hash2 = this.getAFiles()[0].hashCode();
                if (2 == this.getAFiles().length && null != this.getAFiles()[1]) {
                      hash3 = this.getAFiles()[1].hashCode();
                }
            }
            return hash1 + hash2 + hash3;
      }

      @Override
      public boolean equals(Object obj) {
            if (obj != null) {
                  AnswerMM mm = (AnswerMM) obj;
                  return (this.getAText().equals(mm.getAText()) && filesEqual(mm.getAFiles()));
            } else {
                  //System.out.println("Recieved null input in AnswerMM equals :(");
                  return false;
            }
      }

      /**
       * Media Files are named uniquely. If images match they will have the exact same name. Shapes are named by the
       * image, and time in millis that they were created. Therefore we only need to compare if the file names are the
       * same.
       * @param mediaFileAry
       * @return true if the files exist and are the same file names. And true if the files do not exist.
       */
      private boolean filesEqual(String[] mediaFileAry) {
            if(null == mediaFileAry && null == this.getAFiles()) {
                  return true;
            }
            int a = (this.getAFiles().length + mediaFileAry.length);
            switch (a) {
                  case 0: { return true; }
                  case 2: { return this.getAFiles()[0].equals(mediaFileAry[0]); }
                  case 4: { return this.getAFiles()[0].equals(mediaFileAry[0]) && this.getAFiles()[1].equals(mediaFileAry[1]); }
            }
            return false;
      }

      public String toString() {
            String aFil = "null";
            if (this.aFiles != null && this.aFiles.length > 0) {
                  aFil = "\n\t\t qFiles[0]  = " + this.aFiles[0] != null ? this.aFiles[0] : "null"
                      + "\n\t\t qFiles[1]  = " + this.aFiles[1] != null ? this.aFiles[1] : "null";
            }

            return super.toString()
                + "\nAnswerMM: "
                + "\n\t aType  = " + this.aType
                + "\n\t aNumber  = " + this.getANumber()
                + "\n\t aType  = " + this.aType
                + "\n\t aFiles  = " + aFil;
      }
}
