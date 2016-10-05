package securitycamera.modules.webserver.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import com.sun.net.httpserver.BasicAuthenticator;

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

		try {

			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes());
			byte[] mdbytes = md.digest();

			// convert the byte to hex format
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16)
						.substring(1));
			}

			if (username.equals("admin") && sb.toString().equals(
					Settings.getSetting(Settings.USER_PASS, String.class))) {

				if (!loggedIn) {
					loggedIn = true;
					LOGGER.info("Credentials accepted for admin");
				}
				return true;

			} else {

				LOGGER.info("Credentials rejected for " + username + " - "
						+ password);
				return false;
			}

		} catch (NoSuchAlgorithmException e) {
			LOGGER.severe(e.getMessage());
			return false;
		}

	}
}
