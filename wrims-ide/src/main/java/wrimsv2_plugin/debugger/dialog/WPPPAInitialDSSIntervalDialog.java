package wrimsv2_plugin.debugger.dialog;

import hec.heclib.dss.HecDss;
import hec.hecmath.HecMath;
import hec.hecmath.TimeSeriesMath;
import hec.io.TimeSeriesContainer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import wrimsv2.commondata.wresldata.StudyDataSet;
import wrimsv2.debug.Compile;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.launcher.WPPMainTab;
import wrimsv2_plugin.debugger.menuitem.EnableMenus;
import wrimsv2_plugin.debugger.model.WPPDebugTarget;
import wrimsv2_plugin.debugger.view.WPPFileIncExploreView;
import wrimsv2_plugin.debugger.view.WPPVarDetailView;
import wrimsv2_plugin.tools.FileProcess;
import wrimsv2_plugin.tools.TimeOperation;

public class WPPPAInitialDSSIntervalDialog extends Dialog {
	private WPPMainTab mainTab;
	private ProgressBar seriesPAInitPB;
	private ILaunchConfiguration configuration;
	
	public WPPPAInitialDSSIntervalDialog(Shell parent, WPPMainTab mainTab, ProgressBar seriesPAInitPB, ILaunchConfiguration configuration) {
		super(parent, SWT.APPLICATION_MODAL|SWT.MIN|SWT.RESIZE);
		this.mainTab=mainTab;
		this.seriesPAInitPB=seriesPAInitPB;
		this.configuration=configuration;
		setText("Set Generated PA Initial Dss Files Interval");
	}
	
	public void openDialog(){
		Shell shell=new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.setSize(400, 150);
		shell.setLocation(450, 300);
		//shell.pack();
		shell.open();
	}

	 protected void createContents(final Shell shell) {
		GridLayout layout=new GridLayout(2, false);
		layout.marginWidth=20;
		layout.marginHeight=10;
		shell.setLayout(layout);
		
		GridData gridData=new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan=1;
		
		final Text paInitDssInterval = new Text(shell, SWT.BORDER);
		
		Label label1 = new Label(shell, SWT.NONE);
		label1.setText("months");
		
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		ok.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				DebugCorePlugin.paInitialDssInterval=Integer.parseInt(paInitDssInterval.getText());
				createSeriesPAInits();
				shell.close();
			}
		});
		
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				shell.close();
			}
		});
		
		shell.setDefaultButton(ok);
	 }
	 
	public void createSeriesPAInits(){
		final int interval = DebugCorePlugin.paInitialDssInterval;
		final int startYear=Integer.parseInt(mainTab.startYearCombo.getText());
		final int startMonth=TimeOperation.monthValue(mainTab.startMonthCombo.getText());
		final int startDay=Integer.parseInt(mainTab.startDayCombo.getText());
		final int endYear=Integer.parseInt(mainTab.endYearCombo.getText());
		final int endMonth=TimeOperation.monthValue(mainTab.endMonthCombo.getText());
		final int endDay=Integer.parseInt(mainTab.endDayCombo.getText());
		final String initFile = mainTab.fInitFileText.getText();

		Thread thread=new Thread(){
			private int paStartYear;
			private int paStartMonth;
			private int paStartDay;

			public void run(){
				paStartYear=startYear;
				paStartMonth=startMonth;
				paStartDay=startDay;
					
				Calendar cal1 = new GregorianCalendar();
			    Calendar cal2 = new GregorianCalendar();
			    cal1.set(paStartYear, paStartMonth, paStartDay);
				cal2.set(endYear, endMonth, endDay);
				int i = 0;
				boolean isFirstOne=true;
				DebugCorePlugin.isCreateSeriesPAInit=true;
					
				Display.getDefault().asyncExec(new Runnable(){
						
					public void run(){
						seriesPAInitPB.setMinimum(startYear);
						seriesPAInitPB.setMaximum(endYear);
						seriesPAInitPB.setVisible(true);
					}
				});

				while ((cal1.before(cal2) || cal1.equals(cal2)) && DebugCorePlugin.isCreateSeriesPAInit) {
					Display.getDefault().asyncExec(new Runnable(){
							
						public void run(){
								seriesPAInitPB.setSelection(paStartYear);
								seriesPAInitPB.setVisible(true);
							}
						});
						createPAInit(paStartYear, paStartMonth, paStartDay, startYear, startMonth, startDay, i*interval, isFirstOne, initFile);
						cal1.add(Calendar.MONTH, interval);
						paStartYear = cal1.get(Calendar.YEAR);
						paStartMonth = cal1.get(Calendar.MONTH);
						paStartDay = cal1.get(Calendar.DAY_OF_MONTH);
						i++;
						isFirstOne=false;
					}
					
					if (DebugCorePlugin.isCreateSeriesPAInit){
						Display.getDefault().asyncExec(new Runnable(){
						
							public void run(){
								seriesPAInitPB.setSelection(endYear);
								seriesPAInitPB.setVisible(true);
							}
						});
					}
				}
			};
			thread.start();
		}

		
		public void createPAInit(int paStartYear, int paStartMonth, int paStartDay, int startYear, int startMonth, int startDay, int intervalMonths, boolean isFirstOne, String initFile){
			File initDSSFile=new File(initFile);
			if (initFile !=null){
				if (!initDSSFile.isAbsolute()){
					initFile=FileProcess.procRelativePath(initFile, configuration);
					initDSSFile=new File(initFile);
				}
				String paInitFileName = createPAInitFileName(initFile, paStartYear, paStartMonth, paStartDay);
				if (initDSSFile !=null){
					File newInitDSSFile = new File(paInitFileName);
					try {
						FileUtils.copyFile(initDSSFile, newInitDSSFile);
						createInitData(paInitFileName, paStartYear, paStartMonth, paStartDay, startYear, startMonth, startDay, intervalMonths, isFirstOne);
					} catch (IOException e) {
						WPPException.handleException(e);
					}
				}
			}
		}
		
		public String createPAInitFileName(String initFile, int paStartYear, int paStartMonth, int paStartDay){
			File file = new File(initFile);
			String fn = file.getName();
			String fp = file.getParent();
			String prefix="\\pa_"+paStartYear+TimeOperation.getModMonthText(paStartMonth)+TimeOperation.getModDayhText(paStartDay)+"_";
			return fp+prefix+fn;
		}
		
		public void createInitData(String paInitFileName, int paStartYear, int paStartMonth, int paStartDay, int startYear, int startMonth, int startDay, int intervalMonths, boolean isFirstOne){
			HecDss paInitDss;
			try {
				paInitDss=HecDss.open(paInitFileName);
			} catch (Exception e) {
				WPPException.handleException(e);
				return;
			}
			Vector<String> paPathList = paInitDss.getPathnameList();
			Collections.sort(paPathList, Collections.reverseOrder());
			int size = paPathList.size();
			if (isFirstOne){
				/*
				int sYear=startYear;
				for (int i=0; i<size; i++){
					String path = paPathList.get(i);
					try {
						TimeSeriesContainer firstDc = (TimeSeriesContainer)paInitDss.get(path);
						long st=firstDc.startTime;
						long stms=st*60000;
						long l1970=1440l * 25568l * 60000l;
						long ststd=stms-l1970;
						int sYear1=new Date(ststd).getYear()+1900;
						if (sYear1<sYear){
							sYear=sYear1;
						}
					} catch (Exception e1) {
						WPPException.handleException(e1);
					}
				}
				startTime=TimeOperation.createStartTime(sYear, 1, 1, "1DAY");
				endTime=TimeOperation.createEndTime1(startYear, startMonth, startDay, "1DAY");
				*/
			}else{
				//paInitDss.setTimeWindow(startTime, endTime);
				for (int i=0; i<size; i++){
					String path = paPathList.get(i);
					try {
						TimeSeriesContainer dc = (TimeSeriesContainer)paInitDss.get(path);
						TimeSeriesMath tm = new TimeSeriesMath(dc);
						//TimeSeriesMath tm1 = (TimeSeriesMath) tm.extractTimeSeriesDataForTimeSpecification("YEAR", dsy+"-"+paStartYear, true, 0, false);
						HecMath newTm = tm.shiftInTime(intervalMonths+"MON");
						paInitDss.write(newTm);
					} catch (Exception e) {
						WPPException.handleException(e);
					}
				}
			}
			paInitDss.close();
		}
}
