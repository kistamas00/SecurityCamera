package securitycamera.modules;

import java.util.HashSet;
import java.util.Set;

import securitycamera.services.Settings;

public class SecurityCameraModuleContainer {

	private Set<SecurityCameraModule> modules;

	public SecurityCameraModuleContainer() {

		this.modules = new HashSet<SecurityCameraModule>();
	}

	public void addModule(SecurityCameraModule module) {
		modules.add(module);
	}

	public void startAll() {
		modules.forEach(m -> {

			String idString = m.getIdString();
			if (idString == null
					|| Settings.getSetting(idString, Boolean.class) == true) {
				m.start();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <T extends SecurityCameraModule> T getModule(Class<T> c) {

		for (SecurityCameraModule module : modules) {

			if (module.getClass().equals(c)) {
				return (T) module;
			}
		}
		return null;
	}
}
