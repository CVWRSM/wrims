import os
import subprocess
import os.path as P
from scripts.tool import Param, DssHec
from scripts.wrims2 import Utils
from process.dssTransferProcess import DssTransferProcess
from datetime import datetime


class Study:

	#cMap = {} #configMap
#	dssMap = {} #dssVarMap
#	
#	_startYear=None
#	_startMonth=None # Specify month in config file. Don't change month here
#	_numberOfSteps=None

	_logger = Param.logger
	#_batchPath = ''
	_configPath = None
	
	
	#def __init__(self, configPath, batFileName='runConfig_limitedLicense.bat'):
	def __init__(self, studyName, runDir,  batFileName='runConfig_limitedLicense.bat'):	

		
		self._logger = Param.logger
		self._configDir = P.normpath(P.dirname(runDir))
		#self._configPath = P.normpath(P.join(Param.mainScriptDir, configPath))
		#self._logger.info('configPath: '+self._configPath)
		#self._logger.info('configDir: '+P.dirname(self._configPath))
		#self._configDir = P.dirname(self._configPath)
		#self._studyName, configExtension = P.splitext(P.basename(self._configPath))
		self._studyName = studyName
		#self._ms_configPath = P.join( self._configDir, "__generated.config")
		self._batFileName = batFileName
		
		#parse config file and put the map to cMap	
		#self._cMap=Utils.getConfigMap(self._configPath)
		#self._configKeyList = Utils.configKeyList	

			
		self.processList=[]	
		self.processDict={}





	def add_dss_transfer(self, id, destn, destn_Fpart, source=None, transferFile=None ):
		
		dssTransferFile=None
		
		if transferFile!=None:
		
			if "." not in transferFile:
				transferFile = transferFile+".transfer"
		
			dssTransferFile = P.join( self.configDir, transferFile )
		
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
		print "configPath: "+self._configPath
		for pn in self.processDict.keys():
			print "processNames: "+pn
		for key in self._configKeyList:
			if self._cMap.get(key):
				print '=> ' + key + ':' + self._cMap.get(key)	
		
	
	def _run_wrims(self,startYear=None,startMonth=None, numberOfSteps=None, pause=False):

# 	
# 			self._logger.info("Write study config:  "+self.ms_configPath)
# 			# write config file
# 			Utils.generateConfigFile(self.ms_configPath, self.cMap, startYear=startYear, numberOfSteps=numberOfSteps)
# 		
# 			self._logger.info("Run generated config: "+self.ms_configPath)
# 			
# 			subprocess.call(["cmd.exe", "/c", "start", self.batFileName, self.ms_configPath])
# 			

		if startYear!=None and startMonth!=None and numberOfSteps!=None :

			# write configFile
			cf = open(self._configPath,'w+')	
			cf.write("Begin Config\n")
			for key in self._configKeyList:
				if key!='StartYear' and key!='NumberOfSteps' and key!='StartMonth':
					if self._cMap.get(key):
						cf.write( key + '\t' + str(self._cMap.get(key)) + '\n')
			
			cf.write('StartYear      '+str(startYear)+'\n')
			cf.write('StartMonth     '+str(startMonth)+'\n')
			cf.write('NumberOfSteps  '+str(numberOfSteps)+'\n')				
			cf.write("End Config\n")		
			cf.close()
		
			
		self._logger.info("Run study config: "+self._configPath)
		
		if pause:	
			subprocess.call(["cmd.exe", "/c", "start", self._batFileName, self._configPath, "-pause"])
		else:
			subprocess.call(["cmd.exe", "/c", "start", self._batFileName, self._configPath])

	def _run_process(self,processName,startYear,numberOfSteps):
		
		self._logger.info("Run process: "+processName)
		process=self.processDict[processName]
		
		process.startYear=startYear
		process.startMonth=self._startMonth
		process.numberOfSteps=numberOfSteps
		
		process.run()
		
		
	def run(self,startYear=None, startMonth=None, numberOfSteps=None, pause=False):
	
		#self._startYear=startYear
		#self._numberOfSteps=numberOfSteps
		
# 		if StartYear == None:
# 			StartYear = self._StartYear
# 			
# 		if NumberOfSteps == None:
# 			NumberOfSteps = self._NumberOfSteps
		
		self._run_wrims(startYear, startMonth, numberOfSteps, pause)
		
# 		for pn in self.processDict.keys():
# 			self._run_process(pn, StartYear, NumberOfSteps)

	def importConfig(self, configFile):
						
		#parse config file and put the map to cMap	
		
		configPath = P.normpath(P.join(self._configDir , configFile))
		
		#configPath = P.normpath(P.join(Param.mainScriptDir, configPath))
		
		#self._cMap = Utils.newConfigMap()
		try:
			self._cMap=Utils.getConfigMap(configPath)
			
			t_initFile=self._cMap.get("InitFile")
			t_svarFile=self._cMap.get("SvarFile")
			t_dvarFile=self._cMap.get("DvarFile")

			t_initFile=t_initFile.replace('\"','').replace('\'','')
			t_svarFile=t_svarFile.replace('\"','').replace('\'','')
			t_dvarFile=t_dvarFile.replace('\"','').replace('\'','')
				
			self._cMap.put("InitFile",t_initFile)
			self._cMap.put("SvarFile",t_svarFile)
			self._cMap.put("DvarFile",t_dvarFile)
			
		except:
			self._logger.error('Error in parsing '+configPath)	
			
		self._configKeyList = Utils.configKeyList

		
# 		t_initFile=self._cMap.get("InitFile")
# 		t_svarFile=self._cMap.get("SvarFile")
# 		t_dvarFile=self._cMap.get("DvarFile")
# 
# 		t_initFile=t_initFile.replace('\"','').replace('\'','')
# 		t_svarFile=t_svarFile.replace('\"','').replace('\'','')
# 		t_dvarFile=t_dvarFile.replace('\"','').replace('\'','')
# 				
# 		self._cMap.put("InitFile",t_initFile)
# 		self._cMap.put("SvarFile",t_svarFile)
# 		self._cMap.put("DvarFile",t_dvarFile)		


		
		# write configFile
		self._configDir = P.dirname(configPath)
		self._configPath = P.join(self._configDir, self._studyName+'.config')

		# backup configFile if exists
		if os.path.exists(self._configPath):
			t = datetime.now().strftime('%Y%m%d%H%M%S')
			os.rename(self._configPath, self._configPath+'_bac_'+t)
				
		try:
			cf = open(self._configPath,'w+')	
			cf.write("Begin Config\n")
			for key in self._configKeyList:
				if self._cMap.get(key):
					cf.write( key + '\t' + str(self._cMap.get(key)) + '\n')	
			cf.write("End Config\n")		
			cf.close()
			
		except:
			self._logger.error('Error in writing '+self._configPath)			

	
	def createConfig(self, 
					WreslPlus, 
					MainFile, 
					InitFile, 
					InitFPart, 
					SvarFile, 
					SvarAPart, 
					SvarFPart, 
					DvarFile, 
					StartYear, 
					StartMonth, 
					NumberOfSteps=None, 
					StopYear=None,
			 		StopMonth=None,
					LookupSubDir=None,
					ShowWreslLog='Yes',
					PrefixInitToDvarFile='Yes',
					IlpLog='No',
					IlpLogFormat='CplexLp',
					IlpLogVarValue='no',
					IlpLogAllCycles='no'
					):
		
		self._configPath = P.join(self._configDir, self._studyName+'.config')
		self._configKeyList = Utils.configKeyList
		
		if P.exists(self._configPath):
			self._logger.info(self._configPath+'already exists.')
			self._logger.info('Overwriting '+self._configPath)
		
		self._cMap = Utils.newConfigMap()
		
		self._cMap.put('WreslPlus', WreslPlus)
		self._cMap.put('MainFile', P.normpath(MainFile))
		self._cMap.put('Solver', 'XA')
		self._cMap.put('SvarAPart', SvarAPart)
		self._cMap.put('ShowWreslLog', ShowWreslLog)
		self._cMap.put('PrefixInitToDvarFile', PrefixInitToDvarFile)
		self._cMap.put('IlpLog', IlpLog)
		self._cMap.put('IlpLogFormat', IlpLogFormat)		
		self._cMap.put('IlpLogVarValue', IlpLogVarValue)
		self._cMap.put('IlpLogAllCycles', IlpLogAllCycles)
		
				
		if InitFile:
			self._cMap.put('InitFile', P.normpath(InitFile))

		if InitFPart:
			self._cMap.put('InitFPart', InitFPart)
						
		if SvarFile:
			self._cMap.put('SvarFile', P.normpath(SvarFile))
			
		if SvarFPart:
			self._cMap.put('SvarFPart', SvarFPart)	

		if DvarFile:
			self._cMap.put('DvarFile', P.normpath(DvarFile))
						
		if StartYear:
			self._cMap.put('StartYear', StartYear)

		if StartMonth:
			self._cMap.put('StartMonth', StartMonth)
			
		if NumberOfSteps:
			self._cMap.put('NumberOfSteps', NumberOfSteps)
		else:
			self._cMap.put('StopYear', StopYear)
			self._cMap.put('StopMonth', StopMonth)

		if SvarFPart:
			self._cMap.put('SvarFPart', SvarFPart)
			
		if LookupSubDir:
			self._cMap.put('LookupSubDir', LookupSubDir)	
			
		# write configFile
		cf = open(self._configPath,'w+')	
		cf.write("Begin Config\n")
		for key in self._configKeyList:
			if self._cMap.get(key):
				cf.write( key + '\t' + str(self._cMap.get(key)) + '\n')	
		cf.write("End Config\n")		
		cf.close()


	def writeBatch(self, pause=True):
		
		batchPath = P.join(self._configDir, self._studyName+'.bat')
		
		if P.exists(batchPath):
			self._logger.info(batchPath+' already exists.')
			self._logger.info('Overwriting '+batchPath)
			
		else:
			self._logger.info('Writing batch file: '+batchPath)
		
		self._batchPath = batchPath
		
		bf = open(batchPath,'w+')
		
		mainScriptDir = P.dirname(Param.mainScriptPath)
		
		wrims2BatPath = P.normpath(P.join(mainScriptDir, self._batFileName))

		self._logger.info('Param.mainScriptPath: '+Param.mainScriptPath)		
		self._logger.info('mainScriptDir: '+mainScriptDir)
		self._logger.info('wrims2BatPath: '+wrims2BatPath)
		
		bf.write(wrims2BatPath + ' ' + P.join( '%~dp0', self._studyName+'.config'))
		if pause:
			bf.write(' -pause')
		bf.close()
			
			