package wrimsv2_plugin.debugger.console;

import java.util.HashMap;

import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.toolbaritem.EnableButtons;
import wrimsv2_plugin.debugger.toolbaritem.HandlePauseResumeButton;

public class WPPConsolePageParticipant implements IConsolePageParticipant {

	private IPageBookViewPage page;
	
	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(IPageBookViewPage page, IConsole console) {
		//initConsoleButton();
		this.page=page;
		showSolverStatus();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void activated() {
		showSolverStatus();
	}

	@Override
	public void deactivated() {
		showSolverStatus();
	}

	public void initConsoleButton(){
		HandlePauseResumeButton.procPauseResumeToolbarItem(0);
		HashMap<String, Boolean> enableButtonMap=new HashMap<String, Boolean>();
		enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLEBUTTON, false);
		enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEPBUTTON, false);
		new EnableButtons(enableButtonMap);
	}
	
	public void showSolverStatus(){
		String log="";
		if (!DebugCorePlugin.log.equalsIgnoreCase("NONE")){
			log=DebugCorePlugin.log;
		}
		page.getSite().getActionBars().getStatusLineManager().setMessage(DebugCorePlugin.solver+"  "+log);
	}
}
