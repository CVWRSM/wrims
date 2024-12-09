/**
 * 
 */
package calsim.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;

/**
 * Class creates a <b>JPanel</b> version of the Calsim OAS GUI. The code is
 * modified from TabbedPane.java. <br>
 * <br>
 * "tabbed pane of Calsim OAS GUI, which contains main panel, schematic editor, etc."
 * <br>
 * "@version $Id: TabbedPane.java,v 1.1.2.14 2001/07/12 02:00:01 amunevar Exp $"
 * 
 * @author tslawecki
 * 
 */
public class CalLiteGUIPanelWrapper {

	// public static boolean DEBUG = true;

	/**
	 * constructor - creates a panel <s>tabbed pane</s>, which contains several
	 * tabs of main panel, schematic editor, etc.
	 */
	public CalLiteGUIPanelWrapper(JFrame fr) {
		_fr = fr;
		// _tabbedPane = createTabbedPane();
		_panel = createPanel();
	}

	/**
	 * create the tabbed pane (left as reference only)
	 */

	JTabbedPane createTabbedPane() {
		_tabbedPane = new JTabbedPane();
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new BorderLayout());
		jPanel.setBackground(new Color(229, 240, 203));
		// jPanel.add(GuiUtils.createStudyTab());

		_tabbedPane.addTab("Study   ", null, jPanel, "Study tabbed pane");
		_tabbedPane.addTab("Output    ", null, GuiUtils.createMainPanel(_fr),
				"OutPut panel");

		return _tabbedPane;
	}

	/**
	 * Parallel creation of panel only -- LimnoTech
	 * 
	 * @return
	 */
	JPanel createPanel() {

		// JPanel jPanel = new JPanel();
		// jPanel.setLayout(new BorderLayout());
		// jPanel.setBackground(new Color(229, 240, 203));
		// jPanel.add(GuiUtils.createStudyTab());
		// GuiUtils.createStudyTab(); // Created, but not displayed.

		_panel = new JPanel();
		_panel.setLayout(new BorderLayout());
		_panel.add(GuiUtils.createMainPanelCLG(_fr), BorderLayout.CENTER);
		
		return _panel;

	}

	/**
	 * Return the Node/Arc/General menu bar
	 */
	public JTabbedPane getTabbedPane() {
		return _tabbedPane;
	}

	public JPanel getPanel() {
		return _panel;
	}

	/**
   *
   */
	private JTabbedPane _tabbedPane;
	private JFrame _fr;
	private JPanel _panel; // LimnoTech
}
