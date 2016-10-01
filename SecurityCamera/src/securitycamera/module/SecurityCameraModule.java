package securitycamera.module;

public abstract class SecurityCameraModule {

	abstract public void start();

	abstract public void stop();

	@Override
	public boolean equals(Object obj) {
		return getClass().equals(obj.getClass());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
