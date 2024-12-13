/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.text.StyledDocument;
import vista.set.DataReference;
import vista.set.RegularTimeSeries;

/**
 * Frame for monthly tables
 *
 * @author Nicky Sandhu
 * @version $Id: MonthlyTableDisplay.java,v 1.1.2.15 2001/07/12 01:59:52 amunevar Exp $
 */
public class MonthlyTableDisplay extends TextDisplay {
    public static boolean DEBUG = false;
    public static String[] itemText = {
        "Table",
        "Graph"
    };
    public static String[] toolTipText = {
        "Shows data in a table",
        "Shows data in a graph"
    };
    public static int[] itemKeys = {
        KeyEvent.VK_T,
        KeyEvent.VK_G
    };

    /**
     *
     */
    public MonthlyTableDisplay(DataReference ref, boolean isWaterYear, boolean isStartMonth, String startMonth,
        int[] years) {
        super();
        setFrameTitle("MONTHLY REPORT");
        _ref = ref;
        _mr = new MonthlyReport((RegularTimeSeries) _ref.getData(),
            _ref.getPathname(),
            _ref.getFilename(),
            isWaterYear,
            isStartMonth,
            startMonth,
            years);
        addDocument(_mr.getStyledDocument());
    }

    /**
     *
     */
    public MonthlyTableDisplay(DataReference[] refs, boolean isWaterYear, boolean isStartMonth, String startMonth,
        int[] years) {
        super();
        setFrameTitle("MONTHLY REPORT");
        _refs = refs;
        StyledDocument doc = null;
        for (int i = 0; i < _refs.length; i++) {
            DataReference ref = _refs[i];
            if (ref == null) {
                continue;
            }
            MonthlyReport mr = new MonthlyReport((RegularTimeSeries) ref.getData(),
                ref.getPathname(),
                ref.getFilename(),
                isWaterYear,
                isStartMonth,
                startMonth,
                years);
            if (doc == null) {
                doc = mr.getStyledDocument();
            } else {
                doc = mr.appendTo(doc, 0);
            }
        }
        if (doc != null) {
            addDocument(doc);
        }
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
        int mindex = 0;
        JMenuItem tableItem = new JMenuItem(itemText[mindex]);
        tableItem.setToolTipText(toolTipText[mindex]);
        tableItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[mindex], KeyEvent.CTRL_MASK));
        tableItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                table();
            }
        });
        //
        mindex++;
        JMenuItem graphItem = new JMenuItem(itemText[mindex]);
        graphItem.setToolTipText(toolTipText[mindex]);
        graphItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[mindex], KeyEvent.CTRL_MASK));
        graphItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                graph();
            }
        });
        //
        //
        _mbar = super.createJMenuBar();
        JMenu fileMenu = _mbar.getMenu(0);
        fileMenu.insert(tableItem, fileMenu.getItemCount() - 2);
        fileMenu.insert(graphItem, fileMenu.getItemCount() - 2);
        return _mbar;
    }

    /**
     *
     */
    void table() {
        if (DEBUG) {
            System.out.println("Table");
        }
        JFrame fr = null;
        if (_ref != null) {
            fr = AppUtils.tabulate(_ref);
        } else {
            fr = AppUtils.tabulate(_refs);
        }
        if (fr != null) {
            fr.setVisible(true);
        }
    }

    /**
     *
     */
    void graph() {
        if (DEBUG) {
            System.out.println("Graph");
        }
        JFrame fr = null;
        if (_ref != null) {
            fr = AppUtils.plot(_ref);
        } else {
            fr = AppUtils.plot(_refs);
        }
        if (fr != null) {
            fr.setVisible(true);
        }
    }

    //  private StyledDocument _doc;
    private DataReference _ref;
    private DataReference[] _refs;
    private MonthlyReport _mr;
    private JMenuBar _mbar;
//  private JTextPane _jtp;
}
