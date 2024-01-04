import hec.heclib.dss.HecDss as HecDss
import hec.heclib.dss.DSSPathname as DSSPathname
#from hec.heclib.dss import *
#from hec.hecmath import *
import os
import logging
import Param
import wrimsv2.evaluator.TimeOperation as TimeOp
from scripting.element import ConfigReader


def getConfigMap(filePath):
    
    cp = ConfigReader;
    cp.parseFile(filePath)

    return cp.configMap


def generateConfigFile(filePath, cMap, startYear, numberOfSteps):
    
    
    cfile = open(filePath,"w")
    cfile.write("Begin Config\n")
    
    for key in cMap.keySet():
        
        if (key=="StartYear"):
            cfile.write(key+"    "+str(startYear)+"\n")
        elif (key=="NumberOfSteps"): 
            cfile.write(key+"    "+str(numberOfSteps)+"\n")
        else:    
            cfile.write(key+"    "+cMap.get(key)+"\n")
    
    cfile.write("End Config\n")
    cfile.close()

def writeConfigFile(filePath,cMap):
    
    cfile = open(filePath,"w")
    cfile.write("Begin Config\n")
    
    for key in cMap.keySet():
        cfile.write(key+"    "+cMap.get(key)+"\n")
    
    cfile.write("End Config\n")
    cfile.close()
    

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


def getBpartList(dssPathList):
    
    bPartList = []   

    for p in dssPathList:
        p = DSSPathname(str(p))
        bPartList.append(p.bPart())   

    return bPartList

def generateCatalog(dssFile):
    
    outPathList = []
    bPartList = []
    pathList = dssFile.getCondensedCatalog()
    
    for i, p in enumerate(pathList):
        p = DSSPathname(str(p))
        p.setDPart("")
        outPathList.append(p.getPathname())    
        bPartList.append(p.bPart())    
    
    return outPathList, bPartList        
             

def dssDataTransferMonthly(inFilePath, outFilePath, outFpart, startYr, startMon, numberOfMonths, transferMap=None):

    if numberOfMonths<1:
        return

    cleanDssCatalog(inFilePath)
    
    inFile =  HecDss.open(inFilePath)
    outFile = HecDss.open(outFilePath) 

    inFilePathList, inFileBpartList = generateCatalog(inFile)
    
    dssDataTransferMonthly2(inFile, inFilePathList, inFileBpartList, outFile, outFpart, startYr, startMon, numberOfMonths, transferMap)
            
    inFile.close()
    outFile.close()


def dssDataTransferMonthly2(inFile, inFilePathList, inFileBpartList, outFile, outFpart, startYr, startMon, numberOfMonths, transferMap=None):

    if numberOfMonths<1:
        return
    
    if transferMap!=None:
        transferList = transferMap.keySet()
    
    # time window format
    # 02JAN1926 0012        
    timeWindow_begin = "03"+Param.month2Str(startMon)+str(startYr)+" 0000"

    increMon = int(numberOfMonths-1)%12
    increYr = int(numberOfMonths-1)/12
    
    stopMon = (int(startMon) + int(increMon))%12
    
    stopYr  = startYr  + increYr + (int(startMon) + int(increMon))/12

#    print "startYr: "+str(startYr)
#    print "startMon: "+str(startMon)
#    print "numberOfMonths: "+ str(numberOfMonths)
#    print "stopYr: "+str(stopYr)
#    print "stopMon: "+str(stopMon)

    stopDay = TimeOp.numberOfDays(stopMon, stopYr)
    
    timeWindow_end = str(stopDay)+Param.month2Str(stopMon)+str(stopYr)+" 2400"
    
    inFile.setTimeWindow(timeWindow_begin, timeWindow_end)

    
    
    if transferMap==None:
  
        for i, k in enumerate(inFileBpartList):

            p = inFilePathList[i]
            outflow = inFile.read(p)
            outPath = DSSPathname(outflow.getPath())
        
            #outPath.setFPart("copy")        
            outflow.setPathname(outPath.getPathname())
            outFile.write(outflow)      
    
    
    else:
    ################################
        selectedPathList = []
        selectedMap = {}
    
        for i, b in enumerate(inFileBpartList):
    
            if b in transferList:
                print b
                
                p = inFilePathList[i]
                
                selectedPathList.append(p)
                selectedMap[b]=p
    
    
        for k in transferList:
    
            p = selectedMap.get(k)
            outflow = inFile.read(p)
            outPath = DSSPathname(outflow.getPath())
            
    #        outPath.setFPart("copy")        
    #        outflow.setPathname(outPath.getPathname())
    #        outFile.write(outflow)
                   
            for pair in transferMap.get(k):
                #print "pair: "+pair
    
    
                pairList = pair.split("/") 
                
                newB = pairList[0]
                newC = pairList[1]
                
    #            print "newB: "+newB
    #            print "newC: "+newC 
    #            print "newF: "+outFpart                
                outPath.setBPart(newB) 
                outPath.setCPart(newC) 
                outPath.setFPart(outFpart)        
                outflow.setPathname(outPath.getPathname())
                outFile.write(outflow)
        
                        
        
def arrayShift(inArray, shift): 

    outArray = map(lambda x: x+shift, inArray) 
    return outArray


    
    
