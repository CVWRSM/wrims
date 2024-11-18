package wrimsv2_plugin.debugger.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.internal.PluginActionContributionItem;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.ViewPart;

import wrimsv2_plugin.calsimhydro.DefaultCalSimHydro;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.listener.WelcomeViewListener;
import wrimsv2_plugin.debugger.menuitem.EnableMenus;
import wrimsv2_plugin.debugger.toolbaritem.EnableButtons;
import wrimsv2_plugin.debugger.toolbaritem.HandlePauseResumeButton;
import wrimsv2_plugin.debugger.view.UpdateView;
import wrimsv2_plugin.debugger.view.WPPWatchView;
import wrimsv2_plugin.tools.DataProcess;
import wrimsv2_plugin.tools.ProcWatchItem;
import wrimsv2_plugin.tools.ShowDuplicatedWatch;

public class DebuggerStartUp implements IStartup {

	@Override
	public void earlyStartup() {
		SettingPref.load();
		SettingPref.loadCBCDefault();
		SettingPref.loadCBCSetting();
		enableRunMenu();
		initialStudyData();
		DataProcess.initialVariableValueAlt();
		addPerspectiveChangeListener();
		new WelcomeViewListener().addWelcomeViewListener();
	}

	public void enableRunMenu(){
		HashMap<String, Boolean> enableMenuMap=new HashMap<String, Boolean>();
		enableMenuMap.put(DebugCorePlugin.ID_WPP_TERMINATEMENU, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_PAUSEMENU, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_SUSPENDMENU, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_RESUMEMENU, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_RESIMMENU, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLE, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEP, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_CONDITIONALBREAKPOINT, true);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_CLEARCONDITIONALBREAKPOINT, true);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_SAVETODVFILE, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_SAVETOSVFILE, false);
		new EnableMenus(enableMenuMap);
		/*
		HandlePauseResumeButton.procPauseResumeToolbarItem(0);
		HashMap<String, Boolean> enableButtonMap=new HashMap<String, Boolean>();
		enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLEBUTTON, false);
		enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEPBUTTON, false);
		new EnableButtons(enableButtonMap);
		*/
		showSolverStatus();
	}
	
	public void initialStudyData(){
		Map<String, String>[] studiesData = DebugCorePlugin.studiesData;
		for (int i=0; i<4; i++){
			studiesData[i]=new HashMap<String, String>();
		}
	}
		
	public void showSolverStatus(){
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				String log="";
				if (!DebugCorePlugin.log.equalsIgnoreCase("NONE")){
					log=DebugCorePlugin.log;
				}
				String status=DebugCorePlugin.solver+"  "+log;
				
				IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
				IViewPart console = page.findView( IConsoleConstants.ID_CONSOLE_VIEW ); 
				if (console != null){
					try {
						console=page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
						console.getViewSite().getActionBars().getStatusLineManager().setMessage(status);
					} catch (PartInitException e) {
						WPPException.handleException(e);
					}
				}
			}
		});
	}
	
	public void addPerspectiveChangeListener(){
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				Workbench.getInstance().getActiveWorkbenchWindow().addPerspectiveListener(new IPerspectiveListener() {

					@Override
					public void perspectiveActivated(IWorkbenchPage page,
							IPerspectiveDescriptor perspective) {
						String label=perspective.getLabel();
						if (label.equalsIgnoreCase("IDE")){
							showSolverStatus();
						}
					}

					@Override
					public void perspectiveChanged(IWorkbenchPage page,
							IPerspectiveDescriptor perspective, String changeId) {
						String label=perspective.getLabel();
						if (label.equalsIgnoreCase("CalSimHydro")){
							DefaultCalSimHydro dch = new DefaultCalSimHydro();
							dch.load();
						}
					}
				});
			}
			
		});
		
	}	
}
