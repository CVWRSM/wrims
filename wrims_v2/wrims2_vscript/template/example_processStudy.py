#=====================================================================================
#  TimeStep must be monthly 
#=====================================================================================

from scripts.wrims2.study import Study
from scripts.tool import LogUtils, Param



# initialization
#################################
Param.mainScriptPath = __file__
LogUtils.initLogging()
#################################



s = Study("studies/callite_D1641Existing_PA__2012oct/PA_template.config")
s.setConfig("testStudy88", DvarFile="what.dss", StartYear=2012, StartMonth=10, SvarFPart='what', LookupSubDir='subr')
s.writeBatch()


