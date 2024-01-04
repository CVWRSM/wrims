package wrims.schematic;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;

import wrims.misc.ExcelTool;
import wrims.misc.WriteDssToExcel;

public class ToolConstraintAnalysis extends javax.swing.JPanel {


	private JTextField[] _filePath = new JTextField[4];
	private JTextField _startYr = new JTextField(4), _stopYr = new JTextField(4), _startMon = new JTextField(2),
			_stopMon = new JTextField(2);

	private JTextField _dssPartA = new JTextField(10), _dssPartF = new JTextField(10);

	private String wrimsv2EnginePath;
	private String excelFileDir, excelFilePath_default;

	public ToolConstraintAnalysis() {

		wrimsv2EnginePath = System.getenv("WRIMSv2_Engine_Home");

		File excelFileDirF = new File(wrimsv2EnginePath, "tools\\cs3_operation_control\\");
		excelFileDir = excelFileDirF.getAbsolutePath();
		try {
			excelFilePath_default = new File(excelFileDirF, "OperationsControl_CS3_version134.xlsm").getCanonicalPath();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			String msg = new File(excelFileDirF, "OperationsControl_CS3_version134.xlsm").getAbsolutePath();
			JOptionPane.showMessageDialog(null, "File not found: "+msg);
		} 
																										

		_startYr.setDocument(new JTextFieldLimit(4));
		_stopYr.setDocument(new JTextFieldLimit(4));
		_startMon.setDocument(new JTextFieldLimit(2));
		_stopMon.setDocument(new JTextFieldLimit(2));

		_startYr.setText("1921");
		_startYr.setEditable(false);
		_stopYr.setText("2003");
		_stopYr.setEditable(false);
		_startMon.setText("10");
		_startMon.setEditable(false);
		_stopMon.setText("9");
		_stopMon.setEditable(false);

		_dssPartA.setText("CALSIM");
		_dssPartF.setText("CALSIM30_10");

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel p = new JPanel();
		JPanel introBox = new JPanel();
		JPanel inputDssBox = new JPanel();
		JPanel inputDssPartBox = new JPanel();
		JPanel inputDateBox = new JPanel();

		p.setLayout(gbl);

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.weightx = 0;

		// introBox.setBorder(BorderFactory.createTitledBorder(" "));
		introBox.add(createIntroPanel());
		p.add(introBox, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.weightx = 0;

		inputDssBox.setBorder(BorderFactory.createTitledBorder(" "));
		inputDssBox.add(createDssBoxPanel());
		p.add(inputDssBox, c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0;

		inputDssPartBox.setBorder(BorderFactory.createTitledBorder(" "));
		inputDssPartBox.add(createDssPartBoxPanel());
		p.add(inputDssPartBox, c);

		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0;

		inputDateBox.setBorder(BorderFactory.createTitledBorder(" "));
		inputDateBox.add(createDateBoxPanel());
		p.add(inputDateBox, c);

		c.gridx = 2;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1.0;

		JPanel dummy2 = new JPanel();
		dummy2.add(new JLabel(" "));
		p.add(dummy2, c);

		c.gridx = 3;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0;

		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setPreferredSize(new Dimension(600, 10));

		JButton runButton = new JButton("Write Data to Excel");
		ButtonHandler buttonHandler = new ButtonHandler(runButton, progressBar, this);
		runButton.setPreferredSize(new Dimension(135, 55));
		runButton.addActionListener(buttonHandler);


		p.add(runButton, c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.weightx = 0;
		
		p.add(progressBar, c);
		
		this.add(p);

	}

	JPanel createIntroPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 1));

		// panel.add(new JLabel(""));

		JLabel label0 = new JLabel(
				"<html> Instruction: Edit the data below and click the \"Run Excel Macro\" button.</html>");
		panel.add(label0);

		// panel.add(new JLabel(""));

		JLabel label1 = new JLabel(
				"<html>  If you get an error message says \"DSS Paths with No Data Found...\", please edit <br>"
						+ "the DSS paths to match your study output. They are specified in this Excel file <br>"
						+ "named <u><font color=green>OperationsControl_CS3_version134.xlsm</font></u> in the folder \"tools\\cs3_operation_control\" </html>");
		label1.setCursor(new Cursor(Cursor.HAND_CURSOR));
		panel.add(label1);

		label1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if (evt.getClickCount() > 0) {
					try {
						Process p = Runtime.getRuntime().exec("cmd.exe /c start " + excelFileDir);
					}
					catch (IOException ex) {
						System.out.println(ex.getMessage());
						System.out.println();
					}
				}
			}
		});

		// panel.add(new JLabel(""));

		return panel;
	}

	JPanel createDssPartBoxPanel() {

		JPanel panel = new JPanel();

		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		panel.add(new JLabel(" DSS A Part:      "), c);

		c.gridx = 1;
		c.gridy = 0;
		panel.add(_dssPartA, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		panel.add(new JLabel(" "), c);

		c.gridx = 0;
		c.gridy = 2;
		panel.add(new JLabel(" DSS F Part:      "), c);

		c.gridx = 1;
		c.gridy = 2;
		panel.add(_dssPartF, c);

		return panel;
	}

	JPanel createDssBoxPanel() {
		JPanel panel = new JPanel();

		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JLabel label_excelFile = new JLabel(" Excel File Path: ");
		JLabel label_svDss = new JLabel(" SV DSS File Path: ");
		JLabel label_dvDss = new JLabel(" DV DSS File Path: ");

		c.gridx = 1;
		c.gridy = 1;
		panel.add(label_excelFile, c);
		c.gridx = 1;
		c.gridy = 2;
		panel.add(label_svDss, c);
		c.gridx = 1;
		c.gridy = 3;
		panel.add(label_dvDss, c);

		UIManager.put("FileChooser.noPlacesBar", Boolean.TRUE);

		c.gridx = 2;
		c.gridy = 1;
		panel.add(createFilePanel(0, excelFilePath_default, "xlsm", true), c);
		//_filePath[0].setEditable(false);
		c.gridx = 2;
		c.gridy = 2;
		panel.add(createFilePanel(1, "", "dss", true), c);
		c.gridx = 2;
		c.gridy = 3;
		panel.add(createFilePanel(2, "", "dss", true), c);

		return panel;
	}

	JPanel createFilePanel(int type, String text, String extensionName, boolean isEditable) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		_filePath[type] = new JTextField(50);
		_filePath[type].setText(text);
		_filePath[type].setEditable(isEditable);
		panel.add(_filePath[type]);
		JButton button = createFileChooser(type, extensionName);
		button.setEnabled(isEditable);
		panel.add(button);
		return panel;
	}

	JButton createFileChooser(final int n, final String extensionName) {
		JButton chooser = new JButton("Choose");
		chooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				chooseFile(n, extensionName);
			}
		});
		return chooser;
	}

	void chooseFile(int type, String extensionName) {

		JFileChooser c = new JFileChooser();
		c.setPreferredSize(new Dimension(650, 450));
		int rVal = c.showOpenDialog(ToolConstraintAnalysis.this);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			String filePath = c.getSelectedFile().getAbsolutePath();
			
			String ext = FilenameUtils.getExtension(filePath);
			if (ext.equalsIgnoreCase(extensionName) || ext.equals("")) {
				_filePath[type].setText(filePath);
			}
		}
	}

	JPanel createDateBoxPanel() {

		String mon = "  month ";
		String yr = "  year     ";

		JPanel panel = new JPanel();

		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		panel.add(new JLabel(" Start Date:   "), c);

		c.gridx = 1;
		c.gridy = 0;
		panel.add(_startYr, c);

		c.gridx = 2;
		c.gridy = 0;
		panel.add(new JLabel(yr), c);

		c.gridx = 3;
		c.gridy = 0;
		panel.add(_startMon, c);

		c.gridx = 4;
		c.gridy = 0;
		panel.add(new JLabel(mon), c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		panel.add(new JLabel(" "), c);

		c.gridx = 0;
		c.gridy = 2;
		panel.add(new JLabel(" Stop Date:   "), c);

		c.gridx = 1;
		c.gridy = 2;
		panel.add(_stopYr, c);

		c.gridx = 2;
		c.gridy = 2;
		panel.add(new JLabel(yr), c);

		c.gridx = 3;
		c.gridy = 2;
		panel.add(_stopMon, c);

		c.gridx = 4;
		c.gridy = 2;
		panel.add(new JLabel(mon), c);

		return panel;
	}

	void writeDssToExcel() {


		WriteDssToExcel.beginYear = Integer.parseInt(_startYr.getText());
		WriteDssToExcel.beginMonth = Integer.parseInt(_startMon.getText());
		WriteDssToExcel.endYear = Integer.parseInt(_stopYr.getText());
		WriteDssToExcel.endMonth = Integer.parseInt(_stopMon.getText());
		WriteDssToExcel.dssPartA = _dssPartA.getText();
		WriteDssToExcel.dssPartF = _dssPartF.getText();
		WriteDssToExcel.svDssFilePath = _filePath[1].getText(); // "D:\\cvwrsm\\trunk\\excel_java\\CalSim30_10_SV.dss";
		WriteDssToExcel.dvDssFilePath = _filePath[2].getText(); // "D:\\cvwrsm\\trunk\\excel_java\\Version134_052312_WRIMS050912DV.DSS";
		WriteDssToExcel.excelFilePath = _filePath[0].getText();

		JOptionPane.showMessageDialog(this, "before writing");

		try {

			WriteDssToExcel.writeDssToConstraintReport();
			Process p = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", "start", "excel", excelFilePath_default });
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());

			// StringWriter errors = new StringWriter();
			// e.printStackTrace(new PrintWriter(errors));
			// JOptionPane.showMessageDialog(this,errors.toString());
			// e.printStackTrace();

		}

	}

	class ButtonHandler implements ActionListener {

		private JButton button;
		private JProgressBar bar;
		private Component com;

		public ButtonHandler(JButton button, JProgressBar bar, Component com) {

			this.button = button;
			this.bar = bar;
			this.com = com;
		}

		public void actionPerformed(ActionEvent evt) {

			bar.setValue(0);

			WriteDssToExcel w = new WriteDssToExcel();
			
			WriteDssToExcel.Task task = w.new Task();			
			
			w.beginYear = Integer.parseInt(_startYr.getText());
			w.beginMonth = Integer.parseInt(_startMon.getText());
			w.endYear = Integer.parseInt(_stopYr.getText());
			w.endMonth = Integer.parseInt(_stopMon.getText());
			w.dssPartA = _dssPartA.getText();
			w.dssPartF = _dssPartF.getText();
			w.svDssFilePath = _filePath[1].getText(); // "D:\\cvwrsm\\trunk\\excel_java\\CalSim30_10_SV.dss";
			w.dvDssFilePath = _filePath[2].getText(); // "D:\\cvwrsm\\trunk\\excel_java\\Version134_052312_WRIMS050912DV.DSS";
			w.excelFilePath = _filePath[0].getText();	
						
			task.setComponents(this.button);
			task.addPropertyChangeListener(new TaskListener(this.bar, this.button));

			task.execute();

		}
	}

	class TaskListener implements PropertyChangeListener {
		JProgressBar bar;
		JButton button;

		public TaskListener(JProgressBar bar, JButton button) {

			this.bar = bar;
			this.button = button;

		}

		public void propertyChange(PropertyChangeEvent evt) {
			if ("progress" == evt.getPropertyName()) {

					int progress = (Integer) evt.getNewValue();
					bar.setValue(progress);
				
			}
		}

	}

}
