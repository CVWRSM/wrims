package wrimsv2_plugin.debugger.view;

import hec.heclib.dss.HecDss;
import hec.hecmath.HecMath;
import hec.hecmath.HecMathException;
import hec.io.DataContainer;
import hec.io.TimeSeriesContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.tools.DssOperations;
import wrimsv2_plugin.tools.TimeOperation;
import wrimsv2_plugin.tools.VariableProperty;

public class ProcessAltColumn {
	
	public static void AdjustAltColumnNames(TableViewer viewer, int flag){
		Table table=viewer.getTable();
		removeAltColumns(table);
		addAltColumnNames(table, flag);
		table.setRedraw(true);
	}
	
	public static void removeAltColumns(Table table){
		int colSize=table.getColumnCount();
		for (int i=colSize-1; i>=0; i--){
			TableColumn tc = table.getColumn(i);
			String cn=tc.getText();
			if (cn.equals("Alt1") || cn.equals("Alt2") || cn.equals("Alt3") || cn.equals("Alt4")
					|| cn.equals("Alt5") || cn.equals("Alt6")|| cn.equals("Alt7")|| cn.equals("Alt8")){
				tc.dispose();
			}
		}
	}
	
	public static void addAltColumnNames(Table table, int flag){	
		Map<Integer, Integer> altColumnIndex = new HashMap<Integer, Integer>();
		switch (flag){
			case 0:
				altColumnIndex=DebugCorePlugin.variableAltColIndex;
				break;
			case 1:
				altColumnIndex=DebugCorePlugin.watchAltColIndex;
				break;
			case 2:
				altColumnIndex=DebugCorePlugin.variableAltColIndex;
				break;
		}
		
		for (int i=0; i<8; i++){
			if (DebugCorePlugin.selectedStudies[i]){
				altColumnIndex.put(table.getColumnCount(), i);
				TableColumn tc=new TableColumn(table, SWT.NORMAL);
				tc.setText("Alt"+(i+1));
				tc.pack();
			}
		}
	}
	
	public static String addAltColumnData(String vn, int flag, int index){
		Map<String, VariableProperty> vp=new HashMap<String, VariableProperty>();
		Map<Integer, Integer> altColIndex=new HashMap<Integer, Integer>();
		switch (flag){
			case 0:
				vp=DebugCorePlugin.variableProperty;
				altColIndex=DebugCorePlugin.variableAltColIndex;
				break;
			case 1:
				vp=DebugCorePlugin.watchProperty;
				altColIndex=DebugCorePlugin.watchAltColIndex;
				break;
			case 2:
				vp=DebugCorePlugin.allVariableProperty;
				altColIndex=DebugCorePlugin.variableAltColIndex;
				break;
		}
		String aPart = DebugCorePlugin.aPart;
		String svFPart = DebugCorePlugin.svFPart;
		HecDss[] dvDss = DebugCorePlugin.dvDss;
		HecDss[] svDss = DebugCorePlugin.svDss;
		
		int i=altColIndex.get(index);
		if (i<4){
			if (vp.containsKey(vn)){
				VariableProperty property=vp.get(vn);
				String timestep=property.getPartE();
				String ss="B="+vn+", C="+property.getPartC()+", E="+timestep;
				String startTime=TimeOperation.createStartTime(DebugCorePlugin.suspendedYear, DebugCorePlugin.suspendedMonth, DebugCorePlugin.suspendedDay, timestep);
				String endTime=TimeOperation.createEndTime(DebugCorePlugin.suspendedYear, DebugCorePlugin.suspendedMonth, DebugCorePlugin.suspendedDay, timestep);
			
				try {
					DataContainer dc;
					double[] values;
					HecDss dss = dvDss[i];
					Vector v=DebugCorePlugin.dvVector[i];
					String pn=DssOperations.matchPathName(v, vn, property.getPartC(), timestep);
					if (pn!=null){
						dc = dss.get(pn, startTime, endTime);
						values=((TimeSeriesContainer)dc).values;
						if (values.length>0){
							return DebugCorePlugin.df.format(values[0]);
						}
					}else{
						dss=svDss[i];
						v=DebugCorePlugin.svVector[i];
						pn=DssOperations.matchPathName(v, vn, property.getPartC(), timestep);
						if (pn !=null){
							dc = dss.get(pn, startTime, endTime);
							values=((TimeSeriesContainer)dc).values;
							if (values.length>0){
								return DebugCorePlugin.df.format(values[0]);
							}
						}
					}	
					return "";
				} catch (Exception e) {
					WPPException.handleException(e);
					return "";
				}
			}else{
				return "";
			}
		}else{
			int j=i-4;
			Map<String, String> studyData = DebugCorePlugin.studiesData[j];
			if (studyData.containsKey(vn)){
				String dataString=studyData.get(vn).trim().replace(",", "");
				double value=Double.parseDouble(dataString);
				return DebugCorePlugin.df.format(value);
			}else{
				return "";
			}
		}
	}
}
