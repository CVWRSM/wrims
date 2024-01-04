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

package calsim.gui;
//import calsim.app.*;
import calsim.app.InputTableData;
import calsim.app.DefaultTableData;
import calsim.app.Wresler;
import vista.gui.VistaUtils;
//import vista.gui.TableHeaderAction;
import javax.swing.*;
import javax.swing.event.*;
//import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
/**
  * A display panel for InputTableData as its model
  *
  * @author Nicky Sandhu ,Armin Munevar
  * @version $Id: InputDataUI.java,v 1.1.2.3 2001/07/19 22:42:45 amunevar Exp $
  */
public class InputDataUI extends MPanel{
  private JTable _table;
  private JTextArea _textArea;
  private InputTableData _model;
  public DefaultCellEditor dce;
  /**
    *
    */
  public InputDataUI(InputTableData model){
    _table = new JTable();
    _textArea = new JTextArea(5,30);
    setModel(model);
    setLayout(new BorderLayout());
    add(new JScrollPane(_table),BorderLayout.CENTER);
    add(new JScrollPane(_textArea),BorderLayout.SOUTH);
  }
  /**
    *
    */
  public void resetModel(){
	InputTableData model = new DefaultTableData(_model.getTableName(),_model.getHeaders());
    _model = model;
    _table.setModel(model);
	  _table.sizeColumnsToFit(-1);
    _textArea.setText(model.getComment());
  }
	/**
	  *
	  */
  public void setModel(InputTableData model){
    _model = model;
    _table.setModel(model);
/*    if (AppUtils.resTable) {
			int numLevels = AppUtils._numLevels;
      JTextField[] text = new JTextField[15];
      String[] headers = model.getHeaders();
			_table.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent e) {
					checkResLevels();
		    }
				public void keyPressed(KeyEvent e) {}
				public void keyReleased(KeyEvent e) {}
			});
			for (int i = 0; i < headers.length; i++) {
 				text[i] = new JTextField();
        if (i > 1 && i < 12) {
					text[i].addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							checkResLevels();
						}
					});
				}
				_table.getColumn(headers[i]).setCellEditor(new DefaultCellEditor(text[i]));
			}
			AppUtils.resTable = false;
		}*/
    _textArea.setText(model.getComment());
  }

  public void checkResLevels() {
		int row = _table.getSelectedRow();
    int column = _table.getSelectedColumn();
    String slevel = (String)_model.getValueAt(row,1);
    if (slevel.equals("")) return;
    int ilevel = new Integer(slevel).intValue();
		String header = _table.getColumnName(column);
		String snum;
		if (header.startsWith("Level")) {
			if (header.length() == 7) snum = header.substring(5,7).trim(); else snum = header.substring(5,8).trim();
	  	int inum = new Integer(snum).intValue();
			if (inum > ilevel) {
				JOptionPane.showMessageDialog(_table,"Cannot edit cell unitl # of Levels for this reservoir needs to be increased",
    	                                 				"More levels needed",JOptionPane.ERROR_MESSAGE);
				_table.editingStopped(new ChangeEvent(_table));
    	  _model.setValueAt("",row,column);
			}
		}
/*		if (header.equals("# of Levels")) {
			for (int i=ilevel+1; i<12; i++) {
			  String value = (String)_model.getValueAt(row,i);
			  if (!value.equals("")) JOptionPane.showMessageDialog(_table,"More levels specified for reservoir than # of Levels allows",
    	                                 				null,JOptionPane.ERROR_MESSAGE);
			}
		}*/
	}
/*
  CalsimCellRenderer cr = new CalsimCellRenderer();

	private class CalsimCellRenderer extends JLabel implements TableCellRenderer {
		public CalsimCellRenderer() {
			setOpaque(false);
			setVisible(false);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
																											boolean hasFocus, int row, int column) {
			String svalue = (String)value;
			setText(svalue);
			String slevel = (String)_model.getValueAt(row,1);
			int level = new Integer(slevel).intValue();
		  System.out.println(row);
		  if(2+level <= 12){
			  for(int i=level+2;i<12;i++){
			    _model.setValueAt("",row,i);
				}
			}
			return this;
		}
	}
*/
  /**
    *
    */
  public InputTableData getModel(){
    return _model;
  }
  /**
    * returns a menu bar associated with this panels components
    */
  public JMenuBar getJMenuBar(){
    Action loadAction = new AbstractAction("Load..."){
      public void actionPerformed(ActionEvent evt){
				String fname = VistaUtils.getFilenameFromDialog(_table,FileDialog.LOAD,"csv","Comma Separated");
				if ( fname == null ) return;
				try {
					InputTableData model = new DefaultTableData(getModel().getTableName(),getModel().getHeaders());
					model.load(fname);
					setModel(model);
					_table.sizeColumnsToFit(-1);
				} catch (Exception e) {
      			JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
				}
      }
    };
    Action saveAction = new AbstractAction("Save"){
      public void actionPerformed(ActionEvent evt){
			 	saveTo(getModel().getInputFile());
      }
    };
    Action saveAsAction = new AbstractAction("Save As..."){
      public void actionPerformed(ActionEvent evt){
	    	saveTo(null);
      }
    };
    Action addRowAction = new AbstractAction("Add Row"){
      public void actionPerformed(ActionEvent evt){
				getModel().addRow(createDefaultRow());
				_table.tableChanged(new TableModelEvent(_table.getModel()));
      }
    };
    Action insertRowAction = new AbstractAction("Insert Row"){
      public void actionPerformed(ActionEvent evt){
	int [] indices = getSelectedRows();
	if ( indices == null || indices.length == 0 ) return;
	int numberDeleted = 0;
	for(int i=0; i < indices.length; i++) {
	  int currentIndex = indices[i] - numberDeleted;
	  if ( currentIndex >= getModel().getNumberOfRows() ) {
	    continue;
	  }
	  getModel().insertRow(currentIndex,createDefaultRow());
	  numberDeleted++;
	}
	_table.tableChanged(new TableModelEvent(_table.getModel()));
      }
    };
    Action deleteRowAction = new AbstractAction("Delete Row"){
      public void actionPerformed(ActionEvent evt){
	int [] indices = getSelectedRows();
	if ( indices == null || indices.length == 0 ) return;
	int numberDeleted = 0;
	for(int i=0; i < indices.length; i++) {
	  int currentIndex = indices[i] - numberDeleted;
	  if ( currentIndex >= getModel().getNumberOfRows() ) {
	    continue;
	  }
	  getModel().removeRow(currentIndex);
	  numberDeleted++;
	}
	_table.tableChanged(new TableModelEvent(_table.getModel()));
      }
    };
    // installs header listener action
//    ActionListener al = new TableHeaderAction(_table){
//      public void actionPerformed(ActionEvent evt){
//	int col = getColumn();
//	if ( col == -1 ) return;
//	MouseEvent me = getMouseEvent();
	// disabled for now
	//	getModel().sort(col);
	// if shift reverse sort...
	//if ( me.isShiftDown() ) getModel().reverse();
//      }
//    };
    //
    JMenu fmenu = new JMenu("File");
    fmenu.add(loadAction);
    fmenu.add(saveAction);
    fmenu.add(saveAsAction);
    //
    JMenu eMenu = new JMenu("Edit");
    eMenu.add(addRowAction);
    eMenu.add(insertRowAction);
    eMenu.add(deleteRowAction);
    //
    JMenuBar mbar = new JMenuBar();
    mbar.add(fmenu);
    mbar.add(eMenu);
    return mbar;
  }
  /**
    *
    */
  void saveTo(String file){
    if ( file == null ){
      String fname = VistaUtils.getFilenameFromDialog(_table,FileDialog.SAVE,"csv","Comma Separated");
      if ( fname == null ) return;
      file = fname;
    }
    // extract directory name
    File fx = new File(file);
    String dirName = fx.getParent();
    //
    getModel().setComment(_textArea.getText());
    getModel().save(file);

    boolean unequal = false;
    int numrows = _table.getRowCount();
    int row = 0;
    if (getFrameTitle().equals("Reservoir")) {
    	for (; row<numrows; row++) {
				String slevel = (String)_model.getValueAt(row,1);
				int ilevel = new Integer(slevel).intValue();
    	  for (int column=2; column<12; column++) {
    	    String value = (String)_model.getValueAt(row,column);
					if (column <= ilevel+1 && value.equals("")) {
						unequal = true;
						break;
					} else if (column > ilevel+1 && !value.equals("")) {
						unequal = true;
						break;
					}
				}
				if (unequal) break;
			}
		}
		if (unequal) {
			String node = (String)_model.getValueAt(row,0);
			JOptionPane.showMessageDialog(_table,"Unequal # of levels to Levels with values at node: " + node,
    	                                 				"Level Inequality",JOptionPane.ERROR_MESSAGE);
      return;
		}
		unequal = false;
    // generate the wresl file
		try {
			Wresler w = new Wresler(getModel());
			w.saveWresl(dirName);
		} catch (Exception e) {
    	JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		}
  }
  /**
   * returns the title of the frame containing this panel. This could
   * be used to identify this panel by name as well.
   */
  public String getFrameTitle(){
    return getModel().getTableName();
  }
  /**
    *
    */
  int [] getSelectedRows(){
    int [] ri = _table.getSelectedRows();
    if ( ri == null || ri.length == 0 ){
      JOptionPane.showMessageDialog(this,"Message",
				    "Select a row first!",
				    JOptionPane.PLAIN_MESSAGE);
      return null;
    }
    return ri;
  }
  /**
    *
    */
  String [] createDefaultRow(){
    String [] headers = getModel().getHeaders();
    for (int i=0; i < headers.length; i++) System.out.println(headers[i]);
    String [] rowData = new String[headers.length];
    for(int i=0; i< rowData.length; i++){
      rowData[i] = "";
    }
    return rowData;
  }
}
