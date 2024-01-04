import DSStools
import numpy
import random
import os,subprocess

yearBegin = 1932   # year range for random year generation
yearEnd   = 1947   # year range for random year generation
futureYearBegin = 2011

inFile='dss\\OriginalSV.dss' # input DSS file
inPathDSS="A=CALSIM, B=*, E=1MON" # DSS path selection, * is wild card
outFile='dss\\ShiftedSV.dss' # output DSS file

someTableFile =open('test.table','w')


array = numpy.arange(yearBegin, yearEnd+1)
serialList = list(array)
randomList = list(array)
  
FpartList=[] # list of DSS F part for output DSS file



for firstYear in serialList:
	secondYear = randomList.pop(random.randrange(len(randomList))) # randomly generated second year
	print firstYear, secondYear
	someTableFile.writelines( str(firstYear)+' '+str(secondYear)+'\n')
	Fpart="historical_"+str(firstYear)+"_"+str(secondYear) # assemble DSS F part
	FpartList.append(Fpart) # collect F part
	outPathDSS="/${A}/${B}/${C}//${E}/"+Fpart+"/" 
	DSStools.copy_to_future(inFile,inPathDSS,firstYear,firstYear,outFile,outPathDSS,futureYearBegin) # copy historical data to future 
	DSStools.copy_to_future(inFile,inPathDSS,secondYear,secondYear,outFile,outPathDSS,futureYearBegin+1)


someTableFile.close()
print "----Finished copying historical data to future----"
x = raw_input("----Press Enter to run batch studies----")


studyTemplate = open("run\\study.template","r")
textTemplate = studyTemplate.read()

for i, Fpart in enumerate(FpartList):

	text=textTemplate.replace("{Fpart}",Fpart)  # replace DSS F part
	text=text.replace("{ShowWindow}","FALSE")   # Disable pop-up window

	studyFile = open("run\\study.sty","w")      # open and write to study.sty
	studyFile.write(text)
	studyFile.close()

	#subprocess.call(['runWRIMS.bat' ])          # call WRIMS compiled exe to run
	
	print " Finished Run "+str(i+1)+"/"+str(len(serialList))+ "  "+Fpart






