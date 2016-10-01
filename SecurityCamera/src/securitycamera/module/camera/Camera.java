package securitycamera.module.camera;

import java.io.File;
import java.util.logging.Logger;

import org.opencv.core.Core;

import securitycamera.SecurityCamera;
import securitycamera.module.SecurityCameraModule;

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

	public void capturePhoto() {

		if (cameraThread != null) {

			cameraThread.saveNextFrame();
		}
	}

	public byte[] getLastFrameCopy() {
		return cameraThread == null || !cameraThread.isStreaming() ? null
				: cameraThread.getLastFrameCopy();
	}

	public int getPhotoLimit() {
		return cameraThread.getPhotoLimit();
	}

	public int getPhotoLimitPerc() {
		return cameraThread.getPhotoLimitPerc();
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

	public void setStreaming(boolean isStreaming) {
		cameraThread.setStreaming(isStreaming);
	}

	public void setMotionDetection(boolean motionDetection) {
		cameraThread.setMotionDetection(motionDetection);
	}

	public void setPhotoLimit(int photoLimit) {
		cameraThread.setPhotoLimit(photoLimit);
	}
}
