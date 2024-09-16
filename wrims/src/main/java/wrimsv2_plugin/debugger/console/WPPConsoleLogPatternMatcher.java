package wrimsv2_plugin.debugger.console;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.ui.console.FileLink;

import wrimsv2_plugin.debugger.exception.WPPException;

public class WPPConsoleLogPatternMatcher implements IPatternMatchListenerDelegate {
	private String fFilePath; 
	private int line=1;
	private TextConsole console;
	
	
	
	@Override
	public void connect(TextConsole console) {
		this.console=console;
	}
	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void matchFound(PatternMatchEvent event) {
		try { 
			String sourceLocation = console.getDocument().get(event.getOffset()+1, event.getLength()-2);
			int index=sourceLocation.lastIndexOf(":");
			fFilePath=sourceLocation.substring(0, index);
			line=Integer.parseInt(sourceLocation.substring(index+1));
			File sourceFile=new File(fFilePath);
			String canonicalPath = sourceFile.getCanonicalPath();
			IPath path = new Path(canonicalPath);
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
			console.addHyperlink(new FileLink(file, null, -1, -1, line), event.getOffset(), event.getLength());
		} catch (Exception e) {
			WPPException.handleException(e);
		} 
	}
}
