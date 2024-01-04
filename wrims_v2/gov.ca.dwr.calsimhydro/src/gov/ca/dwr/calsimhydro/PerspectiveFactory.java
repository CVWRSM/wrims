package gov.ca.dwr.calsimhydro;

import gov.ca.dwr.calsimhydro.views.CalSimHydroView;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		layout.addView(CalSimHydroView.ID, IPageLayout.LEFT, 1.0f, editorArea);
	}

}
