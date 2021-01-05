package security_and_login;

import forms.SignInModel;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit5.ApplicationTest;
import s3.MediaTransferTester;

public class TokenTesting extends ApplicationTest {

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(TokenTesting.class);

    public void testExpiredTokenRedirect() {

    }

    public void testGetToken() {
        SignInModel sim = new SignInModel();

    }

    public void testRefreshTokenRequest() {

    }

}
