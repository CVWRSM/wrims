import os
import subprocess
import os.path as P
from scripts.tool import Param, DssHec
from scripts.wrims2 import Utils


class Var:

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
			
			