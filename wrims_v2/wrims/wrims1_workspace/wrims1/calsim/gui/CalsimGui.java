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
import java.awt.event.*;
//import java.io.*;
import vista.gui.VistaUtils;
import calsim.app.AppProps;

//import calsim.installer.CalsimBuilder;
/**
 * The main frme of Calsim  GUI
 *
 * @author YanPing Zuo, Armin Munever
 * @version $Id: CalsimGui.java,v 1.1.2.8 2001/10/23 16:28:35 jfenolio Exp $
 */

public class CalsimGui {
  //public final String CALSIM_HOME = "D:\\WRIMS1Development\\Calsim1.3\\calsim"; // CB for IDE use (non-batch file)
  private static JFrame frameBase = new JFrame(GuiUtils.getProgramName()); // version is already in About screen
  public static boolean DEBUG = true;
  public static int FRAME_WIDTH=900;
  public static int FRAME_HEIGHT=666;
  static {
		loadProps();
  }
  /**
   * the main method
   */
  public static void main(String args[]) {
    String prjFile = null;
    if (args.length == 1) {
      prjFile= args[0];
    }
    try {
		  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
    new CalsimGui(prjFile);
  }

  /**
   * constructor
   * create the main frame, retrieve the menu bar from the main panel,
   * and display the main panel.
   */
  public CalsimGui(String prjFile) {
//    JFrame fr = new JFrame(GuiUtils.getProgramName()+GuiUtils.getVersionNo());
    //JFrame fr = new JFrame(GuiUtils.getProgramName()); // version is already in About screen
	frameBase.setIconImage(Toolkit.getDefaultToolkit().
		createImage(VistaUtils.getImageAsBytes("/calsim/gui/calsimoas.gif")));
	Container pane = frameBase.getContentPane();
	pane.setBackground(new Color(207,220,200));
	pane.setLayout(new BorderLayout());
    TabbedPane tabbedPane = new TabbedPane(frameBase);
    pane.add(tabbedPane.getTabbedPane(), BorderLayout.CENTER);
    pane.add(GuiUtils.getStatusPanel(), BorderLayout.SOUTH);
    //
    if ( prjFile != null )
      GuiUtils.getMainPanel().getMainMenuBar().openProject(prjFile);
    else
      GuiUtils.getMainPanel().getMessagePanel().updateMessagePanel();
    //
    frameBase.addWindowListener( new WindowAdapter() {
      public void windowClosing(WindowEvent evt){
		exit();
      }
    });
    //System.out.println(FRAME_WIDTH+" "+FRAME_HEIGHT);
    frameBase.setSize(FRAME_WIDTH,FRAME_HEIGHT);
    frameBase.setExtendedState(JFrame.NORMAL);
    frameBase.setVisible(true);
    frameBase.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

  }

  /**
   * close the window and exit
   * if user made any change without saving, a warning will be displayed.
   */
  void exit() {
    //
      //saveProps();
      GuiUtils.getMainPanel().getMainMenuBar().fileExit();
    //
  }
    /**
     *
     */


  
    public static void loadProps(){
     String sytemName=System.getProperty("os.name");
     if (System.getProperty("os.name").equals("Windows XP")){
    	FRAME_WIDTH = new Integer(AppProps.getProperty("CalsimGui.FRAME_WIDTH1")).intValue();
     }else{
    	 FRAME_WIDTH = new Integer(AppProps.getProperty("CalsimGui.FRAME_WIDTH2")).intValue(); 
     }
	FRAME_HEIGHT = new Integer(AppProps.getProperty("CalsimGui.FRAME_HEIGHT")).intValue();
    }
    /**
     *
     */
    public static void saveProps(){
    FRAME_WIDTH=frameBase.getWidth();
    FRAME_HEIGHT=frameBase.getHeight();
    if (System.getProperty("os.name").equals("Windows XP")){
    	AppProps.setProperty("CalsimGui.FRAME_WIDTH1",new Integer(FRAME_WIDTH).toString());
    }else{
    	AppProps.setProperty("CalsimGui.FRAME_WIDTH2",new Integer(FRAME_WIDTH).toString());
    }
	AppProps.setProperty("CalsimGui.FRAME_HEIGHT",new Integer(FRAME_HEIGHT).toString());
    }
  /**
   * Private variables
   */
//  private MainPanel _panel;
}

