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
import java.awt.Dimension;
/**
 * This interface is to be implemented by any class that wishes to
 * provide data for the schematic. This interface requires the implementation
 * to iterate over each symbol that needs to be drawn. The data required for 
 * drawing a particular symbol is encapsulated in the CalsimSymbolData class
 *
 * @see CalsimSymbolData
 * @author Nicky Sandhu
 * @version $Id: CalsimSchematicDataModel.java,v 1.1.2.4 1999/07/18 20:57:55 nsandhu Exp $
 */
public interface CalsimSchematicDataModel{
  /**
   * resets the data model
   */
  public void reset();
  /**
   * gets the data required to draw the next symbol. 
   * @see CalsimSymbolData
   */
  public CalsimSymbolData nextSymbolData();
  /**
   * @return true while has more symbols to be added
   */
  public boolean hasMoreSymbols();
  /**
    * A rectangle from xmin,ymin to xmax-xmin, ymax-ymin
    */
  //  public DoubleRect getRealBounds();
  /**
    * set the bounds from xmin,ymin to xmax,ymax
    */
  //  public void setRealBounds(double xmin, double ymin, double xmax, double ymax);
  /**
   * gets the maximum value for the x axis
   */
  public double getXMax();
  /**
   * gets the maximum value for the x axis
   */
  public double getXMin();
  /**
   * gets the maximum value for the x axis
   */
  public double getYMax();
  /**
   * gets the maximum value for the x axis
   */
  public double getYMin();
  /**
   * gets the title text for this schematic
   */
  public String getTitleText();
  /**
   * sets the title text for this schematic
   */
  public void setTitleText(String str);
  /**
    * gets the width and height of the schematic in screen pixels
    */
  public Dimension getScreenSize();
  /**
    * sets the width and height of the schematic in screen pixels
    */
  //  public void setScreenSize(int width, int height);
}
