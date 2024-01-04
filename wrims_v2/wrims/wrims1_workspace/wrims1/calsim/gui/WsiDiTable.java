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
import calsim.app.*;
import vista.gui.*;
//import java.io.*;
import java.awt.*;
//import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
/**
	* JTable for Wsi and Di input data
 * @author Armin Munevar
 * @version $Id: WsiDiTable.java,v 1.1.2.3 2001/07/12 02:00:03 amunevar Exp $
 */
public class WsiDiTable extends MPanel{
  public static boolean DEBUG = false;
	/**
		*
		*/
/*	public static void main( String args[] ) {
    JDialog jd = new JDialog();
		jd.setModal(true);
    jd.getContentPane().setLayout(new BorderLayout());
		WsiDiTable table = new WsiDiTable();
		TableModel model = table.getModel();
    jd.setJMenuBar(table.getJMenuBar());
    jd.setTitle(table.getFrameTitle());
    jd.getContentPane().add(table);
    jd.setSize(500,250);
    jd.show();
		System.out.println("all done");
  }*/
  /**
   *
   */
  public WsiDiTable() {
    setLayout(new BorderLayout());
		_model = new WsiDiTableModel();
    _table = new JTable(_model);
		_okButton = createOkButton();
		_cancelButton = createCancelButton();
		JPanel bpanel = new JPanel();
		bpanel.setLayout(new FlowLayout());
		bpanel.add(_okButton);
		bpanel.add(_cancelButton);
		add(new JScrollPane(_table),BorderLayout.CENTER);
		add(bpanel,BorderLayout.SOUTH);

    int uw = 100;
    _table.getColumnModel().getColumn(0).setPreferredWidth(2*uw);
    _table.getColumnModel().getColumn(1).setPreferredWidth(4*uw);
    _table.getColumnModel().getColumn(2).setPreferredWidth(4*uw);
    _table.getColumnModel().getColumn(3).setPreferredWidth(3*uw);
  }
  /**
   *
   */
  public String getFrameTitle(){
    return "Water Supply Index - Demand Index Setup";
  }
  /**
    *
    */
  public JMenuBar getJMenuBar(){
    if ( _mbar == null ) _mbar = createJMenuBar();
    return _mbar;
  }
  /**
   *
   */
  public TableModel getModel(){
    return _table.getModel();
  }
  /**
   *
   */
  public JMenuBar createJMenuBar(){
    JMenuItem deleteItem = new JMenuItem("Delete Row");
    deleteItem.addActionListener( new CursorChangeListener(){
      public void doWork(){
				delete();
      }
    });
    JMenuItem addRowItem = new JMenuItem("Add Row");
    addRowItem.addActionListener( new CursorChangeListener(){
      public void doWork(){
				add();
      }
    });
    JMenuItem insertItem = new JMenuItem("Insert Row");
    insertItem.addActionListener( new CursorChangeListener(){
      public void doWork(){
				insert();
      }
    });
    //
    JMenu editMenu = new JMenu("Edit");
    editMenu.add(addRowItem);
		editMenu.add(insertItem);
		editMenu.add(deleteItem);
    //
    _mbar = new JMenuBar();
    _mbar.add(editMenu);
    return _mbar;
  }
  /**
    *
    */
  public JButton createOkButton(){
		JButton b = new JButton("OK");
		b.addActionListener( new CursorChangeListener() {
			public void doWork() {
				finish();
			}
		});
    return b;
  }
  /**
    *
    */
  public JButton createCancelButton(){
		JButton b = new JButton("Cancel");
		b.addActionListener( new CursorChangeListener() {
			public void doWork() {
				quit();
			}
		});
    return b;
  }
  /**
   * delete rows
   */
  void delete(){
    if (DEBUG) System.out.println("Delete");
    // get user selected rows
    stopEditing();
    int [] ri = _table.getSelectedRows();
    if ( ri == null || ri.length == 0 ){
      JOptionPane.showMessageDialog(this,"Message",
				    "Select a row first!",
				    JOptionPane.PLAIN_MESSAGE);
      return;
    }
    int numberDeleted = 0;
    for(int i=0; i < ri.length; i++) {
      int currentIndex = ri[i] - numberDeleted;
			_model.removeRow(currentIndex);
			numberDeleted++;
    }
    _table.tableChanged(new TableModelEvent(_table.getModel()));
  }
  /**
   *
   */
  void add(){
    if (DEBUG) System.out.println("Add");
    stopEditing();
		_model.addRow(_model.getDefaultData());
    _table.tableChanged(new TableModelEvent(_table.getModel()));
  }
  /**
   *
   */
  void insert(){
    if (DEBUG) System.out.println("Insert");
    // get user selected row
    stopEditing();
    int ri = _table.getSelectedRow();
    if ( ri == -1 ){
      JOptionPane.showMessageDialog(this,"Message",
				    "Select a row first!",
				    JOptionPane.PLAIN_MESSAGE);
      return;
    }
		_model.insertRow(ri,_model.getDefaultData());
    _table.tableChanged(new TableModelEvent(_table.getModel()));
  }
  /**
   *
   */
  void finish(){
    if (DEBUG) System.out.println("Finish");
    stopEditing();
    _table.tableChanged(new TableModelEvent(_table.getModel()));
		JOptionPane.getFrameForComponent(this).dispose();
  }
  /**
   *
   */
  void quit(){
    if (DEBUG) System.out.println("Quit");
    stopEditing();
		_model.setQuitMode(true);
		JOptionPane.getFrameForComponent(this).dispose();
  }
  /**
   *
   */
  void stopEditing(){
    _table.editingStopped(new ChangeEvent(_table));
  }
  /**
   *
   */
  private JTable _table;
  private WsiDiTableModel _model;
  private JButton _okButton, _cancelButton;
  private JMenuBar _mbar;
}
