package securitycamera.modules.webserver.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpExchange;

import securitycamera.SecurityCamera;
import securitycamera.modules.camera.Camera;
import securitycamera.modules.webserver.Webserver;
import securitycamera.services.MD5;
import securitycamera.services.Settings;

public class AdminPageHandler extends MainHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		final Webserver WEBSERVER = SecurityCamera.getModule(Webserver.class);
		final Camera CAMERA = SecurityCamera.getModule(Camera.class);

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

				WEBSERVER.stop();

			} else if (url.equals("/admin/camera/start")) {

				CAMERA.start();

			} else if (url.equals("/admin/camera/stop")) {

				CAMERA.stop();

			} else if (url.equals("/admin/camera/stream/on")) {

				CAMERA.setStreaming(true);

			} else if (url.equals("/admin/camera/stream/off")) {

				CAMERA.setStreaming(false);

			} else if (url.equals("/admin/camera/motiondetection/enable")) {

				CAMERA.setMotionDetection(true);

			} else if (url.equals("/admin/camera/motiondetection/disable")) {

				CAMERA.setMotionDetection(false);

			} else if (url.equals("/admin/camera/capture/photo")) {

				CAMERA.capturePhoto();

			} else if (url.equals("/admin/photolimit")) {

				BufferedReader br = new BufferedReader(
						new InputStreamReader(exchange.getRequestBody()));
				StringBuffer sb = new StringBuffer();
				String line = null;

				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

				br.close();

				if (sb.toString().startsWith("photolimit=")
						&& sb.toString().length() > "photolimit=".length()) {

					int photoLimit = Integer
							.parseInt(sb.toString().split("=")[1]);

					Camera.setPhotoLimit(photoLimit);
				}

			} else if (url.equals("/admin/email")) {

				BufferedReader br = new BufferedReader(
						new InputStreamReader(exchange.getRequestBody()));
				StringBuffer sb = new StringBuffer();
				String line = null;

				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

				br.close();

				if (sb.toString().startsWith("email=")
						&& sb.toString().length() > "email=".length()) {

					String email = sb.toString().split("=")[1].replaceAll("%40",
							"@");

					Settings.setSetting(Settings.EMAIL, email);
				}

			} else if (url.equals("/admin/password")) {

				BufferedReader br = new BufferedReader(
						new InputStreamReader(exchange.getRequestBody()));
				StringBuffer sb = new StringBuffer();
				String line = null;

				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

				br.close();

				if (sb.toString().startsWith("password=")
						&& sb.toString().length() > "password=".length()) {

					String password = sb.toString().split("=")[1];

					Settings.setSetting(Settings.USER_PASS,
							MD5.stringToMD5(password));
				}
			}

			redirect(exchange, "/admin");

		} else {

			sendNotFoundPage(exchange);
		}
	}
}
