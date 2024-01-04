package wrims.schematic.jdiagram;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

import wrims.schematic.MainFrame;

@SuppressWarnings("serial")
public class SchematicEditorFrame extends JFrame {

	protected static final String VERSION_ID = "1.0-v02/07/2011";
	private SchematicEditor editor;
	private String currentFilenameOpen;

	public SchematicEditorFrame() {
		editor = new SchematicEditor();

		String filename = Preferences.userNodeForPackage(
				MainFrame.class).get("last.schematic",
				"resources/wrims/schematic/CS3_NetworkSchematic.xml");
		loadSchematic(filename);

		Icon saveAsIcon = ImageUtil.createImageIcon("images/save_as.png");
		Icon saveIcon = ImageUtil.createImageIcon("images/save.png");
		Icon openIcon = ImageUtil.createImageIcon("images/open.png");

		Action fileSaveAsAction = new AbstractAction("Save As", saveAsIcon) {

			@Override
			public void actionPerformed(ActionEvent e) {
				String filename = chooseFileToSave();
				if (filename == null)
					return;
				try {
					editor.save(filename);
					currentFilenameOpen = filename;
					SchematicEditorFrame.this.setTitle(currentFilenameOpen);
					editor.setTitle(currentFilenameOpen);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		fileSaveAsAction.putValue(Action.NAME, "Save Schematic As...");
		fileSaveAsAction.putValue(Action.SHORT_DESCRIPTION, "Save Schematic As...");

		Action fileOpenAction = new AbstractAction("Open", openIcon) {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					SchematicEditorFrame.this.setCursor(Cursor
							.getPredefinedCursor(Cursor.WAIT_CURSOR));
					String filename = chooseFileToOpen();
					if (filename == null) {
						return;
					}
					loadSchematic(filename);
				} finally {
					SchematicEditorFrame.this.setCursor(Cursor.getDefaultCursor());
				}
			}
		};
		fileOpenAction.putValue(Action.NAME, "Open Schematic...");
		fileOpenAction.putValue(Action.SHORT_DESCRIPTION,"Open Schematic...");

		Action fileSaveAction = new AbstractAction("Save", saveIcon) {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					editor.save(currentFilenameOpen);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		};
		fileSaveAction.putValue(Action.NAME, "Save Schematic");
		fileSaveAction.putValue(Action.SHORT_DESCRIPTION, "Save Schematic");

		JToolBar bar = new JToolBar();
		bar.add(fileOpenAction);
		bar.add(fileSaveAction);
		bar.add(fileSaveAsAction);
		bar.addSeparator();
		bar.add(editor.getExportAction());
		bar.add(editor.getImportAction());
		bar.addSeparator();
		bar.add(editor.getUndoAction());
		bar.add(editor.getRedoAction());
		bar.addSeparator();
		//bar.add(editor.getZoomRectangleAction());
		bar.add(editor.getZoomInAction());
		bar.add(editor.getZoomOutAction());
		bar.add(editor.getZoomNormalAction());
		bar.add(editor.getZoomToFitAction());
		bar.addSeparator();
		JToggleButton autoAlignButton = new JToggleButton(editor.getToggleAutoAlignAction());
		autoAlignButton.doClick();
		bar.add(autoAlignButton);
		bar.add(editor.getVerticalAlignAction());
		bar.add(editor.getHorizontalAlignAction());
		bar.add(new JCheckBox(editor.getToggleEvenlySpaceNodes()));
		// bar.add(new JToggleButton(editor.getToggleGridLinesAction()));
		bar.addSeparator();
		bar.add(editor.createFindPanel());

		JFrame fr = this;
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(BorderLayout.PAGE_START, bar);
		mainPanel.add(editor);

		JMenuBar mbar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(fileOpenAction);
		fileMenu.add(fileSaveAction);
		fileMenu.add(fileSaveAsAction);
		mbar.add(fileMenu);

		JMenu editMenu = new JMenu("Edit");
		editMenu.add(editor.getUndoAction());
		editMenu.add(editor.getRedoAction());
		editMenu.addSeparator();
		editMenu.add(editor.getShrinkToElementsAction());
		editMenu.addSeparator();
		editMenu.add(editor.getSelectFontAction());
		mbar.add(editMenu);

		JMenu toolMenu = new JMenu("Tools");
		toolMenu.add(editor.getHorizontalAlignAction());
		toolMenu.add(editor.getVerticalAlignAction());
		toolMenu.add(new JCheckBoxMenuItem(editor.getToggleEvenlySpaceNodes()));
		mbar.add(toolMenu);
		
		JMenu helpMenu = new JMenu("Help");
		Action aboutAction = new AbstractAction("About") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(SchematicEditorFrame.this, "Schematic Editor Version: "+VERSION_ID);
			}
		};
		helpMenu.add(new JMenuItem(aboutAction));
		
		
		mbar.add(Box.createHorizontalGlue());
		mbar.add(helpMenu);

		fr.setJMenuBar(mbar);

		fr.getContentPane().add(mainPanel);
		fr.pack();
		fr.setSize(800, 600);
		fr.setVisible(true);
	}

	private void loadSchematic(String filename) {
		try {
			editor.load(filename);
			currentFilenameOpen = filename;
			setTitle(currentFilenameOpen);
			Preferences.userNodeForPackage(MainFrame.class).put(
					"last.schematic", currentFilenameOpen);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String chooseFileToSave() {
		Preferences p = Preferences
				.userNodeForPackage(MainFrame.class);
		String currentDirectory = p.get("SCHEMATICS_DIRECTORY", System
				.getProperty("user.dir"));
		JFileChooser chooser = new JFileChooser(currentDirectory);
		chooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "schematics";
			}

			@Override
			public boolean accept(File f) {
				return f != null && f.isFile() || f.isDirectory();
			}
		});
		int rval = chooser.showSaveDialog(this);
		if (rval != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		File selectedFile = chooser.getSelectedFile();
		p.put("SCHEMATICS_DIRECTORY", selectedFile.getParent());
		return selectedFile.getAbsolutePath();
	}

	private String chooseFileToOpen() {
		Preferences p = Preferences
				.userNodeForPackage(MainFrame.class);
		String currentDirectory = p.get("SCHEMATICS_DIRECTORY", System
				.getProperty("user.dir"));
		JFileChooser chooser = new JFileChooser(currentDirectory);
		chooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "schematics";
			}

			@Override
			public boolean accept(File f) {
				return f != null
						&& f.isFile()
						&& (f.getName().toLowerCase().endsWith(".xml") || f
								.getName().toLowerCase().endsWith(".sch"))
						||f.isDirectory();
			}
		});
		int rval = chooser.showOpenDialog(this);
		if (rval != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		File selectedFile = chooser.getSelectedFile();
		p.put("SCHEMATICS_DIRECTORY", selectedFile.getParent());
		return selectedFile.getAbsolutePath();
	}

	public static void main(String[] args) throws Exception {
		new SchematicEditorFrame();
	}
}
