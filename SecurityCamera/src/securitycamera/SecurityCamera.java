package securitycamera;

import java.util.logging.Logger;

import securitycamera.modules.SecurityCameraModule;
import securitycamera.modules.SecurityCameraModuleContainer;
import securitycamera.modules.camera.Camera;
import securitycamera.modules.camera.enums.OStype;
import securitycamera.modules.sig.SystemInformationGatherer;
import securitycamera.modules.webserver.Webserver;

public class SecurityCamera {

	private final static Logger LOGGER = Logger
			.getLogger(SecurityCamera.class.getCanonicalName());
	public final static OStype OS_TYPE = OStype
			.valueOf(System.getProperty("os.name").split(" ")[0].toUpperCase());
	private final static SecurityCameraModuleContainer MODULES = new SecurityCameraModuleContainer();

	public static void main(String args[]) {

		MODULES.addModule(new Camera());
		MODULES.addModule(new Webserver());
		MODULES.addModule(new SystemInformationGatherer());

		LOGGER.info("Start modules on " + OS_TYPE);

		MODULES.startAll();
	}

	public static <T extends SecurityCameraModule> T getModule(Class<T> c) {
		return MODULES.getModule(c);
	}
}
