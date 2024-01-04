import os
from vutils import *
import scripts.tool.DssVista as DV


#-------------------------------------------------------------------------------
inFile="studies/callite_D1641Existing_PA__2012oct/Run/DSS/CL_EXISTING_BO_081011_SV.dss"
outFile = "z:/CL_EXISTING_BO_081011_PA_SV.dss"
futureWY = 2013
historyWYs = [x for x in range(1945, 1946)]
sequentialYRs = 3   # including first year

# UARM: Upper American River Model, that is storage in French Meadows, 
# Hell Hole, and Union Valley Reservoirs, which are upstream of Folsom.
# Tom Fitzhugh 

UARM_at_2012_09 = [300] 
#--------------------------------------------------------------------------------


for beginWY in historyWYs:

	endWY = beginWY + sequentialYRs - 1
	outFpart = str(beginWY)+"_"+str(endWY) # e.g., 1945_1947

	DV.copyDssToFuture_waterYear(inFile, outFile, beginWY, sequentialYRs, futureWY, outFpart)
	
	DV.array2dss(outFile, UARM_at_2012_09, "30SEP2012 2400", "/CALLITE/UARM/STORAGE//1MON/"+ outFpart +"/", "TAF")

	
print "Done!"

exit()
