package wrims.schematic.jdiagram;

import javax.swing.ImageIcon;

public class ImageUtil {

	/** Returns an ImageIcon, or null if the path was invalid. */
	public static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = PanelWithCollapsibleInsetPanel.class
				.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

}
