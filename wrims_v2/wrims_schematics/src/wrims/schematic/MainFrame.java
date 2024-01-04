package wrims.schematic;

import hec.heclib.dss.HecDSSFileAccess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import org.jfree.ui.tabbedui.VerticalLayout;

import vista.gui.VistaUtils;
import wrims.dss.DssViewer;
import wrims.schematic.element.Element;
import wrims.schematic.jdiagram.ElementTask;
import wrims.schematic.jdiagram.ImageUtil;
import wrims.schematic.jdiagram.SchematicViewer;

public class MainFrame extends JPanel implements Runnable, DocumentListener,
		ISchematic { // TextListener
	private static final boolean DEBUG = true;

	private SchematicViewer _viewer;

	public MainFrame() {
		this("", false);
	}

	public MainFrame(final String mainDir, boolean isStandalone) {
		HecDSSFileAccess.setMessageLevel(_messageLevel);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			mainFrame = new JFrame();
			_viewer = new SchematicViewer();
			_viewer.setSchematic(this);
			Cursor mainCursor = mainFrame.getCursor();
			mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try {
				long ti = System.currentTimeMillis();

				// check previously loaded schematics

				Preferences preference = Preferences.userNodeForPackage(
						MainFrame.class);
				
				String filename;
				if (preference.getBoolean("firsttime", true)){
					filename = mainDir + "/wrims/schematic/CS3_NetworkSchematic.xml";
				}else{
					filename = preference.get("last.schematic",
							mainDir + "/wrims/schematic/CS3_NetworkSchematic.xml");
				}
				preference.putBoolean("firsttime", false);
				
				_viewer.load(filename);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mainFrame.setCursor(mainCursor);
			}
			
			_viewer.setClickTask(new ElementTask() {

				@Override
				public void run(Element el) {
					Vector<String> names = new Vector<String>(Arrays.asList(el
							.getName()));
					if (_DssFrame != null) {
						_DssFrame.getFP().retrieve(DssViewer.PLOT, false,
								false, names);
					}
				}
			});

			_mainDir = mainDir;
			_isStandalone = isStandalone;

			mainMenuBar = new JMenuBar();
			createRecentFilesOpenActions(); // CB added

			// CB toolBar = new JToolBar();
			filemenu = new JMenu();
			editmenu = new JMenu();
			viewmenu = new JMenu();
			dssmenu = new JMenu();
			propertyMenu = new JMenu(); // CB added
			insertmenu = new JMenu();
			layoutmenu = new JMenu();
			helpmenu = new JMenu();

			initMenus();
			// CB initToolbar();
			toolBar1 = initToolbar1(); // CB renamed toolbar
			toolBar2 = initToolbar2(); // CB added

			myDesktop = new JDesktopPane();
			this.setLayout(new BorderLayout());

			// CB added section for single period data updating
			JPanel barPanel = new JPanel();
			// CB barPanel.setLayout(new BorderLayout());
			// CB barPanel.add(toolBar, "West");
			barPanel.setLayout(new BoxLayout(barPanel, BoxLayout.Y_AXIS)); // CB
			barPanel.add(toolBar1); // CB renamed
			barPanel.add(toolBar2); // CB added

			// CB contentPane.add(toolBar, "North");
			tabbedPane = new JTabbedPane();
			this.add(tabbedPane);

			inputPane = new InputPanel(tabbedPane);
			tabbedPane.add(inputPane, "Input");

			consolePane = new ConsolePanel(new JTextArea());
			tabbedPane.add(consolePane, "Console");

//			toolsPane = new ToolPanel();
//			tabbedPane.add(toolsPane, "Tools");//out-of-date, removed by LHZ on 11/27/2013 
			// batchPane = new BatchPanel(tabbedPane);
			// tabbedPane.add(batchPane, "Batch");

			outputPane = new JPanel();
			tabbedPane.add(outputPane, "Output");
			outputPane.setLayout(new BorderLayout());
			outputPane.add(barPanel, "North");

			outputPane.add(_viewer, "Center");
			outputPane.validate();

			// CB moved the below here from main(args)
			theApp = mainFrame;

			mainFrame.setTitle("WRIMS Schematic");
			mainFrame.setSize(new Dimension(800, 600));

			mainFrame.setTitle("WRIMS V2");
			Container contentPane2 = mainFrame.getContentPane();
			contentPane2.setLayout(new BorderLayout());
			contentPane2.add("Center", this);
			contentPane2.validate();

			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			mainFrame.setVisible(true);

			if (!MainFrame.IS_DEVELOPER) {
				SchematicRelatedAction.updateAllActions();
			}

			start();

		} catch (Throwable t) {
			System.err.println(t);
			t.printStackTrace();
		}
	}

	/**
	 * CB added.
	 */
	public void changedUpdate(DocumentEvent e) {
		// ? loadDateBox();
	}

	/**
	 * CB added.
	 */
	public void removeUpdate(DocumentEvent e) {
		// No action needed
	}

	/**
	 * CB added.
	 */
	public void insertUpdate(DocumentEvent e) {
		// ? loadDateBox();
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	private String getSelectedDate() {
		if (_dateBox.getSelectedItem() instanceof ComboItem) {
			return ((ComboItem) _dateBox.getSelectedItem()).toString();
		} else { // must be a String
			return (String) _dateBox.getSelectedItem();
		}
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public Vector<String> getTimeWindows() {
		Vector<String> timeWindows = new Vector<String>();
		for (int i = 0; i < ((DefaultComboBoxModel) _dateBox.getModel())
				.getSize(); ++i) {
			if (((DefaultComboBoxModel) _dateBox.getModel()).getElementAt(i) instanceof ComboItem) {
				String period = ((ComboItem) ((DefaultComboBoxModel) _dateBox
						.getModel()).getElementAt(i)).toString();
				if (period.indexOf("-") > -1) {
					timeWindows.add(period);
				}
			}
		}
		return timeWindows;
	}

	public SchematicViewer getViewer() {
		return _viewer;
	}

	/**
	 *
	 */
	private void createRecentFilesOpenActions() {
		// createRecentSchematicFilesOpenActions();
		createRecentProjectFilesOpenActions();
		createRecentPropertyDirectoriesOpenActions();
	}

	private void createRecentFilesOpenActions(int type) {
		if (type == SCHEMATIC_TYPE) {
			createRecentSchematicFilesOpenActions();
		} else if (type == PROJECT_TYPE) {
			createRecentProjectFilesOpenActions();
		} else if (type == PROPERTIES_TYPE) {
			createRecentPropertyDirectoriesOpenActions();
		} else {
			JOptionPane.showMessageDialog(this,
					"Schematic or Project file types allowed",
					"Incorrect File Type", JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * CB - added
	 * 
	 */
	private void createRecentSchematicFilesOpenActions() { // CB added
		for (int i = 0; i < NUMBER_OF_RECENT_FILES_OPENED; ++i) {
			RecentSchematicFilesOpenActions[i] = createSchematicOpenAction(i + 1);
		}
	}

	private void setRecentFileOpenActions(int type) { // CB added
		if (type == SCHEMATIC_TYPE) {
			setRecentSchematicFileOpenActions();
		} else if (type == PROJECT_TYPE) {
			setRecentProjectFileOpenActions();
		} else if (type == PROPERTIES_TYPE) {
			setRecentPropertyDirectoriesOpenActions();
		}
	}

	private void setRecentSchematicFileOpenActions() { // CB added
		setRecentSchematicFileOpenActions(false);
	}

	/**
	 * 
	 * @param isFirstTime
	 */
	private void setRecentSchematicFileOpenActions(boolean isFirstTime) { // CB
		// added
		for (int i = 0; i < NUMBER_OF_RECENT_FILES_OPENED; ++i) {
			if (recentSchematicFilesOpenMenuItems[i] == null) {
				recentSchematicFilesOpenMenuItems[i] = new JMenuItem();
			}
			// CB TO DO: need to add null checks?
			recentSchematicFilesOpenMenuItems[i]
					.setAction(RecentSchematicFilesOpenActions[i]);
			if (RecentSchematicFilesOpenActions[i] == null) {
				recentSchematicFilesOpenMenuItems[i].setVisible(false);
			} else {
				recentSchematicFilesOpenMenuItems[i].setVisible(true);
			}
			if (isFirstTime) {
				// if (RecentFilesOpenActions[i] != null) // THIS LINE WORKS,
				// BUT IF SEPARATOR IS NOT ADDED, THEN IS NOT THERE AFTER FIRST
				// FILE IS OPENED
				if (i == 0) {
					filemenu.addSeparator();
				}
				filemenu.add(recentSchematicFilesOpenMenuItems[i]);
				// if (RecentFilesOpenActions[i] == null)
				// recentFilesOpenMenuItems[i].setVisible(false);
			} else {
				Component[] comps = filemenu.getPopupMenu().getComponents();
				for (int j = 0; j < comps.length; ++j) {
					if (comps[j] == recentSchematicFilesOpenMenuItems[i]) { // CB
						// TODO
						// is
						// there
						// a
						// better
						// way
						// to
						// do
						// this
						// -
						// there
						// should
						// be!
						filemenu.remove(comps[j]);
						filemenu
								.insert(recentSchematicFilesOpenMenuItems[i], j);
						break;
					}
				}
			}
		}
		filemenu.revalidate();
		filemenu.repaint();
	}

	/**
	 * CB - added
	 * 
	 */
	private void createRecentProjectFilesOpenActions() {
		for (int i = 0; i < NUMBER_OF_RECENT_FILES_OPENED; ++i) {
			RecentProjectFilesOpenActions[i] = createProjectOpenAction(i + 1);
		}
	}

	private void setRecentProjectFileOpenActions() { // CB added
		setRecentProjectFileOpenActions(false);
	}

	/**
	 * 
	 * @param isFirstTime
	 */
	private void setRecentProjectFileOpenActions(boolean isFirstTime) { // CB
		// added
		for (int i = 0; i < NUMBER_OF_RECENT_FILES_OPENED; ++i) {
			if (recentProjectFilesOpenMenuItems[i] == null) {
				recentProjectFilesOpenMenuItems[i] = new JMenuItem();
			}
			recentProjectFilesOpenMenuItems[i]
					.setAction(RecentProjectFilesOpenActions[i]);
			if (RecentProjectFilesOpenActions[i] == null) {
				recentProjectFilesOpenMenuItems[i].setVisible(false);
			} else {
				recentProjectFilesOpenMenuItems[i].setVisible(true);
			}
			if (isFirstTime) {
				// if (RecentFilesOpenActions[i] != null) // THIS LINE WORKS,
				// BUT IF SEPARATOR IS NOT ADDED, THEN IS NOT THERE AFTER FIRST
				// FILE IS OPENED
				if (i == 0) {
					dssmenu.addSeparator();
				}
				dssmenu.add(recentProjectFilesOpenMenuItems[i]);
				// if (RecentFilesOpenActions[i] == null)
				// recentFilesOpenMenuItems[i].setVisible(false);
			}
		}
	}

	/**
	 * CB added to remove a bad project name menu item.
	 * 
	 * @param filename
	 */
	/*
	 * public void removeProjectFileOpenAction(final String filename) {
	 * SwingUtilities.invokeLater(new Runnable() { public void run() {
	 * Component[] components = dssmenu.getMenuComponents(); //
	 * recentProjectFilesOpenMenuItems[i] for (int i = 0; i < components.length;
	 * ++i) { if (components[i] instanceof JMenuItem) { if
	 * (((JMenuItem)components
	 * [i]).getText().substring(2).trim().equals(filename)) {
	 * dssmenu.remove(components[i]); break; } } } } }); }
	 */

	/**
	 * CB - added
	 * 
	 */
	private void createRecentPropertyDirectoriesOpenActions() {
		for (int i = 0; i < NUMBER_OF_RECENT_FILES_OPENED; ++i) {
			RecentPropertyDirectoriesOpenActions[i] = createPropertiesOpenAction(i + 1);
		}
	}

	private void setRecentPropertyDirectoriesOpenActions() { // CB added
		setRecentPropertyDirectoriesOpenActions(false);
	}

	/**
	 * 
	 * @param isFirstTime
	 */
	private void setRecentPropertyDirectoriesOpenActions(boolean isFirstTime) { // CB
		// added
		for (int i = 0; i < NUMBER_OF_RECENT_FILES_OPENED; ++i) {
			if (recentPropertyDirectoryOpenMenuItems[i] == null) {
				recentPropertyDirectoryOpenMenuItems[i] = new JMenuItem();
			}
			recentPropertyDirectoryOpenMenuItems[i]
					.setAction(RecentPropertyDirectoriesOpenActions[i]); // CB
			// TO
			// DO:
			// need
			// to
			// add
			// nulls??
			if (RecentPropertyDirectoriesOpenActions[i] == null) {
				recentPropertyDirectoryOpenMenuItems[i].setVisible(false);
			} else {
				recentPropertyDirectoryOpenMenuItems[i].setVisible(true);
			}
			if (isFirstTime) {
				// if (RecentFilesOpenActions[i] != null) // THIS LINE WORKS,
				// BUT IF SEPARATOR IS NOT ADDED, THEN IS NOT THERE AFTER FIRST
				// FILE IS OPENED
				if (i == 0) {
					dssmenu.addSeparator();
				}
				propertyMenu.add(recentPropertyDirectoryOpenMenuItems[i]);
				// if (RecentFilesOpenActions[i] == null)
				// recentFilesOpenMenuItems[i].setVisible(false);
			}
		}
	}

	// ==============================================================
	// Define all the command actions
	// ==============================================================
	static public Icon createIconImage(String resourceName) { // CB changed to
		// public and
		// renamed

		URL url = MainFrame.class.getResource(resourceName); // CB need path
		// to have
		// D:\WRIMS1Development\Calsim1.3\wrims\Schematic\images
		if (url == null) {
			if (!imagesErrorDisplayed) {
				JOptionPane.showMessageDialog(null, "Image file: "
						+ resourceName + " not found.", "alert",
						JOptionPane.ERROR_MESSAGE);
				imagesErrorDisplayed = true;
			}
		} else {
			return new ImageIcon(url);
		}
		return null;
	}

	SchematicRelatedAction FileOpenAction = null;
	{
		// Icon icon = iconImage("images/open.gif");
		Icon icon = createIconImage("images/Open24.gif");
		FileOpenAction = new SchematicRelatedAction("Open Schematic", icon,
				this) {
			public void actionPerformed(ActionEvent e) {
				String filename = chooseFileToOpen();
				if (filename == null) {
					return;
				}
				SchematicViewer view = getCurrentView();
				try {
					view.load(filename);

					Preferences.userNodeForPackage(MainFrame.class).put(
							"last.schematic", filename);

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			public boolean canAct() {
				return true;
			}
		}; // doesn't depend on a view
	}

	SchematicRelatedAction[] RecentSchematicFilesOpenActions = new SchematicRelatedAction[NUMBER_OF_RECENT_FILES_OPENED];

	SchematicRelatedAction FileCloseAction = new SchematicRelatedAction(
			"Close", this) {
		public void actionPerformed(ActionEvent e) {
			closeSchematic();
		}
	};

	SchematicRelatedAction FileSaveAction = null;
	{
		Icon icon = createIconImage("images/Save24.gif");
		FileSaveAction = new SchematicRelatedAction("Save Schematic", icon,
				this) {
			public void actionPerformed(ActionEvent e) {
				saveSchematic();
			}

			public boolean canAct() {
				return true;
			}
		}; // doesn't depend on a view
	}

	SchematicRelatedAction FileSaveAsAction = null;
	{
		FileSaveAsAction = new SchematicRelatedAction("Save Schematic As", this) {
			public void actionPerformed(ActionEvent e) {
				String fileToSave = chooseFileToSave();
				saveAsSchematic(fileToSave);
			}
		};
	}

	SchematicRelatedAction ObjectPropertiesAction = new SchematicRelatedAction(
			"Object Properties", this) {
		public void actionPerformed(ActionEvent e) {
			getView().getProperties();
		}

		public boolean canAct() {
			return true;
		}
	};

	// CB added TODO: make an [XXXXX]Action class which extends
	// DssFrameRelatedAction and is extended by the next four classes??:
	SchematicRelatedAction SingleStepValuesAction = new DssFrameRelatedAction(
			"Single Step Values", null, this) {
		public void actionPerformed(ActionEvent e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if (e.getSource() instanceof JComboBox) {
				if (((JComboBox) e.getSource()).getSelectedItem() instanceof ComboItem) {
					// CB I should have done this more thoroughly (ComboItem,
					// ComboListener, ComboRenderer but in a hurry
					if (((JComboBox) e.getSource()).getSelectedIndex() == 0) {
						int firstIndex = -1;
						for (int i = 0; i < ((JComboBox) e.getSource())
								.getItemCount(); ++i) {
							if (((ComboItem) ((JComboBox) e.getSource())
									.getItemAt(i)).toString().equalsIgnoreCase(
									"OCT 1921")) {
								firstIndex = i;
								break;
							}
						}
						((JComboBox) e.getSource())
								.setSelectedIndex(firstIndex); // does not
						// appear to
						// fire
						// ActionEvent!
						// So ...
						if (((ComboItem) ((JComboBox) e.getSource())
								.getSelectedItem()).isEnabled()) {
							// String date =
							// ((ComboItem)((JComboBox)e.getSource()).getSelectedItem()).toString();;
							// updateValues(date);
							updateValues();
						}
					} else {
						if (((ComboItem) ((JComboBox) e.getSource())
								.getSelectedItem()).isEnabled()) {
							// String date =
							// ((ComboItem)((JComboBox)e.getSource()).getSelectedItem()).toString();;
							// updateValues(date);
							updateValues();
						}
					}
				}
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		public boolean canAct() {
			return super.canAct() && (_DssFrame != null)
					&& _DssFrame.getFP().isFilterValid(); // &&
			// _DssFrame.getFP().rowsSelected();
		}
	};

	SchematicRelatedAction Forward1Action = new DssFrameRelatedAction("",
			createIconImage("images/toolbar/forward.png"), this) {

		public void actionPerformed(ActionEvent e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JComboBox dateBox = _dateBox;
			int firstIndex = -1;
			for (int i = 0; i < dateBox.getItemCount(); ++i) {
				// System.out.println(dateBox.getItemAt(i));
				if (((ComboItem) dateBox.getItemAt(i)).toString()
						.equalsIgnoreCase("OCT 1921")) {
					firstIndex = i;
					break;
				}
			}
			if (dateBox.getSelectedIndex() < firstIndex) {
				dateBox.setSelectedIndex(firstIndex);
			} else if (dateBox.getSelectedIndex() < dateBox.getItemCount() - 1) {
				dateBox.setSelectedIndex(dateBox.getSelectedIndex() + 1);
			}

			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		public boolean canAct() {
			return super.canAct() && (_DssFrame != null)
					&& _DssFrame.getFP().isFilterValid();
		}
	};

	SchematicRelatedAction Back1Action = new DssFrameRelatedAction("",
			createIconImage("images/toolbar/backward.png"), this) {
		public void actionPerformed(ActionEvent e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JComboBox dateBox = _dateBox;
			int firstIndex = -1;
			for (int i = 0; i < dateBox.getItemCount(); ++i) {
				if (((ComboItem) dateBox.getItemAt(i)).toString()
						.equalsIgnoreCase("OCT 1921")) {
					firstIndex = i;
					break;
				}
			}
			if (dateBox.getSelectedIndex() > firstIndex) {
				dateBox.setSelectedIndex(dateBox.getSelectedIndex() - 1);
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		public boolean canAct() {
			return super.canAct() && (_DssFrame != null)
					&& _DssFrame.getFP().isFilterValid();
		}
	};

	SchematicRelatedAction ForwardBlockAction = new DssFrameRelatedAction("",
			createIconImage("images/toolbar/forward_block.png"), this) {
		public void actionPerformed(ActionEvent e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JComboBox dateBox = _dateBox;
			int firstIndex = -1;
			for (int i = 0; i < dateBox.getItemCount(); ++i) {
				if (((ComboItem) dateBox.getItemAt(i)).toString()
						.equalsIgnoreCase("OCT 1921")) {
					firstIndex = i;
					break;
				}
			}
			if (dateBox.getSelectedIndex() < firstIndex) {
				dateBox.setSelectedIndex(firstIndex);
			} else if (dateBox.getSelectedIndex() < dateBox.getItemCount() - 13) {
				dateBox.setSelectedIndex(dateBox.getSelectedIndex() + 12);
			} else {
				dateBox.setSelectedIndex(dateBox.getItemCount() - 1);
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		public boolean canAct() {
			return super.canAct() && (_DssFrame != null)
					&& _DssFrame.getFP().isFilterValid();
		}
	};
	SchematicRelatedAction BackBlockAction = new DssFrameRelatedAction("",
			createIconImage("images/toolbar/backward_block.png"), this) {
		public void actionPerformed(ActionEvent e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JComboBox dateBox = _dateBox;
			int firstIndex = -1;
			for (int i = 0; i < dateBox.getItemCount(); ++i) {
				if (((ComboItem) dateBox.getItemAt(i)).toString()
						.equalsIgnoreCase("OCT 1921")) {
					firstIndex = i;
					break;
				}
			}
			if (dateBox.getSelectedIndex() > firstIndex + 11) {
				dateBox.setSelectedIndex(dateBox.getSelectedIndex() - 12);
			} else {
				dateBox.setSelectedIndex(firstIndex);
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		public boolean canAct() {
			return super.canAct() && (_DssFrame != null)
					&& _DssFrame.getFP().isFilterValid();
		}
	};

	SchematicRelatedAction PlotAction = new SchematicRelatedAction("Plot",
			createIconImage("images/icon24_graph.gif"), this) {
		public void actionPerformed(ActionEvent e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			showOutput("Plot");
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		public boolean canAct() {
			return true;
		}
	};

	SchematicRelatedAction TableAction = new SchematicRelatedAction("Table",
			createIconImage("images/table24.gif"), this) {
		public void actionPerformed(ActionEvent e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			showOutput("Table"); // CB TO DO: tableAction????????????????????
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		public boolean canAct() {
			return true;
		}
	};

	SchematicRelatedAction MonthlyAction = new SchematicRelatedAction(
			"Monthly", // CB why not use DSSFrameRelatedAction here?
			createIconImage("images/monthly24.gif"), this) {
		public void actionPerformed(ActionEvent e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			showOutput("Monthly");
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		public boolean canAct() {
			return true;
		}
	};

	SchematicRelatedAction ExceedanceAction = new SchematicRelatedAction(
			"Exceedance", // CB why not use DSSFrameRelatedAction here?
			createIconImage("images/exceedance_24.gif"), this) {
		public void actionPerformed(ActionEvent e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			showOutput(DssViewer.EXCEEDENCE_STRING);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		public boolean canAct() {
			return true;
		}
	};

	SchematicRelatedAction MonthlyAverageAction = new SchematicRelatedAction(
			"Monthly Average", // CB why not use DSSFrameRelatedAction here?
			createIconImage("images/monthly_avg_24.gif"), this) {
		public void actionPerformed(ActionEvent e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			showOutput(DssViewer.MONTHLY_AVERAGE_STRING);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		public boolean canAct() {
			return true;
		}
	};

	SchematicRelatedAction AboutAction = null;
	{
		AboutAction = new SchematicRelatedAction("About", null, this) {
			public void actionPerformed(ActionEvent e) {
				showAbout();
			}

			public boolean canAct() {
				return true;
			}
		}; // doesn't depend on a view
	}

	/**
	 * CB added.
	 */
	private void addDocumentListener() {
		// if
		// (_DssFrame.mainPanel.getFilterPanel().getBaseDVFileField().getDocument().listeners.contains())
		// {
		_DssFrame.mainPanel.getFilterPanel().getBaseDVFileField().getDocument()
				.addDocumentListener(MainFrame.this);
		// }
	}

	SchematicRelatedAction DssOpenProjectAction = new SchematicRelatedAction(
			"Open Project", this) {
		public void actionPerformed(ActionEvent e) {
			/*
			 * setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			 * 
			 * if (_DSSFrame == null) _DSSFrame = new DSSFrame(false); String
			 * filename = _DSSFrame.mainPanel.getActions().openViewerFile(); //
			 * CB altered AppAction.updateAllActions(); if
			 * (!_DSSFrame.isVisible()) // CB added _DSSFrame.setVisible(true);
			 * setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			 * Schematic.this.updateDSSMenu(filename);
			 */

			openProject();
		}
	};

	SchematicRelatedAction OpenPropertyDirectoryAction = new SchematicRelatedAction(
			"Open Properties Directory", this) {
		public void actionPerformed(ActionEvent e) {
			String filename = openPropertiesLocation();
			updatePropertiesMenu(filename, true); // TODO test this call with
			// boolean arg added
		}
	};

	SchematicRelatedAction[] RecentProjectFilesOpenActions = new SchematicRelatedAction[NUMBER_OF_RECENT_FILES_OPENED];

	SchematicRelatedAction DssOpenViewerAction = null;
	{
		DssOpenViewerAction = new SchematicRelatedAction("Viewer", this) {
			public void actionPerformed(ActionEvent e) {
				if (_DssFrame == null) {
					_DssFrame = new DssFrame(MainFrame.this, true);

					addDocumentListener();
					// CB TODO put this WindowListener somewhere so can be
					// accessed from multiple locations!!!!!!
					_DssFrame.addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent event) {
							_DssFrame.dispose();
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									_DssFrame.mainPanel.stopMonthlyDataWork();
									_DssFrame = null;
									_dateBox
											.setModel(new DefaultComboBoxModel());
									_dateBox.validate();
									_dateBox.repaint();
									updateAllActions();
								}
							});
						}
					});

				} else {
					_DssFrame.setVisible(true);
				}
				SchematicRelatedAction.updateAllActions();
			}
		};
	}

	SchematicRelatedAction[] RecentPropertyDirectoriesOpenActions = new SchematicRelatedAction[NUMBER_OF_RECENT_FILES_OPENED];

	/**
	 * CB - added MAY NEED TO PROVIDE AN APPROPRIATE ERRORS MESSAGES FOR WHEN
	 * THIS DOES NOT WORK
	 * 
	 * @param openAction
	 * @param intPosition
	 */
	private SchematicRelatedAction createSchematicOpenAction(int position) {
		if ((position < 1) || (position > 5)) {
			return null;
		}
		String filename = readRecentlyOpenedFilename(position, SCHEMATIC_TYPE);
		// openAction = new AppAction(position + ": " + filename,
		// Schematic.this) {
		if ((filename == null) || (filename.trim().length() == 0)) {
			return null;
		} else {
			return new SchematicRelatedAction(position + ": " + filename,
					MainFrame.this) {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() instanceof JMenuItem) {
						String filename = ((JMenuItem) e.getSource()).getText()
								.substring(
										((JMenuItem) e.getSource()).getText()
												.indexOf(":") + 1).trim();
						if (filename != null) {
							boolean isFileOpen = isSchematicFileOpen(filename);
							if (!isFileOpen) {
								String result = openSchematic(filename);
								updateFileMenu(result, true);
							}
						}// else
						// removeProjectFileOpenAction(filename);
					}
				}

				public boolean canAct() {
					return true;
				}
			}; // doesn't depend on a view
		}
	}

	/**
	 * CB - added MAY NEED TO PROVIDE AN APPROPRIATE ERROR MESSAGES FOR WHEN
	 * THIS DOES NOT WORK
	 * 
	 * @param position
	 */
	private SchematicRelatedAction createProjectOpenAction(int position) {
		if ((position < 1) || (position > 5)) {
			return null;
		}
		String filename = readRecentlyOpenedFilename(position, PROJECT_TYPE);
		if ((filename == null) || (filename.trim().length() == 0)) {
			return null;
		} else {
			return new SchematicRelatedAction(position + ": " + filename,
					MainFrame.this) {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() instanceof JMenuItem) {
						String filename = ((JMenuItem) e.getSource()).getText()
								.substring(
										((JMenuItem) e.getSource()).getText()
												.indexOf(":") + 1).trim();
						if (filename != null) {
							boolean isProjectFileOpen = isProjectFileOpen(filename);
							if (!isProjectFileOpen) {
								// setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								String result = openProject(filename);
								updateDSSMenu(result, true);
							}
						}
					}
				}

				public boolean canAct() {
					return (getCurrentView() != null);
				}
			};
		}
	}

	/**
	 * CB - added MAY NEED TO PROVIDE AN APPROPRIATE ERROR MESSAGES FOR WHEN
	 * THIS DOES NOT WORK
	 * 
	 * @param position
	 */
	private SchematicRelatedAction createPropertiesOpenAction(int position) {
		if ((position < 1) || (position > 5)) {
			return null;
		}
		String filename = readRecentlyOpenedFilename(position, PROPERTIES_TYPE);
		if ((filename == null) || (filename.trim().length() == 0)) {
			return null;
		} else {
			return new SchematicRelatedAction(position + ": " + filename,
					MainFrame.this) {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() instanceof JMenuItem) {
						String filename = ((JMenuItem) e.getSource()).getText()
								.substring(
										((JMenuItem) e.getSource()).getText()
												.indexOf(":") + 1).trim();
						if (filename != null) {
							boolean isPropertiesDirectoryOpen = isPropertiesDirectoryOpen(filename);
							if (!isPropertiesDirectoryOpen) {
								openPropertiesLocation(filename);
								updatePropertiesMenu(filename, true); // TODO
								// test
								// this
								// call
								// with
								// boolean
								// arg
								// added
							}
						}
					}
				}

				public boolean canAct() {
					return (getCurrentView() != null);
				}
			};
		}
	}

	void initMenus() {
		// ==============================================================
		// Define all the command actions and setup the menus
		// ==============================================================
		JMenuItem item = null;

		filemenu.setText("File");
		filemenu.setMnemonic('F');

		JMenuItem openitem = new JMenuItem("Open Study");
		openitem.addActionListener(new GuiTaskListener("Opening Study...") {
			public void doWork() {
				openStudy();
			};
		});
		filemenu.add(openitem);

		JMenuItem saveitem = new JMenuItem("Save Study");
		saveitem.addActionListener(new GuiTaskListener("Saving Study...") {
			public void doWork() {
				saveStudy();
			};
		});
		filemenu.add(saveitem);

		JMenuItem saveasitem = new JMenuItem("Save Study As");
		saveasitem.addActionListener(new GuiTaskListener("Saving Study As...") {
			public void doWork() {
				saveAsStudy();
			};
		});
		filemenu.add(saveasitem);

		filemenu.addSeparator();

		item = filemenu.add(FileOpenAction);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				Event.CTRL_MASK));
		item.setMnemonic('O');
		item.setIcon(null); // choose not to use icon in menu

		item = filemenu.add(FileSaveAsAction);
		item.setMnemonic('S');

		setRecentSchematicFileOpenActions(true); // CB added

		// CB added if (RecentFilesOpenActions[0] != null)
		// filemenu.addSeparator(); //CB not needed addExitCommand adds
		// separator
		addExitCommand();
		mainMenuBar.add(filemenu);

		viewmenu.setText("View");
		viewmenu.setMnemonic('V');

		item = viewmenu.add(_viewer.getZoomNormalAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6,
				Event.CTRL_MASK | Event.SHIFT_MASK));
		item.setMnemonic('N');

		item = viewmenu.add(_viewer.getZoomInAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6,
				Event.CTRL_MASK));
		item.setMnemonic('I');
		item.setIcon(null); // choose not to use icon in menu

		item = viewmenu.add(_viewer.getZoomOutAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6,
				Event.SHIFT_MASK));
		item.setMnemonic('O');
		item.setIcon(null); // choose not to use icon in menu

//		item = viewmenu.add(_viewer.getZoomRectangleAction());
//		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
//		item.setMnemonic('Z');
//
//		item = viewmenu.add(new JCheckBoxMenuItem(_viewer
//				.getZoomRectangleAction()));

		viewmenu.addSeparator();

		item = viewmenu.add(new JCheckBoxMenuItem(_viewer
				.getToggleLegendAction()));
		item = viewmenu.add(_viewer.getSelectFontAction());
		item = viewmenu.add(_viewer.getSelectPrecisionAction());

		mainMenuBar.add(viewmenu);

		// DSS menu
		dssmenu.setText("DSS");
		dssmenu.setMnemonic('D');

		item = dssmenu.add(DssOpenProjectAction);
		item.setMnemonic('O');

		item = dssmenu.add(DssOpenViewerAction);
		item.setMnemonic('V');

		setRecentProjectFileOpenActions(true); // CB added

		dssmenu.addSeparator();
		// temp? item = dssmenu.add(LoadDefaultsAction);
		// temp? item.setMnemonic('L');

		mainMenuBar.add(dssmenu);

		// CB added section
		// Property (from lookup tables in WRIMS 1) menu
		propertyMenu.setText("Properties");
		propertyMenu.setMnemonic('O');
		item = propertyMenu.add(OpenPropertyDirectoryAction);
		item.setMnemonic('O');
		setRecentPropertyDirectoriesOpenActions(true); // CB added
		// propertyMenu.addSeparator();
		// item = propertyMenu.add(LoadDefaultsAction);
		// item.setMnemonic('L');
		propertyMenu.setEnabled(false);
		mainMenuBar.add(propertyMenu);

		mainMenuBar.add(new JMenuItem(AboutAction));

		// Help menu
		helpmenu.setText("Help");
		helpmenu.setMnemonic('H');

		/*
		 * item = helpmenu.add(AboutAction); item.setMnemonic('A');
		 * item.setIcon(null); //choose not to use icon in menu
		 */

		// mainMenuBar.add(helpmenu);
		mainFrame.setJMenuBar(mainMenuBar);
	}

	@SuppressWarnings("serial")
	private JToolBar initToolbar1() { // CB renamed and changed to private
		JToolBar toolBar = new JToolBar();
		toolBar.setAlignmentX(0);
		JButton button = null;
		button = toolBar.add(FileOpenAction);
		button.setToolTipText("Open schematic window");
		toolBar.addSeparator();
		button = toolBar.add(_viewer.getZoomInAction());
		button.setToolTipText("Zoom In");
		button = toolBar.add(_viewer.getZoomOutAction());
		button.setToolTipText("Zoom Out");
		button = toolBar.add(_viewer.getZoomToFitAction());
		button.setToolTipText("Zoom To All");
		button = toolBar.add(_viewer.getZoomNormalAction());
		toolBar.addSeparator();
		toolBar.add(new JToggleButton(_viewer.getZoomRectangleAction()));
		toolBar.add(new JToggleButton(_viewer.getZoomMagnifierAction()));
		toolBar.addSeparator();
		// button = toolBar.add(_viewer.getBackwardViewAction());
		// button.setToolTipText("Go Backward in View");
		// button = toolBar.add(_viewer.getForwardViewAction());
		// button.setToolTipText("Go Forward in View");

		JPanel markPanel = new JPanel();
		markPanel.setLayout(new BorderLayout());
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.LINE_AXIS));
		eastPanel.add(new JButton(_viewer.getMarkViewAction()));
		eastPanel.add(new JButton(_viewer.getDeleteViewAction()));
		markPanel.add(eastPanel, "East");
		markPanel.add(_viewer.getMarkViewComboBox());
		toolBar.add(markPanel);

		toolBar.addSeparator();
		final JToggleButton panToggle = new JToggleButton(ImageUtil
				.createImageIcon("/wrims/schematic/images/toolbar/pan.png"));
		panToggle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_viewer.setPanMode(panToggle.isSelected());
			}
		});
		panToggle.setToolTipText("Pan");
		toolBar.add(panToggle);

		toolBar.addSeparator();
		button = toolBar.add(PlotAction);
		button.setToolTipText("Plot");
		button = toolBar.add(TableAction);
		button.setToolTipText("Table");
		button = toolBar.add(MonthlyAction); // CB added
		button.setToolTipText("Monthly Table"); // CB added
		button = toolBar.add(ExceedanceAction);
		button.setToolTipText("Exceedance Plot");

		toolBar.addSeparator();

		final JTextField findTextField = new JTextField();
		findTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					getCurrentView().findInView(findTextField.getText());

				}
			}
		});
		JButton findButton = new JButton("Find");
		findButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getCurrentView().findInView(findTextField.getText());
			}
		});
		JPanel findPanel = new JPanel();
		findPanel.setLayout(new BorderLayout());
		findPanel.add(findButton, "East");
		findPanel.add(findTextField);
		toolBar.add(findPanel, "West");

		return toolBar;
	}

	protected JToolBar initToolbar2() { // CB renamed
		JToolBar toolBar = new JToolBar();
		toolBar.setAlignmentX(0);
		JPanel dssPeriodControlPanel = new JPanel();
		dssPeriodControlPanel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();

		gc.anchor = GridBagConstraints.CENTER;
		gc.weightx = 0.0;
		gc.ipadx = 5;
		gc.weighty = 0;
		gc.fill = GridBagConstraints.NONE;

		JLabel showValueBoxesLabel = new JLabel("Show Values");
		_showValueBoxesCheckbox = new JCheckBox();
		_showValueBoxesCheckbox.setSelected(true);
		getCurrentView().setShowValueBoxes(true);
		_showValueBoxesCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getSource() instanceof JCheckBox) {
					boolean isVisible = ((JCheckBox) event.getSource())
							.isSelected();
					getCurrentView().setShowValueBoxes(isVisible);
				}
			}
		});

		String location = "";
		_forward1PeriodButton = new JButton(Forward1Action);
		_back1PeriodButton = new JButton(Back1Action);
		_forward1BlockButton = new JButton(ForwardBlockAction);
		_back1BlockButton = new JButton(BackBlockAction);

		dssPeriodControlPanel.add(new JLabel("        "), gc);
		dssPeriodControlPanel.add(showValueBoxesLabel, gc);
		dssPeriodControlPanel.add(_showValueBoxesCheckbox, gc);
		dssPeriodControlPanel.add(new JLabel("      "), gc);
		dssPeriodControlPanel.add(_forward1PeriodButton, gc);
		dssPeriodControlPanel.add(_back1PeriodButton, gc);
		dssPeriodControlPanel.add(_forward1BlockButton, gc);
		dssPeriodControlPanel.add(_back1BlockButton, gc);
		dssPeriodControlPanel.add(new JLabel("  "), gc);

		dssPeriodControlPanel.add(new JLabel("Date: "), gc);
		// No toolBar.add(dateLabel, gc);
		_dateBox = new JComboBox();
		_dateBox.setAction(SingleStepValuesAction);
		gc.anchor = GridBagConstraints.CENTER;
		gc.weightx = 0.0;
		gc.ipadx = 50;
		dssPeriodControlPanel.add(_dateBox, gc);
		gc.ipadx = 0;
		gc.weightx = 1.0;
		dssPeriodControlPanel.add(new Label(" "), gc);
		toolBar.add(dssPeriodControlPanel, "East");
		// No toolBar.add(_dateBox, gc);
		// No toolBar.add(new Label(" "), gc);
		return toolBar;
	}

	public void addExitCommand() {
		filemenu.addSeparator();

		SchematicRelatedAction ExitAction = new SchematicRelatedAction("Exit",
				this) {
			public void actionPerformed(ActionEvent e) {
				exit();
			}

			public boolean canAct() {
				return true;
			}
		}; // doesn't depend on a view

		JMenuItem item = filemenu.add(ExitAction);
		item.setMnemonic('x');
	}

	public void init() {
	}

	public void start() {
		if (!IS_DEVELOPER) {
			return; // CB added
		}
		// enable drag-and-drop from separate thread
		new Thread(this).start();

		mainFrame.getContentPane().validate(); // CB added - DOES NOTHING. TO
		// DO: CAD tab
	}

	public void run() {
		SchematicRelatedAction.updateAllActions();
	}

	public void destroy() {
		theApp.dispose();
	}

	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		String devMode = System.getProperty("wrims.schematic.devMode");
		IS_DEVELOPER = Boolean.valueOf(devMode);
		String directory = "";
		if ((args != null) && (args.length > 0)) {
			directory = args[0];
		}
		SchematicUtils.schematic = new MainFrame(directory, true);
	}

	static Frame getApp() {
		return theApp;
	}

	void exit() {
		destroy();
		if (_isStandalone) {
			System.exit(0);
		}
		System.gc();
	}

	void showAbout() {
		Properties buildProps = new Properties();
		buildProps.put("name", "WRIMS 2 UI");
		buildProps.put("version", "1.0");
		try {
			buildProps.load(getClass().getResourceAsStream(
					"/wrims/schematic/build.props"));
		} catch (Exception e) {
			buildProps.put("build", "NOT BUILT EVER - DEV Env");
			buildProps.put("buildtime", "" + new Date());
			buildProps.put("system", "OS: " + System.getProperty("os.name"));
			// e.printStackTrace();
		}
		String message = buildProps.getProperty("name") + "\n"
				+ buildProps.getProperty("version");
		message += "\n" + buildProps.getProperty("build") + "\n"
				+ buildProps.getProperty("buildtime");
		message += "\n" + buildProps.getProperty("builder") + "\n"
				+ buildProps.getProperty("system");
		JOptionPane.showMessageDialog(this, message, "About",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public boolean loadDefaultSchematic() {
		return false;
	}

	/**
	 * 
	 * @param filename
	 *            a file recently opened and selected in the File menu
	 */
	String openSchematic(String filename) { // CB added return value.
		if (DEBUG) {
			System.out.println("Opening : " + filename);
		}
		String defaultLoc = null;
		SchematicViewer view = getCurrentView();
		if ((filename != null) && (filename.trim().length() > 0)) {
			try {
				view.load(filename);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return filename; // CB added TODO is here the best place for this line?
	}

	String openProject() {
		return openProject(null);
	}

	String openProject(String filename) { // CB added
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		if (_DssFrame == null) {
			_DssFrame = new DssFrame(this, false);
			addDocumentListener();

			_DssFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent event) {
					// CB need to remove listeners?
					_DssFrame.dispose();
					SwingUtilities.invokeLater(new Runnable() {
						public void run() { // Cb TODO move the run() method
							// contents outside the listener as
							// it is called from > 1 locations
							_DssFrame.mainPanel.stopMonthlyDataWork();
							_DssFrame = null;
							_dateBox.setModel(new DefaultComboBoxModel());
							_dateBox.validate();
							_dateBox.repaint();
							SchematicRelatedAction.updateAllActions();
						}
					});
				}
			});
		}

		if ((filename == null) || (filename.trim().length() == 0)) {
			filename = _DssFrame.mainPanel.getActions().openViewerFile(); // CB
			// altered
		} else {
			// CB added
			String result = _DssFrame.mainPanel.getActions().openViewerFile(
					filename);
			if (result == null) {
				// removeProjectFileOpenAction(filename);
				updateDSSMenu(filename, false);
				return result;
			}
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (!_DssFrame.isVisible()) {
					_DssFrame.setVisible(true);
				}
				_DssFrame.mainPanel.stopMonthlyDataWork();
				_DssFrame.mainPanel.updateSchematicValues();
				SchematicRelatedAction.updateAllActions();
			}
		});
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		return filename;
	}

	/**
	 * CB added.
	 * 
	 * @param isEnabled
	 */
	public void updateDateBoxTWs(final boolean isEnabled,
			final boolean isLongTermType) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				for (int i = 0; i < ((DefaultComboBoxModel) _dateBox.getModel())
						.getSize(); ++i) {
					if (((DefaultComboBoxModel) _dateBox.getModel())
							.getElementAt(i) instanceof ComboItem) {
						String period = ((ComboItem) ((DefaultComboBoxModel) _dateBox
								.getModel()).getElementAt(i)).toString();
						if ((isLongTermType && (period.indexOf("-") > -1))
								|| (!isLongTermType && (period.indexOf("-") == -1))) {
							((ComboItem) ((DefaultComboBoxModel) _dateBox
									.getModel()).getElementAt(i))
									.setEnabled(isEnabled);
						}
					}
				}
			}
		});
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public JComboBox getDateBox() {
		return _dateBox;
	}

	/**
	 * CB added.
	 */
	public void disableMonthItems() {
		ComboBoxModel model = _dateBox.getModel();
		if (model instanceof DefaultComboBoxModel) {
			for (int i = 0; i < ((DefaultComboBoxModel) model).getSize(); ++i) {
				Object element = ((DefaultComboBoxModel) model).getElementAt(i);
				if (element instanceof ComboItem) {
					if (((ComboItem) element).toString().indexOf("-") == -1) {
						((ComboItem) element).setEnabled(false);
					}
				}
			}
		}
	}

	/**
	 * CB added.
	 */
	public void disableLongtermItems() {
		ComboBoxModel model = _dateBox.getModel();
		if (model instanceof DefaultComboBoxModel) {
			for (int i = 0; i < ((DefaultComboBoxModel) model).getSize(); ++i) {
				Object element = ((DefaultComboBoxModel) model).getElementAt(i);
				if (element instanceof ComboItem) {
					if (((ComboItem) element).toString().indexOf("-") > -1) {
						((ComboItem) element).setEnabled(false);
					}
				}
			}
		}
	}

	private String propertiesDirectory; // CB added

	/**
	 * CB added.
	 */
	String openPropertiesLocation() {
		_currentOpenPropertyLocation = openPropertiesLocation(null);
		return _currentOpenPropertyLocation;
	}

	String getPropertiesLocation() {
		return _currentOpenPropertyLocation;
	}

	/**
	 * CB added.
	 * 
	 * @param directory
	 */
	String openPropertiesLocation(String directory) { // CB added
		JFileChooser chooser = null; // = new JFileChooser();
		if ((directory == null) || (directory.trim().length() == 0)) {
			chooser = new JFileChooser();
			chooser.setCurrentDirectory(null);
		} else {
			chooser = new JFileChooser(); // TO DO: don't need this is File
			// exists
			// already!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			File currentFile = new File(directory);
			chooser.setCurrentDirectory(currentFile);
		}
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// chooser.setAcceptAllFileFilterUsed(false);
		Filter inputFilter = new Filter("",
				"Lowest-level directory must be a level above the \"run\" directory");
		chooser.addChoosableFileFilter(inputFilter);
		chooser.setFileFilter(inputFilter);
		int returnVal = chooser.showOpenDialog(mainFrame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String loc = chooser.getSelectedFile().getAbsolutePath();
			if ((loc != null) && !loc.trim().equals("")) {
				// System.out.println(chooser.getSelectedFile().getAbsolutePath());
				File[] children = chooser.getSelectedFile().listFiles(); // looking
				// for
				// one
				// level
				// above
				// "run"
				// directory
				boolean isOK = false;
				for (int i = 0; i < children.length; ++i) {
					if (children[i].getName().equalsIgnoreCase("run")) {
						isOK = true;
						break;
					}
				}
				if (isOK) {
					// if (loc.toLowerCase().lastIndexOf("run") == loc.length()
					// - 3) // looking for "run" directory
					return loc;
				} else {
					return null;
				}
			}
		}
		return null;
	}

	void closeSchematic() {
		// CB replacement block
		if (myDesktop.getAllFrames().length > 0) {
			JInternalFrame[] frames = myDesktop.getAllFrames();
			for (int i = frames.length - 1; i >= 0; --i) {
				try {
					frames[i].setClosed(true);
				} catch (Exception x) {
				}
			}
		}
	}

	void saveSchematic() {
		if (getCurrentView() != null) {
			getCurrentView().save();
		}
	}

	void saveAsSchematic(String filename) {
		if (getCurrentView() != null) {
			getCurrentView().saveAs(filename);
		}
	}

	// Plot the selected variables from schematic
	void showOutput(String type) { // CB I fixed this method
		if (_DssFrame == null || getCurrentView() == null) {
			return;
		}
		Vector<String> selection = getCurrentView().getSelectedNames();
		if (selection == null) {
			return;
		}
		int outputType = DssViewer.getOutputType(type);
		boolean isExceedence = false;
		boolean isAnnualTotal = false;
		if (outputType == DssViewer.EXCEEDENCE) {
			isAnnualTotal = false;
			isExceedence = true;
		} else if (outputType == DssViewer.ANNUAL_TOTAL) {
			isExceedence = false;
			isAnnualTotal = true;
		} else if (outputType == DssViewer.ANNUAL_TOTAL_EXCEEDENCE) {
			isExceedence = true;
			isAnnualTotal = true;
		}
		_DssFrame.getFP().retrieve(outputType, isExceedence, isAnnualTotal,
				selection);
	}

	/**
	 * CB added to allow notification that the value box lables need to be
	 * refreshed for the current date.
	 */
	public void updateValues() {
		if ((_dateBox != null)
				&& _dateBox.isEnabled()
				&& (_dateBox.getSelectedItem() != null)
				&& !(((ComboItem) _dateBox.getSelectedItem()).toString().trim()
						.equals(""))) {
			new Thread(new Runnable() {
				public void run() {
					updateValues(((ComboItem) _dateBox.getSelectedItem())
							.toString());
				}
			}).start();
		}
	}

	/**
	 * CB added to update values in value boxes when a date is selected. Not in
	 * dispatch thread because values are read-only and this method should be
	 * accessed by more than one object (thread) simultaneously.
	 */
	public void updateValues(String date) {
		Hashtable<String, String>[] values = null;
		if (_DssFrame == null) {
			return;
		}

		ProgressMonitor monitor = new ProgressMonitor(this,
				"Updating values...", "", 0, 10);
		monitor.setMillisToDecideToPopup(1000);
		monitor.setMillisToPopup(500);
		monitor.setProgress(0);
		long ti = System.currentTimeMillis();
		Hashtable<String, Object> visibleNodes = getCurrentView()
				.getVisibleNodes();
		monitor.setProgress(1);
		Logger.getLogger(SchematicViewer.WRIMS_SCHEMATIC).fine(
				"Visible nodes calculated in : "
						+ (System.currentTimeMillis() - ti));
		final Hashtable<String, Object> names = new Hashtable<String, Object>();
		for (String v : visibleNodes.keySet()) {
			names.put(v, v);
		}
		_DssFrame.mainPanel.setCursor(Cursor
				.getPredefinedCursor(Cursor.WAIT_CURSOR));
		monitor.setProgress(2);
		ti = System.currentTimeMillis();
		_DssFrame.getFP().loadAllVariableData(names, -1, true, monitor);
		Logger.getLogger(SchematicViewer.WRIMS_SCHEMATIC).fine(
				"Loading all variable date took : "
						+ (System.currentTimeMillis() - ti));
		ti = System.currentTimeMillis();
		monitor.setProgress(5);
		_DssFrame.getFP().getValueViewer().setVariables(names);
		_DssFrame.getFP().getValueViewer().calculateLongTermAverages(
				getTimeWindows(), -1, true, monitor);
		Logger.getLogger(SchematicViewer.WRIMS_SCHEMATIC).fine(
				"Calculating Long Term Averages took : "
						+ (System.currentTimeMillis() - ti));
		monitor.setProgress(9);
		if (_DssFrame.getFP().getValueViewer() == null) {
			values = _DssFrame.getFP().retrieve(
					DssViewer.getOutputType(DssViewer.VALUES_STRING), date,
					getAllVariables());
		} else {
			values = _DssFrame.getFP().retrieve(
					DssViewer.getOutputType(DssViewer.VALUES_STRING), date,
					null);
		}

		HashMap<Integer, Object> dssFileMap = _DssFrame.mainPanel
				.getMessagePanel().getDSSFileNames("BOTH");

		_DssFrame.mainPanel.setCursor(Cursor.getDefaultCursor());
		if (values != null) {
			ti = System.currentTimeMillis();
			getCurrentView().setValues(values,
					!_DssFrame.getMP().getMode().equals("Comp"));
			Logger.getLogger(SchematicViewer.WRIMS_SCHEMATIC).fine(
					"Setting values took : "
							+ (System.currentTimeMillis() - ti));
		} else {
			System.out
					.println("No values yet for method updateValues(String date)");
			JOptionPane.showMessageDialog(null, "No values yet for date = "
					+ date, "Date Does Not Exist", JOptionPane.ERROR_MESSAGE);
		}
		monitor.setProgress(10);
		monitor.close();
	}

	/**
	 * CB added to get all variable without giving up the view or the doc to the
	 * DSS package.
	 * 
	 * @return
	 */
	public Hashtable<String, Object> getAllVariables() {
		if ((getCurrentView() == null)
				|| (getCurrentView().getDiagram() == null)) {
			return null;
		}
		return getCurrentView().getVisibleNodes();
	}

	SchematicViewer getCurrentView() {
		return _viewer;
	}

	// get an image from the given filename
	private static Image getImage(String filename) {

		// to read from file
		ImageIcon icon = new ImageIcon(filename);

		// try to read from URL
		if ((icon == null)
				|| (icon.getImageLoadStatus() != MediaTracker.COMPLETE)) {
			try {
				URL url = new URL(filename);
				icon = new ImageIcon(url);
			} catch (Exception e) { /* not a url */
			}
		}

		// in case file is inside a .jar
		if ((icon == null)
				|| (icon.getImageLoadStatus() != MediaTracker.COMPLETE)) {
			URL url = MainFrame.class.getResource(filename);
			if (url == null) {
				throw new RuntimeException("image " + filename + " not found");
			}
			icon = new ImageIcon(url);
		}

		return icon.getImage();
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public int getSelectedUnits() {
		if (_TAFButton.isSelected()) {
			return TAF;
		} else if (_CFSButton.isSelected()) {
			return CFS;
		} else {
			return -1;
		}
	}

	/**
	 * CB added.
	 * 
	 * @param units
	 */
	public void setUnitsButtons(int units) {
		getSelectedDate();
		if (units == TAF) {
			if (!_TAFButton.isSelected()) {
				_TAFButton.setSelected(true);
				toolBar2.repaint();
				// updateValues(getSelectedDate());
				updateValues();
			}
		} else {
			if (!_CFSButton.isSelected()) {
				_CFSButton.setSelected(true);
				toolBar2.repaint();
				// updateValues(getSelectedDate());
				updateValues();
			}
		}
	}

	/**
	 *
	 *
	 */
	private void readPreferences() { // CB added
		readWindowPreferences();
	}

	/**
	 *
	 */
	private void readWindowPreferences() { // CB added
		// Read window preferences to set window location and size
		int x = _userPrefs
				.getInt("SchematicFrameX", (Toolkit.getDefaultToolkit()
						.getScreenSize().width - DEFAULT_WIDTH) / 2);
		int y = _userPrefs
				.getInt("SchematicFrameY", (Toolkit.getDefaultToolkit()
						.getScreenSize().height - DEFAULT_HEIGHT) / 2);
		setLocation(x, y);
		int width = _userPrefs.getInt("SchematicFrameWidth", DEFAULT_WIDTH);
		int height = _userPrefs.getInt("SchematicFrameHeight", DEFAULT_HEIGHT);
		setSize(width, height);
		// Read preferences to set all SplitPane locations
		// _mainSplit.setDividerLocation(_userPrefs.getInt("MainSplitX",
		// MAINSPLIT_DEFAULT_X));
	}

	/**
	 *
	 */
	private void writeWindowPreferences() { // CB added
		// Write window location and size to preferences
		_userPrefs.put("SchematicFrameX", String.valueOf(getX()));
		_userPrefs.put("SchematicFrameY", String.valueOf(getY()));
		_userPrefs.put("SchematicFrameWidth", String.valueOf(getWidth()));
		_userPrefs.put("SchematicFrameHeight", String.valueOf(getHeight()));

		// Write all SplitPane locations to preferences
		// _userPrefs.putInt("MainSplitX", _mainSplit.getDividerLocation());
	}

	private String readRecentlyOpenedFilename(int number, int type) { // CB
		// added
		return _userPrefs.get(PREFS_FILE_TYPE_STRINGS[type] + number, "");
	}

	/**
	 * CB added. Remove a recently opened file from the preferences. This is to
	 * fix a broken preference (one for which the file does not exist in the
	 * specified location).
	 * 
	 * @param number
	 * @param type
	 * @return
	 */
	private void removeRecentlyOpenedFilenamePreference(String filename,
			int type) { // CB added
		String key = null;
		for (int i = 1; i <= 5; i++) {
			if ((_userPrefs.get(key = PREFS_FILE_TYPE_STRINGS[type] + i, ""))
					.equals(filename)) {
				_userPrefs.remove(key);
				break;
			}
		}
	}

	private boolean isFileRecent(String fileToBeOpened, int type) { // CB added
		if ((type < 0) || (type > 1)) {
			return false;
		}
		for (int i = 1; i <= NUMBER_OF_RECENT_FILES_OPENED; ++i) {
			// System.out.println(PREFS_FILE_TYPE_STRINGS[type] + i + " ="
			// + _userPrefs.get(PREFS_FILE_TYPE_STRINGS[type] + i, "")); //
			// debugging
			if (fileToBeOpened.equalsIgnoreCase(_userPrefs.get(
					PREFS_FILE_TYPE_STRINGS[type] + i, ""))) {
				return false;
			}
		}
		return true;
	}

	public boolean isFileOpen(String fileToBeOpened, int type) { // CB added
		if (type == SCHEMATIC_TYPE) {
			return isSchematicFileOpen(fileToBeOpened);
		} else if (type == PROJECT_TYPE) {
			return isProjectFileOpen(fileToBeOpened);
		} else {
			return false; // CB probably should do a JOptionPane for better
			// messaging!
		}
	}

	private boolean isSchematicFileOpen(String fileToBeOpened) {
		return isFileOpen(fileToBeOpened, SCHEMATIC_TYPE);
	}

	public boolean isProjectFileOpen(String fileToBeOpened) { // CB added
		if ((_DssFrame == null)
				|| (_DssFrame.mainPanel == null)
				|| (_DssFrame.mainPanel.getMessagePanel() == null)
				|| (_DssFrame.mainPanel.getMessagePanel().getProjectName() == null)) {
			return false;
		}
		if (_DssFrame.mainPanel.getMessagePanel().getProjectName()
				.equalsIgnoreCase(fileToBeOpened)) {
			return true;
		}
		return false;
	}

	public boolean isPropertiesDirectoryOpen(String fileToBeOpened) { // CB
		// added
		return fileToBeOpened.equals(_currentOpenPropertyLocation);
	}

	/**
	 * Checks is the last file opened was already in the preferences. If it was
	 * not, the stored recent file preferences are shifted down one (i.e., the
	 * get for recent file i is put as the preference for recent file i + 1, and
	 * the new file is stored for recent file 1. If the last file opened was
	 * already in the preferences,
	 * 
	 * @param lastFileOpened
	 */
	/*
	 * private void updateRecentFilesOpenedPreferences(String lastFileOpened) {
	 * // CB // added updateRecentFilesOpenedPreferences(lastFileOpened,
	 * SCHEMATIC_TYPE); updateRecentFilesOpenedPreferences(lastFileOpened,
	 * PROJECT_TYPE); updateRecentFilesOpenedPreferences(lastFileOpened,
	 * PROPERTIES_TYPE); }
	 */

	/**
	 * Checks is the last file opened was already in the preferences. If it was
	 * not, the stored recent file preferences are shifted down one (i.e., the
	 * get for recent file i is put as the preference for recent file i + 1, and
	 * the new file is stored for recent file 1. If the last file opened was
	 * already in the preferences,
	 * 
	 * @param lastFileOpened
	 */
	private void updateRecentFilesOpenedPreferences(String lastFileOpened,
			int type) { // CB added
		int index = -1;
		// System.out.println("lastFileOpened = " + lastFileOpened); //
		// debugging
		if ((lastFileOpened != null) && !lastFileOpened.trim().equals("")) {
			for (int i = 1; i <= NUMBER_OF_RECENT_FILES_OPENED; ++i) {
				// System.out.println("RecentFile" + i + " = " +
				// _userPrefs.get("RecentFile" + i, "")); //debugging
				if (lastFileOpened.equalsIgnoreCase(_userPrefs.get(
						PREFS_FILE_TYPE_STRINGS[type] + i, ""))) {
					index = i;
					break;
				}
			}
			int intFrom;
			if (index == -1) {
				intFrom = NUMBER_OF_RECENT_FILES_OPENED;
			} else {
				intFrom = index;
			}
			for (int i = intFrom; i >= 2; --i) {
				_userPrefs.put(PREFS_FILE_TYPE_STRINGS[type] + i, _userPrefs
						.get(PREFS_FILE_TYPE_STRINGS[type] + (i - 1), ""));
			}
			_userPrefs.put(PREFS_FILE_TYPE_STRINGS[type] + 1, lastFileOpened);
			// System.out.println(PREFS_FILE_TYPE_STRINGS[type] + 1 + " +
			// _userPrefs.get(PREFS_FILE_TYPE_STRINGS[type] + 1, ""));
			// //debugging
		}
		// reorderPreferences(type);
	}

	private void reorderPreferences(int type) {
		for (int i = 1; i <= 5; ++i) { // CB TODO replace hard 5 with a Java
			// constant
			String value = _userPrefs
					.get(PREFS_FILE_TYPE_STRINGS[type] + i, "");
			// System.out.println("schematic open prefs value " + i + " = " +
			// value);
			if ((value == null) || value.trim().equals("")) {
				if (i < 5) {
					for (int j = i; j < 5; ++j) {
						_userPrefs.put(PREFS_FILE_TYPE_STRINGS[type] + j,
								_userPrefs.get(PREFS_FILE_TYPE_STRINGS[type]
										+ (j + 1), ""));
					}
				}
				_userPrefs.put(PREFS_FILE_TYPE_STRINGS[type] + 5, "");
				break;
			}
		}
		// debugging
		for (int i = 1; i <= 5; ++i) {
			String value = _userPrefs
					.get(PREFS_FILE_TYPE_STRINGS[type] + i, "");
			// System.out.println("schematic open prefs value " + i + " = " +
			// value);
		}
	}

	/**
	 * CB added. TODO combine all three methods of this kind in one using the
	 * type as an arg??????
	 * 
	 * @param filename
	 * @param wasAdded
	 *            true if the filename was added, false if the filename
	 */
	private void updateFileMenu(String filename, boolean wasAdded) { // CB added
		if (filename != null) {
			if (wasAdded) {
				updateRecentFilesOpenedPreferences(filename, SCHEMATIC_TYPE);
			} else {
				removeRecentlyOpenedFilenamePreference(filename, SCHEMATIC_TYPE);
				reorderPreferences(SCHEMATIC_TYPE);
			}
			createRecentFilesOpenActions(SCHEMATIC_TYPE);
			setRecentFileOpenActions(SCHEMATIC_TYPE);
			mainMenuBar.revalidate();
			mainMenuBar.repaint();
		}
	}

	/**
	 * CB added. TODO combine all three methods of this kind in one using the
	 * type as an arg??????
	 * 
	 * @param filename
	 * @param wasAdded
	 */
	public void updateDSSMenu(String filename, boolean wasAdded) { // CB added -
		// made
		// public
		// because
		// is a DSS
		// function
		// (accessed
		// from that
		// package)
		if (filename != null) {
			if (wasAdded) {
				updateRecentFilesOpenedPreferences(filename, PROJECT_TYPE);
			} else {
				removeRecentlyOpenedFilenamePreference(filename, PROJECT_TYPE);
				reorderPreferences(PROJECT_TYPE);
			}
			createRecentFilesOpenActions(PROJECT_TYPE);
			setRecentFileOpenActions(PROJECT_TYPE);
			mainMenuBar.revalidate();
			mainMenuBar.repaint();
		}
	}

	/**
	 * CB added. TODO combine all three methods of this kind in one using the
	 * type as an arg??????
	 * 
	 * @param filename
	 */
	private void updatePropertiesMenu(String filename, boolean wasAdded) { // TODO
		// test
		// this
		// method
		// after
		// properties
		// added
		if ((filename != null) && !filename.trim().equals("")) {
			if (wasAdded) {
				_currentOpenPropertyLocation = filename;
				updateRecentFilesOpenedPreferences(filename, PROPERTIES_TYPE);
			} else {
				removeRecentlyOpenedFilenamePreference(filename,
						PROPERTIES_TYPE);
				reorderPreferences(PROPERTIES_TYPE);
			}
			createRecentFilesOpenActions(PROPERTIES_TYPE);
			setRecentFileOpenActions(PROPERTIES_TYPE);
			mainMenuBar.revalidate();
			mainMenuBar.repaint();
		}
	}

	/**
	 * CB - added for development phase only.
	 * 
	 */
	private void clearRecentFilePreferences() {
		clearRecentFilePreferences(SCHEMATIC_TYPE);
		clearRecentFilePreferences(PROJECT_TYPE);
	}

	/**
	 * CB - added for development phase only.
	 * 
	 */
	private void clearRecentFilePreferences(int type) {
		for (int i = 1; i <= NUMBER_OF_RECENT_FILES_OPENED; ++i) {
			_userPrefs.put(PREFS_FILE_TYPE_STRINGS[type] + i, "");
		}
	}

	/**
	 *
	 *
	 */
	private void writePreferences() { // CB added
		writeWindowPreferences();
	}

	/**
	 * CB added.
	 * 
	 * @param isVisible
	 */
	public void setProgressVisibility(boolean isVisible) {
		// System.out.println("Setting progress visibility to " + isVisible);
		_monthlyProgressLabel.setVisible(isVisible);
		_monthlyProgressBar.setVisible(isVisible);
		_longtermProgressLabel.setVisible(isVisible);
		_longtermProgressBar.setVisible(isVisible);
	}

	/**
	 * CB added.
	 */
	public void resetProgressBars() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				_monthlyProgressBar.setValue(0);
				_longtermProgressBar.setValue(0);
			}
		});
	}

	/**
	 * CB added.
	 * 
	 * @param value
	 */
	public void setMonthlyProgress(final int value) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				_monthlyProgressBar.setValue(value);
			}
		});
	}

	/**
	 * CB added.
	 * 
	 * @param value
	 */
	public void setLongtermProgress(final int value) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				_longtermProgressBar.setValue(value);
			}
		});
	}

	/**
	 * open study
	 */
	void openStudy() {
		String styFile = VistaUtils.getFilenameFromDialog(this,
				FileDialog.LOAD, "sty", "Study files (*.sty)");
		if (styFile == null)
			return;
		// open and load project
		sty = new Study();
		try {
			sty.load(styFile);
			// DJE********************************************************
			if (!sty.isUpdatedStudyObject()) {
				String msg = styFile
						+ " was created with a previous version of CALSIM.";
				JOptionPane.showMessageDialog(this, msg, "Warning",
						JOptionPane.WARNING_MESSAGE);
			}
			inputPane.setStudy(sty);
			// batchPane.setStudy(sty);
			// *************************************************************
		} catch (IOException ioe) {
			VistaUtils.displayException(this, ioe);
		}
	}

	/**
	 * save study
	 */
	void saveStudy() {
		inputPane.updateStudy(sty);
		// batchPane.setStudy(sty);
		String styFile = sty.getFileName();
		if (styFile.equals("")) {
			styFile = VistaUtils.getFilenameFromDialog(this, FileDialog.SAVE,
					"sty", "Study files (*.sty)");
			if (styFile == null)
				return;
		}
		try {
			if (styFile.indexOf(".") == -1)
				styFile += ".sty";
			sty.save(styFile);
		} catch (IOException ioe) {
			VistaUtils.displayException(this, ioe);
		}
	}

	/**
	 * save study
	 */
	void saveAsStudy() {
		inputPane.updateStudy(sty);
		// batchPane.setStudy(sty);
		String styFile = VistaUtils.getFilenameFromDialog(this,
				FileDialog.SAVE, "sty", "Study files (*.sty)");
		if (styFile == null)
			return;
		try {
			if (styFile.indexOf((int) '.') == -1) // no extension
				styFile += ".sty"; // set default extension
			sty.save(styFile);
		} catch (IOException ioe) {
			VistaUtils.displayException(this, ioe);
		}
	}

	public static boolean IS_DEVELOPER = false; // CB added to switch to
	// user-only mode after
	// done programming
	public static String DEFAULT_OPEN_DIR = null;

	public static final int NUMBER_OF_RECENT_FILES_OPENED = 5; // CB added

	public static final int SCHEMATIC_TYPE = 0; // CB added

	public static final int PROJECT_TYPE = 1; // CB added

	public static final int PROPERTIES_TYPE = 2; // CB added

	public static final String[] PREFS_FILE_TYPE_STRINGS = { "RecentSchematic",
			"RecentProject", "RecentProperties" }; // CB added

	public static final int DEFAULT_WIDTH = 800; // CB added

	public static final int DEFAULT_HEIGHT = 600; // CB added

	public static final int CALSIM_II_STYLE = 0; // CB added

	public static final int CALSIM_III_STYLE = 1; // CB added

	static final int[] STYLE = { CALSIM_II_STYLE, CALSIM_III_STYLE }; // CB
	// added

	static final String[] STYLE_NAME = { "CAD", "Flow" }; // CB added

	public static final Color ARC_GREEN = Color.decode("#007000"); // CB added

	public static final Color NODE_GREEN = Color.decode("#73BB73"); // CB added

	public static final Color YUCK = Color.decode("#BBBBFF"); // CB added

	public static final Color ALMOST_YELLOW = Color.decode("#EEEE00"); // CB
	// added

	public static final Color MUSTARD = Color.decode("#DDDD00"); // CB added

	// public static final Color BURNT_ORANGE = Color.decode("#C87800"); //CB
	// added - equiv to (200, 120, 0) fairly dark yellow-brown

	// public static final Color BURNT_ORANGE = Color.decode("#D88800"); //CB
	// added - equiv to (216, 136, 0) medium yellow-brown

	public static final Color BURNT_ORANGE = Color.decode("#E89800"); // CB
	// added
	// -
	// equiv
	// to
	// (232,
	// 152,
	// 0)

	public static final Color BROWN = Color.decode("#777700"); // CB added

	public static final Color VIOLET = Color.decode("#9900EE"); // CB added
	// Color.decode("#EE00EE")
	// is
	// Color.MAGENTA

	/*
	 * public static final String NONE = "None"; //CB added
	 * 
	 * public static final String SOLID = "Solid"; //CB added
	 * 
	 * public static final String DASHED = "Dashed"; //CB added
	 * 
	 * public static final String DOTTED = "Dotted"; //CB added
	 * 
	 * public static final String DASHDOT = "DashDot"; //CB added
	 * 
	 * public static final String DASHDOTDOT = "DashDotDot"; //CB added
	 */

	public static final String[] ALT_STRINGS = { "Alt. 1", "Alt. 2" };

	public final static int TAF = 0;

	public final static int CFS = 1;

	static private boolean imagesErrorDisplayed = false;

	static private Frame theApp = null;

	// State

	static boolean _isStandalone;

	static String _mainDir;

	static JFrame mainFrame;

	protected JTabbedPane tabbedPane;

	protected InputPanel inputPane;

	// protected BatchPanel batchPane;

	protected ConsolePanel consolePane;

	protected ToolPanel toolsPane;

	protected JPanel outputPane;

	protected HashMap myMap = new HashMap();

	// CB protected SchematicView myCurrentView; CHANGED INTERNALFRAME TO HAVE A
	// SchematicView instance variable

	protected JDesktopPane myDesktop;

	// CB protected JGoPalette myPalette;

	// protected JGoPalette[] myPalette; //CB for multiple palette groups (e.g.,
	// CalSim II, CalSim III styles)

	protected JTabbedPane myPaletteTabbedPane;

	protected JMenuBar mainMenuBar;

	protected JToolBar toolBar1; // CB renamed

	private JToolBar toolBar2; // CB added

	JRadioButton _TAFButton = new JRadioButton(); // CB added

	JRadioButton _CFSButton = new JRadioButton(); // CB added

	protected JMenu filemenu;

	// private JMenu recentFilesSubmenu;

	private JMenuItem[] recentSchematicFilesOpenMenuItems = new JMenuItem[NUMBER_OF_RECENT_FILES_OPENED]; // CB
	// added

	private JMenuItem[] recentProjectFilesOpenMenuItems = new JMenuItem[NUMBER_OF_RECENT_FILES_OPENED]; // CB
	// added

	private JMenuItem[] recentPropertyDirectoryOpenMenuItems = new JMenuItem[NUMBER_OF_RECENT_FILES_OPENED]; // CB
	// added

	private String _currentOpenPropertyLocation; // CB added

	protected JMenu editmenu;

	protected JMenu viewmenu;

	protected JMenu dssmenu;

	protected JMenu propertyMenu; // CB added

	protected JMenu insertmenu;

	protected JMenu layoutmenu;

	protected JMenu helpmenu;

	protected JDialog myOverviewDialog;

	private int myDocCount = 1;

	private int _messageLevel = 0;
	DssFrame _DssFrame = null;

	JComboBox _dateBox; // CB added

	JButton _forward1PeriodButton, _back1PeriodButton, _forward1BlockButton,
			_back1BlockButton; // CB added

	JCheckBox _showValueBoxesCheckbox; // CB added

	private JLabel _monthlyProgressLabel = new JLabel("  Monthly Data:");

	private JLabel _longtermProgressLabel = new JLabel("   Long-term Avg:");

	private JProgressBar _monthlyProgressBar = new JProgressBar(); // CB added

	private JProgressBar _longtermProgressBar = new JProgressBar(); // CB added

	private static Preferences _userPrefs = Preferences // CB added
			.userNodeForPackage(MainFrame.class);

	private Study sty = new Study();

	@Override
	public Vector<String> getSelectedNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetValueNodeNames() {
		// TODO Auto-generated method stub

	}

	private String chooseFileToOpen() {
		Preferences p = Preferences.userNodeForPackage(MainFrame.class);
		String currentDirectory = p.get("SCHEMATICS_DIRECTORY", System
				.getProperty("user.dir"));
		JFileChooser chooser = new JFileChooser(currentDirectory);
		chooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "schematics";
			}

			@Override
			public boolean accept(File f) {
				return f != null
						&& f.isFile()
						&& (f.getName().toLowerCase().endsWith(".xml") || f
								.getName().toLowerCase().endsWith(".sch"))
						|| f.isDirectory();
			}
		});
		int rval = chooser.showOpenDialog(this);
		if (rval != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		File selectedFile = chooser.getSelectedFile();
		p.put("SCHEMATICS_DIRECTORY", selectedFile.getParent());
		return selectedFile.getAbsolutePath();
	}

	public String chooseFileToSave() {
		return chooseFileToSave(this);
	}

	public static String chooseFileToSave(Component parent) {
		Preferences p = Preferences.userNodeForPackage(MainFrame.class);
		String currentDirectory = p.get("SCHEMATICS_DIRECTORY", System
				.getProperty("user.dir"));
		JFileChooser chooser = new JFileChooser(currentDirectory);
		chooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "schematics";
			}

			@Override
			public boolean accept(File f) {
				return f != null && f.isFile();

			}
		});
		int rval = chooser.showSaveDialog(parent);
		if (rval != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		File selectedFile = chooser.getSelectedFile();
		p.put("SCHEMATICS_DIRECTORY", selectedFile.getParent());
		return selectedFile.getAbsolutePath();
	}

	@Override
	public void clearValues() {
		getCurrentView().clearAllValueBoxes();
	}
}
