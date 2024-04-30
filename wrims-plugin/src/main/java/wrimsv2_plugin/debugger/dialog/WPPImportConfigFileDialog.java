package wrimsv2_plugin.debugger.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.exception.WPPException;


public class WPPImportConfigFileDialog extends Dialog {
	private Text fileText;
	private	String fileName="";
	private Map<String, String> configMap=new HashMap<String, String>();
	private ArrayList<String> configList=new ArrayList<String>();
	private Table configTable;
	
	public WPPImportConfigFileDialog(Shell parentShell) {
		super(parentShell, SWT.MIN|SWT.RESIZE);
		setText("Import Configuration File");
	}

	public void openDialog(Table configTable, ArrayList<String> configList){
		this.configTable=configTable;
		this.configList=configList;
		Shell shell=new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.setSize(600, 200);
		shell.setLocation(450, 300);
		//shell.pack();
		shell.open();
	}

	protected void createContents(final Shell shell) {
		FillLayout fl = new FillLayout(SWT.VERTICAL);
		shell.setLayout(fl);
		fl.marginWidth=10;
		fl.marginHeight=15;
		
		Label label1=new Label(shell, SWT.NONE);
		label1.setText("Please select a zip file to load:");
		
		Composite fileSelection = new Composite(shell, SWT.NONE);
		GridLayout layout = new GridLayout(15, true);
		fileSelection.setLayout(layout);
		fileText = new Text(fileSelection, SWT.SINGLE | SWT.BORDER);
		GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
		gd1.horizontalSpan = 12;
		fileText.setLayoutData(gd1);
		fileText.setText(fileName);
		
		Button browserButton = new Button(fileSelection, SWT.PUSH);
		browserButton.setText("Browser");
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		gd2.horizontalSpan = 3;
		browserButton.setLayoutData(gd2);
		browserButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						Shell shell=workbench.getActiveWorkbenchWindow().getShell();
						FileDialog dlg=new FileDialog(shell, SWT.OPEN);
						dlg.setFilterNames(new String[]{"Config Files (*.config)", "All Files (*.*)"});
						dlg.setFilterExtensions(new String[]{"*.config", "*.*"});
						dlg.setFileName(fileText.getText());
						String file=dlg.open();
						if (file !=null){
							fileText.setText(file);
						}
					}
				});
			}
		});
	
		Composite okCancel=new Composite(shell, SWT.NONE);
		okCancel.setLayout(layout);
		Button ok = new Button(okCancel, SWT.PUSH);
		ok.setText("OK");
		GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);
		gd3.horizontalSpan = 2;
		ok.setLayoutData(gd3);
		ok.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				okPressed(shell);
			}
		});
		
		Button cancel = new Button(okCancel, SWT.PUSH);
		cancel.setText("Cancel");
		GridData gd4 = new GridData(GridData.FILL_HORIZONTAL);
		gd4.horizontalSpan = 2;
		cancel.setLayoutData(gd4);
		cancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				shell.close();
			}
		});
		
		shell.setDefaultButton(ok);
	}
	
	public void okPressed(Shell shell){
		fileName=fileText.getText();
		checkConfigFile();
		loadConfigs();
		shell.close();
	}
	
	public void loadConfigs(){
		configTable.removeAll();
		for (int i=0; i<configList.size(); i++){
			String config=configList.get(i);
			Set<String> keys = configMap.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()){
				String cfg=it.next();
				if (config.equalsIgnoreCase(cfg)){
					String value=configMap.get(cfg);
					TableItem ti=new TableItem(configTable, SWT.NONE);
					ti.setText(new String[]{config, value});
				}
			}
		}
	}
	
	public void checkConfigFile(){
		String configFilePath = fileText.getText();
		File configFile = new File(configFilePath);

		try {

			Scanner scanner = new Scanner(configFile);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				line = line.trim();
				line = line.replace('\t', ' ');
				//System.out.println(line);
				if (line.indexOf("#") > -1) {
					line = line.substring(0, line.indexOf("#"));
					line = line.trim();
				}
				if (line.indexOf(" ") < 0) continue;
				if (line.lastIndexOf(" ") + 1 >= line.length()) continue;
				if (line.length() < 5) continue;
				
				//System.out.println(line);

				String key = line.substring(0, line.indexOf(" "));

				String value = line.substring(key.length(), line.length());

				value = value.trim();
				value = value + " ";
				if (value.startsWith("\"")) {
					value = value.substring(1, value.lastIndexOf("\""));
					value = value.replace("\"", "");
				}
				else {
					value = value.substring(0, value.indexOf(" "));
					value = value.replace("\"", "");
				}
				
				// break at the line "End Config"
				if (key.equalsIgnoreCase("end") & value.equalsIgnoreCase("config") ) break;
			
				configMap.put(key, value);
			}

		}
		catch (Exception e) {
			WPPException.handleException(e);
		}
	}
}
