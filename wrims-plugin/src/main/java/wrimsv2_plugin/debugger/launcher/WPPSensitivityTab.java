package wrimsv2_plugin.debugger.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.dialog.WPPDssToSqlDialog;
import wrimsv2_plugin.debugger.exception.WPPException;

public class WPPSensitivityTab extends AbstractLaunchConfigurationTab {

	private Button butSensitivity;
	private Text txtTableName;
	private Text txtNumRuns;
	private Text commentTxt;
	private Label commentLabel1;
	private Label commentLabel2;
	private Label commentLabel3;
	private Label commentLabel4;

	@Override
	public void createControl(Composite parent) {
		Font font = parent.getFont();
		
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 0;
		topLayout.numColumns = 7;
		comp.setLayout(topLayout);
		comp.setFont(font);
		
		createVerticalSpacer(comp, 3);
		
		Label sensitivityLabel = new Label(comp, SWT.NONE);
		sensitivityLabel.setText("Sensitivity Run:");
		GridData gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=6;
		sensitivityLabel.setLayoutData(gd);
		sensitivityLabel.setFont(font);
		
		butSensitivity = new Button(comp, SWT.CHECK);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		butSensitivity.setLayoutData(gd);
		butSensitivity.setFont(font);
		butSensitivity.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub	
			}
			
		});
		
		Label tableLabel = new Label(comp, SWT.NONE);
		tableLabel.setText("Sensitivity Index Table Name:");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=4;
		tableLabel.setLayoutData(gd);
		tableLabel.setFont(font);
		
		txtTableName = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		txtTableName.setLayoutData(gd);
		txtTableName.setFont(font);
		txtTableName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
				commentTxt.setText("Add 'define sri {select sri from "+txtTableName.getText()+" where index=1}' to WRESL code  ");
				commentLabel2.setText("sri value will vary from 1 to "+txtNumRuns.getText()+" automatically in the "+txtTableName.getText()+" table for each run  ");
			}
		});
	
		Label numRunsLabel = new Label(comp, SWT.NONE);
		numRunsLabel.setText("Number of Sensitivity Runs:");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=4;
		numRunsLabel.setLayoutData(gd);
		numRunsLabel.setFont(font);
		
		txtNumRuns = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		txtNumRuns.setLayoutData(gd);
		txtNumRuns.setFont(font);
		txtNumRuns.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
				commentLabel2.setText("sri value will vary from 1 to "+txtNumRuns.getText()+" automatically in the "+txtTableName.getText()+" table for each run  ");
			}
		});
		
		commentLabel1 = new Label(comp, SWT.NONE);
		commentLabel1.setText("");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=7;
		commentLabel1.setLayoutData(gd);
		commentLabel1.setFont(font); 
		
		commentTxt = new Text(comp, SWT.READ_ONLY);
		commentTxt.setText("Add 'define sri {select sri from SensitivityIndex where index=1}' to WRESL code  ");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=7;
		commentTxt.setLayoutData(gd);
		commentTxt.setFont(font);
		
		commentLabel2 = new Label(comp, SWT.NONE);
		commentLabel2.setText("sri value will vary from 1 to 1 automatically in the SensitivityIndex table for each run  ");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=7;
		commentLabel2.setLayoutData(gd);
		commentLabel2.setFont(font);
		
		commentLabel3 = new Label(comp, SWT.NONE);
		commentLabel3.setText("Use the value of sri as the index to look up the values of the sensitivity variables");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=7;
		commentLabel3.setLayoutData(gd);
		commentLabel3.setFont(font);
		
		commentLabel4 = new Label(comp, SWT.NONE);
		commentLabel4.setText("The name of the defined variable 'sri' can be flexible");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=7;
		commentLabel4.setLayoutData(gd);
		commentLabel4.setFont(font);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_ISSENSITIVITYRUN, "no");
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_SENSITIVITYINDEXTABLENAME, "SensitivityIndex");
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_NUMSENSITIVITYRUN, "1");
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		String isSensitivityRunString = null;
		String tableName = null;
		String numRuns = null;
		try {
			isSensitivityRunString = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_ISSENSITIVITYRUN, "no");
			if (isSensitivityRunString.equalsIgnoreCase("yes")){
				butSensitivity.setSelection(true);
			}else{
				butSensitivity.setSelection(false);
			}
			
			tableName = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_SENSITIVITYINDEXTABLENAME, "SensitivityIndex");
			txtTableName.setText(tableName);
			
			numRuns = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_NUMSENSITIVITYRUN, "1");
			txtNumRuns.setText(numRuns);
			
			commentLabel1.setText("");
			commentTxt.setText("Add 'define sri {select sri from "+tableName+" where index=1}' to WRESL code  ");
			commentLabel2.setText("sri value will vary from 1 to "+numRuns+" automatically in the "+tableName+" table for each run  ");
			commentLabel3.setText("Use the value of sri as the index to look up the values of the sensitivity variables");
			commentLabel4.setText("The name of the defined variable 'sri' can be flexible");
		} catch (CoreException e) {
			WPPException.handleException(e);
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		boolean isSensitivityRun=butSensitivity.getSelection();
		if (isSensitivityRun){
			configuration.setAttribute(DebugCorePlugin.ATTR_WPP_ISSENSITIVITYRUN, "yes");
		}else{
			configuration.setAttribute(DebugCorePlugin.ATTR_WPP_ISSENSITIVITYRUN, "no");
		}
		
		String tableName=txtTableName.getText();
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_SENSITIVITYINDEXTABLENAME, tableName);
		
		String numRuns=txtNumRuns.getText();
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_NUMSENSITIVITYRUN, numRuns);
	}

	@Override
	public String getName() {
		return "Sensitivity";
	}

}
