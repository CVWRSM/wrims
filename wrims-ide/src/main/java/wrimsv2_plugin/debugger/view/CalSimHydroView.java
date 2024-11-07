package wrimsv2_plugin.debugger.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.debug.ui.AbstractDebugView;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import wrimsv2_plugin.debugger.exception.WPPException;

public class CalSimHydroView extends ViewPart implements ISelectionListener{
	
	private Composite area;
	private Button rfro;
	private Button idc;
	private Button rice;
	private Button refuge;
	private Button hi;
	private Button hdu;

	public CalSimHydroView(){
		super();
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createPartControl(Composite parent) {
		area = new Composite(parent, SWT.NONE);
		area.setLayout(new GridLayout(6, true));
		
		Label label = new Label(area, SWT.NONE);
		label.setText("Please select the modules to run:");
		GridData gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 6;//TODO
		label.setLayoutData(gd);
				
		rfro=new Button(area, SWT.CHECK);
		rfro.setText("Rainfall Runoffs");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 6;//TODO
		rfro.setLayoutData(gd);
		rfro.setSelection(true);
		
		rice=new Button(area, SWT.CHECK);
		rice.setText("Rice");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 6;//TODO
		rice.setLayoutData(gd);
		rice.setSelection(true);
		
		idc=new Button(area, SWT.CHECK);
		idc.setText("Other Corps");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 6;//TODO
		idc.setLayoutData(gd);
		idc.setSelection(true);
		
		refuge=new Button(area, SWT.CHECK);
		refuge.setText("Wet Land Refuge");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 6;//TODO
		refuge.setLayoutData(gd);
		refuge.setSelection(true);
		
		hi=new Button(area, SWT.CHECK);
		hi.setText("Hydrology Integration");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 6;//TODO
		hi.setLayoutData(gd);
		hi.setSelection(true);
		
		hdu=new Button(area, SWT.CHECK);
		hdu.setText("Hydrology Diagnose Utility");
		gd = new GridData(GridData.END);
		gd.horizontalSpan = 6;//TODO
		hdu.setLayoutData(gd);
		hdu.setSelection(true);
		
		Button run = new Button(area, SWT.NONE);
		run.setText("Run");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 6;//TODO
		run.setLayoutData(gd);
		run.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (generateCalSimHydroBatch()){
					runCalSimHydroSelected();
				}else{
					WPPException.handleException(new Exception("CalSimHydro batch file generation failed."));
				}
			}
		});
	}
	
	public boolean generateCalSimHydroBatch(){
		File f=new File("CalSimHydroDefault\\bin\\RunSelected.bat");
		PrintWriter out;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
			out.println("cd ..");
			if (rfro.getSelection()) out.println(".\\bin\\RFRO.exe              .\\1a_RainfallRunoff\\main.dat >RFRO.log");
			if (idc.getSelection()) out.println(".\\bin\\IDCv2_1.exe           .\\2a_IDC\\main.dat            >IDC.log");
			if (rice.getSelection()) {
				out.println(".\\bin\\RiceModel_Avg.exe     .\\3a_Rice\\main.dat          >Rice.log");
				out.println(".\\bin\\RiceModel_Avg.exe     .\\3b_RiceAvg\\main.dat       >RiceAvg.log");
			}
			if (refuge.getSelection()) out.println(".\\bin\\RefugeModel.exe       .\\4_Refuge\\main.dat         >Refuge.log");
			if (hi.getSelection()) out.println(".\\bin\\HydroIntegration.exe  .\\5_HydroIntegration\\main2017.dat      >HydroIntegration.log");
			if (hdu.getSelection()) out.println(".\\bin\\HydroDU.exe           .\\6_HydroDiagnoseUtility\\main.dat      >HydroDU.log");
			out.println("pause");
			out.println("exit");
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public void runCalSimHydroSelected(){
		String chd="CalSimHydroDefault\\bin";
		String che="RunSelected.bat";
		try {
		    String[] commands = {"cmd.exe", "/c", "start", "/w", che};
		    ProcessBuilder builder = new ProcessBuilder(commands);
		    builder.directory(new File(chd));
		    Process process = builder.start();
		} catch (IOException e) {
			WPPException.handleException(e);
		}
	}
	
	@Override
	public void setFocus() {
		// TODO: DWR Review Change
		//This was causing runtime exceptions on load.
		//Tracking under github issue: https://github.com/CentralValleyModeling/wrims/issues/154
		//getSite().getPart().setFocus();
		try
		{
			area.setFocus();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
