package wvscript.gui;

import java.awt.Color;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Params {

	public static final FileNameExtensionFilter filter_txt = new FileNameExtensionFilter("txt file", "txt");
	public static final FileNameExtensionFilter filter_dss = new FileNameExtensionFilter("dss file", "dss");
	public static final FileNameExtensionFilter filter_config = new FileNameExtensionFilter("config file", "config");
	public static final FileNameExtensionFilter filter_run = new FileNameExtensionFilter("run dir", "run");
	public static final String plsSpecifyRunDir = "Please specify study's run directory.";
	public static final String isNotValidRunDir = " is not a valid run directory.";
	public static final String notExist = " does not exist.";

	public static final SpinnerModel spm_start_day = new SpinnerNumberModel(1, 1, 31, 1);
	public static final SpinnerModel spm_stop_day = new SpinnerNumberModel(1, 1, 31, 1);
	public static final SpinnerModel spm_start_month = new SpinnerNumberModel(10, 1, 12, 1);
	public static final SpinnerModel spm_stop_month = new SpinnerNumberModel(10, 1, 12, 1);
	public static final SpinnerModel spm_start_year = new SpinnerNumberModel(1921, 1901, 2099, 1);
	public static final SpinnerModel spm_stop_year = new SpinnerNumberModel(1921, 1901, 2099, 1);
	
    private static final JSpinner spinner = new JSpinner(new SpinnerNumberModel(1,0,10,1));  
    private static final JTextField spinnerTF = ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField();  
    public static final Color spinnerForeground = spinnerTF.getForeground();  
    public static final Color spinnerBackground = spinnerTF.getBackground();  
	
	public static final String[] configKeyList = {
        "WreslPlus",     
        "MainFile", 
        "Solver",
        "InitFile",
        "InitFPart",
        "SvarFile",
        "SvarAPart",
        "SvarFPart",
        "DvarFile",                   
        "TimeStep",
        "StartYear", 
        "StartMonth",
        "StartDay",
        "NumberOfSteps",
        "StopYear",
        "StopMonth",
        "StopDay",
        "LookupSubDir",
        "GroundwaterDir",
        "ShowWreslLog",
        "PrefixInitToDvarFile",
        "SendAliasToDvar",
        "IlpLog",
        "IlpLogFormat",
        "IlpLogVarValue"
			};

}
