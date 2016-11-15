package securitycamera.modules.sig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import securitycamera.SecurityCamera;
import securitycamera.modules.SecurityCameraModule;
import securitycamera.modules.webserver.Webserver;

public class SystemInformationGatherer extends SecurityCameraModule {

	private final static Logger LOGGER = Logger
			.getLogger(Webserver.class.getCanonicalName());
	private Sigar sigar;

	public SystemInformationGatherer() {
		System.setProperty("java.library.path", System.getProperty("user.dir"));
	}

	@Override
	public void start() {

		if (sigar == null) {

			super.start();

			sigar = new Sigar();

			LOGGER.info("Start " + this.getClass().getSimpleName());
		}
	}

	@Override
	public void stop() {

		if (sigar != null) {

			super.stop();

			LOGGER.info("Stop " + this.getClass().getSimpleName());
			sigar.close();
			sigar = null;
		}
	}

	@Override
	public String getIdString() {
		return null;
	}

	public List<Map<String, Object>> gatherInformations() {

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		if (sigar != null) {

			try {

				CpuInfo[] cpuInfoList = sigar.getCpuInfoList();
				CpuPerc[] cpuPercList = sigar.getCpuPercList();

				for (int i = 0; i < cpuInfoList.length; i++) {

					CpuInfo cpuInfo = cpuInfoList[i];
					CpuPerc cpuPerc = cpuPercList[i];

					Map<String, Object> data = new HashMap<String, Object>();

					String cpuName = null;
					switch (SecurityCamera.OS_TYPE) {
					case LINUX:
						cpuName = "CPU (#" + (i + 1) + ")";
						break;
					case WINDOWS:
						cpuName = cpuInfo.getVendor() + " " + cpuInfo.getModel()
								+ " (#" + (i + 1) + ")";
						break;
					}

					data.put("id", "cpuCore" + i);
					data.put("name", cpuName);
					data.put("type", "PROGRESSBAR");
					data.put("value", Math.round(cpuPerc.getCombined() * 100));

					result.add(data);
				}

				Mem mem = sigar.getMem();

				Map<String, Object> data = new HashMap<String, Object>();

				data.put("id", "memory");
				data.put("name", "Memory");
				data.put("type", "PROGRESSBAR");
				data.put("value", Math.round(mem.getUsedPercent()));

				result.add(data);

			} catch (SigarException e) {
				LOGGER.severe(e.getMessage());
			}

		}

		return result;
	}
}
