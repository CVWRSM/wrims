package wrims.schematic.jdiagram.test;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import wrims.schematic.jdiagram.SchematicViewer;

public class SampleZoomRectangleSchematicViewer {
	public static void main(String[] args) {
		final SchematicViewer view = new SchematicViewer();
		try {
			view
					.load("resources/wrims/schematic/CS3_NetworkSchematic.xml");
			view.zoomToAll();
		} catch (Exception e) {
			e.printStackTrace();
		}

		JToolBar bar = new JToolBar();
		bar.add(view.getZoomInAction());
		bar.add(view.getZoomOutAction());
		bar.add(view.getZoomToFitAction());
		bar.add(new JToggleButton(view.getZoomRectangleAction()));

		JFrame fr = new JFrame();
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = fr.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(BorderLayout.PAGE_START, bar);
		contentPane.add(view);
		fr.pack();
		fr.setSize(800, 600);
		fr.setVisible(true);
	}
}
