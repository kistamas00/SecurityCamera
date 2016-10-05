package securitycamera.modules.webserver.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

				File folder = new File("public" + File.separator + "pictures");
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
				String id = "webserver";
				e.put("id", id);
				e.put("name", "Webserver");
				e.put("value", "RUNNING");
				securityCameraStatus.add(e);
				data.put(id, true);

				e = new HashMap<String, Object>();
				id = "camera";
				e.put("id", id);
				e.put("name", "Camera");
				e.put("value", CAMERA.isRunning() ? "RUNNING" : "STOPPED");
				securityCameraStatus.add(e);
				data.put(id, CAMERA.isRunning());

				e = new HashMap<String, Object>();
				id = "stream";
				e.put("id", id);
				e.put("name", "Streaming");
				e.put("value", CAMERA.isStreaming() ? "ON" : "OFF");
				securityCameraStatus.add(e);
				data.put(id, CAMERA.isStreaming());

				e = new HashMap<String, Object>();
				id = "motionDetection";
				e.put("id", id);
				e.put("name", "Motion detection");
				e.put("value", CAMERA.isMotionDetectionEnabled() ? "ENABLED"
						: "DISABLED");
				securityCameraStatus.add(e);
				data.put(id, CAMERA.isMotionDetectionEnabled());

				e = new HashMap<String, Object>();
				id = "email";
				e.put("id", id);
				e.put("name", "E-mail");
				e.put("value",
						Settings.getSetting(Settings.EMAIL, String.class));
				securityCameraStatus.add(e);
				data.put(id, Settings.getSetting(Settings.EMAIL, String.class));

				e = new HashMap<String, Object>();
				id = "photoLimit";
				e.put("id", id);
				e.put("name", "Photo limit");
				e.put("type", "PROGRESSBAR");
				e.put("value", Camera.getPhotoLimitPerc());
				securityCameraStatus.add(e);
				data.put(id, Settings.getSetting(Settings.PHOTO_LIMIT,
						Integer.class));

				data.put("securityCameraStatus", securityCameraStatus);
				data.put("systemStatus", SIG.gatherInformations());

				data.put("numberOfPictures", Camera.getNumberOfPictures());

				sendObject(exchange, data);

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
