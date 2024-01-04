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
import javax.swing.*;
import java.awt.*;
import calsim.schematic.*;
import calsim.schematic.input.InputHandler;

/**
  * A panel to manage display of schematic canvas
  *
  * @author Nicky Sandhu
  * @version $Id: InputSchematicPanel.java,v 1.1.2.4 2000/12/20 20:07:14 amunevar Exp $
  */
public class InputSchematicPanel extends JPanel{
  private CalsimSchematicCanvas _csc;
  private JScrollPane _scrollPane;
  /**
    *
    */
  public InputSchematicPanel(CalsimSchematic sch){
    setLayout(new BorderLayout());
    _scrollPane = new JScrollPane();
    setSchematic(sch);
    add(_scrollPane,BorderLayout.CENTER);
    setBorder(BorderFactory.
	      createTitledBorder(
				 BorderFactory.createMatteBorder(1,1,1,1,Color.blue),
				 "Schematic")
	      );
  }
  /**
    *
    */
  public void setSchematic(CalsimSchematic sch){
    if ( sch == null ) return;
    _csc = new CalsimSchematicCanvas(sch);
    //_csc.add(new PopupMenuHandler());
//    _csc.add(new InputPopupMenuHandler());
    _csc.add(new InputHandler());
    _csc.setSize(sch.getPreferredSize());
    _scrollPane.setViewportView(_csc);
    _scrollPane.setViewportBorder(BorderFactory.createRaisedBevelBorder());
    int vinc = (int) Math.round(Math.max(0.002*_csc.getSize().height,5));
    int hinc = (int) Math.round(Math.max(0.002*_csc.getSize().width,5));
    _scrollPane.getVerticalScrollBar().setUnitIncrement(vinc);
    _scrollPane.getHorizontalScrollBar().setUnitIncrement(hinc);
    _scrollPane.getViewport().putClientProperty("EnableWindowBlit", Boolean.TRUE);
    _scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
  }
  /**
    *
    */
  public void removeSchematic(){
    _scrollPane.setViewportView(new JPanel());
    _scrollPane.revalidate();
  }
}
