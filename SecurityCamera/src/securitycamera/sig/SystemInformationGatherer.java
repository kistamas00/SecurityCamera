package securitycamera.sig;

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

import securitycamera.webserver.Webserver;

public class SystemInformationGatherer {

	private final static Logger LOGGER = Logger
			.getLogger(Webserver.class.getName());
	private Sigar sigar;

	public void start() {

		if (sigar == null) {

			LOGGER.info("Start " + this.getClass().getSimpleName());

			sigar = new Sigar();
		}
	}

	public void stop() {

		LOGGER.info("Stop " + this.getClass().getSimpleName());

		sigar.close();
		sigar = null;
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

					data.put("id", "cpuCore" + i);
					data.put("name", cpuInfo.getVendor() + " "
							+ cpuInfo.getModel() + " (#" + (i + 1) + ")");
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
