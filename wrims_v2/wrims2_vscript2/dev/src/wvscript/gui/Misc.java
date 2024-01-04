package wvscript.gui;

import java.io.File;

public class Misc {

	public static int checkRunDir(String runDir) {

		// 0: valid
		// 1: not ends with run
		// 2: not exist

		if (!runDir.toLowerCase().endsWith("run"))
			return 1;

		File rd = new File(runDir);
		if (!rd.exists())
			return 2;

		return 0;

	}
	
	
	public static String htmlText (String msg1, String msg2, String color){
				
		return "<html>"+msg1+"<font color="+color+">"+msg2+"</font></html>";
	}

}
