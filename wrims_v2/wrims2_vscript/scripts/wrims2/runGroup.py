import os
import subprocess
import os.path as P
from scripts.tool import Param, FileUtils
from scripts.wrims2.study import Study


class RunGroup:


	_logger = Param.logger
	_runList = []
	_groupName = ''	
	
	def __init__(self, runGroupName, batFileName='runConfig_limitedLicense.bat'):

		
		self._logger = Param.logger
		self._logger.info('Creating a RunGroup; batFileName='+batFileName)

		self._batFileName = batFileName
		self._runGroupName = runGroupName
			
		self._runList=[]	


	def add(self, study):
		
		if isinstance(study, Study):
			self._runList.append(study)
			self._logger.info('Adding this study to RunGroup: '+ study._studyName)
		else:
			self._logger.error('Error adding a study to RunGroup')	
			

	def writeBatch(self, simultaneousRun, delaySec=4):
		
		mainScriptDir = P.dirname(Param.mainScriptPath)
		batchPath = P.join(mainScriptDir, self._runGroupName+'.bat')
		
		if P.exists(batchPath):
			self._logger.info('RunGroup batch already exists: '+batchPath)
			self._logger.info('Overwriting '+batchPath)
		
		else:
			self._logger.info('Writing RunGroup batch file: '+batchPath)
		
		
		wrims2BatPath = P.normpath(P.join(mainScriptDir, self._batFileName))

		bf = open(batchPath,'w+')
		subGroupDir = P.join(mainScriptDir, '_'+self._runGroupName)		
		if P.exists(subGroupDir):
			FileUtils.erase(subGroupDir)

	
		if simultaneousRun > 1:
			
			# strange bug. need to investigate.
			try: 
				os.makedirs(subGroupDir)
			except:
				os.mkdir(subGroupDir)	
			
			chunks=[self._runList[x:x+simultaneousRun] for x in xrange(0, len(self._runList), simultaneousRun)]
				
			for i in range(len(chunks)):	

				subGroupPath = P.join(mainScriptDir, subGroupDir, self._runGroupName+'_sequence_'+ str(i)+'.bat')
				bg = open(subGroupPath,'w+')
				
				#for study in chunks[i]:
				for j, study in enumerate(chunks[i]):

					if j < len(chunks[i])-1:
						bg.write("start " + study._batchPath + '\n')
						bg.write("timeout "+str(delaySec)+"\n")
					else:
						bg.write("@title = \"" + study._batchPath + "\"\n")
						bg.write(study._batchPath + '\n')
						
				bg.close()
			
				bf.write('start /wait ' + subGroupPath + '\n')

					
		else:  # sequential run
					
			for study in self._runList:
					
				bf.write("start /WAIT " + study._batchPath + '\n');	
				
		bf.write('echo \"run completed.\"\n')
		bf.write('pause\n')		
				
		bf.close()
			
			