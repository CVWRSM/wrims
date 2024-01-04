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
//import calsim.schematic.*;
//import calsim.schematic.input.*;
import javax.swing.*;
//import java.awt.*;
import java.awt.event.*;
//import java.io.*;
//import vista.gui.VistaUtils;
//import calsim.app.AppProps;
//import calsim.gym.*;
/**
 * The input frame of Calsim  GUI
 *
 * @author YanPing Zuo
 * @version $Id: InputPropertyFrame.java,v 1.1.2.3 2001/07/12 01:59:42 amunevar Exp $
 */

public class InputPropertyFrame
{
  public static boolean DEBUG = true;
  public static int FRAME_WIDTH=850;
  public static int FRAME_HEIGHT=700;
    static {
      loadProps();
    }

  /**
   * constructor
   */
  public InputPropertyFrame(Object obj) {
    _obj = obj;
    JFrame fr = new JFrame("" + _obj);/*
    fr.setIconImage(Toolkit.getDefaultToolkit().
                    createImage(VistaUtils.getImageAsBytes("/calsim/gui/calsimoas.gif")));*/
    fr.addWindowListener( new WindowAdapter() {
      public void windowClosing(WindowEvent evt){
	System.exit(0);
      }
    });
    fr.setSize(FRAME_WIDTH,FRAME_HEIGHT);
    fr.setVisible(true);
    fr.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
  }

    /**
     *
     */
    public static void loadProps(){
      //FRAME_WIDTH = new Integer(AppProps.getProperty("CalsimGui.FRAME_WIDTH")).intValue();
      //FRAME_HEIGHT = new Integer(AppProps.getProperty("CalsimGui.FRAME_HEIGHT")).intValue();
    }
private Object _obj;
}

