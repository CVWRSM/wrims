#     Name: BatchMain.py
#   Author: Hao Xie
#   E-mail: hxie@water.ca.gov
#    Phone: 916.653.1072
# Last Rev: 2024.11.06 - zachary.roy@water.ca.gov
#  Purpose: Batch Main

# python modules
import sys


#CalsimWsiDi class imports
from BatchStudyTab import *

# java class imports - standard
from java.awt import *
from java.awt.event import *
from java.io import *
from java.util import *
from javax.swing import *
from java.lang import *

# Main class


def main():
    # constructor: initialize class parameters
    try: # Dummy try block to get the indentation to work when WRIMS overwrites file.
        studyDvNames=[r"D:\CalLite_v47_x64\callite\DSS\testdv.dss"]
        lookupNames=[r"D:\CalLite_v47_x64\callite\Run\Lookup"]
        launchNames=[r"D:\CalLite_v47_x64\callite\CalLitev47_wsidi.launch"]
        offsets=[1.2]
    except:
        pass
    # WSI-DI curve labels and DSS pathnames
    crvName = ['SWP','CVP_SYS'] 
    crvWsiVar = ['WSI_ACTUAL_SWP','WSI_ACT_CVP_SYS'] 
    crvDiVar = ['DI_ACTUAL_SWP','DI_ACT_CVP_SYS']
        
    # maximum value(TAF) for WSI-DI curve; same value used for WSI and DO components
    crvMax = [20000,20000]
      
    s=StudyTabCl()
    s.runForWsi(studyDvNames,crvName,crvWsiVar,crvDiVar,crvMax,lookupNames,launchNames,offsets)
    sys.exit()

if __name__ == "__main__":
    main()

