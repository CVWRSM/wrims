package wrimsv2_plugin.debugger.menuitem;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;

public class ClearConditionalBreakpointMenu implements IWorkbenchWindowActionDelegate {
	public ClearConditionalBreakpointMenu(){

	}

	@Override
	public void run(IAction action) {
		DebugCorePlugin.conditionalBreakpoint="";
		clearConditionalBreakpoint();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}
	
	public void clearConditionalBreakpoint(){
		if (DebugCorePlugin.isDebugging){
			try {
				DebugCorePlugin.target.sendRequest("conditional_breakpoint:");
			} catch (DebugException e) {
				WPPException.handleException(e);
			}
		}
	}
}
