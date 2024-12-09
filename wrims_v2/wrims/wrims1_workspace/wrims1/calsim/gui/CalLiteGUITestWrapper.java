/**
 * 
 */
package calsim.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import calsim.app.AppUtils;
import calsim.app.DerivedTimeSeries;

import vista.gui.VistaUtils;
import vista.set.DataReference;
import vista.set.Group;

/**
 * Class for testing WRIMS calculation wrapper
 * 
 * @author tslawecki
 * 
 */
public class CalLiteGUITestWrapper {

	private static JFrame frameBase = new JFrame(GuiUtils.getProgramName()); // version
																				// is
																				// already
																				// in
																				// About
																				// screen
	public static boolean DEBUG = true;
	public static int FRAME_WIDTH = 900;
	public static int FRAME_HEIGHT = 666;

	/**
	 * Main method used for testing WRIMS calculation wrapper
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new CalLiteGUITestWrapper();
	}

	public CalLiteGUITestWrapper() {
		Container pane = frameBase.getContentPane();
		pane.setBackground(new Color(207, 220, 200));
		pane.setLayout(new BorderLayout());
		CalLiteGUIPanelWrapper panel = new CalLiteGUIPanelWrapper(frameBase);

		// Put in custom action

		JButton retrieveBtn = (JButton) findFirstButtonWithLabel(
				GuiUtils.getCLGPanel(), "Retrieve");

		for (ActionListener al : retrieveBtn.getActionListeners()) {
			retrieveBtn.removeActionListener(al);
		}

		retrieveBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				retrieve();

			}
		});

		Component openButtonComponent = findFirstButtonWithLabel(
				GuiUtils.getCLGPanel(), "Open");
		if (openButtonComponent != null) {
			JButton openButton = (JButton) openButtonComponent;
			for (ActionListener al : openButton.getActionListeners()) {
				openButton.removeActionListener(al);
			}

			openButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					retrieve2();

				}

			});
		}

		pane.add(panel.getPanel(), BorderLayout.CENTER);
		pane.add(GuiUtils.getStatusPanel(), BorderLayout.SOUTH);

		frameBase.setSize(800, 800);
		frameBase.setExtendedState(JFrame.NORMAL);
		frameBase.setVisible(true);
		frameBase.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		new CalLiteGUIHelper();

	}

	/**
	 * Data retrieval modeled on calsim.gui.GeneratlRetrievePanel.retrieve()
	 * 
	 */
	void retrieve() {
		if (!AppUtils.baseOn) {
			JOptionPane.showMessageDialog(null,
					"The Base DSS files need to be selected",
					"DSS Not Selected", JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			String noRowsString = "";
			JTable _table = GuiUtils.getCLGPanel().getRetrievePanel()
					.getTable();
			if (_table.getRowCount() == 0)
				noRowsString = " after using \"Filter\" to load variables";
			Group _group = GuiUtils.getCLGPanel().getRetrievePanel().getGroup();
			if (_group == null || _table.getSelectedRowCount() == 0) {
				JOptionPane.showMessageDialog(null,
						"Select one or more variables" + noRowsString,
						"Variable(s) Not Selected",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			int[] rows = _table.getSelectedRows(); // checked if count > 0 above
			DataReference[] array = new DataReference[rows.length];
			for (int i = 0; i < rows.length; i++)
				array[i] = _group.getDataReference(rows[i]);
			// GuiUtils.displayData(array);
			for (int i = 0; i < rows.length; i++)
				System.out.println(array[i]);

		} catch (Exception e) {
			VistaUtils.displayException(GuiUtils.getMainPanel(), e);
		}
	}

	/**
	 * Method to find first JButton within a Swing container with indicated
	 * text. Used to find controls in WRIMS GUI.
	 * 
	 * @param comp
	 *            - containing component
	 * @param l
	 *            - string to match
	 * 
	 * @return JButton component inside container with text matching string, or
	 *         null if not found.
	 */

	public static Component findFirstButtonWithLabel(Component comp, String l) {
		if ((comp instanceof JButton)) {
			if (((JButton) comp).getText().equals(l))
				return comp;
		}

		if (comp instanceof Container) {
			Container container = (Container) comp;
			for (int i = 0; i < container.getComponentCount(); i++) {
				Component comp2 = findFirstButtonWithLabel(
						container.getComponent(i), l);
				if (comp2 != null)
					return comp2;
			}
		}
		return null;
	}

	void retrieve2() {
		
		

		GuiUtils.getCLGPanel().getDtsTreePanel().getTable().stopEditing();
		DerivedTimeSeries dts = GuiUtils.getCLGPanel().getDtsTreePanel().
				getTable().getDTS();
		
		System.out.println(dts.getName());
		Vector bParts = dts.getBParts();
		System.out.println(bParts.size());
		if (bParts.size() < 1) {
			JOptionPane.showMessageDialog(null,
					"Specify one or more variables",
					"Variable(s) Not Specified", JOptionPane.WARNING_MESSAGE);
			return;
		}

		Vector cParts = dts.getCParts();
		Vector opIDs = dts.getOpIds();

		for (int i = 0; i < bParts.size(); i++) {
			System.out.println(i + ": " + opIDs.get(i) + ":" + bParts.get(i)
					+ ": " + cParts.get(i));
		}

	}
}
