package wrims.dss.test;

import hec.hecmath.DSS;
import hec.hecmath.DSSFile;

import java.util.Vector;

import vista.db.dss.DSSUtil;
import vista.set.Group;

public class TestDSSRetrieve {
	public static void main(String[] args) {
		long ti = System.currentTimeMillis();

		ti = System.currentTimeMillis();
		Group group = DSSUtil.createGroup("local", args[0]);
		group.getNumberOfDataReferences();
		System.out.println("Time to read entire catalog (vista): "
				+ (System.currentTimeMillis() - ti));

		DSSFile theFile = DSS.open(args[0]);
		Vector<?> pathNames = theFile.getCatalogedPathnames();
		theFile.close();

		System.out.println("Time to read entire catalog: "
				+ (System.currentTimeMillis() - ti));

	}

}
