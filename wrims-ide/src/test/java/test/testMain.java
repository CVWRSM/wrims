package test;

import java.io.File;
import java.lang.Runtime;

public class testMain {
	public static void main(String arg[]){
		try {
			//String commandLine[]=new String[2];
			//commandLine[0]="D:\\cvwrsm\\trunk\\3rd_party\\jre6\\bin\\java";
			//commandLine[1]="-version";
			//Process process = Runtime.getRuntime().exec(commandLine, null, null);
			
			String commandLine[]=new String[4];
			commandLine[0]="D:\\cvwrsm\\trunk\\3rd_party\\jre6\\bin\\java";
			commandLine[1]="test.testApplication";
			commandLine[2]=Integer.toString(3155);
			commandLine[3]=Integer.toString(3156);
			
			Process process = Runtime.getRuntime().exec(commandLine, null, new File("D:\\cvwrsm\\trunk\\wrims_v2\\wrimsv2_plugin\\bin\\"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
