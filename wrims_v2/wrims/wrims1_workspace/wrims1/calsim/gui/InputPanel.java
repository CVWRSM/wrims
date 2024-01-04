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
import calsim.app.*;
//import java.util.*;
/**
  * Panel containing the system tables
  *
  * @author Nicky Sandhu
  * @version $Id: InputPanel.java,v 1.1.2.11 2001/07/12 01:59:41 amunevar Exp $
  */
public class InputPanel extends JTabbedPane{
  String [] tabNames = {"Connectivity","Reservoir","Channel","Delivery","Return","Inflow","Weight"};
  String [] stdFiles = {
    "connectivity-table.csv",
    "reservoir-node-table.csv",
    "channel-arc-table.csv",
    "delivery-arc-table.csv",
    "return-flow-arc-table.csv",
    "inflow-arc-table.csv",
    "weights-table.csv"
  };
  String [] [] headers = {
    new String [] {"Node","Arcs IN","Arcs OUT","Storage","Description"},
    new String [] {"Node","# of Levels","Level 1","Level 2","Level 3","Level 4","Level 5","Level 6","Level 7","Level 8","Level 9","Level 10","Units","Discharge Arc","Description"},
    new String [] {"Arc","Minimum Flow","Units","Maximum Flow","Units","Minimum Instream Flow","Units","Description"},
    new String [] {"Arc","Demand","Units","Description"},
    new String [] {"Arc","Assoc. Delivery Arc","Return Fraction","Description"},
    new String [] {"Arc","Inflow","Units","Description"},
    new String [] {"Dvar","Weight","Dvar Units","Description"}
  };
  /**
    *
    */
  public InputPanel(){
    for(int i=0; i < tabNames.length; i++){
      addTab(tabNames[i],new MTab(new InputDataUI(new DefaultTableData(tabNames[i],headers[i]))));
    }
  }
  /**
    *
    */
  public void clearData(){
    MTab mt;
    InputDataUI mp;
    for(int i=0; i < tabNames.length; i++){
      mt = (MTab) getComponentAt(i);
      mp = (InputDataUI) mt.getMPanel();
      mp.resetModel();
    }
  }
  /**
    *
    */
  public InputPanel(Study study){
    for(int i=0; i < tabNames.length; i++){
      addTab(tabNames[i],new MTab(new InputDataUI(new DefaultTableData(tabNames[i],headers[i]))));
    }
  }
  /**
    * returns the filename to be saved to for the given tab
    */
  public String getFilenameForTab(String tab){
//    boolean found = false;
    for(int i=0; i < tabNames.length; i++){
      if (tab.equalsIgnoreCase(tabNames[i])){
	return stdFiles[i];
      }
    }
    return null;
  }
}
