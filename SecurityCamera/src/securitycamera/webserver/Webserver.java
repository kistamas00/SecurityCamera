package securitycamera.webserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import securitycamera.webserver.auth.UserAuthenticator;
import securitycamera.webserver.handler.AdminPageHandler;
import securitycamera.webserver.handler.BasicPageHandler;

public class Webserver {

	private final static Logger LOGGER = Logger.getLogger(Webserver.class.getName());
	private HttpServer server;
	private boolean isRunning;

	public Webserver() throws IOException {

		this.server = HttpServer.create(new InetSocketAddress(80), 0);

		@SuppressWarnings("unused")
		HttpContext infoPage = server.createContext("/", new BasicPageHandler());
		HttpContext adminPage = server.createContext("/admin", new AdminPageHandler());

		adminPage.setAuthenticator(new UserAuthenticator());

		server.setExecutor(null);
	}

	public void start() {

		if (!isRunning) {

			isRunning = true;

			LOGGER.info("Start " + this.getClass().getSimpleName());
			server.start();
		}
	}

	public void stop() {

		if (isRunning) {

			isRunning = false;

			LOGGER.info("Stop " + this.getClass().getSimpleName());
			server.stop(0);
		}
	}
}
