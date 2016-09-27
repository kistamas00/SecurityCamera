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

				Map<String, Object> map = new HashMap<String, Object>();

				map.put("camera", SecurityCamera.cameraIsRunning());
				map.put("stream", SecurityCamera.cameraIsStreaming());
				map.put("motionDetection",
						SecurityCamera.cameraMotionDetectionEnabled());
				map.put("email", SecurityCamera.getEmailAdress());

				sendObject(exchange, map);

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
