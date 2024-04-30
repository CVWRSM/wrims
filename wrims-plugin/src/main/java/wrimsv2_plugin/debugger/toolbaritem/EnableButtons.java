package wrimsv2_plugin.debugger.toolbaritem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.internal.PluginActionContributionItem;
import org.eclipse.ui.internal.Workbench;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;

public class EnableButtons {
	public EnableButtons(final HashMap<String, Boolean> enableButtonMap){
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
				IViewPart viewPart = page.findView( IConsoleConstants.ID_CONSOLE_VIEW ); 
				if (viewPart != null){
					IViewSite viewSite = viewPart.getViewSite(); 
					IActionBars actionBars = viewSite.getActionBars(); 
					IToolBarManager toolBarManager = actionBars.getToolBarManager();
					Set<String> keys = enableButtonMap.keySet();
					Iterator<String> iterator = keys.iterator();
					while (iterator.hasNext()){
						String id=iterator.next();
						IContributionItem contributionItem = toolBarManager.find(id);
						PluginActionContributionItem pACI = ((PluginActionContributionItem)contributionItem);
						if (pACI !=null){
							IAction action = pACI.getAction();
							action.setEnabled(enableButtonMap.get(id));
						}
					}
				}
			}
		});
	}
}
