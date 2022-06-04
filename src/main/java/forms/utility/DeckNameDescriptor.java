package forms.utility;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DeckNameDescriptor {

    private static StringProperty nameProperty = new SimpleStringProperty("");

    public DeckNameDescriptor() {
        /* EMPTY */
    }

    public String getName() {
        return nameProperty.get();
    }

    public StringProperty nameProperty() { return nameProperty; };

    public void clear() {
        nameProperty().set("");
    }
}
