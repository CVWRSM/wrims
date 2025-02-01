#     Name: Main.py
#   Author: Ben Tustison
#   E-mail: tustison@mbkengineers.com
#    Phone: 916.456.4400
# Last Rev: 06.25.2010
#  Purpose: Mimics StudyTab class from CALSIM

# python modules
import sys
import warnings

#CalsimWsiDi class imports
from StudyTab import *

# java class imports - standard
from java.awt import *
from java.awt.event import *
from java.io import *
from java.util import *
from javax.swing import *
from java.lang import *



# constructor: initialize class parameters
def main():
    try: # Dummy try block to get the indentation to work when WRIMS overwrites file.
        studyDvName=r"C:\Users\zroy\Documents\_Modeling\_WRIMS\_wsidi_from_csv\study\DSS\output\DCR2023_WSIDI_9.3.0_Danube_Adj_v1.8.csv"
        lookupName=r"C:\Users\zroy\Documents\_Modeling\_WRIMS\_wsidi_from_csv\study\Run\lookup"
        launchName=r"C:\Users\zroy\Documents\_Modeling\_WRIMS\_wsidi_from_csv\study\CS3_Adjusted_Dev_wsidi.launch"
        offset=1.2
    except Exception:
        pass

    # WSI-DI curve labels and DSS pathnames
    crvName = ['SWP','CVP_SYS'] 
    crvWsiVar = ['WSI_ACTUAL_SWP','WSI_ACT_CVP_SYS'] 
    crvDiVar = ['DI_ACTUAL_SWP','DI_ACT_CVP_SYS']

    # maximum value(TAF) for WSI-DI curve; same value used for WSI and DO components
    crvMax = [20000,20000]
    s=StudyTabCl()
    s.runForWsi(studyDvName,crvName,crvWsiVar,crvDiVar,crvMax, lookupName, launchName, offset)

def check_version():
    major, minor = sys.version_info[:2]
    if (major != 2) or (minor != 5):
        warnings.warn("Python 2.5 expected, runtme is using %d.%d" % (major, minor))

if __name__ == "__main__":
    check_version()
    main()
