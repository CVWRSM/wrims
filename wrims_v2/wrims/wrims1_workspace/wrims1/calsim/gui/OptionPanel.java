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

import java.awt.*;
import java.awt.event.*;
//import java.io.*;
import java.util.*;
import javax.swing.*;
//import javax.swing.event.*;
//import javax.swing.border.*;
import vista.gui.XYGridLayout;
/**
 * The panel for the control tab.
 *
 * @author Yan-Ping Zuo
 * @version $Id: OptionPanel.java,v 1.1.2.11.2.2 2002/06/20 19:12:27 adraper Exp $
 */

public class OptionPanel extends JPanel {

  public static boolean DEBUG = true;
  public static String[] listString = { "None",	"VAR", "CON", "BOTH" };

  /**
   * Constructor
   */
  public OptionPanel() {

    _check = new JCheckBox[13];
    setLayout( new XYGridLayout(22,30) );
    setBackground(new Color(229,240,203));
    // solver options
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(2,2));
    panel.setBorder(BorderFactory.createTitledBorder("Solver Output"));
    panel.add(createLabel("Save Report :"));
    panel.add(createCheckBoxPanel(0));
    panel.add(createLabel("     Listing:"));
    panel.add(createComboBoxPanel());
    panel.setBackground(new Color(207,220,200));
    add(panel, new Rectangle(1,1,9,6) );
    // state variable output options
    panel = new JPanel();
    //panel.setLayout(new GridLayout(4,4));
    panel.setLayout( new GridLayout(2,2) );
    panel.setBorder(BorderFactory.createTitledBorder("Other Options"));
    panel.add(createLabel("LF90 Output:"));
    panel.add(createCheckBoxPanel(7));
    panel.add(createLabel("Gen WSI-DI Tables:"));
    panel.add(createCheckBoxPanel(8));
    panel.setBackground(new Color(207,220,200));
    add(panel, new Rectangle(11,1,9,6));
    // slack/ surplus output
    panel = new JPanel();
    panel.setLayout(new GridLayout(2,2));
    panel.setBorder(BorderFactory.createTitledBorder("Slack/Surplus Output"));
    panel.add(createLabel("Save Report:"));
    panel.add(createCheckBoxPanel(3));
    panel.add(createLabel("Save All   :"));
    panel.add(createCheckBoxPanel(4));
    panel.setBackground(new Color(207,220,200));
    add(panel, new Rectangle(11,8,9,6));
    // dss options
    panel = new JPanel();
    panel.setLayout(new GridLayout(2,2));
    panel.setBorder(BorderFactory.createTitledBorder("DSS Options"));
    panel.add(createLabel("   Debug :"));
    panel.add(createCheckBoxPanel(5));
    panel.add(createLabel("Save Old :"));
    panel.add(createCheckBoxPanel(6));
    panel.setBackground(new Color(207,220,200));
    add(panel, new Rectangle(1,8,9,6));
    // add xa
    panel = new JPanel();
    //panel.setLayout( new FlowLayout() );
    panel.setLayout(new GridLayout(4,2));
    panel.setBorder(BorderFactory.createTitledBorder("State Variable Output"));
    panel.add(createLabel("Save Report:"));
    panel.add(createCheckBoxPanel(1));
    panel.add(createLabel("Save All   :"));
    panel.add(createCheckBoxPanel(2));
    panel.add(createLabel("Use Restart   :"));
    panel.add(createCheckBoxPanel(9));
    panel.add(createLabel("Gen Restart   :"));
    panel.add(createCheckBoxPanel(10));
    _check[9].setEnabled(false);
    _check[10].setEnabled(false);
    panel.setBackground(new Color(207,220,200));
    add(panel, new Rectangle(11,15,9,10));




    //add(panel, new Rectangle(3,18,13,3));
    // lf90 debug option
//    panel = new JPanel();
//    panel.setLayout(new GridLayout(1,2));
//    add(panel, new Rectangle(1,21,8,3));

    // wsi-di table creation option
//    panel = new JPanel();
//    panel.setLayout(new GridLayout(1,2));
//    add(panel, new Rectangle(12,21,8,3));
    // position analysis option
    panel = new JPanel();
    panel.setBorder(BorderFactory.createTitledBorder("Position Analysis"));
    panel.setLayout(new GridLayout(6,2));
    panel.add(createLabel("Gen Random Table:"));
    panel.add(createCheckBoxPanel(12));
    _check[12].setEnabled(false);
    panel.add(createLabel("Run:"));
    panel.add(createCheckBoxPanel(11));
    panel.add(createLabel("Start Month:"));
    JPanel monpanel = new JPanel();
    monpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    monpanel.add(_months);
    panel.add(monpanel);
    monpanel.setBackground(new Color(207,220,200));
    panel.add(createLabel("Periods:"));
    monpanel = new JPanel();
    monpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    monpanel.add(_nper);
    _nper.setText("0");
    monpanel.setBackground(new Color(207,220,200));
    panel.add(monpanel);
    panel.add(createLabel("First Start Year:"));
    monpanel = new JPanel();
    monpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    monpanel.add(_start);
    _start.setSelectedItem("1921");
    monpanel.setBackground(new Color(207,220,200));
    panel.add(monpanel);
    panel.add(createLabel("Last Start Year:"));
    monpanel = new JPanel();
    monpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    monpanel.add(_stop);
    _stop.setSelectedItem("1921");
    monpanel.setBackground(new Color(207,220,200));
    panel.add(monpanel);
    panel.setBackground(new Color(207,220,200));
    add(panel, new Rectangle(1,15,9,15));

		_months.setEnabled(false);
		_nper.setEnabled(false);
		_start.setEnabled(false);
		_stop.setEnabled(false);

    panel = new JPanel();
    //panel.setLayout( new FlowLayout() );
    panel.setBorder(BorderFactory.createTitledBorder("XA Options:"));
    panel.setLayout( new GridLayout(1,1) );
    //panel.add(createLabel("XA Options:"));
    panel.add(createTextPanel());
    panel.setBackground(new Color(207,220,200));
    add(panel, new Rectangle(11,26,9,4));

		final Hashtable monthIndex = new Hashtable(12);
    monthIndex.put("OCT", new Integer(0));
    monthIndex.put("NOV", new Integer(1));
    monthIndex.put("DEC", new Integer(2));
    monthIndex.put("JAN", new Integer(3));
    monthIndex.put("FEB", new Integer(4));
    monthIndex.put("MAR", new Integer(5));
    monthIndex.put("APR", new Integer(6));
    monthIndex.put("MAY", new Integer(7));
    monthIndex.put("JUN", new Integer(8));
    monthIndex.put("JUL", new Integer(9));
    monthIndex.put("AUG", new Integer(10));
    monthIndex.put("SEP", new Integer(11));

    // generate random table for position analysis
    _check[12].addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (_check[12].isSelected()) {
				System.out.println("genNewRandomTable = " + "True");
			}
			if (!_check[12].isSelected()) {
				System.out.println("genNewRandomTable = " + "False");
			}
			return;
		}
	});
    
		_check[11].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Study sty = AppUtils.getCurrentStudy();
				updateStudy(sty);
		    if (_check[11].isSelected()) {
				  enablePositionComponents(true);
				  GeneralPanel._timeStep.setSelectedItem("1MON");
				  actionSteps();
				  AppUtils.position = true;
			  } else {
				  AppUtils.position = false;
				  enablePositionComponents(false);
				  GeneralPanel._timeStep.setSelectedItem("1MON");
			  }
			}
		});
    _months.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_check[11].isSelected()) {
					actionSteps();
				//	GeneralPanel._month[0].setSelectedItem(_months.getSelectedItem());
				//	GeneralPanel._month[1].setSelectedItem(_months.getSelectedItem());
					//String s = _nper.getText();
					//if (s.equals("")) return;
					//int i = new Integer(s).intValue();
					//AppUtils.nperiods = i;
			/*		int i1 = i - 12*(i/12);
					Integer obj = (Integer)monthIndex.get(_months.getSelectedItem());
					int month = obj.intValue();
          if (i1 == 0 && month == 0) month = i - 1;
					else	month += i1-1;
					if (month > 11) {System.out.println("hi");month -= 12;}
					GeneralPanel._month[1].setSelectedIndex(month);*/
				}
			}
		});
    _nper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_check[11].isSelected()) {
					actionSteps();
				}
				return;
			}
		});
    _start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_check[11].isSelected()) {
					actionSteps();
				}
			}
		});
    _stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_check[11].isSelected()) {
					actionSteps();
				}
			}
		});


  }

  private void actionSteps() {
	GeneralPanel._month[0].setSelectedItem(_months.getSelectedItem());
	GeneralPanel._year[0].setSelectedItem(_start.getSelectedItem());
	String s = _nper.getText();
	int i = new Integer(s).intValue();
	System.out.println("nperiods = " + i);
	AppUtils.nperiods = i;
	int nyrs = i/12;
	System.out.println("nyrs = " + nyrs);
	int remainder = i - 12*nyrs;
	int startmonthindex = _months.getSelectedIndex();
	int stopmonthindex = startmonthindex + remainder - 1;
	if (stopmonthindex > 11) stopmonthindex = stopmonthindex - 12;
	else if (stopmonthindex < 0) stopmonthindex = stopmonthindex + 12;
	GeneralPanel._month[1].setSelectedIndex(stopmonthindex);
	if ((startmonthindex < 3 && stopmonthindex > 2 && remainder > 0) ||
	  	(startmonthindex - 12 + remainder - 1 > 2)) nyrs++;
	System.out.println("nyrs = " + nyrs);

	GeneralPanel._year[1].setSelectedIndex(_stop.getSelectedIndex() + nyrs);
	System.out.println("_stop = " + _stop.getSelectedIndex());
	System.out.println("_year = " + GeneralPanel._year[1].getSelectedIndex());
	GeneralPanel._numberSteps.setText("POSITION ANALYSIS ON");
	GeneralPanel._numberSteps.setForeground(Color.red);
}

  /**
   *
   */
  private JPanel createLabel(String str) {
    JLabel label = new JLabel("              "+str+"      ");
    label.setHorizontalAlignment(SwingConstants.RIGHT);
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    panel.add(label);
    panel.setBackground(new Color(207,220,200));
    return panel;
  }
  /**
   *
   */
  private JPanel createCheckBoxPanel(int type) {
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    _check[type] = new JCheckBox("",false);
    panel.add(_check[type]);
    panel.setBackground(new Color(207,220,200));
    return panel;
  }
  /**
   *
   */
  private JPanel createComboBoxPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    _solverRepList = new JComboBox(listString);
    panel.add(_solverRepList);
    panel.setBackground(new Color(207,220,200));
    return panel;
  }
  /**
   *
   */
  private JPanel createTextPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(FlowLayout.CENTER));
    _addXaOptions = new JTextField(24);
    panel.add(_addXaOptions);
    panel.setBackground(new Color(207,220,200));
    return panel;
  }

  public void enablePositionComponents(boolean on) {
		if (on) {
			_months.setEnabled(true);
			_nper.setEnabled(true);
			_start.setEnabled(true);
			_stop.setEnabled(true);
			GeneralPanel.enableDateBoxes(false);
      //GeneralPanel._month[0].setSelectedItem(_months.getSelectedItem());
      //GeneralPanel._month[1].setSelectedIndex(11);
      //GeneralPanel._year[0].setSelectedItem(_start.getSelectedItem());
      //GeneralPanel._year[1].setSelectedItem(_stop.getSelectedItem());
		} else {
			_months.setEnabled(false);
			_nper.setEnabled(false);
			_start.setEnabled(false);
			_stop.setEnabled(false);
			GeneralPanel.enableDateBoxes(true);
		}
	}


  /**
   * Set the current study
   */
  public void setStudy(Study study) {
    _check[0].setSelected(study.getSolverReportOption().booleanValue());
    _solverRepList.setSelectedItem(study.getSolverReportList());
    _check[1].setSelected(study.getSvReportOption().booleanValue());
    _check[2].setSelected(study.getSvReportSaveOption().booleanValue());
    _check[3].setSelected(study.getSlackReportOption().booleanValue());
    _check[4].setSelected(study.getSlackReportSaveOption().booleanValue());
    _check[5].setSelected(study.getDssDebugOption().booleanValue());
    _check[6].setSelected(study.getDssSaveOption().booleanValue());
    _addXaOptions.setText(study.getAddXaOptions());
    _check[8].setSelected(study.getWsiDiOption().booleanValue());
    _check[9].setSelected(study.getUseRestartOption().booleanValue());
    _check[10].setSelected(study.getGenerateRestartOption().booleanValue());
    _check[11].setSelected(study.getPosAnalysisOption().booleanValue());
    //_check[12].setSelected(study.getGenNewRandomTableOption().booleanValue());
    if (_check[11].isSelected()) enablePositionComponents(true);
    else enablePositionComponents(false);
    _months.setSelectedItem(study.getStartMonth());
    _start.setSelectedItem(GeneralPanel._year[0].getSelectedItem());
    _stop.setSelectedItem(GeneralPanel._year[1].getSelectedItem());
  }
  /**
   * Update the study
   */
  public void updateStudy(Study study) {
    String tmp = new String();
    study.setSolverReportOption(new Boolean(_check[0].isSelected()));
    study.setSolverReportList((String) _solverRepList.getSelectedItem());
    study.setSvReportOption(new Boolean(_check[1].isSelected()));
    study.setSvReportSaveOption(new Boolean(_check[2].isSelected()));
    study.setSlackReportOption(new Boolean(_check[3].isSelected()));
    study.setSlackReportSaveOption(new Boolean(_check[4].isSelected()));
    study.setDssDebugOption(new Boolean(_check[5].isSelected()));
    study.setDssSaveOption(new Boolean(_check[6].isSelected()));

    tmp = GuiUtils.removeEOLChars(_addXaOptions.getText());
		if (_check[0].isSelected()) tmp = "MUTE NO LISTINPUT NO " + tmp;
		study.setAddXaOptions(tmp);

    study.setWsiDiOption(new Boolean(_check[8].isSelected()));
    //study.setUseRestartOption(new Boolean(_check[9].isSelected()));
    //study.setGenerateRestartOption(new Boolean(_check[10].isSelected()));
    study.setUseRestartOption(new Boolean(false));
    study.setGenerateRestartOption(new Boolean(false));
    study.setPosAnalysisOption(new Boolean(_check[11].isSelected()));
  }
  /**
   * Get LF90 Error Output Option
   */
  public boolean getGenRandomTableOption() {
	    return _check[12].isSelected();
	  }
  public boolean getLF90OutputOption() {
    return _check[7].isSelected();
  }
  public boolean getGenNewRandomTableOption() {
	    return _check[12].isSelected();
  }
  /**
   *
   */
  private JCheckBox[] _check;
  private JComboBox _solverRepList;
  private JTextField _addXaOptions;
	public static JComboBox _months = new JComboBox(GeneralPanel.months);
	public static JTextField _nper = new JTextField(5);
	public static JComboBox _start = new JComboBox(AppUtils.getYearArray());
	public static JComboBox _stop = new JComboBox(AppUtils.getYearArray());

}
