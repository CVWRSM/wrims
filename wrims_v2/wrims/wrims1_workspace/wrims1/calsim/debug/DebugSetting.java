package calsim.debug;

public class DebugSetting{
	  public static boolean DEBUG_FILEINPUT = false;
	  private static String _DEBUG_OPTION = "none";
	  
	  public static void setDebugOption(String var){
		  _DEBUG_OPTION = var;  
			
	  }
	  public static String getDebugOption(){
		  return _DEBUG_OPTION;  
			
	  }

}

