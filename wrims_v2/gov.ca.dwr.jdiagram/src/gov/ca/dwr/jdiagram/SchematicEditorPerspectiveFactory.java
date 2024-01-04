package gov.ca.dwr.jdiagram;

import gov.ca.dwr.jdiagram.views.SchematicEditorViewA;
import gov.ca.dwr.jdiagram.views.SchematicEditorViewB;
import gov.ca.dwr.jdiagram.views.SchematicOverview;
import gov.ca.dwr.jdiagram.views.SchematicView;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class SchematicEditorPerspectiveFactory implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		layout.addView(SchematicEditorViewA.ID, IPageLayout.LEFT, 0.5f, editorArea);
		layout.addView(SchematicEditorViewB.ID, IPageLayout.RIGHT, 0.5f, editorArea);
	}

}
