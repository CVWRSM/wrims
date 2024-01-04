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
import vista.time.*;
//import vista.gui.*;
import java.awt.*;
import java.awt.event.*;
//import java.io.*;
import javax.swing.*;
import java.util.Vector;

/**
 * The message panel in the main panel of Calsim GUI
 *
 * @author Yan-Ping Zuo
 * @version $Id: MessagePanel.java,v 1.1.2.18 2001/10/23 16:28:43 jfenolio Exp $
 */

public class MessagePanel
{
  public static boolean DEBUG = false;

  public static String [] labelText = { " Project Name: ",
					" Mode:  ",
					" TW:  ",
					" Base Files ",
					" DV:    ",
					"               SV: ",
					" Comp Files 1",
					" DV:    ",
					"               SV: ",
					"           Units:  ",
					" Comp Files 2",
					" DV:    ",
					"               SV: ",
					" Comp Files 3",
					" DV:    ",
					"               SV: ",
					"  View:  "
					};

  /**
   * constructor
   */
  public MessagePanel(JFrame frame, MainPanel mp) {
    _frame = frame;
    _mp = mp;
    _messagePanelComp = createMessagePanel();
  }
  Font f;
  /**
   * create active messages panel
   */
  JPanel createMessagePanel() {
    int fontSize=10;
    int numOfMessages = 17,
      numOfPanels = 5;
    JPanel panel = new JPanel();
    JPanel panel1 = new JPanel();
//    panel1.setLayout(new GridLayout(0,1,1,1));
    JPanel [] panels = new JPanel[numOfPanels];
    labelNames = new JLabel[labelText.length];
    _messages = new JTextField[numOfMessages];
    for (int i=0; i<numOfPanels; i++) panels[i] = new JPanel();
    for (int i=0; i<labelText.length; i++){
      labelNames[i] = new JLabel( labelText[i]);
      f = labelNames[i].getFont();
      f = new Font(f.getName(),f.getStyle(),fontSize);
      labelNames[i].setFont(f);
      labelNames[i].setForeground(Color.black);
      if (i != 4 || i != 7 ) {
	    labelNames[i].setSize(10,10);
	   } else {
		labelNames[i].setSize(10,10);
	  }
    }
    for (int i=0; i< numOfMessages; i++) {
			_messages[i] = new JTextField(30);
			_messages[i].setBackground(Color.white);
			_messages[i].setEditable(false);
			Font f = _messages[i].getFont();
			f = new Font(f.getName(),f.getStyle(),fontSize);
			_messages[i].setFont(f);
			_messages[i].setMinimumSize(_messages[i].getPreferredSize());
			_messages[i].setHorizontalAlignment(JTextField.CENTER);
	  }
	  Dimension d = new Dimension(0,0);
	  _basedv.addActionListener(new GuiTaskListener( "Retrieving DV File..."){
      public void doWork(){
				MainMenuBar.openBaseDVFile();
      }
    });
	  _basedv.setFont(f);
	  _basedv.setMaximumSize(d);
	  _basesv.setFont(f);
	  _basesv.setSize(8,7);
	  _basesv.addActionListener(new GuiTaskListener( "Retrieving SV File..."){
      public void doWork(){
				MainMenuBar.openBaseSVFile();
      }
    });
	  _comp1dv.addActionListener(new GuiTaskListener( "Retrieving DV File..."){
      public void doWork(){
				MainMenuBar.openCompDVFile();
      }
    });
	  _comp1dv.setFont(f);
	  _comp1dv.setSize(8,7);
	  _comp1sv.setFont(f);
	  _comp1sv.setSize(8,7);
	  _comp1sv.addActionListener(new GuiTaskListener( "Retrieving SV File..."){
      public void doWork(){
				MainMenuBar.openCompSVFile();
      }
    });
	  _comp2dv.addActionListener(new GuiTaskListener( "Retrieving DV File..."){
      public void doWork(){
				MainMenuBar.openComp2DVFile();
      }
    });
	  _comp2dv.setFont(f);
	  _comp2dv.setSize(8,7);
	  _comp2sv.setFont(f);
	  _comp2sv.setSize(8,7);
	  _comp2sv.addActionListener(new GuiTaskListener( "Retrieving SV File..."){
      public void doWork(){
				MainMenuBar.openComp2SVFile();
      }
    });
	  _comp3dv.addActionListener(new GuiTaskListener( "Retrieving DV File..."){
      public void doWork(){
				MainMenuBar.openComp3DVFile();
      }
    });
	  _comp3dv.setFont(f);
	  _comp3dv.setSize(8,7);
	  _comp3sv.setFont(f);
	  _comp3sv.setSize(8,7);
	  _comp3sv.addActionListener(new GuiTaskListener( "Retrieving SV File..."){
      public void doWork(){
				MainMenuBar.openComp3SVFile();
      }
    });
	  _basebox.addItemListener(al1);
	  _comp1box.addItemListener(al1);
	  _comp2box.addItemListener(al1);
    _comp3box.addItemListener(al1);
	  JLabel name = new JLabel(" Dts Tree: ");
	  Font f = _messages[0].getFont();
	  name.setFont(new Font(f.getName(),f.getStyle(),10));
    //
    setModeMessage(AppUtils.BASE);
    plot.setSelected(AppUtils.viewGraph);
    table.setSelected(AppUtils.viewTable);
    monthly.setSelected(AppUtils.viewMonthlyTable);
    _basebox.setSelected(AppUtils.baseOn);
    _comp1box.setSelected(AppUtils.comp1On);
    _comp2box.setSelected(AppUtils.comp2On);
    _comp3box.setSelected(AppUtils.comp3On);
    setTimeWindowMessage("");
    //
    GridBagLayout gb = new GridBagLayout();
    GridBagConstraints gc = new GridBagConstraints();
    panel.setLayout(gb);
    gc.anchor = GridBagConstraints.NORTHWEST;
    //gc.insets = new Insets(0,0,5,5);
    gc.gridx = 0;
    gc.gridy = 0;
    panel.add(labelNames[0],gc);

    gc.gridx = 1;
    //gc.gridwidth = GridBagConstraints.RELATIVE;;
    gc.insets = new Insets(0,2,0,3);
    panel.add(new JLabel("   "),gc);

    gc.gridx = 2;
    //gc.gridwidth = GridBagConstraints.RELATIVE;;
    panel.add(new JLabel("     "),gc);

    gc.gridx = 3;
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    gc.weightx = 1.0;
    panel.add(_messages[0],gc);

    gc.gridx = 4;
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    panel.add(name,gc);

    gc.gridx = 5;
    //gc.gridwidth = GridBagConstraints.REMAINDER;
    gc.weightx = 1.0;
    panel.add(_messages[9],gc);

    gc.anchor = GridBagConstraints.WEST;
    gc.gridx = 0;
    gc.gridy = 1;
    panel.add(labelNames[3],gc);

    gc.gridx = 1;
    gc.insets = new Insets(0,2,0,3);
    //gc.gridwidth = GridBagConstraints.RELATIVE;;
    panel.add(_basebox,gc);

    gc.gridx = 2;
    //gc.gridwidth = GridBagConstraints.RELATIVE;;
    panel.add(_basedv,gc);

    gc.gridx = 3;
    //gc.gridwidth = GridBagConstraints.RELATIVE;;
    gc.weightx = 1.0;
    panel.add(_messages[3],gc);

    gc.gridx = 4;
    gc.insets = new Insets(0,2,0,3);
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    panel.add(_basesv,gc);

    gc.gridx = 5;
    //gc.gridwidth = GridBagConstraints.REMAINDER;
    gc.weightx = 1.0;
    panel.add(_messages[4],gc);

    gc.anchor = GridBagConstraints.WEST;
    gc.gridx = 0;
    gc.gridy = 2;
    panel.add(labelNames[6],gc);

    gc.gridx = 1;
    gc.insets = new Insets(0,2,0,3);
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    panel.add(_comp1box,gc);

    gc.gridx = 2;
    //gc.gridwidth = GridBagConstraints.RELATIVE;;
    panel.add(_comp1dv,gc);

    gc.gridx = 3;
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    gc.weightx = 1.0;
    panel.add(_messages[5],gc);

    gc.gridx = 4;
    gc.insets = new Insets(0,2,0,3);
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    panel.add(_comp1sv,gc);

    gc.gridx = 5;
    gc.gridwidth = GridBagConstraints.REMAINDER;
    gc.weightx = 1.0;
    panel.add(_messages[6],gc);

    gc.anchor = GridBagConstraints.WEST;
    gc.gridx = 0;
    gc.gridy = 3;
    panel.add(labelNames[10],gc);

    gc.gridx = 1;
    gc.insets = new Insets(0,2,0,2);
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    panel.add(_comp2box,gc);

    gc.gridx = 2;
    //gc.gridwidth = GridBagConstraints.RELATIVE;;
    panel.add(_comp2dv,gc);

    gc.gridx = 3;
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    gc.weightx = 1.0;
    panel.add(_messages[10],gc);

    gc.gridx = 4;
    gc.insets = new Insets(0,2,0,2);
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    panel.add(_comp2sv,gc);

    gc.gridx = 5;
    gc.gridwidth = GridBagConstraints.REMAINDER;
    gc.weightx = 1.0;
    panel.add(_messages[11],gc);

    gc.anchor = GridBagConstraints.WEST;
    gc.gridx = 0;
    gc.gridy = 4;
    panel.add(labelNames[13],gc);

    gc.gridx = 1;
    gc.insets = new Insets(0,2,0,2);
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    panel.add(_comp3box,gc);

    gc.gridx = 2;
    //gc.gridwidth = GridBagConstraints.RELATIVE;;
    panel.add(_comp3dv,gc);

    gc.gridx = 3;
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    gc.weightx = 1.0;
    panel.add(_messages[12],gc);

    gc.gridx = 4;
    gc.insets = new Insets(0,2,0,2);
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    panel.add(_comp3sv,gc);

    gc.gridx = 5;
    gc.gridwidth = GridBagConstraints.REMAINDER;
    gc.weightx = 1.0;
    panel.add(_messages[13],gc);
    
    panel.setBackground(new Color(229,240,203));

    /*
    gc.anchor = GridBagConstraints.WEST;
    gc.gridx = 0;
    gc.gridy = 3;
    panel.add(labelNames[9],gc);

    gc.gridx = 1;
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    panel.add(createUnitsPanel(),gc);

    gc.gridx = 2;
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    gc.weightx = 1.0;
    panel.add(labelNames[1],gc);

    gc.gridx = 3;
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    gc.weightx = 1.0;
    panel.add(createModePanel(),gc);

    gc.gridx = 4;
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    panel.add(labelNames[2],gc);

    gc.gridx = 5;
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    gc.weightx = 1.0;
    panel.add(createTWBox(),gc);

    gc.gridx = 6;
    //gc.gridwidth = GridBagConstraints.RELATIVE;
    gc.weightx = 1.0;
    panel.add(labelNames[16],gc);

    gc.gridx = 7;
    //gc.gridwidth = GridBagConstraints.REMAINDER;
    gc.weightx = 1.0;
    panel.add(createViewPanel(),gc);
    */

    //panel.setLayout(new GridLayout(numOfPanels,1));
    //for (int i=0; i< numOfPanels; i++)
      //panel.add(panels[i]);
    panel1.setBorder(
		    BorderFactory.
		    createTitledBorder(
				       BorderFactory.createMatteBorder(1,1,1,1,Color.blue),
				       "Message Panel")
		    );
    panel1.setLayout(new GridBagLayout());
    panel1.setBackground(new Color(229,240,203));
    gc.anchor = GridBagConstraints.NORTHWEST;
    gc.gridx = 0;
    gc.gridy = 0;
    panel1.add(panel,gc);
    gc.anchor = GridBagConstraints.WEST;
    gc.insets = new Insets(3,0,0,0);
    gc.gridx = 0;
    gc.gridy = 1;
    panel1.add(createUtilsPanel(),gc);
    NodeArcMenuBar nodeArcMenuBar = new NodeArcMenuBar(_mp);
		_frame.setJMenuBar(nodeArcMenuBar.getMenuBar());
    //bPanel.add(panel,BorderLayout.CENTER);
    return panel1;
  }
  /**
   *
   */
  private void addInLine(Container mc, Component [] compArray){
		mc.setLayout(new BoxLayout(mc,BoxLayout.X_AXIS));
		for(int i=0; i < compArray.length; i++){
	    mc.add(compArray[i]); mc.add(Box.createHorizontalGlue());
		}
		mc.add(Box.createVerticalStrut(6));
  }

  public JPanel createUtilsPanel() {
		JPanel panel = new JPanel();
    GridBagLayout gb = new GridBagLayout();
    GridBagConstraints gc = new GridBagConstraints();
    panel.setLayout(gb);
    gc.anchor = GridBagConstraints.WEST;
    gc.gridx = 0;
    gc.gridy = 0;
    panel.add(labelNames[1],gc);
    gc.gridx = 1;
		panel.add(createModePanel(),gc);
		gc.gridx = 2;
		panel.add(labelNames[16],gc);
    gc.gridx = 3;
    panel.add(createViewPanel(),gc);
    gc.gridx = 4;
    panel.add(new JLabel("              "),gc);
    gc.gridx = 5;
    panel.add(labelNames[2],gc);
    gc.gridx = 6;
    panel.add(createTWBox(),gc);
    gc.gridx = 7;
    panel.add(labelNames[9],gc);
    gc.gridx = 8;
    panel.add(createUnitsPanel(),gc);
    panel.setBackground(new Color(229,240,203));
    return panel;
	}

  public JPanel createUnitsPanel() {
		JPanel panel = new JPanel();
		ButtonGroup g = new ButtonGroup();
		g.add(taf); g.add(cfs);
		taf.setBackground(new Color(229,240,203));
		cfs.setBackground(new Color(229,240,203));
		taf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (DEBUG) System.out.println("TAF");
				if (taf.isSelected()) AppUtils.useUnits(AppUtils.TAF);
			}
		});
		cfs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (DEBUG) System.out.println("CFS");
				if (cfs.isSelected()) AppUtils.useUnits(AppUtils.CFS);
			}
		});
		taf.setFont(new Font(f.getName(),f.getStyle(),10));
		cfs.setFont(new Font(f.getName(),f.getStyle(),10));
		panel.setPreferredSize(new Dimension(100,12));
		panel.setLayout(new GridLayout(1,0));
		panel.add(taf); panel.add(cfs);
	    panel.setBackground(new Color(229,240,203));
		return panel;
	}

	public JPanel createModePanel() {
		JPanel panel = new JPanel();
		ButtonGroup g = new ButtonGroup();
		g.add(base); g.add(comp); g.add(diff);
		base.setBackground(new Color(229,240,203));
		comp.setBackground(new Color(229,240,203));
		diff.setBackground(new Color(229,240,203));
		base.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					AppUtils.plotComparitive = false;
					AppUtils.plotDifference = false;
				}
				if (DEBUG) {System.out.println("base");System.out.println(AppUtils.plotComparitive);System.out.println(AppUtils.plotDifference);}
			}
		});
		comp.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					AppUtils.plotComparitive = true;
					AppUtils.plotDifference = false;
				}
				if (DEBUG) {System.out.println("comp");System.out.println(AppUtils.plotComparitive);System.out.println(AppUtils.plotDifference);}
			}
		});
		diff.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					AppUtils.plotComparitive = false;
					AppUtils.plotDifference = true;
				}
				if (DEBUG) {System.out.println("diff");System.out.println(AppUtils.plotComparitive);System.out.println(AppUtils.plotDifference);}
			}
		});
		base.setFont(new Font(f.getName(),f.getStyle(),10));
		comp.setFont(new Font(f.getName(),f.getStyle(),10));
		diff.setFont(new Font(f.getName(),f.getStyle(),10));
		panel.setPreferredSize(new Dimension(150,12));
		panel.setLayout(new GridLayout(1,0));
		panel.add(base); panel.add(comp); panel.add(diff);
	    panel.setBackground(new Color(229,240,203));
		return panel;
	}

	public JComboBox createTWBox() {
    String [] twSelections = {
		"OCT1921 - SEP2003",
		"OCT1921 - SEP1994",
		"MAY1928 - OCT1934",
		"JUN1986 - SEP1992",
		"OCT1975 - SEP1977",
		"OCT1983 - SEP1993",
		};
    for (int i=0; i<twSelections.length; i++) _twitems.addElement(twSelections[i]);
    JComboBox twbox = new JComboBox(_twitems);
    twbox.setEditable(true);
    twbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox tb = (JComboBox)e.getSource();
				String tw = (String)tb.getSelectedItem();
				if (DEBUG) System.out.println(tw);
				AppUtils.getCurrentProject().setTimeWindow(tw);
			}
		});
		Dimension d = new Dimension(350,17);
		twbox.setMinimumSize(d);
		twbox.setFont(new Font(f.getName(),f.getStyle(),10));
		return twbox;
	}

	public JPanel createViewPanel() {
		JPanel panel = new JPanel();
		plot.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (DEBUG) System.out.println("Plot Selected");
					AppUtils.viewGraph = true;
				} else {
					if (DEBUG) System.out.println("Plot Deselected");
					AppUtils.viewGraph = false;
				}
			}
		});
		table.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (DEBUG) System.out.println("Table Selected");
					AppUtils.viewTable = true;
				} else {
					if (DEBUG) System.out.println("Table Deselected");
					AppUtils.viewTable = false;
				}
			}
		});
		monthly.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (DEBUG) System.out.println("Monthly Table Selected");
					AppUtils.viewMonthlyTable = true;
				} else {
					if (DEBUG) System.out.println("Monthly Table Deselected");
					AppUtils.viewMonthlyTable = false;
				}
			}
		});
		plot.setFont(new Font(f.getName(),f.getStyle(),10));
		table.setFont(new Font(f.getName(),f.getStyle(),10));
		monthly.setFont(new Font(f.getName(),f.getStyle(),10));
		plot.setBackground(new Color(229,240,203));
		table.setBackground(new Color(229,240,203));
		monthly.setBackground(new Color(229,240,203));
		panel.setPreferredSize(new Dimension(165,12));
		panel.setLayout(new GridLayout(1,0));
		panel.add(plot); panel.add(table); panel.add(monthly);
		return panel;
	}
  /**
   * Update message panel in main gui
   */
  void updateMessagePanel(){
    setTimeWindowMessage(getTimeWindowString());
    //setModeMessage(getModeString());
    setProjectNameMessage(getProjectName());
    setDVFileMessage(getDVFilename());
    setSVFileMessage(getSVFilename());
    setDV2FileMessage(getDV2Filename());
    setSV2FileMessage(getSV2Filename());
    setDV3FileMessage(getDV3Filename());
    setSV3FileMessage(getSV3Filename());
    setDV4FileMessage(getDV4Filename());
    setSV4FileMessage(getSV4Filename());
    //    setUnitsMessage("As Stored");
    setUnitsMessage(getUnits());
    setDtsMasterMessage(getDtsFromProject());
  }

  /**
   * sets time window messages
   */
  void setTimeWindowMessage(String str){
    _messages[2].setText(str);
  }
  /**
   *
   */
  void setModeMessage(String str){
    if (str.equals(AppUtils.BASE)) {
			base.setSelected(true);
		} else if (str.equals(AppUtils.COMP)) {
			base.setSelected(true);
		} else if (str.equals(AppUtils.DIFF)) {
			diff.setSelected(true);
		}
    //_messages[1].setText(str);
  }
  /**
   *
   */
  void setProjectNameMessage(String str){
    _messages[0].setText(str);
  }
  /**
   *
   */
  void setDVFileMessage(String str){
    _messages[3].setText(str);
  }
  /**
   *
   */
  void setSVFileMessage(String str){
    _messages[4].setText(str);
  }
  /**
   *
   */
  void setDV2FileMessage(String str){
    _messages[5].setText(str);
  }
  /**
   *
   */
  void setSV2FileMessage(String str){
    _messages[6].setText(str);
  }
  /**
   *
   */
  void setDV3FileMessage(String str){
    _messages[10].setText(str);
  }
  /**
   *
   */
  void setSV3FileMessage(String str){
    _messages[11].setText(str);
  }
  /**
   *
   */
  void setDV4FileMessage(String str){
    _messages[12].setText(str);
  }
  /**
   *
   */
  void setSV4FileMessage(String str){
    _messages[13].setText(str);
  }
  /**
   *
   */
  void setUnitsMessage(String str){
		if (str.equals(AppUtils.CFS)) {
			cfs.setSelected(true);
		 } else {
			taf.setSelected(true);
		}
//    _messages[7].setText(str);
  }

  static void setDtsMasterMessage(String str) {
	_messages[9].setText(str);
  }

  /**
   *
   */
  String getProjectName(){
    return AppUtils.getCurrentProject().getFilename();
  }
  /**
   *
   */
  String getModeString(){
    if ( AppUtils.plotComparitive )
      return AppUtils.COMP;
    else if ( AppUtils.plotDifference )
      return AppUtils.DIFF;
    else
      return AppUtils.BASE;
  }
  /**
   * get the base DV file name that user chose from the main GUI.
   */
  String getDVFilename() {
    return AppUtils.getCurrentProject().getDVFile();
  }

  static String getDtsMessage() {
	return _messages[9].getText();
  }
  /**
   * get the base SV file name that user chose from the main GUI.
   */
  String getSVFilename() {
    return AppUtils.getCurrentProject().getSVFile();
  }
  /**
   * get the compare DV file name that user chose from the main GUI.
   */
  String getDV2Filename() {
    return AppUtils.getCurrentProject().getDV2File();
  }
  /**
   * get the compare SV file name that user chose from the main GUI.
   */
  String getSV2Filename() {
    return AppUtils.getCurrentProject().getSV2File();
  }
  /**
   * get the compare DV file name that user chose from the main GUI.
   */
  String getDV3Filename() {
    return AppUtils.getCurrentProject().getDV3File();
  }
  /**
   * get the compare SV file name that user chose from the main GUI.
   */
  String getSV3Filename() {
    return AppUtils.getCurrentProject().getSV3File();
  }
  /**
   * get the compare DV file name that user chose from the main GUI.
   */
  String getDV4Filename() {
    return AppUtils.getCurrentProject().getDV4File();
  }
  /**
   * get the compare SV file name that user chose from the main GUI.
   */
  String getSV4Filename() {
    return AppUtils.getCurrentProject().getSV4File();
  }
  /**
   * get the Units of the current project.
   */
  String getUnits() {
    return AppUtils.getCurrentUnits();
  }
  /**
   * get the time window that user modified from a panel.
   */
  String getTimeWindowString() {
    TimeWindow tw = AppUtils.getCurrentProject().getTimeWindow();
    return tw == null? " " : tw.getStartTime().toString().substring(2,9) + " - " + tw.getEndTime().toString().substring(2,9);
  }

  String getDtsFromProject() {
	return AppUtils.getCurrentProject().getDtsPath();
  }
  /**
   * get the message panel component
   */
  public JPanel getMessagePanelComp(){
    return _messagePanelComp;
  }
  JButton _basedv = new JButton("DV: ");
  JButton _basesv = new JButton("  SV: ");
  JButton _comp1dv = new JButton("DV: ");
  JButton _comp1sv = new JButton("  SV: ");
  JButton _comp2dv = new JButton("DV: ");
  JButton _comp2sv = new JButton("  SV: ");
  JButton _comp3dv = new JButton("DV: ");
  JButton _comp3sv = new JButton("  SV: ");

	JCheckBox _basebox = new JCheckBox();
	JCheckBox _comp1box = new JCheckBox();
	JCheckBox _comp2box = new JCheckBox();
	JCheckBox _comp3box = new JCheckBox();

  ItemListener al1 = new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getSource() == _basebox) {
				if (_basebox.isSelected()) {
					AppUtils.baseOn = true;
				} else {
					AppUtils.baseOn = false;
				}
				if (DEBUG) System.out.println(AppUtils.baseOn);
			}
			if (e.getSource() == _comp1box) {
				if (_comp1box.isSelected()) {
					AppUtils.comp1On = true;
				} else {
					AppUtils.comp1On = false;
				}
				if (DEBUG) System.out.println(AppUtils.comp1On);
			}
			if (e.getSource() == _comp2box) {
				if (_comp2box.isSelected()) {
					AppUtils.comp2On = true;
				} else {
					AppUtils.comp2On = false;
				}
				if (DEBUG) System.out.println(AppUtils.comp2On);
			}
			if (e.getSource() == _comp3box) {
				if (_comp3box.isSelected()) {
					AppUtils.comp3On = true;
				} else {
					AppUtils.comp3On = false;
				}
				if (DEBUG) System.out.println(AppUtils.comp3On);
			}
		}
	};

	JRadioButton taf = new JRadioButton("TAF");
	JRadioButton cfs = new JRadioButton("CFS");

	JRadioButton base = new JRadioButton("Base");
	JRadioButton comp = new JRadioButton("Comp");
	JRadioButton diff = new JRadioButton("Diff");

	JCheckBox plot = new JCheckBox("Plot");
	JCheckBox table = new JCheckBox("Table");
	JCheckBox monthly = new JCheckBox("Monthly");

  /*
   * private variables
   */
  JLabel labelNames[];
  private static JTextField _messages[];
  private JPanel _messagePanelComp;
  private MainPanel _mp;
  private JFrame _frame;
  Vector _twitems = new Vector(1,1);
} //end of class MessagePanel
