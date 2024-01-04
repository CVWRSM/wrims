package gov.ca.dwr.jdiagram;

import gov.ca.dwr.jdiagram.views.SchematicOverview;
import gov.ca.dwr.jdiagram.views.SchematicView;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		layout.addView(SchematicView.ID, IPageLayout.LEFT, 1.0f, editorArea);
		layout.addView(SchematicOverview.ID, IPageLayout.BOTTOM, 0.25f, editorArea);
	}

}
