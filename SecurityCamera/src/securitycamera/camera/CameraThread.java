package securitycamera.camera;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import securitycamera.SecurityCamera;
import securitycamera.camera.enums.OStype;
import securitycamera.email.Email;

public class CameraThread extends Thread {

	private final static Logger LOGGER = Logger
			.getLogger(CameraThread.class.getName());
	private final static int FPS = 25;
	private final static double DIFF_LIMIT = 1.75;
	private final static long DETECTION_TIME_LIMIT_DEFAULT = 1000;

	private VideoCapture camera;
	private volatile Mat previousFrame;
	private volatile long previousDetection;

	private volatile boolean isRunning;
	private volatile boolean isStreaming;
	private volatile boolean saveNext;
	private volatile boolean motionDetection;
	private volatile boolean motionDetected;

	private long detectionTimeLimit;
	private Object previousFrameLockObject;

	public CameraThread() {

		isRunning = false;
		isStreaming = false;
		saveNext = false;
		motionDetection = false;
		motionDetected = false;

		detectionTimeLimit = DETECTION_TIME_LIMIT_DEFAULT;
		previousFrameLockObject = new Object();
	}

	@Override
	public synchronized void start() {

		if (isRunning == false) {

			LOGGER.info("Start " + this.getClass().getSimpleName());

			camera = new VideoCapture(0);

			if (camera.isOpened()) {
				isRunning = true;
				LOGGER.info("Camera connected successfully");
				super.start();
			} else {
				LOGGER.severe("Camera error!");
			}
		}
	}

	public void halt() {

		if (isRunning == true) {
			isRunning = false;

			LOGGER.info("Stop " + this.getClass().getSimpleName());

			camera.release();
		}
	}

	@Override
	public void run() {

		while (isRunning) {

			Mat frame = new Mat();
			camera.read(frame);

			if (SecurityCamera.OS_TYPE == OStype.LINUX) {

				Mat frameT = frame.t();
				Core.flip(frameT, frame, 0);
				frameT.release();
			}

			if (motionDetection && previousFrame != null
					&& System.currentTimeMillis()
							- previousDetection >= detectionTimeLimit) {

				double errorL2 = Core.norm(previousFrame, frame, Core.NORM_L2);
				double difference = errorL2 / (double) (previousFrame.rows()
						* previousFrame.cols());
				difference *= 100;

				if (difference > DIFF_LIMIT) {

					motionDetected = true;
					detectionTimeLimit *= 2;

					LOGGER.info("Motion detected, next time limit: "
							+ detectionTimeLimit + " ms");

					previousDetection = System.currentTimeMillis();
				}
			}

			if (saveNext || motionDetected) {

				DateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd-HH-mm-ss");
				String fileName = dateFormat.format(new Date()) + ".jpg";
				String path = "public" + File.separator + "pictures"
						+ File.separator + fileName;

				Imgcodecs.imwrite(path, frame);

				LOGGER.info("Photo created: " + fileName);

				if (motionDetected) {
					Email.sendEmail(new File(path));
				}

				saveNext = false;
				motionDetected = false;
			}

			synchronized (previousFrameLockObject) {

				if (previousFrame != null) {
					previousFrame.release();
				}
				previousFrame = frame;

			}

			try {

				Thread.sleep(1000 / FPS);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public byte[] getLastFrameCopy() {

		MatOfByte mob = new MatOfByte();

		synchronized (previousFrameLockObject) {

			Imgcodecs.imencode(".jpg", previousFrame, mob);

		}

		byte[] encodedMat = Base64.getEncoder().encode(mob.toArray());

		return encodedMat;
	}

	public void saveNextFrame() {
		saveNext = true;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public boolean isStreaming() {
		return isStreaming;
	}

	public boolean motionDetectionEnabled() {
		return motionDetection;
	}

	public void setStreaming(boolean isStreaming) {
		this.isStreaming = isStreaming;
	}

	public void setMotionDetection(boolean motionDetection) {

		if (motionDetection == true) {
			detectionTimeLimit = DETECTION_TIME_LIMIT_DEFAULT;
		}

		this.motionDetection = motionDetection;
	}
}
