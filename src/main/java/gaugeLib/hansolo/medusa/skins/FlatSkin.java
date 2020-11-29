/*
 * Copyright (c) 2016 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gaugeLib.hansolo.medusa.skins;

import gaugeLib.hansolo.medusa.Fonts;
import gaugeLib.hansolo.medusa.Gauge;
import gaugeLib.hansolo.medusa.Section;
import gaugeLib.hansolo.medusa.tools.Helper;

import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

import static gaugeLib.hansolo.medusa.tools.Helper.formatNumber;


/**
 * Originally Created by hansolo on 06.01.16.
 * Modified to improve performance by 300x Jun 2018 By FM.
 */
public class FlatSkin extends GaugeSkinBase {
    private static final double  ANGLE_RANGE      = 360;
    private double               size;
    private Circle               colorRing;
    private Arc                  roundBar;
    private Line                 topLine;
    private Text                 titleText;
    private Text                 valueText;
    private Text                 unitText;
    private Pane                 pane;
    private double               minValue;
    private double               range;
    private double               angleStep;
    private boolean              colorGradientEnabled;
    private int                  noOfGradientStops;
    private boolean              sectionsVisible;
    private List<Section>        sections;
    private InvalidationListener currentValueListener;


    // ******************** Constructors **************************************
    public FlatSkin(Gauge gauge) {
        super(gauge);
        if (gauge.isAutoScale()) gauge.calcAutoScale();
        minValue             = gauge.getMinValue();
        range                = gauge.getRange();
        angleStep            = ANGLE_RANGE / range;
        colorGradientEnabled = gauge.isGradientBarEnabled();
        noOfGradientStops    = gauge.getGradientBarStops().size();
        sectionsVisible      = gauge.getSectionsVisible();
        sections             = gauge.getSections();
        currentValueListener = o -> setBar(gauge.getCurrentValue());

        initGraphics();
        registerListeners();

        setBar(gauge.getCurrentValue());
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        // Set initial size
        //if (Double.compare(gauge.getPrefWidth(), 0.0) <= 0 || Double.compare(gauge.getPrefHeight(), 0.0) <= 0 ||
        //    Double.compare(gauge.getWidth(), 0.0) <= 0 || Double.compare(gauge.getHeight(), 0.0) <= 0) {
        //    if (gauge.getPrefWidth() > 0 && gauge.getPrefHeight() > 0) {
        gauge.setPrefSize(gauge.getPrefWidth(), gauge.getPrefHeight());
        //    } else {
        //        gauge.setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        //    }
        //}

        colorRing = new Circle(50, 50, 50);
        colorRing.setFill(Color.TRANSPARENT);
        colorRing.setStrokeWidth(3);
        colorRing.setStroke(gauge.getBarColor());

        //roundBar = new Arc(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.468, PREFERRED_HEIGHT * 0.468, 90, 0);
        roundBar = new Arc(50, 50, 23, 23, 90, 0);

        roundBar.setType(ArcType.OPEN);
        roundBar.setStroke(gauge.getBarColor());
        roundBar.setStrokeWidth(PREFERRED_WIDTH * 0.15);
        roundBar.setStrokeLineCap(StrokeLineCap.BUTT);
        roundBar.setFill(null);

        topLine = new Line(PREFERRED_WIDTH * 0.5, 1, PREFERRED_WIDTH * 0.5, 0.16667 * PREFERRED_HEIGHT);
        //topLine = new Line(50, 1, 50, 10);
        topLine.setStroke(gauge.getBorderPaint());
        topLine.setFill(Color.TRANSPARENT);

        titleText = new Text(gauge.getTitle());
        titleText.setFont(Fonts.robotoLight(11));
        titleText.setFill(gauge.getTitleColor());
        titleText.setX(50 - (titleText.getBoundsInLocal().getWidth()) / 2);
        titleText.setY(65);
        Helper.enableNode(titleText, !gauge.getTitle().isEmpty());

        valueText = new Text(formatNumber(gauge.getLocale(), gauge.getFormatString(), gauge.getDecimals(), gauge.getCurrentValue()));
        valueText.setFont(Fonts.robotoRegular(22));
        valueText.setFill(gauge.getValueColor());
        valueText.setX(46 - (valueText.getBoundsInLocal().getWidth() / 2));
        valueText.setY(45);

        Helper.enableNode(valueText, gauge.isValueVisible());

        //unitText = new Text(gauge.getUnit());
        //unitText.setFont(Fonts.robotoLight(PREFERRED_WIDTH * 0.08));
        //unitText.setFill(gauge.getUnitColor());
        //Helper.enableNode(unitText, !gauge.getUnit().isEmpty());

        pane = new Pane(colorRing, roundBar, topLine, titleText, valueText);
        //pane.setBackground(new Background(new BackgroundFill(gauge.getBackgroundPaint(), new CornerRadii(1024), Insets.EMPTY)));
        //pane.setBorder(new Border(new BorderStroke(gauge.getBorderPaint(), BorderStrokeStyle.SOLID, new CornerRadii(1024), new BorderWidths(gauge.getBorderWidth()))));

        getChildren().setAll(pane);
    }

    @Override protected void registerListeners() {
        super.registerListeners();
        gauge.currentValueProperty().addListener(currentValueListener);
    }


    // ******************** Methods *******************************************
    @Override protected void handleEvents(final String EVENT_TYPE) {
        super.handleEvents(EVENT_TYPE);
        if ("RECALC".equals(EVENT_TYPE)) {
            minValue  = gauge.getMinValue();
            range     = gauge.getRange();
            angleStep = ANGLE_RANGE / range;
            sections  = gauge.getSections();
       //     redraw();
            setBar(gauge.getCurrentValue());
        } else if ("VISIBILITY".equals(EVENT_TYPE)) {
            Helper.enableNode(titleText, !gauge.getTitle().isEmpty());
            Helper.enableNode(unitText, !gauge.getUnit().isEmpty());
            Helper.enableNode(valueText, gauge.isValueVisible());
        }
    }

    private void setBar( final double VALUE ) {
        double barLength = 0;
        double min = gauge.getMinValue();
        double max = gauge.getMaxValue();
        double clampedValue = Helper.clamp(min, max, VALUE);

        if ( gauge.isStartFromZero() ) {
            if ( ( VALUE > min || min < 0 ) && ( VALUE < max || max > 0 ) ) {
                if ( max < 0 ) {
                    barLength = ( max - clampedValue ) * angleStep;
                } else if ( min > 0 ) {
                    barLength = ( min - clampedValue ) * angleStep;
                } else {
                    barLength = - clampedValue * angleStep;
                }
            }
        } else {
            barLength = ( min - clampedValue ) * angleStep;
        }

        roundBar.setLength(barLength);

        setBarColor(VALUE);
        valueText.setText(formatNumber(gauge.getLocale(), gauge.getFormatString(), gauge.getDecimals(), VALUE));
        //resizeValueText();

    }

    private void setBarColor( final double VALUE ) {
        if (!sectionsVisible && !colorGradientEnabled) {
            roundBar.setStroke(gauge.getBarColor());
            colorRing.setStroke(gauge.getBarColor());
        } else if (colorGradientEnabled && noOfGradientStops > 1) {
            Color dynamicColor = gauge.getGradientLookup().getColorAt((VALUE - minValue) / range);
            roundBar.setStroke(dynamicColor);
            colorRing.setStroke(dynamicColor);
        } else {
            roundBar.setStroke(gauge.getBarColor());
            colorRing.setStroke(gauge.getBarColor());
            for (Section section : sections) {
                if (section.contains(VALUE)) {
                    roundBar.setStroke(section.getColor());
                    colorRing.setStroke(section.getColor());
                    break;
                }
            }
        }
    }

    @Override public void dispose() {
        gauge.currentValueProperty().removeListener(currentValueListener);
        super.dispose();
    }
    

    // ******************** Resizing ******************************************
    private void resizeTitleText() {
        //double maxWidth = 0.56667 * size;


        // The following actually performs worse
        // than the above division. I think
        //int intSize = ((int)size) >> 1;
        //double maxWidth = (double) intSize;
        //double maxWidth = 25;

        double fontSize = 12;
        titleText.setFont(Fonts.robotoLight(fontSize));
        //if (titleText.getLayoutBounds().getWidth() > 40) { Helper.adjustTextSize(titleText, 40, fontSize); }
        Helper.adjustTextSize(titleText, 35, 10);
        titleText.relocate((size - titleText.getLayoutBounds().getWidth()) * 0.5, size * 0.5);
    }
    private void resizeValueText() {
        //double maxWidth = 0.5 * size;
        //int intSize = ((int)size) >> 1;
        //double maxWidth = (double) intSize;
        //double maxWidth = 40;

        //double fontSize = 0.4 * size;

        valueText.setFont(Fonts.robotoRegular(10));
        //if (valueText.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(valueText, 40, fontSize); }
        Helper.adjustTextSize(valueText, 40, 15);
        valueText.relocate((size - valueText.getLayoutBounds().getWidth()) * 0.5, (size - valueText.getLayoutBounds().getHeight()) * 0.25);
    }
    /*
    public void resizeUnitText() {
        double maxWidth = 0.56667 * size;
        double fontSize = 0.08 * size;
        unitText.setFont(Fonts.robotoLight(fontSize));
        if (unitText.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(unitText, maxWidth, fontSize); }
        unitText.relocate((size - unitText.getLayoutBounds().getWidth()) * 0.5, size * 0.66);
    }
    */

    @Override protected void resize() {
        double width  = gauge.getWidth() - gauge.getInsets().getLeft() - gauge.getInsets().getRight();
        double height = gauge.getHeight() - gauge.getInsets().getTop() - gauge.getInsets().getBottom();
        size          = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setMaxSize(size, size);
            pane.relocate((width - size) * 0.5, (height - size) * 0.5);

            //colorRing.setCenterX(size * 0.5);
            colorRing.setCenterX(50);
            //colorRing.setCenterY(size * 0.5);
            colorRing.setCenterY(50);
            //colorRing.setRadius(size * 0.5);
            colorRing.setRadius(50);
            //colorRing.setStrokeWidth(size * 0.0075);
            colorRing.setStrokeWidth(2);
            colorRing.setStrokeType(StrokeType.INSIDE);

            roundBar.setCenterX(50);
            roundBar.setCenterY(50);
            roundBar.setRadiusX(40);
            roundBar.setRadiusY(40);
            roundBar.setStrokeWidth(size * 0.12);


            //roundBar.setCenterX(size * 0.5);
            //roundBar.setCenterY(size * 0.5);
            //roundBar.setRadiusX(size * 0.4135);
            //roundBar.setRadiusY(size * 0.4135);
            //roundBar.setStrokeWidth(size * 0.12);

            topLine.setStartX(50);
            topLine.setStartY(1.375);
            topLine.setEndX(50);
            topLine.setEndY(size * 0.145);

            //Title
            //titleText.setFont(Fonts.robotoLight(18));
            //Helper.adjustTextSize(titleText, 40, 18);
            //titleText.relocate((size - titleText.getLayoutBounds().getWidth()) * 0.45, size * 0.5);
            //titleText.relocate(50, 50);

            //Number
            //valueText.setFont(Fonts.robotoRegular(20));
            //Helper.adjustTextSize(valueText, 35, 20);
            //valueText.relocate(48, (size - valueText.getLayoutBounds().getHeight()) * 0.25);
            //valueText.relocate(25, 20);

        }
    }

    @Override public void redraw() {
        colorGradientEnabled = gauge.isGradientBarEnabled();
        noOfGradientStops    = gauge.getGradientBarStops().size();
        sectionsVisible      = gauge.getSectionsVisible();

    //    pane.setBackground(new Background(new BackgroundFill(gauge.getBackgroundPaint(), new CornerRadii(1024), Insets.EMPTY)));
    //    pane.setBorder(new Border(new BorderStroke(gauge.getBorderPaint(), BorderStrokeStyle.SOLID, new CornerRadii(1024), new BorderWidths(gauge.getBorderWidth() / PREFERRED_WIDTH * size))));

        //setBarColor(gauge.getCurrentValue());
        //valueText.setFill(gauge.getValueColor());
       // unitText.setFill(gauge.getUnitColor());
        //titleText.setFill(gauge.getTitleColor());
        topLine.setStroke(gauge.getBorderPaint());

        //titleText.setText(gauge.getTitle());
        //resizeTitleText();

        //unitText.setText(gauge.getUnit());
        //resizeUnitText();
    }
}
