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
//import vista.set.*;
//import vista.db.dss.DSSUtil;
//import vista.gui.*;
//import vista.time.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
//import java.util.*;
import javax.swing.*;
//import javax.swing.table.*;
//import calsim.wreslcoder.*;
import com.sun.xml.tree.XmlDocument;
import com.sun.xml.tree.TreeWalker;
import org.w3c.dom.Element;


/**
 * GUI for Multistudy runner
 *
 * @author Joel Fenolio
 */

public class MSRGui {
   boolean DEBUG = true;

	 /**
	  *Constructor for MSR Gui.  Accepts a string from the batch file that if not null set the tolerance limits.
	  *Instantiates the Frame and calls the appropriate methods to construct the gui
	  */
	public MSRGui() {

		JTabbedPane mainHolder = new JTabbedPane();

    JMenuBar mb = new JMenuBar();
    mb.add(createFileMenu());
    mb.add(createTransferMenu());
    _fr.setJMenuBar(mb);
    _fr.addWindowListener( new WindowAdapter() {
      public void windowClosing(WindowEvent evt){
				exit();
      }
    });

    JPanel panel = new JPanel();
    Container pane = _fr.getContentPane();
    panel.setLayout(new GridBagLayout());
    panel.setSize(450,450);

    JButton runbutton = new JButton("  Run  ");
    runbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				run();
			}
		});

    GridBagConstraints gc = new GridBagConstraints();

    gc.anchor = GridBagConstraints.NORTHWEST;
    gc.gridx = 0;
    gc.gridy = 0;
    panel.add(createModelPanel(),gc);

    gc.gridx = 0;
    gc.gridy = 1;
    panel.add(createStudiesPanel(),gc);

    gc.gridx = 0;
    gc.gridy = 2;
    panel.add(createOptionsPanel(),gc);

    gc.gridx = 0;
    gc.gridy = 3;
    gc.insets = new Insets(5,0,0,0);
    panel.add(createTWPanel(),gc);

    gc.insets = new Insets(15,0,0,0);
    gc.gridx = 0;
    gc.gridy = 4;
    panel.add(createPositionPanel(),gc);

    gc.insets = new Insets(15,0,0,0);
    gc.gridy = 5;
    gc.anchor = GridBagConstraints.CENTER;
    panel.add(runbutton,gc);

    mainHolder.add("General",panel);
    mainHolder.add("Transfer File",createTransferPanel());

    pane.add(mainHolder);
    _fr.setSize(650,450);
	_fr.setVisible(true);
	_numyears.setEnabled(false);
	_runner = new MultiStudyRunner(_hideWarnings, _hideProgressDetails);
	}
	
	/**
	 *Creates menu bar for the gui
	 */
	public JMenu createFileMenu() {
//		JMenuBar mb = new JMenuBar();
		JMenu menu = new JMenu(" File   ");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem saveAs = new JMenuItem("Save As");
		JMenuItem open = new JMenuItem("Open");
		JMenuItem exit = new JMenuItem("Exit");
    open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					openFile();
			  } catch(Exception ioe){
					ioe.printStackTrace(System.err);
      	}

		  }
		});
    save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			  try {
			    saveFile();
			  } catch(IOException ioe){
					ioe.printStackTrace(System.err);
      	}
			}
		});
    saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			  try {
			  	saveAsFile();
			  } catch(IOException ioe){
					ioe.printStackTrace(System.err);
      	}

			}
		});
    exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    exit();
		  }
		});
		menu.add(open);
		menu.add(save);
		menu.add(saveAs);
		menu.add(exit);
	  return menu;
	}

	/**
	 *Creates menu bar for the gui
	 */
	public JMenu createTransferMenu() {
		JMenu menu = new JMenu(" Transfer   ");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem saveAs = new JMenuItem("Save As");
		JMenuItem open = new JMenuItem("Open");
		JMenuItem newfile = new JMenuItem("New");
    open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filename = getFileName(FileDialog.LOAD, "*dat");
				try {
					openTransfer(filename);
			  } catch(Exception ioe){
					ioe.printStackTrace(System.err);
      	}

		  }
		});
    save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			  try {
					if (_transFile.equals("")) {
						String filename = getFileName(FileDialog.SAVE, "*dat");
						if (filename == null) return;
						_transFile = filename;
					}
					saveTransfer(_transFile);
			  } catch(IOException ioe){
					ioe.printStackTrace(System.err);
      	}
			}
		});
    saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filename = getFileName(FileDialog.SAVE, "*dat");
		    if (filename == null) return;
			  try {
          saveAsTransfer(filename);
			  } catch(IOException ioe){
					ioe.printStackTrace(System.err);
      	}

			}
		});
    newfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_transFile = "";
				_text.setText("");
		  }
		});
		menu.add(newfile);
		menu.add(open);
		menu.add(save);
		menu.add(saveAs);
	  return menu;
	}

	public JPanel createTransferPanel() {

		JPanel p = new JPanel();
		p.setSize(450,450);
		p.setLayout(new BorderLayout());
		final JTextArea text = new JTextArea();
		_text = text;
    text.setLineWrap(true);
    text.setWrapStyleWord(true);
		p.add(new JScrollPane(text),BorderLayout.CENTER);
		JPanel buttons = new JPanel();
		JButton tofrom = new JButton("  Files   ");
		JButton name   = new JButton(" DSS Name ");
		buttons.setLayout(new GridLayout(1,0));
		buttons.add(tofrom);
		buttons.add(name);
		tofrom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				text.append("* Message,d:\\studies\\ewa\\D1485\\dss\\D1485dv.dss,d:\\studies\\ewa\\ewa\\dss\\simEWAdv.dss");
			}
		});
		name.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				text.append("/CALSIM/B-PART/C-PART/,/CALSIM/B-PART/C-PART//1MON/2020D09D/");
			}
		});

		p.add(buttons,BorderLayout.SOUTH);


		return p;
	}

	public JPanel createPositionPanel() {
		JPanel p = new JPanel();

		p.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();

    gc.anchor = GridBagConstraints.NORTHWEST;
    gc.gridx = 0;
    gc.gridy = 0;
		gc.insets = new Insets(1,2,1,3);
		p.add(new JLabel("                   Position Analysis    "),gc);

    gc.gridx = 1;
    gc.gridy = 0;
    p.add(_position,gc);

    gc.gridx = 2;
    gc.gridy = 0;
    gc.insets = new Insets(1,2,1,3);
    p.add(new JLabel("    Years   "),gc);

    gc.gridx = 3;
    gc.gridy = 0;
    p.add(_numyears,gc);

    _position.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_position.isSelected()) _numyears.setEnabled(true); else _numyears.setEnabled(false);
			}
		});

		return p;
	}

	/**
	 *Opens transfer file
	 */
  public void openTransfer(String filename) throws Exception {
		_transFile = filename;
    try {
      _text.read(new InputStreamReader(new FileInputStream(filename)),null);
    }catch(Exception e){
      e.printStackTrace(System.err);
      throw new Exception("Invalid project file: " + _mainFile);
    }
	}
	/**
	 *Saves transfer file
	 */
  public void saveTransfer(String filename) throws IOException {
		File f = new File(filename);
    PrintWriter pw = new PrintWriter(new FileOutputStream(f));
    _text.write(pw); pw.close();
    _transchanged = false;
	}
	/**
	 *Saves transfer file
	 */
  public void saveAsTransfer(String filename) throws IOException {
    PrintWriter pw = new PrintWriter(new FileOutputStream(filename));
    _text.write(pw); pw.close();
    _transchanged = false;
	}
	/**
	 *Opens multistudy file
	 */
  public void openFile() throws Exception {
		String filename = getFileName(FileDialog.LOAD, "*msty");;
    _mainFile = filename;
    try {
      XmlDocument doc = XmlDocument.createXmlDocument(new FileInputStream(_mainFile),false);
      fromXml(doc.getDocumentElement());
    }catch(Exception e){
      e.printStackTrace(System.err);
      throw new Exception("Invalid project file: " + _mainFile);
    }
	}
	/**
	 *Saves msty file
	 */
  public void saveFile() throws IOException {
		if (_mainFile.equals("")) {
			String filename = getFileName(FileDialog.SAVE, "*msty");
			if (filename == null) return;
			_mainFile = filename;
		}
    XmlDocument mstydoc = new XmlDocument();
    XmlDocument doc = new XmlDocument();
    Element master = doc.createElement("MultiStudyFile");
    doc.appendChild(master);
    toXml(mstydoc);
    PrintWriter pw = new PrintWriter(new FileOutputStream(_mainFile));
    mstydoc.write(pw); pw.close();
    _changed = false;
	}
	/**
	 *Saves msty file
	 */
  public void saveAsFile() throws IOException {
		String filename = getFileName(FileDialog.SAVE, "*msty");
    if (filename == null) return;
    XmlDocument mstydoc = new XmlDocument();
    XmlDocument doc = new XmlDocument();
    Element master = doc.createElement("MultiStudyFile");
    doc.appendChild(master);
    _mainFile = filename;
    toXml(mstydoc);
    PrintWriter pw = new PrintWriter(new FileOutputStream(filename));
    mstydoc.write(pw); pw.close();
    _changed = false;
	}
  /**
    * Returns a element of an xml document
    */
  public void toXml(XmlDocument doc){
    Element el = doc.createElement("MultiStudyFile");
    el.appendChild(doc.createComment("MultiStudy xml format"));
    el.setAttribute("name", _mainFile);
    // study files
    Element s1 = doc.createElement("Study");
    s1.setAttribute("D1485",getD1485Sty());
    Element s2 = doc.createElement("Study");
    s2.setAttribute("D1641",getD1641Sty());
    Element s3 = doc.createElement("Study");
    s3.setAttribute("B2",getB2Sty());
    Element s4 = doc.createElement("Study");
    s4.setAttribute("EWA",getEWASty());
    Element s5 = doc.createElement("Transfer");
    s5.setAttribute("B2",getB2Transfer());
    Element s6 = doc.createElement("Transfer");
    s6.setAttribute("EWA",getEWATransfer());
    el.appendChild(s1);
    el.appendChild(s2);
    el.appendChild(s3);
    el.appendChild(s4);
    el.appendChild(s5);
    el.appendChild(s6);
    doc.appendChild(el);
  }

  /**
    *Gets data for msty file
    */
  public void fromXml(Element pe) throws IOException {
    TreeWalker tw = new TreeWalker(pe);
    // study items
    for (int i=0; i<4; i++) {
      Element se = tw.getNextElement("Study");
      if ( i==0) {
	      setD1485Sty(se.getAttribute("D1485"));
      } else if ( i == 1 ){
	      setD1641Sty(se.getAttribute("D1641"));
      } else if ( i == 2 ){
	      setB2Sty(se.getAttribute("B2"));
      } else if ( i == 3 ){
	      setEWASty(se.getAttribute("EWA"));
      }
    }
    for (int i=0; i<2; i++) {
      Element se = tw.getNextElement("Transfer");
      if ( i==0) {
	      setB2Transfer(se.getAttribute("B2"));
      } else if ( i == 1 ){
	      setEWATransfer(se.getAttribute("EWA"));
			}
		}
  }
  /**
   *exits when menu item exit is selected
   */
  public void exit() {
		if (_changed) {
			int option = JOptionPane.showConfirmDialog(null, "Save before exiting?",
                                    "Data Modified",  JOptionPane.YES_NO_OPTION);
      if (option == JOptionPane.YES_OPTION) {
				try {
					saveFile();
				} catch(IOException ioe){
				ioe.printStackTrace(System.err);
				}

			}
    }
		System.exit(0);
	}
	/**
	 *creates the panel for holding the model selector combo box and its label
	 */
  public JPanel createModelPanel() {
		JPanel p = new JPanel();
		JLabel l = new JLabel(" Model      ");
		p.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		_runselector.addItem("B2");
		_runselector.addItem("EWA");
		_runselector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_runtype = (String)_runselector.getSelectedItem();
			}
		});
    gc.anchor = GridBagConstraints.NORTHWEST;
    gc.gridx = 0;
    gc.gridy = 0;
    p.add(l,gc);
    gc.gridx = 1;
    //gc.insets = new Insets(0,2,0,3);
    p.add(_runselector,gc);
		return p;
	}
	/**
	 *Creates panel that holds textfields and buttons for the studies
	 */
	public JPanel createStudiesPanel() {
		JPanel p = new JPanel();

		_d1485button.addActionListener(studyListener);
		_d1641button.addActionListener(studyListener);
		_b2button.addActionListener(studyListener);
		_ewabutton.addActionListener(studyListener);
		_ewatransbutton.addActionListener(transferListener);
		_b2transbutton.addActionListener(transferListener);

    _studyD1485.setMinimumSize(_studyD1485.getPreferredSize());
    _studyD1641.setMinimumSize(_studyD1485.getPreferredSize());
    _studyB2.setMinimumSize(_studyD1485.getPreferredSize());
    _studyEWA.setMinimumSize(_studyD1485.getPreferredSize());
    _transferB2.setMinimumSize(_studyD1485.getPreferredSize());
    _transferEWA.setMinimumSize(_studyD1485.getPreferredSize());

    _studyD1485.addActionListener(textListener);
    _studyD1641.addActionListener(textListener);
    _studyB2.addActionListener(textListener);
    _studyEWA.addActionListener(textListener);
    _transferB2.addActionListener(textListener);
    _transferB2.addMouseListener(textMouseListener);
    _transferEWA.addMouseListener(textMouseListener);
    _transferEWA.addActionListener(textListener);


		p.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();

    gc.gridx = 0;
    gc.gridy = 0;
    p.add(new JLabel("   "),gc);
    gc.gridx = 1;
    p.add(new JLabel("          Studies          "),gc);
    gc.gridx = 2;
    p.add(new JLabel("   "),gc);

    gc.gridx = 0;
    gc.gridy = 1;
    p.add(new JLabel(" D1485  "),gc);
    gc.gridx = 1;
    p.add(_studyD1485,gc);
    gc.gridx = 2;
		gc.insets = new Insets(1,2,1,3);
    p.add(_d1485button,gc);

    gc.gridx = 0;
    gc.gridy = 2;
    p.add(new JLabel(" D1641  "),gc);
    gc.gridx = 1;
    p.add(_studyD1641,gc);
    gc.gridx = 2;
		gc.insets = new Insets(1,2,1,3);
    p.add(_d1641button,gc);

    gc.gridx = 0;
    gc.gridy = 3;
    p.add(new JLabel(" B2  "),gc);
    gc.gridx = 1;
    p.add(_studyB2,gc);
    gc.gridx = 2;
		gc.insets = new Insets(1,2,1,3);
    p.add(_b2button,gc);

    gc.gridx = 0;
    gc.gridy = 4;
    p.add(new JLabel(" EWA  "),gc);
    gc.gridx = 1;
    p.add(_studyEWA,gc);
    gc.gridx = 2;
		gc.insets = new Insets(1,2,1,3);
    p.add(_ewabutton,gc);

    gc.gridx = 0;
    gc.gridy = 5;
    p.add(new JLabel("   "),gc);
    gc.gridx = 1;
    p.add(new JLabel("   Transfer Files   "),gc);
    gc.gridx = 2;
    p.add(new JLabel("   "),gc);

    gc.gridx = 0;
    gc.gridy = 6;
    p.add(new JLabel(" B2  "),gc);
    gc.gridx = 1;
    p.add(_transferB2,gc);
    gc.gridx = 2;
		gc.insets = new Insets(1,2,1,3);
    p.add(_b2transbutton,gc);

    gc.gridx = 0;
    gc.gridy = 7;
    p.add(new JLabel(" EWA  "),gc);
    gc.gridx = 1;
    p.add(_transferEWA,gc);
    gc.gridx = 2;
		gc.insets = new Insets(1,2,1,3);
    p.add(_ewatransbutton,gc);

    return p;
	}
	/**
	 *creates the panel for holding the options textfield and its label
	 */
  public JPanel createOptionsPanel() {
		JPanel p = new JPanel();
		JLabel l = new JLabel("  XA Ops  ");
		p.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
    gc.anchor = GridBagConstraints.NORTHWEST;
    gc.gridx = 0;
    gc.gridy = 0;
    p.add(l,gc);
    gc.gridx = 1;
    //gc.insets = new Insets(0,2,0,3);
    p.add(_options,gc);
		return p;
	}
	/**
	 *Creates panel for the year combo boxes
	 */
	public JPanel createTWPanel() {
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();

    for (int i=1921; i<1995; i++) {
			String s = new String(new Integer(i).toString());
			_syrselector.addItem(s);
			_eyrselector.addItem(s);
		}
		_syrselector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setStartYear((String)_syrselector.getSelectedItem());
			}
		});
		_eyrselector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setEndYear((String)_eyrselector.getSelectedItem());
			}
		});
    _eyrselector.setSelectedIndex(73);
    gc.gridx = 0;
    gc.gridy = 0;
    p.add(new JLabel("         "),gc);
    gc.gridx = 1;
    p.add(new JLabel(" Start Year "),gc);
    gc.gridx = 2;
    p.add(new JLabel("   "),gc);
    gc.gridx = 3;
    p.add(new JLabel(" End Year "),gc);

    gc.gridx = 0;
    gc.gridy = 1;
    p.add(new JLabel("                   Time Window           "),gc);
    gc.gridx = 1;
		gc.insets = new Insets(1,2,1,3);
    p.add(_syrselector,gc);
    gc.gridx = 2;
    p.add(new JLabel("        "),gc);
    gc.gridx = 3;
		gc.insets = new Insets(1,2,1,3);
    p.add(_eyrselector,gc);

    return p;

	}
	/**
	 *Sets the beginning year
	 */
	public void setStartYear(String year) {
		int syear = new Integer(year).intValue();
		_years[0] = syear;
	}
	/**
	 *Sets the ending year
	 */
	public void setEndYear(String year) {
		int eyear = new Integer(year).intValue();
		_years[1] = eyear;
	}
	/**
	 *gets the start year
	 */
  public int getStartYear() {
		return _years[0];
	}
	/**
	 *gets the end year
	 */
  public int getEndYear() {
		return _years[1];
	}

  MouseListener textMouseListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
				JTextField tf = (JTextField)e.getComponent();
				String filename = tf.getText();
				_transFile = filename;
				try {
					openTransfer(filename);
				}catch(Exception e1){
				}
			}
		}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
	};

  ActionListener studyListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
      String filename = getFileName(FileDialog.LOAD,"*sty");
		  if (filename == null) return;
		  if (e.getSource() == _d1485button) {
				setD1485Sty(filename);
			} else if (e.getSource() == _d1641button) {
				setD1641Sty(filename);
			} else if (e.getSource() == _b2button) {
				setB2Sty(filename);
			} else if (e.getSource() == _ewabutton) {
				setEWASty(filename);
			}
			_changed = true;
		}
	};
  /**
   *Displays the file dialog and returns the file name of the file selected
   *Type refers to the fields LOAD and SAVE from the FileDialog class
   *Extension refers to the file extension you want it to filter for
   */
	public String getFileName(int type, String extension) {
  	FileDialog fileDialog = new FileDialog(_fr,"", type);
   	fileDialog.setModal(true);
   	fileDialog.setFile(extension);
   	fileDialog.setVisible(true);
   	if (fileDialog.getFile() == null ) return null;
   	if (fileDialog.getFile().equals(extension)) return null;
   	if (fileDialog.getDirectory() == null) return null;
   	return new String(fileDialog.getDirectory() + fileDialog.getFile());
	}


  ActionListener transferListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		  String filename = getFileName(FileDialog.LOAD, "*dat");
		  if (e.getSource() == _b2transbutton) {
				setB2Transfer(filename);
			} else if (e.getSource() == _ewatransbutton) {
				setEWATransfer(filename);
			}
			_changed = true;
		}
	};

  ActionListener textListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			_changed = true;
		}
	};
	/**
	 *takes the data specified/stored in the gui and passes it to multistudyrunner
	 */
  public void run() {
		String[] studies = new String[4];
		String[] transfer = new String[2];
		studies[0] = getD1485Sty();
		studies[1] = getD1641Sty();
		studies[2] = getB2Sty();
		studies[3] = getEWASty();
		transfer[0] = getB2Transfer();
		transfer[1] = getEWATransfer();
		_tol = getOptions();
		if (checkStudies(studies,transfer)) {
			_runner.run(studies,transfer,_years,_runtype,_tol,getPosAnalysis(),getPosYears());
			JOptionPane.showMessageDialog(null,"Run is complete.",
                                     "Finished",JOptionPane.INFORMATION_MESSAGE);
		}
	}
	/**
	 *checks that all the required field are specified to make a B2 or EWA run
	 */
	public boolean checkStudies(String[] studies, String[] transfer) {
		if (_years[0] >= _years[1]) {
			JOptionPane.showMessageDialog(null,"Start year is after End Year.",
      	                               "Incorrect years",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (_runtype.equals("B2")) {
			for (int i=0; i<3; i++) {
				if(studies[i].equals("")) {
					JOptionPane.showMessageDialog(null,"One of the study files are missing.",
                                     "Missing Study File",JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			if(transfer[0].equals("")) {
				JOptionPane.showMessageDialog(null,"Missing B2 transfer file.",
                                     "Missing Transfer File",JOptionPane.ERROR_MESSAGE);
				return false;
			}
			return true;
		} else if (_runtype.equals("EWA")) {
			for (int i=0; i<4; i++) {
				if(studies[i].equals("")) {
					JOptionPane.showMessageDialog(null,"One of the study files are missing.",
                                     "Missing Study File",JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			for (int i=0; i<2; i++) {
				if(transfer[i].equals("")) {
					JOptionPane.showMessageDialog(null,"Missing a required transfer file.",
      	                               "Missing Transfer File",JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			return true;
		}
		return false;
	}
  /**
   *Sets the pathname of the file selected in the textfield
   */
  public void setD1485Sty(String file) {
		_studyD1485.setText(file);
	}
  /**
   *Sets the pathname of the file selected in the textfield
   */
  public void setD1641Sty(String file) {
		_studyD1641.setText(file);
	}
  /**
   *Sets the pathname of the file selected in the textfield
   */
  public void setB2Sty(String file) {
		_studyB2.setText(file);
	}
  /**
   *Sets the pathname of the file selected in the textfield
   */
  public void setEWASty(String file) {
		_studyEWA.setText(file);
	}
  /**
   *Sets the pathname of the file selected in the textfield
   */
  public void setB2Transfer(String file) {
		_transferB2.setText(file);
	}
  /**
   *Sets the pathname of the file selected in the textfield
   */
  public void setEWATransfer(String file) {
		_transferEWA.setText(file);
	}
  /**
   *Sets the text in the textfield
   */
  public void setOptions(String file) {
		_options.setText(file);
	}
  /**
   *gets the pathname of the file selected in the textfield
   */
  public String getD1485Sty() {
		return _studyD1485.getText();
	}
  /**
   *gets the pathname of the file selected in the textfield
   */
  public String getD1641Sty() {
		return _studyD1641.getText();
	}
  /**
   *gets the pathname of the file selected in the textfield
   */
  public String getB2Sty() {
		return _studyB2.getText();
	}
  /**
   *gets the pathname of the file selected in the textfield
   */
  public String getEWASty() {
		return _studyEWA.getText();
	}
  /**
   *gets the pathname of the file selected in the textfield
   */
  public String getB2Transfer() {
		return _transferB2.getText();
	}
  /**
   *gets the pathname of the file selected in the textfield
   */
  public String getEWATransfer() {
		return _transferEWA.getText();
	}
  /**
   *gets options specified in the textfield
   */
  public String getOptions() {
		return _options.getText();
	}
  /**
   *returns true if position analysis is selected
   */
  public boolean getPosAnalysis() {
		return _position.isSelected();
	}
  /**
   *the number of years for position analysis to run for
   */
  public int getPosYears() {
    String s = _numyears.getText();
		if (s.equals("")) s = "0";
		return new Integer(s).intValue();
	}

  /**
   * Main method
   */
  public static void main(String args[]) {

    try {
		  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		new MSRGui();
	}

	JButton _d1485button = new JButton("Choose");
  JButton _d1641button = new JButton("Choose");
  JButton _b2button = new JButton("Choose");
	JButton _ewabutton = new JButton("Choose");
	JButton _ewatransbutton = new JButton("Choose");
	JButton _b2transbutton = new JButton("Choose");

	JTextArea _text = new JTextArea();

  JCheckBox _position = new JCheckBox();
  JTextField _numyears = new JTextField(5);

  JFrame _fr = new JFrame("B2/EWA MultiStudyRunner for CALSIM");
  JTextField _studyD1485 = new JTextField(30);
  JTextField _studyD1641 = new JTextField(30);
  JTextField _studyB2 = new JTextField(30);
  JTextField _studyEWA = new JTextField(30);
  JTextField _transferB2 = new JTextField(30);
  JTextField _transferEWA = new JTextField(30);
  JTextField _options = new JTextField(30);
  JComboBox _runselector = new JComboBox();
  JComboBox _syrselector = new JComboBox();
  JComboBox _eyrselector = new JComboBox();
  MultiStudyRunner _runner;
  String _tol = new String();
  int[] _years = {1921,1994};
  String _mainFile = "";
  String _transFile = "";
  String _runtype = "B2";
  boolean _changed = false;
  boolean _transchanged = false;
  boolean _hideWarnings;                 // CB added warnings message switch per Easton's request
  private boolean _hideProgressDetails;  // CB added progress details message switch per Easton's request
}































