/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Bjorn Freeman-Benson - initial API and implementation
 *******************************************************************************/
package wrimsv2_plugin.debugger.launcher;


import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;
import org.eclipse.ui.part.PluginTransfer;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.dialog.WPPLoadStudyDssDialog;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.tools.FileProcess;
import wrimsv2_plugin.tools.TimeOperation;

/**
 * Tab to specify the WPP program to run/debug.
 */
public class WPPMainTab extends AbstractLaunchConfigurationTab {
	
	private Text studyText;
	private Text authorText;
	private Text dateText;
	private Text descriptionText;
	public Text fMainFileText;
	private Button fMainFileButton;
	public Text fDvarFileText;
	private Button fDvarFileButton;
	public Text fSvarFileText;
	private Button fSvarFileButton;
	public Text fInitFileText;
	private Button fInitFileButton;
	public Text groundWaterFolderText;
	private Button groundWaterFolderButton;
	public Text aPartText;
	public Text svFPartText;
	public Text initFPartText;
	public Combo timeStepCombo;
	public Combo startYearCombo;
	public Combo startMonthCombo;
	public Combo startDayCombo;
	public Combo endYearCombo;
	public Combo endMonthCombo;
	public Combo endDayCombo;
	private Button pa;
	private Label msl;
	private Combo ms;
	private Button wsidigen;
	private Button ch;
	private Text calsimHydroText;
	private Button calsimHydroButton;
	
	private ILaunchConfiguration launchConfig;
	private String externalPath="";
	
	private DayItemListener sdl=new DayItemListener(1);
	private DayItemListener edl=new DayItemListener(2);
	private MYItemListener sml=new MYItemListener(1);
	private MYItemListener eml=new MYItemListener(2);
	private MYItemListener syl=new MYItemListener(1);
	private MYItemListener eyl=new MYItemListener(2);
	private DropTarget mainFileDt;
	private DropTarget dvarFileDt;
	private DropTarget svarFileDt;
	private DropTarget initFileDt;
	private DropTarget groundWaterFolderDt;	
	private DropTarget calsimHydroDt;
	
	public static String[] months = { "oct", "nov", "dec", "jan", "feb", "mar",
		"apr", "may", "jun", "jul", "aug", "sep" };
	public static String[] timeSteps = { "1MON", "1DAY" };
			
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
		
		Label studyLabel = new Label(comp, SWT.NONE);
		studyLabel.setText("&Study Name:");
		GridData gd = new GridData(GridData.BEGINNING);
		studyLabel.setLayoutData(gd);
		studyLabel.setFont(font);
		
		studyText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 6;
		studyText.setLayoutData(gd);
		studyText.setFont(font);
		studyText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		Label authorLabel = new Label(comp, SWT.NONE);
		authorLabel.setText("&Author:");
		gd = new GridData(GridData.BEGINNING);
		studyLabel.setLayoutData(gd);
		studyLabel.setFont(font);
		
		authorText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 6;
		authorText.setLayoutData(gd);
		authorText.setFont(font);
		authorText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		Label dateLabel = new Label(comp, SWT.NONE);
		dateLabel.setText("&Date:");
		gd = new GridData(GridData.BEGINNING);
		studyLabel.setLayoutData(gd);
		studyLabel.setFont(font);
		
		dateText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 6;
		dateText.setLayoutData(gd);
		dateText.setFont(font);
		dateText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		
		Label descriptionLabel = new Label(comp, SWT.NONE);
		descriptionLabel.setText("&Description:");
		gd = new GridData(GridData.BEGINNING);
		studyLabel.setLayoutData(gd);
		studyLabel.setFont(font);
		
		descriptionText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 6;
		descriptionText.setLayoutData(gd);
		descriptionText.setFont(font);
		descriptionText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		Label mainFileLabel = new Label(comp, SWT.NONE);
		mainFileLabel.setText("&Main WRESL File:");
		gd = new GridData(GridData.BEGINNING);
		mainFileLabel.setLayoutData(gd);
		mainFileLabel.setFont(font);
		
		fMainFileText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 5;
		fMainFileText.setLayoutData(gd);
		fMainFileText.setFont(font);
		fMainFileText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		mainFileDt = new DropTarget(fMainFileText, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
		mainFileDt.setTransfer(new Transfer[] { FileTransfer.getInstance()});
		mainFileDt.addDropListener(new DropTargetAdapter() {
            public void drop(DropTargetEvent event) {
                String fileList[] = null;
                FileTransfer ft = FileTransfer.getInstance();
                if (ft.isSupportedType(event.currentDataType)) {
                    fileList = (String[]) event.data;
                    fMainFileText.setText(fileList[0]);
                }
            }
        });
		
		fMainFileButton = createPushButton(comp, "&Browse", null); //$NON-NLS-1$
		fMainFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseFiles(fMainFileText);
			}
		});
		
		Label dvarFileLabel = new Label(comp, SWT.NONE);
		dvarFileLabel.setText("&Dvar File:");
		gd = new GridData(GridData.BEGINNING);
		dvarFileLabel.setLayoutData(gd);
		dvarFileLabel.setFont(font);
		
		fDvarFileText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 5;
		fDvarFileText.setLayoutData(gd);
		fDvarFileText.setFont(font);
		fDvarFileText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		dvarFileDt = new DropTarget(fDvarFileText, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
		dvarFileDt.setTransfer(new Transfer[] { FileTransfer.getInstance()});
		dvarFileDt.addDropListener(new DropTargetAdapter() {
            public void drop(DropTargetEvent event) {
                String fileList[] = null;
                FileTransfer ft = FileTransfer.getInstance();
                if (ft.isSupportedType(event.currentDataType)) {
                    fileList = (String[]) event.data;
                    fDvarFileText.setText(fileList[0]);
                }
            }
        });
		
		fDvarFileButton = createPushButton(comp, "&Browse", null); //$NON-NLS-1$
		fDvarFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseFiles(fDvarFileText);
			}
		});
		
		Label svarFileLabel = new Label(comp, SWT.NONE);
		svarFileLabel.setText("&Svar File:");
		gd = new GridData(GridData.BEGINNING);
		svarFileLabel.setLayoutData(gd);
		svarFileLabel.setFont(font);
		
		fSvarFileText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 5;
		fSvarFileText.setLayoutData(gd);
		fSvarFileText.setFont(font);
		fSvarFileText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		svarFileDt = new DropTarget(fSvarFileText, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
		svarFileDt.setTransfer(new Transfer[] { FileTransfer.getInstance()});
		svarFileDt.addDropListener(new DropTargetAdapter() {
            public void drop(DropTargetEvent event) {
                String fileList[] = null;
                FileTransfer ft = FileTransfer.getInstance();
                if (ft.isSupportedType(event.currentDataType)) {
                    fileList = (String[]) event.data;
                    fSvarFileText.setText(fileList[0]);
                }
            }
        });
		
		fSvarFileButton = createPushButton(comp, "&Browse", null); //$NON-NLS-1$
		fSvarFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseFiles(fSvarFileText);
			}
		});
		
		Label initFileLabel = new Label(comp, SWT.NONE);
		initFileLabel.setText("&Init File:");
		gd = new GridData(GridData.BEGINNING);
		initFileLabel.setLayoutData(gd);
		initFileLabel.setFont(font);
		
		fInitFileText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 5;
		fInitFileText.setLayoutData(gd);
		fInitFileText.setFont(font);
		fInitFileText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		initFileDt = new DropTarget(fInitFileText, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
		initFileDt.setTransfer(new Transfer[] { FileTransfer.getInstance()});
		initFileDt.addDropListener(new DropTargetAdapter() {
            public void drop(DropTargetEvent event) {
                String fileList[] = null;
                FileTransfer ft = FileTransfer.getInstance();
                if (ft.isSupportedType(event.currentDataType)) {
                    fileList = (String[]) event.data;
                    fInitFileText.setText(fileList[0]);
                }
            }
        });
		
		fInitFileButton = createPushButton(comp, "&Browse", null); //$NON-NLS-1$
		fInitFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseFiles(fInitFileText);
			}
		});
		
		Label groundwaterFolderLabel = new Label(comp, SWT.NONE);
		groundwaterFolderLabel.setText("&Groundwater Folder:");
		gd = new GridData(GridData.BEGINNING);
		groundwaterFolderLabel.setLayoutData(gd);
		groundwaterFolderLabel.setFont(font);
		
		groundWaterFolderText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 5;
		groundWaterFolderText.setLayoutData(gd);
		groundWaterFolderText.setFont(font);
		groundWaterFolderText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		groundWaterFolderDt = new DropTarget(groundWaterFolderText, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
		groundWaterFolderDt.setTransfer(new Transfer[] { FileTransfer.getInstance()});
		groundWaterFolderDt.addDropListener(new DropTargetAdapter() {
            public void drop(DropTargetEvent event) {
                String fileList[] = null;
                FileTransfer ft = FileTransfer.getInstance();
                if (ft.isSupportedType(event.currentDataType)) {
                    fileList = (String[]) event.data;
                    groundWaterFolderText.setText(fileList[0]);
                }
            }
        });
		
		groundWaterFolderButton = createPushButton(comp, "&Browse", null); //$NON-NLS-1$
		groundWaterFolderButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseFolders(groundWaterFolderText);
			}
		});
		
		Label aPart = new Label(comp, SWT.NONE);
		aPart.setText("&A-Part:");
		gd = new GridData(GridData.BEGINNING);
		aPart.setLayoutData(gd);
		aPart.setFont(font);
		
		aPartText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 6;
		aPartText.setLayoutData(gd);
		aPartText.setFont(font);
		aPartText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		Label svFPart = new Label(comp, SWT.NONE);
		svFPart.setText("&SV F-Part:");
		gd = new GridData(GridData.BEGINNING);
		svFPart.setLayoutData(gd);
		svFPart.setFont(font);
		
		svFPartText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 6;
		svFPartText.setLayoutData(gd);
		svFPartText.setFont(font);
		svFPartText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		Label initFPart = new Label(comp, SWT.NONE);
		initFPart.setText("&Init F-Part:");
		gd = new GridData(GridData.BEGINNING);
		initFPart.setLayoutData(gd);
		initFPart.setFont(font);
		
		initFPartText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 6;
		initFPartText.setLayoutData(gd);
		initFPartText.setFont(font);
		initFPartText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		Label timeStep = new Label(comp, SWT.NONE);
		timeStep.setText("&Time Step:");
		gd = new GridData(GridData.BEGINNING);
		timeStep.setLayoutData(gd);
		timeStep.setFont(font);
				
		timeStepCombo=new Combo(comp, SWT.READ_ONLY);
		timeStepCombo.setItems(timeSteps);
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 6;
		timeStepCombo.setLayoutData(gd);
		timeStepCombo.setFont(font);
		timeStepCombo.select(0);
		timeStepCombo.addModifyListener(new TSItemListener());
		
		Label startDate = new Label(comp, SWT.NONE);
		startDate.setText("&Start Date:");
		gd = new GridData(GridData.BEGINNING);
		startDate.setLayoutData(gd);
		startDate.setFont(font);
		
		startYearCombo=new Combo(comp, SWT.READ_ONLY);
		for (int i=1900; i<=2100; i++){
			startYearCombo.add(String.valueOf(i));
		}
		gd = new GridData(GridData.BEGINNING);
		startYearCombo.setLayoutData(gd);
		startYearCombo.setFont(font);
		startYearCombo.select(21);
		startYearCombo.addModifyListener(syl);
		
		Label year = new Label(comp, SWT.NONE);
		year.setText("year");
		gd = new GridData(GridData.BEGINNING);
		year.setLayoutData(gd);
		year.setFont(font);
		
		startMonthCombo=new Combo(comp, SWT.READ_ONLY);
		startMonthCombo.setItems(months);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		startMonthCombo.setLayoutData(gd);
		startMonthCombo.setFont(font);
		startMonthCombo.select(0);
		startMonthCombo.addModifyListener(sml);
		
		Label month = new Label(comp, SWT.NONE);
		month.setText("month");
		gd = new GridData(GridData.BEGINNING);
		month.setLayoutData(gd);
		month.setFont(font);
		
		startDayCombo=new Combo(comp, SWT.READ_ONLY);
		for (int i=1; i<=31; i++){
			startDayCombo.add(String.valueOf(i));
		}
		gd = new GridData(GridData.FILL_HORIZONTAL);
		startDayCombo.setLayoutData(gd);
		startDayCombo.setFont(font);
		startDayCombo.select(30);
		startDayCombo.addModifyListener(sdl);
		//startDayCombo.setEnabled(false);
		
		Label day = new Label(comp, SWT.NONE);
		day.setText("day");
		gd = new GridData(GridData.BEGINNING);
		day.setLayoutData(gd);
		day.setFont(font);
		
		Label endDate = new Label(comp, SWT.NONE);
		endDate.setText("&End Date:");
		gd = new GridData(GridData.BEGINNING);
		endDate.setLayoutData(gd);
		endDate.setFont(font);
		
		endYearCombo=new Combo(comp, SWT.READ_ONLY);
		for (int i=1900; i<=2100; i++){
			endYearCombo.add(String.valueOf(i));
		}
		gd = new GridData(GridData.BEGINNING);
		endYearCombo.setLayoutData(gd);
		endYearCombo.setFont(font);
		endYearCombo.select(103);
		endYearCombo.addModifyListener(eyl);
		
		Label year1 = new Label(comp, SWT.NONE);
		year1.setText("year");
		gd = new GridData(GridData.BEGINNING);
		year1.setLayoutData(gd);
		year1.setFont(font);
		
		endMonthCombo=new Combo(comp, SWT.READ_ONLY);
		endMonthCombo.setItems(months);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		endMonthCombo.setLayoutData(gd);
		endMonthCombo.setFont(font);
		endMonthCombo.select(11);
		endMonthCombo.addModifyListener(eml);
		
		Label month1 = new Label(comp, SWT.NONE);
		month1.setText("month");
		gd = new GridData(GridData.BEGINNING);
		month1.setLayoutData(gd);
		month1.setFont(font);
		
		endDayCombo=new Combo(comp, SWT.READ_ONLY);
		for (int i=1; i<=31; i++){
			endDayCombo.add(String.valueOf(i));
		}
		gd = new GridData(GridData.FILL_HORIZONTAL);
		endDayCombo.setLayoutData(gd);
		endDayCombo.setFont(font);
		endDayCombo.select(29);
		endDayCombo.addModifyListener(edl);
		//endDayCombo.setEnabled(false);
		
		Label day1 = new Label(comp, SWT.NONE);
		day1.setText("day");
		gd = new GridData(GridData.BEGINNING);
		day1.setLayoutData(gd);
		day1.setFont(font);
		
		pa = new Button(comp, SWT.CHECK);
		pa.setText("Position Analysis");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=2;
		pa.setLayoutData(gd);
		pa.setFont(font);
		pa.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
			
		});

		msl = new Label(comp, SWT.NONE);
		msl.setText("Multi-Study");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=1;
		msl.setLayoutData(gd);
		
		ms = new Combo(comp, SWT.NONE);
		for (int i=1; i<=9; i++){
			ms.add(String.valueOf(i));
		}
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=1;
		ms.select(0);
		ms.setLayoutData(gd);
		ms.setFont(font);
		ms.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
			
		});
		
		/*
		wsidigen = new Button(comp, SWT.NONE);
		wsidigen.setText("&Wsi-Di Generator");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=3;
		wsidigen.setLayoutData(gd);
		wsidigen.setFont(font);
		wsidigen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				wsidigenerator();
			}
		});
		*/
		
		Label emptyLabel1 = new Label (comp, SWT.NONE);
		emptyLabel1.setText("");
		gd = new GridData(GridData.END);
		gd.horizontalSpan=3;
		emptyLabel1.setLayoutData(gd);
		
		ch = new Button(comp, SWT.CHECK);
		ch.setText("CalSimHydro");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=1;
		ch.setLayoutData(gd);
		ch.setFont(font);
		ch.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
			
		});
		
		calsimHydroText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 5;
		calsimHydroText.setLayoutData(gd);
		calsimHydroText.setFont(font);
		calsimHydroText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		calsimHydroDt = new DropTarget(calsimHydroText, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
		calsimHydroDt.setTransfer(new Transfer[] { FileTransfer.getInstance()});
		calsimHydroDt.addDropListener(new DropTargetAdapter() {
            public void drop(DropTargetEvent event) {
                String fileList[] = null;
                FileTransfer ft = FileTransfer.getInstance();
                if (ft.isSupportedType(event.currentDataType)) {
                    fileList = (String[]) event.data;
                    calsimHydroText.setText(fileList[0]);
                }
            }
        });
		
		calsimHydroButton = createPushButton(comp, "&Browse", null); //$NON-NLS-1$
		calsimHydroButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseFiles(calsimHydroText);
			}
		});
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		calsimHydroButton.setLayoutData(gd);
	}
	
	public void generateBatch(PrintWriter out, String configFilePath){
		String freeXA;
		//freeXA = launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_FREEXA, "no");
		freeXA="no";
		String jarXA;
		if (freeXA.equalsIgnoreCase("yes")){
			jarXA="CalLiteV16.jar";
		}else{
			jarXA="XAOptimizer.jar";
		}
		out.println("@echo off");
		out.println();
		out.println("set path=" + externalPath + ";"+"lib;%path%");
		out.println("set temp_wrims2=jre\\bin");
		out.println("set TF_CPP_MIN_LOG_LEVEL=2");
		out.println();
		/*
		String xmx="1280m";
		if (System.getProperty("os.arch").equalsIgnoreCase("amd64")){
			xmx="4096m";
		}
		*/
		out.println("jre\\bin\\java -Xmx"+DebugCorePlugin.xmx+"m -Xss1024K -XX:+CreateMinidumpOnCrash -Duser.timezone=Etc/GMT+8 -Djava.library.path=\"" + externalPath + ";lib\" -cp \""+externalPath+";"+"lib\\external;lib\\WRIMSv2.jar;lib\\jep-3.8.2.jar;lib\\jna-3.5.1.jar;lib\\commons-io-2.1.jar;lib\\"+jarXA+";lib\\lpsolve55j.jar;lib\\coinor.jar;lib\\gurobi.jar;lib\\heclib.jar;lib\\jnios.jar;lib\\jpy.jar;lib\\misc.jar;lib\\pd.jar;lib\\vista.jar;lib\\guava-11.0.2.jar;lib\\javatuples-1.2.jar;lib\\kryo-2.24.0.jar;lib\\minlog-1.2.jar;lib\\objenesis-1.2.jar;lib\\jarh5obj.jar;lib\\jarhdf-2.10.0.jar;lib\\jarhdf5-2.10.0.jar;lib\\jarhdfobj.jar;lib\\slf4j-api-1.7.5.jar;lib\\slf4j-nop-1.7.5.jar;lib\\mysql-connector-java-5.1.42-bin.jar;lib\\sqljdbc4-2.0.jar;lib\\java-object-diff-0.95.jar\" wrimsv2.components.ControllerBatch "+"-config="+configFilePath);
		out.close();
	}
	
	protected void browseFiles(Text fileLocationText) {
		FileDialog dlg =new FileDialog(getShell(),SWT.OPEN);
		fileLocationText.setText(dlg.open());
	}
	
	protected void browseFolders(Text folderLocationText) {
		DirectoryDialog dlg =new DirectoryDialog(getShell(),SWT.OPEN);
		folderLocationText.setText(dlg.open());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String studyName=null;
			studyName = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_STUDY, (String)null);
			if (studyName == null) {
				studyText.setText("");
			}else{
				studyText.setText(studyName);
			}		
			String author=null;
			author = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_AUTHOR, (String)null);
			if (author == null) {
				authorText.setText("");
			}else{
				authorText.setText(author);
			}	
			String date=null;
			date = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_DATE, (String)null);
			if (date == null) {
				dateText.setText("");
			}else{
				dateText.setText(date);
			}	
			String description=null;
			description = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_DESCRIPTION, (String)null);
			if (description == null) {
				descriptionText.setText("");
			}else{
				descriptionText.setText(description);
			}	
			String mainFile = null;
			mainFile = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_PROGRAM, (String)null);
			if (mainFile == null) {
				fMainFileText.setText("");
			}else{
				fMainFileText.setText(mainFile);
			}
			String dvarFile = null;
			dvarFile = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_DVARFILE, (String)null);
			if (dvarFile == null) {
				fDvarFileText.setText("");
			}else{
				fDvarFileText.setText(dvarFile);
			}
			String svarFile = null;
			svarFile = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_SVARFILE, (String)null);
			if (svarFile == null) {
				fSvarFileText.setText("");
			}else{
				fSvarFileText.setText(svarFile);
			}
			String initFile = null;
			initFile = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_INITFILE, (String)null);
			if (initFile == null) {
				fInitFileText.setText("");
			}else{
				fInitFileText.setText(initFile);
			}
			String gwDataFolder = null;
			gwDataFolder = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_GWDATAFOLDER, (String)null);
			if (gwDataFolder == null) {
				groundWaterFolderText.setText("");
			}else{
				groundWaterFolderText.setText(gwDataFolder);
			}
			String aPart = null;
			aPart = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_APART, (String)null);
			if (aPart == null) {
				aPartText.setText("");
			}else{
				aPartText.setText(aPart);
			}
			String svFPart = null;
			svFPart = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_SVFPART, (String)null);
			if (svFPart == null) {
				svFPartText.setText("");
			}else{
				svFPartText.setText(svFPart);
			}
			String initFPart = null;
			initFPart = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_INITFPART, (String)null);
			if (initFPart == null) {
				initFPartText.setText("");
			}else{
				initFPartText.setText(initFPart);
			}
			String timeStep = null;
			timeStep = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_TIMESTEP, (String)null);
			if (timeStep == null) {
				timeStepCombo.select(0);
			}else{
				timeStepCombo.setText(timeStep);
			}
			String startYear = null;
			startYear = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_STARTYEAR, (String)null);
			if (startYear == null) {
				startYearCombo.select(21);
			}else{
				startYearCombo.setText(startYear);
			}
			String startMonth = null;
			startMonth = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_STARTMONTH, (String)null);
			if (startMonth == null) {
				startMonthCombo.select(0);
			}else{
				startMonthCombo.setText(startMonth);
			}
			String startDay = null;
			startDay = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_STARTDAY, (String)null);
			if (startDay == null) {
				startDayCombo.select(30);
			}else{
				startDayCombo.setText(startDay);
			}
			String endYear = null;
			endYear = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_ENDYEAR, (String)null);
			if (endYear == null) {
				endYearCombo.select(103);
			}else{
				endYearCombo.setText(endYear);
			}
			String endMonth = null;
			endMonth = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_ENDMONTH, (String)null);
			if (endMonth == null) {
				endMonthCombo.select(11);
			}else{
				endMonthCombo.setText(endMonth);
			}
			String endDay = null;
			endDay = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_ENDDAY, (String)null);
			if (endDay == null) {
				endDayCombo.select(29);
			}else{
				endDayCombo.setText(endDay);
			}
			String lt="0";
			lt = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_LAUNCHTYPE, "0");
			if (lt.equals("1")){
				pa.setSelection(true);
			}else{
				pa.setSelection(false);
			}
			String msr="1";
			msr = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_MULTISTUDY, "1");
			int msv = Integer.parseInt(msr);
			if (msv>9){
				ms.select(8);
			}else{
				ms.select(msv-1);
			}
			
			String chr="0";
			chr = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_CALSIMHYDRORUN, "0");
			if (chr.equals("1")){
				ch.setSelection(true);
			}else{
				ch.setSelection(false);
			}
			
			String che="";
			che = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_CALSIMHYDROEXE, "");
			calsimHydroText.setText(che);
		} catch (CoreException e) {
			setErrorMessage(e.getMessage());
		}
		launchConfig=configuration;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		String studyName = studyText.getText().trim();
		if (studyName.length() == 0) {
			studyName = "";
		}	
		String author = authorText.getText().trim();
		if (author.length() == 0) {
			author = "";
		}	
		String date = dateText.getText().trim();
		if (date.length() == 0) {
			date = "";
		}	
		String description = descriptionText.getText().trim();
		if (description.length() == 0) {
			description = "";
		}	
		String mainFile = fMainFileText.getText().trim();
		if (mainFile.length() == 0) {
			mainFile = "";
		}
		String dvarFile = fDvarFileText.getText().trim();
		if (dvarFile.length() == 0) {
			dvarFile = "";
		}
		String svarFile = fSvarFileText.getText().trim();
		if (svarFile.length() == 0) {
			svarFile = "";
		}
		String initFile = fInitFileText.getText().trim();
		if (initFile.length() == 0) {
			initFile = "";
		}
		String gwDataFolder = groundWaterFolderText.getText().trim();
		if (gwDataFolder.length() == 0) {
			gwDataFolder = "";
		}
		String aPart = aPartText.getText().trim();
		if (aPart.length() == 0) {
			aPart = "";
		}
		String svFPart = svFPartText.getText().trim();
		if (svFPart.length() == 0) {
			svFPart = "";
		}
		String initFPart = initFPartText.getText().trim();
		if (initFPart.length() == 0) {
			initFPart = "";
		}
		String timeStep = timeStepCombo.getText().trim();
		if (timeStep.length() == 0) {
			timeStep = "";
		}
		String startYear = startYearCombo.getText().trim();
		if (startYear.length() == 0) {
			startYear = "";
		}
		String startMonth = startMonthCombo.getText().trim();
		if (startMonth.length() == 0) {
			startMonth = "";
		}
		String startDay = startDayCombo.getText().trim();
		if (startDay.length() == 0) {
			startDay = "";
		}
		String endYear = endYearCombo.getText().trim();
		if (endYear.length() == 0) {
			endYear = "";
		}
		String endMonth = endMonthCombo.getText().trim();
		if (endMonth.length() == 0) {
			endMonth = "";
		}
		String endDay = endDayCombo.getText().trim();
		if (endDay.length() == 0) {
			endDay = "";
		}
		String lt="0";
		if (pa.getSelection()){
			lt="1";
		}else{
			lt="0";
		}
		String msr=ms.getText().trim();
		
		String chr="0";
		if (ch.getSelection()){
			chr="1";
		}else{
			chr="0";
		}
		
		String che=calsimHydroText.getText();
		
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_STUDY, studyName);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_AUTHOR, author);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_DATE, date);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_DESCRIPTION, description);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_PROGRAM, mainFile);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_DVARFILE, dvarFile);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_SVARFILE, svarFile);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_INITFILE, initFile);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_GWDATAFOLDER, gwDataFolder);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_APART, aPart);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_SVFPART, svFPart);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_INITFPART, initFPart);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_TIMESTEP, timeStep);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_STARTYEAR, startYear);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_STARTMONTH, startMonth);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_STARTDAY, startDay);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_ENDYEAR, endYear);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_ENDMONTH, endMonth);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_ENDDAY, endDay);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_LAUNCHTYPE, lt);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_MULTISTUDY, msr);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_CALSIMHYDRORUN, chr);
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_CALSIMHYDROEXE, che);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	@Override
	public String getName() {
		return "Main";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		setMessage(null);
		return true;
	}

	private class TSItemListener implements ModifyListener {
		
		@Override
		public void modifyText(ModifyEvent e) {
			updateLaunchConfigurationDialog();
			String _strTimeStep =timeStepCombo.getText();
			if (TimeOperation.isMonthlyInterval(_strTimeStep)) {
				startDayCombo.removeModifyListener(sdl);
				int startMonth=TimeOperation.monthValue(startMonthCombo.getText());
				int startYear=Integer.parseInt(startYearCombo.getText());	
				startDayCombo.select(TimeOperation.numberOfDays(startMonth, startYear)-1);
				startDayCombo.addModifyListener(sdl);
				//startDayCombo.setEnabled(false);
				
				endDayCombo.removeModifyListener(edl);
				int endMonth=TimeOperation.monthValue(endMonthCombo.getText());
				int endYear=Integer.parseInt(endYearCombo.getText());	
				endDayCombo.select(TimeOperation.numberOfDays(endMonth, endYear)-1);
				endDayCombo.addModifyListener(edl);
				//endDayCombo.setEnabled(false);
			} else {
				//startDayCombo.setEnabled(true);
				//endDayCombo.setEnabled(true);
			}
			
		}
	}

	private class DayItemListener implements  ModifyListener {
		int type;
		
		public DayItemListener(int i) {
			type = i;
		}

		@Override
		public void modifyText(ModifyEvent e) {
			updateLaunchConfigurationDialog();
			if (type==1){
				int startMonth=TimeOperation.monthValue(startMonthCombo.getText());
				int startYear=Integer.parseInt(startYearCombo.getText());	
				int maxDayInMonth=TimeOperation.numberOfDays(startMonth, startYear);
				if (Integer.parseInt(startDayCombo.getText())>maxDayInMonth){
					startDayCombo.removeModifyListener(sdl);
					startDayCombo.select(TimeOperation.numberOfDays(startMonth, startYear)-1);
					startDayCombo.addModifyListener(sdl);
				}
			}else{
				int endMonth=TimeOperation.monthValue(endMonthCombo.getText());
				int endYear=Integer.parseInt(endYearCombo.getText());	
				int maxDayInMonth=TimeOperation.numberOfDays(endMonth, endYear);
				if (Integer.parseInt(endDayCombo.getText())>maxDayInMonth){
					endDayCombo.removeModifyListener(edl);
					endDayCombo.select(TimeOperation.numberOfDays(endMonth, endYear)-1);
					endDayCombo.addModifyListener(edl);
				}
			}
		}
	}

	private class MYItemListener implements ModifyListener {
		int type;

		public MYItemListener(int i) {
			type = i;
		}

		@Override
		public void modifyText(ModifyEvent e) {
			updateLaunchConfigurationDialog();
			if (type==1){
				int startMonth=TimeOperation.monthValue(startMonthCombo.getText());
				int startYear=Integer.parseInt(startYearCombo.getText());	
				int maxDayInMonth=TimeOperation.numberOfDays(startMonth, startYear);
				if (Integer.parseInt(startDayCombo.getText())>maxDayInMonth || TimeOperation.isMonthlyInterval(timeStepCombo.getText())){
					startDayCombo.removeModifyListener(sdl);
					startDayCombo.select(TimeOperation.numberOfDays(startMonth, startYear)-1);
					startDayCombo.addModifyListener(sdl);
				}
			}else{
				int endMonth=TimeOperation.monthValue(endMonthCombo.getText());
				int endYear=Integer.parseInt(endYearCombo.getText());	
				int maxDayInMonth=TimeOperation.numberOfDays(endMonth, endYear);
				if (Integer.parseInt(endDayCombo.getText())>maxDayInMonth || TimeOperation.isMonthlyInterval(timeStepCombo.getText())){
					endDayCombo.removeModifyListener(edl);
					endDayCombo.select(TimeOperation.numberOfDays(endMonth, endYear)-1);
					endDayCombo.addModifyListener(edl);
				}
			}		
		}
	}

}
