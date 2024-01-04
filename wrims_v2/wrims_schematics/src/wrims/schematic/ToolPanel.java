package wrims.schematic;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class ToolPanel extends javax.swing.JPanel {

    public ToolPanel() {

    	this.setLayout(new BorderLayout());
        JTabbedPane jtp = new ToolSubTabs();
        add(jtp);
    }
}

class ToolSubTabs extends JTabbedPane {

	public ToolSubTabs() {

		JPanel jp1 = new ToolConstraintAnalysis();
		JPanel jp2 = new ToolTrendReport();

		this.addTab("CalSim3 Operation Control Analysis", jp1);
		this.addTab("CalSim3 Trend Report", jp2);

		JButton test = new JButton("Press");
		//jp2.add(test);

//		ButtonHandler phandler = new ButtonHandler();
//		test.addActionListener(phandler);
//		setVisible(true);
	}
}

class JTextFieldLimit extends PlainDocument {
	private int limit;

	JTextFieldLimit(int limit) {
		super();
		this.limit = limit;
	}

	JTextFieldLimit(int limit, boolean upper) {
		super();
		this.limit = limit;
	}

	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		if (str == null) return;

		if ((getLength() + str.length()) <= limit) {
			super.insertString(offset, str, attr);
		}
	}
}