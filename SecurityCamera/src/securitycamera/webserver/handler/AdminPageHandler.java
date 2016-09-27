package securitycamera.webserver.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpExchange;

import securitycamera.SecurityCamera;

public class AdminPageHandler extends MainHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		String[] split = exchange.getRequestURI().toString().split("[?]");

		final String url = split[0];
		// final String queryString = split.length > 1 ? split[1] : "";
		// final Map<String, String> params = parseQueryString(queryString);

		if (exchange.getRequestMethod().equals("GET")) {

			if (url.equals("/admin")) {

				sendStaticFile(exchange, Paths.get("content", "settings.html"));

			} else {

				sendNotFoundPage(exchange);
			}

		} else if (exchange.getRequestMethod().equals("POST")) {

			if (url.equals("/admin/webserver/stop")) {

				SecurityCamera.stopWebserver();

			} else if (url.equals("/admin/camera/start")) {

				SecurityCamera.startCamera();

			} else if (url.equals("/admin/camera/stop")) {

				SecurityCamera.stopCamera();

			} else if (url.equals("/admin/camera/stream/on")) {

				SecurityCamera.setStreaming(true);

			} else if (url.equals("/admin/camera/stream/off")) {

				SecurityCamera.setStreaming(false);

			} else if (url.equals("/admin/camera/motiondetection/enable")) {

				SecurityCamera.setMotionDetection(true);

			} else if (url.equals("/admin/camera/motiondetection/disable")) {

				SecurityCamera.setMotionDetection(false);

			} else if (url.equals("/admin/camera/capture/photo")) {

				SecurityCamera.capturePhoto();

			} else if (url.equals("/admin/email")) {

				BufferedReader br = new BufferedReader(
						new InputStreamReader(exchange.getRequestBody()));
				StringBuffer sb = new StringBuffer();
				String line = null;

				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

				br.close();

				String email = sb.toString().split("=")[1].replaceAll("%40",
						"@");

				SecurityCamera.setEmailAdress(email);
			}

			redirect(exchange, "/admin");

		} else {

			sendNotFoundPage(exchange);
		}
	}
}
