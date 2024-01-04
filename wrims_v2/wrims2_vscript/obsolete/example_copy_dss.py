import hec.heclib.dss.HecDss as HecDss
import hec.heclib.dss.DSSPathname as DSSPathname



inFile =  HecDss.open("template\\data\\SV.dss")
outFile = HecDss.open("out.dss") 

timeWindow_begin ="02JAN1926 0012"
timeWindow_end   ="02JAN2012 0012"

inFile.setTimeWindow(timeWindow_begin, timeWindow_end)

data = inFile.read("/CALSIM/EVAP_S_FOLSM/EVAPORATION-RATE//1MON/2020D09E/")

newPath = DSSPathname(data.getPath())

newPath.setBPart("EVAP_S_FOLSM_new") 
newPath.setFPart("newData")   

data.setPathname(newPath.getPathname())

outFile.write(data) 



