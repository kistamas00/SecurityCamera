package securitycamera;

import java.io.IOException;
import java.util.logging.Logger;

import securitycamera.camera.Camera;
import securitycamera.camera.enums.OStype;
import securitycamera.email.Email;
import securitycamera.webserver.Webserver;

public class SecurityCamera {

	private final static Logger LOGGER = Logger.getLogger(SecurityCamera.class.getName());
	public final static OStype OS_TYPE = OStype.valueOf(System.getProperty("os.name").split(" ")[0].toUpperCase());

	private static Camera camera;
	private static Webserver webserver;

	public static void main(String args[]) {

		try {

			camera = new Camera();
			webserver = new Webserver();

		} catch (IOException e) {

			e.printStackTrace();
		}

		LOGGER.info("Start modules on " + OS_TYPE.toString());
		camera.start();
		webserver.start();
	}

	public static void stopWebserver() {
		webserver.stop();
	}

	public static void startCamera() {
		camera.start();
	}

	public static void stopCamera() {
		camera.stop();
	}

	public static void capturePhoto() {
		camera.capturePhoto();
	}

	public static byte[] getLastFrameCopy() {
		return camera.getLastFrameCopy();
	}

	public static boolean cameraIsRunning() {
		return camera.isRunning();
	}

	public static boolean cameraIsStreaming() {
		return camera.isStreaming();
	}

	public static boolean cameraMotionDetectionEnabled() {
		return camera.motionDetectionEnabled();
	}

	public static String getEmailAdress() {
		return Email.getEmailAddress();
	}

	public static void setStreaming(boolean isStreaming) {
		camera.setStreaming(isStreaming);
	}

	public static void setMotionDetection(boolean motionDetection) {
		camera.setMotionDetection(motionDetection);
	}

	public static void setEmailAdress(String emailAddress) {
		Email.setEmailAddress(emailAddress);
	}
}
