package gov.ca.dwr.jdiagram.views;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;

import javax.swing.DebugGraphics;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.mindfusion.diagramming.DiagramView;
import com.mindfusion.diagramming.Overview;

/**
 * This represents the overview to the schematic being displayed by the
 * SchematicView
 * <p>
 */

public class SchematicOverview extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "gov.ca.dwr.jdiagram.views.SchematicOverview";

	private Composite swingContainer;

	private Overview overview;

	/**
	 * The constructor.
	 */
	public SchematicOverview() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@SuppressWarnings("serial")
	public void createPartControl(Composite parent) {
		System.setProperty("sun.awt.noerasebackground", "true");
		swingContainer = new Composite(parent, SWT.BACKGROUND | SWT.EMBEDDED);
		Frame frame = SWT_AWT.new_Frame(swingContainer);
		Panel panel = new Panel(new BorderLayout()) {
			public void update(java.awt.Graphics g) {
				paint(g);
			}
		};
		frame.add(panel);
		JRootPane root = new JRootPane();
		panel.add(root);
		java.awt.Container contentPane = root.getContentPane();
		contentPane.add(new JScrollPane(overview = new Overview()));
		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		if (activePage != null) {
			IViewPart sview = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.findView(SchematicView.ID);
			if (sview != null) {
				SchematicView schematicView = (SchematicView) sview;
				overview.setDiagramView(schematicView.getDiagramView());
				overview.setBounds(schematicView.getDiagramView().getBounds());
				overview.setIgnoreRepaint(true);
				overview.setFitAll(true);
				overview.setDoubleBuffered(true);
			}
		}
		frame.pack();

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		swingContainer.setFocus();
	}

	public void setDiagramView(DiagramView diagramView) {
		overview.setDiagramView(diagramView);
		overview.repaint();
		overview.setBounds(diagramView.getBounds());
		overview.setFitAll(true);
	}
}