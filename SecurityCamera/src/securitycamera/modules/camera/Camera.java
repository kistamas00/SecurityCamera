package securitycamera.modules.camera;

import java.io.File;
import java.util.logging.Logger;

import org.opencv.core.Core;

import securitycamera.SecurityCamera;
import securitycamera.modules.SecurityCameraModule;
import securitycamera.services.Settings;

public class Camera extends SecurityCameraModule {

	private final static Logger LOGGER = Logger
			.getLogger(Camera.class.getCanonicalName());

	private CameraThread cameraThread;

	public Camera() {

		switch (SecurityCamera.OS_TYPE) {
		case LINUX:
			System.load(System.getProperty("user.dir") + File.separator + "lib"
					+ Core.NATIVE_LIBRARY_NAME + ".so");
			break;
		case WINDOWS:
			System.load(System.getProperty("user.dir") + File.separator
					+ Core.NATIVE_LIBRARY_NAME + ".dll");
			break;
		}
	}

	@Override
	public void start() {

		if (cameraThread == null) {

			super.start();
			cameraThread = new CameraThread();

			LOGGER.info("Start " + this.getClass().getSimpleName());
			cameraThread.start();
		}
	}

	@Override
	public void stop() {

		if (cameraThread != null) {

			super.stop();

			LOGGER.info("Stop " + this.getClass().getSimpleName());
			cameraThread.halt();
			cameraThread = null;
		}
	}

	@Override
	public String getIdString() {
		return Settings.CAMERA;
	}

	public static int getNumberOfPictures() {
		return new File(
				Settings.getSetting(Settings.PICTURES_PATH, String.class))
						.listFiles().length;
	}

	public static int getPhotoLimitPerc() {

		int numberOfPictures = getNumberOfPictures();
		int pictureLimit = Settings.getSetting(Settings.PHOTO_LIMIT,
				Integer.class);

		if (numberOfPictures == 0 && pictureLimit == 0) {
			return 100;
		} else {
			return (int) Math
					.round(((double) numberOfPictures / pictureLimit) * 100);
		}
	}

	public void capturePhoto() {

		if (cameraThread != null) {

			cameraThread.saveNextFrame();
		}
	}

	public byte[] getLastFrameCopy() {
		return cameraThread == null
				|| !Settings.getSetting(Settings.STREAM, Boolean.class) ? null
						: cameraThread.getLastFrameCopy();
	}

	public boolean isRunning() {
		return cameraThread != null;
	}

	public boolean isStreaming() {
		return cameraThread == null ? false
				: Settings.getSetting(Settings.STREAM, Boolean.class);
	}

	public boolean isMotionDetectionEnabled() {
		return cameraThread == null ? false
				: Settings.getSetting(Settings.MOTION_DETECTION, Boolean.class);
	}

	public static void setPhotoLimit(int photoLimit) {

		if (photoLimit >= getNumberOfPictures()) {
			Settings.setSetting(Settings.PHOTO_LIMIT, photoLimit);
		}
	}

	public void setStreaming(boolean isStreaming) {

		if (cameraThread != null) {
			cameraThread.setStreaming(isStreaming);
		}
	}

	public void setMotionDetection(boolean motionDetection) {

		if (cameraThread != null) {
			cameraThread.setMotionDetection(motionDetection);
		}
	}
}
