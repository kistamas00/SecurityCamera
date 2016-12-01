package securitycamera.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.Gson;

import securitycamera.SecurityCamera;

public class Settings {

	private static final String SETTINGS_FILE = System.getProperty("user.dir")
			+ File.separator + "conf.ini";
	private final static Logger LOGGER = SecurityCamera.LOGGER;

	private static Map<String, Object> settings = new HashMap<String, Object>();

	public static final String USER_PASS = "userPass";
	public static final String PICTURES_PATH = "picturesPath";
	public static final String WEBSERVER = "webserver";
	public static final String CAMERA = "camera";
	public static final String STREAM = "stream";
	public static final String MOTION_DETECTION = "motionDetection";
	public static final String PHOTO_LIMIT = "photoLimit";
	public static final String EMAIL = "email";

	private static void init() {

		settings.put(USER_PASS,
				"8C6976E5B5410415BDE908BD4DEE15DFB167A9C873FC4BB8A81F6F2AB448A918");
		settings.put(PICTURES_PATH, "public" + File.separator + "pictures");
		settings.put(WEBSERVER, true);
		settings.put(CAMERA, false);
		settings.put(STREAM, false);
		settings.put(MOTION_DETECTION, false);
		settings.put(PHOTO_LIMIT, 20);
		settings.put(EMAIL, "scameraemail@gmail.com");
	}

	@SuppressWarnings("unchecked")
	public static void load() {

		init();

		try {

			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(
					new FileReader(SETTINGS_FILE));

			settings.putAll(gson.fromJson(br.readLine(), settings.getClass()));

			br.close();

			LOGGER.info("Settings loaded");

		} catch (FileNotFoundException e) {

			try {

				PrintWriter pw = new PrintWriter(new FileWriter(SETTINGS_FILE));
				pw.println("{}");
				pw.close();

				LOGGER.info("conf.ini created");

			} catch (IOException e1) {
				LOGGER.severe(e1.getMessage());
			}

		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getSetting(String key, Class<T> type) {

		Object value = settings.get(key);

		if (value instanceof Integer && type.equals(Double.class)) {
			value = ((Integer) value).doubleValue();
		} else if (value instanceof Double && type.equals(Integer.class)) {
			value = ((Double) value).intValue();
		}

		return (T) value;
	}

	public synchronized static void setSetting(final String key, Object value) {

		Object prevValue = settings.put(key, value);

		if (!prevValue.equals(value)) {

			try {

				Gson gson = new Gson();
				PrintWriter pw = new PrintWriter(
						new FileWriter(SETTINGS_FILE, false));

				pw.println(gson.toJson(settings));

				pw.close();

			} catch (IOException e) {
				LOGGER.severe(e.getMessage());
			}
		}
	}
}
