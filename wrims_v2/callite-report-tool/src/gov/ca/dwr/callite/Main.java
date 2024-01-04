package gov.ca.dwr.callite;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("CalLite Report Generator");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel reportPanel = new JPanel();
		reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.Y_AXIS));
		final InputFilePanel inputFilePanel = new InputFilePanel(
				"Template Input File");
		reportPanel.add(inputFilePanel);
		final JButton runButton = new JButton("Generate Report");
		reportPanel.add(runButton);
		runButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				runButton.setEnabled(false);
				runButton.setText("Running...");
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try {
							Report report = new Report(inputFilePanel.getFile());
							File ofile = new File(report.getOutputFile());
							String msgs = Utils.getMessages();
							if (msgs.length() > 0){
								JOptionPane.showMessageDialog(runButton, msgs, "Warning",
										JOptionPane.WARNING_MESSAGE);
							}
							Runtime.getRuntime().exec(
									"cmd /c start "+ofile.getAbsolutePath());
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(runButton, ex
									.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);
						} finally {
							runButton.setText("Generate Report");
							runButton.setEnabled(true);
						}
					}
				});
			}
		});
		frame.getContentPane().add(reportPanel);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.show();
	}

	/**
	 * Creates a panel consisting of label, a textfield populated by a browse
	 * (file browser) button
	 * 
	 * @return
	 */
	@SuppressWarnings("serial")
	private static class InputFilePanel extends JPanel {
		private JTextField textfield;
		private JButton browseButton;

		public InputFilePanel(String label) {
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			this.add(new JLabel(label));
			this.add(textfield = new JTextField(40));
			this.add(browseButton = new JButton("Browse..."));
			browseButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int showOpenDialog = fileChooser
							.showOpenDialog(InputFilePanel.this);
					if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
						textfield.setText(fileChooser.getSelectedFile()
								.getAbsolutePath());
					}
				}
			});
		}

		public String getFile() {
			return textfield.getText();
		}
	}

}
