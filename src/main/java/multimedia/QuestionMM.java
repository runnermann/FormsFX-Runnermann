/*
 * AUTHOR: Lowell Stadelman
 */

package multimedia;

import java.io.Serializable;


/*** IMPORTS ***/
import flashmonkey.FlashMonkeyMain;
import flashmonkey.Question;

/**
 * @author Lowell Stadelman
 */
public class QuestionMM extends Question implements Serializable, Comparable {
      private static final long serialVersionUID = FlashMonkeyMain.VERSION;

      /**
       * VARIABLES
       **/
      private char qType;
      // media files
      private String[] qFiles;

      /**
       * DEFAULT CONSTRUCTOR
       **/
      public QuestionMM() {
            super();
            this.qType = 't';
      }


      /**
       * FULL CONSTRUCTOR
       **/
      //public QuestionMM(String q, int type, String fileName)
      public QuestionMM(String q, char type, String[] fileAry) {
            super(q);
            this.qType = type;
            setQFiles(fileAry);
      }

      // DESCRIPTION: SHALLOW COPY CONSTRUCTOR
      public QuestionMM(QuestionMM original) {
            if (original != null) {
                  this.setQText(original.getQText());
                  this.qType = original.qType;
                  if(original.qFiles != null) {
                        if(original.qFiles.length > 1) {
                              this.qFiles = new String[]{original.qFiles[0], original.qFiles[1]};
                        } else {
                              this.qFiles = new String[]{original.qFiles[0]};
                        }
                  }
            } else {
                  throw new IllegalStateException("questionMM is null in shallow copy constructor");
                  //System.exit(0);
            }
      }

      /**
       * SETTERS
       **/

      public void setQType(char type) {
            this.qType = type;
      }

      public void setQFiles(String[] files) {
            if (files != null) {
                  this.qFiles = new String[files.length];
                  for (int i = 0; i < files.length; i++) {
                        this.qFiles[i] = files[i];
                        //System.out.println(this.qFiles[i]);
                  }
            }
      }


      /*** GETTERS ***/

      /**
       * This Questions media files
       *
       * @return Returns a deep copy of the immutable media files associated with this
       * question.
       */
      public String[] getQFiles() {
            if (this.qFiles != null) {
                  String[] s = new String[this.qFiles.length];
                  for (int i = 0; i < this.qFiles.length; i++) {
                        s[i] = this.qFiles[i];
                  }
                  return s;
            }
            return null;
      }

      /**
       * Returns the media Type. The media type chars
       * I = images, D = drawings, A = audio, or V = video
       *
       * @return returns the media type
       */
      public char getQType() {
            return this.qType;
      }

      /**
       * toStsring method
       *
       * @return Returns the question
       */
      @Override
      public String toString() {
            String qFil = "null";
            if (this.qFiles != null && this.qFiles.length > 0) {
                  qFil = "\n\t\t qFiles[0]  = " + this.qFiles[0] != null ? this.qFiles[0] : "null"
                      + "\n\t\t qFiles[1]  = " + this.qFiles[1] != null ? this.qFiles[1] : "null";
            }
            return super.toString()
                + "\nQuestionMM: "
                + "\n\t numRigth  = " + this.qType
                    + "\n\t qType = " + this.qType
                + "\n\t qFiles  = " + qFil;

      }


}