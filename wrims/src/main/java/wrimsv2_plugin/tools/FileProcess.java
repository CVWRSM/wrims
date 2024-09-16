package wrimsv2_plugin.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.eclipse.debug.core.ILaunchConfiguration;

import wrimsv2.commondata.wresldata.ModelDataSet;
import wrimsv2.commondata.wresldata.StudyDataSet;
import wrimsv2_plugin.batchrun.LaunchConfigInfo;

public class FileProcess {
	public static ArrayList<String> retrieveFileNames(ArrayList<String> fns){
		ArrayList<String> allFiles=new ArrayList<String>();
		for (int i=0; i<fns.size(); i++){
			String fn=fns.get(i);
			File file=new File(fn);
			String fnlc=fn.toLowerCase();
			if (!allFiles.contains(fnlc)) allFiles.add(fnlc);
			file = file.getParentFile();
			while (file != null){
				fnlc=file.getAbsolutePath().toLowerCase();
				if (!allFiles.contains(fnlc)) allFiles.add(fnlc);
				file=file.getParentFile();
			}
		}
		return allFiles;
	}
	
	public static boolean isWreslFile(String path){
		if (path.toLowerCase().endsWith(".wresl")){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isTableFile(String path){
		if (path.toLowerCase().endsWith(".table")){
			return true;
		}else{
			return false;
		}
	}
	
	public static ArrayList<String> getStudyWreslFiles(String mainFilePath, StudyDataSet sds){
		ArrayList<String> fns=new ArrayList<String>();
		fns.add(mainFilePath.toLowerCase());
		Map<String, ModelDataSet> mdsm = sds.getModelDataSetMap();
		Set<String> mns=mdsm.keySet();
		for (String mn: mns){
			ModelDataSet mds=mdsm.get(mn);
			ArrayList<String> ifl = mds.incFileList;
			for (int i=0; i<ifl.size(); i++){
				String fnl = ifl.get(i).toLowerCase();
				if (!fns.contains(fnl)){
					fns.add(fnl);
				}
			}
		}
		return fns;
	}
	
	public static ArrayList<String> getCycleWreslFiles(String mainFilePath, StudyDataSet sds, int index){
		ArrayList<String> fns=new ArrayList<String>();
		fns.add(mainFilePath.toLowerCase());
		Map<String, ModelDataSet> mdsm = sds.getModelDataSetMap();
		String mn=sds.getModelList().get(index);
		ModelDataSet mds=mdsm.get(mn);
		ArrayList<String> ifl = mds.incFileList;
		for (int i=0; i<ifl.size(); i++){
			String fnl = ifl.get(i).toLowerCase();
			if (!fns.contains(fnl)){
				fns.add(fnl);
			}
		}
		return fns;
	}
	
	public static ArrayList<String> getTableFiles(String mainFilePath){
		ArrayList<String> tns=new ArrayList<String>();
		File file=new File(mainFilePath);
		String tfn=file.getParentFile().toString()+"\\lookup";
		File tDir=new File(tfn);
		if (tDir!=null){
			for (File table : tDir.listFiles()) {
				String tn=table.getAbsolutePath().toLowerCase();
				if (tn.endsWith(".table")){
					tns.add(tn);
				}
			}
		}
		return tns;
	}
	
	public static String procRelativePath(String path, ILaunchConfiguration config){
		String absPath=config.getFile().getLocation().toFile().getParentFile().getAbsolutePath();
		absPath=absPath+"\\"+path;
		return absPath;
	}
	
	public static String procRelativePath(String path, String launchFilePath){
		String absPath=new File(launchFilePath).getParentFile().getAbsolutePath();
		absPath=absPath+"\\"+path;
		return absPath;
	}
	
	public static String createSensitivityFilePath(String path, int sri){
		int index=path.lastIndexOf(File.separator)+1;
		String directory = path.substring(0,index);
		String fn = "SR"+sri+"_"+path.substring(index);
		return directory+fn;
	}
}
