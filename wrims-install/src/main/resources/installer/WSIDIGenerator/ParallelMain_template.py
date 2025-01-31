#     Name: ParallelMain.py
#   Author: Hao Xie
#   E-mail: hxie@water.ca.gov
#    Phone: 916.653.1072
# Last Rev: 2024.11.06 - zachary.roy@water.ca.gov
#  Purpose: Parallel Main

# python modules
import shutil
import sys

#CalsimWsiDi class imports
from ParallelStudyTab import *

# java class imports - standard
from java.awt import *
from java.awt.event import *
from java.io import *
from java.util import *
from javax.swing import *
from java.lang import *

# Main class

# constructor: initialize class parameters
def main():
    # constructor: initialize class parameters
    try: # Dummy try block to get the indentation to work when WRIMS overwrites file.
        studyDvNames=[r"Z:\wsidi\02.00_RoCPABaselineNewCOA_20181113[BOR]\CONV\DSS\NewCOA1113_02.00_20181126.DSS",r"Z:\wsidi\02.01_FRSAAlctnUpdte\CONV\DSS\NewCOA1113_02.01_20181126.DSS",r"Z:\wsidi\02.02_OrvleCryovrTrgt1MAF\CONV\DSS\NewCOA1113_02.02_20181126.DSS"]
        lookupNames=[r"Z:\wsidi\02.00_RoCPABaselineNewCOA_20181113[BOR]\CONV\Run\Lookup",r"Z:\wsidi\02.01_FRSAAlctnUpdte\CONV\Run\Lookup",r"Z:\wsidi\02.02_OrvleCryovrTrgt1MAF\CONV\Run\Lookup"]
        engineNames=[r"Z:\wsidi\02.00_RoCPABaselineNewCOA_20181113[BOR]\newcoa1113_02.00.launch.bat",r"Z:\wsidi\02.01_FRSAAlctnUpdte\newcoa1113_02.01.launch.bat",r"Z:\wsidi\02.02_OrvleCryovrTrgt1MAF\newcoa1113_02.02.launch.bat"]
        launchNames=[r"Z:\wsidi\02.00_RoCPABaselineNewCOA_20181113[BOR]\newcoa1113_02.00.launch",r"Z:\wsidi\02.01_FRSAAlctnUpdte\newcoa1113_02.01.launch",r"Z:\wsidi\02.02_OrvleCryovrTrgt1MAF\newcoa1113_02.02.launch"]
        offsets=[1.2,1.2,1.2]
    except Exception:
        pass
    # WSI-DI curve labels and DSS pathnames
    crvName = ['SWP','CVP_SYS'] 
    crvWsiVar = ['WSI_ACTUAL_SWP','WSI_ACT_CVP_SYS'] 
    crvDiVar = ['DI_ACTUAL_SWP','DI_ACT_CVP_SYS']

    # maximum value(TAF) for WSI-DI curve; same value used for WSI and DO components
    crvMax = [20000,20000]

    s=StudyTabCl()
    s.runForWsi(studyDvNames,crvName,crvWsiVar,crvDiVar,crvMax,lookupNames,engineNames,launchNames,offsets)
    sys.exit()

  
if __name__ == "__main__":
    main()

