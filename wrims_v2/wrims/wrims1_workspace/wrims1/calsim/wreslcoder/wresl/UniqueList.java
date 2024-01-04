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
package calsim.wreslcoder.wresl;
import java.util.Vector;

/**
 * This defines a special Vector of Strings.  They're not case sensitive and the underlying
 * Vector is optimized for Wresl use.
 * @author Armin Munevar
 * @version $Id: UniqueList.java,v 1.1.2.3 2001/07/12 02:00:10 amunevar Exp $
 */

public class UniqueList extends Vector {

  /**
   * Creates a new instance with default size and load factor.
   */
  public UniqueList() {
    this(600,300);
  }

  /**
   * Creates a new instance with specified size and load factor.
   *
   * @param n size
   * @param m amount to grow by when current size is exceeded
   *
   */
  public UniqueList(int n, int m) {
    super(n,m);
    idCode = new java.util.Hashtable(n);
  }

  public int getIndexOf(Object id) {
    return super.indexOf(id.toString().toUpperCase());
  }

  /**
   * Adds an element to this instance.  Converts to upper case.
   *
   * @param the element to add
   * @return true if element was successfully added, false
   *         if element was already in this instance.
   */

  public boolean newItem(Object id) {
    String idUpper = id.toString().toUpperCase();
    if (super.indexOf( idUpper) < 0) {
      addElement(idUpper);
      idCode.put(idUpper, new Integer(super.indexOf(idUpper) + 1));
      // +1 for Fortran array compatability
      return true;
    } else {
      return false;
    }
  }

  /*
    Stuff for the ID code handing feature
  */
  private java.util.Hashtable idCode;

  public Integer getIdCode(Object id) {
    return (Integer) idCode.get(id.toString().toUpperCase());
  }

  /* for use by a sorting routine */
  public void swap( int m, int n) {
    String tmp;
    tmp = (String) elementAt( m);
    setElementAt( elementAt( n), m);
    setElementAt(  tmp, n);
  }


}
