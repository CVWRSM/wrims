from scripting.element import DssTransferReader
from scripts.misc import Tools, Param
import hec.heclib.dss.HecDss as HecDss
#import hec.heclib.dss.DSSPathname as DSSPathname

class DssTransferProcess:

    
    startYear=None
    startMonth=None
    numberOfSteps=None
    outFpart=None

    
    def __init__(self, source, destn, destn_Fpart, transferSpecFile=None): 
        
        self.source=source
        self.destination = destn
        self.outFpart = destn_Fpart
        self.transferSpecFile = transferSpecFile
        self._logger = Param.logger
        
        
        
        #parse transferSpecFile
        if transferSpecFile!=None:
            self._logger.info("Parse transfer file: "+self.transferSpecFile)
        
            mr = DssTransferReader
            mr.parseFile(self.transferSpecFile)
            self.transferMap = mr.transferMap

            #print self.transferMap
        
    
    def run(self):
        

#        Tools.cleanDssCatalog(self.source)
#    
#        inFile =  HecDss.open(self.source)
#        outFile = HecDss.open(self.destination) 
#
#        inFilePathList, inFileBpartList = Tools.generateCatalog(inFile)

        if self.transferSpecFile == None:
            
            self._logger.info("Copy all vars ")
            Tools.dssDataTransferMonthly(self.source, self.destination, self.outFpart, self.startYear, self.startMonth, self.numberOfSteps)
        
        else:    
            self._logger.info("Copy vars specified in transfer file")
            Tools.dssDataTransferMonthly(self.source, self.destination, self.outFpart, self.startYear, self.startMonth, self.numberOfSteps, transferMap=self.transferMap)
    
#        inFile.close()
#        outFile.close()
    
