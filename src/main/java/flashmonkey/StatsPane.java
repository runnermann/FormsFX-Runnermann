package flashmonkey;

import campaign.db.Metrics;
import eu.hansolo.tilesfx.runnermann.Tile;
import eu.hansolo.tilesfx.runnermann.TileBuilder;
import eu.hansolo.tilesfx.runnermann.chart.ChartData;
import fileops.utility.Utility;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.paint.Color;
import uicontrols.FxNotify;
import uicontrols.UIColors;

import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

public class StatsPane {

    private static final int GAUGE_WIDTH = 300;
    private static final int ROW_HEIGHT = 200;
    private static int numRows;


    public StatsPane() {
        //height = paneHt;
    }


    public VBox buildAndGet(){
        String emojiPath = "image/Flash_hmm_75.png";
        if(Utility.isConnected()) {
            numRows = 0;
            final Metrics m = new Metrics();
            boolean bool = m.setMetrics();
            if(bool) {
                VBox vBox = buildGauges(m.getMetrics2dArry());

                int paneHeight = ROW_HEIGHT * numRows;
                vBox.setPrefSize(480, paneHeight);
                return vBox;
            }
            else {
                FxNotify.notificationError("No data yet", "I didn't find any decks that are set up. Go to Create -> Earn" +
                        " and create their descriptions.", Pos.CENTER, 8, emojiPath, FlashMonkeyMain.getPrimaryWindow());
                return null;
            }
        } else {
            FxNotify.notificationError("Please Connect", "I did not detect an internet connection. Please check" +
                    " your connection.", Pos.CENTER, 8, emojiPath, FlashMonkeyMain.getPrimaryWindow());
        }
        return null;
    }

    private VBox buildGauges(String[][] values) {
        //paneHeight = ROW_HEIGHT * values.length;
        VBox vBox = new VBox();
        for(String[] sAry : values) {
            if(sAry != null) {
                vBox.getChildren().add(getRow(sAry));
                numRows++;
            }
        }
        return vBox;
    }

    /**
     * A row is populated by a string array.
     * 1st guage, amount earned
     * title of the area of studies = idx 0
     * what is earned e.g. hours    = idx 1
     * value or amount total earned = idx 2
     * 2nd gauge, hours studied:
     * title same as above          = idx 0
     * what is measured e.g. hours  = idx 3
     * Amount hours                 = idx 4
     * Max avg value of hours       = idx 5
     * @param valueRow
     * @return
     */
    private HBox getRow(String[] valueRow) {
        HBox hBox = new HBox();

        ChartData chartData1 = new ChartData("Create", convertTime(valueRow[4]), Tile.BLUE);
        ChartData chartData2 = new ChartData("Review", convertTime(valueRow[5]), Tile.GREEN);

        ChartData chartData3 = new ChartData("Free Recall", convertTime(valueRow[4]), Tile.YELLOW);
        ChartData chartData4 = new ChartData("Testing", convertTime(valueRow[5]), Tile.PINK);


        // the amount earned
        Tile firstTile = TileBuilder.create().skinType(Tile.SkinType.CHARACTER)
                .prefSize(200, ROW_HEIGHT)
                .title(valueRow[0])
                .titleAlignment(TextAlignment.LEFT)
                .text(valueRow[1])
                .textAlignment(TextAlignment.LEFT)
                .description(toCurrencyFormat(valueRow[2]))
                .descriptionColor(Color.web(UIColors.GUAGE_BLUE))
                .build();
        // Hours of interaction
        Tile secondTile = TileBuilder.create()
                .skinType(Tile.SkinType.DONUT_CHART)
                .prefSize(GAUGE_WIDTH, ROW_HEIGHT)
                .title("")
                .text(valueRow[3])
                .unit("Hrs")
                .chartData(chartData1, chartData2)
                .build();
//          @TODO finish points
//        Tile thirdTile = TileBuilder.create()
//                .skinType(Tile.SkinType.DONUT_CHART)
//                .prefSize(GAUGE_WIDTH, ROW_HEIGHT)
//                .title("")
//                .text("POINTS")
//                .unit("Pts")
//                .chartData(chartData1, chartData2)
//                .build();

        // displays the hours studied
//        Tile secondTile = TileBuilder.create()
//                .skinType(Tile.SkinType.CIRCLE_PROGRESS_NUM)
//                .prefSize(GAUGE_WIDTH, ROW_HEIGHT)
//                .title(valueRow[0])
//                .text(valueRow[3])
//                .unit("Hrs")
//                .maxValue(Double.parseDouble(valueRow[5]))
//                .build();

        secondTile.setValue(Double.parseDouble(valueRow[4]));

        hBox.getChildren().addAll(firstTile, secondTile);
        return hBox;
    }

    private long convertTime(String millis) {
        return TimeUnit.MILLISECONDS.toHours(Long.parseLong(millis));
    }

    private String toCurrencyFormat(String amount) {
        NumberFormat numFormat = NumberFormat.getCurrencyInstance();
        double d = Double.parseDouble(amount);
        d *= .01;
        return  numFormat.format(d);

    }


}
