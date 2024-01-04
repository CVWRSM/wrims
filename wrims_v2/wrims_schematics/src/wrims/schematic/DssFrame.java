package wrims.schematic;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class DssFrame extends JFrame {

	public MainPanel mainPanel;

	// private Schematic _schematic; //CB added, then saw it is better in
	// MainPanel due to this and FilterPanel having it.

	// CB added the argument because I could not get the JTextField
	// documentListener to work for Schematic in a hurry
	public DssFrame(ISchematic schematic) {
		super("Wrims DSS Viewer");
		// setSchematic(schematic); //CB added
		raiseWindow(schematic);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE); // CB added

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				mainPanel.resetSchematicValueNodeNames();
				dispose();
			}
		});
	}

	// CB added the Schematic argument because I could not get the JTextField
	// documentListener to work in a hurry
	public DssFrame(ISchematic schematic, boolean isVisible) {
		this(schematic);
		setVisible(isVisible);
	}

	private void raiseWindow(ISchematic schematic) {
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				JFrame tmp = (JFrame) e.getSource();
				if (tmp.getWidth() < 900 || tmp.getHeight() < 600) {
					tmp.setSize(900, 600);
				}
			}
		});

		MainPanel mPanel = new MainPanel(schematic, this);
		mainPanel = mPanel;
		getContentPane().add(mPanel);

		pack();
	}

	// for interfacing from Schematic
	public DssAppAction getActions() {
		return mainPanel.getActions();
	}

	public FilterPanel getFP() {
		return mainPanel.getFilterPanel();
	}

	public MessagePanel getMP() {
		return mainPanel.getMessagePanel();
	}

	public static void main(String[] args) throws Exception {
		// String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		// String lookAndFeel =
		// UIManager.getCrossPlatformLookAndFeelClassName();
		// UIManager.setLookAndFeel(lookAndFeel);

		new DssFrame(null, true); // CB added null
	}
}
