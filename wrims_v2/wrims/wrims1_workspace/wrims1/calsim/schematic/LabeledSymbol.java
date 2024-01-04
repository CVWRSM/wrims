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
import java.awt.*;
//import java.util.Properties;
import vista.graph.*;
/**
 * Symbol is a polygon shape drawn with respect to the x and y coordinates
 * of rectangle. No scaling is currently done.<p>
 * The symbol is a polygon and can be represented by a series of x and y coordinates.
 * The symbol may be filled or outlined. The x and y coordinates are taken to be 
 * in units of pixels ~ 1/72 inch for most displays.
 *
 * @author Nicky Sandhu
 * @version $Id: LabeledSymbol.java,v 1.1.2.3 1999/06/01 16:58:44 nsandhu Exp $
 */
public class LabeledSymbol extends GraphicElement implements ScaledElement{
  /**
   * for debugging
   */
public static final boolean DEBUG = false;
private TextLine _label;
private Symbol _sym;
private static Rectangle CONST_RECT = new Rectangle(0,0,80,80);
  /**
   * Constructor.
   */
public LabeledSymbol(String label, Symbol sym) { 
  _label = new TextLine(label);
  _sym = sym;
  setName("Labeled Symbol");
}
  /**
   * gets the element containing the label
   */
public TextLine getTextLine(){
  return _label;
}
  /**
   *
   */
public Symbol getSymbol(){
  return _sym;
}
  /**
   * true if x,y hits the drawing. More precise than contains(x,y)
   */
public boolean hitsDrawing(int x, int y){
  if ( _sym != null )
    return _sym.hitsDrawing(x,y);
  else
    return false;
}
  /**
   * draws polygon representing symbol. Currently for efficiency no
   * scaling is done. However a subclass will do so. In this way it
   * would be the users choice to use the most suitable class.
   */
public void Draw(){
  Graphics gc = getGraphics();
  Rectangle r = getInsetedBounds();
  //
  CONST_RECT.x = r.x;
  CONST_RECT.y = r.y;
  _sym.draw(gc,CONST_RECT);
  // draw label at center of symbol
  int oh = r.height;
  int ow = r.width;
  r.height = 0;
  r.width = 0;
  _label.draw(gc,r);
  r.height = oh;
  r.width = ow;
}
  /**
   * calculates the preferred size of this element
   * @return The preferred size
   */
public Dimension getPreferredSize(){
  return new Dimension(25,25);
}
  /**
   * calculates the minimum size of this element
   * @return The minimum size
   */
public Dimension getMinimumSize(){
  return _sym.getPreferredSize();
}
  /**
   * prefix tag for storing attributes
   */
public String getPrefixTag(String prefixTag){
  return prefixTag + getName() + ".";
}
}
