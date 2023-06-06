package test.test_studies;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.testng.annotations.Test;

import wrimsv2.commondata.wresldata.Goal;
import wrimsv2.commondata.wresldata.ModelDataSet;
import wrimsv2.commondata.wresldata.StudyDataSet;
import wrimsv2.commondata.wresldata.Svar;
import wrimsv2.components.ControlData;
import wrimsv2.components.Controller;
import wrimsv2.components.Error;
import wrimsv2.components.FilePaths;
import wrimsv2.evaluator.TimeOperation;
import wrimsv2.wreslparser.elements.LogUtils;
import wrimsv2.wreslparser.elements.StudyConfig;
import wrimsv2.wreslparser.elements.StudyParser;
import wrimsv2.wreslparser.elements.TempData;
import wrimsv2.wreslparser.elements.WriteCSV;

public class testStudies {

	@Test
	public void testMain() throws RecognitionException, IOException{
		new Controller();
	}
	
	public void testSimpleStudy() throws RecognitionException, IOException{
        FilePaths.groundwaterDir="";
		FilePaths.setSvarFilePaths("Z:\\CalSimOrig\\CommonData\\BigExampleSV.dss");
        FilePaths.setInitFilePaths("Z:\\CalSimOrig\\CommonData\\BigExampleINIT.dss");
        FilePaths.setDvarFilePaths("Z:\\CalSimOrig\\Example10\\DSS\\TestWRIMSV2DV.dss");
        FilePaths.setMainFilePaths("Z:\\CalSimOrig\\Example10\\run\\Example10.wresl");
		ControlData cd=new ControlData();
		cd.svDvPartF="EXAMPLE";
		cd.initPartF="INIT";
		cd.partA = "CALSIM";
		cd.partE = "1MON";
		cd.timeStep="1MON";
        cd.startYear=1921;
        cd.startMonth=10;
        cd.startDay=31;
        cd.endYear=2003;
        cd.endMonth=9;
        cd.endDay=30;
        cd.simulationTimeFrame=TimeOperation.dssTimeFrame(cd.startYear, cd.startMonth, cd.startDay, cd.endYear, cd.endMonth, cd.endDay);
        cd.currYear=ControlData.startYear;
        cd.currMonth=ControlData.startMonth;
        cd.currDay=ControlData.startDay;
        cd.solverName="XA";
        FilePaths.csvFolderName="csv";
		
		StudyDataSet sds=parseSimpleStudy();
		
		new Controller();
	}
	
	public StudyDataSet parseSimpleStudy() throws RecognitionException, IOException{
		
		String csvFolderPath = "TestWreslWalker_simple_study";
		String inputFilePath = "Z:\\CalSimOrig\\Example10\\run\\Example10.wresl";
		String logFilePath = csvFolderPath+".log";
		
		File absFile = new File(inputFilePath).getAbsoluteFile();
		String absFilePath = absFile.getCanonicalPath().toLowerCase();
		
		
		/// temporary dataset, don't use this because the structure will be changed soon. 
		LogUtils.setLogFile(logFilePath);
		TempData td = new TempData();
		StudyConfig sc = StudyParser.processMainFileIntoStudyConfig(absFilePath);
		td.model_dataset_map=StudyParser.parseModels(sc,td);
		LogUtils.closeLogFile();
		
		
		/// write to StudyDataSet
		StudyDataSet sd = StudyParser.writeWreslData(sc, td); 
				
		/// write full study data to csv files
		WriteCSV.study(sd, csvFolderPath ) ;
	
		return sd;	
	}
	
	public void testCalsim3_BO()throws RecognitionException, IOException{
		FilePaths.groundwaterDir="D:\\cvwrsm\\trunk\\calsim30\\calsim30_bo\\common\\CVGroundwater\\Data\\";
		FilePaths.setMainFilePaths("D:\\cvwrsm\\trunk\\calsim30\\calsim30_bo\\CONV\\Run\\mainCONV_30.wresl");
		FilePaths.setSvarFilePaths("D:\\cvwrsm\\trunk\\calsim30\\calsim30_bo\\common\\DSS\\CalSim30_06_SV.dss");
        FilePaths.setInitFilePaths("D:\\cvwrsm\\trunk\\calsim30\\calsim30_bo\\common\\DSS\\CalSim30_06Init.dss");   
        FilePaths.setDvarFilePaths("D:\\cvwrsm\\trunk\\calsim30\\calsim30_bo\\CONV\\DSS\\TestWRIMSV2DV.dss");
		ControlData cd=new ControlData();
		cd.svDvPartF="CALSIM30_06";
		cd.initPartF="CALSIM30_06";
		cd.partA = "CALSIM";
		cd.partE = "1MON";
		cd.timeStep="1MON";
		cd.startYear=1921;
		cd.startMonth=10;
		cd.startDay=31;
		cd.endYear=2003;
		cd.endMonth=9;
		cd.endDay=30;
        cd.solverName="XA";
        FilePaths.csvFolderName="csv";
		cd.currYear=cd.startYear;
		cd.currMonth=cd.startMonth;
		cd.currDay=cd.startDay;
        
		StudyDataSet sds=parseCalsim3();
		
		new Controller();
	}
	
	public void testParsedCalsim3()throws RecognitionException, IOException{
		FilePaths.groundwaterDir="D:\\cvwrsm\\trunk\\calsim30\\calsim30_bo\\common\\CVGroundwater\\Data\\";
		FilePaths.setMainFilePaths("D:\\cvwrsm\\trunk\\calsim30\\calsim30_bo\\CONV\\Run\\mainCONV_30.wresl");
		FilePaths.setSvarFilePaths("D:\\cvwrsm\\trunk\\calsim30\\calsim30_bo\\common\\DSS\\CalSim30_06_SV.dss");
        FilePaths.setInitFilePaths("D:\\cvwrsm\\trunk\\calsim30\\calsim30_bo\\common\\DSS\\CalSim30_06Init.dss");   
        FilePaths.setDvarFilePaths("D:\\cvwrsm\\trunk\\calsim30\\calsim30_bo\\CONV\\DSS\\TestWRIMSV2DV.dss");
		ControlData cd=new ControlData();
		cd.svDvPartF="CALSIM30_06";
		cd.initPartF="CALSIM30_06";
		cd.partA = "CALSIM";
		cd.partE = "1MON";
		cd.timeStep="1MON";
		cd.startYear=1921;
		cd.startMonth=10;
		cd.startDay=31;
		cd.endYear=2003;
		cd.endMonth=9;
		cd.endDay=30;
        cd.solverName="XA";
        FilePaths.csvFolderName="csv";
		cd.currYear=cd.startYear;
		cd.currMonth=cd.startMonth;
		cd.currDay=cd.startDay;
        
		StudyDataSet sds=parseCalsim3();
		
		new Controller();
	}
	
	public StudyDataSet parseCalsim3() throws RecognitionException, IOException{
		
		String csvFolderPath = "TestWreslWalker_calsim3_full_study";
		String inputFilePath = "D:\\CALSIM30_041311_BO\\CONV\\Run\\mainCONV_30.wresl";
		String logFilePath = csvFolderPath+".log";
		
		File absFile = new File(inputFilePath).getAbsoluteFile();
		String absFilePath = absFile.getCanonicalPath().toLowerCase();
		
		
		/// temporary dataset, don't use this because the structure will be changed soon. 
		LogUtils.setLogFile(logFilePath);
		TempData td = new TempData();
		StudyConfig sc = StudyParser.processMainFileIntoStudyConfig(absFilePath);
		td.model_dataset_map=StudyParser.parseModels(sc,td);
		LogUtils.closeLogFile();
		
		
		/// write to StudyDataSet
		StudyDataSet sd = StudyParser.writeWreslData(sc, td); 
				
		/// write full study data to csv files
		WriteCSV.study(sd, csvFolderPath ) ;
	
		return sd;	
	}

	public void testParsedCalLite()throws RecognitionException, IOException{
		FilePaths.groundwaterDir="";
		FilePaths.setSvarFilePaths("D:\\CalLite_Beta_042611\\DSS\\CL_FUTURE_WHL042611_SV.dss");
        FilePaths.setInitFilePaths("D:\\CalLite_Beta_042611\\DSS\\CalLite2020D09EINIT.dss");
        FilePaths.setDvarFilePaths("D:\\CalLite_Beta_042611\\DSS\\TestWRIMSV2DV.dss");
        FilePaths.setMainFilePaths("D:\\CalLite_Beta_042611\\Run\\main_BO.wresl");
		ControlData cd=new ControlData();
        cd.svDvPartF="2020D09E";
        cd.initPartF="2020D09E";
		cd.partA = "CALSIM";
		cd.partE = "1MON";
		cd.timeStep="1MON";
        cd.startYear=1921;
        cd.startMonth=10;
        cd.startDay=31;
        cd.endYear=2003;
        cd.endMonth=9;
        cd.endDay=30;
        cd.simulationTimeFrame=TimeOperation.dssTimeFrame(cd.startYear, cd.startMonth, cd.startDay, cd.endYear, cd.endMonth, cd.endDay);
        cd.currYear=ControlData.startYear;
        cd.currMonth=ControlData.startMonth;
        cd.currDay=ControlData.startDay;
        cd.solverName="XA";
        FilePaths.csvFolderName="csv";
        
		StudyDataSet sds=parseCalLite();
		
		new Controller();
	}
	
	public StudyDataSet parseCalLite() throws RecognitionException, IOException{
		
		String csvFolderPath = "TestWreslWalker_callite_full_study";
		String inputFilePath = "D:\\CalLite_Beta_042611\\Run\\main_BO.wresl";
		String logFilePath = csvFolderPath+".log";
		
		File absFile = new File(inputFilePath).getAbsoluteFile();
		String absFilePath = absFile.getCanonicalPath().toLowerCase();
		
		
		/// temporary dataset, don't use this because the structure will be changed soon. 
		LogUtils.setLogFile(logFilePath);
		TempData td = new TempData();
		StudyConfig sc = StudyParser.processMainFileIntoStudyConfig(absFilePath);
		td.model_dataset_map=StudyParser.parseModels(sc,td);
		LogUtils.closeLogFile();
		
		
		/// write to StudyDataSet
		StudyDataSet sd = StudyParser.writeWreslData(sc, td); 
				
		/// write full study data to csv files
		WriteCSV.study(sd, csvFolderPath ) ;
	
		return sd;	
	}
}
