package wvscript.app;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.FilenameUtils;

import wvscript.gui.Misc;
import wvscript.gui.Params;
import wvscript.reader.element.ConfigReader;

public class WrimsStudy {

	public File studyRunDir;
	public String configFile;
	//public String studyName;
	public Map<String,String> configMap;
	//public JLabel lbl_status;
	
	public WrimsStudy(JLabel lbl_status) {

		//setStatusLabel(lbl_status);
		this.studyRunDir = null;
		this.configFile = null;
		//this.studyName = "";
		configMap = new HashMap<String,String>();
		
	}
	
//	public void setStatusLabel(JLabel lbl_status){
//		this.lbl_status = lbl_status; 	
//	}
	
//	public boolean checkConfigExtension(String configFileString){
//		// if fail return false
//		
//		// check if extension is .config
//		
//		if (!FilenameUtils.getExtension(configFileString).equalsIgnoreCase("config")) {
//			//lbl_status.setText(Misc.htmlText("", "Config file extension should be .config", "red"));	
//			return false;
//		}
//		
//		return true;
//	}
	
//	public boolean checkConfigExist(String configFileString){
//		// if fail return false
//		
//		File configF = new File(studyRunDir, configFileString);		
//		if (! configF.exists()) {
//			//lbl_status.setText(Misc.htmlText("", "Config file not exist.", "red"));	
//			return false;
//		}
//		
//		return true;
//
//	}
	
	public boolean parseConfig(){
		
		// if fail return 0
		
		File configF = new File(studyRunDir, configFile);	
		
		ConfigReader.setConfigKey(Params.configKeyList);
		
		try {
			ConfigReader.parseFile(configF.getAbsolutePath());
			configMap = ConfigReader.configMap;
			return true;
		} catch (RecognitionException e) {
			// TODO Auto-generated catch block
			//lbl_status.setText(Misc.htmlText("", "Error in parsing config file.", "red"));	
			configMap = null;
			//e.printStackTrace();
		}
		
		return false;
		

	}	
	
	
	

}
