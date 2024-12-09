package calsim.gui;

import calsim.app.Project;
import calsim.app.AppUtils;

public final class CalLiteGUIHelper {
	
	Project project = new Project();
	
	public CalLiteGUIHelper () 
	{
		AppUtils.setCurrentProject(project);
		
		project.setDVFile("D:\\workspace\\CalLite\\Scenarios\\DEFAULT_DV.dss");
		AppUtils.baseOn = true;
	}

}
