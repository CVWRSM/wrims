import hec.heclib.dss.HecDss as HecDss
import hec.heclib.dss.DSSPathname as DSSPathname
#from hec.heclib.dss import *
#from hec.hecmath import *
import os
import logging
import Param


def deleteFile(dir, extensionToDelete):
    
    for item in os.listdir(dir):
        filePath = os.path.join(dir, item)
        if os.path.isfile(filePath):
            #print item
            extension = os.path.splitext(item)[1]
            #print extension
            if extensionToDelete in extension:
                # print "File deleted: "+item
                try: 
                    os.remove(filePath)
                    Param.logger.warning("File deleted: "+filePath)
                except:
                    pass

def cleanDssCatalog(dssPath):
    
    dscFilePath = dssPath[0:-4] +".dsc"
    
    #print "dscFilePath: "+dscFilePath
    
    try:
        os.remove(dscFilePath)
    except:
        pass    
    

def copyDssToFuture(inFile, outFile, beginYR, sequentialYRs, bufferYRs, futureYR = -99 ):

    if futureYR == -99:
        futureYR = beginYR
    
    endYR = sequentialYRs + beginYR - 1
        
    timeWindow_begin = "02JAN"+str(beginYR - bufferYRs)+" 0000"
    timeWindow_end =   "31DEC"+str(endYR + bufferYRs)  +" 2400"
    
    shiftedYR = futureYR - beginYR
    
    #inFile =  HecDss.open(inDssPath)
    inFile.setTimeWindow(timeWindow_begin, timeWindow_end)
    #outFile = HecDss.open(outDssPath) 

  
    pathList = inFile.getCondensedCatalog()
    
    for i, p in enumerate(pathList):
        p = DSSPathname(str(p))
        p.setDPart("")
        pathList[i]=p.getPathname()
        
     
    for i, p in enumerate(pathList):
    
        print "p: "+ p
        outflow = inFile.read(p)
        futurePath = DSSPathname(outflow.getPath())
        futurePath.setFPart(str(beginYR)+"_"+str(endYR))
        outflow.setPathname(futurePath.getPathname())
        
        if shiftedYR == 0 :
            shiftedData = outflow
        else:
            shiftedData = outflow.shiftInTime(str(shiftedYR)+"Y")
        
        outFile.write(shiftedData)



def copyYearlyWYTypesToFuture(inTable, outTable, beginYR, totalYR, futureYR, bufferYRs ):
    

    # copy whole content 
    data = inTable.readlines()
    outTable.writelines(data)

    dataWY = {} # this will map water year type to water year

    for line in data:
        line = line.replace('\n', '')
        element = line.strip().split()
        try:
            WY = int(element[0])

            if WY >= 1920:
                dataWY[WY] = line

        except:
            continue

    outTable.writelines( '\n' )
    
    for i, year in enumerate(range(beginYR, beginYR + totalYR + bufferYRs)):
         
        outTable.writelines( str(futureYR+i) + dataWY[year][5:] + '\n')


def copyMonthlyWYTypesToFuture(inTable, outTable, beginYR, totalYR, futureYR, bufferYRs, monthList ):
    

    # copy whole content 
    data = inTable.readlines()
    outTable.writelines(data)

    dataWY = {} # this dictionary key will be "year_month"

    for line in data:
        line = line.replace('\n', '')
        element = line.strip().split()
        try:
            WY = int(element[0])
            month = int(element[1])

            if WY >= 1920:
                key = str(WY)+'_'+str(month)
                dataWY[key] = line

        except:
            continue

    outTable.writelines( '\n' )
    
    for i, year in enumerate(range(beginYR, beginYR + totalYR + bufferYRs)):
         
        for month in monthList:
            
            key = str(year)+'_'+str(month)
            outTable.writelines( str(futureYR+i) + dataWY[key][5:] + '\n')


def generateStudyFile (studyTemplateFilePath, outStyFilePath, Fpart, showWindow, 
                       runFolder, shiftedSvDssFile, initialDssFile, resultDssFile ):
    
    studyTemplate = open(studyTemplateFilePath, 'r')
    textTemplate = studyTemplate.read()

    
    text=textTemplate.replace('{Fpart}',Fpart)  # replace DSS F part
    text=text.replace('{ShowWindow}',str(showWindow))  
    text=text.replace('{RunFolder}',runFolder)   
    text=text.replace('{DssFilePath_SV}',shiftedSvDssFile)  
    text=text.replace('{DssFilePath_Initial}',initialDssFile) 
    text=text.replace('{DssFilePath_Result}',resultDssFile) 
 

    studyFile = open(outStyFilePath,'w')      # open and write to study.sty
    studyFile.write(text)
    studyFile.close()
    
        
def arrayShift(inArray, shift): 

    outArray = map(lambda x: x+shift, inArray) 
    return outArray


    
    
