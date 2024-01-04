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
import java.awt.*;
import java.util.*;
/**
 * A schematic of symbols. This is the view part of the schematic and is
 * implemented using GraphicElement's from vista.graph
 * 
 * @see vista.graph.GraphicElement
 * @author Nicky Sandhu
 * @version $Id: CalsimSchematic.java,v 1.1.2.5 1999/07/13 16:14:30 nsandhu Exp $
 */
public class CalsimSchematic extends GEContainer{
  // The data model for the schematic
  public CalsimSchematicDataModel _sdm;
  // an hash table to map a symbol to a reference object
  public Hashtable _somap; 
  // a default size for this schematic
  private Dimension _size = new Dimension(100,100);
  // the main container for all the graphic elements
  public GEContainer _sc;
  // the scaled layout used with the main container to layout the graphic elements
  public GEScaledLayout _layout;
  /**
    * creates a schematic from a given schematic data model
    */
  public CalsimSchematic(CalsimSchematicDataModel sdm){
    this(new GEAttr(), sdm);
  }
  /**
    * creates a schematic from the given attributes and model
    */
  public CalsimSchematic(GEAttr attr, CalsimSchematicDataModel sdm){
    super(attr);
    setModel(sdm);
  }
  /**
    * get the data model underlying this schematic
    */
  public CalsimSchematicDataModel getModel(){
    return _sdm;
  }
  /**
    * sets the model
    */
  public void setModel(CalsimSchematicDataModel sdm){
    _sc = new GEContainer( new GEAttr());
    _somap = new Hashtable();
    _sdm = sdm;
    // set the layout to scaled layout
    Scale xScale = new Scale(_sdm.getXMin(), _sdm.getXMax(), 0, 10);
    Scale yScale = new Scale(_sdm.getYMin(), _sdm.getYMax(), 0, 10);
    _layout = new GEScaledLayout(xScale, yScale);
    _sc.setLayout(_layout);
    // add the symbols to this container and add their reference objects
    // to a dictionary to keep track of them.
    _sdm.reset();
    while ( _sdm.hasMoreSymbols()){
      CalsimSymbolData sd = _sdm.nextSymbolData();
      DoublePoint dp1 = sd.getAnchorPoint();
      DoublePoint dp2 = sd.getOtherPoint();
      //_sc.add(sd.getGraphicElement());
      _sc.add( new DoubleRect(dp1.x, dp1.y, dp2.x - dp1.x, dp2.y - dp1.y), 
	       sd.getGraphicElement());
      _somap.put(sd.getGraphicElement(),sd.getReferenceObject());
    }
    // add title
    new TextLine( new TextLineAttr(), _sdm.getTitleText() );
    // remove if added previous elements
    removeAll();
    //
    _sc.setBackgroundColor(Color.white);
    //
    setLayout( new GEBorderLayout() );
    add( GEBorderLayout.CENTER, _sc);
    //    add( GEBorderLayout.NORTH, tl);
    //
    if ( sdm instanceof NetworkSchematicData ){
      NetworkSchematicData nsd = (NetworkSchematicData) sdm;
      setPreferredSize((int) nsd.width, (int) nsd.height);
      setSize(new Dimension((int) nsd.width, (int) nsd.height));
      doLayout();
    }
  }
  /**
    * the model has changed for only the following symbol data
    */
  public void modelChanged(CalsimSymbolData [] csdArray){
    removedFromModel(csdArray);
    addedToModel(csdArray);
  }
  /**
    * Symbols have been added to the model.
    */
  public void addedToModel(CalsimSymbolData [] csdArray){
    // add the symbols to this container and add their reference objects
    // to a dictionary to keep track of them.
    if ( csdArray == null ) return;
    for(int i=0; i < csdArray.length; i++){
      CalsimSymbolData sd = csdArray[i];
      if ( sd == null ) continue;
      DoublePoint dp1 = sd.getAnchorPoint();
      DoublePoint dp2 = sd.getOtherPoint();
      GraphicElement ge = sd.getGraphicElement();
      _sc.add( new DoubleRect(dp1.x, dp1.y, dp2.x - dp1.x, dp2.y - dp1.y), 
	       ge);
      if ( ge instanceof LabeledSymbol ) 
	_sc.drawLast(ge);
      else
	_sc.drawFirst(ge);
      _somap.put(ge,sd.getReferenceObject());
    }
  }
  /**
    * symbols have been removed, the schematic needs to update itself.
    */
  public void removedFromModel(CalsimSymbolData [] csdArray){
    // add the symbols to this container and add their reference objects
    // to a dictionary to keep track of them.
    if ( csdArray == null ) return;
    for(int i=0; i < csdArray.length; i++){
      CalsimSymbolData sd = csdArray[i];
      if ( sd == null ) continue;
      GraphicElement ge = sd.getGraphicElement();
      _sc.remove( ge );
      _sc.getLayout().removeLayoutElement(ge);
      _somap.remove(ge);
    }
  }
  /**
    *
    */
  public void setHiddenVisible(boolean b){
    //
    int count = _sc.getElementCount();
    for(int i=0; i < count; i++){
      GraphicElement ge = (GraphicElement) _sc.getElement(i);
      if ( ((String)getReferenceObject(ge)).indexOf("hidden") >= 0 ) 
	ge.setVisible(b);
    }
  }
  /**
    * gets the reference object associated with this graphic element
    */
  public Object getReferenceObject(GraphicElement ge){
    return _somap.get(ge);
  }
  /**
    * gets the symbol related to this reference object
    */
  public GraphicElement getSymbol(Object ref){
    Enumeration e = _somap.keys();
    while ( e.hasMoreElements() ){
      Object key = e.nextElement();
      if ( _somap.get(key).equals(ref) )
	return (GraphicElement) key;
    }
    return null;
  }
  /**
    * gets the scale in the X direction
    */
  public Scale getXScale(){
    return _layout.getXScale();
  }
  /**
    * gets the scale in the Y direction
    */
  public Scale getYScale(){
    return _layout.getYScale();
  }
  /**
    * add a graphic element symbol along with its reference object
    */
  public void addSymbol(GraphicElement ge, Object obj, 
			DoublePoint dp1, DoublePoint dp2){
    _sc.add(ge);
    _sc.add( new DoubleRect(dp1.x, dp1.y, dp2.x - dp1.x, dp2.y - dp1.y), 
	     ge);
    _somap.put(ge,obj);
  }
  /**
    * removes symbol from this schematic
    */
  public void removeSymbol(GraphicElement ge){
    _sc.remove(ge);
    Object obj = _somap.get(ge);
    _somap.remove(obj);
  }
  /**
    * notifies the layout of a change in the position of a symbol
    */
  public void changeSymbol(GraphicElement ge, DoublePoint dp1, DoublePoint dp2){
    _layout.removeLayoutElement(ge);
    _layout.addLayoutElement
      (
       new DoubleRect(dp1.x, dp1.y, dp2.x - dp1.x, dp2.y - dp1.y), 
       ge);
  }
  /**
    * add a graphic element symbol along with its reference object
    */
  public void addSymbol(GraphicElement ge, Object obj){
    _sc.add(ge);
    Rectangle r = ge.getBounds();
    Scale xs = _layout.getXScale();
    Scale ys = _layout.getYScale();
    _sc.add( new DoubleRect(xs.scaleToDC(r.x),
			    ys.scaleToDC(r.y),
			    xs.scaleToDC(r.x+r.width) - xs.scaleToDC(r.x),
			    ys.scaleToDC(r.y+r.height) - ys.scaleToDC(r.y)),
			    ge);
    _somap.put(ge,obj);
  }
  /**
    * returns the symbol at the given x, y location
    */
  public GraphicElement getHitSymbol(int x, int y){
    // get the element hit
    return _sc.getHitElement(x,y);
  }
  /**
    * first finds out what element got hit and then uses a mapping to
    * determine what object is associated with that element.
    */
  public Object getHitElementObject(int x, int y){
    // get the element hit
    GraphicElement ge = _sc.getHitElement(x,y);
    // get the corresponding reference object for that element
    if ( ge != null ){
      Object obj = _somap.get(ge);
      return obj;
    } else {
      return null;
    }
  }
  /**
    * sets the preferred size for this schematic
    */
  public void setPreferredSize(int width, int height){
    _size = new Dimension(width,height);
    CalsimSchematicDataModel csm = getModel();
    if ( csm instanceof NetworkSchematicData ){
      NetworkSchematicData nsd = (NetworkSchematicData) csm;
      nsd.width = width;
      nsd.height = height;
    }
  }
  /**
    * returns the preferred size for this schematic
    */
  public Dimension getPreferredSize(){
    return _size;
  }
  /**
    * returns the minimum size for this schematic
    */
  public Dimension getMinimumSize(){
    return _size;
  }
}
