package forms;

import com.dlsc.formsfx.view.renderer.FormRenderer;
import flashmonkey.CreateFlash;
import fmannotations.FMAnnotations;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import metadata.DeckMetaData;
import org.slf4j.LoggerFactory;
import uicontrols.SceneCntl;

public class DeckMetaPane extends FormParentPane {

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DeckMetaPane.class);
    
    private Label cardNumLabel;
    private Label imgNumLabel;
    private Label vidNumLabel;
    private Label audNumLabel;
    private Label lastScoreLabel;
    
    protected DeckMetaModel model;
    protected DeckMetaData meta;
    
    public DeckMetaPane() {
        super(); // FormParent
        LOGGER.info("DeckMetaModel called.");
    }

    /**
     * This method is called by Forms api ViewMixin
     */
    @Override
    public void initializeSelf() {
        
        model = new DeckMetaModel();
        meta  = DeckMetaData.getInstance();
        this.initialize(model, meta);
        // retrieve existing metadata
        // from disk and set the metadata
        // object.
        // meta.setDeckMetaFmFile();
        
    }

    /**
     * This method initializes all child class nodes and regions.
     */
    @Override
    public void initializeParts() {
        
        CreateFlash.getInstance().updateDeckInfo(meta); // seems to be clearing out the form after downloading from file or from DB???
        
        cardNumLabel = new Label("Cards: "  + meta.getNumCard());//model.getDataModel().getNumCards());
        imgNumLabel = new Label("Images: "  + meta.getNumImg());//model.getDataModel().getNumImgs());
        vidNumLabel = new Label("Videos: "  + meta.getNumVideo());//model.getDataModel().getNumVideo());
        audNumLabel = new Label("Audio: "   + meta.getNumAudio());//model.getDataModel().getNumAudio());
        lastScoreLabel = new Label("Last Score: " + meta.calcLastScore());//model.getDataModel().getLastScore());
        
        super.formRenderer = new FormRenderer(model.getFormInstance());
    }
    
    

    @Override
    public void setupValueChangedListeners() {
        //model.getFormInstance().changedProperty().addListener((observable, oldValue, newValue) -> changedLabel.setText("The form has " + (newValue ? "" : "not ") + "changed."));
        //model.getFormInstance().validProperty().addListener((observable, oldValue, newValue) -> validLabel.setText("The form is " + (newValue ? "" : "not ") + "valid."));
        //model.getFormInstance().persistableProperty().addListener((observable, oldValue, newValue) -> persistableLabel.setText("The form is " + (newValue ? "" : "not ") + "persistable."));

        //model.getCountry().nameProperty().addListener((observable, oldValue, newValue) -> countryLabel.setText("Country: " + newValue));
        //model.getCountry().currencyShortProperty().addListener((observable, oldValue, newValue) -> currencyLabel.setText("Currency: " + newValue));
        //model.getCountry().populationProperty().addListener((observable, oldValue, newValue) -> populationLabel.setText("Population: " + newValue));
    }

    

    @Override
    public void layoutParts() {
        super.layoutParts();
        LOGGER.info("*** create MetaData form called ***");
        formRenderer.setMaxWidth((SceneCntl.getEditorWd() - 10));
        
        // Card info
        innerGPane.addRow(0, cardNumLabel, imgNumLabel, vidNumLabel, audNumLabel);
        innerGPane.addRow(1, lastScoreLabel);
    }

    @Override
    public GridPane getFormPane() {
        return this.formPane;
    }
    
    
    /* *** FOR TESTING *** */
    @FMAnnotations.DoNotDeployMethod
    public DeckMetaModel getModel() {
        return this.model;
    }
    
    
    

}
