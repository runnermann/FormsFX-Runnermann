package forms.utility;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CodeDescriptor {

      private StringProperty codeProperty = new SimpleStringProperty("");

      public CodeDescriptor() { /* do nothing */}

      public String getCode() {
            return codeProperty.get();
      }

      public StringProperty getCodeProperty() {
            return codeProperty;
      }

      public void clear() {
            codeProperty = new SimpleStringProperty("");
      }
}
