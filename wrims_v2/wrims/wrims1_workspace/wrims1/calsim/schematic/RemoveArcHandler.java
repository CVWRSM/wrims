/*
    Copyright (C) 1996, 1997, 1998 State of California, Department of 
    Water Resources.

    VISTA : A VISualization Tool and Analyzer. 
	Version 1.0beta
	by Nicky Sandhu
    California Dept. of Water Resources
    Division of Planning, Delta Modeling Section
    1416 Ninth Street
    Sacramento, CA 95814
    (916)-653-7552
    nsandhu@water.ca.gov

    Send bug reports to nsandhu@water.ca.gov

    This program is licensed to you under the terms of the GNU General
    Public License, version 2, as published by the Free Software
    Foundation.

    You should have received a copy of the GNU General Public License
    along with this program; if not, contact Dr. Francis Chung, below,
    or the Free Software Foundation, 675 Mass Ave, Cambridge, MA
    02139, USA.

    THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA
    DEPARTMENT OF WATER RESOURCES AND CONTRIBUTORS "AS IS" AND ANY
    EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
    PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE CALIFORNIA
    DEPARTMENT OF WATER RESOURCES OR ITS CONTRIBUTORS BE LIABLE FOR
    ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
    OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS; OR
    BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
    USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
    DAMAGE.

    For more information about VISTA, contact:

    Dr. Francis Chung
    California Dept. of Water Resources
    Division of Planning, Delta Modeling Section
    1416 Ninth Street
    Sacramento, CA  95814
    916-653-5601
    chung@water.ca.gov

    or see our home page: http://wwwdelmod.water.ca.gov/

    Send bug reports to nsandhu@water.ca.gov or call (916)-653-7552

*/
package calsim.schematic;
import calsim.gym.*;
//import vista.graph.*;
//import javax.swing.*;
import java.awt.event.*;
//import java.awt.*;
//import java.util.*;
//import calsim.gui.*;
/**
 * An interface defining the call back functions when the schematic
 * recives meaninful input
 *
 * @author Nicky Sandhu
 * @version $Id: RemoveArcHandler.java,v 1.1.2.2 1999/07/13 16:14:33 nsandhu Exp $
 */
public class RemoveArcHandler implements RequestHandler{
  private CalsimSchematic _cs;
  private CalsimSchematicCanvas _csc;
  private NetworkSchematicData _nsd;
  public RemoveArcHandler(CalsimSchematicCanvas csc){
    _csc = csc;
    _cs = _csc.getSchematic();
    _nsd = (NetworkSchematicData) _cs.getModel();
  }
  /**
    * called when a symbol is dragged 
    */
  public void draggedTo(Object obj, MouseEvent e){
  }
  /**
    * called when a symbol with reference object obj is clicked on
    */
  public void clickedOn(Object obj, MouseEvent e){
    // get arc under this point else quit
    String arcNum = null;
    if ( obj instanceof String ){
      String nm = (String) obj;
      if ( nm.indexOf("inflow") >= 0 ){
	arcNum = nm.substring(nm.indexOf("inflow") + 7,nm.length()).trim();
      } else if ( nm.indexOf("flow") >= 0 ){
	arcNum = nm.substring(nm.indexOf("flow") + 5,nm.length()).trim();
      } else if ( nm.indexOf("demand") >= 0 ){
	arcNum = nm.substring(nm.indexOf("demand") + 7,nm.length()).trim();
      } else {
	//throw new RuntimeException("Unknown type of arc: " + nm);
      }
    }
    if ( arcNum == null ) return;
    // get the network and the arc
    Network net = _nsd.getNetwork();
    Arc arc = net.getArc(arcNum);
    if( arc == null ) return;
    // remove from schematic
    System.out.println("Removing arc " + arcNum);
    System.out.println("Removing " + obj);
    System.out.println("Removing symbol " + _cs.getSymbol(obj));
    _cs.removeSymbol(_cs.getSymbol(obj));
    // remove from network
    net.remove(arc);
    // redraw schematic
    _csc.redraw();
  }
  /**
    * called when a symbol with reference object obj is double clicked on
    */
  public void doubleClickedOn(Object obj, MouseEvent e){
  }
  /**
   * called when a symbol with reference object obj is double clicked on
   */
  public void pressedOn(Object obj, MouseEvent e){
  }
  /**
   * called when a symbol with reference object obj is double clicked on
   */
  public void releasedOn(Object obj, MouseEvent e){
  }
  /**
    * called when a symbol with reference object obj is moved over
    */
  public void movedOver(Object obj, MouseEvent e){
  }
  
}
