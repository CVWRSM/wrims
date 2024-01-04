package wrims.misc;

import java.util.Arrays;


public class DssDataNotFoundException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String errMsg="";

	public DssDataNotFoundException() {
		super(); 
		errMsg = "unknown";
	}

	public DssDataNotFoundException(String errMsg1, String errMsg2) {
		super(errMsg1 + errMsg2); 
		this.errMsg = errMsg1 + errMsg2; 
	}

	public DssDataNotFoundException(String[] errMsgArray) {
		
		//super(Arrays.toString(errMsgArray));
		
		super(flattenArray(errMsgArray));	
		
		this.errMsg = flattenArray(errMsgArray); 
	}
	
	public String getErrMsg() {
		return errMsg;
	}
	
	private static String flattenArray(String[] array){
		
		String out = "";
		
		for ( String m: array){
			out = out + m +"\n"; 
		}
		return out;	
	}
	
}
