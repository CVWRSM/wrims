import os
import subprocess
from os.path import dirname
from scripts.misc import Tools, Param
from DssTransferProcess import DssTransferProcess

class Study:

	#cMap = {} #configMap
#	dssMap = {} #dssVarMap
#	
#	_startYear=None
#	_startMonth=None # Specify month in config file. Don't change month here
#	_numberOfSteps=None

	_logger = Param.logger
	
	
	
	def __init__(self, configPath, batFileName='runConfig.bat'):

		
		self._logger = Param.logger
		self.configPath = configPath
		self.configDir = dirname(self.configPath)
		self.ms_configPath = os.path.join( self.configDir, "__MultiStudyRunner.config")
		self.batFileName = batFileName
		
		#parse config file and put the map to cMap	
		self.cMap=Tools.getConfigMap(self.configPath)
		
		self._startYear=int(self.cMap.get("StartYear"))
		self._startMonth=int(self.cMap.get("StartMonth"))
		self._numberOfSteps=int(self.cMap.get("NumberOfSteps"))
		
		initFile=self.cMap.get("InitFile")
		svarFile=self.cMap.get("SvarFile")
		dvarFile=self.cMap.get("DvarFile")
		
		initFile=initFile.replace('\"','').replace('\'','')
		svarFile=svarFile.replace('\"','').replace('\'','')
		dvarFile=dvarFile.replace('\"','').replace('\'','')
		
		self.InitFile = os.path.join( self.configDir, initFile )
		self.SvarFile = os.path.join( self.configDir, svarFile )
		self.DvarFile = os.path.join( self.configDir, dvarFile )	

		self.InitFPart = self.cMap.get("InitFPart")	
		self.SvarFPart = self.cMap.get("SvarFPart")	
			
		self.processList=[]	
		self.processDict={}





	def add_dss_transfer(self, id, destn, destn_Fpart, source=None, transferFile=None ):
		
		dssTransferFile=None
		
		if transferFile!=None:
		
			if "." not in transferFile:
				transferFile = transferFile+".transfer"
		
			dssTransferFile = os.path.join( self.configDir, transferFile )
		
		else:
			
			#transferFile = "DssCopy_"+str(len(self.processList)+1)
			dssTransferFile = None
			
		if source==None:
			
			source = self.DvarFile


		p = DssTransferProcess(source, destn, destn_Fpart, dssTransferFile)
		
		
		self.processList.append(id)
		self.processDict[id]=p		
		
		
		
	def info(self):
		
		#print "Study Name:  "+self.name
		print "Config path: "+self.configPath
		for pn in self.processDict.keys():
			print "process name: "+pn
		
		
	
	def _run_wrims(self,startYear,numberOfSteps):


		# call cmd to run config path

	
		self._logger.info("Write study config file in dir: "+self.configDir)
		# write config file
		Tools.generateConfigFile(self.ms_configPath, self.cMap, startYear=startYear, numberOfSteps=numberOfSteps)
		
		self._logger.info("Run study config file in dir: "+self.configDir)
		subprocess.call([self.batFileName, self.ms_configPath])
		#subprocess.call(['cmd.exe', '/c', 'RunStudy.bat', self.ms_configPath])


	def _run_process(self,processName,startYear,numberOfSteps):
		
		self._logger.info("Run process: "+processName)
		process=self.processDict[processName]
		
		process.startYear=startYear
		process.startMonth=self._startMonth
		process.numberOfSteps=numberOfSteps
		
		process.run()
		
		
	def run(self, startYear, numberOfSteps):
	
		self._startYear=startYear
		self._numberOfSteps=numberOfSteps

		
		
		self._run_wrims(startYear,numberOfSteps)
		
		for pn in self.processDict.keys():
			self._run_process(pn, startYear, numberOfSteps)
	
	
	
