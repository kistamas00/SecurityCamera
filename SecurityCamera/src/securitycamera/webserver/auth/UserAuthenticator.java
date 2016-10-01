package securitycamera.webserver.auth;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.sun.net.httpserver.BasicAuthenticator;

public class UserAuthenticator extends BasicAuthenticator {

	private static Logger LOGGER = Logger
			.getLogger(UserAuthenticator.class.getCanonicalName());

	private Map<String, String> passwords;
	private Set<String> loggedIn;

	public UserAuthenticator() {

		super(UserAuthenticator.class.getCanonicalName());
		this.passwords = new HashMap<String, String>();
		this.loggedIn = new HashSet<String>();

		passwords.put("admin", "almafa");
	}

	@Override
	public boolean checkCredentials(String username, String password) {

		if (password.equals(passwords.get(username))) {

			if (!loggedIn.contains(username)) {
				LOGGER.info("Credentials accepted for " + username);
				loggedIn.add(username);
			}
			return true;

		} else {

			LOGGER.info(
					"Credentials rejected for " + username + " - " + password);
			return false;
		}
	}
}
