package uicontrols;

import javafx.scene.control.Hyperlink;

/**
 * Extends the JavaFX Hyperlink class and adds
 * the needed link that may be used for a URL. ;)
 */
public class FMHyperlink extends Hyperlink {
      String link;

      public FMHyperlink() {
            super();
            link = "";
      }

      /**
       * Now we can use a hyperlink to get a URL without adding a seperate
       * field to another class.
       * @param label The visible label.
       * @param link The URL or String that will be used when the link is clicked.
       */
      public FMHyperlink(String label, String link) {
            super(label);
            this.link = link;
      }

      public String getLink() {
            return  link;
      }

      public void setLink(String link) {
            this.link = link;
      }
}
