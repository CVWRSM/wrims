from hec.hecmath import *
from hec.heclib.dss import *

#  Open the file and get the data

dssFile = DSS.open("out.dss", "10MAR1926 2400, 09APR1926 2400")
outFile = HecDss.open("out.dss") 

outflow = dssFile.read("/CALSIM/EVAP_S_FOLSM/EVAPORATION-RATE//1MON/NEWDATA/")
#outflow = dssFile.read("/AMERICAN/FOLSOM/FLOW-RES OUT//1DAY/OBS/")
newOutflow = outflow.add(8888888888.)

path = DSSPathname(newOutflow.getPath())
fPart = path.fPart() + " + 888888888888 HecMath"
path.setFPart(fPart)
newOutflow.setPathname(path.getPathname())

dssFile.write( newOutflow)



