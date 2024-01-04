package wvscript.reader.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.testng.Assert;
import org.testng.annotations.Test;

import wvscript.reader.grammar.ConfigSimpleLexer;
import wvscript.reader.grammar.ConfigSimpleParser;



public class ConfigReader {
	
	private static CharStream stream;	
	public static Map<String, String> configMap;
	private static String[] configKeyList;
	private static Map<String , String> configKeyMap;


	public ConfigReader(String[] configKeyListInput){
		
		configKeyList = configKeyListInput;		
		configKeyMap = new HashMap<String , String>();
		
		for (String key: configKeyList) {
			configKeyMap.put(key.toLowerCase(), key);
		
		}

	}
	
	public static void setConfigKey(String[] configKeyListInput){
		
		configKeyList = configKeyListInput;		
		configKeyMap = new HashMap<String , String>();
		
		for (String key: configKeyList) {
			configKeyMap.put(key.toLowerCase(), key);
		
		}

	}	
	
	public static Map<String, String> newConfigMap(){
		
		return new LinkedHashMap<String, String>();
	}
	
	//@Test( groups = { "config" } )
	public static void parseFile(String inputFilePath) throws RecognitionException  {		

		//String inputFilePath = "D:\\cvwrsm\\trunk\\wrims2_scripting\\studies\\callite_svn47\\CONV\\Run\\CONV.config";
		try {
			stream = new ANTLRFileStream(inputFilePath, "UTF8");
			}
	    catch(Exception e) {
	         //e.printStackTrace();
	         System.out.println("Config file not found! "+ inputFilePath);
	         
	        }
		    
		ConfigSimpleLexer lexer = new ConfigSimpleLexer(stream);
		TokenStream tokenStream = new CommonTokenStream(lexer);		

		ConfigSimpleParser parser = new ConfigSimpleParser(tokenStream);
		

		
//	      final Map<String , String> reservedKeyMap = new HashMap<String , String>() {{
//	          put("wreslplus",   "WreslPlus"          );
//	          put("mainfile" ,   "MainFile"           );
//	          put("solver"   ,   "Solver"             );
//	          put("initfile" ,   "InitFile"           );
//	          put("initfpart",   "InitFPart"          );       
//	          put("svarfile" ,   "SvarFile"           );
//	          put("svarapart",   "SvarAPart"          );
//	          put("svarfpart",   "SvarFPart"          );
//	          put("dvarfile" ,   "DvarFile"           );            
//	          put("timestep" ,   "TimeStep"           );
//	          put("startyear",   "StartYear"          );
//	          put("startmonth",  "StartMonth"         );
//	          put("startday",    "StartDay"           );
//	          put("numberofsteps", "NumberOfSteps"    );
//	          put("stopyear" ,    "StopYear"          );
//	          put("stopmonth",    "StopMonth"         );
//	          put("stopday"  ,    "StopDay"           );
//	          put("lookupsubdir"  ,    "LookupSubDir"           );
//	          put("groundwaterdir", "GroundWaterDir"  );
//	          put("showwresllog"  , "ShowWreslLog"    );
//	          put("sendaliastodvar" , "SendAliasToDvar"                    );
//	          put("prefixinittodvarfile", "PrefixInitToDvarFile"           );
//	          put("ilpmaximumfractiondigits" , "IlpMaximumFractionDigits"  );
//	          put("ilplog"  , "IlpLog"                                     );
//	          put("ilplogformat"  , "IlpLogFormat"                         );
//	          put("ilplogvarvalue" ,"IlpLogVarValue"                       );
//	       }};
		
		
		parser.configKeyMap = ConfigReader.configKeyMap;	
		parser.configFile();
		
		
		configMap = parser.configMap;
		
		//return configMap;
		

	}	


}
	
