package wrimsv2_plugin.debugger.exception;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.view.WPPExceptionView;

public class WPPException {
	public static void handleException(final Exception e){
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				try {
					WPPExceptionView exceptionView = (WPPExceptionView) workbench.getActiveWorkbenchWindow().getActivePage().showView(DebugCorePlugin.ID_WPP_EXCEPTION_VIEW);
					exceptionView.addException(e);
				} catch (PartInitException e) {
					WPPException.handleException(e);
				}
			}
		});
	}
}
