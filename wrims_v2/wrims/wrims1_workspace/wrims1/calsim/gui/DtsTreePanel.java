package calsim.gui;

import calsim.app.*;
import java.awt.*;
import java.awt.event.*;
//import java.io.*;
import javax.swing.*;
import javax.swing.tree.*;
//import javax.swing.table.TableColumnModel;
//import javax.swing.event.TableModelEvent;

/**
 * Panel that holds the DTS Tree on the Output side of the GUI
 *
 * @author Joel Fenolio
 * @author Clay Booher - one correction
 * @version $Id: DtsTreePanel.java,v 1.1.2.2 2001/07/12 01:59:36 amunevar Exp $
 */


public class DtsTreePanel extends JPanel {

  public DtsTreePanel() {
	_dtm = new DtsTreeModel(dumbyRoot,tags,null,this);
    _tree = new CalsimTree(_dtm);
    _dtm.setTree(_tree);
    dumbyRoot = null;
    //_fileholder = createFileHolder();
    setLayout(new BorderLayout());
    //add(_fileholder,BorderLayout.NORTH);
    holder.setLeftComponent(createTreeHolder());
    DerivedTimeSeries dts = new DerivedTimeSeries(" ");
    setDTS(dts);
    _table = new DTSTable(dts);
//CB    _fr = new DefaultInternalFrame(_table);
//CB    holder.setRightComponent(_fr);  //CB table is NOT visible with this original line of code (for Java 5 and 6)
    holder.setRightComponent(_table);  //CB THIS FIXED IT SO THE TABLE SHOWS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    holder.setDividerLocation(200);
    add(holder,BorderLayout.CENTER);
    setVisible(true);
    this.setBackground(Color.YELLOW);    //CB NO VISIBLE CHANGE
  }

  public JPanel createTreeHolder() {
	JPanel treeholder = new JPanel(new GridLayout(1,1));
	JScrollPane holder = new JScrollPane(_tree);
	treeholder.add(holder);
	return treeholder;
  }

  public static DtsTreeModel getCurrentModel() {
	return _dtm;
  }

  public void setDTSTable(DerivedTimeSeries dts, MultipleTimeSeries mts) {
	_table.setTableModel(dts,mts);
  }

  public void setDTS(DerivedTimeSeries dts) {
	_dts = dts;
  }

  public DerivedTimeSeries getDTS() {
	return _dts;
  }

  public DTSTable getTable() {
	return _table;
  }

  JSplitPane holder = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
  static DefaultMutableTreeNode dumbyRoot = new DefaultMutableTreeNode("Root");
  static String tags[] = {".dts",".DTS",".mts",".MTS"};
  //static String pics[] = {"d:\\Calsim1\\calsim\\gui\\sphere01.gif","d:\\Calsim1\\calsim\\gui\\sphere01.gif","d:\\Calsim1\\calsim\\gui\\smdi_or.gif","d:\\Calsim1\\calsim\\gui\\smdi_or.gif"};
  private static DtsTreeModel _dtm;
  CalsimTree _tree;
  DTSTable _table;
  DefaultInternalFrame _fr;
  DerivedTimeSeries _dts;
}





