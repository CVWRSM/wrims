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

package calsim.gui;
import calsim.app.*;
import vista.set.*;
//import vista.gui.*;
//import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
//import java.awt.*;
import java.awt.event.*;
/**
 * Frame for monthly tables
 * @author Nicky Sandhu
 * @version $Id: MonthlyTableDisplay.java,v 1.1.2.15 2001/07/12 01:59:52 amunevar Exp $
 */
public class MonthlyTableDisplay extends TextDisplay{
  public static boolean DEBUG = false;
  public static String [] itemText = {
    "Table",
    "Graph"
  };
  public static String [] toolTipText = {
    "Shows data in a table",
    "Shows data in a graph"
  };
  public static int [] itemKeys = {
    KeyEvent.VK_T,
    KeyEvent.VK_G
  };
  /**
   *
   */
  public MonthlyTableDisplay(DataReference ref, boolean isWaterYear, boolean isStartMonth, String startMonth){
    super();
    setFrameTitle("MONTHLY REPORT");
    _ref = ref;
    _mr = new MonthlyReport((RegularTimeSeries) _ref.getData(),
			    _ref.getPathname(),
			    _ref.getFilename()
			    );
    addDocument(_mr.getStyledDocument());
  }
  /**
    *
    */
  public MonthlyTableDisplay(DataReference ref, boolean isWaterYear, boolean isStartMonth, String startMonth, int [] years){
    super();
    setFrameTitle("MONTHLY REPORT");
    _ref = ref;
    _mr = new MonthlyReport((RegularTimeSeries) _ref.getData(),
			    _ref.getPathname(),
			    _ref.getFilename(),
			    isWaterYear,
			    isStartMonth,
			    startMonth,
			    years);
    addDocument(_mr.getStyledDocument());
  }
  /**
    *
    */
  public MonthlyTableDisplay(DataReference [] refs, boolean isWaterYear, boolean isStartMonth, String startMonth, int [] years){
    super();
    setFrameTitle("MONTHLY REPORT");
    _refs = refs;
    StyledDocument doc = null;
    for( int i=0; i < _refs.length; i++ ){
      DataReference ref = _refs[i];
      if ( ref == null ) continue;
      MonthlyReport mr = new MonthlyReport((RegularTimeSeries) ref.getData(),
			      ref.getPathname(),
			      ref.getFilename(),
			      isWaterYear,
 			      isStartMonth,
			      startMonth,
			      years);
      if ( doc == null )
	doc = mr.getStyledDocument();
      else{
	doc = mr.appendTo(doc, 0);
      }
    }
    if ( doc != null )
      addDocument(doc);
  }
  /**
   *
   */
  public MonthlyTableDisplay(String [] lines){
    super();
    String lineSeparator = System.getProperty("line.separator");
    StyledDocument doc = new DefaultStyledDocument();
    StyleContext sc = new StyleContext();
    Style def = sc.getStyle(StyleContext.DEFAULT_STYLE);
    Style normal = sc.addStyle("Normal",def);
    StyleConstants.setFontSize(normal,8);
    for(int i=0; i < lines.length; i++){
      try {
	doc.insertString(doc.getEndPosition().getOffset()-1,lines[i]+lineSeparator,normal);
      }catch(BadLocationException ble){
	System.err.println("Could not insert string for line: " + lines[i]);
      }
    }
    addDocument(doc);
  }
  /**
    *
    */
  public JMenuBar getJMenuBar(){
    if ( _mbar == null ) _mbar = createJMenuBar();
    return _mbar;
  }
  /**
   *
   */
  public JMenuBar createJMenuBar(){
    int mindex=0;
    JMenuItem tableItem = new JMenuItem(itemText[mindex]);
    tableItem.setToolTipText(toolTipText[mindex]);
    tableItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[mindex],KeyEvent.CTRL_MASK));
    tableItem.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	table();
      }
    });
    //
    mindex++;
    JMenuItem graphItem = new JMenuItem(itemText[mindex]);
    graphItem.setToolTipText(toolTipText[mindex]);
    graphItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[mindex],KeyEvent.CTRL_MASK));
    graphItem.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	graph();
      }
    });
    //
    //
    _mbar = super.createJMenuBar();
    JMenu fileMenu = _mbar.getMenu(0);
    fileMenu.insert(tableItem,fileMenu.getItemCount()-2);
    fileMenu.insert(graphItem,fileMenu.getItemCount()-2);
    return _mbar;
  }
  /**
   *
   */
  void table(){
    if (DEBUG) System.out.println("Table");
    JFrame fr = null;
    if ( _ref != null )
      fr = AppUtils.tabulate(_ref);
    else
      fr = AppUtils.tabulate(_refs);
    if ( fr != null ) fr.setVisible(true);
  }
  /**
   *
   */
  void graph(){
    if (DEBUG) System.out.println("Graph");
    JFrame fr = null;
    if ( _ref != null )
      fr = AppUtils.plot(_ref);
    else
      fr = AppUtils.plot(_refs);
    if ( fr != null ) fr.setVisible(true);
  }
//  private StyledDocument _doc;
  private DataReference _ref;
  private DataReference [] _refs;
  private MonthlyReport _mr;
  private JMenuBar _mbar;
//  private JTextPane _jtp;
}
