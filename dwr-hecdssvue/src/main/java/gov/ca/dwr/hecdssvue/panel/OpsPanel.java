package gov.ca.dwr.hecdssvue.panel;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.components.ShowSelected;
import gov.ca.dwr.hecdssvue.dialog.InsertTimeWindowDialog;
import gov.ca.dwr.hecdssvue.views.DSSCatalogView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import gov.ca.dwr.hecdssvue.dts.AppUtils;
import gov.ca.dwr.hecdssvue.dts.Project;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.tools.TimeOperation;

public class OpsPanel extends JPanel {

	private Vector<String> _twitems = new Vector<String>(1, 1);
	
	// added
	//String _timeWindow = DEFAULT_TIME_WINDOW;
	private JTextField[] _pathText;

	private JButton _filterBtn = null;
	private JRadioButton _plotBtn = null;
	private JRadioButton _exceedBtn = null;
	private JRadioButton _annualTotBtn = null;
	private JRadioButton _annualTotExceedBtn = null;
	private JRadioButton _monthlyAvgBtn = null; 

	private JList _monthlist;

	private JRadioButton taf = new JRadioButton("TAF");
	private JRadioButton cfs = new JRadioButton("CFS");

	private JRadioButton comp = new JRadioButton(DssPluginCore.comp);
	private JRadioButton diff = new JRadioButton(DssPluginCore.diff);

	private JRadioButton wateryear = new JRadioButton("oct - sep"); 
	private JRadioButton calendar = new JRadioButton("jan - dec"); ; 
	private JRadioButton fedContract = new JRadioButton("mar - feb");  
	
	private static String[] labelText = {
		"Mode: ", 
		"  Time Window: ", 
		"  Units:  ",
		"  Annual Type: " };
	
	
	private JLabel labelNames[];
	
	private Font f;
	
	public OpsPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); 
		add(createUpperPanel());
		add(createLowerPanel());
	}

	/**
	 * 
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JCheckBox) {
			String name = ((JCheckBox) e.getSource()).getText();
			// TODO: figure out what I was planning or trying to do here
		}
	}

	public void refreshFilter(int dssType) {
		for (int i = 0; i < 6; i++)
			_pathText[i].setText("");
	}

	public static int binarySearch(String[][] array, String key, int column) {
		return binarySearch(array, 0, array.length, key, column);
	}

	public static int binarySearch(String[][] array, int lowerbound,
			int upperbound, String key, int column) {
		int position;
		int compare_count = 1;

		// calculate initial search position.
		position = (lowerbound + upperbound) / 2;

		while (!(array[position][column].equalsIgnoreCase(key))
				&& (lowerbound < upperbound)) {
			compare_count++;
			// if the value in the search position is greater than the key
			// change upperbound to search position minus one.
			if (array[position][column].compareTo(key) > 0) { // CB
				// compareToIgnoreCase
				// does NOT work
				// here
				upperbound = position - 1; //
			} else { // change lowerbound to search position plus one.
				lowerbound = position + 1;
			}
			position = (lowerbound + upperbound) / 2;
		}
		if (lowerbound <= upperbound) {
			// System.out.println(key + " was found in array subscript " +
			// position);
		} else {
			// System.out.println(key + "  not found by binary search");
		}
		return position;
	}

	public ArrayList<Integer> getMonthList() {
		ArrayList<Integer> l = new ArrayList<Integer>();
		int indices[] = _monthlist.getSelectedIndices();
		int i2m[] = { 10, 11, 12, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

		for (int i = 0; i < indices.length; i++) {
			l.add(i2m[indices[i]]);
		}
		return l;
	}


	private String getPathSpec(boolean useRegex) {
		String pathSpec = "";

		String glob = "*";
		if (useRegex)
			glob = ".*";

		for (int i = 0; i < 6; i++) {
			String txt = _pathText[i].getText().trim().toUpperCase();
			if (txt.length() > 0)
				pathSpec = pathSpec + "/" + txt;
			else
				pathSpec = pathSpec + "/" + glob;
		}
		pathSpec = pathSpec + "/";
		return pathSpec;
	}

	public JList createMonthList() {
		String months[] = { "Oct", "Nov", "Dec", "Jan", "Feb", "Mar", "Apr",
				"May", "Jun", "Jul", "Aug", "Sep" };

		final JList list = new JList(months);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(1);
		list.setSelectionInterval(0, months.length - 1);
		list.setToolTipText("Select months to extract for plots/tables");
		list.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				DssPluginCore.months=new ArrayList<Integer>();
				AppUtils.months=new ArrayList<Integer>();
				int[] sels=list.getSelectedIndices();
				for (int i=0; i<sels.length; i++){
					switch (sels[i]) {
		            	case 0:  
		            		DssPluginCore.months.add(10);
		            		AppUtils.months.add(10);
		            		break;
		            	case 1:  
		            		DssPluginCore.months.add(11);
		            		AppUtils.months.add(11);
		            		break;
		            	case 2:  
		            		DssPluginCore.months.add(12);
		            		AppUtils.months.add(12);
		            		break;
		            	case 3:  
		            		DssPluginCore.months.add(1);
		            		AppUtils.months.add(1);
		            		break;
		            	case 4:  
		            		DssPluginCore.months.add(2);
		            		AppUtils.months.add(2);
		            		break;
		            	case 5:  
		            		DssPluginCore.months.add(3);
		            		AppUtils.months.add(3);
		            		break;
		            	case 6:  
		            		DssPluginCore.months.add(4);
		            		AppUtils.months.add(4);
		            		break;
		            	case 7:  
		            		DssPluginCore.months.add(5);
		            		AppUtils.months.add(5);
		            		break;
		            	case 8:  
		            		DssPluginCore.months.add(6);
		            		AppUtils.months.add(6);
		            		break;
		            	case 9:  
		            		DssPluginCore.months.add(7);
		            		AppUtils.months.add(7);
		            		break;
		            	case 10:  
		            		DssPluginCore.months.add(8);
		            		AppUtils.months.add(8);
		            		break;
		            	case 11:  
		            		DssPluginCore.months.add(9);
		            		AppUtils.months.add(9);
		            		break;
		            	default:
		            		break;
					}
				}
				ShowSelected.showSelected();
			}
			
		});

		return list;

	}

	private void setTextFieldAction(JTextField textArea) {
		InputMap inputMap = textArea.getInputMap();
		ActionMap actionMap = textArea.getActionMap();

		Object transferTextActionKey = "TRANSFER_TEXT";
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				transferTextActionKey);
		// Also add shift+enter binding here if you want
		actionMap.put(transferTextActionKey, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				filter();
			}
			
			public void filter(){
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						IWorkbenchPage workBenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
						DSSCatalogView dssCatalogView=(DSSCatalogView) workBenchPage.findView(DSSCatalogView.ID);
						dssCatalogView.getViewer().setInput(DssPluginCore.dssArray);
					}
				});
			}
		});
	}

	public JPanel createUpperPanel() {
		int fontSize = 12;
		labelNames = new JLabel[labelText.length];
		for (int i = 0; i < labelText.length; i++) {
			labelNames[i] = new JLabel(labelText[i]);
			f = labelNames[i].getFont();
			f = new Font(f.getName(), f.getStyle(), fontSize);
			labelNames[i].setFont(f);
			labelNames[i].setForeground(Color.black);
		}
		
		
		JPanel upperPanel = new JPanel();
		upperPanel.add(createUtilsPanel());
		return upperPanel;
	}
	
	public JPanel createUtilsPanel() {
		JPanel panel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		panel.setLayout(gb);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 0;
		panel.add(labelNames[0], gc);
		gc.gridx = 1;
		panel.add(createModePanel(), gc);
		gc.gridx = 2;
		// panel.add(labelNames[16],gc);
		panel.add(labelNames[3], gc); // CB put it back in
		gc.gridx = 3;
		// panel.add(createViewPanel(),gc);
		panel.add(createAnnualTypePanel(), gc); // CB added
		gc.gridx = 4;
		panel.add(new JLabel(""), gc);
		gc.gridx = 5;
		panel.add(labelNames[1], gc);
		gc.gridx = 6;
		panel.add(createTWBox(), gc);
		gc.gridx = 7;
		panel.add(labelNames[2], gc);
		gc.gridx = 8;
		panel.add(createUnitsPanel());
		return panel;
	}

	public JPanel createUnitsPanel() {
		JPanel panel = new JPanel();
		ButtonGroup g = new ButtonGroup();

		if (DssPluginCore.units == DssPluginCore.cfs)
			cfs.setSelected(true);
		else 
			taf.setSelected(true);
		g.add(cfs);
		g.add(taf);
		taf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DssPluginCore.units=DssPluginCore.taf;
				AppUtils.useUnits(DssPluginCore.taf);
				ShowSelected.showSelected();
			}
		});
		cfs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DssPluginCore.units=DssPluginCore.cfs;
				AppUtils.useUnits(DssPluginCore.cfs);
				ShowSelected.showSelected();
			}
		});
		taf.setFont(new Font(f.getName(), f.getStyle(), 12));
		cfs.setFont(new Font(f.getName(), f.getStyle(), 12));
		panel.setPreferredSize(new Dimension(100, 12));
		panel.setLayout(new GridLayout(1, 0));
		panel.add(cfs);
		panel.add(taf);
		return panel;
	}

	public JPanel createModePanel() {
		comp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		diff.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		
		JPanel panel = new JPanel();
		ButtonGroup g = new ButtonGroup();
		g.add(comp);
		g.add(diff);
		comp.setFont(new Font(f.getName(), f.getStyle(), 12));
		diff.setFont(new Font(f.getName(), f.getStyle(), 12));
		panel.setPreferredSize(new Dimension(125, 12));
		panel.setLayout(new GridLayout(1, 0));
		panel.add(comp);
		panel.add(diff);
		comp.setSelected(true);
		DssPluginCore.mode=comp.getText();
		comp.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				DssPluginCore.mode=comp.getText();
				AppUtils.plotComparitive = true;
				AppUtils.plotDifference = false;
				ShowSelected.showSelected();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		diff.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				DssPluginCore.mode=diff.getText();
				AppUtils.plotComparitive = false;
				AppUtils.plotDifference = true;
				ShowSelected.showSelected();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return panel;
	}

	public JPanel createAnnualTypePanel() {
		JPanel panel = new JPanel();
		ButtonGroup g = new ButtonGroup();
		g.add(wateryear);
		g.add(calendar);
		g.add(fedContract);
		setAnnualType(DssPluginCore.WATERYEAR);
		g.setSelected(wateryear.getModel(), true);
		wateryear.setFont(new Font(f.getName(), f.getStyle(), 12));
		calendar.setFont(new Font(f.getName(), f.getStyle(), 12));
		fedContract.setFont(new Font(f.getName(), f.getStyle(), 12));
		panel.setPreferredSize(new Dimension(230, 12));
		panel.setLayout(new GridLayout(1, 0));
		panel.add(wateryear);
		panel.add(calendar);
		panel.add(fedContract);
		wateryear.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				setAnnualType(DssPluginCore.WATERYEAR);
				ShowSelected.showSelected();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		calendar.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				setAnnualType(DssPluginCore.CALENDAR_YEAR);
				ShowSelected.showSelected();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		fedContract.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				setAnnualType(DssPluginCore.FEDERAL_CONTRACT_YEAR);
				ShowSelected.showSelected();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return panel;
	}

	public JComboBox createTWBox() {
		procTWFile();
		for (int i = 0; i < DssPluginCore._twSelections.size(); i++){
			_twitems.addElement(DssPluginCore._twSelections.get(i));
		}
		final JComboBox twbox = new JComboBox(_twitems);
		twbox.setEditable(true);
		twbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox tb = (JComboBox) e.getSource();
				String twSel = (String) tb.getSelectedItem();
				if (twSel.equals("Add...")){
					final IWorkbench workbench=PlatformUI.getWorkbench();
					workbench.getDisplay().asyncExec(new Runnable(){
						public void run(){
							Shell shell=workbench.getActiveWorkbenchWindow().getShell();
							InsertTimeWindowDialog dialog= new InsertTimeWindowDialog(shell, twbox);
							dialog.openDialog();;
						}
					});
				}else{
					setTimeWindow(twSel);
					ShowSelected.showSelected();
				}
			}
		});
		Dimension d = new Dimension(350, 17);
		twbox.setMinimumSize(d);
		twbox.setFont(new Font(f.getName(), f.getStyle(), 12));
		twbox.setSelectedIndex(0);
		DssPluginCore.tw="All";
		return twbox;
	}
	
	public void procTWFile(){
		try {
			File file = new File(DebugCorePlugin.dataDir, DssPluginCore.twFile);
			if (!file.exists()){
				file.createNewFile();
			}else{
				DssPluginCore._twSelections=new ArrayList<String>();
				DssPluginCore._twSelections.add("All");
				FileInputStream fs = new FileInputStream(file.getAbsolutePath());
				BufferedReader br = new BufferedReader(new InputStreamReader(fs));
				LineNumberReader reader = new LineNumberReader(br);
				String line;
				while((line = br.readLine())!=null){
					DssPluginCore._twSelections.add(line);
				}
				DssPluginCore._twSelections.add("Add...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setTimeWindow(String twSel) {
		Project prj = AppUtils.getCurrentProject();
		if (twSel.equals("All")){
			DssPluginCore.tw="All";
			prj.setTimeWindow(AppUtils.DEFAULT_TIME_WINDOW);
		}else{
			prj.setTimeWindow(twSel);
			String[] split = twSel.split(" +");
			String startMonth = split[0].substring(0, 3);
			String endMonth = split[2].substring(0, 3);
			String startYear = split[0].substring(3);
			String endYear = split[2].substring(3);
			try {
				int sYear = Integer.parseInt(startYear);
				int sMonth = TimeOperation.monthValue(startMonth);
				int eYear = Integer.parseInt(endYear);
				int eMonth = TimeOperation.monthValue(endMonth);
				int daysInStartMonth = TimeOperation.numberOfDays(sMonth,
					sYear);
				int daysInEndMonth = TimeOperation.numberOfDays(eMonth,
					eYear);
				DssPluginCore.tw = daysInStartMonth + startMonth
					+ startYear + " 2400 " + daysInEndMonth
					+ endMonth + endYear + " 2400";
			} catch (NumberFormatException nfe) {
				DssPluginCore.tw = "31Oct1921 2400 30Sep2015 2400"; 
			}
		}
	}
	
	public void setAnnualType(int annType) {
		DssPluginCore.annualType = annType;
	}
	
	public JPanel createLowerPanel() {
		int textwidth = 15;
		// create filter panel
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new GridLayout(1, 7));

		_pathText = new JTextField[6];
		_pathText[0] = new JTextField(textwidth);
		setTextFieldAction(_pathText[0]);
		_pathText[0].setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.lightGray), "A"));
		_pathText[1] = new JTextField(textwidth);
		setTextFieldAction(_pathText[1]);
		_pathText[1].setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.lightGray), "B"));
		_pathText[2] = new JTextField(textwidth);
		setTextFieldAction(_pathText[2]);
		_pathText[2].setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.lightGray), "C"));
		_pathText[3] = new JTextField(textwidth);
		setTextFieldAction(_pathText[3]);
		_pathText[3].setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.lightGray), "D"));
		_pathText[4] = new JTextField(textwidth);
		setTextFieldAction(_pathText[4]);
		_pathText[4].setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.lightGray), "E"));
		_pathText[5] = new JTextField(textwidth);
		setTextFieldAction(_pathText[5]);
		_pathText[5].setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.lightGray), "F"));
		
		// filterPanel.add(_varTypeBox);
		for (int i = 0; i < 6; i++) {
			filterPanel.add(_pathText[i]);
			_pathText[i].getDocument().addDocumentListener(new DocumentListener() {
				  public void changedUpdate(DocumentEvent e) {
					      setFilter();
					  }
					  public void removeUpdate(DocumentEvent e) {
						  setFilter();
					  }
					  public void insertUpdate(DocumentEvent e) {
						  setFilter();
					  }
					  
					  public void setFilter(){
							DssPluginCore.filter=getPathSpec(false);
					  }
					  
					  public String getPathSpec(boolean useRegex) {
							String pathSpec = "";

							String glob = "*";
							if (useRegex)
								glob = ".*";

							for (int i = 0; i < 6; i++) {
								String txt = _pathText[i].getText().trim().toUpperCase();
								if (txt.length() > 0)
									pathSpec = pathSpec + "/" + txt;
								else
									pathSpec = pathSpec + "/" + glob;
							}
							pathSpec = pathSpec + "/";
							return pathSpec;
						}
					});
		}
		filterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.blue), "Filter"));

		_filterBtn = new JButton("Filter");
		_filterBtn.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						IWorkbenchPage workBenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
						DSSCatalogView dssCatalogView=(DSSCatalogView) workBenchPage.findView(DSSCatalogView.ID);
						dssCatalogView.getViewer().setInput(DssPluginCore.dssArray);
					}
				});
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		_plotBtn = new JRadioButton("Model Data");
		_plotBtn.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				DssPluginCore.chartType=0;
				ShowSelected.showSelected();
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		_exceedBtn = new JRadioButton("Exceedance");
		_exceedBtn.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				DssPluginCore.chartType=1;
				ShowSelected.showSelected();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		_annualTotBtn = new JRadioButton("Annual Total");
		_annualTotBtn.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				DssPluginCore.chartType=2;
				ShowSelected.showSelected();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		_annualTotExceedBtn = new JRadioButton("Annual Exceedence");
		_annualTotExceedBtn.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				DssPluginCore.chartType=3;
				ShowSelected.showSelected();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		_monthlyAvgBtn = new JRadioButton("Monthly Average");
		_monthlyAvgBtn.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				DssPluginCore.chartType=4;
				ShowSelected.showSelected();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		ButtonGroup group = new ButtonGroup();
	    group.add(_plotBtn);
	    group.add(_exceedBtn);
	    group.add(_annualTotBtn);
	    group.add(_annualTotExceedBtn);
	    group.add(_monthlyAvgBtn);
		_plotBtn.setSelected(true);
		DssPluginCore.chartType=0;

		// List of Months for plots/table
		_monthlist = createMonthList();

		Box btnPanel = new Box(BoxLayout.X_AXIS);
		btnPanel.add(Box.createHorizontalGlue());
		btnPanel.add(_filterBtn);
		btnPanel.add(_monthlist);
		// btnPanel.add(_retrieveBtn);
		btnPanel.add(new JLabel("  "));
		btnPanel.add(_plotBtn);
		btnPanel.add(_exceedBtn);
		btnPanel.add(_annualTotBtn);
		btnPanel.add(_annualTotExceedBtn);
		btnPanel.add(_monthlyAvgBtn); // CB added

		btnPanel.add(Box.createHorizontalGlue());
		//
		Box filterBox = new Box(BoxLayout.Y_AXIS);
		filterBox.add(Box.createVerticalGlue());
		filterBox.add(filterPanel);
		filterBox.add(Box.createVerticalGlue());
		//
		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(Box.createVerticalGlue());
		box.add(filterBox);
		box.add(btnPanel);
		box.add(Box.createVerticalGlue());
		Box box1 = new Box(BoxLayout.X_AXIS);
		box1.add(Box.createHorizontalGlue());
		box1.add(box);
		box1.add(Box.createHorizontalGlue());
		JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new BorderLayout());
		lowerPanel.add(box1, BorderLayout.CENTER);
		return lowerPanel;
	}
}
