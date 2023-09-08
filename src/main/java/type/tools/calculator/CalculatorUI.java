package type.tools.calculator;

import fileops.BaseInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Called by the MathCard when The user is creating a MathProblem.
 * Helps to properly format a math problem so it may be solved by the DijkstraParser.
 * Also used to inform the user of the Math Operators that are available.
 *
 * There are no fields/variables that are saved to the MathCard from this class.
 */
public class CalculatorUI implements BaseInterface {

    private static CalculatorUI CLASS_INSTANCE;

    // THE LOGGER
    // Change to LoggerFactory or remove BEFORE deployment
    private static final Logger LOGGER = LoggerFactory.getLogger(CalculatorUI.class);
    //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(CalculatorUI.class);


    /**
     * private No args constructor
     */
    private CalculatorUI() { /* empty */ }

    /**
     * Singleton class instantiation. There
     * should only be one instance of DrawTools
     * Synchronized
     *
     * @return The class instance
     */
    public static synchronized CalculatorUI getInstance() {
        if (CLASS_INSTANCE == null) {
            CLASS_INSTANCE = new CalculatorUI();
        }
        return CLASS_INSTANCE;
    }


    public static boolean instanceExists() {
        return CLASS_INSTANCE != null;
    }


    /**
     * Deck save action for this class that is called when FlashMonkey main stage
     * is closed. May be used outside of the class. Single point call
     * that is always called when class stage is closed. Should be implemented
     * by any class that contains files to be saved to file or the cloud.
     * E.g. ReadFlash and CreateFlash.
     *
     * @return If there is a scene change depending on an answer from the user,
     * true to continue, false to stop.
     */
    @Override
    public boolean saveOnExit() {
        // Safe to continue. Do not trigger a save popup.
        return true;
    }

    /**
     * To be called by the class on stage.onHidden()
     * Closes this class. Clears the tree. Closes the treeWindow. Does not save
     * the current work.
     */
    @Override
    public void onClose() {

    }
}
