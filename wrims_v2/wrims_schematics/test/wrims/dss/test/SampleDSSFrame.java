package wrims.dss.test;

import java.util.Vector;

import javax.swing.UIManager;

import wrims.dss.DssViewer;
import wrims.schematic.DssFrame;
import wrims.schematic.MainFrame;

public class SampleDSSFrame {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		DssFrame fr = new DssFrame(new MainFrame(args[0], true));
		Vector names = new Vector();
		names.add("I_SHSTA");
		fr.getFP().retrieve(DssViewer.VALUES, false, false, names);
		fr.setVisible(true);
	}
}
