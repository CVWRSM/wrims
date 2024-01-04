
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import com.mindfusion.diagramming.Behavior;
import com.mindfusion.diagramming.Diagram;
import com.mindfusion.diagramming.DiagramView;
import com.mindfusion.diagramming.Overview;

public class SimpleTestForFlicker {

	public static void main(String[] args) throws Exception {
		SimpleTestForFlicker test = new SimpleTestForFlicker(args[0]);
	}

	public SimpleTestForFlicker(String file) {
		diagram = new Diagram();
		diagramView = new DiagramView();
		diagramView.setDiagram(diagram);
		diagramView.setBehavior(Behavior.Modify);

		try {
			diagram.loadFromXml(file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		overview = new Overview();
		overview.setDiagramView(diagramView);
		overview.setFitAll(true);
		overview.setPreferredSize(new Dimension(300, 150));

		createActions();
		JToolBar bar = new JToolBar();
		bar.add(zoomInAction);
		bar.add(zoomOutAction);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(bar, BorderLayout.NORTH);
		panel.add(overview, BorderLayout.EAST);
		panel.add(new JScrollPane(diagramView));

		JFrame fr = new JFrame("SimpleTestForFlicker");
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.getContentPane().add(panel);
		fr.pack();
		fr.setSize(600, 600);
		fr.setVisible(true);

	}

	@SuppressWarnings("serial")
	public void createActions() {
		zoomInAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				overview.suspendRepaint();
				diagramView.suspendRepaint();
				Rectangle2D.Float rect = diagramView.deviceToDoc(diagramView
						.getVisibleRect());
				rect.setRect(rect.x + rect.width / 4, rect.y + rect.height / 4,
						rect.width / 2, rect.height / 2);
				diagramView.zoomToFit(rect);
				diagramView.resumeRepaint();
				overview.resumeRepaint();
			}

		};
		zoomInAction.putValue(Action.SHORT_DESCRIPTION, "zoom in");
		zoomInAction.putValue(Action.SMALL_ICON,
				createImageIcon("ZoomIn16.gif"));

		zoomOutAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				overview.suspendRepaint();
				diagramView.suspendRepaint();
				Rectangle2D.Float rect = diagramView.deviceToDoc(diagramView
						.getVisibleRect());
				rect.setRect(rect.x - rect.width / 2, rect.y - rect.height / 2,
						rect.width * 2, rect.height * 2);
				diagramView.zoomToFit(rect);
				diagramView.resumeRepaint();
				overview.resumeRepaint();
			}

		};
		zoomOutAction.putValue(Action.SHORT_DESCRIPTION, "zoom out");
		zoomOutAction.putValue(Action.SMALL_ICON,
				createImageIcon("ZoomOut16.gif"));
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = SimpleTestForFlicker.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	private AbstractAction zoomInAction;
	private AbstractAction zoomOutAction;
	private Diagram diagram;
	private DiagramView diagramView;
	private Overview overview;

}
