package wrimsv2_plugin.tools;

import static java.util.stream.Collectors.toSet;

import hec.heclib.dss.DSSPathname;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import mil.army.usace.hec.metadata.Interval;
import mil.army.usace.hec.metadata.IntervalFactory;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;

public class DssOperations {
	public static String matchPathName(Vector<?> v, String partB, String partC, String partE) {
		Set<String> equivalentIntervals = findEquivalentDssIntervals(partE);
		String pn=null;
		for(Object path : v) {
			String pnv = path.toString();
			DSSPathname catPathname = new DSSPathname(pnv);
			for (String potentialIntervalMatch : equivalentIntervals) {
				if (catPathname.getBPart().equalsIgnoreCase(partB)
					&& catPathname.getCPart().equalsIgnoreCase(partC)
					&& catPathname.getEPart().equalsIgnoreCase(potentialIntervalMatch)) {
					pn = pnv;
					break;
				}
			}
		}
		return pn;
	}

	public static Set<String> findEquivalentDssIntervals(String partE) {
        return IntervalFactory.findAnyDss(IntervalFactory.equalsName(partE))
            .map(c -> IntervalFactory.findAllDss(i -> i.getMinutes() == c.getMinutes()).stream()
                .map(Interval::getInterval)
                .collect(toSet()))
            .orElse(new HashSet<>());
	}

	public static String matchPathName(Vector v, DSSPathname dssPathname){
		Set<String> equivalentIntervals = findEquivalentDssIntervals(dssPathname.getEPart());
		String pn=null;
		for (Object catPathObj : v){
			String pnv=catPathObj.toString();
			DSSPathname catPath = new DSSPathname(pnv);
			DSSPathname copy = new DSSPathname(dssPathname.getPathname());
			for (String potentialIntervalMatch : equivalentIntervals) {
				copy.setEPart(potentialIntervalMatch);
				if (catPath.isSamePathname(copy.getPathname(), false)) {
					pn = pnv;
					break;
				}
			}
		}
		return pn;
	}
	
	public static synchronized void waitForDSSOp(){
		try {
			while(DebugCorePlugin.isDssInOp){
				Thread.sleep(100);
			}
			DebugCorePlugin.isDssInOp=true;
		} catch (InterruptedException e) {
			WPPException.handleException(e);
		}
	}
}
