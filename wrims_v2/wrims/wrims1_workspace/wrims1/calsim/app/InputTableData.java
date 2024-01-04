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
import javax.swing.table.TableModel;
/**
  * The input table data. Each table has a name, a header ( column description ), and rows. Optionally
  * a comment may also be present.
  *
  * @author Nicky Sandhu ,Armin Munevar
  * @version $Id: InputTableData.java,v 1.1.2.5 2001/05/31 19:58:36 jfenolio Exp $
  */
public interface InputTableData extends TableModel{
  /**
    * the name of this table
    */
  public String getTableName();
  /**
    * the header array for column names
    */
  public String [] getHeaders();
  /**
    * the number of rows of data
    */
  public int getNumberOfRows();
  /**
    * the row data @ the ith row, the length of which must match the # of cols
    */
  public String [] getRowData(int i);
  /**
    * adds a row to this table
    */
  public void addRow(String [] rowData);
  /**
    * inserts a row in this table
    */
  public void insertRow(int i, String [] rowData);
  /**
    * removes a row from this table
    */
  public void removeRow(int i);
  /**
    * gets comment string
    */
  public String getComment();
  /**
    * sets comment string
    */
  public void setComment(String comment);
  /**
    * loads from another file
    */
  public void load(String file);
  /*
    * saves to given file
    */
  public void save(String file);
  /**
    * the current input file or last file to which saved to or loaded from
    */
  public String getInputFile();
  /**
    * sort on given column
    */
  public void sort(int column);
  /**
    * reverse the elements
    */
  public void reverse();
  /**
   * true if data has been modified since last save
   */
  public boolean needsSaving();

}


