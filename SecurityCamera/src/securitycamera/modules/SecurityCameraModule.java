package securitycamera.modules;

import securitycamera.services.Settings;

public abstract class SecurityCameraModule {

	public void start() {

		String idString = getIdString();
		if (idString != null) {
			Settings.setSetting(idString, true);
		}
	}

	public void stop() {

		String idString = getIdString();
		if (idString != null) {
			Settings.setSetting(idString, false);
		}
	}

	abstract public String getIdString();

	@Override
	public boolean equals(Object obj) {
		return getClass().equals(obj.getClass());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
