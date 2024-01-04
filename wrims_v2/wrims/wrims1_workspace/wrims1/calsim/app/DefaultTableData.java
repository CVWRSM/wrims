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

package calsim.app;
import java.io.*;
//import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
//import java.awt.*;
import com.sun.java.util.collections.Comparator;
import com.sun.java.util.collections.Collections;
import com.sun.java.util.collections.Vector;
//import com.sun.java.util.collections.List;
import java.util.Enumeration;
/**
  * A default implementation common to all table data in csv format
  *
  * @author Nicky Sandhu ,Armin Munevar
  * @version $Id: DefaultTableData.java,v 1.1.2.10 2001/10/23 16:28:20 jfenolio Exp $
  */
public class DefaultTableData extends AbstractTableModel implements InputTableData{
  private String _inputFile;
  private String _name;
  private String [] _headers;
  private Vector _rowData;
  private String _comment;
  private boolean _needsSaving;
  public static int _numLevels = 5;

  /**
    *
    */
  public DefaultTableData(String name, String [] headers){
    _inputFile=null;
    _name = name;
    _headers = headers;
    _rowData = new Vector();
    _comment = "";
    _needsSaving = true;
  }
  /**
    *
    */
  public DefaultTableData(String file){
    _name = "";
    _headers = null;
    _rowData = new Vector();
    _comment = "";
    load(file);
    _needsSaving = false;
    addTableModelListener(tml);
  }

  TableModelListener tml = new TableModelListener() {
		public void tableChanged(TableModelEvent e) {
    	//System.out.println(1);
		}
	};
  /**
    *
    */
  private boolean isEmpty(String [] fields){
    if ( fields == null ) return true;
    for(int i=0; i < fields.length; i++){
      if ( ! fields[i].equals("") ) {
	       return false;
      }
    }
    return true;
  }
  /**
    *
    */
  public void loadReservoirTable(CSVParser p, String[] headers) {
		boolean old = false;
		if (!headers[1].equalsIgnoreCase("# of Levels")) old =true;
		int levels = 10;
		String[] fields = new String[10];
    while (true) {
			fields = p.nextLine();
			if (isEmpty(fields)) break;
			String[] oldfields = new String[fields.length+1];
			if (old) {
				oldfields[0] = fields[0];
				for (int i = 1; i < fields.length + 1; i++) {
					if ( i == 1) oldfields[1] = "5"; else oldfields[i] = fields[i-1];
				}
				fields = oldfields;
			} /*else {

				int levels2 = new Integer(fields[1]).intValue();
				if (levels2 > levels) levels = levels2;
			}*/
      _rowData.addElement(fields);
		}
		if (levels > 5) {
      _rowData = resizeReservoirTable(levels,_rowData,headers);
		}
	}
	/**
	  *
	  */
	public Vector resizeReservoirTable(int levels, Vector data, String[] headers) {
		String[] fields = new String[10];
    /*
		int size = headers.length + levels - 5;
		String[] headers2 = new String[size];
		headers2[0] = headers[0];
		headers2[1] = headers[1];
		int i = 1;
		for ( ; i <= levels; i++) {
			headers2[i+1] = "Level " + new Integer(i).toString();
		}
    headers2[i+1] = "Units";
    headers2[i+2] = "Discharge Arc";
    headers2[i+3] = "Description";

		_headers = headers;
		*/
		Vector rowData = new Vector(_rowData.size(),1);
    for (int g = 0; g < _rowData.size(); g++) {
			fields = (String[])_rowData.get(g);
      if ( fields.length != _headers.length ){ // discard or add empty as necessary
	      String [] efs = new String[_headers.length];
	      int i1=0;
	      for(; i1 < fields.length - 3; i1++){
	        efs[i1] = fields[i1];
          	      }
	      for(; i1 < _headers.length - 3; i1++){
	        efs[i1] = "";
	      }
	      efs[i1] = fields[fields.length-3];
	      efs[i1+1] = fields[fields.length-2];
	      efs[i1+2] = fields[fields.length-1];
	      fields = efs;
			}
			rowData.addElement(fields);
    }

    _rowData.clear();
    AppUtils.resTable = true;
    AppUtils._numLevels = _headers.length-5;
    return rowData;
	}
  /**
    *
    */
  public void load(String file){
		String[] fheaders;
		String name;
    CSVParser parser = new CSVParser(file);
    // read name
    name = parser.nextLine()[0];
		if (!name.equalsIgnoreCase(_name) ) {
			parser.close();
		  throw new RuntimeException("Wrong Name! First line must be: " +_name);
		}
    //_name = parser.nextLine()[0];
    // skip 3 lines:
    for(int i=0; i < 3; i++) parser.nextLine();
    // read headers
    fheaders = parser.nextLine();
		if (_name.equalsIgnoreCase("Reservoir")) {
			loadReservoirTable(parser,fheaders);
			return;
		}
		for (int i=0;i<fheaders.length;i++) {
    	if (!fheaders[i].equalsIgnoreCase(_headers[i]) ) {
				parser.close();
			  throw new RuntimeException("Wrong Header! File has: "+ fheaders[i]+"   Should have: "+ _headers[i]);
			}
		}
    // read data till empty line
    while (true){
      String [] fields = parser.nextLine();
      if (isEmpty(fields)) break;
      if ( fields.length != _headers.length ){ // discard or add empty as necessary
	      String [] efs = new String[_headers.length];
	      int i=0;
	      for(; i < Math.min(efs.length, fields.length); i++){
	        efs[i] = fields[i];
	      }
	      for(; i < efs.length; i++){
	        efs[i] = "";
	      }
	      fields= efs;
      }
      _rowData.addElement(fields);
    }
    // read comments till empty line
    StringBuffer commentBuffer = null;
    while (true){
      String [] comments = parser.nextLine();
      if (isEmpty(comments)) break;
      for(int i=0; i< comments.length; i++){
	      if ( commentBuffer == null ) commentBuffer = new StringBuffer(200);
	      commentBuffer.append(comments[i]);
	      if ( i != comments.length-1 ) commentBuffer.append(",");
      }
      commentBuffer.append(System.getProperty("line.separator"));
    }
    if ( commentBuffer != null )
      _comment = commentBuffer.toString();
    else
      _comment = "";
     _inputFile = file;
  }
  /**
    *
    */
  public void save(String file){
    try {
      PrintWriter writer = new PrintWriter(new FileWriter(file));
      // add table name and few lines
      writer.println(_name);
      for(int i=0; i< 3; i++)
 				writer.println(_name);
      // write out header
      for(int i=0; i < _headers.length; i++){
				writer.print(_headers[i]);
				if( i != _headers.length-1 )
	  			writer.print(",");
				else
	  			writer.println();
      }
      // write data
      for(Enumeration e = _rowData.elements(); e.hasMoreElements(); ){
				String [] fields = (String[]) e.nextElement();
				for(int i=0; i < fields.length; i++){
	  			writer.print(fields[i]);
	  			if( i != fields.length-1 )
	    			writer.print(",");
	  			else
	    		writer.println();
				}
      }
      // separating line data to comment section
      writer.println();
      // write comments
      writer.println(_comment);
      // close output
      writer.close();
      _inputFile = file;
      _needsSaving = false;
    }catch(IOException ioe){
      throw new RuntimeException("IOError saving file: " + file + " Error Msg: " + ioe.getMessage());
    }
  }
  /**
    * the name of this table
    */
  public String getTableName(){
    return _name;
  }
  /**
    * the header array for column names
    */
  public String [] getHeaders(){
    return _headers;
  }
  /**
    * the number of rows of data
    */
  public int getNumberOfRows(){
    return _rowData.size();
  }
  /**
    * the row data, the length of which must match the # of cols
    */
  public String [] getRowData(int i){
    return (String []) _rowData.elementAt(i);
  }
  /**
    * adds a row to this table
    */
  public void addRow(String [] rowData){
    _rowData.addElement(rowData);
    _needsSaving = true;
  }
  /**
    * inserts a row in this table
    */
  public void insertRow(int i, String [] rowData){
    _rowData.insertElementAt(rowData,i);
  }
  /**
    * removes a row from this table
    */
  public void removeRow(int i){
    _rowData.removeElementAt(i);
    _needsSaving = true;
  }
  /**
    * gets comment string
    */
  public String getComment(){
    return _comment;
  }
  /**
    * sets comment string
    */
  public void setComment(String comment){
    _comment = comment;
    _needsSaving = true;
  }
  /**
    * the current input file or last file to which saved to or loaded from
    */
  public String getInputFile(){
    return _inputFile;
  }
  /**
    * sort on given column
    */
  public void sort(int column){
    Collections.sort(_rowData, new StringArrayComparator(column));
    _needsSaving = true;
    fireTableDataChanged();
  }
  /**
    * reverse the elements
    */
  public void reverse(){
    Collections.reverse(_rowData);
    _needsSaving = true;
    fireTableDataChanged();
  }
  /**
    *
    *
    * @author Nicky Sandhu
    * @version $Id: DefaultTableData.java,v 1.1.2.10 2001/10/23 16:28:20 jfenolio Exp $
    */
  public class StringArrayComparator implements Comparator{
    private int _col;
    public StringArrayComparator(int col){
      _col = col;
    }
    public int compare(Object o1, Object o2){
      String [] sa1 = (String []) o1;
      String [] sa2 = (String []) o2;
      return sa1[_col].compareTo(sa2[_col]);
    }
    public boolean equals(Object obj){
      return false;
    }
  }
  /*
   * All table model specific stuff is below this line
   */
  /**
   * Number of data references used in DTS calculations
   */
  public int getRowCount(){
    return getNumberOfRows();
  }
  /**
   * Number of columns in the table
   */
  public int getColumnCount(){
    return getHeaders().length;
  }
  /**
   * Returns the name of the column at <i>columnIndex</i>.
   * @param	columnIndex	the index of column
   * @return  the name of the column
   */
  public String getColumnName(int columnIndex){
    return getHeaders()[columnIndex];
  }
  /**
   * Returns true if the cell at <I>rowIndex</I> and <I>columnIndex</I>
   * is editable.  Otherwise, setValueAt() on the cell will not change
   * the value of that cell.
   *
   * @param	rowIndex	the row whose value is to be looked up
   * @param	columnIndex	the column whose value is to be looked up
   * @return	true if the cell is editable.
   * @see #setValueAt
   */
  public boolean isCellEditable(int rowIndex, int columnIndex){
    return true;
  }
  /**
   * Returns an attribute value for the cell at <I>columnIndex</I>
   * and <I>rowIndex</I>.
   *
   * @param	rowIndex	the row whose value is to be looked up
   * @param	columnIndex 	the column whose value is to be looked up
   * @return	the value Object at the specified cell
   */
  public Object getValueAt(int rowIndex, int columnIndex){
    return getRowData(rowIndex)[columnIndex];
  }
  /**
   * Sets an attribute value for the record in the cell at
   * @param	aValue		 the new value
   * @param	rowIndex	 the row whose value is to be changed
   * @param	columnIndex 	 the column whose value is to be changed
   * @see #getValueAt
   * @see #isCellEditable
   */
  public void setValueAt(Object aValue, int rowIndex, int columnIndex){
    getRowData(rowIndex)[columnIndex] = aValue.toString();
    _needsSaving = true;
  }
  /**
   * true if data has been modified since last save
   */
  public boolean needsSaving(){
    return _needsSaving;
  }
}
