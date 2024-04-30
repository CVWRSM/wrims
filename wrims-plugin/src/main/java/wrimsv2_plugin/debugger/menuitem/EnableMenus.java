package wrimsv2_plugin.debugger.menuitem;

import java.util.HashMap;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.SubContributionItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.internal.ActionSetContributionItem;
import org.eclipse.ui.internal.ActionSetMenuManager;
import org.eclipse.ui.internal.PluginActionContributionItem;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchWindow;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;

public class EnableMenus {
	public EnableMenus(final HashMap<String, Boolean> enableMenuMap){
		Display.getDefault().syncExec(new Runnable() {
			public void run() { 
				IWorkbenchWindow window = Workbench.getInstance().getActiveWorkbenchWindow();
				MenuManager menuManager = ((WorkbenchWindow)window).getMenuManager();
				Menu menu = menuManager.getMenu();
				for(MenuItem item : menu.getItems()){ 
					procRunMenu(item);
					procDSSMenu(item);
				}
			}
			
			public void procRunMenu(MenuItem item){
				if (item.getText().equals("&Run")){
					MenuManager subContribution = (MenuManager)item.getData();
					for (IContributionItem subItem:subContribution.getItems()){
						if (enableMenuMap.containsKey(subItem.getId())){
							ActionSetContributionItem aSubItem = (ActionSetContributionItem)subItem;
							IContributionItem inSubItem = aSubItem.getInnerItem();	
							PluginActionContributionItem pACI=(PluginActionContributionItem)inSubItem;
							pACI.getAction().setEnabled(enableMenuMap.get(aSubItem.getId()));
						}
					}
				}
			}
			
			public void procDSSMenu(MenuItem item){
				if (item.getText().equals("DSS")){
					SubContributionItem subContribution = (SubContributionItem)item.getData();
					IContributionItem subItems = subContribution.getInnerItem();
					MenuManager aSubItems = (MenuManager)subItems;
					for (IContributionItem subItem:aSubItems.getItems()){
						if (enableMenuMap.containsKey(subItem.getId())){
							ActionSetContributionItem aSubItem = (ActionSetContributionItem)subItem;
							IContributionItem inSubItem = aSubItem.getInnerItem();	
							PluginActionContributionItem pACI=(PluginActionContributionItem)inSubItem;
							pACI.getAction().setEnabled(enableMenuMap.get(aSubItem.getId()));
						}
					}
				}
			}
		});
	}
}
