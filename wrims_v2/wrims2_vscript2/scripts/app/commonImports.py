#=====================================================================================
# common imports 
#=====================================================================================
from os import path 
from scripts.wrims2.study import Study
from scripts.wrims2.runGroup import RunGroup
from scripts.tool import LogUtils, Param, LookupTable, FileUtils, DssVista

Param.mainScriptPath = path.abspath(__file__) 
Param.mainScriptDir  = path.dirname(Param.mainScriptPath)
LogUtils.initLogging()


