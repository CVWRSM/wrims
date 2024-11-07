package wrimsv2_plugin.debugger.listener;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.IIntroSite;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;

public class WelcomeViewListener {

    public void addWelcomeViewListener() {
    	
    	Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				// Get the active workbench page
		        IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
		        
		        /*
		        IIntroPart introPart = PlatformUI.getWorkbench().getIntroManager().getIntro();
		        final String introID;
		        if (introPart==null){
		        	introID="null";
		        }else{
		        	introID =introPart.getIntroSite().getId();
		        }
		        */
		        
		        // Add a part listener to listen to view changes
		        page.addPartListener(new IPartListener2() {

		            @Override
		            public void partActivated(IWorkbenchPartReference partRef) {
		                //if (partRef.getId().equals(introID)) {
		                //    System.out.println("Welcome view activated.");
		                //}
		            }

		            @Override
		            public void partClosed(IWorkbenchPartReference partRef) {
		                if (partRef.getId().equals("org.eclipse.ui.internal.introview")) {
		                	for (int i=DebugCorePlugin.wrimsGUIPerspectives.length-1; i>=0; i--){
		                		//String perspectiveID="wpp.ideperspective";  
		                		//if (!isPerspectiveLoaded(perspectiveID))
		                    	//openPerspective(perspectiveID);
		                		openPerspective(DebugCorePlugin.wrimsGUIPerspectives[i]);
		                	}
		                }
		            }

		            // Implement other methods as needed
		            @Override
		            public void partOpened(IWorkbenchPartReference partRef) { }

		            @Override
		            public void partDeactivated(IWorkbenchPartReference partRef) { }

		            @Override
		            public void partHidden(IWorkbenchPartReference partRef) { }

		            @Override
		            public void partVisible(IWorkbenchPartReference partRef) { }

		            @Override
		            public void partInputChanged(IWorkbenchPartReference partRef) { }

		            @Override
		            public void partBroughtToTop(IWorkbenchPartReference partRef) { }
		        });
			};
    	});
        
    }
    
    public boolean isPerspectiveLoaded(String perspectiveId) {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage();

        if (activePage != null) {
            IPerspectiveDescriptor[] openedPerspectives = activePage.getOpenPerspectives();
            
            for (IPerspectiveDescriptor perspective : openedPerspectives) {
                if (perspective.getId().equals(perspectiveId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void openPerspective(final String perspectiveId) {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage();
        final IWorkbenchWindow window = activePage.getWorkbenchWindow();

        workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				IPerspectiveDescriptor perspective = workbench.getPerspectiveRegistry().findPerspectiveWithId(perspectiveId);
				activePage.setPerspective(perspective); 
				
				/*
				try {
					workbench.showPerspective(perspectiveId, window);
				} catch (WorkbenchException e) {
					WPPException.handleException(e);
				}
				*/
			}
		});
    }
    
    private void logInfo(String message) {
        IStatus status = new Status(IStatus.INFO, "wrimsv2_plugin", message);
        Platform.getLog(Platform.getBundle("wpp.debuggerapplication")).log(status);
    }
}

