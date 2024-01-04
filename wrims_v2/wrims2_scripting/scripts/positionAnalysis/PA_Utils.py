import hec.heclib.dss.HecDss as HecDss
import Tools 
import Param
import LogUtils
import os
import posixpath as ppath


#=======================================================

_runFolder = None
_dssFolder = None
_lookupFolder = None

_beginFutureYR = None
_beginPastYR = None
_endPastYR = None
_sequentialYRs = None
_dssBufferYRs = None
_tableBufferYRs = None

_originalSvDssFile = None
_shiftedSvDssFile = None

_styTemplateFile = None
_yearlyTableTemplateList = None
_monthlyTableTemplateList = None
_monthlyTableMonthList = None
_exeName = None

_initialDssFile = None
_resultDssFile = None
_showWindow = None

#=======================================================




def setConfig(runFolder, lookupFolder, originalSvDssFile, shiftedSvDssFile,
              styTemplateFile, exeName,
              yearlyTableTemplateList, monthlyTableTemplateList, monthlyTableMonthList,
              initialDssFile, resultDssFile, showWindow):
    

    global _runFolder
    global _dssFolder
    global _lookupFolder
    global _originalSvDssFile 
    global _shiftedSvDssFile 
    global _styTemplateFile 
    global _yearlyTableTemplateList
    global _monthlyTableTemplateList
    global _exeName
    global _monthlyTableMonthList
    global _initialDssFile
    global _resultDssFile
    global _showWindow
    
    _runFolder = runFolder
    _lookupFolder = lookupFolder
    _originalSvDssFile = os.path.normpath(originalSvDssFile)
    _shiftedSvDssFile = os.path.normpath(shiftedSvDssFile)
    _styTemplateFile = styTemplateFile
    _yearlyTableTemplateList = yearlyTableTemplateList
    _monthlyTableTemplateList = monthlyTableTemplateList
    _exeName = exeName
    _monthlyTableMonthList = monthlyTableMonthList
    _initialDssFile = os.path.normpath(initialDssFile)
    _resultDssFile = os.path.normpath(resultDssFile)
    _showWindow = showWindow
    ## Initialize logging
    Param.logger = LogUtils.initLogging(os.path.join(os.path.dirname(_runFolder), Param.logFile))
   
    

def setTime(beginYR, endYR, sequentialYRs, futureYR, dssBufferYRs, tableBufferYRs):
    
    global _beginFutureYR
    global _beginPastYR
    global _endPastYR
    global _sequentialYRs
    global _dssBufferYRs
    global _tableBufferYRs    

         
    _beginFutureYR = futureYR  
    _beginPastYR = beginYR     
    _endPastYR = endYR         
    _sequentialYRs = sequentialYRs
    _dssBufferYRs = dssBufferYRs      
    _tableBufferYRs = tableBufferYRs

## Copy historical data ( 5 years sequentially) to future
def cleanDss():
    
    outDssPath  = os.path.normpath(_shiftedSvDssFile)  
    
    try:
        os.remove(outDssPath)
        Param.logger.warning("DSS deleted: "+outDssPath)
    except:    
        pass                               

## Copy historical data ( 5 years sequentially) to future
def processDss():
    
    inDssPath   = ppath.normpath(_originalSvDssFile)
    outDssPath  = ppath.normpath(_shiftedSvDssFile)
    
    Tools.cleanDssCatalog(inDssPath)
    
    inFile =  HecDss.open(inDssPath)
    outFile = HecDss.open(outDssPath) 
    
    for variable_YR in range( _beginPastYR, _endPastYR + 1 ):
        
           
        Tools.copyDssToFuture(inFile, outFile, variable_YR, _sequentialYRs, _dssBufferYRs, _beginFutureYR )
    
    inFile.close()
    outFile.close()

    Param.logger.info("Finished processDss") 

## Copy historical water-pastYR types to future
def processYearlyTables():
        
    for item in _yearlyTableTemplateList:
        templatePath = os.path.join(_lookupFolder, item)
        
        processYearlyTable(templatePath)
        
    Param.logger.info("Finished processYearlyTables") 
    
## Copy historical water-pastYR types to future
def processYearlyTable(yearlyTableTemplatePath):
    
    #tableTemplatePath= os.path.normpath(tableTemplatePath)
    #tableTemplatePath = tableTemplatePath.replace("\\","/")
    print yearlyTableTemplatePath

    inTable = open(yearlyTableTemplatePath,'r') # read only 
    outTablePath_prepend = os.path.splitext(yearlyTableTemplatePath)[0]+'_'
    
    for pastYR in range( _beginPastYR, _endPastYR + 1 ):
        
        Fpart = str(pastYR) + '_' + str(pastYR + _sequentialYRs - 1)
    
        outTablePath = outTablePath_prepend + Fpart + '.table_PA'    
        outTable  = open(outTablePath,'w')     
        inTable.seek(0,0) # rewind to beginning of file
            
        Tools.copyYearlyWYTypesToFuture(inTable, outTable, pastYR, _sequentialYRs, _beginFutureYR, _tableBufferYRs)
        
        outTable.close()
        
    inTable.close()

## Copy historical water-pastYR types to future
def processMonthlyTables():
        
    for item, monthList in zip(_monthlyTableTemplateList, _monthlyTableMonthList):
        templatePath = os.path.join(_lookupFolder, item)
        
        processMonthlyTable(templatePath, monthList)
        
    Param.logger.info("Finished processMonthlyTables") 

## Copy historical water-pastYR types to future
def processMonthlyTable(monthlyTableTemplatePath, monthList):
    
    #tableTemplatePath= os.path.normpath(tableTemplatePath)
    #tableTemplatePath = tableTemplatePath.replace("\\","/")
    print monthlyTableTemplatePath

    inTable = open(monthlyTableTemplatePath,'r') # read only 
    outTablePath_prepend = os.path.splitext(monthlyTableTemplatePath)[0]+'_'
    
    for pastYR in range( _beginPastYR, _endPastYR + 1 ):
        
        Fpart = str(pastYR) + '_' + str(pastYR + _sequentialYRs - 1)
    
        outTablePath = outTablePath_prepend + Fpart + '.table_PA'    
        outTable  = open(outTablePath,'w')     
        inTable.seek(0,0) # rewind to beginning of file
            
        Tools.copyMonthlyWYTypesToFuture(inTable, outTable, pastYR, _sequentialYRs, _beginFutureYR, _tableBufferYRs, monthList)
        
        outTable.close()
        
    inTable.close()

def cleanTables():
        
    Tools.deleteFile(_lookupFolder, "table_PA")

       


def cleanSty():
        
    Tools.deleteFile(_runFolder, "sty_PA")
    

## Generate sty file
def processSty():
    
    styTemplatePath = os.path.join(_runFolder, _styTemplateFile)
    
    for pastYR in range( _beginPastYR, _endPastYR + 1):
        
        Fpart = str(pastYR) + '_' + str(pastYR + _sequentialYRs - 1)
        
        outStudyPath_prepend = styTemplatePath.replace('.template','_')
        outStudyFilePath = outStudyPath_prepend + Fpart + '.sty_PA' 
        Tools.generateStudyFile(styTemplatePath, outStudyFilePath, Fpart, _showWindow, 
                       _runFolder, _shiftedSvDssFile, _initialDssFile, _resultDssFile )

    Param.logger.info("Finished processSty")  

## Generate run batch
def processRunBatch(wrims_version):
    
    runTemplatePath = os.path.join(Param.templateDir, Param.runTemplateFile[wrims_version])
    
    outFileName = Param.runBatchPrepend + os.path.splitext(_exeName)[0] + '.bat'
    # outFilePath = os.path.join( os.path.dirname(_runFolder), outFileName)
    outFilePath = outFileName
    
    outFile = open(outFilePath,'w')
    
    runTemplate = open(runTemplatePath,'r')
    textTemplate = runTemplate.read()
    
    year_range = ''
    table_list = ''
    
    for pastYR in range( _beginPastYR, _endPastYR + 1):
        
        Fpart = str(pastYR) + '_' + str(pastYR + _sequentialYRs - 1) 
        year_range = year_range + ' ' + Fpart 

    for item in _yearlyTableTemplateList:
        
        table_list = table_list + ' ' + os.path.splitext(item)[0] 
       
    for item in _monthlyTableTemplateList:
        
        table_list = table_list + ' ' + os.path.splitext(item)[0]
                        
    text=textTemplate.replace('{year_range}', year_range)
    text=text.replace('{table_list}', table_list)
    text=text.replace('{run_dir}', os.path.normpath(_runFolder))
    text=text.replace('{lookup_dir}', os.path.normpath(_lookupFolder))
    text=text.replace('{exe_name}', _exeName)

    
    outFile.write(text)
    
    outFile.close()
    
    Param.logger.info("Finished processRunBatch")  