package wrimsv2_plugin.sensitivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.tools.FileProcess;

public class SensitivityRun {

	private int numRuns=1;
	private String tableName="SensitivityIndex";
	private ILaunchConfiguration configuration; 
	
	public SensitivityRun(ILaunchConfiguration configuration){
		this.configuration=configuration;
		try {
			String numRunsString = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_NUMSENSITIVITYRUN, "1");
			numRuns = Integer.parseInt(numRunsString);
			
			tableName = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_SENSITIVITYINDEXTABLENAME, "SensitivityIndex");
		} catch (CoreException e) {
			WPPException.handleException(e);
		}
	}
	
	public int getNumRuns(){
		return numRuns;
	}
	
	public void createSensitivityTable(int sri){
		try {
			int ms=Integer.parseInt(configuration.getAttribute(DebugCorePlugin.ATTR_WPP_MULTISTUDY, "1"));
			for (int sid=1; sid<=ms; sid++){
				String suffix="";
				if (sid>1) suffix="_MS"+sid;
				String mainFilePath = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_PROGRAM+suffix, (String)null);
				String mainFileAbsPath;
				if (new File(mainFilePath).isAbsolute()){
					mainFileAbsPath = mainFilePath;
				}else{
					mainFileAbsPath = FileProcess.procRelativePath(mainFilePath, configuration);
				}				
				int fi=mainFileAbsPath.lastIndexOf(File.separator);
				String mainDirectory = mainFileAbsPath.substring(0,fi+1);
				String tablePath=mainDirectory+"lookup"+File.separator+tableName+".table";
				FileWriter tableWriter = new FileWriter(tablePath);
				PrintWriter out = new PrintWriter(tableWriter);
				out.println(tableName);
				out.println("Index"+"\t"+"SRI");
				out.println("1"+"\t"+sri);
				out.close();
				tableWriter.close();
			}
		} catch (NumberFormatException e) {
			WPPException.handleException(e);
		} catch (CoreException e) {
			WPPException.handleException(e);
		} catch (IOException e) {
			WPPException.handleException(e);
		}
	}
}
