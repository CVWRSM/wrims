package wrims.schematic;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/*
 * Like MenuDemo, but with popup menus added.
 */
public class SchematicPopupMenuRes implements ActionListener, ItemListener {
	JTextArea output;
	JScrollPane scrollPane;
	String newline = "\n";

	public JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menu, submenu;
		JMenuItem menuItem;

		// Create the menu bar.
		menuBar = new JMenuBar();

		// Build the first menu.
		menu = new JMenu("Reservoir Menu");
		// menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
				"The only menu in this program that has menu items");
		menuBar.add(menu);

		// first submenu
		menu.addSeparator();
		submenu = new JMenu("Physical Properties");
		// submenu.setMnemonic(KeyEvent.VK_P);

		menuItem = new JMenuItem("Ratings");
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(
		// KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.addActionListener(this);
		submenu.add(menuItem);

		menuItem = new JMenuItem("Zones");
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(
		// KeyEvent.VK_2, ActionEvent.ALT_MASK));
		menuItem.addActionListener(this);
		submenu.add(menuItem);

		menu.add(submenu);

		// second submenu
		menu.addSeparator();
		submenu = new JMenu("Timeseries (DSS)");
		// submenu.setMnemonic(KeyEvent.VK_T);

		menuItem = new JMenuItem("EOP Storage");
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(
		// KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.addActionListener(this);
		submenu.add(menuItem);

		menuItem = new JMenuItem("Inflows");
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(
		// KeyEvent.VK_2, ActionEvent.ALT_MASK));
		menuItem.addActionListener(this);
		submenu.add(menuItem);

		menuItem = new JMenuItem("Outflows");
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(
		// KeyEvent.VK_3, ActionEvent.ALT_MASK));
		menuItem.addActionListener(this);
		submenu.add(menuItem);

		menuItem = new JMenuItem("Evaporation");
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(
		// KeyEvent.VK_4, ActionEvent.ALT_MASK));
		menuItem.addActionListener(this);
		submenu.add(menuItem);

		menuItem = new JMenuItem("Non-Recovered Spills");
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(
		// KeyEvent.VK_5, ActionEvent.ALT_MASK));
		menuItem.addActionListener(this);
		submenu.add(menuItem);

		menuItem = new JMenuItem("Surface Area");
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(
		// KeyEvent.VK_6, ActionEvent.ALT_MASK));
		menuItem.addActionListener(this);
		submenu.add(menuItem);

		menu.add(submenu);

		menu.addSeparator();

		menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		menu.add(menuItem);

		return menuBar;
	}

	public Container createContentPane() {
		// Create the content-pane-to-be.
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);

		// Create a scrolled text area.
		output = new JTextArea(5, 30);
		output.setEditable(false);
		scrollPane = new JScrollPane(output);

		// Add the text area to the content pane.
		contentPane.add(scrollPane, BorderLayout.CENTER);

		return contentPane;
	}

	public void createPopupMenu() {
		JMenuItem menuItem;

		// Create the popup menu.
		JPopupMenu popup = new JPopupMenu();
		menuItem = new JMenuItem("A popup menu item");
		menuItem.addActionListener(this);
		popup.add(menuItem);
		menuItem = new JMenuItem("Another popup menu item");
		menuItem.addActionListener(this);
		popup.add(menuItem);

		// Add listener to the text area so the popup menu can come up.
		MouseListener popupListener = new PopupListener(popup);
		output.addMouseListener(popupListener);
	}

	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem) (e.getSource());
		String s = "Action event detected." + newline + "    Event source: "
				+ source.getText() + " (an instance of " + getClassName(source)
				+ ")";
		output.append(s + newline);
		output.setCaretPosition(output.getDocument().getLength());
	}

	public void itemStateChanged(ItemEvent e) {
		JMenuItem source = (JMenuItem) (e.getSource());
		String s = "Item event detected."
				+ newline
				+ "    Event source: "
				+ source.getText()
				+ " (an instance of "
				+ getClassName(source)
				+ ")"
				+ newline
				+ "    New state: "
				+ ((e.getStateChange() == ItemEvent.SELECTED) ? "selected"
						: "unselected");
		output.append(s + newline);
		output.setCaretPosition(output.getDocument().getLength());
	}

	// Returns just the class name -- no package info.
	protected String getClassName(Object o) {
		String classString = o.getClass().getName();
		int dotIndex = classString.lastIndexOf(".");
		return classString.substring(dotIndex + 1);
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = SchematicPopupMenuRes.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("WRIMS Schematic");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create/set menu bar and content pane.
		SchematicPopupMenuRes demo = new SchematicPopupMenuRes();
		frame.setJMenuBar(demo.createMenuBar());
		frame.setContentPane(demo.createContentPane());

		// Create and set up the popup menu.
		demo.createPopupMenu();

		// Display the window.
		frame.setSize(300, 200);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	class PopupListener extends MouseAdapter {
		JPopupMenu popup;

		PopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
}
