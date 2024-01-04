package wrims.schematic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * 
 * @author Tom Pruett primary author
 * @auther Clay Booher CB - this class should be rewritten for clarity -
 *         debugging it is a pain.
 * 
 */
public class MessagePanel {
	public static final boolean DEBUG = false;

	public static final int WATERYEAR = 0; // CB added
	public static final int CALENDAR_YEAR = 1; // CB added
	public static final int FEDERAL_CONTRACT_YEAR = 2; // CB added

	/** CB added */
	public static String[] _twSelections = { "OCT1921 - SEP2009","OCT1921 - SEP2003",
			"OCT1928 - SEP1934","OCT1986 - SEP1992","OCT1975 - SEP1977",
			"OCT1976 - SEP1977","OCT1994 - SEP2003","OCT2000 - SEP2009"};
	private int _annualType; // CB added

	private static Preferences _userPrefs; // CB added
	static {
		_userPrefs = Preferences.userNodeForPackage(MessagePanel.class);
		String tws = _userPrefs.get("timewindows", "OCT1921 - SEP2009,OCT1921 - SEP2003,OCT1928 - SEP1934,OCT1986 - SEP1992,OCT1975 - SEP1977,OCT1976 - SEP1977,OCT1994 - SEP2003,OCT2000 - SEP2009");
		_twSelections = tws.split(",");
	}
	public static String CHECKED = "checked";

	public static String[] labelText = {
			" Project Name: ",
			"  Mode: ", // CB this is a mess
			" TW:  ", " Base Files ", " DV:    ", "               SV: ",
			" Comp Files 1", " DV:    ", "               SV: ",
			"         Units:  ", " Comp Files 2", " DV:    ",
			"               SV: ", " Comp Files 3", " DV:    ",
			"               SV:", "    Annual Type: " }; // CB added last one

	// " SV: ",
	// " View: "
	// };

	MainPanel _mainPanel;

	DssAppAction _aa;

	Font f;

	JButton _basedv = null;

	JButton _basesv = null;

	JButton _comp1dv = null;

	JButton _comp1sv = null;

	JButton _comp2dv = null;

	JButton _comp2sv = null;

	JButton _comp3dv = null;

	JButton _comp3sv = null;

	JCheckBox _basebox = new JCheckBox();

	JCheckBox _comp1box = new JCheckBox();

	JCheckBox _comp2box = new JCheckBox();

	JCheckBox _comp3box = new JCheckBox();

	// CB TODO decide if want to set checkbox prefs here or in a method called
	// writeDssFileCheckedPreferences
	ItemListener al1 = new ItemListener() { // CB TODO, alter instance variables
											// to greatly reduce code here and
											// elsewhere in this class
		public void itemStateChanged(ItemEvent e) {
			AppAction.updateAllActions();
			if (e.getSource() == _basebox) { // CB added elses to prevent 3 (or
												// more later) "if" checks.
												// Later made the base box NOT
												// changeable
			// CB always checked now _userPrefs.putBoolean(getProjectName() +
			// CHECKED + "0", _basebox.isSelected()); //CB TO DO: use array of
			// checkboxes and index for String creation
				_mainPanel.stopMonthlyDataWork();
				_mainPanel.updateSchematicValues(); // _basebox is always
													// selected
			} else if (e.getSource() == _comp1box) {
				_userPrefs.putBoolean(getProjectName() + CHECKED + "1",
						_comp1box.isSelected()); // CB TO DO: use array of
													// checkboxes and index for
													// String creation
				_mainPanel.stopMonthlyDataWork();
				_mainPanel.updateSchematicValues(); // if selected or
													// unselected, need to
													// update alt. 2 values
													// (TODO - if files both
													// blank, maybe not)
			} else if (e.getSource() == _comp2box) {
				_userPrefs.putBoolean(getProjectName() + CHECKED + "2",
						_comp2box.isSelected()); // CB TO DO: use array of
													// checkboxes and index for
													// String creation
				if (!_comp1box.isSelected()) // if selected or unselected, need
												// to update alt. 2 values IF
												// _comp1box is NOT selected
												// (TODO - if files both blank,
												// maybe not)
					_mainPanel.stopMonthlyDataWork();
					_mainPanel.updateSchematicValues();
			} else if (e.getSource() == _comp3box) {
				_userPrefs.putBoolean(getProjectName() + CHECKED + "3",
						_comp3box.isSelected()); // CB TO DO: use array of
													// checkboxes and index for
													// String creation
				if (!_comp1box.isSelected() && !_comp2box.isSelected()) // if
																		// selected
																		// or
																		// unselected,
																		// need
																		// to
																		// update
																		// alt.
																		// 2
																		// values
																		// IF
																		// _comp1box
																		// AND
																		// _comp2box
																		// are
																		// NOT
																		// selected
																		// (TODO
																		// - if
																		// files
																		// both
																		// blank,
																		// maybe
																		// not)
					_mainPanel.stopMonthlyDataWork();
					_mainPanel.updateSchematicValues();
			}
		}
	};

	JRadioButton taf = new JRadioButton("TAF");

	JRadioButton cfs = new JRadioButton("CFS");

	JRadioButton base;

	JRadioButton comp;

	JRadioButton diff;

	JRadioButton wateryear; // CB added
	JRadioButton calendar; // CB added
	JRadioButton fedContract; // CB added

	JCheckBox plot = new JCheckBox("Plot");

	JCheckBox table = new JCheckBox("Table");

	JCheckBox monthly = new JCheckBox("Monthly");

	/*
	 * private variables
	 */
	JLabel labelNames[];

	private static JTextField _messages[];

	private JPanel _messagePanelComp;

	Vector<String> _twitems = new Vector<String>(1, 1);

	String _ProjectName = "";

	String _BaseDVFileName = "";

	String _Comp1DVFileName = "";

	String _Comp2DVFileName = "";

	String _Comp3DVFileName = "";

	String _BaseSVFileName = "";

	String _Comp1SVFileName = "";

	String _Comp2SVFileName = "";

	String _Comp3SVFileName = "";

	public MessagePanel(DssAppAction aa, MainPanel mainPanel, int schematicUnits) { // CB
																					// TODO
																					// call
																					// the
																					// other
																					// constrauctor
		_aa = aa;
		_messagePanelComp = createMessagePanel(schematicUnits);
		_mainPanel = mainPanel;
	}

	public MessagePanel(MainPanel mainPanel, int schematicUnits) {
		this(null,mainPanel,schematicUnits);
	}

	/**
	 * create active messages panel
	 */
	JPanel createMessagePanel(int schematicUnits) {
		int fontSize = 10;
		int numOfMessages = 17, numOfPanels = 5;
		JPanel panel = new JPanel();
		JPanel panel1 = new JPanel();
		// panel1.setLayout(new GridLayout(0,1,1,1));
		JPanel[] panels = new JPanel[numOfPanels];
		labelNames = new JLabel[labelText.length];
		_messages = new JTextField[numOfMessages];
		for (int i = 0; i < numOfPanels; i++)
			panels[i] = new JPanel();
		for (int i = 0; i < labelText.length; i++) {
			labelNames[i] = new JLabel(labelText[i]);
			f = labelNames[i].getFont();
			f = new Font(f.getName(), f.getStyle(), fontSize);
			labelNames[i].setFont(f);
			labelNames[i].setForeground(Color.black);
			if (i != 4 || i != 7) {
				labelNames[i].setSize(10, 10);
			} else {
				labelNames[i].setSize(10, 10);
			}
		}
		for (int i = 0; i < numOfMessages; i++) {
			_messages[i] = new JTextField(30);
			_messages[i].setBackground(Color.white);
			_messages[i].setEditable(false);
			Font f = _messages[i].getFont();
			f = new Font(f.getName(), f.getStyle(), fontSize);
			_messages[i].setFont(f);
			_messages[i].setMinimumSize(_messages[i].getPreferredSize());
			_messages[i].setHorizontalAlignment(JTextField.CENTER);
		}
		Dimension d = new Dimension(0, 0);
		_basedv = new JButton(_aa.BaseDVOpenAction);
		_basedv.setFont(f);
		_basedv.setMaximumSize(d);
		_basesv = new JButton(_aa.BaseSVOpenAction);
		_basesv.setFont(f);
		_basesv.setSize(8, 7);
		_comp1dv = new JButton(_aa.Comp1DVOpenAction);
		_comp1dv.setFont(f);
		_comp1dv.setSize(8, 7);
		_comp1sv = new JButton(_aa.Comp1SVOpenAction);
		_comp1sv.setFont(f);
		_comp1sv.setSize(8, 7);
		_comp2dv = new JButton(_aa.Comp2DVOpenAction);
		_comp2dv.setFont(f);
		_comp2dv.setSize(8, 7);
		_comp2sv = new JButton(_aa.Comp2SVOpenAction);
		_comp2sv.setFont(f);
		_comp2sv.setSize(8, 7);
		_comp3dv = new JButton(_aa.Comp3DVOpenAction);
		_comp3dv.setFont(f);
		_comp3dv.setSize(8, 7);
		_comp3sv = new JButton(_aa.Comp3SVOpenAction);
		_comp3sv.setFont(f);
		_comp3sv.setSize(8, 7);

		_basebox.setSelected(true); // CB changed back to always selected
		_basebox.setEnabled(false); // CB decided to make "Base" always selected
		_basebox.addItemListener(al1);
		_comp1box.addItemListener(al1);
		_comp2box.addItemListener(al1);
		_comp3box.addItemListener(al1);

		setTimeWindowMessage("");

		// CB Project row
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		panel.setLayout(gb);
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 0.0; // CB added
		gc.fill = GridBagConstraints.NONE; // CB added
		panel.add(labelNames[0], gc);

		gc.gridx = 1;
		gc.insets = new Insets(0, 2, 0, 3);
		panel.add(new JLabel("   "), gc);

		gc.gridx = 2;
		gc.fill = GridBagConstraints.HORIZONTAL; // CB added
		panel.add(new JLabel("     "), gc);

		gc.gridx = 3;
		gc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(_messages[0], gc); // CB project box

		// CB Base files row
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 1;
		gc.fill = GridBagConstraints.NONE; // CB added
		gc.weightx = 0.0; // CB added
		panel.add(labelNames[3], gc);

		gc.gridx = 1;
		gc.insets = new Insets(0, 2, 0, 3);
		panel.add(_basebox, gc);

		gc.gridx = 2;
		panel.add(_basedv, gc);

		gc.gridx = 3;
		gc.weightx = 0.5; // CB added
		gc.fill = GridBagConstraints.HORIZONTAL; // CB added
		panel.add(_messages[3], gc);

		gc.gridx = 4;
		gc.insets = new Insets(0, 2, 0, 3);
		gc.weightx = 0.0; // CB added
		gc.fill = GridBagConstraints.NONE; // CB added
		panel.add(_basesv, gc);

		gc.gridx = 5;
		gc.weightx = 0.5; // CB added
		gc.fill = GridBagConstraints.HORIZONTAL; // CB added
		panel.add(_messages[4], gc);

		// CB Comp 1 files row
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 2;
		gc.weightx = 0.0; // CB added
		gc.fill = GridBagConstraints.NONE; // CB added
		panel.add(labelNames[6], gc);

		gc.gridx = 1;
		gc.insets = new Insets(0, 2, 0, 2);
		panel.add(_comp1box, gc);

		gc.gridx = 2;
		panel.add(_comp1dv, gc);

		gc.gridx = 3;
		gc.weightx = 0.5; // CB added
		gc.fill = GridBagConstraints.HORIZONTAL; // CB added
		panel.add(_messages[5], gc);

		gc.gridx = 4;
		gc.insets = new Insets(0, 2, 0, 2);
		gc.weightx = 0.0; // CB added
		gc.fill = GridBagConstraints.NONE; // CB added
		panel.add(_comp1sv, gc);

		gc.gridx = 5;
		gc.weightx = 0.5; // CB added
		gc.fill = GridBagConstraints.HORIZONTAL; // CB added
		panel.add(_messages[6], gc);

		// CB Comp 2 files row
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 3;
		gc.weightx = 0.0; // CB added
		gc.fill = GridBagConstraints.NONE; // CB added
		panel.add(labelNames[10], gc);

		gc.gridx = 1;
		gc.insets = new Insets(0, 2, 0, 2);
		panel.add(_comp2box, gc);

		gc.gridx = 2;
		panel.add(_comp2dv, gc);

		gc.gridx = 3;
		// gc.weightx = 0.5; //CB added
		gc.fill = GridBagConstraints.HORIZONTAL; // CB added
		panel.add(_messages[10], gc);

		gc.gridx = 4;
		gc.insets = new Insets(0, 2, 0, 2);
		gc.weightx = 0.0; // CB added
		gc.fill = GridBagConstraints.NONE; // CB added
		panel.add(_comp2sv, gc);

		gc.gridx = 5;
		// gc.weightx = 0.5; //CB added
		gc.fill = GridBagConstraints.HORIZONTAL; // CB added
		panel.add(_messages[11], gc);

		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 4;
		gc.weightx = 0.0; // CB added
		gc.fill = GridBagConstraints.NONE; // CB added
		panel.add(labelNames[13], gc);

		gc.gridx = 1;
		gc.insets = new Insets(0, 2, 0, 2);
		panel.add(_comp3box, gc);

		gc.gridx = 2;
		panel.add(_comp3dv, gc);

		gc.gridx = 3;
		// gc.weightx = 1.0; //CB added
		gc.fill = GridBagConstraints.HORIZONTAL; // CB added
		panel.add(_messages[12], gc);

		gc.gridx = 4;
		gc.insets = new Insets(0, 2, 0, 2);
		gc.weightx = 0.0; // CB added
		gc.fill = GridBagConstraints.NONE; // CB added
		panel.add(_comp3sv, gc);

		gc.gridx = 5;
		gc.weightx = 0.5; // CB added
		gc.fill = GridBagConstraints.HORIZONTAL; // CB added
		panel.add(_messages[13], gc);

		/*
		 * gc.anchor = GridBagConstraints.WEST; gc.gridx = 0; gc.gridy = 3;
		 * panel.add(labelNames[9],gc);
		 * 
		 * gc.gridx = 1; //gc.gridwidth = GridBagConstraints.RELATIVE;
		 * panel.add(createUnitsPanel(),gc);
		 * 
		 * gc.gridx = 2; //gc.gridwidth = GridBagConstraints.RELATIVE;
		 * gc.weightx = 1.0; panel.add(labelNames[1],gc);
		 * 
		 * gc.gridx = 3; //gc.gridwidth = GridBagConstraints.RELATIVE;
		 * gc.weightx = 1.0; panel.add(createModePanel(),gc);
		 * 
		 * gc.gridx = 4; //gc.gridwidth = GridBagConstraints.RELATIVE;
		 * panel.add(labelNames[2],gc);
		 * 
		 * gc.gridx = 5; //gc.gridwidth = GridBagConstraints.RELATIVE;
		 * gc.weightx = 1.0; panel.add(createTWBox(),gc);
		 * 
		 * gc.gridx = 6; //gc.gridwidth = GridBagConstraints.RELATIVE;
		 * gc.weightx = 1.0; panel.add(labelNames[16],gc);
		 * 
		 * gc.gridx = 7; //gc.gridwidth = GridBagConstraints.REMAINDER;
		 * gc.weightx = 1.0; panel.add(createViewPanel(),gc);
		 */

		// panel.setLayout(new GridLayout(numOfPanels,1));
		// for (int i=0; i< numOfPanels; i++)
		// panel.add(panels[i]);
		/*
		 * panel1.setBorder( BorderFactory. createTitledBorder(
		 * BorderFactory.createMatteBorder(1,1,1,1,Color.blue), "") //"Message
		 * Panel") );
		 */
		panel1.setLayout(new GridBagLayout());
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1.0;
		panel1.add(panel, gc);
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(3, 0, 0, 0);
		gc.gridx = 0;
		gc.gridy = 1;
		panel1.add(createUtilsPanel(schematicUnits), gc);
		// /NodeArcMenuBar nodeArcMenuBar = new NodeArcMenuBar(_mp);
		// / _frame.setJMenuBar(nodeArcMenuBar.getMenuBar());
		// bPanel.add(panel,BorderLayout.CENTER);
		return panel1;
	}

	/**
	 *
	 */
	private void addInLine(Container mc, Component[] compArray) {
		mc.setLayout(new BoxLayout(mc, BoxLayout.X_AXIS));
		for (int i = 0; i < compArray.length; i++) {
			mc.add(compArray[i]);
			mc.add(Box.createHorizontalGlue());
		}
		mc.add(Box.createVerticalStrut(6));
	}

	public JPanel createUtilsPanel(int schematicUnits) {
		JPanel panel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		panel.setLayout(gb);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 0;
		panel.add(labelNames[1], gc);
		gc.gridx = 1;
		panel.add(createModePanel(), gc);
		gc.gridx = 2;
		// panel.add(labelNames[16],gc);
		panel.add(labelNames[16], gc); // CB put it back in
		gc.gridx = 3;
		// panel.add(createViewPanel(),gc);
		panel.add(createAnnualTypePanel(), gc); // CB added
		gc.gridx = 4;
		panel.add(new JLabel("            "), gc);
		gc.gridx = 5;
		panel.add(labelNames[2], gc);
		gc.gridx = 6;
		panel.add(createTWBox(), gc);
		gc.gridx = 7;
		panel.add(labelNames[9], gc);
		gc.gridx = 8;
		panel.add(createUnitsPanel(schematicUnits));
		return panel;
	}

	public JPanel createUnitsPanel(int schematicUnits) {
		JPanel panel = new JPanel();
		ButtonGroup g = new ButtonGroup();
//		if (schematicUnits == ISchematic.TAF)
//			taf.setSelected(true);
//		else if (schematicUnits == ISchematic.CFS)
//			cfs.setSelected(true);
		if (schematicUnits == ISchematic.CFS)
			cfs.setSelected(true);
		else 
			taf.setSelected(true);
		g.add(taf);
		g.add(cfs);
		taf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (DEBUG)
					System.out.println("TAF");
				// ctp if (taf.isSelected()) AppUtils.useUnits(AppUtils.TAF);
				_mainPanel.updateSchematicUnitsButtons(ISchematic.TAF);
				_mainPanel.stopMonthlyDataWork();
				_mainPanel.updateSchematicValues();
			}
		});
		cfs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (DEBUG)
					System.out.println("CFS");
				// ctp if (cfs.isSelected()) AppUtils.useUnits(AppUtils.CFS);
				_mainPanel.updateSchematicUnitsButtons(ISchematic.CFS);
				_mainPanel.stopMonthlyDataWork();
				_mainPanel.updateSchematicValues();
			}
		});
		taf.setFont(new Font(f.getName(), f.getStyle(), 10));
		cfs.setFont(new Font(f.getName(), f.getStyle(), 10));
		panel.setPreferredSize(new Dimension(100, 12));
		panel.setLayout(new GridLayout(1, 0));
		panel.add(taf);
		panel.add(cfs);
		return panel;
	}

	public JPanel createModePanel() {
		// base = new JRadioButton(_aa.BaseModeAction);
		comp = new JRadioButton(_aa.CompModeAction);
		diff = new JRadioButton(_aa.DiffModeAction);
		comp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_mainPanel.stopMonthlyDataWork();
				_mode="Comp";
				_mainPanel.updateSchematicValues();
			}
		});
		diff.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_mainPanel.stopMonthlyDataWork();
				_mode="Diff";
				_mainPanel.updateSchematicValues();
			}
		});
		
		JPanel panel = new JPanel();
		ButtonGroup g = new ButtonGroup();
		// g.add(base); g.add(comp); g.add(diff);
		g.add(comp);
		g.add(diff);
		// base.setFont(new Font(f.getName(),f.getStyle(),10));
		comp.setFont(new Font(f.getName(), f.getStyle(), 10));
		diff.setFont(new Font(f.getName(), f.getStyle(), 10));
		panel.setPreferredSize(new Dimension(125, 12));
		panel.setLayout(new GridLayout(1, 0));
		// panel.add(base); panel.add(comp); panel.add(diff);
		panel.add(comp);
		panel.add(diff);
		return panel;
	}

	public JPanel createAnnualTypePanel() {
		JPanel panel = new JPanel();
		ButtonGroup g = new ButtonGroup();
		wateryear = new JRadioButton(_aa.WateryearModeAction);
		calendar = new JRadioButton(_aa.CalendarYearModeAction);
		fedContract = new JRadioButton(_aa.FederalContractYearModeAction);
		g.add(wateryear);
		g.add(calendar);
		g.add(fedContract);
		if (getAnnualTypePreference() == CALENDAR_YEAR) {
			setAnnualType(CALENDAR_YEAR);
			g.setSelected(calendar.getModel(), true);
		} else if (getAnnualTypePreference() == FEDERAL_CONTRACT_YEAR) {
			setAnnualType(FEDERAL_CONTRACT_YEAR);
			g.setSelected(fedContract.getModel(), true);
		} else {
			setAnnualType(WATERYEAR);
			g.setSelected(wateryear.getModel(), true);
		}
		wateryear.setFont(new Font(f.getName(), f.getStyle(), 10));
		calendar.setFont(new Font(f.getName(), f.getStyle(), 10));
		fedContract.setFont(new Font(f.getName(), f.getStyle(), 10));
		panel.setPreferredSize(new Dimension(230, 12));
		panel.setLayout(new GridLayout(1, 0));
		panel.add(wateryear);
		panel.add(calendar);
		panel.add(fedContract);
		return panel;
	}

	public JComboBox createTWBox() {
		for (int i = 0; i < _twSelections.length; i++)
			_twitems.addElement(_twSelections[i]);
		final JComboBox twbox = new JComboBox(_twitems);
		twbox.setEditable(true);
		twbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox tb = (JComboBox) e.getSource();
				String tw = (String) tb.getSelectedItem();
				if (DEBUG)
					System.out.println(tw);
				_mainPanel.getFilterPanel().setTimeWindow(tw);
				// AppUtils.getCurrentProject().setTimeWindow(tw);

				if (!_mainPanel.dateBoxContainsTimeWindow(tw)) {
					((DefaultComboBoxModel)twbox.getModel())
					.insertElementAt(tw, 0);
					twbox.setSelectedIndex(0);
					_mainPanel.updateSchematicDateBox((String) tb.getModel()
							.getSelectedItem());
				}
				// System.out.print("");
			}
		});
		Dimension d = new Dimension(350, 17);
		twbox.setMinimumSize(d);
		twbox.setFont(new Font(f.getName(), f.getStyle(), 10));
		return twbox;
	}

	public JPanel createViewPanel() {
		JPanel panel = new JPanel();
		plot.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (DEBUG)
						System.out.println("Plot Selected");
					// ctp AppUtils.viewGraph = true;
				} else {
					if (DEBUG)
						System.out.println("Plot Deselected");
					// ctp AppUtils.viewGraph = false;
				}
			}
		});
		table.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (DEBUG)
						System.out.println("Table Selected");
					// ctp AppUtils.viewTable = true;
				} else {
					if (DEBUG)
						System.out.println("Table Deselected");
					// ctp AppUtils.viewTable = false;
				}
			}
		});
		monthly.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (DEBUG)
						System.out.println("Monthly Table Selected");
					// ctp AppUtils.viewMonthlyTable = true;
				} else {
					if (DEBUG)
						System.out.println("Monthly Table Deselected");
					// ctp AppUtils.viewMonthlyTable = false;
				}
			}
		});
		plot.setFont(new Font(f.getName(), f.getStyle(), 10));
		table.setFont(new Font(f.getName(), f.getStyle(), 10));
		monthly.setFont(new Font(f.getName(), f.getStyle(), 10));
		panel.setPreferredSize(new Dimension(165, 12));
		panel.setLayout(new GridLayout(1, 0));
		panel.add(plot);
		panel.add(table);
		panel.add(monthly);
		return panel;
	}

	/**
	 * Update message panel in main gui
	 */
	void updateMessagePanel() {
		setTimeWindowMessage(getTimeWindowString());
		// setModeMessage(getModeString());
		setProjectNameMessage(getProjectName());
		setDVFileMessage(getDVFilename());
		setSVFileMessage(getSVFilename());
		setDV2FileMessage(getDV2Filename());
		setSV2FileMessage(getSV2Filename());
		setDV3FileMessage(getDV3Filename());
		setSV3FileMessage(getSV3Filename());
		setDV4FileMessage(getDV4Filename());
		setSV4FileMessage(getSV4Filename());
		readDssFileCheckedPreferences(); // CB added
	}

	/**
	 * sets time window messages
	 */
	void setTimeWindowMessage(String str) {
		_messages[2].setText(str);
	}

	/**
	 *
	 */
	void setModeMessage(String str) {
		// if (str.equals("Base")) {
		// base.setSelected(true);
		// } else if (str.equals("Comp")) {
		if (str.equals("Comp")) {
			comp.setSelected(true);
		} else if (str.equals("Diff")) {
			diff.setSelected(true);
		}
		_mode = str;
		// _messages[1].setText(str);
	}

	/**
	 *
	 */
	void setProjectNameMessage(String str) {
		_messages[0].setText(str);
	}

	/**
	 *
	 */
	void setDVFileMessage(String str) {
		_messages[3].setText(str);
	}

	/**
	 *
	 */
	void setSVFileMessage(String str) {
		_messages[4].setText(str);
	}

	/**
	 *
	 */
	void setDV2FileMessage(String str) {
		_messages[5].setText(str);
	}

	/**
	 *
	 */
	void setSV2FileMessage(String str) {
		_messages[6].setText(str);
	}

	/**
	 *
	 */
	void setDV3FileMessage(String str) {
		_messages[10].setText(str);
	}

	/**
	 *
	 */
	void setSV3FileMessage(String str) {
		_messages[11].setText(str);
	}

	/**
	 *
	 */
	void setDV4FileMessage(String str) {
		_messages[12].setText(str);
	}

	/**
	 *
	 */
	void setSV4FileMessage(String str) {
		_messages[13].setText(str);
	}

	String getDVFilename() {
		return _BaseDVFileName;
	}

	String getSVFilename() {
		return _BaseSVFileName;
	}

	String getDV2Filename() {
		return _Comp1DVFileName;
	}

	String getSV2Filename() {
		return _Comp1SVFileName;
	}

	String getDV3Filename() {
		return _Comp2DVFileName;
	}

	String getSV3Filename() {
		return _Comp2SVFileName;
	}

	String getDV4Filename() {
		return _Comp3DVFileName;
	}

	String getSV4Filename() {
		return _Comp3SVFileName;
	}

	/**
	 *
	 */
	void setUnitsMessage(String str) {
		if (str.equals("CFS")) {
			cfs.setSelected(true);
		} else {
			taf.setSelected(true);
		}
		// _messages[7].setText(str);
	}

	/**
	 *
	 */
	public String getProjectName() {
		return _ProjectName;
	}

	void setProjectName(String nm) {
		_ProjectName = nm;
		// CB added block
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				_messages[0].setText(_ProjectName);
			}
		});
		readDssFileCheckedPreferences(); // CB added TODO - verify it is needed
											// here remove it if not)
	}

	/**
	 * get the time window that user modified from a panel.
	 */
	String getTimeWindowString() {
		// TimeWindow tw = AppUtils.getCurrentProject().getTimeWindow();
		return "TimeWindow";
		// return tw == null? " " : tw.getStartTime().toString().substring(2,9)
		// + " - " + tw.getEndTime().toString().substring(2,9);
	}

	/**
	 * get the message panel component
	 */
	public JPanel getMessagePanelComp() {
		return _messagePanelComp;
	}

	/**
	 * CB added - return a copy of the "built-in time windows of historical
	 * importance.
	 * 
	 * @return
	 */
	public Vector<String> getTimeWindowItemList() {
		return new Vector<String>(_twitems);
	}

	// //////////////////// tp code //////////////////////////
	// public HashMap<String, Object> getDSSFileNames(String type) {
	public HashMap<Integer, Object> getDSSFileNames(String type) { // CB TODO
																	// redo
																	// instance
																	// variables
																	// so much
																	// less code
																	// here and
																	// everywhere
																	// is
																	// necessary!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// CB HashMap<String, Object> hm = new HashMap<String, Object>(20); //CB
	// 5/2008 changed to size 20 (from default of 10) as it had bug noted below.
		HashMap<Integer, Object> hm = new HashMap<Integer, Object>(20); // CB
																		// 5/2008
																		// changed
																		// to
																		// size
																		// 20
																		// (from
																		// default
																		// of
																		// 10)
																		// as it
																		// had
																		// bug
																		// noted
																		// below.
		if (type.equals("DVAR") || type.equals("BOTH")) { // CB added BOTH
		// CB if (_basebox.isSelected())
			if (_basebox.isSelected() && !_BaseDVFileName.trim().equals("")) // CB
			// CB hm.put("Base", _BaseDVFileName);
				hm.put(FilterPanel.BASE_DV_FILE_INT / 4, _BaseDVFileName); // CB
				// CB if (_comp1box.isSelected())
			if (_comp1box.isSelected() && !_Comp1DVFileName.trim().equals("")) // CB)
			// CB hm.put("1", _Comp1DVFileName);
				hm.put(FilterPanel.COMP1_DV_FILE_INT / 4, _Comp1DVFileName); // CB
				// CB if (_comp2box.isSelected())
			if (_comp2box.isSelected() && !_Comp2DVFileName.trim().equals("")) // CB))
			// CB hm.put("2", _Comp2DVFileName); //CB Java 1.6 or Hashmap bug
			// when hm size = 10. This line was overwriting the HashMap entry
			// taken by hm.put("Base", _BaseDVFileName); line above!!!!!!!!!!!!!
				hm.put(FilterPanel.COMP2_DV_FILE_INT / 4, _Comp2DVFileName); // CB
				// CB if (_comp3box.isSelected())
			if (_comp3box.isSelected() && !_Comp3DVFileName.trim().equals("")) // CB)))
			// CB hm.put("3", _Comp3DVFileName);
				hm.put(FilterPanel.COMP3_DV_FILE_INT / 4, _Comp3DVFileName); // CB
		}
		if (type.equals("SVAR") || type.equals("BOTH")) { // CB added BOTH
		// CB if (_basebox.isSelected()) {
			if (_basebox.isSelected() && !_BaseSVFileName.trim().equals("")) { // CB
			// CB if (hm.get("Base") != null) { //CB added ability for > 1
			// associated file
				if (hm.get(FilterPanel.BASE_DV_FILE_INT / 4) != null) { // CB
																		// added
																		// ability
																		// for >
																		// 1
																		// associated
																		// file
					Vector<String> files = new Vector<String>(2);
					files.add(_BaseDVFileName);
					files.add(_BaseSVFileName);
					// CB hm.put("Base", files);
					hm.put(FilterPanel.BASE_DV_FILE_INT / 4, files); // CB
				} else {
					// CB hm.put("Base", _BaseSVFileName);
					hm.put(FilterPanel.BASE_SV_FILE_INT / 4, _BaseSVFileName); // CB
				}
			}

			// CB if (_comp1box.isSelected()) {
			if (_comp1box.isSelected() && !_Comp1SVFileName.trim().equals("")) { // CB
			// CB if (hm.get("1") != null) { //CB added ability for > 1
			// associated file
				if (hm.get(FilterPanel.COMP1_DV_FILE_INT / 4) != null) { // CB
																			// added
																			// ability
																			// for
																			// >
																			// 1
																			// associated
																			// file
					Vector<String> files = new Vector<String>(2);
					files.add(_Comp1DVFileName);
					files.add(_Comp1SVFileName);
					// CB hm.put("1", files);
					hm.put(FilterPanel.COMP1_DV_FILE_INT / 4, files); // CB
				} else {
					// CB hm.put("1", _Comp1SVFileName);
					hm.put(FilterPanel.COMP1_SV_FILE_INT / 4, _Comp1SVFileName); // CB
				}
			}
			// CB if (_comp2box.isSelected())
			if (_comp2box.isSelected() && !_Comp2SVFileName.trim().equals("")) { // CB)
			// if (hm.get("2") != null) { //CB added ability for > 1 associated
			// file
				if (hm.get(FilterPanel.COMP2_DV_FILE_INT / 4) != null) { // CB
																			// added
																			// ability
																			// for
																			// >
																			// 1
																			// associated
																			// file
					Vector<String> files = new Vector<String>(2);
					files.add(_Comp2DVFileName);
					files.add(_Comp2SVFileName);
					// CB hm.put("2", files);
					hm.put(FilterPanel.COMP2_DV_FILE_INT / 4, files); // CB
				} else {
					// CB hm.put("2", _Comp2SVFileName);
					hm.put(FilterPanel.COMP2_SV_FILE_INT / 4, _Comp2SVFileName); // CB
				}
			}
			// CB if (_comp3box.isSelected()) {
			if (_comp3box.isSelected() && !_Comp3SVFileName.trim().equals("")) { // CB)
			// CB if (hm.get("3") != null) { //CB added ability for > 1
			// associated file
				if (hm.get(FilterPanel.COMP3_DV_FILE_INT / 4) != null) { // CB
																			// added
																			// ability
																			// for
																			// >
																			// 1
																			// associated
																			// file
					Vector<String> files = new Vector<String>(2);
					files.add(_Comp3DVFileName);
					files.add(_Comp3SVFileName);
					// CB hm.put("3", files);
					hm.put(FilterPanel.COMP3_DV_FILE_INT / 4, files); // CB
				} else {
					// CB hm.put("3", _Comp3SVFileName);
					hm.put(FilterPanel.COMP3_SV_FILE_INT / 4, _Comp3SVFileName); // CB
				}
			}
		}
		return hm;
	}

	/**
	 * CB added to allow loading of the "rest" of the data for the monthyl value
	 * boxes in a separate thread.
	 * 
	 * @param type
	 * @return
	 */
	public HashMap<Integer, Object> getUnselectedDSSFileNames(String type) { // CB
																				// TODO
																				// redo
																				// instance
																				// variables
																				// so
																				// much
																				// less
																				// code
																				// here
																				// and
																				// everywhere
																				// is
																				// necessary!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		HashMap<Integer, Object> hm = new HashMap<Integer, Object>(20); // CB
																		// 5/2008
																		// changed
																		// to
																		// size
																		// 20
																		// (from
																		// default
																		// of
																		// 10)
																		// as it
																		// had
																		// bug
																		// noted
																		// below.
		if (type.equals("DVAR") || type.equals("BOTH")) { // CB added BOTH
			if (!_basebox.isSelected() && !_BaseDVFileName.trim().equals("")) // CB
				hm.put(FilterPanel.BASE_DV_FILE_INT / 4, _BaseDVFileName); // CB
			if (!_comp1box.isSelected() && !_Comp1DVFileName.trim().equals("")) // CB)
				hm.put(FilterPanel.COMP1_DV_FILE_INT / 4, _Comp1DVFileName); // CB
			if (!_comp2box.isSelected() && !_Comp2DVFileName.trim().equals("")) // CB))
				hm.put(FilterPanel.COMP2_DV_FILE_INT / 4, _Comp2DVFileName); // CB
			if (!_comp3box.isSelected() && !_Comp3DVFileName.trim().equals("")) // CB)))
				hm.put(FilterPanel.COMP3_DV_FILE_INT / 4, _Comp3DVFileName); // CB
		}
		if (type.equals("SVAR") || type.equals("BOTH")) { // CB added BOTH
			if (!_basebox.isSelected() && !_BaseSVFileName.trim().equals("")) { // CB
				if (hm.get(FilterPanel.BASE_DV_FILE_INT / 4) != null) { // CB
																		// added
																		// ability
																		// for >
																		// 1
																		// associated
																		// file
					Vector<String> files = new Vector<String>(2);
					files.add(_BaseDVFileName);
					files.add(_BaseSVFileName);
					hm.put(FilterPanel.BASE_SV_FILE_INT / 4, files); // CB
				} else {
					hm.put(FilterPanel.BASE_SV_FILE_INT / 4, _BaseSVFileName); // CB
				}
			}
			if (!_comp1box.isSelected() && !_Comp1SVFileName.trim().equals("")) { // CB
				if (hm.get(FilterPanel.COMP1_DV_FILE_INT / 4) != null) { // CB
																			// added
																			// ability
																			// for
																			// >
																			// 1
																			// associated
																			// file
					Vector<String> files = new Vector<String>(2);
					files.add(_Comp1DVFileName);
					files.add(_Comp1SVFileName);
					hm.put(FilterPanel.COMP1_SV_FILE_INT / 4, files); // CB
				} else {
					hm.put(FilterPanel.COMP1_SV_FILE_INT / 4, _Comp1SVFileName); // CB
				}
			}
			if (!_comp2box.isSelected() && !_Comp2SVFileName.trim().equals("")) { // CB)
				if (hm.get(FilterPanel.COMP2_DV_FILE_INT / 4) != null) { // CB
																			// added
																			// ability
																			// for
																			// >
																			// 1
																			// associated
																			// file
					Vector<String> files = new Vector<String>(2);
					files.add(_Comp2DVFileName);
					files.add(_Comp2SVFileName);
					hm.put(FilterPanel.COMP2_SV_FILE_INT / 4, files); // CB
				} else {
					hm.put(FilterPanel.COMP2_SV_FILE_INT / 4, _Comp2SVFileName); // CB
				}
			}
			if (!_comp3box.isSelected() && !_Comp3SVFileName.trim().equals("")) { // CB)
				if (hm.get(FilterPanel.COMP3_SV_FILE_INT / 4) != null) { // CB
																			// added
																			// ability
																			// for
																			// >
																			// 1
																			// associated
																			// file
					Vector<String> files = new Vector<String>(2);
					files.add(_Comp3DVFileName);
					files.add(_Comp3SVFileName);
					hm.put(FilterPanel.COMP3_SV_FILE_INT / 4, files); // CB
				} else {
					hm.put(FilterPanel.COMP3_SV_FILE_INT / 4, _Comp3SVFileName); // CB
				}
			}
		}
		return hm;
	}

	/*
	 * CB void setDSSFile(String fn, String dss) { if (DEBUG)
	 * System.out.println("setDSSFile() " + dss + " " + fn); if
	 * (dss.equals("BaseDV")) { _BaseDVFileName = fn; } else if
	 * (dss.equals("Comp1DV")) _Comp1DVFileName = fn; else if
	 * (dss.equals("Comp2DV")) _Comp2DVFileName = fn; else if
	 * (dss.equals("Comp3DV")) _Comp3DVFileName = fn; else if
	 * (dss.equals("BaseSV")) { _BaseSVFileName = fn; } else if
	 * (dss.equals("Comp1SV")) _Comp1SVFileName = fn; else if
	 * (dss.equals("Comp2SV")) _Comp2SVFileName = fn; else if
	 * (dss.equals("Comp3SV")) _Comp3SVFileName = fn;
	 * 
	 * updateMessagePanel(); }
	 */

	/**
	 * CB replaced old String checking method.
	 */
	void setDSSFile(String fn, int dssType) {
		if (DEBUG)
			System.out.println("setDSSFile() " + dssType + " " + fn);
		// CB TODO switch to a switch statement
		if (dssType == FilterPanel.BASE_DV_FILE_INT)
			_BaseDVFileName = fn;
		else if (dssType == FilterPanel.COMP1_DV_FILE_INT)
			_Comp1DVFileName = fn;
		else if (dssType == FilterPanel.COMP2_DV_FILE_INT)
			_Comp2DVFileName = fn;
		else if (dssType == FilterPanel.COMP3_DV_FILE_INT)
			_Comp3DVFileName = fn;
		else if (dssType == FilterPanel.BASE_SV_FILE_INT)
			_BaseSVFileName = fn;
		else if (dssType == FilterPanel.COMP1_SV_FILE_INT)
			_Comp1SVFileName = fn;
		else if (dssType == FilterPanel.COMP2_SV_FILE_INT)
			_Comp2SVFileName = fn;
		else if (dssType == FilterPanel.COMP3_SV_FILE_INT)
			_Comp3SVFileName = fn;

		updateMessagePanel();
	}

	void resetDSSFiles(String projectName) {
		_ProjectName = projectName;
		_BaseDVFileName = "";
		_Comp1DVFileName = "";
		_Comp2DVFileName = "";
		_Comp3DVFileName = "";
		_BaseSVFileName = "";
		_Comp1SVFileName = "";
		_Comp2SVFileName = "";
		_Comp3SVFileName = "";
		updateMessagePanel();
	}

	private String _mode = "Comp";

	public void setMode(String s) {
		_mode = s;
		AppAction.updateAllActions();
		if (DEBUG)
			System.out.println("Mode: " + s);
	}

	public String getMode() {
		return _mode;
	}

	public String getUnits() {
		if (cfs.isSelected())
			return "CFS";
		else
			return "TAF";
	}

	/**
	 * CB added.
	 * 
	 * @param units
	 */
	public void updateUnitsButtons(int units) {
		if (units == ISchematic.TAF) {
			if (!taf.isSelected())
				taf.setSelected(true);
		} else if (units == ISchematic.CFS) {
			if (!cfs.isSelected())
				cfs.setSelected(true);
		}
	}

	/**
	 * CB added.
	 * 
	 * @param annType
	 */
	void setAnnualType(int annType) {
		_annualType = annType;
		setAnnualTypePreference(annType);
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public int getAnnualType() {
		return _annualType;
	}

	/**
	 * CB added.
	 */
	private void readDssFileCheckedPreferences() {
		_comp1box.setSelected(_userPrefs.getBoolean(getProjectName() + CHECKED
				+ "1", true));
//		_comp1box.addItemListener(al1);
		_comp2box.setSelected(_userPrefs.getBoolean(getProjectName() + CHECKED
				+ "2", true));
//		_comp2box.addItemListener(al1);
		_comp3box.setSelected(_userPrefs.getBoolean(getProjectName() + CHECKED
				+ "3", true));
	}

	/**
	 * CB added to shorten the output times called by DSSViewer' show method.
	 * 
	 * @return
	 */
	// public String[] getSelectedFilesKeys() {
	public int[] getSelectedFilesKeys() {
		// Vector<String> selectedKeys = new Vector<String>();
		Vector<Integer> selectedKeys = new Vector<Integer>();
		// if (isBaseActive()) selectedKeys.add("Base");
		if (isBaseActive())
			selectedKeys.add(FilterPanel.BASE_DV_FILE_INT / 4);
		if (_comp1box.isSelected() && !_Comp1DVFileName.trim().equals(""))
			// selectedKeys.add("1");
			selectedKeys.add(FilterPanel.COMP1_DV_FILE_INT / 4);
		if (_comp2box.isSelected() && !_Comp2DVFileName.trim().equals(""))
			// selectedKeys.add("2");
			selectedKeys.add(FilterPanel.COMP2_DV_FILE_INT / 4);
		if (_comp3box.isSelected() && !_Comp3DVFileName.trim().equals(""))
			// selectedKeys.add("3");
			selectedKeys.add(FilterPanel.COMP3_DV_FILE_INT / 4);
		// String[] keys = new String[selectedKeys.size()];
		int[] keys = new int[selectedKeys.size()];
		for (int i = 0; i < keys.length; ++i) {
			keys[i] = selectedKeys.get(i);
		}
		return keys;
	}

	/**
	 * CB rewrote.
	 * 
	 * @return
	 */
	public boolean isBaseActive() {
		if (isDvarBaseActive() || isSvarBaseActive())
			return true;
		return false;
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public boolean isDvarBaseActive() {
		if (!_BaseDVFileName.equals("") && _basebox.isSelected())
			return true;
		return false;
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public boolean isSvarBaseActive() {
		if (!_BaseSVFileName.equals("") && _basebox.isSelected())
			return true;
		return false;
	}

	public boolean isCompMode() {
		if (comp.isSelected())
			return true;
		return false;
	}

	/**
	 * CB added to assist the following "Active" methods.
	 * 
	 * @return
	 */
	private boolean areArgumentNumberOrMoreDVFilesActive(int number) {
		if (number <= 0) // In case of a meaningless argument
			return true;
		int numActive = 0;
		if (_basebox.isSelected() && !_BaseDVFileName.trim().equals("")) // it
																			// is
																			// empty!!!!!!!!!!!!!!!!!!!!!!!!!
			++numActive;
		if (numActive == number)
			return true;
		if (_comp1box.isSelected() && !_Comp1DVFileName.trim().equals(""))
			++numActive;
		if (numActive == number)
			return true;
		if (_comp2box.isSelected() && !_Comp2DVFileName.trim().equals(""))
			++numActive;
		if (numActive == number)
			return true;
		if (_comp3box.isSelected() && !_Comp3DVFileName.trim().equals(""))
			++numActive;
		if (numActive == number)
			return true;
		return false;
	}

	/**
	 * CB added to assist the following "Active" methods.
	 * 
	 * @return
	 */
	private boolean areArgumentNumberOrMoreSVFilesActive(int number) {
		if (number <= 0) // In case of a meaningless argument
			return true;
		int numActive = 0;
		if (_basebox.isSelected() && !_BaseSVFileName.trim().equals(""))
			++numActive;
		if (numActive == number)
			return true;
		if (_comp1box.isSelected() && !_Comp1SVFileName.trim().equals(""))
			++numActive;
		if (numActive == number)
			return true;
		if (_comp2box.isSelected() && !_Comp2SVFileName.trim().equals(""))
			++numActive;
		if (numActive == number)
			return true;
		if (_comp3box.isSelected() && !_Comp3SVFileName.trim().equals(""))
			++numActive;
		if (numActive == number)
			return true;
		return false;
	}

	/**
	 * CB added.
	 */
	public boolean isCompActive() {
		if (isDvarCompActive() || isSvarCompActive())
			return true;
		return false;
	}

	/*
	 * CB public boolean isDiffActive() { //CB altered to not require a "base"
	 * file if (!_BaseDVFileName.equals("") && _basebox.isSelected() &&
	 * !_Comp1DVFileName.equals("")) return true; else if
	 * (!_BaseSVFileName.equals("") && _basebox.isSelected() &&
	 * !_Comp1SVFileName.equals("")) return true; setModeMessage("Comp"); return
	 * false; }
	 */

	/**
	 * CB altered to not require a "base" file.
	 */
	public boolean isDiffActive() {
		if (isDvarDiffActive() || isSvarDiffActive())
			return true;
		comp.setSelected(true);
		if (!getMode().equals("Comp"))
			setMode("Comp"); // CB TODO either change these text modes to int
								// (or a single boolean because there are two)
								// have the mode detected at output time
		return false;
	}

	/*
	 * CB public boolean isDvarCompActive() { if (!_BaseDVFileName.equals("") &&
	 * _basebox.isSelected()) return true; if (!_Comp1DVFileName.equals("") &&
	 * _comp1box.isSelected()) return true; if (!_Comp2DVFileName.equals("") &&
	 * _comp2box.isSelected()) return true; if (!_Comp3DVFileName.equals("") &&
	 * _comp3box.isSelected()) return true; return false; }
	 */

	/**
	 * CB altered.
	 */
	public boolean isDvarCompActive() {
		if (areArgumentNumberOrMoreDVFilesActive(1))
			return true;
		return false;
	}

	/*
	 * CB public boolean isSvarCompActive() { if (!_BaseSVFileName.equals("") &&
	 * _basebox.isSelected()) return true; if (!_Comp1SVFileName.equals("") &&
	 * _comp1box.isSelected()) return true; if (!_Comp2SVFileName.equals("") &&
	 * _comp2box.isSelected()) return true; if (!_Comp3SVFileName.equals("") &&
	 * _comp3box.isSelected()) return true; return false; }
	 */

	/**
	 * CB altered.
	 */
	public boolean isSvarCompActive() {
		if (areArgumentNumberOrMoreSVFilesActive(1))
			return true;
		return false;
	}

	/*
	 * CB public boolean isDvarDiffActive() { if (_BaseDVFileName.equals("") ||
	 * !_basebox.isSelected()) return false; if (!_Comp1DVFileName.equals("") &&
	 * _comp1box.isSelected()) return true; if (!_Comp2DVFileName.equals("") &&
	 * _comp2box.isSelected()) return true; if (!_Comp3DVFileName.equals("") &&
	 * _comp3box.isSelected()) return true; return false; }
	 */

	/**
	 * CB altered.
	 */
	public boolean isDvarDiffActive() {
		if (areArgumentNumberOrMoreDVFilesActive(2))
			return true;
		return false;
	}

	/*
	 * CB public boolean isSvarDiffActive() { if (_BaseSVFileName.equals("") ||
	 * !_basebox.isSelected()) return false; if (!_Comp1SVFileName.equals("") &&
	 * _comp1box.isSelected()) return true; if (!_Comp2SVFileName.equals("") &&
	 * _comp2box.isSelected()) return true; if (!_Comp3SVFileName.equals("") &&
	 * _comp3box.isSelected()) return true; return false; }
	 */

	public boolean isSvarDiffActive() {
		if (areArgumentNumberOrMoreSVFilesActive(2))
			return true;
		return false;
	}

	public HashMap getFileNames() {
		HashMap hm = new HashMap();
		hm.put("BaseDV", _BaseDVFileName);
		hm.put("Comp1DV", _Comp1DVFileName);
		hm.put("Comp2DV", _Comp2DVFileName);
		hm.put("Comp3DV", _Comp3DVFileName);
		hm.put("BaseSV", _BaseSVFileName);
		hm.put("Comp1SV", _Comp1SVFileName);
		hm.put("Comp2SV", _Comp2SVFileName);
		hm.put("Comp3SV", _Comp3SVFileName);
		return hm;
	}

	// CB added
	public void setAnnualTypePreference(int annualType) {
		_userPrefs.putInt("annualType", annualType);
	}

	// CB added
	public int getAnnualTypePreference() {
		return _userPrefs.getInt("annualType", _annualType);
	}
	
	
	public void editTimeWindows(){
		JTable table = new JTable(new DefaultTableModel(new Object[][]{_twSelections}, new Object[]{"Time Windows"}));
		JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(_mainPanel), "Edit Time Windows", ModalityType.MODELESS.APPLICATION_MODAL);
		JButton done = new JButton("Done");
		dialog.getContentPane().add(table);
		dialog.getContentPane().add(done);
		dialog.setVisible(true);

		int nrows = table.getModel().getRowCount();
		ArrayList<String> tws = new ArrayList<String>();
		for(int i=0; i < nrows; i++){
			String valueAt = table.getModel().getValueAt(i, 0).toString();
			if (valueAt==null || valueAt.length()==0){
				continue;
			}
			tws.add(valueAt);
		}
		_twSelections = new String[tws.size()];
		tws.toArray(_twSelections);

		String value = "";
		for(String tw: _twSelections){
			value+=tw+",";
		}
		_userPrefs.put("timewindows", value);
	}

} // end of class MessagePanel
