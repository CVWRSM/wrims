package wrims.schematic;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import wrims.dss.FFilter;

/**
 * Provides static reference to parameters and objects in the the schematic.
 * 
 * @author Clay Booher
 * @version
 */
public final class SchematicUtils {

	/**
	 * Prevents instantiation of this strictly utility class
	 */
	private SchematicUtils() {
		// prevent instantiation
	}

	public static String VERSION = "WRIMS Schematic 1.0 Beta";

	// general objects
	static MainFrame schematic; // CB TODO should make this private in the
								// future if not too much trouble

	public static final String[] MONTHS = { "JAN", "FEB", "MAR", "APR", "MAY",
			"JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

	// Copied Object Vector (for copy and paste options)
	private static Vector _copiedObjects = new Vector();

	/**
	 * Gets the Vector of copied Objects for the purpose of pasting them
	 * 
	 * @return
	 */
	public static Vector getCopiedObjects() {
		return _copiedObjects;
	}

	/**
	 * CB added. No security here, but the design of the WRIMSSchematic code is
	 * mediocre, not how it should have been initially planned.
	 * 
	 * @return
	 */
	public static MainFrame getSchematic() {
		return schematic;
	}

	/**
	 * 
	 * @param x
	 */
	// public static void setStatusLabel(String x) {
	// overLord.setStatusLabel(x);
	// }

	/**
	 * 
	 * @param month
	 * @param year
	 * @return
	 */
	public static int daysInMonth(int month, int year) {
		final int[] daysin = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		int daysInMonth = daysin[month];
		// lines below for leap years
		if (year % 100 != 0 || year % 400 == 0) {
			if (month == 1 && year % 4 == 0) {
				daysInMonth = 29;
			}
		}
		return daysInMonth;
	}

	/**
	 * CB added - altered Nicky's version in VistaUtils.
	 * 
	 * @param comp
	 * @param type
	 * @param extension
	 * @param description
	 * @param dirOnly
	 * @return
	 */
	// public String getDirOrFileFromDialog(Component comp, int type,
	// String extension, String description, boolean dirOnly) {
	public static String getFilenameFromDialog(Component comp, int type,
			String extension, String description, boolean dirOnly) {
		Frame frame = JOptionPane.getFrameForComponent(comp);
		String filename = null;
		// String title = "Save File to..."; // Never read
		// if ( type == FileDialog.LOAD ) title = "Load File from...";
		JFileChooser chooser = new JFileChooser();
		if (dirOnly)
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// chooser.setCurrentDirectory(new File(_lastDirectory)); //CB TODOD use
		// preferences
		if (!dirOnly) {
			javax.swing.filechooser.FileFilter filter = new FFilter(extension,
					description);
			chooser.addChoosableFileFilter(filter);
			chooser.setFileFilter(filter);
		}
		int rv = 0;
		if (type == FileDialog.LOAD) {
			rv = chooser.showOpenDialog(frame);
		} else {
			rv = chooser.showSaveDialog(frame);
		}
		if (rv == JFileChooser.APPROVE_OPTION) {
			if (chooser.getSelectedFile() == null)
				filename = null;
			else
				filename = chooser.getSelectedFile().getPath();
		}
		// CB if (filename != null) { //CB TODO ? use preferences
		// _lastDirectory = chooser.getSelectedFile().getParent() == null ?
		// _lastDirectory
		// : chooser.getSelectedFile().getParent();
		// }
		return filename;
	}
}