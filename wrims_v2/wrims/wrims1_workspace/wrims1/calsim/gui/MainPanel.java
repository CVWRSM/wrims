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
//import calsim.app.*;
//import vista.gui.*;
//import vista.time.*;
import java.awt.*;
//import java.awt.event.*;
//import java.io.*;
//import java.net.URL;
//import java.util.*;
import javax.swing.*;
//import javax.help.*;
//import javax.swing.border.*;
/**
 * The main panel in the main frame of Calsim GUI
 *
 * @author Yan-Ping Zuo, Nicky Sandhu
 * @version $Id: MainPanel.java,v 1.1.4.86 2001/07/12 01:59:47 amunevar Exp $
 */

public class MainPanel extends JPanel
{
  public static boolean DEBUG = true;

  /**
   * constructor
   * Add layout active messages panel, schematic icon, and status panel to the main panel.
   * parameters: frame in which the panel will be displayed
   */
  public MainPanel(JFrame fr) {
    JFrame frame = fr;
    _mainMenuBar = new MainMenuBar(this);
    _messagePanel = new MessagePanel(frame,this);
    setBackground(new Color(229,240,203));
    setLayout(new BorderLayout(5,5));
    add(_messagePanel.getMessagePanelComp(),BorderLayout.NORTH);
    _tabbedPane = new JTabbedPane();
    setRetrievePanel();
    setDtsTreePanel();
    // set selected to general retrieve
    _tabbedPane.setSelectedIndex(0);
    add(_tabbedPane, BorderLayout.CENTER);
  }
  /**
    *
    */
  public void setRetrievePanel(){
    int i = _tabbedPane.indexOfTab("General");
    if ( i == -1 ){
      _tabbedPane.addTab("General",null,new GeneralRetrievePanel(),"General Retrieve Panel");
      i = _tabbedPane.indexOfTab("General");
    }
    _tabbedPane.setSelectedIndex(i);
  }

  public void setDtsTreePanel(){
	int i = -1; //_tabbedPane.indexOfTab("Dts Tree");
    if ( i == -1 ){
      _tabbedPane.addTab("Dts Tree", null, new DtsTreePanel(),"Dts Tree Panel");
      i = _tabbedPane.indexOfTab("Dts Tree");
    }
    _tabbedPane.setSelectedIndex(i);
  }

  /**
   * Return the message panel object in the main panel
   */
  public MessagePanel getMessagePanel() {
    return _messagePanel;
  }
  /**
   * Return the Node/Arc menu bar object in the main panel
   */
  public JMenuBar getNodeArcMenuBar() { // CB Not called from anywhere
    return _nodeArcMenuBar.getMenuBar(); // CB weird technique
  }
  /**
   * Return the Main menu bar object in the main panel
   */
  public MainMenuBar getMainMenuBar() {
    return _mainMenuBar;
  }

  /*
   * private variables
   */
  private MessagePanel _messagePanel;
  private NodeArcMenuBar _nodeArcMenuBar;
  private MainMenuBar _mainMenuBar;
  private JTabbedPane _tabbedPane;
} //end of class MainPanel
/*
  $Log: MainPanel.java,v $
  Revision 1.1.4.86  2001/07/12 01:59:47  amunevar
  removal of all unneeded files for cleanup

  Revision 1.1.4.85  2001/04/18 21:07:47  jfenolio
  dts tree added, start month selection for table display

  Revision 1.1.4.84  2000/12/20 20:07:21  amunevar
  commit for ver 1.0.7

  Revision 1.1.4.83  1999/08/05 23:07:40  nsandhu
  *** empty log message ***

  Revision 1.1.4.82  1999/07/20 22:25:13  zuo
  separate status panel from main Panel

  Revision 1.1.4.81  1999/07/20 18:39:39  zuo
  removed schematic tab

  Revision 1.1.4.80  1999/07/20 16:52:57  zuo
  added status panel to frame

  Revision 1.1.4.79  1999/07/19 19:18:18  zuo
  add StudyPanel.java

  Revision 1.1.4.78  1999/07/18 20:56:49  nsandhu
  *** empty log message ***

  Revision 1.1.4.77  1999/07/02 22:13:54  nsandhu
  first move to eliminate extra frames on screen

  Revision 1.1.4.76  1999/07/02 21:27:18  zuo
  add tabbed pane

  Revision 1.1.4.75  1999/07/02 20:13:40  nsandhu
  *** empty log message ***

 */
