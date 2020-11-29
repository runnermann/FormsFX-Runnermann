/*
 * This is the gauge assymbly class. It creates a gauge the size and width
 * provided in the constructor parameters. The gauge is returned in a GridPane.
 * To create a guage, Define the size of the pane in the constructor, then use the
 * The makeGauge(String name) method will return the gridPane with the gauge.
 */
package flashmonkey;

import gaugeLib.hansolo.medusa.*;
import gaugeLib.hansolo.medusa.GaugeBuilder;
//import gaugeLib.hansolo.medusa.skins.SlimSkin;
//import javafx.application.Application;
//import javafx.application.Application.launch;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
//import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
//import javafx.scene.shape.Rectangle;
//import javafx.stage.Stage;

/**
 * Provides the EncryptedUser.EncryptedUser interface for the Gauges.
 * @author Lowell Stadelman
 */
public class MGauges
{
    private GridPane pane;    
    private Gauge gauge; 
    private GaugeBuilder builder;
    private VBox progrIndBox;
    // the value that the gage represents
    //private double value;
    private double width;
    private double height;
    
    /**
     * Constructor: Creates a gauge that is the width and height and name 
     * of the gauge.
     * @param w
     * @param h
     */
    public MGauges(double w, double h)
    {
        this.pane = new GridPane();
        this.builder = GaugeBuilder.create().skinType(Gauge.SkinType.FLAT);
        this.gauge = builder.decimals(1).maxValue(20).unit("%").build();
        this.width = w;
        this.height = h;
    }
    
    /**
     * Creates a gauge sized to the parameters in the constructor
     * @param name The name of the gauge.
     * @param initialV the initial value.
     * @param high The higest number of elements measured by the gauge.
     * @param low The lowest number of elements measured by the gauge. Gauge
     * can go negitive. 
     * @return Returns a gauge with the prefered height and 
     * width provided in the parameters.
     */
    public GridPane makeGauge(String name, double initialV, double high, double low) 
    {  
        
        pane.setPadding(new Insets(5));  
        //pane.setHgap(10);  
        //pane.setVgap(15);     
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(255,255,255),
                CornerRadii.EMPTY, Insets.EMPTY)));          
        pane.setPrefSize(width, height);
        gauge.setValue(initialV);
        gauge.setMaxValue(high);
        gauge.setMinValue(low);
        gauge.setAnimated(true);
        progrIndBox = getTopicBox(name, Color.rgb(186,104,200), gauge);
        pane.add(progrIndBox, 1, 4);
        return pane;
    }

    public void setBase(int num)
    {
        gauge.setMaxValue( num );
        //gauge.setAnimationDuration(duration);
    }
    
    /**
     * When the value of the gauge is changed, use moveGuage to set
     * the new value of the gauge. Set the duration of the change in
     * parameter d. The duration should change depending on the distance
     * depending on the desired effect. The newValue is the value the 
     * gauge will change to. 
     * 
     * @param d The duration of the change in milliseconds
     * @param newValue The new value of the gauge.
     */
    public void moveNeedle(long d, double newValue)
    {
        gauge.setValue(newValue);
        gauge.setAnimationDuration(d);
    }

    /**
     * Sets a gauge with the name, and color provided in the 
     * parameters
     * @param TEXT
     * @param COLOR
     * @param GAUGE
     * @return Returns a VBox with the gauge inside it. The size of the
     * VBox is provided in the class constructor
     */
    private VBox getTopicBox(final String TEXT, final Color COLOR, final Gauge GAUGE) {  

        GAUGE.setBarColor(COLOR);  
        GAUGE.setBarBackgroundColor(Color.rgb(39,44,50));  
        GAUGE.setAnimated(true); 
        GAUGE.setMajorTickMarksVisible(false);
        GAUGE.setPrefSize(width, height);
        GAUGE.setTitle(TEXT);
        GAUGE.setUnit("");
        VBox vBox = new VBox(GAUGE);
        vBox.setSpacing(3);  
        vBox.setAlignment(Pos.CENTER);  
        return vBox;  
     }       
}


