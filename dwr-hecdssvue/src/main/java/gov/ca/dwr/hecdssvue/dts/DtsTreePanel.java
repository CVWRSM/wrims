/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Panel that holds the DTS Tree on the Output side of the GUI
 *
 * @author Joel Fenolio
 * @author Clay Booher - one correction
 * @version $Id: DtsTreePanel.java,v 1.1.2.2 2001/07/12 01:59:36 amunevar Exp $
 */


public class DtsTreePanel extends JPanel {

    public DtsTreePanel() {
        _dtm = new DtsTreeModel(dumbyRoot, tags, null, this);
        _tree = new CalsimTree(_dtm);
        _dtm.setTree(_tree);
        dumbyRoot = null;
        setLayout(new BorderLayout());
        holder.setLeftComponent(createTreeHolder());
        DerivedTimeSeries dts = new DerivedTimeSeries(" ");
        _table = new DTSTable(dts);
        holder.setRightComponent(
            _table);  //CB THIS FIXED IT SO THE TABLE SHOWS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        holder.setDividerLocation(200);
        add(holder, BorderLayout.CENTER);
        setVisible(true);
        this.setBackground(Color.YELLOW);    //CB NO VISIBLE CHANGE
    }

    public JPanel createTreeHolder() {
        JPanel treeholder = new JPanel(new GridLayout(1, 1));
        JScrollPane holder = new JScrollPane(_tree);
        treeholder.add(holder);
        return treeholder;
    }

    public static DtsTreeModel getCurrentModel() {
        return _dtm;
    }

    public void setDTSTable(DerivedTimeSeries dts, MultipleTimeSeries mts) {
        _table.setTableModel(dts, mts);
    }

    public DTSTable getTable() {
        return _table;
    }

    JSplitPane holder = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    static DefaultMutableTreeNode dumbyRoot = new DefaultMutableTreeNode("Root");
    static String[] tags = {".dts", ".DTS", ".mts", ".MTS"};
    private static DtsTreeModel _dtm;
    CalsimTree _tree;
    DTSTable _table;
}





