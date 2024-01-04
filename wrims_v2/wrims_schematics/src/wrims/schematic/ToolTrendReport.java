package wrims.schematic;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.apache.commons.io.FilenameUtils;

import wrimsv2.evaluator.TimeOperation;

public class ToolTrendReport extends javax.swing.JPanel {

	private JTextField[] _studyLabel = new JTextField[4];
	private JTextField[] _dssFilePath = new JTextField[4];
	private JTextField _startYr = new JTextField(4), _stopYr = new JTextField(4), _startMon = new JTextField(2),
			_stopMon = new JTextField(2);
	private JCheckBox[] _isActive = new JCheckBox[4];
	private JTextField _dssPartA = new JTextField(10), _dssPartF = new JTextField(10);

	private String wrimsv2EnginePath;
	private String batFilePath;
	private String vbsFilePath;
	private String excelFileDir, excelFilePath;
	private File excelFileDirF;
	
	public ToolTrendReport() {

		wrimsv2EnginePath = System.getenv("WRIMSv2_Engine_Home");
		
		File excelFileDirF = new File(wrimsv2EnginePath, "tools\\cs3_trend_report\\");
		excelFileDir = excelFileDirF.getAbsolutePath(); 
		excelFilePath = new File(excelFileDirF, "Trend_Reporting_Ver3.0.xlsb").getAbsolutePath(); // excelFileDir + "Trend_Reporting_Ver3.0.xlsb";
		batFilePath   = new File(excelFileDirF, "callTrendReportMacro.bat").getAbsolutePath();
		vbsFilePath =   new File(excelFileDirF, "callTrendReportMacro.vbs").getAbsolutePath();
		
		//JOptionPane.showMessageDialog(ToolTrendReport.this, excelFilePath);
		
		_startYr.setDocument(new JTextFieldLimit(4));
		_stopYr.setDocument(new JTextFieldLimit(4));
		_startMon.setDocument(new JTextFieldLimit(2));
		_stopMon.setDocument(new JTextFieldLimit(2));

		_startYr.setText("1921");
		_stopYr.setText("2003");
		_startMon.setText("10");
		_stopMon.setText("9");

		_dssPartA.setText("CALSIM");
		_dssPartF.setText("CALSIM30_10");

		_studyLabel[0] = new JTextField("study1", 10);
		_studyLabel[1] = new JTextField("study2", 10);
		_studyLabel[2] = new JTextField("study3", 10);
		_studyLabel[3] = new JTextField("study4", 10);

		_isActive[0] = new JCheckBox();
		_isActive[1] = new JCheckBox();
		_isActive[2] = new JCheckBox();
		_isActive[3] = new JCheckBox();

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

		JButton runButton = new JButton("Run Excel Macro");
		runButton.setPreferredSize(new Dimension(135, 55));
		// runButton.addActionListener(new ButtonHandler());
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				runExcelMacro();
			}
		});
		p.add(runButton, c);

		this.add(p);

	}

	JPanel createIntroPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 1));

		//panel.add(new JLabel(""));

		JLabel label0 = new JLabel(
				"<html> Instruction: Edit the data below and click the \"Run Excel Macro\" button.</html>");
		panel.add(label0);

		//panel.add(new JLabel(""));
		
		JLabel label1 = new JLabel(
				"<html>  If you get an error message says \"DSS Paths with No Data Found...\", please edit <br>" + 
		                "the DSS paths to match your study output. They are specified in this Excel file <br>" +
				        "named <u><font color=green>Trend_Reporting_Ver3.0.xlsb</font></u> in the folder \"tools\\cs3_trend_report\" </html>");
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

		//panel.add(new JLabel(""));


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

		JLabel label0 = new JLabel(" Import   ");
		c.gridx = 0;
		c.gridy = 0;
		panel.add(label0, c);
		c.gridx = 0;
		c.gridy = 1;
		panel.add(createCheckBoxPanel(0), c);
		c.gridx = 0;
		c.gridy = 2;
		panel.add(createCheckBoxPanel(1), c);
		c.gridx = 0;
		c.gridy = 3;
		panel.add(createCheckBoxPanel(2), c);
		c.gridx = 0;
		c.gridy = 4;
		panel.add(createCheckBoxPanel(3), c);

		JLabel label1 = new JLabel(" Study Name ");
		c.gridx = 1;
		c.gridy = 0;
		panel.add(label1, c);
		c.gridx = 1;
		c.gridy = 1;
		panel.add(createTextPanel(0), c);
		c.gridx = 1;
		c.gridy = 2;
		panel.add(createTextPanel(1), c);
		c.gridx = 1;
		c.gridy = 3;
		panel.add(createTextPanel(2), c);
		c.gridx = 1;
		c.gridy = 4;
		panel.add(createTextPanel(3), c);

		JLabel label2 = new JLabel("DSS File Path     ");
		UIManager.put("FileChooser.noPlacesBar", Boolean.TRUE);
		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 5;
		panel.add(label2, c);
		c.gridx = 2;
		c.gridy = 1;
		panel.add(createFilePanel(0), c);
		c.gridx = 2;
		c.gridy = 2;
		panel.add(createFilePanel(1), c);
		c.gridx = 2;
		c.gridy = 3;
		panel.add(createFilePanel(2), c);
		c.gridx = 2;
		c.gridy = 4;
		panel.add(createFilePanel(3), c);

		return panel;
	}

	JPanel createCheckBoxPanel(int type) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(_isActive[type]);
		return panel;
	}

	JPanel createTextPanel(int type) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(_studyLabel[type]);
		return panel;
	}

	JPanel createFilePanel(int type) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		_dssFilePath[type] = new JTextField(43);
		panel.add(_dssFilePath[type]);
		JButton button = createFileChooser(type);
		panel.add(button);
		return panel;
	}

	JButton createFileChooser(final int n) {
		JButton chooser = new JButton("Choose");
		chooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				chooseFile(n);
			}
		});
		return chooser;
	}

	void chooseFile(int type) {

		JFileChooser c = new JFileChooser();
		c.setPreferredSize(new Dimension(650, 450));
		int rVal = c.showOpenDialog(ToolTrendReport.this);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			String filePath = c.getSelectedFile().getAbsolutePath();
			if (FilenameUtils.getExtension(filePath).equalsIgnoreCase("dss")) {
				_dssFilePath[type].setText(filePath);
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

	void generateBatchFile() {
		try {
			FileWriter runFile = new FileWriter(batFilePath);
			PrintWriter out = new PrintWriter(runFile);

			String sty1 = " null null";
			String sty2 = " null null";
			String sty3 = " null null";
			String sty4 = " null null";

			if (_isActive[0].isSelected()) sty1 = _studyLabel[0].getText() + " " + _dssFilePath[0].getText();
			if (_isActive[1].isSelected()) sty2 = _studyLabel[1].getText() + " " + _dssFilePath[1].getText();
			if (_isActive[2].isSelected()) sty3 = _studyLabel[2].getText() + " " + _dssFilePath[2].getText();
			if (_isActive[3].isSelected()) sty4 = _studyLabel[3].getText() + " " + _dssFilePath[3].getText();

			int startYr = Integer.parseInt(_startYr.getText());
			int startMon = Integer.parseInt(_startMon.getText());
			int startDay = InputPanel.getDaysInMonth(startMon, startYr);
			int stopYr = Integer.parseInt(_stopYr.getText());
			int stopMon = Integer.parseInt(_stopMon.getText());
			int stopDay = InputPanel.getDaysInMonth(stopMon, stopYr);

			String startDate = startMon + "/" + startDay + "/" + startYr;
			String stopDate = stopMon + "/" + stopDay + "/" + stopYr;

			String executeCommand =

			"cscript.exe " + vbsFilePath + " " + excelFilePath + " " + startDate + " " + stopDate + " "
					+ _dssPartA.getText() + " " + _dssPartF.getText() + " " + _isActive[0].isSelected() + " "
					+ _isActive[1].isSelected() + " " + _isActive[2].isSelected() + " " + _isActive[3].isSelected()
					+ " "

					+ sty1 + " " + sty2 + " " + sty3 + " " + sty4 + " ";

			out.println(executeCommand);

			out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	void runExcelMacro() {

		generateBatchFile();

		Process p;
		try {

			p = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", "start", batFilePath });

		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}

