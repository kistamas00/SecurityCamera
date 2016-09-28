package securitycamera.webserver.handler;

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

public class BasicPageHandler extends MainHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		String[] split = exchange.getRequestURI().toString().split("[?]");

		final String url = split[0];
		final String queryString = split.length > 1 ? split[1] : "";
		final Map<String, String> params = parseQueryString(queryString);

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

				for (int i = 0; i < listOfFiles.length; i++) {
					fileNames.add(listOfFiles[i].getName());
				}

				Collections.sort(fileNames);

				if (params.size() > 0 && params.containsKey("latest")) {

					int index = fileNames.indexOf(params.get("latest"));
					fileNames = fileNames.subList(index + 1, fileNames.size());
				}

				sendObject(exchange, fileNames);

			} else if (url.equals("/stream/next")) {

				byte[] b = SecurityCamera.getLastFrameCopy();

				sendEncodedImage(exchange, b);

			} else if (url.equals("/status")) {

				Map<String, Object> data = new HashMap<String, Object>();
				List<Map<String, Object>> status = new ArrayList<Map<String, Object>>();

				Map<String, Object> e = new HashMap<String, Object>();
				String id = "webserver";
				e.put("id", id);
				e.put("name", "Webserver");
				e.put("value", "RUNNING");
				status.add(e);
				data.put(id, true);

				e = new HashMap<String, Object>();
				id = "camera";
				e.put("id", id);
				e.put("name", "Camera");
				e.put("value", SecurityCamera.cameraIsRunning() ? "RUNNING"
						: "STOPPED");
				status.add(e);
				data.put(id, SecurityCamera.cameraIsRunning());

				e = new HashMap<String, Object>();
				id = "stream";
				e.put("id", id);
				e.put("name", "Streaming");
				e.put("value",
						SecurityCamera.cameraIsStreaming() ? "ON" : "OFF");
				status.add(e);
				data.put(id, SecurityCamera.cameraIsStreaming());

				e = new HashMap<String, Object>();
				id = "motionDetection";
				e.put("id", id);
				e.put("name", "Motion detection");
				e.put("value", SecurityCamera.cameraMotionDetectionEnabled()
						? "ENABLED" : "DISABLED");
				status.add(e);
				data.put(id, SecurityCamera.cameraMotionDetectionEnabled());

				data.put("email", SecurityCamera.getEmailAdress());
				data.put("status", status);

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
