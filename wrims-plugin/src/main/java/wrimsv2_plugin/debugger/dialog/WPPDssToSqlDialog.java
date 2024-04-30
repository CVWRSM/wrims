package wrimsv2_plugin.debugger.dialog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCConnectionProfileConstants;
import org.eclipse.debug.core.ILaunchConfiguration;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.tools.Encryption;
import wrimsv2_plugin.tools.FileProcess;


public class WPPDssToSqlDialog extends Dialog {
	private Text fileText;
	private String batchFilePath = "DssToSQLDatabase.bat";
	private String databaseURL="none";
	private String sqlGroup="calsim";
	private final String userDefault="root";
	private final String passDefault="none";
	private String ovOption="0";
	private String ovFile;
	private ILaunchConfiguration configuration;
	
	public WPPDssToSqlDialog(Shell parentShell, String databaseURL, String sqlGroup, String ovOption, String ovFile, ILaunchConfiguration configuration) {
		super(parentShell, SWT.MIN|SWT.RESIZE|SWT.APPLICATION_MODAL);
		this.databaseURL=databaseURL;
		this.sqlGroup=sqlGroup;
		this.ovOption=ovOption;
		this.ovFile=ovFile;
		this.configuration=configuration;
		setText("Dss to SQL Database");
	}

	public void openDialog(){
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
		label1.setText("Please select dss info file for conversion:");
		
		Composite fileSelection = new Composite(shell, SWT.NONE);
		GridLayout layout = new GridLayout(15, true);
		fileSelection.setLayout(layout);
		fileText = new Text(fileSelection, SWT.SINGLE | SWT.BORDER);
		GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
		gd1.horizontalSpan = 12;
		fileText.setLayoutData(gd1);
		fileText.setText("");
		
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
						dlg.setFilterNames(new String[]{"All Files (*.*)"});
						dlg.setFilterExtensions(new String[]{"*.*"});
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
		String dssInfoFilePath = fileText.getText();
		File dssInfoFile=new File(dssInfoFilePath );
		if (dssInfoFile.exists()){
			String profileFilePath=generateProfileFile(dssInfoFilePath);
			generateBatchFile(profileFilePath);
			executeBatchFile();
		}
		shell.close();
	}
	
	public String generateProfileFile(String dssInfoFilePath){
		int index=dssInfoFilePath.lastIndexOf(".");
		String profileFilePath = dssInfoFilePath.substring(0, index)+".dpf";
		try {
			FileWriter fw = new FileWriter(profileFilePath);
			PrintWriter out = new PrintWriter(fw);
			generateDatabaseUserInfo(out);
			out.println(databaseURL);
			out.println(sqlGroup);
			out.println(dssInfoFilePath);
			if (ovFile.trim().equals("")){
				out.println("0");
				out.println(".");
			}else if (new File(ovFile).isAbsolute()){
				out.println(ovOption);
				out.println(ovFile);
			}else{
				out.println(ovOption);
				out.println(FileProcess.procRelativePath(ovFile, configuration));
			}
			out.close();
			fw.close();
		} catch (IOException e) {
			WPPException.handleException(e);
		}
		return profileFilePath;
	}
	
	public void generateDatabaseUserInfo(PrintWriter out){
		IConnectionProfile[] profiles = ProfileManager.getInstance().getProfiles();
		int i=0;
		boolean isProfileCreated=false;
		while (i<profiles.length && !isProfileCreated){
			Properties baseProperties = profiles[i].getBaseProperties();
			String urlProperty=baseProperties.getProperty(IJDBCConnectionProfileConstants.URL_PROP_ID);
			if (urlProperty.equalsIgnoreCase(databaseURL)){
				String username=baseProperties.getProperty(IJDBCConnectionProfileConstants.USERNAME_PROP_ID);
				String password=baseProperties.getProperty(IJDBCConnectionProfileConstants.PASSWORD_PROP_ID);
				out.println(Encryption.procEncryption(username));
				out.println(Encryption.procEncryption(password));
				isProfileCreated=true;
			}
			i++;
		}
		if (!isProfileCreated){
			out.println(Encryption.procEncryption(userDefault));
			out.println(Encryption.procEncryption(passDefault));
		}
	}
	
	
	public void generateBatchFile(String profileFilePath){
		try {
			FileWriter fw = new FileWriter(batchFilePath);
			PrintWriter out = new PrintWriter(fw);
			out.println("@echo off");
			out.println("jre\\bin\\java -Xmx4096m -Xss1024K -XX:+CreateMinidumpOnCrash -Duser.timezone=Etc/GMT+8 -Djava.library.path=\"lib\" -cp \"lib\\*\" wrimsv2.sql.DssToSQLDatabase -dss_sql=" + profileFilePath);
			out.close();
			fw.close();
		} catch (IOException e) {
			WPPException.handleException(e);;
		}
	}
	
	public void executeBatchFile(){
		try {
			Process process = Runtime.getRuntime().exec("cmd /c start " + batchFilePath);
		} catch (IOException e) {
			WPPException.handleException(e);;
		}
	}
}
