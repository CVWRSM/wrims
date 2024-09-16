package wrimsv2_plugin.debugger.dialog;

import java.util.HashMap;

import org.eclipse.debug.core.DebugException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.menuitem.EnableMenus;
import wrimsv2_plugin.debugger.toolbaritem.EnableButtons;
import wrimsv2_plugin.debugger.toolbaritem.HandlePauseResumeButton;

public class WPPReSimDialog extends Dialog {
	Combo year;
	Combo month;
	Combo day;
	Combo cycle;
	
	
	public WPPReSimDialog(Shell parent) {
		super(parent, SWT.MIN|SWT.RESIZE);
		setText("Re-Simulation");
	}

	public void openDialog(){
		Shell shell=new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.setSize(450, 280);
		shell.setLocation(450, 300);
		//shell.pack();
		shell.open();
	}

	 protected void createContents(final Shell shell) {
		RowLayout layout=new RowLayout(SWT.VERTICAL);
		layout.marginHeight=10;
		layout.marginWidth=20;
		shell.setLayout(layout);
		
		Composite line1=new Composite(shell, SWT.NONE);
		layout=new RowLayout(SWT.HORIZONTAL);
		layout.justify=true;
		layout.pack=true;
		line1.setLayout(layout);
		Label label1=new Label(line1, SWT.NONE);
		label1.setText("Re-simulate from:");
		
		Composite line2=new Composite(shell, SWT.NONE);
		line2.setLayout(layout);
		final Button but1=new Button(line2, SWT.RADIO);
		but1.setText("1st cycle of year");
		year=new Combo(line2, SWT.BORDER);
		int suspendedYear=DebugCorePlugin.suspendedYear;
		fillCombo(year, DebugCorePlugin.startYear, suspendedYear);
		year.setText(String.valueOf(suspendedYear));
		Label label3=new Label(line2, SWT.NONE);
		label3.setText("month");
		month=new Combo(line2, SWT.BORDER);
		int suspendedMonth=DebugCorePlugin.suspendedMonth;
		fillCombo(month, 1, 12);
		month.setText(String.valueOf(suspendedMonth));
		Label label4=new Label(line2, SWT.NONE);
		label4.setText("day");
		day=new Combo(line2,SWT.BORDER);
		int suspendedDay=DebugCorePlugin.suspendedDay;
		fillCombo(day, 1, 31);
		day.setText(String.valueOf(suspendedDay));
		
		Composite line3=new Composite(shell, SWT.NONE);
		line3.setLayout(layout);
		final Button but2=new Button(line3, SWT.RADIO);
		but2.setText("Cycle");
		cycle=new Combo(line3, SWT.BORDER);
		int suspendedCycle=DebugCorePlugin.suspendedCycle;
		fillCombo(cycle, 1, suspendedCycle);
		cycle.setText(String.valueOf(suspendedCycle));
		Label label2=new Label(line3, SWT.NONE);
		label2.setText("of current step");
		
		final Button but3=new Button(shell, SWT.CHECK);
		but3.setText("Re-compile wresl code");
		
		final Button but4=new Button(shell, SWT.CHECK);
		but4.setText("Re-read data from SV file");
		
		final Button but5=new Button(shell, SWT.CHECK);
		but5.setText("Re-load lookup table");
		
		Composite line7=new Composite(shell, SWT.NONE);
		line7.setLayout(layout);
		Button ok = new Button(line7, SWT.PUSH);
		ok.setText("OK");
		ok.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				if (but1.getSelection()){
					try {
						String compile;
						if (but3.getSelection()){
							compile="recompile";
						}else{
							compile="notrecompile";
						}
						String loadSV;
						if (but4.getSelection()){
							loadSV="loadsv";
						}else{
							loadSV="notloadsv";
						}
						String loadTable;
						if (but5.getSelection()){
							loadTable="loadtable";
						}else{
							loadTable="notloadtable";
						}
						DebugCorePlugin.target.resimDate(compile+":"+loadSV+":"+year.getText()+":"+month.getText()+":"+day.getText()+":"+loadTable);
						enableRunMenu();
					} catch (DebugException e) {
						WPPException.handleException(e);
					}
				}else if (but2.getSelection()){
					try {
						String loadSV;
						if (but4.getSelection()){
							loadSV="loadsv";
						}else{
							loadSV="notloadsv";
						}
						String loadTable;
						if (but5.getSelection()){
							loadTable="loadtable";
						}else{
							loadTable="notloadtable";
						}
						DebugCorePlugin.target.resimCycle(loadSV+":"+cycle.getText()+":"+loadTable);
						enableRunMenu();
					} catch (DebugException e) {
						WPPException.handleException(e);
					}
				}
				shell.close();
			}
		});
		
		Button cancel = new Button(line7, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				shell.close();
			}
		});
		
		but1.addMouseListener(new MouseListener(){

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseUp(MouseEvent e) {
				but2.setSelection(false);
				but3.setEnabled(true);
			}
			
		});
		
		but2.addMouseListener(new MouseListener(){

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseUp(MouseEvent e) {
				but1.setSelection(false);
				but3.setSelection(false);
				but3.setEnabled(false);
			}
			
		});
		
		shell.setDefaultButton(ok);
	 }
	
	public void fillCombo(Combo combo, int start, int end){
		combo.removeAll();
		for (int i=start; i<=end; i++){
			combo.add(String.valueOf(i));
		}
	}
	
	
	public void enableRunMenu(){
		HashMap<String, Boolean> enableMenuMap=new HashMap<String, Boolean>();
		enableMenuMap.put(DebugCorePlugin.ID_WPP_TERMINATEMENU, true);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_PAUSEMENU, true);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_SUSPENDMENU, true);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_RESUMEMENU, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_RESIMMENU, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLE, true);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEP, true);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_SAVETODVFILE, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_SAVETOSVFILE, false);
		new EnableMenus(enableMenuMap);
		HandlePauseResumeButton.procPauseResumeToolbarItem(1);
		HashMap<String, Boolean> enableButtonMap=new HashMap<String, Boolean>();
		enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLEBUTTON, true);
		enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEPBUTTON, true);
		new EnableButtons(enableButtonMap);
	}
}
