package wrimsv2_plugin.debugger.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.tools.FileProcess;

public class WPPFileIncExploreView extends ViewPart {
	private TreeViewer viewer;

	public class FileIncContentProvider implements ITreeContentProvider {

		@Override
		public Object[] getChildren(Object parent) {
			File file = (File) parent;
			File[] files = file.listFiles();
			ArrayList<File> fileFoldersInc=new ArrayList<File> ();
			ArrayList<File> filesInc= new ArrayList<File> ();
			for (int i=0; i<files.length; i++){
				String path=files[i].getAbsolutePath();
				if (DebugCorePlugin.fileFolderWreslInc.contains(path.toLowerCase())){
					if (files[i].isDirectory()){
						fileFoldersInc.add(files[i]);
					}else{
						filesInc.add(files[i]);
					}
				}
			}
			fileFoldersInc.addAll(filesInc);
			File[] fileArray = fileFoldersInc.toArray(new File[fileFoldersInc.size()]);
			return fileArray;
		}

		public Object[] getElements(Object inputElement) {
			return (Object[]) inputElement;
		}

		@Override
		public Object getParent(Object element) {
			File file = (File) element;
			return file.getParentFile();
		}

		@Override
		public boolean hasChildren(Object parent) {
			File file = (File) parent;
			return file.isDirectory();
		}

		@Override
		public void dispose() {

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
		}
		
	}
    
	public class FileLabelProvider extends LabelProvider {

		private final Image folderImage = DebugCorePlugin.getImageDescriptor("folder.gif").createImage();
		private final Image driveImage = DebugCorePlugin.getImageDescriptor("filenav_nav.gif").createImage();
		private final Image fileImage = DebugCorePlugin.getImageDescriptor("file_obj.gif").createImage();

		@Override
		public Image getImage(Object element) {
			File file = (File) element;
			if (file.isDirectory())
				return file.getParent() != null ? folderImage : driveImage;
				return fileImage;
			}

		@Override
		public String getText(Object element) {
			String fileName = ((File) element).getName();
			if (fileName.length() > 0) {
				return fileName;
			}
			return ((File) element).getPath();
		}
		
	}

	
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new FileIncContentProvider());
		viewer.setLabelProvider(new FileLabelProvider());
		File[] files=File.listRoots();
		viewer.setInput(files);
		viewer.addOpenListener(new IOpenListener() {
			@Override
			public void open(OpenEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
								
				final File firstEle = (File)selection.getFirstElement();
				IWorkspace workspace= ResourcesPlugin.getWorkspace();    
				IPath location= Path.fromOSString(firstEle.getAbsolutePath()); 
				final IFile ifile= workspace.getRoot().getFileForLocation(location);
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
		    		public void run(){
		    			String fn=firstEle.getName();
		    			if (FileProcess.isTableFile(fn)){
		    				try {
								Desktop.getDesktop().open(firstEle);
							} catch (IOException e) {
								WPPException.handleException(e);
							}
		    			}else if (FileProcess.isWreslFile(fn)){
		    				IEditorRegistry registry = workbench.getEditorRegistry();
			    			IEditorDescriptor desc= registry.getDefaultEditor(fn);
		    				try {
			    				workbench.getActiveWorkbenchWindow().getActivePage().openEditor(new FileEditorInput(ifile), desc.getId());
			    			} catch (PartInitException e) {
			    				WPPException.handleException(e);
			    			}
		    			}
		    		}
				});
			}
		});
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	public void update(){
		Tree tree=viewer.getTree();
		File[] roots=File.listRoots();
		ArrayList<File> rootArray=new ArrayList<File> ();
		for (int i=0; i<roots.length; i++){
			if (DebugCorePlugin.fileFolderWreslInc.contains(roots[i].getAbsolutePath().toLowerCase())) rootArray.add(roots[i]);
		}
		File[] newRoots=rootArray.toArray(new File[rootArray.size()]);
		viewer.setInput(newRoots);
		tree.update();
	}
}


