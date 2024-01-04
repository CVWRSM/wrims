grammar ConfigSimple;
//import CommonLexer;

options {
  language = Java;
  output=AST;
  ASTLabelType=CommonTree;
}

@header {
  package wvscript.reader.grammar;
  import java.util.Map;
  import java.util.HashMap;
  import java.util.LinkedHashMap;
  import java.util.Arrays;
  import java.util.HashSet;
  import java.util.Set;
}
@lexer::header {
  package wvscript.reader.grammar;
}

@members {
      //public CommonTree commonTree;
      public Map<String, String> configMap = new LinkedHashMap<String, String>();
      public Map<String, ArrayList<String>> transferMap = new LinkedHashMap<String, ArrayList<String>>();
      //public ArrayList<String> varList = new ArrayList<String>();
      public ArrayList<String> t = new ArrayList<String>();

      public Map<String , String> configKeyMap; // this is input from caller
}


dssTransfer : ENDLINE* Begin 'DssTransfer' ENDLINE+ 

			( c=transferItem  
				{ 
				
				  //varList.add($c.key);
				  	
				  if (transferMap.containsKey($c.key)){
					transferMap.get($c.key).add($c.val);
				  } else {
				    t = new ArrayList<String>();
				    t.add($c.val);
				    transferMap.put($c.key, t);
				  } 
				} 
				)+  
			End 'DssTransfer' //{ System.out.println( cMap); }
			
			; 
			
transferItem returns[String key, String val ]
	: 

    k=ID  v=pair     ENDLINE+
   { $key=$k.text.toUpperCase(); $val=$v.text;  }
   //{ System.out.println($k.text); System.out.println($v.text); }
   ;

pair : ID('-'|ID)*  '/' ID('-'|ID)* ; 
   
configFile : 
      ENDLINE* 
      (Begin Config ENDLINE+)? 
			( c=configItem  { configMap.put($c.key, $c.val); } )+  
			(End Config)?  //{ System.out.println( cMap); }
			
			;


configItem returns[String key, String val ]
	: 

    k=configKey         { $key=configKeyMap.get($k.text.toLowerCase()); } 
                        //{ System.out.print("key: "+$key);System.out.print("\t");} 
     
    v2=complex        { $val=$v2.text;} //System.out.println($v2.text);} 
     
    ENDLINE+
   

   ;

complex : ( ID | ID_preINT | INT | '.' | '-' | '_' | '\"' | '\\' | ':' )+ ;




configKey : ke=ID 
{ 
    
    if (!configKeyMap.keySet().contains( $ke.text.toLowerCase() ) ) 
    { throw new RuntimeException("Error! "+$ke.text + " is not a recognized keyword in the config file!" ); } 
}
;


SL_COMMENT : '#' ~('\r'|'\n')*  {$channel=HIDDEN;} ;




Begin  : 'Begin' | 'begin' | 'BEGIN'  ;
End    : 'End' | 'end' | 'END'    ;
Config : 'Config' | 'config' | 'CONFIG' ;

INT :        Digit+ ;
ID_preINT :  Digit ( Letter | Digit | '_' )*;
ID :        Letter ( Letter | Digit | '_' )*;


fragment Letter : 'a'..'z' | 'A'..'Z';

fragment Digit : '0'..'9';



WS : (' ' | '\t' ) {$channel=HIDDEN;};



ENDLINE : ( '\n' | '\r' ) ;
