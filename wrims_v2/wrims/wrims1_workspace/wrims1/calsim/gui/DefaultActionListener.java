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
import vista.gui.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
  * Default action listener
  *
  * @author Nicky Sandhu
  * @version $Id: DefaultActionListener.java,v 1.1.2.3 2001/07/12 01:59:34 amunevar Exp $
  */
public abstract class DefaultActionListener extends WorkerActionListener{
  private Component _comp;
  private Frame _fr;
  private Component _glass;
  private Cursor _oldCursor;
//  private String _preMsg, _postMsg;
//  private String _postMsg;
  private MouseListener _ml = new MouseAdapter(){
    public void mousePressed(MouseEvent e) {}
  };
  /**
    *
    */
  public DefaultActionListener(Component comp, String preMsg){
    this(comp,preMsg,"Done.");
  }
  /**
    *
    */
  public DefaultActionListener(Component comp, String preMsg, String postMsg){
    _comp = comp;
//    _preMsg = preMsg;
 //   _postMsg = postMsg;
    // set this to false if you don't want to use threads. Sets this
    // so for all GuiTaskListener instances. Also does it dynamically.
    USE_THREADS=true;
  }
  public void doPreWork(){
    if ( _comp == null ) return;
    _fr = JOptionPane.getFrameForComponent(_comp);
    if ( _fr instanceof JFrame ){
      JFrame jfr = (JFrame) _fr;
      _glass = jfr.getGlassPane();
      _glass.addMouseListener(_ml);
      _oldCursor = jfr.getCursor();
      _glass.setVisible(true);
      _glass.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    } else{
      _fr.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
  }
  public void doPostWork(){
    if ( _comp == null ) return;
    if ( _fr instanceof JFrame ){
      _glass.setCursor(_oldCursor);
      _glass.removeMouseListener(_ml);
      _glass.setVisible(false);
    } else {
      _fr.setCursor(_oldCursor);
    }
  }
}
