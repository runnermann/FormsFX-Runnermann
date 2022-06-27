package forms.utility;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ResetDescriptor {

      private static final StringProperty resetCodeProperty = new SimpleStringProperty("");
      private static final StringProperty emailEntry = new SimpleStringProperty("");

      public ResetDescriptor() {
            /* do nothing */
      }

      public String getResetCodeProperty() {
            return resetCodeProperty.get();
      }

      public String getEmailProperty() {
            return emailEntry.get().toLowerCase();
      }

      public StringProperty resetCodeProperty() {
            return resetCodeProperty;
      }

      public StringProperty emailEntryProperty() {
            return emailEntry;
      }

      public void clear() {
            resetCodeProperty.setValue("");
            emailEntry.setValue("");
      }
}
