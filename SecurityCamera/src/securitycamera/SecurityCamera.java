package securitycamera;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import securitycamera.enums.OStype;
import securitycamera.modules.SecurityCameraModule;
import securitycamera.modules.SecurityCameraModuleContainer;
import securitycamera.modules.camera.Camera;
import securitycamera.modules.sig.SystemInformationGatherer;
import securitycamera.modules.webserver.Webserver;
import securitycamera.services.Settings;

public class SecurityCamera {

	public final static Logger LOGGER = Logger.getLogger("logger");
	public final static OStype OS_TYPE = OStype
			.valueOf(System.getProperty("os.name").split(" ")[0].toUpperCase());
	private final static SecurityCameraModuleContainer MODULES = new SecurityCameraModuleContainer();

	public static void main(String args[]) {

		Path picturePath = null;
		Path loggerPath = null;

		for (int i = 0; i < args.length; i++) {

			switch (args[i]) {

			case "--picture-path":
			case "-p":

				picturePath = Paths.get(args[++i]);
				break;

			case "--logger-path":
			case "-l":

				loggerPath = Paths.get(args[++i]);
				break;

			default:

				LOGGER.severe("Parameter error!: " + args.toString());
				break;
			}
		}

		if (loggerPath != null) {

			try {

				if (Files.exists(loggerPath)) {

					FileHandler fileHandler = new FileHandler(
							loggerPath.toRealPath().toString());
					fileHandler.setFormatter(new SimpleFormatter());

					LOGGER.addHandler(fileHandler);

					LOGGER.info("Log save in: "
							+ loggerPath.toRealPath().toString());

				} else {

					LOGGER.warning("Path does not exists");
				}

			} catch (IOException e) {
				LOGGER.severe(e.getMessage());
			}
		}

		Settings.load();

		if (picturePath != null) {

			try {

				if (Files.exists(picturePath)) {

					String newPath = picturePath.toRealPath().toString();
					Settings.setSetting(Settings.PICTURES_PATH, newPath);

					LOGGER.info("Pictures folder changed to: " + newPath);

				} else {

					LOGGER.warning("Path does not exists");
				}

			} catch (IOException e) {
				LOGGER.severe(e.getMessage());
			}
		}

		MODULES.addModule(new SystemInformationGatherer());
		MODULES.addModule(new Webserver());
		MODULES.addModule(new Camera());

		LOGGER.info("Start modules on " + OS_TYPE);

		MODULES.startAll();

		LOGGER.info("Modules started!");
	}

	public static <T extends SecurityCameraModule> T getModule(Class<T> c) {
		return MODULES.getModule(c);
	}
}
