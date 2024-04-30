package wrimsv2_plugin.debugger.toolbaritem;

import java.util.HashMap;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionDelegate;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.menuitem.EnableMenus;

public class PauseResume extends ActionDelegate implements IViewActionDelegate{
	IViewPart view;
	
	@Override
	public void init(IViewPart view) {
		this.view=view;		
	}

	public void run(IAction action) {
		try {
			if (DebugCorePlugin.isDebugging){
				if (DebugCorePlugin.target.isSuspended()){
					DebugCorePlugin.debugSet.updateDebugTimeSet();
					DebugCorePlugin.target.resume();
					enableRunMenu(1);
				}else{
					DebugCorePlugin.target.pause();
					enableRunMenu(3);
				}
			}
		} catch (DebugException e) {
			WPPException.handleException(e);
		}
	}
	
	public void enableRunMenu(int flag){
		HandlePauseResumeButton.procPauseResumeToolbarItem(flag);
		HashMap<String, Boolean> enableMenuMap=new HashMap<String, Boolean>();
		HashMap<String, Boolean> enableButtonMap=new HashMap<String, Boolean>();
		if (flag==3){
			enableMenuMap.put(DebugCorePlugin.ID_WPP_TERMINATEMENU, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_PAUSEMENU, false);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_SUSPENDMENU, false);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_RESUMEMENU, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_RESIMMENU, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLE, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEP, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_SAVETODVFILE, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_SAVETOSVFILE, true);
			enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLEBUTTON, true);
			enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEPBUTTON, true);
		}else{
			enableMenuMap.put(DebugCorePlugin.ID_WPP_TERMINATEMENU, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_PAUSEMENU, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_SUSPENDMENU, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_RESUMEMENU, false);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_RESIMMENU, false);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLE, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEP, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_SAVETODVFILE, false);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_SAVETOSVFILE, false);
			enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLEBUTTON, true);
			enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEPBUTTON, true);
		}
		new EnableMenus(enableMenuMap);
		new EnableButtons(enableButtonMap);
	}
}
