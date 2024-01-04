/*

 Copyright (C) 1998, 2000 State of California, Department of Water Resources.

 This program is licensed to you under the terms of the GNU General Public
 License, version 2, as published by the Free Software Foundation.

 You should have received a copy of the GNU General Public License along
 with this program; if not, contact Dr. Sushil Arora, below, or the
 Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.

 THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA DEPARTMENT
 OF WATER RESOURCES AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE CALIFORNIA DEPARTMENT OF WATER RESOURCES OR ITS
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 PROCUREMENT OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS;
 OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 For more information, contact:

 Dr. Sushil Arora
 California Dept. of Water Resources
 Office of State Water Project Planning, Hydrology and Operations Section
 1416 Ninth Street
 Sacramento, CA  95814
 916-653-7921
 sushil@water.ca.gov

 */

package calsim.msw;

import calsim.app.*;
import calsim.gui.GuiUtils;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import vista.gui.DocumentWriter;
import wrims.schematic.Schematic;
import wrims.schematic.SchematicUtils;

/**
 * GUI for Multistudy runner
 *
 * @author Joel Fenolio
 * @author Clay Booher
 */

public class MSWGui extends JFrame implements ActionListener, ComponentListener {
	/**
	 * Constructor for MSW Gui. Accepts a string from the batch file that if not
	 * null set the tolerance limits. Instantiates the Frame and calls the
	 * appropriate methods to construct the gui
	 */
	public MSWGui() {
		super(GuiUtils.getProgramName() + " Multistudy Runner");
		JMenuBar mb = new JMenuBar();
		mb.add(createFileMenu());
		mb.add(createDebugMenu());
		mb.add(createToolMenu());
		setJMenuBar(mb);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				exit();
			}
		});

		addComponentListener(this);

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(createMainFilePanel(), BorderLayout.NORTH);
		northPanel.add(createTablePanel(), BorderLayout.CENTER);
		northPanel.add(createOptionsPanel(), BorderLayout.SOUTH);

/*		combined TW and position panel to have more vertical space for the new textarea
		gc.gridx = 0;
		gc.gridy = 3;
		gc.insets = new Insets(5, 0, 0, 0);
		gc.anchor = GridBagConstraints.CENTER;
		panel.add(createTWPanel(), gc);

		gc.insets = new Insets(15, 0, 0, 0);
		gc.gridx = 0;
		gc.gridy = 4;
		gc.anchor = GridBagConstraints.CENTER;
		panel.add(createPositionPanel(), gc);
*/
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(createTimeAndTermPanel(), BorderLayout.NORTH);

		JPanel runAndExecutionPanel = new JPanel(new BorderLayout());
		runAndExecutionPanel.add(createRunPanel(), BorderLayout.NORTH);
		runAndExecutionPanel.add(createExecutionPanel(), BorderLayout.CENTER);
		centerPanel.add(runAndExecutionPanel, BorderLayout.CENTER);

		JPanel fullPanel = new JPanel(new BorderLayout());
		fullPanel.add(northPanel, BorderLayout.NORTH);
		fullPanel.add(centerPanel, BorderLayout.CENTER);

		getContentPane().add(fullPanel);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});

		pack();
		// for jdk 1.2.2? or greater:
//		readPreferences();
		// for jdk 1.1.8:
		setSize(new Dimension(Math.min(Toolkit.getDefaultToolkit().getScreenSize().width,
			DEFAULT_FRAME_WIDTH), Math.min(Toolkit.getDefaultToolkit().getScreenSize().height,
			DEFAULT_FRAME_HEIGHT)));
		_table.getColumn(_table.getColumnName(0)).setPreferredWidth(53);
	    _table.getColumn(_table.getColumnName(1)).setPreferredWidth(225);
	    _table.getColumn(_table.getColumnName(2)).setPreferredWidth(60);
	    _table.getColumn(_table.getColumnName(3)).setPreferredWidth(60);
	    _table.getColumn(_table.getColumnName(4)).setPreferredWidth(300);
	    _table.getColumn(_table.getColumnName(5)).setPreferredWidth(53);

		setVisible(true);

		if (Toolkit.getDefaultToolkit().getScreenSize().width < MINIMUM_FRAME_WIDTH + 10) {
			JOptionPane.showMessageDialog(null, "Need screen resolution of at least "
				+ (MINIMUM_FRAME_WIDTH + 10) + " x " + (MINIMUM_FRAME_HEIGHT + 10),
					"Screen Resolution Inadequate", JOptionPane.WARNING_MESSAGE);
		}
		_numyears.setEnabled(false);
		_runButton.setEnabled(true);
		_stopButton.setEnabled(false);
	}

	/**
	 * Creates file menu for the gui for the gui menubar
	 */
	public JMenu createFileMenu() {
		JMenu menu = new JMenu(" File   ");
		menu.setMnemonic(KeyEvent.VK_F);
		JMenuItem open = new JMenuItem("Open");
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
		JMenuItem save = new JMenuItem("Save");
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
		JMenuItem saveAs = new JMenuItem("Save As");
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
		JMenuItem exit = new JMenuItem("Exit");
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK));
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					openFile();
				} catch (Exception ioe) {
					ioe.printStackTrace(System.err);
				}
			}
		});
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					saveFile();
				} catch (IOException ioe) {
					ioe.printStackTrace(System.err);
				}
			}
		});
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					saveAsFile();
				} catch (IOException ioe) {
					ioe.printStackTrace(System.err);
				}
			}
		});
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		menu.add(open);
		menu.add(save);
		menu.add(saveAs);
		menu.add(exit);
		return menu;
	}

	/**
	 * Creates menu bar for the gui
	 */
	public JMenu createDebugMenu() {
		JMenu menu = new JMenu(" Debug   ");
		menu.setMnemonic(KeyEvent.VK_D);
		final JCheckBoxMenuItem wrapper = new JCheckBoxMenuItem("Wrapper code");
		wrapper.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_MASK));
		wrapper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setWrapperDebugOption(wrapper.isSelected());
			}
		});

		final JCheckBoxMenuItem transfer = new JCheckBoxMenuItem("Transfer code");
		transfer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_MASK));
		transfer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setTransferDebugOption(transfer.isSelected());
			}
		});

		final JCheckBoxMenuItem build = new JCheckBoxMenuItem("Build code");
		build.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK));
		build.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setBuildDebugOption(build.isSelected());
			}
		});
		menu.add(wrapper);
		menu.add(transfer);
		menu.add(build);
		return menu;
	}

	/**
	 * CB - added - Creates tools bar for the gui
	 */
	public JMenu createToolMenu() {
		JMenu menu = new JMenu("  Tools  ");
		menu.setMnemonic(KeyEvent.VK_T);
		final JCheckBoxMenuItem schematic = new JCheckBoxMenuItem("Schematic Window");
		schematic.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
		schematic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				new Schematic();
				SchematicUtils.schematic = new Schematic();
			}
		});
		menu.add(schematic);
		return menu;
	}


//	added to combine TW and position panel into one panel to save vertical GUI space
	public JPanel createTimeAndTermPanel() {
	    JPanel p = new JPanel();
	    p.setLayout(new GridBagLayout());
	    GridBagConstraints gc = new GridBagConstraints();
	    gc.anchor = GridBagConstraints.CENTER;
	    gc.gridx = 0;
	    gc.gridy = 0;
	    gc.insets = new Insets(15,5,15,8);
	    gc.fill = GridBagConstraints.BOTH; // no effect?
	    p.add(createTWPanel(), gc);
	    gc.gridx = 1;
	    gc.insets = new Insets(25,12,15,0);
	    p.add(createInitOptionsPanel(), gc);

	    gc.gridx = 2;
	    gc.insets = new Insets(20,20,15,5);
	    gc.fill = GridBagConstraints.NONE;
	    p.add(createPositionPanel(),gc);

	    return p;
	}

	public JPanel createPositionPanel() {
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(2, 2, 2, 3);
		p.add(new JLabel("    Enabled  "), gc);

		gc.gridx = 1;
		gc.insets = new Insets(0, 2, 3, 3);
		p.add(_position, gc);

		gc.gridx = 2;
		gc.insets = new Insets(2, 2, 2, 3);
		p.add(new JLabel("    Periods   "), gc);
//		p.add(periods,gc);  // uncomment to allow enabling/diabling
//		periods.setEnabled(false);

		gc.gridx = 3;
		gc.gridy = 0;
		p.add(_numyears, gc);

		_position.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_position.isSelected()) {
					_numyears.setEnabled(true);
					MSWUtil.position = true;
					AppUtils.position = true;
					if (_DVAsInitRadioButton.isSelected()) {
						_initRadioButton.setSelected(true);
					}
				} else {
					_numyears.setEnabled(false);
					MSWUtil.position = false;
					AppUtils.position = false;
				}
			}
		});
    	p.setBorder(new TitledBorder("Position Analysis"));
		return p;
	}

	/**
	 * Opens multistudy file
	 */
	public void openFile() throws Exception {
//		int tableWidth;  //CB added for proper table column widths
		String filename = getFileName(FileDialog.LOAD);
		if (filename == null || filename.trim().length() == 0) return;
		_mainFile = filename;
		// InputStream is = new FileInputStream(_mainFile);
		CSVParser reader = new CSVParser(_mainFile);
		_mainFileField.setText(_mainFile);
		Vector data = new Vector(1, 1);
		int index = 0;
		while (true) {
			String[] line = reader.nextLine();
			if (line == null) break;
			// Echo the file contents
			for (int i = 0; i < line.length; ++i) {
				if (i > 0) System.out.print(" ");
				if (i == line.length - 1) System.out.println(line[i]);
				else System.out.print(line[i]);
			}

			int num = line.length;
			if (num == 1) {
				_name = line[0];
			} else if (num == 3) {
				String sm = line[1].substring(0, 3).toUpperCase();
				String sy = line[1].substring(3, line[1].length());
				String em = line[2].substring(0, 3).toUpperCase();
				String ey = line[2].substring(3, line[2].length());
				_smonselector.setSelectedItem(sm.trim());
				_emonselector.setSelectedItem(em.trim());
				_syrselector.setSelectedItem(sy);
				_eyrselector.setSelectedItem(ey);
			} else if (num == 5) {
				Vector vrow = new Vector(1, 1);
				vrow.addElement(new Integer(index).toString());
				index++;
				for (int i = 0; i < line.length; i++) {
					vrow.addElement(line[i]);
				}
				data.addElement(vrow);
			}
		}
//		writeColumnWidthPreferences(); // Need JDK of 1.2.2? or greater
		_model.setDataVector(data, _cols);
//		readColumnWidthPreferences();
		// for jdk 1.1.8:
	    //CDB Three width sections below added/changed to set proper table column widths
	    //    upon file opening and column resizing
	    _table.getColumn(_table.getColumnName(0)).setPreferredWidth(53);
	    _table.getColumn(_table.getColumnName(1)).setPreferredWidth(225);
	    _table.getColumn(_table.getColumnName(2)).setPreferredWidth(60);
	    _table.getColumn(_table.getColumnName(3)).setPreferredWidth(60);
	    _table.getColumn(_table.getColumnName(4)).setPreferredWidth(300);
	    _table.getColumn(_table.getColumnName(5)).setPreferredWidth(53);

	    _runButton.setEnabled(true);
	    resizeTableValidateAndRepaintAll();
	}

	private void resizeTable() {
//	    _table.setPreferredScrollableViewportSize(_table.getPreferredSize());
//	    int height = Math.max(_table.getPreferredSize().height, 5 * _table.getRowHeight());
		int height = 5 * _table.getRowHeight();
	    _table.setPreferredScrollableViewportSize(new Dimension(_table.getPreferredSize().width,
	    	height));
		_js.setMinimumSize(new Dimension(_mainFileField.getPreferredSize().width,
			_table.getTableHeader().getPreferredSize().height + height));
	}

	private void resizeTableValidateAndRepaintAll() {
		resizeTable();
		validate();
		repaint();
	}

	/**
	 * Saves msty file
	 */
	public void saveFile() throws IOException {
		if (_mainFile.trim().equals("")) {
			String filename = getFileName(FileDialog.SAVE);
			if (filename == null || filename.trim().length() == 0) return;
			_mainFile = filename;
			_mainFileField.setText(_mainFile);
		}
		if (_name.equals("")) {
			File f = new File(_mainFile);
			_name = f.getName();
		}
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
			new FileOutputStream(_mainFile)));
		writer.print(_name);
		writer.println();
		writer.print("* Start date and end date, " + getStartMonth()
				+ getStartYear() + ", " + getEndMonth() + getEndYear());
		writer.println();
		for (int i = 0; i < _table.getRowCount(); i++) {
			for (int j = 1; j < _table.getColumnCount(); j++) {
				String value = (String) _model.getValueAt(i, j);
				writer.print(value);
				if (j < _table.getColumnCount() - 1)
					writer.print(",");
			}
			writer.println();
		}
		writer.close();
	}

	/**
	 * Saves msty file
	 */
	public void saveAsFile() throws IOException {
		String filename = getFileName(FileDialog.SAVE);
		if (filename == null|| filename.trim().length() == 0) return;
		_mainFile = filename;
		_mainFileField.setText(_mainFile);
		if (_name.equals("")) {
			File f = new File(_mainFile);
			_name = f.getName();
		}
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(_mainFile)));
		writer.print(_name);
		writer.println();
		writer.print("* Start date and end date, " + getStartMonth()
				+ getStartYear() + ", " + getEndMonth() + getEndYear());
		writer.println();
		for (int i = 0; i < _table.getRowCount(); i++) {
			for (int j = 1; j < _table.getColumnCount(); j++) {
				String value = (String) _model.getValueAt(i, j);
				writer.print(value);
				if (j < _table.getColumnCount() - 1)
					writer.print(",");
			}
			writer.println();
		}
		writer.close();
	}

	/**
	 * exits when menu item exit is selected
	 */
	public void exit() {
		if (_changed) {
			int option = JOptionPane.showConfirmDialog(null,
					"Save before exiting?", "Data Modified",
					JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				try {
					saveFile();
				} catch (IOException ioe) {
					ioe.printStackTrace(System.err);
					System.exit(1);
				}
			}
		}
//		writePreferences();
		System.exit(0);
	}

	/**
	 * creates the panel for holding the model selector combo box and its label
	 */
	private JPanel createMainFilePanel() {
		JPanel p = new JPanel();
		JLabel l = new JLabel("   Main File   ");
		p.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		_mainFileField.setMinimumSize(_mainFileField.getPreferredSize());

		gc.gridx = 0;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.NONE;
		p.add(l, gc);
		gc.gridx = 1;
		gc.insets = new Insets(15, 2, 15, 5);
		p.add(_mainFileField, gc);
		gc.gridx = 2;
		p.add(_chooseMain, gc);
		_chooseMain.setMnemonic(KeyEvent.VK_C);
	    _chooseMain.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
				try {
					openFile();
				} catch (Exception ioe) {
					ioe.printStackTrace(System.err);
				}
			}
		});
		return p;
	}

	private JPanel createRunPanel() {
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.WEST;

		/* CB added */
		gc.insets = new Insets(18, 0, 0, 25);
		JPanel parserPanel = new JPanel();
		Integer[] delays = {0, 50, 100, 200, 300, 400, 500, 750, 1000};
		JComboBox parseFileDelayBox = new JComboBox(delays);
		parseFileDelayBox.setEditable(true);
		parseFileDelayBox.setSelectedItem(new Integer(_userPrefs.getInt(PARSE_DELAY, 0)));
		parseFileDelayBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int value = ((Integer)((JComboBox)e.getSource()).getSelectedItem()).intValue();
				_parseFileDelay = ((Integer)((JComboBox)e.getSource()).getSelectedItem()).intValue();
				_userPrefs.putInt(PARSE_DELAY, value);
			}
		});
		parserPanel.add(new JLabel("Parse Delay:"));
		parserPanel.add(parseFileDelayBox);
		p.add(parserPanel, gc);

		/* CB added */
		gc.insets = new Insets(0, 0, 0, 15);
		gc.gridx = 1;
	    JCheckBox hideWarnings = new JCheckBox("Hide Warnings");
	    hideWarnings.setSelected(true);
	    hideWarnings.setToolTipText(
	    	"Hides \"local name will be same as global\" warnings for decision variables and timeseries");
		hideWarnings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_hideWarnings =((JCheckBox)e.getSource()).isSelected();
			}
		});
		p.add(hideWarnings, gc);

		/* CB added */
		gc.gridy = 1;
		gc.insets = new Insets(-13, 0, 0, 15);
	    JCheckBox hideProgressDetails = new JCheckBox("Hide Progess Details");
	    hideProgressDetails.setSelected(true);
	    hideProgressDetails.setToolTipText(
	    	"Hides the more detailed progress messages, such as each file parsed");
		hideProgressDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_hideProgressDetails =((JCheckBox)e.getSource()).isSelected();
			}
		});
		p.add(hideProgressDetails, gc);

		gc.gridx = 2;
		gc.gridy = 0;
		gc.insets = new Insets(18, 0, 0, 15);
		gc.anchor = GridBagConstraints.CENTER;
		_runButton.setMnemonic(KeyEvent.VK_R);
		_runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_runButton.setEnabled(false);
				run();
			}
		});
		p.add(_runButton, gc);

		gc.gridx = 3;
		gc.insets = new Insets(18, 5, 0, 0);
		_stopButton.setMnemonic(KeyEvent.VK_S);
		_stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_output.println("Stopping modeling run.  One moment please.");
				_output.println();
				_output.flush();
				MSWGui.this.setWasStopped(true);
			}
		});
		p.add(_stopButton, gc);
		return p;
	}

	/**
	 * Creates panel that holds textfields and buttons for the studies
	 */
	  private JPanel createTablePanel() {
		JPanel p = new JPanel(new BorderLayout());
		JPanel buttons = new JPanel(new GridBagLayout());

		Vector data = new Vector(1, 1);
		final Vector row = new Vector(1, 1);
		row.addElement("0");
		row.addElement("null");
		row.addElement("null");
		row.addElement("null");
		row.addElement("null");
		row.addElement("null");
		data.addElement(row);

		Vector cols = new Vector(1, 1);
		cols.addElement("Order");
		cols.addElement("Study");
		cols.addElement("Periods");
		cols.addElement("Intermediate");
		cols.addElement("Interface");
		cols.addElement("DV/Init");
		_cols = cols;

		_model = new DefaultTableModel(data, cols);
		_table = new JTable(_model);
		_table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		_js = new JScrollPane(_table);
		resizeTable();

		_add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Vector rowData = (Vector) row.clone();
				rowData.setElementAt(new Integer(_model.getRowCount()).toString(), 0);
				_model.addRow(rowData);
				resizeTableValidateAndRepaintAll();
			}
		});
		_remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_table.getSelectedRowCount() > 0) {
					int[] rows = _table.getSelectedRows();
					for (int i = rows.length - 1; i >= 0; --i) {
						_model.removeRow(rows[i]);
					}
					resizeTableValidateAndRepaintAll();
				}
			}
		});
		_chooseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int col = _table.getSelectedColumn();
				String fpath = " ";
				if (col == 1 || col == 3 || col == 4) {
					FileDialog f = new FileDialog(MSWGui.this, "Choose File");
					f.setVisible(true);
					fpath = f.getDirectory() + f.getFile();
					// System.out.println(fpath + _table.getSelectedRow() + " " + col);
					if (fpath != null && fpath.trim().length() > 0
							&& _table.getSelectedRowCount() == 1) {
						_model.setValueAt(fpath, _table.getSelectedRow(), col);
					}
				}
			}
		});

		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = 0;
		gc.gridx = 0;
		gc.insets = new Insets(0, 4, 0, 4);
		buttons.add(_add, gc);
		_add.setMnemonic(KeyEvent.VK_A);
		gc.gridx = 1;
		buttons.add(_remove, gc);
		_remove.setMnemonic(KeyEvent.VK_M);
		gc.gridx = 2;
		buttons.add(_chooseFile, gc);
		_chooseFile.setMnemonic(KeyEvent.VK_H);
		p.add(_js, BorderLayout.NORTH);
		p.add(new JLabel(" "), BorderLayout.CENTER);
		p.add(buttons, BorderLayout.SOUTH);
		return p;
	}

	/**
	 * creates the panel for holding the options textfield and its label
	 */
	private JPanel createOptionsPanel() {
		JPanel p = new JPanel();
		JLabel l = new JLabel("  XA Ops  ");
		p.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.CENTER;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(20, 2, 5, 3);
		p.add(l, gc);
		gc.gridx = 1;
//		gc.fill = GridBagConstraints.HORIZONTAL; // NO EFFECT
		p.add(_options, gc);
		return p;
	}



	/**
	 * Creates panel for the year combo boxes
	 */
	private JPanel createTWPanel() {
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();

		for (int i = 1921; i < 2101; i++) {
			String s = new String(new Integer(i).toString());
			_syrselector.addItem(s);
			_eyrselector.addItem(s);
		}
		_syrselector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setStartYear((String) _syrselector.getSelectedItem());
			}
		});
		_eyrselector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setEndYear((String) _eyrselector.getSelectedItem());
			}
		});
		_syrselector.setEditable(true);
		_eyrselector.setEditable(true);

		_smonselector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setStartMonth((String) _smonselector.getSelectedItem());
			}
		});
		_emonselector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setEndMonth((String) _emonselector.getSelectedItem());
			}
		});
		_eyrselector.setSelectedIndex(73);
		gc.gridx = 0;
		gc.gridy = 0;
		p.add(new JLabel("         "), gc);
		gc.gridx = 1;
		p.add(new JLabel("Month"), gc);
		gc.gridx = 2;
		gc.insets = new Insets(1, 5, 1, 3);
		p.add(new JLabel("Year"), gc);

		gc.gridx = 0;
		gc.gridy = 1;
		p.add(new JLabel("   Start "), gc);
		gc.gridx = 1;
		gc.insets = new Insets(1, 2, 1, 3);
		gc.fill = GridBagConstraints.BOTH; // no effect?
		p.add(_smonselector, gc);
		gc.gridx = 2;
		gc.insets = new Insets(1, 5, 1, 3);
		p.add(_syrselector, gc);

		gc.gridx = 0;
		gc.gridy = 2;
		p.add(new JLabel("    Stop  "), gc);
		gc.gridx = 1;
		gc.insets = new Insets(1, 2, 1, 3);
		p.add(_emonselector, gc);
		gc.gridx = 2;
		gc.insets = new Insets(1, 5, 1, 3);
		p.add(_eyrselector, gc);

		return p;
	}

	private JPanel createInitOptionsPanel() {
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1, 1));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(3, 1));

		_initButtonGroup.add(_initRadioButton);
		_initButtonGroup.add(_DVAsInitRadioButton);
		_initButtonGroup.add(_DVdeleteInitRadioButton);

		buttonPanel.add(_initRadioButton);
		_initRadioButton.addChangeListener(new ChangeListener() {
			// Due to _initRadioButton being in a ButtonGroup, only need to take action
			//   if the button is selected for each button in the group.
			public void stateChanged(ChangeEvent e) {
				if (_initRadioButton.isSelected()) {
					MSWGui.this.setDvFileAsInitFile(false);
					MSWGui.this.setDVDelete(false);
				}
			}
		});
		_initRadioButton.setSelected(true);
		buttonPanel.add(_DVAsInitRadioButton);
		_DVAsInitRadioButton.addChangeListener(new ChangeListener() {
			// Due to __DVAsInitRadioButton being in a ButtonGroup, only need to take action
			//   if the button is selected for each button in the group.
			public void stateChanged(ChangeEvent e) {
				if (_DVAsInitRadioButton.isSelected()) {
					MSWGui.this.setDvFileAsInitFile(true);
					MSWGui.this.setDVDelete(false);
					MSWGui.this._position.setSelected(false);
				}
			}
		});
		buttonPanel.add(_DVdeleteInitRadioButton);
		_DVdeleteInitRadioButton.addChangeListener(new ChangeListener() {
			// Due to _DVdeleteInitRadioButton being in a ButtonGroup, only need to take action
			//   if the button is selected for each button in the group.
			public void stateChanged(ChangeEvent e) {
				if (_DVdeleteInitRadioButton.isSelected()) {
					MSWGui.this.setDvFileAsInitFile(false);
					MSWGui.this.setDVDelete(true);
				}
			}
		});
		p.add(buttonPanel);
		p.setBorder(new TitledBorder("Initialization Options"));
		return p;
	}

	private JPanel createExecutionPanel() {
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();

		_twArea.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				_twArea.setCaretPosition(_twArea.getDocument().getLength());
			}

			public void removeUpdate(DocumentEvent e) {
				_twArea.setCaretPosition(_twArea.getDocument().getLength());
			}

			public void changedUpdate(DocumentEvent e) {
			}
		});

		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		p.add(_twScrollArea = createScrollPane(_twArea), gc); // replaced textfield
		_twScrollArea.setMinimumSize(_twScrollArea.getPreferredSize());
		_twScrollArea.setBorder(new TitledBorder("Status"));
		return p;
	}

	private JScrollPane createScrollPane(JTextArea textArea) {
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		final JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		return areaScrollPane;
	}

//	synchronized void setWasStopped(boolean isStopped) {
	void setWasStopped(boolean isStopped) {
//		_stopButton.setEnabled(!isStopped);
		final boolean stopped = isStopped;
//	    Runnable setStopButtonEnabled = new Runnable() {
//	        public void run() {
	        	_stopButton.setEnabled(!stopped);
//	        }
//	    };
//	    try {
//	    	SwingUtilities.invokeAndWait(setStopButtonEnabled); // threw error!!!!!!!
//	    } catch (InterruptedException e) {
//	    	e.printStackTrace();
//	    } catch (InvocationTargetException e) {
//	    	e.printStackTrace();
//	    }
		setStopPushed(isStopped);
	}

//	synchronized void setStopPushed(boolean wasStopPushed) {
	void setStopPushed(boolean wasStopPushed) {
		_wasStopPushed = wasStopPushed;
	}

//	synchronized public boolean wasStopPushed() {
	public boolean wasStopPushed() {
		return _wasStopPushed;
	}

//	synchronized void setRunDone(boolean isDone) {
	void setRunDone(boolean isDone) {
		_isRunDone = isDone;
	    Runnable setRunButtonEnabled = new Runnable() {
	        public void run() {
	        	_runButton.setEnabled(_isRunDone);
	        }
	    };
	    try {
	    	SwingUtilities.invokeAndWait(setRunButtonEnabled);
	    } catch (InterruptedException e) {
	    	e.printStackTrace();
	    } catch (InvocationTargetException e) {
	    	e.printStackTrace();
	    }
	}

//	synchronized boolean isRunDone() {
	boolean isRunDone() {
		return _isRunDone;
	}

	/**
	 * Sets the beginning year
	 */
	public void setStartYear(String year) {
		_years[0] = year;
	}

	/**
	 * Sets the ending year
	 */
	public void setEndYear(String year) {
		_years[1] = year;
	}

	/**
	 * gets the start year
	 */
	public String getStartYear() {
		return _years[0];
	}

	/**
	 * gets the end year
	 */
	public String getEndYear() {
		return _years[1];
	}

	/**
	 * Sets the beginning month
	 */
	public void setStartMonth(String month) {
		_months[0] = month;
	}

	/**
	 * Sets the ending month
	 */
	public void setEndMonth(String month) {
		_months[1] = month;
	}

	/**
	 * gets the start month
	 */
	public String getStartMonth() {
		return _months[0];
	}

	/**
	 * gets the end month
	 */
	public String getEndMonth() {
		return _months[1];
	}

	/**
	 * gets the start date
	 */
	public String getStartDate() {
		return _dates[0];
	}

	/**
	 * gets the end date
	 */
	public String getEndDate() {
		return _dates[1];
	}

	/**
	 * Displays the file dialog and returns the file name of the file selected
	 * Type refers to the fields LOAD and SAVE from the FileDialog class
	 * Extension refers to the file extension you want it to filter for
	 */
	public String getFileName(int type) {
		FileDialog fileDialog = new FileDialog(this, "", type);
		fileDialog.setModal(true);
		// fileDialog.setFile(extension);
		fileDialog.setVisible(true);
		if (fileDialog.getFile() == null)
			return null;
		// if (fileDialog.getFile().equals(extension)) return null;
		if (fileDialog.getDirectory() == null)
			return null;
		return new String(fileDialog.getDirectory() + fileDialog.getFile());
	}

	private void setDvFileAsInitFile(boolean isDVAsInit) {
		_isDVAsInit = isDVAsInit;
	}

	public boolean isDvFileAsInitFile() {
		return _isDVAsInit;
	}

	private void setDVDelete(boolean deleteDV) {
		this._isDVDelete = deleteDV;
	}

	public boolean isDVDelete() {
		return _isDVDelete;
	}

	/**
	 * takes the data specified/stored in the gui and passes it to
	 * multistudyrunner
	 */
	public void run() {
		if (getPosAnalysis()) {
			MSWUtil.position = true;
			MSWUtil.nperiods = getPosPeriods();
		}
		try {
//			writeColumnWidthPreferences();
			saveFile();
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(null, "IOException: " + ioe, ioe.getMessage(),
				JOptionPane.ERROR_MESSAGE);
			ioe.printStackTrace(System.err);
		}
		if (_mswThread != null) {
			if (_mswThread.isAlive()) _mswThread.interrupt();
			_mswThread = null;
			_output = null;
		}
		_twArea.setText("");
		if (_output == null)
			_output = new PrintWriter(new DocumentWriter(_twArea.getDocument()));
		_mswThread = new MSWThread(_mainFile, this);

//		if (_detectDoneTimer == null) _detectDoneTimer = new javax.swing.Timer(700, this);
		if (_mswThread != null) {
			_mswThread.start();
			// CB - tried join() by itself and in a separate thread, but both locked the gui
		}
//		_detectDoneTimer.start();
	}

	/**
	 * Every timer event, checks size of JFrame to see if it's too large or too
	 * small for proper display. If so, resizes it so components are displayed
	 * properly. CB - added due to output area display problems mostly.
	 */
	public void actionPerformed(ActionEvent e) {
/*		if (e.getSource() == GUIsizeTimer) {
			if (Toolkit.getDefaultToolkit().getScreenSize().width > 1024) {
				if (_fr.getSize().height >= Toolkit.getDefaultToolkit().getScreenSize().height - 60) {
		 			// _fr.setState(Frame.NORMAL); //CDB method not available till later API (jdk1.4)
		 			_fr.setSize(new Dimension(_fr.getSize().width,
		 				Toolkit.getDefaultToolkit().getScreenSize().height - 60));
		 		}
		 	}
		}
		if (e.getSource() == _detectDoneTimer) {
			if (isRunDone()) {
				if (_detectDoneTimer != null) _detectDoneTimer.stop();
				_detectDoneTimer = null;
				_output.println("Run complete. " + _runDoneMessage + ".");
				System.out.println("Run complete. " + _runDoneMessage + ".");
				_mswThread = null; // make sure thread is dead so user can run it again
				_output = null; // NEED?
				JOptionPane.showMessageDialog(null, "Run finished", _runDoneMessage,
					JOptionPane.OK_OPTION);
			}
		}*/
	}

	public void componentResized(ComponentEvent e) {
		if (e.getSource() == this) {
			// To prevent resizing GUI too small.
			if (getSize().width <= MINIMUM_FRAME_WIDTH)
				setSize(MINIMUM_FRAME_WIDTH, getSize().height);
			if (getSize().height <= MINIMUM_FRAME_HEIGHT)
				setSize(getSize().width, MINIMUM_FRAME_HEIGHT);
			repaint();
		}
	}

	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}

	public boolean checkNPeriods() {
		int np = 0;
		for (int i = 0; i < _table.getRowCount(); i++) {
			String svalue = (String) _model.getValueAt(i, 2);
			int ivalue = new Integer(svalue).intValue();
			if (ivalue > np)
				np = ivalue;
		}
		int ratio = MSWUtil.nperiods / np;
		int remainder = MSWUtil.nperiods - ratio * np;
		if (remainder != 0 || ratio == 0) {
			JOptionPane.showMessageDialog(this,
				"Position Analysis nperiods needs to be divisible by: "
				+ new Integer(np).toString(), "Divisiblity error", JOptionPane.ERROR_MESSAGE);
			return false;
		} else {
			return true;
		}
	}

	void setRunDoneMessage(String message) {
		_runDoneMessage = message;
	}

	public String getRunDoneMessage() {
		return _runDoneMessage;
	}

	/**
	 * Sets the text in the textfield
	 */
	public void setOptions(String file) {
		_options.setText(file);
	}

	/**
	 * gets options specified in the textfield
	 */
	public String getOptions() {
		return _options.getText();
	}

	/**
	 * returns true if position analysis is selected
	 */
	public boolean getPosAnalysis() {
		return _position.isSelected();
	}

	/**
	 * the number of years for position analysis to run for
	 */
	public int getPosPeriods() {
		String s = _numyears.getText();
		if (s.equals(""))
			s = "0";
		return new Integer(s).intValue();
	}

	/**
	 * Make sure cursor is consistent over all components.
	 */
	public void setCursor(Cursor cursor) {
		super.setCursor(cursor);
		_mainFileField.setCursor(cursor);
		_options.setCursor(cursor);
		_twArea.setCursor(cursor);
	}

	/**
	 * Utility method.
	 * @param message
	 */
	void println(String message) {
		_output.println(message);
	}

	/**
	 * Utility method.
	 * @param message
	 * @param both if true, prnts to the output area and <code>System.out</code>
	 */
	void println(String message, boolean both) {
		 println(message);
		 if (both) System.out.println();
	}

	void setRunSuccessful(boolean wasSuccessful) {
		_wasRunSuccessful = wasSuccessful;
	}

	public boolean wasRunSuccessful() {
		return _wasRunSuccessful;
	}

	PrintWriter getOutputWriter() {
		return new PrintWriter(new DocumentWriter(_twArea.getDocument()));
	}

	private void setWrapperDebugOption(boolean isOn) {
		_wrapperDebug = isOn;
	}

	public boolean getWrapperDebugOption() {
		return _wrapperDebug;
	}

	private void setTransferDebugOption(boolean isOn) {
		_transferDebug = isOn;
	}

	public boolean getTransferDebugOption() {
		return _transferDebug;
	}

	private void setBuildDebugOption(boolean isOn) {
		_buildDebug = isOn;
	}

	public boolean getBuildDebugOption() {
		return _buildDebug;
	}

	void trimOutput() {
		try {
			int length = _twArea.getDocument().getLength();
			if (length > 300000)
				_twArea.getDocument().remove(0, length / 2);
		} catch (BadLocationException ble) {
			JOptionPane.showMessageDialog(null, "Bad Location Exception",
					"Bad Location Exception", JOptionPane.ERROR_MESSAGE);
		}
	}

/*	void readColumnWidthPreferences() {
		// Read column widths for columns which exist and set the column widths
		for (int i = 0; i < _table.getColumnModel().getColumnCount(); ++i) {
			_table.getColumnModel().getColumn(i).setPreferredWidth(
				_userPrefs.getInt(_table.getColumnModel().getColumn(i).getHeaderValue() + "Width",
					DEFAULT_COLUMN_WIDTH[i]));
		}
	}

	private void readPreferences() {
		readColumnWidthPreferences();
		readWindowPreferences();
	}

	private void readWindowPreferences() {
		// Read window preferences to set window location and size
		int x = _userPrefs.getInt("MSWGuiX", (Toolkit.getDefaultToolkit().getScreenSize().width -
				DEFAULT_FRAME_WIDTH) / 2);
		int y = _userPrefs.getInt("MSWGuiY", (Toolkit.getDefaultToolkit().getScreenSize().height -
				DEFAULT_FRAME_HEIGHT) / 2);
		setLocation(x, y);
		int width = _userPrefs.getInt("MSWGuiWidth", DEFAULT_FRAME_WIDTH);
		int height = _userPrefs.getInt("MSWGuiHeight", DEFAULT_FRAME_HEIGHT);
		setSize(width, height);
	}

	private void writePreferences() {
		writeColumnWidthPreferences();
		writeWindowPreferences();
	}

	void writeColumnWidthPreferences() {
		// Write preferred column widths for all columns
		for (int i = 0; i < _table.getColumnModel().getColumnCount(); ++i) {
			_userPrefs.putInt(_table.getColumnModel().getColumn(i).getHeaderValue()
					+ "Width", _table.getColumnModel().getColumn(i).getPreferredWidth());
		}
	}

	private void writeWindowPreferences() {
		// Write window location and size to preferences
		_userPrefs.put("MSWGuiX", String.valueOf(getX()));
		_userPrefs.put("MSWGuiY", String.valueOf(getY()));
		_userPrefs.put("MSWGuiWidth", String.valueOf(getWidth()));
		_userPrefs.put("MSWGuiHeight", String.valueOf(getHeight()));
	}
*/
	/**
	 * Main method
	 */
	public static void main(String args[]) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		new MSWGui();
	}

	boolean DEBUG = true;

	public final String CALSIM_HOME = "D:\\WRIMS1Development\\Calsim1.3\\calsim";

	//CB added for better maintenance
	public final static int DEFAULT_FRAME_WIDTH = 790;
	public final static int DEFAULT_FRAME_HEIGHT = 590;
	public final static int MINIMUM_FRAME_WIDTH = 590;
	public final static int MINIMUM_FRAME_HEIGHT = 490;
	public final static int[] DEFAULT_COLUMN_WIDTH = {30, 190, 35, 185, 185, 40};
//	public final static String FRAME_X_STRING = "MSWGuiX";

	public final String PARSE_DELAY = "Parse Delay";  //CB added for preferences

	protected Preferences _userPrefs = Preferences.userNodeForPackage(getClass());  //CB added

	private Vector _cols = new Vector(1, 1);

	private JButton _chooseMain = new JButton("Choose");

	private JButton _chooseFile = new JButton("Choose");

	private JButton _add = new JButton("Add");

	private JButton _remove = new JButton("Remove");

	private JCheckBox _position = new JCheckBox();

	private JTextField _numyears = new JTextField(5);

	private JTextField _mainFileField = new JTextField(50);

	private JTextField _options = new JTextField(50);

	public final String[] mon = { "OCT", "NOV", "DEC", "JAN", "FEB", "MAR",
			"APR", "MAY", "JUN", "JUL", "AUG", "SEP" };

	private JComboBox _smonselector = new JComboBox(mon);

	private JComboBox _emonselector = new JComboBox(mon);

	private JComboBox _syrselector = new JComboBox();

	private JComboBox _eyrselector = new JComboBox();

	private JRadioButton _initRadioButton = new JRadioButton("Init File");

	private JRadioButton _DVAsInitRadioButton = new JRadioButton("DV File");

	private JRadioButton _DVdeleteInitRadioButton = new JRadioButton("Init File/Delete DV");

	private ButtonGroup _initButtonGroup = new ButtonGroup();

	private String _mainFile = " ";

//	private String _tol = new String();

	private String[] _dates = { "oct1921", "sep1994" };

	private String[] _months = { "oct", "sep" };

	private String[] _years = { "1921", "1994" };

	private boolean _changed = false;

	private String _name = "";

	private JTable _table;

	private DefaultTableModel _model;

//	private javax.swing.Timer _detectDoneTimer;

	private PrintWriter _output;

	private JTextArea _twArea = new JTextArea(15, 60); // CB replaced _twField

	private JScrollPane _twScrollArea; // CB for scrolling _twArea

	private JButton _stopButton = new JButton("  Stop ");

	private JButton _runButton = new JButton("  Run  ");

	private boolean _isDVAsInit = false;

	private boolean _isDVDelete = false;

//	private boolean _autoNameBackup = false;

	private JScrollPane _js;

	private volatile boolean _isRunDone = false;

	private boolean _wasRunSuccessful = false;

	private volatile boolean _wasStopPushed = false;

	private String _runDoneMessage = "";

	private boolean _wrapperDebug = false;

	private boolean _transferDebug = false;

	private boolean _buildDebug = false;

	private MSWThread _mswThread;

	boolean _hideWarnings = true; // CB added detailed message switch per Easton's request

	boolean _hideProgressDetails = true; // CB added detailed message switch per Easton's request

//	boolean _hasUserMappedSectionOpenProblem = true; //CB added to fix a parsing error that occurs on some machines. some of the time!

	int _parseFileDelay = 0;       //CB added to fix a parsing error that occurs on some machines. some of the time!
}
