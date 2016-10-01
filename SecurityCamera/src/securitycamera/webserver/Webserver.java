package securitycamera.webserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpServer;

import securitycamera.module.SecurityCameraModule;
import securitycamera.webserver.auth.UserAuthenticator;
import securitycamera.webserver.handler.AdminPageHandler;
import securitycamera.webserver.handler.BasicPageHandler;

public class Webserver extends SecurityCameraModule {

	private final static Logger LOGGER = Logger
			.getLogger(Webserver.class.getCanonicalName());
	private HttpServer server;
	private boolean isRunning;

	public Webserver() {

		try {

			this.server = HttpServer.create(new InetSocketAddress(80), 0);

		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
		}

		server.createContext("/", new BasicPageHandler());
		server.createContext("/admin", new AdminPageHandler())
				.setAuthenticator(new UserAuthenticator());

		server.setExecutor(null);
	}

	@Override
	public void start() {

		if (!isRunning) {

			isRunning = true;

			LOGGER.info("Start " + this.getClass().getSimpleName());
			server.start();
		}
	}

	@Override
	public void stop() {

		if (isRunning) {

			isRunning = false;

			LOGGER.info("Stop " + this.getClass().getSimpleName());
			server.stop(0);
		}
	}
}
