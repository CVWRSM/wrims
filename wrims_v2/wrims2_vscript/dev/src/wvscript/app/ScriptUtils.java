package wvscript.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.python.core.PyException;
import org.python.core.PyStringMap;
import org.python.core.PySystemState;

public class ScriptUtils {
	
	public static void initialJython(org.python.util.PythonInterpreter interp, String scriptName) {
		
		// initialize jython 
		interp.exec("import sys");
		interp.exec("import os");
		
		ArrayList<String> p = new ArrayList<String>();
		p.add(".");
		p.add("./lib/vista/lib/Lib");

		
		for ( String pyp: p){
			interp.exec("sys.path.append(\""+ pyp +"\")");
		}
		
		interp.exec("__file__ = os.path.join(os.path.abspath(sys.argv[0]), \'"+scriptName +"\') ");

		interp.execfile("scripts/app/commonImports.py");	
		
	}
	
	public static void cleanVars(org.python.util.PythonInterpreter interp) {
	
		interp.exec("appUtils.clearVars()");	
		
		
	}


}
