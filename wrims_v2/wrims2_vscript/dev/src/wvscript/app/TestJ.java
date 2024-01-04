package wvscript.app;

import java.io.File;
import java.util.ArrayList;

import org.python.core.PyException;
import org.python.core.PyStringMap;
import org.python.core.PySystemState;

public class TestJ {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		org.python.util.PythonInterpreter interp = new org.python.util.PythonInterpreter();
		interp.exec("import sys");
		interp.exec("import os");
		
		ArrayList<String> p = new ArrayList<String>();
		p.add(".");
		p.add("./lib/vista/lib/Lib");


		
		for ( String pyp: p){
		//interp.exec("sys.path.append(\".\")");
			interp.exec("sys.path.append(\""+ pyp +"\")");
		}

		String scriptname = "scripts/app/loadStudy.py"; 
		
		interp.exec("__file__ = os.path.join(os.path.abspath(sys.argv[0]), \'"+scriptname +"\') ");

		interp.execfile(scriptname);
		
		
	}

}
