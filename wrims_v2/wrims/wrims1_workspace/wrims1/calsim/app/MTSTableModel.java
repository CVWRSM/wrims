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
//import javax.swing.*;
import javax.swing.table.*;
/**
 * A table model for derived time series
 *
 * @see DerivedTimeSeries
 *
 * @author Nicky Sandhu
 * @version $Id: MTSTableModel.java,v 1.1.4.6 2001/04/18 21:07:35 jfenolio Exp $
 */
public class MTSTableModel extends AbstractTableModel{
  public static String [] tableHeaders = {
    "Derived Time Series", "Dvar/Svar", "B part", "C part"
  };
  /**
   * creates a table model from the given MTS
   */
  public MTSTableModel(MultipleTimeSeries mts){
    _mts = mts;
  }
  /**
   * Number of data references used in MTS calculations
   */
  public int getRowCount(){
    return _mts.getNumberOfDataReferences();
  }
  /**
   * Number of columns in the table
   */
  public int getColumnCount(){
    return 4;
  }
  /**
   * Returns the name of the column at <i>columnIndex</i>.
   * @param	columnIndex	the index of column
   * @return  the name of the column
   */
  public String getColumnName(int columnIndex){
    return tableHeaders[columnIndex];
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
    String dtsname = _mts.getDTSNameAt(rowIndex);
    if ( columnIndex > 1 ){
      if ( dtsname == null ){
	return true;
      }else {
	if ( dtsname.equals("") )
	  return true;
	else
	  return false;
      }
    } else {
      return true;
    }
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
    if ( columnIndex == 0 ){
      String dtsname = _mts.getDTSNameAt(rowIndex);
      if ( dtsname == null ){
	return "";
      }else{
	return dtsname;
      }
    } else if ( columnIndex == 1 ){
      String dtsname = _mts.getDTSNameAt(rowIndex);
      if ( dtsname != null &&
	   ! dtsname.equals("") )
	return "";
      String varType = _mts.getVarTypeAt(rowIndex);
      if ( varType == null ){
	return "";
      }else{
	return varType;
      }
    } else if ( columnIndex == 2 ){
      String dtsname = _mts.getDTSNameAt(rowIndex);
      if ( dtsname != null &&
	   ! dtsname.equals("") )
	return "";
      String bpart = _mts.getBPartAt(rowIndex);
      if ( bpart == null ){
	return "";
      }else{
	return bpart;
      }
    } else if ( columnIndex == 3 ){
      String dtsname = _mts.getDTSNameAt(rowIndex);
      if ( dtsname != null &&
	   ! dtsname.equals("") )
	return "";
      String cpart = _mts.getCPartAt(rowIndex);
      if ( cpart == null ){
	return "";
      }else{
	return cpart;
      }
    } else{
      throw new InternalError("Invalid column access");
    }
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
    if ( columnIndex == 0 ){
      String dtsname = (String) aValue;
    if ( dtsname.length() > 0 )
	  _mts.setDTSNameAt(rowIndex, dtsname);
    } else if ( columnIndex == 1 ){
      String varType = (String) aValue;
      _mts.setVarTypeAt(rowIndex,varType);
    } else if ( columnIndex == 2 ){
      String bpart = (String) aValue;
      _mts.setBPartAt(rowIndex, bpart);
    } else if ( columnIndex == 3 ){
      String cpart = (String) aValue;
      _mts.setCPartAt(rowIndex, cpart);
    } else{
      throw new InternalError("Invalid column access");
    }
  }
  /**
   *
   */
  private MultipleTimeSeries _mts;
}
