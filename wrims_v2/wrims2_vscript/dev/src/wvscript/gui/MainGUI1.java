package wvscript.gui;

import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JCheckBox;
import java.awt.GridBagConstraints;
import javax.swing.JRadioButton;
import java.awt.Insets;
import javax.swing.JTextField;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;

import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JFormattedTextField;

public class MainGUI1 {

	private JFrame frmWvscript;
	private JPanel panel_Simple_run;
	private JPanel panel_config_wrapper;
	private JTabbedPane tabbedPane_PA;
	FileNameExtensionFilter filter;
	private JTextField textField;
	private JPanel panel_Simple;
	private JTextField textField_1;
	private JTextField textField_2;
	private static final DateFormat df_yyyymmdd = new SimpleDateFormat("yyyy-mm-dd");
	private static final DateFormat df_yyyymm = new SimpleDateFormat("yyyy-mm");
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGUI1 window = new MainGUI1();
					window.frmWvscript.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainGUI1() {

		UIManager.put("FileChooser.readOnly", Boolean.TRUE);

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmWvscript = new JFrame();
		frmWvscript.setTitle("WVscript 1.20");
		frmWvscript.setBounds(100, 100, 608, 551);
		frmWvscript.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTabbedPane tabbedPane_Main = new JTabbedPane(JTabbedPane.TOP);
		frmWvscript.getContentPane().add(tabbedPane_Main, BorderLayout.CENTER);

		panel_Simple = new JPanel();
		tabbedPane_Main.addTab("Simple", null, panel_Simple, null);
		panel_Simple.setLayout(new BorderLayout(0, 0));
		
		panel_Simple_run = new JPanel();
		panel_Simple.add(panel_Simple_run, BorderLayout.WEST);
		simpleRunPanel();
		
		panel_config_wrapper = new JPanel();
		panel_Simple.add(panel_config_wrapper, BorderLayout.CENTER);
		simpleConfigPanel();
		
		tabbedPane_PA = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_Main.addTab("Position Analysis", tabbedPane_PA);	
		positionAnalysisPanel();

	}

	public void simpleConfigPanel() {
		
		panel_config_wrapper.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lbl_basicConfig = new JLabel("Config:");
		lbl_basicConfig.setFont(new Font("SansSerif", Font.BOLD, 13));
		panel_config_wrapper.add(lbl_basicConfig, "2, 4");
		
		JPanel panel_Simple_config = new JPanel();
		panel_Simple_config.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_config_wrapper.add(panel_Simple_config, "2, 6, fill, fill");
		panel_Simple_config.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JRadioButton rdbtn_wreslPlus = new JRadioButton("WRESL+");
		rdbtn_wreslPlus.setHorizontalTextPosition(SwingConstants.LEFT);
		panel_Simple_config.add(rdbtn_wreslPlus, "2, 1, 3, 1");
		
		JButton btnNewButton_1 = new JButton("Init dss...");
		panel_Simple_config.add(btnNewButton_1, "2, 3");
		
		textField_1 = new JTextField();
		panel_Simple_config.add(textField_1, "4, 3, fill, default");
		textField_1.setColumns(10);
		
		JButton btnNewButton = new JButton("Svar dss...");
		panel_Simple_config.add(btnNewButton, "2, 5");
		
		textField = new JTextField();
		panel_Simple_config.add(textField, "4, 5, fill, default");
		textField.setColumns(10);
		
		JButton btnNewButton_2 = new JButton("Dvar dss...");
		panel_Simple_config.add(btnNewButton_2, "2, 7");
		
		textField_2 = new JTextField();
		panel_Simple_config.add(textField_2, "4, 7, fill, default");
		textField_2.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Time step");
		panel_Simple_config.add(lblNewLabel, "2, 11, right, default");
		
		JComboBox comboBox = new JComboBox();
		panel_Simple_config.add(comboBox, "4, 11, fill, default");
		
		JLabel lblNewLabel_1 = new JLabel("Start date (yyyy-mm-dd)");
		panel_Simple_config.add(lblNewLabel_1, "2, 13, right, default");
		
		JFormattedTextField ftf_startDate = new JFormattedTextField(df_yyyymmdd);
		//JFormattedTextField ftf_startDate = new JFormattedTextField();
		ftf_startDate.setColumns(10);
		panel_Simple_config.add(ftf_startDate, "4, 13, fill, default");
		
        try {
            MaskFormatter dateMask = new MaskFormatter("####-##-##");
            dateMask.install(ftf_startDate);
    		ftf_startDate.setText("1921-10-31");
    		
    		JLabel lblNewLabel_2 = new JLabel("Stop date (yyyy-mm-dd)");
    		panel_Simple_config.add(lblNewLabel_2, "2, 15, right, default");
    		
    		JFormattedTextField formattedTextField = new JFormattedTextField();
    		panel_Simple_config.add(formattedTextField, "4, 15, fill, default");
    		
    		JLabel lblNewLabel_3 = new JLabel("Number of time steps");
    		panel_Simple_config.add(lblNewLabel_3, "2, 17, right, default");
    		
    		JFormattedTextField formattedTextField_1 = new JFormattedTextField();
    		panel_Simple_config.add(formattedTextField_1, "4, 17, fill, default");
        } catch (ParseException ex) {
            Logger.getLogger(MaskFormatterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
		
	}

	public void simpleRunPanel() {
	
		panel_Simple_run.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("left:default"),
				ColumnSpec.decode("max(20dlu;min)"),
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("max(10dlu;min)"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
	
		JButton btn_runDir = new JButton("Run dir...");
		panel_Simple_run.add(btn_runDir, "4, 4, fill, default");
	
		JButton btn_openConfig = new JButton("Open config...");
		filter = new FileNameExtensionFilter("txt file", "txt");
		btn_openConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				File dirToLock = new File("Z:\\");
				FileSystemView fsv = new DirectoryRestrictedFileSystemView(dirToLock);
				//JFileChooser chooser = new JFileChooser();
				JFileChooser chooser = new JFileChooser(fsv);
				chooser.setCurrentDirectory(dirToLock);
				chooser.setControlButtonsAreShown(false);
	
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileView(new FileView() {
					
				    @Override
				    public Boolean isTraversable(File f) {
				    	File dirToLock = new File("Z:\\");
				         //return dirToLock.equals(f);
				         return false;
				    }
				});
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				//disableUpFolderButton(chooser);
				//disableDesktopButton(chooser);
				// chooser.setMultiSelectionEnabled(true);
				chooser.setFileFilter(filter);
				int option = chooser.showOpenDialog(frmWvscript);
				System.out.println("option:" + option + " JFileChooser.APPROVE_OPTION:" + JFileChooser.APPROVE_OPTION);
				System.out.println(" file:" + chooser.getSelectedFile().getName());
	
				if (option == JFileChooser.APPROVE_OPTION) {
	
					File sf = chooser.getSelectedFile();
					System.out.println(sf.getName());
					String fileN = "nothing";
					fileN = sf.getName();
					//lbl_config.setText("Config file: " + fileN);
	
					// for (int i = 1; i < sf.length; i++) {
					// filelist += ", " + sf[i].getName();
					// }
					// statusbar.setText("You chose " + filelist);
					// }
					// else {
					// statusbar.setText("You canceled.");
					// }
	
				}
			}
		});
		btn_openConfig.setToolTipText("Optional.");
		panel_Simple_run.add(btn_openConfig, "4, 8, fill, default");
	
		JButton btn_newConfig = new JButton("New config...");
		panel_Simple_run.add(btn_newConfig, "4, 10, fill, default");
	
		JButton btn_initDss = new JButton("Init dss...");
		panel_Simple_run.add(btn_initDss, "4, 14, fill, default");
	
		JButton btn_svarDss = new JButton("Svar dss...");
		panel_Simple_run.add(btn_svarDss, "4, 16, fill, default");
	
		JButton btn_dvarDss = new JButton("Dvar dss...");
		panel_Simple_run.add(btn_dvarDss, "4, 18, fill, default");
	
		JComboBox comboBox_timeStep = new JComboBox();
		comboBox_timeStep.setModel(new DefaultComboBoxModel(new String[]{"Time step:  1MON", "Time step:  1DAY"}));
		panel_Simple_run.add(comboBox_timeStep, "4, 20, left, default");
	
		JButton btn_run = new JButton("Run");
		btn_run.setFont(new Font("Dialog", Font.BOLD, 16));
		panel_Simple_run.add(btn_run, "4, 26, 3, 1, fill, fill");
	
	}

	public void positionAnalysisPanel() {
		JPanel panel_PA_1 = new JPanel();
		tabbedPane_PA.addTab("PA Step 1", null, panel_PA_1, null);
		JPanel panel_PA_2 = new JPanel();
		tabbedPane_PA.addTab("PA Step 2", null, panel_PA_2, null);
		JPanel panel_PA_3 = new JPanel();
		tabbedPane_PA.addTab("Run", null, panel_PA_3, null);
		GridBagLayout gbl_panel_PA_3 = new GridBagLayout();
		gbl_panel_PA_3.columnWidths = new int[]{0, 0, 0};
		gbl_panel_PA_3.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel_PA_3.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_PA_3.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_PA_3.setLayout(gbl_panel_PA_3);

		JCheckBox chckbxNewCheckBox = new JCheckBox("New check box");
		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxNewCheckBox.gridx = 1;
		gbc_chckbxNewCheckBox.gridy = 1;
		panel_PA_3.add(chckbxNewCheckBox, gbc_chckbxNewCheckBox);

		JRadioButton rdbtnNewRadioButton = new JRadioButton("New radio button");
		GridBagConstraints gbc_rdbtnNewRadioButton = new GridBagConstraints();
		gbc_rdbtnNewRadioButton.gridx = 1;
		gbc_rdbtnNewRadioButton.gridy = 2;
		panel_PA_3.add(rdbtnNewRadioButton, gbc_rdbtnNewRadioButton);

	}

	public static void disableUpFolderButton(Container c) {
		int len = c.getComponentCount();
		for (int i = 0; i < len; i++) {
			Component comp = c.getComponent(i);
			if (comp instanceof JButton) {
				JButton b = (JButton) comp;
				Icon icon = b.getIcon();
				if (icon != null && icon == UIManager.getIcon("FileChooser.upFolderIcon")) {
					b.setEnabled(false);
				}
			} else if (comp instanceof Container) {
				disableUpFolderButton((Container) comp);
			}
		}

	}

	public static void disableDesktopButton(Container c) {
		int len = c.getComponentCount();
		for (int i = 0; i < len; i++) {
			Component comp = c.getComponent(i);
			if (comp instanceof JButton) {
				JButton b = (JButton) comp;

				//if (b != null && b.getText() != null && b.getText().equals("Desktop")) {
				if (b!=null && b.getToolTipText()!=null && b.getToolTipText().equals("Desktop")) {
					b.setEnabled(false);
				}
			} else if (comp instanceof Container) {
				disableDesktopButton((Container) comp);
			}
		}

	}
	public JPanel getPanel_Simple() {
		return panel_Simple;
	}
}
