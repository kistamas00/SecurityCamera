package securitycamera.camera;

import java.io.File;
import java.util.logging.Logger;

import org.opencv.core.Core;

import securitycamera.SecurityCamera;

public class Camera {

	private final static Logger LOGGER = Logger
			.getLogger(Camera.class.getName());

	private CameraThread cameraThread;

	public Camera() {

		switch (SecurityCamera.OS_TYPE) {
		case LINUX:
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			break;
		case WINDOWS:
			System.load(System.getProperty("user.dir") + File.separator
					+ Core.NATIVE_LIBRARY_NAME + ".dll");
			break;
		}
	}

	public void start() {

		if (cameraThread == null) {

			cameraThread = new CameraThread();

			LOGGER.info("Start " + this.getClass().getSimpleName());
			cameraThread.start();
		}
	}

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
		;
	}

	public void setMotionDetection(boolean motionDetection) {

		cameraThread.setMotionDetection(motionDetection);
	}

}
