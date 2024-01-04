import os
import scripts.tool.Param as Param
import wrimsv2.evaluator.TimeOperation as TimeOp
from wvscript.reader.element import ConfigReader

configKeyList = [
             "WreslPlus",     
             "MainFile", 
             "Solver",
             "InitFile",
             "InitFPart",
             "SvarFile",
             "SvarAPart",
             "SvarFPart",
             "DvarFile",                   
             "TimeStep",
             "StartYear", 
             "StartMonth",
             "StartDay",
             "NumberOfSteps",
             "StopYear",
             "StopMonth",
             "StopDay",
             "LookupSubDir",
             "GroundwaterDir",
             "ShowWreslLog",
             "PrefixInitToDvarFile",
             "SendAliasToDvar",
             "IlpLog",
             "IlpLogFormat",
             "IlpLogVarValue",
             "IlpLogAllCycles"
             ]

def  newConfigMap():
        
    cp = ConfigReader(configKeyList);

    return cp.newConfigMap()

def  getConfigMap(filePath):
        
    cp = ConfigReader(configKeyList);
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
    
        



    
    
