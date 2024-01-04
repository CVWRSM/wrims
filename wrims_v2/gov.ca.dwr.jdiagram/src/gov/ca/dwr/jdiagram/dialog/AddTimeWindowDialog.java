package gov.ca.dwr.jdiagram.dialog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.jdiagram.SchematicPluginCore;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;

public class AddTimeWindowDialog extends Dialog {
	//private DateCombo dateCombo;
	private Combo dateList;
	private static String[] _monthSelections = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
		"JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
	
	private Text year1, year2;
	private Combo month1, month2;
	private String newTW="OCT1921 - SEP2015";
	
	public AddTimeWindowDialog(Shell parent, Combo dateList) {
		
		super(parent, SWT.MIN);
		this.dateList=dateList;
		setText("Add Time Window");
		
		// TODO Auto-generated constructor stub
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
		GridLayout layout=new GridLayout();
		layout.numColumns = 5;
		layout.makeColumnsEqualWidth = true;
		layout.marginWidth=20;
		layout.marginHeight=20;
		shell.setLayout(layout);
		
		month1=new Combo(shell, SWT.NONE);
		for (int i=0; i<_monthSelections.length; i++){
			month1.add(_monthSelections[i]);
		}
		month1.select(9);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		month1.setLayoutData(gd);
		
		year1= new Text(shell, SWT.BORDER);
		year1.setToolTipText("Year");
		year1.setText("1921");
		year1.setLayoutData(gd);
		
		Label label1=new Label(shell, SWT.NONE);
		label1.setText("To");
		label1.setLayoutData(gd);
		
		month2=new Combo(shell, SWT.NONE);
		for (int i=0; i<_monthSelections.length; i++){
			month2.add(_monthSelections[i]);
		}
		month2.select(8);
		month2.setLayoutData(gd);
		
		year2= new Text(shell, SWT.BORDER);
		year2.setToolTipText("Year");
		year2.setText("2015");
		year2.setLayoutData(gd);
		
		Button ok = new Button(shell, SWT.PUSH);
		ok.setLayoutData(gd);
		ok.setText("OK");
		ok.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				int index=validateTW();
				if (index==DssPluginCore._schematicTwSelections.size()){
					dateList.add(newTW, index);
					dateList.select(index);
					DssPluginCore._schematicTwSelections.add(index, newTW);
					saveTWFile();
					shell.close();
				}else if (index>=0){
					dateList.select(index);
					shell.close();
				}
			}
		});
		
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setLayoutData(gd);
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				shell.close();
			}
		});
		
		shell.setDefaultButton(ok);
	}
	
	public int validateTW(){
		int startyear=Integer.parseInt(year1.getText());
		int endyear=Integer.parseInt(year2.getText());
		if (startyear>endyear){
			showWarning();
	        return -1;
		}else if (startyear==endyear && month1.getSelectionIndex()>month2.getSelectionIndex()){
			showWarning();
	        return -1;
		}else{
			newTW=month1.getText()+startyear+" - "+month2.getText()+endyear;
			int size=DssPluginCore._schematicTwSelections.size();
			int index=size;
			for (int i=0; i<index; i++){
				if (dateList.getItem(i).equals(newTW)){
					return i;
				}
			}
			return index;
		}
		
	}
	
	public void showWarning(){
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				Shell shell=workbench.getActiveWorkbenchWindow().getShell();
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
				messageBox.setText("Warning");
				messageBox.setMessage("Start date should be earlier than ending date");
				messageBox.open();
			}
		});
	}
	
	public void saveTWFile(){
		try {
			File file = new File(DebugCorePlugin.dataDir, SchematicPluginCore.twFile);
			if (!file.exists()){
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsolutePath());
			PrintWriter out = new PrintWriter(fw);
			int size = DssPluginCore._schematicTwSelections.size();
			if (size>1){
				for (int i=1; i<size; i++){
					out.println(DssPluginCore._schematicTwSelections.get(i));
				}
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
