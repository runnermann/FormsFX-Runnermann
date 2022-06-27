package security_and_login;

import org.testfx.api.FxRobot;

import java.awt.geom.Point2D;

/**
 * Robot utility class for the login page
 * Logs in
 */
public class LoginTesterUtility {
	
	// The min corner for the app.
	private static int delta_X;
	private static int delta_Y;
	
	private static int nameX = 200;
	private static int nameY = 450;
	
	private static int pwX = 200;
	private static int pwY = 500;
	
	private static int submitBtnX = 200;
	private static int submitBntY = 570;
	
	public LoginTesterUtility(int delta_X, int delta_Y) {
		this.delta_X = delta_X;
		this.delta_Y = delta_Y;
	}
	
	public static void logIn(String name, String password, FxRobot robot) {

		robot.clickOn(delta_X + nameX, delta_Y + nameY);
		//robot.clickOn(delta_X + nameX, delta_Y + nameY);
		robot.write(name);
		robot.sleep(20);
		
		robot.clickOn(delta_X + pwX, delta_Y + pwY);
		robot.write(password);
		robot.sleep(20);
		
		robot.clickOn(delta_X + submitBtnX, delta_Y + submitBntY);
		
	}
}
