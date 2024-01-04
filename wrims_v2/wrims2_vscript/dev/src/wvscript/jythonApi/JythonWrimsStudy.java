package wvscript.jythonApi;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.python.core.PyException;
import org.python.core.PyStringMap;
import org.python.core.PySystemState;

import wvscript.app.ScriptUtils;

public class JythonWrimsStudy {

	org.python.util.PythonInterpreter interp;
	private final String scriptName = "invokedFromGUI"; 
	private final String studyRunDir;
	private final String studyName;
	private Map<String,String> configMap;

	
	public JythonWrimsStudy(String studyName, String studyRunDir) {

		// initialize
		interp = new org.python.util.PythonInterpreter();
		this.studyRunDir = studyRunDir;
		this.studyName = studyName;
		configMap = new HashMap<String,String>();
		
		//initialJython();
		ScriptUtils.initialJython(interp, scriptName);
				
		interp.exec("s = Study('" + this.studyName + "', r'" + this.studyRunDir + "') ");
		
		
		//interp.exec("studyRunDir = r\'D:\\cvwrsm\\trunk\\wrims2_vscript\\studies\\callite_D1641\\run\' ");
		//interp.exec("print studyRunDir");		
		//interp.exec("configFile = \'D1641.config\' ");
		//interp.exec("print configFile");
		//interp.exec("s = Study(\'testStudy\', studyRunDir) ");
		//interp.exec("s.loadConfig(configFile)  ");
		//interp.exec("s.run() ");		
		
	}
	
//	public void initialJython() {
//	
//		// initialize jython 
//		interp.exec("import sys");
//		interp.exec("import os");
//		
//		ArrayList<String> p = new ArrayList<String>();
//		p.add(".");
//		p.add("./lib/vista/lib/Lib");
//
//		
//		for ( String pyp: p){
//			interp.exec("sys.path.append(\""+ pyp +"\")");
//		}
//		
//		interp.exec("__file__ = os.path.join(os.path.abspath(sys.argv[0]), \'"+scriptName +"\') ");
//
//		interp.execfile("scripts/app/commonImports.py");	
//		
//	}

	public void createConfig(Map<String,String> configMap) {

		this.configMap = configMap;
		interp.exec("s.createConfig( ) ");
	
	}

	public void importConfig(String configFile) {

		interp.exec("s.importConfig('"+ configFile + "') ");
		
	}

	public void run() {

		interp.exec("s.run() ");
		
	}
	
	public void deleteSty() {
		
		interp.exec("s=None ");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		JythonWrimsStudy sty = new JythonWrimsStudy(
				"test3Study",
				"D:\\cvwrsm\\trunk\\wrims2_vscript\\studies\\callite_D1641\\run");
		
		sty.importConfig("D1641.config");

		sty.run();
		//sty.deleteSty();		
		
		//sty.interp.exec("s = Study('" + sty.studyName + "', r'" + sty.studyRunDir + "') ");
		//sty.interp.exec("s.loadConfig('"+ sty.configFile + "') ");
		//sty.interp.exec("s.run() ");	
			
	
	}
}
