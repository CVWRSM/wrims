#=====================================================================================
#  TimeStep must be monthly 
#  NumberOfSteps must be a multiple of 12
#=====================================================================================

import os
from scripts.wrims2.study import Study
from scripts.tool import LogUtils, Param
#import shutil


# initialize
#################################
Param.mainScriptPath = __file__
LogUtils.initLogging()
#################################

# default batch file to call is 'RunStudy.bat'
s1=Study(r"studies\callite_D1641\D1641.config")
s1.run(startYear=1921, numberOfSteps=13)
#s1.run()