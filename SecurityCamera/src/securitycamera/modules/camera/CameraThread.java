package securitycamera.modules.camera;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import securitycamera.SecurityCamera;
import securitycamera.enums.OStype;
import securitycamera.services.Email;
import securitycamera.services.Settings;

public class CameraThread extends Thread {

	private final static Logger LOGGER = SecurityCamera.LOGGER;
	private final static int FPS = 25;
	private final static double DIFF_LIMIT = 1.75;
	private final static long DETECTION_TIME_LIMIT_DEFAULT = 1000;

	private VideoCapture camera;
	private volatile Mat previousFrame;
	private volatile long previousDetection;

	private volatile boolean isRunning;
	private volatile boolean saveNext;
	private volatile boolean motionDetected;

	private long detectionTimeLimit;
	private Object previousFrameLockObject;

	public CameraThread() {

		this.isRunning = false;
		this.saveNext = false;
		this.motionDetected = false;

		this.detectionTimeLimit = DETECTION_TIME_LIMIT_DEFAULT;
		this.previousFrameLockObject = new Object();
	}

	@Override
	public synchronized void start() {

		if (isRunning == false) {

			LOGGER.info("Start " + this.getClass().getSimpleName());

			camera = new VideoCapture(0);

			if (camera.isOpened()) {

				LOGGER.info("Camera connected successfully");

				setStreaming(
						Settings.getSetting(Settings.STREAM, Boolean.class));
				setMotionDetection(Settings
						.getSetting(Settings.MOTION_DETECTION, Boolean.class));
				isRunning = true;

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

			if (Settings.getSetting(Settings.MOTION_DETECTION, Boolean.class)
					&& previousFrame != null && System.currentTimeMillis()
							- previousDetection >= detectionTimeLimit) {

				double errorL2 = Core.norm(previousFrame, frame, Core.NORM_L2);
				double difference = errorL2 / (double) (previousFrame.rows()
						* previousFrame.cols());
				difference *= 100;

				if (difference > DIFF_LIMIT) {

					motionDetected = true;
					detectionTimeLimit *= 2;

					LOGGER.warning("Motion detected, next time limit: "
							+ detectionTimeLimit + " ms");

					previousDetection = System.currentTimeMillis();
				}
			}

			if (saveNext || motionDetected) {

				DateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd-HH-mm-ss");
				String fileName = dateFormat.format(new Date()) + ".jpg";
				String path = Settings.getSetting(Settings.PICTURES_PATH,
						String.class) + File.separator + fileName;

				Imgcodecs.imwrite(path, frame);

				if (motionDetected) {
					Email.sendEmail(new File(path));
				}

				int numberOfPictures = Camera.getNumberOfPictures();
				int photoLimit = Settings.getSetting(Settings.PHOTO_LIMIT,
						Integer.class);

				if (numberOfPictures <= photoLimit) {

					LOGGER.info("Photo created: " + fileName);

				} else {

					File[] fileList = new File(Settings
							.getSetting(Settings.PICTURES_PATH, String.class))
									.listFiles();
					List<String> fileNames = new ArrayList<String>();

					for (File f : fileList) {
						fileNames.add(f.getName());
					}

					Collections.sort(fileNames);

					File picture = new File(Settings
							.getSetting(Settings.PICTURES_PATH, String.class)
							+ File.separator + fileNames.get(0));
					picture.delete();

					LOGGER.warning("Photo limit reached! Photo ("
							+ fileNames.get(0) + ") has been deleted!");
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

	public void setStreaming(boolean isStreaming) {

		Settings.setSetting(Settings.STREAM, isStreaming);
	}

	public void setMotionDetection(boolean motionDetection) {

		if (motionDetection == true) {
			detectionTimeLimit = DETECTION_TIME_LIMIT_DEFAULT;
		}

		Settings.setSetting(Settings.MOTION_DETECTION, motionDetection);
	}
}
