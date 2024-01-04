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
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.File;
import java.io.FilenameFilter;
import vista.gui.VistaUtils;
/**
	* Lookup table panel
 * @author Nicky Sandhu
 * @version $Id: LookupUI.java,v 1.1.2.6 2001/07/12 01:59:43 amunevar Exp $
 */
public class LookupUI extends MPanel{
  public static boolean DEBUG = false;
  private Hashtable _tableUIMap;
  private JComboBox _namesBox;
  private LookupDataUI _dataUI;
  private LookupTableData _emptyModel;
  public LookupUI(){
    _tableUIMap = new Hashtable();
    _namesBox = new JComboBox(new DefaultComboBoxModel());
    _namesBox.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	updateUI();
      }
    });
    JPanel namePanel = new JPanel();
    namePanel.setBackground(new Color(207,220,200));
    namePanel.setLayout(new BorderLayout());
    namePanel.add(_namesBox,BorderLayout.WEST);
    namePanel.setBorder(BorderFactory.createTitledBorder("Table filename"));
    //
    _dataUI = new LookupDataUI();
    _emptyModel = new LookupTableData();
    _dataUI.setModel(_emptyModel);
    //
    setLayout(new BorderLayout());
    add(namePanel,BorderLayout.NORTH);
    add(_dataUI,BorderLayout.CENTER);
  }
  //
  public String getFrameTitle(){
    return "Lookup Table Manager";
  }
  public void clearData(){
    _dataUI.setModel(_emptyModel);
    _tableUIMap.clear();
    _namesBox.removeAllItems();
  }
  //
  public void updateUI(){
    if ( _namesBox == null ) {
      return;
    }
    String tableName = (String) _namesBox.getSelectedItem();
    if ( tableName == null )
      _dataUI.setModel(_emptyModel);
    else
      _dataUI.setModel((InputTableData)_tableUIMap.get(tableName));
  }
  //
  public JMenuBar getJMenuBar(){
    final JComponent comp = this;
    Action loadAction = new AbstractAction("Load..."){
      public void actionPerformed(ActionEvent evt){
	String fname = VistaUtils.getFilenameFromDialog(comp,FileDialog.LOAD,"table","Tables");
	if ( fname == null ) return;
	try{
	  if (DEBUG) System.out.println("fname: " + fname);
	  if (DEBUG) System.out.println(_tableUIMap.containsKey(fname));
	  if ( _tableUIMap.containsKey(fname) ) throw new RuntimeException("File "+fname+"Already Open!");
	  _tableUIMap.put(fname, new LookupTableData(fname));
	  if (DEBUG) System.out.println(_tableUIMap.get(fname));
	  DefaultComboBoxModel model = (DefaultComboBoxModel) _namesBox.getModel();
	  int count = model.getSize();
	  int i;
	  for(i=0; i < count; i++){
	    String name = (String) model.getElementAt(i);
	    if (name.compareTo(fname) >= 0 ) break;
	  }
	  model.insertElementAt(fname,i);
	  _namesBox.setSelectedItem(fname);
	  updateUI();
	} catch (Exception e) {
	  JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
	}
      }
    };
    Action saveAction = new AbstractAction("Save"){
      public void actionPerformed(ActionEvent evt){
	if (DEBUG) System.out.println("Saving " + _namesBox.getSelectedItem());
	InputTableData model = (InputTableData) _tableUIMap.get(_namesBox.getSelectedItem());
	if (model != null && model.needsSaving()) {
	  if (DEBUG) System.out.println("Saving " + model.getInputFile());
	  model.save(model.getInputFile());
	}
      }
    };
    Action closeAction = new AbstractAction("Close"){
      public void actionPerformed(ActionEvent evt){
	String name = (String) _namesBox.getSelectedItem();
	if ( name == null ) return;
	InputTableData model = (InputTableData) _tableUIMap.get(name);
	if (DEBUG) System.out.println("Got close instruction for " + _namesBox.getSelectedItem());
	if (model != null){
	  if (model.needsSaving()) {
	    Frame fr = JOptionPane.getFrameForComponent(comp);
	    int opt = JOptionPane.showConfirmDialog(fr,
						    "Do you want to save current table before closing it?",
						    "Table has been modified",
						    JOptionPane.YES_NO_CANCEL_OPTION);
	    if ( opt == JOptionPane.YES_OPTION ) model.save(model.getInputFile());
	    else if (opt == JOptionPane.CANCEL_OPTION) return;
	  }
	  _tableUIMap.remove(model.getInputFile());
	  DefaultComboBoxModel cmodel = (DefaultComboBoxModel) _namesBox.getModel();
	  int index = cmodel.getIndexOf(model.getInputFile());
	  if (index == cmodel.getSize()) index--;
	  cmodel.removeElement(model.getInputFile());
	  if ( cmodel.getSize() > 0 ) _namesBox.setSelectedIndex(index);
	}
      }
    };
    Action loadAllAction = new AbstractAction("Load all tables in directory"){
      public void actionPerformed(ActionEvent evt){
	String fname = VistaUtils.getFilenameFromDialog(comp,FileDialog.LOAD,"table","Tables");
	if ( fname == null ) return;
	File file1 = new File(fname);
	File parentFile = new File(file1.getParent());
	String [] fnames = null;
	if ( parentFile.isDirectory() ){
	  fnames = parentFile.list(new FilenameFilter(){
	    public boolean accept(File dir, String name){
	      return name.endsWith(".table");
	    }
	  });
	} else{
	  return;
	}
	DefaultComboBoxModel cmodel = (DefaultComboBoxModel) _namesBox.getModel();
	if ( fnames != null ){
	  for(int i=0; i < fnames.length; i++){
	    fnames[i] = parentFile.toString()+File.separator+fnames[i];
	    String filename = fnames[i];
	    try {
	      if ( _tableUIMap.containsKey(filename) ) throw new RuntimeException("File "+filename+"Already Open!");
	      InputTableData model= new LookupTableData(filename);
	      _tableUIMap.put(filename,model);
	      cmodel.addElement(filename);
	    } catch (Exception e) {
	      JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
	    }
	  }
	  updateUI();
	}
      }
    };
    Action saveAllAction = new AbstractAction("Save all tables"){
      public void actionPerformed(ActionEvent evt){
	for(Enumeration en = _tableUIMap.keys(); en.hasMoreElements();){
	  String name = (String) en.nextElement();
	  InputTableData model = (InputTableData) _tableUIMap.get(name);
	  if (model.needsSaving()) model.save(model.getInputFile());
	}
      }
    };
    Action closeAllAction = new AbstractAction("Close all tables"){
      public void actionPerformed(ActionEvent evt){
	for(Enumeration en = _tableUIMap.keys(); en.hasMoreElements();){
	  String name = (String) en.nextElement();
	  InputTableData model = (InputTableData) _tableUIMap.get(name);
	  if (model.needsSaving()) {
	  Frame fr = JOptionPane.getFrameForComponent(comp);
	  int opt = JOptionPane.showConfirmDialog(fr,
						  "Do you want to save current table before closing it?",
						  "Table has been modified",
						  JOptionPane.YES_NO_CANCEL_OPTION);
	  if ( opt == JOptionPane.YES_OPTION ) model.save(model.getInputFile());
	  else if ( opt == JOptionPane.CANCEL_OPTION ) return;
	  }
	}
	_tableUIMap.clear();
	_namesBox.setModel(new DefaultComboBoxModel());
	_dataUI.setModel(_emptyModel);
	updateUI();
      }
    };
    JMenu tmenu = new JMenu("Table");
    tmenu.add(loadAction);
    tmenu.add(saveAction);
    tmenu.add(closeAction);
    tmenu.addSeparator();
    tmenu.add(loadAllAction);
    tmenu.add(saveAllAction);
    tmenu.add(closeAllAction);
    JMenuBar mbar = new JMenuBar();
    mbar.add(tmenu);
    return mbar;
  }
}
