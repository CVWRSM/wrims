package wrimsv2_plugin.debugger.toolbaritem;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.internal.PluginActionContributionItem;
import org.eclipse.ui.internal.Workbench;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;

public class HandlePauseResumeButton {
	public static int status=0;
	
	public static void procPauseResumeToolbarItem(final int flag){
		status=flag;
		Display.getDefault().syncExec(new Runnable() {
			public void run() { 
				IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
				IViewPart viewPart = page.findView( IConsoleConstants.ID_CONSOLE_VIEW );
				if (viewPart != null){
					IViewSite viewSite = viewPart.getViewSite(); 
					IActionBars actionBars = viewSite.getActionBars(); 
					IToolBarManager toolBarManager = actionBars.getToolBarManager();
					IContributionItem contributionItem = toolBarManager.find(DebugCorePlugin.ID_WPP_PAUSERESUMEBUTTON);
					PluginActionContributionItem pACI = ((PluginActionContributionItem)contributionItem);
					IAction action = pACI.getAction();
					if (flag==0){
						action.setEnabled(false);
						action.setImageDescriptor(DebugCorePlugin.getImageDescriptor("pause.png"));
					}else if (flag==1){
						action.setEnabled(true);
						action.setImageDescriptor(DebugCorePlugin.getImageDescriptor("pause.png"));
					}else if (flag==2){
						action.setEnabled(false);
						action.setImageDescriptor(DebugCorePlugin.getImageDescriptor("resume.png"));
					}else if (flag==3){
						action.setEnabled(true);
						action.setImageDescriptor(DebugCorePlugin.getImageDescriptor("resume.png"));
					}
				}
			}
		});
	}
	
	/*
	public static void setVisibilityPauseResumeToolbarItem(final boolean isVisible){
		Display.getDefault().syncExec(new Runnable() {
			public void run() { 
				IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
				IViewPart viewPart = page.findView( IConsoleConstants.ID_CONSOLE_VIEW ); 
				IViewSite viewSite = viewPart.getViewSite(); 
				IActionBars actionBars = viewSite.getActionBars(); 
				IToolBarManager toolBarManager = actionBars.getToolBarManager();
				IContributionItem contributionItem = toolBarManager.find(DebugCorePlugin.ID_WPP_PAUSERESUMEBUTTON);
				contributionItem.setVisible(isVisible);
			}
		});
	}
	*/	
}
