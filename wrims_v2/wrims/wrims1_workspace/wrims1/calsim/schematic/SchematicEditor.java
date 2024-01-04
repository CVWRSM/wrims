package calsim.schematic;
//import calsim.app.*;
import calsim.gym.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import vista.gui.*;
import vista.graph.ImageSerializer;
import java.util.*;
/**
  *
  *
  * @author Nicky Sandhu
  * @version $Id: SchematicEditor.java,v 1.1.2.8 2000/04/14 16:36:05 amunevar Exp $
	Last change:  AM   25 Feb 2000    4:17 pm
  */
public class SchematicEditor {
  CalsimSchematicCanvas _csc = null;
  boolean _modified = false;
  JPanel _mainPanel;
  JButton _saveButton;
  DragHandler _dragHandler;
  public SchematicEditor(String netFile, String xyFile){
    // read network and xy data and create schematic canvas
    Network net = null;
    NetworkSchematicData nsd = null;
    try {
      net = Network.read(netFile);
      nsd = new NetworkSchematicData(net,xyFile);
    }catch(Exception e){
      System.err.println("Could not load schematic data from " + netFile + " & " + xyFile);
      net = new Network();
      nsd = new NetworkSchematicData(net,null);
    }
    CalsimSchematic cs = new CalsimSchematic(nsd);
    _csc = new CalsimSchematicCanvas(cs);
    // create action buttons
    ImageIcon dragIcon = new ImageIcon(VistaUtils.getImageAsBytes("/calsim/schematic/drag.gif"));
    ImageIcon addStorageIcon = new ImageIcon(VistaUtils.getImageAsBytes("/calsim/schematic/storage.gif"));
    ImageIcon addJunctionIcon = new ImageIcon(VistaUtils.getImageAsBytes("/calsim/schematic/junction.gif"));
    ImageIcon addHiddenIcon = new ImageIcon(VistaUtils.getImageAsBytes("/calsim/schematic/hidden.gif"));
    ImageIcon addChannelIcon = new ImageIcon(VistaUtils.getImageAsBytes("/calsim/schematic/channel.gif"));
    ImageIcon addDemandIcon = new ImageIcon(VistaUtils.getImageAsBytes("/calsim/schematic/demand.gif"));
    ImageIcon addReturnIcon = new ImageIcon(VistaUtils.getImageAsBytes("/calsim/schematic/return.gif"));
    ImageIcon addInputIcon = new ImageIcon(VistaUtils.getImageAsBytes("/calsim/schematic/input.gif"));
    ImageIcon deleteNodeIcon = new ImageIcon(VistaUtils.getImageAsBytes("/calsim/schematic/deleteNode.gif"));
    ImageIcon deleteArcIcon = new ImageIcon(VistaUtils.getImageAsBytes("/calsim/schematic/deleteArc.gif"));
    //
    ImageIcon saveIcon = new ImageIcon(VistaUtils.getImageAsBytes("/calsim/schematic/save.gif"));
    ImageIcon openIcon= new ImageIcon(VistaUtils.getImageAsBytes("/calsim/schematic/open.gif"));
    ImageIcon newIcon = new ImageIcon(VistaUtils.getImageAsBytes("/calsim/schematic/new.gif"));
    // create buttons with listeners
    JToggleButton dragButton = new JToggleButton(dragIcon);
    _dragHandler = new DragHandler(_csc);
    dragButton.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	_csc.removeAllHandlers();
	_csc.add(_dragHandler);
      }
    });
    JToggleButton addStorageButton = new JToggleButton(addStorageIcon);
    addStorageButton.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	String id = JOptionPane.showInputDialog("Please input the storage node id: ");
	if ( id == null ) return;
	int number = -1;
	try {
	  number = new Integer(id).intValue();
	  if ( number < 0 ) return;
	}catch(Exception e){
	  return;
	}
	_csc.removeAllHandlers();
	_csc.add(new AddNodeHandler(_csc,id,"storage"));
      }
    });
    JToggleButton addJunctionButton = new JToggleButton(addJunctionIcon);
    addJunctionButton.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	String id = JOptionPane.showInputDialog("Please input the junction node id: ");
//	String id = "1";
	if ( id == null ) return;
	int number = -1;
	try {
	  number = new Integer(id).intValue();
	  if ( number < 0 ) return;
	}catch(Exception e){
	  return;
	}
	_csc.removeAllHandlers();
	_csc.add(new AddNodeHandler(_csc,id,"junction"));
      }
    });
    JToggleButton addHiddenButton = new JToggleButton(addHiddenIcon);
    addHiddenButton.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	String id = JOptionPane.showInputDialog("Please input the hidden node id: ");
	if ( id == null ) return;
	int number = -1;
	try {
	  number = new Integer(id).intValue();
	  if ( number < 0 ) return;
	}catch(Exception e){
	  return;
	}
	_csc.removeAllHandlers();
	_csc.add(new AddNodeHandler(_csc,id,"hidden"));
      }
    });
    JToggleButton addChannelButton = new JToggleButton(addChannelIcon);
    addChannelButton.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	String id = JOptionPane.showInputDialog("Please input the channel arc name: ");
	if ( id == null ) return;
	_csc.removeAllHandlers();
	_csc.add(new AddArcHandler(_csc,id,"flow"));
      }
    });
    JToggleButton addInputButton = new JToggleButton(addInputIcon);
    addInputButton.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	String id = JOptionPane.showInputDialog("Please input the input arc name: ");
	if ( id == null ) return;
	_csc.removeAllHandlers();
	_csc.add(new AddArcHandler(_csc,id,"inflow"));
      }
    });
    JToggleButton addDemandButton = new JToggleButton(addDemandIcon);
    addDemandButton.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	String id = JOptionPane.showInputDialog("Please input the demand arc name: ");
	if ( id == null ) return;
	_csc.removeAllHandlers();
	_csc.add(new AddArcHandler(_csc,id,"demand"));
      }
    });
    JToggleButton addReturnButton = new JToggleButton(addReturnIcon);
    addReturnButton.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	String id = JOptionPane.showInputDialog("Please input the return arc name: ");
	if ( id == null ) return;
	_csc.removeAllHandlers();
	_csc.add(new AddArcHandler(_csc,id,"return"));
      }
    });
    JToggleButton deleteNodeButton = new JToggleButton(deleteNodeIcon);
    deleteNodeButton.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	try {
	  _csc.removeAllHandlers();
	  _csc.add(new RemoveNodeHandler(_csc));
	}catch(Exception e){
	  VistaUtils.displayException(_csc,e);
	}
      }
    });
    JToggleButton deleteArcButton = new JToggleButton(deleteArcIcon);
    deleteArcButton.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	_csc.removeAllHandlers();
	_csc.add(new RemoveArcHandler(_csc));
      }
    });
    //
    _saveButton = new JButton(saveIcon);
    _saveButton.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	String id = VistaUtils.getFilenameFromDialog(_csc,FileDialog.SAVE,
						     "net","Network Files");
	if ( id == null ) return;
	int dotIndex = id.indexOf(".net");
	if ( dotIndex >= 0 ){
	  id = id.substring(0,dotIndex);
	}
	System.out.println("Saving to " + id);
	try {
	  NetworkSchematicData nsd2 = 
	    (NetworkSchematicData) _csc.getSchematic().getModel();
	  Network net2 = nsd2.getNetwork();
	  net2.write(id+".net");
	  nsd2.writeXY(net2,id+".xy");
	}catch(Exception ioe){
	  VistaUtils.displayException((Component) evt.getSource(),ioe);
	}
      }
    });
    JButton openButton = new JButton(openIcon);
    openButton.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	// first query user for save on open schematic
	int option = JOptionPane.showConfirmDialog
	  (_csc,"Do you want to save current schematic?","Query",JOptionPane.YES_NO_OPTION);
	if ( option == JOptionPane.YES_OPTION ){
	  _saveButton.doClick();
	}
	//
	String id = VistaUtils.getFilenameFromDialog(_csc,FileDialog.LOAD,
						     "net","Network Files");
	if ( id == null ) return;
	int dotIndex = id.indexOf(".net");
	if ( dotIndex >= 0 ){
	  id = id.substring(0,dotIndex);
	}
	System.out.println("Opening from " + id);
	try {
	  Network net2 = Network.read(id+".net");
	  NetworkSchematicData model = new NetworkSchematicData(net2,id+".xy");
	  CalsimSchematic cs1 = new CalsimSchematic(model);
	  _csc = new CalsimSchematicCanvas(cs1);
	  _dragHandler.setSchematicCanvas(_csc);
	  _mainPanel.add(_csc,BorderLayout.CENTER);
	}catch(Exception e){
	  VistaUtils.displayException((Component) evt.getSource(),e);
	}
      }
    });
    JButton newButton = new JButton(newIcon);
    newButton.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	// first query user for save on open schematic
	int option = JOptionPane.showConfirmDialog
	  (_csc,"Do you want to save current schematic?","Query",JOptionPane.YES_NO_OPTION);
	if ( option == JOptionPane.YES_OPTION ){
	  _saveButton.doClick();
	}
	//
	try {
	  Network net2 = new Network();
	  NetworkSchematicData model = new NetworkSchematicData(net2,null);
	  _dragHandler.setSchematicCanvas(_csc);
	  _csc.getSchematic().setModel(model);
	  _csc.getCanvas().redoNextPaint();
	  _csc.getCanvas().repaint();
	}catch(Exception e){
	  VistaUtils.displayException((Component) evt.getSource(),e);
	}
      }
    });
    // create button group
    ButtonGroup bg = new ButtonGroup();
    //
    //
    bg.add(dragButton);
    bg.add(addStorageButton);
    bg.add(addJunctionButton);
    bg.add(addHiddenButton);
    bg.add(addChannelButton);
    bg.add(addInputButton);
    bg.add(addDemandButton);
    bg.add(addReturnButton);
    bg.add(deleteNodeButton);
    bg.add(deleteArcButton);
    // create tool bar 
    JToolBar tb = new JToolBar();
    tb.setFloatable(true);
    tb.add(dragButton);
    tb.add(addStorageButton);
    tb.add(addJunctionButton);
    tb.add(addHiddenButton);
    tb.add(addChannelButton);
    tb.add(addInputButton);
    tb.add(addDemandButton);
    tb.add(addReturnButton);
    tb.add(deleteNodeButton);
    tb.add(deleteArcButton);
    tb.add(_saveButton);
    tb.add(openButton);
    tb.add(newButton); 
    //
    JMenuItem sizeEditMenu = new JMenuItem("Set Size...");
    sizeEditMenu.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	try {
	  String id = 
	    JOptionPane.showInputDialog("Please input a pair of numbers x and y: ");
	  StringTokenizer st = new StringTokenizer(id);
	  if ( st.countTokens() != 2 )
	    throw new RuntimeException("Please enter an x and y pair separated by whitespace");
	  int xs = new Integer(st.nextToken()).intValue();
	  int ys = new Integer(st.nextToken()).intValue();
	  _csc.getSchematic().setPreferredSize(xs,ys);
	  Frame fr = JOptionPane.getFrameForComponent(_csc);
	  fr.invalidate();
	  fr.doLayout();
	  fr.repaint();
	}catch(Exception e){
	  VistaUtils.displayException((Component) evt.getSource(),e);
	}
      }
    });
    JMenuItem gridSizeItem = new JMenuItem("Set Grid Size...");
    gridSizeItem.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	try {
	  String id = 
	    JOptionPane.showInputDialog("Please the pixel size for the grid resolution: ");
	  StringTokenizer st = new StringTokenizer(id);
	  int xs = new Integer(st.nextToken()).intValue();
	  _dragHandler.setDragXIncrement(xs);
	  _dragHandler.setDragYIncrement(xs);
	}catch(Exception e){
	  VistaUtils.displayException((Component) evt.getSource(),e);
	}
      }
    });
    JMenuItem saveToGifItem = new JMenuItem("Save to gif...");
    saveToGifItem.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	JComponent comp = (JComponent) evt.getSource();
	String file = VistaUtils.getFilenameFromDialog(comp,FileDialog.SAVE,
						       "gif","GIF Files");
	if ( file == null ) return;
	new ImageSerializer(file,_csc.getCanvas(),ImageSerializer.GIF).run();
      }
    });
    JCheckBoxMenuItem showHiddenAction = new JCheckBoxMenuItem("Show Hidden");
    showHiddenAction.setSelected(false);
    showHiddenAction.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	JCheckBoxMenuItem mi = (JCheckBoxMenuItem) evt.getSource();
	_csc.getSchematic().setHiddenVisible( mi.isSelected() );
	_csc.redraw();
      }
    });
    JMenu editMenu = new JMenu("Edit");
    editMenu.add(sizeEditMenu);
    editMenu.add(gridSizeItem);
    editMenu.add(saveToGifItem);
    editMenu.add(showHiddenAction);
    JMenuBar mbar = new JMenuBar();
    mbar.add(editMenu);
    // create main panel
    _mainPanel = new JPanel();
    _mainPanel.setLayout(new BorderLayout());
    _mainPanel.add(_csc,BorderLayout.CENTER);
    _mainPanel.add(tb,BorderLayout.SOUTH);
    // create frame and display
    JFrame fr = new JFrame();
    fr.setTitle("Schematic Editor v0.2");
    fr.setJMenuBar(mbar);
    fr.getContentPane().add(_mainPanel);
    fr.setSize(800,800);
    fr.setVisible(true);
    fr.addWindowListener( new WindowAdapter(){
      public void windowClosing(WindowEvent evt){ 
	_saveButton.doClick();
	super.windowClosing(evt);
      }
    });
    // activate drag by default
    dragButton.doClick();
  }
  /**
    *
    */
  public CalsimSchematicCanvas getSchematicCanvas(){
    return _csc;
  }
  /**
    *
    */
  public static void main(String [] args){
    if ( args == null || args.length != 2){
      System.out.println("Usage: java SchematicEditor network.net node.xy");
      return;
    }
    SchematicEditor editor = new SchematicEditor(args[0],args[1]);
    Frame fr = JOptionPane.getFrameForComponent(editor.getSchematicCanvas());
    fr.addWindowListener( new WindowAdapter(){
      public void windowClosing(WindowEvent evt){ 
	System.exit(0);
      }
    });
  }
}
