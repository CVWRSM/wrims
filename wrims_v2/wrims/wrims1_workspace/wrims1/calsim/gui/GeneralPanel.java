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
import vista.gui.*;

import java.awt.*;
import java.awt.event.*;
//import java.io.*;
import java.util.*;
import javax.swing.*;
//import javax.swing.border.*;
/**
 * The panel for the general tab.
 *
 * @author Yan-Ping Zuo
 * @version ID: GeneralPanel.java, v 0.7 beta, 07/13/99
	Last change:  AM   23 Sep 1999    3:13 pm
	Changed: 01 Mar 2001 by DJE
 * @version $Id: GeneralPanel.java,v 1.1.2.10.2.1 2002/05/02 01:01:41 adraper Exp $
 * @version $Id: GeneralPanel.java,v 1.1.2.10.2.1 2002/05/02 01:01:41 adraper Exp $
 */

public class GeneralPanel extends JPanel {
  public static boolean DEBUG = true;
  public static int MAX_NUM_SEQUENCES = 99; //CB added  (IF > 99, NEED TO CHANGE FORTRAN TOO)
  //public static	String optionList[] = {"None","SLP", "LGP", "WGP"};
  public static	String optionList[] = {"SLP"};
  public static	String svdvfileAPartList[] = {"CalSim","CalLite"};
//CB  public static	String seqList[] = {"1","2","3","4","5","6","7","8","9","10",
//CB  									"11","12","13","14","15","16","17","18","19","20"};
  public static	String seqList[] = createSequenceList(); //CB added

  public static String[] months = { "OCT",
				    "NOV",
				    "DEC",
				    "JAN",
				    "FEB",
				    "MAR",
				    "APR",
				    "MAY",
				    "JUN",
				    "JUL",
				    "AUG",
				    "SEP"
  };
  //DJE**************************************
  public static String[] timeSteps = {"1DAY","1MON"};
//*********************************************************************

  /**
   * constructor
   */
  public GeneralPanel() {
    setLayout(new BorderLayout());
    _fileText = new JTextField[5];
    _entryText = new JTextField[6];
    _month = new JComboBox[2];
    _year = new JComboBox[2];
    //DJE******************************
    _day = new JComboBox[2];
    dl = new DayItemListener[2];
    ml = new MYItemListener[2];
    yl = new MYItemListener[2];
    //******************************************************
    add(createLabelPanel(), BorderLayout.WEST);
    add(createAttribPanel(), FlowLayout.CENTER);
    add(createImagePanel(),BorderLayout.EAST);
  }

  /**
   * CB added for ease of creation for large number of sequences.
   * @return
   */
	static String[] createSequenceList() {
		String[] sequenceList = new String[MAX_NUM_SEQUENCES];
		for (int i = 1; i <= sequenceList.length; ++i) {
			sequenceList[i - 1] = String.valueOf(i);
		}
		return sequenceList;
	}

  /**
   * Create the Label panel
   */
  JPanel createLabelPanel(){
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(14,1));
    panel.add(createLabel("Study Name:"));
    panel.add(createLabel("Author:"));
    panel.add(createLabel("Date:"));
    panel.add(createLabel("Description:"));
    //    panel.add(createLabel("Hydrology Version:"));
    //    panel.add(createLabel("Working Directory:"));
    panel.add(createLabel("WRESL File:"));
    panel.add(createLabel("SV File:"));
    panel.add(createLabel("SV File A Part:"));
    panel.add(createLabel("DV File:"));
    panel.add(createLabel("Init File:"));
    panel.add(createLabel("Init File F Part:"));
    //DJE*************************************
    panel.add(createLabel("Time Step:"));
    //****************************************
    panel.add(createLabel("Start Date:"));
    panel.add(createLabel("Stop Date:"));
    panel.add(createLabel("Sim Option:"));
    panel.setBackground(new Color(229,240,203));
    return panel;
  }
  /*	private JLabel createLabel(String str) {
	JLabel label = new JLabel("        "+str+"      ");
	label.setHorizontalAlignment(SwingConstants.RIGHT);
	return label;
	}*/
  private JPanel createLabel(String str) {
    JLabel label = new JLabel("              "+str+"      ");
    label.setHorizontalAlignment(SwingConstants.RIGHT);
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    panel.add(label);
    panel.setBackground(new Color(229,240,203));
    return panel;
  }
  /**
   * Create the Attibutes panel
   */
  JPanel createAttribPanel(){
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(14,1));
    panel.add(createTextPanel(0));//name
    panel.add(createTextPanel(1));//author
    panel.add(createTextPanel(2));//date
    panel.add(createDescPanel());	//description
    //		panel.add(createTextPanel(3));//hydrology
    //		panel.add(createFilePanel(4));//directory
    panel.add(createFilePanel(0));//wresl file
    panel.add(createFilePanel(1));//sv file
    panel.add(createSvFileAPartOptionFPartPanel());// file A part 
    panel.add(createFilePanel(2));//dv file
    panel.add(createFilePanel(3));//init file
    panel.add(createTextPanel(4));// init file f part
    //DJE*********************************************
    panel.add(createTimeStepPanel());//time step
    //************************************************
    panel.add(createDatePanel(0));//start date
    panel.add(createDatePanel(1));//end date
    panel.add(createSimOptionPanel());//sim options
    panel.setBackground(new Color(229,240,203));
    return panel;
  }
  
  JPanel createImagePanel(){
	    JPanel panel = new JPanel();
	    panel.setLayout(new BorderLayout());
	    ImageIcon icon1 = new ImageIcon(VistaUtils.getImageAsBytes("/calsim/graphics/aqueduct9707.jpg")); 
	    JLabel label1 = new JLabel("  "); 
	    label1.setIcon(icon1); 
	    label1.setHorizontalAlignment(SwingConstants.CENTER);
	    label1.setVerticalAlignment(SwingConstants.CENTER);
	    panel.add(label1, BorderLayout.NORTH); 
	    ImageIcon icon2 = new ImageIcon(VistaUtils.getImageAsBytes("/calsim/graphics/dwr_logo.jpg")); 
	    JLabel label2 = new JLabel(); 
	    label2.setIcon(icon2); 
	    label2.setHorizontalAlignment(SwingConstants.CENTER);
	    label2.setVerticalAlignment(SwingConstants.CENTER);
	    panel.add(label2, BorderLayout.CENTER);
	    panel.setBackground(new Color(229,240,203));
	    return panel;
  }
  
  JPanel createFilePanel(int type) {
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    _fileText[type] = new JTextField(43);
    panel.add(_fileText[type]);
    JButton button = createFileChooser(type);
    panel.add(button);
    panel.setBackground(new Color(229,240,203));
    return panel;
  }
  JPanel createDescPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    _desc = new JTextArea(2,50);
    _desc.setLineWrap(true);
    _desc.setWrapStyleWord(true);
    JScrollPane descScrollPane = new JScrollPane(_desc);
    descScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    panel.add(descScrollPane);
    panel.setBackground(new Color(229,240,203));
    return panel;
  }

  //DJE***************************************************
  JPanel createTimeStepPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    _timeStep = new JComboBox(timeSteps);
    _timeStep.setSelectedItem("1MON");
    _timeStep.addItemListener(new TSItemListener());
    _numberSteps = new JLabel();
    _numberSteps.setText(getNumberSteps());
    panel.add(_timeStep);
    panel.add(new JLabel("     Number of Time Steps: "));
    panel.add(_numberSteps);
    panel.setBackground(new Color(229,240,203));
    return panel;
  }
  //******************************************************

  JPanel createDatePanel(int type) {
    int initYear;
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    //DJE******************************************************
    _day[type] = new JComboBox();
    for (int d=1;d<=getDaysInMonth(_intMonth[type],_intYear[type]);d++) {
        _day[type].addItem(new Integer(d).toString());
    }
    _day[type].setSelectedIndex(_intDay[type]-1);
    _day[type].setEnabled(false);
    _day[type].addItemListener(dl[type] = new DayItemListener(type));
    //****************************************************************
    _month[type] = new JComboBox(months);
    //DJE*****************************************************
    _month[type].setSelectedIndex(_intMonth[type]-1);
    _month[type].addItemListener(ml[type] = new MYItemListener(type));
    //**********************************************************
    _year[type] = new JComboBox(AppUtils.getYearArray());
    //DJE***************************************************
    initYear = _intYear[type];
    if (_intMonth[type] < 4) initYear--;
    _year[type].setSelectedItem(new Integer(initYear).toString());
    _year[type].addItemListener(yl[type] = new MYItemListener(type));
    _year[type].setEditable(true);
    panel.add(_year[type]);
    panel.add(new JLabel(" Year     "));
    panel.add(_month[type]);
    panel.add(new JLabel(" Month     "));
    panel.add(_day[type]);
    panel.add(new JLabel(" Day"));
    //********************************************************
    panel.setBackground(new Color(229,240,203));
    return panel;
  }
  JPanel createTextPanel(int type) {
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    _entryText[type] = new JTextField(52);
    panel.add(_entryText[type]);
    panel.setBackground(new Color(229,240,203));
    return panel;
  }
  JPanel createTextPanelShort(int type) {
	    JPanel panel = new JPanel();
	    panel.setLayout(new FlowLayout(FlowLayout.LEFT));
	    _entryText[type] = new JTextField(32);
	    panel.add(_entryText[type]);
	    panel.setBackground(new Color(229,240,203));
	    return panel;
	  }
  JPanel createSimOptionPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    _simOption = new JComboBox(optionList);
    _simOption.setSelectedItem("SLP");
    _numSeq = new JComboBox(seqList);
    panel.add(_simOption);
    panel.add(new JLabel("     # Sequences  "));
    panel.add(_numSeq);
    panel.setBackground(new Color(229,240,203));
    return panel;
  }

  JPanel createSvFileAPartOptionFPartPanel() {
	    JPanel panel = new JPanel();
	    panel.setLayout(new FlowLayout(FlowLayout.LEFT));
	    _svdvFileAPartOption = new JComboBox(svdvfileAPartList);
	    _svdvFileAPartOption.setSelectedItem("CalSim");
	    panel.add(_svdvFileAPartOption);
	    panel.add(new JLabel("     SV file F Part:  "));
	    panel.add(createTextPanelShort(5));// sv file f part
	    panel.setBackground(new Color(229,240,203));
	    return panel;
	  }
  /**
   * Create the file choose button
   */
  JButton createFileChooser(final int n){
    JButton chooser = new JButton("Choose");
    chooser.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
				chooseFile(n);
      }
    });
    return chooser;
  }

  /**
   * Event handler of the choose button.  Open a file dialog box, display the selected file in the
   * file name text field.
   */
  void chooseFile(int type){
    String extension = "";
    if (type == 0) extension = "wresl";
    else if (type == 1) extension = "sv.dss";
//CB    else if (type == 2) extension = "dv.dss";  now dv files are often named differently
    else if (type == 2) extension = "dss";
    else if (type == 3) extension = "dss";
    else if (type == 4) extension = "";
    String fileName = VistaUtils.getFilenameFromDialog(this, FileDialog.LOAD, extension, extension +" File");
    if (fileName != null) _fileText[type].setText(fileName);
  }

  //DJE*******************************************************************
  private class TSItemListener implements ItemListener {
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange()==ItemEvent.SELECTED) {
            _strTimeStep = e.getItem().toString();
            if (_strTimeStep.equalsIgnoreCase("1MON")) {
                for (int i = 0; i < 2; i++) {
                    _intDay[i] = getDaysInMonth(_intMonth[i],_intYear[i]);
                    _day[i].removeItemListener(dl[i]);
                    _day[i].setSelectedIndex(_intDay[i]-1);
                    _day[i].addItemListener(dl[i]);
                    _day[i].setEnabled(false);
                }
            }else {
                for (int i = 0; i < 2; i++) _day[i].setEnabled(true);
            }
            _numberSteps.setText(getNumberSteps());
        }
    }
  }

  private int getDaysInMonth(int month,int year) {
    int daysInMonth;
    int daysArray[] = {31,30,31,31,28,31,30,31,30,31,31,30};
    daysInMonth = daysArray[month-1];
    if (leapYear(year) && month == 5) daysInMonth = 29;
    return daysInMonth;
  }

  private boolean leapYear(int year) {
    if (Math.IEEEremainder(year,4)==0 &&
    (Math.IEEEremainder(year,100)!= 0 || Math.IEEEremainder(year,400)==0)) {
        return true;
    }else {
        return false;
    }
  }

  private String getNumberSteps() {
    int numberSteps = 0;
    if (_intYear[0] > _intYear[1] ||
        (_intYear[0] == _intYear[1] &&
        (_intMonth[0] > _intMonth[1] ||
        (_intMonth[0] == _intMonth[1] &&
        _intDay[0] > _intDay[1])))) {
            numberSteps = 0;
        }
    else if (_strTimeStep.equalsIgnoreCase("1MON")){
        numberSteps = (_intMonth[1]-_intMonth[0] + 1) + (_intYear[1] - _intYear[0])*12;
    } else {
        if (_intYear[0]==_intYear[1]) {
            if (_intMonth[0]==_intMonth[1]) {
                numberSteps = _intDay[1]-_intDay[0]+1;
            } else {
                numberSteps = getDaysInMonth(_intMonth[0],_intYear[0])-_intDay[0]+1;
                for (int m = _intMonth[0]+1; m < _intMonth[1]; m++) {
                    numberSteps += getDaysInMonth(m,_intYear[0]);
                }
                numberSteps += _intDay[1];
            }
        } else {
            numberSteps = getDaysInMonth(_intMonth[0],_intYear[0])-_intDay[0]+1;
            for (int m = _intMonth[0]+1; m < 13; m++) {
                numberSteps += getDaysInMonth(m,_intYear[0]);
            }
            for (int y = _intYear[0]+1; y < _intYear[1]; y++) {
                numberSteps += 365;
                if (leapYear(y)) numberSteps++;
            }
            for (int m = 1; m < _intMonth[1]; m++) {
                numberSteps += getDaysInMonth(m,_intYear[1]);
            }
            numberSteps += _intDay[1];
        }
    }
    if (numberSteps == 0) {
        _numberSteps.setForeground(Color.red);
        return "NONE";
    }
    else {
        _numberSteps.setForeground(Color.black);
        return new Integer(numberSteps).toString();
    }
  }

  private class DayItemListener implements ItemListener {
    int type;
    public DayItemListener(int i) {
        type = i;
    }
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange()==ItemEvent.SELECTED) {
            _intDay[type] = new Integer(e.getItem().toString()).intValue();
            _numberSteps.setText(getNumberSteps());
        }
    }
  }

  private class MYItemListener implements ItemListener {
    int type;
    public MYItemListener(int i) {
        type = i;
    }
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange()==ItemEvent.SELECTED) {
            _intMonth[type] = _month[type].getSelectedIndex() + 1;
            _intYear[type] = new Integer(_year[type].getSelectedItem().toString()).intValue();
            if (_intMonth[type] < 4) _intYear[type]++;
            _day[type].removeItemListener(dl[type]);
            for (int d = _day[type].getItemCount();
            d > getDaysInMonth(_intMonth[type],_intYear[type]); d--) {
                _day[type].removeItemAt(d-1);
            }
            for (int d = _day[type].getItemCount();
            d < getDaysInMonth(_intMonth[type],_intYear[type]); d++) {
                _day[type].addItem(new Integer(d+1).toString());
            }
            if (_strTimeStep.equalsIgnoreCase("1MON")) {
                _day[type].setSelectedIndex(getDaysInMonth(_intMonth[type],_intYear[type])-1);
            }
            _intDay[type] = _day[type].getSelectedIndex() + 1;
            _day[type].addItemListener(dl[type]);
            _numberSteps.setText(getNumberSteps());
            //Added to conform with Joel's OptionPanel
           // OptionPanel._months.setSelectedItem(_month[0].getSelectedItem());
           // OptionPanel._start.setSelectedItem(_year[0].getSelectedItem());
            //OptionPanel._stop.setSelectedItem(_year[1].getSelectedItem());
        }
    }
  }
  //*******************************************************************************
  /**
   * Set the current study
   */
  public void setStudy(Study study) {
    _entryText[0].setText(study.getName());
    _entryText[1].setText(study.getAuthor());
    _entryText[2].setText(study.getDate().toString());
    _desc.setText(study.getDescription());
    //    _entryText[3].setText(study.getHydrologyVersion());
    //    _fileText[4].setText(study.getStudyDir());
    _fileText[0].setText(study.getWreslFile());
    _fileText[1].setText(study.getSvFile());
    _svdvFileAPartOption.setSelectedItem(study.getSvDvFileAPartOption());    
    _entryText[5].setText(study.getSvFileFPart());
    _fileText[2].setText(study.getDvFile());
    _fileText[3].setText(study.getInitFile());
    _entryText[4].setText(study.getInitFileFPart());
    _year[0].setSelectedItem(study.getStartYear().toString());
    _month[0].setSelectedItem(study.getStartMonth());
    _year[1].setSelectedItem(study.getStopYear().toString());
    _month[1].setSelectedItem(study.getStopMonth());
    _simOption.setSelectedItem(study.getSimOption());
    _numSeq.setSelectedItem(study.getNumberSequences().toString());
    //DJE***********************************************************
    if (study.isUpdatedStudyObject()) {
        _timeStep.setSelectedItem(study.getTimeStep());//DJE
        _day[0].setSelectedItem(study.getStartDay());//DJE
        _day[1].setSelectedItem(study.getStopDay());//DJE
    }
    else {
        _timeStep.setSelectedItem("1MON");
        updateStudy(study);
    }
    //****************************************************************

  }
  /**
   * Update the study
   */
  public void updateStudy(Study study) {
    study.setName(_entryText[0].getText());
    study.setAuthor(_entryText[1].getText());
    study.setDate(new Date());
    study.setDescription(GuiUtils.removeEOLChars(_desc.getText()));
    //   study.setHydrologyVersion(_entryText[3].getText());
    //   study.setStudyDir(_fileText[4].getText());
    study.setWreslFile(_fileText[0].getText());
    study.setSvFile(_fileText[1].getText());
    study.setSvFileFPart(_entryText[5].getText());    
    study.setSvDvFileAPartOption(_svdvFileAPartOption.getSelectedItem().toString());     
    study.setDvFile(_fileText[2].getText());
    study.setInitFile(_fileText[3].getText());
    study.setInitFileFPart(_entryText[4].getText());
    //DJE**************************************************
    study.setTimeStep(_timeStep.getSelectedItem().toString());
    study.setNumberSteps(_numberSteps.getText());
    study.setStartDay(_day[0].getSelectedItem().toString());
    study.setStopDay(_day[1].getSelectedItem().toString());
    //*******************************************************
    study.setStartMonth(_month[0].getSelectedItem().toString());
    study.setStartYear(new Integer(_year[0].getSelectedItem().toString()));
    study.setStopMonth(_month[1].getSelectedItem().toString());
    study.setStopYear(new Integer(_year[1].getSelectedItem().toString()));
    study.setSimOption(_simOption.getSelectedItem().toString());   
    study.setNumberSequences(new Integer(_numSeq.getSelectedItem().toString()));
    study.updateStudyObject();//DJE************************
  }

  public static void enableDateBoxes(boolean enable) {

	  _timeStep.setEnabled(enable);
	  _month[0].setEnabled(enable);
	  _month[1].setEnabled(enable);
	  _year[0].setEnabled(enable);
	  _year[1].setEnabled(enable);
	}
  /**
   *
   */
  private JTextField[] _fileText, _entryText;
  //DJE***************************************************
  public static JLabel _numberSteps;
  private JComboBox _simOption,_numSeq,_svdvFileAPartOption;
  private String _strTimeStep = new String("1MON");
//  private TSItemListener tsl;
  //private JComboBox[] _month,_year,_day;
  private int[] _intMonth = {1,12};
  private int[] _intYear = {1922,2003};
  private int[] _intDay = {31,30};
  private DayItemListener[] dl;
  private MYItemListener[] ml,yl;
  //*******************************************************
  //Replaces commented line above for use in Joel's OptionPanel. **********
  public static JComboBox[] _month,_year;
  public static JComboBox _timeStep;
  private JComboBox[] _day;
  //*********************************************************

  private JTextArea _desc;
}
