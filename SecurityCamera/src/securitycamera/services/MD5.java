package securitycamera.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class MD5 {

	private final static Logger LOGGER = Logger
			.getLogger(MD5.class.getCanonicalName());

	public static String stringToMD5(String s) {

		try {

			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(s.getBytes());
			byte[] mdbytes = md.digest();

			// convert the byte to hex format
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16)
						.substring(1));
			}

			return sb.toString();

		} catch (NoSuchAlgorithmException e) {
			LOGGER.severe(e.getMessage());
		}

		return null;
	}
}
