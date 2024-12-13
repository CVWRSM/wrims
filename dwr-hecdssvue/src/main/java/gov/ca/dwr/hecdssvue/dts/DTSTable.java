/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import hec.io.DataContainer;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import vista.gui.CursorChangeListener;
import vista.gui.VistaUtils;

/**
 * Derived Timeseries table
 *
 * @author Nicky Sandhu
 * @version $Id: DTSTable.java,v 1.1.4.45 2001/10/23 16:28:35 jfenolio Exp $
 */
public class DTSTable extends MPanel {
    public JButton opencurrent;
    public static boolean DEBUG = false;
    public static String[] itemText = {"Print",
        "Load",
        "Save",
        "Delete Row",
        "Add Row",
        "Insert Row",
        "Quit",
        "Retrieve"

    };
    public static char[] itemChars = {'p', 'l', 's', 'd', 'a', 'i', 'q', 'r'
    };
    public static String[] toolTipText = {"Prints table",
        "Loads table from file",
        "Saves table to file",
        "Deletes selected row",
        "Adds row",
        "Inserts row at current selection",
        "Closes frame",
        "Retrieves and Calculates Data"
    };
    public static int[] itemKeys = {KeyEvent.VK_P,
        KeyEvent.VK_L,
        KeyEvent.VK_S,
        KeyEvent.VK_D,
        KeyEvent.VK_A,
        KeyEvent.VK_I,
        KeyEvent.VK_Q,
        KeyEvent.VK_R
    };

    /**
     *
     */
    public DTSTable(DerivedTimeSeries dts) {
        setLayout(new BorderLayout());
        _table = new JTable(new DTSTableModel(dts));
        _table.registerKeyboardAction(new CursorChangeListener() {
            public void doWork() {
                stopEditing();
            }
        }, null, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), JComponent.WHEN_IN_FOCUSED_WINDOW);
        int uw = 66;
        _table.getColumnModel().getColumn(0).setPreferredWidth(uw);
        _table.getColumnModel().getColumn(1).setPreferredWidth(5 * uw);
        _table.getColumnModel().getColumn(2).setPreferredWidth(uw);
        _table.getColumnModel().getColumn(3).setPreferredWidth(5 * uw);
        _table.getColumnModel().getColumn(4).setPreferredWidth(5 * uw);
        _table.sizeColumnsToFit(-1);  //CB TODO  use doLayout method instead
        _nameField = new JTextField(dts.getName(), 25);
        _nameField.setEditable(false);
        _nameField.addActionListener(new CursorChangeListener() {
            public void doWork() {
                changeNameToField();
            }
        });

        _table.setPreferredScrollableViewportSize(_table.getPreferredSize());  //CB added - but still CANNOT SEE TABLE

        JLabel nameLabel = new JLabel("Derived Time Series: ");
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BorderLayout());
        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(_nameField, BorderLayout.CENTER);
        namePanel.add(createButtonPanel(), BorderLayout.SOUTH);
        add(namePanel, BorderLayout.NORTH);
        add(new JScrollPane(_table), BorderLayout.CENTER);
        setDTS(dts, null);
    }

    public JPanel createButtonPanel() {
        JPanel holder = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addrow = new JButton("Add");
        JButton insertrow = new JButton("Insert");
        JButton deleterow = new JButton("Delete");
        opencurrent = new JButton("Open");
        addrow.addActionListener(new CursorChangeListener() {
            public void doWork() {
                add();
            }
        });
        insertrow.addActionListener(new CursorChangeListener() {
            public void doWork() {
                insert();
            }
        });
        deleterow.addActionListener(new CursorChangeListener() {
            public void doWork() {
                delete();
            }
        });
        holder.add(addrow);
        holder.add(insertrow);
        holder.add(deleterow);
        holder.add(opencurrent);
        return holder;
    }

    /**
     *
     */
    public String getFrameTitle() {
        return "Derived Time Series";
    }

    /**
     *
     */
    public JMenuBar getJMenuBar() {
        if (_mbar == null) {
            _mbar = createJMenuBar();
        }
        return _mbar;
    }

    /**
     *
     */
    public JMenuBar createJMenuBar() {
        JMenuItem deleteItem = new JMenuItem(itemText[3]);
        deleteItem.setToolTipText(toolTipText[3]);
        deleteItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[3], KeyEvent.CTRL_MASK));
        deleteItem.setMnemonic(itemChars[3]);
        deleteItem.addActionListener(new CursorChangeListener() {
            public void doWork() {
                delete();
            }
        });
        JMenuItem addRowItem = new JMenuItem(itemText[4]);
        addRowItem.setToolTipText(toolTipText[4]);
        addRowItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[4], KeyEvent.CTRL_MASK));
        addRowItem.setMnemonic(itemChars[4]);
        addRowItem.addActionListener(new CursorChangeListener() {
            public void doWork() {
                add();
            }
        });
        //
        JMenuItem insertItem = new JMenuItem(itemText[5]);
        insertItem.setToolTipText(toolTipText[5]);
        insertItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[5], KeyEvent.CTRL_MASK));
        insertItem.setMnemonic(itemChars[5]);
        insertItem.addActionListener(new CursorChangeListener() {
            public void doWork() {
                insert();
            }
        });
        //
        int index = 7;
        JMenuItem retrieveItem = new JMenuItem(itemText[index]);
        retrieveItem.setToolTipText(toolTipText[index]);
        retrieveItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[index], KeyEvent.CTRL_MASK));
        retrieveItem.setMnemonic(itemChars[index]);
        retrieveItem.addActionListener(new CursorChangeListener() {
            public void doWork() {
                retrieve();
            }
        });
        //
        JMenu editMenu = new JMenu("Edit");
        editMenu.add(addRowItem);
        editMenu.add(insertItem);
        editMenu.add(deleteItem);
        editMenu.setMnemonic('e');
        //
        _mbar = new JMenuBar();
        return _mbar;
    }

    /**
     * sets the DTS displayed in the table
     */
    public void setDTS(DerivedTimeSeries dts, MultipleTimeSeries mts) {
        if (dts != null) {
            dtm = new DTSTableModel(dts);
            mtm = null;
            _table.setModel(dtm);
        } else {
            mtm = new MTSTableModel(mts);
            dtm = null;
            _table.setModel(new MTSTableModel(mts));
        }
        _table.tableChanged(new TableModelEvent(_table.getModel()));
        if (dts != null) {
            _nameField.setText(dts.getName());
        } else {
            _nameField.setText(mts.getName());
        }
        _dts = dts;
        _mts = mts;
        //  set operation editor
        JComboBox opEditor = new JComboBox();
        if (dts != null) {
            opEditor.addItem("+");
            opEditor.addItem("-");
            opEditor.addItem("*");
            opEditor.addItem("/");
        } else {
            opEditor.setEnabled(false);
        }
        // register tab as editing stopped
        _table.registerKeyboardAction(new AbstractAction("editingStopped") {
            public void actionPerformed(ActionEvent evt) {
                stopEditing();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), JComponent.WHEN_FOCUSED);

        JComboBox dtsEditor = new JComboBox();
        dtsArray = null;
        dtsArray = AppUtils.getCurrentProject().getDTSNames();
        if (dtsArray != null) {
            if (AppUtils.getCurrentProject().getDTSMod()) {
                dtsArray = GuiUtils.sortArray(dtsArray);
                dtsEditor = new JComboBox(dtsArray);
                _dtsEditor = dtsEditor;
            }
        } else {
            dtsEditor = new JComboBox();
            _dtsEditor = dtsEditor;
        }
        AppUtils.getCurrentProject().setDTSMod(false);
        //make var type editor
        varEditor.addItem(AppUtils.SVAR);
        varEditor.addItem(AppUtils.DVAR);
        _bpartEditor.setEditable(true);
        cpartEditor.setEditable(true);
        _bpartEditor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = _table.getSelectedRow();
                if (!start) {
                    findSetCPart(dtm, mtm, row);
                }
            }
        });
        start = false;
        varEditor.removeAllItems();
        varEditor.addItem(AppUtils.SVAR);
        varEditor.addItem(AppUtils.DVAR);
        varEditor.setSelectedIndex(1);

        //

        if (dts != null) {
            _table.getColumn("Derived Time Series").setCellEditor(new DefaultCellEditor(_dtsEditor));
            _table.getColumn("Operator").setCellEditor(new DefaultCellEditor(opEditor));
            _table.getColumn("Dvar/Svar").setCellEditor(new DefaultCellEditor(varEditor));
            _table.getColumn("B part").setCellEditor(new DefaultCellEditor(_bpartEditor));
            _table.getColumn("C part").setCellEditor(new DefaultCellEditor(cpartEditor));
        } else {
            _table.getColumn("Derived Time Series").setCellEditor(new DefaultCellEditor(_dtsEditor));
            _table.getColumn("Dvar/Svar").setCellEditor(new DefaultCellEditor(varEditor));
            _table.getColumn("B part").setCellEditor(new DefaultCellEditor(_bpartEditor));
            _table.getColumn("C part").setCellEditor(new DefaultCellEditor(cpartEditor));
        }
        _table.selectAll();
    }

    /**
     * gets the cpart associated with the chosen bpart in a dts or mts table
     */
    public void findSetCPart(DTSTableModel dtm, MTSTableModel mtm, int r) {
        String b = _bpartEditor.getText().toUpperCase();
        String var = (String) varEditor.getSelectedItem();
        _prj = AppUtils.getCurrentProject();
        Hashtable _svList = _prj.getSVHashtable();
        Hashtable _dvList = _prj.getDVHashtable();
        if (_svList == null || _dvList == null) {
            return;
        }
        if (var.equals(AppUtils.SVAR)) {
            String c = (String) _svList.get(b.toUpperCase());
            if (dtm != null) {
                dtm.setValueAt(c, r, 4);
            } else {
                mtm.setValueAt(c, r, 3);
            }
        } else if (var.equals(AppUtils.DVAR)) {
            String c = (String) _dvList.get(b.toUpperCase());
            if (dtm != null) {
                dtm.setValueAt(c, r, 4);
            } else {
                mtm.setValueAt(c, r, 3);
            }
        }
    }

    /**
     * delete rows
     */
    void delete() {
        if (_dts != null) {
            if (DEBUG) {
                System.out.println("Delete");
            }
            // get user selected rows
            stopEditing();
            int[] ri = _table.getSelectedRows();
            if (ri == null || ri.length == 0) {
                JOptionPane.showMessageDialog(this, "Message",
                    "Select a row first!",
                    JOptionPane.PLAIN_MESSAGE);
                return;
            }
            int numberDeleted = 0;
            for (int i = 0; i < ri.length; i++) {
                int currentIndex = ri[i] - numberDeleted;
                if (currentIndex >= _dts.getNumberOfDataReferences()) {
                    continue;
                }
                _dts.remove(currentIndex);
                numberDeleted++;
            }
            _table.tableChanged(new TableModelEvent(_table.getModel()));
        } else {
            if (DEBUG) {
                System.out.println("Delete");
            }
            stopEditing();
            // get user selected rows
            int[] ri = _table.getSelectedRows();
            if (ri == null || ri.length == 0) {
                JOptionPane.showMessageDialog(this, "Message",
                    "Select a row first!",
                    JOptionPane.PLAIN_MESSAGE);
                return;
            }
            int numberDeleted = 0;
            for (int i = 0; i < ri.length; i++) {
                int currentIndex = ri[i] - numberDeleted;
                if (currentIndex >= _mts.getNumberOfDataReferences()) {
                    continue;
                }
                _mts.remove(currentIndex);
                numberDeleted++;
            }
            _table.tableChanged(new TableModelEvent(_table.getModel()));
        }
    }

    /**
     *
     */
    void add() {
        if (_dts != null) {
            if (DEBUG) {
                System.out.println("Add");
            }
            stopEditing();
            int i = _dts.getNumberOfDataReferences();
            _dts.setVarTypeAt(i, AppUtils.DVAR);
            _table.tableChanged(new TableModelEvent(_table.getModel()));
        } else {
            if (DEBUG) {
                System.out.println("Add");
            }
            stopEditing();
            int index = _mts.getNumberOfDataReferences();
            _mts.setVarTypeAt(index, AppUtils.DVAR);
            _table.tableChanged(new TableModelEvent(_table.getModel()));
        }
    }

    /**
     *
     */
    void insert() {
        if (_dts != null) {
            if (DEBUG) {
                System.out.println("Insert");
            }
            // get user selected row
            stopEditing();
            int ri = _table.getSelectedRow();
            if (ri == -1) {
                JOptionPane.showMessageDialog(this, "Message",
                    "Select a row first!",
                    JOptionPane.PLAIN_MESSAGE);
                return;
            }
            _dts.insertAt(ri);
            _table.tableChanged(new TableModelEvent(_table.getModel()));
        } else {
            if (DEBUG) {
                System.out.println("Insert");
            }
            // get user selected row
            stopEditing();
            int ri = _table.getSelectedRow();
            if (ri == -1) {
                JOptionPane.showMessageDialog(this, "Message",
                    "Select a few rows first!",
                    JOptionPane.PLAIN_MESSAGE);
                return;
            }
            _mts.insertAt(ri);
            _mts.setVarTypeAt(ri, AppUtils.DVAR);
            _table.tableChanged(new TableModelEvent(_table.getModel()));
        }
    }

    /**
     *
     */
    public void retrieve() {
        stopEditing();
        try {
            if (_dts != null) {
                System.out.println(_dts.getName());
                GuiUtils.displayDTS(_dts);
            } else if (_mts != null) {
                GuiUtils.displayMTS(_mts);
            }
        } catch (Exception e) {
            AppUtils.showDssFileErrorDialog(e);
        }
    }

    /**
     *
     */
    public Vector<DataContainer> retrieveData() {
        stopEditing();
        try {
            if (_dts != null) {
                System.out.println(_dts.getName());
                return AppUtils.retrieveDTSData(_dts);
            } else if (_mts != null) {
                return AppUtils.retrieveMTSData(_mts);
            }
        } catch (Exception e) {
            AppUtils.showDssFileErrorDialog(e);
        }
        return new Vector<DataContainer>();
    }

    /**
     *
     */
    void changeNameToField() {
        String name = _nameField.getText();
        if (DEBUG) {
            System.out.println("Name: " + name);
        }
        try {
            if (name == null || name.equals("")) {
                return;
            }
            if (_dts != null) {
                _dts.setName(_nameField.getText());
            }
        } catch (Exception e) {
            AppUtils.showDssFileErrorDialog(e);
        }
    }

    /**
     *
     */
    public void stopEditing() {
        changeNameToField();
        _table.editingStopped(new ChangeEvent(_table));
    }

    public void setTableModel(DerivedTimeSeries dts, MultipleTimeSeries mts) {
        setDTS(dts, mts);
        if (dts != null) {
            _nameField.setText(dts.getName());
        } else {
            _nameField.setText(mts.getName());
        }
        _dts = dts;
        _mts = mts;
    }

    private final JTable _table;
    private final JTextField _nameField;
    private DerivedTimeSeries _dts;
    private MultipleTimeSeries _mts;
    private JMenuBar _mbar;
    Project _prj = AppUtils.getCurrentProject();
    String[] dtsArray;
    JComboBox _dtsEditor = new JComboBox();
    JTextField _bpartEditor = new JTextField();
    JTextField cpartEditor = new JTextField();
    JComboBox varEditor = new JComboBox();
    private boolean start = true;
    DTSTableModel dtm;
    MTSTableModel mtm;
}
