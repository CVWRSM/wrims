package wrims.schematic;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class Filter extends FileFilter {

	public Filter(String sExt, String sDescr) {
		super();
		myExtension = sExt.toLowerCase();
		myDescription = sDescr;
	}

	// Accept all directories and all .xsvg files.
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		// String extension = java.Utils.getExtension(f);
		String sPath = f.getPath();
		sPath.toLowerCase();
		if (sPath.indexOf(".") != -1) {
			if (sPath.endsWith(myExtension)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	// The description of this filter
	public String getDescription() {
		return myDescription;
	}

	public String getExtension() {
		return myExtension;
	}

	private String myExtension;
	private String myDescription;
}
