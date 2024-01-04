#     Name: WsiDiGen.py
#   Author: Ben Tustison
#   E-mail: tustison@mbkengineers.com
#    Phone: 916.456.4400
# Last Rev: 06.25.2010
#  Purpose: Mimics WsiDiGenerator class from CALSMIM

# python class imports
from math import sqrt

# java class imports
from java.awt.Color import *
from java.io import *
from vista.app import *
from vista.graph import *
from vista.set import *
from vista.db.dss import *
import os

tab = "   "

# WsiDiCl class
class WsiDiGenCl:

   # constructor: initialize class parameters
   def __init__(self,configPath,name,wsiVar,diVar,wsiMax,dvName):
       # assign passed constructor arguments
       self.name = name   # wsi-di profile name
       self.wsiVar = wsiVar   # wsi variable
       self.diVar = diVar   # di variable
       self.wsiMax_ub = wsiMax
       self.wsiMax = wsiMax   # maximum wsi variable value
       self.stdyDvName = dvName   # study DV name
       self.configPath = configPath
    
       # set path to run directory based on location of config file
       #file = File(dvName)
       #dir = File(file.getParent())
       #self.runDir = dir.getParent()+File.separator+"run"   # run directory
       self.runDir = os.path.join(os.path.dirname(self.configPath),'run')
              
       # set other class variables
       value = 0.0
       self.report = True
       self.step = 500.0   # Distance between data points on the WSI axis
       self.lookahead = 1.0*self.step   # Distance to look ahead for data beyond segment endpoint to determine coordinates of segment endpoint
       self.mult = 1.0   # standard deviation multiplier for wsidi pair search
       self.wsiMin = 0.0   # minimum wsi value
       self.diMin = self.step   # minimum di value
       self.diMax = 0.0   # maximum di value, set to actual value when WSI-DI data is analyzed
       self.threshold = 0.1   # threshold above which to count wsi-di data pts
       self.nseg = int(wsiMax/self.step)   #  number of wsi-di segments
       self.ndata = 0   # number of wsi-di data points, set to actual value when WSI-DI data is analyzed
       self.data = []   # wsi-di data array

   ### FUNCTIONS

   # count data points above threshold
   def countDataPoints(self,array):
      ndata=0
      for i in range(0,len(array)):
         if (array[i] > self.threshold ):
            ndata += 1
      return ndata;

   # load data
   def load(self,fname):
      print tab + "Loading " + self.wsiVar + " and " + self.diVar

      # initilization
      wsiMax=0.0
      wsiMin=self.wsiMax_ub
      diMin = self.wsiMax_ub*100.0
      diMax=0.0
      wcount = 0
      dcount = 0

      # create a group of the proper file
      g = DSSUtil.createGroup("local",fname)
      gWsi = g
      gDi = g.clone()
      
      # get data for wsi and di, check for exceptions
      gWsi.filterBy(PathPartPredicate("^"+self.wsiVar+"$",Pathname.B_PART),True)
      if (gWsi.getNumberOfDataReferences()!=1):
         raise Exception("No WSI Variable '"+ getWsiVariable() +"' in DSS File!")
      gDi.filterBy(PathPartPredicate("^"+self.diVar+"$",Pathname.B_PART),True)
      if (gDi.getNumberOfDataReferences()!=1):
         raise Exception("No DI Variable '"+ getDiVariable() +"' in DSS File!")
      drWsi = gWsi.getDataReference(0)
      drDi = gDi.getDataReference(0)
      dsWsi = drWsi.getData()
      dsDi = drDi.getData()
      wsi = SetUtils.createYArray(dsWsi)
      di = SetUtils.createYArray(dsDi)
      nd = self.countDataPoints(wsi)

      # last two wsi and first di elements are removed
      nd = nd - 2

      #Define dummy arrays for holdind WSI and DI variables
      tmpWSI = []
      tmpDI = []
      tmpData = []

      #load arrays removing first DI value and last two WSI values
      for i in range(0,len(wsi)):
         if(wsi[i] >= 0.0 and wcount < nd):
            tmpWSI.append(wsi[i])
      	    wcount += 1
   	    if(wsi[i] > wsiMax ):
               wsiMax=wsi[i]
  	    if(wsi[i] < wsiMin ):
               wsiMin=wsi[i]
         if(di[i] >= 0.0 and dcount < nd ):
            if(dcount > 0 ):
	       tmpDI.append(di[i])
	       dcount += 1
	       tmpDI.append(di[i])
	       dcount += 1
	       if(di[i] < diMin ):
                  diMin=di[i]
	       if(di[i] > diMax ):
                  diMax=di[i]
	    else:
               dcount += 1

      #Correct dcount
      dcount-=1

      #Set minimums and maximums of WSI and DI data series
      self.wsiMax = wsiMax
      self.wsiMin = wsiMin
      self.diMax = diMax
      self.diMin = diMin

      # Check for data exceptions
      if(wcount!=dcount):
         raise Exception("Data Mismatch! Wsi Values: "+ str(wcount) +" Di Values: "+ str(dcount))
      if(wcount!=nd):
         raise Exception("Data Mismatch! Data Values: "+ str(nd) +" Wsi Values: "+ str(wcount))

      #Set number of data points
      self.ndata = nd

      # load data array with wsi-di data series
      for i in range(0,self.ndata):
         tmpData.append((tmpWSI[i],tmpDI[i]))
      self.data = tmpData[:]

   # extract array of wsi values from wsi-di array
   def getInputWsi(self):
      wsi = []
      for i in range(0,self.ndata):
         wsi.append(self.data[i][0])
      return wsi

   # extracts array of di values from wsi-di array
   def getInputDi(self):
      di = []
      for i in range(0,self.ndata):
         di.append(self.data[i][1])
      return di

   # calculates slope
   def getSlope(self, wsi0, di0, offset):
      wsiend = wsi0 + self.step
      npoints=0
      wsiin = self.getInputWsi()
      diin = self.getInputDi()
      slope = 0.0
      delwsi = 0.0
      sumdelwsi = 0.0
      sumdiwsi = 0.0
      sumwsi2 = 0.0
      if (wsi0 < (0.33*(self.wsiMax-self.wsiMin)+self.wsiMin)):
         offset *= 2.0
      elif (wsi0 < (0.5*(self.wsiMax-self.wsiMin)+self.wsiMin)):
         offset *= 1.0
      else:
	 offset *= 0.0
      for i in range(self.ndata):
    	 if (wsiin[i] > wsi0 and wsiin[i] <= (wsiend + self.lookahead)):
            npoints += 1
	    delwsi = wsiin[i] - wsi0
	    sumdelwsi = sumdelwsi + wsiin[i] - wsi0
	    sumdiwsi = sumdiwsi + (diin[i]-offset)*delwsi
	    sumwsi2 = sumwsi2 + delwsi*delwsi
      if (npoints > 1 ):
         slope = (sumdiwsi - di0*sumdelwsi)/sumwsi2
      else:
         slope = 0.0
      if (slope < 0.0):
         slope = 0.0
      return slope

   #Get function pair returns a single point on the wsi-di curve
   def getFunctionPair(self, wsi0, di0, offset):
      wsiend = wsi0 + self.step
      slope = self.getSlope(wsi0,di0,offset)
      diend = di0 + slope*(wsiend - wsi0)
      # if wsi0 minimal, set first di equal to the min of the set
      if (wsi0 < 1.0):
         diend=self.diMin
      pair = (wsiend,diend)   # assign wsi/di variables for return
      return pair

   # gets cumulative step-wise standard deviation for input data set as projected from initial wsi-di curve
   def getStandardDev(self,data):
      slope = 0.0
      dist = 0.0
      sumdist = 0.0
      sumdist2 = 0.0
      stdev = 0.0
      npoints=0
      wsiin = self.getInputWsi()
      diin = self.getInputDi()
      for i in range(0,self.nseg):
         wsi0 = data[i][0]
	 di0 = data[i][1]
         for j in range(0,self.ndata):
            if (wsiin[j] > wsi0 and wsiin[j] < (wsi0 + self.step)):
               npoints += 1
 	       slope = self.getSlope(wsi0,di0,0.0)
 	       dist = diin[j] - (di0 + slope*(wsiin[j] - wsi0))
 	       sumdist = sumdist + dist
 	       sumdist2 = sumdist2 + dist*dist
      if (npoints > 0):
         stdev = sqrt((npoints*sumdist2 - sumdist*sumdist)/(npoints*(npoints-1)))
      else:
         stdev = 0.0
      return stdev

   #getAllFunctionPairs returns the final wsi-di curve
   def getAllFunctionPairs(self):
      data = []
      wsi0 = 0.0
      di0 = 0.0
      data.append((wsi0,di0))
      for i in range (1,self.nseg+1):
         data.append(self.getFunctionPair(wsi0,di0,0.0))
	 wsi0 = data[i][0]
	 di0 = data[i][1]
      stdev = self.getStandardDev(data)
      offset = self.mult*stdev
      wsi0 = 0.0
      di0 = 0.0
      data[0] = (wsi0,di0)
      for i in range (1,self.nseg+1):
         data[i] = self.getFunctionPair(wsi0,di0,offset)
	 wsi0 = data[i][0]
	 di0 = data[i][1]
      return data

   #save puts the wsi-di curve in the user specified *.table file
   def save(self,data):
      print tab + "Saving WSI_DI_" + self.name
      print ""
      sep = File.separator
      fname = self.runDir + File.separator + "lookup" + File.separator + "wsi_di_" + self.name + ".table"
      pw = PrintWriter(BufferedWriter(FileWriter(fname)))
      pw.println("wsi_di_" + self.name)
      pw.println("wsi                   di")
      for i in range(0,self.nseg+1):
	 #pw.println(str(int(round(data[i][0]))) + "        " + str(int(round(data[i][1]))))
	 pw.println(str(data[i][0]) + "        " + str(data[i][1]))
      pw.close()

   #execute is the function call to generate a wsi-di table
   def execute(self):
      print tab + "Generating WSI_DI_" + self.name
      output = self.getAllFunctionPairs()
      self.save(output)