package wvscript.gui;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

public class DirectoryRestrictedFileSystemView extends FileSystemView {
	private final File[] rootDirectories;

	public DirectoryRestrictedFileSystemView(File rootDirectory) {
		this.rootDirectories = new File[]{rootDirectory};
	}

	public DirectoryRestrictedFileSystemView(File[] rootDirectories) {
		this.rootDirectories = rootDirectories;
	}

	@Override
	public File createNewFolder(File containingDir) throws IOException {
		throw new UnsupportedOperationException("Unable to create directory");
	}

	@Override
	public File[] getRoots() {
		return rootDirectories;
	}

	@Override
	public boolean isRoot(File file) {
		for (File root : rootDirectories) {
			if (root.equals(file)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public File getHomeDirectory() {
		return rootDirectories[0];
	}

}