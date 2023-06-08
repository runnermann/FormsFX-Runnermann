package campaign.db;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Singleton Class
 * Hours, amount earned, capturing, calculating, and fetching
 */
public class Metrics {

    // 2d array contains an array of arrays with decks usage information
    private String[][] metrics2dArry;
    private final String valueLblString = "VALUE EARNED FROM PEERS";
    private final String hours = "HOURS OF STUDY";
    private final HashMap<String, ArrayList<Object>> buildMap = new HashMap<>();

    public Metrics() {
        /** No args constructor **/
    }

    public String[][] getMetrics2dArry() {
        return metrics2dArry;
    }

    /**
     * Fetch stats from the DB, Parse them into a map to combine stats from like
     * subjects, then output to 2d array
     * @return true if there are results, false otherwise
     */
    public boolean setMetrics() {
        ArrayList<HashMap<String, Object>> result
                = DBFetchToMapAryOBJ.USER_STATS_MULTIPLE.query("not used");

        if(result.get(0).get("empty").equals("true")) {
            return false;
        }
        else {
            metrics2dArry = new String[result.size()][];
            // parse and merge results from like subjects
            for (int i = 0; i < result.size(); i++) {
                parseMap(result.get(i));
            }
            // output to the metrics array
            int i = 0;
            for (ArrayList<Object> s : buildMap.values()) {
                metrics2dArry[i++] = toAry(s);
            }

            return true;
        }
    }

    /**
     * Converts to a String array
     * @param objAry
     * @return
     */
    private String[] toAry(ArrayList<Object> objAry) {
        String[] ret = new String[objAry.size()];
        for(int i = 0; i < objAry.size(); i++) {
            ret[i] = objAry.get(i).toString();
        }

        return ret;
    }


    /**
     * 0 = Area of studies
     * 1 = Pane 1 Lower metric measured
     * 2 = Pane 1 Amount in pay
     * 3 = Pane 2 lower metric measured
     * 4 = Pane 2 amount in hours
     * 5 = Max avg for that area of study used to calc percentage
     * String[] fakeRowOne = {"Software Engineering", value, "469", hours, "1217", "1400"};
     * String[] fakeRowTwo = {"Chemistry", value, "270", hours, "86", "120"};
     * String[] fakeRowThree = {"Physics", value, "390", hours, "172", "120"};
     * String[] fakeRowFour = {"Calculus", value, "640", hours, "400", "500"};
     *
     * Decks that are from the same subject, EG Mathmatics, are accumulated into
     * a single row.
     * @param map
     */
    private void parseMap(HashMap<String, Object> map) {
        // an array is a node containing the elements
        // shown in a row with amount, and hours gauge.
        final ArrayList<Object> cols = getInitAry(6);
        // pre set the labels in the array
        cols.set(1, valueLblString);
        cols.set(3, hours);

        String subj = (String) map.get("t2.subj");
        // check if the subject has been inserted before
        if(buildMap.containsKey(subj)) {
            // if true add the new deck's results to the
            // subject field.
            final ArrayList<Object> existing = buildMap.get(subj);
            existing.set(2, (int) existing.get(2) + (int) map.get("amount"));;
            existing.set(4, (long) existing.get(4) + (long) map.get("create_time"));
            existing.set(5, (long) existing.get(5) + (long) map.get("review_test_time"));

            buildMap.put(subj, existing);
        }
        else {
            // This is the first time this subject is seen,
            // just add the values as an array.
            cols.set(0, subj);
            cols.set(2, map.get("amount"));
            cols.set(4, map.get("create_time"));
            cols.set(5, map.get("review_test_time"));

            buildMap.put(subj, cols);
        }
    }

    private ArrayList<Object> getInitAry(int length) {
        ArrayList<Object> arry = new ArrayList<>(length);
        for(int i = 0; i < length; i++) {
            arry.add((Object) " ");
        }
        return arry;
    }
}
