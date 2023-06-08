/*
 * This is the gauge assymbly class. It creates a gauge the size and width
 * provided in the constructor parameters. The gauge is returned in a GridPane.
 * To create a guage, Define the size of the pane in the constructor, then use the
 * The makeGauge(String name) method will return the gridPane with the gauge.
 */
package flashmonkey;


import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.tilesfx.runnermann.Tile;
import eu.hansolo.tilesfx.runnermann.TileBuilder;
import eu.hansolo.tilesfx.runnermann.colors.Bright;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import uicontrols.UIColors;

import java.time.Duration;
import java.time.LocalTime;


/**
 * Provides the EncryptedUser.EncryptedUser interface for the Gauges.
 *
 * @author Lowell Stadelman
 */
public class MGauges {
    private final GridPane pane;
    private final Gauge gauge;
    private final GaugeBuilder builder;
    private VBox gaugeVBox;
    // the value that the gage represents
    //private double value;
    private final double width;
    private final double height;
    private AnimationTimer animationTimer;
    private Tile countdownTile;
    private Tile durationTile;
    private long timerTime;
    private long durationStartTime;


    /**
     * Constructor: Creates a gauge that is the width and height and name
     * of the gauge.
     *
     * @param w
     * @param h
     */
    public MGauges(double w, double h) {
        this.pane = new GridPane();
        this.builder = GaugeBuilder.create().skinType(Gauge.SkinType.FLAT);
        this.gauge = builder.decimals(1).maxValue(20).unit("%").build();
        this.width = w;
        this.height = h;
    }

    /**
     * Creates a gauge sized to the parameters in the constructor
     *
     * @param name     The name of the gauge.
     * @param initialV the initial value.
     * @param high     The highest number of elements measured by the gauge.
     * @param low      The lowest number of elements measured by the gauge. Gauge
     *                 can go negitive.
     * @return Returns a gauge with the prefered height and
     * width provided in the parameters.
     */
    public GridPane makeGauge(String name, double initialV, double high, double low) {
        pane.setPadding(new Insets(5));
        pane.setPrefSize(width, height);
        pane.setAlignment(Pos.CENTER);
        pane.setId("liteCurvedContainer");
        gauge.setValue(initialV);
        gauge.setMaxValue(high);
        gauge.setMinValue(low);

        //gauge.setAnimated(true);
        gaugeVBox = getTopicBox(name, Color.web(UIColors.GUAGE_BLUE), gauge);
        pane.add(gaugeVBox, 1, 4);
        return pane;
    }

    public GridPane makeTimer(String name, int time) {
        pane.setPrefSize(width, height);
        pane.setAlignment(Pos.CENTER);
        pane.setId("liteCurvedContainer");
        pane.setPadding(new Insets(0));
        gaugeVBox = getClockTimer(time, (int) width);
        pane.add(gaugeVBox, 2, 4);
        return pane;
    }

    public GridPane makeDurTimer(long millis) {
        pane.setPrefSize(width, height);
        pane.setAlignment(Pos.CENTER);
        pane.setId("liteCurvedContainer");
        pane.setPadding(new Insets(0));
        gaugeVBox = getDurationTimer((int) width, (int) height);
        pane.add(gaugeVBox, 2, 4);
        return pane;
    }

    public void setMaxVal(long duration, int num) {
        gauge.setMaxValue(num);
        gauge.setAnimationDuration(duration);
    }

    /**
     * When the value of the gauge is changed, use moveGuage to set
     * the new value of the gauge. Set the duration of the change in
     * parameter d. The duration should change depending on the distance
     * depending on the desired effect. The newValue is the value the
     * gauge will change to.
     *
     * @param d        The duration of the change in milliseconds
     * @param newValue The new value of the gauge.
     */
    public void moveNeedle(long d, double newValue) {
        gauge.setValue(newValue);
        gauge.setAnimationDuration(d);
    }

    /**
     * Sets a gauge with the name, and color provided in the
     * parameters
     *
     * @param TEXT
     * @param COLOR
     * @param GAUGE
     * @return Returns a VBox with the gauge inside it. The size of the
     * VBox is provided in the class constructor
     */
    private VBox getTopicBox(final String TEXT, final Color COLOR, final Gauge GAUGE) {
        GAUGE.setBarColor(COLOR);
        GAUGE.setBarBackgroundColor(Color.web("#E3E3E3"));
        GAUGE.setAnimated(true);
        GAUGE.setMajorTickMarksVisible(false);
        GAUGE.setPrefSize(width, height);
        GAUGE.setTitle(TEXT);
        GAUGE.setUnit("");
        GAUGE.setTitleColor(Color.web("383838"));
        GAUGE.setValueColor(Color.web("383838"));
        GAUGE.setBarBackgroundColor(Color.web("E3E3E3"));
        VBox vBox = new VBox(GAUGE);
        vBox.setMinSize(width, height);
        vBox.setStyle("-fx-background-color: TRANSPARENT"); //#2A2A2A
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    private Duration getDurationTime() {
        long millis = System.currentTimeMillis() - this.durationStartTime;
        return Duration.ofMillis(millis);
    }

    public void setStartTime() {
        this.durationStartTime = System.currentTimeMillis();
    }

    private VBox getDurationTimer(int wd, int ht) {

        Duration dur = getDurationTime();
        int seconds = dur.toSecondsPart() < 10 ? 10 : dur.toSecondsPart();

        durationTile = TileBuilder.create()
                .skinType(Tile.SkinType.TIME)
                .prefSize(wd, ht - 12)
                .duration(LocalTime.of(dur.toHoursPart(), dur.toMinutesPart(), seconds))
                .backgroundColor(Color.web("#FFFFFF00"))
                .valueColor(Color.web("383838"))
                .titleColor(Color.web("383838"))
                .secondsVisible(true)
                .running(true)
                .build();

        VBox vBox = new VBox(durationTile);
        vBox.setMinSize(wd, ht);
        vBox.setMaxSize(wd, ht);
        vBox.setStyle("-fx-background-color: TRANSPARENT"); //#2A2A2A
        vBox.setAlignment(Pos.CENTER);
        return vBox;

    }


    void setConsumedTime() {
        System.out.println("setConsumedTime");
        Duration dur = getDurationTime();
        int seconds = dur.toSecondsPart() < 10 ? 10 : dur.toSecondsPart();
        durationTile.setDuration(LocalTime.of(dur.toHoursPart(), dur.toMinutesPart(), seconds));
    }

    final long[] lastTimerCall = {System.currentTimeMillis()};
    private static final int WD = 120;
    /**
     * @param time The duration of time until the clock resets.
     * @return
     */
    private VBox getClockTimer(int time, int wd) {
        this.timerTime = time;

        countdownTile = TileBuilder.create()
                .skinType(Tile.SkinType.COUNTDOWN_TIMER)
                .prefSize(wd, wd)
                .title("")
                .textVisible(true)
                .description("PREVIOUS")
                .valueColor(Color.web("383838"))
                .backgroundColor(Color.web("#F5F5F5"))
                .barColor(Color.web(UIColors.HIGHLIGHT_GREEN))
                .barBackgroundColor(Color.web("#E3E3E3"))
                .gradientStops(
                        new Stop(0.0, Bright.RED),
                        new Stop(0.25, Bright.RED),
                        new Stop(0.26, Bright.ORANGE),
                        new Stop(0.5, Bright.ORANGE),
                        new Stop(0.51, Bright.YELLOW),
                        new Stop(0.75, Bright.YELLOW),
                        new Stop(0.76, Bright.GREEN),
                        new Stop(1.0, Bright.GREEN))
                .onAlarm(e -> System.out.println("Alarm"))
                .strokeWithGradient(true)
                .timePeriod(Duration.ofSeconds(time))
                .fixedYScale(true)
                .onAlarm(e -> {
                    System.out.println("Alarm");
                })
                .build();

        start();

        VBox vBox = new VBox(countdownTile);
        vBox.setMinSize(width, height);
        vBox.setStyle("-fx-background-color: TRANSPARENT"); //#2A2A2A
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    public void stop() {
        //countdownTile.stop();
        if(countdownTile != null) {
            countdownTile.setRunning(false);
        }
        animationTimer.stop();
    }

    public void start() {
        if (animationTimer != null) {
            countdownTile.setRunning(false);
            lastTimerCall[0] = 0l;
            animationTimer.stop();
        }

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(timerTime != -99) {
                    if (now > lastTimerCall[0] + timerTime * 1000) {
                        if (!countdownTile.isRunning()) {
                            countdownTile.setTimePeriod(Duration.ofSeconds(timerTime));
                            countdownTile.setRunning(true);
                        }
                        lastTimerCall[0] = now;
                    }
                } else {
                    animationTimer.stop();
                }
            }
        };

        animationTimer.start();
    }

    public void setTimerTime(int seconds) {
        this.timerTime = seconds;
    }

    private void resetTimer(Tile countdownTile) {
        countdownTile.setTimePeriod(Duration.ofSeconds(30));
        countdownTile.setRunning(true);
    }
}


