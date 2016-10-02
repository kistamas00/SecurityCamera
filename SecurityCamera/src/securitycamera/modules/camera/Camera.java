package securitycamera.modules.camera;

import java.io.File;
import java.util.logging.Logger;

import org.opencv.core.Core;

import securitycamera.SecurityCamera;
import securitycamera.modules.SecurityCameraModule;

public class Camera extends SecurityCameraModule {

	private final static Logger LOGGER = Logger
			.getLogger(Camera.class.getCanonicalName());

	private static int pictureLimit = 20;

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

			cameraThread = new CameraThread();

			LOGGER.info("Start " + this.getClass().getSimpleName());
			cameraThread.start();
		}
	}

	@Override
	public void stop() {

		if (cameraThread != null) {

			LOGGER.info("Stop " + this.getClass().getSimpleName());
			cameraThread.halt();
			cameraThread = null;
		}
	}

	public static int getNumberOfPictures() {
		return new File("public" + File.separator + "pictures")
				.listFiles().length;
	}

	public static int getPhotoLimit() {
		return pictureLimit;
	}

	public static int getPhotoLimitPerc() {

		int numberOfPictures = getNumberOfPictures();

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
		return cameraThread == null || !cameraThread.isStreaming() ? null
				: cameraThread.getLastFrameCopy();
	}

	public boolean isRunning() {
		return cameraThread != null;
	}

	public boolean isStreaming() {
		return cameraThread == null ? false : cameraThread.isStreaming();
	}

	public boolean isMotionDetectionEnabled() {
		return cameraThread == null ? false
				: cameraThread.isMotionDetectionEnabled();
	}

	public static void setPhotoLimit(int photoLimit) {

		if (photoLimit >= getNumberOfPictures()) {
			pictureLimit = photoLimit;
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
