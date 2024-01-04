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
import vista.graph.*;
//import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
//import java.util.*;
//import calsim.gui.*;
/**
 * An interface defining the call back functions when the schematic
 * recives meaninful input
 *
 * @author Nicky Sandhu
 * @version $Id: AddNodeHandler.java,v 1.1.2.1 1999/06/01 16:58:43 nsandhu Exp $
 */
public class AddNodeHandler implements RequestHandler{
  private CalsimSchematic _cs;
  private CalsimSchematicCanvas _csc;
  private NetworkSchematicData _nsd;
  private GraphicElement _nodeElement;
  private String _label, _type;
  private boolean _clickedOnce;
  public AddNodeHandler(CalsimSchematicCanvas csc, String label, String type){
    _csc = csc;
    _cs = _csc.getSchematic();
    _nsd = (NetworkSchematicData) _cs.getModel();
    _nodeElement = null;
    _label = label.trim().toUpperCase(); _type = type.trim();
    if ( _type.equals("hidden") && !_label.startsWith("X")){
      _label = "X"+_label;
    }
    _clickedOnce = false;
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
    // draw the node element there...
    movedOver(obj,e);
    // add the node to the network and to the schematic
    Network net = _nsd.getNetwork();
    net.addNode(_label);
    if ( _type.equals("storage") ){
      net.getNode(_label).setHasStorage(true);
    }
    float x = (float) _cs.getXScale().scaleToDC(e.getX());
    float y = (float) _cs.getYScale().scaleToDC(e.getY());
    CalsimSymbolData [] csdArray = _nsd.updateNode(_label, x, y);
    _cs.addedToModel(csdArray);
    _clickedOnce = true;
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
    if ( _clickedOnce ) return;
    // draw the node's graphic element at the mouse position
    // create a node element if not yet created
    if ( _nodeElement == null ){
      _nodeElement = _nsd.createNode(_label,_type);
    }
    Rectangle r = _nodeElement.getBounds();
    r.x = e.getX(); r.y = e.getY();
    _nodeElement.setBounds(r);
    // get the schematic image
    Graphics cg = _csc.getCanvas().getGraphics();
    Image img = _csc.getCanvas().getGraphicElementImage();
    cg.drawImage(img,0,0,_csc.getCanvas());
    // draw the node element on the image ( slight flickering ??)
    _nodeElement.draw(cg,r);
  }
  
}
