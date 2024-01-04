## copy 2010 December Init data to future 2011, 2012, ....


import DSStools


inFile='dss\\INIT.dss' #input DSS file

inPathDSS="A=*,E=1MON" # path selection, * is wildcard
 
outFile='dss\\copy_INIT_example.dss' #output DSS file


InitYear = 2010    
futureYearBegin = 2011
futureYearEnd = 2065


for futureYear in range (futureYearBegin, futureYearEnd + 1):

	outPathDSS="/${A}/${B}/${C}//${E}/${F}/"
	DSStools.copy_to_future(inFile,inPathDSS,InitYear,InitYear,outFile,outPathDSS,futureYear)





