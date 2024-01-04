package wrims.dss.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import wrims.dss.DssViewer;
import wrims.dss.dts.DTSWrapper;

public class TestDSSViewer {
	public static void main(String[] args) {
		String mode = "Comp";
		int annualMode = 0;
		String timeWindow = "31OCT1921 2400 30SEP2003 2400";
		Vector<String> dssPaths = new Vector<String>(Arrays.asList(
				"/CALSIM/S_CLRLK/STORAGE//1MON/CALSIM30_06",
				"/CALSIM/S_SHSTA/STORAGE//1MON/CALSIM30_06"));
		Vector<DTSWrapper> dts = new Vector<DTSWrapper>();
		HashMap<Integer, Object> dssFiles = new HashMap<Integer, Object>();
		String dir = "d:/calsim3/";
		dssFiles.put(new Integer(0), new Vector<String>(Arrays.asList(dir
				+ "VERSION42_081811_DV.dss", dir + "CalSim30_06_SV.dss")));
		HashMap<Integer, Object> dssFparts = new HashMap<Integer, Object>();
		dssFparts.put(new Integer(0), new Vector<String>(Arrays.asList(
				"CALSIM30_06", "CALSIM30_06")));
		int[] keys = new int[] { 0 };
		ArrayList<Integer> months = new ArrayList<Integer>(Arrays.asList(10,
				11, 12, 1, 2, 3, 4, 5, 6, 7, 8, 9));
		DssViewer viewer = new DssViewer(mode, annualMode, timeWindow,
				dssPaths, dssFiles, dssFparts, keys, months);
		viewer.show(DssViewer.PLOT, "CFS");
		Vector<String> periods = new Vector<String>(Arrays
				.asList("31OCT1921 2400 - 30SEP2003 2400"));
		Hashtable<String, Object> variables = new Hashtable<String, Object>();
		variables.put("S_CLRLK", "S_CLRLK");
		viewer.setVariables(variables);
		viewer.setUncheckedDssFiles(new HashMap<Integer, Object>());
		viewer.loadVariableData("CFS", -1, true, null);
		variables.put("S_SHSTA", "S_SHSTA");
		viewer.loadVariableData("CFS", -1, true, null);
		viewer.calculateLongTermAverages(periods, -1, true, null);
		Hashtable<String, String>[] values = viewer.show("CFS", "31OCT1921");
		System.out.println(values[0].get("S_CLRLK"));
		System.out.println(values[0].get("S_SHSTA"));
		values = viewer.show("CFS", "30JUN1929");
		System.out.println(values[0].get("S_CLRLK"));
		System.out.println(values[0].get("S_SHSTA"));
	}
}
