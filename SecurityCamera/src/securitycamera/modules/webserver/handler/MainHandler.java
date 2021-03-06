package securitycamera.modules.webserver.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import securitycamera.SecurityCamera;

public abstract class MainHandler implements HttpHandler {

	private static Logger LOGGER = SecurityCamera.LOGGER;

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		LOGGER.info(exchange.getRequestMethod() + " request from "
				+ exchange.getRemoteAddress().getAddress().toString() + " to "
				+ exchange.getRequestURI().toString() + " (" + 200 + ")");

		String responseText = "OK!";

		OutputStream responseBody = exchange.getResponseBody();

		exchange.sendResponseHeaders(200, responseText.length());
		responseBody.write(responseText.getBytes());
		responseBody.flush();
		responseBody.close();
	}

	protected final Map<String, String> parseQueryString(String queryString) {

		Map<String, String> params = new HashMap<String, String>();

		if (!queryString.equals("")) {

			for (String entry : queryString.split("&")) {
				String[] splittedEntry = entry.split("=");
				params.put(splittedEntry[0], splittedEntry[1]);
			}
		}

		return params;
	}

	protected final void sendStaticFile(HttpExchange exchange, Path path)
			throws IOException {

		// LOGGER.info(exchange.getRequestMethod() + " request from " +
		// exchange.getRemoteAddress().getAddress().toString()
		// + " to " + exchange.getRequestURI().toString() + " (" + 200 + ")");

		String url = exchange.getRequestURI().toString();
		String contentType = "text/html";

		if (url.endsWith(".css")) {
			contentType = "text/css";
		} else if (url.endsWith(".js")) {
			contentType = "application/javascript";
		} else if (url.endsWith(".jpg")) {
			contentType = "image/jpeg";
		} else if (url.endsWith(".woff2")) {
			contentType = "font/woff2";
		} else if (url.endsWith(".map")) {
			contentType = "application/json";
		}

		exchange.getResponseHeaders().add("Content-type", contentType);

		OutputStream responseBody = exchange.getResponseBody();

		byte[] encoded = Files.readAllBytes(path);
		exchange.sendResponseHeaders(200, encoded.length);
		responseBody.write(encoded);
		responseBody.flush();
		responseBody.close();
	}

	protected final void sendEncodedImage(HttpExchange exchange,
			byte[] encodedImg) throws IOException {

		if (encodedImg == null) {
			sendNotFoundPage(exchange);
		} else {

			// LOGGER.info(exchange.getRequestMethod() + " request from " +
			// exchange.getRemoteAddress().getAddress().toString()
			// + " to " + exchange.getRequestURI().toString() + " (" + 200 +
			// ")");

			String startString = "data:image/jpeg;base64,";

			exchange.getResponseHeaders().add("Content-type", "text/plain");
			exchange.sendResponseHeaders(200,
					startString.length() + encodedImg.length);
			exchange.getResponseBody().write(startString.getBytes());
			exchange.getResponseBody().write(encodedImg);
			exchange.getResponseBody().flush();
			exchange.getResponseBody().close();
		}
	}

	protected final void sendObject(HttpExchange exchange, Object object)
			throws IOException {

		// LOGGER.info(exchange.getRequestMethod() + " request from " +
		// exchange.getRemoteAddress().getAddress().toString()
		// + " to " + exchange.getRequestURI().toString() + " (" + 200 + ")");

		Gson gson = new Gson();
		String data = gson.toJson(object);

		OutputStream responseBody = exchange.getResponseBody();

		exchange.getResponseHeaders().add("Content-type", "application/json");
		exchange.sendResponseHeaders(200, data.length());
		responseBody.write(data.getBytes());
		responseBody.flush();
		responseBody.close();
	}

	protected final void sendNotFoundPage(HttpExchange exchange)
			throws IOException {

		LOGGER.warning(exchange.getRequestMethod() + " request from "
				+ exchange.getRemoteAddress().getAddress().toString() + " to "
				+ exchange.getRequestURI().toString() + " (" + 404 + ")");

		OutputStream responseBody = exchange.getResponseBody();

		exchange.sendResponseHeaders(404, 0);
		responseBody.flush();
		responseBody.close();
	}

	protected final void redirect(HttpExchange exchange, String url)
			throws IOException {

		LOGGER.info(exchange.getRequestMethod() + " request from "
				+ exchange.getRemoteAddress().getAddress().toString() + " to "
				+ exchange.getRequestURI().toString() + " (" + 301 + ")");

		OutputStream responseBody = exchange.getResponseBody();

		exchange.getResponseHeaders().add("Location", url);
		exchange.sendResponseHeaders(301, 0);
		responseBody.flush();
		responseBody.close();
	}
}
