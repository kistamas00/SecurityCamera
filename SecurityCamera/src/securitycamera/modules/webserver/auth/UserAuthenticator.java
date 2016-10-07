package securitycamera.modules.webserver.auth;

import java.util.logging.Logger;

import com.sun.net.httpserver.BasicAuthenticator;

import securitycamera.services.MD5;
import securitycamera.services.Settings;

public class UserAuthenticator extends BasicAuthenticator {

	private static final Logger LOGGER = Logger
			.getLogger(UserAuthenticator.class.getCanonicalName());

	private boolean loggedIn;

	public UserAuthenticator() {

		super(UserAuthenticator.class.getCanonicalName());
		loggedIn = false;
	}

	@Override
	public boolean checkCredentials(String username, String password) {

		String MD5Password = MD5.stringToMD5(password);

		if (username.equals("admin") && MD5Password.equals(
				Settings.getSetting(Settings.USER_PASS, String.class))) {

			if (!loggedIn) {
				loggedIn = true;
				LOGGER.info("Credentials accepted for admin");
			}
			return true;

		} else {

			LOGGER.info(
					"Credentials rejected for " + username + " - " + password);
			return false;
		}
	}
}
