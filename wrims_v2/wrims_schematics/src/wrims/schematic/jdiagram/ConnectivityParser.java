package wrims.schematic.jdiagram;

import java.io.FileReader;
import java.io.LineNumberReader;

public class ConnectivityParser {
	public ConnectivityParser(){
		
	}
	
	public void parseAndStoreFrom(String file) throws Exception{
		LineNumberReader r = new LineNumberReader(new FileReader(file));
		String line = null;
		while ( (line=r.readLine()) != null){
			line = cleanForComments(line);
			extractContinuityElements(line);
		}
	}

	private void extractContinuityElements(String line) {
		// TODO Auto-generated method stub
		
	}

	private String cleanForComments(String line) {
		return line.split("!")[0];
	}
}
