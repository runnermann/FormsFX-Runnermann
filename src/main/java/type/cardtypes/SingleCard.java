package type.cardtypes;

import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import type.sectiontype.GenericSection;

/**
 * Returns a single section with data in it. Only txt, mmType and path are used.
 *
 * @author Lowell Stadelman
 */
public class SingleCard extends GenericCard
{
    //@Override
    public Pane retrieveCard(String text, char mmType, String ... path)
    {
        GenericSection gs = GenericSection.getInstance();
        StackPane pane = new StackPane();
        pane.getChildren().add( gs.sectionFactory(text, mmType, 1, true, 0, path) );
        pane.setAlignment(Pos.CENTER);
        return pane;
    }
}
