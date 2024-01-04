#=====================================================================================
# example: load a study 
#=====================================================================================
from os import path 
from scripts.wrims2.study import Study
from scripts.wrims2.runGroup import RunGroup
from scripts.tool import LogUtils, Param, LookupTable, FileUtils, DssVista
from scripts.wrims2.process.dssTransferProcess import DssTransferProcess
Param.mainScriptPath = path.abspath(__file__) 
Param.mainScriptDir  = path.dirname(Param.mainScriptPath)
LogUtils.initLogging()




studyRunDir = path.join(Param.mainScriptDir,'studies/callite_PA/Run')


s = Study('testStudy', studyRunDir) # 'testStudy' is the name of the study
s.importConfig('D1641_PA.config')    # a config file named 'testStudy' is created from the imported config
#s.writeBatch(pause=True)  # set pause=True to inspect error message. Default setting is false.


for yr in range(1937,1947):

	s.run(startYear=yr, startMonth=10, numberOfSteps=12)

