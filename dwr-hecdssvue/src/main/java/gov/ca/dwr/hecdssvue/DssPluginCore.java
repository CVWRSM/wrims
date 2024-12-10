package gov.ca.dwr.hecdssvue;

import gov.ca.dwr.hecdssvue.panel.MonthlyTablePanel;
import hec.heclib.dss.CondensedReference;
import hec.heclib.dss.HecDss;
import hec.hecmath.HecMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.prefs.Preferences;

import org.swixml.SwingEngine;

public class DssPluginCore {
	public static String ID_DSSVue_DSSTableView="gov.ca.dwr.hecdssvue.views.DSSTableView";
	
	public static boolean dssEditable=false;
	
	public static final String comp="comp";
	public static final String diff="diff";
	public static String mode=comp;
	
	public final static String taf="TAF";
	public final static String cfs="CFS";
	public static String units=cfs;
	
	public static int chartType=0;
	
	public static final int WATERYEAR = 0; 
	public static final int CALENDAR_YEAR = 1; 
	public static final int FEDERAL_CONTRACT_YEAR = 2;
	public static int annualType=WATERYEAR;

	public final static String DEFAULT_TIME_WINDOW = "All";
	public static String tw=DEFAULT_TIME_WINDOW;
	
	public static Preferences _preferences;

	public static ArrayList<Integer> months= new ArrayList<Integer>(){{
	    add(10);
	    add(11);
	    add(12);
	    add(1);
	    add(2);
	    add(3);
	    add(4);
	    add(5);
	    add(6);
	    add(7);
	    add(8);
	    add(9);
	}};
	
	public static ArrayList<String> _twSelections = new ArrayList<String>(
		    Arrays.asList("All", "OCT1921 - SEP2015","OCT1921 - SEP2003",
		"OCT1928 - SEP1934","OCT1986 - SEP1992","OCT1975 - SEP1977",
		"OCT1976 - SEP1977","OCT1994 - SEP2003","OCT2000 - SEP2015",
		"OCT1997 - SEP2007", "Add..."));
	
	public static ArrayList<String> _schematicTwSelections = new ArrayList<String>(
			Arrays.asList( "Add...", "OCT1921 - SEP2015","OCT1921 - SEP2003",
			"OCT1928 - SEP1934","OCT1986 - SEP1992","OCT1975 - SEP1977",
			"OCT1976 - SEP1977","OCT1994 - SEP2003","OCT2000 - SEP2015", 
			"OCT1997 - SEP2007"));
	
	public static String twFile="twDSS.prf";
	public static boolean isAllWaterYear=true;
	public static HashSet<Integer> filterWaterYear=new HashSet<Integer>();
	
	public static String filter="/*/*/*/*/*/*/";
	
	public static ArrayList<HecDss> dssArray = new ArrayList<HecDss> ();// for multiple dss readin
	public static Vector<CondensedReference> condensedCatalog;
	public static ArrayList<String>[] pathnameLists=new ArrayList[4];
	public static HashMap<String, String>[] dvPathnameMap=new HashMap[4];
	public static HashMap<String, String>[] svPathnameMap=new HashMap[4];
		
	public static ArrayList<String> allStorageNames = new ArrayList<String>();
	public static HashMap<String, String> allPathName=new HashMap<String, String>();
	public static ArrayList<String> allSchematicVariableNames = new ArrayList<String>();
	public static HashMap<String, HecMath>[] allSchematicVariableData=new HashMap[4];
	public static HashMap<Integer, ArrayList<HashMap<String, Double>>> longTermAverageDataCFS=new HashMap();
	public static HashMap<Integer, ArrayList<HashMap<String, Double>>> longTermAverageDataTAF=new HashMap();
	public static HashMap<String, String>[] allSchematicVariableUnitsCFS=new HashMap[4];
	public static HashMap<String, String>[] allSchematicVariableUnitsTAF=new HashMap[4];
	//public static HashMap<String, String>[] allVariableUnits=new HashMap[4];
	
	public static ArrayList<String> geoSchematicVariableNames = new ArrayList<String>();
	public static HashMap<String, HecMath>[] geoSchematicVariableData=new HashMap[4];
	public static HashMap<Integer, ArrayList<HashMap<String, Double>>> geoLongTermAverageDataCFS=new HashMap();
	public static HashMap<Integer, ArrayList<HashMap<String, Double>>> geoLongTermAverageDataTAF=new HashMap();
	public static HashMap<String, String>[] geoSchematicVariableUnitsCFS=new HashMap[4];
	public static HashMap<String, String>[] geoSchematicVariableUnitsTAF=new HashMap[4];
	
	
	public static String dssPerspectiveID="DSS";
	
	public static SwingEngine swix;
	public static String[][] CalLiteLookups=new String[0][0];
	public static String[][] CalSim2Lookups=new String[0][0];
	public static String[][] CalSim3Lookups=new String[0][0];
	public static ArrayList<String> selectedCheckBox=new ArrayList<String>();
	
	public static String lastCopiedDssFolder="c:\\";
	public static MonthlyTablePanel mtp;
	
	public static boolean initWYTDss=true;
}
