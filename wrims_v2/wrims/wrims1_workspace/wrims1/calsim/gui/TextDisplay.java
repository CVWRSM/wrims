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
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.awt.*;
import java.awt.event.*;
/**
 * Panel for monthly table display
 * @author Nicky Sandhu
 * @version $Id: TextDisplay.java,v 1.1.2.15 2001/07/12 02:00:02 amunevar Exp $
 */
public class TextDisplay extends MPanel{
  public static boolean DEBUG = false;
  public static String [] itemText = {
    "Save",
    "Save As Html",
    "Print",
    "Quit",
  };
  public static String [] toolTipText = {
    "Saves to a text file",
    "Saves to a html file",
    "Prints as displayed",
    "Closes this display frame",
  };
  public static int [] itemKeys = {
    KeyEvent.VK_S,
    KeyEvent.VK_H,
    KeyEvent.VK_P,
    KeyEvent.VK_Q,
  };
  /**
   *
   */
  protected TextDisplay(){
  }
  /**
   *
   */
  public TextDisplay(StyledDocument doc){
    addDocument(doc);
  }
  /**
   *
   */
  public TextDisplay(String [] lines){
    String lineSeparator = System.getProperty("line.separator");
    StyledDocument doc = new DefaultStyledDocument();
    StyleContext sc = new StyleContext();
    Style def = sc.getStyle(StyleContext.DEFAULT_STYLE);
    Style normal = sc.addStyle("Normal",def);
    //    StyleConstants.setFontFamily(normal,"TimesRoman");
    StyleConstants.setFontSize(normal,8);
    for(int i=0; i < lines.length; i++){
      try {
	doc.insertString(doc.getEndPosition().getOffset()-1,lines[i]+lineSeparator,normal);
      }catch(BadLocationException ble){
	System.err.println("Could not insert string for line: " + lines[i]);
      }
    }
    addDocument(doc);
  }
  /**
   * adds the given document to this frame
   */
  void addDocument(StyledDocument doc){
    _doc = doc;
    JTextPane jtp = new ReportPane();
    jtp.setEditable(false);
    jtp.setDocument(doc);
    setLayout(new BorderLayout());
    add(new JScrollPane(jtp), BorderLayout.CENTER);
  }
  /**
    *
    */
  public void setFrameTitle(String str){
    _frameTitle = str;
  }
  /**
   *
   */
  public String getFrameTitle(){
    return _frameTitle;
  }
  /**
    *
    */
  public JMenuBar getJMenuBar(){
    if ( _mbar == null ) _mbar = createJMenuBar();
    return _mbar;
  }
  /**
   *
   */
  public JMenuBar createJMenuBar(){

    int mindex=0;
    JMenuItem saveItem = new JMenuItem(itemText[mindex]);
    saveItem.setToolTipText(toolTipText[mindex]);
    saveItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[mindex],KeyEvent.CTRL_MASK));
    saveItem.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	save();
      }
    });
    //
    mindex++;
    JMenuItem saveAsHtmlItem = new JMenuItem(itemText[mindex]);
    //saveAsHtmlItem.setEnabled(false); // ?? temporary
    saveAsHtmlItem.setToolTipText(toolTipText[mindex]);
    saveAsHtmlItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[mindex],KeyEvent.CTRL_MASK));
    saveAsHtmlItem.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	saveAsHtml();
      }
    });
    mindex++;
    //
    JMenuItem printItem = new JMenuItem(itemText[mindex]);
    printItem.setToolTipText(toolTipText[mindex]);
    printItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[mindex],KeyEvent.CTRL_MASK));
    printItem.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	print();
      }
    });
    //
    mindex++;
    JMenuItem quitItem = new JMenuItem(itemText[mindex]);
    quitItem.setToolTipText(toolTipText[mindex]);
    quitItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[mindex],KeyEvent.CTRL_MASK));
    quitItem.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	quit();
      }
    });
    //
    //
    JMenu fileMenu = new JMenu("File");
    fileMenu.add(saveItem);
    fileMenu.add(saveAsHtmlItem);
    fileMenu.add(printItem);
    fileMenu.addSeparator();
    fileMenu.addSeparator();
    fileMenu.add(quitItem);
    //
    _mbar = new JMenuBar();
    _mbar.add(fileMenu);
    return _mbar;
  }
	/**
	 *
	 */
	void print(){
		if (DEBUG) System.out.println("Print");
		Style s = null;
		try {
			s = _doc.getStyle("main");
			StyleConstants.setFontSize(s,6);  //CB changed to 6, from 7, to fit add'l lines from extending hydrology on same page
			Style dateStyle = _doc.getStyle("date style");
			if ( dateStyle != null ) StyleConstants.setFontSize(dateStyle,5);
			GuiUtils.print(GuiUtils.getComponent(JTextPane.class,this));
		} catch(Exception e){
			VistaUtils.displayException(this,e);
		} finally{
			if ( s != null) {
				s = _doc.getStyle("main");
				StyleConstants.setFontSize(s,12);
				Style dateStyle = _doc.getStyle("date style");
				if ( dateStyle != null ) StyleConstants.setFontSize(dateStyle,5);
			}
		}
	}
  /**
   *
   */
  void save(){
    if ( _doc != null ) {
      try {
	String saveFile = VistaUtils.getFilenameFromDialog(this,FileDialog.SAVE,
							   "txt","Text File");
	if ( saveFile == null ) return;
	PrintWriter writer = new PrintWriter(new FileWriter(saveFile));
	String txt = _doc.getText(0,_doc.getLength());
	java.util.StringTokenizer st =
	  new java.util.StringTokenizer(txt,System.getProperty("line.separator"));
	while ( st.hasMoreTokens() ){
	  String line = (String) st.nextToken().trim();
	  writer.println(line);
	}
	writer.close();
      }catch(Exception e){
	VistaUtils.displayException(this,e);
      }
    }
    if (DEBUG) System.out.println("Save");

  }
  /**
   *
   */
  void saveAsHtml(){
    if ( _doc != null ) {
      try {
	String saveFile = VistaUtils.getFilenameFromDialog(this,FileDialog.SAVE,
							   "html","HTML files");
	if ( saveFile == null ) return;
	FileWriter writer = new FileWriter(saveFile);
	new MinimalHTMLWriter(writer,(StyledDocument)_doc).write();
	writer.close();
      }catch(Exception e){
	VistaUtils.displayException(this,e);
      }
    }
    if (DEBUG) System.out.println("Save As Html");
  }
  /**
   *
   */
  void quit(){
    if (DEBUG) System.out.println("Quit");
    JOptionPane.getFrameForComponent(this).dispose();
  }
  private StyledDocument _doc;
  private String _frameTitle = "REPORT";
  private JMenuBar _mbar;
}
