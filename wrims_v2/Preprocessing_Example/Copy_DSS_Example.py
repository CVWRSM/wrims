import DSStools


inFile='dss\\OriginalSV.dss' #input DSS file

inPathDSS="A=CALSIM, B=EVAP*, E=1MON" # path selection, * is wildcard
 
outFile='dss\\copyTest.dss' #output DSS file


beginDate = "1/2/1934"
endDate   = "1/2/1947"
time_window=(beginDate, endDate)

outPathDSS="/${A}/${B}/${C}//${E}/copy_test/" 

DSStools.copy(inFile,inPathDSS,time_window,outFile,outPathDSS)




