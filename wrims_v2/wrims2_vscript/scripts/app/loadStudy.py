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



# specified through GUI
#studyRunDir
configFile = 'D1641.config'






s = Study('testStudy', studyRunDir)
s.loadConfig(configFile)    
#s.writeBatch(pause=True)
s.run()

