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
import vista.app.DataGraph;
import vista.app.DataTable;
import vista.app.MultiDataTable;
import vista.set.*;
import calsim.app.*;
import javax.swing.*;
import java.awt.*;
import java.awt.print.*;  //CB added
//import java.awt.event.*;
//import calsim.app.*;
import java.util.Properties;
import java.io.*;
import java.text.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.*;
import sun.print.*; //CB added
/**
  *	Common utilities for Gui package
  *
  * @author Nicky Sandhu, Yan-Ping Zuo, Armin munevar
  * @version $Id: GuiUtils.java,v 1.1.4.52 2001/10/23 16:28:39 jfenolio Exp $
  */
public class GuiUtils{
  static {
    VistaUtils.AWT_FILE_DIALOG = true;
  }
  /**
   *
   */
  public static String getVersionName(){
    Properties props = new Properties();
    String version = "1.5";
    try {
      props.load(VistaUtils.getResourceAsStream("/calsim/version.txt"));
      version = props.getProperty("version");
    }catch(Exception e){
    }
    return version;
  }

//swu
  public static String getVersionNo(){

    String version = getVersionName().toLowerCase();

    version = version.trim();

    version = version.substring(version.indexOf("version=")+1,version.indexOf("build"));

    return version;
  }

  /**
   * prints component to printer
   */

  public static void print(Component comp) {
		Frame fr = JOptionPane.getFrameForComponent(comp);
		Toolkit dtk = Toolkit.getDefaultToolkit();
		PrintJob pjob = dtk.getPrintJob(fr, "Print Dialog", null);
		if (pjob != null) {
			Graphics pg = pjob.getGraphics();
			if (pg != null) {
				comp.paint(pg);  //CB see paint method of comp's actual class to fit on one page
				pg.dispose(); // flush page
			}
			pjob.end();
		} else {
			try {
				// CB TO DO: this should NOT happen if user cancels print job!
				throw new RuntimeException("No print job available!!");
			} catch (Exception e) {
				VistaUtils.displayException(null, e);
			}
		}
	}

/*  public static void print2(Component comp) {  //CB this will print second page, but NOT fint all on one page without more work
		// Create a printerJob object
		final PrinterJob printJob = PrinterJob.getPrinterJob();
		// Set the printable class to this one since we
		// are implementing the Printable interface
		printJob.setPrintable(((ReportPane)comp).getPrintable(new MessageFormat(""),  //CB some Java version does not like this line
			new MessageFormat("")));
		printJob.setJobName("Monthly Table");
		// Collect the print request attributes.
		final HashPrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
		attrs.add(OrientationRequested.PORTRAIT);
		attrs.add(Chromaticity.COLOR);
		attrs.add(Chromaticity.MONOCHROME);
		attrs.add(PrintQuality.NORMAL);
		attrs.add(PrintQuality.DRAFT);
		attrs.add(PrintQuality.HIGH);
//		attrs.add(new PageRanges(1-2));  //exception
		// Assume US Letter size, someone else can do magic for other paper formats
		attrs.add(new MediaPrintableArea(0.25f, 0.25f, 8.0f, 10.5f, MediaPrintableArea.INCH));
		// Show a print dialog to the user. If the user
		// clicks the print button, then print, otherwise
		// cancel the print job
		if (printJob.printDialog()) {
			// You may want to do the printing in a thread or SwingWorker
			// But this gets the job done all the same.
			try {
				printJob.print(attrs);
			} catch (Exception PrintException) {
				PrintException.printStackTrace();
			}
		}
	} */

  /**
	 * get a string from a dialog box
	 */
  public static String getStringFromDialog(String message) {
    String _message = message;
    String dstring = null;
    dstring = JOptionPane.showInputDialog(_message);
    if ( dstring==null ) return null;
    return dstring;
  }
  /**
   *
   */
  static void convertFramesToPanels(JFrame [] frs,String tabName){
    if( frs == null ) return;
//    MainPanel mp = getMainPanel();
    for(int i=0; i < frs.length; i++){
      if ( frs[i] == null ) continue;
      if ( frs[i] instanceof DataGraph ){
	//mp.getGraphOutputPanel().addToTabbedPane(createPanelFromFrame(frs[i]),tabName);
	//frs[i].dispose();
	frs[i].setVisible(true);
      } else if ( frs[i] instanceof DataTable || frs[i] instanceof MultiDataTable ){
	//	mp.getTableOutputPanel().addToTabbedPane(createPanelFromFrame(frs[i]),tabName);
	//frs[i].dispose();
	frs[i].setVisible(true);
      } else {
	//mp.getMonthlyReportPanel().addToTabbedPane(createPanelFromFrame(frs[i]),tabName);
	//frs[i].dispose();
	frs[i].setVisible(true);
      }
    }
  }
  /**
   * displays data for the given bpart and cpart
   */
  public static void displayData(Component comp, String bpart, String cpart){
    //send bpart and cpart to app
    try {
      JFrame [] frs = AppUtils.displayData(bpart,cpart);
      String tabName = cpart + " @ " + bpart;
      convertFramesToPanels(frs,tabName);
    }catch(Exception e){
      VistaUtils.displayException(comp,e);
    }
  }
  /**
   *
   */
  public static void displayDTS(DerivedTimeSeries dts){
    //send bpart and cpart to app
    JFrame [] frs = AppUtils.displayDTSData(dts);
    String tabName = dts.getName();
    convertFramesToPanels(frs, tabName);
  }
  /**
   *
   */
  public static void displayMTS(MultipleTimeSeries mts){
    //send bpart and cpart to app
    JFrame [] frs = AppUtils.displayData(mts);
    String tabName = mts.getName();
    convertFramesToPanels(frs,tabName);
  }
  /**
   *
   */
  public static void displayData(DataReference ref){
    JFrame [] frs = AppUtils.displayData(ref);
    String tabName = ref.getPathname().getPart(Pathname.B_PART);
    convertFramesToPanels(frs,tabName);
  }
  /**
   *
   */
  public static void displayData(DataReference [] refs){
    JFrame [] frs = AppUtils.displayArrayData(refs);
    String tabName = "";
    for(int i=0; i < refs.length; i++){
      DataReference ref = refs[i];
      if ( ref != null )
	tabName += ref.getPathname().getPart(Pathname.B_PART);
    }
    convertFramesToPanels(frs,tabName);
  }
  /**
   *
   */
  public static void displayMassBalanceData(Component comp, int nodeId){
    try {
      DataReference ref1 = AppUtils.getMassBalance(1,nodeId);
      if ( ref1 == null )
	throw new RuntimeException("No mass balance for node: " + nodeId);
      //        if ( AppUtils.plotDifference || AppUtils.plotComparitive ) {
      //  	if ( ! isInComparableState(prj) )
      //  	  throw new RuntimeException("Cannot compare without loading base and compare files");
      //  	DataReference ref2 = AppUtils.getMassBalance(2,nodeId);
      //  	if ( ref2 == null )
      //  	  throw new RuntimeException("No mass balance in study 2 for node: " + nodeId);
      //  	if ( plotDifference )
      //  	  displayData(ref1.__sub__(ref2));
      //  	else
      //  	  displayData(new DataReference[]{ref1,ref2});
      //        } else {
      GuiUtils.displayData(ref1);
      //        }
    }catch(Exception e){
      VistaUtils.displayException(comp,e);
    }
  }
  /**
   *
   */
  public static boolean isComparable() {
    Project prj = AppUtils.getCurrentProject();
    return AppUtils.isInComparableState(prj);
  }
  /**
   *
   */
  public static String getProgramName(){
    return "WRIMS";
  }
  /**
   *
   */
  public static String getLocaleDescription(){
    String osname = System.getProperty("os.name");
    String osversion = System.getProperty("os.version");
    String archName = System.getProperty("os.arch");
    String javaVendor = System.getProperty("java.vendor");
    String javaVersion = System.getProperty("java.version");
    return "Running java " + javaVersion + " from " + javaVendor + " on " +
      osname + " " + osversion + " " + archName + " machine";
  }
  /**
   * traverses the component to find the first component of the
   * given class.
   */
  public static Component getComponent(Class cl, Container cont){
    Component [] comps = cont.getComponents();
    for(int i=0; i < comps.length; i++){
      if ( cl.isInstance(comps[i]) )
	return comps[i];
      if ( comps[i] instanceof Container ) {
	Component compx = getComponent(cl, (Container) comps[i]);
	if ( compx != null ) return compx;
      }
    }
    return null;
  }
  /**
   *
   */
  static void displayComponents(Container cont, int depth){
    Component [] comps = cont.getComponents();
    //
    if ( comps == null ) return;
    //
    String prestr = "";
    for(int i=0; i < depth; i++) prestr += "-";
    prestr += " ";
    //
    for(int i=0; i < comps.length; i++){
      Component comp = comps[i];
      if ( comp instanceof Container ){
	System.out.println(prestr+comp);
	displayComponents((Container)comp,depth+1);
      } else{
	System.out.println(prestr+comp);
      }
    }
  }
  /**
   *
   */
  public static void checkAndAddToProject(Component comp, DerivedTimeSeries dts){
    String name = dts.getName();
    Project prj = AppUtils.getCurrentProject();
    DerivedTimeSeries dts1 = prj.getDTS(name);
    prj.setDTSMod(true);
    if ( dts1 == null )
      prj.add(dts);
    else{
      int opt = JOptionPane.showConfirmDialog
	(comp,
	 "DTS with name " + name + " already exists in project!\n"+
	 "Do you want to override its definition?","Warning",
	 JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if ( opt == JOptionPane.YES_OPTION ){
	prj.remove(name); // remove old by the same name
	prj.add(dts);
      } else{
      }
    }
  }
  /**
   *
   */
  public static void checkAndAddToProject(Component comp, MultipleTimeSeries mts){
    String name = mts.getName();
    Project prj = AppUtils.getCurrentProject();
    MultipleTimeSeries mts1 = prj.getMTS(name);
    if ( mts1 == null )
      prj.add(mts);
    else{
      int opt = JOptionPane.showConfirmDialog
	(comp,
	 "MTS with name " + name + " already exists in project!\n"+
	 "Do you want to override its definition?","Warning",
	 JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if ( opt == JOptionPane.YES_OPTION ){
	prj.remove(name); // remove old by the same name
	prj.add(mts);
      } else{
      }
    }
  }
  /**
   *
   */
  public static boolean noCheckBoxMenuItemIsChecked(JCheckBoxMenuItem item1,
						    JCheckBoxMenuItem item2,
						    JCheckBoxMenuItem item3){
    System.out.println("noCheckbox");
    if( (item1.isSelected() == false) && (item2.isSelected() == false)
	&& (item3.isSelected() == false))
      return true;
    else
      return false;
  }
  /**
   *
   */
  public static void setStatus(String msg){
    if ( msg != null )
      _status.setText(msg);
  }
  /**
   *
   */
  static MainPanel createMainPanel(JFrame fr){
    if ( _panel == null ) _panel = new MainPanel(fr);
    return _panel;
  }
  /**
   *
   */
  static MainPanel getMainPanel(){
    return _panel;
  }
  /**
   * copies a frames menubar and content pane to create a panel with
   * that menubar and content pane.
   */
  public static JPanel createPanelFromFrame(JFrame fr){
    JMenuBar mbar = fr.getJMenuBar();
    JComponent contentPane = (JComponent) fr.getContentPane();
    contentPane.setAutoscrolls(true);
    JPanel cPanel = new JPanel();
    cPanel.setLayout(new BorderLayout());
    cPanel.add(contentPane);
    JPanel mbox = new JPanel();
    mbox.setBorder(BorderFactory.createRaisedBevelBorder());
    mbox.setLayout(new BoxLayout(mbox,BoxLayout.X_AXIS));
    mbox.add(mbar);
    mbox.add(Box.createHorizontalGlue()); mbox.add(Box.createVerticalStrut(20));
    JButton closeButton = new JButton("X");
    mbox.add(closeButton);
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.add(mbox);
    p.add(cPanel);
    return p;
  }
  /**
   * Get status panel
   */
  public static JPanel getStatusPanel(){
    if(_statusPanel == null) _statusPanel = createStatusPanel();
    return _statusPanel;
  }
  /**
   * create stutas panel which contains a progress bar, a label "Status", and a text field.
   */
  public static JPanel createStatusPanel() {
    JProgressBar progressBar = new JProgressBar();
    progressBar.setStringPainted(true);
    progressBar.setVisible(false);       //// ?? temporary
    JLabel name = new JLabel("Status:");
    _status = new JLabel();
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    panel.add(progressBar);
    panel.add(name);
    panel.add(_status);
    panel.setBorder(BorderFactory.createEtchedBorder());
    return panel;
  }
  /**
   * Create study tab
   */
  public static StudyTab createStudyTab(){
    _studyTab = new StudyTab(new Study());
    return _studyTab;
  }
  /**
   * Get study tab
   */
  public static StudyTab getStudyTab() {
    return _studyTab;
  }
  /**
   *
   */
  public static int doCommand(String dir, String cmd){
    File tmpfile = new File("temp.bat");
    try {
      PrintWriter tempWriter = new PrintWriter(
					       new BufferedWriter(new FileWriter(tmpfile)));
      if ( dir != null){
	File fl = new File(dir);
	String root = fl.getParent();
	while(true){
	  String parent = new File(root).getParent();
	  if ( parent != null )
	    root = parent;
	  else
	    break;
	}
	if ( root.indexOf(":") >= 0 ){
	  tempWriter.println(root.substring(0,root.indexOf(":")+1));
	}
	tempWriter.println("cd " + dir);
      }
      tempWriter.println("start /min "+cmd);
      tempWriter.close();
      Process p = Runtime.getRuntime().exec("temp.bat");
      BufferedReader stdout = new BufferedReader(new InputStreamReader( p.getInputStream()));
      BufferedReader stderr = new BufferedReader(new InputStreamReader( p.getErrorStream()));
      String line;
      while (null != (line = stdout.readLine())) {
	System.out.println(line);
      }
      stdout.close();
      while (null != (line = stderr.readLine())) {
	System.out.println(line);
      }
      stderr.close();
      return p.waitFor();
    } catch (IOException ee) {
      System.out.println(ee.getMessage());
      return 1;
    } catch (InterruptedException ee) {
      System.out.println("Ahem!  You interrupted.");
      return 1;
    } finally{
      // clean up
      tmpfile.delete();
    }
  }
  /**
   * Remove end of line chars from String for Fortran
   */
  public static String removeEOLChars(String s) {
    String ns = s;
    int i=1;
    while (i!=-1) {
      i = ns.indexOf("\n");
      if (i>0) ns=ns.substring(0,i)+" "+ns.substring(i+1);
    }
    return ns;
  }

  public static String[] sortArray(String[] s1) {
    CollationKey tmp;
    Collator col = Collator.getInstance();
    CollationKey[] keys = new CollationKey[s1.length];
    for (int j = 0; j < s1.length; j++) {
	  keys[j] = col.getCollationKey(s1[j]);
	}
    for (int i = 0; i < keys.length; i++) {
  	  for (int j = i + 1; j < keys.length; j++) {
  	    if (keys[i].compareTo(keys[j]) > 0) {
  		  tmp = keys[i];
  		  keys[i] = keys[j];
  		  keys[j] = tmp;
  	    }
  	  }
    }
    String[] s2 = new String[s1.length];
    for (int j = 0; j < s1.length; j++) {
	  s2[j] = keys[j].getSourceString();
  	}
    return s2;
  }

  /**
   *
   */
  static StudyTab _studyTab;
  static MainPanel _panel;
  static JPanel _statusPanel;
  static JLabel _status;

}
/*
$Log: GuiUtils.java,v $
Revision 1.1.4.52  2001/10/23 16:28:39  jfenolio
Changes made are:
1) Bug Fixes to the dts, mts, and the dts tree
2) MSRGui added and MSR modifications made
3) Changes to option pane including new controller for multi-year position analysis
4) Output tab changes include 4 different dss files can be viewed and other modifications to the message panel and menus

Revision 1.1.4.51  2001/07/12 01:59:39  amunevar
removal of all unneeded files for cleanup

Revision 1.1.4.50  2001/04/18 21:07:44  jfenolio
dts tree added, start month selection for table display

Revision 1.1.4.49  2000/12/20 20:07:10  amunevar
commit for ver 1.0.7

Revision 1.1.4.48  2000/06/22 00:57:53  amunevar
commit to update all files before Ryan checks out first time

Revision 1.1.4.47  1999/10/13 16:46:43  nsandhu
*** empty log message ***

Revision 1.1.4.46  1999/09/16 23:54:25  nsandhu
*** empty log message ***

Revision 1.1.4.45  1999/09/16 16:59:56  amunevar
*** empty log message ***

Revision 1.1.4.44  1999/09/01 16:56:58  amunevar
*** empty log message ***

Revision 1.1.4.43  1999/08/26 01:43:07  nsandhu
*** empty log message ***

Revision 1.1.4.42  1999/08/26 01:36:51  nsandhu
*** empty log message ***

Revision 1.1.4.41  1999/08/24 18:43:29  nsandhu
*** empty log message ***

Revision 1.1.4.40  1999/08/23 21:20:25  nsandhu
*** empty log message ***

Revision 1.1.4.39  1999/08/22 19:19:59  nsandhu
added tabbed pane for input data

Revision 1.1.4.38  1999/08/18 19:50:24  nsandhu
*** empty log message ***

Revision 1.1.4.37  1999/08/13 00:59:38  amunevar
*** empty log message ***

Revision 1.1.4.36  1999/08/07 00:23:03  nsandhu
changed about dialog; fixed dts table editing bug

Revision 1.1.4.35  1999/07/29 21:32:27  zuo
add ControlContainer.java

Revision 1.1.4.34  1999/07/25 19:25:31  nsandhu
*** empty log message ***

Revision 1.1.4.33  1999/07/20 22:25:13  zuo
separate status panel from main Panel

Revision 1.1.4.32  1999/07/20 16:52:57  zuo
added status panel to frame

Revision 1.1.4.31  1999/07/18 20:56:48  nsandhu
*** empty log message ***

Revision 1.1.4.30  1999/07/08 00:42:28  nsandhu
*** empty log message ***

Revision 1.1.4.29  1999/07/02 22:49:24  nsandhu
*** empty log message ***

Revision 1.1.4.28  1999/07/02 22:37:59  nsandhu
incorporated yan-ping's changes

Revision 1.1.4.27  1999/07/02 22:13:54  nsandhu
first move to eliminate extra frames on screen

Revision 1.1.4.26  1999/07/02 20:25:51  nsandhu
moved versioning info to calsim directory

*/
