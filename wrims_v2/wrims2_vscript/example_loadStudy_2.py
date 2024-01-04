#=====================================================================================
# example: load a study 
#=====================================================================================
from os import path 
from scripts.wrims2.study import Study
from scripts.wrims2.runGroup import RunGroup
from scripts.tool import LogUtils, Param, LookupTable, FileUtils, DssVista
Param.mainScriptPath = path.abspath(__file__) 
Param.mainScriptDir  = path.dirname(Param.mainScriptPath)
LogUtils.initLogging()




studyRunDir = path.join(Param.mainScriptDir,'studies/callite_old/Run')

s = Study('testStudy2', studyRunDir) # 'testStudy' is the name of the study


wreslPlus='Yes'
mainFile='run\main.wresl'
initFile='Run\DSS\CL_INIT.dss'
initFPart='INITIAL'  
svarFile='Run\DSS\CL_EXISTING_BO_081011_SV.dss' 
svarAPart='CalLite'
svarFPart='2005A01A'
dvarFile='Run\DSS\D1641_DV_2.DSS'
startYear=1921
startMonth=10
numberOfSteps=12  # numberOfSteps overides stopYear and stopMonth
lookupSubDir='PA_Base_D1641_Existing'
#lookupSubDir=None #use default lookup directory

s.createConfig(WreslPlus=wreslPlus, 
               MainFile=mainFile, 
               InitFile=initFile, 
               InitFPart=initFPart, 
               SvarFile=svarFile, 
               SvarAPart=svarAPart, 
               SvarFPart=svarFPart, 
               DvarFile=dvarFile, 
               StartYear=startYear, 
               StartMonth=startMonth, 
               NumberOfSteps=numberOfSteps,
               StopYear=None,
               StopMonth=None,
               LookupSubDir=lookupSubDir,
               ShowWreslLog='Yes',
               PrefixInitToDvarFile='Yes',
               IlpLog='No',
               IlpLogFormat='CplexLp',
               IlpLogVarValue='no',
               IlpLogAllCycles='no'
               )
s.run(pause=True)

