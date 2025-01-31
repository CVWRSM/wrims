#     Name: SequentialMain.py
#   Author: Hao Xie
#   E-mail: hxie@water.ca.gov
#    Phone: 916.653.1072
# Last Rev: 2024.11.08 - zachary.roy@water.ca.gov
#  Purpose: Sequential Main

# python modules
import sys


#CalsimWsiDi class imports
from SequentialStudyTab import *

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
        studyDvNames=[r"D:\WaterYearIndex\DCRBL_CS3_20191228_forecastWYT_corrected_031120_offset_1.2\conv\DSS\DCRL_CS3.dss",r"D:\WaterYearIndex\DCRBL_CS3_20191228_forecastWYT_corrected_031120_offset_2.2\conv\DSS\DCRL_CS3.dss"]
        lookupNames=[r"D:\WaterYearIndex\DCRBL_CS3_20191228_forecastWYT_corrected_031120_offset_1.2\conv\Run\Lookup",r"D:\WaterYearIndex\DCRBL_CS3_20191228_forecastWYT_corrected_031120_offset_2.2\conv\Run\Lookup"]
        engineNames=[r"D:\WaterYearIndex\DCRBL_CS3_20191228_forecastWYT_corrected_031120_offset_1.2\DCRL_CS3_1223_WSIDI.launch.bat",r"D:\WaterYearIndex\DCRBL_CS3_20191228_forecastWYT_corrected_031120_offset_2.2\DCRL_CS3_1223_WSIDI.launch.bat"]
        launchNames=[r"D:\WaterYearIndex\DCRBL_CS3_20191228_forecastWYT_corrected_031120_offset_1.2\DCRL_CS3_1223_WSIDI.launch",r"D:\WaterYearIndex\DCRBL_CS3_20191228_forecastWYT_corrected_031120_offset_2.2\DCRL_CS3_1223_WSIDI.launch"]
    except Exception:
        pass
    # WSI-DI curve labels and DSS pathnames
    crvName = ['SWP','CVP_SYS']
    crvWsiVar = ['WSI_ACTUAL_SWP','WSI_ACT_CVP_SYS'] 
    crvDiVar = ['DI_ACTUAL_SWP','DI_ACT_CVP_SYS']

    # maximum value(TAF) for WSI-DI curve; same value used for WSI and DO components
    crvMax = [20000,20000]

    s=StudyTabCl()
    s.runForWsi(studyDvNames,crvName,crvWsiVar,crvDiVar,crvMax,lookupNames,engineNames,launchNames)
    sys.exit()

  
if __name__ == "__main__":
    main()

