package securitycamera.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import securitycamera.SecurityCamera;

public class SHA256 {

	private final static Logger LOGGER = SecurityCamera.LOGGER;

	public static String stringToSHA256(String s) {

		try {

			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			byte[] hash = digest.digest(s.getBytes(StandardCharsets.UTF_8));
			String result = DatatypeConverter.printHexBinary(hash);

			return result;

		} catch (

		NoSuchAlgorithmException e) {
			LOGGER.severe(e.getMessage());
		}

		return null;
	}
}
