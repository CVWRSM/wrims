package wrims.schematic.jdiagram.test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import wrims.schematic.jdiagram.MagnifierPanel;
import wrims.schematic.jdiagram.SchematicViewer;

public class SampleSchematicViewer {
	public static void main(String[] args) {
		final SchematicViewer view = new SchematicViewer();
		try {
			view
					.load("resources/wrims/schematic/CS3_NetworkSchematic.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		MagnifierPanel magView = new MagnifierPanel(view.getDiagramView());

		JPanel viewPanel = new JPanel();
		viewPanel.setLayout(new BorderLayout());
		//viewPanel.add(BorderLayout.PAGE_END, magView);
		viewPanel.add(BorderLayout.CENTER, view);

		
		JToolBar bar = new JToolBar();
		bar.add(view.getZoomInAction());
		bar.add(view.getZoomOutAction());
		bar.add(view.getZoomToFitAction());

		JFrame fr = new JFrame();
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = fr.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(BorderLayout.PAGE_START, bar);
		contentPane.add(viewPanel);
		
		
		fr.pack();
		fr.setSize(800, 600);
		fr.setVisible(true);
	}
}
