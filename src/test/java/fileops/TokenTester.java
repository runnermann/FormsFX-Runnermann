package fileops;

import flashmonkey.FlashMonkeyMain;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;


/**
 * @TODO Token testing!!!
 * <pre>
 *     Prereqs: Vertx must be running in order to check token generation. Set Token "exp" to 1 minute
 *  1) TOKEN
 *      a) Token is correctly created with user name and password
 *      b) Token is not created when user name is incorrect
 *      c) Token is not created when pw is incorrect
 *  2) DURATION
 *      a) Token does not exire prior to exp
 *      b) Token does not work after it is expired
 *  2) Token Refresh
 *      a) Token is correctly refreshed when provided within the expiration time
 *          and when provided the correct refresh code
 *      b) Token is not refreshed when provided outside the expiration time.
 *      c) Token is not refreshed when provided a fake refresh code
 *  3) TOKEN FAIL
 *      a) Token does not work after it is expired. (see duration)
 *      b) Token is not refreshed when provided the correct refresh code and a fake username
 *      c) Refresh token is not returned when a correct username is provided but not a token
 *
 * </pre>
 *
 */
public class TokenTester extends ApplicationTest {


    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(TokenTester.class);

    CloudOps clops = new CloudOps();

    @BeforeAll
    public void setup() throws Exception {

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(FlashMonkeyMain.class);

    }

    @AfterAll
    public void clearnup() throws Exception {

    }



}
