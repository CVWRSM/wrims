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
///import vista.graph.*;
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
 * @version $Id: RemoveNodeHandler.java,v 1.1.2.2 1999/07/13 16:14:33 nsandhu Exp $
 */
public class RemoveNodeHandler implements RequestHandler{
  private CalsimSchematic _cs;
  private CalsimSchematicCanvas _csc;
  private NetworkSchematicData _nsd;
//  private GraphicElement _nodeElement;
//  private String _label, _type;
  public RemoveNodeHandler(CalsimSchematicCanvas csc){
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
    // get node under this point else quit
    String nodeNum = null;
    if ( obj instanceof String ){
      String nm = (String) obj;
      if ( nm.indexOf("storage") >= 0 ){
	nodeNum = nm.substring(nm.indexOf("storage") + 8,nm.length()).trim();
      } else if ( nm.indexOf("junction") >= 0 ){
	nodeNum = nm.substring(nm.indexOf("junction") + 9,nm.length()).trim();
      } else if ( nm.indexOf("hidden") >= 0 ){
	nodeNum = nm.substring(nm.indexOf("hidden") + 7,nm.length()).trim();
      } else {
      }
    }
    if ( nodeNum == null ) return;
    Network net = _nsd.getNetwork();
    Node node = net.getNode(nodeNum);
    if( node == null ) return;
    // remove from network.. call this first as it throws an exception if 
    // node is connected to stuff.
    net.remove(node);
    // remove from schematic
    _cs.removeSymbol(_cs.getSymbol(obj));
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
