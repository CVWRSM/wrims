import DSStools
import numpy
import random
import os,subprocess

def Process_DSS(yearHistBegin, yearHistEnd, yearFutureBegin, inFile, inPath, outFile, outTableFile):

	array = numpy.arange(yearHistBegin, yearHistEnd+1)
	serialList = list(array)
	randomList = list(array)

	Table =open(outTableFile,'w')
	Table.writelines('firstYear  secondYear   DSSFpart \n')

	for firstYear in serialList:
		secondYear = randomList.pop(random.randrange(len(randomList))) # randomly generated second year
		print firstYear, secondYear

		Fpart=str(firstYear)+"_"+str(secondYear) # assemble DSS F part
		
		Table.writelines( str(firstYear)+'       '+str(secondYear)+'       '+Fpart+'\n')
		
		outPath="/${A}/${B}/${C}//${E}/"+Fpart+"/" 
		DSStools.copy_to_future(inFile,inPath,firstYear,firstYear,outFile,outPath,yearFutureBegin) # copy historical data to future 
		DSStools.copy_to_future(inFile,inPath,secondYear,secondYear,outFile,outPath,yearFutureBegin+1)
	
	Table.close()

def Parse_DSS_F_Part(lookupTableFile):
	
	outList = []
	
	table=open(lookupTableFile,'r')
	lines = table.readlines()[1:]  # read from the second line
	for line in lines:
		line = line.strip().split()
		print line
		outList.append(line[2]) # append the 3rd item
	
	return outList
	
def Run_WRIMS(runStartYear, runSteps, inputTemplateFile, dssFpartList):
	
	studyTemplateFile = open(inputTemplateFile,"r")
	inputTemplate = studyTemplateFile.read()
	
	for i, Fpart in enumerate(dssFpartList):
	
		text=inputTemplate.replace("{Fpart}",Fpart)  # replace DSS F part
		text=text.replace("{ShowWindow}","FALSE")   # Disable pop-up window
		text=text.replace("{start_year}",str(runStartYear))
		text=text.replace("{total_number_of_time_steps}",str(runSteps))		
	
		studyFile = open("run\\study.sty","w")      # open and write to study.sty
		studyFile.write(text)
		studyFile.close()
	
		subprocess.call(['runWRIMS.bat' ])          # call WRIMS compiled exe to run
		
		print " Finished Run "+str(i+1)+"/"+str(len(dssFpartList))+ "  "+Fpart		
		
yearBegin = 1932   # year range for random year generation
yearEnd   = 1934   # year range for random year generation
futureYearBegin = 2011

inDSS='dss\\OriginalSV.dss'       # input DSS file
inPathDSS="A=CALSIM, B=*, E=1MON" # DSS path selection, * is wild card
outDSS='dss\\ShiftedSV.dss'       # shifted DSS file for state variables
LookupTableFile = 'randomYearsProcessed.txt'


# call preprocessing
Process_DSS(yearBegin, yearEnd, futureYearBegin, inDSS, inPathDSS, outDSS, LookupTableFile)

# parse lookupTable and return the F part
DSS_FpartList = Parse_DSS_F_Part(LookupTableFile)

# call run batch
startYear = 2011
totalTimeSteps = 12  # 12 months
Run_WRIMS(startYear, totalTimeSteps, 'run\\study.seperate_functions_template', DSS_FpartList)



