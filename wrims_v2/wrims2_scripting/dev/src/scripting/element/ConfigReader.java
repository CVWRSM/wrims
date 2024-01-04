package scripting.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.testng.Assert;
import org.testng.annotations.Test;

import scripting.grammar.ConfigSimpleLexer;
import scripting.grammar.ConfigSimpleParser;



public class ConfigReader {
	
	private static CharStream stream;	
	public static Map<String, String> configMap;
	private static ArrayList<String> configKeyList;


	public ConfigReader(){
		
		configKeyList = new ArrayList<String>();
		

		configKeyList.add("MainFile");
		configKeyList.add("Solver");   
		configKeyList.add("InitFile");
		configKeyList.add("InitFPart");
		configKeyList.add("SvarFile");
		configKeyList.add("SvarAPart");
		configKeyList.add("SvarFPart");
		configKeyList.add("DvarFile");                   
		configKeyList.add("TimeStep");
		configKeyList.add("StartYear"); 
		configKeyList.add("StartMonth");
		//configKeyList.add("StartDay");
		configKeyList.add("NumberOfSteps");
		configKeyList.add("EndYear");
		configKeyList.add("EndMonth");
		//configKeyList.add("EndDay");
		configKeyList.add("GroundwaterDir");
		configKeyList.add("ShowWreslLog");
		

	}
	
	public static ArrayList<String> getConfigKeyList(){
				
		return configKeyList;
		
		
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
		

		parser.configFile();
		
		//System.out.println(parser.cMap);
		
		//Map<String, String> configMap = new HashMap<String, String>();

		//Assert.assertEquals(0, 1);
		
//		for (String k : parser.cMap.keySet()) {
//			
//			configMap.put(k.toLowerCase(), parser.cMap.get(k));			
//		
//		}
		
		configMap = parser.cMap;
		
		//return configMap;
		

	}	


}
	
