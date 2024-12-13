/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import vista.gui.CursorChangeListener;
import vista.gui.VistaUtils;

/**
 * Multiple Timeseries table
 *
 * @author Nicky Sandhu
 * @version $Id: MTSTable.java,v 1.1.4.32 2001/07/12 01:59:44 amunevar Exp $
 */
public class MTSTable extends MPanel {
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
    public static String[] toolTipText = {"Prints table",
        "Loads table from file",
        "Saves table to file",
        "Deletes selected row",
        "Adds row",
        "Inserts row at current selection",
        "Closes frame",
        "Retrieves and displays data"
    };
    public static char[] itemChars = {'p', 'l', 's', 'd', 'a', 'i', 'q', 'r'
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
    public MTSTable(MultipleTimeSeries mts) {
        setLayout(new BorderLayout());
        _mts = mts;
        _modified = false;
        _table = new JTable(new MTSTableModel(mts));
        _table.registerKeyboardAction(new CursorChangeListener() {
                                          public void doWork() {
                                              stopEditing();
                                          }
                                      },
            null,
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
            WHEN_IN_FOCUSED_WINDOW);
        int uw = 100;
        _table.getColumnModel().getColumn(0).setPreferredWidth(4 * uw);
        _table.getColumnModel().getColumn(1).setPreferredWidth(uw);
        _table.getColumnModel().getColumn(2).setPreferredWidth(2 * uw);
        _table.getColumnModel().getColumn(3).setPreferredWidth(2 * uw);
        _nameField = new JTextField(mts.getName(), 25);
        _nameField.addActionListener(new CursorChangeListener() {
            public void doWork() {
                changeNameToField();
            }
        });
        JLabel nameLabel = new JLabel("Multiple Time Series: ");
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BorderLayout());
        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(_nameField, BorderLayout.CENTER);
        add(namePanel, BorderLayout.NORTH);
        add(new JScrollPane(_table), BorderLayout.CENTER);
        setMTS(mts);
    }

    /**
     *
     */
    public String getFrameTitle() {
        return "Multiple Time Series";
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
        JMenuItem printItem = new JMenuItem(itemText[0]);
        printItem.setToolTipText(toolTipText[0]);
        printItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[0], KeyEvent.CTRL_MASK));
        printItem.setMnemonic(itemChars[0]);
        printItem.addActionListener(new CursorChangeListener() {
            public void doWork() {
                print();
            }
        });
        //
        JMenuItem loadItem = new JMenuItem(itemText[1]);
        loadItem.setToolTipText(toolTipText[1]);
        loadItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[1], KeyEvent.CTRL_MASK));
        loadItem.setMnemonic(itemChars[1]);
        loadItem.addActionListener(new CursorChangeListener() {
            public void doWork() {
                load();
            }
        });
        //
        JMenuItem saveItem = new JMenuItem(itemText[2]);
        saveItem.setToolTipText(toolTipText[2]);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[2], KeyEvent.CTRL_MASK));
        saveItem.setMnemonic(itemChars[2]);
        saveItem.addActionListener(new CursorChangeListener() {
            public void doWork() {
                save();
            }
        });
        //
        JMenuItem exportItem = new JMenuItem("Export");
        exportItem.setToolTipText("Exports data to dss");
        exportItem.addActionListener(new CursorChangeListener() {
            public void doWork() {
                export();
            }
        });
        //
        JMenuItem quitItem = new JMenuItem(itemText[6]);
        quitItem.setToolTipText(toolTipText[6]);
        quitItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[6], KeyEvent.CTRL_MASK));
        quitItem.setMnemonic(itemChars[6]);
        quitItem.addActionListener(new CursorChangeListener() {
            public void doWork() {
                quit();
            }
        });
        //
        JMenuItem deleteItem = new JMenuItem(itemText[3]);
        deleteItem.setToolTipText(toolTipText[3]);
        deleteItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[3], KeyEvent.CTRL_MASK));
        deleteItem.setMnemonic(itemChars[3]);
        deleteItem.addActionListener(new CursorChangeListener() {
            public void doWork() {
                delete();
            }
        });
        //
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
        JMenu tableMenu = new JMenu("Table");
        tableMenu.add(loadItem);
        tableMenu.add(saveItem);
        tableMenu.addSeparator();
        tableMenu.add(exportItem);
        tableMenu.addSeparator();
        tableMenu.add(printItem);
        tableMenu.addSeparator();
        tableMenu.add(retrieveItem);
        tableMenu.addSeparator();
        tableMenu.add(quitItem);
        tableMenu.setMnemonic('t');
        //
        JMenu editMenu = new JMenu("Edit");
        editMenu.add(addRowItem);
        editMenu.add(insertItem);
        editMenu.add(deleteItem);
        editMenu.setMnemonic('e');
        //
        _mbar = new JMenuBar();
        _mbar.add(tableMenu);
        _mbar.add(editMenu);
        return _mbar;
    }

    /**
     *
     */
    void print() {
        if (DEBUG) {
            System.out.println("Print");
        }
        stopEditing();
        GuiUtils.print(this);
    }

    /**
     * sets the MTS displayed in the table
     */
    public void setMTS(MultipleTimeSeries mts) {
        _table.setModel(new MTSTableModel(mts));
        _table.tableChanged(new TableModelEvent(_table.getModel()));
        // register tab as editing stopped
        _table.registerKeyboardAction(new AbstractAction("editingStopped") {
            public void actionPerformed(ActionEvent evt) {
                stopEditing();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), WHEN_FOCUSED);
        //
        _nameField.setText(mts.getName());
        _mts = mts;
        _modified = false;
        // set derived time series editor, this should really
        // be a model onto this info as this could be changing
        // without the table getting recreated ??
        JComboBox dtsEditor = new JComboBox();
        DerivedTimeSeries[] dtsArray = AppUtils.getGlobalDTSList();
        Project prj = AppUtils.getCurrentProject();
        DerivedTimeSeries[] dtsArray1 = prj.getDTSList();
        DerivedTimeSeries[] dtsArray2;
        if (dtsArray1 != null) {
            dtsArray2 = new DerivedTimeSeries[dtsArray.length + dtsArray1.length];
            System.arraycopy(dtsArray, 0, dtsArray2, 0, dtsArray.length);
            System.arraycopy(dtsArray1, 0, dtsArray2, dtsArray.length, dtsArray1.length);
        } else {
            dtsArray2 = dtsArray;
        }
        //
        Arrays.sort(dtsArray2, new DTSComparator());
        dtsEditor.addItem("        ");
        for (int i = 0; i < dtsArray2.length; i++) {
            if (DEBUG) {
                System.out.println("Global: " + dtsArray[i].getName());
            }
            dtsEditor.addItem(dtsArray2[i].getName());
        }
        // make var type editor
        JComboBox varEditor = new JComboBox();
        varEditor.addItem(AppUtils.SVAR);
        varEditor.addItem(AppUtils.DVAR);
        //
        String[] bPartArray = {""}, cPartArray = {""};
        if (AppUtils.getCurrentBParts() != null) {
            bPartArray = AppUtils.getCurrentBParts();
        }
        if (AppUtils.getCurrentCParts() != null) {
            cPartArray = AppUtils.getCurrentCParts();
        }
        _firstBPart = bPartArray[0];
        _firstCPart = cPartArray[0];
        JComboBox bpartEditor = new JComboBox(bPartArray);
        bpartEditor.setEditable(true);
        JComboBox cpartEditor = new JComboBox(cPartArray);
        cpartEditor.setEditable(true);
        //
        _table.getColumn("Derived Time Series").setCellEditor(new DefaultCellEditor(dtsEditor));
        _table.getColumn("Dvar/Svar").setCellEditor(new DefaultCellEditor(varEditor));
        _table.getColumn("B part").setCellEditor(new DefaultCellEditor(bpartEditor));
        _table.getColumn("C part").setCellEditor(new DefaultCellEditor(cpartEditor));
        //
    }

    /**
     *
     */
    void export() {
        stopEditing();
        String dssfile = VistaUtils.getFilenameFromDialog(this, FileDialog.SAVE,
            "dss", "DSS File");
        if (dssfile == null) {
            return;
        }
        AppUtils.exportToDSS(_mts, dssfile);
    }

    /**
     *
     */
    void load() {
        if (DEBUG) {
            System.out.println("Load");
        }
        stopEditing();
        try {
            String mtsfile = VistaUtils.getFilenameFromDialog(this, FileDialog.LOAD,
                "mts", "MTS File");
            if (mtsfile == null) {
                return;
            }
            MultipleTimeSeries mts = MultipleTimeSeries.load(mtsfile);
            if (_modified) {
                int opt = JOptionPane.showConfirmDialog
                    (this,
                        "Current table has been modified! Do you want to save to file?",
                        "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (opt == JOptionPane.YES_OPTION) {
                    save();
                    if (!_modified) {
                        setMTS(mts);
                    }
                } else {
                    setMTS(mts);
                }
            }
        } catch (Exception e) {
            AppUtils.showDssFileErrorDialog(e);
        }
    }

    /**
     *
     */
    void save() {
        stopEditing();
        try {
            String mtsfile = VistaUtils.getFilenameFromDialog(this, FileDialog.SAVE,
                "mts", "MTS File");
            if (mtsfile == null) {
                return;
            }
            if (mtsfile.indexOf('.') == -1)  //no extension
            {
                mtsfile += ".dts";  //set default extension
            }
            _mts.save(new FileOutputStream(mtsfile));
            _modified = false;
        } catch (Exception e) {
            AppUtils.showDssFileErrorDialog(e);
        }
    }

    /**
     * delete row
     */
    void delete() {
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
        _modified = true;
        _table.tableChanged(new TableModelEvent(_table.getModel()));
    }

    /**
     *
     */
    void add() {
        if (DEBUG) {
            System.out.println("Add");
        }
        stopEditing();
        int index = _mts.getNumberOfDataReferences();
        _mts.setVarTypeAt(index, AppUtils.DVAR);
        _mts.setBPartAt(index, _firstBPart);
        _mts.setCPartAt(index, _firstCPart);
        _modified = true;
        _table.tableChanged(new TableModelEvent(_table.getModel()));
    }

    /**
     *
     */
    void insert() {
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
        _modified = true;
        _table.tableChanged(new TableModelEvent(_table.getModel()));
    }

    /**
     *
     */
    void retrieve() {
        stopEditing();
        try {
            GuiUtils.displayMTS(_mts);
        } catch (Exception e) {
            AppUtils.showDssFileErrorDialog(e);
        }
    }

    /**
     *
     */
    void quit() {
        if (DEBUG) {
            System.out.println("Quit");
        }
        stopEditing();
        if (_modified) {
            int opt = JOptionPane.showConfirmDialog
                (this,
                    "Current table has been modified! Do you want to save to file?",
                    "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                save();
                if (!_modified) {
                    JOptionPane.getFrameForComponent(this).dispose();
                }
            } else {
                JOptionPane.getFrameForComponent(this).dispose();
            }
        } else {
            JOptionPane.getFrameForComponent(this).dispose();
        }
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
            if (_mts != null) {
                _mts.setName(_nameField.getText());
            }
            _modified = true;
        } catch (Exception e) {
            AppUtils.showDssFileErrorDialog(e);
        }
    }

    /**
     *
     */
    void stopEditing() {
        changeNameToField();
        _modified = true;
        _table.editingStopped(new ChangeEvent(_table));
    }

    /**
     *
     */
    private final JTable _table;
    private final JTextField _nameField;
    private MultipleTimeSeries _mts;
    private boolean _modified;
    private String _firstBPart, _firstCPart;
    private JMenuBar _mbar;
}
