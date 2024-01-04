package wrims.dss;

import hec.heclib.dss.DSSPathname;
import hec.heclib.dss.HecDataManager;
import hec.heclib.dss.HecDataManagerRemote;
import hec.heclib.dss.HecTimeSeries;
import hec.heclib.util.HecTime;

import java.util.Vector;

public class CondensedReference {

	public String nominalPathname;
	public Vector pathnameList;

	public CondensedReference() {
		nominalPathname = null;
		pathnameList = null;
	}

	public void addPathname(String pathname) {
		if (pathnameList == null) {
			pathnameList = new Vector();
		}
		pathnameList.addElement(pathname);
	}

	public int size() {
		if (pathnameList == null) {
			return 0;
		} else {
			return pathnameList.size();
		}
	}

	public void complete() {
		if (pathnameList == null) {
			return;
		}
		if (pathnameList.size() == 1) {
			nominalPathname = pathnameList.firstElement().toString();
			return;
		} else {
			String first = pathnameList.firstElement().toString();
			String last = pathnameList.lastElement().toString();
			int partPositions[] = new int[7];
			DSSPathname.parsePathname(first, partPositions);
			nominalPathname = first.substring(0, partPositions[4] - 1) + " - "
					+ last.substring(partPositions[3], partPositions[6]);
			return;
		}
	}

	public String toString() {
		return getNominalPathname();
	}

	public String getNominalPathname() {
		if (nominalPathname == null) {
			return "";
		} else {
			return nominalPathname;
		}
	}

	public void getPathnameTimes(HecDataManager dataManager, HecTime start,
			HecTime end) {
		start.setUndefined();
		end.setUndefined();
		if (pathnameList.size() == 0) {
			return;
		}
		String path = pathnameList.firstElement().toString();
		if (path == null) {
			return;
		}
		int partPositions[] = new int[7];
		DSSPathname.parsePathname(path, partPositions);
		start.set(path.substring(partPositions[3], partPositions[4] - 1),
				"0001");
		int interval = HecTimeSeries.getIntervalFromEPart(path.substring(
				partPositions[4], partPositions[5] - 1));
		if (interval > 0) {
			start.adjustToIntervalOffset(interval, 0);
		}
		path = dataManager.nextTimeSeriesPathname(pathnameList.lastElement()
				.toString());
		if ((path != null) && (path.length() > 10)
				&& (partPositions[4] - 1 > partPositions[3])) {
			end.set(path.substring(partPositions[3], partPositions[4] - 1),
					"0000");
			end.cleanTime();
			if (interval > 0) {
				end.adjustToIntervalOffset(interval, 0);
			}
		}
	}

	public void getPathnameTimes(HecDataManagerRemote dataManager,
			HecTime start, HecTime end) {
		start.setUndefined();
		end.setUndefined();
		if (pathnameList.size() == 0) {
			return;
		}
		String path = pathnameList.firstElement().toString();
		if (path == null) {
			return;
		}
		int partPositions[] = new int[7];
		DSSPathname.parsePathname(path, partPositions);
		start.set(path.substring(partPositions[3], partPositions[4] - 1),
				"0001");
		int interval = HecTimeSeries.getIntervalFromEPart(path.substring(
				partPositions[4], partPositions[5] - 1));
		if (interval > 0) {
			start.adjustToIntervalOffset(interval, 0);
		}
		path = dataManager.nextTimeSeriesPathname(pathnameList.lastElement()
				.toString());
		if ((path != null) && (path.length() > 10)
				&& (partPositions[4] - 1 > partPositions[3])) {
			end.set(path.substring(partPositions[3], partPositions[4] - 1),
					"0000");
			end.cleanTime();
			if (interval > 0) {
				end.adjustToIntervalOffset(interval, 0);
			}
		}
	}
}
