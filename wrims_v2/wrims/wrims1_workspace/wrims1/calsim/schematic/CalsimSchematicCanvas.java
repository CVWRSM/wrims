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
import vista.graph.*;
//import vista.set.*;
import vista.gui.VistaUtils;
import javax.swing.*;
import java.awt.*;
//import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
/**
 * A canvas for drawing the schematic. This canvas also has methods to 
 * install handlers for mouse requests on schematic components. 
 * When a mouse is clicked or moved over a particular schematic component
 * the registered RequestHandler objects appropriate methods are called.
 *
 * @see RequestHandler
 * @author Nicky Sandhu
 * @version $Id: CalsimSchematicCanvas.java,v 1.1.2.6 1999/07/18 20:57:54 nsandhu Exp $
 */
public class CalsimSchematicCanvas extends JPanel{
  private CalsimSchematic _sc;
  private Vector _handlers;
//  private static boolean DEBUG = false;
  public GECanvas _canvas;
  public JScrollPane _scrollPane;
//  private Object _currentObject = null;
  public int xi,yi,wi,hi;
  /**
    *
    */
  public CalsimSchematicCanvas(CalsimSchematic sc){
    //
    _handlers = new Vector();
    // add the schematic canvas
    _sc = sc;
    //
    _canvas = new GECanvas(_sc);
    _canvas.setDoubleBuffered(true);
    _canvas.setClipped(false);
    setLayout(new BorderLayout());
    _scrollPane = new JScrollPane(_canvas);
    add(_scrollPane,BorderLayout.CENTER);
    // add mouse listeners to this component
    _canvas.addMouseListener( new MouseAdapter(){
      public void mouseClicked(MouseEvent e){
	int x = e.getX(); int y = e.getY();
	int cc = e.getClickCount();
	Object obj = _sc.getHitElementObject(x,y);
	if ( cc == 1 ) {
	  clickedOn(obj,e);
	} else {
	  doubleClickedOn(obj,e);
	}
      }
      public void mousePressed(MouseEvent e){
	int x = e.getX(); int y = e.getY();
	Object obj = _sc.getHitElementObject(x,y);
	pressedOn(obj,e);
      }
      public void mouseReleased(MouseEvent e){
	int x = e.getX(); int y = e.getY();
	Object obj = _sc.getHitElementObject(x,y);
	releasedOn(obj,e);
      }
    });
    //
    _canvas.addMouseMotionListener( new MouseMotionAdapter(){
      public void mouseMoved(MouseEvent e){
	int x = e.getX(); int y = e.getY();
	Object obj = _sc.getHitElementObject(x,y);
	movedOver(obj,e);
      }
      public void mouseDragged(MouseEvent e){
//	int x = e.getX(); int y = e.getY();
//	Object obj = _sc.getHitElementObject(x,y);
	draggedTo(null,e);
      }
    });
  }
  /**
    *
    */
  public void redraw(){
    Frame fr = JOptionPane.getFrameForComponent(getCanvas());
    Cursor oldCursor = fr.getCursor();
    fr.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    getCanvas().redoNextPaint();
    fr.paint(fr.getGraphics());
    fr.setCursor(oldCursor);
  }
  /**
    *
    */
  public JScrollPane getScrollPane(){
    return _scrollPane;
  }
  /**
    *
    */
  public CalsimSchematic getSchematic(){
    return _sc;
  }
  /**
    *
    */
  public GECanvas getCanvas(){
    return _canvas;
  }
  /**
    *
    */
  public void add(RequestHandler rh){
    _handlers.addElement(rh);
  }
  /**
    *
    */
  public void remove(RequestHandler rh){
    _handlers.removeElement(rh);
  }
  /**
    *
    */
  public void removeAllHandlers(){
    _handlers.removeAllElements();
  }
  /**
    *
    */
  public void clickedOn(Object obj, MouseEvent evt){
    for(Enumeration e = _handlers.elements(); e.hasMoreElements(); ){
      RequestHandler rh = (RequestHandler) e.nextElement();
      try {
	rh.clickedOn(obj,evt);
      }catch(Exception exc){
	VistaUtils.displayException(this,exc);
      }
    }
  }
  /**
    *
    */
  public void doubleClickedOn(Object obj, MouseEvent evt){
    for(Enumeration e = _handlers.elements(); e.hasMoreElements(); ){
      RequestHandler rh = (RequestHandler) e.nextElement();
      try {
	rh.doubleClickedOn(obj,evt);
      }catch(Exception exc){
	VistaUtils.displayException(this,exc);
      }
    }
  }
  /**
    *
    */
  public void movedOver(Object obj, MouseEvent evt){
    for(Enumeration e = _handlers.elements(); e.hasMoreElements(); ){
      RequestHandler rh = (RequestHandler) e.nextElement();
      try {
	rh.movedOver(obj,evt);
      }catch(Exception exc){
	VistaUtils.displayException(this,exc);
      }
    }
  }
  /**
    *
    */
  public void pressedOn(Object obj, MouseEvent evt){
    for(Enumeration e = _handlers.elements(); e.hasMoreElements(); ){
      RequestHandler rh = (RequestHandler) e.nextElement();
      try {
	rh.pressedOn(obj,evt);
      }catch(Exception exc){
	VistaUtils.displayException(this,exc);
      }
    }
  }
  /**
    *
    */
  public void releasedOn(Object obj, MouseEvent evt){
    for(Enumeration e = _handlers.elements(); e.hasMoreElements(); ){
      RequestHandler rh = (RequestHandler) e.nextElement();
      try {
	rh.releasedOn(obj,evt);
      }catch(Exception exc){
	VistaUtils.displayException(this,exc);
      }
    }
  }
  /**
    *
    */
  public void draggedTo(Object obj, MouseEvent evt){
    for(Enumeration e = _handlers.elements(); e.hasMoreElements(); ){
      RequestHandler rh = (RequestHandler) e.nextElement();
      try {
	rh.draggedTo(null,evt);
      }catch(Exception exc){
	VistaUtils.displayException(this,exc);
      }
    }
  }
}
