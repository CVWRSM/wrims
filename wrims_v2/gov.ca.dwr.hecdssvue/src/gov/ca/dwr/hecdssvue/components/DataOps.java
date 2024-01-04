package gov.ca.dwr.hecdssvue.components;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.views.DSSCatalogView;
import hec.dataTable.HecDataTable;
import hec.heclib.dss.CondensedReference;
import hec.heclib.dss.DSSPathname;
import hec.heclib.dss.HecDss;
import hec.heclib.util.HecTime;
import hec.hecmath.DSS;
import hec.hecmath.DSSFile;
import hec.hecmath.HecMath;
import hec.hecmath.HecMathException;
import hec.io.DataContainer;
import hec.io.TimeSeriesContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import calsim.app.AppUtils;
import calsim.app.Project;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;

public class DataOps {

	private static double FACTOR = 1000. * 43560 / (24 * 60 * 60);
	
	public static void saveData(HecDataTable table) {
		if (table.hasDataChanged()){
			int firstError[] = new int[3];
			int numberErrors = table.updateContainers(firstError);
			if (numberErrors > 0) {
				String message = "";
				if (numberErrors > 1) {
					message=message+numberErrors + " errors found.  First Error:\n";
				}
				if (firstError[2] == 0) {
					message=message+"Invalid date / time at ordinate " + (firstError[0] + 1);
				}else {
					message=message+"Dates / times are not ascending";
					HecTime time = new HecTime();
					time.set(firstError[1]);
					message=message+"\n    Date / time at ordinate " + firstError[0] + " is " + time;
					time.set(firstError[2]);
					message=message+"\n    Date / time at ordinate " + (firstError[0] + 1) + " is " + time;
				}
				WPPException.handleException(new Exception(message));
				return;
			}
			Vector<DataContainer> dataVector = table.getDataContainers();
			int size = dataVector.size();
			for (int i=0; i<size; i++){
				DataContainer dc=dataVector.get(i);
				TimeSeriesContainer tsc=(TimeSeriesContainer)dc;
				TimeSeriesContainer ntsc = (TimeSeriesContainer)tsc.clone();
				
				int k=-1;
				int j=0;
				
				while (k<i & j<4){
					if (DebugCorePlugin.selectedStudies[j]){
						k++;
					}
					j++;
				}
				
				String fileName=ntsc.fileName;
				DSSFile dssFile = DSS.open(fileName);
				
				if (k>=0 & k<4){
					String partB=ntsc.location;
					String partC=ntsc.parameter;
					boolean isStorage = partC.trim().toLowerCase().startsWith("storage");
					if (!isStorage){
						ArrayList<String> pathnameLists = DssPluginCore.pathnameLists[k];
						Iterator<String> it = pathnameLists.iterator();
						boolean found = false;
						String pathname;
						while (it.hasNext() & !found){
							pathname=it.next();
							String[] parts = pathname.split("/");
							if (parts[2].equalsIgnoreCase(partB) & parts[3].equalsIgnoreCase(partC)){
								try {
									HecMath origTsc = dssFile.read(getPathname(parts));
									String units=origTsc.getUnits();
									ntsc.units=units;
									
									if (tsc.units.equals(DssPluginCore.taf) && units.equals(DssPluginCore.cfs)){
										ntsc = adjustMonthlyData(ntsc, false); 
									} else if (tsc.units.equals(DssPluginCore.cfs) && units.equals(DssPluginCore.taf)){
										ntsc = adjustMonthlyData(ntsc, true); 
									}
									found=true;
								} catch (HecMathException e) {
								}
							}
						}
					}
				}
				
				
				try {
					dssFile.write(ntsc);
					dssFile.close();
				} catch (HecMathException e) {
					WPPException.handleException(e);
				}
			}
		}
	}

	public static TimeSeriesContainer getMonthlyData(TimeSeriesContainer tsc, ArrayList months) {
		TimeSeriesContainer ntsc = null;

		try {
			List<Double> lvalues = new ArrayList<Double>();
			List<Integer> ltimes = new ArrayList<Integer>();

			double[] values = tsc.values;
			int[] times = tsc.times;

			HecTime ht = new HecTime();
			// ht.set(tsc.startTime);

			for (int i = 0; i < values.length; i++) {
				// System.out.println(times[i]+" "+ht.month()+" "+months[1]+" "+values[i]);

				ht.set(times[i]);
				// WRONG MONTH FIX: subtract 1 min
				ht.add(-1);

				if (DssPluginCore.isAllWaterYear){
					if (months.contains(ht.month())) {
						ltimes.add(times[i]);
						lvalues.add(values[i]);
						// values[i] = Constants.UNDEFINED;
					}
				}else{
					if (months.contains(ht.month())) {
						if (ht.month()>0 && ht.month()<10 && DssPluginCore.filterWaterYear.contains(ht.year())){
							ltimes.add(times[i]);
							lvalues.add(values[i]);
							// values[i] = Constants.UNDEFINED;
						}else if (ht.month()>=10 && ht.month()<=12 && DssPluginCore.filterWaterYear.contains(ht.year()+1)){
							ltimes.add(times[i]);
							lvalues.add(values[i]);
							// values[i] = Constants.UNDEFINED;
						}
					}
				}
			}

			double[] nvalues = new double[lvalues.size()];
			int[] ntimes = new int[ltimes.size()];

			for (int i = 0; i < lvalues.size(); i++) {
				ntimes[i] = ltimes.get(i).intValue();
				nvalues[i] = lvalues.get(i).doubleValue();
				// System.out.println(ntimes[i]+" "+nvalues[i]);
			}

			ntsc = (TimeSeriesContainer) tsc.clone();
			ntsc.values = nvalues;
			ntsc.numberValues = nvalues.length;
			ntsc.times = ntimes;
			ntsc.startTime = ntimes[0];
			ntsc.endTime = ntimes[ntimes.length - 1];
			ntsc.interval = -1; // irregular time-series

		} catch (Exception e) {
		}

		ntsc=unitsConversion(ntsc);
		
		return ntsc;

	}
	
	public static TimeSeriesContainer unitsConversion(TimeSeriesContainer tsc) {
		String units=tsc.units;
		
		String fullName = tsc.fullName;
		String[] parts = fullName.split("/");
		boolean isStorage = parts[3].trim().toLowerCase().startsWith("storage");
		
		if (!DssPluginCore.units.equals(units)) {
			if (DssPluginCore.units.equals(DssPluginCore.taf) && units.equals(DssPluginCore.cfs)){
				tsc = adjustMonthlyData(tsc, true); 
				tsc.units=DssPluginCore.taf;
			} else if (DssPluginCore.units.equals(DssPluginCore.cfs) && units.equals(DssPluginCore.taf) && !isStorage){
				tsc = adjustMonthlyData(tsc, false); 
				tsc.units=DssPluginCore.cfs;
			}
		}
		return tsc;
	}

	public static TimeSeriesContainer adjustMonthlyData(TimeSeriesContainer tsc, boolean isCFStoTAF) { 

		HecTime ht = null;
		try {
			// double[] values = tsc.values;
			double[] nvalues = new double[tsc.values.length];
			int[] times = tsc.times;
			int ndays = 0;
			ht = new HecTime();
			for (int i = 0; i < times.length; i++) {
				ht.set(times[i]);
				// FIX: subtract 1 min
				ht.add(-1);
				ndays = ht.day();
				/*
				 * HecTime BUG: sometimes ht.day() returns a day later than *
				 * should???? I wasted a lot of time on this!!
				 * 
				 * returns ht.day() == 1, ht.hour() == 0 AS OPPOSED TO ht.day()
				 * == 28,30,31 (depending on month), ht.hour() == 24
				 */

				// if (DEBUG) System.out.println(ht.year()+" "+ht.month()+
				// " "+ht.day()+ht.hour()+" "+ht.minute()+" "+ht.second()+" "+times[i]+"
				// "+values[i]);
				// CB nvalues[i] = tsc.values[i] * ndays * factor;
				if (isCFStoTAF) {
					nvalues[i] = tsc.values[i] * ndays / FACTOR;
				} else {
					nvalues[i] = tsc.values[i] / ndays * FACTOR;
				}
			}
			tsc.values = nvalues;

		} catch (Exception e) {
			WPPException.handleException(e);
		}
		return tsc;
	}

	public static TimeSeriesContainer diff(TimeSeriesContainer tsc1, TimeSeriesContainer tsc0){
		try {
			HecMath hm0=HecMath.createInstance(tsc0);
			HecMath hm1=HecMath.createInstance(tsc1);
			hm1=hm1.subtract(hm0);
			return (TimeSeriesContainer)hm1.getData();
		} catch (HecMathException e) {
			WPPException.handleException(e);
			return tsc1;
		}
		
	}
	
	public static void clearGeoSchematicVariableData(){
		DssPluginCore.geoSchematicVariableNames=new ArrayList<String>();
		DssPluginCore.geoSchematicVariableData = new HashMap[4];
		DssPluginCore.geoSchematicVariableUnitsCFS=new HashMap[4];
		DssPluginCore.geoSchematicVariableUnitsTAF=new HashMap[4];
		DssPluginCore.geoLongTermAverageDataCFS=new HashMap();
		DssPluginCore.geoLongTermAverageDataTAF=new HashMap();
		
		for (int kk=0; kk<4; kk++){
			HashMap<String, HecMath> data= new HashMap<String, HecMath>();
			DssPluginCore.geoSchematicVariableData[kk]=data;
			HashMap<String, String> cfsUnitsMap = new HashMap<String, String>();
			DssPluginCore.geoSchematicVariableUnitsCFS[kk]=cfsUnitsMap;
			HashMap<String, String> tafUnitsMap = new HashMap<String, String>();
			DssPluginCore.geoSchematicVariableUnitsTAF[kk]=tafUnitsMap;
		}
		
		for (int ii=0; ii<DssPluginCore._schematicTwSelections.size()-1; ii++){
			ArrayList<HashMap<String, Double>> dataCFS=new ArrayList<HashMap<String, Double>>();
			DssPluginCore.geoLongTermAverageDataCFS.put(ii, dataCFS);
			ArrayList<HashMap<String, Double>> dataTAF=new ArrayList<HashMap<String, Double>>();
			DssPluginCore.geoLongTermAverageDataTAF.put(ii, dataTAF);
			for (int kk=0; kk<4; kk++){
				HashMap<String, Double> altDataCFS=new HashMap<String, Double>();
				dataCFS.add(altDataCFS);
				HashMap<String, Double> altDataTAF=new HashMap<String, Double>();
				dataTAF.add(altDataTAF);
			}
		}
	}
	
	public static void loadAllSchematicVariableData(){
		DssPluginCore.allSchematicVariableUnitsCFS=new HashMap[4];
		DssPluginCore.allSchematicVariableUnitsTAF=new HashMap[4];
		//DssPluginCore.allVariableUnits=new HashMap[4];
		DssPluginCore.longTermAverageDataCFS=new HashMap();
		DssPluginCore.longTermAverageDataTAF=new HashMap();
		DssPluginCore.allSchematicVariableData = new HashMap[4];
		for (int kk=0; kk<4; kk++){
			HashMap<String, HecMath> data= new HashMap<String, HecMath>();
			DssPluginCore.allSchematicVariableData[kk]=data;
			HashMap<String, String> cfsUnitsMap = new HashMap<String, String>();
			DssPluginCore.allSchematicVariableUnitsCFS[kk]=cfsUnitsMap;
			HashMap<String, String> tafUnitsMap = new HashMap<String, String>();
			DssPluginCore.allSchematicVariableUnitsTAF[kk]=tafUnitsMap;
			HashMap<String, String> unitsMap = new HashMap<String, String>();
			//DssPluginCore.allVariableUnits[kk] = unitsMap;
			
			if (DebugCorePlugin.selectedStudies[kk]){
				if (DebugCorePlugin.dvDss[kk] !=null){
					DebugCorePlugin.dvDss[kk].setTimeWindow(DebugCorePlugin.timeWindow);
				}
				if (DebugCorePlugin.svDss[kk] !=null){
					DebugCorePlugin.svDss[kk].setTimeWindow(DebugCorePlugin.timeWindow);
				}
			}	
		}
		
		int size = DssPluginCore.allSchematicVariableNames.size();
		
		for (int i=0; i<4; i++){
			if (DebugCorePlugin.selectedStudies[i]){
				DssPluginCore.pathnameLists[i]=new ArrayList<String>();
				HecDss dvFile = DebugCorePlugin.dvDss[i];
				HecDss svFile = DebugCorePlugin.svDss[i];
				if (dvFile !=null){
					DssPluginCore.dvPathnameMap[i] = generatePathnameMap(dvFile, i);
				}else{
					DssPluginCore.dvPathnameMap[i] = null;
				}
				if (svFile !=null){
					DssPluginCore.svPathnameMap[i] = generatePathnameMap(svFile, i);
				}else{
					DssPluginCore.svPathnameMap[i] = null;
				}
			}
		}
		
		for (int j=0; j<size; j++) {
			String name = DssPluginCore.allSchematicVariableNames.get(j);
			if (DssPluginCore.allPathName.containsKey(name)){
				String pathName = DssPluginCore.allPathName.get(name);
				for (int i=0; i<4; i++){
					if (DebugCorePlugin.selectedStudies[i]){
						HecMath dataSet=null;
						HecDss dvFile = DebugCorePlugin.dvDss[i];
						HecDss svFile = DebugCorePlugin.svDss[i];
						if (dvFile != null){
							try {
								dataSet= dvFile.read(pathName);
								if (dataSet ==null){
									readFromSV(svFile, pathName, name, i);
									continue;
								}else{
									String unit=dataSet.getUnits();
									DssPluginCore.allSchematicVariableData[i].put(name, dataSet);
									DssPluginCore.allSchematicVariableUnitsCFS[i].put(name, unit);
									DssPluginCore.allSchematicVariableUnitsTAF[i].put(name, unit);
									continue;
								}
							} catch (Exception e) {
								readFromSV(svFile, pathName, name, i);
							}
						}else{
							readFromSV(svFile, pathName, name, i);
						}
					}
				}
			}else{
				for (int i=0; i<4; i++){
					if (DebugCorePlugin.selectedStudies[i]){
						HecMath dataSet=null;
						HecDss dvFile = DebugCorePlugin.dvDss[i];
						HecDss svFile = DebugCorePlugin.svDss[i];
					
						String pathName=DssPluginCore.dvPathnameMap[i].get(name);
						if (pathName !=null){
							try {
								dataSet= dvFile.read(pathName);
								String unit=dataSet.getUnits();
								DssPluginCore.allSchematicVariableData[i].put(name, dataSet);
								DssPluginCore.allSchematicVariableUnitsCFS[i].put(name, unit);
								DssPluginCore.allSchematicVariableUnitsTAF[i].put(name, unit);
							}catch (Exception e) {
							}
						}
						pathName=DssPluginCore.svPathnameMap[i].get(name);
						if (pathName !=null){
							try {
								dataSet= svFile.read(pathName);
								String unit=dataSet.getUnits();
								DssPluginCore.allSchematicVariableData[i].put(name, dataSet);
								DssPluginCore.allSchematicVariableUnitsCFS[i].put(name, unit);
								DssPluginCore.allSchematicVariableUnitsTAF[i].put(name, unit);
							}catch (Exception e) {
							}
						}
					}
				}
			}
		}
		
	}
	
	public static void readFromSV(HecDss svFile, String pathName, String name, int i){
		try {
			HecMath dataSet = svFile.read(pathName);
			if (dataSet !=null){
				String unit=dataSet.getUnits();
				DssPluginCore.allSchematicVariableData[i].put(name, dataSet);
				DssPluginCore.allSchematicVariableUnitsCFS[i].put(name, unit);
				DssPluginCore.allSchematicVariableUnitsTAF[i].put(name, unit);
			}
		} catch (Exception e) {
		}
	}
	
	public static HashMap<String, String> generatePathnameMap(HecDss file, int j){
		HashMap<String, String> pathnameMap=new HashMap<String, String> ();
		Vector<CondensedReference> v=file.getCondensedCatalog();
		for (int i=0; i<v.size(); i++){
			CondensedReference cr = v.get(i);
			String pathname=cr.getNominalPathname();
			DSSPathname dssPathname = new DSSPathname(pathname);
			String partB=dssPathname.bPart();
			pathnameMap.put(partB, pathname);
			DssPluginCore.pathnameLists[j].add(pathname);
		}
		return pathnameMap;
	}
	
	public static boolean containParts(String pathName, String[] parts){
		String start="/";
		String end="/";
		for (int i=1; i<4; i++){
			start=start+parts[i]+"/";
		}
		for (int i=5; i<7; i++){
			end=end+parts[i]+"/";
		}
		if (pathName.startsWith(start) && pathName.endsWith(end)){
			return true;
		}else{
			return false;
		}
	}
	
	public static String getPathname(String[] parts) {
		StringBuilder sb = new StringBuilder();
		sb.append("/");
		for (int i = 1; i < parts.length; i++) {
			if (i == 4) {
				sb.append(parts[i].split("-")[0].trim()).append("/");
			} else {
				sb.append(parts[i]).append("/");
			}
		}
		return sb.toString();
	}
	
	public static void deleteSelected(){
		IWorkbench workbench=PlatformUI.getWorkbench();
		IWorkbenchPage workBenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
		DSSCatalogView dssCatalogView=(DSSCatalogView) workBenchPage.findView(DssPluginCore.ID_DSSVue_DSSCatalogView);
		boolean[] foundInDv={false, false, false, false};
		
		if (dssCatalogView !=null){
			Vector<String[]> selectedParts=dssCatalogView.getSelectedParts();
			for (int i=0; i<3; i++){
				if (DebugCorePlugin.selectedStudies[i]){
					HecDss dvFile = DebugCorePlugin.dvDss[i];
					if (dvFile !=null){
						Vector dvPathNameList = dvFile.getPathnameList();
						for (int j=0; j<selectedParts.size(); j++){
							Vector<String> selectedPathname = new Vector<String>();
							String[] parts=selectedParts.get(j);
							for (int k=0; k<dvPathNameList.size(); k++){
								String pathname=(String)dvPathNameList.get(k);
								if (DataOps.containParts(pathname, parts)){
									selectedPathname.add(pathname);
									foundInDv[i]=true;
								}
							}
							dvFile.delete(selectedPathname);
						}
					}
					HecDss svFile = DebugCorePlugin.svDss[i];
					if (!foundInDv[i] && svFile !=null){
						Vector svPathNameList = svFile.getPathnameList();
						for (int j=0; j<selectedParts.size(); j++){	
							Vector<String> selectedPathname = new Vector<String>();
							String[] parts=selectedParts.get(j);
							for (int k=0; k<svPathNameList.size(); k++){
								String pathname=(String)svPathNameList.get(k);
								if (DataOps.containParts(pathname, parts)){
									selectedPathname.add(pathname);
								}
							}
							svFile.delete(selectedPathname);
						}
					}
				}
			}
		}
		TableViewer viewer = dssCatalogView.getViewer();
		viewer.setInput(viewer.getInput());
	}
	
	public static void setProject(){
		Project prj = new Project();
		prj.setDVFile(DebugCorePlugin.studyDvFileNames[0]);
		prj.setDV2File(DebugCorePlugin.studyDvFileNames[1]);
		prj.setDV3File(DebugCorePlugin.studyDvFileNames[2]);
		prj.setDV4File(DebugCorePlugin.studyDvFileNames[3]);
		
		prj.setSVFile(DebugCorePlugin.studySvFileNames[0]);
		prj.setSV2File(DebugCorePlugin.studySvFileNames[1]);
		prj.setSV3File(DebugCorePlugin.studySvFileNames[2]);
		prj.setSV4File(DebugCorePlugin.studySvFileNames[3]);
		
		AppUtils.selectedStudies=DebugCorePlugin.selectedStudies;
		
		prj.setDVHashtable();
		prj.setSVHashtable();
		prj.openDVGroup();
		prj.openDV2Group();
		prj.openDV3Group();
		prj.openDV4Group();
		prj.openSVGroup();
		prj.openSV2Group();
		prj.openSV3Group();
		prj.openSV4Group();
		
		AppUtils.setCurrentProject(prj);
		
		
	}
}
