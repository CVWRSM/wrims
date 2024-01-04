package gov.ca.dwr.wrims.geoschematic;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.jdiagram.SchematicPluginCore;
//import gov.ca.dwr.jdiagram.dialog.AddTimeWindowDialog;
//import gov.ca.dwr.jdiagram.views.SchematicBase;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.tools.TimeOperation;

public class DateSelector extends WorkbenchWindowControlContribution {

	private MapView mapView;
	private Combo dateList;

	public DateSelector(MapView mapView) {
		this.mapView = mapView;
	}

	@Override
	protected Control createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout glContainer = new GridLayout(1, false);
		glContainer.marginTop = 0;
		glContainer.marginHeight = 0;
		glContainer.marginWidth = 0;
		glContainer.marginBottom = 0;
		container.setLayout(glContainer);
		GridData glReader = new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
				1);
		glReader.widthHint = 140;
		glReader.heightHint = 15;
		dateList = new Combo(container, SWT.BORDER | SWT.READ_ONLY
				| SWT.DROP_DOWN);
		dateList.setLayoutData(glReader);
		setDateCombo(10, 1921, 9, 2009);
		dateList.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				SchematicPluginCore.selIndex = dateList.getSelectionIndex();
				if (SchematicPluginCore.selIndex > 0) {
					SchematicPluginCore.selDate = dateList.getText();
					if (!DebugCorePlugin.isDebugging) {
						//schematicView.refreshValues(0, true);
						mapView.refresh();
					} else {
						if (DebugCorePlugin.target != null) {
							//schematicView.refreshValues(1, true);
							mapView.refresh();
						}
					}
				} else if (SchematicPluginCore.selIndex == 0) {
// TODO: figure out the right way to handle this
//					final IWorkbench workbench = PlatformUI.getWorkbench();
//					workbench.getDisplay().asyncExec(new Runnable() {
//						public void run() {
//							Shell shell = workbench.getActiveWorkbenchWindow()
//									.getShell();
//							AddTimeWindowDialog dialog = new AddTimeWindowDialog(
//									shell, SWT.BORDER | SWT.APPLICATION_MODAL,
//									true, false, false, false, false,
//									"Add Time Window", "Add Time Window");
//							dialog.open(getDateList());
//						}
//					});
				}
			}

		});
		return container;
	}

	public void setDateCombo(int startMonth, int startYear, int endMonth,
			int endYear) {
		dateList.removeAll();
		procTWFile();
		for (int i = 0; i < DssPluginCore._schematicTwSelections.size(); i++) {
			dateList.add(DssPluginCore._schematicTwSelections.get(i));
		}
		int j = startMonth;
		for (int i = startYear; i <= endYear; i++) {
			while (j <= 12) {
				dateList.add(TimeOperation.getMonthText(j) + " " + i);
				if (j >= endMonth && i >= endYear) {
					i = endYear + 1;
					j = 13;
				}
				j = j + 1;
			}
			j = 1;
		}
	}

	public Combo getDateList() {
		return dateList;
	}

	public void procTWFile() {
		try {
			File file = new File(DebugCorePlugin.dataDir,
					SchematicPluginCore.twFile);
			if (!file.exists()) {
				file.createNewFile();
			} else {
				DssPluginCore._schematicTwSelections = new ArrayList<String>();
				DssPluginCore._schematicTwSelections.add("Add...");
				FileInputStream fs = new FileInputStream(file.getAbsolutePath());
				BufferedReader br = new BufferedReader(new InputStreamReader(fs));
				String line;
				while ((line = br.readLine()) != null) {
					DssPluginCore._schematicTwSelections.add(line);
				}
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}