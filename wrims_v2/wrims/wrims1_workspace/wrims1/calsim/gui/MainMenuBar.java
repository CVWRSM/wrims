package calsim.gui;
import calsim.app.*;
//import calsim.schematic.*;
//import calsim.gym.Network;
import vista.gui.*;
//import vista.time.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.help.*;
//import javax.swing.border.*;
import calsim.app.Study;
/**
 * The main menu bar the main frame of Calsim OAS GUI
 *
 * @author Yan-Ping Zuo, Nicky Sandhu
 * @version $Id: MainMenuBar.java,v 1.1.2.52.2.1 2002/05/02 01:01:42 adraper Exp $
 */

public class MainMenuBar
{
  public static boolean DEBUG = false;
  public static int [] fileItemKeys = {KeyEvent.VK_C, KeyEvent.VK_X };

  public static int [] projectSubKeys = {KeyEvent.VK_N, KeyEvent.VK_O, KeyEvent.VK_L, KeyEvent.VK_S,
	KeyEvent.VK_A };

  public static int [] openBaseSubKeys = {KeyEvent.VK_F, KeyEvent.VK_B};

  public static int [] editItemKeys = {KeyEvent.VK_T};

  public static int [] DTSItemKeys = {KeyEvent.VK_D};

  public static int [] MTSItemKeys = {KeyEvent.VK_M};

  public static int [] generalItemKeys = {KeyEvent.VK_R};

  public static int [] projectItemKeys = {KeyEvent.VK_W, KeyEvent.VK_V};
  /**
   * constructor
   */
  public MainMenuBar(MainPanel mp) {
    _mainPanel = mp;
    createHelpBroker();
    _systemConsole = null;
    //_systemConsole = new SystemConsole();
    _menuBar = createMenuBar();
  }
  /**
   * Return the main menu bar
   */
  public JMenuBar getMainMenuBar(){
    return _menuBar;
  }
  /**
   *
   */
  void createHelpBroker(){
    try {
      ClassLoader cl = MainPanel.class.getClassLoader();
      URL url = HelpSet.findHelpSet(cl, _helpsetName);
      _mainHS = new HelpSet(cl, url);
    } catch (Exception ee) {
      System.out.println ("Help Set "+_helpsetName+" not found");
      return;
    } catch (ExceptionInInitializerError ex) {
      System.err.println("initialization error:");
      ex.getException().printStackTrace();
    }
    if ( _mainHS !=null ) _mainHB = _mainHS.createHelpBroker();
  }

  /**
   * create menu bar with menus
   */
  public JMenuBar createMenuBar() {
    JMenuBar mbar = new JMenuBar();
    _fileMenu = createFileMenu();
    _editMenu = createEditMenu();
    _viewMenu = createViewMenu();
    _helpMenu = createHelpMenu();
    _debugMenu = createDebugMenu();
    mbar.add(_fileMenu);
    mbar.add(_editMenu);
    mbar.add(_viewMenu);
    mbar.add(_helpMenu);
    mbar.add(_debugMenu);
    return mbar;
  }

  /**
   * create File menu
   */
  JMenu createFileMenu() {
    JMenu menu = new JMenu("File      ");
    JMenuItem com = new JMenuItem("Common Dir");
    com.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Study sty = AppUtils.getCurrentStudy();
				String common = sty.getCommonPath();
				String str = (String) JOptionPane.showInputDialog(GuiUtils.getMainPanel(),
							  "Specify the Common File Directory",
							  "Enter Directory",
							  JOptionPane.PLAIN_MESSAGE,
							  null,
							  null,
							  common);
				if (str == null) {
					return;
				} else {
					sty.setCommonPath(str);
				}
			}
		});
    menu.add(createProjectSubmenu());
    menu.addSeparator();
    menu.add(createStudySubmenu());
    menu.addSeparator();
    menu.add(createOpenBaseMenu());
    menu.add(createOpenCompMenu());
    menu.add(createOpenComp2Menu());
    menu.add(createOpenComp3Menu());
    menu.addSeparator();
    menu.add(createDtsMenu());
    menu.addSeparator();
    menu.add(com);
    menu.addSeparator();
    menu.add(createExitItem());
    menu.setToolTipText("Open project and files");
    menu.setMnemonic('f');
    return menu;
  }

  /**
   * create Project submenu of the File menu
   */
  JMenu createProjectSubmenu() {
    JMenu menu = new JMenu("Project");
    JMenuItem newitem = new JMenuItem("New");  //?? use a loop
    JMenuItem openitem = new JMenuItem("Open");
    JMenuItem loaditem = new JMenuItem("Merge With...");
    JMenuItem saveitem = new JMenuItem("Save");
    JMenuItem saveAsItem = new JMenuItem("Save As");
    newitem.setAccelerator(KeyStroke.getKeyStroke(projectSubKeys[0],KeyEvent.CTRL_MASK));
    openitem.setAccelerator(KeyStroke.getKeyStroke(projectSubKeys[1],KeyEvent.CTRL_MASK));
    loaditem.setAccelerator(KeyStroke.getKeyStroke(projectSubKeys[2],KeyEvent.CTRL_MASK));
    saveitem.setAccelerator(KeyStroke.getKeyStroke(projectSubKeys[3],KeyEvent.CTRL_MASK));
    saveAsItem.setAccelerator(KeyStroke.getKeyStroke(projectSubKeys[4],KeyEvent.CTRL_MASK));
    newitem.setMnemonic('e'); // ?? swing bug, can't have the same mnemonic as the top level menu
    openitem.setMnemonic('o');
    loaditem.setMnemonic('w');
    saveitem.setMnemonic('s');
    newitem.setToolTipText("Create a new project");
    openitem.setToolTipText("Open an existing project");
    loaditem.setToolTipText("Load an existing project");
    saveitem.setToolTipText("Save project");
    saveAsItem.setToolTipText("Save As a new project");
    newitem.addActionListener(new GuiTaskListener("Making New Project...","Opened new project"){
      public void doWork(){
				newProject();
      };
    });
    openitem.addActionListener(new GuiTaskListener("Opening Project..."){
      public void doWork(){
				openProject();
      };
    });
    loaditem.addActionListener(new GuiTaskListener("Loading Project..."){
      public void doWork(){
				loadProject();
      };
    });
    saveitem.addActionListener(new GuiTaskListener("Saving Project..."){
      public void doWork() {
    		try {
    			saveProject();
    		}catch(IOException ioe){
    		  VistaUtils.displayException(_mainPanel,ioe);
    		}
    	};
    });
    saveAsItem.addActionListener(new GuiTaskListener("Saving Project..."){
      public void doWork(){
	    	saveAsProject();
      };
    });
    menu.add(newitem);
    menu.add(openitem);
    menu.add(loaditem);
    menu.addSeparator();
    menu.add(saveitem);
    menu.add(saveAsItem);
    menu.setMnemonic('j');
    return menu;
  }
  /**
   * create Study submenu of the File menu
   */
  JMenu createStudySubmenu() {
    JMenu menu = new JMenu("Study");
    JMenuItem newitem = new JMenuItem("New");  //?? use a loop
    JMenuItem openitem = new JMenuItem("Open");
    JMenuItem saveitem = new JMenuItem("Save");
    JMenuItem saveasitem = new JMenuItem("Save As");
    newitem.addActionListener(new GuiTaskListener("Making New Study...","Opened new study"){
      public void doWork(){
				newStudy();
      };
    });
    openitem.addActionListener(new GuiTaskListener("Opening Study..."){
      public void doWork(){
				openStudy();
      };
    });
    saveitem.addActionListener(new GuiTaskListener("Saving Study..."){
      public void doWork(){
				saveStudy();
      };
    });
    saveasitem.addActionListener(new GuiTaskListener("Saving Study As..."){
      public void doWork(){
				saveAsStudy();
      };
    });
    menu.add(newitem);
    menu.add(openitem);
    menu.addSeparator();
    menu.add(saveitem);
    menu.add(saveasitem);
    return menu;
  }

  /**
   * create Open Base submenu and associated listeners
   */
  JMenu createOpenBaseMenu () {
    JMenu menu = new JMenu("Open Base");
    JMenuItem dvaritem = new JMenuItem("Dvar File");
    JMenuItem svaritem = new JMenuItem("Svar File");
    dvaritem.setAccelerator(KeyStroke.getKeyStroke(openBaseSubKeys[0],KeyEvent.CTRL_MASK));
    svaritem.setAccelerator(KeyStroke.getKeyStroke(openBaseSubKeys[1],KeyEvent.CTRL_MASK));
    dvaritem.setToolTipText("Open base dvar File");
    svaritem.setToolTipText("Open base svar File");
    dvaritem.setMnemonic('i');
    svaritem.setMnemonic('s');
    dvaritem.addActionListener(new GuiTaskListener("Opening Base DV File..."){
      public void doWork(){
				openBaseDVFile();
      }
    });
    svaritem.addActionListener(new GuiTaskListener("Opening Base SV File..."){
      public void doWork(){
				openBaseSVFile();
      }
    });
    menu.add(dvaritem);
    menu.add(svaritem);
    menu.setMnemonic('o');
    return menu;
  }

  /**
   * create Open Comp submenu of the File menu
   */
  JMenu createOpenCompMenu () {
    JMenu menu = new JMenu("Open Comp 1");
    JMenuItem dvaritem = new JMenuItem("Dvar File");
    JMenuItem svaritem = new JMenuItem("Svar File");
    dvaritem.setToolTipText("Open comp dvar File");
    svaritem.setToolTipText("Open comp svar File");
    dvaritem.setMnemonic('i');
    svaritem.setMnemonic('s');
    dvaritem.addActionListener(new GuiTaskListener("Opening Compare DV File..."){
      public void doWork(){
				openCompDVFile();
      }
    });
    svaritem.addActionListener(new GuiTaskListener("Opening Compare SV File..."){
      public void doWork(){
				openCompSVFile();
      }
    });
    menu.add(dvaritem);
    menu.add(svaritem);
    menu.setMnemonic('c');
    return menu;
  }
  /**
   * create Open Comp submenu of the File menu
   */
  JMenu createOpenComp2Menu () {
    JMenu menu = new JMenu("Open Comp 2");
    JMenuItem dvaritem = new JMenuItem("Dvar File");
    JMenuItem svaritem = new JMenuItem("Svar File");
    dvaritem.setToolTipText("Open comp dvar File");
    svaritem.setToolTipText("Open comp svar File");
    dvaritem.setMnemonic('i');
    svaritem.setMnemonic('s');
    dvaritem.addActionListener(new GuiTaskListener("Opening Compare DV File..."){
      public void doWork(){
				openComp2DVFile();
      }
    });
    svaritem.addActionListener(new GuiTaskListener("Opening Compare SV File..."){
      public void doWork(){
				openComp2SVFile();
      }
    });
    menu.add(dvaritem);
    menu.add(svaritem);
    menu.setMnemonic('c');
    return menu;
  }
  /**
   * create Open Comp submenu of the File menu
   */
  JMenu createOpenComp3Menu () {
    JMenu menu = new JMenu("Open Comp 3");
    JMenuItem dvaritem = new JMenuItem("Dvar File");
    JMenuItem svaritem = new JMenuItem("Svar File");
    dvaritem.setToolTipText("Open comp dvar File");
    svaritem.setToolTipText("Open comp svar File");
    dvaritem.setMnemonic('i');
    svaritem.setMnemonic('s');
    dvaritem.addActionListener(new GuiTaskListener("Opening Compare DV File..."){
      public void doWork(){
				openComp3DVFile();
      }
    });
    svaritem.addActionListener(new GuiTaskListener("Opening Compare SV File..."){
      public void doWork(){
				openComp3SVFile();
      }
    });
    menu.add(dvaritem);
    menu.add(svaritem);
    menu.setMnemonic('c');
    return menu;
  }

  /**
   * create Dts Tree menu and items
   */
  JMenu createDtsMenu() {
		JMenu menu = new JMenu("Dts Tree");
		JMenuItem newitem = new JMenuItem("New");
		JMenuItem open = new JMenuItem("Open");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem saveas = new JMenuItem("Save As");
		open.setToolTipText("Opening dts file");
		save.setToolTipText("Saving dts file");
		newitem.addActionListener(new ActionListener() {
	  	public void actionPerformed (ActionEvent ae) {
	    	DefaultMutableTreeNode node = (DefaultMutableTreeNode)DtsTreePanel.getCurrentModel().readData();
        node.removeAllChildren();
        DtsTreePanel.getCurrentModel().setRoot(node);
        Project prj = AppUtils.getCurrentProject();
        prj.setDtsFile("");
        prj.setDtsDir("");
        MessagePanel.setDtsMasterMessage("");
	      String dtsnames[] = prj.getDTSNames();
		    if (dtsnames != null) {
	        for (int i = 0; i < dtsnames.length; i++) {
    	       prj.remove(dtsnames[i]);
          }
        }
        AppUtils.getCurrentProject().clearDTSList();
        AppUtils.getCurrentProject().clearMTSList();
      }
    });
		open.addActionListener(new ActionListener() {
	 	  public void actionPerformed (ActionEvent ae) {
        try {
          startFileDialog();
        } catch (IOException ex) {
        }
      }
    });
    save.addActionListener(new ActionListener() {
	  	public void actionPerformed (ActionEvent ae) {
        try {
          startSaveDialog();
        } catch (IOException ex) {
        }
      }
    });
    saveas.addActionListener(new ActionListener() {
	  	public void actionPerformed (ActionEvent ae) {
        try {
          startSaveAsDialog();
        } catch (IOException ex) {
        }
      }
    });
		menu.add(newitem);
		menu.add(open);
		menu.addSeparator();
		menu.add(save);
		menu.add(saveas);
    return menu;
  }

  public static String filepath = new String();

  public void startFileDialog() throws IOException {
    String filepath = VistaUtils.getFilenameFromDialog(_mainPanel,FileDialog.LOAD,
				      "tree","Tree files (*.tree)");
    if ( filepath == null ) return;
    File file = new File(filepath);
    String filedir = file.getParent();
    MessagePanel.setDtsMasterMessage(filepath);
    AppUtils.getCurrentProject().setDtsFile(filepath);
    AppUtils.getCurrentProject().setDtsDir(filedir);
    try {
      DtsTreeModel _dtm = DtsTreePanel.getCurrentModel();
      _dtm.getFile(filepath,filedir);
      Vector dtsvector = DtsTreeModel.getPrjDts();
      if (dtsvector.elementAt(0) != null) {
  	    for (int i = 0; i<dtsvector.capacity(); i++) {
  		  DerivedTimeSeries dts = (DerivedTimeSeries)dtsvector.elementAt(i);
  		  AppUtils.getCurrentProject().add(dts);
        }
      }
      Vector mtsvector = DtsTreeModel.getPrjMts();
      if (mtsvector.elementAt(0) != null) {
  	    for (int i = 0; i<mtsvector.capacity(); i++) {
  		  MultipleTimeSeries mts = (MultipleTimeSeries)mtsvector.elementAt(i);
  		  AppUtils.getCurrentProject().add(mts);
        }
      }
    } catch (Exception ex) {
      throw new IOException("No File by that name");
    }
  }

  public void startSaveDialog() throws IOException {
    String filepath = AppUtils.getCurrentProject().getDtsPath();
    if (filepath == "") {
      filepath = VistaUtils.getFilenameFromDialog(_mainPanel,FileDialog.SAVE,
					      "tree","Tree File (*.tree)");
	  if ( filepath == null ) return;
   	  File file = new File(filepath);
      String filedir = file.getParent();
      MessagePanel.setDtsMasterMessage(filepath);
      AppUtils.getCurrentProject().setDtsFile(filepath);
      AppUtils.getCurrentProject().setDtsDir(filedir);
    }
    try {
	  DtsTreePanel.getCurrentModel().saveFile(filepath);
    } catch (Exception ex) {
      throw new IOException("Cannot Save File");
    }
  }


  public void startSaveAsDialog() throws IOException {
    String filepath = VistaUtils.getFilenameFromDialog(_mainPanel,FileDialog.SAVE,
					      "tree","Tree files (*.tree)");
	if ( filepath == null ) return;
	File file = new File(filepath);
    String filedir = file.getParent();
    MessagePanel.setDtsMasterMessage(filepath);
    AppUtils.getCurrentProject().setDtsFile(filepath);
    AppUtils.getCurrentProject().setDtsDir(filedir);
    try {
	  DtsTreePanel.getCurrentModel().saveFile(filepath);
    } catch (Exception ex) {
      throw new IOException("Cannot Save File");
    }
  }


  /**
   * create exit item of the File menu
   */
  JMenuItem createExitItem () {
    JMenuItem item = new JMenuItem("Exit");
    item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,KeyEvent.CTRL_MASK));
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
	fileExit();
      }
    });
    item.setToolTipText("Exits this program");
    item.setMnemonic('x');
    return item;
  }


  /**
   * create Edit menu
   */
  JMenu createEditMenu() {
    JMenu menu = new JMenu("Edit      ");
/*    JMenuItem item = new JMenuItem("Time Window");
    item.setAccelerator(KeyStroke.getKeyStroke(editItemKeys[0],KeyEvent.CTRL_MASK));
    item.setMnemonic('w');
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
	editTimeWindow();
      }
    });*/
    //
    JMenu typeItem = createMonthlyTableTypeMenu();
    //
//    item.setToolTipText("Edit time window");
//    JMenu sortItem = createSortMenu();
//    sortItem.setToolTipText("Defines sorting order of years for monthly table");
    //
    //menu.add(createModeMenu());
    //menu.addSeparator();
    //menu.add(createUnitsMenu());
    //menu.addSeparator();
    //menu.add(item);
    //menu.addSeparator();
/*  JMenuItem calendarYearItem = new JRadioButtonMenuItem("Calendar Year");
    JMenuItem waterYearItem    = new JRadioButtonMenuItem("Water Year");
    JMenuItem startmonth 	   = new JRadioButtonMenuItem("Start Month");
    ButtonGroup typeGroup = new ButtonGroup();
    typeGroup.add(calendarYearItem); typeGroup.add(waterYearItem); typeGroup.add(startmonth);
    ActionListener al = new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	    Object source = evt.getSource();
	    if (!( source instanceof JMenuItem )) return;
	    JMenuItem mi = (JMenuItem) source;
	    String txt = mi.getText();
	    if ( txt.equals("Calendar Year") ){
	      AppUtils._isWaterYear = false;
	      AppUtils._isStartMonth = false;
	     } else if (txt.equals("Water Year")) {
	      AppUtils._isWaterYear = true;
	      AppUtils._isStartMonth = false;
	    }
      }
    };
    calendarYearItem.addActionListener(al);
    waterYearItem.addActionListener(al);
//    AppUtils._isWaterYear = false;
    if( AppUtils._isWaterYear )
      waterYearItem.setSelected(true);
    else
      calendarYearItem.setSelected(true);
    */
    JMenuItem formatItem = new JMenuItem("Edit Fraction Digits");
    formatItem.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
				String str = (String) JOptionPane.showInputDialog(GuiUtils.getMainPanel(),
							  "Input Number of Fraction Digits for MT Format",
							  "Enter Integer",
							  JOptionPane.PLAIN_MESSAGE,
							  null,
							  null,
							  "0");
				if (str==null) return;
				int fracDigits = new Integer(str).intValue();
				ReportUtils.setFormat(fracDigits,1,fracDigits,false);
      }
    });

		// Format item for monthly table column width
    JMenuItem formatItem2 = new JMenuItem("Edit Column Width");
    formatItem2.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
				String str = (String) JOptionPane.showInputDialog(GuiUtils.getMainPanel(),
							  "Input Column Width for Monthly Table Format",
							  "Enter Integer",
							  JOptionPane.PLAIN_MESSAGE,
							  null,
							  null,
							  "8");
				if (str==null) return;
				int colwidth = new Integer(str).intValue();
				ReportUtils.setSpaceCount(colwidth);
      }
    });
/*
    startmonth.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    String month = (String)JOptionPane.showInputDialog(GuiUtils.getMainPanel(),
	    						"Choose Beginning Month for Each Year",
								"Month Chooser",
								JOptionPane.PLAIN_MESSAGE,
								null,
								MonthlyReport.getMonths(),
								null);
        AppUtils._startMonth = month;
        AppUtils._isStartMonth = true;
        AppUtils._isWaterYear = false;
	 }});*/
   menu.add(typeItem);
   menu.addSeparator();
   menu.add(formatItem);
   menu.addSeparator();
   menu.add(formatItem2);

    //menu.add(sortItem);
    menu.setToolTipText("Modify time window, units, and mode ...");
    menu.setMnemonic('e');
    return menu;
  }
  /**
   * create Units menu
   */
  JMenu createUnitsMenu() {
    JMenu menu = new JMenu("Units");
    JMenuItem cfs = new JRadioButtonMenuItem(AppUtils.CFS);
    JMenuItem taf = new JRadioButtonMenuItem(AppUtils.TAF);
    _oldUnits = new JRadioButtonMenuItem("Units as Stored");
    ButtonGroup group = new ButtonGroup();
    group.add(cfs);
    group.add(taf);
    //    group.add(_oldUnits);
    cfs.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	//	_oldUnits.setEnabled(false);
	setUnits(AppUtils.CFS);
      }
    });
    taf.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	//	_oldUnits.setEnabled(false);
			setUnits(AppUtils.TAF);
      }
    });
    _oldUnits.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	keepUnits();
      }
    });
    /*    if ( AppUtils.useStoredUnits ){
	_oldUnits.setSelected(AppUtils.useStoredUnits);
	keepUnits();
	}else{*/
	if (AppUtils.useCFS)
	    cfs.doClick();
	else
	    taf.doClick();
	//    }
    menu.add(taf);
    menu.add(cfs);
    //    menu.add(_oldUnits);
    menu.setMnemonic('u');
    return menu;
  }

  /**
   * create Sort menu
   */
  JMenu createSortMenu() {
    JMenu menu = new JMenu("Sort");
    JMenuItem mtnormal = new JRadioButtonMenuItem("Normal");
    JMenuItem mt40_30_30 = new JRadioButtonMenuItem("40-30-30");
    JMenuItem mt60_20_20 = new JRadioButtonMenuItem("60-20-20");
    ButtonGroup group = new ButtonGroup();
    group.add(mtnormal);
    group.add(mt40_30_30);
    group.add(mt60_20_20);
    //
    ActionListener al = new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	Object source = evt.getSource();
	if (!( source instanceof JMenuItem )) return;
	JMenuItem mi = (JMenuItem) source;
	String txt = mi.getText();
	if ( txt.equals("Normal") ){
	  AppUtils._show60_20_20 = false;
	  AppUtils._show40_30_30 = false;
	} else if ( txt.equals("40-30-30") ){
	  AppUtils._show60_20_20 = false;
	  AppUtils._show40_30_30 = true;
	} else if ( txt.equals("60-20-20") ) {
	  AppUtils._show60_20_20 = true;
	  AppUtils._show40_30_30 = false;
	} else {
	  AppUtils._show60_20_20 = false;
	  AppUtils._show40_30_30 = false;
	}
      }
    };
    mtnormal.addActionListener(al);
    mt40_30_30.addActionListener(al);
    mt60_20_20.addActionListener(al);
    if ( AppUtils._show60_20_20 ){
      mt40_30_30.setSelected(true);
    } else if ( AppUtils._show40_30_30 ){
      mt60_20_20.setSelected(true);
    } else {
      mtnormal.setSelected(true);
    }
    menu.add(mtnormal);
    menu.add(mt40_30_30);
    menu.add(mt60_20_20);
    return menu;
  }
  /**
    *
    */
  JMenu createMonthlyTableTypeMenu(){
    JMenu typeItem = new JMenu("Monthly Table Display");
    JMenuItem calendarYearItem = new JRadioButtonMenuItem("Calendar Year");
    JMenuItem waterYearItem    = new JRadioButtonMenuItem("Water Year");
    JMenuItem startmonth 	   = new JRadioButtonMenuItem("Start Month");
    ButtonGroup typeGroup = new ButtonGroup();
    typeGroup.add(calendarYearItem); typeGroup.add(waterYearItem); typeGroup.add(startmonth);
    ActionListener al = new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	    Object source = evt.getSource();
	    if (!( source instanceof JMenuItem )) return;
	    JMenuItem mi = (JMenuItem) source;
	    String txt = mi.getText();
	    if ( txt.equals("Calendar Year") ){
	      AppUtils._isWaterYear = false;
	      AppUtils._isStartMonth = false;
	     } else if (txt.equals("Water Year")) {
	      AppUtils._isWaterYear = true;
	      AppUtils._isStartMonth = false;
	    }
      }
    };
    calendarYearItem.addActionListener(al);
    waterYearItem.addActionListener(al);
//    AppUtils._isWaterYear = false;
    if( AppUtils._isWaterYear )
      waterYearItem.setSelected(true);
    else
      calendarYearItem.setSelected(true);
    /*
    JMenuItem formatItem = new JMenuItem("Edit Fraction Digits");
    formatItem.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
				String str = (String) JOptionPane.showInputDialog(GuiUtils.getMainPanel(),
							  "Input Number of Fraction Digits for MT Format",
							  "Enter Integer",
							  JOptionPane.PLAIN_MESSAGE,
							  null,
							  null,
							  "0");
				if (str==null) return;
				int fracDigits = new Integer(str).intValue();
				ReportUtils.setFormat(fracDigits,1,fracDigits,false);
      }
    });

		// Format item for monthly table column width
    JMenuItem formatItem2 = new JMenuItem("Edit Column Width");
    formatItem2.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
				String str = (String) JOptionPane.showInputDialog(GuiUtils.getMainPanel(),
							  "Input Column Width for Monthly Table Format",
							  "Enter Integer",
							  JOptionPane.PLAIN_MESSAGE,
							  null,
							  null,
							  "8");
				if (str==null) return;
				int colwidth = new Integer(str).intValue();
				ReportUtils.setSpaceCount(colwidth);
      }
    });
*/
    startmonth.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    String month = (String)JOptionPane.showInputDialog(GuiUtils.getMainPanel(),
	    						"Choose Beginning Month for Each Year",
								"Month Chooser",
								JOptionPane.PLAIN_MESSAGE,
								null,
								MonthlyReport.getMonths(),
								null);
        AppUtils._startMonth = month;
        AppUtils._isStartMonth = true;
        AppUtils._isWaterYear = false;
	 }});
    typeItem.add(calendarYearItem);
    typeItem.add(waterYearItem);
    typeItem.add(startmonth);
    //typeItem.add(formatItem);
    //typeItem.add(formatItem2);


    return typeItem;
  }
  /**
   * create Mode menu
   */
  JMenu createModeMenu() {
    JMenu menu = new JMenu("Mode");
    _base = new JRadioButtonMenuItem("Base");
    _compare = new JRadioButtonMenuItem("Compare");
    _diff = new JRadioButtonMenuItem("Difference");
    ButtonGroup group = new ButtonGroup();
    group.add(_base);
    group.add(_compare);
    group.add(_diff);
    _base.setToolTipText("Base mode");
    _compare.setToolTipText("Compare mode");
    _diff.setToolTipText("Diff mode");
    _base.setSelected(true);
    _base.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt){
				setStudyMode("Base");
      }
    });
    _compare.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt){
				setStudyMode("Compare");
      }
    });
    _diff.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt){
				setStudyMode("Difference");
      }
    });
    menu.add(_base);
    menu.add(_compare);
    menu.add(_diff);
    menu.setMnemonic('m');
    return menu;
  }

  /**
   * create View menu
   */
  JMenu createViewMenu() {
    JMenu menu = new JMenu("View      ");
    _tableviewitem = new JCheckBoxMenuItem("Table", AppUtils.viewTable);
    _graphviewitem = new JCheckBoxMenuItem("Plot", AppUtils.viewGraph);
    _mtableviewitem = new JCheckBoxMenuItem("Monthly Table", AppUtils.viewMonthlyTable);
    _tableviewitem.setToolTipText("View data in table format");
    _graphviewitem.setToolTipText("View data in graphic format");
    _mtableviewitem.setToolTipText("View data in standard monthly table format");
    _graphviewitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
				viewGraph(_graphviewitem.isSelected());
      }
    });
    _tableviewitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
				viewTable(_tableviewitem.isSelected());
      }
    });
    _mtableviewitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
				viewMTable(_mtableviewitem.isSelected());
      }
    });
    menu.add(_graphviewitem);
    menu.add(_tableviewitem);
    menu.add(_mtableviewitem);
    menu.setToolTipText("Data view type");
    menu.setMnemonic('v');
    return menu;
  }

  /**
   * create Help menu
   */
  JMenu createHelpMenu() {
    JMenu menu = new JMenu("Help      ");
    JMenuItem aboutItem = new JMenuItem("About WRIMS");//don't include version number here
    JMenuItem contentsItem = new JMenuItem("Contents");
    JMenuItem consoleItem = new JMenuItem("Show Console Messages");
    JMenuItem threadsItem = new JCheckBoxMenuItem("Use Threads");
    GuiTaskListener.USE_THREADS=true;
    threadsItem.setSelected(GuiTaskListener.USE_THREADS);
    JMenu optionsMenu = new JMenu("Options");
    optionsMenu.add(threadsItem);
    optionsMenu.add(consoleItem);
    if ( _systemConsole == null ) consoleItem.setEnabled(false);
    aboutItem.setToolTipText("About the version and authors");
   	contentsItem.setToolTipText("Instructions and tutorial");
    aboutItem.setMnemonic('o');
    contentsItem.setMnemonic('c');
    aboutItem.addActionListener(new GuiTaskListener("Loading help...") {
      public void doWork() {
	      helpAbout();
      }
    });
    consoleItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
	      showConsole();
      }
    });
    threadsItem.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	      JCheckBoxMenuItem mi = (JCheckBoxMenuItem) evt.getSource();
	      GuiTaskListener.USE_THREADS = mi.isSelected();
      }
    });
    contentsItem.addActionListener(new CSH.DisplayHelpFromSource(_mainHB));
    menu.add(contentsItem);
    menu.addSeparator();
    menu.add(optionsMenu);
    menu.addSeparator();
    menu.add(aboutItem);
    menu.addSeparator();
    menu.setToolTipText("Documentation");
    menu.setMnemonic('h');
    return menu;
  }

  JMenu createDebugMenu() {
	    JMenu menu = new JMenu("Debug      ");
	    JMenuItem debugStudy = new JMenuItem("Open File");
	    debugStudy.setMnemonic('o');
	    debugStudy.addActionListener(new GuiTaskListener("Open Study...") {
	      public void doWork() {

		      Study sty = new Study();
		      try {
		        sty.load("D:\\Example_callite_test\\callite_test.sty"); // todo: remove this
		        AppUtils.setCurrentStudy(sty);
		        GuiUtils.getStudyTab().setStudy(sty);
		      }catch(IOException ioe){
		        VistaUtils.displayException(_mainPanel,ioe);
		      }
		      
		      
	      }
	    });
	    menu.add(debugStudy);
	    return menu;
	  }

  
  /**
   * create new study
   */
  void newStudy(){
    if (DEBUG) System.out.println("newStudy");
    Study sty = AppUtils.getCurrentStudy();
    if ( sty.isModified() ) {
      Frame fr = JOptionPane.getFrameForComponent(_mainPanel);
      int opt = JOptionPane.showConfirmDialog(fr,
					      "Do you want to save current study before closing it?",
					      "Study has been modified",
					      JOptionPane.YES_NO_OPTION);
      if ( opt == JOptionPane.YES_OPTION )	saveStudy();
    }
		Study nsty = new Study();
    AppUtils.setCurrentStudy(nsty);
    GuiUtils.getStudyTab().setStudy(nsty);
  }
  /**
   * open study
   */
  void openStudy(){
    String styFile = VistaUtils.getFilenameFromDialog(_mainPanel,FileDialog.LOAD,
						      "sty","Study files (*.sty)");
    if ( styFile == null ) return;
    // open and load project
    Study sty = new Study();
    try {
      sty.load(styFile);
      //DJE********************************************************
      if (!sty.isUpdatedStudyObject()) {
        String msg = styFile + " was created with a previous version of CALSIM.";
        JOptionPane.showMessageDialog(_mainPanel, msg, "Warning", JOptionPane.WARNING_MESSAGE);
      }
      //*************************************************************
      AppUtils.setCurrentStudy(sty);
      GuiUtils.getStudyTab().setStudy(sty);
    }catch(IOException ioe){
      VistaUtils.displayException(_mainPanel,ioe);
    }
  }
  /**
   * save study
   */
  void saveStudy(){
    Study sty = AppUtils.getCurrentStudy();
    GuiUtils.getStudyTab().updateStudy(sty);
    String styFile = sty.getFileName();
    if ( styFile.equals("") ){
      styFile = VistaUtils.getFilenameFromDialog(_mainPanel,FileDialog.SAVE,
						 "sty","Study files (*.sty)");
      if ( styFile == null ) return;
    }
    try{
      if ( styFile.indexOf(".") == -1 )  styFile += ".sty";
      sty.save(styFile);
    }catch(IOException ioe){
      VistaUtils.displayException(_mainPanel,ioe);
    }
  }
  /**
   * save study
   */
  void saveAsStudy(){
    Study sty = AppUtils.getCurrentStudy();
    GuiUtils.getStudyTab().updateStudy(sty);
    String styFile = VistaUtils.getFilenameFromDialog(_mainPanel,FileDialog.SAVE,
						      "sty","Study files (*.sty)");
    if ( styFile == null ) return;
    try{
      if ( styFile.indexOf((int)'.') == -1 )  //no extension
	styFile += ".sty";  //set default extension
      sty.save(styFile);
    }catch(IOException ioe){
      VistaUtils.displayException(_mainPanel,ioe);
    }
  }
  /**
   * create new project
   */
  void newProject(){
    if (DEBUG) System.out.println("newProject");
    Project prj = AppUtils.getCurrentProject();
    if ( prj.isModified() ) {
      Frame fr = JOptionPane.getFrameForComponent(_mainPanel);
      int opt = JOptionPane.showConfirmDialog(fr,
					      "Do you want to save current project before closing it?",
					      "Project has been modified",
					      JOptionPane.YES_NO_OPTION);
      if ( opt == JOptionPane.YES_OPTION )	{
		try {
		  saveProject();
        }catch(IOException ioe){
	      VistaUtils.displayException(_mainPanel,ioe);
	    }
	  }
      String dtsnames[] = prj.getDTSNames();
      if (dtsnames != null) {
        for (int i = 0; i < dtsnames.length; i++) {
   	       prj.remove(dtsnames[i]);
        }
      }
    }
    AppUtils.setCurrentProject(new Project());
		AppUtils.getCurrentProject().clearDTSList();
		AppUtils.getCurrentProject().clearMTSList();
		DerivedTimeSeries[] dts = AppUtils.getGlobalDTSList();
		for (int i=0; i<dts.length; i++) System.out.println(dts[i].getName());
    _mainPanel.getMessagePanel().updateMessagePanel();
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)DtsTreePanel.getCurrentModel().readData();
    node.removeAllChildren();
    DtsTreePanel.getCurrentModel().setRoot(node);
  }
  /**
   *
   */
  void openProject(String prjFile){
    Project prj = AppUtils.getCurrentProject();
    String dtsnames[] = prj.getDTSNames();
    if (dtsnames != null) {
      for (int i = 0; i < dtsnames.length; i++) {
        prj.remove(dtsnames[i]);
      }
    }
    AppUtils.clearDTSList();
    // open and load project
    prj = new Project();  //don't passing the string ?? check the constructor of Project.java
    if (prj.getSVFile() != "") prj.setSVHashtable();
    if (prj.getDVFile() != "") {prj.setDVHashtable();} else {System.out.println(2);};
    try {
      prj.load(prjFile);
      AppUtils.setCurrentProject(prj);
      _mainPanel.getMessagePanel().updateMessagePanel();
    }catch(IOException ioe){
      VistaUtils.displayException(_mainPanel,ioe);
    }
    DtsTreeModel dtm = DtsTreePanel.getCurrentModel();
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)dtm.getRoot();
	  root.removeAllChildren();
	  dtm.setRoot(root);
	  DtsTreeModel.clearVectors();
  }
  /**
   *
   */
  void openProject(){
    String prjFile = VistaUtils.getFilenameFromDialog(_mainPanel,FileDialog.LOAD,
						      "prj","Project files (*.prj)");
    if ( prjFile == null ) return;
    Project prj = AppUtils.getCurrentProject();
    String dtsnames[] = prj.getDTSNames();
    if (dtsnames != null) {
      for (int i = 0; i < dtsnames.length; i++) {
        prj.remove(dtsnames[i]);
      }
    }
    AppUtils.clearDTSList();
    DtsTreeModel dtm = DtsTreePanel.getCurrentModel();
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)dtm.getRoot();
    root.removeAllChildren();
    dtm.setRoot(root);
    DtsTreeModel.clearVectors();
    //Project currentprj = AppUtils.getCurrentProject();
    // open and load project
    prj = new Project();
    try {
      prj.load(prjFile);
      MessagePanel.setDtsMasterMessage(prj.getDtsPath());
      AppUtils.setCurrentProject(prj);
      _mainPanel.getMessagePanel().updateMessagePanel();
    }catch(IOException ioe){
      VistaUtils.displayException(_mainPanel,ioe);
    }
    if (prj.getSVFile() != "") prj.setSVHashtable();
    if (prj.getDVFile() != "") {prj.setDVHashtable();}
    //prj.setBParts();
  }
  /**
   * load project
   */
  void loadProject(){
    String prjFile = VistaUtils.getFilenameFromDialog(_mainPanel,FileDialog.LOAD,
						      "prj","Project files (*.prj)");
    if ( prjFile == null ) return;
    // open and load project
    Project prj = AppUtils.getCurrentProject();
    try {
      prj.load(prjFile);
      _mainPanel.getMessagePanel().updateMessagePanel();
    }catch(IOException ioe){
      VistaUtils.displayException(_mainPanel,ioe);
    }
    AppUtils.setCurrentProject(prj);
  }
  /**
   * save project
   */
  void saveProject() throws IOException {
    Project prj = AppUtils.getCurrentProject();
    String prjFile = prj.getFilename();
    if ( prjFile.equals("") ){
      prjFile = VistaUtils.getFilenameFromDialog(_mainPanel,FileDialog.SAVE,
						 "prj","Project files (*.prj)");
      if ( prjFile == null ) return;
    }
//    String treefile = _mainPanel.getMessagePanel().getDtsMessage();
    String treefile = MessagePanel.getDtsMessage();
    try{
      if (treefile.equals("")) {
	    int end = prjFile.lastIndexOf("\\");
	    String treefile2 = new String(prjFile.substring(0,end) + "\\new-file.tree");
        MessagePanel.setDtsMasterMessage(treefile2);
        AppUtils.getCurrentProject().setDtsFile(treefile2);
        AppUtils.getCurrentProject().setDtsDir(treefile2);
        DtsTreePanel.getCurrentModel().saveFile(treefile2);
      }
        if ( prjFile.indexOf(".") == -1 )  prjFile += ".prj";
        prj.save(prjFile);
        _mainPanel.getMessagePanel().updateMessagePanel();
    }catch(IOException ioe){
      VistaUtils.displayException(_mainPanel,ioe);
    }
  }
  /**
   * save project
   */
  void saveAsProject(){
    String prjFile = VistaUtils.getFilenameFromDialog(_mainPanel,FileDialog.SAVE,
						      "prj","Project files (*.prj)");
    if ( prjFile == null ) return;
    Project prj = AppUtils.getCurrentProject();
//  String treefile = _mainPanel.getMessagePanel().getDtsMessage();
    String treefile = MessagePanel.getDtsMessage();
    try{
      if (treefile.equals("")) {
	    int end = prjFile.lastIndexOf("\\");
	    String treefile2 = new String(prjFile.substring(0,end) + "\\new-file.tree");
        MessagePanel.setDtsMasterMessage(treefile2);
        AppUtils.getCurrentProject().setDtsFile(treefile2);
        AppUtils.getCurrentProject().setDtsDir(treefile2);
        DtsTreePanel.getCurrentModel().saveFile(treefile2);
      }
      if ( prjFile.indexOf((int)'.') == -1 )  //no extension
 	  prjFile += ".prj";  //set default extension
      prj.save(prjFile);
      _mainPanel.getMessagePanel().updateMessagePanel();
    }catch(IOException ioe){
      VistaUtils.displayException(_mainPanel,ioe);
    }
  }
  /**
   * Open a base Dvar dss file
   */
  static void openBaseDVFile(){
//CB    String filename = VistaUtils.getFilenameFromDialog(_mainPanel, FileDialog.LOAD, "dss", "Base DV File");
    String filename = VistaUtils.getFilenameFromDialog(_mainPanel, FileDialog.LOAD, "dss", "Base DV File");
    if (filename == null) return;
    Project prj = AppUtils.getCurrentProject();
    prj.setDVFile(filename);
    prj.setDVHashtable();
    _mainPanel.getMessagePanel().updateMessagePanel();
  }

  /**
   * Open a base Svar dss file
   */
  static void openBaseSVFile(){
    String filename = VistaUtils.getFilenameFromDialog(_mainPanel, FileDialog.LOAD, "sv.dss", "Base SV File");
    if (filename == null) return;
    Project prj = AppUtils.getCurrentProject();
    prj.setSVFile(filename);
    prj.setSVHashtable();
    _mainPanel.getMessagePanel().updateMessagePanel();
  }
  /**
   * Open a Dvar dss file for comparison
   */
  static void openCompDVFile(){
//CB    String filename = VistaUtils.getFilenameFromDialog(_mainPanel, FileDialog.LOAD, "dv.dss", "Compare DV File");
    String filename = VistaUtils.getFilenameFromDialog(_mainPanel, FileDialog.LOAD, "dss", "Compare DV File");
    if (filename == null) return;
    Project prj = AppUtils.getCurrentProject();
    prj.setDV2File(filename);
    _mainPanel.getMessagePanel().updateMessagePanel();
  }
  /**
   * Open a Svar dss file for comparison
   */
  static void openCompSVFile(){
    String filename = VistaUtils.getFilenameFromDialog(_mainPanel, FileDialog.LOAD, "sv.dss", "Compare SV File");
    if (filename == null) return;
    Project prj = AppUtils.getCurrentProject();
    prj.setSV2File(filename);
    _mainPanel.getMessagePanel().updateMessagePanel();
  }
  /**
   * Open a Dvar dss file for comparison
   */
  static void openComp2DVFile(){
//CB    String filename = VistaUtils.getFilenameFromDialog(_mainPanel, FileDialog.LOAD, "dv.dss", "Compare DV File");
    String filename = VistaUtils.getFilenameFromDialog(_mainPanel, FileDialog.LOAD, "dss", "Compare DV File");
    if (filename == null) return;
    Project prj = AppUtils.getCurrentProject();
    prj.setDV3File(filename);
    _mainPanel.getMessagePanel().updateMessagePanel();
  }
  /**
   * Open a Svar dss file for comparison
   */
  static void openComp2SVFile(){
    String filename = VistaUtils.getFilenameFromDialog(_mainPanel, FileDialog.LOAD, "sv.dss", "Compare SV File");
    if (filename == null) return;
    Project prj = AppUtils.getCurrentProject();
    prj.setSV3File(filename);
    _mainPanel.getMessagePanel().updateMessagePanel();
  }
  /**
   * Open a Dvar dss file for comparison
   */
  static void openComp3DVFile(){
//CB    String filename = VistaUtils.getFilenameFromDialog(_mainPanel, FileDialog.LOAD, "dv.dss", "Compare DV File");
	String filename = VistaUtils.getFilenameFromDialog(_mainPanel, FileDialog.LOAD, "dss", "Compare DV File");
    if (filename == null) return;
    Project prj = AppUtils.getCurrentProject();
    prj.setDV4File(filename);
    _mainPanel.getMessagePanel().updateMessagePanel();
  }
  /**
   * Open a Svar dss file for comparison
   */
  static void openComp3SVFile(){
    String filename = VistaUtils.getFilenameFromDialog(_mainPanel, FileDialog.LOAD, "sv.dss", "Compare SV File");
    if (filename == null) return;
    Project prj = AppUtils.getCurrentProject();
    prj.setSV4File(filename);
    _mainPanel.getMessagePanel().updateMessagePanel();
  }

  /**
   * close the main frame and exit.
   * if the project has been modified without saving, an warning will be displayed in a message
   * dialog box.
   */
  void fileExit(){
    if (DEBUG) System.out.println("fileExit");
    // save all the properties anyways
    calsim.gui.CalsimGui.saveProps();
    calsim.app.AppUtils.saveProps();
    //
    Project prj = AppUtils.getCurrentProject();
    if ( prj.isModified() ) {
      Frame fr = JOptionPane.getFrameForComponent(_mainPanel);
      int opt = JOptionPane.showConfirmDialog(fr,
					      "Really want to exit?",
					      "Project has been modified",
					      JOptionPane.YES_NO_OPTION);
      if ( opt == JOptionPane.YES_OPTION ) System.exit(0);
    } else {
      System.exit(0);
    }
  }

  /**
   * Open a time window selection panel, get the time window, and send it to package app.
   */
  void editTimeWindow(){
    if (DEBUG) System.out.println("editTimeWindow");
    try {
      String msg = "TimeWindow: " + "\n"
	//+ "(ex. 'OCT1921 - SEP1994')";
	+ "(ex. 'OCT1921 - SEP2003')";
      String [] twSelections = {
	_mainPanel.getMessagePanel().getTimeWindowString(),
	"OCT1921 - SEP1994",
	"MAY1928 - OCT1934",
	"JUN1986 - SEP1992",
	"OCT1975 - SEP1977",
	"OCT1983 - SEP1993"
      };
      //
      JOptionPane pane = new JOptionPane(msg, JOptionPane.INFORMATION_MESSAGE,
					 JOptionPane.OK_OPTION,
					 null, null, null);
      pane.setWantsInput(true);
      pane.setSelectionValues(twSelections);
      pane.setInitialSelectionValue(_mainPanel.getMessagePanel().getTimeWindowString());
      // show the dialog
      JDialog dialog = pane.createDialog(_mainPanel,"Time Window Selection");
      pane.selectInitialValue();
      // search for JComboBox and make it editable
      JComboBox jcbox = (JComboBox) GuiUtils.getComponent(JComboBox.class,
							  dialog.getContentPane());
      jcbox.setEditable(true);
      dialog.setVisible(true);
      //
      String twstr = (String) jcbox.getEditor().getItem();//pane.getInputValue();
      if ( twstr == null ) return;
      //send timewindow to app
      AppUtils.getCurrentProject().setTimeWindow(twstr);
      _mainPanel.getMessagePanel().updateMessagePanel();
    }catch(Exception e){
      VistaUtils.displayException(_mainPanel,e);
    }
  }

  /**
   * send status of check box of view type to package app.
   * If the last selected view type is unselected, then re-select it, don't send the status to
   * app, and display a worning message.
   */
   void viewGraph(boolean isSelected){
    if (DEBUG) System.out.println("viewGraph: " + isSelected);
    if (GuiUtils.noCheckBoxMenuItemIsChecked(_graphviewitem, _tableviewitem, _mtableviewitem) == false)
      AppUtils.viewGraph = isSelected;
    else{
      _graphviewitem.setSelected(true);
      JOptionPane.showMessageDialog(_mainPanel,
                                    "You need select at least one view type to view the data!",
                                    "Warning",
                                    JOptionPane.WARNING_MESSAGE);
    }
  }

  /**
   * send status of check box of view type to package app.
   */
  void viewTable(boolean isSelected){
    if (DEBUG) System.out.println("viewTable " + isSelected);
    if (GuiUtils.noCheckBoxMenuItemIsChecked(_graphviewitem, _tableviewitem, _mtableviewitem) == false)
      AppUtils.viewTable = isSelected;
    else{
      _tableviewitem.setSelected(true);
      JOptionPane.showMessageDialog(_mainPanel,
                                    "You need select at least one view type to view the data!",
                                    "Warning",
                                    JOptionPane.WARNING_MESSAGE);
    }
  }

  /**
   * send status of check box of view type to package app.
   */
  void viewMTable(boolean isSelected){
    if (DEBUG) System.out.println("viewMTable " + isSelected);
    if (GuiUtils.noCheckBoxMenuItemIsChecked(_graphviewitem, _tableviewitem, _mtableviewitem) == false)
      AppUtils.viewMonthlyTable  = isSelected;
    else{
      _mtableviewitem.setSelected(true);
      JOptionPane.showMessageDialog(_mainPanel,
                                    "You need select at least one view type to view the data!",
                                    "Warning",
                                    JOptionPane.WARNING_MESSAGE);
    }
  }
  /**
   *
   */
  void setStudyMode(String msg){
    //send mode to app
    if (GuiUtils.isComparable() && ( msg.equals("Compare") || msg.equals("Difference"))) {
	if ( msg.equals("Compare") ){
	    AppUtils.plotComparitive = true;
	    AppUtils.plotDifference = false;
	    _compare.setSelected(true);
	} else if ( msg.equals("Difference")){
	    AppUtils.plotComparitive = false;
	    AppUtils.plotDifference = true;
	    _diff.setSelected(true);
	}
	_mainPanel.getMessagePanel().setModeMessage(msg+ " Mode");
    } else{
	if ( msg.equals("Base") ){
	    AppUtils.plotComparitive = false;
	    AppUtils.plotDifference = false;
	    _mainPanel.getMessagePanel().setModeMessage(msg+ " Mode");
	    _base.setSelected(true);
	} else {
	    JOptionPane.showMessageDialog(_mainPanel,
					  "Not all the four DSS fils are opened!\n"
					  + "Go to the File menu to open more DSS file(s)",
					  "Warning",
					  JOptionPane.WARNING_MESSAGE);
	    _base.setSelected(true);
	    return;
	}
    }
  }
  /**
   *
   */
  void showConsole(){
    if ( _systemConsole != null ) _systemConsole.setVisible(true);
  }
  /**
   * display version and authers
   */
  void helpAbout(){
    if (DEBUG) System.out.println("helpAbout");
    ImageIcon dwrIcon =
      new ImageIcon(VistaUtils.getImageAsBytes("/calsim/gui/DWR_Logo-1.0in.gif"));
    JOptionPane.showMessageDialog(_mainPanel,
				  GuiUtils.getVersionName() + "\n"
				  + GuiUtils.getLocaleDescription() + "\n"
	              //+ "Authors: Armin Munevar, Nicky Sandhu, Yan-Ping Zuo" + "\n"
 				  + "Copyright (C) 1998, 2001, 2010 State of California, Department of Water Resources.",
				  "About",
				  JOptionPane.INFORMATION_MESSAGE,
				  dwrIcon);
  }
  /**
   *
   */
  void setUnits(String units){
    AppUtils.useUnits(units);
		if ( _mainPanel.getMessagePanel()!=null ) {
			_mainPanel.getMessagePanel().updateMessagePanel();
		}
  }
  /**
   *
   */
  void keepUnits(){
    AppUtils.useStoredUnits();
  }
  /**
   * Get the File menu in the main menu bar
   */
  public JMenu getFileMenu(){
    return _fileMenu;
  }
  /**
   * Get the Edit menu in the main menu bar
   */
  public JMenu getEditMenu(){
    return _editMenu;
  }
  /**
   * Get the View menu in the main menu bar
   */
  public JMenu getViewMenu(){

    return _viewMenu;
  }
  /**
   * Get the Help menu in the main menu bar
   */
  public JMenu getHelpMenu(){
    return _helpMenu;
  }

  public JMenu getDebugMenu(){
	    return _debugMenu;
	  }
  /*
   * Fields
   */
  private JRadioButtonMenuItem _base, _compare, _diff;
  private JCheckBoxMenuItem _tableviewitem, _graphviewitem, _mtableviewitem;
  private JMenuItem _oldUnits;
  static final String _helpsetName = "CalsimHelp";
  static final String _helpsetLabel = "Calsim OAS - Help";
  //
  HelpSet _mainHS = null;
  HelpBroker _mainHB;
  private JMenuBar _menuBar;
  static MainPanel _mainPanel;
  private JMenu _fileMenu, _editMenu, _viewMenu, _helpMenu, _debugMenu;
  private SystemConsole _systemConsole;
} //end of class MainMenuBar
