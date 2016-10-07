package securitycamera.modules.webserver.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import securitycamera.SecurityCamera;
import securitycamera.modules.camera.Camera;
import securitycamera.modules.sig.SystemInformationGatherer;
import securitycamera.services.Settings;

public class BasicPageHandler extends MainHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		final Camera CAMERA = SecurityCamera.getModule(Camera.class);
		final SystemInformationGatherer SIG = SecurityCamera
				.getModule(SystemInformationGatherer.class);

		String[] split = exchange.getRequestURI().toString().split("[?]");

		final String url = split[0];
		// final String queryString = split.length > 1 ? split[1] : "";
		// final Map<String, String> params = parseQueryString(queryString);

		if (exchange.getRequestMethod().equals("GET")) {

			if (url.equals("/")) {

				sendStaticFile(exchange, Paths.get("content", "state.html"));

			} else if (url.equals("/stream")) {

				sendStaticFile(exchange, Paths.get("content", "stream.html"));

			} else if (url.equals("/pictures")) {

				sendStaticFile(exchange, Paths.get("content", "pictures.html"));

			} else if (url.equals("/pictures/all")) {

				File folder = new File(Settings
						.getSetting(Settings.PICTURES_PATH, String.class));
				File[] listOfFiles = folder.listFiles();
				List<String> fileNames = new ArrayList<String>();

				for (File file : listOfFiles) {
					fileNames.add(file.getName());
				}

				Collections.sort(fileNames);

				sendObject(exchange, fileNames);

			} else if (url.equals("/stream/next")) {

				byte[] b = CAMERA.getLastFrameCopy();

				sendEncodedImage(exchange, b);

			} else if (url.equals("/status")) {

				Map<String, Object> data = new HashMap<String, Object>();
				List<Map<String, Object>> securityCameraStatus = new ArrayList<Map<String, Object>>();

				Map<String, Object> e = new HashMap<String, Object>();
				e.put("id", Settings.WEBSERVER);
				e.put("name", "Webserver");
				e.put("value", "RUNNING");
				securityCameraStatus.add(e);
				data.put(Settings.WEBSERVER, true);

				e = new HashMap<String, Object>();
				e.put("id", Settings.CAMERA);
				e.put("name", "Camera");
				e.put("value", CAMERA.isRunning() ? "RUNNING" : "STOPPED");
				securityCameraStatus.add(e);
				data.put(Settings.CAMERA, CAMERA.isRunning());

				e = new HashMap<String, Object>();
				e.put("id", Settings.STREAM);
				e.put("name", "Streaming");
				e.put("value", CAMERA.isStreaming() ? "ON" : "OFF");
				securityCameraStatus.add(e);
				data.put(Settings.STREAM, CAMERA.isStreaming());

				e = new HashMap<String, Object>();
				e.put("id", Settings.MOTION_DETECTION);
				e.put("name", "Motion detection");
				e.put("value", CAMERA.isMotionDetectionEnabled() ? "ENABLED"
						: "DISABLED");
				securityCameraStatus.add(e);
				data.put(Settings.MOTION_DETECTION,
						CAMERA.isMotionDetectionEnabled());

				e = new HashMap<String, Object>();
				e.put("id", Settings.EMAIL);
				e.put("name", "E-mail");
				e.put("value",
						Settings.getSetting(Settings.EMAIL, String.class));
				securityCameraStatus.add(e);
				data.put(Settings.EMAIL,
						Settings.getSetting(Settings.EMAIL, String.class));

				e = new HashMap<String, Object>();
				e.put("id", Settings.PICTURES_PATH);
				e.put("name", "Pictures location");
				e.put("value",
						Paths.get(Settings.getSetting(Settings.PICTURES_PATH,
								String.class)).toRealPath().toString());
				securityCameraStatus.add(e);
				data.put(Settings.PICTURES_PATH,
						Paths.get(Settings.getSetting(Settings.PICTURES_PATH,
								String.class)).toRealPath().toString());

				e = new HashMap<String, Object>();
				e.put("id", Settings.PHOTO_LIMIT);
				e.put("name", "Photo limit");
				e.put("type", "PROGRESSBAR");
				e.put("value", Camera.getPhotoLimitPerc());
				securityCameraStatus.add(e);
				data.put(Settings.PHOTO_LIMIT, Settings
						.getSetting(Settings.PHOTO_LIMIT, Integer.class));

				data.put("securityCameraStatus", securityCameraStatus);
				data.put("systemStatus", SIG.gatherInformations());

				data.put("numberOfPictures", Camera.getNumberOfPictures());

				sendObject(exchange, data);

			} else if (url.startsWith("/public/pictures/")) {

				String fileName = url.substring("/public/pictures/".length());
				Path path = Paths
						.get((Settings.getSetting(Settings.PICTURES_PATH,
								String.class) + File.separator + fileName)
										.replace("/", File.separator)
										.replace("\\", File.separator));

				if (Files.exists(path)) {

					sendStaticFile(exchange, path);

				} else {

					sendNotFoundPage(exchange);
				}

			} else if (url.startsWith("/public/") && !url.endsWith("/")) {

				String fileName = url.replace("/", File.separator);
				fileName = fileName.substring(1);

				if (Files.exists(Paths.get(fileName))) {

					sendStaticFile(exchange, Paths.get(fileName));

				} else {

					sendNotFoundPage(exchange);
				}

			} else {

				sendNotFoundPage(exchange);
			}

		} else {

			sendNotFoundPage(exchange);
		}
	}
}
