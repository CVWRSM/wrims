/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Bjorn Freeman-Benson - initial API and implementation
 *******************************************************************************/
package wrimsv2_plugin.debugger.core;

import org.eclipse.core.internal.resources.PreferenceInitializer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.menuitem.EnableMenus;
import wrimsv2_plugin.debugger.menuitem.NextTimeStepMenu;
import wrimsv2_plugin.debugger.model.WPPDebugTarget;
import wrimsv2_plugin.debugger.model.WPPValue;
import wrimsv2_plugin.debugger.toolbaritem.DebugSet;
import wrimsv2_plugin.debugger.view.WPPVariableView;
import wrimsv2_plugin.tools.VariableProperty;
import hec.heclib.dss.HecDss;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class DebugCorePlugin extends AbstractUIPlugin {
	//The shared instance.
	private static DebugCorePlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	/**
	 * Unique identifier for the WPP debug model (value 
	 * <code>wpp.debugModel</code>).
	 */
	public static String preferenceFile = "WRIMS2_IDEGUI.epf";
	
	public static WPPDebugTarget target;
	
	public static int launchType = 0;
	
	@SuppressWarnings("unchecked")
	public static Map<String, String>[] variableAltValue=new HashMap[8];
	public static IValue[] variableStack=new WPPValue[0];	
	public static IValue[] goalStack=new WPPValue[0];
	public static IValue[] allVariableStack=new WPPValue[0];
	public static IValue[] allGoalStack=new WPPValue[0];
	public static IValue[] watchStack=new WPPValue[0];
	public static String allVarProperties="";
	public static String weightedVariables="";
	public static ArrayList<String> fileControlGoals;
	public static ArrayList<String> allControlGoals;
	public static ArrayList<String> watchControlGoals;
	public static ArrayList<String> watchItems=new ArrayList<String>();
	public static Map<String, VariableProperty> variableProperty=new HashMap<String, VariableProperty>();
	public static Map<String, VariableProperty> allVariableProperty = new HashMap<String, VariableProperty>();
	public static Map<String, VariableProperty> watchProperty=new HashMap<String, VariableProperty>();
	public static Map<Integer, Integer> variableAltColIndex=new HashMap<Integer, Integer>();;
	public static Map<Integer, Integer> watchAltColIndex=new HashMap<Integer, Integer>();;
	public static String dataDir="data";
	public final static String watchFile = "watch.prf"; 
	
	public static ArrayList<String[]> varDetailTimeseries= new ArrayList<String[]>();
	public static ArrayList<String[]> varDetailFuture= new ArrayList<String[]>();
	public static ArrayList<String[]> varDetailCycle= new ArrayList<String[]>();
	public static int varDetailChoice=0;
	public static ArrayList<String> selectedVariableNames=new ArrayList<String>();
		
	public static int totalNoOfCycle=1;
	public static String timeStep="1DAY";
	public static int startYear=1921;
	public static int startMonth=10;
	public static int startDay=31;
	public static int endYear=2009;
	public static int endMonth=9;
	public static int endDay=30;
	public static int debugYear=2009;
	public static int debugMonth=9;
	public static int debugDay=30;
	public static int debugCycle=99;
	public static int suspendedYear=2009;
	public static int suspendedMonth=9;
	public static int suspendedDay=30;
	public static int suspendedCycle=99;
	public static int paInitialDssInterval;
	public static boolean isCreateSeriesPAInit=true;

	public static DebugSet debugSet;
	
	public static boolean isDebugging=false;
	public static boolean updateSelectedVariable=true;
	
	public static String solver="CBC";
	public static String log="None";
	
	public static String savedSvFileName="";
	public static String savedDvFileName="";
	public static String savedInitFileName="";
	public static String controlGoalsFileName="";
	public static String filterFileName="";
	
	public static String[] studyDvFileNames={"","","",""};
	public static String[] studySvFileNames={"","","",""};
	public static String[] studyFolderNames={"","","",""};
	public static boolean[] selectedStudies={false, false, false, false, false, false, false, false};
	public static HecDss[] dvDss=new HecDss[4];
	public static HecDss[] svDss=new HecDss[4];
	public static Vector[] dvVector=new Vector[4]; 
	public static Vector[] svVector=new Vector[4];
	public static int[] studyTypes={0,0,0,0};
	@SuppressWarnings("unchecked")
	public static Map<String, String>[] studiesData=new HashMap[4];
	public static String timeWindow="31Oct1921 2400 30Sep3000 2400";
	
	public static String aPart="";
	public static String initFPart="";
	public static String svFPart="";
	
	public static DecimalFormat df = new DecimalFormat("#.####");
	
	public static final String ID_WPP_PLUGIN="wrims-ide";
	public static final String ID_WPP_DEBUG_MODEL = "wpp.debugModel";
	public static final String ID_WPP_VARIABLE_VIEW="wpp.variableview";
	public static final String ID_WPP_ALLVARIABLE_VIEW="wpp.allvariableview";
	public static final String ID_WPP_WATCH_VIEW="wpp.watchview";
	public static final String ID_WPP_VARIABLEDETAIL_VIEW="wpp.vardetailview";
	public static final String ID_WPP_VARIABLEMONITOR_VIEW="wpp.varmonitorview";
	public static final String ID_WPP_GOAL_VIEW="wpp.goalview";
	public static final String ID_WPP_ALLGOAL_VIEW="wpp.allgoalview";
	public static final String ID_WPP_FILEINCEXPLORE_VIEW = "wpp.fileincexploreview";
	public static final String ID_WPP_CALSIMHYDRO_VIEW = "wpp.calsimhydroview";
	public static final String ID_WPP_EXCEPTION_VIEW = "wpp.exceptionview";
	public static final String ID_WPP_INFEASIBILITY_VIEW = "wpp.infeasibilityview";
	public static final String ID_WPP_EDITOR="wpp.editor";
	public static final String ID_WPP_RESUMEMENU="wpp.resume";
	public static final String ID_WPP_TERMINATEMENU="wpp.terminate";
	public static final String ID_WPP_PAUSEMENU="wpp.pause";
	public static final String ID_WPP_SUSPENDMENU="wpp.suspend";
	public static final String ID_WPP_NEXTTIMESTEP="wpp.nexttimestep";
	public static final String ID_WPP_NEXTCYCLE="wpp.nextcycle";
	public static final String ID_WPP_RESIMMENU="wpp.resim";
	public static final String ID_WPP_CONDITIONALBREAKPOINT="wpp.conditionalbreakpoint";
	public static final String ID_WPP_CLEARCONDITIONALBREAKPOINT="wpp.clearconditionalbreakpoint";
	public static final String ID_WPP_SAVETODVFILE="wpp.savedvfile";
	public static final String ID_WPP_SAVETOSVFILE="wpp.savesvfile";
	public static final String ID_WPP_SOLVEROPTIONMENU="wpp.solveroption";
	
	public static final String ID_WPP_PAUSERESUMEBUTTON="wpp.pauseresumebutton";
	public static final String ID_WPP_NEXTCYCLEBUTTON="wpp.nextcyclebutton";
	public static final String ID_WPP_NEXTTIMESTEPBUTTON="wpp.nexttimestepbutton";
	/**
	 * Identifier for the WPP launch configuration type
	 * (value <code>wpp.launchType</code>)
	 */
	public static final String ID_WPP_LAUNCH_CONFIGURATION_TYPE = "wpp.launchType";	
	
	public static final String TITLE_ALLVARIABLES_VIEW="All Variables";
	public static final String TITLE_VARIABLES_VIEW="Variables";
	public static final String TITLE_ALLGOALS_VIEW="All Goals";
	public static final String TITLE_GOALS_VIEW="Goals";
	public static final String TITLE_WATCH_VIEW="Watch";
	
	/**
	 * Launch configuration attribute key. Value is a path to a perl
	 * program. The path is a string representing a full path
	 * to a perl program in the workspace. 
	 */
	public static final String ATTR_WPP_STUDY=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_STUDY";
	public static final String ATTR_WPP_AUTHOR=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_AUTHOR";
	public static final String ATTR_WPP_DATE=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_DATE";
	public static final String ATTR_WPP_DESCRIPTION=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_DESCRIPTION";
	public static final String ATTR_WPP_PROGRAM = ID_WPP_DEBUG_MODEL + ".ATTR_WPP_PROGRAM";
	public static final String ATTR_WPP_DVARFILE = ID_WPP_DEBUG_MODEL + ".ATTR_WPP_DVAR";
	public static final String ATTR_WPP_SVARFILE = ID_WPP_DEBUG_MODEL + ".ATTR_WPP_SVAR";
	public static final String ATTR_WPP_INITFILE = ID_WPP_DEBUG_MODEL + ".ATTR_WPP_INIT";
	public static final String ATTR_WPP_GWDATAFOLDER=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_GWDATA";
	public static final String ATTR_WPP_APART=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_APART";
	public static final String ATTR_WPP_SVFPART=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_SVFPART";
	public static final String ATTR_WPP_INITFPART=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_INITFPART";
	public static final String ATTR_WPP_TIMESTEP=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_TIMESTEP";
	public static final String ATTR_WPP_STARTYEAR=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_STARTYEAR";
	public static final String ATTR_WPP_STARTMONTH=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_STARTMONTH";
	public static final String ATTR_WPP_STARTDAY=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_STARTDAY";
	public static final String ATTR_WPP_ENDYEAR=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_ENDYEAR";
	public static final String ATTR_WPP_ENDMONTH=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_ENDMONTH";
	public static final String ATTR_WPP_ENDDAY=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_ENDDAY";
	public static final String ATTR_WPP_WRESLPLUS=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_WRESLPLUS";
	public static final String ATTR_WPP_FREEXA=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_FREEXA";
	public static final String ATTR_WPP_ALLOWSVTSINIT=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_ALLOWSVTSINIT";
	public static final String ATTR_WPP_LAUNCHTYPE=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_LAUNCHTYPE";
	public static final String ATTR_WPP_MULTISTUDY=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_MULTISTUDY";
	public static final String ATTR_WPP_ISFIXDURATION=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_ISFIXDURATION";
	public static final String ATTR_WPP_FIXEDDURATION=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_MSDURATION";
	public static final String ATTR_WPP_VARIABLEDURATION=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_VARIABLEDURATION";
	public static final String ATTR_WPP_DATATRANSFER=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_DATATRANSFER";
	public static final String ATTR_WPP_PASTARTINTERVAL=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_PASTARTINTERVAL";
	public static final String ATTR_WPP_PADURATION=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_PADURATION";
	public static final String ATTR_WPP_PADELINIT=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_PADELINIT";
	public static final String ATTR_WPP_PARESETDVSTART=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_PARESETDVSTART";
	public static final String ATTR_WPP_PADVSTARTYEAR=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_PADVSTARTYEAR";
	public static final String ATTR_WPP_PADVSTARTMONTH=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_PADVSTARTMONTH";
	public static final String ATTR_WPP_PADVSTARTDAY=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_PADVSTARTDAY";
	public static final String ATTR_WPP_DATABASEURL=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_DATABASEURL";
	public static final String ATTR_WPP_SQLGROUP=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_SQLGROUP";
	public static final String ATTR_WPP_OVOPTION=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_OVOPTION";
	public static final String ATTR_WPP_OVFILE=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_OVFILE";
	public static final String ATTR_WPP_ISSENSITIVITYRUN=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_ISSENSITIVITYRUN";
	public static final String ATTR_WPP_SENSITIVITYINDEXTABLENAME=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_SENSITIVITYINDEXTABLENAME";
	public static final String ATTR_WPP_NUMSENSITIVITYRUN=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_NUMSENSITIVITYRUN";
	public static final String ATTR_WPP_CALSIMHYDRORUN=ID_WPP_DEBUG_MODEL + ".CALSIMHYDRORUN";
	public static final String ATTR_WPP_CALSIMHYDROEXE=ID_WPP_DEBUG_MODEL + ".CALSIMHYDROEXE";
	public static final String ATTR_WPP_ALLRESTARTFILES=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_ALLRESTARTFILES";
	public static final String ATTR_WPP_NUMBERRESTARTFILES=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_NUMBERRESTARTFILES";
	public static final String ATTR_WPP_WSIDIOFFSET=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_WSIDIOFFSET";
	public static final String ATTR_WPP_OUTPUTWSIDIDVONLY=ID_WPP_DEBUG_MODEL + "ATTR_WPP_OUTPUTWSIDIDVONLY";
	public static final String ATTR_WPP_IFSISSELENTRY=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_IFS_IS_SELECT_ENTRY";
	public static final String ATTR_WPP_IFSNUMBERSELENTRIES=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_IFS_NUMBER_SELECT_ENTRIES";
	public static final String ATTR_WPP_IFSSELENTRYNAME=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_IFS_SELECT_ENTRY_NAME";
	public static final String ATTR_WPP_COMPILEONLY=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_COMPILEONLY";
	public static final String ATTR_WPP_DSSENDOUTPUT=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_DSSENDOUTPUT";
	public static final String ATTR_WPP_YEARSECTIONOUTPUT=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_YEARSECTIONOUTPUT";
	public static final String ATTR_WPP_MONMEMSECTION=ID_WPP_DEBUG_MODEL + ".ATTR_WPP_MONMEMSECTION";	
	public static final String ATTR_WPP_UNCHANGEGWRESTART=ID_WPP_DEBUG_MODEL + "ATTR_WPP_UNCHANGEGWRESTART";
	public static final String ATTR_WPP_UNCHANGEINITIALDSS=ID_WPP_DEBUG_MODEL + "ATTR_WPP_UNCHANGEINITIALDSS";
	public static final String ATTR_WPP_VHECLIB=ID_WPP_DEBUG_MODEL + "ATTR_WPP_VHECLIB";

	public static final String lineSep=System.getProperty("line.separator");
	public static final String tab="\t";
	
	public static String textVarGoalSearch="";
	
	public static String hoverText="";
	
	public static String conditionalBreakpoint="";
	
	public static ArrayList<String> fileFolderWreslInc=new ArrayList<String>();
	public static String cycleWreslMainFilePath = "";
	
	public static String paInitFile ="";
	public static int paStartYear=1921;
	public static int paStartMonth=10;
	public static int paStartDay=1;
	public static int paEndYear=2009;
	public static int paEndMonth=9;
	public static int paEndDay=30;
	public static int paDuration=12;
	public static int paStartInterval=12;
	public static int paDVStartYear=2009;
	public static int paDVStartMonth=10;
	public static int paDVStartDay=31;
	public static boolean deletePAInit=true;
	public static boolean resetOutputStart=false;
	
	public static int msStartYear=1921;
	public static int msStartMonth=10;
	public static int msStartDay=1;
	public static int msEndYear=2009;
	public static int msEndMonth=9;
	public static int msEndDay=30;
	public static int[] msDuration={12};
	public static int msDurationIndex=0;
	
	public static boolean isDssInOp=false;
	
	public static String repTemplateFile="";
	public static String repBaseFile="";
	public static String repBaseAlias="Base";
	public static String repAltFile="";
	public static String repAltAlias="Alt";
	public static String repNote="";
	public static String repAssumption="";
	public static String repModeler="";
	public static String repFontSize="8";
	public static String reportFileName="";
	
	public static String xmx="4096";
	public static boolean outputCycleToDss=false;
	public static boolean outputAllCycles=true;
	public static String outputCycles="\'\'";
	public static boolean showRunTimeMessage=false;
	public static boolean printGWFuncCalls=false;
	public static boolean trackMemoryUsage=false;
	
	public static String devPass = "ModelingisNo1";
	public static boolean cbcUsed=true;
	public static boolean cbc210Used=false;
	public static boolean cbc298Used=false;
	
	public static boolean isIfsSelFile=true;
	public static String ifsFilePath;
	public static String prevIfsFilterPath="";
	
	public static boolean isRunning=false;
	public static ILaunchConfiguration launchConfig;
	
	public static String[] wrimsGUIPerspectives=new String[]{"wpp.ideperspective", "wpp.dssperspective", "gov.ca.dwr.jdiagram.perspective", "gov.ca.dwr.jdiagram.schematiceditorperspective", "org.eclipse.datatools.sqltools.sqleditor.perspectives.EditorPerspective"};

	public static boolean isWsiDiRun=false;
	
	/**
	 * The constructor.
	 */
	public DebugCorePlugin() {
		super();
		plugin = this; 
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		//new DebuggerPreferenceInitializer().initializeDefaultPreferences();
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static DebugCorePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = DebugCorePlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("example.debug.core.DebugCorePluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}
	
	/**
	 * Return a <code>java.io.File</code> object that corresponds to the specified
	 * <code>IPath</code> in the plugin directory, or <code>null</code> if none.
	 */
	public static File getFileInPlugin(IPath path) {
		try {
			URL installURL =
				new URL(getDefault().getDescriptor().getInstallURL(), path.toString());
			URL localURL = Platform.asLocalURL(installURL);
			return new File(localURL.getFile());
		} catch (IOException ioe) {
			return null;
		}
	}	
	
	public static ImageDescriptor getImageDescriptor(String name) {
		/*
		String iconPath = "icons/";
		try {
		   URL installURL = getDefault().getDescriptor().getInstallURL();
		   URL url = new URL(installURL, iconPath + name);
		   return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
		   WPPException.handleException(e);
		   return ImageDescriptor.getMissingImageDescriptor();
		}
		*/

		return imageDescriptorFromPlugin(ID_WPP_PLUGIN, "icons\\"+name);
	}
}
