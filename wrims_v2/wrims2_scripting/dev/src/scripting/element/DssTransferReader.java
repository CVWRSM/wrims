package scripting.element;

import java.util.ArrayList;
import java.util.Map;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import scripting.grammar.ConfigSimpleLexer;
import scripting.grammar.ConfigSimpleParser;



public class DssTransferReader {
	
	private static CharStream stream;	
	public static Map<String, ArrayList<String>> transferMap;
	//public static ArrayList<String> varList;


	public DssTransferReader(){

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
		

		parser.dssTransfer();
		
		//System.out.println(parser.cMap);
		
		//Map<String, String> configMap = new HashMap<String, String>();

		//Assert.assertEquals(0, 1);
		
//		for (String k : parser.cMap.keySet()) {
//			
//			configMap.put(k.toLowerCase(), parser.cMap.get(k));			
//		
//		}
		
		//configMap = parser.cMap;
		

		//varList = parser.varList;
		transferMap = parser.transferMap;
		
		//return configMap;
		

	}	


}
	
