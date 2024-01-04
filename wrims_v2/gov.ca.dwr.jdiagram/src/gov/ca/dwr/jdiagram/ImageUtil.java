package gov.ca.dwr.jdiagram;

import gov.ca.dwr.jdiagram.views.DiagramSelectionProvider;

import javax.swing.ImageIcon;

public class ImageUtil {

	/** Returns an ImageIcon, or null if the path was invalid. */
	public static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = Activator.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

}
