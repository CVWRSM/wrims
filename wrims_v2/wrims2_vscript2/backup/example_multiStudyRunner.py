#=====================================================================================
# example: multi-study
#=====================================================================================
from os import path 
from scripts.wrims2.study import Study
from scripts.wrims2.runGroup import RunGroup
from scripts.tool import LogUtils, Param, LookupTable, FileUtils, DssVista
from scripts.wrims2.process.dssTransferProcess import DssTransferProcess
Param.mainScriptPath = path.abspath(__file__) 
Param.mainScriptDir  = path.dirname(Param.mainScriptPath)
LogUtils.initLogging()




studyRunDir = path.join(Param.mainScriptDir,'studies/callite_old/Run')
configFile = 'D1641.config'


s = Study('testStudy', studyRunDir) # 'testStudy' is the name of the study
s.importConfig(configFile)    # a config file named 'testStudy' is created from the imported config
#s.writeBatch(pause=True)  # set pause=True to inspect error message. Default setting is false.
#s.run(startYear=1921, startMonth=10, numberOfSteps=12)

source = path.join(studyRunDir,'DSS/D1641_DV.DSS')
destn  = path.join(studyRunDir,'DSS/what.DSS')
destn_Fpart = 'ouch'
dssTransferFile = path.join(studyRunDir,'../example.transfer')
p = DssTransferProcess(source, destn, destn_Fpart, dssTransferFile)
p.run(startYear=1921, startMonth=10, numberOfSteps=12)




